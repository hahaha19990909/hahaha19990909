/** 
 *@システム名: 受付システム 
 *@ファイル名: IUKG01011Dao.java 
 *@更新日付：: 2011/12/26 
 *@Copyright: 2011 token corporation All right reserved  
 *更新履歴：2014/06/23 趙雲剛(SYS)    1.01 	(案件C38117)セールスポイント編集機能の追加
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**<pre>
 * [機 能] 受付未登録一覧。
 * [説 明] 受付未登録一覧データをアクセスするインターフェース。
 * @author [作 成] 2011/10/21 戴氷馨(SYS)
 * </pre>
 */
public interface IUKG01011Dao {
	
	/** <pre>
	 * [説 明] 未登録データを取得する。
	 * @param kaisyaCd 会社コード
	 * @param authorityCd 店舗コード
	 * @param sisyaBlkCd 会社コード
	 * @param postCd 店舗コード
	 * @param sortItem ソート項目
	 * @return 未登録データ
	 * </pre>
	 */
	public Map<String, Object> getMitorokuInfo(String kaisyaCd, 
												// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
									            String userAuthorityCd,
									            String sisyaBlkCd,
									            // ########## 2014/06/23 趙雲剛 ADD End
											   String postCd, 
											   String sortItem) throws Exception;
}
