/**
 * @システム名: 受付システム
 * @ファイル名: UKG03013Action.java
 * @更新日付：: 2018/01/10
 * @Copyright: 2018 token corporation All right reserved
 * 更新履歴：2018/01/10 張陽陽(SYS)        1.0        案件C42036デザイン変更新規作成
 * 変更履歴: 2019/07/03	SYS_程旋    	    1.1        C43100_お客様項目の入力簡素化対応
 * 更新履歴：  2019/12/04 劉恆毅(SYS)       1.2        C44033-1_ホームメイトリサーチと施設情報の連携
 */
package jp.co.token.uketuke.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.exception.DataNotFoundException;
import jp.co.token.uketuke.service.IUKG99003Service;

/**
 * <pre>
 * [機 能] 物件情報(地図タブ)で表示する
 * [説 明] 物件情報(地図タブ)で表示する
 * @author [作 成]
 * </pre>
 */
public class UKG03013Action extends BaseAction {

    /**
     * クラスのシリアル化ID
     */
    private static final long serialVersionUID = -5458276230349927909L;

    /** ログオブジェクト. */
	protected Log log = LogFactory.getLog(UKG03013Action.class);

    /** 地図情報画面用データ */
    private Map<String, Object> ukg03013Data;

    /** 顧客検索サービス */
    private IUKG99003Service UKG99003Services;

    /** 自動車 */
    private final static String TOHOROOTTIME = PropertiesReader.getIntance().getProperty(Consts.TOHO_ROOT_TIME);

    /** 徒歩 */
    private final static String AUTOROOTTIME = PropertiesReader.getIntance().getProperty(Consts.AUTO_ROOT_TIME);

    /**
     * 地図情報画面用データ
     *
     * @return ukg01013Data
     */
    public Map<String, Object> getUkg03013Data() {

        return ukg03013Data;
    }

    /**
     * 地図情報画面用データ
     *
     * @param ukg01013Data
     */
    public void setUkg03013Data(Map<String, Object> ukg03013Data) {

        this.ukg03013Data = ukg03013Data;
    }

    /**
     * 顧客検索サービス
     *
     * @param services
     */
    public void setUKG99003Services(IUKG99003Service services) {

        UKG99003Services = services;
    }

    /**
     * <pre>
     * [説 明] 地図情報で表示する。
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    @SuppressWarnings("unchecked")
    @Override
    public String execute() throws Exception {

    	log.debug("UKG03013Actionを呼び出し 開始");

        // 同期フラグ設定
        setAsyncFlg(true);
        ukg03013Data = new HashMap<String, Object>();
        HttpServletRequest request = ServletActionContext.getRequest();
        String bukken_no = request.getParameter("bukken_no");
//      2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
        String heya_no = request.getParameter("heya_no");
        Map<String, Object> initData = UKG99003Services.getInit(bukken_no,heya_no);
//      2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
        List<Map<String, String>> kuusitu_bukken = (List<Map<String, String>>) initData.get("rst_kuusitu_bukken");
        if (kuusitu_bukken != null && kuusitu_bukken.size() > 0) {
            // 周辺施設一覧データ取得
            Map<String, String> bukkeninfo = kuusitu_bukken.get(0);
            bukkeninfo.put("SYUHEN_IMAGE1_KYORI", convert(bukkeninfo.get("SYUHEN_IMAGE1_KYORI")));
            bukkeninfo.put("SYUHEN_IMAGE2_KYORI", convert(bukkeninfo.get("SYUHEN_IMAGE2_KYORI")));
            bukkeninfo.put("SYUHEN_IMAGE3_KYORI", convert(bukkeninfo.get("SYUHEN_IMAGE3_KYORI")));
            bukkeninfo.put("SYUHEN_IMAGE4_KYORI", convert(bukkeninfo.get("SYUHEN_IMAGE4_KYORI")));
            bukkeninfo.put("SYUHEN_IMAGE5_KYORI", convert(bukkeninfo.get("SYUHEN_IMAGE5_KYORI")));
            ukg03013Data.put("kuusitu_bukken", bukkeninfo);
        } else {
            throw new DataNotFoundException();
        }

        ukg03013Data.put("syuuhenn_list", initData.get("rst_syuuhenn_list"));
        ukg03013Data.put("rossen_list", initData.get("rst_rossen_list"));
        // 地図縮尺度
        String initScale = PropertiesReader.getIntance().getProperty(Consts.UKG99003_MAP_INITSCALE);
        String minScale = PropertiesReader.getIntance().getProperty(Consts.UKG99003_MAP_MINSCALE);
        ukg03013Data.put("INITSCALE", initScale);
        ukg03013Data.put("MINSCALE", minScale);
        ukg03013Data.put("MAP_SERVLETURL", PropertiesReader.getIntance().getProperty(Consts.MAP_SERVLETURL));
        ukg03013Data.put("MAP_KEY", PropertiesReader.getIntance().getProperty(Consts.MAP_KEY));
        ukg03013Data.put("MAP_VERSION", PropertiesReader.getIntance().getProperty(Consts.MAP_VERSION));
        // 自動車
        ukg03013Data.put("TOHOROOTTIME", TOHOROOTTIME);
        // put
        ukg03013Data.put("AUTOROOTTIME", AUTOROOTTIME);

        // 2018/10/25 ADD Start SYS_肖剣生 (案件番号C42036)不具合対応No79 Googleマップ対応
        /**航空写真のURL*/
        ukg03013Data.put("aerialURL", PropertiesReader.getIntance().getProperty(Consts.aerialURL));
        /**ストリートビューのURL*/
        ukg03013Data.put("streetviewURL", PropertiesReader.getIntance().getProperty(Consts.streetviewURL));
        /**Googleマップ有効・無効*/
        ukg03013Data.put("googleMaps", PropertiesReader.getIntance().getProperty(Consts.googleMaps));
        /**Googleマップウィンドウ幅*/
        ukg03013Data.put("googleMapsWindow_w", PropertiesReader.getIntance().getProperty(Consts.googleMapsWindow_w));
        /**Googleマップウィンドウ高*/
        ukg03013Data.put("googleMapsWindow_h", PropertiesReader.getIntance().getProperty(Consts.googleMapsWindow_h));
        // 2018/10/25 ADD End SYS_肖剣生 (案件番号C42036)不具合対応No79 Googleマップ対応

