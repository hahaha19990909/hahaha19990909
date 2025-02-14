/**
 * @システム名: 受付システム
 * @ファイル名: UKG04013Action.java
 * @更新日付：:  2014/01/08
 * @Copyright: 2014 token corporation All right reserved
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
import jp.co.token.uketuke.service.IUKG05023Service;

/**
 * <pre>
 * [機 能] 物件指定
 * [説 明] 物件指定アクション。
 * @author [作 成] 2014/01/8 劉琦(SYS) (案件C37075)オリジナル情報誌リプレース
 * </pre>
 */
public class UKG05023Action extends BaseAction{

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -5629458236532919212L;

 	/** 物件指定画面用データ*/
	private Map<String, Object> ukg05023Data = new HashMap<String, Object>();

	/** お客様情報サービス */
	private IUKG05023Service ukg05023Services;
	/**
	 *  物件指定画面用データ
	* @return ukg05023Data
	*/
	public Map<String, Object> getUkg05023Data() {
		return ukg05023Data;
	}

	/**
	 *  物件指定画面用データ
	 * @param ukg05023Data
	 */
	public void setUkg05023Data(Map<String, Object> ukg05023Data) {
		this.ukg05023Data = ukg05023Data;
	}

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param ukg04012Services サービス対象
	 * </pre>
	 */
	public void setUKG05023Services(IUKG05023Service ukg05023Services) {
		this.ukg05023Services = ukg05023Services;
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
		session.setAttribute(Consts.UKG05023_SORT_INFO, Consts.UKG05023_SORT_JYUN);
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
		ukg05023Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 店舗コードを取得
		String postCd = String.valueOf(loginInfo.get(Consts.POST_CD));
		// 物件名
		String bukkenName = new String(request.getParameter("homemateBukkenName").getBytes("ISO8859-1"), "UTF-8");
		// 物件名リストを検索する
		List<Map<String, Object>> list = (List<Map<String, Object>>) ukg05023Services.searchBukkenNameList(kaisyaCd, postCd, bukkenName).get("rst_list");
		// 住所データ
		ukg05023Data.put("BUKKEN_NAME_LIST", Util.chkDropDownList(list, "BUKKEN_NAME"));
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 物件リスト検索を行う。
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
		String sortkomoku = (String) session.getAttribute(Consts.UKG05023_SORT_INFO);
		// ページ番号
		String strPageNum = "";
		if(session.getAttribute(Consts.UKG05023_PAGE_NUM) != null){
			strPageNum = (String)session.getAttribute(Consts.UKG05023_PAGE_NUM);
		}else{
			strPageNum = String.valueOf(Consts.ONE);
		}
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// ホームメイト物件No.
		String estateBukkenNo = request.getParameter("estatebukkenno");
		// 物件情報を検索する
		Map<String, Object> bukkenInfo = (Map<String, Object>) ukg05023Services.searchBukkenInfo(kaisyaCd,estateBukkenNo,sortkomoku,strPageNum);
		// 物件情報
		String strRstPageNum = "1";
		String strJyouhousiCount = "0";
		List<Map<String, Object>> heyaList = null;
		heyaList = changeBukkenNO((List<Map<String, Object>>) bukkenInfo.get("rst_bukken_info"));
		strRstPageNum = (String)bukkenInfo.get("rst_pageNum");
		strJyouhousiCount = (String)bukkenInfo.get("rst_recordcount");
		if (heyaList == null || heyaList.size() == 0) {
			ukg05023Data.put("rst_msg", Util.getServerMsg("MSGS003", "該当データ"));
		}
		ukg05023Data.put("BUKKENHEYA_INFO", heyaList);
		ukg05023Data.put("strJyouhousiCount", strJyouhousiCount);
		ukg05023Data.put("pageNum", strRstPageNum);
		return "ajaxProcess";
	}
	/**
	 *　物件№を変換する
	 * @param rstTirasiinfo チラシ作成データ
	 * @return rstTirasiinfo チラシ作成データ
	 */
	private List<Map<String, Object>> changeBukkenNO(List<Map<String, Object>> rstTirasiinfo) {
	    if (rstTirasiinfo != null && rstTirasiinfo.size() > 0) {
			for (Map<String, Object> tirasiinfo : rstTirasiinfo) {
				tirasiinfo.put("HOMEMATE_BUKKEN_NO_DISPLAY", DispFormat.getEstateBukkenNo((String) tirasiinfo.get("ESTATEBUKKENNO")));
			}
		}
	    return rstTirasiinfo;
    }

