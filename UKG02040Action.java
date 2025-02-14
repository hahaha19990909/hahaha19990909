/**
 * @システム名: 受付システム
 * @ファイル名: UKG02040Action.java
 * @更新日付：: 2012/03/20
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴: 2013/03/04 SYS_趙雲剛       (案件C37058)社長指示対応
 *           2014/01/20 宮本             Jtestソース抑制(案件C37084-6)
 *           2014/06/23 趙雲剛(SYS) 1.01 (案件C38117)セールスポイント編集機能の追加
 *           2017/08/02 MJEC)長倉   1.02 (案件C41059)中止他決顧客の検索対応
 *           2019/07/19 熊振(SYS)  1.03 (案件C43089)来店日時の入力チェック対応
 *           2020/02/27 劉恆毅(SYS) 1.04  C44041_物件チラシの一括作成
 *           2020/06/24 劉恆毅(SYS 1.05 C43040-2_法人の入力項目追加対応
 *           2021/01/04 南柯創(SYS) 1.06 C45011_物件チラシメール送信
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
import jp.co.token.uketuke.exception.DataNotFoundException;
import jp.co.token.uketuke.formbean.BukkenDataBean;
import jp.co.token.uketuke.service.IUKG02040Service;
import org.apache.struts2.ServletActionContext;
import org.springframework.util.comparator.CompoundComparator;

/**
 * <pre>
 * [機 能] 商談履歴
 * [説 明] 商談履歴を検索・登録する。
 * @author [作 成] 2012/03/20 SYS_馮強華
 * </pre>
 */
