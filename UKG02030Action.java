/**
 * @システム名: 受付システム
 * @ファイル名: UKG02030Action.java
 * @更新日付：: 2011/12/26
 * @Copyright: 2011 token corporation All right reserved
 * 更新履歴: 2013/03/04  SYS_趙雲剛   (案件C37058)社長指示対応
 * 更新履歴: 2013/05/17  SYS_ 郭凡    (案件C37109-1)受付システムSTEP2要望対応
 * 更新履歴: 2014/01/06  SYS_ 謝超    (案件C37075)オリジナル情報誌リプレース
 * 更新履歴：2014/06/10  郭凡(SYS)  (案件番号C38117)セールスポイント編集機能の追加
 * 更新履歴：2015/02/12  温(SYS) (案件番号C39042)受付レスポンス対応
 * 更新履歴：2017/09/25  SYS_ヨウ強 (案件番号C41004)法人仲介担当者登録機能追加対応
 * 更新履歴：2018/01/22  SYS_肖剣生 (案件番号C42036)デザイン変更
 * 更新履歴：2019/06/27  SYS_郝年昇 (案件C43100)お客様項目の入力簡素化対応
 * 更新履歴：2020/03/26  SYS_楊朋朋 (案件C43017)見積書改修
 */
package jp.co.token.uketuke.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.DispFormat;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.exception.DataNotFoundException;
import jp.co.token.uketuke.formbean.KibouJyoukennBean;
import jp.co.token.uketuke.formbean.ToiawaseInfoBean;
import jp.co.token.uketuke.service.IBukkenSearchService;
import jp.co.token.uketuke.service.IUKG02010Service;
import jp.co.token.uketuke.service.IUKG02030Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 希望条件
 * [説 明] お客様の希望条件を入力・登録する。
 * @author [作 成] 2012/06/05 SYS_李鵬飛
 * 履  歴:[修 正]  2014/01/06   謝超(SYS)     オリジナル情報誌リプレース(案件C37075)
 * </pre>
 */
