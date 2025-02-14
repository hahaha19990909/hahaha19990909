/**
 *@システム名: 受付システム
 *@ファイル名: UKG99005Action.java
 *@更新日付：: 2012/02/28
 *@Copyright: 2012 token corporation All right reserved
 * 更新履歴：2014/06/10	郭凡(SYS)   (案件番号C38117)セールスポイント編集機能の追加
 * 更新履歴：2015/03/12	温(SYS)   (案件番号C39042)トップページレイアウト修正
 */
package jp.co.token.uketuke.action;

import java.io.UnsupportedEncodingException;
import java.util.Date;
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

/**<pre>
 * [機 能] 見積書印刷で表示する
 * [説 明] 見積書印刷で表示する
 * @author [作 成]
 * 変更履歴: 2013/02/17 SYS_趙雲剛 (案件C37058)受付システム要望対応
 * 変更履歴: 2013/12/23 SYS_胡涛     (案件C37084-6)家賃保証備考欄の追加対応
 * 変更履歴: 2018/03/27 SOT_KATO     (案件C42066-2)女子割10％対応
 * 変更履歴：2020/03/30    楊朋朋(SYS)   (案件番号C43017)見積書改修
 * </pre>
 */
public class UKG99005Action extends BaseAction {
	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;
	/** 見積書印刷画面用データ */
	private Map<String, Object> ukg99005Data = new HashMap<String, Object>();

	/** トップページサービス */
	private IUKG99005Service UKG99005Services;

	/** セッションのログイン情報 */
	private Map<String, Object> sessionLoginInfo = null;

	/** 物件番号 */
	private String bukkenNo;
	/** 部屋番号 */
	private String heyaNo;

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
	 * @throws UnsupportedEncodingException
	 */
	public void setHeyaNo(String heyaNo) throws UnsupportedEncodingException {
		//2015/03/12 温(SYS) Upd Start (案件番号C39042)既存不具合対応
		//部屋設定は上に移動
		this.heyaNo = heyaNo;
		if (!Util.isEmpty(heyaNo) && "ISO-8859-1".equals(Util.getEncoding(heyaNo))) {
			this.heyaNo = new String(heyaNo.getBytes("ISO-8859-1"), "UTF-8");
		}
		//2015/03/12 温(SYS) Upd End
	}

	/**
	 * セッション情報
	 * @return
	 */
	public Map<String, Object> getSessionLoginInfo() {
		return sessionLoginInfo;
	}

	/**
	 * セッション情報
	 * @param sessionLoginInfo　セッション情報
	 */
	public void setSessionLoginInfo(Map<String, Object> sessionLoginInfo) {
		this.sessionLoginInfo = sessionLoginInfo;
	}

	public void setUKG99005Services(IUKG99005Service services) {
		UKG99005Services = services;
	}

	/**
	 * 見積書印刷画面用データ
	 * @return ukg99005Data
	 */
	public Map<String, Object> getUkg99005Data() {
		return ukg99005Data;
	}

	/**
	 * 見積書印刷画面用データ
	 * @param ukg99005Data
	 */
	public void setUkg99005Data(Map<String, Object> ukg99005Data) {
		this.ukg99005Data = ukg99005Data;
	}