        log.debug("UKG03013Actionを呼び出し 終了");

        return successForward();
    }
    /* 2020/01/10 DEL START 陳龍龍(SYS) C44033-1_ホームメイトリサーチと施設情報の連携*/
    /**
     * <pre>
     * [説 明] 駅情報を取得する
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    /*public String ekiProcess() throws Exception {
        ukg03013Data = new HashMap<String, Object>();
        MapBoundBox boundBox = new MapBoundBox();
        HttpServletRequest request = ServletActionContext.getRequest();
        boundBox.setMaxx(request.getParameter("maxx"));
        boundBox.setMaxy(request.getParameter("maxy"));
        boundBox.setMinx(request.getParameter("minx"));
        boundBox.setMiny(request.getParameter("miny"));
        List<IconBean> iconList = UKG99003Services.getEki(boundBox);
        ukg03013Data.put("ICON_LIST", iconList);
        return "ajaxProcess";
    }

    *//**
     * <pre>
     * [説 明] 範囲の駅を検索する
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     *//*
    public String busstopProcess() throws Exception {
        ukg03013Data = new HashMap<String, Object>();
        HttpServletRequest request = ServletActionContext.getRequest();
        MapBoundBox boundBox = new MapBoundBox();
        boundBox.setMaxx(request.getParameter("maxx"));
        boundBox.setMaxy(request.getParameter("maxy"));
        boundBox.setMinx(request.getParameter("minx"));
        boundBox.setMiny(request.getParameter("miny"));
        ukg03013Data.put("EKI_STOP", UKG99003Services.getEkiStop(boundBox, request.getParameter("categoryIdList")));
        return "ajaxProcess";
    }

    *//**
     * <pre>
     * [説 明] 病院情報を取得する
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     *//*
    public String hospitalProcess() throws Exception {
        ukg03013Data = new HashMap<String, Object>();
        HttpServletRequest request = ServletActionContext.getRequest();
        MapBoundBox boundBox = new MapBoundBox();
        boundBox.setMaxx(request.getParameter("maxx"));
        boundBox.setMaxy(request.getParameter("maxy"));
        boundBox.setMinx(request.getParameter("minx"));
        boundBox.setMiny(request.getParameter("miny"));
        ukg03013Data.put("HOSPITAL_LIST", UKG99003Services.getHospital(boundBox));
        return "ajaxProcess";
    }*/
    /*2020/01/10 DEL END 陳龍龍(SYS) C44033-1_ホームメイトリサーチと施設情報の連携*/

    /**
     * <pre>
     * [説 明] 値を転変
     * @param value 値
     * @return 範囲の駅データ
     * </pre>
     */
    private String convert(String value) throws Exception {

        if (Util.isEmpty(value)) {
            return "";
        }
        BigDecimal decimalvalue = new BigDecimal(value);
        BigDecimal decimalTemporary = new BigDecimal(Double.valueOf(1000f));
        if (decimalvalue.compareTo(decimalTemporary) > 0) {
            BigDecimal decimalTemporary1 = new BigDecimal(Double.valueOf(100));
            BigDecimal decimalTemporary2 = new BigDecimal(Double.valueOf(10));
            return String.format("%.1f", (new BigDecimal(Double.valueOf(Math.ceil(decimalvalue.divide(decimalTemporary1).intValue()))).divide(decimalTemporary2)))
                + "km";
        }
        return value + "m";
    }
}
