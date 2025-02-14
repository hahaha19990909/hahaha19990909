/**
 * @システム名: 受付システム
 * @ファイル名: IUKG03012Dao.java
 * @更新日付：: 2012/5/18
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**
 * <pre>
 * [機 能]　物件情報(希望条件)情報取得
 * [説 明]　物件情報(希望条件)情報取得設定 
 * @author　[作 成] 2012/5/18 SYS_楊
 * </pre>
 */
public interface IUKG03012Dao {

	/**
	 * <pre>
	 * [説 明] ご希望の条件を取得する。
	 * @param kaisyaCD  会社コード
	 * @param uketukeNo  受付№
	 * @param rirekiSEQ  希望条件履歴SEQ
	 * @return ご希望の条件データ
	 * </pre>
	 * 
	 */
	public Map<String, Object> getKibouJyoukenInfo(String kaisyaCD,String uketukeNo,String rirekiSEQ) throws Exception;
	
	
	/**
	 * <pre>
	 * [説 明]こだわリ条件を取得
	 * @param kaisyaCD  会社コード
	 * @param uketukeNo  受付№
	 * @param rirekiSEQ  希望条件履歴SEQ
	 * @return こだわリ条件データ
	 * </pre>
	 */
	public Map<String, Object> getKodawariJyoukenInfo(String kaisyaCD,String uketukeNo,String rirekiSEQ) throws Exception;
	
}