	/**
	 * <pre>
	 * [説 明]物件ソート
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
		// ホームメイト物件No.
		String estateBukkenNo = request.getParameter("estatebukkenno");
		String oldsort = sortitem + " " + oldsortjyun;
		String newsort = sortitem + " " + sortjun;
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// セッションからソート項目を取得する。
		String sortkomoku = (String) session.getAttribute(Consts.UKG05023_SORT_INFO);
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
		session.setAttribute(Consts.UKG05023_SORT_INFO, strbuildsort.toString());
		// 物件情報を検索する
		Map<String, Object> bukkenInfo = (Map<String, Object>) ukg05023Services.searchBukkenInfo(kaisyaCd,estateBukkenNo,strbuildsort.toString(),"1");
		// 物件情報
		String strRstPageNum = "1";
		String strJyouhousiCount = "0";
		List<Map<String, Object>> heyaList = changeBukkenNO((List<Map<String, Object>>) bukkenInfo.get("rst_bukken_info"));
		strRstPageNum = (String)bukkenInfo.get("rst_pageNum");
		strJyouhousiCount = (String)bukkenInfo.get("rst_recordcount");
		if (heyaList == null || heyaList.size() == 0) {
			ukg05023Data.put("rst_msg", Util.getServerMsg("MSGS003", "該当データ"));
		}
		ukg05023Data.put("BUKKENHEYA_INFO", heyaList);
		ukg05023Data.put("strJyouhousiCount", strJyouhousiCount);
		ukg05023Data.put("pageNum", strRstPageNum);
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] ページ切り替える。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String pageIndexChg() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String condition = request.getParameter("condition");
		Map<String, String> paramMap = Util.getParamMap(condition);
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String)mapLoginInfo.get(Consts.KAISYA_CD);
		// ソート順取得する
		String strSort = String.valueOf(session.getAttribute(Consts.UKG05023_SORT_INFO));
		// ホームメイト物件No.
		String estateBukkenNo = paramMap.get("estateBukkenNo");
		// 物件情報データを取得
		HashMap<String, Object> mapJyouhousiData = (HashMap<String, Object>)ukg05023Services
				.searchBukkenInfo(strKaisyaCd,estateBukkenNo,strSort,paramMap.get("pageNum"));
		List<Map<String, Object>> listJyouhousiData = null;
		String strRstPageNum = "1";
		String strJyouhousiCount = "0";
		if(null != mapJyouhousiData){
			listJyouhousiData = changeBukkenNO((List<Map<String, Object>>) mapJyouhousiData.get("rst_bukken_info"));
			strRstPageNum = (String)mapJyouhousiData.get("rst_pageNum");
			strJyouhousiCount = (String)mapJyouhousiData.get("rst_recordcount");
		}
		//ページ番号を設定
		session.setAttribute(Consts.UKG05023_PAGE_NUM,strRstPageNum);
		if(null == listJyouhousiData || 0 == listJyouhousiData.size()){
			ukg05023Data.put("rst_msg", Util.getServerMsg("MSGS003", "該当データ"));
		}
		ukg05023Data.put("BUKKENHEYA_INFO", listJyouhousiData);
		ukg05023Data.put("strJyouhousiCount", strJyouhousiCount);
		ukg05023Data.put("pageNum", strRstPageNum);
		return "ajaxProcess";
	}
}
