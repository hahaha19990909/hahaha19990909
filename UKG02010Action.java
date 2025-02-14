/**
 * @システム名: 受付システム
 * @ファイル名: UKG02010Action.java
 * @更新日付：: 2012/3/7
 * @Copyright: 2011 token corporation All right reserved
 * 更新履歴： 2014/06/10	郭凡(SYS)       1.03  (案件番号C38117)セールスポイント編集機能の追加
 * 更新履歴： 2015/09/18	李暁輝(SYS)     1.04   (C39080-3)入居申込書の改訂－STEP2
 *  更新履歴：2017/08/02	MJEC)長倉       1.05  (案件C38101-1)ホームメイト自動受付対応
 * 更新履歴： 2017/09/22	ヨウ強    1.06    (案件C41004)法人仲介担当者登録機能追加対応
 * 更新履歴： 2018/01/03   朱珂(SYS)    1.07    案件C42036デザイン変更新規作成
 * 更新履歴： 2018/11/01   蒋陽(SYS)    1.08    案件C43040法人の入力項目追加対応
 * 更新履歴：  2020/04/07   楊朋朋(SYS)    1.09 C43017_見積書改修      
 * 更新履歴： 2020/05/06    劉恒毅(SYS)  1.10  C43040-2_法人の入力項目追加対応
 */
package jp.co.token.uketuke.action;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.activation.FileTypeMap;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.DispFormat;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.UnicodeInputStream;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.exception.DataNotFoundException;
import jp.co.token.uketuke.formbean.AnketoDispData;
import jp.co.token.uketuke.formbean.AnketoDispDataBig;
import jp.co.token.uketuke.service.IUKG02010Service;
import jp.co.token.uketuke.service.IUKG02020Service;

/**
 * <pre>
 * [機 能] お客様情報
 * [説 明] お客様情報を入力、登録する。
 * @author [作 成] 2011/10/21 SYS_賈
 * [修 正] by 趙 2012/3/7 STEP2対応
 * </pre>
 */
public class UKG02010Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 6185070289925736585L;

	/** お客様情報画面用データ */
	private Map<String, Object> ukg02010Data = new HashMap<String, Object>();

	/** お客様情報サービス */
	private IUKG02010Service UKG02010Services;

	// 2018/01/03 ADD START 朱珂(SYS) 案件C42036デザイン変更新規作成
	/** アンケート用画面のデータ */
    private Map<String, Object> ukg02020Data = null;

	/** アンケートサービス */
    private IUKG02020Service UKG02020Services;
    // 2018/01/03 ADD END 朱珂(SYS) 案件C42036デザイン変更新規作成

    //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
    //「添付資料」変更用
    private String fileMailStatus = "" ;
    //「法人仲介依頼連絡事項」の変更フラグ
    public boolean houjinRenrakuJikoFlag = false ;
    //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
	/**
	 * <pre>
	 * [説 明] 初期処理。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);

		// お客様情報取得
		getCustomerData();

		// アンケートデータを設定
		setAnketoData();
		// 2018/01/03 ADD START 朱珂(SYS) 案件C42036デザイン変更新規作成
		// アンケートデータを取得
		getAnketoData();
		// 2018/01/03 ADD END 朱珂(SYS) 案件C42036デザイン変更新規作成
		//2017/08/02 MJEC)長倉 ADD Start (案件C38101-1)ホームメイト自動受付対応
		//担当者コードを取得
		Map<String, Object> loginInfo = getLoginInfo();
		ukg02010Data.put("TANTOU_CD", loginInfo.get(Consts.USER_CD));
		//2017/08/02 MJEC)長倉 ADD End

		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] 顧客データ取得
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchKokyakuData() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> loginInfo = getLoginInfo();

		// 会社コード
		String kaisyaCd = (String) loginInfo.get(Consts.KAISYA_CD);
		// 顧客№
		String kokyakuNo = request.getParameter("kokyakuNo");

		// 顧客データ取得
		Map<String, Object> anketoData = UKG02010Services.getKokyakuData(kaisyaCd, kokyakuNo);

		List<Map<String, Object>> pcurList = (List<Map<String, Object>>) anketoData.get("rst_pcurList");
		if (pcurList != null && pcurList.size() > 0) {

			ukg02010Data = pcurList.get(0);
		}

		ukg02010Data.put("NOW_TIME", toGetSysDate());

		return "ajaxProcess";
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

		// 都道府県コードより、市区郡リストを取得する。
		if (!Util.isEmpty(jusyo1)) {
			ukg02010Data.put("JUSYO2_LIST", UKG02010Services.getJusyo2Data(jusyo1));
		} else {
			ukg02010Data.put("JUSYO1_LIST", null);
		}
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 町・大字データ取得
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchMatiData() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg02010Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));
		// 県コード
		String jusyo1 = request.getParameter("jusyo1");
		// 市コード
		String jusyo2 = request.getParameter("jusyo2");
		// 町・大字
		String town = new String(request.getParameter("q").getBytes("ISO8859-1"), "UTF-8");

		// 町・大字データ取得
		List<Map<String, Object>> list = (List<Map<String, Object>>) UKG02010Services.getJusyo3Data(jusyo1, jusyo2,
		                                                                                            town).get("rst_town_list");

		// 町・大字データを取得する。
		ukg02010Data.put("JUSYO3_LIST", Util.chkDropDownList(list, "YUBIN_NO"));

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 郵便番号の入力により住所を検索する
	 * @param yubinNo 郵便番号
	 * @return 住所データ
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchJusyoInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg02010Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));
		// 郵便番号
		String yubinNo = request.getParameter("q");

		// 郵便番号の入力により住所を検索する
		List<Map<String, Object>> list = (List<Map<String, Object>>) UKG02010Services.getJusyoInfo(yubinNo).get("rst_jusyo_list");

		// 住所データ
		ukg02010Data.put("JUSYO_LIST", Util.chkDropDownList(list, "YUBIN_NO"));

		return "ajaxProcess";
	}
    //  2020/04/07  楊朋朋(SYS) ADD Start C43017_見積書改修      
    /**
     * <pre>
     * [説 明] 法人企業マスタ取得
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    public String searchkigyocdData() throws Exception {

        HttpServletRequest request = ServletActionContext.getRequest();
        // 法人企業
        String kigyoCd = request.getParameter("KigyoCd");
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> kigyoInfoList = (List<Map<String, Object>>) UKG02010Services.getKigyoData(kigyoCd).get("out_kigyoInfo");
        // 法人企業取得
        if(kigyoInfoList.size()>0){
            ukg02010Data.put("kigyoInfo", kigyoInfoList.get(0));
        }

        return "ajaxProcess";
    }
    //  2020/04/07  楊朋朋(SYS) ADD End C43017_見積書改修      
	/**
	 * <pre>
	 * [説 明] データの更新処理
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String saveData() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		// ########## 2014/06/10 郭凡 ADD End
		// 画面のデータを転変する
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Set<Entry<String, Object>> set = request.getParameterMap().entrySet();
		// DEL START 2012/3/7 STEP2対応 // parasoft-suppress UC.ACC "36029JTest対応"
		// for (Iterator<Entry<String, Object>> it = set.iterator(); it.hasNext();) { // parasoft-suppress UC.ACC "36029JTest対応"
		// Map.Entry<String, Object> entry = (Entry<String, Object>) it.next(); // parasoft-suppress UC.ACC "36029JTest対応"
		// String value = ((String[]) entry.getValue())[0];// parasoft-suppress UC.ACC "36029JTest対応"
		// if(value == null || "".equals(value)){// parasoft-suppress UC.ACC "36029JTest対応"
		// entry.setValue(null);// parasoft-suppress UC.ACC "36029JTest対応"
		// }else{// parasoft-suppress UC.ACC "36029JTest対応"
		// entry.setValue(value);// parasoft-suppress UC.ACC "36029JTest対応"
		// }// parasoft-suppress UC.ACC "36029JTest対応"
		// }// parasoft-suppress UC.ACC "36029JTest対応"
		// DEL END 2012/3/7 STEP2対応// parasoft-suppress UC.ACC "36029JTest対応"
		// ADD START 2012/3/7 案件36029 STEP2対応// parasoft-suppress UC.ACC "36029JTest対応"
		for (Entry<String, Object> entry : set) {
			String value = ((String[]) entry.getValue())[0];
			entry.setValue(value);
		}
		// ADD END 2012/3/7 案件36029 STEP2対応
		dataMap.putAll(request.getParameterMap());

		// 会社コード
		dataMap.put("pv_kaisya_cd", loginInfo.get(Consts.KAISYA_CD));
		// 受付No.
		dataMap.put("pv_uketuke_no", session.getAttribute(Consts.UKETUKENO));
		// 受付店舗コード // parasoft-suppress UC.ACC "C39080-3Jtest対応"
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加 // parasoft-suppress UC.ACC "C39080-3Jtest対応"
		//dataMap.put("pv_uketuke_post_cd", loginInfo.get(Consts.POST_CD)); // parasoft-suppress UC.ACC "C39080-3Jtest対応"
		dataMap.put("pv_uketuke_post_cd", uketukePostInfo.get(Consts.POST_CD));
		// ########## 2014/06/10 郭凡 UPD End
		//2017/08/02 MJEC)長倉 UPD Start (案件C38101-1)ホームメイト自動受付対応
		// 受付担当者コード
		//顧客情報登録フラグが"0"かつ情報作成区分が"1"か"2"の場合、リストで選択した仲介担当者を設定
		// 2017/09/22 ヨウ強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
		if((dataMap.get("pv_kokyaku_info_flg").equals("0") && (dataMap.get("sakuseiKb").equals("1") || dataMap.get("sakuseiKb").equals("2"))) || ("1".equals((String) dataMap.get("pv_tantou_flg")))){
			dataMap.put("pv_uketuke_tantou_cd", dataMap.get("pv_chukai_tantou_cd"));
		} else {
			//2017/08/02 MJEC)長倉 UPD Start 不具合修正(受付担当者が設定されている場合は受付担当者コードを更新しないよう修正)
			//dataMap.put("pv_uketuke_tantou_cd", loginInfo.get(Consts.USER_CD));
			dataMap.put("pv_uketuke_tantou_cd", dataMap.get("pv_uketuke_tantou_cd"));
		}
		// 2017/09/22 ヨウ強 UPD END (案件C41004)法人仲介担当者登録機能追加対応
		//顧客情報登録フラグ
		dataMap.put("pv_kokyaku_info_flg", "1");
		//2017/08/02 MJEC)長倉 UPD End

		// 更新者
		dataMap.put("pv_upd_tantou_cd", loginInfo.get(Consts.USER_CD));

		// 2018/01/15 肖剣生 ADD Start (案件C42036)デザイン変更
        // リクエストデータを成型する。
        String reqStr = request.getParameter("requestString");
        Map<String, Object> a;
        if (Util.isEmpty(reqStr)) {
            a = new HashMap<String, Object>();
        } else {
            a = UKG02020Services.createRequestData(reqStr);
        } 

        ArrayList<AnketoDispData> returnAnketo = new ArrayList<AnketoDispData>();

        // アンケートフラグ取得
        String tourokuFlgAnketo = (String) dataMap.get("pv_anketo_flg");
        // 顧客情報フラグ取得
        String tourokuFlgKokyaku = "1";
        // 希望条件登録フラグ取得
        String tourokuFlgKibou = (String) dataMap.get("pv_kibou_flg");

        // セッション情報取得
        String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
        String tantouCd = String.valueOf(loginInfo.get(Consts.USER_CD));
        String uketukeNo = (String) session.getAttribute(Consts.UKETUKENO);
        uketukeNo = uketukeNo.replaceAll("-", "");

        // アンケート挿入用データ作成
        List<Map<String, Object>> insertData;
        if (Util.isEmpty(reqStr)) {
            insertData = new ArrayList<Map<String, Object>>();
            Map<String,Object> anketoData = new HashMap<String,Object>();
            anketoData.put("KAISYA_CD", kaisyaCd);
            anketoData.put("UKETUKE_NO", uketukeNo);
            Calendar cal1 = Calendar.getInstance(); 
            Date tm = cal1.getTime();
            anketoData.put("APP_DATE",tm);
            anketoData.put("APP_TANTOU_CD", tantouCd);
            anketoData.put("UPD_DATE",tm);
			anketoData.put("UPD_TANTOU_CD",tantouCd);
            insertData.add(anketoData);
        } else {
            insertData = UKG02020Services
                    .makeAnketoData(a, returnAnketo, kaisyaCd, uketukeNo,
                            tantouCd);
        } 

        // ukg02010、ukg02020を更新する
        Map<String, Object> anketoData = UKG02010Services.updUketukeData(dataMap, insertData, tourokuFlgAnketo,
        		tourokuFlgKokyaku, tourokuFlgKibou);

        // ukg02010、ukg02020処理結果チェック
        // 更新日時
		ukg02010Data.put("UPD_DATE", anketoData.get("pv_rst_upd_date"));
		// 進捗状況コード
		ukg02010Data.put("SINCHOKU_CD", anketoData.get("pv_rst_sinchoku_cd"));

		// 更新して、メッセージ処理
		if ("ERRS003".equals(anketoData.get("rst_code"))) {
			ukg02010Data.put("MSG", Util.getServerMsg("ERRS003"));
			ukg02010Data.put("RESULT", "FAIL");

		} else if ("ERRS999".equals(anketoData.get("rst_code")) || "1111111".equals(anketoData.get("rst_code"))) {
			ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "更新処理"));
			ukg02010Data.put("RESULT", "FAIL");

		} else if ("1".equals(anketoData.get("ukg02020err"))) {
			ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "更新処理"));
			ukg02010Data.put("RESULT", "SUCCESS");

		} else {
			// 進捗状況リスト
			Map<String, Object> sinchokuData = UKG02010Services.getSinchokuData((String) anketoData.get("pv_rst_sinchoku_cd"));
			ukg02010Data.put("SINCHOKU_LIST", (List<Map<String, Object>>) sinchokuData.get("rst_sinchoku_list"));
			// 一部アンケート情報を取得する
			anketoDataProcess((String) loginInfo.get(Consts.KAISYA_CD),
			                  String.valueOf(session.getAttribute(Consts.UKETUKENO)));

			session.setAttribute(Consts.CHUKAITANTOU, String.valueOf(dataMap.get("pv_chukai_tantou_cd")));

			ukg02010Data.put("MSG", Util.getServerMsg("MSGS001", "登録"));
			ukg02010Data.put("RESULT", "SUCCESS");
		}
		// 2018/01/15 肖剣生 ADD End (案件C42036)デザイン変更

        // 2018/01/15 肖剣生 DEL START (案件C42036)デザイン変更
		/*// 受付データ更新
		Map<String, Object> anketoData = UKG02010Services.updUketukeData(dataMap);
		// 更新日時
		ukg02010Data.put("UPD_DATE", anketoData.get("pv_rst_upd_date"));
		// 進捗状況コード
		ukg02010Data.put("SINCHOKU_CD", anketoData.get("pv_rst_sinchoku_cd"));

		// 更新して、メッセージ処理
		if ("ERRS003".equals(anketoData.get("rst_code"))) {
			ukg02010Data.put("MSG", Util.getServerMsg("ERRS003"));
			ukg02010Data.put("RESULT", "FAIL");
		} else if ("ERRS999".equals(anketoData.get("rst_code")) || "1111111".equals(anketoData.get("rst_code"))) {
			ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "更新処理"));
			ukg02010Data.put("RESULT", "FAIL");
		} else {
			// 進捗状況リスト
			Map<String, Object> sinchokuData = UKG02010Services.getSinchokuData((String) anketoData.get("pv_rst_sinchoku_cd"));
			ukg02010Data.put("SINCHOKU_LIST", (List<Map<String, Object>>) sinchokuData.get("rst_sinchoku_list"));
			// 一部アンケート情報を取得する
			anketoDataProcess((String) loginInfo.get(Consts.KAISYA_CD),
			                  String.valueOf(session.getAttribute(Consts.UKETUKENO)));

			session.setAttribute(Consts.CHUKAITANTOU, String.valueOf(dataMap.get("pv_chukai_tantou_cd")));
			ukg02010Data.put("MSG", Util.getServerMsg("MSGS001", "登録"));
			ukg02010Data.put("RESULT", "SUCCESS");
		}*/
		// 2018/01/15 肖剣生 DEL END (案件C42036)デザイン変更

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] お客様情報取得
	 * </pre>
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void getCustomerData() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		// ログイン情報
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		// 会社コード
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		// 受付No.
		String uketukeno = null;

		// 2018/01/03 ADD START 朱珂(SYS) 案件C42036デザイン変更新規作成
		// ユーザー登録権限コード
		String userAuthorityCd = String.valueOf(sessionLoginInfo.get(Consts.USER_AUTHORITY_CD));
        ukg02010Data.put("USER_AUTHORITY_CD", userAuthorityCd);
        // 2018/01/03 ADD END 朱珂(SYS) 案件C42036デザイン変更新規作成

		// 2017/09/22 ヨウ強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
		// 引数.法人仲介依頼受付日時
		String sysdate = (String) session.getAttribute("sysdate");
		// 引数.受付店舗名称
		String postName = (String) session.getAttribute("postnm");
		// 引数.仲介担当者コード
		String tyukaitantoucd = (String) session.getAttribute("tyukaitantoucd");
		// 引数.企業コード
		String kigyoucd = (String) session.getAttribute("kigyoucd");
		// 2017/09/22 ヨウ強 ADD END (案件C41004)法人仲介担当者登録機能追加対応

		// 画面区分
		if ("UKG01011".equals(request.getParameter("historyPageId"))) {
			// 遷移前画面が受付未登録一覧
			uketukeno = request.getParameter("uketukeNo");
			ukg02010Data.put("PAGE_FLAG", request.getParameter("historyPageId"));
			// セッションに受付未登録一覧遷移フラグを設定する。
			session.setAttribute(Consts.UKETUKENO, uketukeno);
			session.setAttribute(Consts.UKG01011_FLG, "UKG01011");
	    //########## 2017/08/10 MJEC)鈴村 ADD Start (案件C38101-1)ホームメイト自動受付対応
		} else if ("UKG01016".equals(request.getParameter("historyPageId"))) {
			// 遷移前画面がホームメイト自動受付一覧
			uketukeno = request.getParameter("uketukeNo");
			ukg02010Data.put("PAGE_FLAG", request.getParameter("historyPageId"));
			// セッションにホームメイト自動受付一覧遷移フラグを設定する。
			session.setAttribute(Consts.UKETUKENO, uketukeno);
			session.setAttribute(Consts.UKG01016_FLG, "UKG01016");
        //########## 2017/08/10 MJEC)鈴村 ADD End
		} else {
			uketukeno = (String) session.getAttribute(Consts.UKETUKENO);
		}

		//########## 2017/08/10 MJEC)鈴村 UPD Start (案件C38101-1)ホームメイト自動受付対応
		//if ("UKG01011".equals(session.getAttribute(Consts.UKG01011_FLG))) {
		if ("UKG01011".equals(session.getAttribute(Consts.UKG01011_FLG)) || "UKG01016".equals(session.getAttribute(Consts.UKG01016_FLG))) {
		//########## 2017/08/10 MJEC)鈴村 UPD End
			ukg02010Data.put("PAGE_FLAG", request.getParameter("historyPageId"));
		}

		// 2017/09/22 ヨウ強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
		// 引数.受付№が空白且つセッション情報.受付№が空白
		String uketukenoFlag = "1";
		if (uketukeno == null || "".equals(uketukeno)) {
			uketukenoFlag = "0";
		}
		// 2017/09/22 ヨウ強 ADD END (案件C41004)法人仲介担当者登録機能追加対応

		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		// 店舗コード // parasoft-suppress UC.ACC "C39080-3Jtest対応"
