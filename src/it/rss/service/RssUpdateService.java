package it.rss.service;

import java.util.List;
import it.rss.parser.Feed;
import it.rss.utils.ConnectionHandler;
import it.rss.utils.CustomPreferences;
import it.rss.notification.NotificationHandler;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RssUpdateService extends Service
{
	private FeedUpdate feedUpdate;
	private BackgroundThread backgroundThread;
	private CustomPreferences preferences;
		
	@Override
	public void onCreate() {
		super.onCreate();
		this.preferences = new CustomPreferences(getApplicationContext());
		this.feedUpdate = new FeedUpdate(getApplicationContext());
		this.backgroundThread = new BackgroundThread();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		backgroundThread.start();		
		Log.e("SERVICE_STATUS", "STARTED");
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		backgroundThread.stopThread();
		Log.e("SERVICE_STATUS", "STOPPED");
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Background thread for updating feeds
	 */
	private final class BackgroundThread extends Thread
	{
		private int frequency;
		private int delay;
		private boolean running;
		
		public BackgroundThread() {
			super("RssUpdateService");
		}
		
		@Override
		public void start() {
			running = true;
			super.start();
		}
		
		public void stopThread() {
			running = false;
		}

		@Override
		public void run()
		{
			frequency = preferences.getUpdateFrequency();
			delay = (frequency * 60 * 1000);
			
			while (running == true)
			{
				Log.e("SERVICE_STATUS", "RUNNING");

				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				if (new ConnectionHandler(getApplicationContext()).isConnected()) {
					List<Feed> feedList = feedUpdate.update();
					int count = feedList.size();
					if (count > 0) {
						String text = "";
						for (Feed feed : feedList) {
							text += feed.getTitle()+": "+feed.getItems().size()+", "; 
						}
						new NotificationHandler(getApplicationContext()).show(text, count);
					}
				}			
			}
		}
	}
}