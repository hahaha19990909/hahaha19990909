/** 
 *@システム名: 受付システム 
 *@ファイル名: IUKG02040Dao.java 
 *@更新日付：: 2011/12/26 
 *@Copyright: 2011 token corporation All right reserved  
 *更新履歴:    2019/07/26 熊振(SYS)  (案件C43089)来店日時の入力チェック対応
 *更新履歴：         2020/06/24 劉恆毅(SYS)   C43040-2_法人の入力項目追加対応
 */
package jp.co.token.uketuke.dao;

import java.util.Date;
import java.util.Map;

/**<pre>
 * [機 能] 商談履歴
 * [説 明] 商談履歴を検索・登録する。
 * @author [作 成] 2011/11/03 SYS_肖
 * </pre>
 */
public interface IUKG02040Dao {

	/** <pre>
	 * [説 明] 商談履歴初期データ取得
	 * @param kaisya_cd          会社コード
	 * @param uketuke_no         受付番号
	 *  @param sortitem          ソート順
	 * @return 商談履歴初期データ
	 * </pre>
	 */
	public Map<String, Object> getInitInfo(String kaisyaCd,
										   String uketukeNo,
										   String sortitem) throws Exception;


	
	/** <pre>
	 * [説 明] 商談中の顧客一覧データを取得する。
	 * @param kaisyaCd 会社コード
	 * @param uketukeno 物件コード
	 * @param searchItem 会社コード
	 * @param startIndex StartIndex
	 * @param endIndex EndIndex
	 * @param sortItem ソート項目
	 * @return 商談中の顧客一覧データ
	 * </pre>
	 */
	public Map<String, Object> getBukenInfo(String kaisyaCd, 
											  String uketukeno, 
											  String searchItem, 
											  Integer startIndex, 
											  Integer endIndex, 
											  String sortItem) throws Exception;
	
	
	/** <pre>
	 * [説 明] 商談データを更新する。
	 * @param kaisyaCd 会社コード
	 * @param tantousya 担当者
	 * @param uketukeNo 受付番号
	 * @param shoudanKb 商談区分
	 * @param shoudanDate 商談日付
	 * @param shoudanCmnt 商談内容
	 * @param seq seq
	 * @param upDate 更新日付
	 * @param raitenUpDate 来店更新日付 
	 * @return 予約データを削除する。
	 * </pre>
	 */
	public Map<String, Object> updateshoudanData(String kaisyaCd, 
											  String tantousya, 
											  String uketukeNo, 
											  String shoudanKb, 
											  String shoudanDate, 
											  String shoudanCmnt,
											  String seq,
											  String upFlg,
											  String upDate) throws Exception;	
	
	/** <pre>
	 * [説 明] 紹介物件を更新する。
	 * @param kaisyaCd 会社コード
	 * @param tantousya 担当者
	 * @param uketukeNo 受付番号
	 * @param bukenNo 物件番号
	 * @param heyaNo 部屋番号
	 * @param okiniiri お気に入り
	 * @return 予約データを削除する。
	 * </pre>
	 */
	public Map<String, Object> updateShoukaiBuken(String kaisyaCd, 
												  String tantousya, 
												  String uketukeNo, 
												  String bukenNo, 
												  String heyaNo,
												  String okiniiri
												  ) throws Exception;
	
	/** <pre>
	 * [説 明] メモを更新する。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param bukenNo 物件番号
	 * @param heyaNo 部屋番号
	 * @param upDate  更新日付 
	 * @param tantousya 担当者
	 * @param shoudanMemo メモ
	 * @return メモを更新する。
	 * </pre>
	 */
	public Map<String, Object> updMemoInfo(String kaisyaCd, 
												  String uketukeNo, 
												  String bukenNo, 
												  String heyaNo,
												  String upDate,
												  String tantousya,
												  String shoudanMemo
												  ) throws Exception;
	
	/** <pre>
	 * [説 明] お客様情報を取得する
	 * @param kaisya_cd          会社コード
	 * @param uketuke_no         受付番号
	 * @return お客様情報を取得する
	 * </pre>
	 */
	public Map<String, Object> getKokyakuInfo(String kaisyaCd,
										      String uketukeNo) throws Exception;
	
