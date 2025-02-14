/** 
 *@システム名: 物件情報（一覧表示）。
 *@ファイル名: IUKG03011Dao.java 
 *@更新日付：: 2012/04/13 
 *@Copyright: 2011 token corporation All right reserved  
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**<pre>
 * [機 能] 物件情報（一覧表示）
 * [説 明] 物件情報（一覧表示）。
 * @author [作 成] 2012/05/17 SYS_馮強華
 * 変更履歴: 2013/05/22	SYS_趙雲剛	(案件C37109)受付システムSTEP2要望対応
 * 更新履歴: 2019/07/03 SYS_郝年昇  (案件 C43100)お客様項目の入力簡素化対応
 * </pre>
 */
public interface IUKG03011Dao {
	/** <pre>
	 * [説 明] 物件データを取得する。
	 * @param kaisyaCd 会社コード
	 * @param uketukeno 物件コード
	 * @param sessionId セッションId
	 * @param searchItem 検索項目
	 * @param kibousetubinohissucd 必須ではない設備コード
	 * @param tokuTenKb 特典区分
	 * @param startIndex StartIndex
	 * @param endIndex EndIndex
	 * @param sortItem ソート項目
	 * @param pageId 画面ID
	 * @return 物件データを取得する。
	 * </pre>
	 */
	public Map<String, Object> getBukenInfo(String kaisyaCd, 
											  String uketukeno, 
											  String sessionId,	
											  String searchItem, 
											// ########## 2013/05/22 SYS_趙雲剛 ADD Start (案件C37109-1) 受付システムSTEP2要望対応
											  String kibousetubinohissucd, 
											// ########## 2013/05/22 SYS_趙雲剛 ADD End
	                                          // 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
                                              String tokuTenKb,
                                              // 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
											  Integer startIndex, 
											  Integer endIndex, 
											  String sortItem,
											  String pageId) throws Exception;
	/** <pre>
	 * [説 明] 紹介物件を非表示に更新する。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param bukenNo 物件番号
	 * @param heyaNo 部屋番号
	 * @param nodispFlg  非表示フラグ
	 * @param tantousya 担当者
	 * @return 紹介物件を非表示に更新する。
	 * </pre>
	 */
	public Map<String, Object> updShoukaiBukenDisp(String kaisyaCd, 
												  String uketukeNo, 
												  String bukenNo, 
												  String heyaNo,
												  String nodispFlg,
												  String tantousya
												  ) throws Exception;
	
	// ########## 2013/05/22 SYS_趙雲剛 ADD Start (案件C37109-1) 受付システムSTEP2要望対応
	/** <pre>
	 * [説 明] 間取りより紹介物件を非表示に更新する。
	 * @param sessionId セッションID
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param bukenNo 物件番号
	 * @param madori 間取り
	 * @param nodispFlg  非表示フラグ
	 * @param tantousya 担当者
	 * @return 紹介物件を非表示に更新する。
	 * </pre>
	 */
	public Map<String, Object> updShoukaiBukenDispMadori( String sessionId, 
												  String kaisyaCd, 
												  String uketukeNo, 
												  String bukenNo, 
												  String madori,
												  String nodispFlg,
												  String tantousya
												  ) throws Exception;
	// ########## 2013/05/22 SYS_趙雲剛 ADD End
	
	/** <pre>
	 * [説 明] 紹介物件を登録する
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param bukenNo 物件番号
	 * @param heyaNo 部屋番号
	 * @param tantousya 担当者
	 * @return 処理コード。
	 * </pre>
	 */
	public Map<String, Object> addbukenInfo(String kaisyaCd, 
												  String uketukeNo, 
												  String bukenNo, 
												  String heyaNo,
												  String tantousya
												  ) throws Exception;
	
}
