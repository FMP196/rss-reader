package it.rss.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class CustomProgressDialog
{	
	private ProgressDialog dialog;
	private boolean onProgress;
	
	public CustomProgressDialog(Context context) {
		this.onProgress = false;
		this.dialog = new ProgressDialog(context);
	}
	
	public void show(String text) {
		dialog.setMessage(text);
		dialog.setIndeterminate(false);
		dialog.setCancelable(false);
		onProgress = true;
		dialog.show();
	}
	
	public void dismiss() {
		if ((onProgress == true) && (dialog != null)) {
			dialog.dismiss();
			onProgress = false;
		}
	}
}