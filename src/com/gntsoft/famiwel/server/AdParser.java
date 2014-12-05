package com.gntsoft.famiwel.server;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.gntsoft.famiwel.main.AdModel;
import com.pluslibrary.PlusConstants;
import com.pluslibrary.server.PlusXmlParser;

public class AdParser extends PlusXmlParser {
	public AdModel doIt(InputStream in) {
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

			String tagName = "";
			String index = "";
			String gubun = "";
			String detailUrl = "";
			String imageUrl = "";

			// xml의 데이터의 끝까지 돌면서 원하는 데이터를 얻어옴
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_TAG) { // 시작 태그를 만났을때.
					// 태그명을 저장
					tagName = mXpp.getName();

					

				} else if (eventType == XmlPullParser.TEXT) { // 내용
					
					if (tagName.equals("index") && index.equals("")) {
						index = mXpp.getText();
					}
					
					
					if (tagName.equals("gubun") && gubun.equals("")) {
						gubun = mXpp.getText();
					}

					if (tagName.equals("url") && detailUrl.equals("")) {
						detailUrl = mXpp.getText();
					}

					if (tagName.equals("image") && imageUrl.equals("")) {
						imageUrl = mXpp.getText();
					}

				} else if (eventType == XmlPullParser.END_TAG) { // 닫는 태그를 만났을때
					// 태그명을 저장
					tagName = mXpp.getName();

					if (tagName.equals("ROW")) {
						AdModel adModel = new AdModel();
						adModel.setIndex(index);
						adModel.setGubun(gubun);
						adModel.setDetailUrl(detailUrl);
						adModel.setImageUrl(imageUrl);
						return adModel;
					}

				}
				eventType = mXpp.next(); // 다음 이벤트 타입

			}
		} catch (Exception e) {
		}

		return null;

	}
}
