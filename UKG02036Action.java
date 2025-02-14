/**
 * @システム名: 受付システム
 * @ファイル名: IUKG02036Dao.java
 * @更新日付：: 2012/05/30
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴: 2013/05/17  SYS_ 郭凡    (案件C37109-1)受付システムSTEP2要望対応
 * 更新履歴: 2014/01/16  SYS_ 謝超    (案件C37075)オリジナル情報誌リプレース
 * 更新履歴：  2014/06/10	 SYS_ 郭凡   (案件番号C38117)セールスポイント編集機能の追加
 * 更新履歴: 2018/08/14   SYS_肖剣生 (案件番号C42036)デザイン変更
 * 更新履歴: 2019/06/27   SYS_郝年昇 (案件番号C43100)お客様項目の入力簡素化対応
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.KibouJyoukennBean;
import jp.co.token.uketuke.formbean.SchoolFacilityBusBean;
import jp.co.token.uketuke.service.IBukkenSearchService;
import jp.co.token.uketuke.service.IUKG02036Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 学校・施設・バス停の設定
 * [説 明] 学校・施設・バス停を設定する。
 * @author [作 成] 2012/05/30 SYS_趙
 * </pre>
 */
public class UKG02036Action extends BaseAction {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -1405375042398666652L;

	/** 希望路線の設定画面用データ */
	private Map<String, Object> ukg02036Data = null;

	/** 希望路線の設定サービス */
	private IUKG02036Service UKG02036Services;

	/** 物件検索サービス */
	// ########## 2014/01/16 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース
	// private IBukkenSearchService BukkenSearchService;
	private IBukkenSearchService bukkenSearchService;
	// ########## 2014/01/16 謝超 UPD End

	/**
	 * <pre>
	 * [説 明] コンストラクタ(引数有る)
	 * </pre>
	 */
	public UKG02036Action(
	    IUKG02036Service uKG02036Services,
	    IBukkenSearchService bukkenSearchService) {

		super();
		UKG02036Services = uKG02036Services;
		// ########## 2014/01/16 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース
		// BukkenSearchService = bukkenSearchService;
		this.bukkenSearchService = bukkenSearchService;
		// ########## 2014/01/16 謝超 UPD End
	}

	/**
	 * <pre>
	 * [説 明] デフォルトコンストラクタ
	 * </pre>
	 */
	public UKG02036Action() {

		// デフォルトコンストラクタ
	}

