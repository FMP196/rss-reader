package it.rss.activity;

import java.util.List;
import it.rss.notification.NotificationHandler;
import it.rss.parser.Feed;
import it.rss.parser.Item;
import it.rss.service.FeedUpdate;
import it.rss.service.RssUpdateService;
import it.rss.service.ServiceStatus;
import it.rss.utils.ConnectionHandler;
import it.rss.utils.CustomPreferences;
import it.rss.utils.CustomProgressDialog;
import it.rss.utils.Tag;
import it.rss.adapter.RssFeedAdapter;
import it.rss.database.DatabaseHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RssReaderActivity extends CustomActivity implements OnClickListener, OnItemClickListener
{
	private ListView listView;
	private TextView txtIntro;
	private ImageButton btnAdd;
	private ImageView imageView;
		
	private int[] ID;	
	private ServiceStatus serviceStatus;
	private FeedUpdate feedUpdate;
	private DatabaseHelper database;
	private CustomPreferences preferences;
	private NotificationHandler notification;
	private CustomProgressDialog dialog;
	private ConnectionHandler connection;
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// bind UI elements with XML
		btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		listView = (ListView) findViewById(R.id.listView);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(this);
		imageView = (ImageView) findViewById(R.id.imageView);
		imageView.setVisibility(View.GONE);	
		txtIntro = (TextView) findViewById(R.id.txtIntroduction);
		txtIntro.setVisibility(View.GONE);
		
		feedUpdate = new FeedUpdate(this);
		database = new DatabaseHelper(this);
		serviceStatus = new ServiceStatus(this);
		notification = new NotificationHandler(this);
		dialog = new CustomProgressDialog(this);
		connection = new ConnectionHandler(this);
		preferences = new CustomPreferences(this);

		// check to avoid the following situation:
		// user forced service to stop; if that happens, the application try
		// to restart the service itself
		if ((serviceStatus.isRunning("it.rss.service.RssUpdateService") == false) && (preferences.isAutoUpdateEnabled() == true)) {
			Intent intent = new Intent(this, RssUpdateService.class);
			startService(intent);
		}
				
		// load all stored feeds from the database
		new LoadRssFeedsTask().execute();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		reloadActivity();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)  {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId())
		{
			case R.id.menu_addrss:
				startActivityForResult(new Intent(this, AddRssActivity.class), Tag.ACTIVITY_RESULT);
				break;
		
			case R.id.menu_settings:
				startActivity(new Intent(this, SettingsActivity.class));
				break;
			
			case R.id.menu_refresh:
				new CheckForUpdatesTask().execute();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
    	menu.setHeaderTitle(database.getFeed(ID[info.position]).getTitle());   	
    	getMenuInflater().inflate(R.menu.main_context, menu);
    }

	@Override
    public boolean onContextItemSelected(MenuItem menuItem) {   
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuItem.getMenuInfo();
    	int feedId = ID[info.position];
    	
    	switch (menuItem.getItemId())
    	{
    		case R.id.menu_delete:
    			database.deleteFeed(feedId);
    			break;

    		case R.id.menu_mark_readed:
    			database.markItemsAsRead(feedId);
    			break;
    			
    		case R.id.menu_mark_unread:
    			database.markItemsAsUnread(feedId);
    			break;
    	}

    	reloadActivity();	
    	return super.onContextItemSelected(menuItem);
    }

	@Override
	protected void onActivityResult(int request, int result, Intent data) {
		super.onActivityResult(request, result, data);
        if (result == Tag.ACTIVITY_RESULT) {
        	reloadActivity();
        }
	}

	// Handle UI elements click
	public void onClick(View view) {
		if ((view == btnAdd) || (view == imageView)) {
			Intent intent = new Intent(this, AddRssActivity.class);
			startActivityForResult(intent, Tag.ACTIVITY_RESULT);
		}
	}

	// Handle list view item click
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		if (parent == listView)
		{
			TextView txtID = (TextView) view.findViewById(R.id.id);
			Intent intent = new Intent(this, DisplayItemsActivity.class);
			intent.putExtra(Tag.ID, txtID.getText().toString());
			startActivity(intent);
		}
	}
	
	@Override
	protected void onPause() {
		dialog.dismiss();
		super.onPause();
	}

	/**
	 * Task to check for new updates;
	 * (this is called when the user clicks on the 'Refresh Menu')
	 */
	class CheckForUpdatesTask extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show(getString(R.string.checkForUpdates));
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
		}

		@Override
		protected String doInBackground(String... params)
		{
			if (connection.isConnected() == false) {
				runOnUiThread(new Runnable() { public void run() {
					String text = getString(R.string.noInternetConnection);
					Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
				}});	
				return null;
			}
			
			List<Feed> feedList = feedUpdate.update();
			int count = feedList.size();
			if (count == 0)
			{
				// no feeds found
				runOnUiThread(new Runnable() { public void run() {
					String text = getString(R.string.noUpdates);
					Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
				}});
			}
			else if (count > 0)
			{
				String text = "";
				for (Feed feed : feedList) {
					text += feed.getTitle()+": ("+feed.getItems().size()+" new) "; 
				}
				
				notification.show(text, count);

				// update the UI (reload the current activity)
				runOnUiThread(new Runnable() { public void run() {
					reloadActivity();
				}});
			}

			return null;
		}	
	}

	/**
	 * Task to load store feeds from database
	 */
	class LoadRssFeedsTask extends AsyncTask<String, String, String>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show(getString(R.string.loadRssFeed));
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
		}

		@Override
		protected String doInBackground(String... params)
		{
			// update the list view 
			runOnUiThread(new Runnable() { public void run()
			{
				// get all feeds from the database
				List<Feed> feedList = database.getFeeds();				
				
				int size = feedList.size();
				
				// create an array with feed ID's
				ID = new int[size];

				for (int i=0; i<size; i++)
				{	
					// get feed
					Feed feed = feedList.get(i);
					
					// for each feed calculate the number of unread items
					List<Item> unreadItems = database.getUnreadItems(feed.getId());					
					feed.setUnreadItems(unreadItems.size());
					
					// populate the feed ID array
					ID[i] = feed.getId();
				}

				if (size == 0)
				{
					// hide some UI elements, because they are visible only if
					// there are some feeds available
					imageView.setVisibility(View.VISIBLE);
					txtIntro.setVisibility(View.VISIBLE);
				}
				else
				{
					// create a custom feed adapter for the feed listview
					RssFeedAdapter adapter = new RssFeedAdapter(getApplicationContext(), R.layout.feedlist, feedList);
					listView.setAdapter(adapter);
				}
			}});

			return null;
		}
	}

}