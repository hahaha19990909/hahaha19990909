/**
 * <pre>
 * @システム名: 受付システム
 * @ファイル名: UKG01015Action.java
 * [機 能] 仮押え状況一覧。
 * [説 明] 仮押え状況一覧アクション。
 * @author [作 成] 2012/03/12 戴氷馨(SYS)
 * 更新履歴：2014/06/23 	趙雲剛 (SYS)		1.01		(案件C38117)セールスポイント編集機能の追加
 * @Copyright: 2012 token corporation All right reserved
 * </pre>
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
import jp.co.token.uketuke.common.DispFormat;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG01015Service;

/**
 * <pre>
 * [機 能] 仮押え状況一覧
 * [説 明] 仮押え状況一覧を検索し、表示する。
 * @author [作 成]
 * 変更履歴: 2014/01/16 SYS_胡 Jtest対応
 * @author [修 正] by 朱珂 2018/04/28 (案件C42036)デザイン変更新規作成の対応
 * </pre>
 */
public class UKG01015Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;

	/** 受仮押え状況一覧サービス */
	private IUKG01015Service ukg01015Services = null;

	/** 仮押え状況一覧画面用データ */
	private Map<String, Object> ukg01015Data = null;

	/**
	 * <pre>
	 * [説 明] 受仮押え状況一覧サービス対象を設定する。
	 * @param ukg01015Service サービス対象
	 * </pre>
	 */
	public void setUkg01015Services(IUKG01015Service ukg01015Services) {

		this.ukg01015Services = ukg01015Services;
	}

	/**
	 * 仮押え状況一覧画面用データ
	 *
	 * @return ukg01015Data
	 */
	public Map<String, Object> getUkg01015Data() {

		return ukg01015Data;
	}

	/**
	 * 仮押え状況一覧画面用データ
	 *
	 * @param ukg01015Data
	 */
	public void setUkg01015Data(Map<String, Object> ukg01015Data) {

		this.ukg01015Data = ukg01015Data;
	}

	/**
	 * <pre>
	 * [説 明] 仮押え状況一覧情報。
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
		session.setAttribute(Consts.UKG01015_SORT_INFO, Consts.UKG01015_SORT_JYUN);
		// 仮押え状況一覧を取得する
		getKariosaeInfo(request.getParameter("processType"));

		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] 仮押え状況一覧を取得する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	private void getKariosaeInfo(String processType) throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		// セッションのログイン情報取得する
		Map<String, Object> loginInfo = getLoginInfo();
		// 会社コード
		String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));

		// 仲介担当者
		String tantouCd = String.valueOf(loginInfo.get(Consts.USER_CD));

		// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		// 店舗コード
		//String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));
		String postcd = request.getParameter("postCd");
		String authorityCd = (String) loginInfo.get(Consts.USER_AUTHORITY_CD);
		// ########## 2014/06/23 趙雲剛 ADD End

		// 受付No.
		String uketukeNo = request.getParameter("uketukeNo");

		// 受仮押え状況データリストを取得する
		String senikenFlg = processType;

		// セッション情報からソート順を取得する
		String sort = String.valueOf(session.getAttribute(Consts.UKG01015_SORT_INFO));
		ukg01015Data = new HashMap<String, Object>();
		// 初期化ソート順を設定する
		ukg01015Data.put("sortkomoku", sort);
		ukg01015Data.put("kaisyacd", kaisyacd);
		ukg01015Data.put("tantouCd", tantouCd);
		ukg01015Data.put("uketukeNo", uketukeNo);
		ukg01015Data.put("processType", processType);
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		/*
		List<Map<String, Object>> resData = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> resData1 = new ArrayList<Map<String,Object>>();
		Map<String, Object> kariosaeinfoList = ukg01015Services.getKariosaeInfo(tantouCd, kaisyacd, postcd, uketukeNo,
				senikenFlg, sort);
		resData1 = (List<Map<String, Object>>) kariosaeinfoList.get("rst_list");
		String rowAttr = "";

		// 重複なデータを消す
		if (resData1.size() <= 1) {
			resData = resData1;
		} else {
			HashMap<String, String> uketukeNoMap = new HashMap<String, String>();
			for (int i = 0; i < resData1.size()-1; i++) {
				Map<String, Object> bukkenList = resData1.get(i);
				Map<String, Object> bukkenList2 = resData1.get(i + 1);

				String bukkenNo1 = String.valueOf(bukkenList.get("ESTATE_BUKKEN_NO"));
				String heyaNo1 = bukkenList.get("HEYA_NO").toString();
				String uketukeNo1 = bukkenList.get("UKETUKE_NO").toString();
				String bukkenNo2 = String.valueOf(bukkenList2.get("ESTATE_BUKKEN_NO"));
				String heyaNo2 = bukkenList2.get("HEYA_NO").toString();
				String uketukeNo2 = bukkenList2.get("UKETUKE_NO").toString();

				if (bukkenNo1.equals(bukkenNo2) && heyaNo1.equals(heyaNo2)) {
					if (!uketukeNo1.equals(uketukeNo2)) {
						if (!uketukeNoMap.containsValue(uketukeNo1)) {
							resData.add(resData1.get(i));
							uketukeNoMap.put("UKETUKE_NO", uketukeNo1);
						}
						if (!uketukeNoMap.containsValue(uketukeNo2)) {
							resData.add(resData1.get(i + 1));
							uketukeNoMap.put("UKETUKE_NO", uketukeNo2);
						}
					}
				} else {
					if (i == (resData1.size()-2)) {
						if (!uketukeNoMap.containsValue(uketukeNo1)) {
							resData.add(resData1.get(i));
						}
						resData.add(resData1.get(i + 1));
					} else {
						if (!uketukeNoMap.containsValue(uketukeNo1)) {
							resData.add(resData1.get(i));
						}
					}
				}
			}
		}
		 */

		Map<String, Object> kariosaeinfoList = ukg01015Services.getKariosaeInfo(tantouCd, kaisyacd, postcd, uketukeNo,
				senikenFlg, sort);
		List<Map<String, Object>> resData = (List<Map<String, Object>>) kariosaeinfoList.get("rst_list");
		ukg01015Data.put("authorityCd", authorityCd);
		ukg01015Data.put("postCd", postcd);
		String rowAttr = "";
		// ########## 2014/06/23 趙雲剛 UPD End
		// 2014/01/16 Start by SYS_胡  Jtest対応
		if (resData != null) {
		// 2014/01/16 End by SYS_胡  Jtest対応
			for (int i = 0; i < resData.size()-1; i++) {
				Map<String, Object> bukkenList = resData.get(i);
				Map<String, Object> bukkenList2 = resData.get(i + 1);

				String bukkenNo1 = String.valueOf(bukkenList.get("ESTATE_BUKKEN_NO"));
				String heyaNo1 = bukkenList.get("HEYA_NO").toString();
				String bukkenNo2 = String.valueOf(bukkenList2.get("ESTATE_BUKKEN_NO"));
				String heyaNo2 = bukkenList2.get("HEYA_NO").toString();

				if (bukkenNo1.equals(bukkenNo2) && heyaNo1.equals(heyaNo2)) {
					if (resData.get(i).get("ROWSPAN") == null) {
						rowAttr = "rowAttr" + i;
						resData.get(i).put("ROWSPAN", "rowAttrTop " + rowAttr);
					} else {
						resData.get(i).put("ROWSPAN", rowAttr);
					}
					resData.get(i + 1).put("ROWSPAN", rowAttr);
				}
			}
		// 2014/01/16 Start by SYS_胡  Jtest対応
		}
		// 2014/01/16 End by SYS_胡  Jtest対応

		if (resData != null && resData.size() > 0) {
			for (Map<String, Object> bukkenInfo : resData) {
				bukkenInfo.put("ESTATE_BUKKEN_NO_DISPLAY", DispFormat.getEstateBukkenNo((String) bukkenInfo.get("ESTATE_BUKKEN_NO")));
			}
		}

		// 画面の連絡手段の編集
		Util.getDataStatus(resData);

		ukg01015Data.put("kariosaeinfoList", resData);
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加 // parasoft-suppress UC.ACC "C38117JTest対応"
//		if("0".equals(senikenFlg)){ // parasoft-suppress UC.ACC "C38117JTest対応"
// // parasoft-suppress UC.ACC "C38117JTest対応"
//			ukg01015Data.put("kariosaeinfoListcount", resData.size()); // parasoft-suppress UC.ACC "C38117JTest対応"
//		}else{
//			ukg01015Data.put("kariosaeinfoListcount", kariosaeinfoList.get("rst_kumisu"));
//		}

		ukg01015Data.put("kariosaeinfoListcount", resData.size());
		// ########## 2014/06/23 趙雲剛 UPD End
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
		String processType = request.getParameter("processType");

		// ソート順をセッションに取得する
		HttpSession session = request.getSession();
		String sortkomoku = (String) session.getAttribute(Consts.UKG01015_SORT_INFO);

		if (Consts.UKG01015_SORT_JYUN.equals(sortkomoku)) {
			sortkomoku = "ESTATE_BUKKEN_NO DESC,HEYA_NO ASC,KARIOSAE_DATE ASC,YUSEN_JYUNI";
		} else {
			sortkomoku = Consts.UKG01015_SORT_JYUN;
		}

		// ソート順設定する
		session.setAttribute(Consts.UKG01015_SORT_INFO, sortkomoku);

		// 追客アラーム情報を取得する
		getKariosaeInfo(processType);
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 申込、キャンセルボタン押下時処理
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String mousicomiCancelProcess() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 担当者コードを取得
		String tantouCd = String.valueOf(loginInfo.get(Consts.USER_CD));
		// 店舗コードを取得
		String postCd = String.valueOf(loginInfo.get(Consts.POST_CD));
		// 受付no.を取得
		String uketukeNo = request.getParameter("uketukeNo");
		session.setAttribute(Consts.UKETUKENO, uketukeNo);
		// 物件no.
		String bukkenNo = request.getParameter("bukkenNo");
		// 部屋no.
		String heyaNo = request.getParameter("heyaNo");
		// 顧客コード
		String kokyakuCd = request.getParameter("kokyakuCd");
		// 顧客区分
		String kokyakuKb = request.getParameter("kokyakuKb");
		// 処理種類を取得
		String type = request.getParameter("type");

		// 仮押え状況一覧を取得する
		Map<String, Object> resData = ukg01015Services.mousicomiCancelProcess(kaisyaCd, uketukeNo, kokyakuCd, kokyakuKb, tantouCd,
		                                                                      postCd, bukkenNo, heyaNo, type);

		ukg01015Data = new HashMap<String, Object>();
		if ("0000000".equals(resData.get("rst_code"))) {
			// 仮押え状況一覧を取得する
			getKariosaeInfo(request.getParameter("processType"));
			session.setAttribute("chgFlg", "1");
		} else if ("MOUSICOMI".equals(type)) {
			ukg01015Data.put("MESSAGE", Util.getServerMsg("ERRS002", "申込み処理"));
		} else if ("CANCEL".equals(type)) {
			ukg01015Data.put("MESSAGE", Util.getServerMsg("ERRS002", "仮押え情報のキャンセル"));
		}
		ukg01015Data.put("UPD_RESTULT", resData.get("rst_code"));
		return "ajaxProcess";
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
		ukg01015Data = new HashMap<String, Object>();
		ukg01015Data.put("chgFlg", session.getAttribute("chgFlg"));
		session.removeAttribute("chgFlg");
		return "ajaxProcess";
	}

	// 2018/04/28 朱珂(SYS)  ADD START 案件C42036デザイン変更新規作成

	public String getUserAgent() throws Exception{
		String agentRequest = "";
		HttpServletRequest request = ServletActionContext.getRequest();
		String agent=request.getHeader("User-Agent").toLowerCase();
		if(agent.indexOf("firefox") > 0){
			agentRequest="1";
		} else {
			agentRequest="2";
		}

		return agentRequest;
	}
}
