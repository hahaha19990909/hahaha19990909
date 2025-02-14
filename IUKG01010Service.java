/**
 * @システム名: 受付システム
 * @ファイル名: IUKG01010Service.java
 * @更新日付：: 2011/12/26
 * @Copyright: 2011 token corporation All right reserved
 * 更新履歴：2013/01/25	林森(SYS)		1.01		(案件番号C37058)受付システム要望対応
 * 更新履歴：2013/05/16 郭凡(SYS)		1.02		(案件C37109-1)受付システムSTEP2要望対応
 * 更新履歴：2014/05/13 胡(SYS)			1.03		(案件C38100)トップページで既存個人を検索（他）対応
 * 更新履歴：2014/06/23 趙雲剛(SYS)  	1.04 		(案件C38117)セールスポイント編集機能の追加
 * 更新履歴：2017/08/04 MJEC)長倉		1.05		(案件C38101-1)ホームメイト自動受付対応 (案件C41059)中止他決顧客の検索対応
 * 更新履歴：2017/09/12 張虎強(SYS)		1.05		(案件C41004)法人仲介担当者登録機能追加対応
 * 更新履歴：2020/06/24 劉恆毅(SYS)    1.06         C43040-2_法人の入力項目追加対応
 */
package jp.co.token.uketuke.service;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * [機 能] トップページ
 * [説 明] トップページサービスインターフェース。。
 * @author [作 成] 2011/10/18 馮強華(SYS)
 * </pre>
 */
public interface IUKG01010Service {

	/**
	 * <pre>
	 * [説 明] 画面初期データ取得。
	 * @param kaisyaCd 会社コード
	 * @param postCd 店舗コード
	 * @param searchPostCd 検索店舗コード
	 * @param authorityCd ユーザー権限コード
	 * @param sisyaBlkCd 支社ブロックコード
	 * @param tantouCd 担当者コード
	 * @param yoyakuDate 予約日
	 * @param uketukegetuDate 受付月
	 * @param searchItem 検索条件
	 * @param kanaIndex1 kanaIndex1
	 * @param kanaIndex2 kanaIndex2
	 * @param startIndex StartIndex
	 * @param endIndex EndIndex
	 * @param taketuCyushiFlg 他決・中止チェックボックスフラグ
	 * @param sortItem ソート項目
	 * @return 画面初期データ
	 * </pre>
	 */
	public Map<String, Object> getInitInfo(String kaisyaCd,
	                                       String postCd,
	                                       // ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
	                                       String searchPostCd,
	                                       String userAuthorityCd,
	                                       String sisyaBlkCd,
	                                       // ########## 2014/06/23 趙雲剛 ADD End
	                                       String tantouCd,
	                                       String yoyakuDate,
	                                       String uketukegetuDate,
	                                       // ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
	                                       String searchItem,
	                                       String kanaIndex1,
	                                       String kanaIdex2,
	                                       // ########## 2013/01/25 林森 ADD End
	                                       Integer startIndex,
	                                       Integer endIndex,
	                                       // ########## 2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
	                                       String taketuCyushiFlg,
	                                       // ########## 2017/08/04 MJEC)長倉 ADD End
	                                       String sortItem) throws Exception;

	// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
	/**
	 * <pre>
	 * [説 明] 店舗一覧取得。
	 * @param kaisyaCd 会社コード
	 * @param authorityCd ユーザー権限コード
	 * @param sisyaBlkCd 支社ブロックコード
	 * @return 店舗一覧
	 * </pre>
	 */
	public Map<String, Object> getPostList(String kaisyaCd,
	                                       String authorityCd,
	                                       String sisyaBlkCd) throws Exception;
	
	/**
	 * <pre>
	 * [説 明] 店舗情報取得。
	 * @param kaisyaCd 会社コード
	 * @param postCd 店舗コード
	 * @return 店舗情報
	 * </pre>
	 */
	public Map<String, Object> getPostInfo(String kaisyaCd,
	                                       String postCd) throws Exception;
	// ########## 2014/06/23 趙雲剛 ADD End
	
	/**
	 * <pre>
	 * [説 明] 受付番号を取得する。
	 * @param kaisyaCd 会社コード
	 * @param userCd ログインユーザコード
	 * @param postCd 店舗コード
	 * @param kokyakuCd 顧客コード
	 * @param uketukehoho 仲介担当コード
	 * @param uketukehoho 受付方法
	 * @param kokyakuSb 顧客種別
	 * @return 受付番号
	 * </pre>
	 */
	public Map<String, Object> getUketukeNo(String kaisyaCd,
	                                        String userCd,
	                                        String postCd,
	                                        String kokyakuCd,
	                                        String chukaitantouCd,
	                                        String uketukehoho,
	                                        String kokyakuSb) throws Exception;

