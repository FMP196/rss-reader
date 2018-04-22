package it.rss.activity;

import java.util.List;
import it.rss.adapter.RssItemsAdapter;
import it.rss.database.DatabaseHelper;
import it.rss.parser.Item;
import it.rss.utils.CustomProgressDialog;
import it.rss.utils.Tag;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class DisplayItemsActivity extends CustomActivity implements OnItemClickListener, OnItemLongClickListener
{
	private ListView listView;

	private String feedId;
	private String itemLink;
	private DatabaseHelper database;
	private CustomProgressDialog dialog;
	private List<Item> items;
	
	private static final int MENU_READ = 0;
	private static final int MENU_UNREAD = 1;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display);
		
		// hiding 'Add Feed' button
		((ImageButton) findViewById(R.id.btnAdd)).setVisibility(View.GONE);

		// bind UI elements with XML
		listView = (ListView) findViewById(R.id.listView);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(this);
		listView.setOnItemLongClickListener(this);

		// create database
		database = new DatabaseHelper(getApplicationContext());

		dialog = new CustomProgressDialog(DisplayItemsActivity.this);
		
		// get the selected feed ID from the previous Activity
		Intent intent = getIntent();
		feedId = intent.getStringExtra(Tag.ID);		
		
		// fetch feeds from database
		new FetchRssItemsTask().execute(feedId);
	}

	/**
	 * Handle list view items click
	 */
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
	{
		if (parent == listView)
		{
			String link = ((TextView) view.findViewById(R.id.link)).getText().toString();
				
			// once the item has been clicked, we mark it as readed
			database.markItemAsRead(link);
		
			Intent intent = new Intent(this, WebViewActivity.class);
			intent.putExtra(Tag.LINK, link);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.display, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{
			case R.id.menu_mark_read:
				database.markItemsAsRead(Integer.parseInt(feedId));
				break;

			case R.id.menu_mark_unread:
				database.markItemsAsUnread(Integer.parseInt(feedId));
				break;
		}
		
		reloadActivity();
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	menu.setHeaderTitle(getString(R.string.menu_options)); 
    	
    	for (Item item : items) {
    		if (item.getLink().equals(itemLink)) {
    			if (item.isReaded() == true) {
    				menu.add(0, MENU_UNREAD, 0, "Mark as Unread");
    			}
    			else {
    				menu.add(0, MENU_READ, 0, "Mark as Read");
    			}
    		}
    	}
    	
    	getMenuInflater().inflate(R.menu.display_context, menu);       	
	}

	@Override
	public boolean onContextItemSelected(MenuItem menuItem)
	{
		super.onContextItemSelected(menuItem);
				
		switch (menuItem.getItemId())
		{
			case MENU_READ:
				database.markItemAsRead(itemLink);
				reloadActivity();
				break;

			case MENU_UNREAD:
				database.markItemAsUnread(itemLink);
				reloadActivity();
				break;

			case R.id.menu_share_post:
				Intent shareIntent = new Intent(this, SharePostActivity.class);
				shareIntent.putExtra(Tag.LINK, itemLink);
				startActivity(shareIntent);
		        break;
		}
		
		return true;
	}
	
	// Handle long click on list view item
	public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id)
	{
		if (parent == listView) {
			// extract "link" from the item selected
			TextView txtItemLink = (TextView) view.findViewById(R.id.link);
			itemLink = txtItemLink.getText().toString();
		}
		
		return false;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		reloadActivity();
	}

	/**
	 * Fetch rss items from the database and put them
	 * into the ListView
	 */
	class FetchRssItemsTask extends AsyncTask <String, List<Item>, String>
	{
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show(getString(R.string.fetchRssFeedItems));
		}
 
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
		}

		@Override
		protected String doInBackground(final String... params)
		{
			int id = Integer.parseInt(params[0]);

			// get all items from the database
			items = database.getItems(id);

			runOnUiThread(new Runnable() { public void run() {
				RssItemsAdapter adapter = new RssItemsAdapter(getApplicationContext(), R.layout.itemlist, items);
				registerForContextMenu(listView);
				listView.setAdapter(adapter);
			}});
			
			return null;
		}		
	}
}