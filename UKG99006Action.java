/**
 * @システム名: 受付システム
 * @ファイル名: UKG99006Action.java
 * @更新日付 : 2012/05/08
 * @Copyright: 2012 token corporation All right reserved
 * 変更履歴: 2013/05/16 SYS_張強 (案件C37109)受付システムSTEP2要望対応
 * 変更履歴: 2013/06/03 SYS_郭凡 (案件C37103-1)チラシ作成機能の追加-STEP2
 *         2014/01/20 宮本 Jtestソース抑制(案件C37084-6)
 * 更新履歴： 2014/06/10	郭凡(SYS)       1.03  (案件C38117)セールスポイント編集機能の追加
 * 更新履歴： 2017/08/10	 MJEC)長倉      1.04  (案件C41059)中止他決顧客の検索対応
 * 更新履歴： 2019/06/27    熊振(SYS)       1.05  (案件C43100)お客様項目の入力簡素化対応
 */
package jp.co.token.uketuke.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.AnketoDispData;
import jp.co.token.uketuke.formbean.AnketoDispDataBig;
import jp.co.token.uketuke.formbean.IconGyouBean;
import jp.co.token.uketuke.formbean.SetubiIconBean;
import jp.co.token.uketuke.service.IUKG02020Service;
import jp.co.token.uketuke.service.IUKG99006Service;
import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 物件情報詳細で表示する
 * [説 明] 物件情報詳細で表示する
 * @author SYS_李鵬飛
 * </pre>
 */
