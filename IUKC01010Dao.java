/**
 *@システム名: 受付システム
 *@ファイル名: IUKC01010Dao.java
 *@更新日付  : 2013/04/22
 *@Copyright : 2013 token corporation All right reserved
 * 更新履歴  : 2014/06/30 温(SYS)   1.01   (案件C38117)セールスポイント編集機能の追加
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**
 * <pre>
 * [機 能] 受付状況CSV出力。
 * [説 明] 受付状況CSV出力処理を行う。
 * @author [作 成] 2013/04/22 温(SYS)
 * </pre>
 */
public interface IUKC01010Dao {

	/**
	 * <pre>
	 * [説 明] 受付状況CSVデータ取得
	 * @param strKaisyacd 会社コード
	 * @param strPostcd 店舗コード
	 * @param strDate 日付
	 * @param strUserAuthority ユーザー登録権限コード
	 * @param strSisyaBlkCd 支店ブロックコード
	 * @return 受付状況CSVデータ
	 * </pre>
	 */
	public Map<String, Object> getUketukeJoukyoCsv(
			String strKaisyacd,String strPostcd,String strDate,
			// ########## 2014/06/23 温 UPD Start (案件C38117)セールスポイント編集機能の追加
			String strUserAuthority,String strSisyaBlkCd) throws Exception;
			// ########## 2014/06/23 温 UPD End

}
