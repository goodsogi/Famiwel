package com.gntsoft.famiwel;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;

/**
 * 공통 팝업
 * 
 * @author user
 * 
 */
public abstract class FWCommonDialog extends Dialog {

	protected Activity mActivity;

	public FWCommonDialog(final Activity activity, final int theme,
			int layoutId) {
		super(activity, theme);
		mActivity = activity;
		setOwnerActivity(activity);
		setCancelable(true);
		setContentView(layoutId);
		// 버튼에 리스너 추가
		addListenerToButton();

	}

	/**
	 * 버튼에 리스너 추가
	 */
	abstract protected void addListenerToButton();

	/**
	 * 팝업창 취소 여부 설정
	 * 
	 * @param flag
	 * @return
	 */
	public FWCommonDialog cancelable(boolean flag) {
		setCancelable(flag);
		return this;
	}

}
