/**
 * @システム名: 受付システム
 * @ファイル名: IUKG02030Service.java
 * @更新日付：: 2011/12/26
 * @Copyright: 2011 token corporation All right reserved
 * 更新履歴: 2014/01/03  SYS_ 謝超    (案件C37075)オリジナル情報誌リプレース
 * 更新履歴: 2019/06/27  SYS  郝年昇  (案件C43100)お客様項目の入力簡素化対応
 */
package jp.co.token.uketuke.service;

import java.util.List;
import java.util.Map;

import jp.co.token.uketuke.formbean.KibouJyoukennBean;

/**
 * <pre>
 * [機 能] 希望条件
 * [説 明] お客様の希望条件を入力・登録する。
 * @author [作 成] 2011/11/02 SYS_賈
 * </pre>
 */
public interface IBukkenSearchService {

	//2014/01/03 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース
//	/**
//	 * <pre>
//	 * [説 明] 希望条件初期化情報の取得
//	 * @param kaisya_cd          会社コード
//	 * @param uketuke_no         受付№
//	 * @return 希望条件初期データ
//	 * </pre>
//	 */
//	public String getInitInfo(String kaisya_cd,
//	                          String uketuke_no,
//	                          KibouJyoukennBean kiboujyoukenBean) throws Exception;
	/**
	 * <pre>
	 * [説 明] 希望条件初期化情報の取得
	 * @param kibouJyoukennBean          希望条件データ
	 * @return 希望条件初期データ
	 * </pre>
	 */
	public String getInitInfo(KibouJyoukennBean kiboujyoukenBean) throws Exception;
	//2014/01/03 謝超 UPD End

	/**
	 * <pre>
	 * [説 明] こだわり条件データの取得
	 * @param 希望条件データ     kibouJyoukenBean
	 * </pre>
	 *
	 * @return
	 */
	public Map<String, Object> getKodawariInfo(KibouJyoukennBean kiboujyoukenBean) throws Exception;

	/**
	 * <pre>
	 * [説 明] 物件検索の共通
	 *
	 * @return 検索結果 件数
	 * </pre>
	 */
	// 2019/06/20 UPD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
	// public Map<String, Object> getBukkenCount(KibouJyoukennBean kiboujyoukenBean) throws Exception;
	public Map<String, Object> getBukkenCount(KibouJyoukennBean kiboujyoukenBean,String tokuTenFlg) throws Exception;
	// 2019/06/20 UPD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
	
   	/**
	 * <pre>
	 * [説 明] 希望条件データ保存
	 *
	 * @return 成功フラグ
	 * </pre>
	 */
    // 2019/08/05 UPD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
	// public String save(KibouJyoukennBean kiboujyoukenBean) throws Exception;
	public  Map<String, Object> save(KibouJyoukennBean kiboujyoukenBean) throws Exception;
    // 2019/08/05 UPD END  郝年昇(SYS) C43100_お客様項目の入力簡素化対応

	/**
	 * <pre>
	 * [説 明] 希望条件で検索結果の登録処理を行います
	 * @param sessionId セッションID
	 * @param kibouJyoukenBean 希望条件データ
	 * @param batchDate バッチ用日付
	 * </pre>
	 */
	public Map<String, Object> getKuusituBukkenInfo(String sessionId,
	                                                KibouJyoukennBean kibouJyoukenBean,
	                                                String batchDate) throws Exception;

	//2014/01/03 謝超 ADD Start (案件C37075)オリジナル情報誌リプレース
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
	public Map<String,Object> getBukkenList(KibouJyoukennBean kiboujyoukenBean,
			String sortJyoken, int startRow, int endRow) throws Exception;

	/**
	 * <pre>
	 * [説 明] 情報誌を保存する
	 * @param flag 処理フラグ
	 * @param processType 遷移フラグ
	 * @param user ユーザー
	 * @param magazineDataMap 情報誌データ
	 * @param pageDataList 情報誌ページデータ
	 * @param bukkenDataList 情報誌物件データ
	 * @param kiboujyoukenBeanList 希望条件データリスト
	 * @return 情報誌の保存結果
	 * </pre>
	 */
	public Map<String, Object> saveMagazineData(String flag, String processType, String user, Map<String, Object> magazineDataMap, List<Map<String, Object>> pageDataList, List<Map<String, Object>> bukkenDataList,List<KibouJyoukennBean> kiboujyoukenBeanList)
			throws Exception;
	//2014/01/03 謝超 ADD End

}
