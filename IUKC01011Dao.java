/**
 *@システム名: 受付システム
 *@ファイル名: IUKC01011Dao.java
 *@更新日付  : 2017/09/25
 *@Copyright : 2017 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**
 * <pre>
 * [機 能] 法人仲介依頼状況CSV出力。
 * [説 明] 法人仲介依頼状況CSV出力処理を行う。
 * @author [作 成] 2017/09/5 SYS_張
 * </pre>
 */
public interface IUKC01011Dao {

	/**
	 * <pre>
	 * [説 明] 受付状況CSVデータ取得
	 * @param strKaisyacd 会社コード
	 * @param strDate 日付
	 * @param strUserAuthority ユーザー登録権限コード
	 * @param strSisyaBlkCd 支店ブロックコード
	 * @return 法人仲介依頼状況CSVデータ
	 * </pre>
	 */
	public Map<String, Object> getUketukeHoujinCsv(String strKaisyacd,
			String strDate,String strUserAuthority,String strSisyaBlkCd) throws Exception;
}
