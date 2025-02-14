/**
 * @システム名: 希望地域の設定(エリア選択)
 * @ファイル名: IUKG02032Action.java
 * @更新日付： 2012/05/29
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴: 2013/05/17  SYS_ 郭凡    (案件C37109-1)受付システムSTEP2要望対応
 * 更新履歴: 2014/01/07  SYS_ 郭凡    (案件C37075)オリジナル情報誌リプレース
 * 更新履歴: 2014/06/10	 SYS_ 郭凡    (案件番号C38117)セールスポイント編集機能の追加
 * 更新履歴: 2018/08/14   SYS_肖剣生 (案件番号C42036)デザイン変更
 * 更新履歴: 2019/06/20   SYS_郝年昇 (案件番号C43100)お客様項目の入力簡素化対応
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.JusyohaniBean;
import jp.co.token.uketuke.formbean.KibouJyoukennBean;
import jp.co.token.uketuke.service.IBukkenSearchService;
import jp.co.token.uketuke.service.IUKG02032Service;
import jp.co.token.uketuke.service.IUKG04012Service;

/**
 * <pre>
 * [機 能] 希望地域の設定(エリア選択)
 * [説 明]希望地域の設定(エリア選択)する。
 * @author [作 成] 2012/05/29 SYS_郭
 * </pre>
 */
public class UKG02032Action extends BaseAction {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4778159824126799116L;

	/** 希望地域の設定(エリア選択)画面用データ */
	private Map<String, Object> ukg02032Data = new HashMap<String, Object>();

	/** 物件検索サービス */
	private IBukkenSearchService bukkenSearchService;

	// 2014/01/07  郭凡 ADD Start (案件番号C37075)オリジナル情報誌リプレース
	/** 希望地域の設定(エリア選択) */
	private IUKG02032Service UKG02032Services;
	// 2014/01/07  郭凡 ADD End
	
	// 2018/09/26 SYS_肖剣生 ADD START (案件番号C42036)デザイン変更
	/** チラシ作成(地図検索)サービス */
	private IUKG04012Service UKG04012Services;
	// 2018/09/26 SYS_肖剣生 ADD END
	
	/**
	 * <pre>
	 * [説 明] 物件検索サービスを設定する。
	 * @param bukkenSearchService 物件検索サービス
	 * </pre>
	 */
	public void setBukkenSearchService(IBukkenSearchService bukkenSearchService) {

		this.bukkenSearchService = bukkenSearchService;
	}

	/** 希望県コード */
	private String prefectureCd;

	/** JIS住所コード */
	private String hidJisJusyoCd;

	/** 地点１緯度 */
	private String hidPoint1Ido;

	/** 地点１経度 */
	private String hidPoint1Keido;

	/** 地点2緯度 */
	private String hidPoint2Ido;

	/** 地点2経度 */
	private String hidPoint2Keido;

	/** 中心緯度 */
	private String hidCenterIdo;

	/** 中心経度 */
	private String hidCenterKeido;

	/** 地図縮尺コード */
	private String hidMapScaleCd;

	/** 最小地図縮尺コード */
	private String minScale;

	/** 範囲指定区分 */
	private String hidAreaSiteiKb;

	/** 初期化フラグ */
	private String hid02032InitFlg;

	/** 地図サーブレットURL */
	private String mapServleturl;
	
	/** 地図のキー */
	private String mapKey;
	
	/** 地図のバージョン */
	private String mapVersion;
	// 2014/01/07  郭凡 ADD Start (案件番号C37075)オリジナル情報誌リプレース
	/** プリセット№ */
	private String hidPreset;	
	// 2014/01/07  郭凡 ADD End
	/**
	 * <pre>
	 * [説 明] 希望県コードを取得する。
	 * @return 希望県コード
	 * </pre>
	 */
	public String getPrefectureCd() {

		return prefectureCd;
	}

	/**
	 * <pre>
	 * [説 明] 希望県コードを設定する。
	 * @param prefectureCd　希望県コード
	 * </pre>
	 */
	public void setPrefectureCd(String prefectureCd) {

		this.prefectureCd = prefectureCd;
	}

	/**
	 * <pre>
	 * [説 明] hid02032InitFlg%%を取得する。
	 * @return the hid02032InitFlg%%
	 * </pre>
	 */
	public String getHid02032InitFlg() {

		return hid02032InitFlg;
	}

	/**
	 * <pre>
	 * [説 明] hid02032InitFlg%%を設定する。
	 * @param hid02032InitFlg hid02032InitFlg%%
	 * </pre>
	 */
	public void setHid02032InitFlg(String hid02032InitFlg) {

		this.hid02032InitFlg = hid02032InitFlg;
	}

	public String getHidPoint1Ido() {

		return hidPoint1Ido;
	}

