/** 
 *@システム名: 受付システム 
 *@ファイル名: IUKG02043Dao.java 
 *@更新日付：: 2012/03/06 
 *@Copyright: 2012 token corporation All right reserved  
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**<pre>
 * [機 能] アラーム通知情報の登録
 * [説 明] アラーム通知情報の登録を行う。
 * @author [作 成] 2012/03/06 馮強華(SYS)
 * </pre>
 */
public interface IUKG02043Dao {

	/**
	 * アラーム通知情報の登録
	 * @param kaisyaCd    会社コード
	 * @param tantousya   担当者
	 * @param uketukeNo   受付番号
	 * @param alarmdate   アラム日
	 * @param alarmmemo   アラムメモ
	 * @param haitadate   排他日付
	 * @return アラーム通知情報の登録の結果
	 * @throws Exception
	 */
	public Map<String, Object> updAlarmInfo(String kaisyaCd, 
											 String tantousya,
			                                 String uketukeNo, 
			                                 String alarmdate, 
			                                 String alarmmemo,
			                                 String haitadate) throws Exception;
	/**
	 * アラーム通知初期情報取得
	 * @param kaisyaCd    会社コード
	 * @param uketukeNo   受付番号
	 * @return アラーム通知情報の登録の結果
	 * @throws Exception
	 */
	public Map<String, Object> initAlarmInfo(String kaisyaCd, 
			                                 String uketukeNo) throws Exception;
}
