/**
 * @システム名: オンライン仲介システム
 * @ファイル名: IUKG02044Dao.java
 * @更新日付：: 2021/04/14
 * @Copyright: 2021 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.List;
import java.util.Map;

import jp.co.token.uketuke.formbean.OnlineMailBean;

/**
 * <pre>
 * [機 能] オンライン仲介予約
 * [説 明] オンライン仲介予約の予約登録を行う。
 * @author [作 成] 2021/04/14 楊振華(SYS)
 * </pre>
 */
public interface IUKG02044Dao {

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
	 * [説 明] 予約履歴j情報を取得
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
     * [説 明] メール送信情報を取得する
     * @param kaisyaCd 会社コード
     * @param yoyakuDate 予約日
     * @param chukaitantou 仲介担当者
     * @param postCd 店舗コード
     * @return チェック結果
     * </pre>
     */
	public OnlineMailBean getOnlineMail(String kaisyaCd,
	                               String uketukeNo,
	                               String chukaitantou,
	                               String postCd) throws Exception;

	/**
     * ルームIDを取得する
     * @param kaisyaCd 会社コード
     * @param uketukeNo 受付№
     * @param yoyakuDate 予約日
     * @return ルームID
     * @throws Exception
     */
	public String getAccessKey(String kaisyaCd,
                               String uketukeNo,
                               String yoyakuDate) throws Exception;

	/**
     * <pre>
     * [説 明] 予約データ追加処理
     * @param kaisyaCd 会社コード
     * @param uketukeNo 受付NO.
     * @param yoyakuDate オンライン仲介予約日
     * @param accessKey ルームID
     * @param url ビデオ通話URL
     * @param postCd 受付店舗コード
     * @param chukaitantou 仲介担当者コード
     * @return 予約データ追加処理の結果
     * @throws Exception
     * </pre>
     */
	public Map<String, Object> addYoyakuInfo(String kaisyaCd,
                                             String uketukeNo,
                                             String yoyakuDate,
                                             String accessKey,
                                             String url,
                                             String postCd,
                                             String chukaitantou) throws Exception;

	/**
     * <pre>
     * [説 明] 予約データ更新処理
     * @param kaisyaCd 会社コード
     * @param uketukeNo 受付番号
     * @param yoyakuDateOld 修正前オンライン仲介予約日
     * @param yoyakuDate 仲介予約日
     * @param videoUrl ビデオ通話URL
     * @param chukaitantou 仲介担当者コード
     * @return 予約データ追加処理の結果
     * </pre>
     */
	public Map<String, Object> updYoyakuInfo(String kaisyaCd,
                                            String uketukeNo,
                                            String yoyakuDateOld,
                                            String yoyakuDate,
                                            String videoUrl,
                                            String chukaiTantouCd) throws Exception;

}
