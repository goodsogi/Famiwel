package com.gntsoft.famiwel.server;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.pluslibrary.PlusConstants;
import com.pluslibrary.server.PlusXmlParser;

/**
 * 패미웰 기본 파서
 * 
 * @author jeff
 * 
 */
public class FWParser extends PlusXmlParser {
	public ArrayList<FWModel> doIt(InputStream in) {
		ArrayList<FWModel> datas = new ArrayList<FWModel>();
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
			String url = "";
			String link = "";
			String name = "";
			String price = "";
			String sale_price = "";
			String percentage = "";
			String gubun = "";
			String delivery_price = "";

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
					
					if (isItemTag && tagName.equals("ad_url") && url.equals("")) {
						url = mXpp.getText();
					}
					if (isItemTag && tagName.equals("ad_link") && link.equals("")) {
						link = mXpp.getText();
					}
					
					if (isItemTag && tagName.equals("ad_name") && name.equals("")) {
						name = mXpp.getText();
					}
					if (isItemTag && tagName.equals("ad_market_price") && price.equals("")) {
						price = mXpp.getText();
					}
					if (isItemTag && tagName.equals("ad_price") && sale_price.equals("")) {
						sale_price = mXpp.getText();
					}
					if (isItemTag && tagName.equals("ad_discount") && percentage.equals("")) {
						percentage = mXpp.getText();
					}
					if (isItemTag && tagName.equals("gubun") && gubun.equals("")) {
						gubun = mXpp.getText();
					}
					if (isItemTag && tagName.equals("gubun") && gubun.equals("")) {
						gubun = mXpp.getText();
					}
					if (isItemTag && tagName.equals("ad_deliv_price") && delivery_price.equals("")) {
						delivery_price = mXpp.getText();
					}

				} else if (eventType == XmlPullParser.END_TAG) { // 닫는 태그를 만났을때
					// 태그명을 저장
					tagName = mXpp.getName();

					if (tagName.equals("ROW")) {

						FWModel model = new FWModel();
						model.link = link;
						model.url = url;
						model.title = title;
						model.name = name;
						model.price = price;
						model.sale_price = sale_price;
						model.percentage = percentage;
						model.gubun = gubun;
						model.delivery_price = delivery_price;
						datas.add(model);

						isItemTag = false; // 초기화
						tagName = "";
						link = "";
						title = "";
						url = "";
						name = "";
						price = "";
						sale_price = "";
						percentage = "";
						gubun = "";
						delivery_price = "";

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
