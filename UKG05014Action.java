/**
 * @システム名: 受付システム
 * @ファイル名: UKG05014Action.java
 * @更新日付： 2014/1/24
 * @Copyright: 2013 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;

/**
 * <pre>
 * [機 能] ページ並び替え(掲載状況確認)
 * [説 明] ページ並び替え(掲載状況確認)アクション。
 * @author [作 成] 2014/1/24 郭凡(SYS) (案件C37075)オリジナル情報誌リプレース
 * </pre>
 */
public class UKG05014Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -5629458236532919212L;

	/** ページ並び替え(掲載状況確認)画面用データ */
	private Map<String, Object> ukg05014Data = new HashMap<String, Object>();


	/**
	 * ページ並び替え(掲載状況確認)画面用データ
	 *
	 * @return ukg05014Data　画面データ
	 */
	public Map<String, Object> getUkg05014Data() {
		return ukg05014Data;
	}

	/**
	 * ページ並び替え(掲載状況確認)画面用データ
	 *
	 * @param ukg05014Data
	 *            画面データ
	 */
	public void setUkg05014Data(Map<String, Object> ukg05014Data) {
		this.ukg05014Data = ukg05014Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面を初期化。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);

		// リクエストを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		String strProcessflg = request.getParameter("processflg");
		String strPageid = request.getParameter("pageid");

		// 画面の値を設定
		ukg05014Data.put("processflg", strProcessflg);
		ukg05014Data.put("pageid", strPageid);
		// 最大掲載可能ページ数
		ukg05014Data.put("maxkeisaipagesuu", PropertiesReader.getIntance().getProperty(Consts.UKG05020_MAX_KEISAI_PAGESUU));

		return successForward();
	}

}