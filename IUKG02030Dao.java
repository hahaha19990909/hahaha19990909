/**
 * @システム名: 受付システム
 * @ファイル名: IUKG02030Dao.java
 * @更新日付：: 2011/12/26
 * @Copyright: 2011 token corporation All right reserved
 * 更新履歴: 2013/05/17  SYS_ 郭凡    (案件C37109-1)受付システムSTEP2要望対応
 *           2014/01/06  SYS_ 謝超    (案件C37075)オリジナル情報誌リプレース
 *          2017/09/25  SYS_ヨウ強 (案件番号C41004)法人仲介担当者登録機能追加対応
 *          2019/06/28  SYS_郝年昇 (案件番号C43100)お客様項目の入力簡素化対応
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

import jp.co.token.uketuke.formbean.KibouJyoukennBean;

/**
 * <pre>
 * [機 能] 希望条件
 * [説 明] お客様の希望条件を入力・登録する。
 * @author [作 成] 2011/11/02 SYS_賈
 * </pre>
 */
public interface IUKG02030Dao {

	/**
	 * <pre>
	 * [説 明] 共通データ取得
	 * @return 共通データ
	 * </pre>
	 */
	public Map<String, Object> getCommonData() throws Exception;

	/**
	 * <pre>
	 * [説 明] 問い合わせ№により物件情報取得
	 * @param kaisya_cd          会社コード
	 * @param toiawaseNo         問合せ№
	 * @return 物件情報
	 * </pre>
	 */
	public Map<String, Object> getBukkenInfo(String kaisya_cd,
	                                         String toiawaseNo) throws Exception;

	/**
	 * <pre>
	 * [説 明] 問い合わせ№により部屋情報取得
	 * @param bukkenNo           物件№
	 * @param toiawaseNo         問合せ№
	 * @return 部屋情報
	 * </pre>
	 */
	public Map<String, Object> getHeyaInfo(String bukkenNo,
	                                       String toiawaseNo) throws Exception;

	/**
	 * <pre>
	 * [説 明] 問い合わせ№によりその他部屋情報取得
	 * @param bukkenNo           物件№
	 * @param toiawaseNo         問合せ№
	 * @return 部屋情報
	 * </pre>
	 */
	public Map<String, Object> getSonotaHeyaInfo(String bukkenNo,
	                                             String toiawaseNo) throws Exception;

	/**
	 * <pre>
	 * [説 明] 希望条件初期化情報の取得
	 * @param kaisya_cd          会社コード
	 * @param uketuke_no         受付№
	 * @return 希望条件初期データ
	 * </pre>
	 */
	public Map<String, Object> getInitInfo(String kaisya_cd,
	                                       String uketuke_no) throws Exception;

	// 2014/01/06 謝超 ADD Start (案件C37075)オリジナル情報誌リプレース
	/**
	 * <pre>
	 * [説 明] 物件検索の共通
	 * @param kibouJyoukenBean 希望条件データ
	 * @param sortJyoken ソート条件
	 * @param startRow 開始行
	 * @param endRow 終了行
	 * @return 物件情報リスト
	 * </pre>
	 */
	public Map<String, Object> getBukkenList(KibouJyoukennBean kiboujyoukenBean,
			String sortJyoken, int startRow, int endRow) throws Exception;

	// 2014/01/06 謝超 ADD End

