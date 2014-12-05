package com.gntsoft.famiwel.best;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.gntsoft.famiwel.FWCommonFragment;
import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.main.GridListAdapter;
import com.gntsoft.famiwel.main.GridViewWithHeaderAndFooter;
import com.gntsoft.famiwel.main.MainActivity;
import com.gntsoft.famiwel.server.FWApiConstants;
import com.gntsoft.famiwel.server.FWModel;
import com.gntsoft.famiwel.server.FWParser;
import com.pluslibrary.server.PlusHttpClient;
import com.pluslibrary.server.PlusOnGetDataListener;
import com.pluslibrary.utils.PlusLogger;

public class NewBestFragment extends FWCommonFragment implements
		PlusOnGetDataListener {

	private static final int GET_LIST = 1;
	private GridListAdapter mAdapter;
	private View mListHeaderView;
	private View mListFootererView;

	public NewBestFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		mListHeaderView = inflater.inflate(R.layout.best_list_header, null, false);
		mListFootererView = inflater.inflate(R.layout.list_footer, null, false);

		View rootView = inflater.inflate(R.layout.new_fragment_best, container,
				false);
		getServerData();
		return rootView;
	}

	private void getServerData() {
		new PlusHttpClient(mActivity, this, false).execute(GET_LIST,
				FWApiConstants.GET_BEST, new FWParser());

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

		}

	}

	/**
	 * 리스트 생성
	 * 
	 */
	private void makeList(final ArrayList<FWModel> model) {

		GridViewWithHeaderAndFooter list = (GridViewWithHeaderAndFooter) mActivity
				.findViewById(R.id.list_best);
		if (list == null || model == null || mActivity == null)
			return;
		mOriginalModel = model;
		mOriginalModelSize = model.size();

		// 멤버 변수 초기화
		mModel = new ArrayList<FWModel>();

		addItem();

		// addHeaderView는 setAdapter 이전에 해야 함
		list.addHeaderView(mListHeaderView);
		list.addFooterView(mListFootererView);
		mAdapter = new GridListAdapter(mActivity, this, mModel);

		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// 헤더뷰가 2개를 차지해서 2를 빼야 함
				((MainActivity) mActivity)
						.showWebpage(FWConstants.BANNER_URL_HEAD
								+ model.get(position-2).link);

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

	}

	@Override
	protected void notifyListView() {
		if (mAdapter != null)
			mAdapter.notifyDataSetChanged();

	}

}
