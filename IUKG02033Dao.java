/**
 *@システム名: 市区町村リスト
 *@ファイル名: IUKG02033Dao.java
 *@更新日付：: 2012/05/29
 *@Copyright: 2012 token corporation All right reserved
 * 更新履歴: 2014/01/21  SYS_ 謝超    (案件C37075)オリジナル情報誌リプレース
 */
package jp.co.token.uketuke.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**<pre>
 * [機 能] 市区町村リスト
 * [説 明] 市区町村リストする。
 * @author [作 成] 2012/05/29 SYS_郭
 * </pre>
 */
public interface IUKG02033Dao {

	/** <pre>
	 * [説 明] 都道府県リストを取得する。
	 * @return 都道府県リストのデータ
	 * </pre>
	 */
	public List<Map<String, Object>> getJusyoData() throws Exception;

	/**
	 * <pre>
	 * [説 明]市町区リストを取得する。
	 * @param p_tdfk_code  都道府県CODE
	 * @return 市町区リストのデータ
	 * </pre>
	 */
	public Map<String, Object> getSmkData(HashMap<String, Object> paramMap) throws Exception;

	// 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
	/**
	 * <pre>
	 * [説 明]市町区リストを取得する。
	 * @param paramMap  パラメータ
	 * @return 市町区リストのデータ
	 * </pre>
	 */
	public Map<String, Object> getSmkDataSintyaku(HashMap<String, Object> paramMap) throws Exception;

	/**
	 * <pre>
	 * [説 明]町リストを取得する。
	 * @param paramMap  パラメータ
	 * @return 市町区リストのデータ
	 * </pre>
	 */
	public Map<String, Object> getSmkMatiData(HashMap<String, Object> paramMap) throws Exception;

	/**
	 * <pre>
	 * [説 明]町リストを取得する。
	 * @param paramMap  パラメータ
	 * @return 市町区リストのデータ
	 * </pre>
	 */
	public Map<String, Object> getSmkMatiDataSintyaku(HashMap<String, Object> paramMap) throws Exception;
	// 2014/01/21  謝超 ADD End

}
