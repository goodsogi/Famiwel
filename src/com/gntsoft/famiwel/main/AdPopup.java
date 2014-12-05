package com.gntsoft.famiwel.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.ImageView;

import com.gntsoft.famiwel.FWCommonDialog;
import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pluslibrary.PlusConstants;
import com.pluslibrary.utils.PlusOnClickListener;

/**
 * 광고 팝업
 * 
 * @author user
 * 
 */
public class AdPopup extends FWCommonDialog {

	// 이미지 다운로드
	protected ImageLoader mImageLoader;
	protected DisplayImageOptions mOption;

	public AdPopup(Activity activity, int theme, AdModel datas) {
		super(activity, theme, R.layout.popup_ad);
		// 취소 불가
		this.setCancelable(false);

		// UIL 초기화
		mImageLoader = ImageLoader.getInstance();

		mOption = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		showAd(datas);

		saveAdDetailUrl(datas.getDetailUrl());
	}

	private void saveAdDetailUrl(String detailUrl) {
		SharedPreferences sharedPreference = mActivity.getSharedPreferences(
				FWConstants.PREF_NAME, Context.MODE_PRIVATE);
		Editor editor = sharedPreference.edit();
		editor.putString(FWConstants.PRE_AD_DETAIL_URL, detailUrl);
		editor.commit();

	}

	private void showAd(final AdModel adModel) {
		ImageView imageView = (ImageView) findViewById(R.id.ad_image);
		mImageLoader.displayImage(adModel.getImageUrl(), imageView, mOption);

		imageView.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				goToDetailPage(adModel.getDetailUrl(), adModel.getGubun());

			}

		});

	}

	private void goToDetailPage(String detailPageUrl, String gubun) {
		dismiss();
		Intent intent = new Intent(mActivity, MainActivity.class);
		intent.putExtra(PlusConstants.KEY_URL, detailPageUrl);
		intent.putExtra(FWConstants.KEY_PUSH_GUBUN, gubun);
		mActivity.startActivity(intent);

	}

	/**
	 * 버튼에 리스너 추가
	 */
	@Override
	protected void addListenerToButton() {

		SharedPreferences sharedPreference = mActivity.getSharedPreferences(
				FWConstants.PREF_NAME, Context.MODE_PRIVATE);
		final Editor e = sharedPreference.edit();
		// 초기화
		e.putBoolean(FWConstants.KEY_NEVER_OPEN_AD, false);
		e.commit();

		// 다시보지않기
		Button neverOpen = (Button) findViewById(R.id.never_open);
		neverOpen.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				AdPopup.this.dismiss();
				e.putBoolean(FWConstants.KEY_NEVER_OPEN_AD, true);
				e.commit();
			}
		});

		// 닫기
		Button close = (Button) findViewById(R.id.close);
		close.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				AdPopup.this.dismiss();
			}
		});
	}

}
