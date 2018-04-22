package it.rss.activity;

import it.rss.service.RssUpdateService;
import it.rss.utils.CustomPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.content.Intent;

public class SettingsActivity extends CustomActivity implements OnSeekBarChangeListener, OnCheckedChangeListener, OnClickListener 
{
	private SeekBar seekBarFreq;
	private TextView txtFrequencyRefresh;
	private CheckBox chkBoxAutoUpdate;
	private CheckBox chkBoxNotification;
	private CheckBox chkBoxFullscreen;
	private ImageView imageBitlyAccount;

	private Intent intentService;
	private CustomPreferences preferences;
	private boolean SELF_FREQUENCY;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		// hiding 'Add Feed' button
		((ImageButton) findViewById(R.id.btnAdd)).setVisibility(View.GONE);
		
		// binding UI element with XML objects
		txtFrequencyRefresh = (TextView) findViewById(R.id.txtFrequencyRefresh);
		
		chkBoxAutoUpdate = (CheckBox) findViewById(R.id.checkBoxAutoUpdate);
		chkBoxAutoUpdate.setOnClickListener(this);
		
		chkBoxNotification = (CheckBox) findViewById(R.id.checkBoxNotification);
		chkBoxNotification.setOnCheckedChangeListener(this);

		chkBoxFullscreen = (CheckBox) findViewById(R.id.checkBoxFullscreen);
		chkBoxFullscreen.setOnCheckedChangeListener(this);
		
		seekBarFreq = (SeekBar) findViewById(R.id.seekBarFreq);
		seekBarFreq.setOnSeekBarChangeListener(this);
		
		imageBitlyAccount = (ImageView) findViewById(R.id.imbBitlyAccount);
		imageBitlyAccount.setOnClickListener(this);
 
		preferences = new CustomPreferences(this);

		// these instructions are used to avoid some function calls
		// when the activity is created
		SELF_FREQUENCY = true;
		if (preferences.getUpdateFrequency() == seekBarFreq.getProgress()) {
			// here we check if the progress bar value is the same
			// as the last time the app was closed
			SELF_FREQUENCY = false;
		}
		
		// update UI
		chkBoxAutoUpdate.setChecked(preferences.isAutoUpdateEnabled());
		chkBoxNotification.setChecked(preferences.isNotificationEnabled());
		chkBoxFullscreen.setChecked(preferences.isFullscreenEnabled());
		seekBarFreq.setEnabled(preferences.isAutoUpdateEnabled());
		seekBarFreq.setProgress(preferences.getUpdateFrequency());
		
		// create intent service to update feeds
		intentService = new Intent(this, RssUpdateService.class);
	}

	// Handle check/uncheck notification and auto update check box
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView == chkBoxNotification) {
			preferences.setNotification(isChecked);
		}
		else if (buttonView == chkBoxFullscreen) {
			preferences.setFullscreen(isChecked);
		}
	}

	// Handle button click
	public void onClick(View view) {
		if (view == imageBitlyAccount) {
			// start bit.ly account activity
			startActivity(new Intent(this, BitlyAccountActivity.class));
		}
		else if (view == chkBoxAutoUpdate) {
			boolean isChecked = chkBoxAutoUpdate.isChecked();
			seekBarFreq.setEnabled(isChecked);
			preferences.setAutoUpdate(isChecked);
			
			if (isChecked == true)
				startService(intentService);
			else
				stopService(intentService);	
		}
	}

	// Handle progress bar value while moving
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		String s = getString(R.string.updateEvery) + " " + progress; 
		if (progress <= 1) { 
			s += " " + getString(R.string.hour);
		} else { 
			s += " " + getString(R.string.hours);
		}
		txtFrequencyRefresh.setText(s);
	}

	// Handle progress bar value on start tracking
	public void onStartTrackingTouch(SeekBar seekBar) { }

	// Handle progress bar value on stop tracking
	public void onStopTrackingTouch(SeekBar seekBar) {
		if (seekBar.getProgress() == 0)
			seekBarFreq.setProgress(1);
		
		preferences.setUpdateFrequency(seekBar.getProgress());

		if (SELF_FREQUENCY == false) {
			boolean isChecked = chkBoxAutoUpdate.isChecked();
			if (isChecked == true) {
				stopService(intentService);
				startService(intentService);
			}
		}
		
		SELF_FREQUENCY = false;
	}
}