	/**
	 * <pre>
	 * [説 明] 予約データリストを取得する。
	 * @param kaisyaCd 会社コード
	 * @param postCd 店舗コード
	 * @param authorityCd ユーザー権限コード
	 * @param sisyaBlkCd 支社ブロックコード
	 * @param tDate 日付
	 * @return 予約データ
	 * </pre>
	 */
	public Map<String, Object> getYoyakuInfo(String kaisyaCd,
	                                         String postCd,
	                                         // ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
	                                         String userAuthorityCd,
	                                         String sisyaBlkCd,
	                                         // ########## 2014/06/23 趙雲剛 ADD End
	                                         String tDate) throws Exception;

	/**
	 * <pre>
	 * [説 明] 受付状況データを取得する。
	 * @param kaisyaCd 会社コード
	 * @param authorityCd ユーザー権限コード
	 * @param sisyaBlkCd 支社ブロックコード
	 * @param postCd 店舗コード
	 * @param tantouCd 担当者コード
	 * @param tDate 日付
	 * @return 受付客数
	 * </pre>
	 */
	public Map<String, Object> getUketukeInfo(String kaisyaCd,
											  // ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
											  String authorityCd,
											  String sisyaBlkCd,
											  // ########## 2014/06/23 趙雲剛 ADD End
	                                          String postCd,
	                                          String tantouCd,
	                                          String tDate) throws Exception;

	/**
	 * <pre>
	 * [説 明] 未登録データデータを取得する。
	 * @param kaisyaCd 会社コード
	 * @param authorityCd ユーザー権限コード
	 * @param sisyaBlkCd 支社ブロックコード
	 * @param postCd 店舗コード
	 * @return 未登録データ
	 * </pre>
	 */
	public Map<String, Object> getMitorokuInfo(String kaisyaCd,
												// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
									            String userAuthorityCd,
									            String sisyaBlkCd,
									            // ########## 2014/06/23 趙雲剛 ADD End
	                                           String postCd) throws Exception;

	//########## 2017/08/04 MJEC)長倉 ADD Start (案件C38101-1)ホームメイト自動受付対応
	/**
	 * <pre>
	 * [説 明] ホームメイト自動受付データを取得する。
	 * @param kaisyaCd 会社コード
	 * @param authorityCd ユーザー権限コード
	 * @param sisyaBlkCd 支社ブロックコード
	 * @param postCd 店舗コード
	 * @return ホームメイト自動受付データ
	 * </pre>
	 */
	public Map<String, Object> getHomemateInfo(String kaisyaCd,
									            String userAuthorityCd,
									            String sisyaBlkCd,
	                                           String postCd) throws Exception;
	//########## 2017/08/04 MJEC)長倉 ADD End

	/**
	 * <pre>
	 * [説 明] 既存法人一覧データを取得する。
	 * @param kaisyaCd 会社コード
	 * @param postCd 店舗コード
	 * @param kanaIndex1 kanaIndex1
	 * @param kanaIdex2 kanaIndex2
	 * @return 既存法人一覧データ
	 * </pre>
	 */
	public Map<String, Object> getKisonHojinInfo(String kaisyaCd,
	                                             String postCd,
	                                             String kanaIndex1,
	                                             String kanaIdex2) throws Exception;

	/**
	 * <pre>
	 * [説 明] 商談中の顧客一覧データを取得する。
	 * @param kaisyaCd 会社コード
	 * @param postCd 店舗コード
	 * @param authorityCd ユーザー権限コード
	 * @param sisyaBlkCd 支社ブロックコード
	 * @param searchItem 会社コード
	 * @param wheretantou 担当者条件
	 * @param kanaIndex1 kanaIndex1
	 * @param kanaIndex2 kanaIndex2
	 * @param startIndex StartIndex
	 * @param endIndex EndIndex
	 * @param taketuCyushiFlg 他決・中止チェックボックスフラグ
	 * @param sortItem ソート項目
	 * @return 商談中の顧客一覧データ
	 * </pre>
	 */
	public Map<String, Object> getKokyakuInfo(String kaisyaCd,
	                                          String postCd,
	                                          // ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
		                                       String userAuthorityCd,
		                                       String sisyaBlkCd,
		                                       // ########## 2014/06/23 趙雲剛 ADD End
	                                          String searchItem,
	                                          // ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
	                                          String wheretantou,
	                                          // ########## 2013/01/25 林森 ADD End
	                                          String kanaIndex1,
	                                          String kanaIdex2,
	                                          Integer startIndex,
	                                          Integer endIndex,
	                                          // ########## 2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
	                                          String taketuCyushiFlg,
	                                          // ########## 2017/08/04 MJEC)長倉 ADD End
	                                          String sortItem) throws Exception;

	/**
	 * <pre>
	 * [説 明] 予約データ背景色を設定する。
	 * @param yoyakuMapList 予約データ
	 * @param nowTime 現在時刻
	 * @return なし
	 * </pre>
	 */
	public void changeyoyakuList(List<Map<String, Object>> yoyakuMapList,
	                             String nowTime) throws Exception;

	/**
	 * <pre>
	 * [説 明] お知らせ情報更新
	 * @param updInfo 更新データ
	 * @return なし
	 * </pre>
	 */
	public Map<String, Object> updOshiraseInfo(String updInfo) throws Exception;

