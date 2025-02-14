/**
 * @システム名: 受付システム
 * @ファイル名: UKG01010Action.java
 * @更新日付：: 2011/12/26
 * @Copyright: 2011 token corporation All right reserved
 * 更新履歴：2013/01/25	林森(SYS)		1.01		(案件番号C37058)受付システム要望対応
 *           2013/02/28	謝超(SYS)		1.02		(案件番号C37074)チラシ作成機能の追加
 *           2013/04/22	温(SYS)			1.03		(案件番号C37107)受付状況データダウンロード機能
 *           2013/05/16 郭凡(SYS)		1.04		(案件C37109-1)受付システムSTEP2要望対応
 *           2014/05/07 趙雲剛(SYS)		1.05  		(案件C37075)オリジナル情報誌リプレース
 *           2014/05/13 胡(SYS)         1.06  		(案件C38100)トップページで既存個人を検索（他）対応
 *           2014/06/23 趙雲剛(SYS)     1.07		(案件C38117)セールスポイント編集機能の追加
 *           2017/08/04 MJEC)長倉		1.08		(案件C38101-1)ホームメイト自動受付対応 (案件C41059)中止他決顧客の検索対応
 *           2017/09/12	張虎強(SYS)	    1.09        (案件C41004)法人仲介担当者登録機能追加対応
 *           2018/02/02 SYS_肖剣生              1.10        (案件番号C42036)デザイン変更
 *           2019/07/03 SYS_程旋        1.2          C43100_お客様項目の入力簡素化対応
 *           2020/04/14 SYS_楊朋朋        1.3          C44072_希望部屋数等を追加
 *           2020/06/24 SYS_劉恆毅        1.4          C43040-2_法人の入力項目追加対応
 *           2021/04/10 許亜静                 1.5         C45032_オンライン仲介システムの導入
 */
package jp.co.token.uketuke.action;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.fw.base.IBaseLoginAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.DispFormat;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.CookieNinsyoRequestBean;
import jp.co.token.uketuke.formbean.CookieNinsyoResponseBean;
import jp.co.token.uketuke.service.ICookiesApiService;
import jp.co.token.uketuke.service.IUKG00010Service;
import jp.co.token.uketuke.service.IUKG01010Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] トップページ。
 * [説 明] トップページアクション。
 * @author [作 成] 2011/10/18 馮強華(SYS)
 * </pre>
 */
//2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
public class UKG01010Action extends BaseAction implements IBaseLoginAction{
//2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 732633708451744703L;

	/** トップページ画面データ対象 */
	private Map<String, Object> ukg01010Data = null;

	/** セッションのログイン情報 */
	private Map<String, Object> sessionLoginInfo = null;

	/** トップページサービス */
	private IUKG01010Service UKG01010Services;
//	2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
	/**
     * Cookie認証情報APIサービス
     */
    private ICookiesApiService cookieApiService;

    /** ログインサービス */
	private IUKG00010Service ukg00010Services;
//	2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
	/** ソート項目 **/
	private String sortItem = null;

	/** 受付方法 **/
	private String rdoUketukehouhou;

	/** 顧客種別 **/
	private String rdoKokyakusb;

	/** 顧客コード **/
	private String hiddkokyakucd;

	// 2017/09/18  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
	/** 企業コード **/
	private String hiddkigyoucd;
	
	/** 企業名 **/
	private String hiddkigyounm;
	// 2017/09/18  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
	
	/** 担当者 **/
	private String selTantou;

	/** エラーメッセージ **/
	private String hidderrMsg;

	/**
	 * エラーメッセージ
	 *
	 * @return
	 */
	public String getHidderrMsg() {

		return hidderrMsg;
	}

	/**
	 * エラーメッセージ
	 *
	 * @param hidderrMsg
	 *            　エラーメッセージ
	 */
	public void setHidderrMsg(String hidderrMsg) {

		this.hidderrMsg = hidderrMsg;
	}

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

	/**
	 * 顧客コード
	 *
	 * @return 顧客コード
	 */
	public String getHiddkokyakucd() {

		return hiddkokyakucd;
	}

	/**
	 * 顧客コード
	 *
	 * @param hiddkokyakucd
	 *            顧客コード
	 */
	public void setHiddkokyakucd(String hiddkokyakucd) {

		this.hiddkokyakucd = hiddkokyakucd;
	}

	// 2017/09/18  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
	/**
	 * 企業コード
	 *
	 * @return 企業コード
	 */
	public String getHiddkigyoucd() {
		return hiddkigyoucd;
	}
	
	/**
	 * 企業コード
	 *
	 * @param hiddkokyakucd
	 *            企業コード
	 */
	public void setHiddkigyoucd(String hiddkigyoucd) {
		this.hiddkigyoucd = hiddkigyoucd;
	}
	
	/**
	 * 企業名
	 *
	 * @return 企業名
	 */
	public String getHiddkigyounm() {
		return hiddkigyounm;
	}
	
	/**
	 * 企業名
	 *
	 * @param hiddkokyakucd
	 *            企業名
	 */
	public void setHiddkigyounm(String hiddkigyounm) {
		this.hiddkigyounm = hiddkigyounm;
	}
	
	// 2017/09/18  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
	/**
	 * 担当者
	 *
	 * @return 担当者
	 */
	public String getSelTantou() {

		return selTantou;
	}

	/**
	 * 担当者
	 *
	 * @param selTantou
	 *            担当者
	 */
	public void setSelTantou(String selTantou) {

		this.selTantou = selTantou;
	}

	/**
	 * 受付方法
	 *
	 * @return 受付方法
	 */
	public String getRdoUketukehouhou() {

		return rdoUketukehouhou;
	}

	/**
	 * 受付方法
	 *
	 * @param rdoUketukehouhou
	 *            受付方法
	 */
	public void setRdoUketukehouhou(String rdoUketukehouhou) {

		this.rdoUketukehouhou = rdoUketukehouhou;
	}

	/**
	 * 顧客種別
	 *
	 * @return 顧客種別
	 */
	public String getRdoKokyakusb() {

		return rdoKokyakusb;
	}

	/**
	 * 顧客種別
	 *
	 * @param rdoKokyakusb
	 *            顧客種別
	 */
	public void setRdoKokyakusb(String rdoKokyakusb) {

		this.rdoKokyakusb = rdoKokyakusb;
	}

	/**
	 * ソート項目
	 *
	 * @return ソート項目
	 */
	public String getSortItem() {

		return sortItem;
	}

	/**
	 * ソート項目
	 *
	 * @return ソート項目
	 */
	public void setSortItem(String sortItem) {

		this.sortItem = sortItem;
	}

	/**
	 * トップページ画面データ対象
	 *
	 * @return トップページ画面データ対象
	 */
	public Map<String, Object> getUkg01010Data() {

		return ukg01010Data;
	}

	/**
	 * トップページ画面データ対象
	 *
	 * @param ukg01010Data
	 *            トップページ画面データ対象
	 */
	public void setUkg01010Data(Map<String, Object> ukg01010Data) {

		this.ukg01010Data = ukg01010Data;
	}

	/**
	 * トップページサービス
	 *
	 * @param services
	 */
	public void setUKG01010Services(IUKG01010Service services) {

		UKG01010Services = services;
	}

	/**
	 * 初回ログインフラグ
	 */
	private String isFromLogin;

	/**
	 * 初回ログインフラグを取得
	 *
	 * @return 初回ログインフラグ
	 */
	public String getIsFromLogin() {

		return isFromLogin;
	}

	/**
	 * 初回ログインフラグを設定
	 *
	 * @param isFromLogin
	 *            初回ログインフラグ
	 */
	public void setIsFromLogin(String isFromLogin) {

		this.isFromLogin = isFromLogin;
	}
//	2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
	/**
     * <pre>
     * Cookie認証APIサービス対象を設定する。
     * @param iBehamaticsApiService
     * </pre>
     */
    public void setCookieApiService(ICookiesApiService cookieApiService) {
          this.cookieApiService = cookieApiService;
    }

