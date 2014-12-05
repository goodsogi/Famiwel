package com.gntsoft.famiwel;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;

import com.gntsoft.famiwel.server.FWModel;
import com.pluslibrary.utils.PlusDpPixelConverter;
import com.pluslibrary.utils.PlusLogger;

abstract public class FWCommonFragment extends Fragment {
	protected ArrayList<FWModel> mModel;
	protected ArrayList<FWModel> mOriginalModel;
	protected Button mViewMore;
	protected int mDataCount;
	protected int mOriginalModelSize;
	protected FragmentActivity mActivity;
	private ScrollView mScrollView;
	protected boolean mLoadingMore;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//아래 코드가 없으면 0으로 초기화 안됨 
		mDataCount =0;
		mLoadingMore = false;
		mActivity = getActivity();
		// 리스너 등록
		addListenerButton();

	}

	/**
	 * 리스너 등록
	 */
	abstract protected void addListenerButton();

	/**
	 * 리스트뷰의 높이 강제 지정
	 * 
	 * @param listView
	 */
	protected void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		int rowHeight = PlusDpPixelConverter.doIt(mActivity, 276);
		for (int i = 0; i < listAdapter.getCount(); i++) {
			totalHeight += rowHeight;
		}

		if (totalHeight == 0)
			throw new RuntimeException("totalHeight == 0");

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	protected void setGridViewHeightBasedOnChildren(GridView gridView) {
		ListAdapter listAdapter = gridView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		// f를 붙여야 올림이 제대로 계산됨
		int totalHeight = (int) (PlusDpPixelConverter.doIt(mActivity, 250) * Math
				.ceil(listAdapter.getCount() / 2f));

		ViewGroup.LayoutParams params = gridView.getLayoutParams();
		params.height = totalHeight
				+ (int) (PlusDpPixelConverter.doIt(mActivity, 10) * Math
						.ceil(listAdapter.getCount() / 2f));
		gridView.setLayoutParams(params);

	}

	/**
	 * 아이템 추가
	 * 
	 */
	protected void addItem() {
		mLoadingMore = true;
		int modelSize = 0;
		if ((mOriginalModelSize - mDataCount) > 10) {
			modelSize = 10;
		} else {
			modelSize = mOriginalModelSize - mDataCount;
			if (mViewMore != null)
				mViewMore.setVisibility(View.GONE);
		}
		PlusLogger.doIt("zero","mOriginalModelSize: " + mOriginalModelSize + "mDataCount: " + mDataCount + "model size: " + modelSize);
		
		if(modelSize <=0) {
			
			return;
		}
		
		for (int i = 0; i < modelSize; i++) {
			mModel.add(mOriginalModel.get(mDataCount + i));
		}
		mDataCount += 10;
		notifyListView();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				mLoadingMore = false;
				PlusLogger.doIt("zero","postDelayed mLoadingMore: " + mLoadingMore);
				
			}
		}, 2000);
		
	}

	abstract protected void notifyListView();

	public void focusScrollViewTop() {
		ScrollView scrollView = getScrollView();
		// scrollView.fullScroll(ScrollView.FOCUS_UP);

		if (scrollView != null)
			scrollView.scrollTo(0, 0);

	}

	private ScrollView getScrollView() {
		// TODO Auto-generated method stub
		return mScrollView;
	}

	protected void setScrollView(ScrollView scrollView) {
		mScrollView = scrollView;
	}
}
