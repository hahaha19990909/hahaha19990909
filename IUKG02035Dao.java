/**
 * @システム名: 受付システム
 * @ファイル名: IUKG02035Dao.java
 * @更新日付：: 2012/05/30
 * @Copyright: 2012 token corporation All right reserved
 */

package jp.co.token.uketuke.dao;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 * [機 能] 通勤・通学時間の設定
 * [説 明] 通勤・通学時間の設定する。
 * @author [作 成]
 * </pre>
 */
public interface IUKG02035Dao {

	/**
	 * <pre>
	 * [説 明] 乗り換え許容回数を取得する。
	 * 	
	 * @return 乗り換え許容回数を取得する。
	 * </pre>
	 */
	public Map<String, Object> getNorikaeKaisuu() throws Exception;

	/**
	 * <pre>
	 * [説 明] 駅情報の取得
	 * @param paramMap    駅情報MAP
	 * </pre>
	 */
	public Map<String, Object> ekiInfoList(Map<String, Object> paramMap) throws Exception;

	/**
	 * <pre>
	 * [説 明] 路線情報の取得
	 * @param paramMap    路線情報MAP
	 * </pre>
	 */
	public Map<String, Object> rosenInfoList(Map<String, Object> paramMap) throws Exception;

	/**
	 * <pre>
	 * [説 明] タイムリスト取得
	 * 	
	 * @return タイムリストの取得
	 * </pre>
	 */
	public List<Map<String, Object>> getTimeList() throws Exception;

	/**
	 * <pre>
	 * [説 明] 駅情報の保存
	 * @param paramMap    路線情報MAP
	 * </pre>
	 */
	public Map<String, Object> saveStationData(Map<String, Object> paramMap) throws Exception;
}
