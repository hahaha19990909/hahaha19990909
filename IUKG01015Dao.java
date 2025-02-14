/**
 * @システム名: 受付システム
 * @ファイル名: IUKG01015Dao.java
 * @更新日付： 2012/03/12
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**
 * <pre>
 * [機 能] 仮押え状況一覧。
 * [説 明] 仮押え状況一覧データをアクセスするインターフェース。
 * @author [作 成] 2012/03/12 戴氷馨(SYS)
 * </pre>
 */
public interface IUKG01015Dao {

	/**
	 * <pre>
	 * [説 明] 仮押え状況データを取得する。
	 * @param tantouCd 担当者コード
	 * @param kaisyaCd 会社コード
	 * @param postCd 店舗コード
	 * @param uketukeNo 受付№
	 * @param senikenFlg 遷移元フラグ
	 * @param sort ソート
	 * @return 仮押え状況データ
	 * </pre>
	 */
	public Map<String, Object> getKariosaeInfo(String tantouCd,
	                                           String kaisyaCd,
	                                           String postCd,
	                                           String uketukeNo,
	                                           String senikenFlg,
	                                           String sort) throws Exception;

	/**
	 * <pre>
	 * [説 明] 申込、キャンセルボタン押下時処理
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付no.
	 * @param tantou 担当者コード
	 * @param updateDate 更新日付
	 * @param type 処理種類
	 * @return 処理結果
	 * </pre>
	 */
	public Map<String, Object> mousicomiCancelProcess(String kaisyaCd, 
													  String uketukeNo,
													  String kokyakuCd,
													  String kokyakuKb,
													  String tantouCd, 
													  String postCd, 
													  String bukkenNo, 
													  String heyaNo, 
													  String type) throws Exception;
}
