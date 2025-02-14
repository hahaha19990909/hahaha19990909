/**
 * @システム名: 受付システム
 * @ファイル名: UKG01013Action.java
 * @更新日付：: 2012/02/28
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.Map;

import jp.co.token.fw.base.BaseAction;

/**
 * <pre>
 * [機 能] 物件案内資料選択
 * [説 明] 物件案内資料選択を検索し、表示する。
 * @author [作 成]
 * 変更履歴: 2014/01/16 SYS_胡 Jtest対応
 * </pre>
 */
public class UKG99004_2Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;

	/** 物件案内資料選択画面用データ */
	private Map<String, Object> ukg09904Data = new HashMap<String, Object>();

	/**
	 * 物件案内資料選択画面用データ
	 * 
	 * @return ukg01013Data
	 */
	public Map<String, Object> getUkg01013Data() {

		return ukg09904Data;
	}

	/**
	 * 物件案内資料選択画面用データ
	 * 
	 * @param ukg01013Data
	 */
	public void setUkg01013Data(Map<String, Object> ukg01013Data) {

		// 2014/01/16 Start by SYS_胡  Jtest対応
		//this.ukg09904Data = ukg01013Data;
		ukg09904Data = ukg01013Data;
		// 2014/01/16 End by SYS_胡  Jtest対応
	}

	/**
	 * <pre>
	 * [説 明] 物件案内資料選択。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);

		return successForward();
	}
}
