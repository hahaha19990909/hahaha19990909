/**
 * @システム名: 受付システム
 * @ファイル名: UKG03013Action.java
 * @更新日付：: 2018/01/10
 * @Copyright: 2018 token corporation All right reserved
 * 更新履歴：2018/02/09 張陽陽(SYS)        1.0        案件C42036デザイン変更新規作成
 * 更新履歴：2019/06/27 郝年昇(SYS)        1.1        案件C43100お客様項目の入力簡素化対応
 */
package jp.co.token.uketuke.action;

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
import jp.co.token.uketuke.formbean.KibouJyoukennBean;
import jp.co.token.uketuke.formbean.SchoolFacilityBusBean;
import jp.co.token.uketuke.service.IBukkenSearchService;
import jp.co.token.uketuke.service.IUKG02036Service;

/**
 * <pre>
 * [機 能] バス停の設定
 * [説 明] バス停を設定する。
 * @author [作 成] 2018/02/09 張陽陽(SYS)
 * </pre>
 */
public class UKG02037Action extends BaseAction {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -1405375042398666652L;

    /** ログオブジェクト. */
    protected Log log = LogFactory.getLog(UKG02037Action.class);

    /** 希望路線の設定画面用データ */
    private Map<String, Object> ukg02037Data = null;

    /** 希望路線の設定サービス */
    private IUKG02036Service UKG02036Services;

    /** 物件検索サービス */
    private IBukkenSearchService bukkenSearchService;

    /**
     * <pre>
     * [説 明] 画面初期表示
     * @return アクション処理結果
     * @throws Exception 処理異常
     * </pre>
     */
    @Override
    public String execute() throws Exception {
        log.info("UKG02037Actionを呼び出し 開始");

        // ログイン情報
        Map<String, Object> uketukePostInfo = getUketukePostInfo();

        ukg02037Data = new HashMap<String, Object>();

        String jusyo_cd = Consts.SPACE;
        String strInitDataFlg = Consts.SPACE;
        String jusyo2 = Consts.SPACE;

        // 都道府県リストを取得する
        ukg02037Data.put("JUSYO_LIST", UKG02036Services.getJusyoData());

        HttpServletRequest request = ServletActionContext.getRequest();
        KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);
        String maxCount = PropertiesReader.getIntance().getProperty(Consts.UKG02036_MAXKENSAKUSUU);

        if (kibouJyoukennBean != null) {
            SchoolFacilityBusBean schoolFacilityBusBean = kibouJyoukennBean.getSchoolFacilityBusBean();
            if ("6".equals(kibouJyoukennBean.getKibouJyoukennCode())) {
                strInitDataFlg = "1";
                jusyo_cd = schoolFacilityBusBean.getJisCode().substring(0, 2);
                jusyo2 = schoolFacilityBusBean.getSiCode();
                ukg02037Data.put("SchoolFacilityBusBean", schoolFacilityBusBean);
                ukg02037Data.put("selSisetu", Util.toEmpty(schoolFacilityBusBean.getSetubiTypeCode()));
            } else {
                strInitDataFlg = "0";
                jusyo_cd = (String) uketukePostInfo.get(Consts.POST_JUSYO_CD);
                jusyo2 = (String) request.getSession().getAttribute("SHIKUGUN_CD");
                if (!Util.isEmpty(kibouJyoukennBean.getPrefectureCd())) {
                    jusyo_cd = kibouJyoukennBean.getPrefectureCd();
                }else {
                    jusyo2 = (String) uketukePostInfo.get(Consts.POST_SHIKUGUN_CD);
                }
            }
        }

        ukg02037Data.put("INITDATAFLG", strInitDataFlg);

        // 店舗所在都道府県コード
        ukg02037Data.put("POST_JUSYO_CD", jusyo_cd);

        // 市区町村リストを取得する
        ukg02037Data.put("JUSYO2DATA", UKG02036Services.getJusyo2Data(jusyo_cd));
        ukg02037Data.put("POSTSHIKUGUNCD", jusyo2);

        // 駅選択最大数
        ukg02037Data.put("MAXCOUNT", maxCount);

        log.info("UKG02037Actionを呼び出し 終了");