	/** <pre>
	 * [説 明] 進捗を更新する。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param tantousya 担当者
	 * @param sinchoukuCd 進捗コード
	 * @param upDate  更新日付 
	 * @return 進捗を更新する。
	 * </pre>
	 */
	public Map<String, Object> updSinchoukuInfo(String kaisyaCd, 
												  String uketukeNo, 
												  String tantousya,
												  String sinchoukuCd,
												  String upDate
                                                  //2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
                                                  ,String shoudancmnt
                                                  //2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
												  ) throws Exception;
	
	/** <pre>
	 * [説 明] アラーム解除。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param tantousya 担当者
	 * @param upDate  更新日付 
	 * @return アラーム解除戻り値。
	 * </pre>
	 */
	public Map<String, Object> updAlarmInfo(String kaisyaCd, 
												  String uketukeNo, 
												  String tantousya,
												  String upDate
												  ) throws Exception;
	
	/** <pre>
	 * [説 明] メモを更新する。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param upDate  更新日付 
	 * @param tantousya 担当者
	 * @param shoudanMemo メモ
	 * @return メモを更新する。
	 * </pre>
	 */
	public Map<String, Object> updRaitenMemo(String kaisyaCd, 
												  String uketukeNo, 
												  String upDate,
												  String tantousya,
												  String shoudanMemo
												  ) throws Exception;
	
	/** <pre>
	 * [説 明] 商談状況取得処理。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param postCd 店舗コード
	 * @param tantouCd 担当者コード
	 * @return 商談状況取得処理。
	 * </pre>
	 */
	public Map<String, Object> getsyoudanInfo(String kaisyaCd, 
												  String uketukeNo,
												  String postCd,
												  String tantouCd
												  ) throws Exception;
	
	/** <pre>
	 * [説 明] 仮押え物件欄確認処理。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param bukkenNo 物件番号
	 * @param heyaNo 部屋番号
	 * @param tantousya 担当者
	 * @param upDate  更新日付 
	 * @return 仮押え物件欄確認処理戻り値。
	 * </pre>
	 */
	public Map<String, Object> updkariosae(String kaisyaCd, 
												  String uketukeNo, 
												  String bukenNo,
												  String heyaNo,
												  String tantousya,
												  String upDate
												  ) throws Exception;
	
	/** <pre>
	 * [説 明] 現地案内実施処理。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param tantousya 担当者
	 * @param yoyakuNo  予約番号
	 * @param gentiannaiDate  現地案内日付
	 * @param upDate  更新日付 
	 * @return 現地案内実施処理戻り値。
	 * </pre>
	 */
	public Map<String, Object> updgentiannai(String kaisyaCd, 
												  String uketukeNo, 
												  String tantousya,
												  String gentiannaiDate,
												  String bukkeNo,
												  String heyaNo,
												  String seq,
												  String upDate
												  )  throws Exception;
	
	/** <pre>
	 * [説 明] 商談履歴取得。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @return 商談履歴情報。
	 * </pre>
	 */
	public Map<String, Object> getshoudanRireki(String kaisyaCd, 
												  String uketukeNo
												  ) throws Exception;
	/** <pre>
	 * [説 明] 希望条件検索ワークに該当受付№の新着物件情報を追加
	 * @param sessionId セッションID
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param nowDate 現在日時
	 * @return 追加処理戻り値。
	 * </pre>
	 */
	public Map<String, Object> addSinchaku(String sessionId, 
											String kaisyaCd,
										    String uketukeNo,
											Date nowDate) throws Exception;
	
	/** <pre>
	 * [説 明]希望条件検索ワークに該当受付№のチラシ印刷物件追加処理
	 * @param sessionId セッションID
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param nowDate 現在日時
	 * @return 追加処理戻り値。
	 * </pre>
	 */
	public Map<String, Object> addTirashi(String sessionId, 
											String kaisyaCd,
										    String uketukeNo,
											Date nowDate) throws Exception;
	
	/* 2019/07/26 熊振 C43089_来店日時の入力チェック対応 STRAT */
	/** <pre>
	 * [説 明] メモを更新する。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param raitenDateAno  来店日時 
	 * @return 来店日時更新する。
	 * </pre>
	 */
	public Map<String, Object> upDateRaitenDate(String kaisyaCd,
													String uketukeNo,
													String raitenDateAno) throws Exception;
	
	/* 2019/07/26 熊振 C43089_来店日時の入力チェック対応 END */
}