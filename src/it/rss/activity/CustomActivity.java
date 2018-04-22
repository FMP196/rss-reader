package it.rss.activity;

import it.rss.utils.CustomPreferences;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager.LayoutParams;

public class CustomActivity extends Activity
{	
	private CustomPreferences preferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preferences = new CustomPreferences(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (preferences.isFullscreenEnabled() == true) {
			getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	/**
	 * Reload the current activity
	 */
	public void reloadActivity() {
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}
}