public class UKG99006Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = 4807617362968369317L;

	/** 物件情報詳細画面用データ */
	private Map<String, Object> data = null;

	/** 物件情報詳細サービス */
	private IUKG99006Service UKG99006Services;
	/* 2019/07/11 熊振 C43100_お客様項目の入力簡素化対応 STRAT */
	/*アンケートマスタ*/
	private HashMap<String, Object> ukg99006Data = null;

	private IUKG02020Service UKG02020Services;
	/**
	 * @return the ukg99006Data
	 */
	public HashMap<String, Object> getUkg99006Data() {
		return ukg99006Data;
	}

	/**
	 * @param uKG99006Services
	 *            the uKG99006Services to set
	 */
	public void setUkg99006Data(HashMap<String, Object> ukg99006Data) {
		this.ukg99006Data = ukg99006Data;
	}

	/**
     * サービス設定
     *
     * @param uKG02020Services
     */
    public void setUKG02020Services(IUKG02020Service uKG02020Services) {
        UKG02020Services = uKG02020Services;
    }
	/* 2019/07/11 熊振 C43100_お客様項目の入力簡素化対応 END */

	/**
	 * @param uKG99006Services
	 *            the uKG99006Services to set
	 */
	public void setUKG99006Services(IUKG99006Service uKG99006Services) {

		UKG99006Services = uKG99006Services;
	}

	/**
	 * @return the data
	 */
	public Map<String, Object> getData() {

		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Map<String, Object> data) {

		this.data = data;
	}

	/**
	 * <pre>
	 * [説 明] 物件情報詳細で表示する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override // parasoft-suppress CDD.DUPM "C37084-6抑制"
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true); // parasoft-suppress CDD.DUPC "C37084-6抑制"

		// 初期化のパラメター設定
		HttpServletRequest request = ServletActionContext.getRequest();
		String bukkenNo = String.valueOf(request.getParameter("bukkenNo"));
		String heyaNo = String.valueOf(request.getParameter("heyaNo"));
		if (!Util.isEmpty(heyaNo) && "ISO-8859-1".equals(Util.getEncoding(heyaNo))) {
			heyaNo = new String(heyaNo.getBytes("ISO-8859-1"), "UTF-8");
		}
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);

		/* 2019/07/11 熊振 C43100_お客様項目の入力簡素化対応 STRAT */
		// アンケートデータを取得
		getAnketoData();
		/* 2019/07/11 熊振 C43100_お客様項目の入力簡素化対応 END */

		// ########## 2014/06/10 郭凡 ADD End
		data = new HashMap<String, Object>();
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		data.put("authorityCd", authorityCd);
		// ########## 2014/06/10 郭凡 ADD End
		// 2018/10/16 肖剣生(SYS) ADD START 案件C42036デザイン変更新規作成(不具合No076)
		data.put("setubiCount", PropertiesReader.getIntance().getProperty("setubiCount"));
		// 2018/10/16 肖剣生(SYS) ADD END 案件C42036デザイン変更新規作成(不具合No076)
		data.put("bukkenNo", bukkenNo);
		data.put("heyaNo", heyaNo);
		return successForward();
	}

	/**
	 * 初期化の物件情報の取得
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String initInfoGet() throws Exception {

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
		// ########## 2013/06/03 郭凡 ADD Start (案件番号C37103-1)チラシ作成機能の追加-STEP2
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
//		String post = sessionLoginInfo.get(Consts.POST_CD).toString();
		String post = (String)uketukePostInfo.get(Consts.POST_CD);
		// ########## 2014/06/10 郭凡 UPD End
		// ########## 2013/06/03 郭凡 ADD End

		data.put("kaisyaCd", kaisyaCd);
		data.put("uketukeNo", uketukeNo);
		data.put("tantouCd", sessionLoginInfo.get(Consts.USER_CD).toString());

		// 初期データを取得する
		// ########## 2013/06/03 郭凡 UPD Start (案件番号C37103-1)チラシ作成機能の追加-STEP2
//		Map<String, Object> resultInfo = UKG99006Services.getInitInfo(kaisyaCd,
//				uketukeNo, bukkenNo, heyaNo);
		Map<String, Object> resultInfo = UKG99006Services.getInitInfo(kaisyaCd,
				uketukeNo, bukkenNo, heyaNo, post);
		// ########## 2013/06/03 郭凡 UPD End

		List<Map<String, Object>> initInfo = (List<Map<String, Object>>) resultInfo // parasoft-suppress CDD.DUPC "C37084-6抑制"
				.get("RST_INITINFO");
		data.put("sysDate", Util.getSysDateYMD().replace("/", ""));
		if (initInfo == null || initInfo.size() == 0) {
			data.put("dataFound", "1");
			data.put("rstMsg", Util.getServerMsg("ERRS011", "対象の物件は入居中"));

			return "ajaxProcess";
		}

		// 外観イメッジ設定
		if (initInfo.get(0).get("GAIKANIMGPATH") != null
				&& !Util.isEmpty(initInfo.get(0).get("GAIKANIMGPATH")
						.toString())) {
			initInfo.get(0).put("GAIKANIMGPATH",
			                    Util.setImgPath((String) initInfo.get(0).get("GAIKANIMGPATH"), "1"));
		}

		//2013/05/16  SYS_張強   ADD Start (案件C37109)受付システムSTEP2要望対応
		String noMustSetubiCd = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		// ログイン会社コード
		paramMap.put("param_kaisyacd", kaisyaCd);
		// 該当受付№
		paramMap.put("param_uketukeno", uketukeNo);
		String heyaSetuBicdStr = (String)initInfo.get(0).get("HEYASETUBICD");
		if (Util.isEmpty(heyaSetuBicdStr)) {
			heyaSetuBicdStr = "0";
		}
		long heyaSetuBicd = Long.parseLong(heyaSetuBicdStr);
		// 設備コードから設備№を取得する
		String setubiNo = Util.getSetubiNo(Long.toBinaryString(heyaSetuBicd));
		// 設備コード
		paramMap.put("param_setubino", setubiNo);
		// 希望設備コード必須ではない情報を取得
		Map<String, Object> setubiObj = UKG99006Services.getSetubiData(paramMap);
		if (setubiObj.get("out_setubi_cd") != null) {
			noMustSetubiCd = (String)setubiObj.get("out_setubi_cd");
		} else {
			noMustSetubiCd = "0";
		}
		// 希望設備コード必須ではない
		data.put("noMusetSetubiCd", noMustSetubiCd);
		// 角部屋情報を取得
		String kadobeyaSetubiNo = null;
		Map<String, Object> kadobeyaJouhou = UKG99006Services.getKadobeyaSetubiNo(paramMap);
		if (kadobeyaJouhou.get("PV_KADOBEYA_SETUBI_NO") != null) {
			kadobeyaSetubiNo = (String)kadobeyaJouhou.get("PV_KADOBEYA_SETUBI_NO");
		} else {
			kadobeyaSetubiNo = "30";
		}
		//角部屋設備Noを設定
		data.put("kadobeyaSetubiNo", kadobeyaSetubiNo);
		//2013/05/16  SYS_張強   ADD End

		data.put("initInfo", resultInfo.get("RST_INITINFO"));
		data.put("sonotaHeya", resultInfo.get("RST_SONOTAHEYA"));
		return "ajaxProcess";
	}

	/**
	 * イメッジリスト検索
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String imgListSearch() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		data = new HashMap<String, Object>();
		// リクエストからパラメターを取得する
		String bukkenNo = request.getParameter("bukkenNo");
		String heyaNo = request.getParameter("heyaNo");
		String flag = request.getParameter("flag");

		// bukkenNo = "133331549";
		// heyaNo = "00202";

		Map<String, Object> imgListData = UKG99006Services.imageLazyLoad(
				bukkenNo, heyaNo);
		List<Map<String, Object>> imgList = (List<Map<String, Object>>) imgListData
				.get("rst_imgInfo");
		Util.setImgListPath(imgList);

		data.put("imgInfoList", imgList);

		// セッションに保存する
		if (Util.isEmpty(flag)) {
			session.setAttribute("imgInfoList", imgList); // parasoft-suppress PB.API.ONS "36029JTest対応"
		} else {
			session.setAttribute("imgInfoList_right", imgList); // parasoft-suppress PB.API.ONS "36029JTest対応"
		}

		//2019/08/08  SYS_李強   ADD Start (案件C41024)パノラマ画像対応
		session.setAttribute("bukkenNo", bukkenNo);
		session.setAttribute("heyaNo", heyaNo);
		//2019/08/08  SYS_李強   ADD End (案件C41024)パノラマ画像対応

		return "ajaxProcess";
	}

	/**
	 * 仮押さえ処理
	 *
	 * @return
	 */
	public String kariOsae() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();

		Map<String, Object> paramMap = new HashMap<String, Object>();

		// ソート順設定する
		HttpSession session = request.getSession();
		paramMap.put("PV_KAISYA_CD", sessionLoginInfo.get(Consts.KAISYA_CD)
				.toString());

		paramMap.put("PV_UKETUKE_CD", session.getAttribute(Consts.UKETUKENO)
				.toString());

		// ログイン担当者コード
		paramMap.put("PV_TANTOU_CD", sessionLoginInfo.get(Consts.USER_CD)
				.toString());

		// 画面の物件№
		paramMap.put("PV_BUKENN_NO", request.getParameter("bukkenNo"));

		// 画面のONWEB物件№
		paramMap.put("PV_ONWED_BUKKEN_NO",
				request.getParameter("onWebBukkenNo"));
		// 画面の不動産会社№
		paramMap.put("PV_ESTATE_BUKKEN_NO",
				request.getParameter("fuDoSanKaisyaNo"));

		// 画面のログインの店舗コード

		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