//		String postCd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD)); // parasoft-suppress UC.ACC "C39080-3Jtest対応"
		String postCd = String.valueOf(uketukePostInfo.get(Consts.POST_CD));
		// ########## 2014/06/10 郭凡 UPD End

		// お客様情報取得
		// 2017/09/22 ヨウ強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
//		List<Map<String, Object>> customerInfo = (List<Map<String, Object>>) UKG02010Services.getCustomerData(kaisyacd,
//				uketukeno).get("rst_pcurList");
		List<Map<String, Object>> customerInfo = null;
		if ("0".equals(uketukenoFlag)) {
			customerInfo = (List<Map<String, Object>>) UKG02010Services.getCustomerInfo(kigyoucd).get("rst_pcurList");
		} else {
			customerInfo = (List<Map<String, Object>>) UKG02010Services.getCustomerData(kaisyacd, uketukeno).get(
					"rst_pcurList");
		}
		// 2017/09/22 ヨウ強 UPD END (案件C41004)法人仲介担当者登録機能追加対応

		// 進捗コード
		String sinchokuCd = null;

		if (customerInfo != null && customerInfo.size() > 0) {

			ukg02010Data = customerInfo.get(0);

			// 都道府県コードより、市区郡リストを取得する。
			String jisJusyoCd = (String) ukg02010Data.get("JIS_JUSYO_CD");
			if (jisJusyoCd != null) {

				ukg02010Data.put("JUSYO2_LIST", UKG02010Services.getJusyo2Data(jisJusyoCd.substring(0, 2)));
			} else {

				ukg02010Data.put("JUSYO1_LIST", null);
			}

			sinchokuCd = (String) ukg02010Data.get("SINCHOKU_CD");
			// 2017/09/22 ヨウ強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
			if (sinchokuCd == null || "".equals(sinchokuCd)) {
				sinchokuCd = "0";
			}
			// 2017/09/22 ヨウ強 UPD END (案件C41004)法人仲介担当者登録機能追加対応

			// 顧客No.をフォーマットする。
			String kokyakuNo = (String) ukg02010Data.get("KOKYAKU_CD");
			ukg02010Data.put("KOKYAKU_CD", DispFormat.getKokyakuNo(kokyakuNo));


			//########## 2015/09/18 李暁輝 ADD  START  (C39080-3)入居申込書の改訂－STEP2

			// 勤務先都道府県コードより、市区郡リストを取得する。
			String KinmusakiJisJusyoCd = (String) ukg02010Data.get("KINMUSAKI_JIS_JUSYO_CD");
			if (KinmusakiJisJusyoCd != null) {

				ukg02010Data.put("KINMUSAKI_JUSYO2_LIST", UKG02010Services.getJusyo2Data(KinmusakiJisJusyoCd.substring(0, 2)));
			} else {

				ukg02010Data.put("KINMUSAKI_JUSYO2_LIST", null);
			}

			if(!Util.isEmpty((String)ukg02010Data.get("KINZOKU_NENSU"))){
				// 勤務先勤続年数年
				int intKinzokuNensu = Integer.valueOf((String)ukg02010Data.get("KINZOKU_NENSU"))/12;
				if(intKinzokuNensu == 0){
					//勤務先勤続年数年
					ukg02010Data.put("KINZOKU_NEN", String.valueOf(""));
				}else{
					//勤務先勤続年数年
					ukg02010Data.put("KINZOKU_NEN", String.valueOf(intKinzokuNensu));
				}

				int intKinzokuGatusu = Integer.valueOf((String)ukg02010Data.get("KINZOKU_NENSU"))%12;
				if(intKinzokuGatusu == 0){
					//勤務先勤続年数月
					ukg02010Data.put("KINZOKU_GATUSU", String.valueOf(""));
				}else{
					//勤務先勤続年数月
					ukg02010Data.put("KINZOKU_GATUSU", String.valueOf(intKinzokuGatusu));
				}
			}else{
				// 勤務先勤続年数年
				ukg02010Data.put("KINZOKU_NEN", "");
				//勤務先勤続年数月
				ukg02010Data.put("KINZOKU_GATUSU", "");
			}

			//##########2015/09/18 李暁輝 ADD End (C39080-3)入居申込書の改訂－STEP2

			// 2017/09/22 ヨウ強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
			// 法人仲介依頼先の最大店舗数を取得する
			ukg02010Data.put("chukaiIraiMaxCount", PropertiesReader.getIntance().getProperty("chukaiIraiMaxCount"));

			if ("4".equals(ukg02010Data.get("UKETUKE_KB"))
					&& ("3".equals((String) ukg02010Data.get("SAKUSEI_KB")) || "4".equals((String) ukg02010Data
							.get("SAKUSEI_KB")))) {
				Map<String, Object> postData = UKG02010Services.getTempoInfo(kaisyacd,
						(String) ukg02010Data.get("HOUJIN_IRAIMOTO_UKETUKE_NO"));
				ukg02010Data.put("POST_LIST", (List<Map<String, Object>>) postData.get("kaisyaData"));
				int size = ((List<Map<String, Object>>) postData.get("kaisyaData")).size();
				ukg02010Data.put("POST_LIST_SIZE", size);
			}

			// 社宅斡旋依頼書情報を取得
			if ("4".equals(ukg02010Data.get("UKETUKE_KB")) || "0".equals(uketukenoFlag)) {
				ukg02010Data.put(
						"SYATAKUINFO_LIST",
						(List<Map<String, Object>>) UKG02010Services.getSyatakuInfo(kaisyacd, uketukeno).get(
								"rst_syatakuInforList"));

				// PDF保存可能サイズ（MB)
				ukg02010Data.put("pdfMaxSize", PropertiesReader.getIntance().getProperty("pdfMaxSize"));
				// PDF保存可能数
				ukg02010Data.put("pdfMaxCount", PropertiesReader.getIntance().getProperty("pdfMaxCount"));
				// PDFデータルートパス
				ukg02010Data.put("pdfRootPath", PropertiesReader.getIntance().getProperty("pdfRootPath"));
				// PDF一時フォルダ
				ukg02010Data.put("pdfTempFolder", PropertiesReader.getIntance().getProperty("pdfTempFolder"));
                //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
                // 添付資料保存可能ファイル拡張子
                ukg02010Data.put("shiryouSaveEnableExtent", PropertiesReader.getIntance().getProperty("shiryouSaveEnableExtent"));
                //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応

				int num1 = Integer.parseInt(PropertiesReader.getIntance().getProperty("pdfMaxCount"));
				int[] tempMaxList = new int[num1];
				ukg02010Data.put("tempMaxList", tempMaxList);
				int num2 = ((List<Map<String, Object>>) ukg02010Data.get("SYATAKUINFO_LIST")).size();
				int num3 = num1 - num2;
				int[] tempList = new int[num3];
				ukg02010Data.put("tempList", tempList);
			}
			// 2017/09/22 ヨウ強 ADD END (案件C41004)法人仲介担当者登録機能追加対応

		} else {

			throw new DataNotFoundException();
		}

		// 一部アンケート情報を取得する
		anketoDataProcess(kaisyacd, uketukeno);

		// 進捗状況リスト
		Map<String, Object> sinchokuData = UKG02010Services.getSinchokuData(sinchokuCd);
		ukg02010Data.put("SINCHOKU_LIST", (List<Map<String, Object>>) sinchokuData.get("rst_sinchoku_list"));

		// 仲介担当者リスト、都道府県リスト、生年月日リストを取得する
		Map<String, Object> otherListData = UKG02010Services.getOtherListData(kaisyacd, postCd);

		// 仲介担当者リスト 取得した仲介担当者コードを存在しない場合は、追加する
		List<Map<String, Object>> chukaiTantouList = (List<Map<String, Object>>) otherListData.get("rst_chukai_tantou_list");
		chukaiTantouContains(chukaiTantouList);
		ukg02010Data.put("CHUKAI_TANTOU_LIST", chukaiTantouList);

		// 都道府県リスト
		ukg02010Data.put("JUSYO1_LIST", (List<Map<String, Object>>) otherListData.get("rst_jusyo1_list"));

		// 生年月日リスト
		ukg02010Data.put("YMD_LIST", (List<Map<String, Object>>) otherListData.get("rst_ymd_list"));

		//########## 2015/9/18 李暁輝 ADD  START  (C39080-3)入居申込書の改訂－STEP2

		// 年齢層リスト
		ukg02010Data.put("NENREISOU_LIST", (List<Map<String, Object>>) otherListData.get("rst_nenreisou_list"));
		// 職業リスト
		ukg02010Data.put("SYOKUGYOUCD_LIST", (List<Map<String, Object>>) otherListData.get("rst_syokugyoucd_list"));

		//########## 2015/9/18 李暁輝 ADD END  (C39080-3)入居申込書の改訂－STEP2

		// 2017/09/22 ヨウ強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
		// 法人希望室数リスト
		if ("0".equals(uketukenoFlag)) {
			ukg02010Data.put("HOUJINKIBOUSHITSUSUU_LIST", (List<Map<String, Object>>) UKG02010Services
					.getShitsuSuuList().get("rst_houjinkiboushitsusuu_list"));
		}

		// 店舗名リストデータを取得
		ukg02010Data.put("TEMPONALIST",
				(List<Map<String, Object>>) UKG02010Services.getTempoNa(kaisyacd).get("rst_tempoNaList"));

		if (sysdate != null && !"".equals(sysdate)) {
			// 法人仲介依頼受付日時(日付)
			String houjinDate = sysdate.substring(0, 10).replace('-', '/');
			ukg02010Data.put("houjinDate", houjinDate);
			// 法人仲介依頼受付日時(時刻)
			String houjinTime = sysdate.substring(sysdate.length() - 8, sysdate.length() - 3);
			ukg02010Data.put("houjinTime", houjinTime);
		}

		ukg02010Data.put("uketukenoFlag", uketukenoFlag);

		String postTantou = "";
		if (postName != null && !"".equals(postName)) {
			postTantou = postName + "　（" + (String) sessionLoginInfo.get(Consts.USER_NAME) + "）";
		}
		ukg02010Data.put("postTantou", postTantou);

		String post_tantou = (String) ukg02010Data.get("POST_TANTOU");
		String tantou_name = (String) ukg02010Data.get("TANTOU_NAME");
		if (post_tantou != null && !"".equals(post_tantou) && (tantou_name == null || "".equals(tantou_name))) {
			String user_name = (String) sessionLoginInfo.get(Consts.USER_NAME);
			post_tantou = post_tantou + "　（" + user_name + "）";
			ukg02010Data.put("UKETUKE_TANTOU_NAME", user_name);
			ukg02010Data.put("POST_TANTOU", post_tantou);
			ukg02010Data.put("TANTOU_FLG", "1");
		}

		// 法人仲介担当者リスト取得
		ukg02010Data.put(
				"HOUJINTANTOU_LIST",
				(List<Map<String, Object>>) UKG02010Services.getHoujinTantouList(kaisyacd).get(
						"OUT_HOUJINTANTOULIST"));

		// 仲介担当者選択
		ukg02010Data.put("tyukaitantoucd", tyukaitantoucd);

		String chukai_tantou = (String) ukg02010Data.get("CHUKAI_TANTOU");
		if (chukai_tantou == null || "".equals(chukai_tantou)) {
			chukai_tantou = (String) sessionLoginInfo.get(Consts.USER_CD);
			ukg02010Data.put("CHUKAI_TANTOU", chukai_tantou);
		}

		// 2018/10/29 蒋陽 ADD START 案件C43040-1_法人の入力項目追加対応
		if ("1".equals(uketukenoFlag)) {
			ukg02010Data.put("KIGYO_NAME", (String) ukg02010Data.get("HOUJIN_KIGYO_NAME"));
			kigyoucd= (String) ukg02010Data.get("HOUJIN_KIGYOU_CD");
		}
		// 2018/10/29 蒋陽 ADD END 案件C43040-1_法人の入力項目追加対応
		ukg02010Data.put("kigyoucd", kigyoucd);
		// 2017/09/22 ヨウ強 ADD END (案件C41004)法人仲介担当者登録機能追加対応

		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		// 店舗所在都道府県コード
