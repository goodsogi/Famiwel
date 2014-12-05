package com.gntsoft.famiwel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pluslibrary.PlusConstants;
import com.pluslibrary.R;
import com.pluslibrary.utils.PlusToaster;

/**
 * 웹뷰
 * 
 * @author jeff
 * 
 */
public class FWWebViewActivity extends Activity {
	private Intent mIntent;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plus_webview);
		
		mIntent = getIntent();
		String url = mIntent.getExtras().getString(PlusConstants.KEY_URL);
		WebView webView = (WebView) findViewById(R.id.webview);

		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setPluginState(PluginState.ON);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDisplayZoomControls(false);
		webSettings.setUserAgentString(webSettings.getUserAgentString() 
			    + " " + FWConstants.WEBVIEW_USER_AGENT);
		
		//아래 코드만 달면 팝업창이 뜸 
		webView.setWebChromeClient(new WebChromeClient());

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				PlusToaster.doIt(FWWebViewActivity.this, "url: " + url);
				
				

				return false;
			}
		});

		webView.loadUrl(url);
	}
	
//	@Override 
//
//    public void onStart() { 
//
//        super.onStart(); 
//
//        CookieSyncManager.createInstance(this);
//
//    }   
//	
//	@Override
//
//    public void onResume(){
//
//    	super.onResume();
//
//    	CookieSyncManager.getInstance().startSync();
//
//    }
//	
//	@Override
//
//    public void onPause(){
//
//    	super.onPause();
//
//    	CookieSyncManager.getInstance().stopSync();
//
//    }
//	

}
