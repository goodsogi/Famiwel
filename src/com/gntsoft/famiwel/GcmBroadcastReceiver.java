package com.gntsoft.famiwel;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * GCM 리시버
 * 
 * @author jeff
 * 
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName comp = new ComponentName(context.getPackageName(),
				com.gntsoft.famiwel.gcm.GcmIntentService.class.getName());
		// 서비스 시작, 단말기가 깨어 있게 유지
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}
}