    /**
	 * <pre>
	 * [説 明] ログインサービス対象を設定する。
	 * @param ukg00010Services サービス対象
	 * </pre>
	 */
	public void setUkg00010Services(IUKG00010Service ukg00010Services) {

		this.ukg00010Services = ukg00010Services;
	}
//	2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
	/**
	 * <pre>
	 * [説 明] 初期検索を行う。
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

//		2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		if(sessionLoginInfo==null) {
			// ログイン情報確認
	        CookieNinsyoResponseBean responseBean = confirmLoginsessionKey();
	        if(responseBean == null) {
	        	return "login";
	        }else {
	        	String txtUserID = responseBean.getShainCd();
	        	String kaisyaCd = null;
	        	// 会社コードを取得
	    		if (Util.isEmpty(kaisyaCd)) {
	    			kaisyaCd = PropertiesReader.getIntance().getProperty("honbanKaisyaCd");
	    		}
	    		// ログイン者の基本情報リスト取得する
	    		Map<String, Object> userjyouhouList = ukg00010Services.getUserJyouhouList(kaisyaCd, txtUserID);
	    		// ログイン情報をチェック
	    		List<Map<String, Object>> rstlist = (List<Map<String, Object>>) userjyouhouList.get("rst_list");
	    		if (rstlist == null || rstlist.size() == 0) { // 取得データがない場合、メッセージを表示して、ログイン画面へ遷移します
	    			return "login";
	    		}
	    		// 取得データをセッションのログイン情報に格納する
	    		Map<String, Object> loginInfo = rstlist.get(0);
	    		sessionLoginInfo = new HashMap<String, Object>();
	    		sessionLoginInfo.put(Consts.KAISYA_CD, kaisyaCd);
	    		sessionLoginInfo.put(Consts.POST_CD, loginInfo.get("POST_CD"));
	    		sessionLoginInfo.put(Consts.POST_NAME, loginInfo.get("POST_NAME"));
	    		sessionLoginInfo.put(Consts.USER_CD, loginInfo.get("TANTOU_CD"));
	    		sessionLoginInfo.put(Consts.LOGIN_USER_CD, txtUserID);
	    		sessionLoginInfo.put(Consts.USER_NAME, loginInfo.get("TANTOU_NAME"));
	    		//役職区分
	    		sessionLoginInfo.put(Consts.YAKUSYOKU_KB_CD, loginInfo.get("YAKUSYOKU_KB_CD"));
	    		//出力順
	    		sessionLoginInfo.put(Consts.OUTPUT_ORDER, loginInfo.get("OUTPUT_ORDER"));
	    		//支社ブロックコード
	    		sessionLoginInfo.put(Consts.SISYA_BLK_CD, loginInfo.get("SISYA_BLK_CD"));
	    		String authorityCd = "0";//通常ユーザー
	    		if ("7".equals(loginInfo.get("SYOZOKU_KB_CD"))) {
	    			authorityCd = "3";// ブロックユーザー
	    		} else {
	    			if ("2".equals(loginInfo.get("YAKUSYOKU_KB_CD"))
	    					&& "999".equals(loginInfo.get("OUTPUT_ORDER"))
	    					&& !Util.isEmpty((String) loginInfo.get("SISYA_BLK_CD"))) {
	    				authorityCd = "1";// ブロックユーザー
	    			}
	    			if (ukg00010Services.checkPostTantou((String) loginInfo
	    					.get("POST_CD"))) {
	    				authorityCd = "2";// 本社担当者ユーザー
	    			}
	    		}
	    		sessionLoginInfo.put(Consts.USER_AUTHORITY_CD, authorityCd);
	    		String jisJusyoCd = String.valueOf(loginInfo.get("JIS_JUSYO_CD"));
	    		if (!Util.isEmpty(jisJusyoCd)) {
	    			sessionLoginInfo.put(Consts.POST_JUSYO_CD, jisJusyoCd.substring(0, 2));
	    			sessionLoginInfo.put(Consts.POST_SHIKUGUN_CD, jisJusyoCd.substring(2, 5));
	    		} else {
	    			sessionLoginInfo.put(Consts.POST_JUSYO_CD, Consts.DEFAULT_POST_JUSYO_CD.substring(0, 2));
	    			sessionLoginInfo.put(Consts.POST_SHIKUGUN_CD, Consts.DEFAULT_POST_JUSYO_CD.substring(2, 5));
	    		}
	    		session.setAttribute(Consts.LOGIN_INFO, sessionLoginInfo);
	        }
		}
//		2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応

		//########## 2013/05/16 郭凡 DEL Start (案件C37109-1)受付システムSTEP2要望対応
		//session.setAttribute(Consts.UKG01010_SORT_INFO, Consts.UKG01010_SORT_JYUN);
		// ########## 2013/05/16 郭凡 DEL End
		ukg01010Data = new HashMap<String, Object>();

		// 未登録一覧画面へ遷移フラグを取得する
		if ((session.getAttribute(Consts.UKG01011_FLG) != null) && (request.getParameter("home") == null)) {
			String pageBackFlg = String.valueOf(session.getAttribute(Consts.UKG01011_FLG));
			session.setAttribute(Consts.UKG01011_FLG, null);
			ukg01010Data.put("pageBackFlg", pageBackFlg);

		} else if ((session.getAttribute(Consts.UKG01013_FLG) != null) && (request.getParameter("home") == null)) {
			// 追客アラーム情報画面へ遷移フラグを取得する
			String pageBackFlg = String.valueOf(session.getAttribute(Consts.UKG01013_FLG));
			session.setAttribute(Consts.UKG01013_FLG, null);
			ukg01010Data.put("pageBackFlg", pageBackFlg);

		//########## 2017/08/16 MJEC)鈴村 ADD Start (案件C38101-1)ホームメイト自動受付対応
		// ホームメイト自動受付一覧画面へ遷移フラグを取得する
		} else if ((session.getAttribute(Consts.UKG01016_FLG) != null) && (request.getParameter("home") == null)) {
			String pageBackFlg = String.valueOf(session.getAttribute(Consts.UKG01016_FLG));
			session.setAttribute(Consts.UKG01016_FLG, null);
			ukg01010Data.put("pageBackFlg", pageBackFlg);
		//########## 2017/08/16 MJEC)鈴村 ADD End
		}
		// homeボタンから戻ってきた場合は未登録一覧のソート順があればクリアする
		if (request.getParameter("home") != null) {

			session.removeAttribute(Consts.UKG01011_FLG);
			session.removeAttribute(Consts.UKG01013_FLG);
			//########## 2017/08/16 MJEC)鈴村 ADD Start (案件C38101-1)ホームメイト自動受付対応
			session.removeAttribute(Consts.UKG01016_FLG);
			//########## 2017/08/16 MJEC)鈴村 ADD End
			if (session.getAttribute(Consts.UKG01011_SORT_INFO) != null) {
				session.removeAttribute(Consts.UKG01011_SORT_INFO);
			} else if (session.getAttribute(Consts.UKG01013_SORT_INFO) != null) {
				session.removeAttribute(Consts.UKG01013_SORT_INFO);
			//########## 2017/08/16 MJEC)鈴村 ADD Start (案件C38101-1)ホームメイト自動受付対応
			} else if (session.getAttribute(Consts.UKG01016_SORT_INFO) != null) {
				session.removeAttribute(Consts.UKG01016_SORT_INFO);
			//########## 2017/08/16 MJEC)鈴村 ADD End
			}
		}

		Date date = new Date();
		String nowTime = new SimpleDateFormat(Consts.FORMAT_DATE_TIME_NUMBER).format(date);
		String nowMonth = new SimpleDateFormat(Consts.FORMAT_DATE_YYYYMM).format(date);
		String nowYmd = Util.formatDate(date, Consts.FORMAT_DATE_YYMMDD);

		// ########## 2013/04/18 温 ADD Start 案件C37107_受付状況データダウンロード機能
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		String strPrintCsvTougetu = Util.formatDate(cal.getTime(), Consts.FORMAT_DATE_MM);
		String strPrintCsvTougetuYmd = Util.formatDate(cal.getTime(), Consts.FORMAT_DATE_YYMMDD);
		cal.add(Calendar.MONTH,-1);
		String strPrintCsvZengetu = Util.formatDate(cal.getTime(), Consts.FORMAT_DATE_MM);
		String strPrintCsvZengetuYmd = Util.formatDate(cal.getTime(), Consts.FORMAT_DATE_YYMMDD);
		cal.add(Calendar.MONTH,-1);
		String strPrintCsvZenzengetu = Util.formatDate(cal.getTime(), Consts.FORMAT_DATE_MM);
		String strPrintCsvZenzengetuYmd = Util.formatDate(cal.getTime(), Consts.FORMAT_DATE_YYMMDD);
		//データ設定
		ukg01010Data.put("PrintCsvTougetu", setNumberToZenkaku(strPrintCsvTougetu));
		ukg01010Data.put("PrintCsvZengetu", setNumberToZenkaku(strPrintCsvZengetu));
		ukg01010Data.put("PrintCsvZenzengetu", setNumberToZenkaku(strPrintCsvZenzengetu));
		ukg01010Data.put("PrintCsvTougetuYmd", strPrintCsvTougetuYmd);
		ukg01010Data.put("PrintCsvZengetuYmd", strPrintCsvZengetuYmd);
		ukg01010Data.put("PrintCsvZenzengetuYmd", strPrintCsvZenzengetuYmd);
		// ########## 2013/04/18 温 ADD End

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		String postCd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
        String sisyaBlkCd = (String) sessionLoginInfo.get(Consts.SISYA_BLK_CD);
        // ########## 2014/06/23 趙雲剛 ADD End

        // 2017/09/12  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
        String postNm = String.valueOf(sessionLoginInfo.get(Consts.POST_NAME));
        // 2017/09/12  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
        
		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		//担当者
		String tantouCd = null; // parasoft-suppress CDD.DUPC "C37109-1JTest対応"
		if (null == session.getAttribute("tantouCd")) {
			tantouCd = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
			session.setAttribute("tantouCd",tantouCd);
		}else {
			if (!"clear".equals(session.getAttribute("tantouCd"))) {
				tantouCd = String.valueOf(session.getAttribute("tantouCd"));
			}
		}
		//検索欄入力値
		String searchitem = null;
		if (null != session.getAttribute("searchitem")) {
			searchitem = String.valueOf(session.getAttribute("searchitem"));
		}else{
			session.setAttribute("searchitemview","顧客名を入力");
			session.setAttribute("txtflg", false);
		}
		//カナ
		String oyaKana = "全て";
		String koKana = null;
		if (null != session.getAttribute("oyaKana")) {
			oyaKana = String.valueOf(session.getAttribute("oyaKana"));
		}
		if (null != session.getAttribute("koKana")) {
			koKana = String.valueOf(session.getAttribute("koKana"));
		}
		//ページ
		int startRowNum = 1;
		int endRowNum = Consts.INT_21;
		if (null != session.getAttribute("startRowNum")) {
			startRowNum = Integer.valueOf(session.getAttribute("startRowNum").toString());
		}
		if (null != session.getAttribute("endRowNum")) {
			endRowNum = Integer.valueOf(session.getAttribute("endRowNum").toString());
		}
		// ########## 2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//中止・他決チェックボックスフラグ
		String taketuCyushiFlg = String.valueOf(Consts.ZERO);
		if (null != session.getAttribute("taketuCyushiFlg")) {
			taketuCyushiFlg = String.valueOf(session.getAttribute("taketuCyushiFlg").toString());
		}
		// ########## 2017/08/04 MJEC)長倉 ADD End
		//ソート
		String sortkomoku = Consts.UKG01010_SORT_JYUN;
		//########## 2013/05/16 郭凡 UPD Start (案件C37109-1)受付システムSTEP2要望対応 // parasoft-suppress UC.ACC "C37109-1JTest対応"
		//if (null != session.getAttribute("sortkomoku")) { // parasoft-suppress UC.ACC "C37109-1JTest対応"
		//	sortkomoku = String.valueOf(session.getAttribute("sortkomoku"));
		//}
		if (null != session.getAttribute(Consts.UKG01010_SORT_INFO)) {
			sortkomoku = String.valueOf(session.getAttribute(Consts.UKG01010_SORT_INFO));
		}
		session.setAttribute(Consts.UKG01010_SORT_INFO, sortkomoku);
		// ########## 2013/05/16 郭凡 UPD End
		//ページＮｏ．
		if (null == session.getAttribute("pageNum")) {
			session.setAttribute("pageNum",1);
		}
		// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		String searchPostCd = (String) session.getAttribute("showspindex");
		// 2017/09/12  張虎強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
		/*if("0".equals(authorityCd)){*/
		String postName = null;
		if("0".equals(authorityCd) || "3".equals(authorityCd)){
		// 2017/09/12  張虎強 UPD END (案件C41004)法人仲介担当者登録機能追加対応
			// ########## 2014/06/23 趙雲剛 ADD End
			searchPostCd = postCd;
			// 2017/09/12  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
			postName = postNm;
			// 2017/09/12  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
			if (null == session.getAttribute("showspindex")) {
				session.setAttribute("showspindex",String.valueOf(sessionLoginInfo.get(Consts.USER_CD)));
			}
			// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		}else{
			// 店舗リスト取得する
			Map<String, Object> rstList = UKG01010Services.getPostList(
					String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD)),
					authorityCd,
	                sisyaBlkCd);
			List<Map<String, Object>> postlist = (List<Map<String, Object>>) rstList.get("rstpostlist");
			// 2017/09/12  張虎強 DEL START (案件C41004)法人仲介担当者登録機能追加対応
			/*String postName = null;*/
			// 2017/09/12  張虎強 DEL END (案件C41004)法人仲介担当者登録機能追加対応
			if (null == searchPostCd) {
				searchPostCd = (String) postlist.get(0).get("POST_CD");
				session.setAttribute("showspindex",searchPostCd);
			}else{
				session.setAttribute("showspindex", searchPostCd);
				if("xxx".equals(searchPostCd) || searchPostCd.length() < 3 ){
					searchPostCd = "";
				}
			}
			if(Util.isEmpty(searchPostCd)){
				if("1".equals(authorityCd)){
					postName = "ブロック内の全顧客";
				}else{
					postName = "全国の全顧客";
				}
			}else{
				for(Map<String, Object> post : postlist){
					if(searchPostCd.equals(post.get("POST_CD"))){
						postName = (String) post.get("POST_NAME");
						break;
					}
				}
			}
			ukg01010Data.put("postName", postName);
			ukg01010Data.put("postlist", postlist);
			tantouCd = "";
		}
		// ########## 2014/06/23 趙雲剛 ADD End
		Map<String, Object> initInfo = UKG01010Services.getInitInfo(
				String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD)),
                // ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
				//String.valueOf(sessionLoginInfo.get(Consts.POST_CD)),
				postCd,
				searchPostCd,
				authorityCd,
				sisyaBlkCd,
                // ########## 2014/06/23 趙雲剛 UPD End
				// ########## 2017/08/04 MJEC)長倉 UPD Start (案件C41059)中止他決顧客の検索対応
				//tantouCd,nowYmd, nowMonth,searchitem,oyaKana,koKana,
                //startRowNum, endRowNum,sortkomoku);
                tantouCd,nowYmd, nowMonth,searchitem,oyaKana,koKana,
                startRowNum, endRowNum,taketuCyushiFlg,sortkomoku);
				// ########## 2017/08/04 MJEC)長倉 UPD End

		// ########## 2013/01/25 林森 ADD End

		// ########## 2013/01/25 林森 DEL Start (案件C37058)受付システム要望対応
//		Map<String, Object> initInfo = UKG01010Services.getInitInfo(String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD)),
//		                                                            String.valueOf(sessionLoginInfo.get(Consts.POST_CD)),
//		                                                            tantouCd,
//		                                                            nowYmd, nowMonth, 1, Consts.INT_21,
//		                                                            Consts.UKG01010_SORT_JYUN);
		// ########## 2013/01/25 林森 DEL End
        
        // 2020/04/14 楊朋朋(SYS) UPD Start  C44072_希望部屋数等を追加
        // 受付方法
        //ukg01010Data.put("uketukehoho", (List<Map<String, Object>>) initInfo.get("rstuketukehoho"));
		// ログイン情報.ユーザー登録権限が3以外の場合「法人仲介依頼」が非表示
        List<Map<String, Object>> tempList = (List<Map<String, Object>>) initInfo.get("rstuketukehoho");
        if(!"3".equals(authorityCd)){
            for(int i=0; i<tempList.size();i++){
                Map<String, Object> tempMap = tempList.get(i);
                if("4".equals(tempMap.get("KOUMOKU_SB_CD"))){
                    tempList.remove(i);
                }
            }
        }
        ukg01010Data.put("uketukehoho", tempList);
        // 2020/04/14 楊朋朋(SYS) UPD End C44072_希望部屋数等を追加
		// 担当者リスト取得する
		ukg01010Data.put("tantouList", (List<Map<String, Object>>) initInfo.get("rsttantoList"));
		
		// 2017/09/12  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
		if ("3".equals(authorityCd)) {
			// 法人仲介担当者リスト
			ukg01010Data.put("houjintantouList",(List<Map<String, Object>>) initInfo.get("rsthoujintantoList"));
			
		}
		// 2017/09/12  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応

		// 予約日付を取得する
		//2019/04/29 張強軍(SYS) UDP START QA_C42036_037
		//ukg01010Data.put("yoyakudate", Util.getPrevNextDate(date, 0, Consts.FORMAT_YOYAKUDATE));
		ukg01010Data.put("yoyakudate", foramtDate(Util.getPrevNextDate(date, 0, Consts.FORMAT_YOYAKUDATE)));
		//2019/04/29 張強軍(SYS) UDP END QA_C42036_037
		ukg01010Data.put("hiddate", Util.getPrevNextDate(date, 0, Consts.FORMAT_DATE_YMDHMSZ));
		// 現在Appサーバ時間を設定する
		ukg01010Data.put("nowtime", nowTime);
		// 現在Appサーバ時間を設定する
		ukg01010Data.put("nowYmd", nowYmd);

		// 商談中の顧客一覧データ取得
		List<Map<String, Object>> rstKokyakuinfo = (List<Map<String, Object>>) initInfo.get("rstKokyakuinfo");
		// 2017/09/12  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
		if ("3".equals(authorityCd)) {
			// 受付情報作成区分が'4' 商談中の顧客一覧データ取得
			List<Map<String, Object>> rstSakuseiinfo = (List<Map<String, Object>>) initInfo.get("rstsakuseiinfo");
			for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
				if(rstSakuseiinfo.size() ==0){
					rstKokyakuinfo.get(i).put("SHOW_FLAG", "0");
				} else {
					for(Map<String, Object> Sakuseiinfo : rstSakuseiinfo){
						if(rstKokyakuinfo.get(i).get("UKETUKE_NO").equals(Sakuseiinfo.get("HOUJIN_IRAIMOTO_UKETUKE_NO"))){
							rstKokyakuinfo.get(i).put("Sinchoku_Name", Sakuseiinfo.get("Sinchoku_Name"));
							rstKokyakuinfo.get(i).put("Sinchoku_Cd", Sakuseiinfo.get("Sinchoku_Cd"));
							rstKokyakuinfo.get(i).put("MADORI", Sakuseiinfo.get("MADORI"));
							rstKokyakuinfo.get(i).put("SITEI_AREA", Sakuseiinfo.get("SITEI_AREA"));
							rstKokyakuinfo.get(i).put("SEQ", Sakuseiinfo.get("SEQ"));
							rstKokyakuinfo.get(i).put("KIBOU_BUKKEN_KB", Sakuseiinfo.get("KIBOU_BUKKEN_KB"));
							rstKokyakuinfo.get(i).put("KIBOU_JYOUKEN_SITEI_CD", Sakuseiinfo.get("KIBOU_JYOUKEN_SITEI_CD"));
							rstKokyakuinfo.get(i).put("KIBOU_YATIN_FROM", Sakuseiinfo.get("KIBOU_YATIN_FROM"));
							rstKokyakuinfo.get(i).put("KIBOU_YATIN_TO", Sakuseiinfo.get("KIBOU_YATIN_TO"));
						}
						if("".equals(rstKokyakuinfo.get(i).get("MADORI")) || rstKokyakuinfo.get(i).get("MADORI") == null){
							if("".equals(rstKokyakuinfo.get(i).get("SITEI_AREA")) || rstKokyakuinfo.get(i).get("SITEI_AREA") == null){
								if("".equals(rstKokyakuinfo.get(i).get("KIBOU_YATIN_FROM")) || rstKokyakuinfo.get(i).get("KIBOU_YATIN_FROM") == null){
									if("".equals(rstKokyakuinfo.get(i).get("KIBOU_YATIN_TO")) || rstKokyakuinfo.get(i).get("KIBOU_YATIN_TO") == null){
									rstKokyakuinfo.get(i).put("SHOW_FLAG", "0");
									} else {
										rstKokyakuinfo.get(i).put("SHOW_FLAG", "1");
									}
								}
							}
						}
					}
				}
			}
		} else {
			for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
				rstKokyakuinfo.get(i).put("SHOW_FLAG", "1");
			}
		}
		/* 2018/11/13 王一氷 ADD START 案件C43040-1_法人の入力項目追加対応 */
        for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
            String kiguoName = "";
            if(!"".equals(rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME")) && rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME") != null){
                kiguoName = rstKokyakuinfo.get(i).get("KIGYO_NAME")+"("+ rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME") +")";
            }else{
                kiguoName = (String) rstKokyakuinfo.get(i).get("KIGYO_NAME");
            }
            if(!"".equals(kiguoName) && kiguoName != null){
                /* 2019/04/15 張強軍 UDP START 案件C43040-1_法人の入力項目追加対応 */
                /*if(kiguoName.length() > 15){
                    rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 15));
                    rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(15, kiguoName.length()));
                }*/
                /*2019/08/08 UDP START 張強軍(SYS) 不具合対応(C43040-006)*/
                 /*if(kiguoName.length() > 10){
                    rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 10));
                    rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(10, kiguoName.length()));
                 }*/
                 if(kiguoName.length() > 14){
                     rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 14));
                     rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(14, kiguoName.length()));
                  }
                 /* 2018/11/13 張強軍 UDP END 案件C43040-1_法人の入力項目追加対応 */
                 /*2019/08/08 UDP END 張強軍(SYS) 不具合対応(C43040-006)*/
                 else{
                    rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName);
                    rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME","");
                }
            }
        }
        /* 2018/11/13 王一氷 ADD END 案件C43040-1_法人の入力項目追加対応 */
		// 2017/09/12  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
		//受付№を変換する
		dispUketukeNO(rstKokyakuinfo);
		String kanaindex1 = "";
		ukg01010Data.put("KANA_IDX_1", kanaindex1);
		ukg01010Data.put("kokyakuInfo", rstKokyakuinfo);
		ukg01010Data.put("kokyakuInfoCount", String.valueOf(initInfo.get("rstrecordcount")));
		ukg01010Data.put("sysdate", Util.getSysDateYMD());
		// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		ukg01010Data.put("userAuthorityCd", sessionLoginInfo.get(Consts.USER_AUTHORITY_CD));
		ukg01010Data.put("postCd", sessionLoginInfo.get(Consts.POST_CD));
        // ########## 2014/06/23 趙雲剛 ADD End
		if (Util.isEmpty(isFromLogin)) {
			isFromLogin = "0";
		}
		ukg01010Data.put("isFromLogin", isFromLogin);
		// ########## 2014/05/13 胡 ADD Start (案件C38100)トップページで既存個人を検索（他）対応
		ukg01010Data.put("nenngouList", (List<Map<String, Object>>) initInfo.get("rst_nengou_list"));
		// ########## 2014/05/13 胡 ADD End
		// ########## 2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//中止・他決チェックボックスフラグをセット
		ukg01010Data.put("taketuCyushiFlg", taketuCyushiFlg);
		// ########## 2017/08/04 MJEC)長倉 ADD End
		session.removeAttribute(Consts.UKG03011BEAN_SESSION_KEY);
		session.removeAttribute(Consts.UKG03030_LEFT_BEAN_SESSION_KEY);
		session.removeAttribute(Consts.UKG03030_RIGHT_BEAN_SESSION_KEY);
		session.removeAttribute(Consts.UKG03012BEAN_SESSION_KEY);
		session.removeAttribute(Consts.KIBOUJYOUKENBEAN);
		session.removeAttribute("annai_btn_firstInitFlg");
		session.removeAttribute("kigenngkirei_firstInitFlg");
		session.removeAttribute("karikaijyo_firstInitFlg");
		// ########## 2013/02/28 謝超 ADD Start (案件番号C37074)チラシ作成機能の追加
		session.removeAttribute(Consts.UKG99004_SESSION);
		session.removeAttribute(Consts.UKG04011_PAGE_NUM);
		session.removeAttribute(Consts.UKG04011_SORT_INFO);
		// ########## 2013/02/28 謝超 ADD End
        // 2019/03/18 ADD START 汪会(SYS) C42036-142
        // 新規受付No･発行ボタン表示フラグ
        ukg01010Data.put("shinkiUketukeDis", session.getAttribute("shinkiUketukeDis"));
        // 2019/03/18 ADD END 汪会(SYS) C42036-142
		return successForward();
	}
