package com.gntsoft.famiwel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pluslibrary.PlusConstants;
import com.pluslibrary.server.PlusHttpClient;
import com.pluslibrary.server.PlusInputStreamStringConverter;
import com.pluslibrary.server.PlusOnGetDataListener;

/**
 * GCM 등록
 */
public class FWGcmRegister extends Activity {

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	protected static final String SENDER_ID = "124340636891"; // 프로젝트 id

	private Activity mActivity;
	private GoogleCloudMessaging mGcm;

	public FWGcmRegister(Activity activity) {
		mActivity = activity;

	}

	public void doIt() {

		// 단말기에 설치된 Play Services APK 체크. 이상 없으면 GCM 등록
		if (checkPlayServices()) {
			mGcm = GoogleCloudMessaging.getInstance(mActivity);
			String regid = getRegistrationId();

			if (regid.isEmpty()) {

				registerInBackground();
			} else {
				Log.i(PlusConstants.LOG_TAG, "getting saved registration id");
			}
		} else {
			Log.i(PlusConstants.LOG_TAG,
					"No valid Google Play Services APK found.");
		}

	}

	/**
	 * 사용자 단말기에 구글 플레이 서비스 apk가 설치되었는지 확인. 만약 설치되어 있지 않다면 구글 플레이 마켓에서 다운받거나 시스템
	 * 설정에서 활성화하게 하는 창 띄움
	 * 
	 * @param activity
	 * 
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(mActivity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, mActivity,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(PlusConstants.LOG_TAG, "This device is not supported.");
			}
			return false;
		}
		return true;
	}

	/**
	 * GCM 등록 id와 app 버전코드를 sharedpreference에 저장 {@code SharedPreferences}.
	 * 
	 * @param context
	 * 
	 * @param regId
	 *            등록 ID
	 */
	private void storeRegistrationId(String regId) {
		final SharedPreferences prefs = getGcmPreferences();
		int appVersion = getAppVersion();
		Log.i(PlusConstants.LOG_TAG, "Saving regId on app version "
				+ appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * 등록 id 가져오기. 결과값이 없다면 등록할 필요 있음
	 * 
	 * 
	 * @return 등록 id, 등록한 id가 없으면 ""
	 */
	public String getRegistrationId() {
		final SharedPreferences prefs = getGcmPreferences();
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(PlusConstants.LOG_TAG, "Registration not found.");
			return "";
		}
		// 앱이 업데이트되었는지 체크. 만약 업데이트된 경우 이전 등록 id는 작동하지 않을 수 있으므로 삭제
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion();
		if (registeredVersion != currentVersion) {
			Log.i(PlusConstants.LOG_TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * 앱을 GCM서버에 등록. 등록 id와 앱 version code를 sharedpreference에 저장
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (mGcm == null) {
						mGcm = GoogleCloudMessaging.getInstance(mActivity);
					}
					String regid = mGcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					storeRegistrationId(regid);

				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					// 에러가 발생한 경우 사용자가 다시 등록하게 함!!
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// GCM 서버 전송 결과 처리 !!
				Log.i(PlusConstants.LOG_TAG,
						"registration id is registered in GCM server: " + msg);
			}
		}.execute(null, null, null);
	}

	/**
	 * @return 앱 version code
	 */
	private int getAppVersion() {
		try {
			PackageInfo packageInfo = mActivity.getPackageManager()
					.getPackageInfo(mActivity.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// 아래 오류는 발생하면 안됨
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return
	 */
	private SharedPreferences getGcmPreferences() {

		return mActivity.getSharedPreferences(PlusConstants.PREF_NAME,
				Context.MODE_PRIVATE);
	}

}