	public void setHidPoint1Ido(String hidPoint1Ido) {

		this.hidPoint1Ido = hidPoint1Ido;
	}

	public String getHidPoint1Keido() {

		return hidPoint1Keido;
	}

	public void setHidPoint1Keido(String hidPoint1Keido) {

		this.hidPoint1Keido = hidPoint1Keido;
	}

	public String getHidPoint2Ido() {

		return hidPoint2Ido;
	}

	public void setHidPoint2Ido(String hidPoint2Ido) {

		this.hidPoint2Ido = hidPoint2Ido;
	}

	public String getHidPoint2Keido() {

		return hidPoint2Keido;
	}

	public void setHidPoint2Keido(String hidPoint2Keido) {

		this.hidPoint2Keido = hidPoint2Keido;
	}

	public String getHidCenterIdo() {

		return hidCenterIdo;
	}

	public void setHidCenterIdo(String hidCenterIdo) {

		this.hidCenterIdo = hidCenterIdo;
	}

	public String getHidCenterKeido() {

		return hidCenterKeido;
	}

	public void setHidCenterKeido(String hidCenterKeido) {

		this.hidCenterKeido = hidCenterKeido;
	}

	public String getHidMapScaleCd() {

		return hidMapScaleCd;
	}

	public void setHidMapScaleCd(String hidMapScaleCd) {

		this.hidMapScaleCd = hidMapScaleCd;
	}

	public String getHidAreaSiteiKb() {

		return hidAreaSiteiKb;
	}

	public void setHidAreaSiteiKb(String hidAreaSiteiKb) {

		this.hidAreaSiteiKb = hidAreaSiteiKb;
	}

	public String getHidJisJusyoCd() {

		return hidJisJusyoCd;
	}

	public void setHidJisJusyoCd(String hidJisJusyoCd) {

		this.hidJisJusyoCd = hidJisJusyoCd;
	}

	public String getMinScale() {

		return minScale;
	}

	public void setMinScale(String minScale) {

		this.minScale = minScale;
	}

    public String getMapServleturl() {
    
    	return mapServleturl;
    }

    public void setMapServleturl(String mapServleturl) {
    
    	this.mapServleturl = mapServleturl;
    }

    public String getMapKey() {
    
    	return mapKey;
    }

    public void setMapKey(String mapKey) {
    
    	this.mapKey = mapKey;
    }

    public String getMapVersion() {
    
    	return mapVersion;
    }

    public void setMapVersion(String mapVersion) {
    
    	this.mapVersion = mapVersion;
    }
// 2014/01/07  郭凡 ADD Start (案件番号C37075)オリジナル情報誌リプレース
    public String getHidPreset() {

		return hidPreset;
    }

    public void setHidPreset(String hidPreset) {

		this.hidPreset = hidPreset;
    }

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param services サービス対象
	 * </pre>
	 */
	public void setUKG02032Services(IUKG02032Service uKG02032Services) {

		UKG02032Services = uKG02032Services;
	}
// 2014/01/07  郭凡 ADD End

	// 2018/09/26 SYS_肖剣生 ADD START (案件番号C42036)デザイン変更
	public void setUKG04012Services(IUKG04012Service uKG04012Services) {
		UKG04012Services = uKG04012Services;
	}
	// 2018/09/26 SYS_肖剣生 ADD END
	
