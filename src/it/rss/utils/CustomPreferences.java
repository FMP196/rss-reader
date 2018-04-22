package it.rss.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CustomPreferences
{	
	private Editor editor;
	private SharedPreferences preferences;
	
	public CustomPreferences(Context context) {
		this.preferences = context.getSharedPreferences(Tag.USER_PREFERENCES, Context.MODE_PRIVATE);
	}
	
	// GET Methods
	
	public int getUpdateFrequency() {
		return (preferences.getInt(Tag.FREQUENCY, Tag.FREQUENCY_DEFAULT));
	}
	
	public boolean isAutoUpdateEnabled() {
		return (preferences.getBoolean(Tag.AUTO_UPDATE, Tag.AUTO_UPDATE_DEFAULT));
	}
	
	public boolean isNotificationEnabled() {
		return (preferences.getBoolean(Tag.NOTIFICATION, Tag.NOTIFICATION_DEFAULT));
	}
	
	public boolean isFullscreenEnabled() {
		return (preferences.getBoolean(Tag.FULLSCREEN, Tag.FULLSCREEN_DEFAULT));
	}
	
	public boolean isBitlyDefaultAccountEnabled() {
		return (preferences.getBoolean(Tag.BITLY_DEFAULT, false));
	}
	
	public String getBitlyUsername() {
		return (preferences.getString(Tag.BITLY_USERNAME, Tag.BITLY_USERNAME_DEFAULT));
	}
	
	public String getBitlyKey() {
		return (preferences.getString(Tag.BITLY_KEY, Tag.BITLY_KEY_DEFAULT));
	}
	
	// SET Methods
	
	public void setUpdateFrequency(int updateFrequency) {
		editor = preferences.edit();
		editor.putInt(Tag.FREQUENCY, updateFrequency);
		editor.commit();
	}
	
	public void setAutoUpdate(boolean autoUpdate) {
		editor = preferences.edit();
		editor.putBoolean(Tag.AUTO_UPDATE, autoUpdate);
		editor.commit();
	}
	
	public void setNotification(boolean notification) {
		editor = preferences.edit();
		editor.putBoolean(Tag.NOTIFICATION, notification);
		editor.commit();
	}
	
	public void setFullscreen(boolean fullscreen) {
		editor = preferences.edit();
		editor.putBoolean(Tag.FULLSCREEN, fullscreen);
		editor.commit();
	}
	
	public void setBitlyAccount(String username, String key) {
		editor = preferences.edit();
		editor.putString(Tag.BITLY_USERNAME, username);
		editor.putString(Tag.BITLY_KEY, key);
		editor.commit();
	}
	
	public void setBitlyDefaultAccountEnabled(boolean enabled) {
		editor = preferences.edit();
		editor.putBoolean(Tag.BITLY_DEFAULT, enabled);
		editor.commit();
	}
}