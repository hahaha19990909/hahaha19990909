/**
 * @システム名: 受付システム
 * @ファイル名: UKG05021Action.java
 * @更新日付： 2014/01/08
 * @Copyright: 2013 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.DispFormat;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.KibouJyoukennBean;
import jp.co.token.uketuke.formbean.MagazineBaseData;
import jp.co.token.uketuke.service.IBukkenSearchService;
import jp.co.token.uketuke.service.IUKG05021Service;
import jp.co.token.uketuke.service.impl.UKG05021ServiceImpl;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] プリセット検索
 * [説 明] プリセット検索アクション。
 * @author [作 成] 2014/01/08 謝超(SYS) (案件C37075)オリジナル情報誌リプレース
 * @author [変 更] 2018/02/11 SYS_肖剣生 (案件C42036)デザイン変更
 * @author [変 更] 2019/06/27 SYS_郝年昇 (案件C43100)お客様項目の入力簡素化対応
 * </pre>
 */
public class UKG05021Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 6914550433895639772L;


	/** プリセット検索画面用データ */
	private Map<String, Object> ukg05021Data = new HashMap<String, Object>();

	/** 物件検索サービス */
	private IBukkenSearchService bukkenSearchServiceCom;
	
	/** プリセット検索サービス */
	private IUKG05021Service ukg05021Services;

	/**
	 * <pre>
	 * [説 明] デフォルトコンストラクタ
	 * </pre>
	 */
	public UKG05021Action() {

		// デフォルトコンストラクタ
	}
	
	/**
	 * <pre>
	 * [説 明] コンストラクタ(引数有る)
	 * </pre>
	 */
	public UKG05021Action(
			IUKG05021Service ukg05021Services,
	    IBukkenSearchService bukkenSearchServiceCom) {

		super();
		this.ukg05021Services = ukg05021Services;
		this.bukkenSearchServiceCom = bukkenSearchServiceCom;
	}
	
	/**
	 * <pre>
	 * [説 明] 物件検索サービス対象を設定する。
	 * @param bukkenSearchServiceCom サービス対象
	 * </pre>
	 */
	public void setbukkenSearchServiceCom(
			IBukkenSearchService bukkenSearchServiceCom) {
		this.bukkenSearchServiceCom = bukkenSearchServiceCom;
	}

	/**
	 * <pre>
	 * [説 明] プリセット検索サービス対象を設定する。
	 * @param ukg05021Service サービス対象
	 * </pre>
	 */
	public void setUkg05021Services(IUKG05021Service ukg05021Services) {
		this.ukg05021Services = ukg05021Services;
	}

	/**
	 * プリセット検索画面用データ
	 * 
	 * @return ukg05021Data　画面データ
	 */
	public Map<String, Object> getUkg05021Data() {
		return ukg05021Data;
	}

	/**
	 * プリセット検索画面用データ
	 * 
	 * @param ukg05021Data
	 *            画面データ
	 */
	public void setUkg05021Data(Map<String, Object> ukg05021Data) {
		this.ukg05021Data = ukg05021Data;
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
		
		// セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		
		// 希望条件データを取得
		KibouJyoukennBean kibouJyoukennBean = getKibojyoukenData();
		
		// 希望条件初期化情報の取得
		bukkenSearchServiceCom.getInitInfo(kibouJyoukennBean);
		
		String strPresetNo = Util.toEmpty(request.getParameter("presetNo"));
		
		session.setAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo, kibouJyoukennBean);
		
		// 物件件数を取得
		// 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		// Map<String, Object> resultInfo = bukkenSearchServiceCom.getBukkenCount(kibouJyoukennBean);
		Map<String, Object> resultInfo = bukkenSearchServiceCom.getBukkenCount(kibouJyoukennBean,Consts.BUKEN_COUNT_All);
		// 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		
		String heyaCount = "";
		ukg05021Data.put("errMsg", "");
		if (!"0000000".equals(resultInfo.get("restCd").toString())) {
			ukg05021Data.put("errMsg", Util.getServerMsg("ERRS002", "検索処理"));
		}else {
			heyaCount = (String)resultInfo.get("heyaCount");
		}
		
		// 物件区分
		List<Map<String, Object>> bukkenKbList = (List<Map<String, Object>>)ukg05021Services.getBukkenKb().get("RST_BUKKEN_KB");
		
		// 物件種別
		Map<String, Object> bukkenSb = ukg05021Services.getBukkenSb();
		List<Map<String, Object>> bukkenSbKyojyuuList = (List<Map<String, Object>>)bukkenSb.get("RST_BUKKEN_SB_KYOJYUU");
		List<Map<String, Object>> bukkenSbJigyouList = (List<Map<String, Object>>)bukkenSb.get("RST_BUKKEN_SB_JIGYOU");
		
		// 築年数
		List<Map<String, Object>> tikunensuuList = ((UKG05021ServiceImpl)ukg05021Services).getCommonDate("012");
		
		// 家賃(下限)、家賃(上限)
		List<Map<String, Object>> yatinList = ((UKG05021ServiceImpl)ukg05021Services).getCommonDate("013");
		
		// 希望間取り
		List<Map<String, Object>> madoriList = ((UKG05021ServiceImpl)ukg05021Services).getCommonDate("045");
		
		// 管理区分
		List<Map<String, Object>> kanriKbList = ((UKG05021ServiceImpl)ukg05021Services).getCommonDate("097");
		
		// 専有面積
		List<Map<String, Object>> mensekiList = ((UKG05021ServiceImpl)ukg05021Services).getCommonDate("015");
		
		// 駅からの徒歩分
		List<Map<String, Object>> ekikaraList = ((UKG05021ServiceImpl)ukg05021Services).getCommonDate("046");
		
		// 事業用家賃(下限)、家賃(上限)
		List<Map<String, Object>> yatinJigyouList = ((UKG05021ServiceImpl)ukg05021Services).getCommonDate("014");
		
		// 駐車場要不要
		List<Map<String, Object>> parkCodeList = ((UKG05021ServiceImpl)ukg05021Services).getCommonDate("028");
		Collections.reverse(parkCodeList);
		
		// その他条件
		List<HashMap<String, Object>> sonotaList = (List<HashMap<String, Object>>)ukg05021Services.getSonotaList().get("RST_SONOTA");
		
		ukg05021Data.put("heyaCount", Util.getMoneyFormat(Long.valueOf(heyaCount)).trim());
		ukg05021Data.put("bukkenKbList", bukkenKbList);
		ukg05021Data.put("bukkenSbKyojyuuList", bukkenSbKyojyuuList);
		ukg05021Data.put("bukkenSbJigyouList", bukkenSbJigyouList);
		ukg05021Data.put("tikunensuuList", tikunensuuList);
		ukg05021Data.put("yatinList", yatinList);
		ukg05021Data.put("yatinJigyouList", yatinJigyouList);
		ukg05021Data.put("madoriList", madoriList);
		ukg05021Data.put("kanriKbList", kanriKbList);
		ukg05021Data.put("mensekiList", mensekiList);
		ukg05021Data.put("ekikaraList", ekikaraList);
		ukg05021Data.put("parkCodeList", parkCodeList);
		ukg05021Data.put("kibouJyoukennBean", kibouJyoukennBean);
		ukg05021Data.put("sonotaList", sonotaList);
		ukg05021Data.put("strPresetNo", strPresetNo);
		
		// 2018/02/11 SYS_肖剣生 ADD START (案件番号C42036)デザイン変更
		String prefectureCd = kibouJyoukennBean.getPrefectureCd();
		if (prefectureCd == null || "".equals(prefectureCd)) {
			Map<String, Object> uketukePostInfo = getUketukePostInfo();
			prefectureCd = (String) uketukePostInfo.get(Consts.POST_JUSYO_CD);
		}
		ukg05021Data.put("prefectureCd", prefectureCd);
		// 2018/02/11 SYS_肖剣生 ADD END (案件番号C42036)デザイン変更
		
		
		return successForward();
	}
	/**
	 * <pre>
	 * [説 明] 物件件数検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String searchBukkenCount() throws Exception {
		//セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String strPresetNo = Util.toEmpty(request.getParameter("strPresetNo"));
		
		// 希望条件データを取得
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
				
		setDataFromParams(kibouJyoukennBean);
		session.setAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo, kibouJyoukennBean);
		// 物件件数を取得
        // 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
        // Map<String, Object> resultInfo = bukkenSearchServiceCom.getBukkenCount(kibouJyoukennBean);
		Map<String, Object> resultInfo = bukkenSearchServiceCom.getBukkenCount(kibouJyoukennBean,Consts.BUKEN_COUNT_All);
        // 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応

		String heyaCount = "0";
		ukg05021Data.put("errMsg", "");
		if (!"0000000".equals(resultInfo.get("restCd").toString())) {
			ukg05021Data.put("errMsg", Util.getServerMsg("ERRS002", "検索処理"));
		}else {
			heyaCount = (String)resultInfo.get("heyaCount");
		}
		
		ukg05021Data.put("heyaCount", Util.getMoneyFormat(Long.valueOf(heyaCount)).trim());
		return "ajaxProcess";
	}
	
	/**
	 * <pre>
	 * [説 明] 希望条件をクリア。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String clearKiboJyouken() throws Exception {
		//セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();// parasoft-suppress CDD.DUPC "C37075JTest対応"
		HttpSession session = request.getSession();
		String strPresetNo = Util.toEmpty(request.getParameter("strPresetNo"));
		
		// 希望条件指定コードを取得
		String kiboCd = Util.toEmpty(request.getParameter("kiboCd"));
		
		// 希望条件データを取得
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
		
		// 希望条件指定コード をクリア
		kibouJyoukennBean.setKibouJyoukennCode(null);
		// 希望条件表示をクリア
		kibouJyoukennBean.setKiboujyokenDisp("");
		// 県コードをクリア
		kibouJyoukennBean.setPrefectureCd("");
		
		// 希望地域(エリア指定)の場合
		if("1".equals(kiboCd)) {
			kibouJyoukennBean.setJusyohaniBean(null);
		// 希望地域(市区郡指定)の場合
		}else if("2".equals(kiboCd)) {
			kibouJyoukennBean.setJyusyoList(null);
			kibouJyoukennBean.setJyusyoYuubinBangouList(null);
		// 希望駅の場合
		}else if("3".equals(kiboCd)) {
			kibouJyoukennBean.setEkiList(null);
		}
		
		session.setAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo, kibouJyoukennBean);
		// 部屋件数を取得
		// 2019/06/20 ADD START 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		// Map<String, Object> resultInfo = bukkenSearchServiceCom.getBukkenCount(kibouJyoukennBean);
		Map<String, Object> resultInfo = bukkenSearchServiceCom.getBukkenCount(kibouJyoukennBean,Consts.BUKEN_COUNT_All);
		// 2019/06/20 ADD END 郝年昇(SYS) C43100_お客様項目の入力簡素化対応
		
		String heyaCount = (String)resultInfo.get("heyaCount");
		
		ukg05021Data.put("heyaCount", Util.getMoneyFormat(Long.valueOf(heyaCount)).trim());
		return "ajaxProcess";
	}
	
	/**
	 * <pre>
	 * [説 明] 希望条件を取得。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String kogamenDataSet() throws Exception {
		//セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String strPresetNo = Util.toEmpty(request.getParameter("strPresetNo"));
		
		// 希望条件データを取得
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
				
		ukg05021Data.put("kibouJyoukennBean", kibouJyoukennBean);
		return "ajaxProcess";
	}
	
	/**
	 * <pre>
	 * [説 明] 物件検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchBukkenInfo() throws Exception {
		//セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String strPresetNo = Util.toEmpty(request.getParameter("strPresetNo"));
		
		// 希望条件データを取得
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
		session.setAttribute(Consts.UKG05021_SORT_INFO, Consts.UKG05021_SORT_JYUN);
		// 物件を取得
		Map<String, Object> resultInfo = bukkenSearchServiceCom.getBukkenList(kibouJyoukennBean, Consts.UKG05021_SORT_JYUN, 1, 15);
		List<HashMap<String, Object>> bukkenList = (List<HashMap<String, Object>>)resultInfo.get("RST_CSR");
		formatEastNo(bukkenList);
		if(null == bukkenList || 0 == bukkenList.size()){
			ukg05021Data.put("NODATA_MESSAGE", Util.getServerMsg("MSGS003", "該当データ"));
		}
		ukg05021Data.put("bukkenList", bukkenList);
		
		return "ajaxProcess";
	}
	
	/**
	 * <pre>
	 * [説 明] ページを換える。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String pageIndexChg() throws Exception {
		//セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String condition = request.getParameter("condition");
		
		Map<String, String> paramMap = Util.getParamMap(condition);
		String strPresetNo = Util.toEmpty(paramMap.get("strPresetNo"));
		String strPageNum = paramMap.get("pageNum");
		//行No
		int startRowNum;
		int endRowNum;
		
		startRowNum = (Integer.parseInt(strPageNum) - 1)  *  Consts.FIFTTEN + 1;
		endRowNum = startRowNum + Consts.FOURTTEN;
		
		// 希望条件データを取得
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
		String sortJyouken = (String)session.getAttribute(Consts.UKG05021_SORT_INFO);
		// 物件を取得
		Map<String, Object> resultInfo = bukkenSearchServiceCom.getBukkenList(kibouJyoukennBean, sortJyouken, startRowNum, endRowNum);
		List<HashMap<String, Object>> bukkenList = (List<HashMap<String, Object>>)resultInfo.get("RST_CSR");
		formatEastNo(bukkenList);
		if(null == bukkenList || 0 == bukkenList.size()){
			ukg05021Data.put("NODATA_MESSAGE", Util.getServerMsg("MSGS003", "該当データ"));
		}
		ukg05021Data.put("bukkenList", bukkenList);
		
		return "ajaxProcess";
	}
	
	/**
	 * <pre>
	 * [説 明] 物件をソート。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String sort() throws Exception {
		//セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String strPresetNo = Util.toEmpty(request.getParameter("strPresetNo"));
		
		// 希望条件データを取得
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
		String sortJyouken = (String)session.getAttribute(Consts.UKG05021_SORT_INFO);
		
		// ソート順取得する
		String oldSort = request.getParameter("oldSort");
		String newSort = request.getParameter("newSort");
		
		// 新しいソート順を作成
		if(!Util.isEmpty(oldSort) && !Util.isEmpty(newSort)){
			if(sortJyouken.startsWith(oldSort)) {
				sortJyouken = sortJyouken.replaceAll(oldSort, newSort);
			} else {
				if(sortJyouken.endsWith(oldSort)) {
					sortJyouken = sortJyouken.replaceAll("," + oldSort, "");
				} else {
					sortJyouken = sortJyouken.replaceAll(oldSort + ",", "");
				}
				sortJyouken = newSort + "," + sortJyouken;
			}
		}
		session.setAttribute(Consts.UKG05021_SORT_INFO, sortJyouken);
		
		// 物件を取得
		Map<String, Object> resultInfo = bukkenSearchServiceCom.getBukkenList(kibouJyoukennBean, sortJyouken, 1, 15);
		List<HashMap<String, Object>> bukkenList = (List<HashMap<String, Object>>)resultInfo.get("RST_CSR");
		formatEastNo(bukkenList);
		
		if(null == bukkenList || 0 == bukkenList.size()){
			ukg05021Data.put("NODATA_MESSAGE", Util.getServerMsg("MSGS003", "該当データ"));
		}
		ukg05021Data.put("bukkenList", bukkenList);
		
		return "ajaxProcess";
	}
	
	/**
	 * パラメータを取得
	 * @param kibouJyoukennBean 希望条件データ
	 */
	private void setDataFromParams(KibouJyoukennBean kibouJyoukennBean) {
		//セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		
		kibouJyoukennBean.setBukkenKb(request.getParameter("bukkenKb"));
		kibouJyoukennBean.setBukkenSb(request.getParameter("bukkenSb"));
		kibouJyoukennBean.setTikunenCd(request.getParameter("tikunensuu"));
		kibouJyoukennBean.setYatinTop(request.getParameter("yatinTop"));
		kibouJyoukennBean.setYatinTopVal(request.getParameter("yatinTopValue"));
		kibouJyoukennBean.setYatinLow(request.getParameter("yatinDown"));
		kibouJyoukennBean.setYatinLowVal(request.getParameter("yatinDownValue"));
		kibouJyoukennBean.setKiboMardoriList(request.getParameter("madori"));
		kibouJyoukennBean.setKanriKubunList(request.getParameter("kanriKb"));
		kibouJyoukennBean.setKibouMensekiTop(request.getParameter("mensekiTop"));
		kibouJyoukennBean.setKibouMensekiTopVal(request.getParameter("mensekiTopValue"));
		kibouJyoukennBean.setKibouMensekiDown(request.getParameter("mensekiDown"));
		kibouJyoukennBean.setKibouMensekiDownVal(request.getParameter("mensekiDownValue"));
		kibouJyoukennBean.setEkiMiniute(request.getParameter("ekiKara"));
		kibouJyoukennBean.setEkiMiniuteCd(request.getParameter("ekiKaraCd"));
		if("1".equals(kibouJyoukennBean.getEkiMiniuteCd())) {
			kibouJyoukennBean.setSearchMod("1");
		} else {
			kibouJyoukennBean.setSearchMod("2");
		}
		String sonotaList = request.getParameter("sonota");
		kibouJyoukennBean.setSonotaList(sonotaList);
		// 設備コードを作成して設定する
		int cnt = Integer.parseInt(PropertiesReader.getIntance().getProperty("setubiCount"));
		String setubiBin = Util.StringTume("", cnt, '0');
		if(!Util.isEmpty(sonotaList)){
			String sonotas[] = sonotaList.split(",");
			int setubiNo = 0;
			for (String sonota : sonotas) {
				setubiNo = Integer.parseInt(sonota);
				setubiBin = setubiBin.substring(0, setubiNo - 1) + "1" + setubiBin.substring(setubiNo);
			}
			kibouJyoukennBean.setMustSetubiBin(setubiBin);
			kibouJyoukennBean.setMustSetubi(String.valueOf(Long.parseLong(setubiBin, 2)));
		} else{
			kibouJyoukennBean.setMustSetubiBin(setubiBin);
			kibouJyoukennBean.setMustSetubi("0");
		}
		kibouJyoukennBean.setInitMoneyTotal(request.getParameter("initMoney"));
		kibouJyoukennBean.setParkMustFlg(request.getParameter("parkFlag"));
		kibouJyoukennBean.setKyokiKanriHiFlg("0");
		kibouJyoukennBean.setParkHiFlg("0");
	}
	
	/**
	 * <pre>
	 * [説 明] 希望条件データを初期化
	 * @return 希望条件データ
	 * </pre>
	 */
	private KibouJyoukennBean getKibojyoukenData() {
		//セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		
		//引数を取得
		String strPresetNo = request.getParameter("presetNo");
		String strHakkoubiDate = request.getParameter("hakkoubiDate");
		String strSeq = request.getParameter("seq");
		
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String)mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String)mapLoginInfo.get(Consts.POST_CD);
		
		// 希望条件データを取得
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
		
		if(null == kibouJyoukennBean) {
			kibouJyoukennBean = new KibouJyoukennBean();
		}
		kibouJyoukennBean.setKaisyaCd(strKaisyaCd);
		kibouJyoukennBean.setPostCd(strPostCd);
		kibouJyoukennBean.setGamenKubun(Consts.UKG05021_GAMEN_KB);
		
		// 情報誌データを取得
		MagazineBaseData magazineBaseData = kibouJyoukennBean.getMagazineBaseData();
		if(null == magazineBaseData) {
			magazineBaseData = new MagazineBaseData();
		}
		
		magazineBaseData.setJyouhoushiHakkoubi(strHakkoubiDate);
		magazineBaseData.setHakkouSeq(strSeq);
		magazineBaseData.setPurisettoNo(strPresetNo);
		
		kibouJyoukennBean.setMagazineBaseData(magazineBaseData);
		kibouJyoukennBean.setGamenKubun(Consts.UKG05021_GAMEN_KB);
		
		return kibouJyoukennBean;
	}
	
	/**
	 *物件お問合わせ№を作成。
	 */
	private void formatEastNo(List<HashMap<String, Object>> list){
		if(list == null || list.size() == 0) {
			return;
		}
		
		String estateBukkenNo = "";
		for (Map<String, Object> map : list) {
			estateBukkenNo = DispFormat.getEstateBukkenNo((String)map.get("ESTATE_BUKKEN_NO"));
			map.put("ESTATE_BUKKEN_NO", estateBukkenNo);
		}
		
	}

}