        return successForward();
    }

    /**
     * <pre>
     * [説 明] 市区郡データ取得
     * @return アクション処理結果
     * @throws Exception 処理異常
     * </pre>
     */
    public String searchShikukohriData() throws Exception {
        log.info("UKG02037Action_searchShikukohriDataを呼び出し 開始");

        HttpServletRequest request = ServletActionContext.getRequest();
        // 県コード
        String jusyo1 = request.getParameter("jusyo1");

        ukg02037Data = new HashMap<String, Object>();
        // 都道府県コードより、市区郡リストを取得する。
        if (!Util.isEmpty(jusyo1)) {
            ukg02037Data.put("JUSYO2DATA", UKG02036Services.getJusyo2Data(jusyo1));
        } else {
            ukg02037Data.put("JUSYO2DATA", null);
        }

        log.info("UKG02037Action_searchShikukohriDataを呼び出し 終了");

        return "ajax";
    }

    /**
     * <pre>
     * [説 明] バス会社リストを取得する
     * @return アクション処理結果
     * @throws Exception 処理異常
     * </pre>
     */
    public String searchBusKaisyaList() throws Exception {
        log.info("UKG02037Action_searchBusKaisyaListを呼び出し 開始");

        ukg02037Data = new HashMap<String, Object>();
        HttpServletRequest request = ServletActionContext.getRequest();
        // 施設分類1情報
        String jyusyo = request.getParameter("jusyo");
        String cityCd = request.getParameter("cityCd");

        // バス会社リストを取得する。
        ukg02037Data.put("BUSKAISYALIST", UKG02036Services.getBusKaisyaList(jyusyo, cityCd));

        log.info("UKG02037Action_searchBusKaisyaListを呼び出し 終了");

        return "ajax";
    }

    /**
     * <pre>
     * [説 明] バス路線リスト生成する
     * @return アクション処理結果
     * @throws Exception 処理異常
     * </pre>
     */
    public String searchBusRosenList() throws Exception {
        log.info("UKG02037Action_searchBusRosenListを呼び出し 開始");

        ukg02037Data = new HashMap<String, Object>();
        HttpServletRequest request = ServletActionContext.getRequest();
        String jyusyo = request.getParameter("jyusyo");
        String cityCd = request.getParameter("cityCd");
        String busKaisya = request.getParameter("busKaisya");

        // バス路線リスト生成する。
        ukg02037Data.put("BUSROSENLIST", UKG02036Services.getBusRosenList(jyusyo + cityCd, busKaisya));

        log.info("UKG02037Action_searchBusRosenListを呼び出し 終了");

        return "ajax";
    }

    /**
     * <pre>
     * [説 明] バス停データを検索
     * @return アクション処理結果
     * @throws Exception 処理異常
     * </pre>
     */
    public String searchShisetsuBusList() throws Exception {
        log.info("UKG02037Action_searchShisetsuBusListを呼び出し 開始");

        ukg02037Data = new HashMap<String, Object>();
        HttpServletRequest request = ServletActionContext.getRequest();
        String busKaisyaCode = request.getParameter("busKaisyaCode");
        String busCategoryCode = request.getParameter("busCategoryCode");
        String maxcount = request.getParameter("maxcount");

        // 施設バスマスタ検索
        ukg02037Data.put("BUSROSENLIST", UKG02036Services.getShisetsuBusList(busKaisyaCode, busCategoryCode, maxcount));

        log.info("UKG02037Action_searchShisetsuBusListを呼び出し 終了");

        return "ajax";
    }

    /**
     * <pre>
     * [説 明] ukg02037Data作成
     * @return アクション処理結果
     * @throws Exception 処理異常
     * </pre>
     */
    public String toSession02037Data() throws Exception {
        log.info("UKG02037Action_toSession02037Dataを呼び出し 開始");

        ukg02037Data = new HashMap<String, Object>();
        HttpServletRequest request = ServletActionContext.getRequest();
        KibouJyoukennBean kibouJyoukenn = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);

        KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) kibouJyoukenn.clone();
        SchoolFacilityBusBean schoolFacilityBusBean = new SchoolFacilityBusBean();

        // 希望条件指定コード
        // 不具合対応　No16　START
        //kibouJyoukennBean.setKibouJyoukennCode("5");
        kibouJyoukennBean.setKibouJyoukennCode("6");
        // 不具合対応　No16　END
        kibouJyoukennBean.setSchoolFacilityBusBean(schoolFacilityBusBean);

        schoolFacilityBusBean.setSetubiName(request.getParameter("setubiName"));
        schoolFacilityBusBean.setSetubiKb(request.getParameter("setubiKb"));
        schoolFacilityBusBean.setSetubiTypeCode(request.getParameter("sisetutype"));
        schoolFacilityBusBean.setJisCode(request.getParameter("jisCode"));
        schoolFacilityBusBean.setSetubiIdo(request.getParameter("setubiIdo"));
        schoolFacilityBusBean.setSetubiKeido(request.getParameter("setubiKeido"));
        schoolFacilityBusBean.setKibourange(request.getParameter("kibourange"));
        schoolFacilityBusBean.setSiCode(request.getParameter("siCode"));
        schoolFacilityBusBean.setBusKaisyaCode(request.getParameter("busKaisyaCode"));
        schoolFacilityBusBean.setBusCategoryCode(request.getParameter("busCategoryCode"));
        kibouJyoukennBean.setPrefectureCd(request.getParameter("prefectureCd"));

        resultProcess(kibouJyoukennBean, schoolFacilityBusBean);

        log.info("UKG02037Action_toSession02037Dataを呼び出し 終了");

        return "ajax";
    }

    /**
     * <pre>
     * [説 明] 結果処理
     * @throws Exception 処理異常
     * </pre>
     */
    private void resultProcess(KibouJyoukennBean kibouJyoukennBean, SchoolFacilityBusBean schoolFacilityBusBean) throws Exception {
        List<Map<String, Object>> koumoku = UKG02036Services.getKoumoku("044",null);

        for (Map<String, Object> map : koumoku) {
            schoolFacilityBusBean.setKibourange((String) map.get("KOUMOKU_SB_VAL"));
            schoolFacilityBusBean.setKibourangeCode((String) map.get("KOUMOKU_SB_CD"));
            kibouJyoukennBean.setSchoolFacilityBusBean(schoolFacilityBusBean);

            // 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
            // Map<String, Object> resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean);
            Map<String, Object> resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean,Consts.BUKEN_COUNT_All);
            // 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
            
            ukg02037Data.put("errMsg", "");

            if (!"0000000".equals(resul.get("restCd"))) {
                ukg02037Data.put("errMsg", Util.getServerMsg("ERRS002", "検索処理"));
                map.put("HEYACOUNT", "0");
            } else {
                map.put("HEYACOUNT", resul.get("bukkenCount").toString());
            }
        }

        ukg02037Data.put("KIBOURANGELIST", koumoku);
    }

    /**
     * <pre>
     * [説 明] 設定ボタンの処理
     * @return アクション処理結果
     * @throws Exception 処理異常
     * </pre>
     */
    public String save2Session() throws Exception {
        log.info("UKG02037Action_save2Sessionを呼び出し 開始");

        ukg02037Data = new HashMap<String, Object>();
        HttpServletRequest request = ServletActionContext.getRequest();
        KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);

        if (kibouJyoukennBean == null) {
            kibouJyoukennBean = new KibouJyoukennBean();
        }

        String sisetutype = request.getParameter("sisetutype");

        SchoolFacilityBusBean schoolFacilityBusBean = new SchoolFacilityBusBean();
        schoolFacilityBusBean.setSetubiKb(request.getParameter("setubiKb"));
        schoolFacilityBusBean.setJisCode(request.getParameter("jisCode"));
        schoolFacilityBusBean.setSiCode(request.getParameter("siCode"));
        schoolFacilityBusBean.setSetubiTypeCode(sisetutype);
        schoolFacilityBusBean.setSetubiName(request.getParameter("setubiName"));
        schoolFacilityBusBean.setSetubiIdo(request.getParameter("setubiIdo"));
        schoolFacilityBusBean.setSetubiKeido(request.getParameter("setubiKeido"));
        schoolFacilityBusBean.setBusKaisyaCode(request.getParameter("busKaisyaCode"));
        schoolFacilityBusBean.setBusCategoryCode(request.getParameter("busCategoryCode"));
        schoolFacilityBusBean.setKibourangeCode(request.getParameter("kibourangeCode"));

        // 希望条件指定コード
        // 不具合対応　No16　START
        //kibouJyoukennBean.setKibouJyoukennCode("5");
        kibouJyoukennBean.setKibouJyoukennCode("6");
        // 不具合対応　No16　END
        kibouJyoukennBean.setPrefectureCd(request.getParameter("prefectureCd"));
        // 施設までの距離コード
        schoolFacilityBusBean.setKibourange(request.getParameter("kibourange"));
        kibouJyoukennBean.setKiboujyokenDisp(request.getParameter("jyoukenn"));
        kibouJyoukennBean.setSchoolFacilityBusBean(schoolFacilityBusBean);

        request.getSession().setAttribute(Consts.KIBOUJYOUKENBEAN, kibouJyoukennBean);

        log.info("UKG02037Action_save2Sessionを呼び出し 終了");

        return "ajax";
    }

    /**
     * <pre>
     * [説 明] 画面のデータを取得する。
     * @return 画面のデータ
     * </pre>
     */
    public Map<String, Object> getUkg02037Data() {
        return ukg02037Data;
    }

    /**
     * <pre>
     * [説 明] 画面のデータを設定する。
     * @param ukg02037Data 画面のデータ
     * </pre>
     */
    public void setUkg02037Data(Map<String, Object> ukg02037Data) {
        this.ukg02037Data = ukg02037Data;
    }

    /**
     * <pre>
     * [説 明] サービス対象を設定する。
     * @param services サービス対象
     * </pre>
     */
    public void setUKG02036Services(IUKG02036Service uKG02036Services) {
        UKG02036Services = uKG02036Services;
    }

    /**
     * <pre>
     * [説 明] 物件検索サービスを設定する。
     * @param bukkenSearchService 物件検索サービス
     * </pre>
     */
    public void setBukkenSearchService(IBukkenSearchService bukkenSearchService) {
        this.bukkenSearchService = bukkenSearchService;
    }
}
