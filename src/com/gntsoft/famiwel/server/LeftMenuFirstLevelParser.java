package com.gntsoft.famiwel.server;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.pluslibrary.PlusConstants;
import com.pluslibrary.server.PlusXmlParser;

/**
 * 왼쪽 메뉴 메인메뉴 파싱
 */
public class LeftMenuFirstLevelParser extends PlusXmlParser {

	public ArrayList<String> doIt(InputStream in) {
		ArrayList<String> model = new ArrayList<String>();

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

			// xml의 데이터의 끝까지 돌면서 원하는 데이터를 얻어옴
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_TAG) { // 시작 태그를 만났을때.
					// 태그명을 저장
					tagName = mXpp.getName();
					if (tagName.equals("ROW"))
						isItemTag = true;

				} else if (eventType == XmlPullParser.TEXT) { // 내용
					if (isItemTag && tagName.equals("ad_title")
							&& title.equals("")) {
						title = mXpp.getText();
					}

				} else if (eventType == XmlPullParser.END_TAG) { // 닫는 태그를 만났을때
					// 태그명을 저장
					tagName = mXpp.getName();

				
					if (tagName.equals("ROW")) {

						model.add(title);

						isItemTag = false; // 초기화
						tagName = "";
						title = "";

					}

				}

				eventType = mXpp.next(); // 다음 이벤트 타입
			}

		} catch (Exception e) {
			model = null;
		}

		return model;

	}

}
