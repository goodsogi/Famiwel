package com.gntsoft.famiwel.check;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gntsoft.famiwel.FWCommonFragment;
import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.main.MainActivity;
import com.gntsoft.famiwel.server.FWApiConstants;

public class CheckFragment extends FWCommonFragment {

	public CheckFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//((MainActivity)mActivity).setContainerViewVisibility(true);
		View rootView = inflater.inflate(R.layout.fragment_check, container,
				false);

		return rootView;
	}

	private void showWebpage() {
		// if (((MainActivity) mActivity).getViewpagerCurrentPosition() !=
		// MainActivity.VIEWPAGER_POSITION_CHECK)
		// return;
		if (mActivity == null)
			return;
		WebView webView = (WebView) mActivity.findViewById(R.id.webview_check);
		if (webView == null)
			return;
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
		// 아래 코드만 달면 팝업창이 뜸
		webView.setWebChromeClient(new WebChromeClient());

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				return false;
			}
		});
		webView.loadUrl(FWApiConstants.CHECK_ATTENDANCE);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		showWebpage();
	}

	@Override
	protected void addListenerButton() {

	}

	@Override
	protected void notifyListView() {
		// TODO Auto-generated method stub
		
	}

}
