/**
 * @システム名: 受付システム
 * @ファイル名: UKG00010Action.java
 * @更新日付：: 2011/12/26
 * @Copyright: 2011 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.CookieNinsyoRequestBean;
import jp.co.token.uketuke.formbean.CookieNinsyoResponseBean;
import jp.co.token.uketuke.service.ICookiesApiService;
import jp.co.token.uketuke.service.IUKG00010Service;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;

/**
 * <pre>
 * [機 能] ログイン。
 * [説 明] ログインアクション。
 * @author [作 成] 2011/10/18 戴氷馨(SYS)
 * 変更履歴: 2014/06/23	SYS_趙雲剛	(案件C38117)セールスポイント編集機能の追加
 * 変更履歴: 2017/09/12	SYS_張虎強	(案件C41004)法人仲介担当者登録機能追加対応
 * 変更履歴: 2019/07/03	SYS_程旋    	C43100_お客様項目の入力簡素化対応
 * </pre>
 */
public class UKG00010Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 6045632979035592097L;

	/** ユーザID */
	private String txtUserID;

	/** パスワード */
	private String password;

	/** 会社コード */
	private String kaisyaCd;

	/** トークン */
	private String requestToken;

	/** 画面ID */
	private String pageId;

	/** 受付番号 */
	private String uketukeNo;

	/** ログインサービス */
	private IUKG00010Service ukg00010Services;

//	2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
	/** cookie状態 */
	private String cookieState;
	/**
     * Cookie認証情報APIサービス
     */
    private ICookiesApiService cookieApiService;