	/**
	 * <pre>
	 * [説 明] 希望条件データ取得
	 * @param kaisya_cd          会社コード
	 * @param uketuke_no         受付№
	 * @param kenCd              都道府県コード
	 * @param shiCd              市区郡コード
	 * @return 希望条件データ
	 * </pre>
	 */
	public Map<String, Object> getKibouJyouken(String kaisya_cd,
	                                           String uketuke_no,
	                                           String kenCd,
	                                           String shiCd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 希望設備データ取得
	 * @param kaisya_cd          会社コード
	 * @param uketuke_no         受付№
	 * @return 希望設備データ
	 * </pre>
	 */
	public Map<String, Object> getKibouSetubi(String kaisya_cd,
	                                          String uketuke_no) throws Exception;

	/**
	 * <pre>
	 * [説 明] お客様情報取得
	 * @param kaisya_cd          会社コード
	 * @param uketuke_no         受付№
	 * @return 希望設備データ
	 * </pre>
	 */
	public Map<String, Object> getCustomerData(String kaisya_cd,
	                                           String uketuke_no) throws Exception;

	/**
	 * <pre>
	 * [説 明] 市区郡一覧取得
	 * @param ken_cd          県コード
	 * @return 市データ
	 * </pre>
	 */
	public Map<String, Object> getJusyo2List(String ken_cd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 町・大字一覧取得
	 * @param ken_cd          県コード
	 * @param city_cd         市コード
	 * @return 町・大字一覧
	 * </pre>
	 */
	public Map<String, Object> getJusyo3List(String ken_cd,
	                                         String city_cd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 都道府県の路線一覧取得
	 * @param ken_cd          県コード
	 * @param rosen_kb        路線区分
	 * @return 路線一覧
	 * </pre>
	 */
	public Map<String, Object> getRosenList(String ken_cd,
	                                        String rosen_kb) throws Exception;

	/**
	 * <pre>
	 * [説 明] 都道府県県コードにより、路線区分リスト取得
	 * @param ken_cd          県コード
	 * @return 路線一覧
	 * </pre>
	 */
	public Map<String, Object> getRosenKbnList(String ken_cd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 路線の駅一覧取得
	 * @param rosen_cd        路線コード
	 * @return 駅一覧
	 * </pre>
	 */
	public Map<String, Object> getEkiList(String rosen_cd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 路線コードにより、路線区分取得
	 * @param rosen_cd        路線コード
	 * @return 路線区分
	 * </pre>
	 */
	public Map<String, Object> getRosenKbn(String rosen_cd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 更新日付取得
	 * @param kaisya_cd          会社コード
	 * @param uketuke_no         受付№
	 * @return 更新日付
	 * </pre>
	 */
	public Map<String, Object> getUpdDate(String kaisya_cd,
	                                      String uketuke_no) throws Exception;

	/**
	 * <pre>
	 * [説 明] 家賃リストデータ取得
	 * @return 共通データ
	 * </pre>
	 */
	public Map<String, Object> getYatinListData() throws Exception;

	/**
	 * <pre>
	 * [説 明] 認証用物件№リスト取得
	 * </pre>
	 */
	public Map<String, Object> chkGetBukkenList(String bukenNo1,
	                                            String bukenNo2,
	                                            String bukenNo3,
	                                            String bukenNo4) throws Exception;

	/**
	 * <pre>
	 * [説 明] 物件種別区分より画面リスト取得
	 * </pre>
	 */
	public Map<String, Object> getBukkenSbKbnList() throws Exception;

	/**
	 * <pre>
	 * [説 明] 物件数の取得すること
	 * </pre>
	 */
	public Map<String, Object> getBukkenCount(KibouJyoukennBean kiboujyoukenBean) throws Exception;

	/**
	 * <pre>
	 * [説 明] 希望条件保存処理
	 * </pre>
	 */
	public Map<String, Object> save(KibouJyoukennBean kiboujyoukenBean) throws Exception;

	/**
	 * <pre>
	 * [説 明] こだわり情報の取得
	 * </pre>
	 */
	public Map<String, Object> getKodawariInfo(KibouJyoukennBean kiboujyoukenBean) throws Exception;

	/**
	 * <pre>
	 * [説 明] こだわり情報の取得
	 * @param sessionId　セッションID
	 * @param kiboujyoukenBean 希望条件データ
	 * @param batchDate バッチ用データ
	 * </pre>
	 */
	public Map<String, Object> getKuusituBukkenInfo(String sessionId,
	                                                KibouJyoukennBean kiboujyoukenBean,
	                                                String batchDate) throws Exception;

	/**
	 * <pre>
	 * [説 明] ワークデータの登録
	 * @param 登録データMAP
	 * @return 共通データ
	 * </pre>
	 */
	public Map<String, Object> wkDataUpd(Map<String, Object> paramMap) throws Exception;

    // 2019/06/20 UPD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応

	 // 2013/1/23  楊きょう(SYS)  ADD START 案件C37058
	/** <pre>
	 * [説 明] メモを更新する。
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param upDate  更新日付
	 * @param tantousya 担当者
	 * @param memoNaiyou メモ 内容
	 * @param kokyakuName お客様名
     * @param kokyakuNameKana お客様名カナ
     * @param telNo 電話番号
     * @param emailNo メールアドレス
     * @param flg 更新区分フラグ
     * @param sinpoflg 前進捗状況区分フラグ
	 * @return 更新の結果。
	 * </pre>
	 */
	//public Map<String, Object> updMemoInfo(String kaisyaCd,
	//										   String uketukeNo,
	//										   String upDate,
	//										   String tantousya,
	//										   String memoNaiyou) throws Exception;
	public Map<String, Object> updMemoInfo(String kaisyaCd,
            String uketukeNo,
            String upDate,
            String tantousya,
            String memoNaiyou,
            String kokyakuName,
            String kokyakuNameKana,
            String telNo,
            String emailNo,
            String flg,
            String sinpoflg) throws Exception;
	// 2013/1/23  楊きょう(SYS)  ADD END 案件C37058
    // 2019/06/20 UPD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応

	//2013/05/17    郭凡(SYS)  ADD START 案件C37109-1
	/**
	 * <pre>
	 * [説 明] 物件名リストを取得する。
	 * @param kaisyacd 会社コード
	 * @param postCd 店舗コード
	 * @param bukkenName 物件名
	 * @return 物件名リスト
	 * </pre>
	 */
	public Map<String, Object> searchBukkenNameList(String kaisyacd,
													String postCd,
													String bukkenName) throws Exception;

	//2013/05/17    郭凡(SYS)  ADD END 案件C37109-1

	// 2017/09/25 SYS_ヨウ強  ADD START C41004_法人仲介担当者登録機能追加対応
	/**
	 * <pre>
	 * [説 明] アップロード日時の取得
	 * @param kaisya_cd          会社コード
	 * @param houjin_uketuke_no  法人仲介依頼元受付NO.
	 * @return アップロード日時
	 * </pre>
	 */
	public Map<String, Object> getUploadDate(String kaisya_cd,
	                                           String houjin_uketuke_no) throws Exception;

	/**
	 * <pre>
	 * [説 明] 法人希望部屋数の取得
	 * @param kaisya_cd 会社コード
	 * @param uketuke_no 受付NO.
	 * @return 法人希望部屋数
	 * </pre>
	 */
	public Map<String, Object> getKibouHeyaSuu(String kaisya_cd,
	                                           String uketuke_no) throws Exception;

	/**
	 * <pre>
	 * [説 明] 店舗情報を取得
	 * @param kaisya_cd 会社コード
	 * @param uketuke_no 受付NO.
	 * @return 店舗情報
	 * </pre>
	 */
	public Map<String, Object> getTenpoInfo(String kaisya_cd,
	                                           String uketuke_no) throws Exception;
	// 2017/09/25 SYS_ヨウ強  ADD END C41004_法人仲介担当者登録機能追加対応
}
