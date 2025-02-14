/**
 * @システム名: 受付システム
 * @ファイル名: IHelpBusCommonService.java
 * @更新日付：: 2012/08/01
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.service;

import java.util.Map;

/**
 * <pre>
 * [機 能] バス停ヘルプ画面共通。
 * [説 明] 県の取扱バス会社を取得すること。
 * @author [作 成] 2012/08/01 謝超(SYS)
 * </pre>
 */
public interface IHelpBusCommonService {

	/**
	 * <pre>
	 * [説 明] 県の取扱バス会社を取得すること。
	 * @param jouhouCd 県コード
	 * @return バス停ヘルプ画面共通データ
	 * </pre>
	 */
	public Map<String, Object> getOfficeByJouhouCd(String jouhouCd) throws Exception;
	
	/**
	 * <pre>
	 * [説 明] 県の名前を取得すること。
	 * @param jouhouCd 県コード
	 * @return 県の名前
	 * </pre>
	 */
	public Map<String, Object> getJouhouNameByJouhouCd(String jouhouCd) throws Exception;
}
