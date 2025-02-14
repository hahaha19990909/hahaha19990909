/**
 *@システム名: 受付システム
 *@ファイル名: UKG99008Action.java
 *@更新日付：: 2015/09/23
 *@Copyright: 2012 token corporation All right reserved
 * 更新履歴：2020/03/30    楊朋朋(SYS)   (案件番号C43017)見積書改修
 *       ： 2020/05/18     劉恒毅(SYS)   (案件C44063-2)入居対策リストの入居日対応
 */
package jp.co.token.uketuke.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG99005Service;
import jp.co.token.uketuke.service.IUKG99008Service;

/**
 * <pre>
 * [機 能] 申込書印刷で表示する
 * [説 明] 申込書印刷で表示する
 * @author [作 成]
 * </pre>
 */
public class UKG99008Action extends BaseAction {
    /**
     * クラスのシリアル化ID
     */
    private static final long serialVersionUID = -5458276230349927909L;


    /** 申込書印刷画面用データ */
    private Map<String, Object> ukg99008Data = new HashMap<String, Object>();

    /** トップページサービス */
    private IUKG99008Service UKG99008Services;

    /** セッションのログイン情報 */
    private Map<String, Object> sessionLoginInfo = null;

    /** 物件番号 */
    private String bukkenNo;

    /** 部屋番号 */
    private String heyaNo;

    /** 顧客区分 */
    private String kokyaKbn;
    
    //  2020/03/30   楊朋朋(SYS) ADD Start C43017_見積書改修
    
    private IUKG99005Service UKG99005Services;
    
    //  2020/03/30   楊朋朋(SYS) ADD End C43017_見積書改修
    
    /**
     * <pre>
     * [説 明] 物件番号を取得する。
     * @return the 物件番号
     * </pre>
     */
    public String getBukkenNo() {
        return bukkenNo;
    }

    /**
     * <pre>
     * [説 明] 物件番号を設定する。
     * @param bukkenNo 物件番号
     * </pre>
     */
    public void setBukkenNo(String bukkenNo) {
        this.bukkenNo = bukkenNo;
    }

    /**
     * <pre>
     * [説 明] 部屋番号を取得する。
     * @return the 部屋番号
     * </pre>
     */
    public String getHeyaNo() {
        return heyaNo;
    }

    /**
     * <pre>
     * [説 明] 部屋番号を設定する。
     * @param heyaNo 部屋番号
     * </pre>
     *
     * @throws UnsupportedEncodingException
     */
    public void setHeyaNo(String heyaNo) throws UnsupportedEncodingException {
        // 部屋設定は上に移動
        this.heyaNo = heyaNo;
        if (!Util.isEmpty(heyaNo) && "ISO-8859-1".equals(Util.getEncoding(heyaNo))) {
            this.heyaNo = new String(heyaNo.getBytes("ISO-8859-1"), "UTF-8");
        }
    }
    //  2020/03/30   楊朋朋(SYS) ADD Start C43017_見積書改修
    /**
     * @param uKG99005Services the uKG99005Services to set
     */
    public void setUKG99005Services(IUKG99005Service uKG99005Services) {
        UKG99005Services = uKG99005Services;
    }
    //  2020/03/30   楊朋朋(SYS) ADD End C43017_見積書改修
    /**
     * <pre>
     * [説 明] 顧客区分を取得する。
     * @return  顧客区分
     * </pre>
     */
    public String getKokyaKbn() {
        return kokyaKbn;
    }

    /**
     * <pre>
     * [説 明] 顧客区分を設定する。
     * @param kokyaKbn 顧客区分
     * </pre>
     */
    public void setKokyaKbn(String kokyaKbn) {
        this.kokyaKbn = kokyaKbn;
    }

    /**
     * <pre>
     * [説 明]セッション情報を取得する。
     * @return セッション情報
     * </pre>
     */
    public Map<String, Object> getSessionLoginInfo() {
        return sessionLoginInfo;
    }

    /**
     * <pre>
     * [説 明]セッション情報を設定する。
     * @param sessionLoginInfo セッション情報
     * </pre>
     */
    public void setSessionLoginInfo(Map<String, Object> sessionLoginInfo) {
        this.sessionLoginInfo = sessionLoginInfo;
    }

