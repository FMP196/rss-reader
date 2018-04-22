package it.rss.parser;

import java.util.ArrayList;
import java.util.List;

public class Feed {

	private int id;
	private int unread;
	private String title;
	private String link;
	private String rsslink;
	private String lastdate;
	private List<Item> items;

	public Feed(String title, String link, String rsslink, String lastdate) {
		this.title = title;
		this.link = link;
		this.rsslink = rsslink;
		this.lastdate = lastdate;
		this.unread = 0;
		this.items = new ArrayList<Item>();
	}

	public Feed(int id, String title, String link, String rsslink, String lastdate) {
		this(title, link, rsslink, lastdate);
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getRssLink() {
		return rsslink;
	}

	public String getLastdate() {
		return lastdate;
	}
	
	public int getUnreadItems() {
		return unread;
	}
	
	public void setUnreadItems(int n) {
		this.unread = n;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}	
	
	@Override
	public String toString() {
		return title;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		
		if (o == this)
			return true;
		
		if (o instanceof Feed) {
			Feed feed = (Feed) o;
			if ((feed.title.equals(title)) && 
				(feed.link.equals(link)) &&
				(feed.rsslink.equals(rsslink)) &&
				(feed.lastdate.equals(lastdate))) {
				return true;
			}
		}
		
		return false;
	}
}