package it.rss.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatus
{
	private Context context;
	
	/**
	 * Default constructor
	 * @param context - Application context
	 */
	public ServiceStatus(Context context) {
		this.context = context;
	}
	
	/**
	 * Determine if a specific service is currently running
	 * @param servicename - Service name
	 * @return - True if the service is running, False otherwise.
	 */
	public boolean isRunning(String servicename)
	{
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo runningService : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (servicename.equals(runningService.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}