	/**
	 * <pre>
	 * [説 明] 画面のデータを取得する。
	 * @return 画面のデータ
	 * </pre>
	 */
	public Map<String, Object> getUkg02032Data() {

		return ukg02032Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg02032Data 画面のデータ
	 * </pre>
	 */
	public void setUkg02032Data(Map<String, Object> ukg02032Data) {

		this.ukg02032Data = ukg02032Data;
	}

	public UKG02032Action(
	    IBukkenSearchService bukkenSearchService) {

		super();
		this.bukkenSearchService = bukkenSearchService;
	}

	public UKG02032Action() {

		super();
	}

	/**
	 * <pre>
	 * [説 明] 都道府県リストのデータ設定
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		// ログイン情報
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		// 2014/01/07  郭凡 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		String presetNo = Util.toEmpty(request.getParameter("presetNo"));
		String pageId = Util.toEmpty(request.getParameter("pageId"));
		ukg02032Data.put("pageId", pageId);
		// 2014/01/07  郭凡 ADD End
		
		// 2018/09/26 SYS_肖剣生 ADD START (案件番号C42036)デザイン変更
		// 会社コードを取得
		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String postCd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		// 緯度経度の取得
		List<Map<String, Object>> postionList = (List<Map<String, Object>>) UKG04012Services.searchCenterPosition(kaisyaCd, postCd).get("rst_position_list");
		hidCenterIdo = postionList.get(0).get("I_DO").toString();
		hidCenterKeido = postionList.get(0).get("KEI_DO").toString();
		// 2018/09/26 SYS_肖剣生 ADD END
		
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		// ########## 2014/06/10 郭凡 ADD End
		if ("0".equals(hid02032InitFlg)) {
			if (Util.isEmpty(prefectureCd)) {
				// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
				if ("".equals(presetNo) || presetNo == null) {
					prefectureCd = (String)uketukePostInfo.get(Consts.POST_JUSYO_CD);
				} else {
					prefectureCd = (String)sessionLoginInfo.get(Consts.POST_JUSYO_CD);
				}
				// ########## 2014/06/10 郭凡 UPD End
			}
			hidMapScaleCd = PropertiesReader.getIntance().getProperty(Consts.UKG03032_MAP_INITSCALE);
			hidAreaSiteiKb = "0";
			// 2014/01/07  郭凡 ADD Start (案件番号C37075)オリジナル情報誌リプレース	
			hidPreset = presetNo;
			// 2014/01/07  郭凡 ADD End

		} else {
			// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース
//			KibouJyoukennBean kibouJyokenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);
			KibouJyoukennBean kibouJyokenBean = null;
			kibouJyokenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN+presetNo);
			// 2014/01/07  郭凡 UPD End
			
			JusyohaniBean jusyohani = kibouJyokenBean.getJusyohaniBean();
			if (jusyohani != null) {
				hidJisJusyoCd = jusyohani.getJisJusyoCd();
				prefectureCd = hidJisJusyoCd.substring(0, 2);

				// 指定地域名
				ukg02032Data.put("SiteiAreaName", jusyohani.getAreaName());

				hidPoint1Keido = jusyohani.getPoint1Keido();
				hidPoint1Ido = jusyohani.getPoint1Ido();

				hidPoint2Keido = jusyohani.getPoint2Keido();
				hidPoint2Ido = jusyohani.getPoint2Ido();
				hidCenterIdo = jusyohani.getCenterIdo();
				hidCenterKeido = jusyohani.getCenterKeido();
				hidMapScaleCd = jusyohani.getMapScaleCd();
				hidAreaSiteiKb = jusyohani.getAreaSiteiKb();
				hid02032InitFlg = "1";
				// 2014/01/07  郭凡 ADD Start (案件番号C37075)オリジナル情報誌リプレース	
				hidPreset = presetNo;
				// 2014/01/07  郭凡 ADD End
			}

		}
		// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース
//		minScale = PropertiesReader.getIntance().getProperty(Consts.UKG03032_MAP_MINSCALE);
		if("05020".equals(pageId)){
			minScale = PropertiesReader.getIntance().getProperty(Consts.UKG05020_MAP_MINSCALE);
		}else{
			minScale = PropertiesReader.getIntance().getProperty(Consts.UKG03032_MAP_MINSCALE);
		}
		// 2014/01/07  郭凡 UPD End
		mapServleturl = PropertiesReader.getIntance().getProperty(Consts.MAP_SERVLETURL);
		mapKey = PropertiesReader.getIntance().getProperty(Consts.MAP_KEY);
		mapVersion = PropertiesReader.getIntance().getProperty(Consts.MAP_VERSION);
		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] 市町区のデータ設定
	 * @return 処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String kuusituBukkenCount() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		
		// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース	
//		KibouJyoukennBean kibouJyoukenn = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		KibouJyoukennBean kibouJyoukenn = null; 
		String presetNo = Util.toEmpty(request.getParameter("hidPreset"));
		kibouJyoukenn = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN+presetNo);
		// 2014/01/07  郭凡 UPD End
		KibouJyoukennBean kibouJyokenBean = (KibouJyoukennBean) kibouJyoukenn.clone();

		JusyohaniBean jusyohaniBean = kibouJyokenBean.getJusyohaniBean();
		if (jusyohaniBean == null) {
			jusyohaniBean = new JusyohaniBean();
		} else {
			jusyohaniBean = (JusyohaniBean) jusyohaniBean.clone();
		}

		kibouJyokenBean.setPrefectureCd("");
		kibouJyokenBean.setKibouJyoukennCode("1");
		jusyohaniBean.setPoint1Ido(hidPoint1Ido);
		jusyohaniBean.setPoint1Keido(hidPoint1Keido);
		jusyohaniBean.setPoint2Keido(hidPoint2Keido);
		jusyohaniBean.setPoint2Ido(hidPoint2Ido);
		kibouJyokenBean.setJusyohaniBean(jusyohaniBean);
		// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース	
//		Map<String, Object> resul = bukkenSearchService.getBukkenCount(kibouJyokenBean);
		Map<String, Object> result = null;
		if(Consts.UKG05022_GAMEN_KB.equals(kibouJyoukenn.getGamenKubun())){
			result = UKG02032Services.getshicyakubukkenCount((String) sessionLoginInfo.get(Consts.KAISYA_CD),
					jusyohaniBean);
			// 2018/08/13 SYS_肖剣生 ADD START (案件番号C42036)デザイン変更
			if ("0000000".equals(result.get("restCd").toString())) {
				ukg02032Data.put("heyaCount", result.get("heyaCount"));
			}
			// 2018/08/13 SYS_肖剣生 ADD END (案件番号C42036)デザイン変更
		}else {
		    // 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		    // result = bukkenSearchService.getBukkenCount(kibouJyokenBean);
			result = bukkenSearchService.getBukkenCount(kibouJyokenBean,Consts.BUKEN_COUNT_All);
			// 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
			
			// 2018/08/13 SYS_肖剣生 ADD START (案件番号C42036)デザイン変更
			if ("0000000".equals(result.get("restCd").toString())) {
				ukg02032Data.put("heyaCount", result.get("bukkenCount"));
			}
			// 2018/08/13 SYS_肖剣生 ADD END (案件番号C42036)デザイン変更
		}
		// 2014/01/07  郭凡 UPD End
		
		ukg02032Data.put("errMsg", "");
		// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース	 // parasoft-suppress UC.ACC "C37075JTest対応"
//		if (!"0000000".equals(resul.get("restCd").toString())) { // parasoft-suppress UC.ACC "C37075JTest対応"
		if (!"0000000".equals(result.get("restCd").toString())) {
		// 2014/01/07  郭凡 UPD End	
			ukg02032Data.put("errMsg", Util.getServerMsg("ERRS002", "検索処理"));
		}
		// 2018/08/13 SYS_肖剣生 DEL START (案件番号C42036)デザイン変更
		/*//2013/05/17    郭凡(SYS)  UPD START 案件C37109-1
		//ukg02032Data.put("bukkenCount", resul.get("bukkenCount"));
		// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース	
//		ukg02032Data.put("heyaCount", resul.get("heyaCount"));
		ukg02032Data.put("heyaCount", result.get("heyaCount"));
		// 2014/01/07  郭凡 UPD End
		//2013/05/17    郭凡(SYS)  UPD END 案件C37109-1
*/		// 2018/08/13 SYS_肖剣生 DEL END (案件番号C42036)デザイン変更
		
		return "kuusituBukkenCount";

	}

