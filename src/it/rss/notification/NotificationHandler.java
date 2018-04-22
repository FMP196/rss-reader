package it.rss.notification;

import it.rss.activity.R;
import it.rss.activity.RssReaderActivity;
import it.rss.utils.CustomPreferences;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHandler
{
	private Context context;
	private NotificationManager manager;
	private CustomPreferences preferences;
	
	public NotificationHandler(Context context) {
		this.context = context;
		this.preferences = new CustomPreferences(context);
		this.manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

	}
	
	/**
	 * Display a simple notification for feeds
	 */
	public void show(String text, int count)
	{
		int NOTIFICATION_ID = 0;
		String name = context.getString(R.string.app_name);
		String title = context.getString(R.string.newFeedsFound);
		
		// check if user has disabled notifications
		if (preferences.isNotificationEnabled() == false)
			return;

		Notification notification = new Notification(R.drawable.ic_rss, name, System.currentTimeMillis());
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.number = count;
		
		Intent intent = new Intent(context, RssReaderActivity.class);
		
		// resume the background activity, or open a new one if it is needed
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.LAUNCHER");
		
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);		
		notification.setLatestEventInfo(context, title, text, pendingIntent);
		
		manager.notify(NOTIFICATION_ID, notification);
	}
}