//	2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
	/**
     * <pre>
     * [機 能] ログイン情報確認
     * [説 明] ログインセッションキー受渡し
     * [備 考]
     * @return boolean 処理結果
     */
    private CookieNinsyoResponseBean confirmLoginsessionKey() {
        String loginSessionKey = Util.getCookie(getRequest().getCookies(),Consts.COOKIE_LOGIN_SESSION_KEY);
        // クッキーからログインセッションキーを取得できない場合
        if (Util.isEmpty(loginSessionKey)) {
            return null;
        }
        try {
            // Cookie認証システム(ログイン情報確認)を呼び出し
            CookieNinsyoResponseBean cookieNinsyoResponse = getCookieNinsyoResult(Consts.REQUEST_C002, loginSessionKey);
            // 認証結果成功の場合
            if (cookieNinsyoResponse != null && Consts.RETURNCD_0.equals(cookieNinsyoResponse.getReturnCd())) {
                return cookieNinsyoResponse;
            }
        } catch (Exception e) {
            log.error("ログイン情報確認でエラー", e);
        }
        return null;
    }
    /**
     * <pre>
     * [機 能] Cookie認証システムを呼び出し
     * [説 明]
     * [備 考]
     * @param requestCode 要求処理コード
     * @param sessionCookieKey ログインセッションキー
     * @return BehamaticsApiResponse 処理結果
     */
    private CookieNinsyoResponseBean getCookieNinsyoResult(String requestCode,String loginSessionKey) {
    	CookieNinsyoResponseBean responseBean = null;
        try {
            HttpServletRequest request = ServletActionContext.getRequest();
            // カレントURL取得
            String currentUrl = new String(request.getRequestURL());
            // 要求データ設定
            CookieNinsyoRequestBean requestBean = Util.getRequestBean(requestCode, null, loginSessionKey);
            // Cookie認証システムの処理をCallする
            responseBean = cookieApiService.callCookieNisnsyo(currentUrl, requestBean, CookieNinsyoResponseBean.class);
        } catch (Exception e) {
            log.error("Cookie認証システム呼出でエラー", e);
        }

        return responseBean;
    }
