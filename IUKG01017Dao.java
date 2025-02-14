/**
 *@システム名: 受付システム
 *@ファイル名: IUKG01017Dao.java
 *@更新日付：: 2017/07/31
 *@Copyright: 2011 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**<pre>
 * [機 能] ホームメイト自動受付キャンセル理由登録
 * [説 明] ホームメイト自動受付キャンセル理由登録データアクセスインタフェース
 * @author [作 成] 2017/07/31 MJEC)鈴村
 * </pre>
 */
public interface IUKG01017Dao {

	/** <pre>
	 * [説 明] ホームメイト自動受付キャンセル理由登録
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付番号
	 * @param tantousya 担当者
	 * @param cancleReason キャンセル理由
	 * @param kibouFlg 希望条件ラグ
	 * @param updDate 画面格納日付
	 * @return なし
	 * </pre>
	 */
	public Map<String, Object> upCancleInfo(String kaisyaCd, String uketukeNo,
			String tantousya, String cancleReason,
			String kibouFlg, String updDate) throws Exception;
}
