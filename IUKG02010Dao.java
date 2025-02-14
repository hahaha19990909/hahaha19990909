/**
 *@システム名: 受付システム
 *@ファイル名: IUKG02010Dao.java
 *@更新日付：: 2011/12/26
 *@Copyright: 2011 token corporation All right reserved
 * 更新履歴： 2017/09/22	ヨウ強    1.01    (案件C41004)法人仲介担当者登録機能追加対応
 * 更新履歴：  2020/04/07   楊朋朋(SYS)    1.02 C43017_見積書改修      
 * 更新履歴：  2020/05/06   楊朋朋(SYS)    1.03 C43040-2_法人の入力項目追加対応 
 */
package jp.co.token.uketuke.dao;

import java.util.List;
import java.util.Map;

/**<pre>
 * [機 能] お客様情報
 * [説 明] お客様情報を入力、登録する。
 * @author [作 成] 2011/10/21 SYS_賈
 * @author [更 新] 2018/01/15 肖剣生  (案件C42036)デザイン変更
 * </pre>
 */
public interface IUKG02010Dao {
	/** <pre>
	 * [説 明] アンケート項目名を取得する。
	 * @return アンケート項目
	 * </pre>
	 */
	public Map<String, Object> getAnketomData() throws Exception;

	/** <pre>
	 * [説 明] お客様情報取得
	 * @param kaisya_cd 会社コード
	 * @param uketuke_no 受付№
	 * @return お客様情報データ
	 * </pre>
	 */
	public Map<String, Object> getCustomerData(String kaisya_cd,
			String uketuke_no) throws Exception;

	/** <pre>
	 * [説 明] 一部アンケート情報を取得する
	 * @param kaisya_cd 会社コード
	 * @param uketuke_no 受付№
	 * @return 一部アンケート情報
	 * </pre>
	 */
	public Map<String, Object> getAnketoData(String kaisya_cd,
			String uketuke_no) throws Exception;

	/** <pre>
	 * [説 明] 都道府県コードより、市区郡リストを取得する。
	 * @param jusyo1 県コード
	 * @return 市区郡リスト
	 * </pre>
	 */
	public List<Map<String, Object>> getJusyo2Data(String jusyo1) throws Exception;

	/** <pre>
	 * [説 明] 進捗状況リストを取得する。
	 * @param sinchokuCd 進捗コード
	 * @return 進捗状況リストのデータ
	 * </pre>
	 */
	public Map<String, Object> getSinchokuData(String sinchokuCd) throws Exception;

	/** <pre>
	 * [説 明] 仲介担当者リスト、都道府県リスト、生年月日リストを取得する。
	 * @param kaisya_cd 会社コード
	 * @param post_cd 店舗コード
	 * @return 仲介担当者リスト、都道府県リスト、生年月日リストのデータ
	 * </pre>
	 */
	public Map<String, Object> getOtherListData(String kaisya_cd, String post_cd) throws Exception;

	/** <pre>
	 * [説 明] 顧客データ取得
	 * @param kaisya_cd 会社コード
	 * @param uketuke_no 受付№
	 * @return 顧客データ
	 * </pre>
	 */
	public Map<String, Object> getKokyakuData(String kaisya_cd, String kokyaku_no) throws Exception;

	/** <pre>
	 * [説 明] 受付データ更新
	 * @param uketukeData 受付データ
	 * @return 受付データ更新の結果
	 * </pre>
	 */
	public Map<String, Object> updUketukeData(Map<String, Object> uketukeData) throws Exception;

	/** <pre>
	 * [説 明] 町・大字データ取得
	 * @param jusyo1 県コード
	 * @param jusyo2 市コード
	 * @param town 町
	 * @return 町・大字データ
	 * </pre>
	 */
	public Map<String, Object> getJusyo3Data(String jusyo1, String jusyo2, String town) throws Exception;

	/** <pre>
	 * [説 明] 郵便番号の入力により住所を検索する
	 * @param yubinNo 郵便番号
	 * @return 住所データ
	 * </pre>
	 */
	public Map<String, Object> getJusyoInfo(String yubinNo) throws Exception;

