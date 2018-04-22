package it.rss.database;

import it.rss.parser.Feed;
import it.rss.parser.Item;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{	
	public static final int DB_VERSION        = 1;
	public static final String DB_NAME        = "_rss";
	
	public static final String TABLE_FEED     = "_feed";
	public static final String TABLE_ITEM     = "_item";
	
	public static final String FEED_ID        = "_feed_id";
	public static final String FEED_TITLE     = "_feed_title";
	public static final String FEED_LINK      = "_feed_link";
	public static final String FEED_RSSLINK   = "_feed_rsslink";
	public static final String FEED_LASTDATE  = "_feed_lastdate";
	
	public static final String ITEM_ID        = "_item_id";
	public static final String ITEM_TITLE     = "_item_title";
	public static final String ITEM_DESCR     = "_item_description";
	public static final String ITEM_LINK      = "_item_link";
	public static final String ITEM_PUBDATE   = "_item_pubdate";
	public static final String ITEM_READED    = "_item_readed";
	public static final String ITEM_FAVOURITE = "_item_favourite";
	
	// Default constructor
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		String sql_create_feed_table = 
				"CREATE TABLE " + TABLE_FEED + "(" +
						FEED_ID + " INTEGER PRIMARY KEY, " +
						FEED_TITLE + " TEXT, " +
						FEED_LINK + " TEXT, " +
						FEED_RSSLINK + " TEXT, " +
						FEED_LASTDATE + " TEXT " +
				")";

		String sql_create_item_table =
				"CREATE TABLE " + TABLE_ITEM + "(" +
						ITEM_ID + " INTEGER, " +
						ITEM_TITLE + " TEXT, " +
						ITEM_DESCR + " TEXT, " +
						ITEM_LINK + " TEXT, " +
						ITEM_PUBDATE + " TEXT, " +
						ITEM_READED + " TEXT, " +
						ITEM_FAVOURITE + " TEXT " +
				")";

		db.execSQL(sql_create_feed_table);
		db.execSQL(sql_create_item_table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVers, int newVers)
	{
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEED);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
		onCreate(db);
	}

	/**
	 * Add a new Feed in the database
	 * @param feed - Feed
	 * @return True on operation successful, False if the
	 * feed already exists
	 */
	public boolean addFeed(Feed feed)
	{
		int feedId = 0;
		boolean result = false;
		String query = "SELECT * FROM " + TABLE_FEED + " WHERE " + FEED_RSSLINK + " = '" + feed.getRssLink() + "'";

		SQLiteDatabase db = getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		// check if feed already exists
		if (cursor.moveToFirst()) {
			feedId = cursor.getInt(0); // return 0 if feed doesn't exists
			cursor.close();
		}

		if (feedId == 0)
		{
			// Feed doesn't exists
						
			ContentValues feedValues = new ContentValues();
			feedValues.put(FEED_TITLE, feed.getTitle());
			feedValues.put(FEED_LINK, feed.getLink());
			feedValues.put(FEED_RSSLINK, feed.getRssLink());
			feedValues.put(FEED_LASTDATE, feed.getLastdate());
			
			// insert feed into the database
			db.insert(TABLE_FEED, null, feedValues);
			
			// queries again the database to update the 'feedId'
			// value and get the new one
			cursor = db.rawQuery(query, null);
			if (cursor.moveToFirst()) {
				feedId = cursor.getInt(0);
				cursor.close();
			}
			
			for (Item item : feed.getItems())
			{
				ContentValues itemValues = new ContentValues();
			
				itemValues.put(ITEM_ID, feedId);
				itemValues.put(ITEM_TITLE, item.getTitle());
				itemValues.put(ITEM_DESCR, item.getDescription());
				itemValues.put(ITEM_PUBDATE, item.getPubdate());
				itemValues.put(ITEM_LINK, item.getLink());
				itemValues.put(ITEM_READED, ""+item.isReaded()	);
				itemValues.put(ITEM_FAVOURITE, ""+item.isFavourite());
				
				db.insert(TABLE_ITEM, null, itemValues);
			}
			
			result = true;
		}
		
		db.close();
		return result;
	}
	
	/**
	 * Get all feeds from database
	 * @return - Feed list
	 */
	public List<Feed> getFeeds()
	{
		List<Feed> feedList = new ArrayList<Feed>();
		String query = "SELECT * FROM " + TABLE_FEED + " ORDER BY " + FEED_ID + " DESC";

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {				
				int id = cursor.getInt(0);
				String title = cursor.getString(1);
				String link = cursor.getString(2);
				String rsslink = cursor.getString(3);
				String lastdate = cursor.getString(4);
				
				Feed feed = new Feed(id, title, link, rsslink, lastdate);
				feed.setItems(getItems(id));
				
				feedList.add(feed);
			}
			while (cursor.moveToNext());
		}
		
		cursor.close();
		db.close();

		return feedList;
	}
	
	/**
	 * Extract a feed from the database
	 * @param feedId - Feed Identification
	 * @return - Feed
	 */
	public Feed getFeed(int feedId) {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_FEED + " WHERE " + FEED_ID + " = " + feedId;
		Cursor cursor = db.rawQuery(query, null);
		cursor.moveToFirst();
		
		String title = cursor.getString(1);
		String link = cursor.getString(2);
		String rsslink = cursor.getString(3);
		String lastdate = cursor.getString(4);
		
		Feed feed = new Feed(feedId, title, link, rsslink, lastdate);
		return feed;
	}
	
	/**
	 * Update the Feed lastdate
	 * @param feedId - Feed ID
	 * @param lastDate - Last date the feed was modified
	 */
	public void updateFeed(int feedId, String lastDate) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FEED_LASTDATE, lastDate);
		db.update(TABLE_FEED, values, FEED_ID + " = " + feedId, null);
		db.close();
	}
	
	
	/**
	 * Remove a feed from the database
	 * @param feed - Feed to remove
	 */
	public void deleteFeed(int feedId)
	{
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_FEED, FEED_ID + " = " + feedId, null);
		db.delete(TABLE_ITEM, ITEM_ID + " = " + feedId, null);
		db.close();
	}
	
	/**
	 * Add a new item into the database
	 * @param feedId - Feed ID
	 * @param item - Item
	 */
	public void addItem(int feedId, Item item)
	{
		SQLiteDatabase db = getWritableDatabase();
		ContentValues itemValues = new ContentValues();
	
		itemValues.put(ITEM_ID, feedId);
		itemValues.put(ITEM_TITLE, item.getTitle());
		itemValues.put(ITEM_DESCR, item.getDescription());
		itemValues.put(ITEM_PUBDATE, item.getPubdate());
		itemValues.put(ITEM_LINK, item.getLink());
		itemValues.put(ITEM_READED, ""+item.isReaded()	);
		itemValues.put(ITEM_FAVOURITE, ""+item.isFavourite());
		
		db.insert(TABLE_ITEM, null, itemValues);
		db.close();
	}
	
	/**
	 * Get all items from the database by ID
	 * @param ID - Item ID
	 * @return - Item list extracted from the database
	 */
	public List<Item> getItems(int feedId)
	{
		List<Item> items = new ArrayList<Item>();
		String query = "SELECT * FROM " + TABLE_ITEM + " WHERE " + ITEM_ID + " = " + feedId;

		SQLiteDatabase database = getReadableDatabase();
		Cursor cursor = database.rawQuery(query, null);

		if (cursor.moveToFirst()) {
			do {
				String title = cursor.getString(1);
				String description = cursor.getString(2);
				String link = cursor.getString(3);
				String pubdate = cursor.getString(4);
				boolean readed = Boolean.parseBoolean(cursor.getString(5).toLowerCase());
				boolean favourite = Boolean.parseBoolean(cursor.getString(6).toLowerCase());
				
				Item item = new Item(title, description, link, pubdate, readed, favourite);	
				items.add(item);
			}
			while (cursor.moveToNext());
		}

		cursor.close();
		database.close();

		return items;
	}

	/**
	 * Mark an Item as already readed
	 * @param itemLink - Item link
	 */
	public void markItemAsRead(String itemLink)
	{
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ITEM_READED, "true");
		db.update(TABLE_ITEM, values, ITEM_LINK + " = '" + itemLink + "'", null);
		db.close();
	}
	
	/**
	 * Mark an Item as unread
	 * @param itemLink - Item link
	 */
	public void markItemAsUnread(String itemLink)
	{
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ITEM_READED, "false");
		db.update(TABLE_ITEM, values, ITEM_LINK + " = '" + itemLink + "'", null);
		db.close();
	}
	
	/**
	 * Mark an Item as favourite
	 * @param itemLink - Item link
	 */
	public void markItemAsFavourite(String itemLink)
	{
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ITEM_FAVOURITE, "true");
		db.update(TABLE_ITEM, values, ITEM_LINK + " = '" + itemLink + "'", null);
		db.close();
	}
	
	/**
	 * Mark all Items as already readed
	 * @param feedId - Feed ID
	 */
	public void markItemsAsRead(int feedId)
	{
		SQLiteDatabase db = getWritableDatabase();		
		ContentValues values = new ContentValues();
		values.put(ITEM_READED, "true");
		db.update(TABLE_ITEM, values, ITEM_ID + " = " + feedId, null);
		db.close();
	}
	
	/**
	 * Mark all Items as unread
	 * @param feedId - Feed ID
	 */
	public void markItemsAsUnread(int feedId)
	{
		SQLiteDatabase db = getWritableDatabase();		
		ContentValues values = new ContentValues();
		values.put(ITEM_READED, "false");
		db.update(TABLE_ITEM, values, ITEM_ID + " = " + feedId, null);
		db.close();
	}
	
	/**
	 * Mark an Item as not favourite
	 * @param itemLink - Item link
	 */
	public void markItemAsNotFavourite(String itemLink)
	{
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(ITEM_FAVOURITE, "false");
		db.update(TABLE_ITEM, values, ITEM_LINK + " = '" + itemLink + "'", null);
		db.close();
	}

	/**
	 * Extract all the readed items from the database
	 * @param feedId - Feed ID
	 * @return - Readed items list
	 */
	public List<Item> getReadedItems(int feedId)
	{
		List<Item> items = new ArrayList<Item>();
		String query = "SELECT * FROM " + TABLE_ITEM + " WHERE " + ITEM_ID + " = " + feedId;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		if (cursor.moveToFirst()) {
			do {
				boolean readed = Boolean.parseBoolean(cursor.getString(5).toLowerCase());
				if (readed == true) {	
					String title = cursor.getString(1);
					String description = cursor.getString(2);
					String link = cursor.getString(3);
					String pubdate = cursor.getString(4);
					boolean favourite = Boolean.parseBoolean(cursor.getString(6).toLowerCase());
					
					Item item = new Item(title, description, link, pubdate, readed, favourite);
					items.add(item);
				}
			}
			while (cursor.moveToNext());
		}
		
		db.close();
		return items;
	}
	
	/**
	 * Extract all the unreaded items from the database
	 * @param feedId - Feed ID
	 * @return - Unreaded items list
	 */
	public List<Item> getUnreadItems(int feedId)
	{
		List<Item> items = new ArrayList<Item>();
		String query = "SELECT * FROM " + TABLE_ITEM + " WHERE " + ITEM_ID + " = " + feedId;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		if (cursor.moveToFirst()) {
			do {
				boolean readed = Boolean.parseBoolean(cursor.getString(5).toLowerCase());
				if (readed == false) {	
					String title = cursor.getString(1);
					String description = cursor.getString(2);
					String link = cursor.getString(3);
					String pubdate = cursor.getString(4);
					boolean favourite = Boolean.parseBoolean(cursor.getString(6).toLowerCase());
					
					Item item = new Item(title, description, link, pubdate, readed, favourite);
					items.add(item);
				}
			}
			while (cursor.moveToNext());
		}
		
		db.close();
		return items;
	}
	
	/**
	 * Extract all the favourite items from the database
	 * @param feedId - Feed ID
	 * @return - Favourite items list
	 */
	public List<Item> getFavouriteItems(int feedId)
	{
		List<Item> items = new ArrayList<Item>();
		String query = "SELECT * FROM " + TABLE_ITEM + " WHERE " + ITEM_ID + " = " + feedId;
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
		if (cursor.moveToFirst()) {
			do {
				boolean favourite = Boolean.parseBoolean(cursor.getString(6).toLowerCase());
				if (favourite == true) {	
					String title = cursor.getString(1);
					String description = cursor.getString(2);
					String link = cursor.getString(3);
					String pubdate = cursor.getString(4);
					boolean readed = Boolean.parseBoolean(cursor.getString(5).toLowerCase());
					
					Item item = new Item(title, description, link, pubdate, readed, favourite);
					items.add(item);
				}
			}
			while (cursor.moveToNext());
		}
		
		db.close();
		return items;
	}
}