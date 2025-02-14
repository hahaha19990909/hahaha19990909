/**
 * @システム名: 受付システム
 * @ファイル名: UKG05011Action.java
 * @更新日付：:  2014/01/20
 * @Copyright: 2014 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG05011Service;

/**
 * <pre>
 * [機 能] 表紙設定
 * [説 明]表紙設定アクション。
 * @author [作 成] 2014/01/20劉琦(SYS) (案件C37075)オリジナル情報誌リプレース
 * </pre>
 */
public class UKG05011Action extends BaseAction{

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -5629458236532919212L;

 	/**表紙設定画面用データ*/
	private Map<String, Object> ukg05011Data = new HashMap<String, Object>();

	/** 表紙設定サービスを取得する  */
	private IUKG05011Service ukg05011Services;

	public Map<String, Object> getUkg05011Data() {
		return ukg05011Data;
	}

	public void setUkg05011Data(Map<String, Object> ukg05011Data) {
		this.ukg05011Data = ukg05011Data;
	}

	public void setUkg05011Services(IUKG05011Service ukg05011Services) {
		this.ukg05011Services = ukg05011Services;
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
		// セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		// 遷移フラグ(1:新規 2:編集 3:流用)
		String strProcessType = request.getParameter("processType");
		//情報誌遷移元フラグ
		String strpageflag = request.getParameter("pageflag");
		if(("") .equals(strProcessType) || strProcessType == null)
		{
			strProcessType = "1";
		}
		//発行日
		String hakkouDate = "";
		if(("1").equals(strProcessType))
		{
			//発行日(システム日付)
			 Date date = new Date();
			 hakkouDate = Util.formatDate(date, Consts.FORMAT_DATE_YMD);
		}else{
			//発行日
			 hakkouDate = request.getParameter("hakkouDate");
		}
		//情報誌名
		String magazineName = request.getParameter("magazineName");
		if (!Util.isEmpty(magazineName)) {
			if ("ISO-8859-1".equals(Util.getEncoding(magazineName))) {
				magazineName = new String(magazineName.getBytes("ISO-8859-1"), "UTF-8");
			}
		}
		//表紙タイプ
		String hyoushiType = request.getParameter("hyoushiType");
		//表紙タイプCode
		String hyoushiTypeCode = request.getParameter("hyoushiTypeCode");
		//賃貸物件情報１
		String tintaiBukken1 = request.getParameter("tintaiBukken1");
		//賃貸物件情報２
		String tintaiBukken2 = request.getParameter("tintaiBukken2");
		//賃貸物件情報３
		String tintaiBukken3 = request.getParameter("tintaiBukken3");
		//賃貸物件情報
		String tintaiBukken = request.getParameter("tintaiBukken");
		//キャッチコピー１
		String catchCopy1 = request.getParameter("catchCopy1");
		if (!Util.isEmpty(catchCopy1)) {
			if ("ISO-8859-1".equals(Util.getEncoding(catchCopy1))) {
				catchCopy1 = new String(catchCopy1.getBytes("ISO-8859-1"), "UTF-8");
			}
		}
		//キャッチコピー２
		String catchCopy2 = request.getParameter("catchCopy2");
		if (!Util.isEmpty(catchCopy2)) {
			if ("ISO-8859-1".equals(Util.getEncoding(catchCopy2))) {
				catchCopy2 = new String(catchCopy2.getBytes("ISO-8859-1"), "UTF-8");
			}
		}
		//キャッチコピー３
		String catchCopy3 = request.getParameter("catchCopy3");
		if (!Util.isEmpty(catchCopy3)) {
			if ("ISO-8859-1".equals(Util.getEncoding(catchCopy3))) {
				catchCopy3 = new String(catchCopy3.getBytes("ISO-8859-1"), "UTF-8");
			}
		}
		//設備一覧
		String setubiItiranUmu = request.getParameter("setubiItiranUmu");
		//間取説明
		String madoriSetumeiUmu = request.getParameter("madoriSetumeiUmu");
		//裏表紙
		String uraHyoushiUmu = request.getParameter("uraHyoushiUmu");
		Map<String, Object> magazineData = new HashMap<String, Object>();
		magazineData.put("HAKKOU_DATE", hakkouDate);
		magazineData.put("MAGAZINE_NAME", magazineName);
		magazineData.put("KOUMOKU_SB_CD",  hyoushiType);
		magazineData.put("KOUMOKU_SB_VAL",  hyoushiTypeCode);
		magazineData.put("TINTAI_BUKKEN1", tintaiBukken1);
		magazineData.put("TINTAI_BUKKEN2", tintaiBukken2);
		magazineData.put("TINTAI_BUKKEN3", tintaiBukken3);
		magazineData.put("TINTAI_BUKKEN", tintaiBukken);
		magazineData.put("CATCH_COPY1", catchCopy1);
		magazineData.put("CATCH_COPY2", catchCopy2);
		magazineData.put("CATCH_COPY3", catchCopy3);
		magazineData.put("SETUBI_ITIRAN_UMU", setubiItiranUmu);
		magazineData.put("MADORI_SETUMEI_UMU", madoriSetumeiUmu);
		magazineData.put("URA_HYOUSHI_UMU", uraHyoushiUmu);
		// 表紙タイプ、物件情報リストを取得する。
		Map<String, Object> bikouList = (Map<String, Object>)ukg05011Services.getbikouInfo();
		// キャッチコピーリストを取得する。
		Map<String, Object> chcopylist = (Map<String, Object>)ukg05011Services.getchcopyInfo();
		//表紙デザイン
		List<Map<String, Object>> ListbikouData = null;
		//賃貸物件情報
		List<Map<String, Object>> ListkoumokusbcdData = null;
		//キャッチコピー
		List<Map<String, Object>> ListchtchcopyData = null;
		if(bikouList != null){
			ListbikouData = (List<Map<String, Object>>)bikouList.get("res_bikouList");
			ListkoumokusbcdData = (List<Map<String, Object>>)bikouList.get("res_koumokusbcdList");
		}
		if(chcopylist != null){
			ListchtchcopyData = (List<Map<String, Object>>)chcopylist.get("res_chtchcopy_list");
		}
		if( null == bikouList || 0 == bikouList.size()){
			ukg05011Data.put("NODATA_MESSAGE", Util.getServerMsg("MSGS003", "オリジナル情報誌の登録"));
		}
		if(null == chcopylist || 0 == chcopylist.size()){
			ukg05011Data.put("NODATA_MESSAGE", Util.getServerMsg("MSGS003", "オリジナル情報誌の登録"));
		}
		ukg05011Data.put("ListbikouData", ListbikouData);
		ukg05011Data.put("ListkoumokusbcdData", ListkoumokusbcdData);
		ukg05011Data.put("ListchtchcopyData", ListchtchcopyData);
		ukg05011Data.put("processType", strProcessType);
		ukg05011Data.put("pageflag", strpageflag);
		ukg05011Data.put("magazineData", magazineData);
		return successForward();
	}
}
