package com.gntsoft.famiwel.intro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.FWGcmRegister;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.login.LoginActivity;
import com.gntsoft.famiwel.main.MainActivity;
import com.gntsoft.famiwel.server.FWApiConstants;
import com.pluslibrary.server.PlusHttpClient;
import com.pluslibrary.server.PlusInputStreamStringConverter;
import com.pluslibrary.server.PlusOnGetDataListener;
import com.pluslibrary.utils.PlusToaster;

/**
 * 인트로 화면
 * 
 * @author jeff
 * 
 */
public class SplashActivity extends Activity implements PlusOnGetDataListener {

	final static int DELAY_MILLI_SECOND = 3000;
    private static final int AUTO_LOG_IN = 1;
    private SharedPreferences mSharedPreference;

    /** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.activity_splash);
		// 푸시 등록
		FWGcmRegister gcmRegister = new FWGcmRegister(this);
		gcmRegister.doIt();

        mSharedPreference = getSharedPreferences(FWConstants.PREF_NAME,
                Context.MODE_PRIVATE);

        // 로그인유지 처리
        if (mSharedPreference.getBoolean(FWConstants.KEY_KEEP_LOGIN, false)) {

            doAutoLogin();
        } else {


            goLoginActivity();
        }

	}
    private void doAutoLogin() {
        String id = mSharedPreference.getString(FWConstants.KEY_USER_ID, "");

        String password = mSharedPreference.getString(FWConstants.KEY_USER_PASSWORD,
                "");

        FWGcmRegister gcmRegister = new FWGcmRegister(this);
        String deviceId = gcmRegister.getRegistrationId();

        new PlusHttpClient(this, this, false).execute(AUTO_LOG_IN,
                FWApiConstants.LOG_IN + "?mid=" + id + "&pass=" + password
                        + "&deviceId=" + deviceId,
                new PlusInputStreamStringConverter());

    }

    @Override
    public void onSuccess(Integer from, Object datas) {
    	if(datas == null) return;
    	
        switch (from) {

            case AUTO_LOG_IN:
                if (((String) datas).contains("OK")) {
                   // PlusToaster.doIt(SplashActivity.this, "로그인했습니다");

                    goMainActivity();
                } else {
                   // PlusToaster.doIt(SplashActivity.this, "아이디와 비밀번호를 확인하세요");
                    goLoginActivity();
                }
                break;

            default:
                break;
        }

    }
    private void goMainActivity() {


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                finish();

                Intent intent = new Intent(SplashActivity.this,
                        MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        }, DELAY_MILLI_SECOND);

    }


    private void goLoginActivity() {

		// 로그인 액티비티로 이동

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				finish();

				Intent intent = new Intent(SplashActivity.this,
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);

			}
		}, DELAY_MILLI_SECOND);

	}

}
