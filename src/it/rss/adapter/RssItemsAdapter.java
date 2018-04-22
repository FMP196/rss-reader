package it.rss.adapter;

import java.util.ArrayList;
import java.util.List;
import it.rss.activity.R;
import it.rss.database.DatabaseHelper;
import it.rss.parser.Item;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RssItemsAdapter extends ArrayAdapter<Item>
{	
	private TextView txtTitle;
	private TextView txtDescription;
	private TextView txtLink;
	private TextView txtPubdate;
	
	private int resourceId;
	private Context context;
	private DatabaseHelper database;
	private List<Item> items = new ArrayList<Item>();
	
	public RssItemsAdapter(Context context, int resourceId, List<Item> items)
	{
		super(context, resourceId, items);
		this.context = context;
		this.items = items;
		this.resourceId = resourceId;
		this.database = new DatabaseHelper(context);
	}
	
	@Override
	public int getCount() {
		return items.size();
	}
	
	@Override
	public Item getItem(int index) {
		return items.get(index);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		final Item item = getItem(position);		
		
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resourceId, parent, false);
		}
		
		txtTitle = (TextView) row.findViewById(R.id.title);
		txtTitle.setText(item.getTitle());
		
		//android.R.drawable.title_bar_tall
		
		txtDescription = (TextView) row.findViewById(R.id.description);
		txtDescription.setText(item.getDescription());
		
		txtLink = (TextView) row.findViewById(R.id.link);
		txtLink.setText(item.getLink());
		
		txtPubdate = (TextView) row.findViewById(R.id.pubdate);
		txtPubdate.setText(item.getPubdate());
				
		final ImageView imgFavourite = (ImageView) row.findViewById(R.id.favorite);
		imgFavourite.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (item.isFavourite() == true) {
					imgFavourite.setImageResource(android.R.drawable.star_off);
					item.setFavourite(false);
					database.markItemAsNotFavourite(item.getLink());
				}
				else {
					imgFavourite.setImageResource(android.R.drawable.star_on);
					item.setFavourite(true);
					database.markItemAsFavourite(item.getLink());
				}
			}
		});
		
		// change image for unread items and set bold text for item's title
		if (item.isReaded() == false) {
			txtTitle.setTextAppearance(context, R.style.BoldText);
		}
		else {
			txtTitle.setTextAppearance(context, R.style.TitleText);
		}

		// add a "star" icon on favourite items
		if (item.isFavourite() == true)
			imgFavourite.setImageResource(android.R.drawable.star_on);
		else
			imgFavourite.setImageResource(android.R.drawable.star_off);
		
		return row;
	}
	
}