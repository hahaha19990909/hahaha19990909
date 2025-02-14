/**
 * @システム名: 受付システム
 * @ファイル名: UKG05020Action.java
 * @更新日付： 2014/1/3
 * @Copyright: 2014 token corporation All right reserved
 * 更新履歴：2020/04/14   程旋(SYS)  C44030_お部屋探しNaviマルチブラウザ対応
 */
package jp.co.token.uketuke.action;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import jp.co.token.uketuke.service.IUKG05020Service;

import org.apache.struts2.ServletActionContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

/**
 * <pre>
 * [機 能] 情報誌掲載編集
 * [説 明] 情報誌掲載編集アクション。
 * @author [作 成] 2014/1/3 趙雲剛(SYS) (案件C37075)オリジナル情報誌リプレース
 * </pre>
 */
public class UKG05020Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 4135154380962449071L;

	/** 情報誌掲載編集画面用データ */
	private Map<String, Object> ukg05020Data = null;

	/** 情報誌掲載編集サービスを取得する */
	private IUKG05020Service ukg05020Services = null;

	/** 物件検索サービス */
	private IBukkenSearchService bukkenSearchServiceCom;

	/**
	 * <pre>
	 * [説 明] 情報誌掲載編集サービス対象を設定する。
	 * @param ukg05020Service サービス対象
	 * </pre>
	 */
	public void setUKG05020Services(IUKG05020Service ukg05020Services) {
		this.ukg05020Services = ukg05020Services;
	}

	/**
	 * <pre>
	 * [説 明] 物件検索サービス対象を設定する。
	 * @param bukkenSearchServiceCom サービス対象
	 * </pre>
	 */
	public void setbukkenSearchServiceCom(IBukkenSearchService bukkenSearchServiceCom) {
		this.bukkenSearchServiceCom = bukkenSearchServiceCom;
	}

	/**
	 * 情報誌掲載編集画面用データ
	 *
	 * @return ukg05020Data　画面データ
	 */
	public Map<String, Object> getUkg05020Data() {
		return ukg05020Data;
	}

	/**
	 * 情報誌掲載編集画面用データ
	 *
	 * @param ukg05020Data
	 *            画面データ
	 */
	public void setUkg05020Data(Map<String, Object> ukg05020Data) {
		this.ukg05020Data = ukg05020Data;
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

		// セッションから希望条件beanを削除する。
		for (int i = 1; i <= 6; i++) {
			request.getSession().removeAttribute(Consts.KIBOUJYOUKENBEAN+i);
		}

		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String) mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String) mapLoginInfo.get(Consts.POST_CD);
		ukg05020Data = new HashMap<String, Object>();
		// 遷移フラグ(1:新規 2:編集 3:流用)
		String strProcessType = request.getParameter("processType");
		// 情報誌発行日
		String hakkouDate = request.getParameter("hakkouDate");
		// 発行連番
		String seq = request.getParameter("seq");
		// 表紙削除フラグ
		String hyoushiTypeFlag = request.getParameter("hyoushiTypeFlag");

		// 最大掲載可能ページ数を取得
		String strMaxKeisaiPageSuu = PropertiesReader.getIntance().getProperty(
				Consts.UKG05020_MAX_KEISAI_PAGESUU);
		// キャッチコピー最大表示数
		String strShowCatchcopy = PropertiesReader.getIntance().getProperty(
				Consts.UKG05020_SHOW_CATCH_COPY_SUU);

		Map<String, Object> initData = ukg05020Services.getInitInfo(
				strKaisyaCd, strPostCd, hakkouDate, seq, strProcessType, strShowCatchcopy);

		// キャッチコピーリストデータを取得する
		List<Map<String, Object>> catchCopyData = (List<Map<String, Object>>) initData.get("rst_catch_copy");
		ukg05020Data.put("catchCopyData", catchCopyData);

		// オリジナル情報誌ページレイアウト(居住用)
		ukg05020Data.put("layoutKyojuu", ukg05020Services.getCommonDate("101"));

		// オリジナル情報誌ページレイアウト(事業用)
		ukg05020Data.put("layoutJigyou", ukg05020Services.getCommonDate("102"));

		//引数取得で取得した遷移フラグが1：新規の場合、情報誌検索処理は行わない。
		if("1".equals(strProcessType)){
			Map<String, Object> magazineData = new HashMap<String, Object>();
			magazineData.put("HAKKOU_DATE", hakkouDate);
			magazineData.put("HAKKOU_DATE_COPY", hakkouDate);
			magazineData.put("MAGAZINE_NAME", request.getParameter("magazineName"));
			magazineData.put("TINTAI_BUKKEN1", request.getParameter("tintaiBukken1"));
			magazineData.put("TINTAI_BUKKEN2", request.getParameter("tintaiBukken2"));
			magazineData.put("TINTAI_BUKKEN3", request.getParameter("tintaiBukken3"));
			magazineData.put("TINTAI_BUKKEN", request.getParameter("tintaiBukken"));
			magazineData.put("CATCH_COPY1", request.getParameter("catchCopy1"));
			magazineData.put("CATCH_COPY2", request.getParameter("catchCopy2"));
			magazineData.put("CATCH_COPY3", request.getParameter("catchCopy3"));
			magazineData.put("HYOUSHI_TYPE", request.getParameter("hyoushiType"));
			magazineData.put("HYOUSHI_TYPE_CODE", request.getParameter("hyoushiTypeCode"));
			magazineData.put("MADORI_SETUMEI_UMU", request.getParameter("madoriSetumeiUmu"));
			magazineData.put("SETUBI_ITIRAN_UMU", request.getParameter("setubiItiranUmu"));
			magazineData.put("URA_HYOUSHI_UMU", request.getParameter("uraHyoushiUmu"));
			ukg05020Data.put("magazineData", magazineData);
		} else if ("2".equals(strProcessType) || "3".equals(strProcessType)) {
			magazineBukkenDataProcess(initData, strProcessType, hyoushiTypeFlag);
		}
		ukg05020Data.put("kaisyaCd", strKaisyaCd);
		ukg05020Data.put("postCd", strPostCd);
		ukg05020Data.put("userCd", mapLoginInfo.get(Consts.LOGIN_USER_CD));
		ukg05020Data.put("processType", strProcessType);
		ukg05020Data.put("maxKeisaiPageSuu", strMaxKeisaiPageSuu);
		ukg05020Data.put("showCatchcopy", strShowCatchcopy);
		ukg05020Data.put("sysdate", Util.getSysDateYMD());
		// リクエストURL,設定ファイル(uketuke.properties).magazineURL
		String printActionId = PropertiesReader.getIntance().getProperty("magazineURL");
		ukg05020Data.put("printActionId", printActionId);
		String tirashiTitleName = PropertiesReader.getIntance().getProperty(Consts.MAGAZINE_PDF_WINDOWSHOWINGNAME);
		ukg05020Data.put("magazineTitleName", tirashiTitleName);
		String tirashiDownloadName = PropertiesReader.getIntance().getProperty(Consts.MAGAZINE_PDF_DOWNLOADNAME);
		ukg05020Data.put("magazineDownloadName", tirashiDownloadName);
		/*2020/04/14 程旋 ADD START C44030_お部屋探しNaviマルチブラウザ対応*/
		String chouhyouServer = PropertiesReader.getIntance().getProperty(Consts.CHOUHYOUSERVER);
		ukg05020Data.put("chouhyouServer", chouhyouServer);
		/*2020/04/14 程旋 ADD END C44030_お部屋探しNaviマルチブラウザ対応*/
		ukg05020Data.put("errorMessage", Util.getServerMsg("ERRS002", "オリジナル情報誌の作成"));
		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] 物件データを取得する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String requestBukkenData() throws Exception {

		// セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		// 物件№
		String bukkenNo = request.getParameter("bukkenNo");
		// 部屋№
		String heyaNo = request.getParameter("heyaNo");

		// 物件データを取得する
		Map<String, Object> result = ukg05020Services.getBukkenData(bukkenNo, heyaNo);
		// 掲載物件データを設定する
		ukg05020Data = new HashMap<String, Object>();
		List<Map<String, Object>> bukkenList = (List<Map<String, Object>>) result.get("bukkenList");
		if(bukkenList != null && bukkenList.size() != 0){
			Map<String, Object> bukkenData = (Map<String, Object>) bukkenList.get(0);
			// 築年月
			try {
				if(!Util.isEmpty((String) bukkenData.get("TIKUNENGETU"))){
					Date chikuNengetsu = new SimpleDateFormat(Consts.FORMAT_DATE_YMD).parse((String) bukkenData.get("TIKUNENGETU"));
					bukkenData.put("TIKUNENGETU_DISP", Util.formatDate(chikuNengetsu, Consts.FORMAT_DATE_YYYY_MM));
				}else{
					bukkenData.put("TIKUNENGETU_DISP","");
				}
			} catch (ParseException e) {
				bukkenData.put("TIKUNENGETU_DISP","");
			}
			// 間取図パス
			bukkenData.put("MADORI_IMAGE_PATH_DISP", Util.wmfToSVG((String) bukkenData.get("MADORI_IMAGE_PATH"), 332f, 250f));
			// 家賃
			if(!Util.isEmpty((String) bukkenData.get("YATIN"))){
				bukkenData.put("YATIN_DISP", Util.getMoneyFormat(Long.valueOf((String)bukkenData.get("YATIN"))).trim());
			}else{
				bukkenData.put("YATIN_DISP","0");
			}
			// 共益費
			if(!Util.isEmpty((String) bukkenData.get("KYO_KANHI"))){
				bukkenData.put("KYO_KANHI_DISP", Util.getMoneyFormat(Long.valueOf((String)bukkenData.get("KYO_KANHI"))).trim());
			}else{
				bukkenData.put("KYO_KANHI_DISP","0");
			}
			// 不動産会社物件No.
			String estateBukkenNo = DispFormat.getBukkenOtoiawaseNo((String)bukkenData.get("ESTATE_BUKKEN_NO") ,(String)bukkenData.get("HEYA_NO"));
			bukkenData.put("ESTATE_BUKKEN_NO_DISP", estateBukkenNo);
			ukg05020Data.put("bukkenData", bukkenData);
		}else{
			ukg05020Data.put("dataFound", "1");
			ukg05020Data.put("rstMsg", Util.getServerMsg("ERRS011", "対象の部屋は入居中"));
		}
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 物件データを取得する(複数)
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String requestFukusuuBukkenData() throws Exception {

		// セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		// 物件№
		String bukkenNo = request.getParameter("bukkenNo");
		// 部屋№
		String heyaNoList = request.getParameter("heyaNoList");

		// 物件データを取得する
		Map<String, Object> bukkenData = ukg05020Services.getFukusuuBukkenData(bukkenNo, heyaNoList);
		// 掲載物件データを設定する
		ukg05020Data = new HashMap<String, Object>();
		List<Map<String, Object>> bukkenList = (List<Map<String, Object>>) bukkenData.get("bukkenList");
		if(bukkenList != null && bukkenList.size() != 0){
			// リストに部屋のソートを保存する
			List<Map<String, Object>> newBukkenList = new ArrayList<Map<String, Object>>();
			String [] heyaArray = heyaNoList.replaceAll("'", "").split(",");
			for(String heyaNo : heyaArray){
				for(Map<String, Object> bukken : bukkenList){
					if(heyaNo.equals(bukken.get("HEYA_NO"))){
						newBukkenList.add(bukken);
						continue;
					}
				}
			}
			// 掲載物件データ設定r
			bukkenData = keisaiDataSet(newBukkenList);
		}else{
			ukg05020Data.put("dataFound", "1");
			ukg05020Data.put("rstMsg", Util.getServerMsg("ERRS011", "対象の物件は入居中"));
		}
		ukg05020Data.put("bukkenData", bukkenData);
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 最新更新日付まだ発行連番を取得する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String requestUpdDateMaxSeq() throws Exception {

		// セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String) mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String) mapLoginInfo.get(Consts.POST_CD);
		// 遷移フラグ(1:新規 2:編集 3:流用)
		String strProcessType = request.getParameter("processType");
		// 情報誌発行日
		String hakkouDate = request.getParameter("hakkouDate");
		// 発行連番
		String seq = request.getParameter("seq");
		// 更新日付
		String updDate = request.getParameter("updDate");

		// 物件データを取得する
		Map<String, Object> result = ukg05020Services.getUpdDateMaxSeq(strKaisyaCd, strPostCd, hakkouDate, seq, strProcessType);
		// 掲載物件データを設定する
		ukg05020Data = new HashMap<String, Object>();
		ukg05020Data.put("sysdate", result.get("rst_sys_date"));
		if("2".equals(strProcessType)){
			if(!updDate.equals(result.get("rst_data"))){
				ukg05020Data.put("MSG", Util.getServerMsg("ERRS009"));
			}
		}else{
			seq = (String) result.get("rst_data");
			if(Integer.valueOf(seq) >= 100){
				ukg05020Data.put("MSG", Util.getServerMsg("ERRS012","発行連番"));
			}else{
				ukg05020Data.put("seq", seq);
			}
		}

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 情報誌データを保存する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String saveMagazineData() throws Exception {

		// セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String) mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String) mapLoginInfo.get(Consts.POST_CD);
		String strUser = (String) mapLoginInfo.get(Consts.USER_CD);
		ObjectMapper dataMap = new ObjectMapper();
		// 処理フラグ
		String flag = request.getParameter("flag");
		// 遷移フラグ
		String processType = request.getParameter("processType");
		// 情報誌データ
		String magazine = request.getParameter("magazine");
		//String文字列からjsonデータを読み込み、MAPデータに置き換える。
		Map<String, Object> magazineData = dataMap.readValue(magazine,
				new TypeReference<Map<String, Object>>() {});
		// 情報誌ページデータ
		String pageList = request.getParameter("pageList");
		List<Map<String, Object>> pageDataList = dataMap.readValue(pageList,
				new TypeReference<List<Map<String, Object>>>() {});
		// 情報誌物件データ
		String bukkenList = request.getParameter("bukkenList");
		List<Map<String, Object>> bukkenDataList = null;
		if(!Util.isEmpty(bukkenList)){
			bukkenDataList = dataMap.readValue(bukkenList,
					new TypeReference<List<Map<String, Object>>>() {});
		}else{
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("", "");
			bukkenDataList = new ArrayList<Map<String, Object>>();
			bukkenDataList.add(map);
		}
		// 情報誌プリセットデータ
		String presetTabData = request.getParameter("presetTabData");
		List<Map<String, Object>> presetTabList = dataMap.readValue(presetTabData,
				new TypeReference<List<Map<String, Object>>>() {});

		ukg05020Data = new HashMap<String, Object>();
		List<KibouJyoukennBean> kiboujyoukenBeanList = new ArrayList<KibouJyoukennBean>();

		KibouJyoukennBean kiboujyoukenBean1 = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + "1");
		MagazineBaseData magazineBaseData1 = kiboujyoukenBean1.getMagazineBaseData();
		String strHakkoubi = magazineBaseData1.getJyouhoushiHakkoubi();
		String strSeq = magazineBaseData1.getHakkouSeq();

		// 情報誌物件データを保存する
		for (int i = 1; i < 6; i++) {
			KibouJyoukennBean kiboujyoukenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + i);
			MagazineBaseData magazineBaseData = null;
			if(kiboujyoukenBean == null){
				if("2".equals(processType)){
					continue;
				}
				kiboujyoukenBean = new KibouJyoukennBean();
				kiboujyoukenBean.setKaisyaCd(strKaisyaCd);
				kiboujyoukenBean.setPostCd(strPostCd);
				magazineBaseData = new MagazineBaseData();
				magazineBaseData.setPurisettoNo((String) presetTabList.get(i-1).get("presetNo"));
				if("3".equals(processType)){
					kiboujyoukenBean.setGamenKubun(Consts.UKG05021_GAMEN_KB);
					magazineBaseData.setJyouhoushiHakkoubi(strHakkoubi);
					magazineBaseData.setHakkouSeq(strSeq);
					kiboujyoukenBean.setMagazineBaseData(magazineBaseData);
					bukkenSearchServiceCom.getInitInfo(kiboujyoukenBean);
				}
			}else{
				magazineBaseData = kiboujyoukenBean.getMagazineBaseData();
			}
			magazineBaseData.setPurisettoName((String) presetTabList.get(i-1).get("presetName"));
			magazineBaseData.setJyouhoushiHakkoubi((String) magazineData.get("HAKKOU_DATE"));
			magazineBaseData.setHakkouSeq((String) magazineData.get("SEQ"));
			kiboujyoukenBean.setMagazineBaseData(magazineBaseData);
			kiboujyoukenBeanList.add(kiboujyoukenBean);
		}
		String resultMsg = "";
		Map<String, Object> result = null;
		try{
			result = bukkenSearchServiceCom.saveMagazineData(flag, processType, strUser, magazineData, pageDataList, bukkenDataList, kiboujyoukenBeanList);
		}catch(Exception e){
			resultMsg = e.getMessage();
			if("ERRS009".equals(resultMsg)){
				ukg05020Data.put("MSG", Util.getServerMsg("ERRS009"));
			}else{
				ukg05020Data.put("MSG", Util.getServerMsg("ERRS002", "登録"));
			}
			ukg05020Data.put("result", "FAIL");
			return "ajaxProcess";
		}
		ukg05020Data.put("upd_date", result.get("rst_upd_date"));
		ukg05020Data.put("MSG", Util.getServerMsg("MSGS004", "編集中の情報誌"));
		ukg05020Data.put("result", "SUCCESS");
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 情報誌編集データを状態更新する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String printmagazineData() throws Exception {

		// セッションを取得
		HttpServletRequest req = ServletActionContext.getRequest();
		// ユーザ情報を取得
		Map<String, Object> mapLoginInfo = getLoginInfo();
		String strKaisyaCd = (String) mapLoginInfo.get(Consts.KAISYA_CD);
		String strPostCd = (String) mapLoginInfo.get(Consts.POST_CD);
		String strUser = (String) mapLoginInfo.get(Consts.USER_CD);
		// 状態
		String status = req.getParameter("status");
		// 情報誌パス
		String pdfPath = req.getParameter("pdfPath");
		// 情報誌発行日
		String hakkouDate = req.getParameter("hakkouDate");
		// 発行連番
		String seq = req.getParameter("seq");
		// 更新日時
		String updDate = req.getParameter("updDate");

		// 物件データを取得する
		Map<String, Object> result = ukg05020Services.printmagazineData(strKaisyaCd, strPostCd, hakkouDate, seq, status, strUser, pdfPath, updDate);

		// 掲載物件データを設定する
		ukg05020Data = new HashMap<String, Object>();
		ukg05020Data.put("result", result.get("rst_code"));
		if("ERRS003".equals(result.get("rst_code"))){
			ukg05020Data.put("MSG", Util.getServerMsg("ERRS009"));
		}
		ukg05020Data.put("upd_date", result.get("p_rst_update_date"));
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] INFOログを出力する
	 * @return ログを出力結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String printLog() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		String layoutType = request.getParameter("layoutType");
		String madoriSetumeiUmu = request.getParameter("madoriSetumeiUmu");
		String setubiItiranUmu = request.getParameter("setubiItiranUmu");
		String uraHyoushiUmu = request.getParameter("uraHyoushiUmu");
		String bukkenSu = request.getParameter("bukkenSu");
		String userId = request.getParameter("userId");
		String message = "[オリジナル情報誌作成]情報誌印刷 ユーザID：" + userId + "  表紙タイプ:"
				+ layoutType + " 設備一覧:" + madoriSetumeiUmu + " 間取説明:"
				+ setubiItiranUmu + " 裏表紙:"+ uraHyoushiUmu + " 物件数:"+ bukkenSu;
		log.info(message);
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 情報誌データ処理
	 * @param initData 情報誌データ
	 * @param strProcessType 処理フラグ
	 * @param hyoushiTypeFlag 表紙削除フラグ
	 * @return 情報誌データ
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	private void magazineBukkenDataProcess(Map<String, Object> initData, String strProcessType, String hyoushiTypeFlag)
			throws Exception {

		// 情報誌データを取得する
		Map<String, Object> magazineData = ((List<Map<String, Object>>) initData.get("rst_magazine")).get(0);

		// 削除フラグが'1'：削除済の場合、'0'を設定する
		if(("2".equals(strProcessType) || "3".equals(strProcessType)) && "1".equals(hyoushiTypeFlag)){
			magazineData.put("HYOUSHI_TYPE", "0");
			magazineData.put("HYOUSHI_TYPE_CODE", "00");
			magazineData.put("HYOUSHI_TYPE_FLAG", "1");
		}

		magazineData.put("HAKKOU_DATE_COPY", magazineData.get("HAKKOU_DATE"));
		magazineData.put("SEQ_COPY", magazineData.get("SEQ"));
		if("3".equals(strProcessType)){
			magazineData.put("HAKKOU_DATE", Util.formatDate(new Date(), Consts.FORMAT_DATE_YMD));
			magazineData.put("SEQ", null);
		}

		ukg05020Data.put("magazineData", magazineData);

		// 情報誌ページ物件データを取得する
		List<Map<String, Object>> magazineBukkenDataList = (List<Map<String, Object>>) initData
				.get("rst_magazine_bukken");

		// プリセット名データを取得する
		List<Map<String, Object>> presetNameList = (List<Map<String, Object>>) initData
				.get("rst_preset_name");

		// ページ番号(前)
		String lastPage = null;
		// 掲載位置(前)
		String lastkeisaiNo = null;
		// ページリスト
		List<Map<String, Object>> pageList = new ArrayList<Map<String, Object>>();
		// 掲載物件リスト
		List<Map<String, Object>> keisaiList = null;
		// 物件リスト
		List<Map<String, Object>> bukkenList = null;
		for (Map<String, Object> pageBukkenData : magazineBukkenDataList) {
			if (!pageBukkenData.get("PAGE").equals(lastPage)) {
				Map<String, Object> pageData = new HashMap<String, Object>();
				// ページ番号
				pageData.put("PAGE", pageBukkenData.get("PAGE"));
				// 物件区分
				pageData.put("BUKKEN_KB", pageBukkenData.get("BUKKEN_KB"));
				// レイアウトタイプ
				pageData.put("LAYOUT_TYPE", pageBukkenData.get("LAYOUT_TYPE"));
				// 掲載可能数
				pageData.put("KEISAI_COUNT", pageBukkenData.get("KEISAI_COUNT"));
				// キャッチコピー
				pageData.put("CATCH_COPY", pageBukkenData.get("CATCH_COPY"));
				// 掲載物件リスト
				keisaiList = new ArrayList<Map<String, Object>>();
				pageData.put("keisaiList", keisaiList);
				// ページリストに追加する。
				pageList.add(pageData);
				lastkeisaiNo = null;
			}
			if(pageBukkenData.get("KEISAI_NO") != null){
				if (!pageBukkenData.get("KEISAI_NO").equals(lastkeisaiNo)) {
					Map<String, Object> keisaiBukken = new HashMap<String, Object>();
					keisaiBukken.put("KEISAI_NO", pageBukkenData.get("KEISAI_NO"));
					// 掲載物件リスト
					bukkenList = new ArrayList<Map<String, Object>>();
					keisaiBukken.put("bukkenList", bukkenList);
					// 掲載物件リストに追加する。
					keisaiList.add(keisaiBukken);
				}
				Map<String, Object> bukkenData = new HashMap<String, Object>();
				// 物件連番
				bukkenData.put("BUKKEN_SEQ", pageBukkenData.get("BUKKEN_SEQ"));
				// ホームメイト物件No
				bukkenData.put("HOMEMATE_BUKKEN_NO", pageBukkenData.get("HOMEMATE_BUKKEN_NO"));
				// 不動産会社物件No.
				bukkenData.put("ESTATE_BUKKEN_NO", pageBukkenData.get("ESTATE_BUKKEN_NO"));
				// 部屋No.
				bukkenData.put("HEYA_NO", pageBukkenData.get("HEYA_NO"));
				// 物件名
				bukkenData.put("BUKKEN_NAME", pageBukkenData.get("BUKKEN_NAME"));
				// 所在地
				bukkenData.put("JYUSYO", pageBukkenData.get("JYUSYO"));
				// 交通
				bukkenData.put("TRAFFIC", pageBukkenData.get("TRAFFIC"));
				// 家賃
				bukkenData.put("YATIN", pageBukkenData.get("YATIN"));
				// 共益費/管理費名
				bukkenData.put("KYO_KAN_NAME", pageBukkenData.get("KYO_KAN_NAME"));
				// 共益費
				bukkenData.put("KYO_KANHI", pageBukkenData.get("KYO_KANHI"));
				// 間取り
				bukkenData.put("MADORI", pageBukkenData.get("MADORI"));
				// 面積
				bukkenData.put("MENSEKI", pageBukkenData.get("MENSEKI"));
				// 部屋種別
				bukkenData.put("BUKKEN_SB", pageBukkenData.get("BUKKEN_SB"));
				// 部屋種別名
				bukkenData.put("BUKKEN_SB_NAME", pageBukkenData.get("BUKKEN_SB_NAME"));
				// 築年月
				bukkenData.put("TIKUNENGETU", pageBukkenData.get("TIKUNENGETU"));
				// 外観画像パス
				bukkenData.put("GAIKAN_IMAGE_PATH", pageBukkenData.get("GAIKAN_IMAGE_PATH"));
				// 間取図パス
				bukkenData.put("MADORI_IMAGE_PATH", pageBukkenData.get("MADORI_IMAGE_PATH"));
				// 複数フラグ
				bukkenData.put("MDRCOUNT", pageBukkenData.get("MDRCOUNT"));
				// 入居状況
				bukkenData.put("NYUKYO_SB_CD", pageBukkenData.get("NYUKYO_SB_CD"));
// ADD START 2015.02.13 h-nakano 部屋情報削除対応(C39067)
				// Success21削除フラグ
				bukkenData.put("LOGICAL_DEL_FLG", pageBukkenData.get("LOGICAL_DEL_FLG"));
// ADD END   2015.02.13 h-nakano 部屋情報削除対応(C39067)
				// 物件リストに追加する。
				bukkenList.add(bukkenData);
			}
			lastkeisaiNo = (String) pageBukkenData.get("KEISAI_NO");
			lastPage = (String) pageBukkenData.get("PAGE");
		}

		for (Map<String, Object> pageData : pageList) {
			keisaiList = (List<Map<String, Object>>) pageData.get("keisaiList");
			int kaisaiNum = 1;
			int layoutCount = Integer.valueOf((String) pageData
					.get("KEISAI_COUNT"));
			List<Map<String, Object>> newKeisaiList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> keisaiBukken : keisaiList) {
				// 掲載物件データ設定
				int keisaiCount = Integer.valueOf((String) keisaiBukken.get("KEISAI_NO")).intValue() - kaisaiNum;
				if(keisaiCount > 0){
					for (int i = keisaiCount; i > 0; i--) {
						newKeisaiList.add(new HashMap<String, Object>());
						kaisaiNum++;
					}
				}

				List<Map<String, Object>> keisaiBukkenList = (List<Map<String, Object>>) keisaiBukken.get("bukkenList");
				if(keisaiBukkenList != null && keisaiBukkenList.size() != 0){
					Map<String, Object> newKeisaiBukken = keisaiDataSet(keisaiBukkenList);
					newKeisaiBukken.put("KEISAI_NO", keisaiBukken.get("KEISAI_NO"));
					newKeisaiList.add(newKeisaiBukken);
				}

				kaisaiNum++;
			}
			for (int i = layoutCount - kaisaiNum; i >= 0; i--) {
				newKeisaiList.add(new HashMap<String, Object>());
			}
			pageData.put("keisaiList", newKeisaiList);
		}

		ukg05020Data.put("pageList", pageList);
		ukg05020Data.put("presetNameList", presetNameList);
	}

	/**
	 * <pre>
	 * [説 明] 掲載物件データ設定
	 * @param bukkenList 物件リストデータ
	 * @return 掲載物件データ
	 * </pre>
	 */
	private Map<String, Object> keisaiDataSet(List<Map<String, Object>> bukkenList) {

		Map<String, Object> keisaiBukken = new HashMap<String, Object>();
		keisaiBukken.put("bukkenList", bukkenList);
		ArrayList<String> heyaNoList = new ArrayList<String>();
		long maxYatin = 0;
		long minYatin = 0;
		long maxkyoKanhi = 0;
		long minKyoKanhi = 0;
		Map<String, Object> bukken;	// 物件情報
		for (int i = bukkenList.size() - 1; i >= 0; i--) {
			bukken = bukkenList.get(i);
			if("1".equals(bukken.get("NYUKYO_SB_CD")) && bukkenList.size() > 1){
				bukkenList.remove(bukken);
				continue;
			}
// ADD START 2015.02.13 h-nakano 部屋情報削除対応(C39067)
			if("1".equals(bukken.get("LOGICAL_DEL_FLG")) && bukkenList.size() > 1){
				bukkenList.remove(bukken);
				continue;
			}
// ADD END   2015.02.13 h-nakano 部屋情報削除対応(C39067)
			// 部屋No.
			heyaNoList.add((String) bukken.get("HEYA_NO"));
			// 家賃
			if(Util.isEmpty((String)bukken.get("YATIN"))){
				minYatin = 0;
			}else{
				// 最大家賃
				if(Long.valueOf((String)bukken.get("YATIN")) > maxYatin){
					maxYatin = Long.valueOf((String)bukken.get("YATIN"));
				}
				// 最小家賃
				if(Long.valueOf((String)bukken.get("YATIN")) < minYatin || minYatin == 0){
					minYatin = Long.valueOf((String)bukken.get("YATIN"));
				}
			}
			// 共益費
			if(Util.isEmpty((String)bukken.get("KYO_KANHI"))){
				minKyoKanhi = 0;
			}else{
				// 最大共益費
				if(Long.valueOf((String)bukken.get("KYO_KANHI")) > maxkyoKanhi){
					maxkyoKanhi = Long.valueOf((String)bukken.get("KYO_KANHI"));
				}
				// 最小共益費
				if(Long.valueOf((String)bukken.get("KYO_KANHI")) < minKyoKanhi || minKyoKanhi == 0){
					minKyoKanhi = Long.valueOf((String)bukken.get("KYO_KANHI"));
				}
			}
		}
		String heya = "";
		for(int i = heyaNoList.size()-1; i >= 0; i--){
			heya += "," + heyaNoList.get(i);
		}
		heya = heya.substring(1,heya.length());
	    bukken = bukkenList.get(0);
		// ホームメイト物件No
		keisaiBukken.put("HOMEMATE_BUKKEN_NO", bukken.get("HOMEMATE_BUKKEN_NO"));
		// 不動産会社物件No.
		String estateBukkenNo = DispFormat.getBukkenOtoiawaseNo(
				(String) bukken.get("ESTATE_BUKKEN_NO"),
				(String) bukken.get("HEYA_NO"));
		keisaiBukken.put("ESTATE_BUKKEN_NO", estateBukkenNo);
		// 物件名
		keisaiBukken.put("BUKKEN_NAME", bukken.get("BUKKEN_NAME"));
		// 物件区分
		keisaiBukken.put("BUKKEN_KB", bukken.get("BUKKEN_KB"));
		// 所在地
		keisaiBukken.put("JYUSYO", bukken.get("JYUSYO"));
		// 交通
		keisaiBukken.put("TRAFFIC", bukken.get("TRAFFIC"));
		// 間取り
		keisaiBukken.put("MADORI", bukken.get("MADORI"));
		// 面積
		keisaiBukken.put("MENSEKI", bukken.get("MENSEKI"));
		// 部屋種別名
		keisaiBukken.put("BUKKEN_SB_NAME", bukken.get("BUKKEN_SB_NAME"));
		// 築年月
		try {
			if(!Util.isEmpty((String) bukken.get("TIKUNENGETU"))){
				Date chikuNengetsu = new SimpleDateFormat(Consts.FORMAT_DATE_YMD).parse((String) bukken.get("TIKUNENGETU"));
				keisaiBukken.put("TIKUNENGETU", Util.formatDate(chikuNengetsu, Consts.FORMAT_DATE_YYYY_MM));
			}else{
				keisaiBukken.put("TIKUNENGETU", "");
			}
		} catch (ParseException e) {
			keisaiBukken.put("TIKUNENGETU", "");
		}
		// 外観画像パス
		keisaiBukken.put("GAIKAN_IMAGE_PATH", bukken.get("GAIKAN_IMAGE_PATH"));
		// 間取図パス
		keisaiBukken.put("MADORI_IMAGE_PATH", Util.wmfToSVG((String) bukken.get("MADORI_IMAGE_PATH"), 332f, 250f));
		// 複数フラグ
		if("1".equals(bukken.get("NYUKYO_SB_CD"))){
			keisaiBukken.put("MDRCOUNT", Integer.valueOf(bukken.get("MDRCOUNT").toString()) + 1);
		}else{
			keisaiBukken.put("MDRCOUNT", bukken.get("MDRCOUNT"));
		}
		// 入居状況
		keisaiBukken.put("NYUKYO_SB_CD", bukken.get("NYUKYO_SB_CD"));
// ADD START 2015.02.13 h-nakano 部屋情報削除対応(C39067)
		// Success21削除フラグ
		keisaiBukken.put("LOGICAL_DEL_FLG", bukken.get("LOGICAL_DEL_FLG"));
// ADD END   2015.02.13 h-nakano 部屋情報削除対応(C39067)
		// 物件名
		keisaiBukken.put("BUKKEN_HEYA", keisaiBukken.get("BUKKEN_NAME") + "(" + heya + ")");
		// 家賃
		keisaiBukken.put("YATIN",maxYatin == minYatin ? Util.getMoneyFormat(maxYatin).trim() : Util.getMoneyFormat(minYatin).trim() + "～" + Util.getMoneyFormat(maxYatin).trim());
		// 共益費/管理費名
		keisaiBukken.put("KYO_KAN_NAME", bukken.get("KYO_KAN_NAME"));
		// 共益費
		keisaiBukken.put("KYO_KANHI", maxkyoKanhi == minKyoKanhi ? Util.getMoneyFormat(maxkyoKanhi).trim() : Util.getMoneyFormat(minKyoKanhi).trim() + "～" + Util.getMoneyFormat(maxkyoKanhi).trim());
		return keisaiBukken;
	}
}