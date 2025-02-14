/** 
 *@システム名: 受付システム 
 *@ファイル名: IUKG02020Dao.java 
 *@更新日付：: 2011/12/26 
 *@Copyright: 2011 token corporation All right reserved  
 */
package jp.co.token.uketuke.dao;


import java.util.List;
import java.util.Map;

/**<pre>
 * [機 能] アンケート情報取得
 * [説 明] アンケート情報取得設定
 * @author [作 成] 2011/10/21 A.Wanibe(SYS)
 * </pre>
 */
public interface IUKG02020Dao {
	/** <pre>
	 * [説 明] アンケートデータを取得する
	 * @param uketukeNo 受付No.
	 * @param kaisyaCD 会社コード
	 * @return　Map アンケート情報のMap
	 * </pre>
	 */
	public Map<String, Object> getAnketoData(String uketukeNo,String kaisyaCD) throws Exception;
	/** <pre>
	 * [説 明] お客様情報取得
	 * @param uketuke_no 受付№
	 * @param kaisya_cd 会社コード
	 * @return Map お客様情報のMap
	 * </pre>
	 */
	public Map<String, Object> getKokyakuData(String uketukeNo,String kaisyaCD) throws Exception;
	/** <pre>
	 * [説 明] 排他チェック用更新日時取得
	 * @param uketuke_no 受付№
	 * @param kaisya_cd 会社コード
	 * @param id アンケートID
	 * @return 顧客データ
	 * </pre>
	 */
	public Map<String, Object> haitaCheck(String uketukeNo,String kaisyaCD,String id) throws Exception;
	/** <pre>
	 * [説 明]アンケートデータ作成トランザクション
	 * @param　data インサート用アンケートデータ
	 * @param　anketo アンケート登録フラグ
	 * @param kokyaku 顧客登録フラグ
	 * @param kibou 希望条件登録フラグ
	 * @return　true 成功　false 失敗
	 * </pre>
	 */
	public boolean tranAnketoData(List<Map<String,Object>> data,String anketo,String kokyaku,String kibou) throws Exception;
}
