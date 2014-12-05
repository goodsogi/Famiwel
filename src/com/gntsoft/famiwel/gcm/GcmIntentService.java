package com.gntsoft.famiwel.gcm;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.IntentService;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.GcmBroadcastReceiver;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.main.MainActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.pluslibrary.PlusConstants;

/**
 * 푸시 받은 메시지 처리
 * 
 * @author jeff
 */
public class GcmIntentService extends IntentService {

	private static final String TAG = GcmIntentService.class.getSimpleName();

	private static final int NOTIFICATION_ID = 11;

	private static final long PUSH_DELAY_TIME = 3000;

	private static int notificationID = 8888;

	// 이전 버전과 달리 project id로 생성자를 만들 필요 없음

	private boolean mSoundEnabled;

	private boolean mVibrationEnabled;

	private Vibrator mVibrator;

	private MediaPlayer mMediaPlayer;

	private String mMessage;

	// 이미지 다운로드
	protected ImageLoader mImageLoader;

	private boolean mIsFirstTime;

	public GcmIntentService() {
		super(TAG);
		mIsFirstTime = true;

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		//같은 푸시 메시지가 두번 들어와 한번만 처리 
		if(!mIsFirstTime) {
			
			setFirstTimeTrue();
			
			
			return;
		}
		
		mIsFirstTime = false;

		// 생성자에서는 getApplicationContext()가 null pointer 오류 발생
		SharedPreferences sharedPreference = getApplicationContext()
				.getSharedPreferences(FWConstants.PREF_NAME,
						Context.MODE_PRIVATE);
		// 푸시를 사용하지 않는다고 설정되어 있는 경우
		if (!sharedPreference.getBoolean(FWConstants.KEY_ENABLE_ALARM, true))
			return;

		mSoundEnabled = true;
		mVibrationEnabled = true;

		mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mMediaPlayer = MediaPlayer.create(getApplicationContext(),
				R.raw.push_sound);

		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			/*
			 * Filter messages based on message type. Since it is likely that
			 * GCM will be extended in the future with new message types, just
			 * ignore any message types you're not interested in, or that you
			 * don't recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				Log.w(FWConstants.LOG_TAG, "Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				Log.w(FWConstants.LOG_TAG, "Deleted messages on server: "
						+ extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				// 푸시로 받은 쿠폰 인덱스 처리
				// gid에는 null이 들어옴
				// String detailPageUrl = extras.containsKey("gid") ?
				// extras.get("gid").toString() : "";
				String detailPageUrl = extras.containsKey("url") ? extras.get(
						"url").toString() : "";
				String imageUrl = extras.containsKey("img") ? extras.get("img")
						.toString() : "";
				String title = extras.containsKey("title") ? extras
						.get("title").toString() : "";
				String msg = extras.containsKey("msg") ? extras.get("msg")
						.toString() : "";
				String gubun = extras.containsKey("gubun") ? extras
						.get("gubun").toString() : "";

				String isAdPopup = extras.containsKey("popup") ? extras.get(
						"popup").toString() : "N";
				// 메뉴 코드 gubun
				// goods : 상품페이지
				// bokji : 복지관
				// event : 이벤트페이지

				String decodedMsg = null;
				String decodedTitle = null;
				try {
					decodedMsg = URLDecoder.decode(msg, "euc-kr");
					decodedTitle = URLDecoder.decode(title, "euc-kr");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				
				

				if (isAdPopup.equals("Y")) {

					showAdPopup(detailPageUrl, imageUrl, decodedTitle,
							decodedMsg, gubun);

				} else {
					// notification과 dialog 중 선택 !! 주석 해제!!
					getPushImage(getApplicationContext(), detailPageUrl,
							imageUrl, decodedTitle, decodedMsg, gubun);
				}

				// getPushImage(getApplicationContext(), detailPageUrl,
				// imageUrl,
				// decodedTitle, decodedMsg, gubun);

			}
		}

		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void setFirstTimeTrue() {
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mIsFirstTime = true;
				
			}
		}, PUSH_DELAY_TIME);
		
	}

	// /**
	// * 비콘 팝업 띄움
	// *
	// * @param uuid
	// */
	// private void launchBeaconPopup(String couponIdx) {
	// Intent intent = new Intent(getApplicationContext(),
	// BeaconDialogActivity.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// intent.putExtra(FWConstants.KEY_COUPON_IDX, couponIdx);
	// getApplicationContext().startActivity(intent);
	//
	// }

	private void getPushImage(final Context context,
			final String detailPageUrl, String imageUrl,
			final String decodedTitle, final String decodedMsg,
			final String gubun) {

		// 이미지 가져옴
		// UIL 초기화
		mImageLoader = ImageLoader.getInstance();
		mImageLoader.loadImage(imageUrl, new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String s, View view) {

			}

			@Override
			public void onLoadingFailed(String s, View view,
					FailReason failReason) {
			}

			@Override
			public void onLoadingComplete(String s, View view, Bitmap bitmap) {

				sendBigPictureStyleNotification(context, detailPageUrl, bitmap,
						decodedTitle, decodedMsg, gubun);

				// if (mIsAdPopup.equals("Y")) {
				//
				// showAdPopup(detailPageUrl, bitmap, decodedTitle,
				// decodedMsg, gubun);
				//
				// } else {
				// sendBigPictureStyleNotification(context, detailPageUrl,
				// bitmap, decodedTitle, decodedMsg, gubun);
				// }

			}

			@Override
			public void onLoadingCancelled(String s, View view) {
			}
		});
	}

	protected void showAdPopup(String detailPageUrl, String imageUrl,
			String decodedTitle, String decodedMsg, String gubun) {

		Intent intent = new Intent(getApplicationContext(),
				MainActivity.class);
		// 서비스에서 액티비티를 실행할 때 FLAG_ACTIVITY_NEW_TASK 필요
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(FWConstants.KEY_IS_AD, true);
		intent.putExtra(FWConstants.KEY_AD_DETAIL_URL,
				FWConstants.BANNER_URL_HEAD + detailPageUrl);
		// bitmap을 그대로 전달하면 오류가 발생하여 파일로 저장한 다음 파일이름 전달
		intent.putExtra(FWConstants.KEY_AD_IMG_URL, imageUrl);

		getApplicationContext().startActivity(intent);

	}

	// protected void showPushPopup(String detailPageUrl, Bitmap bitmap,
	// String decodedTitle, String decodedMsg, String gubun) {
	// String filename = "push_bitmap.png";
	// saveBitmapToFile(filename, bitmap);
	//
	// Intent intent = new Intent(getApplicationContext(),
	// PushPopupActivity.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// intent.putExtra(PlusConstants.KEY_URL, FWConstants.BANNER_URL_HEAD
	// + detailPageUrl);
	// // bitmap을 그대로 전달하면 오류가 발생하여 파일로 저장한 다음 파일이름 전달
	// intent.putExtra(FWConstants.KEY_PUSH_BITMAP, filename);
	//
	// intent.putExtra(FWConstants.KEY_PUSH_TITLE, decodedTitle);
	// intent.putExtra(FWConstants.KEY_PUSH_MSG, decodedMsg);
	// intent.putExtra(FWConstants.KEY_PUSH_GUBUN, gubun);
	// getApplicationContext().startActivity(intent);
	//
	// }
	//
	// private void saveBitmapToFile(String filename, Bitmap bitmap) {
	//
	//
	// try {
	// // Write file
	//
	// FileOutputStream stream = this.openFileOutput(filename,
	// Context.MODE_PRIVATE);
	// bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
	//
	// // Cleanup
	// stream.close();
	// bitmap.recycle();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// }

	// private void popupNotification(Context context, String detailPageUrl,
	// Bitmap bitmap, String decodedTitle, String decodedMsg, String gubun) {
	//
	// NotificationManager mNotificationManager = (NotificationManager) context
	// .getSystemService(Context.NOTIFICATION_SERVICE);
	// Notification notification = new Notification.Builder(this)
	// .setContentTitle(decodedMsg).setContentText(decodedMsg)
	// .setSmallIcon(R.drawable.appicon).build();
	//
	// RemoteViews bigContentView = new RemoteViews(context.getPackageName(),
	// R.layout.custom_notification);
	// bigContentView.setImageViewBitmap(R.id.image, bitmap);
	// bigContentView.setTextViewText(R.id.title, decodedTitle);
	// bigContentView.setTextViewText(R.id.msg, decodedMsg);
	// bigContentView.setTextViewText(R.id.time, PlusTimeFormatter.doIt());
	//
	//
	// RemoteViews normalContentView = new RemoteViews(context.getPackageName(),
	// R.layout.normal_custom_notification);
	// normalContentView.setImageViewBitmap(R.id.image, bitmap);
	// normalContentView.setTextViewText(R.id.title, decodedTitle);
	// normalContentView.setTextViewText(R.id.msg, decodedMsg);
	//
	// // 아래 코드가 없으면 notification이 크게 표시안됨
	// notification.priority = Notification.PRIORITY_MAX;
	// notification.bigContentView = bigContentView;
	// notification.contentView = normalContentView;
	//
	// Intent notificationIntent = new Intent(context, MainActivity.class);
	// notificationIntent.putExtra(PlusConstants.KEY_URL,
	// FWConstants.BANNER_URL_HEAD + detailPageUrl);
	// notificationIntent.putExtra(FWConstants.KEY_PUSH_GUBUN, gubun);
	// // PendingIntent.FLAG_UPDATE_CURRENT를 주지 않으면 null이나 이상한 extra 값이 들어감
	// PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
	// notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	// notification.contentIntent = contentIntent;
	//
	// // notification.flags |= Notification.FLAG_NO_CLEAR; // Do not clear the
	// // notification
	// notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
	// notification.defaults |= Notification.DEFAULT_VIBRATE; // Vibration
	// notification.defaults |= Notification.DEFAULT_SOUND; // Sound
	//
	// mNotificationManager.notify(notificationID, notification);
	// notificationID = (notificationID - 1) % 1000 + 9000;
	//
	// }

	private void sendBigPictureStyleNotification(Context context,
			String detailPageUrl, Bitmap bitmap, String decodedTitle,
			String decodedMsg, String gubun) {
		PendingIntent pi = getPendingIntent(context, detailPageUrl, gubun);
		Builder builder = new Notification.Builder(this);
		builder.setContentTitle(decodedTitle)
		// Notification title
				.setContentText(decodedMsg)
				// you can put subject line.
				.setSmallIcon(R.drawable.smallappicon);

		// Now create the Big picture notification.
		Notification notification = new Notification.BigPictureStyle(builder)
		// 확대했을 때 기존 두 번째 라인은 사라지고 두번째 라인은 setSummaryText로 설정
				.bigPicture(bitmap).setSummaryText(decodedMsg).build();
		// PendingIntent 사용
		notification.contentIntent = pi;
		// Put the auto cancel notification flag
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		NotificationManager notificationManager = getNotificationManager();
		// 같은 id를 적용해서 하나만 표시
		notificationManager.notify(0, notification);

		playSoundVibration();
	}

	private NotificationManager getNotificationManager() {
		return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	private PendingIntent getPendingIntent(Context context,
			String detailPageUrl, String gubun) {

		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.putExtra(PlusConstants.KEY_URL,
				FWConstants.BANNER_URL_HEAD + detailPageUrl);
		notificationIntent.putExtra(FWConstants.KEY_PUSH_GUBUN, gubun);
		// PendingIntent.FLAG_UPDATE_CURRENT를 주지 않으면 null이나 이상한 extra 값이 들어감!!
		return PendingIntent.getActivity(context, 0, notificationIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

	}

	/**
	 * 푸시 수신시 알림음이나 진동
	 */
	private void playSoundVibration() {
		// 진동
		if (mVibrationEnabled) {
			mVibrator.vibrate(new long[] { 200, 200, 200, 200 }, -1);
		}

		// 소리
		if (mSoundEnabled) {
			mMediaPlayer.start();
		}

	}

}
