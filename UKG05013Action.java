/**
 * @システム名: 受付システム
 * @ファイル名: UKG05013Action.java
 * @更新日付： 2013/12/30
 * @Copyright: 2013 token corporation All right reserved
 * 更新履歴：2018/02/09      孫博易(SYS)       1.01         (案件番号C42036)デザイン変更
 * 更新履歴：2020/04/14   　　程旋(SYS)  1.02    C44030_お部屋探しNaviマルチブラウザ対応
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG05013Service;

/**
 * <pre>
 * [機 能] バックナンバー操作
 * [説 明] バックナンバー操作アクション。
 * @author [作 成] 2013/12/30 謝超(SYS) (案件C37075)オリジナル情報誌リプレース
 * </pre>
 */
public class UKG05013Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -5629458236532919212L;

	/** バックナンバー操作画面用データ */
	private Map<String, Object> ukg05013Data = new HashMap<String, Object>();

	/** バックナンバー操作サービスを取得する */
	private IUKG05013Service ukg05013Services = null;

	/**
	 * <pre>
	 * [説 明] バックナンバー操作サービス対象を設定する。
	 * @param ukg05013Service サービス対象
	 * </pre>
	 */
	public void setUKG05013Services(IUKG05013Service ukg05013Services) {
		this.ukg05013Services = ukg05013Services;
	}

	public Map<String, Object> getUkg05013Data() {
		return ukg05013Data;
	}
	public void setUkg05013Data(Map<String, Object> ukg05013Data) {
		this.ukg05013Data = ukg05013Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面を初期化。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String execute() throws Exception {
		// 同期フラグ設定
		setAsyncFlg(true);
		// リクエストを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		//発行日を取得
		String strHakkoubi = request.getParameter("hakkoubi");
		//発行連番を取得
		String strSeq = request.getParameter("seq");
		// 2018/02/09 SYS_孫博易 ADD START   (案件番号C42036)デザイン変更
		String sinntyoku = request.getParameter("sinntyoku");
		// 2018/02/09 SYS_孫博易 ADD END   (案件番号C42036)デザイン変更
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String)mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String)mapLoginInfo.get(Consts.POST_CD);
		//情報誌データ取得
	    Map<String, Object> initdate = ukg05013Services.getJouhou(strKaisyaCd, strPostCd, strHakkoubi, strSeq);

		List<HashMap<String,Object>> list = (List<HashMap<String,Object>>)initdate.get("out_magazinelist");
	    String bikou = "";
	    String hyoushiTypeFlag = "";
	    String status = "";
	    String tantouName = "";
	    String hakou = "";
	    String jouhouName = "";
	    String upddate = "";
	    String magazineSakuseiIraiDate = "";
	    String magazinePath = "";
	    // 2018/02/09 SYS_孫博易 ADD START   (案件番号C42036)デザイン変更
	    //if(null != list){
	    if(null != list && list.size() > 0){
	    // 2018/02/09 SYS_孫博易 ADD END   (案件番号C42036)デザイン変更
	    	//共通マスタ備考取得
	    	bikou = (String)list.get(0).get("BIKOU");
	    	//共通マスタ削除フラグ取得
	    	hyoushiTypeFlag = (String)list.get(0).get("LOGICAL_DEL_FLG");
	    	//ステータス取得
	    	status = (String)list.get(0).get("STATUS");
	    	//作成者取得
	    	tantouName = (String)list.get(0).get("TANTOU_NAME");
	    	//更新日(画面表示)取得
	        hakou = (String)list.get(0).get("MAGAZINE_UPD_DATE");
	        //情報誌名取得
	        jouhouName = (String)list.get(0).get("HYOUSHI_NAME");
	        //更新日取得
	        upddate = (String)list.get(0).get("UPD_DATE");
	        //情報誌作成依頼日
	        magazineSakuseiIraiDate = (String)list.get(0).get("MAGAZINE_SAKUSEI_IRAI_DATE");
	        //情報誌パス
	        magazinePath = (String)list.get(0).get("MAGAZINE_PATH");
	    }
	     // 画面の値を設定
	    ukg05013Data.put("bikou", bikou);
	    ukg05013Data.put("hyoushiTypeFlag",hyoushiTypeFlag);
	    ukg05013Data.put("status", status);
	    ukg05013Data.put("tantouName", tantouName);
	    ukg05013Data.put("hakou", hakou);
		ukg05013Data.put("jouhouName", jouhouName);
		ukg05013Data.put("upddate", upddate);
		ukg05013Data.put("strHakkoubi", strHakkoubi);
		ukg05013Data.put("seq", strSeq);
		// 2018/02/09 SYS_孫博易 ADD START   (案件番号C42036)デザイン変更
		ukg05013Data.put("sinntyoku", sinntyoku);
		// 2018/02/09 SYS_孫博易 ADD END   (案件番号C42036)デザイン変更
		ukg05013Data.put("magazineSakuseiIraiDate", magazineSakuseiIraiDate);
		ukg05013Data.put("magazinePath", magazinePath);

		String printActionId = PropertiesReader.getIntance().getProperty("magazineURL");
		ukg05013Data.put("printActionId", printActionId);
		String tirashiTitleName = PropertiesReader.getIntance().getProperty(Consts.MAGAZINE_PDF_WINDOWSHOWINGNAME);
		ukg05013Data.put("magazineTitleName", tirashiTitleName);
		String tirashiDownloadName = PropertiesReader.getIntance().getProperty(Consts.MAGAZINE_PDF_DOWNLOADNAME);
		ukg05013Data.put("magazineDownloadName", tirashiDownloadName);
		/*2020/04/14 程旋 ADD START C44030_お部屋探しNaviマルチブラウザ対応*/
		String chouhyouServer = PropertiesReader.getIntance().getProperty(Consts.CHOUHYOUSERVER);
		ukg05013Data.put("chouhyouServer", chouhyouServer);
		/*2020/04/14 程旋 ADD END C44030_お部屋探しNaviマルチブラウザ対応*/
		ukg05013Data.put("kaisyaCd", strKaisyaCd);
		ukg05013Data.put("postCd", strPostCd);
		ukg05013Data.put("userCd", mapLoginInfo.get(Consts.LOGIN_USER_CD));
		ukg05013Data.put("errorMessage", Util.getServerMsg("ERRS002", "オリジナル情報誌の作成"));

		return successForward();
	}
	/**
	 * <pre>
	 * [説 明] 排他チェック。
	 *  @return アクション処理結果
	 *  @throws Exception　処理異常
	 * </pre>
	 */
	public String check() throws Exception{
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String)mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String)mapLoginInfo.get(Consts.POST_CD);
		// リクエストを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		String strSeq = request.getParameter("seq");
		String strHakkou = request.getParameter("hakkoubi");
		//情報誌データ取得
		Map<String, Object> updDate = ukg05013Services.updDateCheck(strKaisyaCd, strPostCd, strHakkou, strSeq);
		@SuppressWarnings("unchecked")
		List<HashMap<String,Object>> list = (List<HashMap<String,Object>>) updDate.get("out_upddate");
		String upd_date = "";
		if(list.size() == 0){
			upd_date = null;
		}else{
			if(null != upd_date){
			  //最新更新日付を取得
				upd_date = (String)list.get(0).get("UPD_DATE");
			}
		}
		//メッセージ内容を設定
		ukg05013Data.put("errMsg", Util.getServerMsg("ERRS009"));
		ukg05013Data.put("upddate", upd_date);
		return "ajaxProcess";
	}
	/**
	 * <pre>
	 * [説 明] 情報誌を削除。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String deleteJouhoushiData() throws Exception {
		// リクエストを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		String strSeq = request.getParameter("seq");
		String strHakkoubi = request.getParameter("hakkoubi");
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String)mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String)mapLoginInfo.get(Consts.POST_CD);
		String strTantouCd = (String)mapLoginInfo.get(Consts.USER_CD);
        //情報誌を削除処理
		ukg05013Services.deleteJouhoushiData(strKaisyaCd, strPostCd, strTantouCd, strHakkoubi, strSeq);
		return "ajaxProcess";
	}
	/**
	 * <pre>
	 * [説 明] 情報誌データ印刷する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String printJouhouDate() throws Exception{
	// セッションを取得
	HttpServletRequest request = ServletActionContext.getRequest();
	// ユーザ情報を取得
	Map<String, Object> mapLoginInfo = getLoginInfo();
	String strKaisyaCd = (String) mapLoginInfo.get(Consts.KAISYA_CD);
	String strPostCd = (String) mapLoginInfo.get(Consts.POST_CD);
	String strUser = (String) mapLoginInfo.get(Consts.USER_CD);
	// 状態
	String status = request.getParameter("status");
	// 情報誌パス
	String pdfPath = request.getParameter("pdfPath");
	// 情報誌発行日
	String hakkouDate = request.getParameter("hakkouDate");
	// 発行連番
	String seq = request.getParameter("seq");

	// 物件データを取得する
	Map<String, Object> result = ukg05013Services.printmagazineData(strKaisyaCd, strPostCd, hakkouDate, seq, status, strUser, pdfPath);
	// 掲載物件データを設定する
	ukg05013Data = new HashMap<String, Object>();
	ukg05013Data.put("result", result.get("rst_code"));
	ukg05013Data.put("upd_date", result.get("rst_update_date"));

	return "ajaxProcess";
	}
   /**
    * <pre>
    * [説 明] 情報誌データ編集する。
    * @return アクション処理結果
    * @throws Exception　処理異常
    * </pre>
    */

	public String hensyuCheck() throws Exception{
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String)mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String)mapLoginInfo.get(Consts.POST_CD);
		// リクエストを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		String strSeq = request.getParameter("seq");
		String strHakkou = request.getParameter("hakkoubi");
	    //情報誌データ取得
		Map<String,Object> returndate = ukg05013Services.hensyuCheck(strKaisyaCd, strPostCd, strHakkou, strSeq );
	    @SuppressWarnings("unchecked")
		//情報誌件数リスト
	    List<Map<String, Object>> jouInfo = (List<Map<String, Object>>) returndate.get("out_count");
	    String count = "";
	    if(null != returndate){
	    	//情報誌件数を取得
	    	count = (String) jouInfo.get(0).get("MAGAZINE_COUNT");
	    }
        ukg05013Data.put("count", count);
        //メッセージ内容を設定
	    ukg05013Data.put("errMsg", Util.getServerMsg("ERRS013","該当情報誌データ"));
	    return "ajaxProcess";
	}
}