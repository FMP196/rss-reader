package it.rss.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import it.rss.utils.ConnectionHandler;
import it.rss.utils.CustomProgressDialog;
import it.rss.utils.Tag;
import it.rss.parser.Feed;
import it.rss.parser.Parser;
import it.rss.database.DatabaseHelper;

public class AddRssActivity extends CustomActivity implements OnClickListener, OnCheckedChangeListener
{
	private Button btnOk;
	private Button btnCancel;
	private TextView txtUrl;
	private CheckBox chkBox;
	private CustomProgressDialog dialog;

	private Feed feed;
	private Parser parser;
	private boolean extern;
	private boolean searchOnWebsite;
	private DatabaseHelper database;
	private ConnectionHandler connection;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addrss);
				
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		txtUrl = (TextView) findViewById(R.id.txtUrl);
		chkBox = (CheckBox) findViewById(R.id.checkBoxSearch);
		chkBox.setOnCheckedChangeListener(this);
		
		parser = new Parser();
		database = new DatabaseHelper(getApplicationContext());
		connection = new ConnectionHandler(getApplicationContext());
		dialog = new CustomProgressDialog(AddRssActivity.this);
		
		searchOnWebsite = false;
		
		// Handle intents launched by extern activities;
	    // get the intent that started this activity 
		extern = false;
		Intent intent = getIntent();
		if (intent.getDataString() != null)
		{
			// if data is not null it means that the call was made by an 
			// extern intent, to add a new feed
			// ---
			// update the url text field
			txtUrl.setText(intent.getDataString());
			extern = true;
		}
	}
	
	@Override
	protected void onPause()
	{
		dialog.dismiss();
		super.onPause();

		// close the current activity
		finish();
	}

	/**
	 * Handle UI elements click
	 * @param view - View component
	 */
	public void onClick(View view) {
		if (view == btnOk) {
			saveRssFeed();
		} 
		else if (view == btnCancel) {
			super.onBackPressed();
		}
	}
	
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == chkBox) {
			if (isChecked == false) {
				txtUrl.setHint(getString(R.string.rssAddress));
				searchOnWebsite = false;
			}
			else {
				txtUrl.setHint(getString(R.string.httpAddress));
				searchOnWebsite = true;
			}
		}
	}

	/**
	 * Check URL validity and if successfull, call a new task 
	 * to handle the insertion of a new feed into the database
	 */
	private void saveRssFeed()
	{
		boolean validUrl = false;
		String url = txtUrl.getText().toString();
		
		// check if the inserted URL starts with 'http://' prefix
		if ((url.toLowerCase().startsWith("http://")) == false) {
			String tmp = "http://" + url;
			url = tmp;
		}		
		
		// check if the url inserted by the user is valid
		validUrl = parser.isValid(url);
		if (validUrl == true)
		{
			// check internet connection
			if (connection.isConnected() == false) {
				String text = getString(R.string.noInternetConnection);
				Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
			}
			else {
				new AddRssFeedTask().execute(url);
			}
		}
		else {
			// url not valid
			String text = getString(android.R.string.httpErrorBadUrl);
			Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		}
	}	

	/**
	 * Async Task to add new feeds
	 */
	class AddRssFeedTask extends AsyncTask<String, String, String>
	{		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show(getString(R.string.fetchRssFeed));
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
		}

		@Override
		protected String doInBackground(String... params)
		{		
			// get feed
			feed = parser.getFeed(params[0], searchOnWebsite);

			if (feed == null)
			{
				// no feeds found at the given Url
				runOnUiThread(new Runnable() { public void run() {
					String text = getString(R.string.noRssFeedsFound);
					Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
				}});
			}
			else
			{
				// valid feed found at the given Url; add it to the database
				boolean result = database.addFeed(feed);
				
				if (result == false)
				{
					// Feed already exists
					runOnUiThread(new Runnable() { public void run() {
						String text = "'"+feed.getTitle() + "' " + getString(R.string.feedAlreadyExists);
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
					}});
				}

				// if the activity call was made by an extern application
				// then display a Toast to show the operation result
				if ((extern == true) && (result == true)) {
					runOnUiThread(new Runnable() { public void run() {
						String text = feed.getTitle() + "\n" + getString(R.string.addFeedSuccesfull);
						Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
					}});
				}

				// close the current Activity and send the result to the previous one
				Intent intent = getIntent();
				setResult(Tag.ACTIVITY_RESULT, intent);
				finish();
			}

			return null;
		}		
	}
}