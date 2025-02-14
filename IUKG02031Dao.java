/** 
 *@システム名: 希望地域の設定(市区郡選択)
 *@ファイル名: IUKG02031Dao.java 
 *@更新日付：: 2012/05/08 
 *@Copyright: 2012 token corporation All right reserved  
 */
package jp.co.token.uketuke.dao;

import java.util.List;
import java.util.Map;

/**<pre>
 * [機 能] 希望地域の設定(市区郡選択)
 * [説 明] 希望地域の設定(市区郡選択)する。
 * @author [作 成] 2012/05/08 SYS_郭
 * </pre>
 */
public interface IUKG02031Dao {

	/** <pre>
	 * [説 明] 都道府県リストを取得する。
	 * @return 都道府県リストのデータ
	 * </pre>
	 */
	public List<Map<String, Object>> getJusyoData(String jouhouCd) throws Exception;
	
}
