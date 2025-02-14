/**
 * @システム名: 受付システム
 * @ファイル名: IUKG02041Dao.java
 * @更新日付：: 2012/3/29
 * @Copyright: 2011 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.List;
import java.util.Map;

import jp.co.token.uketuke.formbean.BukkenDataBean;

/**
 * <pre>
 * [機 能] 次回来店・現地案内予約
 * [説 明] 次回来店・現地案内の予約登録を行う。
 * @author [作 成] 2011/11/14 戴氷馨(SYS)
 * [修 正] by 趙 2012/3/29 STEP2対応
 * </pre>
 */
public interface IUKG02041Dao {

	/**
	 * <pre>
	 * [説 明] 予約日初期値取得
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付№
	 * @return 予約日初期値
	 * </pre>
	 */
	public List<Map<String, Object>> getAnnaiInitData(String kaisyaCd,
	                                               String uketukeNo) throws Exception;
	
	/**
	 * <pre>
	 * [説 明] 予約状況データを取得
	 * @param kaisyaCd 会社コード
	 * @param yoyakuDate 予約日
	 * @param chukaitantou 仲介担当者
	 * @return 予約状況データリスト
	 * </pre>
	 */
	public List<Map<String, Object>> getYoyakuInfo(String kaisyaCd,
	                                               String yoyakuDate,
	                                               String chukaitantou) throws Exception;

	/**
	 * <pre>
	 * [説 明] 同一時間の予約チェック
	 * @param kaisyaCd 会社コード
	 * @param yoyakuDate 予約日
	 * @param chukaitantou 仲介担当者
	 * @return チェック結果
	 * </pre>
	 */
	public boolean checkYoyakuTime(String kaisyaCd,
	                               String yoyakuDate,
	                               String chukaitantou) throws Exception;

	/**
	 * <pre>
	 * [説 明] 予約データ追加処理
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param yoyakuDate 予約日
	 * @param postCd 店舗コード
	 * @param chukaitantou 仲介担当者コード
	 * @param tantouCd 担当者コード
	 * @return 予約データ追加処理の結果
	 * </pre>
	 */
	public Map<String, Object> addYoyakuInfo(String kaisyaCd,
	                                         String uketukeNo,
	                                         String yoyakuDate,
	                                         String postCd,
	                                         String chukaitantou,
	                                         String tantouCd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 現地案内データ追加処理
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param updateMode 更新モード 
	 * @param yoyakuDate 予約日
	 * @param bukkenNo 物件№
	 * @param heyaNo 部屋№
	 * @param seq 現地案内予約SEQ
	 * @param bukkenList 物件/部屋リスト
	 * @param postCd 店舗コード
	 * @param chukaitantou 仲介担当者コード
	 * @param tantouCd 担当者コード
	 * @return 予約データ追加処理の結果
	 * </pre>
	 */
	public Map<String, Object> addAnnaiInfo(String kaisyaCd,
	                                        String uketukeNo,
	                                        String updateMode,
	                                        String yoyakuDate,
	                                        String bukkenNo,
	                                        String heyaNo,
	                                        String seq,
	                                        List<BukkenDataBean> bukkenList,
	                                        String postCd,
	                                        String chukaitantou,
	                                        String tantouCd) throws Exception;

	/**
	 * <pre>
	 * [説 明] 来店予約データ論理削除処理
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @return 来店予約データ論理削除処理の結果
	 * </pre>
	 */
	public Map<String, Object> delYoyakuInfo(String kaisyaCd,
	                                         String uketukeNo) throws Exception;

	/**
	 * <pre>
	 * [説 明] 現地案内データ追加処理
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param updateMode 更新モード 
	 * @param yoyakuDate 予約日
	 * @param bukkenNo 物件№
	 * @param heyaNo 部屋№
	 * @param seq 現地案内予約SEQ
	 * @param bukkenList 物件/部屋リスト
	 * @param postCd 店舗コード
	 * @param chukaitantou 仲介担当者コード
	 * @param tantouCd 担当者コード
	 * @return 予約データ追加処理の結果
	 * </pre>
	 */
	public Map<String, Object> delAnnaiInfo(String kaisyaCd,
	                                        String uketukeNo,
	                                        String updateMode,
	                                        String bukkenNo,
	                                        String heyaNo,
	                                        String seq,
	                                        List<BukkenDataBean> bukkenList) throws Exception;
}