	/**
	 * <pre>
	 * [説 明] 市町区のデータ設定
	 * @return 処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String btnSettei() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		
		// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース	
//		KibouJyoukennBean kibouJyokenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);
		KibouJyoukennBean kibouJyokenBean = null; 
		String presetNo = Util.toEmpty(request.getParameter("hidPreset"));
		kibouJyokenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN+presetNo);
		// 2014/01/07  郭凡 UPD End		
		
		if (kibouJyokenBean == null) {
			kibouJyokenBean = new KibouJyoukennBean();
		}

		kibouJyokenBean.setPrefectureCd(hidJisJusyoCd.substring(0, 2));
		session.setAttribute("SHIKUGUN_CD", hidJisJusyoCd.substring(2, 5));
		kibouJyokenBean.setKibouJyoukennCode("1");

		JusyohaniBean jusyohani = new JusyohaniBean();
		String areaName = request.getParameter("areaName");
		String name[] = areaName.split("_");
		jusyohani.setAreaName(name[0] + name[1]);
		jusyohani.setPoint1Ido(hidPoint1Ido);
		jusyohani.setPoint1Keido(hidPoint1Keido);
		jusyohani.setPoint2Keido(hidPoint2Keido);
		jusyohani.setPoint2Ido(hidPoint2Ido);
		jusyohani.setCenterIdo(hidCenterIdo);
		jusyohani.setCenterKeido(hidCenterKeido);
		jusyohani.setMapScaleCd(hidMapScaleCd);
		jusyohani.setAreaSiteiKb(hidAreaSiteiKb);
		jusyohani.setJisJusyoCd(hidJisJusyoCd);
		kibouJyokenBean.setJusyohaniBean(jusyohani);
		kibouJyokenBean.setKiboujyokenDisp(name[0] + name[1]);
		// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース	
//		session.setAttribute(Consts.KIBOUJYOUKENBEAN, kibouJyokenBean);
		kibouJyokenBean.setJyusyoList(null);
		kibouJyokenBean.setJyusyoYuubinBangouList(null);
		kibouJyokenBean.setEkiList(null);
		session.setAttribute(Consts.KIBOUJYOUKENBEAN+presetNo, kibouJyokenBean);
		// 2014/01/07  郭凡 UPD End	

		return "btnSettei";

	}
}