	/**
	 * <pre>
	 * [説 明] 画面初期表示
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		// ログイン情報
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
//		Map<String, Object> sessionLoginInfo = getLoginInfo();
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		// ########## 2014/06/10 郭凡 UPD End
		ukg02036Data = new HashMap<String, Object>();
		String jusyo_cd = null;
		String strInitDataFlg = null;
		String jusyo2 = null;
		HttpServletRequest request = ServletActionContext.getRequest();
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);
		String maxCount = PropertiesReader.getIntance().getProperty(Consts.UKG02036_MAXKENSAKUSUU);
		// 都道府県リストを取得する
		ukg02036Data.put("JUSYO_LIST", UKG02036Services.getJusyoData());
		if (kibouJyoukennBean != null) {
			SchoolFacilityBusBean schoolFacilityBusBean = kibouJyoukennBean.getSchoolFacilityBusBean();
			if ("5".equals(kibouJyoukennBean.getKibouJyoukennCode())) {
				strInitDataFlg = "1";
				jusyo_cd = schoolFacilityBusBean.getJisCode().substring(0, 2);
				jusyo2 = schoolFacilityBusBean.getSiCode();
				ukg02036Data.put("SchoolFacilityBusBean", schoolFacilityBusBean);
				ukg02036Data.put("selSisetu", schoolFacilityBusBean.getSetubiTypeCode());
			} else {
				strInitDataFlg = "0";
				// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
//				jusyo_cd = (String) sessionLoginInfo.get(Consts.POST_JUSYO_CD);
				jusyo_cd = (String) uketukePostInfo.get(Consts.POST_JUSYO_CD);
				// ########## 2014/06/10 郭凡 UPD End
				jusyo2 = (String) request.getSession().getAttribute("SHIKUGUN_CD");

				if (!Util.isEmpty(kibouJyoukennBean.getPrefectureCd())) {
					jusyo_cd = kibouJyoukennBean.getPrefectureCd();
				}else {
					// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
//					jusyo2 = (String) sessionLoginInfo.get(Consts.POST_SHIKUGUN_CD);
					jusyo2 = (String) uketukePostInfo.get(Consts.POST_SHIKUGUN_CD);
					// ########## 2014/06/10 郭凡 UPD End
				}
			}
		}
		ukg02036Data.put("INITDATAFLG", strInitDataFlg);
		// 店舗所在都道府県コード
		ukg02036Data.put("POST_JUSYO_CD", jusyo_cd);

		// 市区町村リストを取得する
		ukg02036Data.put("JUSYO2DATA", UKG02036Services.getJusyo2Data(jusyo_cd));
		ukg02036Data.put("POSTSHIKUGUNCD", jusyo2);

		// 生活施設検索ボタン取得する
		ukg02036Data.put("SISETUTABLIST", UKG02036Services.getKoumoku("074",null));

		// 駅選択最大数
		ukg02036Data.put("MAXCOUNT", maxCount);
		ukg02036Data.put("MAP_SERVLETURL", PropertiesReader.getIntance().getProperty(Consts.MAP_SERVLETURL));
		ukg02036Data.put("MAP_KEY", PropertiesReader.getIntance().getProperty(Consts.MAP_KEY));
		ukg02036Data.put("MAP_VERSION", PropertiesReader.getIntance().getProperty(Consts.MAP_VERSION));
		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] 市区郡データ取得
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchJusyo2Data() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// 県コード
		String jusyo1 = request.getParameter("jusyo1");
		ukg02036Data = new HashMap<String, Object>();
		// 都道府県コードより、市区郡リストを取得する。
		if (!Util.isEmpty(jusyo1)) {
			ukg02036Data.put("JUSYO2DATA", UKG02036Services.getJusyo2Data(jusyo1));
		} else {
			ukg02036Data.put("JUSYO2DATA", null);
		}
		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] バス会社リストを取得する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchBusKaisyaList() throws Exception {

		ukg02036Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		// 施設分類1情報
		String jyusyo = request.getParameter("jusyo");
		String cityCd = request.getParameter("cityCd");

		// バス会社リストを取得する。
		ukg02036Data.put("BUSKAISYALIST", UKG02036Services.getBusKaisyaList(jyusyo, cityCd));

		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] バス路線リスト生成する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchBusRosenList() throws Exception {

		ukg02036Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		String jyusyo = request.getParameter("jyusyo");
		String cityCd = request.getParameter("cityCd");
		String busKaisya = request.getParameter("busKaisya");

		// バス路線リスト生成する。
		ukg02036Data.put("BUSROSENLIST", UKG02036Services.getBusRosenList(jyusyo + cityCd, busKaisya));

		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] 施設バスマスタ検索
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchShisetsuBusList() throws Exception {

		ukg02036Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		String busKaisyaCode = request.getParameter("busKaisyaCode");
		String busCategoryCode = request.getParameter("busCategoryCode");
		String maxcount = request.getParameter("maxcount");

		// 施設バスマスタ検索
		ukg02036Data.put("BUSROSENLIST", UKG02036Services.getShisetsuBusList(busKaisyaCode, busCategoryCode, maxcount));

		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] UKG02036Data作成
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String toSession02036Data() throws Exception {

		ukg02036Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		KibouJyoukennBean kibouJyoukenn = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);

		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) kibouJyoukenn.clone();

		SchoolFacilityBusBean schoolFacilityBusBean = new SchoolFacilityBusBean();
		// 希望条件指定コード
		kibouJyoukennBean.setKibouJyoukennCode("5");
		kibouJyoukennBean.setSchoolFacilityBusBean(schoolFacilityBusBean);
		if ("31".equals(request.getParameter("sisetutype")) || "61".equals(request.getParameter("sisetutype"))) {
			schoolFacilityBusBean.setSetubiName(request.getParameter("setubiName"));
			schoolFacilityBusBean.setSetubiKb(request.getParameter("setubiKb"));
			schoolFacilityBusBean.setSetubiTypeCode(request.getParameter("sisetutype"));
			schoolFacilityBusBean.setJisCode(request.getParameter("jisCode"));
			schoolFacilityBusBean.setSiCode(request.getParameter("siCode"));

			// ########## 2014/01/16 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース
			// Map<String, Object> resul = BukkenSearchService.getBukkenCount(kibouJyoukennBean);
			// 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
			// Map<String, Object> resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean);
			Map<String, Object> resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean,Consts.BUKEN_COUNT_All);
			// 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
			// ########## 2014/01/16 謝超 UPD End
			ukg02036Data.put("errMsg", "");
			if (!"0000000".equals(resul.get("restCd"))) {
				ukg02036Data.put("errMsg", Util.getServerMsg("ERRS002", "検索処理"));
			}
			// 2018/08/13 SYS_肖剣生 UPD START (案件番号C42036)デザイン変更
			/*//2013/05/17    郭凡(SYS)  UPD START 案件C37109-1
			//String bukkenCount = resul.get("bukkenCount").toString();
			//ukg02036Data.put("BUKKENCOUNT", bukkenCount);
			String heyaCount = resul.get("heyaCount").toString();
			ukg02036Data.put("HEYACOUNT", heyaCount);
			//2013/05/17    郭凡(SYS)  UPD END 案件C37109-1
            */
			ukg02036Data.put("HEYACOUNT", resul.get("bukkenCount").toString());
			// 2018/08/13 SYS_肖剣生 UPD END (案件番号C42036)デザイン変更
			kibouJyoukennBean.setSchoolFacilityBusBean(schoolFacilityBusBean);
		} else {
			schoolFacilityBusBean.setSetubiName(request.getParameter("setubiName"));
			schoolFacilityBusBean.setSetubiKb(request.getParameter("setubiKb"));
			schoolFacilityBusBean.setSetubiTypeCode(request.getParameter("sisetutype"));
			schoolFacilityBusBean.setJisCode(request.getParameter("jisCode"));
			schoolFacilityBusBean.setSetubiIdo(request.getParameter("setubiIdo"));
			schoolFacilityBusBean.setSetubiKeido(request.getParameter("setubiKeido"));
			schoolFacilityBusBean.setKibourange(request.getParameter("kibourange"));
			schoolFacilityBusBean.setSiCode(request.getParameter("siCode"));
			schoolFacilityBusBean.setBusKaisyaCode(request.getParameter("busKaisyaCode"));
			schoolFacilityBusBean.setBusCategoryCode(request.getParameter("busCategoryCode"));
			resultProcess(kibouJyoukennBean, schoolFacilityBusBean);
		}
		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] 設定ボタンの処理
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String save2Session() throws Exception {

		ukg02036Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);
		if (kibouJyoukennBean == null) {
			kibouJyoukennBean = new KibouJyoukennBean();
		}
		String sisetutype = request.getParameter("sisetutype");
		SchoolFacilityBusBean schoolFacilityBusBean = new SchoolFacilityBusBean();
		schoolFacilityBusBean.setSetubiKb(request.getParameter("setubiKb"));
		schoolFacilityBusBean.setJisCode(request.getParameter("jisCode"));
		schoolFacilityBusBean.setSiCode(request.getParameter("siCode"));
		schoolFacilityBusBean.setSetubiTypeCode(sisetutype);
		if ("31".equals(sisetutype) || "61".equals(sisetutype)) {
			schoolFacilityBusBean.setSetubiName(request.getParameter("setubiName"));
		} else {
			schoolFacilityBusBean.setSetubiName(request.getParameter("setubiName"));
			schoolFacilityBusBean.setSetubiIdo(request.getParameter("setubiIdo"));
			schoolFacilityBusBean.setSetubiKeido(request.getParameter("setubiKeido"));
			schoolFacilityBusBean.setBusKaisyaCode(request.getParameter("busKaisyaCode"));
			schoolFacilityBusBean.setBusCategoryCode(request.getParameter("busCategoryCode"));
			schoolFacilityBusBean.setKibourangeCode(request.getParameter("kibourangeCode"));
		}
		// 希望条件指定コード
		kibouJyoukennBean.setKibouJyoukennCode("5");
		kibouJyoukennBean.setPrefectureCd(request.getParameter("prefectureCd"));
		// 施設までの距離コード
		schoolFacilityBusBean.setKibourange(request.getParameter("kibourange"));
		kibouJyoukennBean.setKiboujyokenDisp(request.getParameter("jyoukenn"));
		kibouJyoukennBean.setSchoolFacilityBusBean(schoolFacilityBusBean);
		request.getSession().setAttribute(Consts.KIBOUJYOUKENBEAN, kibouJyoukennBean);
		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] 【F000】施設リスト作成処理(通常処理)
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String sisetuListTuujyoSakusei() throws Exception {
		ukg02036Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		String setubiKb = request.getParameter("setubiKb");
		List<Map<String, Object>> koumoku = UKG02036Services.getSisetuListData("067",setubiKb);
		ukg02036Data.put("SETUBISELLIST", koumoku);
		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] 施設マスタ検索(通常パターン)する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchShisetsuList() throws Exception {
		ukg02036Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		String shisetuCode = request.getParameter("shisetuCode");
		String prefectureCd = request.getParameter("prefectureCd");
		String siCode = request.getParameter("siCode");
		// 施設マスタ検索(通常パターン)する。
		ukg02036Data.put("SHISETSULIST", UKG02036Services.getShisetsuList(shisetuCode, prefectureCd, siCode));
		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] 結果処理
	 * @throws Exception　処理異常
	 * </pre>
	 */
	private void resultProcess(KibouJyoukennBean kibouJyoukennBean,
	                           SchoolFacilityBusBean schoolFacilityBusBean) throws Exception {

		List<Map<String, Object>> koumoku = UKG02036Services.getKoumoku("044",null);
		for (Map<String, Object> map : koumoku) {
			schoolFacilityBusBean.setKibourange((String) map.get("KOUMOKU_SB_VAL"));
			schoolFacilityBusBean.setKibourangeCode((String) map.get("KOUMOKU_SB_CD"));
			kibouJyoukennBean.setSchoolFacilityBusBean(schoolFacilityBusBean);
			// ########## 2014/01/16 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース
			// Map<String, Object> resul = BukkenSearchService.getBukkenCount(kibouJyoukennBean);
			// 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
			// Map<String, Object> resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean);
			Map<String, Object> resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean,Consts.BUKEN_COUNT_All);			
			// 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
			// ########## 2014/01/16 謝超 UPD End
			ukg02036Data.put("errMsg", "");
			if (!"0000000".equals(resul.get("restCd"))) {
				ukg02036Data.put("errMsg", Util.getServerMsg("ERRS002", "検索処理"));
				//2013/05/17    郭凡(SYS)  UPD START 案件C37109-1
				//map.put("BUKKENCOUNT", "0");
				 map.put("HEYACOUNT", "0");
				//2013/05/17    郭凡(SYS)  UPD END 案件C37109-1
			} else {
				// 2018/08/13 SYS_肖剣生 UPD START (案件番号C42036)デザイン変更
				/*//2013/05/17    郭凡(SYS)  UPD START 案件C37109-1
				//String bukkenCount = resul.get("bukkenCount").toString();
				//map.put("BUKKENCOUNT", bukkenCount);
				String heyaCount = resul.get("heyaCount").toString();
				map.put("HEYACOUNT", heyaCount);*/
				map.put("HEYACOUNT", resul.get("bukkenCount").toString());
				//2013/05/17    郭凡(SYS)  UPD END 案件C37109-1
				// 2018/08/13 SYS_肖剣生 UPD END (案件番号C42036)デザイン変更
			}
		}
		ukg02036Data.put("KIBOURANGELIST", koumoku);
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを取得する。
	 * @return 画面のデータ
	 * </pre>
	 */
	public Map<String, Object> getUkg02036Data() {

		return ukg02036Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg02036Data 画面のデータ
	 * </pre>
	 */
	public void setUkg02036Data(Map<String, Object> ukg02036Data) {

		this.ukg02036Data = ukg02036Data;
	}

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param services サービス対象
	 * </pre>
	 */
	public void setUKG02036Services(IUKG02036Service uKG02036Services) {

		UKG02036Services = uKG02036Services;
	}

	/**
	 * <pre>
	 * [説 明] 物件検索サービスを設定する。
	 * @param bukkenSearchService　物件検索サービス
	 * </pre>
	 */
	public void setBukkenSearchService(IBukkenSearchService bukkenSearchService) {
		// ########## 2014/01/16 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース
		// BukkenSearchService = bukkenSearchService;
		this.bukkenSearchService = bukkenSearchService;
		// ########## 2014/01/16 謝超 UPD End
	}
}
