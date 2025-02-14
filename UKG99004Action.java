/**
 * @システム名: 受付システム
 * @ファイル名: UKG99004Action.java
 * @更新日付：: 2012/06/26
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴：2013/02/28	謝超(SYS)		1.01		(案件番号C37074)チラシ作成機能の追加
 * 2013/09/03	趙雲剛(SYS)	1.02		(案件C37103-1)チラシ作成機能の追加-STEP2
 * 更新履歴：2014/06/10	郭凡(SYS)       1.03  (案件番号C38117)セールスポイント編集機能の追加
 * 更新履歴：2015/10/16	孫(SYS)         1.04  (案件C39077-3)特記事項チラシ掲載対応
 * 更新履歴：2018/01/19	肖剣生(SYS)     1.05  (案件C42036)チラシレイアウト変更
 * 更新履歴：2020/04/14   程旋(SYS)     1.06  C44030_お部屋探しNaviマルチブラウザ対応
 * 更新履歴：2020/02/27   劉恆毅        1.07  C44041_物件チラシの一括作成
 * 更新履歴：2020/04/09   劉恆毅(SYS)   1.08  C44017-1_チラシ型物件案内カードの物件画像選択機能
 * 更新履歴:2021/01/05   南柯創(SYS)     1.09  (案件C45011_物件チラシメール送信)
 */
package jp.co.token.uketuke.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.UnicodeInputStream;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG99004Service;

/**
 * <pre>
 * [機 能] チラシ選択
 * [説 明] チラシ選択する。
 * @author [作 成] 2012/06/26  楊きょう(SYS)
 * </pre>
 */
public class UKG99004Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;

	/** チラシ選択画面用データ */
	// ########## 2013/02/28 謝超UPD Start (案件番号C37074)チラシ作成機能の追加
	// private Map<String, Object> ukg99004Data = null;
	private HashMap<String, Object> ukg99004Data = null;
	// ########## 2013/02/28 謝超 UPD End

//	2021/01/07 ADD START 南柯創(SYS) C45011_物件チラシメール送信
	private Map<String, Object> result = null;

	/**
	 * @return result
	 */
	public Map getResult() {
		return result;
	}