    /**
     * <pre>
     * [説 明]サービスを設定する。
     * @param services サービス
     * </pre>
     */
    public void setUKG99008Services(IUKG99008Service services) {
        UKG99008Services = services;
    }

    /**
     * <pre>
     * [説 明]申込書印刷画面用データ
     * @return ukg99008Data
     * </pre>
     */
    public Map<String, Object> getUkg99008Data() {
        return ukg99008Data;
    }

    /**
     * <pre>
     * [説 明]申込書印刷画面用データ
     * @param ukg99008Data
     * </pre>
     */
    public void setUkg99008Data(Map<String, Object> ukg99008Data) {
        this.ukg99008Data = ukg99008Data;
    }

    /**
     * <pre>
     * [説 明] 申込書印刷で表示する。
     * @return アクション処理結果
     * @throws Exception 処理異常
     * </pre>
     */
    @Override
    public String execute() throws Exception {
        // 同期フラグ設定
        setAsyncFlg(true);
        HttpServletRequest request = ServletActionContext.getRequest();
        // 顧客区分
        kokyaKbn = request.getParameter("kokyakuSb");
        return successForward();
    }

    /**
     * <pre>
     * [説 明] ドロップダウン初期情報取得。
     * @throws Exception 処理異常
     * </pre>
     */
    public String gamenInitInfo() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        // セッションのログイン情報取得する
        sessionLoginInfo = getLoginInfo();
        HttpSession session = request.getSession();
        // 会社コード
        String strKaisyacd = String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD));
        ukg99008Data.put("kaisyacd", strKaisyacd);
        // 受付番号
        String strUketukeno = String.valueOf(session.getAttribute(Consts.UKETUKENO));
        ukg99008Data.put("uketukeno", strUketukeno);
        // 担当者コード
        ukg99008Data.put("tantoucd", String.valueOf(sessionLoginInfo.get(Consts.USER_CD)));

        // 入居者家賃保証利用料
        ukg99008Data.put("consthosyouryou", PropertiesReader.getIntance().getProperty("UKG99008.yatinHosyouRyou"));

        // 初期化情報を取得する。
        UKG99008Services.dropdownInitInfo(strKaisyacd, strUketukeno, bukkenNo, heyaNo, ukg99008Data);
        return "initInfo";
    }

    /**
     * <pre>
     * [説 明] データ登録処理。
     * @throws Exception 処理異常
     * </pre>
     */
    public String insUpdmosikomiInfo() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        // セッションのログイン情報取得する
        sessionLoginInfo = getLoginInfo();
        HttpSession session = request.getSession();

        // パラメータを作成する。
        Map<String, Object> paramMap = new HashMap<String, Object>();
        // 会社コード
        paramMap.put("param_kaisyacd", String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD)));
        // 担当者コード
        paramMap.put("param_tantoucd", String.valueOf(sessionLoginInfo.get(Consts.USER_CD)));
        // 担当者名
        paramMap.put("param_tantouname", String.valueOf(sessionLoginInfo.get(Consts.USER_NAME)));
        // 店舗コード
        paramMap.put("param_postcd", String.valueOf(sessionLoginInfo.get(Consts.POST_CD)));
        // 受付NO
        paramMap.put("param_uketukeno", String.valueOf(session.getAttribute(Consts.UKETUKENO)));

        // 2017/01/16 SOT Yotsumoto ADD Start (案件C41014-2)e-申請対応
        // 顧客コード
        paramMap.put("param_kokyakuCd", request.getParameter("kokyakucd"));
        // 顧客連番
        paramMap.put("param_kokyakuSeq", request.getParameter("kokyakuseq"));
        // 2017/01/16 SOT Yotsumoto ADD End (案件C41014-2)e-申請対応
        // 物件No
        paramMap.put("param_homemateno", bukkenNo);
        // 号室
        paramMap.put("param_heyano", heyaNo);

        // 表示フラグ
        paramMap.put("param_displayFlg", request.getParameter("displayflg"));
        // 税有無区分
        paramMap.put("param_zeiumukb", request.getParameter("zeiumukb"));
        // 保証人代行会社"
        if ("1".equals(request.getParameter("chksabisuFlg"))) {
            paramMap.put("param_daikokaisyacd", PropertiesReader.getIntance().getProperty("UKG99008.daikoKaisyaCd"));
        } else {
            paramMap.put("param_daikokaisyacd", "");
        }
        paramMap.put("param_sabisuFlg", request.getParameter("chksabisuFlg"));
        // 家賃
        paramMap.put("param_yatin", request.getParameter("yatin"));
        // 税額
        paramMap.put("param_tax", PropertiesReader.getIntance().getProperty("tax"));
        // 共益費金額
        paramMap.put("param_kyouekihi", request.getParameter("kyouekihi"));
        // 共益費金額区分
        paramMap.put("param_kyouekihiKb", request.getParameter("kyouekihiKb"));
        // 駐車場①金額
        paramMap.put("param_tinryou1", request.getParameter("tinryou1"));
        // 駐車場①金額区分
        paramMap.put("param_tinryou1Kb", request.getParameter("tinryou1Kb"));
        // 駐車場➁金額
        paramMap.put("param_tinryou2", request.getParameter("tinryou2"));
        // 駐車場➁金額区分
        paramMap.put("param_tinryou2Kb", request.getParameter("tinryou2Kb"));
        // その他費用①名称
        paramMap.put("param_metchiyouName1", request.getParameter("metchiyouName1"));
        // その他費用①金額
        paramMap.put("param_metchiyouKin1", request.getParameter("metchiyouKin1"));
        // その他費用①金額区分
        paramMap.put("param_metchiyouKb1", request.getParameter("metchiyouKb1"));
        // その他費用➁名称
        paramMap.put("param_metchiyouName2", request.getParameter("metchiyouName2"));
        // その他費用➁金額
        paramMap.put("param_metchiyouKin2", request.getParameter("metchiyouKin2"));
        // その他費用➁金額区分
        paramMap.put("param_metchiyouKb2", request.getParameter("metchiyouKb2"));
        // その他費用③名称
        paramMap.put("param_metchiyouName3", request.getParameter("metchiyouName3"));
        // その他費用③金額
        paramMap.put("param_metchiyouKin3", request.getParameter("metchiyouKin3"));
        // その他費用③金額区分
        paramMap.put("param_metchiyouKb3", request.getParameter("metchiyouKb3"));
        // その他費用④名称
        paramMap.put("param_metchiyouName4", request.getParameter("metchiyouName4"));
        // その他費用④金額
        paramMap.put("param_metchiyouKin4", request.getParameter("metchiyouKin4"));
        // その他費用④金額区分
        paramMap.put("param_metchiyouKb4", request.getParameter("metchiyouKb4"));
        // 入居予定日
        paramMap.put("param_nyukyodate", request.getParameter("nyukyodate"));
        // 賃料支援有無
        paramMap.put("param_yatinsien", request.getParameter("yatinsien"));
        // 割引サービス
        paramMap.put("param_waribikicd", request.getParameter("waribikicd"));
        // 割引サービスの割引率
        paramMap.put("param_waribikritu", request.getParameter("waribikiRitu"));
        // 礼金
        paramMap.put("param_reiKin", request.getParameter("reiKin"));
        // 敷金
        paramMap.put("param_sikiKin", request.getParameter("sikiKin"));
        // その他一時金①名称
        paramMap.put("param_etckeiyakuName1", request.getParameter("etckeiyakuName1"));
        // その他一時金①金額
        paramMap.put("param_etckeiyakuKin1", request.getParameter("etckeiyakuKin1"));
        // その他一時金➁名称
        paramMap.put("param_etckeiyakuName2", request.getParameter("etckeiyakuName2"));
        // その他一時金➁金額
        paramMap.put("param_etckeiyakuKin2", request.getParameter("etckeiyakuKin2"));
        // その他一時金③名称
        paramMap.put("param_etckeiyakuName3", request.getParameter("etckeiyakuName3"));
        // その他一時金③金額
        paramMap.put("param_etckeiyakuKin3", request.getParameter("etckeiyakuKin3"));
        // その他一時金④名称
        paramMap.put("param_etckeiyakuName4", request.getParameter("etckeiyakuName4"));
        // その他一時金④金額
        paramMap.put("param_etckeiyakuKin4", request.getParameter("etckeiyakuKin4"));
        // その他一時金⑤名称
        paramMap.put("param_etckeiyakuName5", request.getParameter("etckeiyakuName5"));
        // その他一時金⑤金額
        paramMap.put("param_etckeiyakuKin5", request.getParameter("etckeiyakuKin5"));
        // 仲介手数料
        paramMap.put("param_tyukairyou", request.getParameter("tyukairyou"));
        // 消毒施工料
        paramMap.put("param_syoudokuhi", request.getParameter("syoudokuhi"));
        // 火災(損害)保険料
        paramMap.put("param_kasaihokenryou", request.getParameter("kasaihokenryou"));
        // 保証人代行料
        paramMap.put("param_daikoryo", request.getParameter("daikoryo"));
        // 償却金額
        paramMap.put("param_sikisyokai", request.getParameter("sikisyokai"));
        // 契約書データ入居の更新時間
        paramMap.put("param_nyukyoupdtime", request.getParameter("nyukyoupdtime"));
        // 契約書データの更新時間
        paramMap.put("param_keiyakubaceupdtime", request.getParameter("keiyakuupdtime"));

        //2019/07/26 TK Sato ADD START (案件C43110-2)消費税対応
        // 税率(増税後)
        paramMap.put("param_newTax", PropertiesReader.getIntance().getProperty("newTax"));
        // 増税開始日
        paramMap.put("param_newTaxStartDate", PropertiesReader.getIntance().getProperty("newTaxStartDate"));
        //2019/07/26 TK Sato ADD START (案件C43110-2)消費税対応


        // 2017/01/16 SOT Yotsumoto ADD Start (案件C41014-2)e-申請対応
        String errMsg = "";
        // 最低家賃の取得
        log.debug("会社コード："+sessionLoginInfo.get(Consts.KAISYA_CD));
        log.debug("顧客コード"+request.getParameter("kokyakucd"));
        log.debug("顧客Seq"+request.getParameter("kokyakuseq"));
        Map<String, Object> fetchInfo = UKG99008Services.fetchMinYatin(String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD)),
        															   String.valueOf(request.getParameter("kokyakucd")),
        															   String.valueOf(request.getParameter("kokyakuseq")));
        ukg99008Data.put("minYatin",(String)fetchInfo.get("out_minYatin"));
        // 2017/01/16 SOT Yotsumoto ADD End (案件C41014-2)e-申請対応
        Map<String, Object> insertInfo = UKG99008Services.prinrSyori(paramMap);
        // 2017/01/16 SOT Yotsumoto DEL Start (案件C41014-2)e-申請対応
