package com.gntsoft.famiwel.server;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.pluslibrary.PlusConstants;
import com.pluslibrary.server.PlusXmlParser;
/**
 * 왼쪽 메뉴 서브메뉴 파싱
 */
public class LeftMenuThirdLevelParser extends PlusXmlParser {
	
	public ArrayList<ArrayList<ArrayList<MenuModel>>> doIt(InputStream in) {
		ArrayList<ArrayList<ArrayList<MenuModel>>> model = new ArrayList<ArrayList<ArrayList<MenuModel>>>();

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
			String link = "";

			ArrayList<MenuModel> secondLevelData = null;
			ArrayList<ArrayList<MenuModel>> firstLevelData = null;
			// xml의 데이터의 끝까지 돌면서 원하는 데이터를 얻어옴
			while (eventType != XmlPullParser.END_DOCUMENT) {

				if (eventType == XmlPullParser.START_TAG) { // 시작 태그를 만났을때.
					// 태그명을 저장
					tagName = mXpp.getName();
					if (tagName.equals("THIRDROW"))
						isItemTag = true;
					else if (tagName.equals("SUBROW"))
						secondLevelData = new ArrayList<MenuModel>();
					else if (tagName.equals("ROW"))
						firstLevelData = new ArrayList<ArrayList<MenuModel>>();

				} else if (eventType == XmlPullParser.TEXT) { // 내용
					if (isItemTag && tagName.equals("third_title")
							&& title.equals("")) {
						title = mXpp.getText();
					}

					if (isItemTag && tagName.equals("third_link")
							&& link.equals("")) {
						link = mXpp.getText();
					}

				} else if (eventType == XmlPullParser.END_TAG) { // 닫는 태그를 만났을때
					// 태그명을 저장
					tagName = mXpp.getName();

					if (tagName.equals("THIRDROW")) {
						MenuModel memuModel = new MenuModel();
						memuModel.title = title;
						memuModel.link = link;
						secondLevelData.add(memuModel);

						isItemTag = false; // 초기화
						tagName = "";
						link = "";
						title = "";

					}else if (tagName.equals("SUBROW")) {

						firstLevelData.add(secondLevelData);

					} else if (tagName.equals("ROW")) {

						model.add(firstLevelData);

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
