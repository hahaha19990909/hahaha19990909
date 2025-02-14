/**
 *@システム名: 受付システム
 *@ファイル名: IUKG00010Dao.java
 *@更新日付：: 2011/12/26
 *@Copyright: 2011 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**<pre>
 * [機 能] ログイン。
 * [説 明] ログインデータをアクセスするインターフェース。
 * @author [作 成] 2011/10/18 戴氷馨(SYS)
 * </pre>
 */
public interface IUKG00010Dao {

	/**
	 * <pre>
	 * [説 明] ログイン者の基本情報リスト取得する。
	 * @param kaisyacd 会社コード
	 * @param userid ユーザID
	 * @return ユーザ基本情報
	 * </pre>
	 */
	public Map<String, Object> getUserJyouhouList(String kaisyacd, String userid)  throws Exception;

	/**
	 * <pre>
	 * [説 明] ログイン者の基本情報リスト取得する。(東建以外のユーザー)
	 * @param kaisyacd 会社コード
	 * @param userid ユーザID
	 * @param password パスワード
	 * @return ユーザ基本情報
	 * </pre>
	 */
	public Map<String, Object> getUserJyouhouList2(String kaisyacd, String userid, String password)  throws Exception;

	/**
	 * <pre>
	 * [説 明] 受付№判定情報リスト取得
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付№
	 * @return 受付№判定情報
	 * </pre>
	 */
	public Map<String, Object> getUketukeList(String kaisyaCd, String uketukeNo)  throws Exception;
}
