/** 
 *@システム名: 受付システム 
 *@ファイル名: IUKG02041Dao.java 
 *@更新日付：: 2011/03/16
 *@Copyright: 2011 token corporation All right reserved 
 * 更新履歴：2020/06/24 劉恆毅(SYS)    1.01         C43040-2_法人の入力項目追加対応
 */
package jp.co.token.uketuke.dao;

import java.util.List;
import java.util.Map;

/**<pre>
 * [機 能] 追客アラーム情報
 * [説 明] 追客アラーム情報を検索し、表示する。
 * @author [作 成] 2012/03/16 SYS_趙
 * </pre>
 */
public interface IUKG01013Dao {

	/**<pre>
	 * [説 明] 追客アラーム情報を取得
	 * @param kaisyaCd 会社コード
	 * @param chukaiTantou 仲介担当者コード
	 * @param postCd 受付店舗コード
	 * @param senni 遷移元フラグ
	 * @param order ソート
	 * @return 追客アラーム情報
	 * </pre>
	 */
	public List<Map<String, Object>> getAlarmInfo(String kaisyaCd,
			String chukaiTantou, String postCd, String senni, String order) throws Exception;
	
	/**<pre>
	 * [説 明] 中止、他決ボタン押下時処理
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付no.
	 * @param tantou 担当者コード
	 * @param updateDate 更新日付
	 * @param type 処理種類
	 * @return 処理結果
	 * </pre>
	 */
	public Map<String, Object> chuushiTaketuProcess(String kaisyaCd, String uketukeNo,
    //2020/06/24 劉恆毅(SYS) UPD Start C43040-2_法人の入力項目追加対応
            //String tantou, String updateDate, String type) throws Exception;
            String tantou, String updateDate, String type, String shoudancmnt) throws Exception;
    //2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
}
