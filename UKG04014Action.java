/**
 * @システム名: チラシ作成(物件周辺地図用)
 * @ファイル名: UKG04014Action.java
 * @更新日付：: 2018/08/14
 * @Copyright: 2018 token corporation All right reserved
 * 更新履歴：  2019/01/06  劉恆毅 C44033-1_ホームメイトリサーチと施設情報の連携
 */
package jp.co.token.uketuke.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.IconBean;
import jp.co.token.uketuke.formbean.MapBoundBox;
import jp.co.token.uketuke.service.IUKG04014Service;
import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] チラシ作成(物件周辺地図用)
 * [説 明] チラシ作成(物件周辺地図用)アクション。
 * @author [作 成] 2018/08/14 王一氷(SYS)
 * </pre>
 */
public class UKG04014Action extends BaseAction {

    /** クラスのシリアル化ID */
    private static final long serialVersionUID = -5629458236532919212L;

    /** 地図で表示する画面用データ */
    private Map<String, Object> ukg04014Data = null;

    /** チラシ作成(地図検索)サービス */
    private IUKG04014Service ukg04014Services;

    /**
     * 地図から検索画面用データ
     *
     * @return ukg04014Data
     */
    public Map<String, Object> getUkg04014Data() {
        return ukg04014Data;
    }

    /**
     * 地図から検索画面用データ
     *
     * @param ukg04014Data
     */
    public void setUkg04014Data(Map<String, Object> ukg04014Data) {
        this.ukg04014Data = ukg04014Data;
    }

    /**
     * <pre>
     * [説 明] サービス対象を設定する。
     * @param ukg04014Services サービス対象
     * </pre>
     */
    public void setUKG04014Services(IUKG04014Service ukg04014Services) {

        this.ukg04014Services = ukg04014Services;
    }