public class UKG02030Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;

	/** 顧客検索画面用データ */
	private Map<String, Object> ukg02030Data = null;

	/** 顧客検索サービス */
	private IUKG02030Service UKG02030Services;

	/** 物件検索サービス */
	// ########## 2014/01/06 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース
	// private IBukkenSearchService BukkenSearchService;
	private IBukkenSearchService bukkenSearchService;
	// ########## 2014/01/06 謝超 UPD End

	/**
	 * 物件情報BEAN
	 */
	private KibouJyoukennBean kibouJyoukennBean;
    
    // 2020/03/26 楊朋朋(SYS) ADD Start C44072_希望部屋数等を追加 
    /** お客様情報サービス */
    private IUKG02010Service UKG02010Services;
    // 2020/03/26 楊朋朋(SYS) ADD End C44072_希望部屋数等を追加 

	/**
	 * @param bukkenSearchService
	 *            the bukkenSearchService to set
	 */
	public void setBukkenSearchService(IBukkenSearchService bukkenSearchService) {

		// ########## 2014/01/06 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース
		// BukkenSearchService = bukkenSearchService;
		this.bukkenSearchService = bukkenSearchService;
		// ########## 2014/01/06 謝超 UPD End
	}

	/**
	 * 希望条件画面データ対象
	 *
	 * @param ukg02030Data
	 *            希望条件画面データ対象
	 */
	public Map<String, Object> getUkg02030Data() {

		return ukg02030Data;
	}

	/**
	 * 希望条件画面データ対象
	 *
	 * @return 希望条件画面データ対象
	 */
	public void setUkg02030Data(Map<String, Object> ukg02030Data) {

		this.ukg02030Data = ukg02030Data;
	}

	/**
	 * @return the kibouJyoukennBean
	 */
	public KibouJyoukennBean getKibouJyoukennBean() {

		return kibouJyoukennBean;
	}

	/**
	 * @param kibouJyoukennBean
	 *            the kibouJyoukennBean to set
	 */
	public void setKibouJyoukennBean(KibouJyoukennBean kibouJyoukennBean) {

		this.kibouJyoukennBean = kibouJyoukennBean;
	}

	/**
	 * 希望条件サービス
	 *
	 * @param services
	 */
	public void setUKG02030Services(IUKG02030Service services) {

		UKG02030Services = services;
	}

    // 2020/03/26 楊朋朋(SYS) ADD Start C44072_希望部屋数等を追加 
    /**
     * @param uKG02010Services  お客様情報サービス
     */
    public void setUKG02010Services(IUKG02010Service uKG02010Services) {
       UKG02010Services = uKG02010Services;
    }
    // 2020/03/26 楊朋朋(SYS) ADD End C44072_希望部屋数等を追加 
    /**
	 * <pre>
	 * [説 明] 検索処理。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg02030Data = new HashMap<String, Object>();

		// セッション情報取得
		Map<String, Object> loginInfo = getLoginInfo();
		String kaisyaCd = loginInfo.get(Consts.KAISYA_CD).toString();
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		// 新規の場合、市区郡コードを取得する
//		String postCd = loginInfo.get(Consts.POST_CD).toString();
		String postCd = (String)uketukePostInfo.get(Consts.POST_CD);
		String authorityCd = (String) loginInfo.get(Consts.USER_AUTHORITY_CD);
		ukg02030Data.put("authorityCd", authorityCd);
		// ########## 2014/06/10 郭凡 UPD End
		String tantouCd = loginInfo.get(Consts.USER_CD).toString();
		// 2013/03/04  SYS_趙雲剛  ADD Start  (案件C37058)社長指示対応
		session.removeAttribute("pageFlag");
		// 2013/03/04  SYS_趙雲剛  ADD End
		String uketukeNo = "";

		// 遷移元画面を判定uketukeNo
		if (Consts.GAMENID_UKG01011.equals(request.getParameter("historyPageId"))) {

			// 遷移前画面が受付未登録一覧
			uketukeNo = request.getParameter("uketukeNo");
			ukg02030Data.put("beforeGamen", Consts.GAMENID_UKG01011);

			// セッションに受付未登録一覧遷移フラグを設定する。
			session.setAttribute(Consts.UKETUKENO, uketukeNo);
			session.setAttribute(Consts.UKG01011_FLG, Consts.GAMENID_UKG01011);
		} else {
			uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
			ukg02030Data.put("beforeGamen", request.getParameter("historyPageId"));
		}

		if (Consts.GAMENID_UKG01011.equals(session.getAttribute(Consts.UKG01011_FLG))) {
			ukg02030Data.put("beforeGamen", Consts.GAMENID_UKG01011);
		}
		//########## 2017/08/16 MJEC)鈴村 ADD Start (案件C38101-1)ホームメイト自動受付対応
		// ホームメイト自動受付一覧フラグがonの場合、セッション情報を初期化する。
		if (session.getAttribute(Consts.UKG01016_FLG) != null) {
			session.setAttribute(Consts.UKG01016_FLG, null);
		}
		//########## 2017/08/16 MJEC)鈴村 ADD End
		ukg02030Data.put("uketukeNo", uketukeNo);
		kibouJyoukennBean = new KibouJyoukennBean();
		// 希望条件データをセッションに保存する
		session.setAttribute(Consts.KIBOUJYOUKENBEAN, kibouJyoukennBean);

		kibouJyoukennBean.setKaisyaCd(kaisyaCd);
		kibouJyoukennBean.setUketukeNo(uketukeNo);
		kibouJyoukennBean.setTantouCd(tantouCd);
		kibouJyoukennBean.setPostCd(postCd);

		// お客様情報取得
		getCostomerData(kaisyaCd, uketukeNo);

		// 画面上のリストデータ取得
		getGamenListCmData();

		// 希望条件データ取得
		// 2014/03/11  SYS_ 謝超  ADD Start  (案件C37075)オリジナル情報誌リプレース
		//getInitInfo(kaisyaCd, uketukeNo,kibouJyoukennBean);
		getInitInfo(kaisyaCd, uketukeNo, postCd,kibouJyoukennBean);
		// 2014/03/11  SYS_ 謝超 UPD End

		session.setAttribute(Consts.KIBOUJYOUKENBEAN, kibouJyoukennBean);


		// 2017/09/25 SYS_ヨウ強  ADD START C41004_法人仲介担当者登録機能追加対応
		// 受付情報作成区分を取得
		String sakuSeiKb = (String)((Map<String, Object>)ukg02030Data.get("customerData")).get("SAKUSEI_KB");
		// プロパティファイルからPDF保存可能数を取得
		String pdfRootPath = PropertiesReader.getIntance().getProperty("pdfRootPath");
		// PDFパラメータを設定
		ukg02030Data.put("pdfRootPath", pdfRootPath);
		if (Consts.UKG02030_SAKUSEI_KB.equals(sakuSeiKb)) {
			// 法人紹介依頼元受付No
			String iraiMotoUketukeNo = (String)((Map<String, Object>)ukg02030Data.get("customerData")).get("HOUJIN_IRAIMOTO_UKETUKE_NO");
			// アップロード日時の取得
			getUploadDate(kaisyaCd,iraiMotoUketukeNo);
			// 法人希望部屋数を取得
			getHoujinKibouHeyaSuu(kaisyaCd,uketukeNo);
			// 仲介依頼先店舗情報を取得
			getTenpoInfo(kaisyaCd,iraiMotoUketukeNo);
		}
		// 2017/09/25 SYS_ヨウ強  ADD END C41004_法人仲介担当者登録機能追加対応、

		//2015/02/12 温(SYS) ADD Start (案件番号C39042)受付レスポンス対応
		List<ToiawaseInfoBean> bukkenList = kibouJyoukennBean.getBukkenInfoList();
		if(bukkenList != null){
			for(int i = 0; i < bukkenList.size(); i++){
				//初期化部屋情報取得
				ToiawaseInfoBean toiawaseInfo = bukkenList.get(i);
				heyaInfoInit(toiawaseInfo);
			}
		}
		//画面上各リスト取得
		getBukkenSbKbnList();
		//初期化物件、部屋数取得
		bukkenCountSearchInit(kibouJyoukennBean);

		// 2018/01/22 SYS_肖剣生 ADD START (案件番号C42036)デザイン変更
		// 路線サーブレットURL
		ukg02030Data.put("rosenServleturl",PropertiesReader.getIntance().getProperty(Consts.ROSEN_SERVLETURL));
		// 路線キーコード
		ukg02030Data.put("rosenKeycode",PropertiesReader.getIntance().getProperty(Consts.ROSEN_KEYCODE));
		// 県コード
		String prefectureCd = kibouJyoukennBean.getPrefectureCd();
		if (prefectureCd == null || "".equals(prefectureCd)) {
			prefectureCd = (String) uketukePostInfo.get(Consts.POST_JUSYO_CD);
		}
		ukg02030Data.put("POST_JUSYO_CD", prefectureCd);
		// 2018/01/22 SYS_肖剣生 ADD END (案件番号C42036)デザイン変更

		session.setAttribute("ukg02030Data", ukg02030Data);
		//2015/02/12 温(SYS) ADD End

		return successForward();
	}

	//2015/02/12 温(SYS) ADD Start (案件番号C39042)受付レスポンス対応
	/**
	 * <pre>
	 * [説 明] 初期化部屋情報取得
	 * @param toiawaseInfo 問い合わせ物件情報
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public void heyaInfoInit(ToiawaseInfoBean toiawaseInfo) throws Exception {

		//部屋情報取得
		Map<String, Object> heyaInfoData = UKG02030Services.getHeyaInfo(toiawaseInfo.getBukkenNo(), toiawaseInfo.getHeyaNo());
		List<Map<String, Object>> heyaInfo = (List<Map<String, Object>>)heyaInfoData.get("RST_HEYA_INFO");
		if(heyaInfo.size() > 0){
			//入居区分
			toiawaseInfo.setNyukuoSbCd((String)heyaInfo.get(0).get("NYUKYO_SB_CD"));
		}
		//その他部屋数
		toiawaseInfo.setHeyaCnt((String)heyaInfoData.get("RST_KUSHITUCNT"));

	}
	/**
	 * <pre>
	 * [説 明] 初期化物件、部屋数取得
	 * @param kibouJyoukennBean 希望条件情報
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public void bukkenCountSearchInit(KibouJyoukennBean kibouJyoukennBean) throws Exception {

		//物件、部屋数の取得前検索条件の変換
		List<Map<String, Object>> ekiTimeInfo = (List<Map<String, Object>>)ukg02030Data.get("ekiKaraTime");
		List<Map<String, Object>> mensekiInfo = (List<Map<String, Object>>)ukg02030Data.get("kibouMensekiList");
		//駅時間、面積
		String ekiMiniute = kibouJyoukennBean.getEkiMiniuteCd();
		String mensekiDown = kibouJyoukennBean.getKibouMensekiDown();
		String mensekiTop = kibouJyoukennBean.getKibouMensekiTop();
		//駅時間
		for(int i = 0; i < ekiTimeInfo.size(); i++){
			Map<String, Object> ekiTime = (Map<String, Object>)ekiTimeInfo.get(i);
			if(ekiMiniute.equals(ekiTime.get("KOUMOKU_SB_CD"))){
				kibouJyoukennBean.setEkiMiniute((String)ekiTime.get("KOUMOKU_SB_VAL"));
				break;
			}
		}
		//面積の上限、下限
		for(int i = 0; i < mensekiInfo.size(); i++){
			Map<String, Object> menseki = (Map<String, Object>)mensekiInfo.get(i);
			if(mensekiDown.equals(menseki.get("KOUMOKU_SB_CD"))){
				kibouJyoukennBean.setKibouMensekiDownVal((String)menseki.get("KOUMOKU_SB_VAL"));
			}
			if(mensekiTop.equals(menseki.get("KOUMOKU_SB_CD"))){
				kibouJyoukennBean.setKibouMensekiTopVal((String)menseki.get("KOUMOKU_SB_VAL"));
			}
		}
		// 物件、部屋数の取得
		// 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
        // Map<String, Object> resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean);
        Map<String, Object> resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean,Consts.BUKEN_COUNT_All);
        // 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応

		String bukkenCount = resul.get("bukkenCount").toString();
		String heyaCount = resul.get("heyaCount").toString();
		// 部屋数の設定
		kibouJyoukennBean.setHeyaCount(heyaCount);
		// 物件数の設定
		kibouJyoukennBean.setBukkenCount(bukkenCount);
		// 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean,Consts.BUKEN_COUNT_TOKUTEN);
		bukkenCount = resul.get("bukkenCount").toString();
		kibouJyoukennBean.setBukkenTokutenCount(bukkenCount);
		// 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
	}
	//2015/02/12 温(SYS) ADD End

	// 初期化のデータの取得 // parasoft-suppress UC.ACC "C39042Jtest対応"
	// ########## 2014/01/06 謝超UPD Start (案件C37075)オリジナル情報誌リプレース // parasoft-suppress UC.ACC "C39042Jtest対応"
	//	private void getInitInfo(String kaisyaCd, // parasoft-suppress UC.ACC "C39042Jtest対応"
	//            String uketukeNo,
	//            KibouJyoukennBean kiboujyoukenBean) throws Exception {
	private void getInitInfo(String kaisyaCd,
	                         String uketukeNo,
	                         String postCd,
	                         KibouJyoukennBean kiboujyoukenBean) throws Exception {

		// データベースから画面用リストデータ取得する // parasoft-suppress UC.ACC "C39042Jtest対応"
		// BukkenSearchService.getInitInfo(kaisyaCd, uketukeNo, kiboujyoukenBean); // parasoft-suppress UC.ACC "C39042Jtest対応"
		kiboujyoukenBean.setKaisyaCd(kaisyaCd);
		kiboujyoukenBean.setUketukeNo(uketukeNo);
		kiboujyoukenBean.setPostCd(postCd);
		kiboujyoukenBean.setGamenKubun(Consts.GAMENID_UKG02030);
		bukkenSearchService.getInitInfo(kiboujyoukenBean);
		// ########## 2014/01/06 謝超UPD End
	}

	/**
	 * <pre>
	 * [説 明] 画面上リストデータ取得
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	private void getGamenListCmData() throws Exception {

		// データベースから画面用リストデータ取得する
		Map<String, Object> commonData = UKG02030Services.getCommonData();
		// 法人希望部屋数リスト
		ukg02030Data.put("kibouHeyaSuuList", (List<Map<String, Object>>) commonData.get("RST_KIBOUHEYASUU"));
		// 入居時期コードリスト
		ukg02030Data.put("kibouJikiList", (List<Map<String, Object>>) commonData.get("RST_KIBOUJIKI"));

		// 2013/1/23  楊きょう(SYS)  UPD START 案件C37058 // parasoft-suppress UC.ACC "C39042Jtest対応"
		// 築年数 // parasoft-suppress UC.ACC "C39042Jtest対応"
		//ukg02030Data.put("tikunensuuList", (List<Map<String, Object>>) commonData.get("RST_TIKUNENSUU")); // parasoft-suppress UC.ACC "C39042Jtest対応"
		List<Map<String, Object>> tikunensuuList = (List<Map<String, Object>>) commonData.get("RST_TIKUNENSUU");

		ukg02030Data.put("tikunensuuList",tikunensuuList);
		// 2013/1/23  楊きょう(SYS)  UPD END 案件C37058

		// 物件区分
		ukg02030Data.put("bukkenKbList", (List<Map<String, Object>>) commonData.get("RST_BUKKENKB"));
		// 物件種別
		ukg02030Data.put("bukkenSbList", (List<Map<String, Object>>) commonData.get("RST_BUKKENSBLIST"));
		// 駐車場フラグ処理
		ukg02030Data.put("parkFlgList", (List<Map<String, Object>>) commonData.get("RST_PARKFLG_LIST"));
		// 希望間取り
		ukg02030Data.put("kiboMadori", (List<Map<String, Object>>) commonData.get("RST_KIBOUMATORI"));
		// 駅らか徒歩時間
		ukg02030Data.put("ekiKaraTime", (List<Map<String, Object>>) commonData.get("RST_WALKING"));
		// 居住用の初期費用
		ukg02030Data.put("initMoneyList", (List<Map<String, Object>>) commonData.get("RST_INITMONEY_LIST"));
		// 事業用の初期費用
		ukg02030Data.put("initMoneyList2", (List<Map<String, Object>>) commonData.get("RST_INITMONEY_LIST2"));
		// 人気情報の取得
		ukg02030Data.put("ninkiInfoDisp", (List<Map<String, Object>>) commonData.get("RST_NINKI_INFO"));

		// 2013/1/23  楊きょう(SYS)  ADD START 案件C37058
		//入居即時区分
		List<Map<String, Object>> sokujiKbListData = (List<Map<String, Object>>) commonData.get("RST_SOKUJI_KB_LIST");

		ukg02030Data.put("sokujiKbList", sokujiKbListData);

		//入居時期種別
		ukg02030Data.put("jikiKbList", (List<Map<String, Object>>) commonData.get("RST_JIKI_SB_LIST"));

		// 2013/1/23  楊きょう(SYS)  ADD END 案件C37058

		// 入居年リスト取得
		yearListEdit();
		// 入居月リスト取得
		monthListEdit();
	}

	/**
	 * <pre>
	 * [説 明] 入居時期の年リストを取得
	 * @throws Exception 処理異常
	 * </pre>
	 */
	private void yearListEdit() {

		// カレンダークラスから該当年数を取得
		int year1 = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MARCH);
		if (month == 11) {
			year1++;
		}
		int year2 = year1 + 1;

		// 文字列の年数編集
		String strYear01 = String.valueOf(year1);
		String strYear02 = String.valueOf(year2);

		List<Map<String, String>> yearList = new ArrayList<Map<String, String>>();

		String kibouYear = String.valueOf(ukg02030Data.get("KIBOU_NYUKYO_YEAR"));
		if (!Util.isEmpty(kibouYear) && !kibouYear.equals(strYear01) && !kibouYear.equals(strYear02)) {
			Map<String, String> objYear00 = new HashMap<String, String>();
			objYear00.put("yearCd", kibouYear);
			objYear00.put("yearNm", kibouYear + Consts.KANJI_YEAR);
			yearList.add(objYear00);
		}

		Map<String, String> objYear01 = new HashMap<String, String>();
		objYear01.put("yearCd", strYear01);
		objYear01.put("yearNm", strYear01 + Consts.KANJI_YEAR);
		yearList.add(objYear01);

		Map<String, String> objYear02 = new HashMap<String, String>();
		objYear02.put("yearCd", strYear02);
		objYear02.put("yearNm", strYear02 + Consts.KANJI_YEAR);
		yearList.add(objYear02);

		ukg02030Data.put("yearList", yearList);
	}

	/**
	 * <pre>
	 * [説 明] 入居時期の月リストを取得
	 * @throws Exception 処理異常
	 * </pre>
	 */
	private void monthListEdit() {

		List<Map<String, String>> monthList = new ArrayList<Map<String, String>>();
		for (int i = 1; i < Consts.MOUTHCOUNT; i++) {
			Map<String, String> objMonth = new HashMap<String, String>();
			String month = String.valueOf(i);

			if (i < 10) {
				objMonth.put("monthCd", Consts.STR_ZERO + month);
				objMonth.put("monthNm", month + Consts.KANJI_MONTH);
				monthList.add(objMonth);
			} else {
				objMonth.put("monthCd", month);
				objMonth.put("monthNm", month + Consts.KANJI_MONTH);
				monthList.add(objMonth);
			}
			ukg02030Data.put("monthList", monthList);
		}
	}

	/**
	 * 問合せ偉№により、物件情報の取得
	 * bukkenInfoGet()
	 *
	 * @throws Exception
	 */
	public String bukkenInfoGet() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// セッション情報取得
		Map<String, Object> loginInfo = getLoginInfo();

		// 問い合わせ№の1、2№の連結取得
		String toiawaseNo = request.getParameter("toiawaseNo");
		// 会社コード
		String kaisyaCd = loginInfo.get(Consts.KAISYA_CD).toString();

		ukg02030Data = new HashMap<String, Object>();
		Map<String, Object> bukkenInfoData = UKG02030Services.getBukkenInfo(kaisyaCd, toiawaseNo);
		ukg02030Data.put("bukkenInfo", bukkenInfoData.get("RST_BUKKEN_INFO"));

		return "ajaxProcess";
	}

	/**
	 * 問合せ№により、部屋情報の取得
	 * heyaInfoGet()
	 *
	 * @throws Exception
	 */
	public String heyaInfoGet() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// 部屋№
		String toiawaseNo = request.getParameter("toiawaseNo");
		// 会社コード
		String bukkenNo = request.getParameter("bukkenNo");

		ukg02030Data = new HashMap<String, Object>();
		Map<String, Object> heyaInfoData = UKG02030Services.getHeyaInfo(bukkenNo, toiawaseNo);
		ukg02030Data.put("heyaInfo", heyaInfoData.get("RST_HEYA_INFO"));
		ukg02030Data.put("kushituCnt", heyaInfoData.get("RST_KUSHITUCNT"));

		return "ajaxProcess";
	}

	/**
	 * 問合せ№により、その他部屋情報の取得
	 * heyaInfoGet()
	 *
	 * @throws Exception
	 */
	public String sonotaHeyaInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// 部屋№
		String toiawaseNo = request.getParameter("toiawaseNo");
		// 会社コード
		String bukkenNo = request.getParameter("bukkenNo");

		ukg02030Data = new HashMap<String, Object>();
		Map<String, Object> heyaInfoData = UKG02030Services.getSonotaHeyaInfo(bukkenNo, toiawaseNo);
		ukg02030Data.put("sonotaHeyaInfo", heyaInfoData.get("RST_HEYA_INFO"));

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] お客様情報データを取得
	 * @param  会社コード
	 * @param  受付番号
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	private void getCostomerData(String kaisyaCd,
	                             String uketukeNo) throws Exception {

		// お客様情報データを取得する
		Map<String, Object> customerData = UKG02030Services.getCustomerData(kaisyaCd, uketukeNo);

		List<Map<String, Object>> customerList = (List<Map<String, Object>>) customerData.get("rst_record");

		if (customerList.size() == 0) {
			throw new DataNotFoundException();
		}
		Map<String, Object> customerDataMap = customerList.get(0);
		customerDataMap.put("UKETUKE_NO", DispFormat.getUketukeNo(customerDataMap.get("UKETUKE_NO").toString()));
		// 2017/09/25 SYS_ヨウ強 ADD START C41004_法人仲介担当者登録機能追加対応
		// 法人紹介依頼元受付Noを設定する
		customerDataMap.put("HOUJIN_IRAIMOTO_UKETUKE_NO", customerDataMap.get("HOUJIN_IRAIMOTO_UKETUKE_NO"));
		// 受付情報作成区分を設定する
		customerDataMap.put("SAKUSEI_KB", customerDataMap.get("SAKUSEI_KB"));
		// 2017/09/25 SYS_ヨウ強 ADD END C41004_法人仲介担当者登録機能追加対応
		ukg02030Data.put("customerData", customerDataMap);
	}

	// 2013/1/23  楊きょう(SYS)  ADD START 案件C37058
	/**
	 * メモ更新処理
	 *
	 * @return
	 */
	public String updMemoInfo() throws Exception {

		// セッション情報取得
		Map<String, Object> loginInfo = getLoginInfo();

		HttpServletRequest request = ServletActionContext.getRequest();
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		String tantousya = String.valueOf(loginInfo.get(Consts.USER_CD));
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		String upDate = request.getParameter("update");
		String memoNaiyou = request.getParameter("memo");
		// 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
        String kokyakuName = request.getParameter("kokyakuName");
        String kokyakuNameKana = request.getParameter("kokyakuNameKana");
        String telNo = request.getParameter("telNo");
        String emailNo = request.getParameter("emailNo");
        String flg = request.getParameter("flg");
        String sinpoflg = request.getParameter("sinpoflg");
		// 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		// 初期データを取得する
        // 2019/06/20 UPD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		// Map<String, Object> returnInfo = UKG02030Services.updMemoInfo(kaisyaCd, uketukeNo, upDate,
		//                                                              tantousya, memoNaiyou);
		Map<String, Object> returnInfo = UKG02030Services.updMemoInfo(kaisyaCd, uketukeNo, upDate,
                tantousya, memoNaiyou,kokyakuName,kokyakuNameKana,telNo,emailNo,flg,sinpoflg);
        // 2019/06/20 UPD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応

		String errMsg = "";
		// 更新処理失敗の場合
		errMsg = errMsg(String.valueOf(returnInfo.get("rst_code")));
		// エラーメッセージをセットする
		ukg02030Data = new HashMap<String, Object>();
		ukg02030Data.put("errMsg", errMsg);
		ukg02030Data.put("memoUpdate", returnInfo.get("rst_update"));
		return "ajaxProcess";
	}

	/**
	 * ｄｂ操作失敗の場合、エラーメッセージを取得する
	 *
	 * @return
	 */
	private String errMsg(String msgId) {

		String errMsg = "";
		// 更新処理失敗の場合
		if ("ERRS009".equals(msgId)) {
			errMsg = Util.getServerMsg("ERRS009");
		} else if ("ERRS002".equals(msgId) || "1111111".equals(msgId)) {
			errMsg = Util.getServerMsg("ERRS002", "更新処理");
		}
		return errMsg;
	}
	// 2013/1/23  楊きょう(SYS)  ADD END 案件C37058

	/**
	 * 市リスト取得処理
	 * getJusyo2List()
	 *
	 * @throws Exception
	 */
	public String jusyo2List() throws Exception {

		ukg02030Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		String kenCode = request.getParameter("jusyo2_kenCode");

		Map<String, Object> jusyo2ListData = UKG02030Services.getJusyo2List(kenCode);
		ukg02030Data.put("jusyo2List", jusyo2ListData.get("rst_list"));

		return "ajaxProcess";
	}

	/**
	 * 町・大字一覧取得取得処理
	 * getJusyo3List()
	 *
	 * @throws SQLException
	 *             SQL例外
	 * @throws Exception
	 *             一般例外
	 */
	public String jusyo3List() throws Exception {

		ukg02030Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		String kenCode = request.getParameter("jusyo3_kenCode");
		String cityCode = request.getParameter("jusyo3_cityCode");

		Map<String, Object> jusyo3ListData = UKG02030Services.getJusyo3List(kenCode, cityCode);
		ukg02030Data.put("jusyo3List", jusyo3ListData.get("rst_list"));

		return "ajaxProcess";
	}

	/**
	 * 都道府県の路線一覧取得
	 * getRosenList()
	 *
	 * @throws SQLException
	 *             SQL例外
	 * @throws Exception
	 *             一般例外
	 */
	public String rosenList() throws Exception {

		ukg02030Data = new HashMap<String, Object>();
		// 都道府県コードと路線コード取得する
		HttpServletRequest request = ServletActionContext.getRequest();
		String kenCd = request.getParameter("rosen_kenCode");
		String rosenCd = request.getParameter("rosen_rosenkb");

		Map<String, Object> rosenListData = UKG02030Services.getRosenList(kenCd, rosenCd);
		ukg02030Data.put("rosenList", rosenListData.get("rst_list"));

		return "ajaxProcess";
	}

	/**
	 * 都道府県の県コードにより、路線区分を取得する
	 * rosenKbnListGet()
	 *
	 * @throws SQLException
	 *             SQL例外
	 * @throws Exception
	 *             一般例外
	 */
	public String rosenKbnListGet() throws Exception {

		ukg02030Data = new HashMap<String, Object>();
		// 都道府県コードと路線コード取得する
		HttpServletRequest request = ServletActionContext.getRequest();
		String kenCd = request.getParameter("kenCd");

		Map<String, Object> rosenListData = UKG02030Services.getRosenKbnList(kenCd);
		ukg02030Data.put("rosenKbnList", rosenListData.get("rst_list"));

		return "ajaxProcess";
	}

	/**
	 * 路線の駅一覧取得
	 * getRosenList()
	 *
	 * @throws SQLException
	 *             SQL例外
	 * @throws Exception
	 *             一般例外
	 */
	public String ekiList() throws Exception {

		ukg02030Data = new HashMap<String, Object>();
		// 路線コード取得
		String rosenCd = ServletActionContext.getRequest().getParameter("eki_rosenCode");

		ukg02030Data.put("ekiList", UKG02030Services.getEkiList(rosenCd).get("rst_list"));

		return "ajaxProcess";
	}

	/**
	 * 路線の駅一覧取得
	 * getRosenList()
	 *
	 * @throws SQLException
	 *             SQL例外
	 * @throws Exception
	 *             一般例外
	 */
	public String rosenKbnGet() throws Exception {

		ukg02030Data = new HashMap<String, Object>();
		// 路線コード取得
		String rosenCd = ServletActionContext.getRequest().getParameter("rosenCd");

		Map<String, Object> list = UKG02030Services.getRosenKbn(rosenCd);
		ukg02030Data.put("ROSEN_KB", list.get("rst_rosen_kb"));

		return "ajaxProcess";
	}

	/**
	 * 保存ボタン押下処理
	 * kokyakuJyouho()
	 *
	 * @throws SQLException
	 *             SQL例外
	 * @throws Exception
	 *             一般例外
	 */
	public String doSave() throws Exception {

		ukg02030Data = new HashMap<String, Object>(); // parasoft-suppress CDD.DUPC "36029JTest対応"
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);

		// 物件№取得
		kibouJyoukennBean.setBukkenKb(request.getParameter("bukkenkb"));

		if (!"1".equals(kibouJyoukennBean.getBukkenKb())) {
			int cnt = Integer.parseInt(PropertiesReader.getIntance().getProperty("setubiCount"));
			// 必須もの設定
			kibouJyoukennBean.setMustSetubiBin(Util.StringTume("", cnt, '0'));
			kibouJyoukennBean.setMustSetubi("0");
			// 必須ではない設定
			kibouJyoukennBean.setNoMustSetubiBin(Util.StringTume("", cnt, '0'));
			kibouJyoukennBean.setNoMustSetubi("0");
		}
		kibouJyoukennBean.setBukkenSb(request.getParameter("bukkensb"));
		kibouJyoukennBean.setTikunenCd(request.getParameter("tikunensuCd"));
		kibouJyoukennBean.setYatinLow(request.getParameter("yatinFrom"));
		kibouJyoukennBean.setYatinTop(request.getParameter("yatinTo"));
		kibouJyoukennBean.setKyokiKanriHiFlg(request.getParameter("kyokiKanriHiFlg"));
		kibouJyoukennBean.setParkHiFlg(request.getParameter("parkHiFlg"));
		kibouJyoukennBean.setRekinZero(request.getParameter("rekinZero"));
		kibouJyoukennBean.setSikihosyoukinZero(request.getParameter("sikihosyoukinZero"));
		kibouJyoukennBean.setInitMoneyTotal(request.getParameter("initMoneyTotal"));
		kibouJyoukennBean.setKiboMardoriList(request.getParameter("madori"));
		kibouJyoukennBean.setEkiMiniute(request.getParameter("ekiMiniute"));
		kibouJyoukennBean.setEkiMiniuteCd(request.getParameter("ekiMiniuteCd"));
		kibouJyoukennBean.setKibouMensekiDown(request.getParameter("kibouMensekiDown"));
		kibouJyoukennBean.setKibouMensekiTop(request.getParameter("kibouMensekiTop"));
		kibouJyoukennBean.setParkMustFlg(request.getParameter("parkMustFlg"));
		kibouJyoukennBean.setParkSuu(request.getParameter("parkSuu"));
		kibouJyoukennBean.setNyukyuOtona(request.getParameter("nyukyuOtona"));
		kibouJyoukennBean.setNyukyuKodomo(request.getParameter("nyukyuKodomo"));
		kibouJyoukennBean.setHoujinkibouHeyaSuu(request.getParameter("houjinkibouHeyaSuu"));
		kibouJyoukennBean.setHoujinkibouHeyaSuuList(request.getParameter("houjinkibouHeyaSuuList"));
		kibouJyoukennBean.setKibounyukyuYear(request.getParameter("kibounyukyuYear"));
		kibouJyoukennBean.setKibounyukyuMonth(request.getParameter("kibounyukyuMonth"));
		kibouJyoukennBean.setKibounyukyuTime(request.getParameter("kibounyukyuTime"));
		kibouJyoukennBean.setToiawase(request.getParameter("toiawase"));
		kibouJyoukennBean.setKyakuInfoFlg(request.getParameter("kokyaku_info_flg"));
		kibouJyoukennBean.setAnketoFlg(request.getParameter("anketo_flg"));
		kibouJyoukennBean.setUketukeKbn(request.getParameter("uketuke_kb"));

		// 2013/1/23  楊きょう(SYS)  ADD START 案件C37058
		kibouJyoukennBean.setSokujiKb(request.getParameter("sokujiKb"));
		kibouJyoukennBean.setJikiKb(request.getParameter("jikiKb"));
		// 2013/1/23  楊きょう(SYS)  ADD END 案件C37058
		if ("1".equals(kibouJyoukennBean.getEkiMiniuteCd())) {
			kibouJyoukennBean.setSearchMod("1");
		} else {
			kibouJyoukennBean.setSearchMod("2");
		}

		// ########## 2014/01/06 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース // parasoft-suppress UC.ACC "C39042Jtest対応"
		// String rstCd = BukkenSearchService.save(kibouJyoukennBean); // parasoft-suppress UC.ACC "C39042Jtest対応"
	    // 2019/08/05 UPD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		// String rstCd = bukkenSearchService.save(kibouJyoukennBean);
		Map<String, Object> saveData= bukkenSearchService.save(kibouJyoukennBean);
		String rstCd=saveData.get("rst_code").toString();
	    // 2019/08/05 UPD UPD 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		// ########## 2014/01/06 謝超 UPD End

		String errMsg = "";
		// 登録処理失敗する場合
		if (Consts.ERRS999.equals(rstCd)) {
			errMsg = Util.getServerMsg(Consts.ERRS002, Consts.STR_TOROKU);
		}

		// 更新成功の場合
		if ("0000000".equals(rstCd)) {
			errMsg = Util.getServerMsg(Consts.MSGS001, Consts.STR_TOROKU);
			ukg02030Data.put("saveFlg", Consts.SAVE_SUCCESS);
			ukg02030Data.put("rirekiSeq", kibouJyoukennBean.getRirekiSeq());
		    // 2019/08/05 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
	         ukg02030Data.put("update",saveData.get("rst_update").toString());
		    // 2019/08/05 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
			session.setAttribute(Consts.KIBOUJYOUKENBEAN, kibouJyoukennBean);
		}
		ukg02030Data.put("rstMsg", errMsg);

		return "ajaxProcess";
	}

	/**
	 * 物件種別より各リスト取得
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String bukkenSbKbnListGet() throws Exception {

		//2015/02/12 温(SYS) DEL Start (案件番号C39042)受付レスポンス対応
		//ukg02030Data = new HashMap<String, Object>();
		//2015/02/12 温(SYS) DEL End
		// 物件№
		HttpServletRequest request = ServletActionContext.getRequest();
		// 物件№取得 // parasoft-suppress UC.ACC "C39042Jtest対応"
		//String bukkenKbn = request.getParameter("bukkenkb"); // parasoft-suppress UC.ACC "C39042Jtest対応"

		kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);

		// 物件区分リスト取得 // parasoft-suppress UC.ACC "C39042Jtest対応"
		//getBukkenSbKbnList(bukkenKbn); // parasoft-suppress UC.ACC "C39042Jtest対応"
		//2015/02/12 温(SYS) UPD Start (案件番号C39042)受付レスポンス対応
		//getBukkenSbKbnList();
		ukg02030Data = (Map<String,Object>) request.getSession().getAttribute("ukg02030Data");
		request.getSession().removeAttribute("ukg02030Data");
		//2015/02/12 温(SYS) UPD End
		return "ajaxProcess";
	}

	/**
	 * 物件区分により画面上各リスト取得
	 *
	 * @param bukkenKbn
	 */
	//private void getBukkenSbKbnList(String bukkenKbn) throws Exception { // parasoft-suppress UC.ACC "C39042Jtest対応"
    @SuppressWarnings("unchecked")
	private void getBukkenSbKbnList() throws Exception {
		Map<String, Object> commonData = UKG02030Services.getBukkenSbKbnList();

		//2015/02/12 温(SYS) Del Start (案件番号C39042)受付レスポンス対応
		//ukg02030Data = new HashMap<String, Object>();
		//2015/02/12 温(SYS) Del End

		// 物件区分が居住用の場合
		// 駐車場台数リスト
		ukg02030Data.put("parkingSuuList1", commonData.get("rst_parking_list1"));
		// 家賃リスト(居住用)
		ukg02030Data.put("rst_yatinlist1", commonData.get("rst_yatin_list1"));
		// 入居人数リスト
		ukg02030Data.put("nyuukyoninnzuList", commonData.get("rst_nyuukyoninnzu"));

		// 人気条件簡単選択のDIVのデータ
		ukg02030Data.put("ninkiInfoList", commonData.get("rst_ninki_info"));

		// こだわり物件データの取得 // parasoft-suppress UC.ACC "C39042Jtest対応"
		// ########## 2014/01/06 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース // parasoft-suppress UC.ACC "C39042Jtest対応"
		// Map<String, Object> map = BukkenSearchService.getKodawariInfo(kibouJyoukennBean); // parasoft-suppress UC.ACC "C39042Jtest対応"
		Map<String, Object> map = bukkenSearchService.getKodawariInfo(kibouJyoukennBean);
		// ########## 2014/01/06 謝超 UPD End
		ukg02030Data.put("kodawariInfo", map.get("RST_KODAWARI_DATA"));
		ukg02030Data.put("ninkiInfo", map.get("RST_NINKI_DATA"));

		// 物件区分が事業用の場合
		// 駐車場台数リスト
		ukg02030Data.put("parkingSuuList2", commonData.get("rst_parking_list2"));
		// 家賃リスト(事業用)
		ukg02030Data.put("rst_yatinlist2", commonData.get("rst_yatin_list2"));
		// 専有面積
		ukg02030Data.put("kibouMensekiList", commonData.get("rst_senyou_area"));

		// 物件区分が駐車場の場合
		// 駐車場台数（「駐車場」）
		ukg02030Data.put("parkingSuuList3", commonData.get("rst_parking_list3"));
		// 駐車料金
		ukg02030Data.put("rst_yatinlist3", commonData.get("rst_yatin_list3"));
        // 2020/03/26 楊朋朋(SYS) ADD Start C44072_希望部屋数等を追加
        //希望部屋数コードリスト。
        ukg02030Data.put("selKibouHayaSuuCdList", (List<Map<String, Object>>) UKG02010Services
                .getShitsuSuuList().get("rst_houjinkiboushitsusuu_list"));
        // 2020/03/26 楊朋朋(SYS) ADD End C44072_希望部屋数等を追加
	}

	/**
	 * 物件の数の検索
	 *
	 * @return
	 */
	public String bukkenCountSearch() throws Exception {

		ukg02030Data = new HashMap<String, Object>();
		// 物件№
		HttpServletRequest request = ServletActionContext.getRequest();

		HttpSession session = request.getSession();

		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);

		// 物件№取得
		kibouJyoukennBean.setBukkenKb(request.getParameter("bukkenkb"));

		if (!"1".equals(kibouJyoukennBean.getBukkenKb())) {
			int cnt = Integer.parseInt(PropertiesReader.getIntance().getProperty("setubiCount"));
			// 必須もの設定
			kibouJyoukennBean.setMustSetubiBin(Util.StringTume("", cnt, '0'));
			kibouJyoukennBean.setMustSetubi("0");
			// 必須ではない設定
			kibouJyoukennBean.setNoMustSetubiBin(Util.StringTume("", cnt, '0'));
			kibouJyoukennBean.setNoMustSetubi("0");
		}

		kibouJyoukennBean.setBukkenSb(request.getParameter("bukkensb"));
		kibouJyoukennBean.setTikunenCd(request.getParameter("tikunensuCd"));
		kibouJyoukennBean.setYatinLow(request.getParameter("yatinFrom"));
		kibouJyoukennBean.setYatinTop(request.getParameter("yatinTo"));
		kibouJyoukennBean.setKyokiKanriHiFlg(request.getParameter("kyokiKanriHiFlg"));
		kibouJyoukennBean.setParkHiFlg(request.getParameter("parkHiFlg"));
		kibouJyoukennBean.setRekinZero(request.getParameter("rekinZero"));
		kibouJyoukennBean.setSikihosyoukinZero(request.getParameter("sikihosyoukinZero"));
		kibouJyoukennBean.setInitMoneyTotal(request.getParameter("initMoneyTotal"));
		kibouJyoukennBean.setKiboMardoriList(request.getParameter("madori"));
		kibouJyoukennBean.setEkiMiniute(request.getParameter("ekiMiniute"));
		kibouJyoukennBean.setEkiMiniuteCd(request.getParameter("ekiMiniuteCd"));
		kibouJyoukennBean.setKibouMensekiDown(request.getParameter("kibouMensekiDown"));
		kibouJyoukennBean.setKibouMensekiTop(request.getParameter("kibouMensekiTop"));
		kibouJyoukennBean.setParkMustFlg(request.getParameter("parkMustFlg"));
		kibouJyoukennBean.setParkSuu(request.getParameter("parkSuu"));
		kibouJyoukennBean.setNyukyuOtona(request.getParameter("nyukyuOtona"));
		kibouJyoukennBean.setNyukyuKodomo(request.getParameter("nyukyuKodomo"));
		kibouJyoukennBean.setHoujinkibouHeyaSuu(request.getParameter("houjinkibouHeyaSuu"));
		kibouJyoukennBean.setHoujinkibouHeyaSuuList(request.getParameter("houjinkibouHeyaSuuList"));
		kibouJyoukennBean.setKibounyukyuYear(request.getParameter("kibounyukyuYear"));
		kibouJyoukennBean.setKibounyukyuMonth(request.getParameter("kibounyukyuMonth"));
		kibouJyoukennBean.setKibounyukyuTime(request.getParameter("kibounyukyuTime"));
		kibouJyoukennBean.setYatinLowVal(request.getParameter("yatinFromVal"));
		kibouJyoukennBean.setYatinTopVal(request.getParameter("yatinToVal"));
		kibouJyoukennBean.setKibouMensekiDownVal(request.getParameter("kibouMensekiDownVal"));
		kibouJyoukennBean.setKibouMensekiTopVal(request.getParameter("kibouMensekiTopVal"));

		if ("1".equals(kibouJyoukennBean.getEkiMiniuteCd())) {
			kibouJyoukennBean.setSearchMod("1");
		} else {
			kibouJyoukennBean.setSearchMod("2");
		}

		// ########## 2014/01/06 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース // parasoft-suppress UC.ACC "C39042Jtest対応"
		// Map<String, Object> resul = BukkenSearchService.getBukkenCount(kibouJyoukennBean); // parasoft-suppress UC.ACC "C39042Jtest対応"
		// 2019/06/20 UPD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		// Map<String, Object> resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean);
		Map<String, Object> resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean,Consts.BUKEN_COUNT_All);
		// 2019/06/20 UPD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応		
		// ########## 2014/01/06 謝超 UPD End
		String bukkenCount = resul.get("bukkenCount").toString();

		//2013/05/17    郭凡(SYS)  ADD START 案件C37109-1
		String heyaCount = resul.get("heyaCount").toString();
		kibouJyoukennBean.setHeyaCount(heyaCount);
		//2013/05/17    郭凡(SYS)  ADD END 案件C37109-1

		// 物件数の取得
		kibouJyoukennBean.setBukkenCount(bukkenCount);

		// 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
	    resul = bukkenSearchService.getBukkenCount(kibouJyoukennBean,Consts.BUKEN_COUNT_TOKUTEN);
        bukkenCount = resul.get("bukkenCount").toString();
        kibouJyoukennBean.setBukkenTokutenCount(bukkenCount);
		// 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		this.kibouJyoukennBean = kibouJyoukennBean;
		// session.setAttribute("KIBOUJYOUKENBEAN", kibouJyoukennBean); // parasoft-suppress UC.ACC "C39042Jtest対応"

		// 物件区分リスト取得

		return "ajaxProcess";
	}

	/**
	 * セッションから子画面データの取得
	 *
	 * @return
	 * @throws Exception
	 */
	public String kogamenDataSet() throws Exception {

		// 物件№
		HttpServletRequest request = ServletActionContext.getRequest();

		kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);
		kibouJyoukennBean.setKiboujyokenDisp(kibouJyoukennBean.getKiboujyokenDisp().replaceAll(",", "／"));
		return "ajaxProcess";
	}

	/**
	 * セッションから子画面データのクリアする
	 *
	 * @return
	 * @throws Exception
	 */
	public String kogamenDataClear() throws Exception {

		// 物件№
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);

		// 希望条件1データをクリアする
		kibouJyoukennBean.setJusyohaniBean(null);
		// 希望条件2データをクリアする
		kibouJyoukennBean.setJyusyoList(null);
		//2014/03/11 謝超ADD Start (案件C37075)オリジナル情報誌リプレース
		// 市区郡データリストデータをクリアする
		kibouJyoukennBean.setJyusyoYuubinBangouList(null);
		//2014/03/11 謝超 ADD End
		// 希望条件3データをクリアする
		kibouJyoukennBean.setEkiList(null);
		// 希望条件4データをクリアする
		kibouJyoukennBean.setRosenCopBeanList(null);
		// 希望条件5データをクリアする
		kibouJyoukennBean.setSchoolFacilityBusBean(null);
		//########## 2017/08/17 MJEC)長倉 UPD Start (案件C38101-1)ホームメイト自動受付対応
		//0がセットされる箇所をnullに統一
		//kibouJyoukennBean.setKibouJyoukennCode("0");
		// 希望指定コードはnullに設定する
		kibouJyoukennBean.setKibouJyoukennCode(null);
		//########## 2017/08/17 MJEC)長倉 UPD End
		kibouJyoukennBean.setPrefectureCd(null);
		session.setAttribute(Consts.KIBOUJYOUKENBEAN, kibouJyoukennBean);

		return "ajaxProcess";
	}

	/**
	 * 設備コードの設定
	 *
	 * @return
	 * @throws Exception
	 */
	public String setSetubiCd() throws Exception {

		// 物件№
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		KibouJyoukennBean kibouBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);
		String[] setubiNoArr = request.getParameter("setubiNo").split(",");
		String[] setubiKbnArr = request.getParameter("setubiKbn").split(",");
		for (int i = 0; i < setubiNoArr.length; i++) {
			int setubiNo = Integer.parseInt(setubiNoArr[i]);
			String setubiKbn = setubiKbnArr[i];

			setSetubiCd(kibouBean, setubiNo, setubiKbn);
		}

		kibouJyoukennBean = kibouBean;
		ukg02030Data = new HashMap<String, Object>();
		ukg02030Data.put("mustSetubi", kibouBean.getMustSetubi());
		ukg02030Data.put("noMustSetubi", kibouBean.getNoMustSetubi());
		session.setAttribute(Consts.KIBOUJYOUKENBEAN, kibouBean);
		return "ajaxProcess";
	}

	/**
	 * 設備№の設定
	 *
	 * @param setubiNo
	 *            十進数の設備№
	 * @param setubiKbn
	 *            設備区分
	 */
	public void setSetubiCd(KibouJyoukennBean kibouBean,
	                        int setubiNo,
	                        String setubiKbn) {

		if ("1".equals(setubiKbn)) {
			kibouBean.setMustSetubiBin(kibouBean.getMustSetubiBin().substring(0, setubiNo - 1)
			    + "1" + kibouBean.getMustSetubiBin().substring(setubiNo));

			kibouBean.setMustSetubi(String.valueOf(Long.parseLong(kibouBean.getMustSetubiBin(), 2)));

			kibouBean.setNoMustSetubiBin(kibouBean.getNoMustSetubiBin().substring(0, setubiNo - 1)
			    + "0" + kibouBean.getNoMustSetubiBin().substring(setubiNo));

			kibouBean.setNoMustSetubi(String.valueOf(Long.parseLong(kibouBean.getNoMustSetubiBin(), 2)));

		} else if ("9".equals(setubiKbn)) {
			// 引数.必須区分が'9'(検索ではない)の場合
			kibouBean.setMustSetubiBin(kibouBean.getMustSetubiBin().substring(0, setubiNo - 1)
			    + "0" + kibouBean.getMustSetubiBin().substring(setubiNo));

			kibouBean.setMustSetubi(String.valueOf(Long.parseLong(kibouBean.getMustSetubiBin(), 2)));

			kibouBean.setNoMustSetubiBin(kibouBean.getNoMustSetubiBin().substring(0, setubiNo - 1)
			    + "0" + kibouBean.getNoMustSetubiBin().substring(setubiNo));

			kibouBean.setNoMustSetubi(String.valueOf(Long.parseLong(kibouBean.getNoMustSetubiBin(), 2)));
		} else if ("0".equals(setubiKbn)) {
			// 引数.必須区分が'0'(必須ではない)の場合
			kibouBean.setMustSetubiBin(kibouBean.getMustSetubiBin().substring(0, setubiNo - 1)
			    + "0" + kibouBean.getMustSetubiBin().substring(setubiNo));

			kibouBean.setMustSetubi(String.valueOf(Long.parseLong(kibouBean.getMustSetubiBin(), 2)));

			kibouBean.setNoMustSetubiBin(kibouBean.getNoMustSetubiBin().substring(0, setubiNo - 1)
			    + "1" + kibouBean.getNoMustSetubiBin().substring(setubiNo));

			kibouBean.setNoMustSetubi(String.valueOf(Long.parseLong(kibouBean.getNoMustSetubiBin(), 2)));
		}

	}

	/**
	 * 選定の物件情報を処理する
	 *
	 * @return
	 * @throws Exception
	 */
	public String bukkenSearchSave() throws Exception {

		// 物件№
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		KibouJyoukennBean kibouJyoukenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);
		// 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		String tokuTenKb = request.getParameter("tokuTenKb");
		session.setAttribute("tokuTenKb",tokuTenKb);
		// 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応

		// ########## 2014/01/06 謝超 UPD Start (案件C37075)オリジナル情報誌リプレース // parasoft-suppress UC.ACC "C39042Jtest対応"
		// Map<String, Object> map = BukkenSearchService.getKuusituBukkenInfo(session.getId(), kibouJyoukenBean, null); // parasoft-suppress UC.ACC "C39042Jtest対応"
		Map<String, Object> map = bukkenSearchService.getKuusituBukkenInfo(session.getId(), kibouJyoukenBean, null);
		// ########## 2014/01/06 謝超 UPD End
		ukg02030Data = new HashMap<String, Object>();

		String rstCd = map.get("rst_code").toString();
		ukg02030Data.put("rstCd", rstCd);

		if (!"0000000".equals(rstCd)) {
			ukg02030Data.put("rstMsg", Util.getServerMsg(Consts.ERRS002, Consts.STR_TOROKU));
		}

		return "ajaxProcess";
	}

	/**
	 *  駐車場ラジオボタン選択
	 *
	 * @return
	 * @throws Exception
	 */
	public String parkSessionSettring() throws Exception {

		// 物件№
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		KibouJyoukennBean kibouJyoukenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);

		kibouJyoukenBean.setBukkenKb("3");

		return "ajaxProcess";
	}

	/**
	 * 選定の物件情報を処理する
	 *
	 * @return
	 * @throws Exception
	 */
	public String wkSearch() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();

		String kaisyaCd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String tantouCd = sessionLoginInfo.get(Consts.USER_CD).toString();
		String uketukeNo = session.getAttribute(Consts.UKETUKENO).toString();

		String bukkenNo = request.getParameter("bukkenNo");
		String heyaNo = request.getParameter("heyaNo");

		// ########## 2017/08/17 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		String sinchokuCd = request.getParameter("sinchokuCd");
		// ########## 2017/08/17 MJEC)長倉 ADD End

		Map<String, Object> paramMap = new HashMap<String, Object>();

		paramMap.put("PV_SESSION_ID", session.getId());
		// 会社コード
		paramMap.put("PV_KAISYA_CD", kaisyaCd);
		// 受付№
		paramMap.put("PV_UKETUKE_CD", uketukeNo);
		paramMap.put("PV_UKETUKE_CD", uketukeNo);
		paramMap.put("PV_HOMEMATE_BUKKEN_NO", bukkenNo);
		paramMap.put("PV_HEYA_NO", heyaNo);
		paramMap.put("PV_PARKING_KB", "1");
		paramMap.put("PV_TANTOU_CD", tantouCd);
		paramMap.put("PV_NOWDATE", new Date());
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		paramMap.put("PV_AUTHORITY_CD", authorityCd);
		// ########## 2014/06/10 郭凡 ADD End
		// ########## 2017/08/17 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
		paramMap.put("PV_SINCHOKU_CD", sinchokuCd);
		// ########## 2017/08/17 MJEC)長倉 ADD End
		Map<String, Object> resultMap = UKG02030Services.wkDataUpd(paramMap);
		ukg02030Data = new HashMap<String, Object>();
		ukg02030Data.put("rstCd", resultMap.get("rst_code"));
		if (!"0000000".equals(resultMap.get("rst_code").toString())) {
			ukg02030Data.put("rstMsg", Util.getServerMsg(Consts.ERRS002, "登録処理"));
		}

		return "ajaxProcess";
	}

	/**
	 * セッションをクリアする
	 *
	 * @return
	 */
	public String clearSession() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		session.removeAttribute(Consts.UKG03011BEAN_SESSION_KEY);
		session.removeAttribute(Consts.UKG03030_LEFT_BEAN_SESSION_KEY);
		session.removeAttribute(Consts.UKG03030_RIGHT_BEAN_SESSION_KEY);
		session.removeAttribute(Consts.UKG03012BEAN_SESSION_KEY);
		session.removeAttribute(Consts.KIBOUJYOUKENBEAN);
		return "ajaxProcess";
	}

	//2013/05/17    郭凡(SYS)  ADD START 案件C37109-1
	/**
	 * <pre>
	 * [説 明] 物件名リスト検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchBukkenNameList() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		// セッション情報取得
		Map<String, Object> loginInfo = getLoginInfo();
		String kaisyaCd = loginInfo.get(Consts.KAISYA_CD).toString();
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		// 新規の場合、市区郡コードを取得する
//		String postCd = loginInfo.get(Consts.POST_CD).toString();
		String postCd = (String)uketukePostInfo.get(Consts.POST_CD);
		// ########## 2014/06/10 郭凡 UPD End
		// 物件名
	   String bukkenName = new String(request.getParameter("nyuryokuBukkenName").getBytes("ISO8859-1"), "UTF-8");

	   // 物件名リストを検索する
	   List<Map<String, Object>> list = (List<Map<String, Object>>) UKG02030Services.searchBukkenNameList(kaisyaCd, postCd,bukkenName).get("rst_list");

	   // 物件名リスト
	   ukg02030Data = new HashMap<String, Object>();
	   ukg02030Data.put("BUKKEN_NAME_LIST", Util.chkDropDownList(list, "BUKKEN_NAME"));

	   return "ajaxProcess";
	}
	//2013/05/17    郭凡(SYS)  ADD END 案件C37109-1

	// 2017/09/25 SYS_ヨウ強  ADD START C41004_法人仲介担当者登録機能追加対応
	/**
	 * <pre>
	 * [説 明] アップロード日付を検索処理
	 * @param kaisyaCd 会社コード
	 * @param HoujinUketukeNo 法人仲介依頼元受付NO.
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public void getUploadDate(String kaisyaCd, String HoujinUketukeNo) throws Exception {
		Map<String, Object> pdfData = UKG02030Services.getUploadDate(kaisyaCd, HoujinUketukeNo);
		// 社宅斡旋依頼PDFファイル名とパスを取得
		List<Map<String, Object>> pdfMapList = (List<Map<String, Object>>) pdfData.get("rst_record");
		ukg02030Data.put("iraiPafMapList", pdfMapList);
	}

	/**
	 * <pre>
	 * [説 明] 法人希望部屋数の取得
	 * @param kaisyaCd 会社コード
	 * @param uketukeNo 受付No
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public void getHoujinKibouHeyaSuu(String kaisyaCd, String uketukeNo) throws Exception {
		Map<String, Object> hetaSuuData = UKG02030Services.getKibouHeyaSuu(kaisyaCd, uketukeNo);
		// 法人希望部屋数リストの取得
		List<Map<String, Object>> heyaList = (List<Map<String, Object>>) hetaSuuData.get("RST_HEYASU");
		if (heyaList.size() > 0) {
			Map<String, Object> heyaSuuDataMap = heyaList.get(0);
			// 法人希望部屋数を設定
			heyaSuuDataMap.put("KIBOUHEYASUU", heyaSuuDataMap.get("KIBOUHEYASUU"));
            // 2019/06/25 ADD START 汪会(SYS) C42036-BUG210
            // 法人希望部屋数数字の設定
            heyaSuuDataMap.put("HOUJIN_KIBOU_HEYA_SUU_FLG", heyaSuuDataMap.get("HOUJIN_KIBOU_HEYA_SUU_FLG"));
            // 2019/06/25 ADD END 汪会(SYS) C42036-BUG210
            // 2019/07/02 ADD START ラボバグ対応 h-nakano
            heyaSuuDataMap.put("KIBOU_HEYA_SUU_CD", heyaSuuDataMap.get("KIBOU_HEYA_SUU_CD"));
            // 2019/07/02 ADD END ラボバグ対応 h-nakano
			ukg02030Data.put("heyaSuuDataMap", heyaSuuDataMap);
		}
	}

	/**
	 * <pre>
	 * [説 明] 店舗情報を取得
	 * @param kaisyaCd 会社コード
	 * @param iraiMotoUketukeNo 受付No
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public void getTenpoInfo(String kaisyaCd, String iraiMotoUketukeNo) throws Exception {
		// 店舗情報リストを取得
		 Map<String, Object> postData = UKG02030Services.getTenpoInfo(kaisyaCd, iraiMotoUketukeNo);
		 // 店舗情報を設定
		 ukg02030Data.put("tenpoMapList", (List<Map<String, Object>>) postData.get("kaisyaData"));
	}

	/**
	 * PDFファイル名日時リンク押下処理
	 * @throws Exception
	 *
	 */
	public String pdfLinkCheck() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		ukg02030Data = new HashMap<String, Object>();
		ukg02030Data.put("request_token", session.getAttribute(Consts.REQUEST_TOKEN));
		// PDFデータルートパス
		String pdfRootPath = PropertiesReader.getIntance().getProperty("pdfRootPath");

		// PDF一時フォルダ
		String pdfPath = PropertiesReader.getIntance().getProperty("pdfTempFolder");
		if(Util.isEmpty(pdfRootPath) || Util.isEmpty(pdfPath)){
			ukg02030Data.put("RESULT", "SYSERR");
			throw new Exception();
		}

		// ファイル名称
		String fileName = pdfRootPath + pdfPath;

		BufferedInputStream inputStream = null;
		File file = null;
		try {
			if ("".equals(fileName)) {
				ukg02030Data.put("MSG", Util.getServerMsg("ERRS002", "PDFまたはドキュワークスのファイル取得"));
				ukg02030Data.put("RESULT", "FAIL");
				return "ajaxProcess";
			}

			file = new File(fileName);

			if (file.exists()) {
				inputStream = new BufferedInputStream(new FileInputStream(file));

			} else {
				ukg02030Data.put("MSG", Util.getServerMsg("ERRS002", "PDFまたはドキュワークスのファイル取得"));
				ukg02030Data.put("RESULT", "FAIL");
				return "ajaxProcess";
			}

		} catch (Exception e) {
			e.printStackTrace();
			ukg02030Data.put("MSG", Util.getServerMsg("ERRS002", "PDFまたはドキュワークスのファイル取得"));
			ukg02030Data.put("RESULT", "FAIL");
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				LOG.error("ストリームをクローズする時に、エラーが発生しました。", e);
				ukg02030Data.put("MSG", Util.getServerMsg("ERRS002", "PDFまたはドキュワークスのファイル取得"));
				ukg02030Data.put("RESULT", "FAIL");
				return "ajaxProcess";
			}
		}
		ukg02030Data.put("MSG", "");
		ukg02030Data.put("RESULT", "SUCCESS");
		return "ajaxProcess";
	}

	/**
	 * PDFファイル名日時リンク押下処理
	 *
	 * @throws Exception
	 *
	 */
	public void pdfLink() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		// PDFデータルートパス
		String pdfRootPath = PropertiesReader.getIntance().getProperty("pdfRootPath");

		// PDFファイルパス
		String folderPathName = (String) request.getParameter("PDF_PATH");
		// PDFファイル名称
		String folderName = (String) request.getParameter("PDF_FILE_NAME");
		// ファイル名称
		String fileName = pdfRootPath + folderPathName;

		BufferedInputStream inputStream = null;
		OutputStream outputStream = null;
		File file = null;
		try {
			if ("".equals(fileName)) {
				return;
			}
			file = new File(fileName);
			folderName = file.getName();
			LOG.debug("PDFまたはドキュワークスのファイル[" + folderName + "]のダウンロードのダウンロードを始まります。");

			if (file.exists()) {
				inputStream = new BufferedInputStream(new FileInputStream(file));
				// 2018/03/16 H.Sato updated start (案件C41004)法人仲介担当者登録機能追加対応
				//response.setContentType("application/pdf");
				String mimetype = FileTypeMap.getDefaultFileTypeMap().getContentType(fileName);
				response.setContentType(mimetype);
				// 2018/03/16 H.Sato updated end (案件C41004)法人仲介担当者登録機能追加対応
				String downLoadfilePath = "attachment;filename= "
						+ new String(folderName.getBytes("shift-JIS"), "iso-8859-1");
				response.setHeader("Content-disposition", downLoadfilePath);

				int fileSize = inputStream.available();
				response.setContentLength(fileSize);
				outputStream = response.getOutputStream();
				byte[] b = new byte[32 * 1024];
				int len = 0;
				while ((len = inputStream.read(b)) != -1) {
					outputStream.write(b, 0, len);
				}
				outputStream.flush();

				LOG.debug("PDFまたはドキュワークスのファイル[" + file.getAbsolutePath() + "]をダウンロードしました。");
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				LOG.error("PDFまたはドキュワークスのファイル[" + file.getAbsolutePath() + "]が見つかれません。");
			}

		} catch (IOException e) {
			e.printStackTrace();
			LOG.debug("ファイル「" + fileName + "」をオープンする時に、エラーが発生しました。", e);
			try {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} catch (IOException ioe) {
				e.printStackTrace();
				LOG.debug("ファイル「" + fileName + "」をエラー送信する時に、エラーが発生しました。", e);
			}
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				LOG.error("ストリームをクローズする時に、エラーが発生しました。", e);
			}
		}
		return;
	}
	// 2017/09/25 SYS_ヨウ強  ADD END C41004_法人仲介担当者登録機能追加対応
}
