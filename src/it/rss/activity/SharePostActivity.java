package it.rss.activity;

import it.rss.bitly.BitlyHandler;
import it.rss.utils.CustomProgressDialog;
import it.rss.utils.Tag;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SharePostActivity extends CustomActivity implements OnClickListener
{
	private Button btnOk;
	private Button btnCancel;
	private TextView txtDescription;
	
	private String link;
	private BitlyHandler bitly;
	private CustomProgressDialog dialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sharepost);
			
		// bind UI elements with XML
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		txtDescription = (TextView) findViewById(R.id.txtDescription);
		
		dialog = new CustomProgressDialog(SharePostActivity.this);
		
		// get link to share
		Intent intent = getIntent();
		link = intent.getStringExtra(Tag.LINK);
		
		// create bit.ly service handler
		bitly = new BitlyHandler(this);
	}

	// OnClick handler
	public void onClick(View view)
	{
		if (view == btnOk) {
			new BitlyHandlerTask().execute();
		}
		else if (view == btnCancel) {
			onBackPressed();
		}
	}
	
	@Override
	protected void onPause() {
		dialog.dismiss();
		super.onPause();
		onBackPressed();
	}
	
	
	/**
	 * Creation of a new task to handle the "short-link" convertion
	 */
	private class BitlyHandlerTask extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			dialog.show(getString(R.string.bitlyService));
		}

		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			dialog.dismiss();
		}
		
		@Override
		protected String doInBackground(String... params)
		{
			String shareText = null;

			String shortUrl = bitly.getShortUrl(link);
			if (shortUrl == null)
			{
				// some error occured
				runOnUiThread(new Runnable() { public void run() {
					String text = getString(R.string.bitlyError);
					Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();	
				}});
			}
			else
			{
				// share 'description' is optional for the user;
				String description = txtDescription.getText().toString().trim();
				if (description.length() > 0)
					shareText = description + "\n" + shortUrl;
				else
					shareText = shortUrl;
				
				Intent shareIntent = new Intent();			
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

				try {
					startActivity(Intent.createChooser(shareIntent, getString(R.string.menu_share)));
				} catch (ActivityNotFoundException e) {
					e.printStackTrace();
				}
				
				// close the current activity 
				onBackPressed();
			}
			
			return null;
		}
	}
}