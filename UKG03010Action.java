/**
 * @システム名: 受付システム
 * @ファイル名: UKG03010Action.java
 * @更新日付 : 2012/05/17
 * 更新履歴: 2013/02/07  SYS_胡涛         (案件C37058)受付システム要望対応
 * 更新履歴: 2013/03/04  SYS_趙雲剛    (案件C37058)社長指示対応
 * 変更履歴: 2013/05/20  SYS_張強 (案件C37109)受付システムSTEP2要望対応
 * 更新履歴: 2013/07/02  SYS_趙雲剛    (案件C37103-1) チラシ作成機能の追加-STEP2
 * 更新履歴：  2014/06/10	 郭凡(SYS)       1.03  (案件C38117)セールスポイント編集機能の追加
 * 更新履歴：  2017/08/17	 MJEC)長倉       1.04  (案件C41059)中止他決顧客の検索対応
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.UKG03011Bean;
import jp.co.token.uketuke.service.IUKG03010Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 物件情報詳細で表示する
 * [説 明] 物件情報詳細で表示する
 * @author SYS_戴氷馨
 * </pre>
 */
public class UKG03010Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = 4807617362964269327L;

	/** 物件情報詳細画面用データ */
	private Map<String, Object> data = null;

	/** 物件情報詳細サービス */
	private IUKG03010Service UKG03010Services;

	/**
	 * @param UKG03010Services
	 *            the UKG03010Services to set
	 */
	public void setUKG03010Services(IUKG03010Service uKG03010Services) {

		UKG03010Services = uKG03010Services;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Map<String, Object> data) {

		this.data = data;
	}

	/**
	 * @return the data
	 */
	public Map<String, Object> getData() {

		return data;
	}

	/**
	 * <pre>
	 * [説 明] 物件情報詳細で表示する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);
		data = new HashMap<String, Object>();
		// 初期化のパラメター設定
		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		HttpSession session = request.getSession();

		// 顧客様名
		String customName = request.getParameter("customer");
		if (!Util.isEmpty(customName)) {
			if ("ISO-8859-1".equals(Util.getEncoding(customName))) {
				data.put("userName", new String(customName.getBytes("ISO-8859-1"), "UTF-8"));
			} else {
				data.put("userName", customName);
			}
		} else {
			data.put("userName", "＜お客様名未登録＞");
		}

		// 登録済みフラグ
		String torokuFlg = request.getParameter("kibouFlg");
		data.put("torokuFlg", torokuFlg);

		// 希望履歴SEQ
		String kibouRirekiSeq = request.getParameter("kibouRirekiSeq");
		data.put("kibouRirekiSeq", kibouRirekiSeq);

		/** お気に入り数 */
		String okiniIrisuu = request.getParameter("okiniIrisuu");
		if (Util.isEmpty(okiniIrisuu)) {
			Map<String, Object> paramMap = new HashMap<String, Object>();
			// ログイン会社コード
			paramMap.put("PV_KAISYA_CD", sessionLoginInfo.get(Consts.KAISYA_CD).toString());
			// 該当受付№
			paramMap.put("PV_UKETUKE_CD", session.getAttribute(Consts.UKETUKENO).toString());
			Map<String, Object> oKiniiriSuuInfo = UKG03010Services.getOkiniIriSuu(paramMap);
			okiniIrisuu = Util.toEmpty(String.valueOf(oKiniiriSuuInfo.get("RST_COUNT")));
		}
		data.put("okiniIrisuu", okiniIrisuu);

		// 外観間取り区分
		String madoriKbn = String.valueOf(request.getParameter("madoriKbn"));
		if (Util.isEmpty(madoriKbn)) {
			madoriKbn = "1";
		}
		data.put("madoriKbn", madoriKbn);

		// 必須設備コード
		String mustSetubi = request.getParameter("kibouSetubiHissuCd");
		// 必須ではない設備コード
		String noMustSetubi = request.getParameter("kibouSetubiNoHissuCd");

		data.put("mustSetubi", mustSetubi);
		data.put("noMustSetubi", noMustSetubi);

		// セッションIDの取得
		String sessionId = String.valueOf(request.getParameter("sessionId"));
		if (Util.isEmpty(sessionId)) {
			data.put("sessionId", session.getId());
		} else {
			data.put("sessionId", sessionId);
		}

		// 外観の場合、外観選定
		if ("1".equals(madoriKbn)) {
			data.put("gaikan", "primary_grey2_active");
			data.put("madori", "primary_grey2");
		} else {
			data.put("gaikan", "primary_grey2");
			data.put("madori", "primary_grey2_active");
		}

		// 引数の物件№判定する
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		String bukkenNo = (String) request.getParameter("bukkenNo");
		String heyaNo = (String) request.getParameter("heyaNo");
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		data.put("authorityCd", authorityCd);
		// ########## 2014/06/10 郭凡 ADD End
		//2017/08/17 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//会社コード
		String kaisyaCd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		//受付No
		String uketukeNo = session.getAttribute(Consts.UKETUKENO).toString();
		//進捗コードの取得、設定
		Map<String, Object> customerInfoData = UKG03010Services.getCustomerData(kaisyaCd, uketukeNo);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> customerData = (List<Map<String, Object>>) customerInfoData.get("rst_record");
		String sinchokuCd = (String) customerData.get(0).get("SINCHOKU_CD");
		data.put("sinchokuCd", sinchokuCd);
		//2017/08/17 MJEC)長倉 ADD End
		//2013/03/04  SYS_趙雲剛   ADD Start (案件C37058)社長指示対応
		String pageFlag = String.valueOf(request.getParameter("pageFlag"));
		if (null == pageFlag || "".equals(pageFlag) || "null".equals(pageFlag)) {
			pageFlag = String.valueOf(session.getAttribute("pageFlag"));
		}else {
			session.setAttribute("pageFlag", pageFlag);
		}
		data.put("pageFlag", pageFlag);
		//2013/03/04  SYS_趙雲剛   ADD End
		data.put("bukkenNo", bukkenNo);
		data.put("heyaNo", heyaNo);
		//画面状況データ
		UKG03011Bean ukg03011Bean = (UKG03011Bean) session.getAttribute(Consts.UKG03011BEAN_SESSION_KEY);
		if (ukg03011Bean == null) {
			ukg03011Bean = new UKG03011Bean();
		}
		// 2018/10/16 肖剣生(SYS) UPD START 案件C42036デザイン変更新規作成
		//ukg03011Bean.setPageDataCount(5);
		ukg03011Bean.setPageDataCount(10);
		// 2018/10/16 肖剣生(SYS) UPD END 案件C42036デザイン変更新規作成
		return successForward();
	}

	/**
	 * 画面の詳細部分の情報検索
	 *
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String detailInfoGet() throws Exception {

		data = new HashMap<String, Object>();

		// 初期化のパラメター設定
		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();

		// ソート順設定する
		HttpSession session = request.getSession();
		String kaisyaCd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String uketukeNo = session.getAttribute(Consts.UKETUKENO).toString();
		String bukkenNo = String.valueOf(request.getParameter("bukkenNo"));
		String heyaNo = String.valueOf(request.getParameter("heyaNo"));
		String sessionId = String.valueOf(request.getParameter("sessionId"));
		data.put("uketukeNo", uketukeNo);
		data.put("tantouCd", sessionLoginInfo.get(Consts.USER_CD).toString());
		// 初期データを取得する
		// 2013/07/02  SYS_趙雲剛   UPD Start (案件C37103-1) チラシ作成機能の追加-STEP2
		//Map<String, Object> resultInfo = UKG03010Services.getInitInfo(kaisyaCd, uketukeNo, bukkenNo, heyaNo, sessionId);
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
//		String post = sessionLoginInfo.get(Consts.POST_CD).toString();
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		String post = uketukePostInfo.get(Consts.POST_CD).toString();
		// ########## 2014/06/10 郭凡 UPD End
		Map<String, Object> resultInfo = UKG03010Services.getInitInfo(kaisyaCd, uketukeNo, bukkenNo, heyaNo, sessionId, post);
		// 2013/07/02  SYS_趙雲剛   UPD End

		List<Map<String, Object>> initInfo = (List<Map<String, Object>>) resultInfo.get("RST_INITINFO");
		data.put("sysDate", Util.getSysDateYMD().replace("/", ""));
		if (initInfo == null || initInfo.size() == 0) {
			data.put("dataFound", "0");
			data.put("rstMsg", Util.getServerMsg("ERRS011", "対象の物件は入居中"));

			return "ajaxProcess";
		}

		// 外観イメッジ設定
		if (initInfo.get(0).get("GAIKANIMGPATH") != null
		    && !Util.isEmpty(initInfo.get(0).get("GAIKANIMGPATH").toString())) {
			initInfo.get(0).put("GAIKANIMGPATH",
			                    Util.setImgPath((String) initInfo.get(0).get("GAIKANIMGPATH"), "1"));
		}

		// 内観間取りの設定
		if (initInfo.get(0).get("NAIKANIMGPATH") != null
		    && !Util.isEmpty(initInfo.get(0).get("NAIKANIMGPATH").toString())) {
			initInfo.get(0).put("NAIKANIMGPATH",
			                    Util.wmfToSVG((String) initInfo.get(0).get("NAIKANIMGPATH"), 332f, 250f));
		}

		//2013/05/20  SYS_張強   ADD Start (案件C37109)受付システムSTEP2要望対応
		String noMustSetubiCd = String.valueOf(request.getParameter("noMustSetubi"));
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// ログイン会社コード
		paramMap.put("param_kaisyacd", kaisyaCd);
		// 該当受付№
		paramMap.put("param_uketukeno", uketukeNo);
		// 希望設備コード必須ではない情報を取得
		if ((null == noMustSetubiCd) || ("".equals(noMustSetubiCd))) {
			Map<String, Object> setubiObj = UKG03010Services.getSetubiData(paramMap);
			if (setubiObj.get("out_setubi_cd") != null) {
				noMustSetubiCd = (String)setubiObj.get("out_setubi_cd");
			} else {
				noMustSetubiCd = "0";
			}
		}
		// 希望設備コード必須ではない
		data.put("noMusetSetubiCd", noMustSetubiCd);
		// 角部屋情報を取得
		String kadobeyaSetubiNo = null;
		Map<String, Object> kadobeyaJouhou = UKG03010Services.getKadobeyaSetubiNo(paramMap);
		if (kadobeyaJouhou.get("PV_KADOBEYA_SETUBI_NO") != null) {
			kadobeyaSetubiNo = (String)kadobeyaJouhou.get("PV_KADOBEYA_SETUBI_NO");
		} else {
			kadobeyaSetubiNo = "30";
		}
		//角部屋設備Noを設定
		data.put("kadobeyaSetubiNo", kadobeyaSetubiNo);
		//2013/05/20  SYS_張強   ADD End

		data.put("initInfo", resultInfo.get("RST_INITINFO"));
		data.put("sonotaHeya", resultInfo.get("RST_SONOTAHEYA"));

		return "ajaxProcess";
	}

	/**
	 * 非表示チェックされた処理
	 *
	 * @return
	 */
	public String noDispUpd() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();

		Map<String, Object> paramMap = new HashMap<String, Object>();

		// ソート順設定する
		HttpSession session = request.getSession();
		// ログイン会社コード
		paramMap.put("param_kaisya_cd", sessionLoginInfo.get(Consts.KAISYA_CD).toString());
		// 該当受付№
		paramMap.put("param_uketuke_no", session.getAttribute(Consts.UKETUKENO).toString());
		// 画面の物件№
		paramMap.put("param_homemate_bukken_no", request.getParameter("bukkenNo"));
		// 画面の部屋№
		paramMap.put("param_heya_no", request.getParameter("heyaNo"));
		// 非表示区分"1"
		paramMap.put("param_no_disp_flg", "1");
		// 駐車場区分"1"
		paramMap.put("param_bukken_parking_kb", "1");
		// ログイン担当者コード
		paramMap.put("param_tantou_cd", sessionLoginInfo.get(Consts.USER_CD).toString());
		// 画面の現在の日時
		paramMap.put("pv_nowdate", new Date());

		Map<String, Object> kushituObj = UKG03010Services.noDispChecked(paramMap);

		// クライアントオブジェクト
		data = new HashMap<String, Object>();

		String rstCd = kushituObj.get("rst_code").toString();
		data.put("rstCd", rstCd);
		if ("1111111".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS002", "更新処理"));
		}
		return "ajaxProcess";
	}

	/**
	 * 商談内容ボタン押下処理
	 *
	 * @return
	 */
	public String talkNaiyou() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();

		HttpSession session = request.getSession();
		session.removeAttribute(Consts.UKG03011BEAN_SESSION_KEY);
		session.removeAttribute(Consts.UKG03012BEAN_SESSION_KEY);

		Map<String, Object> paramMap = new HashMap<String, Object>();

		String sessionId = request.getParameter("sessionId");

		paramMap.put("pv_session_id", sessionId);
		// ログイン会社コード
		paramMap.put("pv_kaisya_cd", sessionLoginInfo.get(Consts.KAISYA_CD).toString());
		// 該当受付№
		paramMap.put("pv_uketuke_cd", session.getAttribute(Consts.UKETUKENO).toString());

		UKG03010Services.talkNaiyou(paramMap);

		return "ajaxProcess";
	}

	//2013/02/18  SYS_胡涛    ADD Start (案件C37058)受付システム要望対応
	/**
	 * 入居中に変更するボタン押下処理
	 *
	 * @return
	 */
	public String updateHeyaNyukyo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();

		Map<String, Object> paramMap = new HashMap<String, Object>();

		// ログイン担当者コード
		paramMap.put("PV_TANTOU_CD", sessionLoginInfo.get(Consts.USER_CD)
				.toString());

		// 画面の物件№
		paramMap.put("PV_BUKENN_NO", request.getParameter("bukkenNo"));

		// 画面の部屋№
		paramMap.put("PV_HEYA_NO", request.getParameter("heyaNo"));
		// 画面の現在の日時
		paramMap.put("PV_NOWDATE", new Date());

		Map<String, Object> nyukyoObj = UKG03010Services.updateHeyaNyukyo(paramMap);

		// クライアントオブジェクト
		data = new HashMap<String, Object>();
		String rstCd = nyukyoObj.get("RST_CODE").toString();
		data.put("rstCd", rstCd);
		if("0000000".equals(rstCd)){
			data.put("rstMsg", Util.getServerMsg("MSGS001", "登録"));
		} else if ("1111111".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS002", "更新処理"));
		} else if ("1111112".equals(rstCd) || "1111115".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS003"));
		} else if ("1111116".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS006", "物件"));
		}
		return "ajaxProcess";
	}
	//2013/02/18  SYS_胡涛    ADD End
}