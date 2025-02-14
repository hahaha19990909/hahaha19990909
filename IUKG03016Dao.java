/**
 * @システム名: 受付システム
 * @ファイル名: IUKG03016Dao.java
 * @更新日付：: 2019/12/12
 * @Copyright: 2019 token corporation All right reserved
 */
package jp.co.token.uketuke.dao;

import java.util.Map;

/**
 * <pre>
 * [機 能]　周辺施設
 * [説 明]　周辺施設
 * @author　[作 成] 2019/12/12 SYS_劉恆毅
 * </pre>
 */
public interface IUKG03016Dao {

    /**
     * <pre>
     * [説 明]施設コードを取得
     * @param bunrui_s_cd  施設小分類
     * @param center_i_do  中心.緯度
     * @param center_kei_do  中心.経度
     * @param shisetu_kyori  距離
     * @return こだわリ条件データ
     * </pre>
     */
    public Map<String, Object> getShisetsuScope(String bunruiSCd, String centerIDo, String centerKeiDo, String shisetuKyori) throws Exception;
    
    /**
     * <pre>
     * [説 明] 小分類コードを取得する
     * @param bunruiKbn          施設分類区分コード（キー）
     * @return 小分類コード
     * </pre>
     */
    public Map<String, Object> getKokatekoriCode(String bunruiKbn) throws Exception;
    
}
