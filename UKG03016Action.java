/**
 * @システム名: 受付システム
 * @ファイル名: UKG03016Action.java
 * @更新日付：: 2019/12/04
 * @Copyright: 2019 token corporation All right reserved
 * 更新履歴：2019/12/04 劉恆毅(SYS)        1.0        C44033-1_ホームメイトリサーチと施設情報の連携
 */
package jp.co.token.uketuke.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.impl.UKG03016ServiceImpl;

/**
 * <pre>
 * [機 能] 周辺施設表示
 * [説 明] 周辺施設で表示する
 * @author [作 成]
 * </pre>
 */
public class UKG03016Action extends BaseAction {

    /**
     * クラスのシリアル化ID
     */
    private static final long serialVersionUID = -5458276230349927909L;

    /**
     *  ログオブジェクト.
     */
    protected Log log = LogFactory.getLog(UKG03016Action.class);

    private UKG03016ServiceImpl UKG03016Services;
    
    public UKG03016ServiceImpl getUKG03016Services() {
        return UKG03016Services;
    }

    public void setUKG03016Services(UKG03016ServiceImpl uKG03016Services) {
        UKG03016Services = uKG03016Services;
    }
    /** 周辺施設情報画面用データ */
    public Map<String, Object> ukg03016Data = new HashMap<String, Object>();
    public Map<String, Object> getUkg03016Data() {
          return ukg03016Data;
     }

     /**
     * 周辺施設情報画面用データ
     *
     * @param ukg01013Data
     */
    public void setUkg03016Data(Map<String, Object> ukg03016Data) {

        this.ukg03016Data = ukg03016Data;
    }
    
    /**
     * <pre>
     * [説 明] 周辺施設初期化
     * @return アクション処理結果
     * @throws Exception 処理異常
     * </pre>
     */
    @SuppressWarnings("unchecked")
    @Override
    public String execute() throws Exception {
        
        log.debug("UKG03016Actionを呼び出し 開始");
        
        // 同期フラグ設定
        setAsyncFlg(true);
        
        List<Map<String, Object>>  shisetsuCommonList = UKG03016Services.getCommonDate(Consts.SHISETSU_JOUHOUSBCD);
        String kokatekori = "";
        List<Map<String, String>> kokatekoriList = null;
        for(Map<String, Object> shisetsuCommon : shisetsuCommonList) {
            kokatekori = (String)shisetsuCommon.get("BIKOU");
            if(kokatekori != null && !"".equals(kokatekori)) {
                continue;
            }
            kokatekoriList = (List<Map<String, String>>)UKG03016Services.getKokatekoriCode(shisetsuCommon.get("KOUMOKU_SB_VAL").toString()).get("bunruis_cd");
            shisetsuCommon.put("BIKOU",kokatekoriList.get(0).get("KOBUNRUI_S_CD")) ;
        }
        ukg03016Data.put("shisetsuCommonList", shisetsuCommonList);
        ukg03016Data.put("jyoriCommonList", UKG03016Services.getCommonDate(Consts.JYORI_JOUHOUSBCD));
        
        log.debug("UKG03016Actionを呼び出し 終了");
        return successForward();
    }
    /**
     * <pre>
     * [説 明]施設リスト取得
     * @return 施設リスト
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public String bukkenList() throws Exception {
        try {
            HttpServletRequest request = getRequest();
            Map<String, Object> ShisetsuScope = new HashMap<String, Object>();
            ShisetsuScope = UKG03016Services.getShisetsuScope(request.getParameter("bunrui_s_cd"),
                    request.getParameter("center_i_do"),
                    request.getParameter("center_kei_do"),
                    request.getParameter("shisetu_kyori"));
            List<HashMap<String, String>> shisetuList = (List<HashMap<String, String>> ) ShisetsuScope.get("out_shisetuList");
            List<String> shisetsuJouhouList = null;

            if (shisetuList != null && shisetuList.size() != 0) {
                shisetsuJouhouList = new ArrayList<String>();
                for(HashMap<String, String> shisetu : shisetuList) {
                    shisetsuJouhouList.add(shisetu.get("DATA_CD"));
                }
            }
            ukg03016Data.put("bukkenList", shisetsuJouhouList);
        } catch (Exception e) {
            ukg03016Data.put("rstMsg",Util.getServerMsg("ERRS002", "施設データリスト取得"));
        }
        return "ajaxProcess";
    }

    /**
     * <pre>
     * [説 明]施設情報取得
     * @return 施設情報
     * </pre>
     */
    public String bukkenDetail() throws Exception {
        try {
            HttpServletRequest request = getRequest();
            PropertiesReader propertiesReader = PropertiesReader.getIntance();
            String shisetsuApiUrl = propertiesReader.getProperty(Consts.SHISETSU_API_JOUHOU_URL);
            Map<String, Object> shisetsuJouhou = null;
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("system_id", Consts.IMAGE_SYSTEM_ID);
            shisetsuJouhou = new HashMap<String, Object>();
            String dataCd = request.getParameter("data_cd");
            parameterMap.put("data_cd", dataCd);
            String apiResult = Util.callApiByURL(shisetsuApiUrl, parameterMap);
            shisetsuJouhou = Util.formatJsonToMap(apiResult);
            if(shisetsuJouhou.size() > 0) {
                String imgUrl = propertiesReader.getProperty(Consts.SHISETSU_API_IMAGE_URL) + "?system_id=" + Consts.IMAGE_SYSTEM_ID + "&data_cd=" + dataCd;
                shisetsuJouhou.put("big_img_url", imgUrl);
                ukg03016Data.put("bukkenDetail", shisetsuJouhou);
            } else {
                ukg03016Data.put("rstMsg",Util.getServerMsg("ERRS002", "施設データ取得"));
            }
            ukg03016Data.put("bukkenDetail", shisetsuJouhou);
        } catch (Exception e) {
            ukg03016Data.put("rstMsg",Util.getServerMsg("ERRS002", "施設データ取得"));
        }
        return "ajaxProcess";
    }
    
}