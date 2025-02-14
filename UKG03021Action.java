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
import jp.co.token.uketuke.service.impl.UKG03021ServiceImpl;

/**
 * <pre>
 * [機 能] 周辺施設表示
 * [説 明] 周辺施設で表示する
 * @author [作 成]
 * </pre>
 */
public class UKG03021Action extends BaseAction {

    /**
     * クラスのシリアル化ID
     */
    private static final long serialVersionUID = -5458276230349927909L;

    /**
     *  ログオブジェクト.
     */
    protected Log log = LogFactory.getLog(UKG03021Action.class);

    private UKG03021ServiceImpl UKG03021Services;
    
    public UKG03021ServiceImpl getUKG03021Services() {
        return UKG03021Services;
    }

    public void setUKG03021Services(UKG03021ServiceImpl uKG03021Services) {
        UKG03021Services = uKG03021Services;
    }
    /** 周辺施設情報画面用データ */
    public Map<String, Object> ukg03021Data = new HashMap<String, Object>();
    public Map<String, Object> getUkg03021Data() {
          return ukg03021Data;
     }

     /**
     * 周辺施設情報画面用データ
     *
     * @param ukg01013Data
     */
    public void setUkg03021Data(Map<String, Object> ukg03021Data) {

        this.ukg03021Data = ukg03021Data;
    }
    /** <pre>
     * [説 明] 施設一覧を取得する
     * @return  施設一覧
     * @throws Exception
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public String shisetuIchiranData() throws Exception{
        try {
            HttpServletRequest request = getRequest();
            String eki_bunrui_s_cd = request.getParameter("eki_bunrui_s_cd");
            String bus_bunrui_s_cd = request.getParameter("bus_bunrui_s_cd");
            String sotota_bunrui_s_cd = request.getParameter("sotota_bunrui_s_cd");
            String param_maxx = request.getParameter("maxx");
            String param_maxy = request.getParameter("maxy");
            String param_minx = request.getParameter("minx");
            String param_miny = request.getParameter("miny");
            String param_shisetu_i_do = request.getParameter("param_shisetu_i_do");
            String param_shisetu_kei_do = request.getParameter("param_shisetu_kei_do");
            Map<String, Object> shisetuIchiranData = new HashMap<String, Object>();
            shisetuIchiranData = UKG03021Services.getShisetuIchiranData(eki_bunrui_s_cd,
                                                            bus_bunrui_s_cd,
                                                            sotota_bunrui_s_cd,
                                                            param_maxx,
                                                            param_maxy,
                                                            param_minx,
                                                            param_miny,
                                                            param_shisetu_i_do,
                                                            param_shisetu_kei_do);
            List<Map<String, String>> shisetuList = (List<Map<String, String>> ) shisetuIchiranData.get("shisetu_ichiran");
            List<Map<String, Object>> shisetsuJouhouList = new ArrayList<Map<String,Object>>();
            Map<String, Object> shisetsuJouhou = null;
            if (shisetuList != null && shisetuList.size() != 0) {
                for(Map<String, String> shisetu : shisetuList) {
                    shisetsuJouhou = new HashMap<String, Object>();
                    shisetsuJouhou.put("data_cd", shisetu.get("DATA_CD"));
                    shisetsuJouhou.put("IMG_KBN", shisetu.get("IMG_KBN"));
                    shisetsuJouhou.put("I_DO", shisetu.get("I_DO"));
                    shisetsuJouhou.put("KEI_DO", shisetu.get("KEI_DO"));
                    shisetsuJouhou.put("BUNRUI_S_CD", shisetu.get("BUNRUI_S_CD"));
                    shisetsuJouhouList.add(shisetsuJouhou);
                }
            }
            ukg03021Data.put("shisetuIchiranData", shisetsuJouhouList);
        } catch (Exception e) {
            ukg03021Data.put("rstMsg",Util.getServerMsg("ERRS002", "施設データリスト取得"));
        }
        return "ajaxProcess";
    }
    /**
     * <pre>
     * [説 明]施設データを取得
     * @return こだわリ条件データ
     * </pre>
     */
    public String shisetuDetail() throws Exception {
    	try {
        HttpServletRequest request = getRequest();

        Map<String, Object> shisetsuScope = new HashMap<String, Object>();
        //施設CD
        shisetsuScope.put("data_cd", request.getParameter("data_cd"));
        //システムID（"pccrm"固定）
        shisetsuScope.put("system_id", Consts.IMAGE_SYSTEM_ID);
        String shisetsuApiUrl = PropertiesReader.getIntance().getProperty(Consts.SHISETSU_API_JOUHOU_URL);
        String apiResult = Util.callApiByURL(shisetsuApiUrl, shisetsuScope);
            
        Map<String, Object>  resultMap = Util.formatJsonToMap(apiResult);
            if(resultMap.size() > 0) {
        resultMap.put("bunruiName", UKG03021Services.getCommonDate(Consts.SHISETSU_BUNNRUI, request.getParameter("bunrui_kbn")).get("KOUMOKU_SB_NAME"));
                ukg03021Data.put("shisetuData", resultMap);
            } else {
                ukg03021Data.put("rstMsg",Util.getServerMsg("ERRS002", "施設データ取得"));
            }
        
    	} catch (Exception e) {
    		ukg03021Data.put("rstMsg",Util.getServerMsg("ERRS002", "施設データ取得"));
        }
        return "ajaxProcess";
    }
}