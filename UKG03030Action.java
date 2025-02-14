/** 
 *@システム名: 受付システム 
 *@ファイル名: UKG03030Action.java 
 *@更新日付：: 2012/05/28 
 *@Copyright: 2012 token corporation All right reserved  
 * 更新履歴： 2014/06/10	郭凡(SYS)       1.01  (案件番号C38117)セールスポイント編集機能の追加
 * 変更履歴: 2019/07/15    熊振(SYS)        1.02  (案件番号C43100)お客様項目の入力簡素化対応
 */
package jp.co.token.uketuke.action;

import java.util.ArrayList;
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
import jp.co.token.uketuke.service.IUKG02020Service;
import jp.co.token.uketuke.service.IUKG03030Service;

import org.apache.struts2.ServletActionContext;

/**<pre>
 * [機 能] お気に入り物件
 * [説 明] お気に入り物件を検索し、表示する。
 * @author [作 成] SYS_馮強華
 * </pre>
 */
public class UKG03030Action extends BaseAction {
	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;

	/** 物件情報（お気に入り表示）用データ */
	private Map<String, Object> data = new HashMap<String, Object>();
	

	/**物件情報（お気に入り表示）サービス */
	private IUKG03030Service UKG03030Services;
	
	/* 2019/07/15 熊振 C43100_お客様項目の入力簡素化対応 STRAT */
	/*アンケートマスタ*/
	private HashMap<String, Object> ukg03030Data = null;
	
	private IUKG02020Service UKG02020Services;
	/**
	 * @return the ukg03030Data
	 */
	public HashMap<String, Object> getUkg03030Data() {
		return ukg03030Data;
	}
	
	/**
	 * @param uKG03030Services
	 *            the uKG03030Services to set
	 */
	public void setUkg03030Data(HashMap<String, Object> ukg03030Data) {
		this.ukg03030Data = ukg03030Data;
	}
	
	/**
     * サービス設定
     *
     * @param uKG02020Services
     */
    public void setUKG02020Services(IUKG02020Service uKG02020Services) {
        UKG02020Services = uKG02020Services;
    }
	/* 2019/07/15 熊振 C43100_お客様項目の入力簡素化対応 END */