	// ########## 2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
	/**
	 * 担当者受付組数取得
	 * @param kaisyaCd ログインユーザ会社コード
	 * @param postCd ログイン情報.店舗コード
	 * @param tantouCdList ログイン情報.店舗コード.担当者リス
	 * @return 担当者の当月の受付組数,担当者の保有組数
	 */
	public Map<String, Object> getUketukeKumiSuu(String kaisyaCd,String postCd,
			String tantouCds)throws Exception;
	// ########## 2013/01/25 林森 ADD End
	
	// ########## 2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
	/**
	 * 店舗受付組数取得
	 * @param kaisyaCd ログインユーザ会社コード
	 * @param postCd ログイン情報.店舗コード
	 * @param authorityCd ユーザー権限コード
	 * @param sisyaBlkCd 支社ブロックコード
	 * @return 店舗の当月の受付組数,担当者の保有組数
	 */
	public Map<String, Object> getPostUketukeKumiSuu(String kaisyaCd, String postCd, String authorityCd, String sisyaBlkCd) throws Exception;
	// ########## 2014/06/23 趙雲剛 ADD End
	
	// ########## 2013/05/16 郭凡 ADD Start (案件C37109-1)受付システムSTEP2要望対応
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
            String tantou, String updateDate, String type,String shoudancmnt) throws Exception;
	//2020/06/24 劉恆毅(SYS) UPD End C43040-2_法人の入力項目追加対応
	// ########## 2013/05/16 郭凡 ADD End
	// ########## 2014/05/13 胡 ADD Start (案件C38100)トップページで既存個人を検索（他）対応
	/** <pre>
	 * [説 明] 顧客情報取得
	 * @param kaisya_cd      会社コード
	 * @param kokyaku_sb     顧客種別
	 * @param kokyaku_name   顧客名
	 * @param kokyaku_name   顧客名(含む)
	 * @param kokyaku_name_k 顧客名カナ
	 * @param kokyaku_name_k 顧客名カナ(含む)
	 * @param birth_day      生年月日
	 * @param tel_no         電話番号
	 * @param keitai_no      携帯番号
	 * @param sortitem       ソート項目
	 * @return 顧客情報データ
	 * </pre>
	 */
	public Map<String, Object> getKokyakuJyouho(String kaisya_cd,
												String kokyaku_sb,
												String kokyaku_name,
												String kokyaku_name_chk,
												String kokyaku_name_k,
												String kokyaku_name_k_chk,
												String birth_day,
												String tel_no,
												String keitai_no,
												String kanaindex1,
												String kanaindex2,
												Integer startindex,
												Integer endindex,
												String sortitem) throws Exception;
	// ########## 2014/05/13 胡 ADD End
	
	// 2017/09/12  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
	/** <pre>
	 * [説 明] 顧客情報取得
	 * @param kigyouCode      企業コード
	 * @param kigyouName      企業名
	 * @return 顧客情報データ
	 * </pre>
	 */
	public Map<String, Object> gethoujinSelectInfo(String kigyouCode, String kigyouName) throws Exception;
	

	/**
	 * <pre>
	 * [説 明] 法人仲介依頼件数を取得する。
	 * @param kaisyaCd 会社コード
	 * @param authorityCd ユーザー権限コード
	 * @param sisyaBlkCd 支社ブロックコード
	 * @param sysDate 日付
	 * @return 法人仲介依頼件数
	 * </pre>
	 */
	public Map<String, Object> getHoujinIraiInfo(String kaisyaCd,
			String userAuthorityCd, 
			String sisyaBlkCd, 
			String sysDate)
			throws Exception;
	
	/**
	 * <pre>
	 * [説 明] 法人仲介依頼状況データを取得する。
	 * @param kaisyaCd 会社コード
	 * @param authorityCd ユーザー権限コード
	 * @param sisyaBlkCd 支社ブロックコード
	 * @param sysDate 日付
	 * @return 法人仲介依頼状況データ
	 * </pre>
	 */
	public Map<String, Object> getHoujinIraiData(String kaisyaCd,
			String userAuthorityCd, 
			String sisyaBlkCd, 
			String sysDate)
			throws Exception;

	/**
	 * <pre>
	 * [説 明] 検索用の法人仲介受付一覧データを取得する。
	 * @param kaisyaCd 会社コード
	 * @param showNum 表示件数
	 * @param sisyaBlkCd 支店ブロックコード
	 * @param authorityCd ユーザー権限コード
	 * @param postCd 店舗コード
	 * @return 法人仲介受付一覧データ
	 * </pre>
	 */
	public Map<String, Object> getHoujinUketukeData(String kaisyaCd,
			int showNum, 
			String sisyaBlkCd,
			String authorityCd,
			String postCd) throws Exception;
	
	/**
	 * <pre>
	 * [説 明] 会社情報取得する。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付No
	 * @return 会社情報データ
	 * </pre>
	 */
	public Map<String, Object> getKaisyaData(String kaisyacd, String uketukeNo) throws Exception;
	
	// 2017/09/12  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
}