//		paramMap.put("PV_CYUKAYITENBO_CODE",
//				sessionLoginInfo.get(Consts.POST_CD).toString());
		paramMap.put("PV_CYUKAYITENBO_CODE",
				uketukePostInfo.get(Consts.POST_CD).toString());
		// ########## 2014/06/10 郭凡 UPD End

		// 画面の部屋№
		paramMap.put("PV_HEYA_NO", request.getParameter("heyaNo"));
		// 画面の現在の日時
		paramMap.put("PV_NOWDATE", new Date());
		// 画面の物件駐車場区分
		paramMap.put("PV_BUKKEN_PAKING_KB", "1");

		Map<String, Object> kariOsaeObj = UKG99006Services.kariOsae(paramMap);

		// クライアントオブジェクト
		data = new HashMap<String, Object>();

		String rstCd = kariOsaeObj.get("RST_CODE").toString();
		if (Consts.SUCCESS.equals(rstCd)) {
			data.put("rstCd", rstCd);
			data.put("rst_kariosaeJyuni", kariOsaeObj.get("RST_KARIOSAEJYUNYI")
					.toString());
		} else if ("1111111".equals(rstCd) || "1111114".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS002", "仮押え"));
		} else if ("1111112".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS009"));
		} else if ("1111115".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS011", "対象の部屋は入居中"));
		}
		return "ajaxProcess";
	}

	/**
	 * 仮押さえキャンセル処理
	 *
	 * @return
	 */
	public String kariOsaeCancel() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();

		Map<String, Object> paramMap = new HashMap<String, Object>();

		// ソート順設定する
		HttpSession session = request.getSession();
		// 会社コード
		paramMap.put("PV_KAISYA_CD", sessionLoginInfo.get(Consts.KAISYA_CD)
				.toString());
		// 受付№
		paramMap.put("PV_UKETUKE_CD", session.getAttribute(Consts.UKETUKENO)
				.toString());
		// ログイン担当者コード
		paramMap.put("PV_TANTOU_CD", sessionLoginInfo.get(Consts.USER_CD)
				.toString());
		// 画面の物件№
		paramMap.put("PV_BUKENN_NO", request.getParameter("bukkenNo"));
		// 画面の部屋№
		paramMap.put("PV_HEYA_NO", request.getParameter("heyaNo"));
		// 画面の現在の日時
		paramMap.put("PV_NOWDATE", new Date());

		Map<String, Object> kariOsaeObj = UKG99006Services
				.kariOsaeCancel(paramMap);

		// クライアントオブジェクト
		data = new HashMap<String, Object>();

		String rstCd = kariOsaeObj.get("RST_CODE").toString();
		if (Consts.SUCCESS.equals(rstCd)) {
			data.put("rstCd", rstCd);
		} else if ("1111111".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS002", "仮押え情報のキャンセル"));
		}
		return "ajaxProcess";
	}

	/**
	 * 申込ボタン押下処理
	 *
	 * @return
	 */
	public String moshikomi() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		// ########## 2014/06/10 郭凡 ADD End

		Map<String, Object> paramMap = new HashMap<String, Object>();

		// ソート順設定する
		HttpSession session = request.getSession();

		// ログイン会社コード
		paramMap.put("PV_KAISYA_CD", sessionLoginInfo.get(Consts.KAISYA_CD)
				.toString());

		// 該当受付№
		paramMap.put("PV_UKETUKE_CD", session.getAttribute(Consts.UKETUKENO)
				.toString());

		// 画面の顧客コード
		paramMap.put("PV_KOKYAKU_CD", request.getParameter("customCd"));

		// ログイン担当者コード
		paramMap.put("PV_TANTOU_CD", sessionLoginInfo.get(Consts.USER_CD)
				.toString());

		// ログイン店舗コード
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
//		paramMap.put("PV_POST_CD", sessionLoginInfo.get(Consts.POST_CD)
//				.toString());
		paramMap.put("PV_POST_CD", uketukePostInfo.get(Consts.POST_CD));
		// ########## 2014/06/10 郭凡 UPD End

		// 画面の物件№
		paramMap.put("PV_BUKENN_NO", request.getParameter("bukkenNo"));
		// 画面のONWEB物件№
		paramMap.put("PV_ONWED_BUKKEN_NO",
				request.getParameter("onWebBukkenNo"));
		// 画面の不動産会社№
		paramMap.put("PV_ESTATE_BUKKEN_NO",
				request.getParameter("fuDoSanKaisyaNo"));

		// 画面の部屋№
		paramMap.put("PV_HEYA_NO", request.getParameter("heyaNo"));
		// 画面の現在の日時
		paramMap.put("PV_NOWDATE", new Date());
		// 画面の物件駐車場区分
		paramMap.put("PV_BUKKEN_PARKING_KB", "1");
		// 画面の顧客区分
		paramMap.put("PV_KOKYAKU_SB", request.getParameter("kokyakuSb"));

		Map<String, Object> moshikomiObj = UKG99006Services.moshikomi(paramMap);

		// クライアントオブジェクト
		data = new HashMap<String, Object>();

		String rstCd = moshikomiObj.get("RST_CODE").toString();
		data.put("rstCd", rstCd);
		// ########## 2013/06/03 郭凡 ADD Start (案件番号C37103-1)チラシ作成機能の追加-STEP2
		data.put("KOKYAKU_CD", moshikomiObj.get("PV_KOKYAKU_CD"));
		// ########## 2013/06/03 郭凡 ADD End
		if ("1111111".equals(rstCd) || "1111114".equals(rstCd)
				|| "1111116".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS002", "申込み処理"));
		} else if ("1111112".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS009"));
		} else if ("1111113".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS008"));
		} else if ("1111115".equals(rstCd)) {
			data.put("rstMsg", Util.getServerMsg("ERRS011", "対象の部屋は入居中"));
		}
		return "ajaxProcess";
	}

	/**
	 * 空室情報リンク押下処理
	 *
	 * @return
	 */
	public String kushitu() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();

		Map<String, Object> paramMap = new HashMap<String, Object>();

		// ソート順設定する
		HttpSession session = request.getSession();
		// ログイン会社コード
		paramMap.put("param_kaisya_cd", sessionLoginInfo.get(Consts.KAISYA_CD)
				.toString());
		// 該当受付№
		paramMap.put("param_uketuke_no", session.getAttribute(Consts.UKETUKENO)
				.toString());
		// 画面の物件№
		paramMap.put("param_homemate_bukken_no",
				request.getParameter("bukkenNo"));
		// 画面の部屋№
		paramMap.put("param_heya_no", request.getParameter("heyaNo"));
		// 画面の物件駐車場区分
		paramMap.put("param_bukken_parking_kb", "1");
		// ログイン担当者コード
		paramMap.put("param_tantou_cd", sessionLoginInfo.get(Consts.USER_CD)
				.toString());
		// 画面の現在の日時
		paramMap.put("pv_nowdate", new Date());
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		// ########## 2017/08/10 MJEC)長倉 UPD Start (案件C41059)中止他決顧客の検索対応
		//進捗コードを取得
		String sinchokuCd = request.getParameter("sinchokuCd");
		//if("0".equals(authorityCd)){
		//ユーザー登録権限が'0'かつ進捗コードが'6'(他決)か'7'(中止)以外の場合処理を行う。
		if("0".equals(authorityCd) && !(sinchokuCd.equals("6") || sinchokuCd.equals("7"))){
		// ########## 2017/08/10 MJEC)長倉 UPD End
			UKG99006Services.kushitu(paramMap);
		}
		// ########## 2014/06/10 郭凡 UPD End

		return "ajaxProcess";
	}

	/**
	 * 適合度リンク押下処理
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String tekigodoClick() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		Map<String, Object> paramMap = new HashMap<String, Object>();
		String heyaSetubiCdStr = String.valueOf(request
				.getParameter("heyaSetubiCd"));
		String noMustSetubiCdStr = String.valueOf(request
				.getParameter("noMustSetubiCd"));

		if (Util.isEmpty(heyaSetubiCdStr)) {
			heyaSetubiCdStr = "0";
		}
		if (Util.isEmpty(noMustSetubiCdStr)) {
			noMustSetubiCdStr = "0";
		}
		// 引数設備コード
		long heyaSetubiCd = Long.parseLong(heyaSetubiCdStr);
		// 引数の希望設備コード
		long noMustSetubiCd = Long.parseLong(noMustSetubiCdStr);

		// 設備コードとAND演算したの希望設備コード(2進数文字列)
		String kiboSetubi = Util.getSetubiCd(heyaSetubiCd, noMustSetubiCd);
		String noMustSetubiNo = Util.getSetubiNo(Long
				.toBinaryString(noMustSetubiCd));

		// 部屋設備NoList
		paramMap.put("param_setubino", noMustSetubiNo);
		Map<String, Object> setuBiObj = UKG99006Services
				.getSetuBiList(paramMap);

		List<Map<String, Object>> listMap = (List<Map<String, Object>>) setuBiObj
				.get("OUT_SETUBILIST");

		for (Map<String, Object> map : listMap) {

			if (kiboSetubi.charAt(Integer.parseInt(map.get("SETUBINO")
					.toString()) - 1) == '1') {
				map.put("kbnDisp", "○");
				map.put("kbn", "1");
			} else {
				map.put("kbnDisp", "×");
				map.put("kbn", "0");
			}

		}

		// クライアントオブジェクト
		data = new HashMap<String, Object>();
		data.put("SetubiList", listMap);
		return "ajaxProcess";
	}

	/**
	 * 適合度のバーのデータ計算
	 *
	 * @return
	 * @throws Exception
	 */
	public String tekigodoBarSet() throws Exception {

		data = new HashMap<String, Object>();
		// 初期化のパラメター設定
		HttpServletRequest request = ServletActionContext.getRequest();
		String heyaSetubiCd = String.valueOf(request
				.getParameter("heyaSetubiCd"));
		String noMustSetubiCd = String.valueOf(request
				.getParameter("noMustSetubiCd"));

		if (Util.isEmpty(heyaSetubiCd)) {
			heyaSetubiCd = "0";
		}
		if (Util.isEmpty(noMustSetubiCd)) {
			noMustSetubiCd = "0";
		}

		data.put(
				"tekigodo",
				Util.getTekigodo(Long.parseLong(heyaSetubiCd),
						Long.parseLong(noMustSetubiCd)));

		return "ajaxProcess";
	}

	/**
	 * 希望設備コード取得
	 *
	 * @param kaisyaCd
	 *            会社コード
	 * @param uketukeNo
	 *            受付№
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> kiboSetubiCdGet(String kaisyaCd, String uketukeNo)
			throws Exception {

		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 会社コード
		paramMap.put("param_KaisyaCd", kaisyaCd);
		// 受付№
		paramMap.put("param_uketukeNo", uketukeNo);

		Map<String, Object> kiboSetuBi = UKG99006Services
				.getKiboSetubiCd(paramMap);

		return kiboSetuBi;
	}

	/**
	 * 希望設備コード取得
	 *
	 * @param kaisyaCd
	 *            会社コード
	 * @param uketukeNo
	 *            受付№
	 * @return
	 * @throws Exception
	 */
	public String starUpd() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();

		String kaisyaCd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String tantouCd = sessionLoginInfo.get(Consts.USER_CD).toString();
		String uketukeNo = session.getAttribute(Consts.UKETUKENO).toString();

		String bukkenNo = request.getParameter("bukkenNo");
		String heyaNo = request.getParameter("heyaNo");
		String okiniiri = request.getParameter("okiniiri");

		Map<String, Object> paramMap = new HashMap<String, Object>();
		// 会社コード
		paramMap.put("param_kaisya_cd", kaisyaCd);
		// 受付№
		paramMap.put("param_uketuke_no", uketukeNo);
		paramMap.put("param_homemate_bukken_no", bukkenNo);
		paramMap.put("param_heya_no", heyaNo);
		paramMap.put("param_okiniiri", okiniiri);
		paramMap.put("param_tantou_cd", tantouCd);
		paramMap.put("pv_nowdate", new Date());

		UKG99006Services.starUpd(paramMap);

		return "ajaxProcess";
	}

	/**
	 * こだわり条件の検索
	 *
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String kodowariSet() throws Exception {

		// 初期化のパラメター設定
		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		HttpSession session = request.getSession();
		String setubiCd = String.valueOf(request.getParameter("setubiCd"));
		if (Util.isEmpty(setubiCd)) {
			setubiCd = "0";
		}
		// 設備コード
		long heyaSetubiCd = Long.parseLong(setubiCd);
		String mustSetubiCd = String.valueOf(request
				.getParameter("mustSetubiCd"));
		String noMustSetubiCd = String.valueOf(request.getParameter(
				"noMustSetubiCd").toString());

		// ホームメイト物件No
		paramMap.put("param_bukkenno", request.getParameter("bukkenNo"));
		// 号室
		paramMap.put("param_heyano", request.getParameter("heyaNo"));
		// ログイン会社コード
		paramMap.put("param_kaisyacd", sessionLoginInfo.get(Consts.KAISYA_CD)
				.toString());
		// 該当受付№
		//########## 2013/06/03 郭凡 UPD Start (案件番号C37103-1)チラシ作成機能の追加-STEP2
//		paramMap.put("param_uketukeno", session.getAttribute(Consts.UKETUKENO)
//				.toString());
		paramMap.put("param_uketukeno", session.getAttribute(Consts.UKETUKENO));
		//########## 2013/06/03 郭凡 UPD End
		// 設備コードから設備№を取得する
		String setubiNo = Util.getSetubiNo(Long.toBinaryString(heyaSetubiCd));
		// 設備コード
		paramMap.put("param_setubino", setubiNo);

		Map<String, Object> setubiObj = UKG99006Services
				.getSetubiData(paramMap);

		data = new HashMap<String, Object>();
		// 設備リスト
		data.put("setubiList", setubiObj.get("out_setubilist"));

		if (Util.isEmpty(mustSetubiCd)
				&& setubiObj.get("out_setubicdhissu") != null) {
			mustSetubiCd = setubiObj.get("out_setubicdhissu").toString();
		}
		if (Util.isEmpty(noMustSetubiCd)
				&& setubiObj.get("out_setubi_cd") != null) {
			noMustSetubiCd = setubiObj.get("out_setubi_cd").toString();
		}
		// 希望設備コード必須
		data.put("mustSetubiCd", mustSetubiCd);
		// 希望設備コード必須ではない
		data.put("noMusetSetubiCd", noMustSetubiCd);

		if (Util.isEmpty(mustSetubiCd)) {
			mustSetubiCd = "0";
		}
		if (Util.isEmpty(noMustSetubiCd)) {
			noMustSetubiCd = "0";
		}

		// AND演算を行います。
		String hissuNo = Util.getSetubiCd(heyaSetubiCd,
				Long.parseLong(mustSetubiCd));
		String noHissuNo = Util.getSetubiCd(heyaSetubiCd,
				Long.parseLong(noMustSetubiCd));

		List<Map<String, Object>> listMap = (List<Map<String, Object>>) setubiObj
				.get("out_setubilist");

		IconGyouBean iconGyou = null;
		SetubiIconBean setubiBean = null;
		String naiyou = "";

		List<SetubiIconBean> list = new ArrayList<SetubiIconBean>();
		List<IconGyouBean> listIconGyou = null;
		for (int i = 0; i < listMap.size(); i++) {
			listIconGyou = new ArrayList<IconGyouBean>();

			setubiBean = new SetubiIconBean();
			naiyou = String.valueOf(listMap.get(i).get("ICONNAIYOU1"));
			if (!Util.isEmpty(naiyou)) {
				iconGyou = new IconGyouBean();
				iconGyou.setNaiyou(naiyou);
				iconGyou.setFontsize(String.valueOf(listMap.get(i).get(
						"FONTSIZE1")));
				listIconGyou.add(iconGyou);
			}
			naiyou = String.valueOf(listMap.get(i).get("ICONNAIYOU2"));
			if (!Util.isEmpty(naiyou)) {
				iconGyou = new IconGyouBean();
				iconGyou.setNaiyou(naiyou);
				iconGyou.setFontsize(String.valueOf(listMap.get(i).get(
						"FONTSIZE2")));
				listIconGyou.add(iconGyou);
			}
			naiyou = String.valueOf(listMap.get(i).get("ICONNAIYOU3"));
			if (!Util.isEmpty(naiyou)) {
				iconGyou = new IconGyouBean();
				iconGyou.setNaiyou(naiyou);
				iconGyou.setFontsize(String.valueOf(listMap.get(i).get(
						"FONTSIZE3")));
				listIconGyou.add(iconGyou);
			}
			// 必須区分
			setubiBean.setHissuKbn(checkSetubiNo(hissuNo, noHissuNo, (Integer
					.parseInt(listMap.get(i).get("SETUBINO").toString())) - 1));
			// 表示内容
			setubiBean.setIconGyouBeanList(listIconGyou);
			// 表示順
			setubiBean.setSort(Integer.parseInt(String.valueOf(listMap.get(i)
					.get("ICONDISPSORT"))));

			list.add(setubiBean);
		}
		// ソート処理を行います
		SortSetubi sortSetubi = new SortSetubi();
		Collections.sort(list, sortSetubi);

		data.put("setubiList", list);

		return "ajaxProcess";
	}

	/**
	 * 必須か、必須ではないかのチェック
	 *
	 * @param hissNo
	 *            必須の設備№
	 * @param noHissNo
	 *            必須ではない設備№
	 * @param setubiNo
	 *            設備№
	 * @return
	 */
	private int checkSetubiNo(String hissNo, String noHissNo, int i) {

		if (hissNo.charAt(i) == '1') {
			return 1;
		} else if (noHissNo.charAt(i) == '1') {
			return 2;
		} else {
			return 3;
		}
	}

	/**
	 * ソートメソッド
	 */
	private static class SortSetubi implements Comparator<Object> {

		public int compare(Object obj1, Object obj2) {

			SetubiIconBean setubi1 = (SetubiIconBean) obj1;
			SetubiIconBean setubi2 = (SetubiIconBean) obj2;

			if (setubi1.getHissuKbn() == setubi2.getHissuKbn()) {
				if (setubi1.getSort() > setubi2.getSort()) {
					return 1;
				} else if (setubi1.getSort() < setubi2.getSort()) {
					return -1;
				} else {
					return 0;
				}
			} else if (setubi1.getHissuKbn() < setubi2.getHissuKbn()) {
				return -1;
			} else {
				return 1;
			}
		}
	}

	//2013/05/16  SYS_張強   ADD Start (案件C37109)受付システムSTEP2要望対応
	/**
	 * 角部屋かどうかの確認
	 *
	 * @return
	 * @throws Exception
	 */
	public String KadobeyaKakunin() throws Exception {

		// 初期化のパラメター設定
		HttpServletRequest request = ServletActionContext.getRequest();
		String heyaSetsubiCdStr = String.valueOf(request.getParameter("heyaSetubiCd"));
		String kadobeyaSetubiNo = String.valueOf(request.getParameter("kadobeyaSetubiNo"));
		if (Util.isEmpty(heyaSetsubiCdStr)) {
			heyaSetsubiCdStr = "0";
		}
		// 設備コード
		long heyaSetubiCd = Long.parseLong(heyaSetsubiCdStr);

		// 設備コードから設備№を取得する
		String heyaSetubiNo = Util.getSetubiNo(Long.toBinaryString(heyaSetubiCd));
		// 角部屋フラグ
		String kadobeyaFlg = "0";
		// 該当部屋は角部屋の場合、角部屋フラグを"1"にする
		if (heyaSetubiNo.indexOf(kadobeyaSetubiNo) != -1) {
			kadobeyaFlg = "1";
		}

		data = new HashMap<String, Object>();
		// 角部屋フラグを設定
		data.put("kadobeyaFlg", kadobeyaFlg);

		return "ajaxProcess";
	}
	//2013/05/16  SYS_張強   ADD End

	/* 2019/07/11 熊振 C43100_お客様項目の入力簡素化対応 STRAT */
	/**
	 * アンケートデータを取得する
	 */
	@SuppressWarnings("unchecked")
	private void getAnketoData() throws Exception {
	    // 同期フラグ設定
        setAsyncFlg(true);
        ukg99006Data = new HashMap<String, Object>();

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession sessionInfo = request.getSession();
        Map<String, Object> loginInfo = getLoginInfo();
        // 受付No.
        String uketukeNo;
        // 遷移元画面を判定uketukeNo
        if ("UKG01011".equals(request.getParameter("historyPageId"))) {
            // 遷移前画面が受付未登録一覧
            uketukeNo = request.getParameter("uketukeNo");
//            ukg99006Data.put("beforeGamen", "UKG01011");

            // セッションに受付未登録一覧遷移フラグを設定する。
            sessionInfo.setAttribute(Consts.UKETUKENO, uketukeNo);
            sessionInfo.setAttribute(Consts.UKG01011_FLG, "UKG01011");
        } else {
            uketukeNo = String.valueOf(sessionInfo.getAttribute(Consts.UKETUKENO));
//            ukg99006Data.put("beforeGamen", "");
        }

//        if ("UKG01011".equals(sessionInfo.getAttribute(Consts.UKG01011_FLG))) {
//        	ukg99006Data.put("beforeGamen", "UKG01011");
//        }
        // セッション情報取得
        String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));

        // 会社コードと受付Noをセッションに設定する。
        ukg99006Data.put("kaisyaCd", kaisyaCd);
        ukg99006Data.put("uketukeNo", uketukeNo);

        // アンケートデータ取得
        Map<String, Object> anketoData = UKG02020Services.getAnketoData(
                uketukeNo, kaisyaCd);

        // アンケートデータ取得
        List<Map<String, Object>> anketoDbData = (List<Map<String, Object>>) anketoData.get("rst_record");
        // アンケートデータ整形
        List<AnketoDispDataBig> dispAnketo = UKG02020Services
                .getDispAnketo(anketoDbData);
        // アンケートデータ（保存用）作成
        ArrayList<AnketoDispData> beforeAnketo = (ArrayList<AnketoDispData>) UKG02020Services
                .getBeforeAnketo(dispAnketo);
        // アンケートデータ取得
        ukg99006Data.put("anketoData", dispAnketo);

        // ログイン情報
//        Map<String, Object> sessionLoginInfo = getLoginInfo();
//        String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
//        ukg99006Data.put("authorityCd", authorityCd);

        // アンケートデータをセッションに保存
        sessionInfo.setAttribute(Consts.UKG02020_ANKETO_DATA, beforeAnketo);
	}
	/* 2019/07/11 熊振 C43100_お客様項目の入力簡素化対応 END */
}