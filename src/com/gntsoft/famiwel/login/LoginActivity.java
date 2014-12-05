package com.gntsoft.famiwel.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.FWGcmRegister;
import com.gntsoft.famiwel.FWWebViewActivity;
import com.gntsoft.famiwel.FWWebpageUrls;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.main.MainActivity;
import com.gntsoft.famiwel.server.FWApiConstants;
import com.pluslibrary.PlusConstants;
import com.pluslibrary.server.PlusHttpClient;
import com.pluslibrary.server.PlusInputStreamStringConverter;
import com.pluslibrary.server.PlusOnGetDataListener;
import com.pluslibrary.utils.PlusClickGuard;
import com.pluslibrary.utils.PlusToaster;

public class LoginActivity extends Activity implements PlusOnGetDataListener {

	private static final int LOG_IN = 0;

	private String mID;
	private SharedPreferences mSharedPreference;
	private String mPassword;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		showLoginLayout();

	}

	private void showLoginLayout() {
		setContentView(R.layout.activity_login);
		mSharedPreference = getSharedPreferences(FWConstants.PREF_NAME,
				Context.MODE_PRIVATE);
		// 아이디저장 처리
		if (mSharedPreference.getBoolean(FWConstants.KEY_SAVE_ID, false)) {

			showID();
			return;
		}

	}

	/**
	 * 아이디 표시
	 */
	private void showID() {
		EditText idInput = (EditText) findViewById(R.id.id_input);
		idInput.setText(mSharedPreference
				.getString(FWConstants.KEY_USER_ID, ""));

		CheckBox saveId = (CheckBox) findViewById(R.id.save_id);
		saveId.setChecked(true);

	}

	/**
	 * 메인으로 이동
	 */
	private void goMain() {
		finish();
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

	}

	/**
	 * 회원가입
	 * 
	 * @param v
	 */
	public void signUp(View v) {

		PlusClickGuard.doIt(v);
		Intent intent = new Intent(this, FWWebViewActivity.class);
		intent.putExtra(PlusConstants.KEY_URL, FWWebpageUrls.URL_MAIN
				+ FWWebpageUrls.JOIN_MEMBERSHIP);
		startActivity(intent);

	}

	/**
	 * 아이디찾기
	 * 
	 * @param v
	 */
	public void findID(View v) {
		PlusClickGuard.doIt(v);

		Intent intent = new Intent(this, FWWebViewActivity.class);
		intent.putExtra(PlusConstants.KEY_URL, FWWebpageUrls.URL_MAIN
				+ FWWebpageUrls.FIND_ID);
		startActivity(intent);
	}

	/**
	 * 비밀번호찾기
	 * 
	 * @param v
	 */
	public void findPassword(View v) {
		PlusClickGuard.doIt(v);
		Intent intent = new Intent(this, FWWebViewActivity.class);
		intent.putExtra(PlusConstants.KEY_URL, FWWebpageUrls.URL_MAIN
				+ FWWebpageUrls.FIND_PASSWORD);
		startActivity(intent);
	}

	/**
	 * 로그인
	 * 
	 * @param v
	 */
	public void doLogin(View v) {

		PlusClickGuard.doIt(v);
		// 아이디
		EditText idInput = (EditText) findViewById(R.id.id_input);
		mID = idInput.getText().toString();

		if (mID.equals("")) {
			PlusToaster.doIt(this, "아이디를 입력해주세요");
			return;
		}
		// 비밀번호
		EditText passwordInput = (EditText) findViewById(R.id.password_input);
		mPassword = passwordInput.getText().toString();

		if (mPassword.equals("")) {
			PlusToaster.doIt(this, "비밀번호를 입력해주세요");
			return;
		}

		FWGcmRegister gcmRegister = new FWGcmRegister(this);
		String deviceId = gcmRegister.getRegistrationId();

		new PlusHttpClient(this, this, false).execute(LOG_IN,
				FWApiConstants.LOG_IN + "?mid=" + mID + "&pass=" + mPassword
						+ "&deviceId=" + deviceId,
				new PlusInputStreamStringConverter());

	}

	@Override
	public void onSuccess(Integer from, Object datas) {
		switch (from) {
		case LOG_IN:

			if (((String) datas).contains("OK")) {
				PlusToaster.doIt(LoginActivity.this, "로그인했습니다");

				Editor e = mSharedPreference.edit();
				// 로그인유지 저장
				CheckBox autoLogin = (CheckBox) findViewById(R.id.keep_login);
				if (autoLogin.isChecked()) {

					e.putBoolean(FWConstants.KEY_KEEP_LOGIN, true);

				}

				// 아이디 저장
				CheckBox saveID = (CheckBox) findViewById(R.id.save_id);
				if (saveID.isChecked()) {

					e.putBoolean(FWConstants.KEY_SAVE_ID, true);

				}

				e.putString(FWConstants.KEY_USER_ID, mID);
				e.putString(FWConstants.KEY_USER_PASSWORD, mPassword);

				e.commit();

				goMain();
			} else {
				PlusToaster.doIt(LoginActivity.this, "아이디와 비밀번호를 확인하세요");
			}
			break;

		default:
			break;
		}

	}

	/**
	 * 왼쪽 메뉴 하단 버튼 클릭시 웹페이지 이동(주문배송/관심상품/장바구니/마이페이지)
	 * 
	 * @param v
	 */
	public void openWebpage(View v) {
		String url = null;
		switch (v.getId()) {

		case R.id.copyright_company:
			url = FWWebpageUrls.URL_MAIN + FWWebpageUrls.COPYRIGHT_COMPANY;
			break;
		case R.id.copyright_agreement:
			url = FWWebpageUrls.URL_MAIN + FWWebpageUrls.COPYRIGHT_AGREEMENT;
			break;
		case R.id.copyright_privacy:
			url = FWWebpageUrls.URL_MAIN + FWWebpageUrls.COPYRIGHT_PRIVACY;
			break;
		case R.id.copyright_customer_center:
			url = FWWebpageUrls.URL_MAIN
					+ FWWebpageUrls.COPYRIGHT_CUSTOMER_CENTER;
			break;

		}
		Intent intent = new Intent(this, FWWebViewActivity.class);
		intent.putExtra(PlusConstants.KEY_URL, url);
		startActivity(intent);

	}

}