	/** <pre>
	 * [説 明] 見積書印刷で表示する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {
		// 同期フラグ設定
		setAsyncFlg(true);
		return successForward();
	}

	/**
	 * 初期化処理
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String initInfo() throws Exception {


		HttpServletRequest request = ServletActionContext.getRequest();

		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		// ソート順設定する
		HttpSession session = request.getSession();
		String kaisyacd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String uketukeno = session.getAttribute(Consts.UKETUKENO).toString();
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		String toriatukaiKanouFlg = "0";
		  if("0".equals(authorityCd)){
		   toriatukaiKanouFlg = "1";
		  }
//		//初期化データ取得する // parasoft-suppress UC.ACC "C39042Jtest対応"
//		Map<String, Object> returnData = UKG99005Services.getInitInfo(kaisyacd, // parasoft-suppress UC.ACC "C39042Jtest対応"
//				uketukeno, bukkenNo, heyaNo);
		Map<String, Object> returnData = UKG99005Services.getInitInfo(kaisyacd,
				uketukeno, bukkenNo, heyaNo, toriatukaiKanouFlg);
		 // ########## 2014/06/10 郭凡 UPD End
		// 初期データを取得する
		List<Map<String, Object>> flgInfo = (List<Map<String, Object>>) returnData
				.get("out_mitumoriInfo");
		ukg99005Data.put("initFlg", flgInfo.get(0).get("MITUMORI_SEQ"));

		// 駐車場単位
		ukg99005Data.put("chousyabaList", returnData.get("out_tyusyabatani"));
		// 共益費/管理日単位
		ukg99005Data.put("kyoekihiList", returnData.get("out_kyoekihi"));
		ukg99005Data.put("kanrihiList", returnData.get("out_kanrihitani"));
		// 礼金単位
		ukg99005Data.put("rekintaniList", returnData.get("out_rekintani"));
		// 敷金/保証金単位
		ukg99005Data.put("sikikintaiList", returnData.get("out_sikikintani"));
		// 敷引/償却/解約引単位
		ukg99005Data.put("kaiyakutaniList", returnData.get("out_kaiyakutani"));
		//消費税
		String tax = PropertiesReader.getIntance().getProperty("tax");
		ukg99005Data.put("tax", tax);
		// 2013/12/23	SYS_胡涛	ADD Start (案件C37084-6)家賃保証備考欄の追加対応
		// 入居者家賃保証利用料金額区分
		ukg99005Data.put("yatinHosyouRyoutaniList", returnData.get("out_yatinHosyouRyoutani"));
		// 2013/12/23	SYS_胡涛	ADD End

		return "initInfo";
	}

	/**
	 * 新規見積書データ取得処理
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String mitumoriNewInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		// ソート順設定する
		HttpSession session = request.getSession();
		String kaisyacd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String uketukeno = session.getAttribute(Consts.UKETUKENO).toString();

		// 初期データを取得する
		Map<String, Object> inItInfo = UKG99005Services.getmitumoriNewInfo(
				kaisyacd, uketukeno, bukkenNo, heyaNo);
		List<Map<String, Object>> mitummoriDataList = (List<Map<String, Object>>) inItInfo
				.get("out_heyaInfo");

/* 2018/03/27 ADD_BEGIN SOT_KATO (案件C42066-2)女子割10％対応 */
		ukg99005Data.put("kokyakuInfo", inItInfo.get("out_kokyakuInfo"));
/* 2018/03/27 ADD_END   SOT_KATO (案件C42066-2)女子割10％対応 */
		ukg99005Data.put("kiounyukyoInfo", inItInfo.get("out_kiounyukyoInfo"));
		ukg99005Data.put("mitummoriDataList", mitummoriDataList);
		// 2013/02/17	SYS_趙雲剛		ADD Start (案件C37058)受付システム要望対応
		ukg99005Data.put("NOW_TIME", Util.getSysDateYMD());
		// 2013/02/17	SYS_趙雲剛		Add End
		return "mitumoriNewInfo";
	}

	/**
	 * 既存見積書データ取得処理
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String mitumoriOldInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();
		// ソート順設定する
		HttpSession session = request.getSession();
		String kaisyacd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String uketukeno = session.getAttribute(Consts.UKETUKENO).toString();

		String mitumoriSeq = request.getParameter("mitumoriSeq");

		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		String toriatukaiKanouFlg = "0";
		  if("0".equals(authorityCd)){
		   toriatukaiKanouFlg = "1";
		  }
		// 初期データを取得する // parasoft-suppress UC.ACC "C39042Jtest対応"
//		Map<String, Object> inItInfo = UKG99005Services.getOldmitumoriInfo( // parasoft-suppress UC.ACC "C39042Jtest対応"
//				kaisyacd, uketukeno, bukkenNo, heyaNo, mitumoriSeq);
		Map<String, Object> inItInfo = UKG99005Services.getOldmitumoriInfo(
				kaisyacd, uketukeno, bukkenNo, heyaNo, mitumoriSeq, toriatukaiKanouFlg);
		 // ########## 2014/06/10 郭凡 UPD End
		List<Map<String, Object>> mitummoriDataList = (List<Map<String, Object>>) inItInfo
				.get("out_heyaInfo");
		ukg99005Data.put("mitummoriDataList", mitummoriDataList);

		return "mitumoriOldInfo";

	}

	/**
	 * 見積書データ登録処理
	 * @return
	 * @throws Exception
	 */
	public String insertMitumoriInfo () throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		sessionLoginInfo = getLoginInfo();

		Map<String, Object> paramMap = new HashMap<String, Object>();

		// ソート順設定する
		HttpSession session = request.getSession();
		paramMap.put("param_kaisya_cd",sessionLoginInfo.get(Consts.KAISYA_CD).toString());
		paramMap.put("param_uketuke_no",session.getAttribute(Consts.UKETUKENO).toString());
		paramMap.put("param_app_tantou_cd",String.valueOf(sessionLoginInfo.get(Consts.USER_CD)));
		paramMap.put("param_upd_tantou_cd",String.valueOf(sessionLoginInfo.get(Consts.USER_CD)));

		paramMap.put("param_homemate_bukken_no",bukkenNo);
		paramMap.put("param_heya_no",heyaNo);

		paramMap.put("param_nyukyo_yotei_date",request.getParameter("nyukyoyoteiDate"));
		paramMap.put("param_hiwari_kijyun",request.getParameter("kijunnissu"));
		paramMap.put("param_hasuu",request.getParameter("hassu"));
		paramMap.put("param_yatin",request.getParameter("yatin"));
		paramMap.put("param_tin_ryou",request.getParameter("tinryo1"));

		paramMap.put("param_parking_hi_kb",request.getParameter("tinryo1kb"));
		paramMap.put("param_tin_ryou2",request.getParameter("tinryo2"));
		paramMap.put("param_parking_hi_kb2",request.getParameter("tinryo2kb"));
		paramMap.put("param_kyo_kan_hi",request.getParameter("kyokanhi"));
		paramMap.put("param_kyo_kan_hi_kb",request.getParameter("kyokanhikb"));
		paramMap.put("param_kyo_kan_hi_sel_kb",request.getParameter("kyokanhiselkb"));
		paramMap.put("param_m_etc_hiyou_item1",request.getParameter("metchiyouitem1"));
		paramMap.put("param_m_etc_hiyou_kin1",request.getParameter("metchiyouKin1"));
		paramMap.put("param_m_etc_hiyou_item2",request.getParameter("metchiyouitem2"));
		paramMap.put("param_m_etc_hiyou_kin2",request.getParameter("metchiyouKin2"));

		paramMap.put("param_chukai_ryou",request.getParameter("chukairyo"));

		paramMap.put("param_waribiki_service",request.getParameter("waribikiservice"));
		paramMap.put("param_tin_ryou_sien",request.getParameter("tinryosien"));
		paramMap.put("param_rei_kin",request.getParameter("reikin"));
		paramMap.put("param_rei_kin_umu",request.getParameter("reikinkb"));
		paramMap.put("param_siki_hosyou_kin",request.getParameter("sikihosyoukingaku"));


		paramMap.put("param_siki_hosyou_kin_umu",request.getParameter("sikiHosyouKinTanni"));
		paramMap.put("param_siki_hosyou_sel_kb",request.getParameter("sikihosyoselkb"));
		paramMap.put("param_siki_syo_kai",request.getParameter("sikisyokaikingaku"));
		paramMap.put("param_siki_syo_kai_unit",request.getParameter("sikisyokaiTanni"));
		paramMap.put("param_siki_syo_kai_sel_kb",request.getParameter("sikisyokaiselkb"));
		paramMap.put("param_etc_hiyou_item1",request.getParameter("etchiyouitem1"));
		paramMap.put("param_etc_hiyou_kin1",request.getParameter("etchiyoukin1"));
		paramMap.put("param_etc_hiyou_item2",request.getParameter("etchiyouitem2"));
		paramMap.put("param_etc_hiyou_kin2",request.getParameter("etchiyoukin2"));

		paramMap.put("param_etc_hiyou_item3",request.getParameter("etchiyouitem3"));
		paramMap.put("param_etc_hiyou_kin3",request.getParameter("etchiyoukin3"));
		paramMap.put("param_etc_hiyou_item4",request.getParameter("etchiyouitem4"));
		paramMap.put("param_etc_hiyou_kin4",request.getParameter("etchiyoukin4"));
		paramMap.put("param_shoudoku_ryou",request.getParameter("syoudokuSekouRyou"));

		paramMap.put("param_songai_hoken_ryou",request.getParameter("songaiHokenRyou"));
		paramMap.put("param_rentai_daikou_ryou",request.getParameter("rentaiHosyouninDaikouRyou"));
		// 2013/12/23	SYS_胡涛	ADD Start (案件C37084-6)家賃保証備考欄の追加対応
		// 入居者家賃保証利用料金額区分
		// 2019/06/18 汪会(SYS) UPD START C42036-不具合対応-BUG209
		/*paramMap.put("param_rentai_daikou_ryou_kb",request.getParameter("rentaiHosyouninDaikouRyoukb"));*/
		String rentaiHosyouninDaikouRyou = request.getParameter("rentaiHosyouninDaikouRyou");
		if(rentaiHosyouninDaikouRyou != "") {
			paramMap.put("param_rentai_daikou_ryou_kb","1");
		}else {

			paramMap.put("param_rentai_daikou_ryou_kb","0");
		}
		// 2019/06/18 汪会(SYS) UPD END C42036-不具合対応-BUG209
		// 2013/12/23	SYS_胡涛	ADD End
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		String toriatukaiKanouFlg = "0";
		  if("0".equals(authorityCd)){
		   toriatukaiKanouFlg = "1";
		  }
		paramMap.put("param_toriatukaiKanouFlg",toriatukaiKanouFlg);
		// ########## 2014/06/10 郭凡 ADD End
		paramMap.put("param_kazei_kb",request.getParameter("kazeikb"));

		// 初期データを取得する
		Map<String, Object> mitumoriInfo = UKG99005Services.insertMitumoriInfo(paramMap);

		String errMsg = "";
		// 更新処理失敗の場合
		if ("ERRS002".equals(mitumoriInfo.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS002", "更新処理");
		}

		// エラーメッセージをセットする
		ukg99005Data.put("errMsg", errMsg);

		ukg99005Data.put("kaisyaCd", sessionLoginInfo.get(Consts.KAISYA_CD).toString());
		ukg99005Data.put("uketukeNo", session.getAttribute(Consts.UKETUKENO).toString());
		ukg99005Data.put("tantouCd", String.valueOf(sessionLoginInfo.get(Consts.USER_CD)));
		ukg99005Data.put("mitumoriSeq", mitumoriInfo.get("rst_mitumoriSeq"));
		return "insertMitumoriInfo";
	}

	/**
	 * 割引サービス取得
	 * @return
	 * @throws Exception
	 */
    @SuppressWarnings({ "unchecked" })
	public String warivikiServiceList() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String param_list_naiyo = request.getParameter("param_list_naiyo");
        // 2020/03/30   楊朋朋(SYS)  UPD Start C43017_見積書改修
        // 初期データを取得する
        // Map<String, Object> warivikiServiceList = UKG99005Services.getWarivikiServiceList("148", param_list_naiyo);
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
                                temp.put("KOUMOKU_SB_RYAKU_NAME", String.valueOf(sublease_genn));
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

        ukg99005Data.put("warivikiServiceList", wkslist);
        ukg99005Data.put("sublease_genn", String.valueOf(sublease_genn));
        ukg99005Data.put("gennGakuRituType", gennGakuRituType);
        //  2020/03/30   楊朋朋(SYS) UPD End C43017_見積書改修
		return "warivikiServiceList";
	}

	/**
	 * <pre>
	 * [説 明] 商談履歴データ更新処理
     * @return 更新処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String dataUpdate() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		Map<String, Object> loginInfo = getLoginInfo();
		// 会社コードを取得
	    String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		//受付№
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));

		// 担当者コードを取得
	    String tantouCd = String.valueOf(loginInfo.get(Consts.USER_CD));
		String bukkenNo = request.getParameter("bukkenNo");
		String heyaNO = request.getParameter("heyaNo");
		if (!Util.isEmpty(heyaNO) && "ISO-8859-1".equals(Util.getEncoding(heyaNO))) {
			heyaNO = new String(heyaNO.getBytes("ISO-8859-1"), "UTF-8");
		}
		UKG99005Services.updateData(kaisyaCd, uketukeNo, bukkenNo, heyaNO,tantouCd, new Date());
       return "ajaxProcess";
	}

	// 2013/12/23 SYS_胡涛     ADD Start (案件C37084-6)家賃保証備考欄の追加対応
	/**
	 * 指定部屋の家賃情報取得処理
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String yatinHosyouInfo() throws Exception {
		// 初期データを取得する
		Map<String, Object> inItInfo = UKG99005Services.getYatinHosyouInfo(bukkenNo, heyaNo);
		List<Map<String, Object>> yatinHosyouInfoList = (List<Map<String, Object>>) inItInfo.get("rst_yatinInfo");
		ukg99005Data.put("yatinHosyouInfoList", yatinHosyouInfoList);

		return "yatinHosyouInfo";
	}
	// 2013/12/23 SYS_胡涛     ADD End
}