//		ukg02010Data.put("POST_JUSYO_CD", sessionLoginInfo.get(Consts.POST_JUSYO_CD));
		ukg02010Data.put("POST_JUSYO_CD", uketukePostInfo.get(Consts.POST_JUSYO_CD));
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		ukg02010Data.put("AUTHORITY_CD", authorityCd);
		// ########## 2014/06/10 郭凡 UPD End

		ukg02010Data.put("KAISYA_CD", kaisyacd);
		ukg02010Data.put("UKETUKE_NO", uketukeno);
		ukg02010Data.put("UKETUKE_NO_FROMAT", DispFormat.getUketukeNo(uketukeno));
		ukg02010Data.put("NOW_TIME", toGetSysDate());
		// 2017/09/22 ヨウ強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
		ukg02010Data.put("NOW_TIMEHM", toGetSysDateTime());
		// 2018/02/27 ADD START 朱珂(SYS) 案件C42036デザイン変更新規作成
        if (ukg02010Data.get("SAKUSEI_KB") == null) {
        	ukg02010Data.put("SAKUSEI_KB", "");
        }
        // 2018/02/27 ADD END 朱珂(SYS) 案件C42036デザイン変更新規作成
		// お客様情報画面用データはセッションを設定する
		session.setAttribute("UKG02010DATAMAP", ukg02010Data);
		// 2017/09/22 ヨウ強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
		
		// 2018/10/29 蒋陽 ADD START 案件C43040-1_法人の入力項目追加対応
		ukg02010Data.put("KIGYO_NAME_BK",(String) ukg02010Data.get("KIGYO_NAME"));
        //2020/05/06 ADD START 劉恒毅(SYS) C43040-2_法人の入力項目追加対応        
        ukg02010Data.put("HOUJIN_IRAI_TANTOU_CD",(String) ukg02010Data.get("HOUJIN_IRAI_TANTOU_CD") == null?"":(String) ukg02010Data.get("HOUJIN_IRAI_TANTOU_CD"));
        ukg02010Data.put("HOUJIN_IRAI_TANTOU_NAME",(String) ukg02010Data.get("HOUJIN_IRAI_TANTOU_NAME") == null?"":(String) ukg02010Data.get("HOUJIN_IRAI_TANTOU_NAME"));
        ukg02010Data.put("HOUJIN_IRAI_TEL_NO",(String) ukg02010Data.get("HOUJIN_IRAI_TEL_NO") == null?"":(String) ukg02010Data.get("HOUJIN_IRAI_TEL_NO"));
        //2020/05/06 ADD END 劉恒毅(SYS) C43040-2_法人の入力項目追加対応
		if(uketukenoFlag=="0")
		{
			ukg02010Data.put("KIGYO_NAME_KANA_BK",(String) ukg02010Data.get("KIGYO_NAME_KANA"));
			ukg02010Data.put("TEL_NO_BK",(String) ukg02010Data.get("TEL_NO"));
			ukg02010Data.put("FAX_NO_BK",(String) ukg02010Data.get("FAX_NO"));
			ukg02010Data.put("YUBIN_NO_BK",(String) ukg02010Data.get("YUBIN_NO"));
			ukg02010Data.put("JUSYO3_BK",(String) ukg02010Data.get("JUSYO3"));
			ukg02010Data.put("JUSYO4_BK",(String) ukg02010Data.get("JUSYO4"));

			//お客様名 空白
			ukg02010Data.put("KIGYO_NAME","");
			//お客様名カナ 空白
			ukg02010Data.put("KIGYO_NAME_KANA","");
			//入居予定者名 空白
			ukg02010Data.put("NYUKYOSYA_NAME","");
			//入居予定者名カナ 空白
			ukg02010Data.put("NYUKYOSYA_NAME_K","");
			//電話番号 空白
			ukg02010Data.put("TEL_NO","");
			//FAX番号 空白
			ukg02010Data.put("FAX_NO","");
            //郵便番号 空白
            ukg02010Data.put("YUBIN_NO","");
            //町・大字  空白
            ukg02010Data.put("JUSYO2_LIST",null);
            ukg02010Data.put("JUSYO3","");
            //丁・番地・マンション名  空白
            ukg02010Data.put("JUSYO4","");
		}
		// 2018/10/29 蒋陽 ADD END 案件C43040-1_法人の入力項目追加対応
	}

	/**
	 * <pre>
	 * [説 明] 一部アンケート情報を取得する
	 * @param kaisya_cd          会社コード
	 * @param uketuke_no         受付№
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	private void anketoDataProcess(String kaisyacd,
	                               String uketukeno) throws Exception {

		// 一部アンケート情報を取得する
		List<Map<String, Object>> anketoData = (List<Map<String, Object>>) UKG02010Services.getAnketoData(kaisyacd,
		                                                                                                  uketukeno).get("rst_anketo_list");
		if (anketoData != null && anketoData.size() > 0) {
			for (Map<String, Object> anketo : anketoData) {

				if ("25".equals(anketo.get("ANKETO_ID"))) {
					ukg02010Data.put("ANKETO_ID25", anketo.get("ANKETO_ANSWER"));
					ukg02010Data.put("APP_TANTOU_CD25", anketo.get("APP_TANTOU_CD"));
					ukg02010Data.put("APP_DATE25", anketo.get("APP_DATE"));
				} else if ("28".equals(anketo.get("ANKETO_ID"))) {
					ukg02010Data.put("ANKETO_ID28", anketo.get("ANKETO_ANSWER"));
					ukg02010Data.put("APP_TANTOU_CD28", anketo.get("APP_TANTOU_CD"));
					ukg02010Data.put("APP_DATE28", anketo.get("APP_DATE"));
				}
			}
		}
	}

	/**
	 * <pre>
	 * [説 明] 取得した仲介担当者コードを存在しない場合は、追加する
	 * @param chukaiTantouList 仲介担当者リスト
	 * </pre>
	 */
	private void chukaiTantouContains(List<Map<String, Object>> chukaiTantouList) {

		boolean blnHava = false;
		for (Map<String, Object> map : chukaiTantouList) {
			if (map.get("TANTOU_CD").equals(ukg02010Data.get("CHUKAI_TANTOU"))) {
				// 取得した仲介担当者コードを存在しない場合
				blnHava = true;
				break;
			}
		}
		if ((!blnHava) && (!Util.isEmpty((String) ukg02010Data.get("CHUKAI_TANTOU")))) {
			Map<String, Object> chukaiTantouMap = new HashMap<String, Object>();
			chukaiTantouMap.put("TANTOU_CD", ukg02010Data.get("CHUKAI_TANTOU"));
			chukaiTantouMap.put("TANTOU_NAME", ukg02010Data.get("CHUKAI_NAME"));
			chukaiTantouList.add(chukaiTantouMap);
		}
	}

	/**
	 * <pre>
	 * [説 明] アンケートデータを設定
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	private void setAnketoData() throws Exception {

		// アンケート項目名を取得する
		List<Map<String, Object>> anketoData = (List<Map<String, Object>>) UKG02010Services.getAnketomData().get("rst_pcurList");

		if (anketoData != null && anketoData.size() > 0) {

			for (Map<String, Object> anketo : anketoData) {
				ukg02010Data.put("SORT_TATE_"
				                     + anketo.get("DISP_SORT_TATE2") + "_YOKO_" + anketo.get("DISP_SORT_YOKO2"),
				                 anketo.get("ANKETO_NAIYOU2"));
			}
		}
	}

	/**
	 * <pre>
	 * [説 明] システム日付を取得する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	private static String toGetSysDate() {

		// システム日付を取得
		Calendar ca = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(Consts.FORMAT_DATE_YMD);
		return format.format(ca.getTime());
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを取得する。
	 * @return 画面のデータ
	 * </pre>
	 */
	public Map<String, Object> getUkg02010Data() {

		return ukg02010Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg02040Data 画面のデータ
	 * </pre>
	 */
	public void setUkg02010Data(Map<String, Object> ukg02010Data) {

		this.ukg02010Data = ukg02010Data;
	}

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param services サービス対象
	 * </pre>
	 */
	public void setUKG02010Services(IUKG02010Service services) {

		UKG02010Services = services;
	}

	// 2018/01/03 ADD START 朱珂(SYS) 案件C42036デザイン変更新規作成
	/**
     * サービス設定
     *
     * @param uKG02020Services
     */
    public void setUKG02020Services(IUKG02020Service uKG02020Services) {
        UKG02020Services = uKG02020Services;
    }

    /**
     * 画面データ取得
     *
     * @return　画面データ
     */
    public Map<String, Object> getUkg02020Data() {
        return ukg02020Data;
    }

    /**
     * 画面データ設定
     *
     * @param ukg02020Data
     */
    public void setUkg02020Data(Map<String, Object> ukg02020Data) {
        this.ukg02020Data = ukg02020Data;
    }
	// 2018/01/03 ADD END 朱珂(SYS) 案件C42036デザイン変更新規作成

	// 2017/09/22 ヨウ強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
	/**
	 * <pre>
	 * [説 明] データの更新処理(仲介依頼送信)
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String soushiShori() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();
		ukg02010Data = (Map<String, Object>) session.getAttribute("UKG02010DATAMAP");
		ukg02010Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));
		// 画面のデータを転変する
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Set<Entry<String, Object>> set = request.getParameterMap().entrySet();
		for (Entry<String, Object> entry : set) {
			String value = ((String[]) entry.getValue())[0];
			entry.setValue(value);
		}
		dataMap.putAll(request.getParameterMap());

		// 会社コード
		// 2018/03/15 H.Sato updated start (案件C41004)法人仲介担当者登録機能追加対応
		//String kaisya_cd = (String) loginInfo.get(Consts.KAISYA_CD);
		final String kaisya_cd = (String) loginInfo.get(Consts.KAISYA_CD);
		// 2018/03/15 H.Sato updated end (案件C41004)法人仲介担当者登録機能追加対応
		dataMap.put("pv_kaisya_cd", kaisya_cd);
		// 受付No.
		String uketuke_no = (String) session.getAttribute(Consts.UKETUKENO);
		dataMap.put("pv_uketuke_no", uketuke_no);
		// 店舗コード
		dataMap.put("pv_post_cd", session.getAttribute("postcd"));
		// 担当者コード
		String tantou_cd = (String) loginInfo.get(Consts.USER_CD);
		dataMap.put("pv_tantou_cd", tantou_cd);

		String uketukeNo = "";
		// 2018/03/15 H.Sato updated start (案件C41004)法人仲介担当者登録機能追加対応
		//if (uketuke_no == null || "".equals(uketuke_no)) {
		final boolean isUketuke_noEmpty = (uketuke_no == null || "".equals(uketuke_no));
		if (isUketuke_noEmpty) {
		// 2018/03/15 H.Sato updated end (案件C41004)法人仲介担当者登録機能追加対応
			// 受付№採番
			Map<String, Object> upInfo = UKG02010Services.getUketukeNo(kaisya_cd, tantou_cd);
			uketukeNo = (String) upInfo.get("pv_out_uketukeno");
			if ("ERRS999".equals(upInfo.get("rst_code"))) {
				ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "更新処理"));
				ukg02010Data.put("RESULT", "FAIL");
				return "ajaxProcess";
			} else if ("ERRS005".equals(upInfo.get("rst_code"))) {
				ukg02010Data.put("MSG", Util.getServerMsg("ERRS005"));
				return "ajaxProcess";
			}
		}
		dataMap.put("pv_moto_uketukeno", uketukeNo);

		// 社宅斡旋依頼書情報を取得
		List<Map<String, Object>> syatakuinfoList = (List<Map<String, Object>>) ukg02010Data.get("SYATAKUINFO_LIST");
		// PDFデータルートパス
		String pdfRootPath = PropertiesReader.getIntance().getProperty("pdfRootPath");
		// PDF一時フォルダ
		String pdfTempFolder = PropertiesReader.getIntance().getProperty("pdfTempFolder");
		// PDFデータを結合
		String pdfDataStr = "";

		// 2018/03/15 H.Sato added start (案件C41004)法人仲介担当者登録機能追加対応
		ExecutorService executor = Executors.newCachedThreadPool();
		// 2018/03/15 H.Sato added end (案件C41004)法人仲介担当者登録機能追加対応

		if (syatakuinfoList != null && syatakuinfoList.size() > 0) {
			int i = 0;
			Map<String, Object> syatakuinfo = new HashMap<String, Object>();
			while (i < syatakuinfoList.size()) {
				syatakuinfo = syatakuinfoList.get(i);
				String pdfStatus = (String) syatakuinfo.get("PDF_FILE_STATUS") == null ? "" : (String) syatakuinfo
						.get("PDF_FILE_STATUS");
				String pdfName = (String) syatakuinfo.get("PDF_FILE_NAME") == null ? "" : (String) syatakuinfo
						.get("PDF_FILE_NAME");
				String pdfPath = (String) syatakuinfo.get("PDF_FILE_PATH") == null ? "" : (String) syatakuinfo
						.get("PDF_FILE_PATH");
				String pdfSeq = (String) syatakuinfo.get("SYATAKU_PDF_SEQ") == null ? "" : (String) syatakuinfo
						.get("SYATAKU_PDF_SEQ");
				if ("2".equals(pdfStatus)) {
					pdfPath = pdfRootPath + pdfTempFolder + pdfPath;
					// 2018/03/15 H.Sato updated start (案件C41004)法人仲介担当者登録機能追加対応
					/*
					String tempDirPath = "";
					String UKETUKENO = "";
					if (uketuke_no == null || "".equals(uketuke_no)) {
						pdfSeq = String.valueOf(i + 1);
						UKETUKENO = uketukeNo;
					} else {
						UKETUKENO = uketuke_no;
					}
					tempDirPath = pdfRootPath + UKETUKENO;
					 */
					if(isUketuke_noEmpty) { pdfSeq = String.valueOf(i + 1); }
					final String tempDirPath = String.format("%s%s", pdfRootPath, (isUketuke_noEmpty)? uketukeNo : uketuke_no);
					// 2018/03/15 H.Sato updated end (案件C41004)法人仲介担当者登録機能追加対応

					// 2018/03/15 H.Sato updated start (案件C41004)法人仲介担当者登録機能追加対応
					/*
					// 社宅斡旋依頼PDF移動
					File tempDir = new File(tempDirPath);
					if (!tempDir.exists()) {
						tempDir.mkdirs();
					}
					FileUtils.copyFileToDirectory(new File(pdfPath), tempDir);
					new File(pdfPath).delete();
					pdfPath = UKETUKENO + "\\" + new File(pdfPath).getName();
					 */
					final String pdfPathX = pdfPath;
					executor.execute(new Runnable() {
						@Override
						public void run() {
							// 社宅斡旋依頼PDF移動
							File tempDir = new File(tempDirPath);
							if (!tempDir.exists()) {
								tempDir.mkdirs();
							}
							try {
								File pdfFile = new File(pdfPathX);
								FileUtils.copyFileToDirectory(pdfFile, tempDir);
								pdfFile.delete();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					pdfPath = String.format("%s\\%s", ((isUketuke_noEmpty)? uketukeNo : uketuke_no), new File(pdfPath).getName() );
					// 2018/03/15 H.Sato updated end (案件C41004)法人仲介担当者登録機能追加対応
				}
				String tempString = pdfStatus + "," + pdfName + "," + pdfPath + "," + pdfSeq + ";";
				pdfDataStr += tempString;
                //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
                fileMailStatus += pdfStatus;
                //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
				i++;
			}
		}
		dataMap.put("pv_pdf_data_str", pdfDataStr);

		// 登録処理を行う
		Map<String, Object> updData = UKG02010Services.uketukeDataUpd(dataMap);

		// 更新して、メッセージ処理
		if ("ERRS003".equals(updData.get("rst_code"))) {
			ukg02010Data.put("MSG", Util.getServerMsg("ERRS003"));
			ukg02010Data.put("RESULT", "FAIL");
		} else if ("ERRS999".equals(updData.get("rst_code"))) {
			ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "更新処理"));
			ukg02010Data.put("RESULT", "FAIL");
		} else if ("ERRS005".equals(updData.get("rst_code"))) {
			ukg02010Data.put("MSG", Util.getServerMsg("ERRS005"));
		} else {
			ukg02010Data.put("RESULT", "SUCCESS");
		}

		// メール送信
		if ("0000000".equals(updData.get("rst_code"))) {
			// 2018/03/15 H.Sato updated start (案件C41004)法人仲介担当者登録機能追加対応
			/*
			if (!"".equals((String) dataMap.get("pv_post_cd_str"))) {
				String uketuke_No ="";
				if (uketuke_no == null || "".equals(uketuke_no)) {
					uketuke_No = uketukeNo;
				} else {
					uketuke_No = uketuke_no;
				}
				soushinMeru(kaisya_cd, (String) dataMap.get("pv_post_cd_str"), (String) dataMap.get("pv_kokyaku_name"),
						uketuke_No, (String) dataMap.get("pv_houjin_chukai_date"));
			}
			 */
			final String pv_post_cd_str = (String) dataMap.get("pv_post_cd_str");
            //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
            final String allPostCodeStr = (String) dataMap.get("allPostCodeStr");
            final String allUketukeNoStr = (String) dataMap.get("allUketukeNoStr");
            //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
			
			if (!"".equals(pv_post_cd_str)) {
				final String pv_kokyaku_name = (String) dataMap.get("pv_kokyaku_name");
				final String pv_houjin_chukai_date = (String) dataMap.get("pv_houjin_chukai_date");
				final String mailUketukeNo = (isUketuke_noEmpty)? uketukeNo : uketuke_no;

				executor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							soushinMeru(kaisya_cd, pv_post_cd_str, pv_kokyaku_name, mailUketukeNo, pv_houjin_chukai_date);
                            //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
                            if(!isUketuke_noEmpty && (fileMailStatus.indexOf("1") != -1 || fileMailStatus.indexOf("2") != -1 || houjinRenrakuJikoFlag)) {
                                iraiUpdateMail(kaisya_cd, allPostCodeStr, pv_kokyaku_name, mailUketukeNo, pv_houjin_chukai_date, allUketukeNoStr);
                            }
                            //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			// 2018/03/15 H.Sato updated end (案件C41004)法人仲介担当者登録機能追加対応
		}
		executor.shutdown();
		return "ajaxProcess";
	}

	/**
	 * [説 明] メール送信
	 * [更 新] 2018/03/14 H.Sato. 速度改善
	 * @param kaisya_cd,会社コード
	 * @param postCd,店舗コード
	 * @return 送信結果 boo boolean
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void soushinMeru(String kaisya_cd, String postCd, String HOUJIN_NAME, String uketuke_No, String uketuke_Date) throws Exception {

		// メールの送信元アドレス
		String mailSMTP = PropertiesReader.getIntance().getProperty("mailSMTP");
		// メール送信元のポート
		String mailPORT = PropertiesReader.getIntance().getProperty("mailPORT");
		// 送信元アドレス
		String sendAddress = PropertiesReader.getIntance().getProperty("sendAddress");
		// メールの送信元の認証パスワード
		String sendPassword = PropertiesReader.getIntance().getProperty("sendPassword");
		// 仲介依頼送信メールタイトル
		String iraiMailTitle = PropertiesReader.getIntance().getProperty("iraiMailTitle");
		// 仲介依頼送信メールBCC
		String iraiMailBcc = PropertiesReader.getIntance().getProperty("iraiMailBcc");
		// 仲介依頼送信メール本文
		String iraiMailBodyPath = PropertiesReader.getIntance().getProperty("iraiMailBodyPath");
		// 法人仲介依頼未登録メールの末尾本文
		String alarmMailFooterPath = PropertiesReader.getIntance().getProperty("alarmMailFooterPath");
		// 仲介依頼送信メール担当者所属コード
		String iraiMailSyozokuCd = PropertiesReader.getIntance().getProperty("iraiMailSyozokuCd");


		Transport transport = null;
		Properties props = System.getProperties();
		props.setProperty("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", mailSMTP);
		props.put("mail.smtp.port", mailPORT);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.ssl.checkserveridentity", "false");
		props.put("mail.smtp.ssl.trust", mailSMTP);
		props.put("mail.smtp.starttls.enable", "true");
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(true);

		// メールアドレス取得
		String[] postCdList = postCd.split(",");
		for (int j = 0; j < postCdList.length; j++) {
			postCd = postCdList[j];
			List<InternetAddress> mailList1 = new ArrayList<InternetAddress>();
			List<InternetAddress> mailList2 = new ArrayList<InternetAddress>();

			// 仲介依頼先店舗代表メールアドレス取得
			List<Map<String, Object>> listMap1 = (List<Map<String, Object>>) UKG02010Services
					.getTempoDaihyouMailAddress(kaisya_cd, postCd).get("rst_mailAddress_list");

			// 仲介依頼先店舗の各社員メールアドレスリスト取得
			List<Map<String, Object>> listMap2 = (List<Map<String, Object>>) UKG02010Services.getShainMailAddress(
					kaisya_cd, postCd, iraiMailSyozokuCd).get("rst_shainMailAddress_list");

			if (listMap1 != null && listMap1.size() > 0) {
				if ((String) listMap1.get(0).get("E_MAIL_ADDRESS") != null) {
					mailList1.add(new InternetAddress((String) listMap1.get(0).get("E_MAIL_ADDRESS")));
				}
			}

			if (listMap2 != null && listMap2.size() > 0) {
				// 2018/03/16 H.Sato updated start (案件C41004)法人仲介担当者登録機能追加対応
				//	if ((String) listMap2.get(0).get("E_MAIL_ADDRESS") != null) {
				//		mailList2.add(new InternetAddress((String) listMap2.get(0).get("E_MAIL_ADDRESS")));
				//	}
				for(Map<String, Object> map : listMap2) {
					if ((String) map.get("E_MAIL_ADDRESS") != null) {
						mailList2.add(new InternetAddress((String) map.get("E_MAIL_ADDRESS")));
					}
				}
				// 2018/03/16 H.Sato updated end (案件C41004)法人仲介担当者登録機能追加対応
			}

			InternetAddress[] address1 = new InternetAddress[mailList1.size()];
			for (int i = 0; i < mailList1.size(); i++) {
				address1[i] = mailList1.get(i);
			}

			InternetAddress[] address2 = new InternetAddress[mailList2.size()];
			for (int i = 0; i < mailList2.size(); i++) {
				address2[i] = mailList2.get(i);
			}

			if (address1 == null || address1.length == 0 || address2 == null || address2.length == 0) {
				return;
			}

			// 仲介依頼先店舗の受付№取得
			String uketukeNo = (String)UKG02010Services.getUketuke_No(kaisya_cd, postCd, uketuke_No).get("pv_uketuke_no");

			try {
				// メールを設定
				MimeMessage msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(sendAddress));
				msg.setRecipients(Message.RecipientType.TO, address1);
				msg.setRecipients(Message.RecipientType.CC, address2);
				msg.setRecipients(Message.RecipientType.BCC, iraiMailBcc);
				msg.setSubject(MimeUtility.encodeText(iraiMailTitle.replace("%1", HOUJIN_NAME), "iso-2022-jp", "B"));
				msg.setSentDate(new Date());

				// メール内容を設定
				MimeBodyPart mbp1 = new MimeBodyPart();
				InputStreamReader fr = new InputStreamReader(new UnicodeInputStream(new FileInputStream(iraiMailBodyPath), "UTF-8"), "UTF-8");
				InputStreamReader frd = new InputStreamReader(new UnicodeInputStream(new FileInputStream(alarmMailFooterPath), "UTF-8"), "UTF-8");
				BufferedReader br = new BufferedReader(fr);
				BufferedReader bfr = new BufferedReader(frd);
				StringBuffer sb = new StringBuffer();
				String str = "";
				while ((str = br.readLine()) != null) {
					sb.append(str);
					sb.append("\n");
				}
				while ((str = bfr.readLine()) != null) {
					sb.append(str);
					sb.append("\n");
				}
				String msgContent = sb.toString().replace("{HOUJIN_NAME}", HOUJIN_NAME);
				msgContent = msgContent.replace("{HOUJIN_CHUKAI_DATE}", uketuke_Date);
				msgContent = msgContent.replace("{UKETUKE_NO}", uketukeNo);
                //2020/05/06 ADD START 劉恒毅(SYS) C43040-2_法人の入力項目追加対応
                Map<String, Object> houjinIraiInfoList = (Map<String, Object>)UKG02010Services.getHoujinIraiInfo(kaisya_cd,uketuke_No);
                String iraiTantouStr = "";
                if("0000000".equals(houjinIraiInfoList.get("rst_code"))) {
                    List<Map<String, Object>> houjinIraiInfo = (List<Map<String, Object>>) houjinIraiInfoList.get("pv_houjin_irai_info");
                    if (houjinIraiInfo.size() > 0) {
                        iraiTantouStr = (String) houjinIraiInfo.get(0).get("HOUJIN_IRAI_TANTOU_NAME");
                        String houjinIraiTelNo = (String) houjinIraiInfo.get(0).get("TEL_NO");
                        if(houjinIraiTelNo != null && !"".equals(houjinIraiTelNo)) {
                            iraiTantouStr += "（" + houjinIraiTelNo + "）";
                        }
                    }
                }
                msgContent = msgContent.replace("{IRAI_TANTOU}", iraiTantouStr);
                //2020/05/06 ADD END 劉恒毅(SYS) C43040-2_法人の入力項目追加対応
				mbp1.setText(msgContent, "iso-2022-jp");
				Multipart mp = new MimeMultipart();
				mp.addBodyPart(mbp1);
				msg.setContent(mp);

				// メールを送信
				transport = session.getTransport("smtp");
				if ("true".equals(props.get("mail.smtp.auth"))) {
					transport.connect(mailSMTP, sendAddress, sendPassword);
				} else {
					transport.connect();
				}
				transport.sendMessage(msg, msg.getAllRecipients());

			} catch (Exception e) {
				throw new Exception(e);
			} finally {
				transport.close();
			}
		}
	}

    //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
    /**
     * [説 明] 法人仲介依頼内容変更通知メール
     * @param kaisyacd,会社コード
     * @param postCd,店舗コード
     * @param kigyouName,法人企業名
     * @param uketukeNo,受付No
     * @param uketukeDate,受付日
     * @return 送信結果 boo boolean
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "resource" })
    public void iraiUpdateMail(String kaisyacd, String postCd, String kigyouName, String uketukeNo, String uketukeDate,String uketukeNoListStr) throws Exception {

        // メールの送信元アドレス
        String mailSMTP = PropertiesReader.getIntance().getProperty("mailSMTP");
        // メール送信元のポート
        String mailPORT = PropertiesReader.getIntance().getProperty("mailPORT");
        // 送信元アドレス
        String sendAddress = PropertiesReader.getIntance().getProperty("sendAddress");
        // メールの送信元の認証パスワード
        String sendPassword = PropertiesReader.getIntance().getProperty("sendPassword");
        // 法人仲介依頼内容変更通知メールタイトル
        String iraiMailTitle = PropertiesReader.getIntance().getProperty("iraiUpdateMailTitle");
        // 仲介依頼送信メールBCC
        String iraiMailBcc = PropertiesReader.getIntance().getProperty("iraiMailBcc");
        // 法人仲介依頼内容変更通知メール本文
        String iraiMailBodyPath = PropertiesReader.getIntance().getProperty("iraiUpdateMailPath");
        // 法人仲介依頼内容変更通知メールの末尾本文
        String alarmMailFooterPath = PropertiesReader.getIntance().getProperty("alarmMailFooterPath");
        // 仲介依頼送信メール担当者所属コード
        String iraiMailSyozokuCd = PropertiesReader.getIntance().getProperty("iraiMailSyozokuCd");

        Transport transport = null;
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", mailSMTP);
        props.put("mail.smtp.port", mailPORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.checkserveridentity", "false");
        props.put("mail.smtp.ssl.trust", mailSMTP);
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);

        // メールアドレス取得
        String[] postCdList = postCd.split(",");
        String[] uketukeNoList = uketukeNoListStr.split(",");
        for (int j = 0; j < postCdList.length; j++) {
            postCd = postCdList[j];
            // メールアドレス取得
            List<InternetAddress> mailList1 = new ArrayList<InternetAddress>();
            List<InternetAddress> mailList2 = new ArrayList<InternetAddress>();

            // 仲介依頼先店舗代表メールアドレスと仲介担当メールアドレス取得
            List<Map<String, Object>> listMap1 = (List<Map<String, Object>>) UKG02010Services
                    .getTempoDaihyouAndTanTouMailAddress(kaisyacd, postCd,uketukeNoList[j]).get("rst_mailAddress_list");

            // 仲介依頼先店舗の各社員メールアドレスリスト取得
            List<Map<String, Object>> listMap2 = (List<Map<String, Object>>) UKG02010Services.getShainMailAddress(
                    kaisyacd, postCd, iraiMailSyozokuCd).get("rst_shainMailAddress_list");

            if (listMap1 != null && listMap1.size() > 0) {
                for(Map<String, Object> map : listMap1) {
                    if ((String) map.get("E_MAIL_ADDRESS") != null) {
                        mailList1.add(new InternetAddress((String) map.get("E_MAIL_ADDRESS")));
                    }
                }
            }

            if (listMap2 != null && listMap2.size() > 0) {
                for(Map<String, Object> map : listMap2) {
                    if ((String) map.get("E_MAIL_ADDRESS") != null) {
                        mailList2.add(new InternetAddress((String) map.get("E_MAIL_ADDRESS")));
                    }
                }
            }

            InternetAddress[] address1 = new InternetAddress[mailList1.size()];
            for (int i = 0; i < mailList1.size(); i++) {
                address1[i] = mailList1.get(i);
            }

            InternetAddress[] address2 = new InternetAddress[mailList2.size()];
            for (int i = 0; i < mailList2.size(); i++) {
                address2[i] = mailList2.get(i);
            }

            if (address1 == null || address1.length == 0 || address2 == null || address2.length == 0) {
                return;
            }
            try {
                // メールを設定
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(sendAddress));
                msg.setRecipients(Message.RecipientType.TO, address1);
                msg.setRecipients(Message.RecipientType.CC, address2);
                msg.setRecipients(Message.RecipientType.BCC, iraiMailBcc);
                msg.setSubject(MimeUtility.encodeText(iraiMailTitle.replace("%1", kigyouName), "iso-2022-jp", "B"));
                msg.setSentDate(new Date());

                // メール内容を設定
                MimeBodyPart mbp1 = new MimeBodyPart();
                InputStreamReader fr = new InputStreamReader(new UnicodeInputStream(new FileInputStream(iraiMailBodyPath), "UTF-8"), "UTF-8");
                InputStreamReader frd = new InputStreamReader(new UnicodeInputStream(new FileInputStream(alarmMailFooterPath), "UTF-8"), "UTF-8");
                BufferedReader br = new BufferedReader(fr);
                BufferedReader bfr = new BufferedReader(frd);
                StringBuffer sb = new StringBuffer();
                String str = "";
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                    sb.append("\n");
                }
                while ((str = bfr.readLine()) != null) {
                    sb.append(str);
                    sb.append("\n");
                }
                String henkouNaiyou = "";
                if(fileMailStatus.indexOf("2") != -1) {
                    henkouNaiyou += "「添付資料」の追加";
                }
                if(fileMailStatus.indexOf("1") != -1) {
                    if(!"".equals(henkouNaiyou)) {
                        henkouNaiyou += "、";
                    }
                    henkouNaiyou += "「添付資料」の削除";
                }
                if(houjinRenrakuJikoFlag) {
                    if(!"".equals(henkouNaiyou)) {
                        henkouNaiyou += "、";
                    }
                    henkouNaiyou += "「法人仲介依頼連絡事項」の変更";
                }
                String msgContent = sb.toString();
                msgContent = msgContent.replace("{HENKOU_NAIYOU}", henkouNaiyou);
                msgContent = msgContent.replace("{HOUJIN_NAME}", kigyouName);
                msgContent = msgContent.replace("{HOUJIN_CHUKAI_DATE}", uketukeDate);
                msgContent = msgContent.replace("{UKETUKE_NO}", uketukeNoList[j]);
                Map<String, Object> houjinIraiInfoList = (Map<String, Object>)UKG02010Services.getHoujinIraiInfo(kaisyacd,uketukeNo);
                String iraiTantouStr = "";
                if("0000000".equals(houjinIraiInfoList.get("rst_code"))) {
                    List<Map<String, Object>> houjinIraiInfo = (List<Map<String, Object>>) houjinIraiInfoList.get("pv_houjin_irai_info");
                    if (houjinIraiInfo.size() > 0) {
                        iraiTantouStr = (String) houjinIraiInfo.get(0).get("HOUJIN_IRAI_TANTOU_NAME");
                        String houjinIraiTelNo = (String) houjinIraiInfo.get(0).get("TEL_NO");
                        if(houjinIraiTelNo != null && !"".equals(houjinIraiTelNo)) {
                            iraiTantouStr += "（" + houjinIraiTelNo + "）";
                        }
                    }
                }
                msgContent = msgContent.replace("{IRAI_TANTOU}", iraiTantouStr);
                mbp1.setText(msgContent, "iso-2022-jp");
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(mbp1);
                msg.setContent(mp);

                // メールを送信
                transport = session.getTransport("smtp");
                if ("true".equals(props.get("mail.smtp.auth"))) {
                    transport.connect(mailSMTP, sendAddress, sendPassword);
                } else {
                    transport.connect();
                }
                transport.sendMessage(msg, msg.getAllRecipients());

            } catch (Exception e) {
                throw new Exception(e);
            } finally {
                transport.close();
            }
        }
    }
    //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応

	/**
	 * <pre>
	 * [説 明] 保存3ボタン押下処理
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String hozonProcess() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();
		ukg02010Data = (Map<String, Object>) session.getAttribute("UKG02010DATAMAP");
		ukg02010Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));
		// 画面のデータを転変する
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Set<Entry<String, Object>> set = request.getParameterMap().entrySet();
		for (Entry<String, Object> entry : set) {
			String value = ((String[]) entry.getValue())[0];
			entry.setValue(value);
		}
		dataMap.putAll(request.getParameterMap());

		// 会社コード
		dataMap.put("pv_kaisya_cd", loginInfo.get(Consts.KAISYA_CD));
		// 受付No.
		String uketuke_no = (String) session.getAttribute(Consts.UKETUKENO);
		dataMap.put("pv_uketuke_no", uketuke_no);
		// 担当者コード
		dataMap.put("pv_tantou_cd", loginInfo.get(Consts.USER_CD));
		// 社宅斡旋依頼書情報を取得
		List<Map<String, Object>> syatakuinfoList = (List<Map<String, Object>>) ukg02010Data.get("SYATAKUINFO_LIST");
		// PDFデータルートパス
		String pdfRootPath = PropertiesReader.getIntance().getProperty("pdfRootPath");
		// PDF一時フォルダ
		String pdfTempFolder = PropertiesReader.getIntance().getProperty("pdfTempFolder");
		if(Util.isEmpty(pdfRootPath) || Util.isEmpty(pdfTempFolder)){
			ukg02010Data.put("RESULT", "SYSERR");
			throw new Exception();
		}
		// PDFデータを結合
		String pdfDataStr = "";
		if (syatakuinfoList != null && syatakuinfoList.size() > 0) {
			int i = 0;
			Map<String, Object> syatakuinfo = new HashMap<String, Object>();
			while (i < syatakuinfoList.size()) {
				syatakuinfo = syatakuinfoList.get(i);
				String pdfStatus = (String) syatakuinfo.get("PDF_FILE_STATUS") == null ? "" : (String) syatakuinfo
						.get("PDF_FILE_STATUS");
				String pdfName = (String) syatakuinfo.get("PDF_FILE_NAME") == null ? "" : (String) syatakuinfo
						.get("PDF_FILE_NAME");
				String pdfPath = (String) syatakuinfo.get("PDF_FILE_PATH") == null ? "" : (String) syatakuinfo
						.get("PDF_FILE_PATH");
				String pdfSeq = (String) syatakuinfo.get("SYATAKU_PDF_SEQ") == null ? "" : (String) syatakuinfo
						.get("SYATAKU_PDF_SEQ");
				if ("2".equals(pdfStatus)) {
					pdfPath = pdfRootPath + pdfTempFolder + pdfPath;
					String tempDirPath = pdfRootPath + uketuke_no;
					// 社宅斡旋依頼PDF移動
					File tempDir = new File(tempDirPath);
					if (!tempDir.exists()) {
						tempDir.mkdirs();
					}
					FileUtils.copyFileToDirectory(new File(pdfPath), tempDir);
					new File(pdfPath).delete();
					pdfPath = uketuke_no + "\\" + new File(pdfPath).getName();
				}
				String tempString = pdfStatus + "," + pdfName + "," + pdfPath + "," + pdfSeq + ";";
				pdfDataStr += tempString;
                //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
                fileMailStatus += pdfStatus;
                //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
				i++;
			}
		}
		dataMap.put("pv_pdf_data_str", pdfDataStr);
		dataMap.put("pv_moto_uketukeno", "");

		// 更新処理を行う
		Map<String, Object> updData = UKG02010Services.updHozonData(dataMap);
		//社宅斡旋依頼PDFデータ取得処理
		pdfInit();

		// 更新日時
		ukg02010Data.put("UPD_DATE", updData.get("pv_rst_upd_date"));

		// 更新して、メッセージ処理
		if ("ERRS003".equals(updData.get("rst_code"))) {
			ukg02010Data.put("MSG", Util.getServerMsg("ERRS003"));
			ukg02010Data.put("RESULT", "FAIL");
		} else if ("ERRS999".equals(updData.get("rst_code"))) {
			ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "更新処理"));
			ukg02010Data.put("RESULT", "FAIL");
		} else {
			ukg02010Data.put("MSG", Util.getServerMsg("MSGS001", "登録"));
			ukg02010Data.put("RESULT", "SUCCESS");
		}
        //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
        // メール送信
        if ("0000000".equals(updData.get("rst_code"))) {
            ExecutorService executor = Executors.newCachedThreadPool();
            if (fileMailStatus.indexOf("1") != -1 || fileMailStatus.indexOf("2") != -1 || houjinRenrakuJikoFlag) {
                final String pv_post_cd_str = (String) dataMap.get("pv_post_cd_str");
                final String pv_kokyaku_name = (String) dataMap.get("pv_kokyaku_name");
                final String pv_houjin_chukai_date = (String) dataMap.get("pv_houjin_chukai_date");
                final String mailUketukeNo =  (String) dataMap.get("pv_uketuke_no");
                final String kaisya_cd =  (String) dataMap.get("pv_kaisya_cd");
                final String allUketukeNoStr =  (String) dataMap.get("pv_uketuke_no_str");

                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            iraiUpdateMail(kaisya_cd, pv_post_cd_str, pv_kokyaku_name, mailUketukeNo, pv_houjin_chukai_date, allUketukeNoStr);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] PDF保存ボタン押下処理
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String pdfSave() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		ukg02010Data = (Map<String, Object>) session.getAttribute("UKG02010DATAMAP");
		ukg02010Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));

		// 社宅斡旋依頼書情報を取得
		List<Map<String, Object>> syatakuinfoList = (List<Map<String, Object>>) ukg02010Data.get("SYATAKUINFO_LIST");
		if (syatakuinfoList == null) {
			syatakuinfoList = new ArrayList<Map<String, Object>>();
		}
		//
		Map<String, Object> tempPdfMap = new HashMap<String, Object>();

		// PDFデータルートパス
		String pdfRootPath = PropertiesReader.getIntance().getProperty("pdfRootPath");
		// PDF一時フォルダ
		String pdfTempFolder = PropertiesReader.getIntance().getProperty("pdfTempFolder");
		if(Util.isEmpty(pdfRootPath) || Util.isEmpty(pdfTempFolder)){
			ukg02010Data.put("RESULT", "SYSERR");
			return "pdfinit";
		}
		// セッションID
		String sessionId = session.getId();
		// ユーザーID
		String userId = (String) sessionLoginInfo.get(Consts.USER_CD);
		// アップロード日時(YYYYMMDDHH24MISSsss)
		String uploadTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		// システム日付日時(YYYYMMDDHH24MISS)
		String systemTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		// フォルダ
		String folderName = sessionId + userId + uploadTime;

		MultiPartRequestWrapper wrapper = (MultiPartRequestWrapper) request;
		File[] files = wrapper.getFiles("files");
        //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
        String[] fileFileNames = wrapper.getFileNames("files");
        //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応

		// ファイル種類のmap
		HashMap<String, String> mFileTypes = new HashMap<String, String>();
		// pdf
		mFileTypes.put("pdf", "25504446");
		// XDW
		mFileTypes.put("xdw", "600E8201");
		String nameSuffix = "";

		try {
            //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
            //for (File file : files) {
            for(int i = 0; i < files.length; i++) {
                File file = files[i];
            //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
				// ファイル種類のチェック
				FileInputStream fileIs = null;
				String value = "";
				try {
					fileIs = new FileInputStream(file);
					byte[] b = new byte[4];
					fileIs.read(b, 0, b.length);
					value = bytesToHexString(b);
				} finally {
					if (fileIs != null) {
						fileIs.close();
					}
				}
                //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
                //if (!mFileTypes.get("pdf").equals(value) && !mFileTypes.get("xdw").equals(value)) {
                //    ukg02010Data.put("MSG", Util.getServerMsg("ERRC002", "PDFまたはドキュワークスのファイル"));
                if (!mFileTypes.get("pdf").equals(value) && !mFileTypes.get("xdw").equals(value)
                    && !isSuffixCheck(fileFileNames[i])) {
                    ukg02010Data.put("MSG", Util.getServerMsg("ERRC002", "PDF・Excel・Wordまたはドキュワークスのファイル"));
               //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
					ukg02010Data.put("RESULT", "FAIL");
					return "pdfinit";
				}
			}
            //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
            //for (File file : files) {
            for(int i = 0; i < files.length; i++) {
                File file = files[i];
            //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
				// ファイルの拡張子を取得する
				FileInputStream fileIs = null;
				String value = "";
				try {
					fileIs = new FileInputStream(file);
					byte[] b = new byte[4];
					fileIs.read(b, 0, b.length);
					value = bytesToHexString(b);
				} finally {
					if (fileIs != null) {
						fileIs.close();
					}
				}
                //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
                String fileSuffix = fileFileNames[i];
                //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
				if (mFileTypes.get("pdf").equals(value)) {
					nameSuffix = ".pdf";
				}
                //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
                 else if (isSuffixCheck(fileSuffix)) {
                    nameSuffix = fileSuffix.substring(fileSuffix.lastIndexOf("."));
                //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
				} else {
					nameSuffix = ".xdw";
				}
				tempPdfMap = new HashMap<String, Object>();
				// PDFフォルダを作成する
				File outFilePath = new File(pdfRootPath + pdfTempFolder + folderName);
				if (!outFilePath.exists()) {
					outFilePath.mkdirs();
				}

				// PDFファイルを作成する
				File pdfFile = new File(outFilePath + "\\" + systemTime + nameSuffix);
				if (pdfFile.exists()) {
					systemTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(new SimpleDateFormat(
							"yyyyMMddHHmmss").parse(systemTime).getTime() + 1000));
					pdfFile = new File(outFilePath + "\\" + systemTime + nameSuffix);
				}
				pdfFile.createNewFile();
				FileInputStream fileInputStream = new FileInputStream(file);
				OutputStream outputStream = new FileOutputStream(pdfFile);
				try {
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = fileInputStream.read(buffer)) > -1) {
						outputStream.write(buffer, 0, len);
					}
				} finally {
					outputStream.close();
					fileInputStream.close();
				}
				// PDFファイル名
				tempPdfMap.put("PDF_FILE_NAME", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(
						new SimpleDateFormat("yyyyMMddHHmmss").parse(systemTime).getTime())));
				// PDFファイルパス
				tempPdfMap.put("PDF_FILE_PATH", folderName + "\\" + systemTime + nameSuffix);
				// PDFファイルステータス
				tempPdfMap.put("PDF_FILE_STATUS", "2");
				// 社宅斡旋依頼書情報
				syatakuinfoList.add(tempPdfMap);
			}
			int delFlg = 0;
			for (Map<String, Object> tempMap : syatakuinfoList) {
				if ("1".equals(tempMap.get("PDF_FILE_STATUS"))) {
					delFlg++;
				}
			}
			// 社宅斡旋依頼書情報
			ukg02010Data.put("SYATAKUINFO_LIST", syatakuinfoList);
			int num1 = Integer.parseInt(PropertiesReader.getIntance().getProperty("pdfMaxCount"));
			int num3 = num1 - syatakuinfoList.size() + delFlg;
			int[] tempList = new int[num3];
			ukg02010Data.put("tempList", tempList);

			ukg02010Data.put("MSG", "");
			ukg02010Data.put("RESULT", "SUCCESS");
		} catch (Exception e) {
            //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
            //ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "PDFの保存"));
            ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "添付の保存"));
            //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
			ukg02010Data.put("RESULT", "FAIL");
		}

		return "pdfinit";
	}
    //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
    /**
     * <pre>
     * [説 明] 拡張子判断 
     * </pre>
     */
    private boolean isSuffixCheck(String fileName) {
        String suffixStr = fileName.substring(fileName.lastIndexOf(".")).toUpperCase();
        // 添付資料保存可能ファイル拡張子
        String shiryousuffixStr = PropertiesReader.getIntance().getProperty("shiryouSaveEnableExtent").toUpperCase();
        if(shiryousuffixStr.indexOf(suffixStr) != -1) {
            return true;
        }
        return false;
    }
    //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
	/**
	 * <pre>
	 * [説 明] PDF削除ボタン押下処理
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String pdfDelete() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg02010Data = (Map<String, Object>) session.getAttribute("UKG02010DATAMAP");
		ukg02010Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));
		// PDFデータルートパス
		String pdfRootPath = PropertiesReader.getIntance().getProperty("pdfRootPath");
		// PDF一時フォルダ
		String pdfTempFolder = PropertiesReader.getIntance().getProperty("pdfTempFolder");
		if(Util.isEmpty(pdfRootPath) || Util.isEmpty(pdfTempFolder)){
			ukg02010Data.put("RESULT", "SYSERR");
			return "pdfinit";
		}

		// 社宅斡旋依頼書情報リストを取得
		List<Map<String, Object>> syatakuinfoList = (List<Map<String, Object>>) ukg02010Data.get("SYATAKUINFO_LIST");
		try {
			int index = Integer.parseInt(request.getParameter("PDF_INDEX"));
			int delFlg = 0;

			// 社宅斡旋依頼書情報
			Map<String, Object> tempPdfMap = syatakuinfoList.get(index);
			// PDFファイルパス
			String folderName = (String) tempPdfMap.get("PDF_FILE_PATH");
			// PDFファイルステータス
			String pdfFileStatus = (String) tempPdfMap.get("PDF_FILE_STATUS");

			// PDFファイルを作成する
			File pdfFile = new File(pdfRootPath + folderName);
			// PDFファイルを作成する
			File tempPdfFile = new File(pdfRootPath + pdfTempFolder + folderName);
			if (pdfFile.exists() || "3".equals(pdfFileStatus)) {
				// PDFファイルステータス
				tempPdfMap.put("PDF_FILE_STATUS", "1");
			} else if (tempPdfFile.exists() || "2".equals(pdfFileStatus)) {
				// 選択行の社宅斡旋依頼書情報[i]を削除
				syatakuinfoList.remove(index);
				// ファイルを削除
				tempPdfFile.delete();
			}
			for (Map<String, Object> tempMap : syatakuinfoList) {
				if ("1".equals(tempMap.get("PDF_FILE_STATUS"))) {
					delFlg++;
				}
			}
			// 社宅斡旋依頼書情報
			ukg02010Data.put("SYATAKUINFO_LIST", syatakuinfoList);
			int num1 = Integer.parseInt(PropertiesReader.getIntance().getProperty("pdfMaxCount"));
			int num3 = num1 - syatakuinfoList.size() + delFlg;
			int[] tempList = new int[num3];
			ukg02010Data.put("tempList", tempList);

			ukg02010Data.put("MSG", "");
			ukg02010Data.put("RESULT", "SUCCESS");
		} catch (Exception e) {
			ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "PDFの保存"));
			ukg02010Data.put("RESULT", "FAIL");
		}
		return "pdfinit";
	}

	/**
	 * PDFファイル名日時リンク押下チェック処理
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String pdfLinkCheck() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg02010Data = (Map<String, Object>) session.getAttribute("UKG02010DATAMAP");
		ukg02010Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));
		// PDFデータルートパス
		String pdfRootPath = PropertiesReader.getIntance().getProperty("pdfRootPath");
		// PDF一時フォルダ
		String pdfTempFolder = PropertiesReader.getIntance().getProperty("pdfTempFolder");
		if(Util.isEmpty(pdfRootPath) || Util.isEmpty(pdfTempFolder)){
			ukg02010Data.put("RESULT", "SYSERR");
			throw new Exception();
		}

		// 社宅斡旋依頼書情報リストを取得
		List<Map<String, Object>> syatakuinfoList = (List<Map<String, Object>>) ukg02010Data.get("SYATAKUINFO_LIST");

		int index = Integer.parseInt(request.getParameter("PDF_INDEX"));

		// 社宅斡旋依頼書情報
		Map<String, Object> tempPdfMap = syatakuinfoList.get(index);

		// PDFファイルパス
		String folderPathName = (String) tempPdfMap.get("PDF_FILE_PATH");
		// PDFファイルステータス
		String pdfFileStatus = (String) tempPdfMap.get("PDF_FILE_STATUS");
		// ファイル名称
		String fileName = "";
		if ("2".equals(pdfFileStatus)) {
			fileName = pdfRootPath + pdfTempFolder + folderPathName;

		} else {
			fileName = pdfRootPath + folderPathName;
		}

		BufferedInputStream inputStream = null;
		File file = null;
		try {
			if ("".equals(fileName)) {
                //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
                //ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "PDFまたはドキュワークスのファイル取得"));
                ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "PDF・Excel・Wordまたはドキュワークスのファイル取得"));
                //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
				ukg02010Data.put("RESULT", "FAIL");
				return "ajaxProcess";
			}

			file = new File(fileName);

			if (file.exists()) {
				inputStream = new BufferedInputStream(new FileInputStream(file));

			} else {
                //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
                //ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "PDFまたはドキュワークスのファイル取得"));
                ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "PDF・Excel・Wordまたはドキュワークスのファイル取得"));
                //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
				ukg02010Data.put("RESULT", "FAIL");
				return "ajaxProcess";
			}

		} catch (Exception e) {
			e.printStackTrace();
            //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
            //ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "PDFまたはドキュワークスのファイル取得"));
            ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "PDF・Excel・Wordまたはドキュワークスのファイル取得"));
            //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
			ukg02010Data.put("RESULT", "FAIL");
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				LOG.error("ストリームをクローズする時に、エラーが発生しました。", e);
                //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
                //ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "PDFまたはドキュワークスのファイル取得"));
                ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "PDF・Excel・Wordまたはドキュワークスのファイル取得"));
                //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
				ukg02010Data.put("RESULT", "FAIL");
				return "ajaxProcess";
			}
		}
		ukg02010Data.put("MSG", "");
		ukg02010Data.put("RESULT", "SUCCESS");
		return "ajaxProcess";
	}

	/**
	 * PDFファイル名日時リンク押下処理
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void pdfLink() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession();
		ukg02010Data = (Map<String, Object>) session.getAttribute("UKG02010DATAMAP");
		// PDFデータルートパス
		String pdfRootPath = PropertiesReader.getIntance().getProperty("pdfRootPath");
		// PDF一時フォルダ
		String pdfTempFolder = PropertiesReader.getIntance().getProperty("pdfTempFolder");

		// 社宅斡旋依頼書情報リストを取得
		List<Map<String, Object>> syatakuinfoList = (List<Map<String, Object>>) ukg02010Data.get("SYATAKUINFO_LIST");

		int index = Integer.parseInt(request.getParameter("PDF_INDEX"));

		// 社宅斡旋依頼書情報
		Map<String, Object> tempPdfMap = syatakuinfoList.get(index);

		// PDFファイルパス
		String folderPathName = (String) tempPdfMap.get("PDF_FILE_PATH");
		// PDFファイル名称
		String folderName = (String) tempPdfMap.get("PDF_FILE_NAME");
		// PDFファイルステータス
		String pdfFileStatus = (String) tempPdfMap.get("PDF_FILE_STATUS");
		// ファイル名称
		String fileName = "";
		if ("2".equals(pdfFileStatus)) {
			fileName = pdfRootPath + pdfTempFolder + folderPathName;

		} else {
			fileName = pdfRootPath + folderPathName;
		}

		BufferedInputStream inputStream = null;
		OutputStream outputStream = null;
		File file = null;
		try {
			if (fileName.equals("")) {
				return;
			}

			file = new File(fileName);
			folderName = file.getName();
            //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
            //LOG.debug("PDFまたはドキュワークスのファイル[" + folderName + "]のダウンロードのダウンロードを始まります。");
            LOG.debug("PDF・Excel・Wordまたはドキュワークスのファイル[" + folderName + "]のダウンロードのダウンロードを始まります。");
            //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応

			if (file.exists()) {
				inputStream = new BufferedInputStream(new FileInputStream(file));
				// 2018/03/16 H.Sato updated start (案件C41004)法人仲介担当者登録機能追加対応
				//response.setContentType("application/pdf");
				String mimetype = FileTypeMap.getDefaultFileTypeMap().getContentType(fileName);
				response.setContentType(mimetype);
				// 2018/03/16 H.Sato updated end (案件C41004)法人仲介担当者登録機能追加対応
				String downLoadfilePath = "attachment;filename= "
						+ new String(folderName.getBytes("shift-JIS"), "iso-8859-1");
				response.setHeader("Content-disposition", downLoadfilePath);

				int fileSize = inputStream.available();
				response.setContentLength(fileSize);
				outputStream = response.getOutputStream();
				byte[] b = new byte[32 * 1024];
				int len = 0;
				while ((len = inputStream.read(b)) != -1) {
					outputStream.write(b, 0, len);
				}
				outputStream.flush();

                //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
                //LOG.debug("PDFまたはドキュワークスのファイル[" + file.getAbsolutePath() + "]をダウンロードしました。");
                LOG.debug("PDF・Excel・Wordまたはドキュワークスのファイル[" + file.getAbsolutePath() + "]をダウンロードしました。");
                //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
                //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
                //LOG.debug("PDFまたはドキュワークスのファイル[" + file.getAbsolutePath() + "]をダウンロードしました。");
                LOG.error("PDF・Excel・Wordまたはドキュワークスのファイル[" + file.getAbsolutePath() + "]が見つかれません。");
                //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
			}

		} catch (IOException e) {
			e.printStackTrace();
			LOG.debug("ファイル「" + fileName + "」をオープンする時に、エラーが発生しました。", e);
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} catch (IOException ioe) {
				e.printStackTrace();
				LOG.debug("ファイル「" + fileName + "」をエラー送信する時に、エラーが発生しました。", e);
			}
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				LOG.error("ストリームをクローズする時に、エラーが発生しました。", e);
			}
		}
		return;
	}

	/**
	 * <pre>
	 * [説 明] 社宅斡旋依頼PDFデータ取得処理
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String pdfInit() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg02010Data = (Map<String, Object>) session.getAttribute("UKG02010DATAMAP");
		ukg02010Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));
		// ログイン情報
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		// 会社コード
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		// 引数.受付№
		String uketukeno = (String) session.getAttribute(Consts.UKETUKENO);

		// 社宅斡旋依頼書情報を取得
		List<Map<String, Object>> pdfList = (List<Map<String, Object>>) UKG02010Services.getSyatakuInfo(kaisyacd,
				uketukeno).get("rst_syatakuInforList");
		ukg02010Data.put("SYATAKUINFO_LIST", pdfList);

		int num1 = Integer.parseInt(PropertiesReader.getIntance().getProperty("pdfMaxCount"));
		int num3 = num1 - pdfList.size();
		if (num3 < 0) {
			num3 = 0;
		}
		int[] tempList = new int[num3];
		ukg02010Data.put("tempList", tempList);

		return "pdfinit";
	}

	/**
	 * <pre>
	 * [説 明] 店舗名リストデータを検索
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchPostData() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg02010Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));
		// ログイン情報
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		// 会社コード
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));

		// 店舗コード/店舗名
		String postInput = request.getParameter("postInput");
		if (postInput.length() == 1 && postInput.getBytes().length == 1) {
			ukg02010Data.put("POSTDATA_LIST", null);
		} else {

			String postPara = URLDecoder.decode(URLDecoder.decode(postInput, "UTF-8"), "UTF-8").trim();

			// 店舗データ取得
			List<Map<String, Object>> list = (List<Map<String, Object>>) UKG02010Services.getPostData(kaisyacd,
					postPara).get("rst_postDataList");

			// 店舗データを取得する。
			ukg02010Data.put("POSTDATA_LIST", list);
		}
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] システム日付を取得する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	private static String toGetSysDateTime() {

		// システム日付を取得
		Calendar ca = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(Consts.FORMAT_DATE_YMDHMS);
		return format.format(ca.getTime());
	}

	/**
	 * <pre>
	 * [説 明] byte[]からStringへの転換
	 * @param b byte[]
	 * @return 処理結果 String
	 * </pre>
	 */
	private String bytesToHexString(byte[] b) {
		StringBuilder builder = new StringBuilder();
		if (b == null || b.length <= 0) {
			return null;
		}
		String hv;
		for (int i = 0; i < b.length; i++) {
			hv = Integer.toHexString(b[i] & 0xFF).toUpperCase();
			if (hv.length() < 2) {
				builder.append(0);
			}
			builder.append(hv);
		}
		return builder.toString();
	}
	// 2017/09/22 ヨウ強 ADD END (案件C41004)法人仲介担当者登録機能追加対応

	// 2018/01/03 ADD START 朱珂(SYS) 案件C42036デザイン変更新規作成
	/**
	 * アンケートデータを取得する
	 */
	@SuppressWarnings("unchecked")
	private void getAnketoData() throws Exception {
	    // 同期フラグ設定
        setAsyncFlg(true);
        ukg02020Data = new HashMap<String, Object>();

        HttpServletRequest request = ServletActionContext.getRequest();
        HttpSession sessionInfo = request.getSession();
        Map<String, Object> loginInfo = getLoginInfo();
        // 受付No.
        String uketukeNo;
        // 遷移元画面を判定uketukeNo
        if ("UKG01011".equals(request.getParameter("historyPageId"))) {
            // 遷移前画面が受付未登録一覧
            uketukeNo = request.getParameter("uketukeNo");
            ukg02020Data.put("beforeGamen", "UKG01011");

            // セッションに受付未登録一覧遷移フラグを設定する。
            sessionInfo.setAttribute(Consts.UKETUKENO, uketukeNo);
            sessionInfo.setAttribute(Consts.UKG01011_FLG, "UKG01011");
        } else {
            uketukeNo = String.valueOf(sessionInfo.getAttribute(Consts.UKETUKENO));
            ukg02020Data.put("beforeGamen", "");
        }

        if ("UKG01011".equals(sessionInfo.getAttribute(Consts.UKG01011_FLG))) {
            ukg02020Data.put("beforeGamen", "UKG01011");
        }
        // セッション情報取得
        String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));

        // 会社コードと受付Noをセッションに設定する。
        ukg02020Data.put("kaisyaCd", kaisyaCd);
        ukg02020Data.put("uketukeNo", uketukeNo);

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
        ukg02020Data.put("anketoData", dispAnketo);

        // ログイン情報
        Map<String, Object> sessionLoginInfo = getLoginInfo();
        String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
        ukg02020Data.put("authorityCd", authorityCd);

        // アンケートデータをセッションに保存
        sessionInfo.setAttribute(Consts.UKG02020_ANKETO_DATA, beforeAnketo);
	}
	
    //2020/05/06 ADD START 楊朋朋 C43040-2_法人の入力項目追加対応
    /**
     * 法人仲介依頼を削除します
     **/
    public String updHoujinnTyuukaiIlaiFlag() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        ukg02010Data = new HashMap<String, Object>();
        //受付No
        final String uketukeNo = request.getParameter("uketukeNo")== null ? "":request.getParameter("uketukeNo").replace("-", "");
        //店舗No
        final String uketukePostCd = request.getParameter("uketukePostCd")== null ? "":request.getParameter("uketukePostCd");
        // 法人仲介受付日時
        final String uketukeDate = request.getParameter("uketukeDate")== null ? "":request.getParameter("uketukeDate");
        //法人企業名
        final String kigyouName = request.getParameter("kigyouName")== null ? "":request.getParameter("kigyouName");
        //法人仲介依頼担当
        final String iraiTantouName = request.getParameter("iraiTantouName")== null ? "":request.getParameter("iraiTantouName");
        //連絡先
        final String iraiTelNo = request.getParameter("iraiTelNo")== null ? "":request.getParameter("iraiTelNo");
        //更新日
        final String uketukeUpdDate = request.getParameter("uketukeUpdDate")== null ? "":request.getParameter("uketukeUpdDate");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatUketukeUpdDate = format.format(format.parse(uketukeUpdDate));
        formatUketukeUpdDate = formatUketukeUpdDate.replace("-", "/");
        
        ExecutorService executor = Executors.newCachedThreadPool();
        // ログイン情報
        Map<String, Object> sessionLoginInfo = getLoginInfo();
        // 会社コード
        final String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
        try{
            //担当者コードを取得
            String userCd = (String)getLoginInfo().get(Consts.USER_CD);
            Map<String, Object> delData = UKG02010Services.updHoujinnTyuukaiIlaiFlag(kaisyacd, uketukeNo, formatUketukeUpdDate, userCd);
            if ("ERRS003".equals(delData.get("rst_code"))) {
                ukg02010Data.put("status", false);
                ukg02010Data.put("MSG", Util.getServerMsg("ERRS003"));
                return "ajaxProcess";
            } else if ("ERRS999".equals(delData.get("rst_code"))) {
                ukg02010Data.put("status", false);
                ukg02010Data.put("MSG", Util.getServerMsg("ERRS002", "削除処理"));
                return "ajaxProcess";
            }
            ukg02010Data.put("status", true);
        }catch(Exception e){
            ukg02010Data.put("status", false);
            return "ajaxProcess";
        }    
        // 法人仲介依頼削除送信
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    delChukaiUketukeMeru(kaisyacd,uketukePostCd,uketukeNo, kigyouName,iraiTantouName,iraiTelNo, uketukeDate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return "ajaxProcess";
    };
    /**
     * 受付本件を削除します
     * @return 
     * @throws Exception 
     */
	public String delUketuke(){
        HttpServletRequest request = getRequest();
        // ログイン情報
        Map<String, Object> sessionLoginInfo = getLoginInfo();
        // 会社コード
        final String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
        //受付No
        final String uketukeNo = String.valueOf(request.getSession().getAttribute(Consts.UKETUKENO));
        //担当者コードを取得
        final String userCd = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
        //更新日
        final String uketukeUpdDate = request.getParameter("pv_rst_upd_date")== null ? "":request.getParameter("pv_rst_upd_date");
        
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String formatUketukeUpdDate = format.format(format.parse(uketukeUpdDate));
            
            UKG02010Services.updHoujinnTyuukaiIlaiFlag(kaisyacd, uketukeNo, formatUketukeUpdDate, userCd);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return "ajaxProcess";
    }

    /**
     * [説 明] 法人仲介依頼削除送信
     * @param kaisyacd,会社コード
     * @param uketukePostCd,店舗コード
     * @param uketukeNo,受付No
     * @param kigyouName,法人企業名
     * @param iraiTantouName,法人仲介依頼担当
     * @param iraiTelNo,法人仲介依頼担当電話番号
     * @param uketukeDate,受付日
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void delChukaiUketukeMeru(String kaisyacd, String uketukePostCd, String uketukeNo,String kigyouName, String iraiTantouName,
        String iraiTelNo,String uketukeDate) throws Exception {

        // メールの送信元アドレス
        String mailSMTP = PropertiesReader.getIntance().getProperty("mailSMTP");
        // メール送信元のポート
        String mailPORT = PropertiesReader.getIntance().getProperty("mailPORT");
        // 送信元アドレス
        String sendAddress = PropertiesReader.getIntance().getProperty("sendAddress");
        // メールの送信元の認証パスワード
        String sendPassword = PropertiesReader.getIntance().getProperty("sendPassword");
        // 法人仲介依頼削除送信メールタイトル
        String iraiDeleteMailTitle = PropertiesReader.getIntance().getProperty("iraiDeleteMailTitle");
        // 法人仲介依頼削除送信メールBCC
        String iraiMailBcc = PropertiesReader.getIntance().getProperty("iraiMailBcc");
        // 法人仲介依頼削除送信メール本文
        String iraiDeleteMailPath = PropertiesReader.getIntance().getProperty("iraiDeleteMailPath");
        // 送信メールの末尾本文
        String alarmMailFooterPath = PropertiesReader.getIntance().getProperty("alarmMailFooterPath");
        // 法人仲介依頼削除送信メール担当者所属コード
        String chukaiMailSyozokuCd = PropertiesReader.getIntance().getProperty("iraiMailSyozokuCd");
        
        Transport transport = null;
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", mailSMTP);
        props.put("mail.smtp.port", mailPORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.checkserveridentity", "false");
        props.put("mail.smtp.ssl.trust", mailSMTP);
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);

        // メールアドレス取得
        List<InternetAddress> mailList1 = new ArrayList<InternetAddress>();
        List<InternetAddress> mailList2 = new ArrayList<InternetAddress>();

        // 仲介依頼先店舗代表メールアドレスと仲介担当メールアドレス取得
        List<Map<String, Object>> listMap1 = (List<Map<String, Object>>) UKG02010Services
                .getTempoDaihyouAndTanTouMailAddress(kaisyacd, uketukePostCd,uketukeNo).get("rst_mailAddress_list");

        // 仲介依頼先店舗の各社員メールアドレスリスト取得
        List<Map<String, Object>> listMap2 = (List<Map<String, Object>>) UKG02010Services.getShainMailAddress(
                kaisyacd, uketukePostCd, chukaiMailSyozokuCd).get("rst_shainMailAddress_list");

        if (listMap1 != null && listMap1.size() > 0) {
            for(Map<String, Object> map : listMap1) {
                if ((String) map.get("E_MAIL_ADDRESS") != null) {
                    mailList1.add(new InternetAddress((String) map.get("E_MAIL_ADDRESS")));
                }
            }
        }

        if (listMap2 != null && listMap2.size() > 0) {
            for(Map<String, Object> map : listMap2) {
                if ((String) map.get("E_MAIL_ADDRESS") != null) {
                    mailList2.add(new InternetAddress((String) map.get("E_MAIL_ADDRESS")));
                }
            }
        }

        InternetAddress[] address1 = new InternetAddress[mailList1.size()];
        for (int i = 0; i < mailList1.size(); i++) {
            address1[i] = mailList1.get(i);
        }

        InternetAddress[] address2 = new InternetAddress[mailList2.size()];
        for (int i = 0; i < mailList2.size(); i++) {
            address2[i] = mailList2.get(i);
        }

        if (address1 == null || address1.length == 0 || address2 == null || address2.length == 0) {
            return;
        }

        try {
            // メールを設定
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(sendAddress));
            msg.setRecipients(Message.RecipientType.TO, address1);
            msg.setRecipients(Message.RecipientType.CC, address2);
            msg.setRecipients(Message.RecipientType.BCC, iraiMailBcc);
            msg.setSubject(MimeUtility.encodeText(iraiDeleteMailTitle.replace("%1", kigyouName), "iso-2022-jp", "B"));
            msg.setSentDate(new Date());

            // メール内容を設定
            MimeBodyPart mbp1 = new MimeBodyPart();
            InputStreamReader fr = new InputStreamReader(new UnicodeInputStream(new FileInputStream(iraiDeleteMailPath), "UTF-8"), "UTF-8");
            // メールフッタ
            InputStreamReader frd = new InputStreamReader(new UnicodeInputStream(new FileInputStream(alarmMailFooterPath), "UTF-8"), "UTF-8");
            @SuppressWarnings("resource")
            BufferedReader br = new BufferedReader(fr);
            BufferedReader bfr = new BufferedReader(frd);
            StringBuffer sb = new StringBuffer();
            String str = "";
            while ((str = br.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
            while ((str = bfr.readLine()) != null) {
                sb.append(str);
                sb.append("\n");
            }
            String msgContent = sb.toString().replace("{HOUJIN_NAME}", kigyouName);
            msgContent = msgContent.replace("{HOUJIN_CHUKAI_DATE}", uketukeDate);
            msgContent = msgContent.replace("{UKETUKE_NO}", uketukeNo);
            msgContent = msgContent.replace("{CHUKAI_TANTOU_NAME}", iraiTantouName);
            String iraiTelStr = "";
            if( !"".equals(iraiTantouName) && !"".equals(iraiTelNo) ) {
                iraiTelStr = "（" + iraiTelNo + "）";
            }
            msgContent = msgContent.replace("{CHUKAI_TANTOU_TEL}", iraiTelStr);
            mbp1.setText(msgContent, "iso-2022-jp");
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(mbp1);
            msg.setContent(mp);

            // メールを送信
            transport = session.getTransport("smtp");
            if ("true".equals(props.get("mail.smtp.auth"))) {
                transport.connect(mailSMTP, sendAddress, sendPassword);
            } else {
                transport.connect();
            }
            transport.sendMessage(msg, msg.getAllRecipients());

        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            transport.close();
        }
    }
      //2020/05/06 ADD END 楊朋朋 C43040-2_法人の入力項目追加対応
}
