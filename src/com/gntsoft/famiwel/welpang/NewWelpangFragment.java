package com.gntsoft.famiwel.welpang;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.gntsoft.famiwel.FWCommonFragment;
import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.FWWebpageUrls;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.main.GridListAdapter;
import com.gntsoft.famiwel.main.GridViewWithHeaderAndFooter;
import com.gntsoft.famiwel.main.MainActivity;
import com.gntsoft.famiwel.server.FWApiConstants;
import com.gntsoft.famiwel.server.FWModel;
import com.gntsoft.famiwel.server.FWParser;
import com.gntsoft.famiwel.server.MenuModel;
import com.gntsoft.famiwel.server.WelpangSubParser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pluslibrary.server.PlusHttpClient;
import com.pluslibrary.server.PlusOnGetDataListener;
import com.pluslibrary.utils.PlusDpPixelConverter;
import com.pluslibrary.utils.PlusLogger;
import com.pluslibrary.utils.PlusOnClickListener;

public class NewWelpangFragment extends FWCommonFragment implements
		PlusOnGetDataListener {

	private static final int GET_LIST = 1;
	private static final int GET_WELPANG_SUB = 2;
	private GridListAdapter mAdapter;

	// 이미지 다운로드
	protected ImageLoader mImageLoader;
	protected DisplayImageOptions mOption;
	private View mListHeaderView;
	private View mListFootererView;
	private boolean mIsFirstTime;

	public NewWelpangFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mIsFirstTime = true;

		mListHeaderView = inflater.inflate(R.layout.welpang_list_header, null, false);
		mListFootererView = inflater
				.inflate(R.layout.welpang_list_footer, null, false);

		View rootView = inflater.inflate(R.layout.new_fragment_welpang,
				container, false);
		initImageLoader();
		getServerData();

		return rootView;
	}

	private void initImageLoader() {

		mImageLoader = ImageLoader.getInstance();

		mOption = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.empty_photo)
				.showImageOnFail(R.drawable.empty_photo).cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

	}

	private void getServerData() {

		new PlusHttpClient(mActivity, this, false).execute(GET_WELPANG_SUB,
				FWApiConstants.GET_WELPANG_SUB, new WelpangSubParser());

		new PlusHttpClient(mActivity, this, false).execute(GET_LIST,
				FWApiConstants.GET_WELPANG_LIST, new FWParser());

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onSuccess(Integer from, Object datas) {
		switch (from) {

		case GET_LIST:
			makeList((ArrayList<FWModel>) datas);

			break;
		case GET_WELPANG_SUB:
			makeTopSub((ArrayList<MenuModel>) datas);

			break;
		}

	}

	/**
	 * 상단 서브 메뉴 생성
	 * 
	 * @param datas
	 */
	private void makeTopSub(ArrayList<MenuModel> datas) {

		LinearLayout container = (LinearLayout) mListHeaderView
				.findViewById(R.id.container_welpang_sub);
		if (container == null)
			return;
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		// 넓이를 무시해서 강제 지정
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				PlusDpPixelConverter.doIt(mActivity, 78),
				LayoutParams.MATCH_PARENT);
		for (final MenuModel m : datas) {
			// parent view를 지정하면 removeView 관련 오류 남
			View v = inflater.inflate(R.layout.item_welpang_sub, null);
			ImageView img = (ImageView) v.findViewById(R.id.item_img);
			mImageLoader.displayImage(FWWebpageUrls.URL_MAIN + m.img, img,
					mOption);
			TextView title = (TextView) v.findViewById(R.id.item_title);
			title.setText(m.title);
			v.setOnClickListener(new PlusOnClickListener() {

				@Override
				protected void doIt() {
					PlusLogger.doIt("welpang", "link: " + m.link);
					//mDataCount를 0으로 초기화시켜야 함 
					mDataCount = 0;
					
					new PlusHttpClient(mActivity, NewWelpangFragment.this,
							false).execute(GET_LIST, FWWebpageUrls.URL_MAIN
							+ m.link, new FWParser());

				}
			});
			container.addView(v, params);

		}

	}

	/**
	 * 리스트 생성
	 * 
	 */
	private void makeList(final ArrayList<FWModel> model) {
		GridViewWithHeaderAndFooter list = (GridViewWithHeaderAndFooter) mActivity
				.findViewById(R.id.list_welpang);
		if (list == null || model == null || mActivity == null)
			return;
		mOriginalModel = model;
		mOriginalModelSize = model.size();

		// 멤버 변수 초기화
		mModel = new ArrayList<FWModel>();

		mViewMore = (Button) mActivity.findViewById(R.id.view_more_welpang);

		addItem();
		// addHeaderView는 setAdapter 이전에 해야 함
		if(mIsFirstTime) {
		list.addHeaderView(mListHeaderView);
		list.addFooterView(mListFootererView);
		mIsFirstTime = false;
		}

		mAdapter = new GridListAdapter(mActivity, this, mModel);

		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 헤더뷰가 2개를 차지해서 2를 빼야 함
				((MainActivity) mActivity)
						.showWebpage(FWConstants.BANNER_URL_HEAD
								+ model.get(position - 2).link);

			}
		});

		list.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				int lastInScreen = firstVisibleItem + visibleItemCount;
				PlusLogger.doIt("zero", "mLoadingMore: " + mLoadingMore);
				if ((lastInScreen == totalItemCount) && !(mLoadingMore)) {

					addItem();

				}
			}
		});

	}

	@Override
	protected void addListenerButton() {
		// 상품 더보기
		Button viewMore = (Button) mListFootererView
				.findViewById(R.id.view_more_welpang);
		viewMore.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				addItem();

			}
		});

		final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) mListHeaderView
				.findViewById(R.id.top_container_welpang);
		final int moveWidthValue = PlusDpPixelConverter.doIt(mActivity, 78);

		Button forwardScrollView = (Button) mListHeaderView
				.findViewById(R.id.scrollview_forward);
		forwardScrollView.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				horizontalScrollView.scrollBy(moveWidthValue, 0);
			}
		});

		Button backwardScrollView = (Button) mListHeaderView
				.findViewById(R.id.scrollview_backward);
		backwardScrollView.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				horizontalScrollView.scrollBy(-moveWidthValue, 0);
			}
		});

	}

	@Override
	protected void notifyListView() {
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();

	}

}
