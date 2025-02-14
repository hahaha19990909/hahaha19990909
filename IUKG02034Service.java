/**
 * @システム名: 受付システム
 * @ファイル名: IUKG02034Dao.java
 * @更新日付：: 2012/05/08
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴: 2014/01/07  SYS_ 郭凡    (案件C37075)オリジナル情報誌リプレース
 */
package jp.co.token.uketuke.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import jp.co.token.uketuke.formbean.KibouJyoukennBean;

/**
 * <pre>
 * [機 能] 希望路線の設定
 * [説 明] 希望路線を設定する。
 * @author [作 成] 2012/05/08 SYS_趙
 * </pre>
 */
public interface IUKG02034Service {

	/**
	 * <pre>
	 * [説 明] 都道府県リストを取得する。
	 * @return 都道府県リストのデータ
	 * </pre>
	 */
	public List<Map<String, Object>> getJusyoData() throws Exception;

	/** <pre>
	 * [説 明] 路線情報を取得する 。
	 * @param ekiCd 駅コード
	 * @param kaisyaCd 会社コード
	 * @param kibouJyoukennBean 希望条件データ
	 * @return 路線情報
	 * </pre>
	 */
	public Map<String, Object> getEkiInfo(String ekiCd, String kaisyaCd, KibouJyoukennBean kibouJyoukennBean) throws Exception;

	/**
	 * <pre>
	 * [説 明] 担当者の店舗の「都道府県」路線情報を取得する 。
	 * @param jusyo_cd 「都道府県」コード
	 * @return 担当者の店舗の「都道府県」路線情報のデータ
	 * </pre>
	 */
	public Map<String, String> getRosenData(String jusyo_cd) throws Exception;

	/** <pre>
	 * [説 明] 県コードを検索する。
	 * @param ekiCd 駅コード
	 * @return 県コード
	 * </pre>
	 */
    public String getRefectureCd(String ekiCd) throws Exception;

	/** <pre>
	 * [説 明] 希望物件数を検索する 。
	 * @param ekiCd 駅コード
	 * @return 希望物件数
	 * </pre>
	 */
	public BigDecimal getEkiBukkenCount(String ekiCd) throws Exception;

	/** <pre>
	 * [説 明] 路線リストを検索する。
	 * @param ekiCd 駅コード
	 * @return 路線リスト
	 * </pre>
	 */
	public List<Map<String, Object>> getRossenList(String ekiCd) throws Exception;
	// 2014/01/07  郭凡 ADD Start (案件番号C37075)オリジナル情報誌リプレース	
	/** <pre>
	 * [説 明] 新着空室路線情報を取得する 。
	 * @param ekiCd 駅コード
	 * @param kaisyaCd 会社コード
	 * @param kibouJyoukennBean 希望条件データ
	 * @return 路線情報
	 * </pre>
	 */
	public Map<String, Object> getShicyakuEkiInfo(String ekiCd, String kaisyaCd, KibouJyoukennBean kibouJyoukennBean) throws Exception;
	// 2014/01/07  郭凡 ADD End
}
