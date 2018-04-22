package it.rss.parser;

public class Item {
	
	private String title;
	private String description;
	private String link;
	private String pubdate;
	private boolean readed;
	private boolean favourite;
	
	public Item(String title, String description, String link, String pubdate) {
		this.title = title;
		this.description = description.substring(0, description.length()/3) + " ...";
		this.link = link;
		this.pubdate = pubdate;
		this.favourite = false;
		this.readed = false;
	}
	
	public Item(String title, String description, String link, String pubdate, boolean readed, boolean favourite) {
		this(title, description, link, pubdate);
		this.readed = readed;
		this.favourite = favourite;
	}

	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getLink() {
		return link;
	}
	
	public String getPubdate() {
		return pubdate;
	}
	
	public boolean isReaded() {
		return readed;
	}
	
	public boolean isFavourite() {
		return favourite;
	}
	
	public void setReaded(boolean readed) {
		this.readed = readed;
	}
	
	public void setFavourite(boolean favourite) {
		this.favourite = favourite;
	}	
	
	@Override
	public String toString() {
		return title;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		
		if (o == this)
			return true;
		
		if (o instanceof Item)
		{
			Item item = (Item) o;
			if ((item.title.equals(title)) && 
				(item.link.equals(link))) {
				return true;
			}
		}
		
		return false;
	}
}