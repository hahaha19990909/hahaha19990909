/**
 * @システム名: 受付システム
 * @ファイル名: UKG03020Action.java
 * @更新日付：: 2012/05/16
 * 変更履歴: 2013/01/28 SYS_胡涛  案件C37058
 * 変更履歴: 2013/03/08 SYS_温  案件C37074
 * 更新履歴：  2014/06/10	 郭凡(SYS)  (案件番号C38117)セールスポイント編集機能の追加
 * 更新履歴：  2017/08/16	 MJEC)長倉  (案件C41059)中止他決顧客の検索対応
 * 変更履歴:  2019/07/03	 SYS_程旋    C43100_お客様項目の入力簡素化対応
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.UKG03011Bean;
import jp.co.token.uketuke.formbean.UKG03020Bean;
import jp.co.token.uketuke.service.IUKG03020Service;
import jp.co.token.uketuke.service.IUKG99003Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 地図で表示する
 * [説 明] 地図で表示する。
 * @author [作 成]
 * </pre>
 */
public class UKG03020Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230329127909L;

	/** 地図で表示する画面用データ */
	private UKG03020Bean ukg03020Data = null;

	/**
	 * 地図で表示する画面用データ
	 *
	 * @return ukg03020Data
	 */
	public UKG03020Bean getUkg03020Data() {

		return ukg03020Data;
	}

	/**
	 * 地図で表示する画面用データ
	 *
	 * @param ukg03020Data
	 */
	public void setUkg03020Data(UKG03020Bean ukg03020Data) {

		this.ukg03020Data = ukg03020Data;
	}

	/** セッションのログイン情報 */
	private Map<String, Object> sessionLoginInfo = null;

	/**
	 * セッション情報
	 *
	 * @return
	 */
	public Map<String, Object> getSessionLoginInfo() {

		return sessionLoginInfo;
	}

	/**
	 * セッション情報
	 *
	 * @param sessionLoginInfo
	 *            　セッション情報
	 */
	public void setSessionLoginInfo(Map<String, Object> sessionLoginInfo) {

		this.sessionLoginInfo = sessionLoginInfo;
	}

	/** 地図表示サービス */
	private IUKG03020Service UKG03020Services;

	/**
	 * 地図表示サービス
	 *
	 * @param services
	 */
	public void setUKG03020Services(IUKG03020Service uKG03020Services) {

		UKG03020Services = uKG03020Services;
	}

	/** 顧客検索サービス */
	private IUKG99003Service UKG99003Services;

	/**
	 * 顧客検索サービス
	 *
	 * @param services
	 */
    public void setUKG99003Services(IUKG99003Service uKG99003Services) {

    	UKG99003Services = uKG99003Services;
    }

	/**
	 * <pre>
	 * [説 明] 地図で表示する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
    @Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);
		ukg03020Data = new UKG03020Bean();

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		// 物件No.
		ukg03020Data.setBukkenNo(request.getParameter("bukkenNo"));
		// 部屋No.
		ukg03020Data.setHeyaNo(request.getParameter("heyaNo"));
		// 詳細/一覧区分(1:詳細 2:一覧)
		ukg03020Data.setDetailListFlag(request.getParameter("detailListFlag"));

		// セッションID
		ukg03020Data.setSessionId(request.getParameter("sessionId"));
		// お気に入り数
		ukg03020Data.setOkiniIrisuu(request.getParameter("okiniIrisuu"));

		// 外観/間取図区分
		ukg03020Data.setMadoriKb(request.getParameter("madoriKbn"));
		// 地図中心経度
		ukg03020Data.setCenterKeido(request.getParameter("centerKeido"));
		// 地図中心緯度
		ukg03020Data.setCenterIdo(request.getParameter("centerIdo"));
		// 希望設備コード(必須)
		ukg03020Data.setKibouSetubiHissuCd(request.getParameter("kibouSetubiHissuCd"));
		// 希望設備コード(必須ではない)
		ukg03020Data.setKibouSetubiNoHissuCd(request.getParameter("kibouSetubiNoHissuCd"));
		ukg03020Data.setSyuhenBunrui(request.getParameter("syuhenBunrui"));
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		ukg03020Data.setAuthorityCd(authorityCd);
		// ########## 2014/06/10 郭凡 ADD End
		// 顧客様名
		String customName = request.getParameter("customer");
		if (!Util.isEmpty(customName)) {
			if ("ISO-8859-1".equals(Util.getEncoding(customName))) {
				// お客様名
				ukg03020Data.setKokyakuName(new String(customName.getBytes("ISO-8859-1"), "UTF-8"));
			} else {
				ukg03020Data.setKokyakuName(customName);
			}
		} else {
			// お客様名
			ukg03020Data.setKokyakuName("＜お客様名未登録＞");
		}

		if(Util.isEmpty(ukg03020Data.getDetailListFlag())){
			if(Util.isEmpty(ukg03020Data.getBukkenNo()) || Util.isEmpty(ukg03020Data.getHeyaNo())){

				ukg03020Data.setDetailListFlag("2");
			}else{

				ukg03020Data.setDetailListFlag("1");
			}
		}
//		2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
		Map<String, Object> initData = UKG99003Services.getInit(ukg03020Data.getBukkenNo(),ukg03020Data.getHeyaNo());
//		2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
		// 周辺施設の表示
		ukg03020Data.setSyuuhennList((List<Map<String, Object>>) initData.get("rst_syuuhenn_list"));

		// 地図縮尺度
		String initScale = PropertiesReader.getIntance().getProperty(Consts.UKG03020_MAP_INITSCALE);
		String minScale = PropertiesReader.getIntance().getProperty(Consts.UKG03020_MAP_MINSCALE);
		ukg03020Data.setInitscale(initScale);
		ukg03020Data.setMinScale(minScale);

		// 一覧
		ukg03020Data.setDispNoHyoujiFlg("true");
		ukg03020Data.setPageNum(1);
		ukg03020Data.setSortJun(Consts.UKG03020_SORT_JYUN);
		if (Util.isEmpty(ukg03020Data.getMadoriKb())) {
			ukg03020Data.setMadoriKb("1");
		}

		//画面状況データ
		UKG03011Bean ukg03011Bean = (UKG03011Bean) session.getAttribute(Consts.UKG03011BEAN_SESSION_KEY);
		if (ukg03011Bean == null) {
			ukg03011Bean = new UKG03011Bean();
		}
		// 2018/10/16 肖剣生(SYS) UPD START 案件C42036デザイン変更新規作成
		//ukg03011Bean.setPageDataCount(5);
		ukg03011Bean.setPageDataCount(10);
		// 2018/10/16 肖剣生(SYS) UPD END 案件C42036デザイン変更新規作成
		ukg03020Data.setMapServleturl(PropertiesReader.getIntance().getProperty(Consts.MAP_SERVLETURL));
		ukg03020Data.setMapKey(PropertiesReader.getIntance().getProperty(Consts.MAP_KEY));
		ukg03020Data.setMapVersion(PropertiesReader.getIntance().getProperty(Consts.MAP_VERSION));

		// 2013/1/30  SYS_胡涛  ADD START 案件C37058
		// セッションのログイン情報取得する
		Map<String, Object> loginInfo = getLoginInfo();
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		// 初期データを取得する
		Map<String, Object> shisetuInfoData = UKG03020Services.shisetuDisplayOnMap(kaisyaCd, uketukeNo);
		List<Map<String, Object>> shisetuData = (List<Map<String, Object>>) shisetuInfoData.get("rst_shisetsupos_list");
		if(shisetuData != null && shisetuData.size() > 0){
			ukg03020Data.setShisetuDispFlg("true");
		} else {
			ukg03020Data.setShisetuDispFlg("false");
		}
		// 2013/1/30  SYS_胡涛  ADD END

		//2013/03/08 SYS_温 Add Start (案件C37074_チラシ作成機能の追加)
		//徒歩スピード設定
		ukg03020Data.setTohoRootTime(PropertiesReader.getIntance().getProperty(Consts.TOHO_ROOT_TIME));
		//自動車スピード設定
		ukg03020Data.setAutoRootTime(PropertiesReader.getIntance().getProperty(Consts.AUTO_ROOT_TIME));
		//2013/03/08 SYS_温 Add End

		//2017/08/16 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//進捗コードの取得、設定
		Map<String, Object> customerInfoData = UKG03020Services.getCustomerData(kaisyaCd, uketukeNo);
		List<Map<String, Object>> customerData = (List<Map<String, Object>>) customerInfoData.get("rst_record");
		String sinchokuCd = (String) customerData.get(0).get("SINCHOKU_CD");
		ukg03020Data.setSinchokuCd(sinchokuCd);
		//2017/08/16 MJEC)長倉 ADD End
		
		// 2018/10/25 ADD Start SYS_肖剣生 (案件番号C42036)不具合対応No79 Googleマップ対応
		/**航空写真のURL*/
		ukg03020Data.setAerialURL(PropertiesReader.getIntance().getProperty(Consts.aerialURL));
		/**ストリートビューのURL*/
		ukg03020Data.setStreetviewURL(PropertiesReader.getIntance().getProperty(Consts.streetviewURL));
		/**Googleマップ有効・無効*/
		ukg03020Data.setGoogleMaps(PropertiesReader.getIntance().getProperty(Consts.googleMaps));
		/**Googleマップウィンドウ幅*/
		ukg03020Data.setGoogleMapsWindow_w(PropertiesReader.getIntance().getProperty(Consts.googleMapsWindow_w));
		/**Googleマップウィンドウ高*/
		ukg03020Data.setGoogleMapsWindow_h(PropertiesReader.getIntance().getProperty(Consts.googleMapsWindow_h));
		// 2018/10/25 ADD End SYS_肖剣生 (案件番号C42036)不具合対応No79 Googleマップ対応

		return successForward();
	}

	/**
	 * 地図生成処理
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
    public String bukkenSetInit() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg03020Data = new UKG03020Bean();
		// セッションのログイン情報取得する
		Map<String, Object> loginInfo = getLoginInfo();

		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String maxx = request.getParameter("maxx");
		String maxy = request.getParameter("maxy");
		String minx = request.getParameter("minx");
		String miny = request.getParameter("miny");
		String sessionId = request.getParameter("sessionId");
		UKG03011Bean ukg03011Bean = (UKG03011Bean) session.getAttribute(Consts.UKG03011BEAN_SESSION_KEY);
		String dispNoHyoujiFlg = null;
		if(ukg03011Bean != null){
			dispNoHyoujiFlg = ukg03011Bean.getDispNoHyoujiFlg();
		}
		Map<String, Object> bukkenSetInitList = UKG03020Services.getBukkenSitInit(kaisyaCd, uketukeNo, sessionId, dispNoHyoujiFlg, maxx, maxy, minx, miny);
		List<Map<String, Object>> resData = (List<Map<String, Object>>) bukkenSetInitList.get("rst_bukkensit_list");

		ukg03020Data.setBukkenSetInitList(resData);

		return "ajaxProcess";
	}

	/**
	 * 地図アイコンクリック時処理
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
    public String fetchBukenInfo() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg03020Data = new UKG03020Bean();

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));

		String bukkenNo = request.getParameter("bukkenNo");
		String sessionId = request.getParameter("sessionId");
		UKG03011Bean ukg03011Bean = (UKG03011Bean) session.getAttribute(Consts.UKG03011BEAN_SESSION_KEY);
		String dispNoHyoujiFlg = null;
		if(ukg03011Bean != null){
			dispNoHyoujiFlg = ukg03011Bean.getDispNoHyoujiFlg();
		}
		// 初期データを取得する
		Map<String, Object> bukenInfoData = UKG03020Services.getBukenInfo(kaisyaCd, sessionId, uketukeNo, bukkenNo, dispNoHyoujiFlg);
		List<Map<String, Object>> out_bukenInfo = (List<Map<String, Object>>) bukenInfoData.get("out_bukenInfo");

		// 物件情報
		ukg03020Data.setBukenInfoList(out_bukenInfo);

		//設定ファイルからイメージPathを取得する
		ukg03020Data.setImagePath("imgIO.imgIO?type=1&path=");
		ukg03020Data.setImagePathM("imgIO.imgIO?type=3&path=");
		if (out_bukenInfo == null || out_bukenInfo.size() == 0) {
			ukg03020Data.setRstMsg(Util.getServerMsg("ERRS011", "対象の部屋は入居中"));
		}else{
			if("1".equals(dispNoHyoujiFlg)){
				if("1".equals(out_bukenInfo.get(0).get("NO_DISP_FLG"))){
					ukg03020Data.setRstMsg(Util.getServerMsg("ERRS011", "対象の部屋は入居中"));
				}
			}
		}
		return "ajaxProcess";
	}

	/**
	 * 地図アイコンクリック時処理
	 *
	 * @throws Exception
	 */
	public String addShoukaiBukken() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg03020Data = new UKG03020Bean();

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String userCD = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));

		String bukkenNo = request.getParameter("bukkenNo");
		String heyaNo = request.getParameter("heyaNo");

		// 初期データを取得する
		UKG03020Services.addShoukaiBukken(kaisyaCd, uketukeNo, bukkenNo, heyaNo,userCD);

		return "ajaxProcess";
	}
	// 2013/1/28  SYS_胡涛  ADD START 案件C37058
	/**
	 * 地図で希望施設表示処理
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String shisetuDisplayOnMap() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg03020Data = new UKG03020Bean();

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		// 初期データを取得する
		Map<String, Object> shisetuInfoData = UKG03020Services.shisetuDisplayOnMap(kaisyaCd, uketukeNo);
		List<Map<String, Object>> shisetuData = (List<Map<String, Object>>) shisetuInfoData.get("rst_shisetsupos_list");
		if(ukg03020Data.getKibouShisetuInfo() != null){
			ukg03020Data.setKibouShisetuInfo(null);
		}
		 ukg03020Data.setKibouShisetuInfo(shisetuData);

		return "ajaxProcess";
	}

	// 2013/1/28  SYS_胡涛  ADD END
}
