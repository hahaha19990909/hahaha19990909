/**
 * @システム名: 受付システム
 * @ファイル名: UKG04013Action.java
 * @更新日付：: 2013/2/28
 * @Copyright: 2013 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG04013Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] チラシ作成
 * [説 明] チラシ作成アクション。
 * @author [作 成] 2013/02/28 趙雲剛(SYS)
 * </pre>
 */
public class UKG04013Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -5629458236532919212L;

	/** お客様情報画面用データ */
	private Map<String, Object> ukg04013Data = new HashMap<String, Object>();

	/** お客様情報サービス */
	private IUKG04013Service ukg04013Services;

	/**
	 * 物件№を指定して検索画面用データを
	 * @return ukg04013Data
	 */
	public Map<String, Object> getUkg04013Data() {
		return ukg04013Data;
	}

	/**
	 * 物件№を指定して検索画面用データ
	 * @param ukg04013Data
	 */
	public void setUkg04013Data(Map<String, Object> ukg04013Data) {
		this.ukg04013Data = ukg04013Data;
	}

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param ukg04012Services サービス対象
	 * </pre>
	 */
	public void setUKG04013Services(IUKG04013Service ukg04013Services) {

		this.ukg04013Services = ukg04013Services;
	}

	/**
	 * <pre>
	 * [説 明] 初期検索を行う。
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
		session.setAttribute(Consts.UKG04013_SORT_INFO, Consts.UKG04013_SORT_JYUN);
		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] 物件名リスト検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchBukkenNameList() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> loginInfo = getLoginInfo();
		HttpSession session = request.getSession();
		ukg04013Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));

		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 店舗コードを取得
		String postCd = String.valueOf(loginInfo.get(Consts.POST_CD));
		// 物件名
		String bukkenName = new String(request.getParameter("homemateBukkenName").getBytes("ISO8859-1"), "UTF-8");

		// 物件名リストを検索する
		List<Map<String, Object>> list = (List<Map<String, Object>>) ukg04013Services.searchBukkenNameList(kaisyaCd, postCd, bukkenName).get("rst_list");

		// 住所データ
		ukg04013Data.put("BUKKEN_NAME_LIST", Util.chkDropDownList(list, "BUKKEN_NAME"));

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 物件情報検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchBukkenInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> loginInfo = getLoginInfo();
		HttpSession session = request.getSession();
		// セッションからソート項目を取得する。
		String sortkomoku = (String) session.getAttribute(Consts.UKG04013_SORT_INFO);
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 物件№
		String estatebukkenno = request.getParameter("estatebukkenno");
		// 物件情報を検索する
		Map<String, Object> bukkenInfo = (Map<String, Object>) ukg04013Services.searchBukkenInfo(kaisyaCd, estatebukkenno);
		// 物件情報
		List<Map<String, Object>> bukken = (List<Map<String, Object>>) bukkenInfo.get("rst_bukken_info");
		ukg04013Data.put("NODATA_MESSAGE", Util.getServerMsg("MSGS003", "部屋情報"));
		if (bukken == null || bukken.size() == 0) {
			ukg04013Data.put("rst_msg", Util.getServerMsg("ERRS006", "物件"));
		} else {
			ukg04013Data.put("BUKKEN_INFO", bukken);
			String bukkenNo = (String) bukken.get(0).get("BUKKENNO");
			ukg04013Data.put("IMAGEPATH", "imgIO.imgIO?type=1&path=");
			// 部屋情報を検索する
			Map<String, Object> heya = (Map<String, Object>) ukg04013Services.searchHeyaList(kaisyaCd, bukkenNo, sortkomoku);

			List<Map<String, Object>> heyaList = (List<Map<String, Object>>) heya.get("rst_heya_info");
			ukg04013Data.put("HEYA_INFO", heyaList);
		}

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 部屋情報検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchHeyaList() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> loginInfo = getLoginInfo();
		HttpSession session = request.getSession();
		String sortitem = request.getParameter("sortItem");
		String sortjun = request.getParameter("sortjun");
		String oldsortjyun = request.getParameter("oldsortjyun");

		String oldsort = sortitem + " " + oldsortjyun;
		String newsort = sortitem + " " + sortjun;

		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 物件№
		String bukkenno = request.getParameter("bukkenno");

		// セッションからソート項目を取得する。
		String sortkomoku = (String) session.getAttribute(Consts.UKG04013_SORT_INFO);
		if (sortkomoku.endsWith(oldsort)) {
			oldsort = "," + oldsort;
		} else if (sortkomoku.startsWith(oldsort)) {
			oldsort = oldsort + ",";
		}
		// 新しいソート項目処理
		sortkomoku = sortkomoku.replace(oldsort, "").replace(",,", ",");
		StringBuilder strbuildsort = new StringBuilder(sortkomoku);
		newsort = newsort + ",";
		strbuildsort.insert(0, newsort);

		// ソート項目がセッションに保存する。
		session.setAttribute(Consts.UKG04013_SORT_INFO, strbuildsort.toString());

		// 物件情報を検索する
		Map<String, Object> bukkenInfo = (Map<String, Object>) ukg04013Services.searchHeyaList(kaisyaCd, bukkenno, strbuildsort.toString());
		// 部屋情報
		List<Map<String, Object>> heyaList = (List<Map<String, Object>>) bukkenInfo.get("rst_heya_info");
		if (heyaList == null || heyaList.size() == 0) {
			ukg04013Data.put("rst_msg", Util.getServerMsg("ERRS006", "部屋"));
		} else {
			ukg04013Data.put("HEYA_INFO", heyaList);
		}

		return "ajaxProcess";
	}
}
