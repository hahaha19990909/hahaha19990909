/**
 *@システム名: 受付システム
 *@ファイル名: IUKG00010Service.java
 *@更新日付：: 2011/12/26
 *@Copyright: 2011 token corporation All right reserved
 */
package jp.co.token.uketuke.service;

import java.util.Map;

/**<pre>
 * [機 能] ログイン。
 * [説 明] ログインサービスインターフェース。
 * @author [作 成] 2011/10/18 戴氷馨(SYS)
 * 変更履歴: 2014/06/23 趙雲剛(SYS)  1.01 	(案件C38117)セールスポイント編集機能の追加
 * </pre>
 */
public interface IUKG00010Service{
	/** <pre>
	 * [説 明] ユーザIDを認証する、SASLとLADPを利用する
	 * @param userId ユーザＩＤ
	 * @param password ユーザパスワード
	 * @return　メッセージID
	 * </pre>
	 */
	public String checkUserId(String userId,String password) throws Exception;

	/** <pre>
	 * [説 明] ログイン者の基本情報リスト取得する。
	 * @param kaisyacd 会社コード
	 * @param userid ユーザID
	 * @return ユーザ基本情報
	 * </pre>
	 */
	public Map<String, Object> getUserJyouhouList(String kaisyacd, String userid) throws Exception;

	/** <pre>
	 * [説 明] ログイン者の基本情報リスト取得する。(東建以外のユーザー)
	 * @param kaisyacd 会社コード
	 * @param userid ユーザID
	 * @param password パスワード
	 * @return ユーザ基本情報
	 * </pre>
	 */
	public Map<String, Object> getUserJyouhouList2(String kaisyacd, String userid, String password) throws Exception;

	/**
	 * <pre>
	 * [説 明] パラメータで渡した画面IDをチェックする。
	 * @param pageId 画面ID
	 * @return true/false
	 * </pre>
	 */
	public boolean checkPageId(String pageId) throws Exception;

	/**
	 * <pre>
	 * [説 明] 受付№判定情報リスト取得
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付№
	 * @return 受付№判定情報
	 * </pre>
	 */
	public Map<String, Object> getUketukeList(String kaisyaCd, String uketukeNo)  throws Exception;

	// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
	/**
	 * <pre>
	 * [説 明] 本社担当者の店舗コードをチェックする。
	 * @param postCd 店舗コード
	 * @return true/false
	 * </pre>
	 */
	public boolean checkPostTantou(String postCd) throws Exception;
	// ########## 2014/06/23 趙雲剛 ADD End
}
