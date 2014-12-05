package com.gntsoft.famiwel.main;

public class AdModel {
	
	private String mIndex;
	private String mGubun;
	private String mDetailUrl;
	private String mImageUrl;

	public void setGubun(String gubun) {
		mGubun = gubun;
	}

	public void setDetailUrl(String detailUrl) {
		mDetailUrl = detailUrl;
	}
	
	public void setIndex(String index) {
		mIndex = index;
	}

	public void setImageUrl(String imageUrl) {
		mImageUrl = imageUrl;
	}
	
	public String getIndex() {
		return mIndex;
	}

	public String getGubun() {
		return mGubun;
	}

	public String getDetailUrl() {
		return mDetailUrl;
	}

	public String getImageUrl() {
		return mImageUrl;
	}

}