//	2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
	/**
	 * 発行ボタン押下処理
	 *
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             一般例外
	 */
	public String uketukehakkou() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		String usercd = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String postcd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));

		String tempkokyakucd = request.getParameter("kokakucd");
		String chukaitantoucd = request.getParameter("tantoucd");
		String uketukehoho = request.getParameter("uketukehoho");
		String kokyakusb = request.getParameter("kokakusb");
		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		String pageNum = request.getParameter("pageNum");
		// ########## 2013/01/25 林森 ADD End
		String btnFlg = request.getParameter("btnFlg");
		// 受付番号取得する
		Map<String, Object> uketukeNoInfo = UKG01010Services.getUketukeNo(kaisyacd, usercd, postcd, tempkokyakucd,
		                                                                  chukaitantoucd, uketukehoho, kokyakusb);

		String errMsg = "";
		// 更新処理失敗の場合、またテーブルロックは失敗の場合
		if ("ERR0001".equals(uketukeNoInfo.get("rst_code")) || "ERRS999".equals(uketukeNoInfo.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS002", "更新処理");
			// シリアル番号が最大値(999999）の場合
		} else if ("ERR0002".equals(uketukeNoInfo.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS005");
			log.error("受付№が最大値に達しています、新規受付ができません。");
		}

		ukg01010Data = new HashMap<String, Object>();
		// エラーメッセージ
		ukg01010Data.put("errMsg", errMsg);

		HttpSession session = request.getSession();
		// 受付番号をセッションに格納する
		session.setAttribute(Consts.UKETUKENO, String.valueOf(uketukeNoInfo.get("rstno")));
		// 仲介担当者コード
		session.setAttribute(Consts.CHUKAITANTOU, chukaitantoucd);
		// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		//受付店舗情報がセッションに保存する
		savePostInfoToSession();
		// ########## 2014/06/23 趙雲剛 ADD End
				
		ukg01010Data.put("uketukeNo", uketukeNoInfo.get("rstno"));
		ukg01010Data.put("kaisyaCd", kaisyacd);
		ukg01010Data.put("btnFlg", btnFlg);
		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		session.setAttribute("pageNum", pageNum);
		// ########## 2013/01/25 林森 ADD End
		// 該当画面のトークンを設定(二次サブミットチェックため)
		ukg01010Data.put("requestToken", session.getAttribute(Consts.REQUEST_TOKEN));
		return "uketukehakkou";
	}

	/**
	 * 既存法人一覧データ取得処理
	 *
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception
	 *             一般例外
	 */
	@SuppressWarnings("unchecked")
	public String kisonHojinInfo() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg01010Data = new HashMap<String, Object>();
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String postcd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));

		// 既存法人一覧データ取得
		Map<String, Object> hojinInfo = UKG01010Services.getKisonHojinInfo(kaisyacd, postcd, null, null);

		String kanaindex1 = "";

		ukg01010Data.put("KISON_KANA_IDX_1", kanaindex1);

		ukg01010Data.put("hojinInfo", (List<Map<String, Object>>) hojinInfo.get("rstHojinInfo"));
		return "kisonHojinInfo";
	}

	/**
	 * 予約データ取得
	 *
	 * @throws Exception
	 *             一般例外
	 */
	@SuppressWarnings("unchecked")
	public String yoyakuInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String dateflg = request.getParameter("dateflg");
		String date = request.getParameter("date");

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		//String postcd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		String postcd = request.getParameter("postCd");
		// ########## 2014/06/23 趙雲剛 UPD End
				
		ukg01010Data = new HashMap<String, Object>();

		if (!Util.isEmpty(dateflg)) {
			String yoyakudate = "";
			String hiddYoyakudate = "";

			int intdate = 0;

			if ("next".equals(dateflg)) {
				intdate += 1;
			} else {
				intdate -= 1;
			}

			// 予約日付を取得する
			//2019/04/29 張強軍(SYS) UDP START QA_C42036_037
			/*yoyakudate = Util.getPrevNextDate(date, intdate, Consts.FORMAT_YOYAKUDATE);*/
			yoyakudate = foramtDate(Util.getPrevNextDate(date, intdate, Consts.FORMAT_YOYAKUDATE));
			//2019/04/29 張強軍(SYS) UDP END QA_C42036_037
			hiddYoyakudate = Util.getPrevNextDate(date, intdate, Consts.FORMAT_DATE_YMDHMS);

			ukg01010Data.put("yoyakudate", yoyakudate);
			ukg01010Data.put("hiddate", hiddYoyakudate);

			date = hiddYoyakudate.substring(0, 10).replace("-", "");
		}
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		// Map<String, Object> yoyakuInfo = UKG01010Services.getYoyakuInfo(kaisyacd, postcd, date);
		Map<String, Object> yoyakuInfo = UKG01010Services.getYoyakuInfo(
				kaisyacd, postcd,
				(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
				(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD), date);
		// ########## 2014/06/23 趙雲剛 UPD End
		Map<String, Object> yoyakuMap = new HashMap<String, Object>();
		yoyakuMap.put("raitenList", (List<Map<String, Object>>) yoyakuInfo.get("rstraitenList"));
		yoyakuMap.put("bukenList", (List<Map<String, Object>>) yoyakuInfo.get("rstbukenList"));
		//2021/04/10 ADD START 許亜静 C45032_オンライン仲介システムの導入
		yoyakuMap.put("onlineList", (List<Map<String, Object>>) yoyakuInfo.get("rstonlineList"));
		//2021/04/10 ADD END 許亜静 C45032_オンライン仲介システムの導入
		ukg01010Data.put("yoyakuInfo", yoyakuMap);

		return "yoyakuInfo";
	}

	/**
	 * <pre>
	 * [説 明] ページ切り替える。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String pageIndexChg() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String condition = request.getParameter("condition");
		Map<String, String> paramMap = Util.getParamMap(condition);
		String oyaKana = paramMap.get("kana1");
		String koKana = paramMap.get("kana2");
		String searchItem = paramMap.get("searchItem");

		ukg01010Data = new HashMap<String, Object>();
		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		String wheretantou = paramMap.get("wheretantou");
		HttpSession session = request.getSession();
		if ("undefined".equals(wheretantou) || "clear".equals(wheretantou)) {
			wheretantou = null;
		}
		// ########## 2013/03/14 SYS_林森 ADD Start (案件C37058)受付システム要望対応
		// ########## 2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//他決中止チェックボックスフラグを取得
		String taketuCyushiFlg = String.valueOf(Consts.ZERO);
		if (null != session.getAttribute("taketuCyushiFlg")) {
			taketuCyushiFlg = String.valueOf(session.getAttribute("taketuCyushiFlg").toString());
		}
		// ########## 2017/08/04 MJEC)長倉 ADD End
		// セッションの中に保存している小カナがNULLではない場合、検索条件とあわせて検索する。
		if (null != session.getAttribute("koKana")) {
			String session_kokana = String.valueOf(session.getAttribute("koKana"));
			if(!"戻る".equals(session_kokana)){
				koKana = String.valueOf(session.getAttribute("koKana"));
			}
		}
		// ########## 2013/03/14 SYS_林森 ADD End
		// ########## 2013/01/25 林森 ADD End

		// ########## 2013/01/25 林森 UPD Start (案件C37058)受付システム要望対応 // parasoft-suppress UC.ACC "c37058JTest対応"
		//if ("undefined".equals(searchItem)) { // parasoft-suppress UC.ACC "c37058JTest対応"
		if ("undefined".equals(searchItem) || "clear".equals(searchItem)) {
		// ########## 2013/01/25 林森 UPD End
			searchItem = null;
		}
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		//String postcd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		String postcd = paramMap.get("postCd");
		// ########## 2014/06/23 趙雲剛 UPD End
		// ページ切り替える機能用パラメータを取得する
		int startRowNum = (Integer.parseInt(paramMap.get("pageNum")) - 1) * Consts.PAGEIDX + 1;
		int endRowNum = startRowNum + Consts.PAGEIDX;

		// ソート順取得する
		// ########## 2013/01/25 林森 DEL Start (案件C37058)受付システム要望対応
		//HttpSession session = request.getSession();
		// ########## 2013/01/25 林森 DEL End
		String sortkomoku = String.valueOf(session.getAttribute(Consts.UKG01010_SORT_INFO));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		// 業務処理（取得した条件で該当ページのデータを取得する）
		//Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(kaisyacd, postcd, searchItem,wheretantou, oyaKana,
		 //                                                                 koKana, startRowNum, endRowNum, sortkomoku);
		// ########## 2017/08/04 MJEC)長倉 UPD Start (案件C41059)中止他決顧客の検索対応
		//Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(
		//		kaisyacd, postcd,
		//		(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
		//		(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD), searchItem,
		//		wheretantou, oyaKana, koKana, startRowNum, endRowNum,
		//		sortkomoku);
		Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(
				kaisyacd, postcd,
				(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
				(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD), searchItem,
				wheretantou, oyaKana, koKana, startRowNum, endRowNum,
				taketuCyushiFlg, sortkomoku);
		// ########## 2017/08/04 MJEC)長倉 UPD End
        // ########## 2014/06/23 趙雲剛 UPD End
		// 2017/09/12  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
		// 商談中の顧客一覧データ取得
				List<Map<String, Object>> rstKokyakuinfo = (List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo");
				if ("3".equals((String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD))) {
					// 受付情報作成区分が'4' 商談中の顧客一覧データ取得
					List<Map<String, Object>> rstSakuseiinfo = (List<Map<String, Object>>) kokyakuInfo.get("rstsakuseiinfo");
					if(rstSakuseiinfo.size() ==0){
						for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
							rstKokyakuinfo.get(i).put("SHOW_FLAG", "0");
						}
					} else {
						for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
							for(Map<String, Object> Sakuseiinfo : rstSakuseiinfo){
								if(rstKokyakuinfo.get(i).get("UKETUKE_NO").equals(Sakuseiinfo.get("HOUJIN_IRAIMOTO_UKETUKE_NO"))){
									rstKokyakuinfo.get(i).put("Sinchoku_Name", Sakuseiinfo.get("Sinchoku_Name"));
									rstKokyakuinfo.get(i).put("Sinchoku_Cd", Sakuseiinfo.get("Sinchoku_Cd"));
									rstKokyakuinfo.get(i).put("MADORI", Sakuseiinfo.get("MADORI"));
									rstKokyakuinfo.get(i).put("SITEI_AREA", Sakuseiinfo.get("SITEI_AREA"));
									rstKokyakuinfo.get(i).put("SEQ", Sakuseiinfo.get("SEQ"));
									rstKokyakuinfo.get(i).put("KIBOU_BUKKEN_KB", Sakuseiinfo.get("KIBOU_BUKKEN_KB"));
									rstKokyakuinfo.get(i).put("KIBOU_JYOUKEN_SITEI_CD", Sakuseiinfo.get("KIBOU_JYOUKEN_SITEI_CD"));
									rstKokyakuinfo.get(i).put("KIBOU_YATIN_FROM", Sakuseiinfo.get("KIBOU_YATIN_FROM"));
									rstKokyakuinfo.get(i).put("KIBOU_YATIN_TO", Sakuseiinfo.get("KIBOU_YATIN_TO"));
								}
								if("".equals(rstKokyakuinfo.get(i).get("MADORI")) || rstKokyakuinfo.get(i).get("MADORI") == null){
									if("".equals(rstKokyakuinfo.get(i).get("SITEI_AREA")) || rstKokyakuinfo.get(i).get("SITEI_AREA") == null){
										if("".equals(rstKokyakuinfo.get(i).get("KIBOU_YATIN_FROM")) || rstKokyakuinfo.get(i).get("KIBOU_YATIN_FROM") == null){
											if("".equals(rstKokyakuinfo.get(i).get("KIBOU_YATIN_TO")) || rstKokyakuinfo.get(i).get("KIBOU_YATIN_TO") == null){
											rstKokyakuinfo.get(i).put("SHOW_FLAG", "0");
											} else {
												rstKokyakuinfo.get(i).put("SHOW_FLAG", "1");
											}
										}
									}
								}
							}
						}
					}
				} else {
					for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
						rstKokyakuinfo.get(i).put("SHOW_FLAG", "1");
					}
				}
                /* 2018/11/13 王一氷 ADD START 案件C43040-1_法人の入力項目追加対応 */
                for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
                    String kiguoName = "";
                    if(!"".equals(rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME")) && rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME") != null){
                        kiguoName = rstKokyakuinfo.get(i).get("KIGYO_NAME")+"("+ rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME") +")";
                    }else{
                        kiguoName = (String) rstKokyakuinfo.get(i).get("KIGYO_NAME");
                    }
                    if(!"".equals(kiguoName) && kiguoName != null){
                      /* 2018/11/13 張強軍 UDP START 案件C43040-1_法人の入力項目追加対応 */
                       /* if(kiguoName.length() > 15){
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 15));
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(15, kiguoName.length()));
                        }*/
                       /*2019/08/08 UDP START 張強軍(SYS) 不具合対応(C43040-006)*/
                        /*if(kiguoName.length() > 10){
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 10));
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(10, kiguoName.length()));
                        }*/
                        if(kiguoName.length() > 14){
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 14));
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(14, kiguoName.length()));
                        }
                        /* 2018/11/13 張強軍 UDP END 案件C43040-1_法人の入力項目追加対応 */
                        /*2019/08/08 UDP END 張強軍(SYS) 不具合対応(C43040-006)*/
                        else{
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName);
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME","");
                        }
                    }
                }
                /* 2018/11/13 王一氷 ADD END 案件C43040-1_法人の入力項目追加対応 */
		// 2017/09/12  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
		
		// サービスから取得したデータを画面に設定する
				
		// 2017/09/12  張虎強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
		/*ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo")));*/
		if("3".equals((String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD))){
			ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) rstKokyakuinfo));
		}else{
			ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo")));
		}
		// 2017/09/12  張虎強 UPD END (案件C41004)法人仲介担当者登録機能追加対応
		ukg01010Data.put("kokyakuInfoCount", String.valueOf(kokyakuInfo.get("rstrecordcount")));
		ukg01010Data.put("sysdate", Util.getSysDateYMD());

		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		session.setAttribute("startRowNum", startRowNum);
		session.setAttribute("endRowNum", endRowNum);
		// ########## 2013/01/25 林森 ADD End

		//########## 2013/05/16 郭凡 ADD Start (案件C37109-1)受付システムSTEP2要望対応
		session.setAttribute("pageNum",paramMap.get("pageNum"));
		// ########## 2013/05/16 郭凡 ADD End

		return "pageIndexChg";
	}

	/**
	 * <pre>
	 * [説 明] ソート
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String sort() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		String oyaKana = request.getParameter("kana1");
		String koKana = request.getParameter("kana2");

		String sortitem = request.getParameter("sortitem");
		String sortjun = request.getParameter("sortjun");
		String oldsortjyun = request.getParameter("oldsortjyun");
		String searchItem = request.getParameter("searchItem");
		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		String wheretantou = request.getParameter("wheretantou");
		String sortId = request.getParameter("sortId");
		if ("undefined".equals(wheretantou) || "clear".equals(wheretantou)) {
			wheretantou = null;
		}
		if ("undefined".equals(searchItem) || "clear".equals(searchItem)) {
			searchItem = null;
		}
		// ########## 2013/01/25 林森 ADD End

		String oldsort = sortitem + " " + oldsortjyun;
		String newsort = sortitem + " " + sortjun;

		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		//String postcd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		String postcd = request.getParameter("postCd");
		// ########## 2014/06/23 趙雲剛 UPD End
		String sortkomoku = (String) session.getAttribute(Consts.UKG01010_SORT_INFO);

		if (sortkomoku.endsWith(oldsort)) {
			oldsort = "," + oldsort;
		} else if (sortkomoku.startsWith(oldsort)) {
			oldsort = oldsort + ",";
		}

		sortkomoku = sortkomoku.replace(oldsort, "").replace(",,", ",");
		StringBuilder strbuildsort = new StringBuilder(sortkomoku);
		newsort = newsort + ",";
		strbuildsort.insert(0, newsort);
		sortkomoku = strbuildsort.toString();
		// ########## 2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//他決中止チェックボックスフラグを取得
		String taketuCyushiFlg = String.valueOf(Consts.ZERO);
		if (null != session.getAttribute("taketuCyushiFlg")) {
			taketuCyushiFlg = String.valueOf(session.getAttribute("taketuCyushiFlg").toString());
		}
		// ########## 2017/08/04 MJEC)長倉 ADD End
		// ソート順設定する
		session.setAttribute(Consts.UKG01010_SORT_INFO, sortkomoku);
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		// 業務処理（取得した条件で該当ページのデータを取得する）
		//Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(kaisyacd, postcd, searchItem,wheretantou, oyaKana,
        //        koKana, 1, Consts.INT_21, sortkomoku);
		// ########## 2017/08/04 MJEC)長倉 UPD Start (案件C41059)中止他決顧客の検索対応
		//Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(
		//		kaisyacd, postcd,
		//		(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
		//		(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD), searchItem,
		//		wheretantou, oyaKana, koKana, 1, Consts.INT_21, sortkomoku);
		Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(
				kaisyacd, postcd,
				(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
				(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD), searchItem,
				wheretantou, oyaKana, koKana, 1, Consts.INT_21, taketuCyushiFlg, sortkomoku);
				// ########## 2017/08/04 MJEC)長倉 ADD End
		// ########## 2014/06/23 趙雲剛 UPD End
		// 2017/09/12  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
				// 商談中の顧客一覧データ取得
				List<Map<String, Object>> rstKokyakuinfo = (List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo");
				if ("3".equals((String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD))) {
					// 受付情報作成区分が'4' 商談中の顧客一覧データ取得
					List<Map<String, Object>> rstSakuseiinfo = (List<Map<String, Object>>) kokyakuInfo.get("rstsakuseiinfo");
					if(rstSakuseiinfo.size() ==0){
						for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
							rstKokyakuinfo.get(i).put("SHOW_FLAG", "0");
						}
					} else {
						for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
							for(Map<String, Object> Sakuseiinfo : rstSakuseiinfo){
								if(rstKokyakuinfo.get(i).get("UKETUKE_NO").equals(Sakuseiinfo.get("HOUJIN_IRAIMOTO_UKETUKE_NO"))){
									rstKokyakuinfo.get(i).put("Sinchoku_Name", Sakuseiinfo.get("Sinchoku_Name"));
									rstKokyakuinfo.get(i).put("Sinchoku_Cd", Sakuseiinfo.get("Sinchoku_Cd"));
									rstKokyakuinfo.get(i).put("MADORI", Sakuseiinfo.get("MADORI"));
									rstKokyakuinfo.get(i).put("SITEI_AREA", Sakuseiinfo.get("SITEI_AREA"));
									rstKokyakuinfo.get(i).put("SEQ", Sakuseiinfo.get("SEQ"));
									rstKokyakuinfo.get(i).put("KIBOU_BUKKEN_KB", Sakuseiinfo.get("KIBOU_BUKKEN_KB"));
									rstKokyakuinfo.get(i).put("KIBOU_JYOUKEN_SITEI_CD", Sakuseiinfo.get("KIBOU_JYOUKEN_SITEI_CD"));
									rstKokyakuinfo.get(i).put("KIBOU_YATIN_FROM", Sakuseiinfo.get("KIBOU_YATIN_FROM"));
									rstKokyakuinfo.get(i).put("KIBOU_YATIN_TO", Sakuseiinfo.get("KIBOU_YATIN_TO"));
								}
								if("".equals(rstKokyakuinfo.get(i).get("MADORI")) || rstKokyakuinfo.get(i).get("MADORI") == null){
									if("".equals(rstKokyakuinfo.get(i).get("SITEI_AREA")) || rstKokyakuinfo.get(i).get("SITEI_AREA") == null){
										if("".equals(rstKokyakuinfo.get(i).get("KIBOU_YATIN_FROM")) || rstKokyakuinfo.get(i).get("KIBOU_YATIN_FROM") == null){
											if("".equals(rstKokyakuinfo.get(i).get("KIBOU_YATIN_TO")) || rstKokyakuinfo.get(i).get("KIBOU_YATIN_TO") == null){
											rstKokyakuinfo.get(i).put("SHOW_FLAG", "0");
											} else {
												rstKokyakuinfo.get(i).put("SHOW_FLAG", "1");
											}
										}
									}
								}
							}
						}
					}
				} else {
					for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
						rstKokyakuinfo.get(i).put("SHOW_FLAG", "1");
					}
				}
				/* 2018/11/13 王一氷 ADD START 案件C43040-1_法人の入力項目追加対応 */
                for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
                    String kiguoName = "";
                    if(!"".equals(rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME")) && rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME") != null){
                        kiguoName = rstKokyakuinfo.get(i).get("KIGYO_NAME")+"("+ rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME") +")";
                    }else{
                        kiguoName = (String) rstKokyakuinfo.get(i).get("KIGYO_NAME");
                    }
                    if(!"".equals(kiguoName) && kiguoName != null){
                     /* 2018/11/13 張強軍 UDP START 案件C43040-1_法人の入力項目追加対応 */
                       /* if(kiguoName.length() > 15){
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 15));
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(15, kiguoName.length()));
                        }*/
                      /*2019/08/08 UDP START 張強軍(SYS) 不具合対応(C43040-006)*/
                        /*if(kiguoName.length() > 10){
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 10));
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(10, kiguoName.length()));
                        }*/
                        if(kiguoName.length() > 14){
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 14));
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(14, kiguoName.length()));
                        }
                        /* 2018/11/13 張強軍 UDP END 案件C43040-1_法人の入力項目追加対応 */
                        /*2019/08/08 UDP END 張強軍(SYS) 不具合対応(C43040-006)*/
                        else{
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName);
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME","");
                        }
                    }
                }
                /* 2018/11/13 王一氷 ADD END 案件C43040-1_法人の入力項目追加対応 */
		// 2017/09/12  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
		ukg01010Data = new HashMap<String, Object>();
		ukg01010Data.put("sysdate", Util.getSysDateYMD());
		// 2017/09/12  張虎強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
		/*ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo")));*/
		if("3".equals((String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD))){
			ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) rstKokyakuinfo));
		}else{
			ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo")));
		}
		// 2017/09/12  張虎強 UPD END (案件C41004)法人仲介担当者登録機能追加対応
		ukg01010Data.put("kokyakuInfoCount", String.valueOf(kokyakuInfo.get("rstrecordcount")));
		// 改ページ用
		ukg01010Data.put("sortitem", sortitem);
		ukg01010Data.put("sortjun", sortjun);

		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		//########## 2013/05/16 郭凡 UPD Start (案件C37109-1)受付システムSTEP2要望対応
		// session.setAttribute("sortkomoku", sortkomoku);
		session.setAttribute(Consts.UKG01010_SORT_INFO, sortkomoku);
		// ########## 2013/05/16 郭凡 UPD End
		session.setAttribute("sortId", sortId);
		session.setAttribute("sortOrder", oldsortjyun);
	   // ########## 2013/01/25 林森 ADD End

		//########## 2013/05/16 郭凡 ADD Start (案件C37109-1)受付システムSTEP2要望対応
		session.setAttribute("startRowNum", 1);
		session.setAttribute("endRowNum", Consts.INT_21);
		session.setAttribute("pageNum",1);
		// ########## 2013/05/16 郭凡 ADD End
		// 2018/02/02 SYS_肖剣生 ADD START   (案件番号C42036)デザイン変更
		ukg01010Data.put("userAuthorityCd", sessionLoginInfo.get(Consts.USER_AUTHORITY_CD));
		// 2018/02/02 SYS_肖剣生 ADD END   (案件番号C42036)デザイン変更

		return "sort";
	}

	/**
	 * <pre>
	 * [説 明] 検索ボタンで一覧データを検索する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchkokyakuInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String searchitem = request.getParameter("searchitem"); //

		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		HttpSession session = request.getSession();
		String wheretantou = request.getParameter("wheretantou");
		String showspindex = request.getParameter("showspindex");
		String sessionRemove = request.getParameter("sessionRemove");
		String clearsearchBtn = request.getParameter("clearsearchBtn");
		// ########## 2013/01/25 林森 ADD End
		// ########## 2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//他決中止チェックボックスフラグを取得
		String taketuCyushiFlg = request.getParameter("taketuCyushiFlg");
		// ########## 2017/08/04 MJEC)長倉 ADD End
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		//String postcd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		String postcd = request.getParameter("postCd");
		// ########## 2014/06/23 趙雲剛 UPD End

		// ソート順取得する
		// ########## 2013/01/25 林森 DEL Start (案件C37058)受付システム要望対応
		//HttpSession session = request.getSession();
		// ########## 2013/01/25 林森 DEL

		String KANA_IDX_1 = "全て";
		if ("clear".equals(searchitem)) {
			// KANA_IDX_1 = "";
			searchitem = null;
			//########## 2013/05/16 郭凡 UPD Start (案件C37109-1)受付システムSTEP2要望対応
			//session.setAttribute(Consts.UKG01010_SORT_INFO, Consts.UKG01010_SORT_JYUN);
			if("true".equals(clearsearchBtn)){
				session.setAttribute(Consts.UKG01010_SORT_INFO, Consts.UKG01010_SORT_JYUN);
			}
			// ########## 2013/05/16 郭凡 UPD End
		}
		String sortkomoku = (String) session.getAttribute(Consts.UKG01010_SORT_INFO);
		// ########## 2013/01/25 林森 UPD Start (案件C37058)受付システム要望対応
		//Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(kaisyacd, postcd, searchitem, KANA_IDX_1, "",
		//                                                                  1, Consts.INT_21, sortkomoku);

		//担当者コードをセッションに保存
		if ("undefined".equals(wheretantou) || "clear".equals(wheretantou) || "".equals(wheretantou)) {
			wheretantou = null;
			session.setAttribute("tantouCd", "clear");
		}else {
			session.setAttribute("tantouCd", wheretantou.substring(10));
		}
		//検索欄入力値をセッションに保存
		if (null == searchitem || "".equals(searchitem)) {
			session.removeAttribute("searchitem");
			session.setAttribute("searchitemview","顧客名を入力");
			session.setAttribute("txtflg", false);
		} else {
			session.setAttribute("searchitem", searchitem);
			String searchitemview = searchitem.substring(searchitem.indexOf("$")+1);
			if ("all".equals(searchitemview)) {
				String searchItemCD = searchitem.substring(0,searchitem.indexOf("$"));
				if ("KOKYAKU_SB1_ALL".equals(searchItemCD)) {
					//法人の場合
					session.setAttribute("searchitemview","法人顧客名を入力");
				} else {
					//個人の場合
					session.setAttribute("searchitemview","個人顧客名を入力");
				}
				session.setAttribute("txtflg", false);
			}else {
				session.setAttribute("searchitemview",searchitemview);
				session.setAttribute("txtflg", true);
			}
		}
		// ########## 2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//他決中止チェックボックスフラグをセッションにセット
		session.setAttribute("taketuCyushiFlg", taketuCyushiFlg);
		// ########## 2017/08/04 MJEC)長倉 ADD End

		session.setAttribute("showspindex", showspindex);

		String oyaKana = KANA_IDX_1;
		String koKana = "";
		int startRowNum = 1;
		int endRowNum = Consts.INT_21;
		if (null != sessionRemove && "true".equals(sessionRemove)) {
			session.removeAttribute("oyaKana");
			session.removeAttribute("koKana");
			session.removeAttribute("startRowNum");
			session.removeAttribute("endRowNum");
			//session.removeAttribute("sortkomoku");
			//画面表示用
			session.removeAttribute("sortId");
			session.removeAttribute("sortOrder");
			session.removeAttribute("pageNum");
			session.setAttribute("reDrawFlg",0);
			session.removeAttribute("koKanaIndexNum");
		}else {
			//カナ検索
			if (null != session.getAttribute("oyaKana")) {
				oyaKana = String.valueOf(session.getAttribute("oyaKana"));
			}
			//
			if (null != session.getAttribute("startRowNum")) {
				startRowNum = Integer.valueOf(session.getAttribute("startRowNum").toString());
			}
			if (null != session.getAttribute("endRowNum")) {
				endRowNum = Integer.valueOf(session.getAttribute("endRowNum").toString());
			}
		}
		//ソート項目 // parasoft-suppress UC.ACC "C37109-1JTest対応"
		//########## 2013/05/16 郭凡 UPD Start (案件C37109-1)受付システムSTEP2要望対応 // parasoft-suppress UC.ACC "C37109-1JTest対応"
		//if (null != session.getAttribute("sortkomoku") && !"true".equals(clearsearchBtn)) { // parasoft-suppress UC.ACC "C37109-1JTest対応"
		//	sortkomoku = String.valueOf(session.getAttribute("sortkomoku"));
		//}
		if (null != session.getAttribute(Consts.UKG01010_SORT_INFO) && !"true".equals(clearsearchBtn)) {
			sortkomoku = String.valueOf(session.getAttribute(Consts.UKG01010_SORT_INFO));
		}
		// ########## 2013/05/16 郭凡 UPD End

		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		// 業務処理（取得した条件で該当ページのデータを取得する）
		//Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(kaisyacd, postcd, searchitem,wheretantou,oyaKana,koKana,
		//		startRowNum, endRowNum, sortkomoku);
		// ########## 2017/08/04 MJEC)長倉 UPD Start (案件C41059)中止他決顧客の検索対応
		//Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(
		//		kaisyacd, postcd,
		//		(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
		//		(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD), searchitem,
		//		wheretantou, oyaKana, koKana, startRowNum, endRowNum,
		//		sortkomoku);
		Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(
						kaisyacd, postcd,
						(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
						(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD), searchitem,
						wheretantou, oyaKana, koKana, startRowNum, endRowNum,
						taketuCyushiFlg,sortkomoku);
		// ########## 2017/08/04 MJEC)長倉 UPD End
		// ########## 2014/06/23 趙雲剛 UPD End
		// ########## 2013/01/25 林森 UPD End
		// 2017/09/12  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
		// 商談中の顧客一覧データ取得
		List<Map<String, Object>> rstKokyakuinfo = (List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo");
		if ("3".equals((String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD))) {
			// 受付情報作成区分が'4' 商談中の顧客一覧データ取得
			List<Map<String, Object>> rstSakuseiinfo = (List<Map<String, Object>>) kokyakuInfo.get("rstsakuseiinfo");
			if(rstSakuseiinfo.size() ==0){
				for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
					rstKokyakuinfo.get(i).put("SHOW_FLAG", "0");
				}
			} else {
				for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
					for(Map<String, Object> Sakuseiinfo : rstSakuseiinfo){
						if(rstKokyakuinfo.get(i).get("UKETUKE_NO").equals(Sakuseiinfo.get("HOUJIN_IRAIMOTO_UKETUKE_NO"))){
							rstKokyakuinfo.get(i).put("Sinchoku_Name", Sakuseiinfo.get("Sinchoku_Name"));
							rstKokyakuinfo.get(i).put("Sinchoku_Cd", Sakuseiinfo.get("Sinchoku_Cd"));
							rstKokyakuinfo.get(i).put("MADORI", Sakuseiinfo.get("MADORI"));
							rstKokyakuinfo.get(i).put("SITEI_AREA", Sakuseiinfo.get("SITEI_AREA"));
							rstKokyakuinfo.get(i).put("SEQ", Sakuseiinfo.get("SEQ"));
							rstKokyakuinfo.get(i).put("KIBOU_BUKKEN_KB", Sakuseiinfo.get("KIBOU_BUKKEN_KB"));
							rstKokyakuinfo.get(i).put("KIBOU_JYOUKEN_SITEI_CD", Sakuseiinfo.get("KIBOU_JYOUKEN_SITEI_CD"));
							rstKokyakuinfo.get(i).put("KIBOU_YATIN_FROM", Sakuseiinfo.get("KIBOU_YATIN_FROM"));
							rstKokyakuinfo.get(i).put("KIBOU_YATIN_TO", Sakuseiinfo.get("KIBOU_YATIN_TO"));
						}
						if("".equals(rstKokyakuinfo.get(i).get("MADORI")) || rstKokyakuinfo.get(i).get("MADORI") == null){
							if("".equals(rstKokyakuinfo.get(i).get("SITEI_AREA")) || rstKokyakuinfo.get(i).get("SITEI_AREA") == null){
								if("".equals(rstKokyakuinfo.get(i).get("KIBOU_YATIN_FROM")) || rstKokyakuinfo.get(i).get("KIBOU_YATIN_FROM") == null){
									if("".equals(rstKokyakuinfo.get(i).get("KIBOU_YATIN_TO")) || rstKokyakuinfo.get(i).get("KIBOU_YATIN_TO") == null){
									rstKokyakuinfo.get(i).put("SHOW_FLAG", "0");
									} else {
										rstKokyakuinfo.get(i).put("SHOW_FLAG", "1");
									}
								}
							}
						}
					}
				}
			}
		} else {
			for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
				rstKokyakuinfo.get(i).put("SHOW_FLAG", "1");
			}
		}
		/* 2018/11/13 王一氷 ADD START 案件C43040-1_法人の入力項目追加対応 */
        for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
            String kiguoName = "";
            if(!"".equals(rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME")) && rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME") != null){
                kiguoName = rstKokyakuinfo.get(i).get("KIGYO_NAME")+"("+ rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME") +")";
            }else{
                kiguoName = (String) rstKokyakuinfo.get(i).get("KIGYO_NAME");
            }
            if(!"".equals(kiguoName) && kiguoName != null){
              /* 2019/04/15 張強軍 UDP START 案件C43040-1_法人の入力項目追加対応 */
                /*if(kiguoName.length() > 15){
                    rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 15));
                    rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(15, kiguoName.length()));
                }*/
            	/*2019/08/08 UDP START 張強軍(SYS) 不具合対応(C43040-006)*/
                /*if(kiguoName.length() > 10){
                    rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 10));
                    rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(10, kiguoName.length()));
                }*/
                if(kiguoName.length() > 14){
                    rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 14));
                    rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(14, kiguoName.length()));
                }
                /* 2019/04/15 張強軍 UDP END 案件C43040-1_法人の入力項目追加対応 */
                /*2019/08/08 UDP END 張強軍(SYS) 不具合対応(C43040-006)*/
                else{
                    rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName);
                    rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME","");
                }
            }
        }
        /* 2018/11/13 王一氷 ADD END 案件C43040-1_法人の入力項目追加対応 */
		// 2017/09/12  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
		
		
		ukg01010Data = new HashMap<String, Object>();
		// 2017/09/12  張虎強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
		/*ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo")));*/
		if("3".equals((String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD))){
			ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) rstKokyakuinfo));
		}else{
			ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo")));
		}
		// 2017/09/12  張虎強 UPD END (案件C41004)法人仲介担当者登録機能追加対応
		ukg01010Data.put("kokyakuInfoCount", String.valueOf(kokyakuInfo.get("rstrecordcount")));
		
		ukg01010Data.put("sysdate", Util.getSysDateYMD());

		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		String pageNum = request.getParameter("pageNum");
		ukg01010Data.put("pageNum",pageNum);
		session.setAttribute("pageNum",pageNum);
		//カナ検査、画面表示用
		ukg01010Data.put("oyaKana",oyaKana);
		// ########## 2013/01/25 林森 ADD End

		//########## 2013/05/16 郭凡 ADD Start (案件C37109-1)受付システムSTEP2要望対応
		session.setAttribute("startRowNum", startRowNum);
		session.setAttribute("endRowNum", endRowNum);
		// ########## 2013/05/16 郭凡 ADD End

		// 2018/02/02 SYS_肖剣生 ADD START   (案件番号C42036)デザイン変更
		ukg01010Data.put("userAuthorityCd", sessionLoginInfo.get(Consts.USER_AUTHORITY_CD));
		// 2018/02/02 SYS_肖剣生 ADD END   (案件番号C42036)デザイン変更

		return "kokyakuInfo";
	}

	/**
	 * <pre>
	 * [説 明] 受付状況データを検索する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchUketukeInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg01010Data = new HashMap<String, Object>();

		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		//String postcd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		String postcd = request.getParameter("postCd");
		// ########## 2014/06/23 趙雲剛 UPD End
		String tantouCd = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));

		String sysDate = Util.getSysDateYM();
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		// Map<String, Object> uketukeInfoList = UKG01010Services.getUketukeInfo(kaisyacd, postcd, tantouCd, sysDate);
		Map<String, Object> uketukeInfoList = UKG01010Services.getUketukeInfo(
				kaisyacd,
				(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
				(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD), postcd,
				tantouCd, sysDate);
		// ########## 2014/06/23 趙雲剛 UPD End
		List<Map<String, Object>> list = (List<Map<String, Object>>) uketukeInfoList.get("OUT_UKETUKEINFO");
		if (list != null && list.size() > 0) {
			ukg01010Data.put("uketukeInfo", list.get(0));
		}

		ukg01010Data.put("oshirase", uketukeInfoList.get("OUT_OSHIRASE"));

		ukg01010Data.put("requestToken", session.getAttribute(Consts.REQUEST_TOKEN));
		return "searchUketukeInfo";
	}

	/**
	 * <pre>
	 * [説 明] 未登録一覧データを再検索する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchMitorokuInfo() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg01010Data = new HashMap<String, Object>();

		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		//String postcd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		// Map<String, Object> mitorokuInfo = UKG01010Services.getMitorokuInfo(kaisyacd, postcd);
		HttpServletRequest request = ServletActionContext.getRequest();
		String postcd = request.getParameter("postCd");

		Map<String, Object> mitorokuInfo = UKG01010Services.getMitorokuInfo(kaisyacd,
				(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
        		(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD),
				postcd);
		// ########## 2014/06/23 趙雲剛 UPD End
		ukg01010Data.put("mitorokuInfo", mitorokuInfo.get("rstMitorokuinfo"));
		ukg01010Data.put("mitorokuInfoCount", mitorokuInfo.get("rstmitorokucount"));

		return "searchMitorokuInfo";
	}

	//########## 2017/08/04 MJEC)長倉 ADD Start (案件C38101-1)ホームメイト自動受付対応
	/**
	 * <pre>
	 * [説 明] ホームメイト自動受付一覧データを検索する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchHomemateInfo() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg01010Data = new HashMap<String, Object>();

		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		HttpServletRequest request = ServletActionContext.getRequest();
		String postcd = request.getParameter("postCd");
		//ホームメイト自動受付一覧データを取得する。
		Map<String, Object> homemateInfo = UKG01010Services.getHomemateInfo(kaisyacd,
				(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
        		(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD),
				postcd);
		ukg01010Data.put("homemateInfo", homemateInfo.get("rstHomemateinfo"));
		ukg01010Data.put("homemateInfoCount", homemateInfo.get("rstHomematecount"));

		return "searchHomemateInfo";
	}
	//########## 2017/08/04 MJEC)長倉 ADD End

	/**
	 * <pre>
	 * [説 明] カナインデックス切り替える。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String kanaIndexChg() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String condition = request.getParameter("condition"); // [kana1=ア,kana2=イ]の条件を取得する
		// カナインデックス替える機能用パラメータを取得する
		Map<String, String> paramMap = Util.getParamMap(condition);
		String oyaKana = paramMap.get("kana1");
		String koKana = paramMap.get("kana2");
		String indexflg = paramMap.get("indexflg");
		String searchItem = paramMap.get("searchItem");
		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		String wheretantou = paramMap.get("wheretantou");
		// ########## 2013/01/25 林森 ADD End
		ukg01010Data = new HashMap<String, Object>();

		// ########## 2013/01/25 林森 UPD Start (案件C37058)受付システム要望対応

		if ("undefined".equals(wheretantou) || "clear".equals(wheretantou)) {
			wheretantou = null;
		}
		//if ("undefined".equals(searchItem)) { // parasoft-suppress UC.ACC "c37058Test対応"
		if ("undefined".equals(searchItem) || "clear".equals(searchItem)) {
		// ########## 2013/01/25 林森 UPD End
			searchItem = null;
		}

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String postcd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		// プロパティファイルよりカナインデックス表示数を取得する
		// ########## 2014/05/07 趙雲剛 UPD Start (案件C37075)オリジナル情報誌リプレース
		//String indexKirikae = PropertiesReader.getIntance("/uketuke.properties").getProperty("indexKirikae");
		String indexKirikae = PropertiesReader.getIntance().getProperty("indexKirikae");
		// ########## 2014/05/07 趙雲剛 UPD End
		ukg01010Data.put("sysdate", Util.getSysDateYMD());
		if ("index2".equals(indexflg)) {

			// 既存法人一覧データ取得
			Map<String, Object> hojinInfo = UKG01010Services.getKisonHojinInfo(kaisyacd, postcd, oyaKana, koKana);
			List<Map<String, Object>> hojinList = (List<Map<String, Object>>) hojinInfo.get("rstHojinInfo");
			ukg01010Data.put("hojinInfo", hojinList);

			// 親カナ、子カナインデックス作成フラグ設定
			if (Util.isEmpty(koKana)) {
				if ("全て".equals(oyaKana)) {
					ukg01010Data.put("reDrawFlg2", "0");
				} else {
					if (hojinList.size() > Integer.valueOf(indexKirikae) && !"ン".equals(oyaKana)) {
						ukg01010Data.put("reDrawFlg2", "1");
					} else {
						ukg01010Data.put("reDrawFlg2", "0");
					}
				}

			} else if ("戻る".equals(koKana)) {
				ukg01010Data.put("reDrawFlg2", "0");
			} else {
				ukg01010Data.put("reDrawFlg2", "1");

			}

			return "kisonHojinInfo";

		}

		// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		postcd = paramMap.get("postCd");
		// ########## 2014/06/23 趙雲剛 ADD End
				
		// ソート順取得する
		HttpSession session = request.getSession();
		// ########## 2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//他決中止チェックボックスフラグを取得
		String taketuCyushiFlg = String.valueOf(Consts.ZERO);
		if (null != session.getAttribute("taketuCyushiFlg")) {
			taketuCyushiFlg = String.valueOf(session.getAttribute("taketuCyushiFlg").toString());
		}
		// ########## 2017/08/04 MJEC)長倉 ADD End

		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応\
		if (null != session.getAttribute("txtflg") &&
				Boolean.valueOf(session.getAttribute("txtflg").toString()) &&
				null != session.getAttribute("searchitem")) {

			searchItem = String.valueOf(session.getAttribute("searchitem"));
		}
		// ########## 2013/01/25 林森 ADD End

		String sortkomoku = String.valueOf(session.getAttribute(Consts.UKG01010_SORT_INFO));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		// 業務処理（取得した条件で該当ページのデータを取得する）
		//Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(kaisyacd, postcd, searchItem,wheretantou, oyaKana,
        //        koKana, 1, Consts.INT_21, sortkomoku);
		// ########## 2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(
		//		kaisyacd, postcd,
		//		(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
		//		(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD), searchItem,
		//		wheretantou, oyaKana, koKana, 1, Consts.INT_21, sortkomoku);
		Map<String, Object> kokyakuInfo = UKG01010Services.getKokyakuInfo(
				kaisyacd, postcd,
				(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
				(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD), searchItem,
				wheretantou, oyaKana, koKana, 1, Consts.INT_21, taketuCyushiFlg, sortkomoku);
		// ########## 2017/08/04 MJEC)長倉 ADD End
		// ########## 2014/06/23 趙雲剛 UPD End
		// 2017/09/12  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
				// 商談中の顧客一覧データ取得
				List<Map<String, Object>> rstKokyakuinfo = (List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo");
				if ("3".equals((String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD))) {
					// 受付情報作成区分が'4' 商談中の顧客一覧データ取得
					List<Map<String, Object>> rstSakuseiinfo = (List<Map<String, Object>>) kokyakuInfo.get("rstsakuseiinfo");
					if(rstSakuseiinfo.size() ==0){
						for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
							rstKokyakuinfo.get(i).put("SHOW_FLAG", "0");
						}
					} else {
						for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
							for(Map<String, Object> Sakuseiinfo : rstSakuseiinfo){
								if(rstKokyakuinfo.get(i).get("UKETUKE_NO").equals(Sakuseiinfo.get("HOUJIN_IRAIMOTO_UKETUKE_NO"))){
									rstKokyakuinfo.get(i).put("Sinchoku_Name", Sakuseiinfo.get("Sinchoku_Name"));
									rstKokyakuinfo.get(i).put("Sinchoku_Cd", Sakuseiinfo.get("Sinchoku_Cd"));
									rstKokyakuinfo.get(i).put("MADORI", Sakuseiinfo.get("MADORI"));
									rstKokyakuinfo.get(i).put("SITEI_AREA", Sakuseiinfo.get("SITEI_AREA"));
									rstKokyakuinfo.get(i).put("SEQ", Sakuseiinfo.get("SEQ"));
									rstKokyakuinfo.get(i).put("KIBOU_BUKKEN_KB", Sakuseiinfo.get("KIBOU_BUKKEN_KB"));
									rstKokyakuinfo.get(i).put("KIBOU_JYOUKEN_SITEI_CD", Sakuseiinfo.get("KIBOU_JYOUKEN_SITEI_CD"));
									rstKokyakuinfo.get(i).put("KIBOU_YATIN_FROM", Sakuseiinfo.get("KIBOU_YATIN_FROM"));
									rstKokyakuinfo.get(i).put("KIBOU_YATIN_TO", Sakuseiinfo.get("KIBOU_YATIN_TO"));
								}
								if("".equals(rstKokyakuinfo.get(i).get("MADORI")) || rstKokyakuinfo.get(i).get("MADORI") == null){
									if("".equals(rstKokyakuinfo.get(i).get("SITEI_AREA")) || rstKokyakuinfo.get(i).get("SITEI_AREA") == null){
										if("".equals(rstKokyakuinfo.get(i).get("KIBOU_YATIN_FROM")) || rstKokyakuinfo.get(i).get("KIBOU_YATIN_FROM") == null){
											if("".equals(rstKokyakuinfo.get(i).get("KIBOU_YATIN_TO")) || rstKokyakuinfo.get(i).get("KIBOU_YATIN_TO") == null){
											rstKokyakuinfo.get(i).put("SHOW_FLAG", "0");
											} else {
												rstKokyakuinfo.get(i).put("SHOW_FLAG", "1");
											}
										}
									}
								}
							}
						}
					}
				} else {
					for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
						rstKokyakuinfo.get(i).put("SHOW_FLAG", "1");
					}
				}
				/* 2018/11/13 王一氷 ADD START 案件C43040-1_法人の入力項目追加対応 */
                for(int i = 0 ; i < rstKokyakuinfo.size() ; i++){
                    String kiguoName = "";
                    if(!"".equals(rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME")) && rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME") != null){
                        kiguoName = rstKokyakuinfo.get(i).get("KIGYO_NAME")+"("+ rstKokyakuinfo.get(i).get("NYUKYOSYA_NAME") +")";
                    }else{
                        kiguoName = (String) rstKokyakuinfo.get(i).get("KIGYO_NAME");
                    }
                    if(!"".equals(kiguoName) && kiguoName != null){
                        /* 2019/04/15 張強軍 UDP START 案件C43040-1_法人の入力項目追加対応 */
                        /*if(kiguoName.length() > 15){
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 15));
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(15, kiguoName.length()));
                        }*/
                    	/*2019/08/08 UDP START 張強軍(SYS) 不具合対応(C43040-006)*/
                        /*if(kiguoName.length() > 10){
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 10));
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(10, kiguoName.length()));
                        }*/
                        if(kiguoName.length() > 14){
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName.substring(0, 14));
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME",kiguoName.substring(14, kiguoName.length()));
                        }
                        /* 2019/04/15 張強軍 UDP END 案件C43040-1_法人の入力項目追加対応 */
                        /*2019/08/08 UDP END 張強軍(SYS) 不具合対応(C43040-006)*/
                        else{
                            rstKokyakuinfo.get(i).put("KIGYO_NAME",kiguoName);
                            rstKokyakuinfo.get(i).put("NYUKYOSYA_NAME","");
                        }
                    }
                }
                /* 2018/11/13 王一氷 ADD END 案件C43040-1_法人の入力項目追加対応 */
		// 2017/09/12  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
		// 2017/09/12  張虎強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
		/*ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo")));*/
		if("3".equals((String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD))){
			ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) rstKokyakuinfo));
		}else{
			ukg01010Data.put("kokyakuInfo", dispUketukeNO((List<Map<String, Object>>) kokyakuInfo.get("rstKokyakuinfo")));
		}
		// 2017/09/12  張虎強 UPD END (案件C41004)法人仲介担当者登録機能追加対応
		String recordcount = String.valueOf(kokyakuInfo.get("rstrecordcount"));
		ukg01010Data.put("kokyakuInfoCount", recordcount);

		// 親カナ、子カナインデックス作成フラグ設定
		if (Util.isEmpty(koKana)) {
			if ("全て".equals(oyaKana)) {
				ukg01010Data.put("reDrawFlg", "0");
			} else {

				// データ件数チェック
				if (Integer.valueOf(recordcount) > Integer.valueOf(indexKirikae) && !"ン".equals(oyaKana)) {
					ukg01010Data.put("reDrawFlg", "1");
				} else {
					ukg01010Data.put("reDrawFlg", "0");
				}
			}

		} else if ("戻る".equals(koKana)) {
			ukg01010Data.put("reDrawFlg", "0");
		} else {
			ukg01010Data.put("reDrawFlg", "1");
		}

		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		if (null != oyaKana && !"".equals(oyaKana)) {
			//ページ
			session.setAttribute("startRowNum",1);
			session.setAttribute("endRowNum",Consts.INT_21);
			session.setAttribute("oyaKana", oyaKana);
			//########## 2013/05/16 郭凡 ADD Start (案件C37109-1)受付システムSTEP2要望対応
			session.setAttribute("pageNum",1);
			// ########## 2013/05/16 郭凡 ADD End
		}else {
			session.removeAttribute("oyaKana");
		}
		//子カタカナ
		if (null != koKana && !"".equals(koKana)) {
			session.setAttribute("koKana", koKana);
			//親カナのインデックス
			int koKanaIndexNum = -1;
			String oyaKanaIndexId = "";
			if ("ア".equals(oyaKana)) {
				koKanaIndexNum = 0;
				oyaKanaIndexId = "kanaOya2_1";
			}else if ("カ".equals(oyaKana)) {
				koKanaIndexNum = 1;
				oyaKanaIndexId = "kanaOya3_1";
			}else if ("サ".equals(oyaKana)) {
				koKanaIndexNum = 2;
				oyaKanaIndexId = "kanaOya4_1";
			}else if ("タ".equals(oyaKana)) {
				koKanaIndexNum = 3;
				oyaKanaIndexId = "kanaOya5_1";
			}else if ("ナ".equals(oyaKana)) {
				koKanaIndexNum = 4;
				oyaKanaIndexId = "kanaOya6_1";
			}else if ("ハ".equals(oyaKana)) {
				koKanaIndexNum = 5;
				oyaKanaIndexId = "kanaOya7_1";
			}else if ("マ".equals(oyaKana)) {
				koKanaIndexNum = 6;
				oyaKanaIndexId = "kanaOya8_1";
			}else if ("ヤ".equals(oyaKana)) {
				koKanaIndexNum = 7;
				oyaKanaIndexId = "kanaOya9_1";
			}else if ("ラ".equals(oyaKana)) {
				koKanaIndexNum = 8;
				oyaKanaIndexId = "kanaOya10_1";
			}else if ("ワ".equals(oyaKana)) {
				koKanaIndexNum = 9;
				oyaKanaIndexId = "kanaOya11_1";
			}
			session.setAttribute("koKanaIndexNum", koKanaIndexNum);
			session.setAttribute("oyaKanaIndexId", oyaKanaIndexId);
		}else {
			session.removeAttribute("koKana");
			session.removeAttribute("koKanaIndexNum");
			session.removeAttribute("oyaKanaIndexId");
		}
		session.setAttribute("reDrawFlg", ukg01010Data.get("reDrawFlg").toString());
		// ########## 2013/01/25 林森 ADD End

		// 2018/02/02 SYS_肖剣生 ADD START   (案件番号C42036)デザイン変更
		ukg01010Data.put("userAuthorityCd", sessionLoginInfo.get(Consts.USER_AUTHORITY_CD));
		// 2018/02/02 SYS_肖剣生 ADD END   (案件番号C42036)デザイン変更

		return "kanaIndexChg";
	}

	/**
	 * <pre>
	 * [説 明] お客様情報画面へ遷移する
	 * @throws Exception　処理異常
	 * </pre>
	 */
	// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
	//public String goukg02010() {
	public String goukg02010() throws Exception {
	// ########## 2014/06/23 趙雲剛 UPD End

		HttpServletRequest req = ServletActionContext.getRequest();
		String uktukeno = req.getParameter("uketukeNo");
		String tantousya = req.getParameter("tantousya");
		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		String pageNum = req.getParameter("pageNum");
		// ########## 2013/01/25 林森 ADD End
		// 受付番号をセッションに格納する
		HttpSession session = req.getSession();
		session.setAttribute(Consts.UKETUKENO, uktukeno);
		// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		//受付店舗情報がセッションに保存する
		savePostInfoToSession();
		// ########## 2014/06/23 趙雲剛 ADD End
		session.setAttribute(Consts.CHUKAITANTOU, tantousya);
		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		session.setAttribute("pageNum", pageNum);
		// ########## 2013/01/25 林森 ADD End
		return "ukg02010";
	}

	/**
	 * <pre>
	 * [説 明] お客様情報画面へ遷移する
	 * @throws Exception　処理異常
	 * </pre>
	 */
	// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
	//public String goukg02040() {
	public String goukg02040() throws Exception {
	// ########## 2014/06/23 趙雲剛 UPD End

		HttpServletRequest request = ServletActionContext.getRequest();
		String uktukeno = request.getParameter("uketukeNo");
		String tantousya = request.getParameter("tantousya");
		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		String pageNum = request.getParameter("pageNum");
		// ########## 2013/01/25 林森 ADD End
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();
		session.setAttribute(Consts.UKETUKENO, uktukeno);
		// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		//受付店舗情報がセッションに保存する
		savePostInfoToSession();
		// ########## 2014/06/23 趙雲剛 ADD End
		session.setAttribute(Consts.CHUKAITANTOU, tantousya);
		// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
		session.setAttribute("pageNum", pageNum);
		// ########## 2013/01/25 林森 ADD End
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		String userID = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));

		// INFOログを出力する
		log.info("顧客情報参照  ユーザID：" + userID + " 受付No:" + uktukeno);

		return "ukg02040";
	}

	// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
	/**
	 * <pre>
	 * [説 明] 受付店舗情報がセッションに保存する
	 * @throws Exception　処理異常
	 * </pre>
	 */
	
	public String savePostInfoToSession() throws Exception {
	
		HttpServletRequest req = ServletActionContext.getRequest();
		HttpSession session = req.getSession();
		String postcd = req.getParameter("postCd");
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		HashMap<String, Object> postInfo = (HashMap<String, Object>) searchPostInfo(String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD)), postcd);
		if (postInfo != null) {
			postInfo.put(Consts.POST_CD, postcd);
			if (Util.isEmpty((String) postInfo.get(Consts.POST_JUSYO_CD))) {
				postInfo.put(Consts.POST_JUSYO_CD, Consts.DEFAULT_POST_JUSYO_CD.substring(0, 2));
				postInfo.put(Consts.POST_SHIKUGUN_CD, Consts.DEFAULT_POST_JUSYO_CD.substring(2, 5));
			}
			session.setAttribute(Consts.UKETUKE_POST_INFO, postInfo);
		}
		return "searchUketukeInfo";
	}
	// ########## 2014/06/23 趙雲剛 ADD End
	
	/**
	 * <pre>
	 * [説 明] お知らせ情報更新
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String updSyoukaiInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String updInfo = request.getParameter("updInfo");
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();
		session.setAttribute(Consts.UKETUKENO, updInfo.split(Consts.SLASH)[0]);
		ukg01010Data = new HashMap<String, Object>();
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		if(!"0".equals(sessionLoginInfo.get(Consts.USER_AUTHORITY_CD))){
			ukg01010Data = new HashMap<String, Object>();
			ukg01010Data.put("errMsgCd", "0000000");
			return "updSyoukaiInfo";
		}
		// ########## 2014/06/23 趙雲剛 ADD End
		updInfo += "/" + sessionLoginInfo.get(Consts.USER_CD) + "/" + sessionLoginInfo.get(Consts.KAISYA_CD);

		Map<String, Object> oshiraseList = UKG01010Services.updOshiraseInfo(updInfo);

		ukg01010Data = new HashMap<String, Object>();
		ukg01010Data.put("errMsgCd", oshiraseList.get("rst_code"));

		if ("ERRS010".equals(oshiraseList.get("rst_code"))) {
			// エラーメッセージ
			ukg01010Data.put("errMsg", Util.getServerMsg("ERRS010"));
		}
		return "updSyoukaiInfo";
	}

	/**
	 * <pre>
	 * [説 明]セッションにソート順をクリアする
	 * @return closePage
	 * </pre>
	 */
	public String closePage() {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションにソート順をクリアする
		HttpSession session = request.getSession();
		session.removeAttribute(Consts.UKG01015_SORT_INFO);
		ukg01010Data = new HashMap<String, Object>();
		ukg01010Data.put("chgFlg", session.getAttribute("chgFlg"));
		return "closePage";
	}

	// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
	/**
	 * 担当者受付組数取得
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String uketukeKumiSuu() throws Exception{

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		HttpServletRequest request = ServletActionContext.getRequest();
		String tantouCds = request.getParameter("tantouCds");
		if (null == tantouCds || "".equals(tantouCds)) {
			tantouCds = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		}

		Map<String, Object> kumiSuuInfo = UKG01010Services.getUketukeKumiSuu(
				String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD)),
                String.valueOf(sessionLoginInfo.get(Consts.POST_CD)), tantouCds);
		ukg01010Data = new HashMap<String, Object>();
		//当月の受付組数
		if (null != kumiSuuInfo.get("OUT_MONTHKUMISUU")) {
			ukg01010Data.put("monthkumisuu",(List<Map<String, Object>>) kumiSuuInfo.get("OUT_MONTHKUMISUU"));
		}
		//保有組数
		if (null != kumiSuuInfo.get("OUT_KUMISUU")) {
			ukg01010Data.put("umisuu",(List<Map<String, Object>>)kumiSuuInfo.get("OUT_KUMISUU"));
		}

		return "uketukeKumiSuuInfo";
	}
	// ########## 2013/01/25 林森 ADD End

	// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
	/**
	 * 店舗受付組数取得
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String postUketukeKumiSuu() throws Exception{

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		HttpServletRequest request = ServletActionContext.getRequest();
		String postCd = request.getParameter("postCd");

		Map<String, Object> kumiSuuInfo = UKG01010Services.getPostUketukeKumiSuu(
				String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD)),
				postCd,
				(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
				(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD));
		ukg01010Data = new HashMap<String, Object>();
		//当月の受付組数
		if (null != kumiSuuInfo.get("OUT_MONTHKUMISUU")) {
			ukg01010Data.put("monthkumisuu",(List<Map<String, Object>>) kumiSuuInfo.get("OUT_MONTHKUMISUU"));
		}
		//保有組数
		if (null != kumiSuuInfo.get("OUT_KUMISUU")) {
			ukg01010Data.put("umisuu",(List<Map<String, Object>>)kumiSuuInfo.get("OUT_KUMISUU"));
		}

		return "uketukeKumiSuuInfo";
	}
	/**
	 * 店舗情報を取得
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> searchPostInfo(String kaisyaCd, String postCd) throws Exception {
		Map<String, Object> map = UKG01010Services.getPostInfo(kaisyaCd, postCd);
		List<Map<String, Object>> postInfo = (List<Map<String, Object>>)map.get("rstpostinfo");
		if (postInfo != null && postInfo.size() > 0) {
			return postInfo.get(0);
		}
		return null;
	}
	
	// ########## 2014/06/23 趙雲剛 ADD End
	/**
	 * 受付№を変換する
	 * @param rstKokyakuinfo 商談中の顧客一覧データ
	 * @return rstKokyakuinfo 商談中の顧客一覧データ
	 */
	private List<Map<String, Object>> dispUketukeNO(List<Map<String, Object>> rstKokyakuinfo) {

	    if (rstKokyakuinfo != null && rstKokyakuinfo.size() > 0) {
			for (Map<String, Object> kokyakuinfo : rstKokyakuinfo) {
				kokyakuinfo.put("UKETUKE_NO_DISPLAY", DispFormat.getUketukeNo((String) kokyakuinfo.get("UKETUKE_NO")));
			}
		}
	    return rstKokyakuinfo;
    }

	// ########## 2013/04/18 温 ADD Start 案件C37107_受付状況データダウンロード機能
	/**
	 * 半角数字を全角数字に変換する
	 * @param str 変換前文字列(null不可)
	 * @return 変換後文字列
	 */
	private String setNumberToZenkaku(String str) {

		int intNum = Integer.parseInt(str);
		String strRet = "";
		if(intNum < 10){
			switch(intNum) {
				case 1 : strRet = "１"; break;
				case 2 : strRet = "２"; break;
				case 3 : strRet = "３"; break;
				case 4 : strRet = "４"; break;
				case 5 : strRet = "５"; break;
				case 6 : strRet = "６"; break;
				case 7 : strRet = "７"; break;
				case 8 : strRet = "８"; break;
				case 9 : strRet = "９"; break;
				default : strRet = "";
			}
		}else{
			strRet = String.valueOf(intNum);
		}
		return strRet + "月";
	}
	// ########## 2013/04/18 温 ADD End
	//########## 2013/05/16 郭凡 ADD Start (案件C37109-1)受付システムSTEP2要望対応
	/**
	 * <pre>
	 * [説 明] 中止、他決、商談中ボタン押下時処理
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String chuushiTaketuProcess() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 受付no.を取得
		String uketukeNo = request.getParameter("uketukeNo");
		session.setAttribute(Consts.UKETUKENO, uketukeNo);
		// 担当者コードを取得
		String tantou = String.valueOf(loginInfo.get(Consts.USER_CD));
		// 更新日付を取得
		String updateDate = request.getParameter("updateDate");
		// 処理種類を取得
		String type = request.getParameter("type");
        //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
        // 商談内容を取得
        String shoudancmnt = request.getParameter("shoudancmnt");
        //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
		
		ukg01010Data = new HashMap<String, Object>();
		// 進捗状況を更新して、取得する。関数名とは異なるが商談中に変更時もこの関数で処理をする。
		Map<String, Object> resData = UKG01010Services.chuushiTaketuProcess(kaisyaCd, uketukeNo, tantou, updateDate,
        //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
                                                                            //type);
                                                                            type,shoudancmnt);
        //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
		// ########## 2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//他決中止チェックボックスフラグを取得
		String taketuCyushiFlg = String.valueOf(Consts.ZERO);
		if (null != session.getAttribute("taketuCyushiFlg")) {
			taketuCyushiFlg = String.valueOf(session.getAttribute("taketuCyushiFlg").toString());
		}
		// ########## 2017/08/04 MJEC)長倉 ADD End
		ukg01010Data.put("UPD_RESTULT", resData.get("rst_code"));
		if ("0000000".equals(resData.get("rst_code"))) {
			sessionLoginInfo = getLoginInfo();
			Date date = new Date();
			String nowMonth = new SimpleDateFormat(Consts.FORMAT_DATE_YYYYMM).format(date);
			String nowYmd = Util.formatDate(date, Consts.FORMAT_DATE_YYMMDD);
			//担当者
			String tantouCd = null;
			if (null == session.getAttribute("tantouCd")) {
				tantouCd = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
				session.setAttribute("tantouCd", tantouCd);
			} else {
				if (!"clear".equals(session.getAttribute("tantouCd"))) {
					tantouCd = String.valueOf(session.getAttribute("tantouCd"));
				}
			}
			//検索欄入力値
			String searchitem = null;
			if (null != session.getAttribute("searchitem")) {
				searchitem = String.valueOf(session.getAttribute("searchitem"));
			}else{
				session.setAttribute("searchitemview","顧客名を入力");
				session.setAttribute("txtflg", false);
			}
			//カナ
			String oyaKana = "全て";
			String koKana = null;
			if (null != session.getAttribute("oyaKana")) {
				oyaKana = String.valueOf(session.getAttribute("oyaKana"));
			}
			if (null != session.getAttribute("koKana")) {
				koKana = String.valueOf(session.getAttribute("koKana"));
			}
			//ページ
			int startRowNum = 1;
			int endRowNum = Consts.INT_21;
			if (null != session.getAttribute("startRowNum")) {
				startRowNum = Integer.valueOf(session.getAttribute("startRowNum").toString());
			}
			if (null != session.getAttribute("endRowNum")) {
				endRowNum = Integer.valueOf(session.getAttribute("endRowNum").toString());
			}
			//ソート
			String sortkomoku = Consts.UKG01010_SORT_JYUN;
			//########## 2013/05/16 郭凡 UPD Start (案件C37109-1)受付システムSTEP2要望対応 // parasoft-suppress UC.ACC "C37109-1JTest対応"
			//if (null != session.getAttribute("sortkomoku")) { // parasoft-suppress UC.ACC "C37109-1JTest対応"
			//	sortkomoku = String.valueOf(session.getAttribute("sortkomoku"));
			//}
			if (null != session.getAttribute(Consts.UKG01010_SORT_INFO)) {
				sortkomoku = String.valueOf(session.getAttribute(Consts.UKG01010_SORT_INFO));
			}
			// ########## 2013/05/16 郭凡 UPD End
			String pageNum = request.getParameter("pageNum");
			Map<String, Object> initInfo = UKG01010Services.getInitInfo(
					String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD)),
	                String.valueOf(sessionLoginInfo.get(Consts.POST_CD)),
	                // ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
	                // ########## 2017/08/04 MJEC)長倉 UPD Start 既存不具合修正
	                //ストアドにてnullでは絞込みが出来ず、全データを取得してきてしまう。
	                //null,
	                String.valueOf(sessionLoginInfo.get(Consts.POST_CD)),
	                // ########## 2017/08/04 MJEC)長倉 UPD End
	                (String) sessionLoginInfo.get(Consts.SISYA_BLK_CD),
	                (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
	                // ########## 2014/06/23 趙雲剛 ADD End
	                // ########## 2017/08/04 MJEC)長倉 UPD Start (案件C41059)中止他決顧客の検索対応
	                //tantouCd,nowYmd, nowMonth,searchitem,oyaKana,koKana,
	                //startRowNum, endRowNum,sortkomoku);
	                tantouCd,nowYmd, nowMonth,searchitem,oyaKana,koKana,
	                startRowNum, endRowNum,taketuCyushiFlg,sortkomoku);
					// ########## 2017/08/04 MJEC)長倉 UPD End
			List<Map<String, Object>> rstKokyakuinfo = (List<Map<String, Object>>) initInfo.get("rstKokyakuinfo");
			if(!"1".equals(pageNum) && (rstKokyakuinfo == null || rstKokyakuinfo.size() == 0)){
				pageNum = String.valueOf((Integer.parseInt(request.getParameter("pageNum")) - 1));
				 startRowNum = (Integer.parseInt(pageNum) - 1) * Consts.PAGEIDX + 1;
				 endRowNum = startRowNum + Consts.PAGEIDX;
				            initInfo = UKG01010Services.getInitInfo(
							String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD)),
			                String.valueOf(sessionLoginInfo.get(Consts.POST_CD)),
			                // ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
			                // ########## 2017/08/04 MJEC)長倉 UPD Start 既存不具合修正
			                //ストアドにてnullでは絞込みが出来ず、全データを取得してきてしまう。
			                //null,
			                String.valueOf(sessionLoginInfo.get(Consts.POST_CD)),
			                // ########## 2017/08/04 MJEC)長倉 UPD End
			                (String) sessionLoginInfo.get(Consts.SISYA_BLK_CD),
			                (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
			                // ########## 2014/06/23 趙雲剛 ADD End
			                // ########## 2017/08/04 MJEC)長倉 UPD Start (案件C41059)中止他決顧客の検索対応
			                //tantouCd,nowYmd, nowMonth,searchitem,oyaKana,koKana,
			                //startRowNum, endRowNum,sortkomoku);
			                tantouCd,nowYmd, nowMonth,searchitem,oyaKana,koKana,
			                startRowNum, endRowNum,taketuCyushiFlg,sortkomoku);
							// ########## 2017/08/04 MJEC)長倉 UPD End
				            rstKokyakuinfo = (List<Map<String, Object>>) initInfo.get("rstKokyakuinfo");
			}
			//受付№を変換する
			dispUketukeNO(rstKokyakuinfo);
			ukg01010Data.put("kokyakuInfo", rstKokyakuinfo);
			ukg01010Data.put("kokyakuInfoCount", String.valueOf(initInfo.get("rstrecordcount")));
			// 2018/02/02 SYS_肖剣生 ADD START   (案件番号C42036)デザイン変更
			ukg01010Data.put("userAuthorityCd", sessionLoginInfo.get(Consts.USER_AUTHORITY_CD));
			// 2018/02/02 SYS_肖剣生 ADD END   (案件番号C42036)デザイン変更

			ukg01010Data.put("pageNum",pageNum);
			session.setAttribute("pageNum",pageNum);
			session.setAttribute("startRowNum",startRowNum);
			session.setAttribute("endRowNum",endRowNum);
		} else if ("ERRS003".equals(resData.get("rst_code"))) {
			ukg01010Data.put("MESSAGE", Util.getServerMsg("ERRS003"));
		} else if ("ERRS999".equals(resData.get("rst_code")) || "1111111".equals(resData.get("rst_code"))) {
			ukg01010Data.put("MESSAGE", Util.getServerMsg("ERRS002", "更新処理"));
		}
		return "chuushiTaketuProcess";
	}
	// ########## 2013/05/16 郭凡 ADD End
	// ########## 2014/05/13 胡 ADD Start (案件C38100)トップページで既存個人を検索（他）対応
	/**
	 * 検索ボタン押下処理
	 * kokyakuJyouho()
	 * @throws SQLException		SQL例外
	 * @throws Exception		一般例外
	 */
	@SuppressWarnings("unchecked")
	public String kokyakuJyouho() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();

		//セッションのログイン情報取得する
        Map<String, Object> loginInfo = getLoginInfo();
        ukg01010Data = new HashMap<String, Object>();

        String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));

        String rdKokyakuSb1 = request.getParameter("rdKokyakuSb");
        String kokyaku = request.getParameter("kokyaku");
        String chkKokyaku = request.getParameter("chkKokyaku");
        String txtKokyakuKana = request.getParameter("txtKokyakuKana");
        String chkKokyakuKana = request.getParameter("chkKokyakuKana");
        String birthday = request.getParameter("birthday");
        String txtTel = request.getParameter("txtTel").replace("-", "");
        String txtMobile = request.getParameter("txtMobile").replace("-", "");

        // 顧客情報データを取得する
        Map<String, Object> kokyakuJyouhoData = UKG01010Services.getKokyakuJyouho(kaisyacd,
																					rdKokyakuSb1,
																					kokyaku,
																					chkKokyaku,
																					txtKokyakuKana,
																					chkKokyakuKana,
																					birthday,
																					txtTel,
																					txtMobile,
																					"",
																					"",
																					1,
																					null,
																					"");
        ukg01010Data.put("kokyakuJyouhoList", (List<Map<String, Object>>) kokyakuJyouhoData.get("pcurList"));

		return "kisonKojinInfo";
	}
	// ########## 2014/05/13 胡 ADD
	
	// 2017/09/18  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
	/**
	 * 法人検索ボタン押下処理
	 * houjinSelect()
	 * @throws SQLException		SQL例外
	 * @throws Exception		一般例外
	 */
	@SuppressWarnings("unchecked")
	public String houjinSelectInfo() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        ukg01010Data = new HashMap<String, Object>();
        // 企業コード
        String kigyouCode = request.getParameter("kigyouCode");
        // 企業名
        String kigyouName = request.getParameter("kigyouName");
        // 企業情報データを取得する
        Map<String, Object> houjinData = UKG01010Services.gethoujinSelectInfo(kigyouCode,kigyouName);
        ukg01010Data.put("houjinList", (List<Map<String, Object>>) houjinData.get("pcurList"));
		return "houjinListInfo";
	}
	
	/**
	 * <pre>
	 * [説 明] 法人仲介依頼件数を取得する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchHoujinIraiInfo() throws Exception {
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg01010Data = new HashMap<String, Object>();
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String sysDate = Util.getSysDateYM();
		// 法人仲介依頼件数を取得する
		Map<String, Object> houjinIraiInfo = UKG01010Services.getHoujinIraiInfo(kaisyacd,
				(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
        		(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD),
        		sysDate);
		ukg01010Data.put("houjinIraiCount", houjinIraiInfo.get("rstHoujinIraiCount"));
		return "searchHoujinIraiInfo";
	}

	/**
	 * <pre>
	 * [説 明] 法人仲介依頼状況データを取得
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchHoujinIraiData() throws Exception {
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg01010Data = new HashMap<String, Object>();
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String sysDate = Util.getSysDateYM();
		Map<String, Object> houjinIraiData = UKG01010Services.getHoujinIraiData(kaisyacd,
				(String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD),
				(String) sessionLoginInfo.get(Consts.SISYA_BLK_CD),
				sysDate);
		ukg01010Data.put("houjinIraiData", houjinIraiData.get("rstHoujinIraiData"));
		return "searchHoujinIraiData";
	}

	/**
	 * <pre>
	 * [説 明] 法人仲介受付一覧データを再検索する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchHoujinUketukeData() throws Exception {
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg01010Data = new HashMap<String, Object>();
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		HttpServletRequest request = ServletActionContext.getRequest();
		int showNum = 0;
		if(request.getParameter("showNum") != null){
			showNum = Integer.parseInt(request.getParameter("showNum"));
		}
		String sisyaBlkCd = (String) sessionLoginInfo.get(Consts.SISYA_BLK_CD);
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		String postCd = request.getParameter("postCd");
		Map<String, Object> houjinUketukeData = UKG01010Services.getHoujinUketukeData(kaisyacd, showNum, sisyaBlkCd, authorityCd, postCd);
		ukg01010Data.put("houjinUketukeInfo", houjinUketukeData.get("rstHoujinUketukeinfo"));
		ukg01010Data.put("houjinUketukeCount", houjinUketukeData.get("rstHoujinUketukecount"));
		return "searchHoujinUketukeData";
	}

	/**
	 * <pre>
	 * [説 明] お客様情報画面へ遷移する
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String houjingoukg02010() throws Exception {
		HttpServletRequest req = ServletActionContext.getRequest();
		String pageNum = req.getParameter("pageNum");
		String kigyoucd = req.getParameter("kigyoucd");
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		String tyukaitantoucd = req.getParameter("tantoucd");
		String tyukaitantounm = req.getParameter("tantounm");
		if (null == tyukaitantoucd || "".equals(tyukaitantoucd)) {
			tyukaitantoucd = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		}
		// 受付番号をセッションに格納する
		HttpSession session = req.getSession();
		String searchPostNm = String.valueOf(sessionLoginInfo.get(Consts.POST_NAME));
		String searchPostCd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		if (null == session.getAttribute("showspindex")) {
			session.setAttribute("showspindex", String.valueOf(sessionLoginInfo.get(Consts.USER_CD)));
		}
		//受付店舗情報がセッションに保存する
		savePostInfoToSession();
		session.setAttribute("pageNum", pageNum);
		session.setAttribute("kigyoucd", kigyoucd);
		session.setAttribute(Consts.UKETUKENO, "");
		session.setAttribute("tyukaitantoucd", tyukaitantoucd);
		session.setAttribute("tyukaitantounm", tyukaitantounm);
		session.setAttribute("sysdate", Util.getSysDate());
		session.setAttribute("postcd", searchPostCd);
		session.setAttribute("postnm", searchPostNm);
		session.setAttribute("uketuketantoucd", tyukaitantoucd);
		return "ukg02010";
	}

	/**
	 * <pre>
	 * [説 明] 「進捗状況」リンク押下時会社データを取得する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchKaisyaData() throws Exception {
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg01010Data = new HashMap<String, Object>();
		HttpServletRequest req = ServletActionContext.getRequest();
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = req.getParameter("uketukeno");
		Map<String, Object> kaisyaData = UKG01010Services.getKaisyaData(kaisyacd, uketukeNo);
		ukg01010Data.put("kaisyaData", kaisyaData);
		return "searchKaisyaData";
	}
	// 2017/09/18  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
	
    // 2019/03/18 ADD START 汪会(SYS) C42036-142
    /**
     * <pre>
     * [説 明] 新規受付No･発行ボタン状態がセッションで更新する
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    public String updateSession() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession session = request.getSession();
        // 新規受付No･発行ボタン表示フラグ
        String shinkiUketukeDis = request.getParameter("shinkiUketukeDis");
        session.setAttribute("shinkiUketukeDis", shinkiUketukeDis);
        return successForward();
    }
    // 2019/03/18 ADD END 汪会(SYS) C42036-142
  //2019/04/29 張強軍(SYS) ADD START QA_C42036_037
    private String foramtDate(String prevNextDate){
       
        String newDate ="";
        String newWeek ="";
        if(prevNextDate != null && prevNextDate != ""){
        String date =prevNextDate.substring(0, 10);
        String week=prevNextDate.substring(12,15);
        if(week.equals("MON")){
            newWeek = "(月)";
        }else if(week.equals("TUE")){
            newWeek = "(火)";
        }else if(week.equals("WED")){
            newWeek = "(水)";
         }else if(week.equals("THU")){
            newWeek = "(木)";
         }else if(week.equals("FRI")){
            newWeek = "(金)";
         }else if(week.equals("SAT")){
            newWeek = "(土)";
         }else if(week.equals("SUN")){
            newWeek = "(日)";
          }
        newDate =date+newWeek;
        }
        return newDate;
    }
  //2019/04/29 張強軍(SYS) ADD END QA_C42036_037
}
