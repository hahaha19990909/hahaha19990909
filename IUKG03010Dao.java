/**
 * @システム名: 受付システム
 * @ファイル名: IUKG03010Dao.java
 * @更新日付 : 2012/05/17
 * 変更履歴: 2013/02/06 SYS_胡涛  案件C37058
 * 変更履歴: 2013/05/20 SYS_張強 (案件C37109)受付システムSTEP2要望対応
 * 変更履歴: 2013/07/02 SYS_趙雲剛 (案件C37103-1)チラシ作成機能の追加-STEP2
 * 変更履歴: 2017/08/17 MJEC)長倉 (案件C41059)中止他決顧客の検索対応
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**
 * <pre>
 * [機 能] 物件情報詳細
 * [説 明] 物件情報詳細DAO
 * @author SYS_戴氷馨
 * </pre>
 */
public interface IUKG03010Dao {

	/**
	 * <pre>
	 * [説 明] 画面初期化情報取得
	 * @param kaisyaCd         会社CD
	 * @param uketukeNo        受付№
	 * @param bukkenNo         物件№
	 * @param heyaNo           部屋№
	 * @param post             店舗CD
	 * @param sessionId        セッションID
	 * @return 画面初期化情報の取得
	 * </pre>
	 */
	public Map<String, Object> getInitInfo(String kaisyaCd,
	                                       String uketukeNo,
	                                       String bukkenNo,
	                                       String heyaNo,
	                                       // 2013/07/02  SYS_趙雲剛   ADD Start (案件C37103-1) チラシ作成機能の追加-STEP2
	                                       String post,
	                                       // 2013/07/02  SYS_趙雲剛   ADD End
	                                       String sessionId) throws Exception;

	/**
	 * <pre>
	 * [説 明] 非表示チェックボックスるチェックされた処理
	 * @param paramMap    該当物件MAP
	 * </pre>
	 */
	public Map<String, Object> noDispChecked(Map<String, Object> paramMap) throws Exception;

	/**
	 * <pre>
	 * [説 明] 商談内容確認ボタン処理
	 * @param paramMap    該当物件MAP
	 * </pre>
	 */
	public Map<String, Object> talkNaiyou(Map<String, Object> paramMap) throws Exception;

	/**
	 * <pre>
	 * [説 明] お気に入り数の取得
	 * @param paramMap    該当物件MAP
	 * </pre>
	 */
	public Map<String, Object> getOkiniIriSuu(Map<String, Object> paramMap) throws Exception;

	// 2013/02/06  SYS_胡涛  ADD START 案件C37058
	/**
	 * <pre>
	 * [説 明] 入居中に変更する処理
	 * @param paramMap    該当物件MAP
	 * </pre>
	 */
	public Map<String, Object> updateHeyaNyukyo(Map<String, Object> paramMap) throws Exception;
	// 2013/02/06  SYS_胡涛  ADD END
	//2013/05/20  SYS_張強   ADD Start (案件C37109)受付システムSTEP2要望対応
	/**
	 * <pre>
	 * [説 明] 希望設備データ取得を行う
	 * @param paramMap    設備MAP
	 * </pre>
	 */
	public Map<String, Object> getSetubiData(Map<String, Object> paramMap) throws Exception;
	/**
	 * <pre>
	 * [説 明] 角部屋設備No取得を行う
	 * @param paramMap    設備MAP
	 * </pre>
	 */
	public Map<String, Object> getKadobeyaSetubiNo(Map<String, Object> paramMap) throws Exception;
	//2013/05/20  SYS_張強   ADD End

	// 2017/08/17 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
	/** <pre>
	 * [説 明] お客様情報取得
	 * @param kaisya_cd 会社コード
	 * @param uketuke_no 受付№
	 * @return お客様情報データ
	 * </pre>
	 */
	public Map<String, Object> getCustomerData(String kaisya_cd, String uketuke_no) throws Exception;
	// 2017/08/17 MJEC)長倉 ADD End
}