    /**
     * <pre>
     * [説 明] 初期検索を行う。
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    @SuppressWarnings("unchecked")
    @Override
    public String execute() throws Exception {

        // 同期フラグ設定
        setAsyncFlg(true);
        Map<String, Object> loginInfo = getLoginInfo();
        ukg04014Data = new HashMap<String, Object>();

        // 会社コードを取得
        String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
        String postCd = String.valueOf(loginInfo.get(Consts.POST_CD));
        // 緯度経度の取得
        List<Map<String, Object>> postionList = (List<Map<String, Object>>) ukg04014Services.searchCenterPosition(kaisyaCd, postCd).get("rst_position_list");
        ukg04014Data.put("centerIdo", postionList.get(0).get("I_DO"));
        ukg04014Data.put("centerKeido", postionList.get(0).get("KEI_DO"));

        Map<String, Object> initData = ukg04014Services.getInit();

        ukg04014Data.put("syuuhenn_list", initData.get("rst_syuuhenn_list"));
        ukg04014Data.put("rossen_list", initData.get("rst_rossen_list"));

        Map<String, Object> bukkennkubunnData = ukg04014Services.getBukkennkubunn();
        ukg04014Data.put("bukkennkubunn_list", bukkennkubunnData.get("rst_bukkennkubunn_list"));

        Map<String, Object> kyojuuyouData = ukg04014Services.getKyojuuyou();
        ukg04014Data.put("kyojuuyou_list", kyojuuyouData.get("rst_kyojuuyou_list"));

        Map<String, Object> jigyouyouData = ukg04014Services.getJigyouyou();
        ukg04014Data.put("jigyouyou_list", jigyouyouData.get("rst_jigyouyou_list"));

        // 都道府県コードがログイン情報.店舗所在都道府県コードを設定する
        String jusyo_cd = (String) loginInfo.get(Consts.POST_JUSYO_CD);
        if("01".equals(jusyo_cd)){
            ukg04014Data.put("jusyo_cd", "01a");
        }else{
            ukg04014Data.put("jusyo_cd", jusyo_cd);
        }
        // 都道府県リストを取得する
        List<Map<String, Object>> JusyoDataList = (List<Map<String, Object>>) ukg04014Services.getJusyoData("081");
        ukg04014Data.put("JUSYO_LIST", JusyoDataList);

        // メプ定数を取得する
        ukg04014Data.put("MAP_SERVLETURL",PropertiesReader.getIntance().getProperty(Consts.MAP_SERVLETURL));
        ukg04014Data.put("MAP_KEY",PropertiesReader.getIntance().getProperty(Consts.MAP_KEY));
        ukg04014Data.put("MAP_VERSION",PropertiesReader.getIntance().getProperty(Consts.MAP_VERSION));
        ukg04014Data.put("MAP_INITSCALE",PropertiesReader.getIntance().getProperty(Consts.UKG04014_MAP_INITSCALE));
        ukg04014Data.put("MAP_MINSCALE",PropertiesReader.getIntance().getProperty(Consts.UKG04014_MAP_MINSCALE));

        return successForward();
    }

    /**
     * <pre>
     * [説 明] 対象エリアの空室物件を検索を行う。
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public String kuusituBukken() throws Exception {

        HttpServletRequest request = ServletActionContext.getRequest();
        Map<String, Object> loginInfo = getLoginInfo();
        ukg04014Data = new HashMap<String, Object>();

        // 会社コードを取得
        String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("param_kaisyaCd", kaisyaCd);
        paramMap.put("param_maxx", request.getParameter("maxx"));
        paramMap.put("param_maxy", request.getParameter("maxy"));
        paramMap.put("param_minx", request.getParameter("minx"));
        paramMap.put("param_miny", request.getParameter("miny"));
        paramMap.put("param_jigyouyou", request.getParameter("jigyouyou"));
        paramMap.put("param_kyojuuyou", request.getParameter("kyojuuyou"));
        paramMap.put("param_nyuKuuKb", request.getParameter("nyu_kuu_kb"));

        Map<String, Object> madoriData = ukg04014Services.getMadori(request.getParameter("kyojuuyou"));
        ukg04014Data.put("madoriData", madoriData.get("rst_madori"));

        // 物件情報検索処理(A004)
        Map<String, Object> resul = ukg04014Services.getBukkenInfo(paramMap);
        List<Map<String, Object>> bukkenList = (List<Map<String, Object>>) resul.get("rst_bukkensit_list");

        String[] searchedBukkenList;
        Map<String, Object> selected = new HashMap<String, Object>();
        Map<String, Object> searchedBukken = new HashMap<String, Object>();
        // 選択した物件情報取得処理
        String bukenListString = request.getParameter("bukkenSelectedList");
        if(bukenListString != "" && bukenListString.length() > 0){
            paramMap = new HashMap<String, Object>();
            String[] hayaList;
            boolean addFlag = true;
            searchedBukkenList = bukenListString.split(",");
            for (int i=0; i<searchedBukkenList.length; i++) {
                hayaList = searchedBukkenList[i].split(":");
                // 選択した物件の物件No
                paramMap.put("param_bukkenno", hayaList[0]);
                // 選択した物件の物件No
                paramMap.put("param_heyano", hayaList[1]);
                addFlag = true;
                for(int k=0; k<bukkenList.size(); k++){
                    searchedBukken = bukkenList.get(k);
                    // 検索した物件リステに選択した物件を含むの場合、追加しない
                    if(searchedBukken.get("HOMEMATE_BUKKEN_NO").equals(hayaList[0]) &&
                            searchedBukken.get("HEYA_NO").equals(hayaList[1])){
                        addFlag = false;
                    }
                }
                if(addFlag){
                    // 検索した物件リステに選択した物件を含むの場合、追加する
                    selected = ukg04014Services.getSelectBukkenInfo(paramMap);
                    bukkenList.addAll((List<Map<String, Object>>) selected.get("out_bukenInfo"));
                }
            }
            // 取得した物件リストをソートする
            Map<String, Object> tempObject = new HashMap<String, Object>();
            Integer bukenNo;
            for(int i=0; i<bukkenList.size(); i++){
                bukenNo = Integer.valueOf((String)bukkenList.get(i).get("HOMEMATE_BUKKEN_NO"));
                for(int j=0; j<bukkenList.size(); j++){
                    if( bukenNo > Integer.valueOf((String)bukkenList.get(j).get("HOMEMATE_BUKKEN_NO")) ){
                        tempObject = bukkenList.get(i);
                        bukkenList.set(i, bukkenList.get(j));
                        bukkenList.set(j, tempObject);
                    }
                }
            }
        }

        ukg04014Data.put("bukken",bukkenList);

        return "ajaxProcess";
    }

    /**
     * 地図アイコンクリック時処理
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public String fetchBukenInfo() throws Exception{
        HttpServletRequest request = ServletActionContext.getRequest();
        ukg04014Data = new HashMap<String, Object>();
        String bukkenNo = request.getParameter("bukkenNo");
        String heyaNo = request.getParameter("heyaNo");

        // 物件データを取得する
        Map<String, Object> bukenInfoData = null;
        List<Map<String, Object>> bukenInfoMap = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> out_bukenInfo = new ArrayList<Map<String, Object>>();
        String[] heyaNoList = heyaNo.split(",");
        for(int i = 0; i < heyaNoList.length; i++){
            bukenInfoData = ukg04014Services.popBukenInfo(bukkenNo,heyaNoList[i]);
            bukenInfoMap = (List<Map<String, Object>>) bukenInfoData.get("out_bukenInfo");
            out_bukenInfo.addAll(bukenInfoMap);
        }

        // 設定ファイルからイメージPathを取得する
        for (Map<String, Object> bukenInfo : out_bukenInfo) {
            bukenInfo.put("GK_IMAGE_PATH", "imgIO.imgIO?type=1&path="+bukenInfo.get("GK_IMAGE_PATH"));
            bukenInfo.put("NK_IMAGE_PATH", Util.wmfToSVG((String)bukenInfo.get("NK_IMAGE_PATH"),82f,61f));
        }
        ukg04014Data.put("out_bukenInfo", out_bukenInfo);
        ukg04014Data.put("sysdate", Util.getSysDate());
        if (out_bukenInfo == null || out_bukenInfo.size() == 0) {
            ukg04014Data.put("rst_msg", Util.getServerMsg("ERRS006", "空室物件"));
        }
        return "ajaxProcess";
    }

    /**
     * <pre>
     * [説 明] 駅情報を取得する
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    public String ekiProcess() throws Exception {
        ukg04014Data = new HashMap<String, Object>();
        MapBoundBox boundBox = new MapBoundBox();
        HttpServletRequest request = ServletActionContext.getRequest();
        boundBox.setMaxx(request.getParameter("maxx"));
        boundBox.setMaxy(request.getParameter("maxy"));
        boundBox.setMinx(request.getParameter("minx"));
        boundBox.setMiny(request.getParameter("miny"));
        /*2019/12/27 劉恆毅 UPD Start C44033-1_ホームメイトリサーチと施設情報の連携*/
        /*List<IconBean> iconList = ukg04014Services.getEki(boundBox);*/
        String ekiCode = request.getParameter("ekiStrCode");
        List<IconBean> iconList = ukg04014Services.getEki(boundBox, ekiCode);
        /*2019/12/27 劉恆毅 UPD End C44033-1_ホームメイトリサーチと施設情報の連携*/
        ukg04014Data.put("ICON_LIST", iconList);
        return "ajaxProcess";
    }

    /**
     * <pre>
     * [説 明] 範囲の駅を検索する
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    public String busstopProcess() throws Exception {
        ukg04014Data = new HashMap<String, Object>();
        HttpServletRequest request = ServletActionContext.getRequest();
        MapBoundBox boundBox = new MapBoundBox();
        boundBox.setMaxx(request.getParameter("maxx"));
        boundBox.setMaxy(request.getParameter("maxy"));
        boundBox.setMinx(request.getParameter("minx"));
        boundBox.setMiny(request.getParameter("miny"));
        ukg04014Data.put("EKI_STOP", ukg04014Services.getEkiStop(boundBox, request.getParameter("I_DO"),request.getParameter("KEI_DO")));
        return "ajaxProcess";
    }

    /*2020/01/02 劉恆毅 DEL Start C44033-1_ホームメイトリサーチと施設情報の連携*/
    /**
     * <pre>
     * [説 明] 病院情報を取得する
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    /*public String hospitalProcess() throws Exception {
        ukg04014Data = new HashMap<String, Object>();
        HttpServletRequest request = ServletActionContext.getRequest();
        MapBoundBox boundBox = new MapBoundBox();
        boundBox.setMaxx(request.getParameter("maxx"));
        boundBox.setMaxy(request.getParameter("maxy"));
        boundBox.setMinx(request.getParameter("minx"));
        boundBox.setMiny(request.getParameter("miny"));
        ukg04014Data.put("HOSPITAL_LIST", ukg04014Services.getHospital(boundBox));
        return "ajaxProcess";
    }*/
    /*2020/01/02 劉恆毅 DEL End C44033-1_ホームメイトリサーチと施設情報の連携*/
    /*2020/01/02 劉恆毅 ADD Start C44033-1_ホームメイトリサーチと施設情報の連携*/
    /**
     * <pre>
     * [説 明] 駅とバス以外の施設情報を取得する
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    public String sonotaProcess() throws Exception {
        ukg04014Data = new HashMap<String, Object>();
        HttpServletRequest request = ServletActionContext.getRequest();
        MapBoundBox boundBox = new MapBoundBox();
        boundBox.setMaxx(request.getParameter("maxx"));
        boundBox.setMaxy(request.getParameter("maxy"));
        boundBox.setMinx(request.getParameter("minx"));
        boundBox.setMiny(request.getParameter("miny"));
        String shisetuCode = request.getParameter("sonotaStrCode");
        ukg04014Data.put("SONOTA_SHISETU_LIST", ukg04014Services.getSonotaShisetu(boundBox, shisetuCode));
        return "ajaxProcess";
    }
    /*2020/01/02 劉恆毅 ADD End C44033-1_ホームメイトリサーチと施設情報の連携*/

    /**
     * <pre>
     * [説 明] 「選択した物件や施設が地図の表示内に収まる」できるかどうかの判断
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    public String bukkenPositionDecide() throws Exception {

        ukg04014Data = new HashMap<String, Object>();
        MapBoundBox boundBox = new MapBoundBox();
        HttpServletRequest request = ServletActionContext.getRequest();
        boundBox.setMaxx(request.getParameter("maxx"));
        boundBox.setMaxy(request.getParameter("maxy"));
        boundBox.setMinx(request.getParameter("minx"));
        boundBox.setMiny(request.getParameter("miny"));
        String bukkenList = request.getParameter("bukkenNo");
        ukg04014Data.put("bukkenFlg", ukg04014Services.bukkenPositionDecide(boundBox,bukkenList,request.getParameter("KITENN_IDO"),request.getParameter("KITENN_KEIDO")));
        return "ajaxProcess";
    }
}
