package com.gntsoft.famiwel.main;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Stack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gntsoft.famiwel.FWCommonFragment;
import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.FWWebpageUrls;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.login.LoginActivity;
import com.gntsoft.famiwel.server.AdParser;
import com.gntsoft.famiwel.server.FWApiConstants;
import com.gntsoft.famiwel.server.LeftMenuFirstLevelParser;
import com.gntsoft.famiwel.server.LeftMenuSecondLevelParser;
import com.gntsoft.famiwel.server.LeftMenuThirdLevelParser;
import com.gntsoft.famiwel.server.MenuModel;
import com.gntsoft.famiwel.server.UserNameParser;
import com.pluslibrary.PlusConstants;
import com.pluslibrary.server.PlusHttpClient;
import com.pluslibrary.server.PlusOnGetDataListener;
import com.pluslibrary.utils.PlusClickGuard;
import com.pluslibrary.utils.PlusDpPixelConverter;
import com.pluslibrary.utils.PlusInternectConnectionDetector;
import com.pluslibrary.utils.PlusLogger;
import com.pluslibrary.utils.PlusOnClickListener;
import com.pluslibrary.utils.PlusToaster;
import com.pluslibrary.utils.PlusViewHolder;

public class MainActivity extends FragmentActivity implements
		PlusOnGetDataListener {
	private final int GET_AD = 11;
	private View mLeftMenu;
	private View mPreviousIndicator;
	protected int mOpenedGroupPosition = -1;
	// 왼쪽 메뉴 데이터
	private final int GET_USER_NAME = 4;
	private final int GET_LEFT_MENU_FIRST = 2;
	private final int GET_LEFT_MENU_SECOND = 1;
	private final int GET_LEFT_MENU_THIRD = 3;
	private ArrayList<String> mFirstLevelDatas;
	private ViewPager mViewPager;
	private ViewPagerAdapter mFragmentAdapter;
	private Stack<Integer> mBackStack;
	private ArrayList<ArrayList<MenuModel>> mSecondLevelDatas;
	private ArrayList<ArrayList<ArrayList<MenuModel>>> mThirdLevelDatas;
	private LinearLayout mTopTab;
	private LinearLayout mTopTabNavi;
	private View mWebTopLine;
	private WebView mWebView;
	private String mWebUrl;

	public static final int VIEWPAGER_POSITION_HOME = 0;
	public static final int VIEWPAGER_POSITION_WELPANG = 1;
	public static final int VIEWPAGER_POSITION_BEST = 2;
	public static final int VIEWPAGER_POSITION_CHECK = 3;
	public static final int VIEWPAGER_POSITION_WELFARE = 4;
	protected static final String URL_WEB_SYNC = "http://famiwel.co.kr/_prozn/_system/m/login_web.php?";
	protected static final String URL_BLANK = "about:blank";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 인터넷 연결 체크
		if (!PlusInternectConnectionDetector.hasConnection(this))
			return;

		init();

		setOtherView();

		getUserName();

		// 왼쪽 drawer 생성
		getLeftMenuData();

		// 왼쪽 메뉴 설정 생성
		makeLeftSetting();

		// 버튼 리스너
		addButtonListener();
		// 처음에 광고보여주는 기능은 뺌
		// getAdData();

	}

	public void showWebpage(String url) {
		setOtherViewVisibility(false);
		setWebViewVisibility(true);
		loadUrlWebView(url);
	}

	/**
	 * 메인 로고를 눌렀을 때 메인화면으로 돌아감
	 * 
	 * @param v
	 */
	public void goBackMain(View v) {
		if (mWebView.isShown())
			exitWebView();
		mViewPager.setCurrentItem(0);

	}

	/**
	 * 왼쪽 메뉴 설정
	 * 
	 * @param v
	 */
	public void doSetting(View v) {
		RelativeLayout container = (RelativeLayout) findViewById(R.id.container_left_setting);
		if (v.isSelected()) {
			v.setSelected(false);
			container.setVisibility(View.GONE);
		} else {
			v.setSelected(true);
			container.setVisibility(View.VISIBLE);

		}

	}

	public void callCustomerService(View v) {
		Uri uri = Uri.parse("tel:1544-5792");
		Intent it = new Intent(Intent.ACTION_DIAL, uri);
		startActivity(it);

	}

	/**
	 * 로그아웃
	 * 
	 * @param v
	 */
	public void doLogout(View v) {
		new AlertDialog.Builder(this).setTitle("알림").setCancelable(false)
				.setMessage("로그아웃되었습니다. 이용해주셔서 감사합니다.")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						// 자동로그인, 아이디 저장 해지
						SharedPreferences sharedPreference = getSharedPreferences(
								FWConstants.PREF_NAME, Context.MODE_PRIVATE);
						Editor e = sharedPreference.edit();
						e.putBoolean(FWConstants.KEY_KEEP_LOGIN, false);
						e.putBoolean(FWConstants.KEY_SAVE_ID, false);
						e.commit();

						finish();

						Intent intent = new Intent(MainActivity.this,
								LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
								| Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);

					}
				})

				.show();

	}

	/**
	 * 왼쪽 메뉴 하단 버튼 클릭시 웹페이지 이동(주문배송/관심상품/장바구니/마이페이지)
	 * 
	 * @param v
	 */
	public void openWebpage(View v) {
		String url = null;
		switch (v.getId()) {

		case R.id.order:
			url = FWWebpageUrls.URL_MAIN + FWWebpageUrls.ORDER_DELIVERY;
			closeLeftMenu();
			break;
		case R.id.interest_product:
			url = FWWebpageUrls.URL_MAIN + FWWebpageUrls.INTEREST_PRODUCT;
			closeLeftMenu();
			break;
		case R.id.cart:
			url = FWWebpageUrls.URL_MAIN + FWWebpageUrls.CART;
			closeLeftMenu();
			break;
		case R.id.main_cart:
			url = FWWebpageUrls.URL_MAIN + FWWebpageUrls.CART;
			break;
		case R.id.main_mypage:
			url = FWWebpageUrls.URL_MAIN + FWWebpageUrls.MYPAGE;
			break;
		case R.id.mypage:
			url = FWWebpageUrls.URL_MAIN + FWWebpageUrls.MYPAGE;
			closeLeftMenu();
			break;
		case R.id.copyright_company:
			url = FWWebpageUrls.URL_MAIN2 + FWWebpageUrls.COPYRIGHT_COMPANY;
			break;
		case R.id.copyright_agreement:
			url = FWWebpageUrls.URL_MAIN2 + FWWebpageUrls.COPYRIGHT_AGREEMENT;
			break;
		case R.id.copyright_privacy:
			url = FWWebpageUrls.URL_MAIN2 + FWWebpageUrls.COPYRIGHT_PRIVACY;
			break;
		case R.id.copyright_customer_center:
			url = FWWebpageUrls.URL_MAIN2
					+ FWWebpageUrls.COPYRIGHT_CUSTOMER_CENTER;
			break;

		}

		showWebpage(url);

		// Intent intent = new Intent(this, FWWebViewActivity.class);
		// intent.putExtra(PlusConstants.KEY_URL, url);
		// startActivity(intent);

	}

	public void doSearch(String keyword) {
		if (keyword.equals(""))
			PlusToaster.doIt(this, "검색어를 입력해주세요");

		String encodedKeyword = null;
		try {
			encodedKeyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showWebpage(FWWebpageUrls.URL_MAIN2 + "search.php?ki=" + encodedKeyword);

	}

	/**
	 * fragment 변경
	 * 
	 */
	public void changeContent(int fragmentName) {
		switch (fragmentName) {
		case FWConstants.FRAGMENT_HOME:
			mViewPager.setCurrentItem(0);
			break;
		case FWConstants.FRAGMENT_WELPANG:
			mViewPager.setCurrentItem(1);
			break;
		case FWConstants.FRAGMENT_BEST:
			mViewPager.setCurrentItem(2);
			break;
		case FWConstants.FRAGMENT_CHECK:
			mViewPager.setCurrentItem(3);
			break;
		case FWConstants.FRAGMENT_WELFARE:
			mViewPager.setCurrentItem(4);
			break;

		}

	}

	/**
	 * 왼쪽 메뉴 열기
	 * 
	 * @param v
	 */
	public void toggleLeftMenu(View v) {
		PlusClickGuard.doIt(v);
		// mDrawerLayout.openDrawer(mLeftMenu);
		if (v.isSelected()) {
			// 닫음
			v.setSelected(false);
			mLeftMenu.setVisibility(View.GONE);
			// mMainContainer.startAnimation(mSlideOut);

		} else {
			// 열림
			v.setSelected(true);
			mLeftMenu.setVisibility(View.VISIBLE);
			// mMainContainer.startAnimation(mSlideIn);

		}
	}

	@Override
	public void onBackPressed() {
		if (mWebView.isShown()) {
			// 뒤로가기 누르면 무조건 종료(안전함)
			// exitWebView();
			// 웹뷰 뒤로가기 처리
			if (mWebView.canGoBack()) {
				mWebView.goBack();
			} else {
				exitWebView();
			}
		} else if (getFragmentManager().getBackStackEntryCount() != 0) {
			// popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE); <--
			// 맨 밑 fragment를 pop
			getFragmentManager().popBackStack();

		} else if (getFragmentManager().getBackStackEntryCount() == 0) {

			// 종료 확인창 띄움
			new AlertDialog.Builder(this)
					.setTitle("알림")
					.setMessage("앱을 종료하시겠습니까?")
					.setPositiveButton("확인",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

									finish();

								}
							})
					.setNegativeButton("취소",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							})
					.setOnCancelListener(
							new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
								}
							}).show();
		}
	}

	@Override
	public void onSuccess(Integer from, Object datas) {
		switch (from) {

		case GET_AD:
			checkIfShowAd((AdModel) datas);
			break;

		case GET_LEFT_MENU_FIRST:

			mFirstLevelDatas = (ArrayList<String>) datas;

			// 왼쪽 메뉴 두번째 레벨 메뉴명 가져오기
			new PlusHttpClient(this, this, false).execute(GET_LEFT_MENU_SECOND,
					FWApiConstants.GET_MENU, new LeftMenuSecondLevelParser());
			break;
		case GET_LEFT_MENU_SECOND:
			mSecondLevelDatas = (ArrayList<ArrayList<MenuModel>>) datas;

			// 왼쪽 메뉴 세번째 레벨 메뉴명 가져오기
			new PlusHttpClient(this, this, false).execute(GET_LEFT_MENU_THIRD,
					FWApiConstants.GET_MENU, new LeftMenuThirdLevelParser());

			break;

		case GET_LEFT_MENU_THIRD:
			mThirdLevelDatas = (ArrayList<ArrayList<ArrayList<MenuModel>>>) datas;
			makeLeftMenu();
			break;
		case GET_USER_NAME:
			showUserName((String) datas);
			break;
		default:
			break;
		}

	}

	private void checkIfShowAd(AdModel datas) {
		SharedPreferences sharedPreference = getSharedPreferences(
				FWConstants.PREF_NAME, Context.MODE_PRIVATE);

		// 다시보지 않기가 체크되어 있고 광고가 이전 광고와 같으면 광고창 표시 안함
		if (sharedPreference.getBoolean(FWConstants.KEY_NEVER_OPEN_AD, false)
				&& getPreAdDetailUrl().equals(datas.getDetailUrl()))
			return;

		showAdPopup(datas);

	}

	private void showAdPopup(AdModel datas) {
		// 팝업을 여백없이 전체화면으로 표시하기 위해 스타일 지정
		AdPopup popup = new AdPopup(this,
				android.R.style.Theme_Black_NoTitleBar_Fullscreen, datas);
		popup.show();

	}

	private String getPreAdDetailUrl() {
		SharedPreferences sharedPreference = getSharedPreferences(
				FWConstants.PREF_NAME, Context.MODE_PRIVATE);
		return sharedPreference.getString(FWConstants.PRE_AD_DETAIL_URL, "");
	}

	private void getUserName() {
		SharedPreferences sharedPreference = getSharedPreferences(
				FWConstants.PREF_NAME, Context.MODE_PRIVATE);
		String userId = sharedPreference.getString(FWConstants.KEY_USER_ID, "");

		new PlusHttpClient(this, this, false).execute(GET_USER_NAME,
				FWApiConstants.GET_USER_NAME + "?mid=" + userId,
				new UserNameParser());

	}

	private void getAdData() {

		new PlusHttpClient(this, this, false).execute(GET_AD,
				FWApiConstants.GET_AD, new AdParser());

	}

	private void init() {

		// 뒤로가기용
		mBackStack = new Stack<Integer>();
		mBackStack.push(0);

		mViewPager = (ViewPager) findViewById(R.id.container);
		mFragmentAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mFragmentAdapter);
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// mViewPager.getAdapter().notifyDataSetChanged();
				// indicator 변경
				mBackStack.push(position);
				changeTabIndicator(position);
				PlusLogger.doIt("onPageSelected position: " + position);
				// if(position <= 2) setScrollViewTop();

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				PlusLogger.doIt("arg0: " + arg0 + " arg1: " + arg1 + " arg2: "
						+ arg2);

			}

			@Override
			public void onPageScrollStateChanged(int position) {
				PlusLogger.doIt("onPageScrollStateChanged position: "
						+ position);

			}
		});

		// 탭 지시자 초기화
		changeTabIndicator(0);

		// 로그인 연동
		syncWebLogin();
	}

	protected void setScrollViewTop() {

		// getSupportFragmentManager().findFragmentByTag("android:switcher:" +
		// R.id.container + ":" + mViewPager.getCurrentItem());
		// 로 현재 fragment를 가져올 수 있음
		final FWCommonFragment currentFragment = (FWCommonFragment) getSupportFragmentManager()
				.findFragmentByTag(
						"android:switcher:" + R.id.container + ":"
								+ mViewPager.getCurrentItem());

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				currentFragment.focusScrollViewTop();

			}
		}, 500);
		// currentFragment.focusScrollViewTop();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		boolean isAd = intent.getBooleanExtra(FWConstants.KEY_IS_AD, false);
		if (isAd) {
			makeAdModel(intent);
		} else {

			String gubun = intent.getStringExtra(FWConstants.KEY_PUSH_GUBUN);
			String url = intent.getStringExtra(PlusConstants.KEY_URL);
			showPushWebpage(gubun, url);
		}

	}

	private void makeAdModel(Intent intent) {
		AdModel adModel = new AdModel();
		String imageUrl = intent.getStringExtra(FWConstants.KEY_AD_IMG_URL);
		String detailUrl = intent.getStringExtra(FWConstants.KEY_AD_DETAIL_URL);
		adModel.setImageUrl(imageUrl);
		adModel.setDetailUrl(detailUrl);

		checkIfShowAd(adModel);

	}

	private void showPushWebpage(String gubun, String url) {

		if (FWConstants.GUBUN_PUSH_MAIN.equals(gubun)) {
			mViewPager.setCurrentItem(0);
			showWebpage(url);

		} else if (FWConstants.GUBUN_PUSH_WELPANG.equals(gubun)) {
			mViewPager.setCurrentItem(1);
			showWebpage(url);
		} else if (FWConstants.GUBUN_PUSH_BEST.equals(gubun)) {
			mViewPager.setCurrentItem(2);
			showWebpage(url);
		} else if (FWConstants.GUBUN_PUSH_ATTEND.equals(gubun)) {
			mViewPager.setCurrentItem(3);
		}

		else if (FWConstants.GUBUN_PUSH_WELFARE.equals(gubun)) {
			mViewPager.setCurrentItem(4);
			showWebpage(url);
		} else if (FWConstants.GUBUN_PUSH_EVENT.equals(gubun)) {

		}

		// Intent intent = new Intent(this, FWWebViewActivity.class);
		// intent.putExtra(PlusConstants.KEY_URL, url);
		// startActivity(intent);

	}

	private void syncWebLogin() {
		// CookieSyncManager.createInstance(this);
		// CookieManager cookieManager = CookieManager.getInstance();
		// List<Cookie> cookies =
		// ((DefaultHttpClient)httpclient).getCookieStore().getCookies();
		// Log.e("cookie", "setSyncCookie start");
		// if (!cookies.isEmpty()) {
		// for (int i = 0; i < cookies.size(); i++) {
		// String cookieString = cookies.get(i).getName() + "="+
		// cookies.get(i).getValue();
		//
		// cookieManager.setCookie(domain, cookieString);
		// Log.e("cookie test : ", cookieString);
		// }
		// }
		// }

		// CookieSyncManager.createInstance(this);
		// CookieSyncManager.getInstance().startSync();

		SharedPreferences sharedPreferences = getSharedPreferences(
				FWConstants.PREF_NAME, Context.MODE_PRIVATE);

		String userId = sharedPreferences
				.getString(FWConstants.KEY_USER_ID, "");
		String userPassword = sharedPreferences.getString(
				FWConstants.KEY_USER_PASSWORD, "");
		mWebView = (WebView) findViewById(R.id.webview_for_sync);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDomStorageEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setPluginState(PluginState.ON);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDisplayZoomControls(false);
		webSettings.setUserAgentString(webSettings.getUserAgentString() + " "
				+ FWConstants.WEBVIEW_USER_AGENT);
		// 아래 코드만 달면 팝업창이 뜸
		mWebView.setWebChromeClient(new WebChromeClient());

		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				return false;
			}

			// WebView가 로딩이 끝나면 시작

			public void onPageFinished(final WebView view, String url) {

				// CookieSyncManager.getInstance().sync();
				//
				// if(url.contains(URL_WEB_SYNC) ) {
				// PlusToaster.doIt(MainActivity.this, "clearHistory");
				// view.post(new Runnable() {
				//
				// @Override
				// public void run() {
				// view.clearHistory();
				//
				// }
				// });
				// }

				if (url.contains(URL_WEB_SYNC)) {

					CookieSyncManager.getInstance().sync();
					mWebView.loadUrl(URL_BLANK);
				}

				// 페이지 로딩이 끝나야 onPageFinished가 호출되는 데 긴 웹페이지의 경우 로딩이 완료되지 않아
				// view.clearHistory();가
				// 호출되지 않았을 때 뒤로가기 누르면 공백 페이지 보이는 오류 발생 가능
				// url.equals(URL_BLANK)||url.equals(mWebUrl) 만 제대로 작동
				if (url.equals(URL_BLANK) || url.equals(mWebUrl)) {
					// onPageFinished에서만 제대로 작동함
					view.clearHistory();
				}

				// if(url.contains(URL_WEB_SYNC) || url.equals(URL_BLANK)) {
				// PlusToaster.doIt(MainActivity.this, "clearHistory");
				// view.clearHistory();
				// }

			}

		});

		mWebView.loadUrl(URL_WEB_SYNC + "mid=" + userId + "&pass="
				+ userPassword);

	}

	/**
	 * 왼쪽 메뉴 설정 생성
	 */
	private void makeLeftSetting() {

		SharedPreferences sharedPreference = getSharedPreferences(
				FWConstants.PREF_NAME, Context.MODE_PRIVATE);
		final Editor e = sharedPreference.edit();
		// 자동로그인
		CheckBox autoLogin = (CheckBox) findViewById(R.id.checkbox_autologin);
		autoLogin.setChecked(sharedPreference.getBoolean(
				FWConstants.KEY_KEEP_LOGIN, false));
		autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				e.putBoolean(FWConstants.KEY_KEEP_LOGIN, isChecked);
				e.commit();
			}
		});
		// 푸시 알림 받기
		CheckBox enableAlarm = (CheckBox) findViewById(R.id.checkbox_alarm);
		enableAlarm.setChecked(sharedPreference.getBoolean(
				FWConstants.KEY_ENABLE_ALARM, true));
		enableAlarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				e.putBoolean(FWConstants.KEY_ENABLE_ALARM, isChecked);
				e.commit();
			}
		});
	}

	/**
	 * 탭바 인디게이터 변경
	 * 
	 * @param position
	 */
	private void changeTabIndicator(int position) {

		if (mPreviousIndicator != null)
			mPreviousIndicator.setSelected(false);

		View view = null;

		switch (position) {

		case 0:
			view = (View) findViewById(R.id.indicator_tab_home);
			break;
		case 1:
			view = (View) findViewById(R.id.indicator_tab_best);
			break;
		case 2:
			view = (View) findViewById(R.id.indicator_tab_seven);
			break;
		case 3:
			view = (View) findViewById(R.id.indicator_tab_check);
			break;
		case 4:
			view = (View) findViewById(R.id.indicator_tab_coupon);
			break;

		}

		view.setSelected(true);
		mPreviousIndicator = view;
	}

	private void addButtonListener() {
		// 액션바 검색 아이콘
		final Button search = (Button) findViewById(R.id.search);

		final LinearLayout searchBar = (LinearLayout) findViewById(R.id.top_search_bar);

		final EditText searchInput = (EditText) findViewById(R.id.top_search_input);
		searchInput.setFocusable(false);

		search.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				if (search.isSelected()) {
					search.setSelected(false);
					searchInput.setFocusable(false);
					searchInput.setText("");
					searchBar.setVisibility(View.GONE);
				} else {
					search.setSelected(true);
					searchInput.setFocusableInTouchMode(true);
					searchInput.setFocusable(true);
					searchBar.setVisibility(View.VISIBLE);
				}

			}
		});

		searchInput
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							doSearch(v.getText().toString());
							return true;
						}
						return false;
					}
				});

		Button searchBtn = (Button) findViewById(R.id.top_search_btn);
		searchBtn.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				doSearch(searchInput.getText().toString());

			}
		});

		// 홈
		TextView home = (TextView) findViewById(R.id.tab_home);
		home.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				// changeTabIndicator(0);
				changeContent(FWConstants.FRAGMENT_HOME);

			}
		});
		// 베스트
		TextView best = (TextView) findViewById(R.id.tab_best);
		best.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				// changeTabIndicator(2);
				changeContent(FWConstants.FRAGMENT_BEST);

			}
		});
		// 웰팡
		TextView welpang = (TextView) findViewById(R.id.tab_welpang);
		welpang.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				// changeTabIndicator(1);
				changeContent(FWConstants.FRAGMENT_WELPANG);

			}
		});
		// 출석체크
		TextView check = (TextView) findViewById(R.id.tab_check);
		check.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				// changeTabIndicator(3);
				changeContent(FWConstants.FRAGMENT_CHECK);

			}
		});
		// 복지존
		TextView coupon = (TextView) findViewById(R.id.tab_welfare);
		coupon.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				// changeTabIndicator(4);
				changeContent(FWConstants.FRAGMENT_WELFARE);

			}
		});
	}

	protected void closeLeftMenu() {
		Button menuButton = (Button) findViewById(R.id.left_menu);
		menuButton.setSelected(false);
		mLeftMenu.setVisibility(View.GONE);

	}

	/**
	 * 수동으로 메뉴 생성
	 */
	private void getLeftMenuData() {
		mLeftMenu = findViewById(R.id.left_drawer);

		// 왼쪽 메뉴 주 메뉴명 가져오기
		new PlusHttpClient(this, this, false).execute(GET_LEFT_MENU_FIRST,
				FWApiConstants.GET_MENU, new LeftMenuFirstLevelParser());

	}

	private void showUserName(String userName) {
		TextView userNameView = (TextView) findViewById(R.id.user_name);
		userNameView.setText(userName);

	}

	/**
	 * 3단계 레벨
	 */
	private void makeLeftMenu() {
		final ExpandableListView elist = (ExpandableListView) findViewById(R.id.elist_left_drawer);
		elist.setAdapter(new FirstLevelAdapter());
	}

	public class FirstLevelAdapter extends BaseExpandableListAdapter {
		protected LayoutInflater mLayoutInflater;

		public FirstLevelAdapter() {
			super();

			mLayoutInflater = LayoutInflater.from(MainActivity.this);

		}

		@Override
		public Object getChild(int arg0, int arg1) {
			return arg1;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			CustomExpandableListview ce = new CustomExpandableListview(
					MainActivity.this);
			ce.setAdapter(new SecondLevelAdapter(groupPosition, childPosition));
			ce.setGroupIndicator(null);
			return ce;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mSecondLevelDatas.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupPosition;
		}

		@Override
		public int getGroupCount() {
			return mFirstLevelDatas.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.elist_item_first, parent, false);
			}

			TextView category = PlusViewHolder.get(convertView, R.id.category);
			category.setText(mFirstLevelDatas.get(groupPosition));

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
	}

	public class CustomExpandableListview extends ExpandableListView {
		int intGroupPosition, intChildPosition, intGroupid;

		public CustomExpandableListview(Context context) {
			super(context);
		}

		/**
		 * 아래 코드가 없으면 작동을 안함
		 */
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(
					PlusDpPixelConverter.doIt(MainActivity.this, 250),
					MeasureSpec.AT_MOST);
			// 최대로 넉넉하게 잡음
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(2000,
					MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	public class SecondLevelAdapter extends BaseExpandableListAdapter {
		protected LayoutInflater mLayoutInflater;
		private int mFirstLevelPosition;
		private int mSecondLevelPosition;

		public SecondLevelAdapter(int groupPosition, int childPosition) {
			super();
			mFirstLevelPosition = groupPosition;
			mSecondLevelPosition = childPosition;
			mLayoutInflater = LayoutInflater.from(MainActivity.this);

		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.elist_item_third, parent, false);
			}
			LinearLayout container = PlusViewHolder.get(convertView,
					R.id.container_third_menu);
			container.setOnClickListener(new PlusOnClickListener() {

				@Override
				protected void doIt() {

					closeLeftMenu();

					showWebpage(FWWebpageUrls.URL_MAIN
							+ mThirdLevelDatas.get(mFirstLevelPosition)
									.get(mSecondLevelPosition)
									.get(childPosition).link);

					// // 웹뷰로 이동
					// Intent intent = new Intent(MainActivity.this,
					// FWWebViewActivity.class);
					// intent.putExtra(
					// PlusConstants.KEY_URL,
					// FWWebpageUrls.URL_MAIN
					// + mThirdLevelDatas.get(mFirstLevelPosition)
					// .get(mSecondLevelPosition)
					// .get(childPosition).link);
					// startActivity(intent);

				}
			});
			TextView title = PlusViewHolder.get(convertView, R.id.title);
			title.setText(mThirdLevelDatas.get(mFirstLevelPosition)
					.get(mSecondLevelPosition).get(childPosition).title);

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return mThirdLevelDatas.get(mFirstLevelPosition)
					.get(mSecondLevelPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupPosition;
		}

		@Override
		public int getGroupCount() {
			// return mSecondLevelDatas.get(mFirstLevelPosition).size();
			return 1;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(
						R.layout.elist_item_second, parent, false);
			}

			TextView title = PlusViewHolder.get(convertView, R.id.title);
			title.setText(mSecondLevelDatas.get(mFirstLevelPosition).get(
					mSecondLevelPosition).title);

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return true;
		}

	}

	private void setOtherView() {
		mTopTab = (LinearLayout) findViewById(R.id.top_tab);
		mTopTabNavi = (LinearLayout) findViewById(R.id.indicator_top_tab);
		mWebTopLine = (View) findViewById(R.id.top_line_web);
		mWebView = (WebView) findViewById(R.id.webview_for_sync);

	}

	private void setOtherViewVisibility(boolean isVisible) {
		mTopTab.setVisibility(isVisible ? View.VISIBLE : View.GONE);
		mTopTabNavi.setVisibility(isVisible ? View.VISIBLE : View.GONE);
		mViewPager.setVisibility(isVisible ? View.VISIBLE : View.GONE);

	}

	private void setWebViewVisibility(boolean isVisible) {
		mWebTopLine.setVisibility(isVisible ? View.VISIBLE : View.GONE);
		mWebView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
		// LinearLayout.LayoutParams layoutParams = new
		// LinearLayout.LayoutParams(
		// isVisible ? LayoutParams.MATCH_PARENT : 0,
		// isVisible ? LayoutParams.MATCH_PARENT : 0);
		// mWebView.setLayoutParams(layoutParams);

	}

	private void exitWebView() {
		setOtherViewVisibility(true);
		setWebViewVisibility(false);
		// 이전 페이지가 보여 빈페이지를 로딩
		mWebView.loadUrl(URL_BLANK);

	}

	private void loadUrlWebView(String url) {

		mWebUrl = url;
		mWebView.loadUrl(url);

	}
}
