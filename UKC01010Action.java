/**
 * @システム名: 受付システム
 * @ファイル名: UKC01010Action
 * @更新日付  : 2013/04/22
 * @Copyright : 2013 token corporation All right reserved
 * 更新履歴   : 2014/06/30 温(SYS)   1.01   (案件C38117)セールスポイント編集機能の追加
 */
package jp.co.token.uketuke.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKC01010Service;
/**
 * <pre>
 * [機 能] 受付状況CSV出力
 * [説 明] 受付状況CSV出力アクション
 * @author [作 成] 2013/04/22 温(SYS)
 * 変更履歴: 2014/01/16 SYS_胡 Jtest対応
 * </pre>
 */
public class UKC01010Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 732633708451744703L;
	/** 受付状況CSVのサービス */
	private IUKC01010Service UKC01010Services;
	/** セッションのログイン情報 */
	private Map<String, Object> sessionLoginInfo = null;
	/** 受付状況CSVカウント数 **/
	private String strCsvListCnt;
	/** 受付状況CSVエラーメッセージ **/
	private String strErrorMsg;

	//担当者
	private static final String TANTOUSYA = "担当者";
	//担当者名
	private static final String TANTOUSYA_NAMAE = "担当者名";
	// ########## 2014/06/23 温 ADD Start (案件C38117)セールスポイント編集機能の追加
	//店舗コード
	private static final String TENPO_CD = "店舗コード";
	//店舗名
	private static final String TENPO_NAME = "店舗名";
	// ########## 2014/06/23 温 ADD End
	//抽出項目（個人）
	private static final String K_MAIL = "(新規)メール数-個人";
	private static final String K_TEL = "(新規)電話数-個人";
	private static final String K_MAIL_YOBIKOMI = "メール呼込来店数-個人";
	private static final String K_TEL_YOBIKOMI = "電話呼込来店数-個人";
	private static final String K_RAITEN_SINKI = "(新規)来店数-個人";
	private static final String K_RAITEN_SAI = "再来店数-個人";
	private static final String K_GENTI = "現地案内数-個人";
	//抽出項目（法人）
	private static final String H_MAIL = "(新規)メール数-法人";
	private static final String H_TEL = "(新規)電話数-法人";
	private static final String H_HOUJIN_KIGYO = "法人企業クラブ数";
	private static final String H_MAIL_YOBIKOMI = "メール呼込来店数-法人";
	private static final String H_TEL_YOBIKOMI = "電話呼込来店数-法人";
	private static final String H_RAITEN_SINKI = "(新規)来店数-法人";
	private static final String H_HOUJIN_YOBIKOMI = "法人企業呼込数";
	private static final String H_RAITEN_SAI = "再来店数-法人";
	private static final String H_GENTI = "現地案内数-法人";
	//抽出項目（仕入）
	private static final String TYOKUSETU_SIRE_TOSU = "直接仕入数(棟数)";
	private static final String TYOKUSETU_SIRE_KOSU = "直接仕入数(戸数)";
	private static final String KANSETU_SIRE_TOSU = "間接仕入数(棟数)";
	private static final String KANSETU_SIRE_KOSU = "間接仕入数(戸数)";
	private static final String AKURIRU_KANBAN = "アクリル看板設置棟数";
	//抽出項目（来店予定）
	private static final String YOYAKU_SINKI = "新規アポイント獲得組数";
	private static final String YOYAKU_SAI = "再来アポイント獲得組数";

	/**
	 * <pre>
	 * [説 明] 受付状況CSVのサービスを取得する。
	 * @return 受付状況CSVのサービス
	 * </pre>
	 */
	public void setUKC01010Services(IUKC01010Service ukc01010Services) {

		UKC01010Services = ukc01010Services;
	}

	/**
	 * 受付状況CSVカウント数を取得する
	 * @return 受付状況CSVカウント数
	 */
	public String getStrCsvListCnt() {
		return strCsvListCnt;
	}

	/**
	 * 受付状況CSVカウント数を設定する
	 */
	public void setStrCsvListCnt(String strCsvListCnt) {
		this.strCsvListCnt = strCsvListCnt;
	}

	/**
	 * 受付状況CSVエラーメッセージを取得する
	 * @return 受付状況CSVエラーメッセージ
	 */
	public String getStrErrorMsg() {
		return strErrorMsg;
	}

	/**
	 * 受付状況CSVエラーメッセージを設定する
	 */
	public void setStrErrorMsg(String strErrorMsg) {
		this.strErrorMsg = strErrorMsg;
	}

	/**<pre>
	 * [説 明] 受付状況CSVデータ取得。
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
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		//会社コード
		String strKaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		//店舗コード
		// ########## 2014/06/23 温 UPD Start (案件C38117)セールスポイント編集機能の追加
		//String strPostcd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		String strPostcd = request.getParameter("postCd");
		// ########## 2014/06/23 温 UPD End
		//日付
		String strDate = request.getParameter("strDate");
		// ########## 2014/06/23 温 ADD Start (案件C38117)セールスポイント編集機能の追加
		//ユーザー登録権限コード
		String strUserAuthority = String.valueOf(sessionLoginInfo.get(Consts.USER_AUTHORITY_CD));
		//支店ブロックコード
		String strSisyaBlkCd = (String) sessionLoginInfo.get(Consts.SISYA_BLK_CD);
		//CSV出力フラグ
		String strCsvFlg = "";
		if("0".equals(strUserAuthority)){
			strCsvFlg = "0";
		}else{
			if(!Util.isEmpty(strPostcd)){
				strCsvFlg = "0";
			}else{
				strCsvFlg = "1";
			}
		}
		// ########## 2014/06/23 温 ADD End
		//ユーザーコード
		String strUser = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		//対象年月
		String strYM = strDate.substring(0,4) + "年" + strDate.substring(4,6) + "月";
		try{
			//出力CSVデータ取得
			// ########## 2014/06/23 温 UPD Start (案件C38117)セールスポイント編集機能の追加
			//Map<String, Object> restInfo = UKC01010Services.getUketukeJoukyoCsv(strKaisyacd, strPostcd, strDate);
			Map<String, Object> restInfo = UKC01010Services.getUketukeJoukyoCsv(strKaisyacd, strPostcd, strDate, strUserAuthority,strSisyaBlkCd);
			// ########## 2014/06/23 温 UPD End
			//出力CSVデータ取得
			List<Map<String, Object>> restList = (List<Map<String, Object>>) restInfo.get("rst_list");
			ArrayList<List<String>> csvList = new ArrayList<List<String>>();
			//受付状況CSVデータカウント数
			strCsvListCnt = String.valueOf(restList.size());
			if(restList.size() != 0) {
				//出力CSVデータをセッションに保持
				// ########## 2014/06/23 温 UPD Start (案件C38117)セールスポイント編集機能の追加
				//csvList = setCsvDataList(restList, strDate);
				csvList = setCsvDataList(restList, strDate, strCsvFlg);
				// ########## 2014/06/23 温 UPD End
				session.setAttribute(Consts.CSV_DOWNLOAD_FILENAME, Consts.UKC01010_CSVFILENAME);
				session.setAttribute(Consts.CSV_DOWNLOAD_LIST, csvList);
				//CSVダウンロードInfoログ
				String strInfo = "受付状況CSVダウンロード ユーザID：" + strUser + "  店舗コード:" + strPostcd + "  対象年月：" + strYM;
				log.info(strInfo);
			}
		} catch (Exception e){
			//CSVエラーメッセージの設定
			strErrorMsg = Util.getServerMsg("ERRS002", "受付状況CSV出力");
		}
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] CSVデータリストは縦横を入れ替える処理
	 * @param restList CSVデータ
	 * @param strDate 日付(YYYYMMDD形式)
	 * @param strCsvFlg CSV出力フラグ
	 * @return 縦横入れ替え後のCSVデータリスト
	 * @throws Exception　処理異常
	 * </pre>
	 */
	// ########## 2014/06/23 温 UPD Start (案件C38117)セールスポイント編集機能の追加
	//private ArrayList<List<String>> setCsvDataList(List<Map<String, Object>> restList, String strDate) throws Exception{
	private ArrayList<List<String>> setCsvDataList(List<Map<String, Object>> restList, String strDate, String strCsvFlg) throws Exception{
		// ########## 2014/06/23 温 UPD End

		//CSV出力オブジェクトを生成する
		ArrayList<List<String>> retCsvList = new ArrayList<List<String>>();
		List<String> csvList = new ArrayList<String>();
		Map<String, Object> csvMap = new HashMap<String, Object>();
		//担当者ごとに、出力オブジェクト設定、抽出項目は23個ある
		// 2014/01/16 Start by SYS_胡  Jtest対応
		/*List<String> csvList1 = null;
		List<String> csvList2 = null;
		List<String> csvList3 = null;
		List<String> csvList4 = null;
		List<String> csvList5 = null;
		List<String> csvList6 = null;
		List<String> csvList7 = null;
		List<String> csvList8 = null;
		List<String> csvList9 = null;
		List<String> csvList10 = null;
		List<String> csvList11 = null;
		List<String> csvList12 = null;
		List<String> csvList13 = null;
		List<String> csvList14 = null;
		List<String> csvList15 = null;
		List<String> csvList16 = null;
		List<String> csvList17 = null;
		List<String> csvList18 = null;
		List<String> csvList19 = null;
		List<String> csvList20 = null;
		List<String> csvList21 = null;
		List<String> csvList22 = null;
		List<String> csvList23 = null;*/
		List<String> csvList1 = new ArrayList<String>();
		List<String> csvList2 = new ArrayList<String>();
		List<String> csvList3 = new ArrayList<String>();
		List<String> csvList4 = new ArrayList<String>();
		List<String> csvList5 = new ArrayList<String>();
		List<String> csvList6 = new ArrayList<String>();
		List<String> csvList7 = new ArrayList<String>();
		List<String> csvList8 = new ArrayList<String>();
		List<String> csvList9 = new ArrayList<String>();
		List<String> csvList10 = new ArrayList<String>();
		List<String> csvList11 = new ArrayList<String>();
		List<String> csvList12 = new ArrayList<String>();
		List<String> csvList13 = new ArrayList<String>();
		List<String> csvList14 = new ArrayList<String>();
		List<String> csvList15 = new ArrayList<String>();
		List<String> csvList16 = new ArrayList<String>();
		List<String> csvList17 = new ArrayList<String>();
		List<String> csvList18 = new ArrayList<String>();
		List<String> csvList19 = new ArrayList<String>();
		List<String> csvList20 = new ArrayList<String>();
		List<String> csvList21 = new ArrayList<String>();
		List<String> csvList22 = new ArrayList<String>();
		List<String> csvList23 = new ArrayList<String>();
		// 2014/01/16 End by SYS_胡  Jtest対応
		//タイトルの設定
		// ########## 2014/06/23 温 ADD Start (案件C38117)セールスポイント編集機能の追加
		if("0".equals(strCsvFlg)){
			csvList.add(TANTOUSYA);
			csvList.add(TANTOUSYA_NAMAE);
		}else{
			csvList.add(TENPO_CD);
			csvList.add(TENPO_NAME);
		}
		// ########## 2014/06/23 温 ADD End
		csvList.add("");
		int theMaxDay = getMonthMaxDay(strDate);
		//タイトルの日付設定
		for (int i = 0; i < theMaxDay; i++) {
			csvList.add(setMonthCalendar(strDate,i));
		}
		//タイトル行をCSV出力オブジェクトに設定
		retCsvList.add(csvList);

		//CSV一覧データ設定
		// ########## 2014/06/23 温 UPD Start (案件C38117)セールスポイント編集機能の追加
		//前回行の担当者コード/店舗コード
		//String strOldTantouCd = "";
		String strOldCd = "";
		//今回行の担当者コード/店舗コード
		//String strNewTantouCd = "";
		String strNewCd = "";
		//次回行の担当者コード/店舗コード
		//String strNextTantouCd = "";
		String strNextCd = "";
		// ########## 2014/06/23 温 UPD End
		//取得された日付の日
		String strCal = "";
		int intCal;
		int index = 0;
		//0を設定するかどうかのフラグ
		boolean bolSetCsvZeroFlg = false;
		//該当担当者設定完了時、CSV出力オブジェクトに設定するかどうかフラグ
		boolean bolSetCsvListFlg = false;
		//縦横を入れ替え
		for (int j = 0; j < restList.size(); j++) {
			csvMap = restList.get(j);
			//担当者コード
			// ########## 2014/06/23 温 UPD Start (案件C38117)セールスポイント編集機能の追加
			//strNewTantouCd = (String)csvMap.get("TANTO");
			if("0".equals(strCsvFlg)){
				strNewCd = (String)csvMap.get("TANTO");
			}else{
				strNewCd = (String)csvMap.get("POST_CD");
			}
			if(!strNewCd.equals(strOldCd)){
				// ########## 2014/06/23 温 UPD End
				//担当者が変わる場合、List初期化
				csvList1 = new ArrayList<String>();
				csvList2 = new ArrayList<String>();
				csvList3 = new ArrayList<String>();
				csvList4 = new ArrayList<String>();
				csvList5 = new ArrayList<String>();
				csvList6 = new ArrayList<String>();
				csvList7 = new ArrayList<String>();
				csvList8 = new ArrayList<String>();
				csvList9 = new ArrayList<String>();
				csvList10 = new ArrayList<String>();
				csvList11 = new ArrayList<String>();
				csvList12 = new ArrayList<String>();
				csvList13 = new ArrayList<String>();
				csvList14 = new ArrayList<String>();
				csvList15 = new ArrayList<String>();
				csvList16 = new ArrayList<String>();
				csvList17 = new ArrayList<String>();
				csvList18 = new ArrayList<String>();
				csvList19 = new ArrayList<String>();
				csvList20 = new ArrayList<String>();
				csvList21 = new ArrayList<String>();
				csvList22 = new ArrayList<String>();
				csvList23 = new ArrayList<String>();
				//担当者コード/店舗コード
				// ########## 2014/06/23 温 UPD Start (案件C38117)セールスポイント編集機能の追加
				if("0".equals(strCsvFlg)){
					//ユーザー登録権限が'0'の場合→担当者
					//担当者コード
					csvList1.add((String)csvMap.get("TANTO"));
					csvList2.add((String)csvMap.get("TANTO"));
					csvList3.add((String)csvMap.get("TANTO"));
					csvList4.add((String)csvMap.get("TANTO"));
					csvList5.add((String)csvMap.get("TANTO"));
					csvList6.add((String)csvMap.get("TANTO"));
					csvList7.add((String)csvMap.get("TANTO"));
					csvList8.add((String)csvMap.get("TANTO"));
					csvList9.add((String)csvMap.get("TANTO"));
					csvList10.add((String)csvMap.get("TANTO"));
					csvList11.add((String)csvMap.get("TANTO"));
					csvList12.add((String)csvMap.get("TANTO"));
					csvList13.add((String)csvMap.get("TANTO"));
					csvList14.add((String)csvMap.get("TANTO"));
					csvList15.add((String)csvMap.get("TANTO"));
					csvList16.add((String)csvMap.get("TANTO"));
					csvList17.add((String)csvMap.get("TANTO"));
					csvList18.add((String)csvMap.get("TANTO"));
					csvList19.add((String)csvMap.get("TANTO"));
					csvList20.add((String)csvMap.get("TANTO"));
					csvList21.add((String)csvMap.get("TANTO"));
					csvList22.add((String)csvMap.get("TANTO"));
					csvList23.add((String)csvMap.get("TANTO"));
					//担当者名
					csvList1.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList2.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList3.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList4.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList5.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList6.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList7.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList8.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList9.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList10.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList11.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList12.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList13.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList14.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList15.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList16.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList17.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList18.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList19.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList20.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList21.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList22.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
					csvList23.add(Util.toEmpty((String)csvMap.get("TANTO_NAME")));
				}else{
					//ユーザー登録権限が'0'以外の場合→店舗
					//店舗コード
					csvList1.add((String)csvMap.get("POST_CD"));
					csvList2.add((String)csvMap.get("POST_CD"));
					csvList3.add((String)csvMap.get("POST_CD"));
					csvList4.add((String)csvMap.get("POST_CD"));
					csvList5.add((String)csvMap.get("POST_CD"));
					csvList6.add((String)csvMap.get("POST_CD"));
					csvList7.add((String)csvMap.get("POST_CD"));
					csvList8.add((String)csvMap.get("POST_CD"));
					csvList9.add((String)csvMap.get("POST_CD"));
					csvList10.add((String)csvMap.get("POST_CD"));
					csvList11.add((String)csvMap.get("POST_CD"));
					csvList12.add((String)csvMap.get("POST_CD"));
					csvList13.add((String)csvMap.get("POST_CD"));
					csvList14.add((String)csvMap.get("POST_CD"));
					csvList15.add((String)csvMap.get("POST_CD"));
					csvList16.add((String)csvMap.get("POST_CD"));
					csvList17.add((String)csvMap.get("POST_CD"));
					csvList18.add((String)csvMap.get("POST_CD"));
					csvList19.add((String)csvMap.get("POST_CD"));
					csvList20.add((String)csvMap.get("POST_CD"));
					csvList21.add((String)csvMap.get("POST_CD"));
					csvList22.add((String)csvMap.get("POST_CD"));
					csvList23.add((String)csvMap.get("POST_CD"));
					//店舗名
					csvList1.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList2.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList3.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList4.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList5.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList6.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList7.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList8.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList9.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList10.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList11.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList12.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList13.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList14.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList15.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList16.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList17.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList18.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList19.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList20.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList21.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList22.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
					csvList23.add(Util.toEmpty((String)csvMap.get("POST_NAME")));
				}
				// ########## 2014/06/23 温 UPD End
				//抽出項目
				csvList1.add(K_MAIL);
				csvList2.add(K_TEL);
				csvList3.add(K_MAIL_YOBIKOMI);
				csvList4.add(K_TEL_YOBIKOMI);
				csvList5.add(K_RAITEN_SINKI);
				csvList6.add(K_RAITEN_SAI);
				csvList7.add(K_GENTI);
				csvList8.add(H_MAIL);
				csvList9.add(H_TEL);
				csvList10.add(H_HOUJIN_KIGYO);
				csvList11.add(H_MAIL_YOBIKOMI);
				csvList12.add(H_TEL_YOBIKOMI);
				csvList13.add(H_RAITEN_SINKI);
				csvList14.add(H_HOUJIN_YOBIKOMI);
				csvList15.add(H_RAITEN_SAI);
				csvList16.add(H_GENTI);
				csvList17.add(TYOKUSETU_SIRE_TOSU);
				csvList18.add(TYOKUSETU_SIRE_KOSU);
				csvList19.add(KANSETU_SIRE_TOSU);
				csvList20.add(KANSETU_SIRE_KOSU);
				csvList21.add(AKURIRU_KANBAN);
				csvList22.add(YOYAKU_SINKI);
				csvList23.add(YOYAKU_SAI);
				//日付index
				index = 1;
			}
			//抽出項目設定
			strCal = (String)csvMap.get("CAL");
			intCal = Integer.parseInt(strCal.substring(6));
			//抽出日付と設定日付異なる場合、抽出日付前の日付の項目に0を設定する
			if(intCal != index){
				for (int k = 0; k < intCal-index; k++) {
					csvList1.add("0");
					csvList2.add("0");
					csvList3.add("0");
					csvList4.add("0");
					csvList5.add("0");
					csvList6.add("0");
					csvList7.add("0");
					csvList8.add("0");
					csvList9.add("0");
					csvList10.add("0");
					csvList11.add("0");
					csvList12.add("0");
					csvList13.add("0");
					csvList14.add("0");
					csvList15.add("0");
					csvList16.add("0");
					csvList17.add("0");
					csvList18.add("0");
					csvList19.add("0");
					csvList20.add("0");
					csvList21.add("0");
					csvList22.add("0");
					csvList23.add("0");
				}
			}
			//抽出日付行のデータ設定
			csvList1.add((String)csvMap.get("K_MAIL"));
			csvList2.add((String)csvMap.get("K_TEL"));
			csvList3.add((String)csvMap.get("K_MAIL_YOBIKOMI"));
			csvList4.add((String)csvMap.get("K_TEL_YOBIKOMI"));
			csvList5.add((String)csvMap.get("K_RAITEN_SINKI"));
			csvList6.add((String)csvMap.get("K_RAITEN_SAI"));
			csvList7.add((String)csvMap.get("K_GENTI"));
			csvList8.add((String)csvMap.get("H_MAIL"));
			csvList9.add((String)csvMap.get("H_TEL"));
			csvList10.add((String)csvMap.get("H_HOUJIN_KIGYO"));
			csvList11.add((String)csvMap.get("H_MAIL_YOBIKOMI"));
			csvList12.add((String)csvMap.get("H_TEL_YOBIKOMI"));
			csvList13.add((String)csvMap.get("H_RAITEN_SINKI"));
			csvList14.add((String)csvMap.get("H_HOUJIN_YOBIKOMI"));
			csvList15.add((String)csvMap.get("H_RAITEN_SAI"));
			csvList16.add((String)csvMap.get("H_GENTI"));
			csvList17.add((String)csvMap.get("TYOKUSETU_SIRE_TOSU"));
			csvList18.add((String)csvMap.get("TYOKUSETU_SIRE_KOSU"));
			csvList19.add((String)csvMap.get("KANSETU_SIRE_TOSU"));
			csvList20.add((String)csvMap.get("KANSETU_SIRE_KOSU"));
			csvList21.add((String)csvMap.get("AKURIRU_KANBAN"));
			csvList22.add((String)csvMap.get("YOYAKU_SINKI"));
			csvList23.add((String)csvMap.get("YOYAKU_SAI"));
			index = intCal+1;
			//取得データの最後行データの場合
			if(j == restList.size()-1){
				//CSV出力オブジェクトに設定する
				bolSetCsvListFlg = true;
				if(intCal != theMaxDay){
					//抽出日付は当月最終日ではない場合、抽出日付後の日付の項目に0を設定する
					bolSetCsvZeroFlg = true;
				}
			//担当者変わる場合
			}else if(j != restList.size()-1){
				//次の担当者を取得
				// ########## 2014/06/23 温 UPD Start (案件C38117)セールスポイント編集機能の追加
				//strNextTantouCd = (String)restList.get(j+1).get("TANTO");
				if("0".equals(strCsvFlg)){
					strNextCd = (String)restList.get(j+1).get("TANTO");
				}else{
					strNextCd = (String)restList.get(j+1).get("POST_CD");
				}
				if(!strNextCd.equals(strNewCd)){
					// ########## 2014/06/23 温 UPD End
					//担当者変わる場合、CSV出力オブジェクトに設定する
					bolSetCsvListFlg = true;
					if(intCal != theMaxDay){
						//抽出日付は当月最終日ではない場合、抽出日付後の日付の項目に0を設定する
						bolSetCsvZeroFlg = true;
					}
				}
			}
			if(bolSetCsvZeroFlg){
				//抽出日付後の日付の項目に0を設定する
				for (int ii = 0; ii < theMaxDay-intCal; ii++) {
					csvList23.add("0");
					csvList22.add("0");
					csvList21.add("0");
					csvList20.add("0");
					csvList19.add("0");
					csvList18.add("0");
					csvList17.add("0");
					csvList16.add("0");
					csvList15.add("0");
					csvList14.add("0");
					csvList13.add("0");
					csvList12.add("0");
					csvList11.add("0");
					csvList10.add("0");
					csvList9.add("0");
					csvList8.add("0");
					csvList7.add("0");
					csvList6.add("0");
					csvList5.add("0");
					csvList4.add("0");
					csvList3.add("0");
					csvList2.add("0");
					csvList1.add("0");
				}
				//フラグリセット
				bolSetCsvZeroFlg = false;
			}
			//担当者変わる、あるいは最後一行のデータの場合、CSV出力オブジェクトに設定する
			if(bolSetCsvListFlg){
				retCsvList.add(csvList1);
				retCsvList.add(csvList2);
				retCsvList.add(csvList3);
				retCsvList.add(csvList4);
				retCsvList.add(csvList5);
				retCsvList.add(csvList6);
				retCsvList.add(csvList7);
				retCsvList.add(csvList8);
				retCsvList.add(csvList9);
				retCsvList.add(csvList10);
				retCsvList.add(csvList11);
				retCsvList.add(csvList12);
				retCsvList.add(csvList13);
				retCsvList.add(csvList14);
				retCsvList.add(csvList15);
				retCsvList.add(csvList16);
				retCsvList.add(csvList17);
				retCsvList.add(csvList18);
				retCsvList.add(csvList19);
				retCsvList.add(csvList20);
				retCsvList.add(csvList21);
				retCsvList.add(csvList22);
				retCsvList.add(csvList23);
				//フラグリセット
				bolSetCsvListFlg = false;
			}
			//次のループ開始時、担当者コード保持
			// ########## 2014/06/23 温 UPD Start (案件C38117)セールスポイント編集機能の追加
			//strOldTantouCd = (String)csvMap.get("TANTO");
			if("0".equals(strCsvFlg)){
				strOldCd = (String)csvMap.get("TANTO");
			}else{
				strOldCd = (String)csvMap.get("POST_CD");
			}
			// ########## 2014/06/23 温 UPD End
		}
		//CSV出力オブジェクトを戻る
		return retCsvList;
	}

	/**
	 * <pre>
	 * [説 明] 該当月の最大日数を取得処理
	 * @param strDate 日付(YYYYMMDD形式)
	 * @return 該当月の日数
	 * </pre>
	 */
	private int getMonthMaxDay(String strDate){

		//年取得
		String strYear = strDate.substring(0,4);
		int intYear = Integer.parseInt(strYear);
		//月取得
		String strMonth = strDate.substring(4,6);
		int intMonth = Integer.parseInt(strMonth)-1;
		//カレンダーオブジェクト設定
		Calendar cal = Calendar.getInstance();
		//年月設定
		cal.set(Calendar.YEAR,intYear);
		cal.set(Calendar.MONTH,intMonth);
		//日数取得
		int intmaxDate = cal.getActualMaximum(Calendar.DATE);
		//日数を戻る
		return intmaxDate;
	}

	/**
	 * <pre>
	 * [説 明] 日付の日数加算処理
	 * @param strDate 日付(YYYYMMDD形式)
	 * @param i 加算日数
	 * @return 加算後の日付(YYYYMMDD形式)
	 * @throws Exception　処理異常
	 * </pre>
	 */
	private String setMonthCalendar(String strDate,int i) throws Exception{

		//カレンダーオブジェクト設定
		SimpleDateFormat sdf = new SimpleDateFormat(Consts.FORMAT_DATE_YYMMDD);
		Date date = sdf.parse(strDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		//日数加算処理
		calendar.add(Calendar.DAY_OF_MONTH,i);
		String strRet = Util.formatDate(calendar.getTime(), Consts.FORMAT_DATE_YYMMDD);
		//加算後の日付を戻る
		return strRet;
	}

}
