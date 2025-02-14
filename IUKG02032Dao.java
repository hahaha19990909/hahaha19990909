/** 
 *@システム名: 希望地域の設定(エリア選択)
 *@ファイル名: IUKG02032Dao.java 
 *@更新日付：: 2012/05/29 
 *@Copyright: 2012 token corporation All right reserved  
 * 更新履歴: 2014/01/07  SYS_ 郭凡    (案件C37075)オリジナル情報誌リプレース
 */
package jp.co.token.uketuke.dao;

import java.util.HashMap;
import java.util.Map;

import jp.co.token.uketuke.formbean.JusyohaniBean;

/**<pre>
 * [機 能] 希望地域の設定(エリア選択)
 * [説 明] 希望地域の設定(エリア選択)する。
 * @author [作 成] 2012/05/29 SYS_郭
 * </pre>
 */
public interface IUKG02032Dao {
	
	/**
	 * <pre>
	 * [説 明]空室物件情報検索。
	 * @param paramMap  Mapオッボジェックト
	 * @return paramMap
	 * </pre>
	 */
	public Map<String, Object> getkuusitubukkenCount(HashMap<String, Object> paramMap) throws Exception;

	// 2014/01/07  郭凡 ADD Start (案件番号C37075)オリジナル情報誌リプレース
	/**
	 * <pre>
	 * [説 明]新着空室物件情報検索。
	 * @param paramMap  Mapオッボジェックト
	 * @return paramMap
	 * </pre>
	 */
	public Map<String, Object> getshicyakubukkenCount(String kaisyaCd, JusyohaniBean jusyohani) throws Exception;
	// 2014/01/07  郭凡 ADD End
}