	/**
	 * @param UKG03030Services
	 *            the UKG03030Services to set
	 */
	public void setUKG03030Services(IUKG03030Service uKG03030Services) {

		UKG03030Services = uKG03030Services;
	}
	
	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Map<String, Object> data) {

		this.data = data;
	}

	public Map<String, Object> getData() {

		return data;
	}
	
	/** <pre>
	 * [説 明] お気に入り物件。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {
		//同期フラグ設定
		setAsyncFlg(true);

		// 初期化のパラメター設定
		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();
		data = new HashMap<String, Object>();
		
		data.put("okiniirisu", request.getParameter("okiniIrisuu"));
		if (data.get("okiniirisu") == null || "".equals(data.get("okiniirisu"))) {
			// セッションのログイン情報取得する
			Map<String, Object> sessionLoginInfo = getLoginInfo();
			String kaisyacd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
			String uketukeno = String.valueOf(session.getAttribute(Consts.UKETUKENO));
			Map<String, Object> result = UKG03030Services.getokiniiriCount(kaisyacd, uketukeno);
			data.put("okiniirisu", result.get("rst_okiniiriCount"));
		}
		
		// 外観間取り区分
		String madoriKbn = String.valueOf(request.getParameter("madoriKbn"));
		if (Util.isEmpty(madoriKbn)) {
			madoriKbn = "1";
		}
		data.put("madoriKbn", madoriKbn);

		// セッションIDの取得
		String sessionId = String.valueOf(request.getParameter("sessionId"));
		if (Util.isEmpty(sessionId)) {
			data.put("sessionId", session.getId());
		} else {
			data.put("sessionId", sessionId);
		}
		
		
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
		
		/* 2019/07/11 熊振 C43100_お客様項目の入力簡素化対応 STRAT */
		// アンケートデータを取得
		getAnketoData();
		/* 2019/07/11 熊振 C43100_お客様項目の入力簡素化対応 END */
		
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		data.put("authorityCd", authorityCd);
		// ########## 2014/06/10 郭凡 ADD End
		data.put("tabFlg", request.getParameter("tabFlg"));
		data.put("bukkenNo", request.getParameter("bukkenNo"));
		data.put("heyaNo", request.getParameter("heyaNo"));
		data.put("kibouSetubiHissuCd", request.getParameter("kibouSetubiHissuCd"));
		data.put("kibouSetubiNoHissuCd", request.getParameter("kibouSetubiNoHissuCd"));
		
		data.put("kibouRirekiSeq", request.getParameter("kibouRirekiSeq"));
		data.put("kibouUpdateflg", request.getParameter("kibouUpdateflg"));
		// 2018/10/16 肖剣生(SYS) ADD START 案件C42036デザイン変更新規作成(不具合No076)
		data.put("setubiCount", PropertiesReader.getIntance().getProperty("setubiCount"));
		// 2018/10/16 肖剣生(SYS) ADD END 案件C42036デザイン変更新規作成(不具合No076)
		session.removeAttribute(Consts.UKG03030_LEFT_BEAN_SESSION_KEY);
		session.removeAttribute(Consts.UKG03030_RIGHT_BEAN_SESSION_KEY);
		return successForward();
	}
	
	
	/**
	 * お気に入り数取得する
	 * @return
	 * @throws Exception
	 */
	public String fetchokiiniiriCount () throws Exception {
		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		
		String kaisyaCd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String uketukeno = session.getAttribute(Consts.UKETUKENO).toString();
		
		data = new HashMap<String, Object>();
		Map<String, Object> result = UKG03030Services.getokiniiriCount(kaisyaCd, uketukeno);
		data.put("okiniirisu", result.get("rst_okiniiriCount"));
		
		return "ajaxProcess";
	}
	/**
	 * お気に入り物件検索
	 * 
	 * @return
	 */
	public String okiniiriInfoSearch() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		data = new HashMap<String, Object>();

		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		
		String kaisyaCd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String uketukeno = session.getAttribute(Consts.UKETUKENO).toString();
		
		Map<String, Object> restMap = UKG03030Services.getOkiniiriInfo(kaisyaCd, uketukeno);

		data.put("okiniiriInfoList", restMap.get("rst_okiniiri_list"));
		
		return "okiniiriInfoSearch";
	}
	
	/**
	 * 希望条件検索ワークのデータを削除する
	 * 
	 * @return
	 */
	public String delkibouKesaku() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		data = new HashMap<String, Object>();

		session.removeAttribute(Consts.UKG03011BEAN_SESSION_KEY);
		session.removeAttribute(Consts.UKG03012BEAN_SESSION_KEY);
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		
		String kaisyaCd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String uketukeNo = session.getAttribute(Consts.UKETUKENO).toString();
		String sessionId = request.getParameter("sessionId");
		
		UKG03030Services.delkibouKesaku(sessionId, kaisyaCd, uketukeNo);
		
		return "ajaxProcess";
	}
	/* 2019/07/15 熊振 C43100_お客様項目の入力簡素化対応 STRAT */
	/**
	 * アンケートデータを取得する
	 */
	@SuppressWarnings("unchecked")
	private void getAnketoData() throws Exception {
	    // 同期フラグ設定
        setAsyncFlg(true);
        ukg03030Data = new HashMap<String, Object>();

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
        ukg03030Data.put("kaisyaCd", kaisyaCd);
        ukg03030Data.put("uketukeNo", uketukeNo);

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
        ukg03030Data.put("anketoData", dispAnketo);

        // ログイン情報
//        Map<String, Object> sessionLoginInfo = getLoginInfo();
//        String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
//        ukg03030Data.put("authorityCd", authorityCd);

        // アンケートデータをセッションに保存
        sessionInfo.setAttribute(Consts.UKG02020_ANKETO_DATA, beforeAnketo);
	}	
	/* 2019/07/15 熊振 C43100_お客様項目の入力簡素化対応 END */
}
