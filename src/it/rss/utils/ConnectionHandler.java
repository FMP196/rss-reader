package it.rss.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionHandler
{
	private Context context;
	private ConnectivityManager manager;

	// Default constructor
	public ConnectionHandler(Context context) {
		this.context = context;
	}

	/**
	 * Check if the device is connected to internet;
	 * @return - True if the device is connected, False otherwise;
	 */
	public boolean isConnected()
	{
		manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager != null)
		{
			NetworkInfo[] info = manager.getAllNetworkInfo();

			if (info != null)
			{
				for (int i=0; i<info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}

		return false;
	}
}