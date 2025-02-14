/**
 *@システム名: 受付システム
 *@ファイル名: IUKG99009Dao.java
 *@更新日付：: 2018/12/25
 *@Copyright: 2018 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**<pre>
 * [機 能] 物件パノラマ画像一覧表示
 * [説 明] 検索条件によって、物件のパノラマ内観とパノラマ外観画像一覧を検索し、表示する。
 * @author [作 成] 2018/12/25 SYS_薛
 * </pre>
 */
public interface IUKG99009Dao {

    /**
     * <pre>
     * [説 明] パノラマ画像イメージリスト
     * @param bukkenNo         物件№
     * @param heyaNo           部屋№
     * @return パノラマ画像イメージリストの取得
     * </pre>
     */
    public Map<String, Object> getPanoramaImageList(String bukkenNo,
                                             String heyaNo) throws Exception;
}
