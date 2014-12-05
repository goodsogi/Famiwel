package com.gntsoft.famiwel.server;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.pluslibrary.PlusConstants;
import com.pluslibrary.server.PlusXmlParser;

/**
 * 배너 파서
 * 
 * @author jeff
 * 
 */
public class BannerParser extends PlusXmlParser {
	public ArrayList<BannerModel> doIt(InputStream in) {
		ArrayList<BannerModel> datas = new ArrayList<BannerModel>();
		try {

			// XmlPullParser xml데이터를 저장
			mXpp.setInput(in, PlusConstants.SERVER_ENCODING_TYPE);

		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			// 이벤트 저장할 변수선언
			int eventType = mXpp.getEventType();

			boolean isItemTag = false;
			String tagName = "";
			String title = "";
			String idx = "";
			String url = "";
			String link = "";

			// xml의 데이터의 끝까지 돌면서 원하는 데이터를 얻어옴
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_TAG) { // 시작 태그를 만났을때.
					// 태그명을 저장
					tagName = mXpp.getName();
					if (tagName.equals("ROW"))
						isItemTag = true;

				} else if (eventType == XmlPullParser.TEXT) { // 내용
					if (isItemTag && tagName.equals("ad_title") && title.equals("")) {
						title = mXpp.getText();
					}
					if (isItemTag && tagName.equals("ad_idx") && idx.equals("")) {
						idx = mXpp.getText();
					}
					if (isItemTag && tagName.equals("ad_url") && url.equals("")) {
						url = mXpp.getText();
					}
					if (isItemTag && tagName.equals("ad_link") && link.equals("")) {
						link = mXpp.getText();
					}

				} else if (eventType == XmlPullParser.END_TAG) { // 닫는 태그를 만났을때
					// 태그명을 저장
					tagName = mXpp.getName();

					if (tagName.equals("ROW")) {

						BannerModel model = new BannerModel();
						model.link = link;
						model.url = url;
						model.title = title;
						model.idx = idx;
						datas.add(model);

						isItemTag = false; // 초기화
						tagName = "";
						link = "";
						idx = "";
						title = "";
						url = "";

					}

				}

				eventType = mXpp.next(); // 다음 이벤트 타입
			}

		} catch (Exception e) {
			datas = null;
		}

		return datas;

	}
}
