package it.rss.adapter;

import it.rss.activity.R;
import it.rss.parser.Feed;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class RssFeedAdapter extends ArrayAdapter<Feed>
{	
	private TextView txtId;
	private TextView txtTitle;
	private TextView txtLink;
	private TextView txtUnread;
		
	private Context context;
	private int resourceId;
	private List<Feed> feeds = new ArrayList<Feed>();
		
	// Default constructor
	public RssFeedAdapter(Context context, int resourceId, List<Feed> feeds)
	{
		super(context, resourceId, feeds);
		this.context = context;
		this.resourceId = resourceId;
		this.feeds = feeds;
	}
	
	@Override
	public int getCount() {
		return feeds.size();
	}
	
	@Override
	public Feed getItem(int index) {
		return feeds.get(index);
	}
		
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{		
		View row = convertView;
		Feed feed = getItem(position);

		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resourceId, parent, false);
		}
		
 		txtId = (TextView) row.findViewById(R.id.id);
		txtId.setText(""+feed.getId());
		
		txtTitle = (TextView) row.findViewById(R.id.title);
		txtTitle.setText(feed.getTitle());
		
		txtLink = (TextView) row.findViewById(R.id.link);
		txtLink.setText(feed.getLink());
		
		txtUnread = (TextView) row.findViewById(R.id.unread);
		txtUnread.setText("Unread: "+feed.getUnreadItems()+"/"+feed.getItems().size());
	
		// set bold text for unread items
		if (feed.getUnreadItems() > 0)
			txtUnread.setTextAppearance(context, R.style.BoldText);
		else
			txtUnread.setTextAppearance(context, R.style.UnreadText);
				
		return row;
	}
}