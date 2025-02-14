/**
 * @システム名: 受付システム
 * @ファイル名: IUKG05032Dao.java
 * @更新日付：: 2014/01/08
 * @Copyright: 2013 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;
import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.service.IUKG05032Service;
/**
 * <pre>
 * [機 能] レイアウト設定
 * [説 明] レイアウト設定
 * @author [作 成] 2014/1/3 SYS_劉琦 (案件C37075)オリジナル情報誌リプレース
 * </pre>
 */
public class UKG05032Action extends BaseAction {

	/**クラスのシリアル化ID */
	private static final long serialVersionUID = -5629458236532919212L;
    /** レイアウト設定画面用データ*/
	private Map<String,Object> ukg05032Date = new HashMap<String,Object>();
	/** レイアウト設定サービスを取得する*/
	private IUKG05032Service ukg05032Services;

	/**
	 * <pre>
	 * [説 明] レイアウト設定サービス対象を設定する。
	 * @param ukg05032Servicesサービス対象
	 * </pre>
	 */
	public void setUkg05032Services(IUKG05032Service ukg05032Services) {
		this.ukg05032Services = ukg05032Services;
	}

	/**
	 * レイアウト設定画面用データ
	 * @return ukg05032Date画面データ
	 */
	public Map<String, Object> getUkg05032Date() {
		return ukg05032Date;
	}
	/**
	 * レイアウト設定画面用データを設定する
	 * @param ukg05010Data
	 */
	public void setUkg05032Date(Map<String, Object> ukg05032Date) {
		this.ukg05032Date = ukg05032Date;
	}

	/**
	 * <pre>
	 * [説 明] 初期データを取得する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String execute() throws Exception{

		HttpServletRequest request = ServletActionContext.getRequest();
		//パラメータ取得
		String strlayoutId = request.getParameter("layoutId");
		//初期化データ取得
		Map<String,Object> koumokuInfo = ukg05032Services.getKoumokuInfo();
		//画面の値を設定
	    ukg05032Date.put("koumokuList",koumokuInfo.get("out_koumoku"));
	    ukg05032Date.put("layoutId", strlayoutId);
	    return successForward();
     }

}
