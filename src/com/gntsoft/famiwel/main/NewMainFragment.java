package com.gntsoft.famiwel.main;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.gntsoft.famiwel.FWCommonFragment;
import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.server.BannerModel;
import com.gntsoft.famiwel.server.BannerParser;
import com.gntsoft.famiwel.server.FWApiConstants;
import com.gntsoft.famiwel.server.FWModel;
import com.gntsoft.famiwel.server.FWParser;
import com.pluslibrary.server.PlusHttpClient;
import com.pluslibrary.server.PlusOnGetDataListener;
import com.pluslibrary.utils.PlusDpPixelConverter;
import com.pluslibrary.utils.PlusLogger;
import com.pluslibrary.utils.PlusOnClickListener;

/**
 * 메인 fragment
 * 
 * @author jeff
 * 
 */
public class NewMainFragment extends FWCommonFragment implements
		PlusOnGetDataListener {

	private static final int GET_MAIN_BANNER = 0;
	private static final int GET_LIST = 1;
	private ListView mList;
	private MainListAdapter mAdapter;
	private Button mGoListTop;
	private View mListHeaderView;
	private View mListFootererView;

	public NewMainFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// ((MainActivity)mActivity).setContainerViewVisibility(true);

		mListHeaderView = inflater.inflate(R.layout.main_list_header, null, false);
		mListFootererView = inflater.inflate(R.layout.list_footer, null, false);

		View rootView = inflater.inflate(R.layout.new_fragment_main, container,
				false);
		// setScrollView((com.gntsoft.famiwel.utils.ScrollViewExt)
		// rootView.findViewById(R.id.container_main));
		getServerData();

		return rootView;
	}

	private void getServerData() {
		// if (((MainActivity) mActivity).getViewpagerCurrentPosition() !=
		// MainActivity.VIEWPAGER_POSITION_HOME)
		// return;

		// 메인 배너 데이터 가져오기
		new PlusHttpClient(mActivity, this, false).execute(GET_MAIN_BANNER,
				FWApiConstants.GET_MAIN_BANNER, new BannerParser());

		// 메인 리스트 데이터 가져오기
		new PlusHttpClient(mActivity, this, false).execute(GET_LIST,
				FWApiConstants.GET_MAIN_LIST, new FWParser());

		PlusLogger.doIt("MainFragment getServerData");

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	private void makeList(final ArrayList<FWModel> model) {
		PlusLogger.doIt("hide", "MainFragment makeList");

		// 스크롤하면 10개씩 더 가져오는 처리는 안되어 있음!!
		// 블랙타임 참고하여 onScrollChanged 수정해야 함!!
		mList = (ListView) mActivity.findViewById(R.id.list_main);
		if (mList == null)
			return;

		mOriginalModel = model;
		mOriginalModelSize = model.size();

		// 멤버 변수 초기화
		mModel = new ArrayList<FWModel>();

		addItem();
		// addHeaderView는 setAdapter 이전에 해야 함
		mList.addHeaderView(mListHeaderView);
		mList.addFooterView(mListFootererView);

		mAdapter = new MainListAdapter(mActivity, this,
				(ArrayList<FWModel>) mModel);
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 헤더뷰가 2개를 차지해서 1을 빼야 함
				((MainActivity) mActivity)
						.showWebpage(FWConstants.BANNER_URL_HEAD
								+ model.get(position - 1).link);

				// 원래 코드
				// 상세페이지 이동(웹뷰)
				//
				// Intent intent = new Intent(mActivity,
				// FWWebViewActivity.class);
				// intent.putExtra(PlusConstants.KEY_URL,
				// FWConstants.BANNER_URL_HEAD + model.get(position).link);
				// startActivity(intent);

			}
		});

		mList.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				showTopButton(firstVisibleItem);

				int lastInScreen = firstVisibleItem + visibleItemCount;
				if ((lastInScreen == totalItemCount) && !(mLoadingMore)) {

					addItem();

				}
			}

			private void showTopButton(int firstVisibleItem) {
				if (isHideTopButton(firstVisibleItem)) {

					mGoListTop.setVisibility(View.GONE);

				} else {
					if (!mGoListTop.isShown())
						mGoListTop.setVisibility(View.VISIBLE);
				}

			}

			private boolean isHideTopButton(int firstVisibleItem) {
				// TODO Auto-generated method stub
				return firstVisibleItem == 0 ? true : false;
			}
		});

		// setListViewHeightBasedOnChildren(mList);

	}

	/**
	 * 상단 ViewPager
	 * 
	 */
	private void makeViewPager(final ArrayList<BannerModel> model) {

		// dot 생성
		makeDots(model);

		final ViewPager photoViewpager = (ViewPager) mListHeaderView
				.findViewById(R.id.ad_viewflipper);

		photoViewpager.setAdapter(new PlusPagerAdapter(mActivity, model));

		// 플리킹할 때 활성화된 이미지 위치를 알려주는 dot 색상 변경
		photoViewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				changeDotBackground(model.size(), position);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		Button goLeft = (Button) mListHeaderView.findViewById(R.id.go_left);
		goLeft.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				photoViewpager.setCurrentItem(photoViewpager.getCurrentItem() + 1);

			}
		});

		Button goRight = (Button) mListHeaderView.findViewById(R.id.go_right);
		goRight.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				photoViewpager.setCurrentItem(photoViewpager.getCurrentItem() - 1);

			}
		});

	}

	/**
	 * 닷 생성
	 * 
	 * @param model
	 */
	private void makeDots(ArrayList<BannerModel> model) {
		LinearLayout dotContainer = (LinearLayout) mListHeaderView
				.findViewById(R.id.dot_container);

		for (int i = 0; i < model.size(); i++) {
			ImageView dot = new ImageView(mActivity);
			dot.setImageResource(i == 0 ? R.drawable.dot_on
					: R.drawable.dot_off);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					PlusDpPixelConverter.doIt(mActivity, 6),
					PlusDpPixelConverter.doIt(mActivity, 6));
			layoutParams.rightMargin = 20;
			dot.setLayoutParams(layoutParams);

			dotContainer.addView(dot);
		}
	}

	/**
	 * 닷 배경 변경
	 * 
	 * @param dotNums
	 * @param position
	 */
	protected void changeDotBackground(int dotNums, int position) {
		// TODO Auto-generated method stub

		LinearLayout dotContainer = (LinearLayout) mListHeaderView
				.findViewById(R.id.dot_container);

		for (int i = 0; i < dotNums; i++) {
			ImageView dot = (ImageView) dotContainer.getChildAt(i);
			dot.setImageResource(i == position ? R.drawable.dot_on
					: R.drawable.dot_off);

		}

	}

	@Override
	protected void addListenerButton() {

		// // 상품 더보기
		// Button viewMore = (Button)
		// mActivity.findViewById(R.id.view_more_main);
		// viewMore.setOnClickListener(new PlusOnClickListener() {
		//
		// @Override르
		// protected void doIt() {
		// addItem();
		// mAdapter.notifyDataSetChanged();
		// setListViewHeightBasedOnChildren(mList);
		//
		// }
		// });

		// 리스트 맨위로 이동
		// final com.gntsoft.famiwel.utils.ScrollViewExt container =
		// (com.gntsoft.famiwel.utils.ScrollViewExt) mActivity
		// .findViewById(R.id.container_main);
		mGoListTop = (Button) mActivity.findViewById(R.id.go_list_top);
		mGoListTop.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				PlusLogger.doIt("top", "button pressed");
				//scrollTo는 작동하지 않음 
				mList.setSelection(0);

			}
		});
		// container.setScrollViewListener(this);

	}

	@Override
	public void onSuccess(Integer from, Object datas) {

		// if(((ArrayList<BannerModel>) datas).size() == 0 )
		// PlusToaster.doIt(mActivity, "datas size is 0");
		switch (from) {
		case GET_MAIN_BANNER:
			makeViewPager((ArrayList<BannerModel>) datas);
			break;

		case GET_LIST:

			makeList((ArrayList<FWModel>) datas);

			break;

		}

	}

	@Override
	protected void notifyListView() {
		//
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();

	}

}
