/**
 * @システム名: 受付システム
 * @ファイル名: UKG05010Action.java
 * @更新日付： 2013/12/26
 * @Copyright: 2013 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.struts2.ServletActionContext;
import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.service.IUKG05012Service;

/**
 * <pre>
 * [機 能] オリジナル情報誌トップ
 * [説 明] オリジナル情報誌トップ
 * @author [作 成] 2013/12/26 謝超(SYS) (案件C37075)オリジナル情報誌リプレース
 * </pre>
 */
public class UKG05010Action extends BaseAction{
	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -4052506919220609316L;

	/** 画面用データ */
	private Map<String, Object> ukg05013Data = new HashMap<String, Object>();
	
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
	 * 画面用データ
	 * 
	 * @return ukg05013Data　画面データ
	 */
	public Map<String, Object> getUkg05013Data() {
		return ukg05013Data;
	}

	/**
	 * 画面用データ
	 * 
	 * @param ukg05013Data
	 *            画面データ
	 */
	public void setUkg05013Data(Map<String, Object> ukg05013Data) {
		this.ukg05013Data = ukg05013Data;
	}
	
	public String execute() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		//UKG05020画面戻るボタン押下パラメータ取得
		String flag = request.getParameter("flag");
		String saveFlag = request.getParameter("saveFlag");
		session.setAttribute("flag", flag);
		session.setAttribute("saveFlag", saveFlag);
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String)mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String)mapLoginInfo.get(Consts.POST_CD);
		String strPageNum = String.valueOf(Consts.ONE);
		String strSort = Consts.UKG05012_SORT_JYUN;
		// バックナンバー日数を取得
		String strBacknumberNitisuu = PropertiesReader.getIntance().getProperty(Consts.UKG05012_BACKNUMBER_NITISUU);
		// 情報誌データを取得
		HashMap<String, Object> mapJyouhousiData = (HashMap<String, Object>)ukg05012Services
				.getJyouhousiList(strKaisyaCd, strPostCd, strBacknumberNitisuu, strSort, strPageNum);
		String strJyouhousiCount = (String)mapJyouhousiData.get("rst_recordcount");
		ukg05013Data.put("strJyouhousiCount", strJyouhousiCount);
		
		return successForward();
	}
}