//      String errMsg = "";
        // 2017/01/16 SOT Yotsumoto DEL End (案件C41014-2)e-申請対応
        // 更新処理失敗の場合
        if ("ERRS002".equals(insertInfo.get("rst_code"))) {
            errMsg = Util.getServerMsg("ERRS002", "更新処理");
        } else if ("ERRS003".equals(insertInfo.get("rst_code"))) {
            errMsg = Util.getServerMsg("ERRS003");
        }

        ukg99008Data.put("newNyukyoupdtime", insertInfo.get("out_nyukyoupdtime"));
        ukg99008Data.put("newKeiyakuupdtime", insertInfo.get("out_keiyakuupdtime"));
        // エラーメッセージをセットする
        ukg99008Data.put("errMsg", errMsg);
        return "insUpdmosikomiInfo";
    }

    /**
     * <pre>
     * [説 明] 申込書印刷画面初期化情報を取得する。
     * @throws Exception 処理異常
     * </pre>
     */
    public String newMosikomiInfo() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        // セッションのログイン情報取得する
        sessionLoginInfo = getLoginInfo();

        HttpSession session = request.getSession();
        // 会社コード
        String strKaisyacd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
        // 受付番号
        String strUketukeno = session.getAttribute(Consts.UKETUKENO).toString();

        // 2017/01/16 SOT Yotsumoto ADD Start (案件C41014-2)e-申請対応
        // 顧客コード
        String strKokyakucd = request.getParameter("kokyakucd");

        // 顧客SEQ
        String strKokyakuseq = request.getParameter("kokyakuseq");

        Map<String, Object> fetchInfo = UKG99008Services.fetchMinYatin(strKaisyacd,strKokyakucd,strKokyakuseq);
        ukg99008Data.put("minYatin",(String)fetchInfo.get("out_minYatin"));
        // 2017/01/16 SOT Yotsumoto ADD END (案件C41014-2)e-申請対応

        // 画面初期化
        UKG99008Services.fetchNewmosikomiInfo(strKaisyacd, strUketukeno, bukkenNo, heyaNo, ukg99008Data);
        return "initInfo";
    }

    /**
     * <pre>
     * [説 明] 申込書印刷画面既存情報を取得する。
     * @throws Exception 処理異常
     * </pre>
     */
    public String oldMosikomiInfo() throws Exception {

        HttpServletRequest request = ServletActionContext.getRequest();

        // セッションのログイン情報取得する
        sessionLoginInfo = getLoginInfo();

        // 会社コード
        String strKaisyacd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();

        // 顧客コード
        String strKokyakucd = request.getParameter("kokyakucd");

        // 顧客SEQ
        String strKokyakuseq = request.getParameter("kokyakuseq");

        // 2017/01/16 SOT Yotsumoto ADD Start (案件C41014-2)e-申請対応
        Map<String, Object> fetchInfo = UKG99008Services.fetchMinYatin(strKaisyacd,strKokyakucd,strKokyakuseq);
        ukg99008Data.put("minYatin",(String)fetchInfo.get("out_minYatin"));
        // 2017/01/16 SOT Yotsumoto ADD END (案件C41014-2)e-申請対応
        // 賃借顧客データのレコード数を取得する
        String recordCount = UKG99008Services.tinsyakukokyakuCount(
                String.valueOf(sessionLoginInfo.get(Consts.KAISYA_CD)), request.getParameter("kokyakucd"),
                request.getParameter("kokyakuseq"));
        ukg99008Data.put("tinsyakukokyakucount", recordCount);

        // 既存情報を取得する。
        UKG99008Services.fetchOldmosikomiInfo(strKaisyacd, bukkenNo, heyaNo, strKokyakucd, strKokyakuseq, ukg99008Data);
        return "initInfo";
    }

    /**
     * 割引サービス取得
     *
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public String warivikiServiceLstInfo() throws Exception {

        HttpServletRequest request = ServletActionContext.getRequest();

        // リスト内容
        String param_list_naiyo = request.getParameter("param_list_naiyo");

        // 2020/03/30   楊朋朋(SYS)  UPD Start C43017_見積書改修
        // 初期データを取得する
        // Map<String, Object> warivikiServiceList = UKG99008Services.getWarivikiServiceList("148", param_list_naiyo);
        // ukg99008Data.put("warivikiServiceList", warivikiServiceList.get("rst_list"));
        
        //受付№
        HttpSession session = request.getSession();
        String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
        //法人企業マスタ減額率
        List<Map<String, Object>> gennGakuRituList = (List<Map<String, Object>>) UKG99005Services.getGennGakuRitu(uketukeNo).get("rst_uketukeno");
        // 部屋情報取得する
        Map<String, Object> inItInfo = UKG99005Services.getYatinHosyouInfo(bukkenNo, heyaNo);
        List<Map<String, Object>> yatinHosyouInfoList = (List<Map<String, Object>>) inItInfo.get("rst_yatinInfo");
        Map<String, Object> waribikiServiceKbMap = new HashMap<String, Object>();
        Map<String, Object> gennGakuRituMap = gennGakuRituList.get(0);
        if(yatinHosyouInfoList.size()>0){
            waribikiServiceKbMap = yatinHosyouInfoList.get(0);
        }

       // 初期データを取得する
        Map<String, Object> warivikiServiceList = UKG99005Services.getWarivikiServiceList("148", param_list_naiyo);
        List<HashMap<String,Object>> wkslist = (List<HashMap<String,Object>>)warivikiServiceList.get("rst_list");
        int sublease_genn = 0;
        //法人企業コード
        String gennGakuRituType = "0";
        if(gennGakuRituMap!=null ){
            //「法人企業コード(HOUJIN_KIGYOU_CD)」が設定されている場合、「法人企業クラブ会員」と判断できる
            if (gennGakuRituMap.get("HOUJIN_KIGYOU_CD")!=null && !"".equals(gennGakuRituMap.get("HOUJIN_KIGYOU_CD"))){
                if(waribikiServiceKbMap != null){
                    // 法人企業マスタ．サブリース減額率を設定します。
                    // 部屋データ．覚書種別（区分）が、「4（サブリース経営管理システム）」・「5（サブリース経営代行システム）」・「6（サブリース総合代行システム）」いずれかの場合
                    if("4".equals(waribikiServiceKbMap.get("MEMO_SB_CD")) || "5".equals(waribikiServiceKbMap.get("MEMO_SB_CD")) || "6".equals(waribikiServiceKbMap.get("MEMO_SB_CD"))){
                        if(gennGakuRituMap.get("SUBLEASE_GENGAKU_RITU")!=null && !"".equals(gennGakuRituMap.get("SUBLEASE_GENGAKU_RITU"))){
                            sublease_genn = Integer.parseInt((String)gennGakuRituMap.get("SUBLEASE_GENGAKU_RITU"));
                        }
                    }else{
                        if(gennGakuRituMap.get("GENGAKU_RITU")!=null && !"".equals(gennGakuRituMap.get("GENGAKU_RITU"))){
                            sublease_genn = Integer.parseInt((String)gennGakuRituMap.get("GENGAKU_RITU"));
                        }
                    }
                    //つづり合わせ
                    if(wkslist!=null && wkslist.size()>0){
                        for(int i=0;i<wkslist.size();i++){
                            HashMap<String, Object> temp = wkslist.get(i);
                            //「’18’（8：法人団体割引サービス）」内容に減額率を追加表示する。
                            if("18".equals(temp.get("KOUMOKU_SB_CD"))){
                                temp.put("KOUMOKU_SB_NAME",temp.get("KOUMOKU_SB_NAME")+String.valueOf(sublease_genn)+"%");
                                wkslist.remove(i);
                                wkslist.add(i, temp);
                           }
                        }
                    }
                }
                gennGakuRituType = "1";
                // 法人企業クラブ会員以外の場合
            }else{
                if(wkslist!=null && wkslist.size()>0){
                    for(int i=0;i<wkslist.size();i++){
                        HashMap<String, Object> temp = wkslist.get(i);
                        //「8：法人団体割引サービス」は非表示
                        if("18".equals(temp.get("KOUMOKU_SB_CD").toString())){
                            wkslist.remove(temp);
                         }
                    }
                }
            }
        }

        ukg99008Data.put("warivikiServiceList", wkslist);
        ukg99008Data.put("sublease_genn", String.valueOf(sublease_genn));
        ukg99008Data.put("gennGakuRituType", gennGakuRituType);
        //  2020/03/30   楊朋朋(SYS) UPD End C43017_見積書改修
        return "warivikiServiceList";
    }

    /**
     * 指定部屋の家賃情報取得処理
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public String yatinHosyouInfo() throws Exception {
        // 初期データを取得する
        Map<String, Object> inItInfo = UKG99008Services.fetchYatinHosyouInfo(bukkenNo, heyaNo);

        List<Map<String, Object>> yatinHosyouInfoList = (List<Map<String, Object>>) inItInfo.get("rst_yatinInfo");
        ukg99008Data.put("yatinHosyouInfoList", yatinHosyouInfoList);
        return "initInfo";
    }

 // 2019/07/24 TK Matsuda ADD START (案件C43110-2)消費税対応
    /**
     * <pre>
     * [説 明] 増税後賃貸条件を取得する。
     * @throws Exception 処理異常
     * </pre>
     */
    public String afterZouzeiInfo() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();

        String newTaxStartDate = PropertiesReader.getIntance().getProperty("newTaxStartDate");

        // 賃貸条件変更(増税)データのレコード数を取得する
        String recordCount = UKG99008Services.countZouzeiTintaiJyoken(bukkenNo, heyaNo, newTaxStartDate);
        ukg99008Data.put("zouzeiRecordCount", recordCount);

        if(!"0".equals(recordCount)){
          // 増税後賃貸条件を取得する。
            UKG99008Services.fetchZouzeiTintaiJyoken(bukkenNo, heyaNo, newTaxStartDate, ukg99008Data);
        }
        return "afterZouzeiInfo";
    }

    /**
     * <pre>
     * [説 明] 増税前部屋情報を取得する。
     * @throws Exception 処理異常
     * </pre>
     */
    public String beforeZouzeiHeyaInfo() throws Exception {
        
        HttpServletRequest request = ServletActionContext.getRequest();
        UKG99008Services.fetchBeforeZouzeiHeyaInfo(bukkenNo, heyaNo, ukg99008Data);
        return "beforeZouzeiHeyaInfo";
    }

    /**
     * <pre>
     * [説 明] 増税データを取得する。
     * @throws Exception 処理異常
     * </pre>
     */
    public String beforeZouzeiKeiyakuInfo() throws Exception {

        HttpServletRequest request = ServletActionContext.getRequest();
	sessionLoginInfo = getLoginInfo();
        // 会社コード
        String strKaisyaCd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();

        // 顧客コード
        String kokyakuCd = request.getParameter("kokyakucd");

        // 顧客SEQ
        String kokyakuSeq = request.getParameter("kokyakuseq");

        UKG99008Services.fetchBeforeZouzeiKeiyakuInfo(strKaisyaCd, bukkenNo, heyaNo, kokyakuCd, kokyakuSeq, ukg99008Data);
        return "beforeZouzeiKeiyakuInfo";
    }

 // 2019/07/24 TK Matsuda ADD END (案件C43110-2)消費税対応
    //2020/05/18 劉恒毅 ADD START C44063-2_入居対策リストの入居日対応
    /**
     * 割引サービスを取得
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public String jisyaJyouyou() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        //物件NO
        String bukkenNoStr = request.getParameter("bukkenNoStr");
        //部屋NO
        String heyaNoStr = request.getParameter("heyaNoStr");
        //入居日
        String nyuukyoDate = request.getParameter("nyuukyoDate");
        // 自社情報を取得
        Map<String, Object> jisyaJyouyou = UKG99008Services.getJisyaJyouyou(bukkenNoStr, heyaNoStr, nyuukyoDate);

        //割引サービス
        String jisyaServiceKb = "";
        //賃料支援 有/無
        String tinryouSien = "";
        
        //戻り値
        String jisyaReturnVal = String.valueOf(jisyaJyouyou.get("RETURN_VAL"));
        if("0".equals(jisyaReturnVal)) {
            jisyaServiceKb = "1";
            // 受付システム情報を取得
            String serviceKb = (String)jisyaJyouyou.get("JISYA_SERVICE_KB");
            if (!Util.isEmpty(serviceKb)) {
                List<Map<String, Object>> uketukeJisyaJyouou =  (List<Map<String, Object>>) UKG99008Services.getCommon("126",serviceKb).get("rst_list");
                if(uketukeJisyaJyouou.size() > 0) {
                    String[] jisyaJyouou = ((String) uketukeJisyaJyouou.get(0).get("BIKOU")).split(",");
                    jisyaServiceKb = jisyaJyouou[0];
                    tinryouSien = jisyaJyouou[1];
                }
            }
        }
        //割引サービス
        ukg99008Data.put("jisyaServiceKb",jisyaServiceKb);
        //賃料支援 有/無
        ukg99008Data.put("tinryouSien",tinryouSien);
        
        return "ajaxProcess";
    }
    //2020/05/18 劉恒毅 ADD END C44063-2_入居対策リストの入居日対応
}
