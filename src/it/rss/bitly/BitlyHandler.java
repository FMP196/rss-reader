package it.rss.bitly;

import it.rss.bitly.BitlyAndroid;
import it.rss.utils.CustomPreferences;
import android.content.Context;

public class BitlyHandler
{	
	private CustomPreferences preferences;
	
	public BitlyHandler(Context context) {
		preferences = new CustomPreferences(context);
	}
	
	/**
	 * Convert a "long-link" to a "short-link" using the bit.ly service
	 * @param url - URL to convert
	 * @return - Short link
	 */
	public String getShortUrl(String url)
	{
		String shortUrl = null;
		BitlyAndroid bitly = new BitlyAndroid(preferences.getBitlyUsername(), preferences.getBitlyKey());
		
		try {
			shortUrl = bitly.getShortUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return shortUrl;
	}
}