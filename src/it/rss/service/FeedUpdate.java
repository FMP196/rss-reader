package it.rss.service;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import it.rss.database.DatabaseHelper;
import it.rss.parser.Feed;
import it.rss.parser.Item;
import it.rss.parser.Parser;

public class FeedUpdate
{	
	private Parser parser;
	private DatabaseHelper database;
	
	public FeedUpdate(Context context) {
		this.parser = new Parser();
		this.database = new DatabaseHelper(context);
	}
	
	/**
	 * Check for update
	 * @return - Number of Items found
	 */
	public List<Feed> update()
	{
		// get all feeds from the database
		List<Feed> feeds = database.getFeeds();
		
		// list of new feeds found
		List<Feed> newFeedList = new ArrayList<Feed>();

		// search for updated feeds;
		// the search criteria is based on the last publication date
		for (Feed localFeed : feeds)
		{
			// get database items
			Feed remoteFeed = parser.getFeed(localFeed.getRssLink(), false);
							
			// compare feeds date
			if (remoteFeed.getLastdate().equalsIgnoreCase(localFeed.getLastdate()) == false)
			{
				int count = 0;
				
				// we use this feed only for notifications so we don't really
				// need all informations (such as link, lastdate, etc,.)
				Feed newFeed = new Feed(localFeed.getTitle(), null, null, null);
				
				// compare remote posts with local posts
				for (Item remoteItem : remoteFeed.getItems()) {
					boolean found = false;

					for (Item localItem : localFeed.getItems()) {
						if (remoteItem.equals(localItem)) {
							found = true;
						}
					}

					if (found == false) {
						// the remote item is not present in the database;
						// so we provide to insert it inside the right feed list
						database.addItem(localFeed.getId(), remoteItem);
						newFeed.getItems().add(remoteItem);
						count++;
					}
				}
				
				// update feed's last date building
				database.updateFeed(localFeed.getId(), remoteFeed.getLastdate());
				
				if (count > 0)
					newFeedList.add(newFeed);
			}
		}

		return newFeedList;
	}
}