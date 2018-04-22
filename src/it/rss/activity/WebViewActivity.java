package it.rss.activity;

import it.rss.utils.Tag;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.annotation.SuppressLint;
import android.content.Intent;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewActivity extends CustomActivity
{
	private String url;
	private Intent intent;
	private WebView webview;
	private WebSettings websettings;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		webview = (WebView) findViewById(R.id.webView);

		// replace the current handler
		webview.setWebViewClient(new WebViewClient());
		webview.setWebChromeClient(new WebChromeClient());
		
		websettings = webview.getSettings();
		websettings.setJavaScriptEnabled(true);
		websettings.setBuiltInZoomControls(true);
		
		// use cache if content is there, otherwise use network
		websettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		
		// get post link
		intent = getIntent();
		url = intent.getStringExtra(Tag.LINK);
		
		// load the given url
		webview.loadUrl(url);
	}
}