//    2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応

	/** テスト用画面のデータ */
	private Map<String, Object> ukg00010Data = new HashMap<String, Object>();

	/** システムのタイトルをプの名前 */
	private String systemTitleName;
	/**
	 * <pre>
	 * [説 明] 受付番号。
	 * @return 受付番号
	 * </pre>
	 */
	public String getUketukeNo() {

		return uketukeNo;
	}

	/**
	 * <pre>
	 * [説 明] 受付番号。
	 * @return 受付番号
	 * </pre>
	 */
	public void setUketukeNo(String uketukeNo) {

		this.uketukeNo = uketukeNo;
	}

	/**
	 * <pre>
	 * [説 明] 画面ID。
	 * @return 画面ID
	 * </pre>
	 */
	public String getPageId() {

		return pageId;
	}

	/**
	 * <pre>
	 * [説 明] 画面ID。
	 * @return 画面ID
	 * </pre>
	 */
	public void setPageId(String pageId) {

		this.pageId = pageId;
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを取得する。
	 * @return 画面のデータ
	 * </pre>
	 */
	public Map<String, Object> getUkg00010Data() {

		return ukg00010Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg00010Data 画面のデータ
	 * </pre>
	 */
	public void setUkg00010Data(Map<String, Object> ukg00010Data) {

		this.ukg00010Data = ukg00010Data;
	}

	/**
	 * <pre>
	 * [説明] トークンを取得する。
	 * @return　トークン
	 * </pre>
	 */
	public String getRequestToken() {

		return requestToken;
	}

	/**
	 * <pre>
	 * [説明] トークンを設定する。
	 * @param requestToken トークン
	 * </pre>
	 */
	public void setRequestToken(String requestToken) {

		this.requestToken = requestToken;
	}

	/**
	 * <pre>
	 * [説明] 会社コードを取得する。
	 * @return　会社コード
	 * </pre>
	 */
	public String getKaisyaCd() {

		return kaisyaCd;
	}

	/**
	 * <pre>
	 * [説明] 会社コードを設定する。
	 * @param name 会社コード
	 * </pre>
	 */
	public void setKaisyaCd(String kaisyaCd) {

		this.kaisyaCd = kaisyaCd;
	}

	/**
	 * <pre>
	 * [説明] ユーザIDを取得する。
	 * @return　ユーザID
	 * </pre>
	 */
	public String getTxtUserID() {

		return txtUserID;
	}

	/**
	 * <pre>
	 * [説明] ユーザIDを設定する。
	 * @param name ユーザID
	 * </pre>
	 */
	public void setTxtUserID(String txtUserID) {

		this.txtUserID = txtUserID;
	}

	/**
	 * <pre>
	 * [説明] パスワードを取得する。
	 * @return パスワード
	 * </pre>
	 */
	public String getPassword() {

		return password;
	}

	/**
	 * <pre>
	 * [説明] パスワードを設定する。
	 * @param password パスワード
	 * </pre>
	 */
	public void setPassword(String password) {

		this.password = password;
	}

//	2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
	/**
	 * <pre>
	 * [説明] cookie状態を設定する。
	 * @param cookieState cookie状態
	 * </pre>
	 */
	public void setCookieState(String cookieState) {
		this.cookieState = cookieState;
	}
//	2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
	/**
	 * <pre>
	 * [説 明] ログインサービス対象を設定する。
	 * @param ukg00010Services サービス対象
	 * </pre>
	 */
	public void setUkg00010Services(IUKG00010Service ukg00010Services) {

		this.ukg00010Services = ukg00010Services;
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
//	2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
	/**
	 * <pre>
	 * [説 明] ログイン処理を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {

		Util.getSqlComnLog("jp.co.token.uketuke.sqlmap.UKG00010.userjyouhouList");

		// 管理者パスワードを取得
		String managerPw = PropertiesReader.getIntance().getProperty("kanriPassword");

		// 2019/06/06  鈴木 UPD START C44009_管理者パスワード利用停止
//		// 入力したパスワードが管理者パスワードと比較して
//		if (managerPw.equals(password)) {
//			// LDAP認証なし
//			// INFOログを出力する
//			log.info("ログイン時刻:" + Util.getSysDate() + " ユーザID：" + txtUserID + "が管理用パスワードでログインしました。");
//
//		} else {
//			// 入力したパスワードが管理者パスワード以外の場合、LADP認証
//			String errorMsg = ukg00010Services.checkUserId(txtUserID, password);
//
//			if (!Util.isEmpty(errorMsg)) {
//				ukg00010Data.put("errorMsg", errorMsg);
//				return "login";
//			}
//
//			// INFOログを出力する
//			log.info("ログイン時刻:" + Util.getSysDate() + " ユーザID：" + txtUserID + "がログインしました。");
//		}
//
//		// 会社コードを取得
//		if (Util.isEmpty(kaisyaCd)) {
//			kaisyaCd = PropertiesReader.getIntance().getProperty("honbanKaisyaCd");
//		}
//		// ログイン者の基本情報リスト取得する
//		Map<String, Object> userjyouhouList = ukg00010Services.getUserJyouhouList(kaisyaCd, txtUserID);

		// 会社コードを取得
		if (Util.isEmpty(kaisyaCd)) {
			kaisyaCd = PropertiesReader.getIntance().getProperty("honbanKaisyaCd");
		}

		Map<String, Object> userjyouhouList;

		// 入力したパスワードが管理者パスワードと比較して
		if (managerPw.equals(password)) {
			// LDAP認証なし
			// ログイン者の基本情報リスト取得する
			userjyouhouList = ukg00010Services.getUserJyouhouList(kaisyaCd, txtUserID);
			//2019/09/12 佐藤 ADD START 不具合対応
			// INFOログを出力する
			log.info("ログイン時刻:" + Util.getSysDate() + " ユーザID：" + txtUserID + "が管理用パスワードでログインしました。");
			//2019/09/12 佐藤 ADD END 不具合対応
		} else if (!kaisyaCd.equals(PropertiesReader.getIntance().getProperty("honbanKaisyaCd"))) {
			// ログイン者の基本情報リスト取得する
			userjyouhouList = ukg00010Services.getUserJyouhouList2(kaisyaCd, txtUserID,password);
		} else {
			// 入力したパスワードが管理者パスワード以外の場合、LADP認証
			String errorMsg = ukg00010Services.checkUserId(txtUserID, password);

			if (!Util.isEmpty(errorMsg)) {
				ukg00010Data.put("errorMsg", errorMsg);
				return "login";
			}
			// ログイン者の基本情報リスト取得する
			userjyouhouList = ukg00010Services.getUserJyouhouList(kaisyaCd, txtUserID);

			//2019/09/12 佐藤 ADD START 不具合対応
			CookieNinsyoResponseBean responseBean = confirmLoginsessionKey();
			if (responseBean == null) {
				// INFOログを出力する
				log.info("ログイン時刻:" + Util.getSysDate() + " ユーザID：" + txtUserID + "がログインしました。");

				if(cookieState.equals("1")) {
					// Cookie認証システム(ログイン情報登録)を呼び出し
					CookieNinsyoResponseBean cookieNinsyoResponse = getCookieNinsyoResult(Consts.REQUEST_C001, null);
					// クッキー情報の格納を行う。
					setLoginSessionKeyCookie(cookieNinsyoResponse);
				}
			}else{
				txtUserID = responseBean.getShainCd();
			}
		}
		// 2019/06/06  鈴木 UPD END C44009_管理者パスワード利用停止

//		2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応

		//2019/09/12 佐藤 DEL START 不具合対応
		// ログイン情報確認
		//CookieNinsyoResponseBean responseBean = confirmLoginsessionKey();
		// 認証成功の場合
		//if (responseBean == null) {

			// 入力したパスワードが管理者パスワードと比較して
		//	if (managerPw.equals(password)) {
				// LDAP認証なし
				// INFOログを出力する
		//		log.info("ログイン時刻:" + Util.getSysDate() + " ユーザID：" + txtUserID + "が管理用パスワードでログインしました。");

		//	} else {
				// 入力したパスワードが管理者パスワード以外の場合、LADP認証
		//		String errorMsg = ukg00010Services.checkUserId(txtUserID, password);

		//		if (!Util.isEmpty(errorMsg)) {
		//			ukg00010Data.put("errorMsg", errorMsg);
		//			return "login";
		//		}

				// INFOログを出力する
		//		log.info("ログイン時刻:" + Util.getSysDate() + " ユーザID：" + txtUserID + "がログインしました。");

		//		if(cookieState.equals("1")) {
					// Cookie認証システム(ログイン情報登録)を呼び出し
		//			CookieNinsyoResponseBean cookieNinsyoResponse = getCookieNinsyoResult(Consts.REQUEST_C001, null);
					// クッキー情報の格納を行う。
		//			setLoginSessionKeyCookie(cookieNinsyoResponse);
		//		}
		//		}

		//}else {
		//	txtUserID = responseBean.getShainCd();
		//}
		//2019/09/12 佐藤 DEL END 不具合対応
//        2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応

		// ログイン情報をチェック
		List<Map<String, Object>> rstlist = (List<Map<String, Object>>) userjyouhouList.get("rst_list");
		if (rstlist == null || rstlist.size() == 0) { // 取得データがない場合、メッセージを表示して、ログイン画面へ遷移します
			ukg00010Data.put("errorMsg", Util.getServerMsg("ERRS001"));
			log.error("「" + txtUserID + "」対応のデータがありません。");
			return "login";

		}
//      2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
		// 2019/06/06  鈴木 UPD START C44009_管理者パスワード利用停止
		// INFOログを出力する
//		if (managerPw.equals(password)) {
//			log.info("ログイン時刻:" + Util.getSysDate() + " ユーザID：" + txtUserID + "が管理用パスワードでログインしました。");
//		} else {
//			log.info("ログイン時刻:" + Util.getSysDate() + " ユーザID：" + txtUserID + "がログインしました。");
//		}
		// 2019/06/06  鈴木 UPD START C44009_管理者パスワード利用停止
//      2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
		// 取得データをセッションのログイン情報に格納する
		Map<String, Object> loginInfo = rstlist.get(0);
		Map<String, Object> sessionLoginInfo = new HashMap<String, Object>();

		sessionLoginInfo.put(Consts.KAISYA_CD, kaisyaCd);
		sessionLoginInfo.put(Consts.POST_CD, loginInfo.get("POST_CD"));
		sessionLoginInfo.put(Consts.POST_NAME, loginInfo.get("POST_NAME"));
		sessionLoginInfo.put(Consts.USER_CD, loginInfo.get("TANTOU_CD"));
		sessionLoginInfo.put(Consts.LOGIN_USER_CD, txtUserID);
		sessionLoginInfo.put(Consts.USER_NAME, loginInfo.get("TANTOU_NAME"));
		// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		//役職区分
		sessionLoginInfo.put(Consts.YAKUSYOKU_KB_CD, loginInfo.get("YAKUSYOKU_KB_CD"));
		//出力順
		sessionLoginInfo.put(Consts.OUTPUT_ORDER, loginInfo.get("OUTPUT_ORDER"));
		//支社ブロックコード
		sessionLoginInfo.put(Consts.SISYA_BLK_CD, loginInfo.get("SISYA_BLK_CD"));
		String authorityCd = "0";//通常ユーザー
		// 2017/09/12  張虎強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
		/*if("2".equals(loginInfo.get("YAKUSYOKU_KB_CD"))
				&& "999".equals(loginInfo.get("OUTPUT_ORDER"))
				&& !Util.isEmpty((String) loginInfo.get("SISYA_BLK_CD"))){
			authorityCd = "1";//ブロックユーザー
		}
		if (ukg00010Services.checkPostTantou((String) loginInfo.get("POST_CD"))) {
			authorityCd = "2";//本社担当者ユーザー
		}*/
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
		// 2017/09/12  張虎強 UPD END (案件C41004)法人仲介担当者登録機能追加対応
		sessionLoginInfo.put(Consts.USER_AUTHORITY_CD, authorityCd);
		// ########## 2014/06/23 趙雲剛 ADD End
		String jisJusyoCd = String.valueOf(loginInfo.get("JIS_JUSYO_CD"));

		if (!Util.isEmpty(jisJusyoCd)) {
			sessionLoginInfo.put(Consts.POST_JUSYO_CD, jisJusyoCd.substring(0, 2));
			sessionLoginInfo.put(Consts.POST_SHIKUGUN_CD, jisJusyoCd.substring(2, 5));
		// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		} else {
			sessionLoginInfo.put(Consts.POST_JUSYO_CD, Consts.DEFAULT_POST_JUSYO_CD.substring(0, 2));
			sessionLoginInfo.put(Consts.POST_SHIKUGUN_CD, Consts.DEFAULT_POST_JUSYO_CD.substring(2, 5));
		// ########## 2014/06/23 趙雲剛 ADD End
		}
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		// セッションID表示
		log.info("sessionId:" + session.getId());
		session.setAttribute(Consts.LOGIN_INFO, sessionLoginInfo); // parasoft-suppress PB.API.ONS "36014JTest対応"

		// 該当画面のトークンを設定
		ukg00010Data.put("requestToken", session.getAttribute(Consts.REQUEST_TOKEN));

		String chukaitantouCd = null;

		ukg00010Data.put("errMsg", "");
		// リクエスト情報.画面IDが指定されていない場合
		if (Util.isEmpty(pageId)) {

			pageId = "UKG01010";

		} else {

			// 受付№判定
			Map<String, Object> restInfo = ukg00010Services.getUketukeList(kaisyaCd, uketukeNo);
			List<Map<String, Object>> restList = (List<Map<String, Object>>) restInfo.get("rst_list");

			if (restList.size() > 0) {

				chukaitantouCd = String.valueOf(restList.get(0).get("CHUKAI_TANTOU_CD"));
				// マスタに存在していない場合、トップページに遷移する
				if (!ukg00010Services.checkPageId(pageId)) {
					pageId = "UKG01010";
				} else {
					// 受付番号をセッションに格納する
					session.setAttribute(Consts.CHUKAITANTOU, chukaitantouCd);
					// 受付番号をセッションに格納する
					session.setAttribute(Consts.UKETUKENO, uketukeNo);
				}

			} else {
				pageId = "UKG01010";
				ukg00010Data.put("errMsg", "ERRC003");
			}
		}
		// 該当画面のトークンを設定
		ukg00010Data.put("pageId", pageId);
		return Action.SUCCESS;
	}

	/**
	 * <pre>
	 * [説 明] ログアウト処理を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String logOut() {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
//		2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
		loginOutCookies();
//		2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
		return "logOut";
	}
//	2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
	/** <pre>
     * [機 能] ログインセッションキー削除処理を行う。
     * [説 明] ・Cookie認証サーバにログインセッションキーの削除処理を要求します
     *         ・Cookieに保持しているログインセッションキーをクリアします
     * [備 考]
     * @return アクション処理結果
     * </pre>
     */
    private void loginOutCookies() {
        // セッションキーと結果コードを取得
        Cookie cookies[] = getRequest().getCookies();
        String loginSessionKey = Util.getCookie(cookies, Consts.COOKIE_LOGIN_SESSION_KEY);
        if (!Util.isEmpty(loginSessionKey)) {
            // Cookie認証システム(ログイン情報削除)を呼び出し
            getCookieNinsyoResult(Consts.REQUEST_C003, loginSessionKey);
            // Cookieに保持しているログインセッションキーをクリアします。
            HttpServletResponse response = ServletActionContext.getResponse();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    // ログインセッションキーの格納されているCookieを削除します。
                    if (Consts.COOKIE_LOGIN_SESSION_KEY.equals(cookie.getName())) {
                        cookie.setMaxAge(0);
                        cookie.setValue("");
                        response.addCookie(cookie);
                    }
                }
            }
        }
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
            CookieNinsyoRequestBean requestBean = null;
            if(Consts.REQUEST_C003.equals(requestCode)) {
            	requestBean = Util.getRequestBean(requestCode, null, loginSessionKey);
            }else {
            	requestBean = Util.getRequestBean(requestCode, txtUserID, loginSessionKey);
            }
            // Cookie認証システムの処理をCallする
            responseBean = cookieApiService.callCookieNisnsyo(currentUrl, requestBean, CookieNinsyoResponseBean.class);
        } catch (Exception e) {
            log.error("Cookie認証システム呼出でエラー", e);
        }

        return responseBean;
    }
    /**
     * <pre>
      * [機 能] クッキー情報の格納を行う。
      * [説 明] クッキー情報の格納。
      * [備 考]
      * @param cookieNinsyoResponse Cookie認証結果
     * </pre>
     */
    private void setLoginSessionKeyCookie(CookieNinsyoResponseBean cookieNinsyoResponse) {
        if (cookieNinsyoResponse == null) {
            return;
        }
        HttpServletResponse response = ServletActionContext.getResponse();
        // Cookieの保持期限（単位：日）
        int cookieDeadline = Integer.parseInt(PropertiesReader.getIntance().getProperty(Consts.API_COOKIENINSYO_COOKIEDEADLINE));
        int setTime = 60 * 60 * 24 * cookieDeadline;
        // ログインセッションキー取得
        String loginSessionKey = cookieNinsyoResponse.getSessionKey();
        Cookie cookieNinsyo = new Cookie(Consts.COOKIE_LOGIN_SESSION_KEY, loginSessionKey);
        // 発行日から1ヶ月間の有効期限を設定
        cookieNinsyo.setMaxAge(setTime);
        response.addCookie(cookieNinsyo);
    }
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
//    2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
	/**
	 * システムのタイトルをプの名前をセット
	 * @return
	 */
	public String selectSystemTitleName() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		// システムのタイトルをプの名前をセット
		systemTitleName = Util.getSystemTitleName();
		session.setAttribute("systemTitleName", systemTitleName);
		return "ajaxProcess";
	}

	public void setSystemTitleName(String systemTitleName) {
		this.systemTitleName = systemTitleName;
	}
	public String getSystemTitleName() {
		return systemTitleName;
	}


}
