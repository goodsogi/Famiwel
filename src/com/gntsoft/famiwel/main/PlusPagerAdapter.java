package com.gntsoft.famiwel.main;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.FWWebViewActivity;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.server.BannerModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pluslibrary.PlusConstants;
import com.pluslibrary.utils.PlusLogger;
import com.pluslibrary.utils.PlusOnClickListener;

/**
 * 광고 viewpager 어댑터
 * 
 * @author jeff
 * 
 */
class PlusPagerAdapter extends PagerAdapter {

	private LayoutInflater mInflater;
	private ArrayList<BannerModel> mDatas;
	// 이미지 다운로드
	protected ImageLoader mImageLoader;
	protected DisplayImageOptions mOption;
	private Context mContext;

	public PlusPagerAdapter(Context c, ArrayList<BannerModel> model) {
		super();
		mInflater = LayoutInflater.from(c);
		mContext = c;
		mDatas = model;
		// UIL 초기화
		mImageLoader = ImageLoader.getInstance();

		mOption = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object instantiateItem(View pager, final int position) {
		View v = mInflater.inflate(R.layout.viewpager_item, null);
		ImageView photoItem = (ImageView) v.findViewById(R.id.banner_img);

		mImageLoader.displayImage(
				FWConstants.BANNER_URL_HEAD + mDatas.get(position).url,
				photoItem, mOption);
		photoItem.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				
				((MainActivity) mContext).showWebpage(FWConstants.BANNER_URL_HEAD + mDatas.get(position).link);
				PlusLogger.doIt(FWConstants.BANNER_URL_HEAD
						+ mDatas.get(position).link);
				
				
				
				
//				// 해당 배너 링크를 웹뷰로 염
//				Intent intent = new Intent(mContext, FWWebViewActivity.class);
//				intent.putExtra(PlusConstants.KEY_URL,
//						FWConstants.BANNER_URL_HEAD + mDatas.get(position).link);
//				PlusLogger.doIt(FWConstants.BANNER_URL_HEAD
//						+ mDatas.get(position).link);
//				mContext.startActivity(intent);

			}
		});

		((ViewPager) pager).addView(v, 0);

		return v;
	}

	@Override
	public void destroyItem(View pager, int position, Object view) {
		((ViewPager) pager).removeView((View) view);
	}

	@Override
	public boolean isViewFromObject(View pager, Object obj) {
		return pager == obj;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	@Override
	public void finishUpdate(View arg0) {
	}
}
