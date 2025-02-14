/**
 * @システム名: 受付システム
 * @ファイル名: UKG03011Action.java
 * @更新日付：: 2012/05/21
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴： 2014/06/10 郭凡(SYS) (案件番号C38117)セールスポイント編集機能の追加
 * 更新履歴： 2015/02/12 温(SYS)   (案件番号C39042)受付レスポンス対応
 * 更新履歴： 2017/08/10 MJEC)長倉 (案件C41059)中止他決顧客の検索対応
 * 更新履歴 ： 2018/02/01  張陽陽(SYS)	   案件C42036デザイン変更新規作成
 * 更新履歴 ： 2019/07/03 郝年昇(SYS)    案件 C43100お客様項目の入力簡素化対応
 */
package jp.co.token.uketuke.action;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.UKG03011Bean;
import jp.co.token.uketuke.service.IUKG03011Service;
import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 物件一覧画面
 * [説 明] 物件一覧表示する
 * @author [作 成] 2012/05/21 馮強華(SYS)
 * 変更履歴: 2013/05/22	SYS_趙雲剛	(案件C37109)受付システムSTEP2要望対応
 * </pre>
 */
public class UKG03011Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;

	/** 一覧で表示する画面用データ */
	private Map<String, Object> ukg03011Data = null;

	/** 物件一覧サービス */
	private IUKG03011Service UKG03011Services = null;

	/** セッションのログイン情報 */
	private Map<String, Object> sessionLoginInfo = null;

	/**
	 * @param ukg03011Data
	 *            the ukg03011Data to set
	 */
	public void setUkg03011Data(Map<String, Object> data) {

		ukg03011Data = data;
	}

	/**
	 * @return the ukg03011Data
	 */
	public Map<String, Object> getUkg03011Data() {

		return ukg03011Data;
	}

	/**
	 * 物件一覧サービス
	 *
	 * @param services
	 */
	public void setUKG03011Services(IUKG03011Service services) {

		UKG03011Services = services;
	}

	/**
	 * <pre>
	 * [説 明] 一覧で表示する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		// 画面状況データ
		UKG03011Bean ukg03011Bean = (UKG03011Bean) session.getAttribute(Consts.UKG03011BEAN_SESSION_KEY);
		if (ukg03011Bean == null) {

			ukg03011Bean = new UKG03011Bean();
		}

		String sessionId = request.getParameter("sessionId");
		String kibousetubiCd = request.getParameter("kibousetubiCd");
		ukg03011Bean.setDispNoHyoujiFlg("0");
		ukg03011Bean.setPageNum(1);
		ukg03011Bean.setPageId("");
		ukg03011Bean.setSortJun(Consts.UKG03011_SORT_JYUN);
		ukg03011Bean.setMadoriKb("1");
		ukg03011Bean.setSessionId(sessionId);
		ukg03011Bean.setKibousetubiCd(kibousetubiCd);
		// 画面状況データ
		session.setAttribute(Consts.UKG03011BEAN_SESSION_KEY, ukg03011Bean);

		// 画面状況データ
		session.setAttribute(Consts.UKG03011BEAN_SESSION_KEY, ukg03011Bean);
		// 2018/09/26 肖剣生(SYS) ADD START 案件C42036デザイン変更新規作成(不具合No062)
		ukg03011Data.put("setubiCount", PropertiesReader.getIntance().getProperty("setubiCount"));
		// 2018/09/26 肖剣生(SYS) ADD END 案件C42036デザイン変更新規作成(不具合No062)

		return successForward();
	}

	/**
	 * 物件一覧検索する
	 *
	 * @return
	 * @throws Exception
	 */
	public String searchBukenInfo() throws Exception {
		// 2018/02/02 張陽陽(SYS)  ADD START 案件C42036デザイン変更新規作成
		HttpServletRequest request = ServletActionContext.getRequest();
		String pageId = request.getParameter("pageId");
		String resultCode = successForward();
		// 2018/02/02 張陽陽(SYS)  ADD END   案件C42036デザイン変更新規作成

		ukg03011Data = new HashMap<String, Object>();
		// ##########2018/08/15 SYS_孫博易 ADD START   (案件番号C42036)デザイン変更 bug26
		String madoriKbn = request.getParameter("madoriKbn");
		ukg03011Data.put("madoriKbn", madoriKbn);
		// ##########2018/08/15 SYS_孫博易 ADD END   (案件番号C42036)デザイン変更 bug26
		// 2018/09/26 肖剣生(SYS) ADD START 案件C42036デザイン変更新規作成(不具合No062)
		ukg03011Data.put("setubiCount", PropertiesReader.getIntance().getProperty("setubiCount"));
		// 2018/09/26 肖剣生(SYS) ADD END 案件C42036デザイン変更新規作成(不具合No062)
		// 2018/10/16  肖剣生(SYS) ADD START 案件C42036デザイン変更新規作成(不具合No077)
		ukg03011Data.put("resetPageNum", "1");
		// 2018/10/16  肖剣生(SYS) ADD END 案件C42036デザイン変更新規作成(不具合No077)
		// 物件一覧検索する
		fetchBukenInfo(ukg03011Data);

		// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
		if (Consts.PAGEID_UKG03030.equals(pageId)) {
			// お気に入り物件一覧画面
			resultCode = Consts.PAGEID_UKG03031;
		}
		return resultCode;
		// 2018/02/02 張陽陽(SYS)  UPD END   案件C42036デザイン変更新規作成
	}

	/**
	 * 物件一覧検索する
	 *
	 * @throws Exception
	 */
    @SuppressWarnings("unchecked")
    private void fetchBukenInfo(Map<String, Object> ukg03011Data) throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeno = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String pageId = request.getParameter("pageId");
		String sessionId = request.getParameter("sessionId");
		String kibousetubiCd = request.getParameter("kibousetubiCd");
		String bukenAreaFlg = request.getParameter("bukenAreaFlg");
	    // 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
        String tokuTenKb =  String.valueOf(session.getAttribute("tokuTenKb"));
        // 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		// 2018/02/02 張陽陽(SYS)  ADD START 案件C42036デザイン変更新規作成
		ukg03011Data.put("bukenAreaFlg", bukenAreaFlg);
		// ソート用
		String sortFlag = request.getParameter("sortFlag");
		ukg03011Data.put("sortFlag", Util.toEmpty(sortFlag));
		// 2018/02/02 張陽陽(SYS)  ADD END   案件C42036デザイン変更新規作成
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		// セッションのログイン情報取得する
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		// ########## 2014/06/10 郭凡 ADD End
		// 画面状況データ
		String sessionKey;
		if (Consts.PAGEID_UKG03030.equals(pageId)) {
			if("right".equals(bukenAreaFlg)){
				sessionKey = Consts.UKG03030_RIGHT_BEAN_SESSION_KEY;
			}else{
				sessionKey = Consts.UKG03030_LEFT_BEAN_SESSION_KEY;
			}
		} else {
			sessionKey = Consts.UKG03011BEAN_SESSION_KEY;
		}
		UKG03011Bean ukg03011Bean = (UKG03011Bean) session.getAttribute(sessionKey);
		if (ukg03011Bean == null) {

			ukg03011Bean = new UKG03011Bean();
			ukg03011Bean.setDispNoHyoujiFlg("1");
			ukg03011Bean.setPageNum(1);
			ukg03011Bean.setPageId("");
			ukg03011Bean.setSortJun(Consts.UKG03011_SORT_JYUN);
			ukg03011Bean.setMadoriKb("1");
			ukg03011Bean.setSessionId(sessionId);
			ukg03011Bean.setKibousetubiCd(kibousetubiCd);
		}

		// 画面状況データ
		session.setAttribute(sessionKey, ukg03011Bean);

		String sortItem = "";
		// ソート順取得する
		sortItem = ukg03011Bean.getSortJun();

		if (Consts.PAGEID_UKG03030.equals(pageId)) {
			ukg03011Bean.setDispNoHyoujiFlg("1");
			// 2018/02/02 張陽陽(SYS) UPD START 案件C42036デザイン変更新規作成
			ukg03011Bean.setPageDataCount(Consts.MAX);
			String praSortItem = request.getParameter("sortItem");
			if (!Util.isEmpty(praSortItem)) {
				sortItem = praSortItem;
			} else {
				sortItem = Consts.UKG03031_SORT_JYUN;
			}
			// 2018/02/02 張陽陽(SYS) UPD END 案件C42036デザイン変更新規作成
		} else {
			String [] sortItemArry = sortItem.split(",");

			ukg03011Data.put("sortItem", sortItemArry[0].split(" ")[0]);

			ukg03011Data.put("sortJun", sortItemArry[0].split(" ")[1]);
		}
		
		// 2018/10/16  肖剣生(SYS) ADD START 案件C42036デザイン変更新規作成(不具合No077)
		String resetPageNum = (String) ukg03011Data.get("resetPageNum");
		if (resetPageNum != null && "1".equals(resetPageNum)) {
			ukg03011Bean.setPageNum(1);
		}
		// 2018/10/16  肖剣生(SYS) ADD END 案件C42036デザイン変更新規作成(不具合No077)

		// ページ開始数
		int startRowNum = (ukg03011Bean.getPageNum() - 1) * ukg03011Bean.getPageDataCount() + 1;
		// ページ終了数
		int endRowNum = startRowNum - 1 + ukg03011Bean.getPageDataCount();

		// 初期データを取得する
		Map<String, Object> bukenInfoData = UKG03011Services.getBukenInfo(kaisyacd, uketukeno,
		                                                                  ukg03011Bean.getSessionId(),
		                                                                  ukg03011Bean.getDispNoHyoujiFlg(),
		                                                                  // ########## 2013/05/22 SYS_趙雲剛 ADD Start (案件C37109-1) 受付システムSTEP2要望対応
		                                                                  ukg03011Bean.getKibousetubiCd(),
		                                                                  // ########## 2013/05/22 SYS_趙雲剛 ADD End
		                                                                  // 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		                                                                  tokuTenKb,
		                                                                  // 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		                                                                  startRowNum, endRowNum, sortItem, pageId);
		// ########## 2013/05/22 SYS_趙雲剛 ADD Start (案件C37109-1) 受付システムSTEP2要望対応 // parasoft-suppress UC.ACC "C39042Jtest対応"
		// ########## 2015/02/12 温(SYS) DEL Start (案件番号C39042)受付レスポンス対応 // parasoft-suppress UC.ACC "C39042Jtest対応"
		//int rownum = 0; // parasoft-suppress UC.ACC "C39042Jtest対応"
		//if (bukenInfoData.get("out_bukencount") != null) { // parasoft-suppress UC.ACC "C39042Jtest対応"
		//	rownum = ((BigDecimal) bukenInfoData.get("out_bukencount")).intValue(); // parasoft-suppress UC.ACC "C39042Jtest対応"
		//} // parasoft-suppress UC.ACC "C39042Jtest対応"
		//if (rownum < startRowNum) { // parasoft-suppress UC.ACC "C39042Jtest対応"
		//	ukg03011Bean.setPageNum(rownum / ukg03011Bean.getPageDataCount());
			// ページ開始数
		//	startRowNum = (ukg03011Bean.getPageNum() - 1) * ukg03011Bean.getPageDataCount() + 1;
			// ページ終了数
		//	endRowNum = startRowNum - 1 + ukg03011Bean.getPageDataCount();
		//}
		//bukenInfoData = UKG03011Services.getBukenInfo(kaisyacd, uketukeno,
		//														ukg03011Bean.getSessionId(), ukg03011Bean.getDispNoHyoujiFlg(),
		//														ukg03011Bean.getKibousetubiCd(), startRowNum, endRowNum,
		//														sortItem, pageId);
		// ########## 2015/02/12 温(SYS) DEL End
		// ########## 2013/05/22 SYS_趙雲剛 ADD End
		// 物件情報
		ukg03011Data.put("bukenInfoList", bukenInfoData.get("out_bukenInfo"));
		// 物件情報件数
		ukg03011Data.put("bukenInfoCount", bukenInfoData.get("out_bukencount"));
		// 2018/10/16  肖剣生(SYS) ADD START 案件C42036デザイン変更新規作成(不具合No077)
		int totalCount = Integer.parseInt(bukenInfoData.get("out_bukencount").toString());
		int maxPageNum = totalCount / ukg03011Bean.getPageDataCount();
		if ((totalCount % ukg03011Bean.getPageDataCount()) > 0) {
			maxPageNum++;
		}
		ukg03011Data.put("maxPageNum", maxPageNum);
		// 2018/10/16  肖剣生(SYS) ADD END 案件C42036デザイン変更新規作成(不具合No077)
		// ########## 2013/05/22 SYS_趙雲剛 ADD Start (案件C37109-1) 受付システムSTEP2要望対応
		// 部屋情報件数
		ukg03011Data.put("heyaInfoCount", bukenInfoData.get("out_heyacount"));
		// ########## 2013/05/22 SYS_趙雲剛 ADD End
		// 外観/間取図区分
		ukg03011Data.put("madoriKb", ukg03011Bean.getMadoriKb());
		// 希望設備コード
		ukg03011Data.put("kibousetubiCd", ukg03011Bean.getKibousetubiCd());

		// 非表示にした物件を表示する
		ukg03011Data.put("dispNoHyoujiFlg", ukg03011Bean.getDispNoHyoujiFlg());

		ukg03011Data.put("pageNum", ukg03011Bean.getPageNum());
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		ukg03011Data.put("authorityCd", authorityCd);
		// ########## 2014/06/10 郭凡 ADD End

		// ##########2018/08/15 SYS_孫博易 ADD START   (案件番号C42036)デザイン変更 bug26
		// 外観間取り区分
		String madoriKbn = (String) ukg03011Data.get("madoriKbn");
		if (Util.isEmpty(madoriKbn)) {
			madoriKbn = "1";
		}
		ukg03011Data.put("madoriKbn", madoriKbn);
		// 外観の場合、外観選定
		if ("1".equals(madoriKbn)) {
			ukg03011Data.put("btnGaikanMadori", "bukkenListHeader2");
		} else {
			ukg03011Data.put("btnGaikanMadori", "bukkenListHeader2-1");
		}
		// ##########2018/08/15 SYS_孫博易 ADD END   (案件番号C42036)デザイン変更 bug26
		
		// 設定ファイルからイメージPathを取得する
		for (Map<String, Object> bukenInfo : (List<HashMap<String, Object>>) bukenInfoData.get("out_bukenInfo")) {

			bukenInfo.put("GK_IMAGE_PATH", "imgIO.imgIO?type=1&path="+bukenInfo.get("GK_IMAGE_PATH"));
			//bukenInfo.put("NK_IMAGE_PATH", Util.wmfToSVG((String)bukenInfo.get("NK_IMAGE_PATH"),82f,61f));
			// 2018/03/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
			bukenInfo.put("NK_IMAGE_PATH", Util.wmfToSVG((String)bukenInfo.get("NK_IMAGE_PATH"),124f,90f));
			// 2018/03/02 張陽陽(SYS)  UPD END   案件C42036デザイン変更新規作成
		}
	}

	/**
	 * <pre>
	 * [説 明] ソート
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String sort() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String sortitem = request.getParameter("sortitem");
		String sortjun = request.getParameter("sortjun");
		String oldsortjyun = request.getParameter("oldsortjyun");
		String[] sortArry = sortitem.split("#");

		String oldsort = "";
		for (int i = 0; i < sortArry.length; i++) {
			String strSplit = i == 0 ? "" : ",";
			oldsort += strSplit + sortArry[i] + " " + oldsortjyun;
		}

		String newsort = "";
		for (int i = 0; i < sortArry.length; i++) {
			String strSplit = i == 0 ? "" : ",";
			newsort += strSplit + sortArry[i] + " " + sortjun;
		}

		// 検索条件を取得する
		HttpSession session = request.getSession();
		String sessionKey = Consts.UKG03011BEAN_SESSION_KEY;
		// 画面状況データ
		UKG03011Bean ukg03011Bean = (UKG03011Bean) session.getAttribute(sessionKey);

		String sortkomoku = ukg03011Bean.getSortJun();

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

		// ソート順設定する
		ukg03011Bean.setSortJun(sortkomoku);
		// ページ数を１ページにする
		ukg03011Bean.setPageNum(1);
		// 画面状況データ
		session.setAttribute(sessionKey, ukg03011Bean);

		ukg03011Data = new HashMap<String, Object>();
		// 物件一覧データを再検索する
		fetchBukenInfo(ukg03011Data);

		// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
		String pageId = request.getParameter("pageId");
		String resultCode = successForward();

		if (Consts.PAGEID_UKG03031.equals(pageId)) {
			// お気に入り物件一覧画面
			resultCode = Consts.PAGEID_UKG03031;
		}

		return resultCode;
		// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
	}

	/**
	 * <pre>
	 * [説 明] ページ切り替える。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String pageIndexChg() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String condition = request.getParameter("condition");
		String pageId = request.getParameter("pageId");
		String bukenAreaFlg = request.getParameter("bukenAreaFlg");
		Map<String, String> paramMap = Util.getParamMap(condition);
		String sessionKey;
		if (Consts.PAGEID_UKG03030.equals(pageId)) {
			if("right".equals(bukenAreaFlg)){
				sessionKey = Consts.UKG03030_RIGHT_BEAN_SESSION_KEY;
			}else{
				sessionKey = Consts.UKG03030_LEFT_BEAN_SESSION_KEY;
			}
		} else {
			sessionKey = Consts.UKG03011BEAN_SESSION_KEY;
		}
		// 検索条件を取得する
		HttpSession session = request.getSession();
		// 画面状況データ
		UKG03011Bean ukg03011Bean = (UKG03011Bean) session.getAttribute(sessionKey);
		ukg03011Bean.setPageNum(Integer.parseInt(paramMap.get("pageNum")));

		// 物件一覧データを再検索する
		ukg03011Data = new HashMap<String, Object>();
		fetchBukenInfo(ukg03011Data);

		// 2018/10/16  肖剣生(SYS) UPD START 案件C42036デザイン変更新規作成(不具合No077)
		//return "ajaxProcess";
		return successForward();
		// 2018/10/16  肖剣生(SYS) UPD END 案件C42036デザイン変更新規作成(不具合No077)
	}

	/**
	 * 外観/間取図区分を設定する
	 *
	 * @return アクション処理結果
	 * @throws Exception
	 *             処理異常
	 */
	public String imageKb() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String imageKb = request.getParameter("imageKb");

		HttpSession session = request.getSession();
		// 画面状況データ
		UKG03011Bean ukg03011Bean = (UKG03011Bean) session.getAttribute(Consts.UKG03011BEAN_SESSION_KEY);
		ukg03011Bean.setMadoriKb(imageKb);
		ukg03011Data = new HashMap<String, Object>();
		fetchBukenInfo(ukg03011Data);

		return "ajaxProcess";
	}

	/**
	 * 紹介物件を非表示に更新する
	 *
	 * @return アクション処理結果
	 * @throws Exception
	 *             処理異常
	 */
	public String updShoukaiBukenDisp() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String tantousya = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String bukenNo = request.getParameter("bukenNo");
		String heyaNo = request.getParameter("heyaNo");
		String nodispFlg = request.getParameter("nodispFlg");

		// 初期データを取得する
		Map<String, Object> returnInfo = UKG03011Services.updShoukaiBukenDisp(kaisyaCd, uketukeNo, bukenNo, heyaNo,
		                                                                      nodispFlg, tantousya);

		String errMsg = "";

		String msgId = String.valueOf(returnInfo.get("rst_code"));
		// 更新処理失敗の場合
		if ("ERRS002".equals(msgId)) {
			errMsg = Util.getServerMsg("ERRS002", "更新処理");
		}
		// エラーメッセージをセットする
		ukg03011Data = new HashMap<String, Object>();
		ukg03011Data.put("errMsg", errMsg);

		return "ajaxProcess";
	}
	 // ########## 2013/05/22 SYS_趙雲剛 ADD Start (案件C37109-1) 受付システムSTEP2要望対応
	/**
	 * 間取りより紹介物件を非表示に更新する
	 *
	 * @return アクション処理結果
	 * @throws Exception
	 *             処理異常
	 */
	public String updShoukaiBukenDispMadori() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String tantousya = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String sessionId = request.getParameter("sessionId");
		String bukenNo = request.getParameter("bukenNo");
		String madori = request.getParameter("madori");
		String nodispFlg = request.getParameter("nodispFlg");

		// 初期データを取得する
		Map<String, Object> returnInfo = UKG03011Services.updShoukaiBukenDispMadori(sessionId, kaisyaCd, uketukeNo, bukenNo, madori,
		                                                                      nodispFlg, tantousya);

		String errMsg = "";

		String msgId = String.valueOf(returnInfo.get("rst_code"));
		// 更新処理失敗の場合
		if ("ERRS002".equals(msgId)) {
			errMsg = Util.getServerMsg("ERRS002", "更新処理");
		}
		// エラーメッセージをセットする
		ukg03011Data = new HashMap<String, Object>();
		ukg03011Data.put("errMsg", errMsg);

		return "ajaxProcess";
	}
	// ########## 2013/05/22 SYS_趙雲剛 ADD End
	/**
	 * 非表示物件フラグを設定する
	 *
	 * @return アクション処理結果
	 * @throws Exception
	 *             処理異常
	 */
	public String dispFlg() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String dispFlg = request.getParameter("dispFlg");

		HttpSession session = request.getSession();
		// 画面状況データ
		UKG03011Bean ukg03011Bean = (UKG03011Bean) session.getAttribute(Consts.UKG03011BEAN_SESSION_KEY);
		ukg03011Bean.setDispNoHyoujiFlg(dispFlg);
		// ページ数を１ページにする
		ukg03011Bean.setPageNum(1);
		return "ajaxProcess";
	}

	/**
	 * 紹介物件を非表示に更新する
	 *
	 * @return アクション処理結果
	 * @throws Exception
	 *             処理異常
	 */
	public String addbukenInfo() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String tantousya = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String bukenNo = request.getParameter("bukenNo");
		String heyaNo = request.getParameter("heyaNo");
		// ########## 2017/08/10 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		//進捗コードを取得。
		String sinchokuCd = request.getParameter("sinchokuCd");
		// ########## 2017/08/10 MJEC)長倉 ADD End
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		// ########## 2017/08/10 MJEC)長倉 UPD Start (案件C41059)中止他決顧客の検索対応
		//if("0".equals(authorityCd)){
		//ユーザー登録権限が'0'かつ進捗コードが'6'(他決)か'7'(中止)以外の場合処理を行う。
		if("0".equals(authorityCd) && !(sinchokuCd.equals("6") || sinchokuCd.equals("7"))){
		// ########## 2017/08/10 MJEC)長倉 UPD End
			// 初期データを取得する
			@SuppressWarnings("unused") Map<String, Object> returnInfo = UKG03011Services.addbukenInfo(kaisyaCd, uketukeNo,
					bukenNo, heyaNo,
					tantousya);
		}
		// ########## 2014/06/10 郭凡 UPD End

		return "ajaxProcess";
	}

	// 2018/02/02 張陽陽(SYS)  ADD START 案件C42036デザイン変更新規作成
	/**
	 * 家賃をフォーマット
	 * @param s 家賃データ
	 * @throws Exception
	 */
	public String formatYatin(String s) throws Exception{
		String rs = "0.0";
		if (!Util.isEmpty(s)) {
			Double yatin = Double.valueOf(s);
			yatin = yatin/10000;
			DecimalFormat fmt = new DecimalFormat("#.0");
			fmt.setRoundingMode(RoundingMode.FLOOR);
			rs =  fmt.format(yatin);
		}
		return rs;
	}

	/**
	 * 月をフォーマット
	 * @param s 月
	 * @throws Exception
	 */
	public String formatMonth(String s) throws Exception{
		String rs = "";
		if (!Util.isEmpty(s)) {
			rs = s.replaceFirst("^0*", "");
		}
		return rs;
	}
	// 2018/02/02 張陽陽(SYS)  ADD START 案件C42036デザイン変更新規作成
}
