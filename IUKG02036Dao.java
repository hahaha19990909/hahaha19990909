/**
 *@システム名: 受付システム
 *@ファイル名: IUKG02034Dao.java
 *@更新日付：: 2012/05/30
 *@Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * [機 能] 学校・施設・バス停の設定
 * [説 明] 学校・施設・バス停を設定する。
 * @author [作 成] 2012/05/30 SYS_趙
 * </pre>
 */
public interface IUKG02036Dao {

	/** <pre>
	 * [説 明] 都道府県リストを取得する。
	 * @return 都道府県リストのデータ
	 * </pre>
	 */
	public List<Map<String, Object>> getJusyoData() throws Exception;

	/** <pre>
	 * [説 明] 都道府県コードより、市区郡リストを取得する。
	 * @param jusyo1 県コード
	 * @return 市区郡リスト
	 * </pre>
	 */
	public List<Map<String, Object>> getJusyo2Data(String jusyo1) throws Exception ;

	/** <pre>
	 * [説 明] バス会社リストを取得する。
	 * @param jyusyo 地域情報
	 * @param jusyo2 市 コード
	 * @return バス会社リスト
	 * </pre>
	 */
	public List<Map<String, Object>> getBusKaisyaList(String jyusyo, String jusyo2) throws Exception;

	/** <pre>
	 * [説 明] バス路線リストを取得する。
	 * @param jyusyo 地域情報
	 * @param busKaisya バス会社コード
	 * @return バス会社リスト
	 * </pre>
	 */
	public List<Map<String, Object>> getBusRosenList(String jyusyo, String busKaisya) throws Exception;

	/** <pre>
	 * [説 明] 施設バスマスタを検索する。
	 * @param buskaisya バス会社名
	 * @param rosenn バス路線
	 * @param maxCount 最大件数
	 * @return バス会社リスト
	 * </pre>
	 */
	public List<Map<String, Object>> getShisetsuBusList(String buskaisya, String rosenn, String maxCount) throws Exception;

	/**
	 * <pre>
	 * [説 明] 項目を取得する。
	 * @param jouhouSbCd 情報種類コード
	 * @param kmSbCd 項目種類コード
	 * @return 項目リストのデータ
	 * </pre>
	 */
	public List<Map<String, Object>> getKoumoku(String jouhouSbCd, String kmSbCd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 情報種別コードと備考からデータを取得。
	 * @param jouhouSbCd 情報種類コード
	 * @param kmSbCd 備考
	 * @return 項目リストのデータ
	 * </pre>
	 */
	public List<Map<String, Object>> getSisetuListData(String jouhouSbCd, String bikou) throws Exception;

	/**
	 * <pre>
	 * [説 明] 施設マスタ検索(通常パターン)する。
	 * @param shisetuCode 施設分類コード
	 * @param prefectureCode 県コード
	 * @param siCode 市コード
	 * @return 施設リストのデータ
	 * </pre>
	 */
	public List<Map<String, Object>> getShisetsuList(String shisetuCode, String prefectureCode, String siCode) throws Exception;
}
