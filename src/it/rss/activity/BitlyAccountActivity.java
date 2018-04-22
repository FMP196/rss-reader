package it.rss.activity;

import it.rss.utils.CustomPreferences;
import it.rss.utils.Tag;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class BitlyAccountActivity extends CustomActivity implements OnClickListener, OnCheckedChangeListener
{
	private Button btnOk;
	private Button btnCancel;
	private EditText txtKey;
	private EditText txtUsername;
	private CheckBox chkBoxBitlyAccount;
	private CustomPreferences preferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bitly);

		// bind UI elements with XML
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtKey = (EditText) findViewById(R.id.txtKey);
		chkBoxBitlyAccount = (CheckBox) findViewById(R.id.checkBoxBitlyAccount);
		chkBoxBitlyAccount.setOnCheckedChangeListener(this);

		// get user preferences
		preferences = new CustomPreferences(this);
		
		// load user preferences
		chkBoxBitlyAccount.setChecked(preferences.isBitlyDefaultAccountEnabled());
		txtUsername.setText(preferences.getBitlyUsername());
		txtKey.setText(preferences.getBitlyKey());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		onBackPressed();
	}

	/**
	 * Handle button clicks ('OK' and 'Cancel')
	 * @param view - View
	 */
	public void onClick(View view)
	{
		if (view == btnOk)
		{
			String username = null;
			String key = null;
			boolean enabled = chkBoxBitlyAccount.isChecked();

			if (enabled == true)
			{
				// get default 'username' and 'key' values
				username = Tag.BITLY_USERNAME_DEFAULT;
				key = Tag.BITLY_KEY_DEFAULT;
			}
			else
			{
				// set user account details for bit.ly services
				username = txtUsername.getText().toString();
				key = txtKey.getText().toString();
			}

			preferences.setBitlyDefaultAccountEnabled(enabled);
			preferences.setBitlyAccount(username, key);
		}
		else if (view == btnCancel) {
			// do nothing
		}
		
		// after finish, return to the previous activity;
		// this function will be executed if the user clicks on both 
		// 'Ok' and 'Cancel' buttons
		onBackPressed();
	}

	/**
	 * Handle check box default account for bit.ly services
	 * If enabled the default account will be used (see "Tag.java"),
	 * otherwise the user can set his personal account
	 */
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (buttonView == chkBoxBitlyAccount)
		{
			// if the default account will be used, then disable
			// the "username" and "key" text fields and populate them
			// with the default "username" and "key" values
			txtKey.setEnabled(!isChecked);
			txtUsername.setEnabled(!isChecked);

			if (isChecked == true)
			{
				// the user chooses to use the default account for bit.ly services;
				// so we update the 'username' and 'key' text fields with the default values
				txtUsername.setText(preferences.getBitlyUsername());
				txtKey.setText(preferences.getBitlyKey());
			}
		}
	}
}