	// 2017/09/22 ヨウ強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
	/**
	 * <pre>
	 * [説 明] お客様情報取得(受付№が空白且つセッション情報.受付№が空白の場合)
	 * @param kigyo_cd 企業コード
	 * @return お客様情報データ
	 * </pre>
	 */
	public Map<String, Object> getCustomerInfo(String kigyo_cd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 仲介依頼先店舗情報を取得（取得した受付区分 が4[法人仲介依頼]の場合）
	 * @param kaisya_cd 会社コード
	 * @param uketuke_no 受付№
	 * @return 仲介依頼先店舗情報
	 * </pre>
	 */
	public Map<String, Object> getTempoInfo(String kaisya_cd, String uketuke_no) throws Exception;

	/**
	 * <pre>
	 * [説 明] 店舗名リストデータを取得する
	 * @param kaisya_cd 会社コード
	 * @return 店舗名リストデータ
	 * </pre>
	 */
	public Map<String, Object> getTempoNa(String kaisya_cd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 仲介依頼先店舗代表メールアドレスを取得する
	 * @param kaisya_cd 会社コード
	 * @param postCd 店舗コード
	 * @return 仲介依頼先店舗代表メールアドレス
	 * </pre>
	 */
	public Map<String, Object> getTempoDaihyouMailAddress(String kaisya_cd, String postCd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 仲介依頼先店舗の各社員メールアドレスリストを取得する
	 * @param kaisya_cd 会社コード
	 * @param postCd 店舗コード
	 * @param iraiMailSyozokuCd 担当者所属コード
	 * @return 仲介依頼先店舗の各社員メールアドレスリスト
	 * </pre>
	 */
	public Map<String, Object> getShainMailAddress(String kaisya_cd, String postCd, String iraiMailSyozokuCd)
			throws Exception;

	/**
	 * <pre>
	 * [説 明] 社宅斡旋依頼書情報を取得。
	 * @param kaisya_cd 会社コード
	 * @param uketuke_no 受付№
	 * @return 社宅斡旋依頼書情報データ
	 * </pre>
	 */
	public Map<String, Object> getSyatakuInfo(String kaisya_cd, String uketuke_no) throws Exception;

	/**
	 * <pre>
	 * [説 明] 法人希望室数リストデータを取得する
	 * @return 法人希望室数リストデータ
	 * </pre>
	 */
	public Map<String, Object> getShitsuSuuList() throws Exception;

	/**
	 * <pre>
	 * [説 明] 法人仲介担当者リスト取得
	 * @param kaisya_cd 会社コード
	 * @return 法人仲介担当者リストデータ
	 * </pre>
	 */
	public Map<String, Object> getHoujinTantouList(String kaisya_cd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 店舗名データ取得
	 * @param kaisya_cd 会社コード
	 * @param postPara 店舗コード/店舗名
	 * @return 店舗名データ
	 * </pre>
	 */
	public Map<String, Object> getPostData(String kaisya_cd, String postPara) throws Exception;

	/**
	 * <pre>
	 * [説 明] 仲介依頼送信,データの更新処理
	 * @param uketukeData 受付データ
	 * @return 更新の結果
	 * </pre>
	 */
	public Map<String, Object> uketukeDataUpd(Map<String, Object> uketukeData) throws Exception;

	/**
	 * <pre>
	 * [説 明] 保存3ボタン押下処理の更新処理
	 * @param uketukeData 受付データ
	 * @return 更新の結果
	 * </pre>
	 */
	public Map<String, Object> updHozonData(Map<String, Object> uketukeData) throws Exception;

	/**
	 * <pre>
	 * [説 明] 受付№採番
	 * @param kaisya_cd 会社コード
	 * @param tantou_cd 担当者コード
	 * @return 受付№
	 * </pre>
	 */
	public Map<String, Object> getUketukeNo(String kaisya_cd, String tantou_cd) throws Exception;
	
	/**
	 * <pre>
	 * [説 明] 仲介依頼先店舗の受付№を取得する
	 * @param kaisya_cd 会社コード
	 * @param postCd 店舗コード
	 * @param moto_Uketuke_No 依頼元受付№
	 * @return 仲介依頼先店舗の受付№
	 * </pre>
	 */
	public Map<String, Object> getUketuke_No(String kaisya_cd, String postCd, String moto_Uketuke_No) throws Exception;
	// 2017/09/22 ヨウ強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
	
	// 2018/01/15 肖剣生 ADD Start (案件C42036)デザイン変更
	/** <pre>
	 * [説 明] アンケートデータ追加トランザクション
	 * @param data インサート用アンケートデータ
	 * @param anketo アンケート登録フラグ
	 * @param kokyaku 顧客情報登録フラグ
	 * @param kibou 希望条件登録フラグ
	 * @return true 成功　false 失敗
	 * </pre>
	 */
	public boolean tranAnketoData(List<Map<String, Object>> data,
			String anketo, String kokyaku, String kibou) throws Exception;
	// 2018/01/15 肖剣生 ADD End (案件C42036)デザイン変更
    //  2020/04/07  楊朋朋(SYS) ADD Start C43017_見積書改修      
    /**
     * <pre>
     * [説 明] 法人企業マスタ取得
     * @param kigyoCd 企業コード
     * @throws Exception 
     * </pre>
     */
    public Map<String, Object> getKigyoData(String kigyoCd) throws Exception ;
    //  2020/04/07  楊朋朋(SYS) ADD End C43017_見積書改修      
    
    //2020/05/06 ADD START 楊朋朋 C43040-2_法人の入力項目追加対応
    /** <pre>
     * 「説明」　法人仲介依頼を削除します
     * @param kaisyacd 会社コード
     * @param uketukeNo 
     * @param uketukeUpdDate 更新日
     * @param userCd ユーザコード
     * @return なし
     * </pre>
     */
    public Map<String, Object> updHoujinnTyuukaiIlaiFlag(String kaisyacd, String uketukeNo,String uketukeUpdDate,String userCd) throws Exception;
    /**
     * <pre>
     * [説 明] 仲介依頼先店舗代表メールアドレスと仲介担当メールアドレス取得
     * @param kaisya_cd 会社コード
     * @param postCd 店舗コード
     * @param uketukeNo 受付no
     * @return 仲介依頼先店舗の各社員メールアドレスリスト
     * </pre>
     */
    public Map<String, Object> getTempoDaihyouAndTanTouMailAddress(String kaisyacd, String uketukePostCd,
            String uketukeNo) throws Exception ;
    /**
     * <pre>
     * [説 明] 仲介依頼先担当者を取得
     * @param kaisyacd 会社コード
     * @param uketukeNo 受付№
     * @return 仲介依頼担当者情報
     * </pre>
     */
    public Map<String, Object> getHoujinIraiInfo(String kaisyacd, String uketukeNo) throws Exception ;
    //2020/05/06 ADD END 楊朋朋 C43040-2_法人の入力項目追加対応
}
