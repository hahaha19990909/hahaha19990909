/**
 * @システム名: 受付システム
 * @ファイル名: UKG01013Action.java
 * @更新日付：: 2012/03/16
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG01013Service;

/**
 * <pre>
 * [機 能] 追客アラーム情報
 * [説 明] 追客アラーム情報を検索し、表示する。
 * @author [作 成] 2012/03/16 SYS_趙
 * @author [修 正] 2014/06/23 趙雲剛 (案件C38117)セールスポイント編集機能の追加
 *                2020/06/24 SYS_劉恆毅     C43040-2_法人の入力項目追加対応
 * </pre>
 */
public class UKG01013Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;

	/** 追客アラーム情報画面用データ */
	private Map<String, Object> ukg01013Data = new HashMap<String, Object>();

	/** 追客アラーム情報サービス */
	private IUKG01013Service ukg01013Services;

	/**
	 * 追客アラーム情報画面用データ
	 *
	 * @return ukg01013Data
	 */
	public Map<String, Object> getUkg01013Data() {

		return ukg01013Data;
	}

	/**
	 * 追客アラーム情報画面用データ
	 *
	 * @param ukg01013Data
	 */
	public void setUkg01013Data(Map<String, Object> ukg01013Data) {

		this.ukg01013Data = ukg01013Data;
	}

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param services サービス対象
	 * </pre>
	 */
	public void setUKG01013Services(IUKG01013Service services) {

		ukg01013Services = services;
	}

	/**
	 * <pre>
	 * [説 明] 追客アラーム情報画面の初期化処理。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		session.setAttribute(Consts.UKG01013_SORT_INFO, Consts.UKG01013_SORT_JYUN);
		// 追客アラーム情報を取得する。
		getAlarmInfo(request.getParameter("processType"), Consts.UKG01013_SORT_JYUN);
		return successForward();
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
		String oldsort = sortitem + " " + oldsortjyun;
		String newsort = sortitem + " " + sortjun;

		// ソート順をセッションに取得する
		HttpSession session = request.getSession();
		String sortkomoku = (String) session.getAttribute(Consts.UKG01013_SORT_INFO);

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
		session.setAttribute(Consts.UKG01013_SORT_INFO, sortkomoku);

		// 追客アラーム情報を取得する
		getAlarmInfo(request.getParameter("processType"), sortkomoku);
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 中止、他決ボタン押下時処理
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String chuushiTaketuProcess() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 受付no.を取得
		String uketukeNo = request.getParameter("uketukeNo");
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

		// 追客アラーム情報を取得する
		Map<String, Object> resData = ukg01013Services.chuushiTaketuProcess(kaisyaCd, uketukeNo, tantou, updateDate,
        //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
                                                                            //type);
                                                                            type,shoudancmnt);
        //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
		ukg01013Data.put("UPD_RESTULT", resData.get("rst_code"));
		if ("0000000".equals(resData.get("rst_code"))) {
			session.setAttribute("chgFlg", "1");
		} else if ("ERRS003".equals(resData.get("rst_code"))) {
			ukg01013Data.put("MESSAGE", Util.getServerMsg("ERRS003"));
		} else if ("ERRS999".equals(resData.get("rst_code")) || "1111111".equals(resData.get("rst_code"))) {
			ukg01013Data.put("MESSAGE", Util.getServerMsg("ERRS002", "更新処理"));
		}
		// 追客アラーム情報を取得する
		getAlarmInfo(request.getParameter("processType"),
		             String.valueOf(session.getAttribute(Consts.UKG01013_SORT_INFO)));
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 追客アラーム情報を取得する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	private void getAlarmInfo(String processType,
	                          String sort) throws Exception {

		Map<String, Object> loginInfo = getLoginInfo();

		// 会社コードを取得
		String kaisyaCD = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 担当者
		String chukaiTantou = String.valueOf(loginInfo.get(Consts.USER_CD));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		HttpServletRequest request = ServletActionContext.getRequest();
		// 店舗コード
		//String postCd = (String.valueOf(loginInfo.get(Consts.POST_CD)));
		String postCd = request.getParameter("postCd");
		String authorityCd = (String) loginInfo.get(Consts.USER_AUTHORITY_CD);
        ukg01013Data.put("postCd", postCd);
        ukg01013Data.put("authorityCd", authorityCd);
    	// ########## 2014/06/23 趙雲剛 UPD End
		// 追客アラーム情報を取得する
		List<Map<String, Object>> resData = ukg01013Services.getAlarmInfo(kaisyaCD, chukaiTantou, postCd, processType,
		                                                                  sort);
		ukg01013Data.put("processType", processType);
		//画面の連絡手段の編集
		Util.getDataStatus(resData);

		ukg01013Data.put("alarminfo", resData);
		ukg01013Data.put("alarminfocount", resData == null ? 0 : resData.size());
	}

	/**
	 * <pre>
	 * [説 明]セッションにソート順をクリアする
	 * @return closePage
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String closePage() {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションにソート順をクリアする
		HttpSession session = request.getSession();
		session.removeAttribute(Consts.UKG01013_SORT_INFO);
		ukg01013Data.put("chgFlg", session.getAttribute("chgFlg"));
		session.removeAttribute("chgFlg");
		return "ajaxProcess";
	}
}
