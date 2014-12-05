package com.gntsoft.famiwel.main;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gntsoft.famiwel.FWCommonAdapter;
import com.gntsoft.famiwel.FWCommonFragment;
import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.server.FWModel;
import com.pluslibrary.utils.PlusLogger;
import com.pluslibrary.utils.PlusPriceFormatter;
import com.pluslibrary.utils.PlusViewHolder;

public class MainListAdapter extends FWCommonAdapter<FWModel> {

	private Fragment mFragment;
	private boolean mIsFirstTime = true;

	public MainListAdapter(Context context, Fragment fragment, ArrayList<FWModel> datas) {
		super(context, R.layout.list_item_main, datas);
		mFragment = fragment;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		//PlusLogger.doIt("MainListAdapter getView position: " + position);

		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.list_item_main,
					parent, false);
		}
		// 아이콘
		ImageView img = PlusViewHolder.get(convertView, R.id.icon);
		mImageLoader.displayImage(
				FWConstants.BANNER_URL_HEAD + mDatas.get(position).url, img,
				mOption);

		// 무료배송
		if (mDatas.get(position).delivery_price.equals("0")) {
			ImageView freeDelivery = PlusViewHolder.get(convertView,
					R.id.free_delivery);
			freeDelivery.setImageResource(R.drawable.free_delivery);
		}

		// 부제목
		final TextView subtitle = PlusViewHolder
				.get(convertView, R.id.subtitle);
		subtitle.setText(mDatas.get(position).name);

		// 퍼센티지
		final TextView percentage = PlusViewHolder.get(convertView,
				R.id.percentage);
		percentage.setText(mDatas.get(position).percentage + "%");
		// 가격
		final TextView price = PlusViewHolder.get(convertView, R.id.price);
		price.setText(PlusPriceFormatter.doIt(mDatas.get(position).price) + "원");

		// 가운데 줄
		price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

		// 세일가격
		final TextView salePrice = PlusViewHolder.get(convertView,
				R.id.sale_price);
		salePrice
				.setText(PlusPriceFormatter.doIt(mDatas.get(position).sale_price)
						+ "원");
		
//		if(position == 9 && mIsFirstTime) {
//			focusScrollViewTop();
//			mIsFirstTime = false;
//		}
		
		//PlusLogger.doIt("position: " + position);

		return convertView;
	}

	private void focusScrollViewTop() {
		((FWCommonFragment) mFragment).focusScrollViewTop();
		
	}

}