//	2021/01/07 ADD END 南柯創(SYS) C45011_物件チラシメール送信
	/** チラシ選択画面サービス */
	private IUKG99004Service UKG99004Services;

	/**
	 * チラシ選択画面用データ
	 *
	 * @return ukg99004Data 画面用データ
	 */
	// ########## 2013/02/28 謝超UPD Start (案件番号C37074)チラシ作成機能の追加
	// public Map<String, Object> getUkg99004Data() {
	public HashMap<String, Object> getUkg99004Data() {
	// ########## 2013/02/28 謝超 UPD End
		return ukg99004Data;
	}

	/**
	 * チラシ選択画面用データ
	 *
	 * @param ukg99004Data
	 *            画面用データ
	 */
	// ########## 2013/02/28 謝超UPD Start (案件番号C37074)チラシ作成機能の追加
	// public void setUkg99004Data(Map<String, Object> ukg99004Data) {
	public void setUkg99004Data(HashMap<String, Object> ukg99004Data) {
	// ########## 2013/02/28 謝超 UPD End

		this.ukg99004Data = ukg99004Data;
	}

	/**
	 * チラシ選択画面サービス
	 *
	 * @param uKG99004Services
	 *            画面サービス
	 */
	public void setUKG99004Services(IUKG99004Service uKG99004Services) {

		UKG99004Services = uKG99004Services;
	}

	/**
	 * <pre>
	 * [説 明]  チラシ選択。
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

		Map<String, Object> loginInfo = getLoginInfo();

		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		// ########## 2014/06/10 郭凡 ADD End

		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 担当者コードを取得
		String tantouCd = String.valueOf(loginInfo.get(Consts.USER_CD));

		// リクエストURL,設定ファイル(uketuke.properties).tirasiURL
		String printActionId = PropertiesReader.getIntance().getProperty("tirasiURL");

		// 引数取得
		List<Map<String, Object>> bukkenHeyaList = new ArrayList<Map<String, Object>>();

		String bukkenNo1 = request.getParameter("bukkenNo1");
		String heyaNo1 = request.getParameter("heyaNo1");
		String bukkenNo2 = request.getParameter("bukkenNo2");
		String heyaNo2 = request.getParameter("heyaNo2");
		if (!Util.isEmpty(heyaNo1) && "ISO-8859-1".equals(Util.getEncoding(heyaNo1))) {
			heyaNo1 = new String(heyaNo1.getBytes("ISO-8859-1"), "UTF-8");
		}
		if (!Util.isEmpty(heyaNo2) && "ISO-8859-1".equals(Util.getEncoding(heyaNo2))) {
			heyaNo2 = new String(heyaNo2.getBytes("ISO-8859-1"), "UTF-8");
		}
		if (!Util.isEmpty(bukkenNo1) && !Util.isEmpty(heyaNo1)) {
			Map<String, Object> bukkenHeyaData = new HashMap<String, Object>();
            //2020/02/19   劉恆毅(SYS) UPD START 案件C44041_物件チラシの一括作成
	        //bukkenHeyaData.put("bukkenNo", bukkenNo1);
            //bukkenHeyaData.put("heyaNo", heyaNo1);
            //bukkenHeyaList.add(bukkenHeyaData);
            String[] bukkenNoArray = bukkenNo1.split(",");
            String[] heyaNoArray = heyaNo1.split(",");
            for (int i = 0 ; i < bukkenNoArray.length ; i++) {
                bukkenHeyaData = new HashMap<String, Object>();
                bukkenHeyaData.put("bukkenNo", bukkenNoArray[i]);
                bukkenHeyaData.put("heyaNo", heyaNoArray[i]);
                bukkenHeyaList.add(bukkenHeyaData);
            }
            //2020/02/19   劉恆毅(SYS) UPD END 案件C44041_物件チラシの一括作成
		}
		if (!Util.isEmpty(bukkenNo2) && !Util.isEmpty(heyaNo2)) {
			Map<String, Object> bukkenHeyaData2 = new HashMap<String, Object>();
			bukkenHeyaData2.put("bukkenNo", bukkenNo2);
			bukkenHeyaData2.put("heyaNo", heyaNo2);
			bukkenHeyaList.add(bukkenHeyaData2);
		}
		// チラシタイプ取得
		Map<String, Object> chirashiMapDb = UKG99004Services.getChirashi();
		List<Map<String, Object>> chirashiListDb = (List<Map<String, Object>>) chirashiMapDb.get("rst_list");
		// 画面表示処理
		List<Map<String, Object>> chirashiList = chirashiGamenHyouji(bukkenHeyaList, chirashiListDb);

		//2018/01/19 肖剣生　ADD　START　案件C42036(チラシレイアウト変更)
		String[] tempName = null;
		for(int j = 0; j < chirashiList.size(); j++){
			tempName = chirashiList.get(j).get("chirashiName").toString().split("\\(");
			if(tempName.length > 1 && tempName[0].contains("物件掲載用")){
				chirashiList.get(j).put("KOUMOKU_SB_NAME1", tempName[0].toString());
				chirashiList.get(j).put("KOUMOKU_SB_NAME2", tempName[1].substring(0, tempName[1].length()-1));
			}else{
				chirashiList.get(j).put("KOUMOKU_SB_NAME1", chirashiList.get(j).get("chirashiName").toString());
			}
		}
		//2018/01/19 肖剣生　ADD　END

		// 色コード取得
		Map<String, Object> iroMapDb = UKG99004Services.getIro();
		List<Map<String, Object>> iroListDb = (List<Map<String, Object>>) iroMapDb.get("rst_list");

		List<Map<String, Object>> iroList = new ArrayList<Map<String, Object>>();
		//
		for (Map<String, Object> iroData : iroListDb) {
			Map<String, Object> iroMap = new HashMap<String, Object>();
			iroMap.put("iro", iroData.get("KOUMOKU_SB_VAL"));
			iroMap.put("iroName", iroData.get("KOUMOKU_SB_NAME"));
			iroMap.put("iroCode", iroData.get("KOUMOKU_SB_CD"));
			//2013/09/03   趙雲剛   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2
			iroMap.put("iroRyakuName", iroData.get("KOUMOKU_SB_RYAKU_NAME"));
			// 2013/09/03   趙雲剛   ADD END
			iroList.add(iroMap);
		}

		// 画面表示
		iroList = iroHyoujiStyle(iroList);
		ukg99004Data = new HashMap<String, Object>();

		// 2015/10/16 孫博易 ADD Start (案件C39077-3)特記事項チラシ掲載対応
		if (bukkenHeyaList != null) {
			String bukenAreaFlg = request.getParameter("bukenAreaFlg");
			//2020/02/27 劉恆毅 UPD Start C44041_物件チラシの一括作成
//			String bukkenNo = "";
//			String heyaNo = "";
//			if ("_right".equals(bukenAreaFlg)) {
//				bukkenNo = bukkenNo2;
//				heyaNo = heyaNo2;
//			} else {
//				bukkenNo = bukkenNo1;
//				heyaNo = heyaNo1;
//			}
//			Map<String, Object> resultTokkijikouInfo = UKG99004Services.getTokkijikou(bukkenNo,heyaNo);
//			List<Map<String, Object>> tokkijikouInfoList = (List<Map<String, Object>>) resultTokkijikouInfo.get("rst_list");
//			if (tokkijikouInfoList != null && tokkijikouInfoList.size() == 1) {
//				String tokkijikouBukken = (String) tokkijikouInfoList.get(0).get("TOKKI_JIKOU_BUKKEN");
//				String tokkijikouHeya = (String) tokkijikouInfoList.get(0).get("TOKKI_JIKOU_HEYA");
//				// 物件建物情報と部屋情報の両方の特記事項（Web掲載用）が登録される場合
//				if (tokkijikouBukken != null && tokkijikouHeya != null) {
//					ukg99004Data.put("bikouTextarea", tokkijikouHeya);
//				} else if (tokkijikouBukken == null && tokkijikouHeya != null){
//					ukg99004Data.put("bikouTextarea", tokkijikouHeya);
//				} else if (tokkijikouBukken != null && tokkijikouHeya == null){
//					ukg99004Data.put("bikouTextarea", tokkijikouBukken);
//				} else {
//					ukg99004Data.put("bikouTextarea", "");
//				}
//			}
			String[] bukkenNoArr = null;
			String[] heyaNoArr = null;
			if ("_right".equals(bukenAreaFlg)) {
				bukkenNoArr = bukkenNo2.split(",");
				heyaNoArr = heyaNo2.split(",");
			} else {
				bukkenNoArr = bukkenNo1.split(",");
				heyaNoArr = heyaNo1.split(",");
			}
			List<Map<String, Object>> bukkenInfoList = new ArrayList<Map<String,Object>>();
			Map<String, Object> resultTokkijikouInfo = null;
			List<Map<String, Object>> tokkijikouInfoList = null;
			Map<String, Object> bukkenInfo = null;
			for(int i = 0 ; i < bukkenNoArr.length ; i++ ) {
				bukkenInfo = new HashMap<String, Object>();
				resultTokkijikouInfo = UKG99004Services.getTokkijikou(bukkenNoArr[i],heyaNoArr[i]);
				tokkijikouInfoList = (List<Map<String, Object>>) resultTokkijikouInfo.get("rst_list");
				if (tokkijikouInfoList != null && tokkijikouInfoList.size() == 1) {
					String tokkijikouBukken = (String) tokkijikouInfoList.get(0).get("TOKKI_JIKOU_BUKKEN");
					String tokkijikouHeya = (String) tokkijikouInfoList.get(0).get("TOKKI_JIKOU_HEYA");
					// 物件建物情報と部屋情報の両方の特記事項（Web掲載用）が登録される場合
					if (tokkijikouBukken != null && tokkijikouHeya != null) {
						bukkenInfo.put("bikouTextarea", tokkijikouHeya);
					} else if (tokkijikouBukken == null && tokkijikouHeya != null){
						bukkenInfo.put("bikouTextarea", tokkijikouHeya);
					} else if (tokkijikouBukken != null && tokkijikouHeya == null){
						bukkenInfo.put("bikouTextarea", tokkijikouBukken);
					} else {
						bukkenInfo.put("bikouTextarea", "");
					}
					bukkenInfo.put("bukkenName", (String) tokkijikouInfoList.get(0).get("BUKKEN_NAME"));
					bukkenInfo.put("bukkenSalesPoint", (String) tokkijikouInfoList.get(0).get("BUKKEN_SALES_POINT"));
					bukkenInfo.put("heyaSalesPoint", (String) tokkijikouInfoList.get(0).get("HEYA_SALES_POINT"));
					//2021/01/06 ADD START 南柯創(SYS) C45011_物件チラシメール送信
					bukkenInfo.put("homemateBukkenNo", String.valueOf(tokkijikouInfoList.get(0).get("HOMEMATE_BUKKEN_NO")));
					bukkenInfo.put("jusyo1", String.valueOf(tokkijikouInfoList.get(0).get("JUSYO1")));
					bukkenInfo.put("jusyo2", String.valueOf(tokkijikouInfoList.get(0).get("JUSYO2")));
					bukkenInfo.put("HEYA_NO", String.valueOf(tokkijikouInfoList.get(0).get("HEYA_NO")));
					bukkenInfo.put("madori", String.valueOf(tokkijikouInfoList.get(0).get("MADORI")));
					bukkenInfo.put("yatin", String.valueOf(tokkijikouInfoList.get(0).get("YATIN")));
					//2021/01/06 ADD END 南柯創(SYS) C45011_物件チラシメール送信
				}
				bukkenInfo.put("bukkenNo", bukkenNoArr[i]);
				bukkenInfo.put("heyaNo", heyaNoArr[i]);
				
		    //2020/03/31 劉恆毅 ADD Start C44017-1_チラシ型物件案内カードの物件画像選択機能
            //内観、外観画像を取得
            Map<String, Object> gazouList =  UKG99004Services.getGazou(bukkenNoArr[i],  heyaNoArr[i]);
            bukkenInfo.put("naikanGazouList", gazouList.get("RES_NAIKAN_GAZOU_LIST"));
            bukkenInfo.put("gaikanGazouList", gazouList.get("RES_GAIKAN_GAZOU_LIST"));
            //2020/03/31 劉恆毅 ADD End C44017-1_チラシ型物件案内カードの物件画像選択機能
				bukkenInfoList.add(bukkenInfo);
			}
			ukg99004Data.put("bukkenInfoList", bukkenInfoList);
			//2020/02/27 劉恆毅 UPD End C44041_物件チラシの一括作成
		}
		// 2015/10/16 孫博易 ADD End (案件C39077-3)特記事項チラシ掲載対応
		ukg99004Data.put("kaisyaCd", kaisyaCd);
		ukg99004Data.put("tantouCd", tantouCd);
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
//		ukg99004Data.put("postCd", loginInfo.get(Consts.POST_CD));
		ukg99004Data.put("postCd", uketukePostInfo.get(Consts.POST_CD));
		// ########## 2014/06/10 郭凡 UPD End
		ukg99004Data.put("userCd", loginInfo.get(Consts.LOGIN_USER_CD));
		ukg99004Data.put("printActionId", printActionId);
		ukg99004Data.put("bukkenHeyaList", bukkenHeyaList);

		ukg99004Data.put("chirashiList", chirashiList);
		ukg99004Data.put("iroList", iroList);
		ukg99004Data.put("bukenAreaFlg", request.getParameter("bukenAreaFlg"));
		//2020/02/27 劉恆毅 UPD Start C44041_物件チラシの一括作成
		ukg99004Data.put("postCd_loginInfo", loginInfo.get(Consts.POST_CD));
		ukg99004Data.put("bukkenListFlag", request.getParameter("bukkenListFlag"));
		//2020/02/27 劉恆毅 UPD End C44041_物件チラシの一括作成
		// ########## 2013/02/28 謝超 ADD Start (案件番号C37074)チラシ作成機能の追加
		String yobidasiKb = request.getParameter("yobidasiKb");
		ukg99004Data.put("yobidasiKb", yobidasiKb);
		if(request.getSession().getAttribute(Consts.UKG99004_SESSION) == null) {
			request.getSession().setAttribute(Consts.UKG99004_SESSION, ukg99004Data);
		}
		// ########## 2013/02/28 謝超 ADD End
		// 2013/11/20   趙雲剛   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2）
		String tirashiTitleName = PropertiesReader.getIntance().getProperty(Consts.TIRASHI_PDF_WINDOWSHOWINGNAME);
		ukg99004Data.put("tirashiTitleName", tirashiTitleName);
		String tirashiDownloadName = PropertiesReader.getIntance().getProperty(Consts.TIRASHI_PDF_DOWNLOADNAME);
		ukg99004Data.put("tirashiDownloadName", tirashiDownloadName);
		// 2013/11/20   趙雲剛   ADD END
		/*2020/04/14 程旋 ADD START C44030_お部屋探しNaviマルチブラウザ対応*/
		String chouhyouServer = PropertiesReader.getIntance().getProperty(Consts.CHOUHYOUSERVER);
		ukg99004Data.put("chouhyouServer", chouhyouServer);
		/*2020/04/14 程旋 ADD END C44030_お部屋探しNaviマルチブラウザ対応*/
		//2021/01/05 ADD START 南柯創(SYS) C45011_物件チラシメール送信
		String mailTitle = PropertiesReader.getIntance().getProperty(Consts.MAIL_TITLE);
		String mailTxtPath = PropertiesReader.getIntance().getProperty(Consts.MAIL_TXT_PATH);
		String mailHeyaPath = PropertiesReader.getIntance().getProperty(Consts.MAILHEYA_TXT_PATH);
		InputStreamReader mailTxt = new InputStreamReader(new UnicodeInputStream(new FileInputStream(mailTxtPath), "UTF-8"), "UTF-8");
		BufferedReader mailTxtBufferedReader = new BufferedReader(mailTxt);
		StringBuffer mailTxtStringBuffer = new StringBuffer();
        String mailtxtString = "";
        //Eメール本文作成
        int i = 0;
        while ((mailtxtString = mailTxtBufferedReader.readLine()) != null) {
        	i++;
        	if ( i == 1) {
        		mailTxtStringBuffer.append(mailtxtString);
			}else {
				mailTxtStringBuffer.append("\n");
				mailTxtStringBuffer.append(mailtxtString);
			}
        }
        mailTxtBufferedReader.close();
        String mailNaiyou = mailTxtStringBuffer.toString();
        InputStreamReader mailHeyaTxt = new InputStreamReader(new UnicodeInputStream(new FileInputStream(mailHeyaPath), "UTF-8"),"UTF-8");
        BufferedReader mailHeyaTxtBufferedReader = new BufferedReader(mailHeyaTxt);
        StringBuffer mailHeyaTxtStringBuffer = new StringBuffer();
        String mailHeyatxtString = "";
        int j = 0;
        while ((mailHeyatxtString = mailHeyaTxtBufferedReader.readLine()) != null) {
        	j++;
        	if ( j == 1) {
        		mailHeyaTxtStringBuffer.append(mailHeyatxtString);
			}else {
				mailHeyaTxtStringBuffer.append("\n");
				mailHeyaTxtStringBuffer.append(mailHeyatxtString);
			}
		}
        mailHeyaTxtBufferedReader.close();
        String mailHeyaJyouhou = mailHeyaTxtStringBuffer.toString();
		HttpSession session = request.getSession();
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		Map<String, Object> mailNaiyouData =  UKG99004Services.getMailNaiyou(kaisyaCd,uketukeNo,tantouCd);
		List<Map<String, Object>> mailInfo = (List<Map<String, Object>>) mailNaiyouData.get("rst_mailjyouhou");
		List<Map<String, Object>> bukkenInfoDataLit = (List<Map<String, Object>>) ukg99004Data.get("bukkenInfoList");
		String mailHeyaJyouhouTxt = "";
		if (bukkenInfoDataLit != null && bukkenInfoDataLit.size() > 0) {
			for (Iterator<Map<String, Object>> iterator = bukkenInfoDataLit.iterator(); iterator.hasNext();) {
				Map<String, Object> bukkenInfo = (Map<String, Object>) iterator.next();
				String homemateBukkenNo = String.valueOf(bukkenInfo.get("homemateBukkenNo"));
				String jusyo1 = String.valueOf(bukkenInfo.get("jusyo1"));
				String jusyo2 = String.valueOf(bukkenInfo.get("jusyo2"));
				String houseName = String.valueOf(bukkenInfo.get("bukkenName"));
				String heyaNo = String.valueOf(bukkenInfo.get("HEYA_NO"));
				String madori = String.valueOf(bukkenInfo.get("madori"));
				String yatin = String.valueOf(bukkenInfo.get("yatin"));
				String homeMateURL = PropertiesReader.getIntance().getProperty(Consts.HOMEMATEURL);
				String url = homeMateURL + homemateBukkenNo + heyaNo + "/";
				mailHeyaJyouhou = mailHeyaJyouhou.replace("{HOMEMATE_BUKKEN_NO}",homemateBukkenNo);
				mailHeyaJyouhou = mailHeyaJyouhou.replace("{JUSYO1}",jusyo1);
				mailHeyaJyouhou = mailHeyaJyouhou.replace("{JUSYO2}",jusyo2);
				mailHeyaJyouhou = mailHeyaJyouhou.replace("{BUKKEN_NAME}",houseName);
				mailHeyaJyouhou = mailHeyaJyouhou.replace("{HEYA_NO}",heyaNo);
				mailHeyaJyouhou = mailHeyaJyouhou.replace("{MADORI}",madori);
				mailHeyaJyouhou = mailHeyaJyouhou.replace("{YATIN}",yatin);
				mailHeyaJyouhou = mailHeyaJyouhou.replace("{HOMEMATE_URL}", url);
				if (iterator.hasNext()) {
					/*2021/02/04 UPDATE START 南柯創 C45011_物件チラシメール送信  不具合C45011-004*/
					//mailHeyaJyouhouTxt += mailHeyaJyouhou + "\r\n";
					mailHeyaJyouhouTxt += mailHeyaJyouhou + "\r\n\n";
					/*2021/02/04 UPDATE END 南柯創 C45011_物件チラシメール送信  不具合C45011-004*/
					mailHeyaJyouhou = mailHeyaTxtStringBuffer.toString();
				}else {
					mailHeyaJyouhouTxt += mailHeyaJyouhou;
				}
			}

		}
		if (mailInfo != null && mailInfo.size() > 0) {
			String kokyakuName = String.valueOf(mailInfo.get(0).get("KOKYAKU_NAME"));
			String eMailAddress = String.valueOf(mailInfo.get(0).get("KOKYAKU_E_MAIL_ADDRESS"));
			String postName = String.valueOf(mailInfo.get(0).get("POST_NAME"));
			String tantouName = String.valueOf(mailInfo.get(0).get("TANTOU_NAME"));
			String telNo = String.valueOf(mailInfo.get(0).get("TEL_NO"));
			String faxNo = String.valueOf(mailInfo.get(0).get("FAX_NO"));
			String businessHours = String.valueOf(mailInfo.get(0).get("BUSINESS_HOURS"));
			String holiday = String.valueOf(mailInfo.get(0).get("HOLIDAY"));
			String tenpoEMailAddress = String.valueOf(mailInfo.get(0).get("TENPO_E_MAIL_ADDRESS"));
			String tantouEMailAddress = String.valueOf(mailInfo.get(0).get("TANTOU_E_MAIL_ADDRESS"));
			mailNaiyou = mailNaiyou.replace("{KOKYAKU_NAME}", kokyakuName);
			mailNaiyou = mailNaiyou.replace("{CHUKAI_TENPO}", postName);
			mailNaiyou = mailNaiyou.replace("{CHUKAI_TANTOU_NAME}", tantouName);
			mailNaiyou = mailNaiyou.replace("{SYOUKAI_BUKKEN}",mailHeyaJyouhouTxt );
			mailNaiyou = mailNaiyou.replace("{TEL_NO}",telNo);
			mailNaiyou = mailNaiyou.replace("{FAX_NO}",faxNo);
			mailNaiyou = mailNaiyou.replace("{BUSINESS_HOURS}",businessHours);
			mailNaiyou = mailNaiyou.replace("{HOLIDAY}",holiday);
			ukg99004Data.put("sourisaki", kokyakuName+"様("+eMailAddress+")");
			ukg99004Data.put("koukyakuAddress", eMailAddress);
			ukg99004Data.put("mailNaiyou", mailNaiyou);
			ukg99004Data.put("tenpoEMailAddress", tenpoEMailAddress);
			ukg99004Data.put("tantouEMailAddress", tantouEMailAddress);
		}
		ukg99004Data.put("mailTitle", mailTitle);
		ukg99004Data.put("shoudanrirekiKB", request.getParameter("shoudanrirekiKB"));
		//2021/01/05 ADD END 南柯創(SYS) C45011_物件チラシメール送信
		return successForward();
	}

	/**
	 * <pre>
	 * [説 明]  チラシリストを設定
	 * @param  bukkenHeyaList 物件/部屋リスト
	 * @param  chirashiListDb チラシリスト
	 * </pre>
	 */
	private List<Map<String, Object>> chirashiGamenHyouji(List<Map<String, Object>> bukkenHeyaList,
	                                                      List<Map<String, Object>> chirashiListDb) {

		List<Map<String, Object>> chirashiList = new ArrayList<Map<String, Object>>();

		if (bukkenHeyaList != null && bukkenHeyaList.size() == 1) {
			for (Map<String, Object> chirashiData : chirashiListDb) {
				if ("1".equals(chirashiData.get("KOUMOKU_SB_VAL"))) {
					Map<String, Object> chirashiMap = new HashMap<String, Object>();
					chirashiMap.put("chirashiName", chirashiData.get("KOUMOKU_SB_NAME"));

					//2018/01/19 肖剣生　ADD　START　案件C42036(チラシレイアウト変更)
					//chirashiMap.put("chirashiSetsuMei", "");
					chirashiMap.put("chirashiSetsuMei", chirashiData.get("BIKOU"));
					//2018/01/19 肖剣生　ADD　END

					chirashiMap.put("reiautoCode", chirashiData.get("KOUMOKU_SB_CD"));
					chirashiMap.put("koumokuSbVal", chirashiData.get("KOUMOKU_SB_VAL"));
			        //2020/03/31 劉恆毅(SYS) ADD Start C44017-1_チラシ型物件案内カードの物件画像選択機能
					//内観、外観画像数
					chirashiMap.put("gazouSu", chirashiData.get("KOUMOKU_SB_RYAKU_NAME"));
			        //2020/03/31 劉恆毅(SYS) ADD End C44017-1_チラシ型物件案内カードの物件画像選択機能
					chirashiList.add(chirashiMap);
				}
			}
			//2020/02/27 劉恆毅 UPD Start C44041_物件チラシの一括作成
        //} else if (bukkenHeyaList != null && bukkenHeyaList.size() == 2) {
        } else if (bukkenHeyaList != null && bukkenHeyaList.size() >= 2) {
		    //2020/02/27 劉恆毅 UPD End C44041_物件チラシの一括作成
			for (Map<String, Object> chirashiData : chirashiListDb) {

				if ("1".equals(chirashiData.get("KOUMOKU_SB_VAL")) || "2".equals(chirashiData.get("KOUMOKU_SB_VAL"))) {
					Map<String, Object> chirashiMap = new HashMap<String, Object>();
					chirashiMap.put("chirashiName", chirashiData.get("KOUMOKU_SB_NAME"));
					chirashiMap.put("chirashiSetsuMei", chirashiData.get("BIKOU"));
					chirashiMap.put("reiautoCode", chirashiData.get("KOUMOKU_SB_CD"));
					chirashiMap.put("koumokuSbVal", chirashiData.get("KOUMOKU_SB_VAL"));
			        //2020/03/31 劉恆毅(SYS) ADD Start C44017-1_チラシ型物件案内カードの物件画像選択機能
					//内観、外観画像数
					chirashiMap.put("gazouSu", chirashiData.get("KOUMOKU_SB_RYAKU_NAME"));
			        //2020/03/31 劉恆毅(SYS) ADD End C44017-1_チラシ型物件案内カードの物件画像選択機能
					chirashiList.add(chirashiMap);
				}
			}

		}

		return chirashiList;
	}

	/**
	 * <pre>
	 * [説 明] 色表示を設定
	 * @param  iroList 色リスト
	 * </pre>
	 */
	private List<Map<String, Object>> iroHyoujiStyle(List<Map<String, Object>> iroList) {

		List<Map<String, Object>> newIroList = new ArrayList<Map<String, Object>>();
		// 色リストのサイズ
		int length = iroList.size();
		int point = 0;
		// 毎行表示の数
		int maigyou = 3;
		while (point < length) {
			Map<String, Object> iroMap = new HashMap<String, Object>();
			for (int k = 1; k < maigyou + 1; k++) {
				iroMap.put("color" + k, iroList.get(point).get("iro"));
				iroMap.put("iroName" + k, iroList.get(point).get("iroName"));
				iroMap.put("iroCode" + k, iroList.get(point).get("iroCode"));
				//2013/09/03   趙雲剛   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2
				iroMap.put("iroRyakuName" + k,  iroList.get(point).get("iroRyakuName"));
				// 2013/09/03   趙雲剛   ADD END
				point++;
				if (point >= length) {
					break;
				}
			}
			newIroList.add(iroMap);
		}
		return newIroList;
	}

	/**
	 * <pre>
	 * [説 明] 商談履歴データ更新処理
	 * @return 更新処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String dataUpdate() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		Map<String, Object> loginInfo = getLoginInfo();
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 受付№
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));

		// 担当者コードを取得
		String tantouCd = String.valueOf(loginInfo.get(Consts.USER_CD));
		// 引数取得,物件/部屋
		//2020/02/27 劉恆毅 UPD Start C44041_物件チラシの一括作成
		/*String bukkenHeyaData = request.getParameter("bukkenHeyaData");*/
		
		/*String bukkenheya[] = bukkenHeyaData.trim().split(",");*/
		
		/*int bukkenheyaNum = bukkenArr.length;*/
		//物件
		String bukkenData = request.getParameter("bukkenData");
		//部屋
		String heyaData = request.getParameter("heyaData");

		String[] bukkenArr = bukkenData.trim().split(",");
		String[] heyaArr = heyaData.trim().split(",");

		//2020/02/27 劉恆毅 UPD End C44041_物件チラシの一括作成

		// 現在日時

		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		if("0".equals(authorityCd)){
		//2020/02/27 劉恆毅 UPD Start C44041_物件チラシの一括作成
			/*if (bukkenheyaNum > 0) {
				UKG99004Services.updateData(kaisyaCd, uketukeNo, bukkenheya[0], bukkenheya[1], tantouCd, new Date());
			}
			if (bukkenheyaNum > 2) {
				UKG99004Services.updateData(kaisyaCd, uketukeNo, bukkenheya[2], bukkenheya[3], tantouCd, new Date());
			}*/
			for(int i = 0 ; i < bukkenArr.length ; i++) {
				UKG99004Services.updateData(kaisyaCd, uketukeNo, bukkenArr[i], heyaArr[i], tantouCd, new Date());
			}
	    //2020/02/27 劉恆毅 UPD End C44041_物件チラシの一括作成
		}
		// ########## 2014/06/10 郭凡 UPD End
		return "ajaxProcess";
	}
	// ########## 2013/02/28 謝超 ADD Start (案件番号C37074)チラシ作成機能の追加
	/**
	 * <pre>
	 * [説 明] 画面データ登録処理
	 * @return 登録処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String dataSave() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		String reiautoCode = request.getParameter("reiautoCode");
		String checkedColorCd = request.getParameter("checkedColorCd");
		String chkTanntousya = request.getParameter("chkTanntousya");

		ukg99004Data = new HashMap<String, Object>();
		ukg99004Data.put("reiautoCode", reiautoCode);
		ukg99004Data.put("checkedColorCd", checkedColorCd);
		ukg99004Data.put("chkTanntousya", chkTanntousya);

		session.setAttribute(Consts.UKG99004_SESSION, ukg99004Data);

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] INFOログを出力する
	 * @return ログを出力結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String printLog() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		String reiautoCode = request.getParameter("reiautoCode");
		String checkedColorCd = request.getParameter("checkedColorCd");
		String userId = request.getParameter("userId");
		String bukkenHeyaData = request.getParameter("bukkenHeyaData");
		String bukkenheya[] = bukkenHeyaData.trim().split(",");

		String message = "[チラシ作成]チラシ印刷 ユーザID：" + userId + "  物件No:" + bukkenheya[0] + " 号室:"+
				bukkenheya[1]+" チラシタイプ：[" + reiautoCode + "] 色：[" + checkedColorCd + "]";
		log.info(message);

		return "ajaxProcess";
	}
	// ########## 2013/02/28 謝超 ADD End
	//2021/01/06 ADD START 南柯創(SYS) C45011_物件チラシメール送信
	/**
	 * <pre>
	 * [説 明]   メールを送信する
	 * @return メールを送信結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String sendMail() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		// Eメールの送信元アドレス
        String mailSMTP = PropertiesReader.getIntance().getProperty("mailSMTP");
        // 送信元アドレス
        String AddressFrom = request.getParameter("tantouEMailAddress");
        if (Util.isEmpty(AddressFrom)) {
        	// 送信元アドレス
        	AddressFrom = PropertiesReader.getIntance().getProperty("sendAddress");
		}
        // 物件案内メールタイトル
        String bukkenGuideMailTitle = request.getParameter("mailTitle");
        // 物件案内メール本文
        String bukkenGuideMailBody = request.getParameter("mailNaiyou");
		Properties props = new Properties();
		props.put("mail.smtp.host", mailSMTP);
		Session session = Session.getInstance(props, null);
		session.setDebug(true);
		String addressTo = request.getParameter("koukyakuAddress");//お顧客Eメールアドレス
		InternetAddress[] addressBCC = new InternetAddress[2];
		addressBCC[0] = new InternetAddress(request.getParameter("tenpoEMailAddress"));//店舗代表Eメールアドレス
		addressBCC[1] = new InternetAddress(request.getParameter("tantouEMailAddress"));//担当者Eメールアドレス
        String ajaxMSG = "";
        String pdfPath = "";
        //2021/03/03 ADD START 南柯創(SYS) C45011_物件チラシメール送信  不具合C45011-006
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        //2021/03/03 ADD END 南柯創(SYS) C45011_物件チラシメール送信  不具合C45011-006
        result = new HashMap<String, Object>();
        String tenpuFlag = request.getParameter("tenpuariFlag");//1:添付あり   2：添付なし
        //2021/03/03 DELETE START 南柯創(SYS) C45011_物件チラシメール送信  不具合C45011-006
//      String workPath = "";
//    	String workUrl = "";
        //2021/03/03 DELETE END 南柯創(SYS) C45011_物件チラシメール送信  不具合C45011-006
        try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(AddressFrom));
			msg.setSubject(bukkenGuideMailTitle, "iso-2022-jp");
			msg.setHeader("Content-Transfer-Encoding", "7bit");
			msg.setSentDate(new Date());
			msg.setRecipients(RecipientType.TO, addressTo);
			msg.setRecipients(RecipientType.BCC, addressBCC);
			if ("1".equals(tenpuFlag)) {
				MimeBodyPart mbp = new MimeBodyPart();
				MimeBodyPart filePart = new MimeBodyPart();
				Multipart mp = new MimeMultipart();
				mbp.setContent(bukkenGuideMailBody,"text/plain;charset=UTF-8");
				String pdfName = request.getParameter("fileName");
				//2021/03/03 UPDATE START 南柯創(SYS) C45011_物件チラシメール送信  不具合C45011-006
//				workPath = PropertiesReader.getIntance().getProperty(Consts.TIRASIFILEPATH);
//				workUrl = PropertiesReader.getIntance().getProperty(Consts.TIRASIFILEURL);
//	        	pdfPath = workPath+pdfName.replace(workUrl, "");//filePath
//				FileDataSource source = new FileDataSource(pdfPath);
				String serverName = PropertiesReader.getIntance().getProperty(Consts.CHOUHYOUSERVER);
				String tirashiDownloadName = PropertiesReader.getIntance().getProperty(Consts.TIRASHI_PDF_DOWNLOADNAME);
				URL url = new URL("http://" + serverName + "/report/" + tirashiDownloadName + ".showPDF?name="+pdfName+"&downName=" + tirashiDownloadName);
				String [] pdfNameArray = pdfName.split("/");
				pdfName = pdfName.substring(pdfName.lastIndexOf("/") + 1);
		    	pdfPath = PropertiesReader.getIntance().getProperty("CoReportsRootPath") + Consts.PDF_TEMP_PATH + pdfNameArray[2];
		    	File sFile  = new File(pdfPath);
		    	HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		    	urlConnection.setRequestMethod("GET");
		    	urlConnection.setDoOutput(true);
		        urlConnection.setDoInput(true);
		        urlConnection.setUseCaches(false);
		        urlConnection.connect();
		        inputStream = urlConnection.getInputStream();
		        if (!sFile.exists()) {
		        	sFile.mkdirs();
		        }
		        outputStream = new FileOutputStream(pdfPath + "/" + pdfName);
		        int len = 0;
				while ((len = inputStream.read()) != -1) {
					outputStream.write(len);
				}
		        urlConnection.disconnect();
		        sFile = new File(pdfPath + "/" + pdfName);
	        	FileDataSource source = new FileDataSource(sFile);
	        	//2021/03/03 UPDATE END 南柯創(SYS) C45011_物件チラシメール送信  不具合C45011-006
				filePart.setDataHandler(new DataHandler(source));
				filePart.setFileName(MimeUtility.encodeWord(source.getFile().getName()));
				mp.addBodyPart(mbp);
				mp.addBodyPart(filePart);
				msg.setContent(mp);
			}else {
				msg.setContent(bukkenGuideMailBody,"text/plain;charset=UTF-8");
			}
			//Sendメール
			Transport.send(msg);
			ajaxMSG= Util.getServerMsg("MSGS005");
			result.put("CODE", "0000000");
			result.put("MSG", ajaxMSG);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("メールの送信に失敗しました。", e);
			ajaxMSG = Util.getServerMsg("ERRS016");
			result.put("MSG", ajaxMSG);
		}finally {
		    //2021/03/03 ADD START 南柯創(SYS) C45011_物件チラシメール送信  不具合C45011-006
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
			//2021/03/03 ADD END 南柯創(SYS) C45011_物件チラシメール送信  不具合C45011-006
		}
		return "ajaxProcess";
	}
	/**
	 * <pre>
	 * [説 明] Eメール送信の場合、商談履歴データ更新処理
	 * @return 更新処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String updateDate() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		Map<String, Object> loginInfo = getLoginInfo();
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 受付№
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		// 担当者コードを取得
		String tantouCd = String.valueOf(loginInfo.get(Consts.USER_CD));
		//物件
		String bukkenData = request.getParameter("bukkenData");
		//部屋
		String heyaData = request.getParameter("heyaData");
		String[] bukkenArr = bukkenData.trim().split(",");
		String[] heyaArr = heyaData.trim().split(",");
		//添付選択Flag
		String tenpuFlag = request.getParameter("tenpuariFlag");
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		if("0".equals(authorityCd)){
			for(int i = 0 ; i < bukkenArr.length ; i++) {
				UKG99004Services.updateMailSoushinDate(kaisyaCd, uketukeNo, bukkenArr[i], heyaArr[i], tantouCd, tenpuFlag, new Date());
			}
		}
		return "ajaxProcess";
	}
	//2021/01/06 ADD END 南柯創(SYS) C45011_物件チラシメール送信
}