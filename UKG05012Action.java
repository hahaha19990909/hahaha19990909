/**
 * @システム名: 受付システム
 * @ファイル名: UKG05012Action.java
 * @更新日付： 2013/12/26
 * @Copyright: 2013 token corporation All right reserved
 * 更新履歴：2018/02/09      孫博易(SYS)       1.01         (案件番号C42036)デザイン変更
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
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG05012Service;

/**
 * <pre>
 * [機 能] 情報誌メニュー
 * [説 明] 情報誌メニューアクション。
 * @author [作 成] 2013/12/26 謝超(SYS) (案件C37075)オリジナル情報誌リプレース
 * </pre>
 */
public class UKG05012Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -5629458236532919212L;

	/** 情報誌メニュー画面用データ */
	private Map<String, Object> ukg05012Data = new HashMap<String, Object>();

	/** 情報誌メニューサービスを取得する */
	private IUKG05012Service ukg05012Services = null;

	/**
	 * <pre>
	 * [説 明] 情報誌メニューサービス対象を設定する。
	 * @param ukg05012Service サービス対象
	 * </pre>
	 */
	public void setUKG05012Services(IUKG05012Service ukg05012Services) {
		this.ukg05012Services = ukg05012Services;
	}

	/**
	 * 情報誌メニュー画面用データ
	 * 
	 * @return ukg05012Data　画面データ
	 */
	public Map<String, Object> getUkg05012Data() {
		return ukg05012Data;
	}

	/**
	 * 情報誌メニュー画面用データ
	 * 
	 * @param ukg05012Data
	 *            画面データ
	 */
	public void setUkg05012Data(Map<String, Object> ukg05012Data) {
		this.ukg05012Data = ukg05012Data;
	}

	/**
	 * <pre>
	 * [説 明] 初期検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);
		
		// セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String)mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String)mapLoginInfo.get(Consts.POST_CD);
		String saveFlag = request.getParameter("saveFlag");
		String flag = request.getParameter("flag");
		// 2018/02/09 SYS_孫博易 ADD START   (案件番号C42036)デザイン変更
		String hidHakkouDate = request.getParameter("hidHakkouDate");
		String hidSeq = request.getParameter("hidSeq");
		String sinntyoku = request.getParameter("sinntyoku");
		// 2018/02/09 SYS_孫博易 ADD END   (案件番号C42036)デザイン変更
		
		// バックナンバー日数を取得
		String strBacknumberNitisuu = PropertiesReader.getIntance().getProperty(Consts.UKG05012_BACKNUMBER_NITISUU);
		
		String strPageNum = String.valueOf(Consts.ONE);
		
		//ソート順
		String strSort = "";
		String sortItem = "";
		if(session.getAttribute(Consts.UKG05012_SORT_INFO) != null){
			// ソート順を設定する
			if("1".equals(saveFlag)){
				strSort = Consts.UKG05012_SORT_JYUN;
				session.setAttribute(Consts.UKG05012_SORT_INFO, Consts.UKG05012_SORT_JYUN);
				session.setAttribute(Consts.UKG05012_SENTAKU_SORT, Consts.UKG05012_SORT_HAKKOUBI);
				sortItem = "UKG05012_SORT_HAKKOUBI";
			}else{
				if(!"1".equals(flag)){
					session.setAttribute(Consts.UKG05012_SORT_INFO, Consts.UKG05012_SORT_JYUN);
					session.setAttribute(Consts.UKG05012_SENTAKU_SORT, Consts.UKG05012_SORT_HAKKOUBI);
				}
				strSort = (String)session.getAttribute(Consts.UKG05012_SORT_INFO);
				sortItem = (String)session.getAttribute(Consts.UKG05012_SENTAKU_SORT);
			}
		}else{
			// ソート順を設定する
			strSort = Consts.UKG05012_SORT_JYUN;
			session.setAttribute(Consts.UKG05012_SORT_INFO, Consts.UKG05012_SORT_JYUN);
			session.setAttribute(Consts.UKG05012_SENTAKU_SORT, Consts.UKG05012_SORT_HAKKOUBI);
		}
		
		// 情報誌データを取得
		HashMap<String, Object> mapJyouhousiData = (HashMap<String, Object>)ukg05012Services
				.getJyouhousiList(strKaisyaCd, strPostCd, strBacknumberNitisuu, strSort, strPageNum);
		
		List<Map<String, Object>> listJyouhousiData = null;
		String strJyouhousiCount = "0";
		String strRstPageNum = "1";
		if(null != mapJyouhousiData){
			listJyouhousiData = (List<Map<String, Object>>)mapJyouhousiData.get("rst_list");
			strJyouhousiCount = (String)mapJyouhousiData.get("rst_recordcount");
			strRstPageNum = (String)mapJyouhousiData.get("rst_pageNum");
		}
		//ページ番号を設定
		session.setAttribute(Consts.UKG05012_PAGE_NUM,strRstPageNum); // parasoft-suppress CDD.DUPC "C37075JTest対応"
		
		// 2018/02/09 SYS_孫博易 UPD START   (案件番号C42036)デザイン変更
//		if(null == listJyouhousiData || 0 == listJyouhousiData.size()){ // parasoft-suppress CDD.DUPC "C37075JTest対応"
//			ukg05012Data.put("NODATA_MESSAGE", Util.getServerMsg("MSGS003", "オリジナル情報誌の登録"));
//		}
		ukg05012Data.put("NODATA_MESSAGE", Util.getServerMsg("MSGS003", "オリジナル情報誌の登録"));
		// 2018/02/09 SYS_孫博易 UPD END   (案件番号C42036)デザイン変更
		//西暦から和暦を変換する。
		for(Map<String, Object> map : listJyouhousiData){
			String hakkouDate = ((String) map.get("HAKKOU_DATE")).replaceAll("/","");
			String month = hakkouDate.substring(4, 6);
			String day = hakkouDate.substring(6, hakkouDate.length());
			hakkouDate = DispFormat.getWareki(hakkouDate) + "年" + month + "月" + day + "日";
			map.put("HAKKOU_DATEDisp",hakkouDate);
		}
		ukg05012Data.put("listJyouhousiData", listJyouhousiData);
		ukg05012Data.put("strJyouhousiCount", strJyouhousiCount);
		ukg05012Data.put("sortItem", sortItem);
		// 2018/02/09 SYS_孫博易 ADD START   (案件番号C42036)デザイン変更
		ukg05012Data.put("hidHakkouDate", hidHakkouDate);
		ukg05012Data.put("hidSeq", hidSeq);
		ukg05012Data.put("sinntyoku", sinntyoku);
		// 2018/02/09 SYS_孫博易 ADD END   (案件番号C42036)デザイン変更
		return successForward();
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
		String strPostCd = (String)mapLoginInfo.get(Consts.POST_CD);
		
		// バックナンバー日数を取得
		String strBacknumberNitisuu = PropertiesReader.getIntance().getProperty(Consts.UKG05012_BACKNUMBER_NITISUU);

		// ソート順取得する
		String strSort = String.valueOf(session.getAttribute(Consts.UKG05012_SORT_INFO));
		
		// 情報誌データを取得
		HashMap<String, Object> mapJyouhousiData = (HashMap<String, Object>)ukg05012Services
				.getJyouhousiList(strKaisyaCd, strPostCd, strBacknumberNitisuu, strSort, paramMap.get("pageNum"));
		
		List<Map<String, Object>> listJyouhousiData = null;
		String strRstPageNum = "1";
		String strJyouhousiCount = "0";
		if(null != mapJyouhousiData){
			listJyouhousiData = (List<Map<String, Object>>)mapJyouhousiData.get("rst_list");
			strRstPageNum = (String)mapJyouhousiData.get("rst_pageNum");
			strJyouhousiCount = (String)mapJyouhousiData.get("rst_recordcount");
		}
		
		//ページ番号を設定
		session.setAttribute(Consts.UKG05012_PAGE_NUM,strRstPageNum);
		if(null == listJyouhousiData || 0 == listJyouhousiData.size()){
			ukg05012Data.put("NODATA_MESSAGE", Util.getServerMsg("MSGS003", "オリジナル情報誌の登録"));
		}
		//西暦から和暦を変換する。
		for(Map<String, Object> map : listJyouhousiData){
			String hakkouDate = ((String) map.get("HAKKOU_DATE")).replaceAll("/","");
			String month = hakkouDate.substring(4, 6);
			String day = hakkouDate.substring(6, hakkouDate.length());
			hakkouDate = DispFormat.getWareki(hakkouDate) + "年" + month + "月" + day + "日";
			map.put("HAKKOU_DATEDisp",hakkouDate);
		}
		ukg05012Data.put("listJyouhousiData", listJyouhousiData);
		ukg05012Data.put("strJyouhousiCount", strJyouhousiCount);
		ukg05012Data.put("pageNum", strRstPageNum);
		
		return "ajaxProcess";
	}
	
	/**
	 * <pre>
	 * [説 明] ソート
	 * @return ソート処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String sort() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		
		String sortItem = request.getParameter("sortItem");
		
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String)mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String)mapLoginInfo.get(Consts.POST_CD);
		
		// バックナンバー日数を取得
		String strBacknumberNitisuu = PropertiesReader.getIntance().getProperty(Consts.UKG05012_BACKNUMBER_NITISUU);

		// ソート順取得する
		String strSort = "";
		if("UKG05012_SORT_HAKKOUBI".equals(sortItem)){
			strSort = Consts.UKG05012_SORT_HAKKOUBI + Consts.UKG05012_SORT_JYUN;
		}else if("UKG05012_SORT_TANTOU".equals(sortItem)){
			strSort = Consts.UKG05012_SORT_TANTOU + Consts.UKG05012_SORT_JYUN;
		}else if("UKG05012_SORT_PAGESUU".equals(sortItem)){
			strSort = Consts.UKG05012_SORT_PAGESUU + Consts.UKG05012_SORT_JYUN;
		}else {
			strSort = Consts.UKG05012_SORT_KEISAISUU + Consts.UKG05012_SORT_JYUN;
		}
		session.setAttribute(Consts.UKG05012_SENTAKU_SORT, sortItem);
		session.setAttribute(Consts.UKG05012_SORT_INFO, strSort);

		// ページ番号
		session.setAttribute(Consts.UKG05012_PAGE_NUM, String.valueOf(Consts.ONE));
		
		// 情報誌データを取得
		HashMap<String, Object> mapJyouhousiData = (HashMap<String, Object>)ukg05012Services
				.getJyouhousiList(strKaisyaCd, strPostCd, strBacknumberNitisuu, strSort, "1");
		
		List<Map<String, Object>> listJyouhousiData = null;
		
		String strJyouhousiCount = "0";
		if(null != mapJyouhousiData){
			listJyouhousiData = (List<Map<String, Object>>)mapJyouhousiData.get("rst_list");
			strJyouhousiCount = (String)mapJyouhousiData.get("rst_recordcount");
		}
		
		if(null == listJyouhousiData || 0 == listJyouhousiData.size()){
			ukg05012Data.put("NODATA_MESSAGE", Util.getServerMsg("MSGS003", "オリジナル情報誌の登録"));
		}
		//西暦から和暦を変換する。
		for(Map<String, Object> map : listJyouhousiData){
			String hakkouDate = ((String) map.get("HAKKOU_DATE")).replaceAll("/","");
			String month = hakkouDate.substring(4, 6);
			String day = hakkouDate.substring(6, hakkouDate.length());
			hakkouDate = DispFormat.getWareki(hakkouDate) + "年" + month + "月" + day + "日";
			map.put("HAKKOU_DATEDisp",hakkouDate);
		}
		ukg05012Data.put("listJyouhousiData", listJyouhousiData);
		ukg05012Data.put("strJyouhousiCount", strJyouhousiCount);

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] ukg05012画面データチェック処理
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String ukg05012Check() throws Exception {

		
		return "ukg05013Check";
	}
	
	/**
	 * <pre>
	 * [説 明] ukg05012画面データチェック処理
	 * @return 処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String ukg05012DataCheck() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();		
		
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String kaisyacd = (String)mapLoginInfo.get(Consts.KAISYA_CD);
		String postCd = (String)mapLoginInfo.get(Consts.POST_CD);
		
		String hakkouDate = request.getParameter("hakkouDate");
		String seq = request.getParameter("seq");

		Map<String, Object> result = ukg05012Services.getUkg05012Check(kaisyacd,postCd,hakkouDate,seq);
		
		ukg05012Data.put("errMsg", "");
		if (!"1".equals(result.get("checkFlg"))) {
			ukg05012Data.put("errMsg", Util.getServerMsg("ERRS013", "該当情報誌データ"));
		}

		ukg05012Data.put("checkFlg", result.get("checkFlg"));
		
		return "ajaxProcess";

	}	
}