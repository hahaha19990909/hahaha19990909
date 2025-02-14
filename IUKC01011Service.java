/**
 *@システム名: 受付システム
 *@ファイル名: IUKC01011Service.java
 *@更新日付  : 2017/00/25
 *@Copyright : 2017 token corporation All right reserved
 */
package jp.co.token.uketuke.service;

import java.util.Map;

public interface IUKC01011Service {

	/**
	 * <pre>
	 * [説 明] 法人仲介依頼状況CSV出力データ取得
	 * @param strKaisyacd 会社コード
	 * @param strDate 日付
	 * @param strUserAuthority ユーザー登録権限コード
	 * @param strSisyaBlkCd 支店ブロックコード
	 * @return 法人仲介依頼状況CSVデータ
	 * </pre>
	 */
	public Map<String, Object> getUketukeHoujinCsv(String strKaisyacd,
			String strDate, String strUserAuthority, String strSisyaBlkCd) throws Exception;

}
