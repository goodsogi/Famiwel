package com.gntsoft.famiwel.server;

/**
 * 패미웰 api 상수
 * 
 * @author jeff
 * 
 */
public class FWApiConstants {
	public static final String API_MAIN = "http://famiwel.co.kr/webservice/"; // api
	public static final String LOG_IN = API_MAIN + "login_ok.php"; // 로그인
	public static final String GET_MAIN_BANNER = API_MAIN
			+ "get_main_banner.php"; // 메인배너
	public static final String GET_MAIN_LIST = API_MAIN + "get_main_list.php"; // 메인리스트
	public static final String GET_WELPANG_LIST = API_MAIN
			+ "get_welpang_list.php"; // 웰팡리스트
	public static final String GET_MENU = API_MAIN + "get_menu_xml.php"; // 메뉴
	public static final String GET_BEST = API_MAIN + "get_best100_list.php"; // 베스트
	public static final String GET_WELPANG_SUB = API_MAIN
			+ "get_welpang_sub_menu.php"; // 웰팡 서브 메뉴
	public static final String CHECK_ATTENDANCE = "http://famiwel.co.kr/_prozn/_system/m/wheel.php?top_menu=4&tplMode=android"; // 출석체크(웹페이지)
	public static final String GET_WELFARE = API_MAIN + "get_welfare_zone.php"; // 복지존
	public static final String GET_WELFARE_DETAIL = API_MAIN
			+ "get_welfare_zone_2.php?bz_subject=01"; // 복지존상세
	public static final String GET_USER_NAME =API_MAIN
			+ "get_userinfo.php";
	public static final Object GET_AD = API_MAIN
			+ "get_ad.php";
}
