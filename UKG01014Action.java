/**
 * <pre>
 * @システム名: 受付システム
 * @ファイル名: UKG01014Action.java
 * [機 能] お客様希望物件 新着情報。
 * [説 明] お客様希望物件 新着情報を検索し、表示する。
 * @author [作 成] 2012/05/02 戴氷馨(SYS)
 * 更新履歴：2014/06/23 	趙雲剛 (SYS)		1.01		(案件C38117)セールスポイント編集機能の追加
 * @Copyright: 2012 token corporation All right reserved
 * </pre>
 */
package jp.co.token.uketuke.action;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG01014Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] お客様希望物件 新着情報
 * [説 明] お客様希望物件 新着情報を検索し、表示する。
 * @author [作 成]  SYS_戴氷馨
 * </pre>
 */
public class UKG01014Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;

	/** お客様希望物件 新着情報サービス */
	private IUKG01014Service ukg01014Services = null;

	/**
	 * <pre>
	 * [説 明] お客様希望物件 新着情報サービス対象を設定する。
	 * @param ukg01014Service サービス対象
	 * </pre>
	 */
	public void setUkg01014Services(IUKG01014Service ukg01014Services) {

		this.ukg01014Services = ukg01014Services;
	}

	/** お客様希望物件 新着情報画面用データ */
	private Map<String, Object> ukg01014Data = null;

	/**
	 * お客様希望物件 新着情報画面用データ
	 *
	 * @return ukg01014Data
	 */
	public Map<String, Object> getUkg01014Data() {

		return ukg01014Data;
	}

	/**
	 * お客様希望物件 新着情報画面用データ
	 *
	 * @param ukg01014Data
	 */
	public void setUkg01014Data(Map<String, Object> ukg01014Data) {

		this.ukg01014Data = ukg01014Data;
	}

	/**
	 * <pre>
	 * [説 明] お客様希望物件 新着情報。
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
		session.setAttribute(Consts.UKG01014_SORT_INFO, Consts.UKG01014_SORT_JYUN);
		session.setAttribute(Consts.UKG01014_SORT_FLAG, "1");

		// 新着情報を取得する
		getSintyakuJyouhou(request.getParameter("processType"));

		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] 新着情報を取得する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	private void getSintyakuJyouhou(String processType) throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest(); // parasoft-suppress CDD.DUPC "C38117JTest対応"

		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		// セッションのログイン情報取得する
		Map<String, Object> loginInfo = getLoginInfo();
		// 会社コード
		String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));

		// 仲介担当者
		String tantouCd = String.valueOf(loginInfo.get(Consts.USER_CD));

		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		// 店舗コード
		//String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));
		String postcd = request.getParameter("postCd");
		String authorityCd = (String) loginInfo.get(Consts.USER_AUTHORITY_CD);
		// ########## 2014/06/23 趙雲剛 UPD End
		// 受付No.
		String uketukeNo = request.getParameter("uketukeNo");

		// ソート列
		String sortitem = request.getParameter("sortitem");

		if(Util.isEmpty(sortitem)){
			sortitem = "RU.KOKYAKU_RYAKU_NAME_K";
		}

		// 受仮押え状況データリストを取得する
		String senikenFlg = processType;

		// セッション情報からソート順を取得する
		String sort = String.valueOf(session.getAttribute(Consts.UKG01014_SORT_INFO));

		String systime = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		// 初期化ソート順を設定する
		ukg01014Data = new HashMap<String, Object>();
		ukg01014Data.put("sortkomoku", sort);
		ukg01014Data.put("kaisyacd", kaisyacd);
		ukg01014Data.put("uketukeNo", uketukeNo);
		ukg01014Data.put("processType", processType);
		ukg01014Data.put("sysDate", systime);

		sort = sort.replace("#", ",");
		Map<String, Object> kariosaeinfoList = ukg01014Services.getSintyakuInfo(tantouCd, kaisyacd, postcd, senikenFlg,
        sort);
        // ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
        ukg01014Data.put("postCd", postcd);
        ukg01014Data.put("authorityCd", authorityCd);
		// ########## 2014/06/23 趙雲剛 ADD End
		
		List<Map<String, Object>> resData = (List<Map<String, Object>>) kariosaeinfoList.get("rst_list");
		String rowAttr = "";
		// テーブルグループ化
		for (int i = 0; i < resData.size()-1; i++) {
			Map<String, Object> currentData = resData.get(i);
			Map<String, Object> nextData = resData.get(i + 1);
			if ("RU.KOKYAKU_RYAKU_NAME_K".equals(sortitem) || "CHUKAI_TANTOU_CD".equals(sortitem)) {
				String kokyakuCd1;
				String kokyakuCd2;
				if(currentData.get("UKETUKE_NO") != null){
					kokyakuCd1 = currentData.get("UKETUKE_NO").toString();
				}
				else{
					kokyakuCd1 = "0";
				}
				if(nextData.get("UKETUKE_NO") != null){
					kokyakuCd2 = nextData.get("UKETUKE_NO").toString();
				}
				else{
					kokyakuCd2 = "0";
				}

				if (kokyakuCd1.equals(kokyakuCd2)) {
					if (currentData.get("ROWSPAN_KOKYAKU") == null) {
						rowAttr = "rowAttr" + i;
						currentData.put("ROWSPAN_KOKYAKU", "rowAttrTop "+rowAttr);
					} else {
						currentData.put("ROWSPAN_KOKYAKU", rowAttr);
					}
					nextData.put("ROWSPAN_KOKYAKU", rowAttr);
				}
			}


			if ("ESTATE_BUKKEN_NO#SB.HEYA_NO".equals(sortitem)) {
				String bukkenNo1 = currentData.get("ESTATE_BUKKEN_NO").toString();
				String heyaNo1 = currentData.get("HEYA_NO").toString();
				String bukkenNo2 = nextData.get("ESTATE_BUKKEN_NO").toString();
				String heyaNo2 = nextData.get("HEYA_NO").toString();
				if (bukkenNo1.equals(bukkenNo2) && heyaNo1.equals(heyaNo2)) {
					if (currentData.get("ROWSPAN_BUKKEN") == null) {
						rowAttr = "rowAttr" + i;
						currentData.put("ROWSPAN_BUKKEN", "rowAttrTop "+rowAttr);
					} else {
						currentData.put("ROWSPAN_BUKKEN", rowAttr);
					}
					nextData.put("ROWSPAN_BUKKEN", rowAttr);
				}
			}
		}
		// 画面の連絡手段の編集
		Util.getDataStatus(resData);

		ukg01014Data.put("sintyakuJyouhouListcount", resData.size());
		ukg01014Data.put("sintyakuJyouhouList", resData);
		ukg01014Data.put("sessionId", session.getId());
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
		String sortjun = request.getParameter("sortjun");
		String oldsortjyun = request.getParameter("oldsortjyun");

		String[] sortArry = request.getParameter("sortitem").split("#");

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

		// ソート順をセッションに取得する
		HttpSession session = request.getSession();
		String sortkomoku = (String) session.getAttribute(Consts.UKG01014_SORT_INFO);

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
		session.setAttribute(Consts.UKG01014_SORT_INFO, sortkomoku);

		// 追客アラーム情報を取得する
		getSintyakuJyouhou(processType);
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 解除ボタン押下時処理
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String kaijyoProcess() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 受付no.を取得
		String uketukeNo = request.getParameter("uketukeNo");
		// 物件no.
		String bukkenNo = request.getParameter("bukkenNo");
		// 部屋no.
		String heyaNo = request.getParameter("heyaNo");
		// 更新日付
		String updDate = request.getParameter("updDate");

		// 解除行の更新日付を取得する
		Map<String, Object> sinsyakuList = ukg01014Services.kaijyoProcess(kaisyaCd, uketukeNo, bukkenNo, heyaNo, updDate);

		ukg01014Data = new HashMap<String, Object>();
		if ("ERRS009".equals(sinsyakuList.get("rst_code"))) {
			ukg01014Data.put("MESSAGE", Util.getServerMsg("ERRS009", ""));
		}
		else if("ERRS002".equals(sinsyakuList.get("rst_code"))) {
			ukg01014Data.put("MESSAGE", Util.getServerMsg("ERRS002", "新着情報処理"));
		}
		else{
			session.setAttribute("chgFlg", "1");
			getSintyakuJyouhou(request.getParameter("processType"));
		}

		ukg01014Data.put("UPD_RESTULT", sinsyakuList.get("rst_code"));
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] お客様名クリック時処理
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String gotoUKG03010() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> loginInfo = getLoginInfo();
		ukg01014Data = new HashMap<String, Object>();
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 受付no.を取得
		String uketukeNo = request.getParameter("uketukeNo");
		// セッションidを取得
		String sessionId = request.getParameter("sessionId");

		// 希望条件検索ワーク追加
		Map<String, Object> sinchaku = ukg01014Services.addSinchaku(sessionId, kaisyaCd, uketukeNo, new Date());
		if ("0000000".equals(sinchaku.get("rst_code"))) {
			// ソート順をセッションに取得する
			HttpSession session = request.getSession();
			// セッションに下記の値を設定する。
			session.setAttribute(Consts.UKETUKENO, uketukeNo);
		} else {
			ukg01014Data.put("message", Util.getServerMsg("ERRS002", "登録処理"));
		}
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
		ukg01014Data = new HashMap<String, Object>();
		ukg01014Data.put("chgFlg", session.getAttribute("chgFlg"));
		session.removeAttribute("chgFlg");
		return "ajaxProcess";
	}

}
