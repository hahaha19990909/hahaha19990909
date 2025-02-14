/**
 *@システム名: 受付システム
 *@ファイル名: IUKG01016Dao.java
 *@更新日付：: 2017/07/31
 *@Copyright: 2011 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**<pre>
 * [機 能] ホームメイト自動受付一覧
 * [説 明] ホームメイト自動受付一覧データアクセスインタフェース
 * @author [作 成] 2017/07/31 MJEC)鈴村
 * </pre>
 */
public interface IUKG01016Dao {

	/** <pre>
	 * [説 明] ホームメイト自動受付一覧データを取得する。
	 * @param kaisyaCd 会社コード
	 * @param authorityCd 店舗コード
	 * @param sisyaBlkCd 会社コード
	 * @param postCd 店舗コード
	 * @param sortItem ソート項目
	 * @return 未登録データ
	 * </pre>
	 */
	public Map<String, Object> getHomemateInfo(String kaisyaCd,
	                                           String userAuthorityCd,
	                                           String sisyaBlkCd,
	                                           String postCd,
	                                           String sortItem) throws Exception;
}
