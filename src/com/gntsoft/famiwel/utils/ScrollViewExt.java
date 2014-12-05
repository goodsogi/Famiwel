package com.gntsoft.famiwel.utils;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * ScrollView 맨끝에 스크롤된 것 감지
 * 
 * @author jeff
 * 
 */
public class ScrollViewExt extends ScrollView {
	private ScrollViewListener scrollViewListener = null;

	public ScrollViewExt(Context context) {
		super(context);
	}

	public ScrollViewExt(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScrollViewExt(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
		}
	}
}