public class UKG02040Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 4787801101794404622L;

	/** 商談履歴画面用データ */
	private Map<String, Object> ukg02040Data = null;

	/** 商談履歴サービス */
	private IUKG02040Service UKG02040Services;

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

	/**
	 * <pre>
	 * [説 明] 画面のデータを取得する。
	 * @return 画面のデータ
	 * </pre>
	 */
	public Map<String, Object> getUkg02040Data() {

		return ukg02040Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg02040Data 画面のデータ
	 * </pre>
	 */
	public void setUkg02040Data(Map<String, Object> ukg02040Data) {

		this.ukg02040Data = ukg02040Data;
	}

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param services サービス対象
	 * </pre>
	 */
	public void setUKG02040Services(IUKG02040Service services) {

		UKG02040Services = services;
	}

	/**
	 * <pre>
	 * [説 明] 初期処理。
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

		// ソート順設定する
		HttpSession session = request.getSession();
		session.setAttribute(Consts.UKG02040_SORT_INFO, Consts.UKG02040_SORT_JYUN);
		// 受付未登録フラグがonの場合、セッション情報を初期化する。
		if (session.getAttribute(Consts.UKG01011_FLG) != null) {
			session.setAttribute(Consts.UKG01011_FLG, null);
		}
		//########## 2017/08/16 MJEC)鈴村 ADD Start (案件C38101-1)ホームメイト自動受付対応
		// ホームメイト自動受付一覧フラグがonの場合、セッション情報を初期化する。
		if (session.getAttribute(Consts.UKG01016_FLG) != null) {
			session.setAttribute(Consts.UKG01016_FLG, null);
		}
		//########## 2017/08/16 MJEC)鈴村 ADD End
		ukg02040Data = new HashMap<String, Object>();
		// セッションからセッションIDを取得する
		ukg02040Data.put("token", session.getAttribute(Consts.REQUEST_TOKEN));
		// 2013/03/04  SYS_趙雲剛  ADD Start  (案件C37058)社長指示対応
		session.removeAttribute("pageFlag");
		// 2013/03/04  SYS_趙雲剛  ADD End
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		String kaisyacd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String uketukeno = null;
		String searchidx = request.getParameter("searchItem");
		ukg02040Data.put("searchIdx", searchidx);
		// 遷移元画面を判定uketukeNo
		if ("UKG01013".equals(request.getParameter("historyPageId"))) {
			// 遷移前画面が追客アラーム情報
			uketukeno = request.getParameter("uketukeNo");
			ukg02040Data.put("beforeGamen", "UKG01013");

			// セッションに追客アラーム情報遷移フラグを設定する。
			session.setAttribute(Consts.UKETUKENO, uketukeno);
			session.setAttribute(Consts.CHUKAITANTOU, request.getParameter("chukaitantoucd"));
			session.setAttribute(Consts.UKG01013_FLG, "UKG01013");
		} else {
			uketukeno = String.valueOf(session.getAttribute(Consts.UKETUKENO));
			ukg02040Data.put("beforeGamen", "");
		}

		if ("UKG01013".equals(session.getAttribute(Consts.UKG01013_FLG))) {
			ukg02040Data.put("beforeGamen", "UKG01013");
		}

		session.setAttribute(Consts.UKETUKENO, uketukeno);

		// 帳票印刷用会社コードと受付番号設定する
		ukg02040Data.put("kaisyaCd", kaisyacd);
		ukg02040Data.put("uketukeNo", uketukeno);

		// 現在日付
		String nowYmd = Util.formatDate(new Date(), Consts.FORMAT_DATE_YYMMDD);
		ukg02040Data.put("nowDay", nowYmd);

		String sortJun = Consts.UKG02040_SORT_JYUN.replace("#", ",");
		// 初期データを取得する
		Map<String, Object> initInfoData = UKG02040Services.getInitInfo(kaisyacd, uketukeno, sortJun);
		List<Map<String, Object>> bukenInfo = (List<Map<String, Object>>)initInfoData.get("out_bukenInfo");
		for (Map<String, Object> entry : bukenInfo) {
			String heyaNo = (String) entry.get("HEYA_NO");
			if (!Util.isEmpty(heyaNo) && "ISO-8859-1".equals(Util.getEncoding(heyaNo))) {
				heyaNo = new String(heyaNo.getBytes("ISO-8859-1"), "UTF-8");
			}
		}

		// 紹介情報
		ukg02040Data.put("syoukaiInfoList", bukenInfo);
		ukg02040Data.put("syoukaiInfoCount", initInfoData.get("out_bukencount"));
		List<Map<String, Object>> kibobuk = (List<Map<String, Object>>) initInfoData.get("out_kibobkInfo");

		if (kibobuk == null || kibobuk.size() <= 0) {
			// 制御データが取得できない場合
			throw new DataNotFoundException();
		}

		String kibouBukkenFlg = "1";
		for (Map<String, Object> entry : kibobuk) {
			String str = (String) entry.get("KIBOU_BUKKEN_KB");
			// 駐車場
			if ("3".equals(str)) {
				kibouBukkenFlg = "0";
			} else {
				kibouBukkenFlg = "1";
			}

			ukg02040Data.put("rirekiSeq", entry.get("KIBOU_JYOUKEN_RIREKI_SEQ"));
			ukg02040Data.put("setubiCdHiss", entry.get("KIBOU_HEYA_SETUBI_CD_HISSU"));
			ukg02040Data.put("setubiCdNoHiss", entry.get("KIBOU_HEYA_SETUBI_CD_NO_HISSU"));
			ukg02040Data.put("setubiCd", entry.get("KIBOU_JYOUKEN_SITEI_CD"));

			ukg02040Data.put("KOKYAKU_INFO_FLG", entry.get("KOKYAKU_INFO_FLG"));
			ukg02040Data.put("UKETUKE_KB", entry.get("UKETUKE_KB"));
			ukg02040Data.put("ANKETO_FLG", entry.get("ANKETO_FLG"));
			ukg02040Data.put("KIBOU_FLG", entry.get("KIBOU_FLG"));
			ukg02040Data.put("kokyakuSb", entry.get("KOKYAKU_SB"));

            // ########## 2017/11/06 MJEC)鈴村 ADD Start (案件C38101-1)ホームメイト自動受付対応
            ukg02040Data.put("sakuseiKb", entry.get("SAKUSEI_KB"));
            // ########## 2017/11/06 MJEC)鈴村 ADD End
			// ########## 2017/08/02 MJEC)鈴村 ADD Start (案件C41059)中止他決顧客の検索対応
			ukg02040Data.put("sinchokuKb", entry.get("SINCHOKU_KB"));
			// ########## 2017/08/02 MJEC)鈴村 ADD End
		}

		// 希望物件フラグ
		ukg02040Data.put("kibouBukkenFlg", kibouBukkenFlg);
		String sessionId = session.getId();
		ukg02040Data.put("sessionId", sessionId);

		if (session.getAttribute("annai_btn_firstInitFlg") != null) {
			ukg02040Data.put("annai_btn_firstInitFlg", "");
		} else {
			session.setAttribute("annai_btn_firstInitFlg", "init");
			ukg02040Data.put("annai_btn_firstInitFlg", "init");
		}

		if (session.getAttribute("kigenngkirei_firstInitFlg") != null) {
			ukg02040Data.put("kigenngkirei_firstInitFlg", "");
		} else {
			session.setAttribute("kigenngkirei_firstInitFlg", "init");
			ukg02040Data.put("kigenngkirei_firstInitFlg", "init");
		}

		if (session.getAttribute("karikaijyo_firstInitFlg") != null) {
			ukg02040Data.put("karikaijyo_firstInitFlg", "");
		} else {
			session.setAttribute("karikaijyo_firstInitFlg", "init");
			ukg02040Data.put("karikaijyo_firstInitFlg", "init");
		}
        //2020/03/19 劉恆毅 ADD Start C44041_物件チラシの一括作成
        ukg02040Data.put("MAXSENTAKUSUU",PropertiesReader.getIntance().getProperty(Consts.UKG04010_MAXSENTAKUSUU));
        //2020/03/19 劉恆毅 ADD End C44041_物件チラシの一括作成
		return successForward();
	}

	/**
	 * 物件検索画面から、商談履歴画面に遷移し一覧データを再検索する
	 *
	 * @return
	 */
	public String searchSyoukaiInfo() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeno = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String searchItem = request.getParameter("searchItem");

		// ソート順取得する
		String sortItem = String.valueOf(session.getAttribute(Consts.UKG02040_SORT_INFO));

		// 初期データを取得する
		Map<String, Object> bukenInfoData = UKG02040Services.getBukenInfo(kaisyacd, uketukeno, searchItem, 0,
		                                                                  Consts.PAGEIDX, sortItem);
		// 紹介情報
		ukg02040Data.put("syoukaiInfoList", bukenInfoData.get("out_bukenInfo"));

		// 紹介情報件数
		ukg02040Data.put("syoukaiInfoCount", bukenInfoData.get("out_bukencount"));

		return "searchSyoukaiInfo";
	}

	/**
	 * 商談履歴取得処理
	 *
	 * @return
	 * @throws Exception
	 */
	public String searchShoudanRirekiInfo() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));

		createshoudanInfo(kaisyaCd, uketukeNo);

		return "searchShoudanRirekiInfo";
	}

	/**
	 * 商談履歴データ再生成する
	 *
	 * @param kaisyaCd
	 * @param uketukeNo
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void createshoudanInfo(String kaisyaCd,
	                               String uketukeNo) throws Exception {

		ukg02040Data = new HashMap<String, Object>();

		List<Map<String, Object>> tirashitemp = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> raitenyoyakutemp = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> mitumoritemp = new ArrayList<Map<String, Object>>();
        //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
        List<Map<String, Object>> taketuTyusitemp = new ArrayList<Map<String, Object>>();
        //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
        //2021/01/04 ADD START 南柯創(SYS) C45011_物件チラシメール送信
        List<Map<String, Object>> mailSoushinmp = new ArrayList<Map<String,Object>>();
        //2021/01/04 ADD END 南柯創(SYS) C45011_物件チラシメール送信

		// 商談履歴データを取得する
		Map<String, Object> shoudanRirekiData = UKG02040Services.getshoudanRireki(kaisyaCd, uketukeNo);
		List<Map<String, Object>> rirekiList = (List<Map<String, Object>>) shoudanRirekiData.get("out_syodanNaiyo");

		List<Map<String, Object>> tirashiList = (List<Map<String, Object>>) shoudanRirekiData.get("out_tirashiPrint");
		createDetailItem(tirashiList, tirashitemp);

        //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
        List<Map<String, Object>> taketuTyusiList = (List<Map<String, Object>>) shoudanRirekiData.get("out_taketuTyusi");
        createDetailItem(taketuTyusiList, taketuTyusitemp);
        //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応

		List<Map<String, Object>> mitumoriList = (List<Map<String, Object>>) shoudanRirekiData.get("out_mitumori");
		createDetailItem(mitumoriList, mitumoritemp);

		List<Map<String, Object>> raitenyoyakuList = (List<Map<String, Object>>) shoudanRirekiData.get("out_raitenyoyaku");
		createDetailItem(raitenyoyakuList, raitenyoyakutemp);

		//2021/01/04 ADD START 南柯創(SYS) C45011_物件チラシメール送信
		List<Map<String,Object>> mailSoushinList = (List<Map<String,Object>>) shoudanRirekiData.get("out_mailSoushin");
		createDetailItem(mailSoushinList, mailSoushinmp);
		//2021/01/04 ADD END 南柯創(SYS) C45011_物件チラシメール送信

		rirekiList.addAll(tirashitemp);
		rirekiList.addAll(raitenyoyakutemp);
		rirekiList.addAll(mitumoritemp);
		//2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
		rirekiList.addAll(taketuTyusitemp);
		//2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
		//2021/01/04 ADD START 南柯創(SYS) C45011_物件チラシメール送信
		rirekiList.addAll(mailSoushinmp);
		//2021/01/04 ADD END 南柯創(SYS) C45011_物件チラシメール送信

		CompoundComparator cc = new CompoundComparator();
		cc.addComparator(new MapComparator("SHOUDAN_DATE"));
		Collections.sort(rirekiList, cc);

		// 商談履歴情報
		ukg02040Data.put("syoudanRirekiList", rirekiList);

		// 商談履歴明細データ
		List<Map<String, Object>> handInputrirekiList = createtedouShoudanRirekiList(rirekiList);
		ukg02040Data.put("handInputsyoudanRirekiList", handInputrirekiList);

	}

	/**
	 * 手動で登録された商談内容リストを生成する
	 *
	 * @param rirekiList
	 * @return
	 */
	private List<Map<String, Object>> createtedouShoudanRirekiList(List<Map<String, Object>> rirekiList) {

		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> o : rirekiList) {
			if ("0".equals(String.valueOf(o.get("SHOUDAN_SYUBETU_KB")))) {
				retList.add(o);
			}
		}
		return retList;
	}

	/**
	 * 商談履歴データ結果を再生成する
	 *
	 * @param o1
	 * @param o2
	 */
	private void createDetailItem(List<Map<String, Object>> o1,
	                              List<Map<String, Object>> o2) {

		tempShoudandate = "";
		tempItemMp = null;
		tempnaiyo = "";
		int index = 0;
		int listCount = o1.size();
		for (Map<String, Object> o : o1) {
			createItem(o, o2, listCount, index);
			index++;
		}
	}

	String tempShoudandate = "";

	Map<String, Object> tempItemMp = null;

	String tempnaiyo = "";

	/**
	 * 商談履歴データ結果を再生成する
	 *
	 * @param o1
	 * @param o2
	 * @param listCount
	 * @param index
	 */
	private void createItem(Map<String, Object> o1,
	                        List<Map<String, Object>> o2,
	                        int listCount,
	                        int index) {

		if ("".equals(tempShoudandate)) {

			tempShoudandate = String.valueOf(o1.get("SHOUDAN_DATE")).substring(0, 10);
			tempItemMp = new HashMap<String, Object>();

			tempnaiyo = String.valueOf(o1.get("SHOUDAN_NAIYOU"));

			createItem(o1, tempItemMp);

			if (listCount == 1) {
				o2.add(tempItemMp);
			}

		} else if (tempShoudandate.equals(o1.get("SHOUDAN_DATE").toString().substring(0, 10))) {

			tempnaiyo += "<br/>" + String.valueOf(o1.get("SHOUDAN_NAIYOU"));
			tempItemMp.put("SHOUDAN_NAIYOU", tempnaiyo); // parasoft-suppress BD.EXCEPT.NP "C37084-6抑制"

			if (index + 1 == listCount) {
				o2.add(tempItemMp);
			}

		} else if (!tempShoudandate.equals(o1.get("SHOUDAN_DATE").toString().substring(0, 10))) {

			o2.add(tempItemMp);

			tempItemMp = new HashMap<String, Object>();
			tempShoudandate = String.valueOf(o1.get("SHOUDAN_DATE")).substring(0, 10);
			createItem(o1, tempItemMp);
			tempnaiyo = String.valueOf(o1.get("SHOUDAN_NAIYOU"));

			if (index + 1 == listCount) {
				o2.add(tempItemMp);
			}
		}
	}

	/**
	 * 商談履歴データ結果を再生成する
	 *
	 * @param o1
	 * @param o2
	 */
	private void createItem(Map<String, Object> o1,
	                        Map<String, Object> o2) {

		o2.put("SHOUDAN_SEQ", String.valueOf(o1.get("SHOUDAN_SEQ")));
		o2.put("SHOUDAN_KB", String.valueOf(o1.get("SHOUDAN_KB")));
		o2.put("SHOUDAN_DATE", String.valueOf(o1.get("SHOUDAN_DATE")));
		o2.put("SHOUDAN_TIME", String.valueOf(o1.get("SHOUDAN_TIME")));
		o2.put("SHOUDAN_NAIYOU", String.valueOf(o1.get("SHOUDAN_NAIYOU")));
		o2.put("UPD_DATE", String.valueOf(o1.get("UPD_DATE")));
		o2.put("SHOUDAN_SYUBETU_KB", String.valueOf(o1.get("SHOUDAN_SYUBETU_KB")));
		o2.put("ORDER_SHOUDAN_DATE", String.valueOf(o1.get("ORDER_SHOUDAN_DATE")));
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

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		String sortitem = request.getParameter("sortitem");
		String sortjun = request.getParameter("sortjun");
		String oldsortjyun = request.getParameter("oldsortjyun");
		String searchItem = request.getParameter("searchItem");

		String[] sortArry = sortitem.split("#"); // parasoft-suppress CDD.DUPC "36029JTest対応"

		String oldsort = ""; // parasoft-suppress CDD.DUPC "36029JTest対応"
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
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeno = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String sortkomoku = (String) session.getAttribute(Consts.UKG02040_SORT_INFO);

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
		session.setAttribute(Consts.UKG02040_SORT_INFO, sortkomoku);

		Map<String, Object> bukenInfoData = UKG02040Services.getBukenInfo(kaisyacd, uketukeno, searchItem, 0,
		                                                                  Consts.PAGEIDX, sortkomoku);
		// 紹介情報
		ukg02040Data.put("syoukaiInfoList", bukenInfoData.get("out_bukenInfo"));
		// 紹介情報件数
		ukg02040Data.put("syoukaiInfoCount", bukenInfoData.get("out_bukencount"));
		// 改ページ用
		ukg02040Data.put("sortitem", sortitem);
		ukg02040Data.put("sortjun", sortjun);
		return "sort";
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
		Map<String, String> paramMap = Util.getParamMap(condition);

		String searchItem = paramMap.get("searchItem");

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();
		// セッションのログイン情報取得する
		HttpSession session = request.getSession();
		String kaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeno = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String sortkomoku = (String) session.getAttribute(Consts.UKG02040_SORT_INFO);

		// ページ切り替える機能用パラメータを取得する
		int startRowNum = (Integer.parseInt(paramMap.get("pageNum")) - 1) * Consts.PAGEIDX + 1;
		int endRowNum = startRowNum - 1 + Consts.PAGEIDX;

		sortkomoku = sortkomoku.replace("#", ",");

		// 業務処理（取得した条件で該当ページのデータを取得する）
		Map<String, Object> bukenInfoData = UKG02040Services.getBukenInfo(kaisyacd, uketukeno, searchItem, startRowNum,
		                                                                  endRowNum, sortkomoku);

		ukg02040Data = new HashMap<String, Object>();
		// 紹介情報
		ukg02040Data.put("syoukaiInfoList", bukenInfoData.get("out_bukenInfo"));
		// 紹介情報件数
		ukg02040Data.put("syoukaiInfoCount", bukenInfoData.get("out_bukencount"));

		return "pageIndexChg";
	}

	/***
	 * 追加修正ボタン押下処理
	 *
	 * @return
	 * @throws Exception
	 */
	public String addshoudanrireki() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		// セッションのログイン情報取得する
		HttpSession session = request.getSession();
		String kaisyaCd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String tantousya = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));

		String shoudanKb = request.getParameter("shoudanKb");

		// 商談日付
		String shoudanDate = request.getParameter("shoudanDate");
		shoudanDate = shoudanDate.replace("/", "-") + ":00";

		String shoudanCmnt = request.getParameter("shoudanCmnt");
		String seq = request.getParameter("seq");
		String upFlg = request.getParameter("addbtnFlg");
		String upDate = request.getParameter("uppDate");

		Map<String, Object> bukenInfoData = UKG02040Services.updateshoudanData(kaisyaCd, tantousya, uketukeNo,
		                                                                       shoudanKb, shoudanDate, shoudanCmnt,
		                                                                       seq, upFlg, upDate);
		String errMsg = "";
		// 更新処理失敗の場合
		if ("ERRS003".equals(bukenInfoData.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS003");
			log.error(bukenInfoData.get("rst_msg"));
		} else if ("ERRS002".equals(bukenInfoData.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS002", "履歴追加");
			log.error(bukenInfoData.get("rst_msg"));
		}

		// エラーメッセージをセットする
		ukg02040Data.put("errMsg", errMsg);

		if (Util.isEmpty(errMsg)) {
			searchShoudanRirekiInfo();
		}

		return "addshoudanrireki";
	}

	/***
	 * 現在日付を取得する
	 *
	 * @return アクション処理結果
	 * @throws Exception
	 */
	public String fetchNowDayTime() {

		Date date = new Date();
		String nowYmd = Util.getSysDateYMD();
		String nowtime = Util.formatDate(date, "HH:mm");
		ukg02040Data = new HashMap<String, Object>();
		ukg02040Data.put("NowTime", nowtime);
		ukg02040Data.put("NowDay", nowYmd);
		return "fetchNowDay";
	}

	/***
	 * お気にはいり更新処理
	 *
	 * @return アクション処理結果
	 * @throws Exception
	 */
	public String updateOkinihairi() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		String kaisyaCd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String tantousya = sessionLoginInfo.get(Consts.USER_CD).toString();
		String uketukeNo = session.getAttribute(Consts.UKETUKENO).toString();

		String bukenNo = request.getParameter("bukenNo");
		String heyaNo = request.getParameter("heyaNo");
		String okiniiri = request.getParameter("okiniiri");

		Map<String, Object> bukenInfoData = UKG02040Services.updateShoukaiBuken(kaisyaCd, tantousya, uketukeNo,
		                                                                        bukenNo.trim(), heyaNo.trim(), okiniiri);

		String errMsg = "";
		if ("ERRS002".equals(bukenInfoData.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS002", "お気に入り操作");
		}

		ukg02040Data.put("update", bukenInfoData.get("out_update"));
		// エラーメッセージをセットする
		ukg02040Data.put("errMsg", errMsg);

		return "updateOkinihairi";
	}

	/**
	 * メモ更新処理
	 *
	 * @return
	 */
	public String updMemoInfo() throws Exception {

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
		String upDate = request.getParameter("update");
		String shoudanMemo = request.getParameter("shoudanMemo");

		// 初期データを取得する
		Map<String, Object> returnInfo = UKG02040Services.updMemoInfo(kaisyaCd, uketukeNo, bukenNo, heyaNo, upDate,
		                                                              tantousya, shoudanMemo);

		String errMsg = "";
		// 更新処理失敗の場合
		errMsg = errMsg(String.valueOf(returnInfo.get("rst_code")));
		// エラーメッセージをセットする
		ukg02040Data = new HashMap<String, Object>();
		ukg02040Data.put("errMsg", errMsg);
		ukg02040Data.put("memoUpdate", returnInfo.get("rst_update"));
		return "updMemoInfo";
	}

	/**
	 * お客様情報データを再検索する
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String searchKokyakuInfo() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));

		// お客様情報データを取得する
		Map<String, Object> kokyakuInfoData = UKG02040Services.getKokyakuInfo(kaisyaCd, uketukeNo);
		List<Map<String, Object>> kokyakuInfo = (List<Map<String, Object>>) kokyakuInfoData.get("rst_record");

		if (kokyakuInfo != null && kokyakuInfo.size() > 0) {
			ukg02040Data.put("kokyakuInfo", kokyakuInfo.get(0));
		} else {
			ukg02040Data.put("kokyakuInfo", null);
		}

		// お客様情報情報
		ukg02040Data.put("kokyakuInfoList", kokyakuInfoData.get("rst_record"));
		// 進捗リスト
		ukg02040Data.put("sichoukuInfoList", kokyakuInfoData.get("rst_sintyokuInfo"));

		return "searchKokyakuInfo";
	}

	/**
	 * お客さま情報進捗状況変更処理
	 *
	 * @return
	 */
	public String updsinchokuInfo() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String tantousya = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String sinchoukuCd = request.getParameter("sinchokuCd");
		String upDate = request.getParameter("upDate");
        //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
        String shoudancmnt = request.getParameter("shoudanCmnt");
        //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応

		// お客さま情報進捗状況更新
		Map<String, Object> sinchoukunfoData = UKG02040Services.updSinchoukuInfo(kaisyaCd, uketukeNo, tantousya,
        //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
                                                                                 //sinchoukuCd, upDate);
                                                                                 sinchoukuCd, upDate,shoudancmnt);
        //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
		String errMsg = "";
		// 更新処理失敗の場合
		errMsg = errMsg(String.valueOf(sinchoukunfoData.get("rst_code")));
		// エラーメッセージをセットする
		ukg02040Data.put("errMsg", errMsg);
		ukg02040Data.put("raitenUketukeUpdate", sinchoukunfoData.get("rst_update"));

		return "updsinchokuInfo";
	}

	/**
	 * アラーム解除処理
	 *
	 * @return
	 */
	public String updAlarmInfo() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String tantousya = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String upDate = request.getParameter("upDate");

		// アラーム解除更新
		Map<String, Object> sinchoukunfoData = UKG02040Services.updAlarmInfo(kaisyaCd, uketukeNo, tantousya, upDate);

		String errMsg = "";
		// 更新処理失敗の場合
		errMsg = errMsg(String.valueOf(sinchoukunfoData.get("rst_code")));
		// エラーメッセージをセットする
		ukg02040Data.put("errMsg", errMsg);
		ukg02040Data.put("raitenUketukeUpdate", sinchoukunfoData.get("rst_update"));

		return "updAlarmInfo";
	}

	/**
	 * 来店メモを更新する
	 *
	 * @return
	 */
	public String updRaitenMemo() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String tantousya = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		String upDate = request.getParameter("update");
		String memo = request.getParameter("memo");

		// 初期データを取得する
		Map<String, Object> returnInfo = UKG02040Services.updRaitenMemo(kaisyaCd, uketukeNo, upDate, tantousya, memo);

		String errMsg = "";
		// 更新処理失敗の場合
		errMsg = errMsg(String.valueOf(returnInfo.get("rst_code")));
		// エラーメッセージをセットする
		ukg02040Data.put("errMsg", errMsg);
		ukg02040Data.put("memoUpdate", returnInfo.get("rst_update"));
		return "updRaitenMemo";
	}

	/**
	 * 商談情報取得処理
	 *
	 * @return
	 */
	public String searchSyoudanInfo() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		//String postCd = String.valueOf(sessionLoginInfo.get(Consts.POST_CD));
		// セッションの受付店舗情報取得する
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		String postCd = String.valueOf(uketukePostInfo.get(Consts.POST_CD));
		// ########## 2014/06/23 趙雲剛 UPD End
		// 仲介担当者コード
		String chukaitantou = String.valueOf(session.getAttribute(Consts.CHUKAITANTOU));
		// お客様情報データを取得する
		Map<String, Object> kokyakuInfoData = UKG02040Services.getsyoudanInfo(kaisyaCd, uketukeNo, postCd, chukaitantou);

		// お気に入り物件数
		ukg02040Data.put("okiniiriCount", kokyakuInfoData.get("out_okiniiri_bukencount"));
		// チラシ印刷数
		ukg02040Data.put("tirashiPrintCount", kokyakuInfoData.get("out_tirashi_printcount"));
		// 新着物件数
		ukg02040Data.put("sinchakuBkCount", kokyakuInfoData.get("out_sinchaku_bukencount"));

		Date date = new Date();
		String nowYmd = Util.formatDate(date, Consts.FORMAT_DATE_YYMMDD);

		// 現地案内予約データ取得
		ukg02040Data.put("nowDay", nowYmd);

		// 仮押え物件データ
		ukg02040Data.put("kariosaeList", kokyakuInfoData.get("out_kariosae_bukenInfo"));

		// 現地案内予約データ取得
		ukg02040Data.put("gentiannaiList", kokyakuInfoData.get("out_genti_annai"));

		ukg02040Data.put("sessionUketukeNo", uketukeNo);

		return "searchSyoudanInfo";
	}

	/**
	 * 仮押え物件欄確認ボタン押下処理
	 *
	 * @return
	 */
	public String updkariosae() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String tantousya = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		String bukenNo = request.getParameter("bukenNo");
		String heyaNo = request.getParameter("heyaNo");
		String upDate = request.getParameter("upDate");

		// 仮押え物件欄確認ボタン押下処理
		Map<String, Object> returnData = UKG02040Services.updkariosae(kaisyaCd, uketukeNo, bukenNo, heyaNo, tantousya,
		                                                              upDate);

		String errMsg = "";
		// 更新処理失敗の場合
		errMsg = errMsg(String.valueOf(returnData.get("rst_code")));
		// エラーメッセージをセットする
		ukg02040Data.put("errMsg", errMsg);

		return "updkariosae";
	}

	/**
	 * 現地案内予約リンク押下処理
	 *
	 * @return
	 */
	public String gentiYoyakuLinck() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		session.removeAttribute(Consts.HAYA_LIST_DATA);

		String bukkenNo = request.getParameter("bukkenNo");
		String heyaNo = request.getParameter("heyaNo");
		String seq = request.getParameter("seq");

		String bukkenNoArray[] = bukkenNo.split(",");
		String heyaNoArray[] = heyaNo.split(",");
		String seqArray[] = seq.split(",");

		List<BukkenDataBean> bukennHayaList = new ArrayList<BukkenDataBean>();
		for (int i = 0; i < bukkenNoArray.length; i++) {
			bukennHayaList.add(new BukkenDataBean(bukkenNoArray[i], heyaNoArray[i], seqArray[i]));
		}
		session.setAttribute(Consts.HAYA_LIST_DATA, bukennHayaList); // parasoft-suppress PB.API.ONS "36029JTest対応"
		return "gentiYoyakuLinck";
	}

	/**
	 * 現地案内実施処理
	 *
	 * @return
	 */
	public String updjishiJyoutai() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String tantousya = String.valueOf(sessionLoginInfo.get(Consts.USER_CD));
		String bukkenNo = request.getParameter("bukkenNo");
		String upDate = request.getParameter("upDate");
		String heyaNO = request.getParameter("heyaNO");
		String seq = request.getParameter("seq");
		String gentiannaiDate = request.getParameter("gentiannaiDate");

		// 現地案内実施処理
		Map<String, Object> returnData = UKG02040Services.updgentiannai(kaisyaCd, uketukeNo, tantousya, gentiannaiDate,
		                                                                bukkenNo, heyaNO, seq, upDate);

		String errMsg = "";
		// 更新処理失敗の場合
		errMsg = errMsg(String.valueOf(returnData.get("rst_code")));
		// エラーメッセージをセットする
		ukg02040Data.put("errMsg", errMsg);

		return "updjishiJyoutai";
	}

	/**
	 * 新着物件一覧ボタン処理
	 *
	 * @return
	 */
	public String addSinchaku() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String sessionId = session.getId();

		// 現地案内実施処理
		Map<String, Object> returnData = UKG02040Services.addSinchaku(sessionId, kaisyaCd, uketukeNo, new Date());

		String errMsg = "";
		// 更新処理失敗の場合
		errMsg = errMsg(String.valueOf(returnData.get("rst_msg")));
		// エラーメッセージをセットする
		ukg02040Data.put("errMsg", errMsg);
		ukg02040Data.put("sessionId", sessionId);
		return "addSinchaku";
	}

	/**
	 * チラシ印刷物件一覧ボタン押下処理
	 *
	 * @return
	 */
	public String addTirashi() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String sessionId = session.getId();

		// 現地案内実施処理
		Map<String, Object> returnData = UKG02040Services.addTirashi(sessionId, kaisyaCd, uketukeNo, new Date());

		String errMsg = "";
		// 更新処理失敗の場合
		errMsg = errMsg(String.valueOf(returnData.get("rst_msg")));
		// エラーメッセージをセットする
		ukg02040Data.put("errMsg", errMsg);
		ukg02040Data.put("sessionId", sessionId);
		return "addTirashi";
	}




	/**
	 * ｄｂ操作失敗の場合、エラーメッセージを取得する
	 *
	 * @return
	 */
	private String errMsg(String msgId) {

		String errMsg = "";
		// 更新処理失敗の場合
		if ("ERRS009".equals(msgId)) {
			errMsg = Util.getServerMsg("ERRS009");
		} else if ("ERRS002".equals(msgId) || "1111111".equals(msgId)) {
			errMsg = Util.getServerMsg("ERRS002", "更新処理");
		}
		return errMsg;
	}
	/* 2019/07/22 熊振 C43089_来店日時の入力チェック対応 STRAT */
	/**
	 * 来店日時更新処理
	 *
	 * @return
	 */
	public String upDateRaitenDate() throws Exception {

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		ukg02040Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String raitenDateAno = request.getParameter("raitenDateAno");

		Map<String, Object> returnData = UKG02040Services.upDateRaitenDate(kaisyaCd, uketukeNo, raitenDateAno);
		String errMsg = "";
		// 更新処理失敗の場合
		errMsg = errMsg(String.valueOf(returnData.get("rst_msg")));
		// エラーメッセージをセットする
		ukg02040Data.put("errMsg", errMsg);
		return "upDateRaitenDate";
	}
	/* 2019/07/22 熊振 C43089_来店日時の入力チェック対応 END */
}

/**
 * <pre>
 * [機 能] ソートクラス
 * [説 明] 昇順ソート項目を設定する。
 * @author [作 成] 2012/03/20 SYS_馮強華
 * </pre>
 */
class MapComparator implements Comparator<Map<String, Object>> {

	String sortItem = null;

	MapComparator(
	    String sotItem) {

		sortItem = sotItem;
	}

	public int compare(Map<String, Object> o1,
	                   Map<String, Object> o2) {

		return -o1.get(sortItem).toString().compareTo(o2.get(sortItem).toString());
	}
}

/**
 * <pre>
 * [機 能] ソートクラス
 * [説 明] 降順ソート項目を設定する。
 * @author [作 成] 2012/03/20 SYS_馮強華
 * </pre>
 */
class Map2Comparator implements Comparator<Map<String, Object>> {

	String sortItem = null;

	Map2Comparator(
	    String sotItem) {

		sortItem = sotItem;
	}

	public int compare(Map<String, Object> o1,
	                   Map<String, Object> o2) {

		return o1.get(sortItem).toString().compareTo(o2.get(sortItem).toString());
	}
}
