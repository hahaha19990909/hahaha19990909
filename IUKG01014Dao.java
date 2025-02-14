/**
 * @システム名: 受付システム
 * @ファイル名: IUKG01015Dao.java
 * @更新日付： 2012/03/12
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.Date;
import java.util.Map;

/**
 * <pre>
 * [機 能] 仮押え状況一覧。
 * [説 明] 仮押え状況一覧データをアクセスするインターフェース。
 * @author [作 成] 2012/03/12 戴氷馨(SYS)
 * </pre>
 */
public interface IUKG01014Dao {

	/**
	 * <pre>
	 * [説 明] 仮押え状況データを取得する。
	 * @param tantouCd 担当者コード
	 * @param kaisyaCd 会社コード
	 * @param postCd 店舗コード
	 * @param senikenFlg 遷移元フラグ
	 * @param sort ソート
	 * @return 仮押え状況データ
	 * </pre>
	 */
	public Map<String, Object> getSintyakuInfo(String tantouCd,
	                                           String kaisyaCd,
	                                           String postCd,
	                                           String senikenFlg,
	                                           String sort) throws Exception;

	/**<pre>
	 * [説 明] 解除行の更新日付を取得する
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付no.
	 * @param bukkenNo 物件no.
	 * @param heyaNo 部屋no.
	 * @param type 処理種類
	 * @return 処理結果
	 * </pre>
	 */
	public Map<String, Object> kaijyoProcess(String kaisyaCd,
	                                         String uketukeNo,
	                                         String bukkenNo, 
	                                         String heyaNo,
	                                         String updDate) throws Exception;
	
	/**<pre>
	 * [説 明] 希望条件検索ワークに該当受付№の新着物件情報を追加
	 * @param sessionId セッションID
	 * @param kaisyaCd 会社コード
	 * @param uketukeCd 受付№
	 * @param nowdate 現在日時
	 * @return 処理結果
	 * </pre>
	 */
	public Map<String, Object> addSinchaku(String sessionId, String kaisyaCd, String uketukeCd,Date nowdate) throws Exception;
	
}
