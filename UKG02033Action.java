/**
 * @システム名: 市区町村リスト
 * @ファイル名: IUKG02033Action.java
 * @更新日付： 2012/05/29
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴: 2013/05/17  SYS_ 郭凡    (案件C37109-1)受付システムSTEP2要望対応
 *           2014/01/21  SYS_ 謝超    (案件C37075)オリジナル情報誌リプレース
 *           2018/01/23  SYS_肖剣生    (案件C42036)デザイン変更
 */
package jp.co.token.uketuke.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.CityBean;
import jp.co.token.uketuke.formbean.CityBigBean;
import jp.co.token.uketuke.formbean.KibouJyoukennBean;
import jp.co.token.uketuke.service.IUKG02031Service;
import jp.co.token.uketuke.service.IUKG02033Service;
import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 市区町村リスト
 * [説 明]市区町村リストする。
 * @author [作 成] 2012/05/29 SYS_郭
 * </pre>
 */
public class UKG02033Action extends BaseAction {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4778159824126799116L;

	/**
	 * CONST_COMPARATOR
	 */
	private static final DataComparator CONST_COMPARATOR = new DataComparator();

	//########## 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
	/**
	 * CONST_MATI_COMPARATOR
	 */
	private static final MatiDataComparator CONST_MATI_COMPARATOR = new MatiDataComparator();
	//########## 2014/01/21  謝超 ADD End

	/** 市区町村リスト画面用データ */
	private Map<String, Object> ukg02033Data = new HashMap<String, Object>();

	/** 市区町村リストサービス */
	private IUKG02033Service UKG02033Services;
	
	// 2018/01/23 SYS_肖剣生 ADD START (案件C42036)デザイン変更
	/** 市区町村リストサービス */
	private IUKG02031Service UKG02031Services;
	// 2018/01/23 SYS_肖剣生 ADD END (案件C42036)デザイン変更

	/** 選択最大数 */
	private String hidMaxSentaku;

	//########## 2014/03/20  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
	/** 町最大合計数 */
	private String hidMaxMatisuu;
	//########## 2014/03/20  謝超 ADD End


	/** 選択された県コード */
	private String prefectureCd;

	/**
	 * <pre>
	 * [説 明] 選択された県コードを取得する。
	 * @return the 選択された県コード
	 * </pre>
	 */
	public String getPrefectureCd() {

		return prefectureCd;
	}

	/**
	 * <pre>
	 * [説 明] 選択された県コードを設定する。
	 * @param prefectureCd 選択された県コード
	 * </pre>
	 */
	public void setPrefectureCd(String prefectureCd) {

		this.prefectureCd = prefectureCd;
	}

	/**
	 * @return the hidMaxSentaku
	 */
	public String getHidMaxSentaku() {

		return hidMaxSentaku;
	}

	/**
	 * <pre>
	 * [説 明] 選択最大数を設定する。
	 * @param hidMaxSentaku 選択最大数
	 * </pre>
	 */
	public void setHidMaxSentaku(String hidMaxSentaku) {

		this.hidMaxSentaku = hidMaxSentaku;
	}

	//########## 2014/03/20  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
	/**
	 * @return 町最大合計数
	 */
	public String getHidMaxMatisuu() {
		return hidMaxMatisuu;
	}

	/**
	 * [説 明]町最大合計数を設定する。
	 * @param hidMaxMatisuu 町最大合計数
	 */
	public void setHidMaxMatisuu(String hidMaxMatisuu) {
		this.hidMaxMatisuu = hidMaxMatisuu;
	}
	//########## 2014/03/20  謝超 ADD End

	/**
	 * @return the ukg02033Data
	 */
	public Map<String, Object> getUkg02033Data() {

		return ukg02033Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg02033Data 画面のデータ
	 * </pre>
	 */
	public void setUkg02033Data(Map<String, Object> ukg02033Data) {

		this.ukg02033Data = ukg02033Data;
	}

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param services サービス対象
	 * </pre>
	 */
	public void setUKG02033Services(IUKG02033Service uKG02033Services) {

		UKG02033Services = uKG02033Services;
	}

	// 2018/01/23 SYS_肖剣生 ADD START (案件C42036)デザイン変更
	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param services サービス対象
	 * </pre>
	 */
	public void setUKG02031Services(IUKG02031Service uKG02031Services) {

		UKG02031Services = uKG02031Services;
	}
	// 2018/01/23 SYS_肖剣生 ADD END (案件C42036)デザイン変更
	
	/**
	 * <pre>
	 * [説 明] 都道府県リストのデータ設定
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		smkDataGet();
		// 2018/01/23 SYS_肖剣生 ADD START (案件C42036)デザイン変更
		List<Map<String, Object>> JusyoDataList = (List<Map<String, Object>>) UKG02031Services.getJusyoData("010");
		ukg02033Data.put("JUSYO_LIST", JusyoDataList);
		
		HttpServletRequest request = ServletActionContext.getRequest();
		String presetNo = Util.toEmpty(request.getParameter("presetNo"));
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN + presetNo);
		// 不具合5対応
		if (kibouJyoukennBean == null) {
			kibouJyoukennBean = new KibouJyoukennBean();
		}

		String jyokenDisp = kibouJyoukennBean.getKiboujyokenDisp();
		if (Util.isEmpty(jyokenDisp)) {
			jyokenDisp = "";
		}
		if (Util.isEmpty(kibouJyoukennBean.getKibouJyoukennCode()) || "2".equals(kibouJyoukennBean.getKibouJyoukennCode())) {
			ukg02033Data.put("kibouchiyiki", jyokenDisp.replace(",", "&nbsp;&nbsp;<span class=\"separtor\"></span>&nbsp;&nbsp;")
					.replace("、", "&nbsp;&nbsp;<span class=\"separtor\"></span>&nbsp;&nbsp;"));
		} else {
			ukg02033Data.put("kibouchiyiki", "");
		}
		
		// 2018/01/23 SYS_肖剣生 ADD END (案件C42036)デザイン変更
		// 2018/02/12 SYS_肖剣生 UPD START (案件C42036)デザイン変更
		//return successForward();
		if (Util.isEmpty(presetNo)) {
			return successForward();
		} else {
			return "success_ff_popup";
		}
		// 2018/02/12 SYS_肖剣生 UPD END (案件C42036)デザイン変更
	}

	/**
	 * 希望家賃の条件により検索項目を設定する
	 * 
	 * @param kyokiKanriHiFlg
	 * @param parkHiFlg
	 * @return
	 */
	private String yatinSearchMod(String kyokiKanriHiFlg,
	                              String parkHiFlg) {

		String yatinMod = "";
		if ("0".equals(kyokiKanriHiFlg) && "0".equals(parkHiFlg)) {
			yatinMod = "1";
		} else if ("1".equals(kyokiKanriHiFlg) && "0".equals(parkHiFlg)) {
			yatinMod = "2";
		} else if ("0".equals(kyokiKanriHiFlg) && "1".equals(parkHiFlg)) {
			yatinMod = "3";
		} else if ("1".equals(kyokiKanriHiFlg) && "1".equals(parkHiFlg)) {
			yatinMod = "4";
		}
		return yatinMod;
	}

	/**
	 * <pre>
	 * [説 明] 市町区のデータ設定
	 * @return 処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String smkDataGet() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		// 検索条件を取得する
		HttpSession session = request.getSession();
		//########## 2014/01/21  謝超 UPD Start (案件番号C37075)オリジナル情報誌リプレース
		// KibouJyoukennBean kibouJyokenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);
		// プリセットコード
		String presetNo = Util.toEmpty(request.getParameter("presetNo"));
		KibouJyoukennBean kibouJyokenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN+presetNo);
		//########## 2014/01/21  謝超 UPD End
		if (kibouJyokenBean == null) {
			kibouJyokenBean = new KibouJyoukennBean();
		}
		// 最大選択数
		hidMaxSentaku = PropertiesReader.getIntance().getProperty(Consts.UKG02033_MAXSETTEISUU);

		//########## 2014/03/20  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		//町最大合計数
		hidMaxMatisuu = PropertiesReader.getIntance().getProperty(Consts.UKG02033_MAXMATISETTEISUU);
		//########## 2014/03/20  謝超 ADD End
		// 設定されている市区郡
		List<CityBean> checkedDataList = kibouJyokenBean.getJyusyoList();

		if (checkedDataList != null && checkedDataList.size() > 0 && "".equals(prefectureCd)) {

			prefectureCd = kibouJyokenBean.getPrefectureCd();
		}

		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo(); // parasoft-suppress CDD.DUPC "C37075JTest対応"
		String kaisyacd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();

		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		//########## 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		//　画面がUKG05022以外の場合
		if(!Consts.UKG05022_GAMEN_KB.equals(kibouJyokenBean.getGamenKubun())){
		//########## 2014/01/21  謝超 ADD End
		String yatintype = "";
		String yatinTop = "0";
		String yatinLow = "0";
		String mensekiTop = "0";
		String mensekiLow = "0";

		if ("1".equals(kibouJyokenBean.getBukkenKb())) {
			yatintype = yatinSearchMod(kibouJyokenBean.getKyokiKanriHiFlg(), kibouJyokenBean.getParkHiFlg());
			if (Integer.parseInt(kibouJyokenBean.getYatinTopVal()) >= Integer.parseInt(kibouJyokenBean.getYatinLowVal())) {
				yatinTop = kibouJyokenBean.getYatinTopVal();
				yatinLow = kibouJyokenBean.getYatinLowVal();
			} else {
				yatinTop = kibouJyokenBean.getYatinLowVal();
				yatinLow = kibouJyokenBean.getYatinTopVal();
			}

			paramMap.put("param_reikinzerokb", kibouJyokenBean.getRekinZero());
			paramMap.put("param_sikihosyozerokb", kibouJyokenBean.getSikihosyoukinZero());
			paramMap.put("param_madoricd", kibouJyokenBean.getKiboMardoriList());
			paramMap.put("param_setubicd", kibouJyokenBean.getMustSetubi());

		} else if ("2".equals(kibouJyokenBean.getBukkenKb())) {

			yatintype = yatinSearchMod(kibouJyokenBean.getKyokiKanriHiFlg(), kibouJyokenBean.getParkHiFlg());
			if (Integer.parseInt(kibouJyokenBean.getYatinTopVal()) >= Integer.parseInt(kibouJyokenBean.getYatinLowVal())) {
				yatinTop = kibouJyokenBean.getYatinTopVal();
				yatinLow = kibouJyokenBean.getYatinLowVal();
			} else {
				yatinTop = kibouJyokenBean.getYatinLowVal();
				yatinLow = kibouJyokenBean.getYatinTopVal();
			}
			//2013/05/17    郭凡(SYS)  ADD START 案件C37109-1
		}
		//2013/05/17    郭凡(SYS)  ADD END 案件C37109-1

		//2013/05/17    郭凡(SYS)  ADD START 案件C37109-1
		if(!(Util.isEmpty(kibouJyokenBean.getKibouMensekiTopVal()) || Util.isEmpty(kibouJyokenBean.getKibouMensekiDownVal()))){
		//2013/05/17    郭凡(SYS)  ADD END 案件C37109-1
			if (Integer.parseInt(kibouJyokenBean.getKibouMensekiTopVal()) >= Integer.parseInt(kibouJyokenBean.getKibouMensekiDownVal())) {
				mensekiTop = kibouJyokenBean.getKibouMensekiTopVal();
				mensekiLow = kibouJyokenBean.getKibouMensekiDownVal();
			} else {
				mensekiTop = kibouJyokenBean.getKibouMensekiDownVal();
				mensekiLow = kibouJyokenBean.getKibouMensekiTopVal();
			}

			paramMap.put("param_kibouMensekiTop", mensekiTop);
			paramMap.put("param_kibouMensekiLow", mensekiLow);

		}
		paramMap.put("param_ekiminute", kibouJyokenBean.getEkiMiniute());
		paramMap.put("param_kibou_parking_flg", kibouJyokenBean.getParkMustFlg());
		paramMap.put("param_searchmod", kibouJyokenBean.getSearchMod());
		paramMap.put("param_tikunencd", kibouJyokenBean.getTikunenCd());
		paramMap.put("param_kaisyacd", kaisyacd);
		paramMap.put("param_tdfk_code", prefectureCd);
		paramMap.put("param_bukkenkb", kibouJyokenBean.getBukkenKb());
		paramMap.put("param_bukkensb", kibouJyokenBean.getBukkenSb());
		paramMap.put("param_yatintype", yatintype);
		paramMap.put("param_kibouyatintop", yatinTop);
		paramMap.put("param_kibouyatinlower", yatinLow);
		paramMap.put("param_uketuke_no", session.getAttribute(Consts.UKETUKENO));
		paramMap.put("param_shokihiyocd", kibouJyokenBean.getInitMoneyTotal());
		paramMap.put("param_nyukuukb", "0");
		//########## 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		// 管理区分
		paramMap.put("param_kanriKb", kibouJyokenBean.getKanriKubunList());
		// 画面がUKG05022の場合
		}else{
			// 県コード
			paramMap.put("param_tdfk_code", prefectureCd);
			// 会社コード
			paramMap.put("param_kaisyacd", kaisyacd);
			// 空室になった日数
			paramMap.put("param_kuusituniti", PropertiesReader.getIntance().getProperty(Consts.UKG05022_KUUSHITUNITISUU));
		}
		//受付コード
		paramMap.put("param_uketuke_no", "00000000");
		//########## 2014/01/21  謝超 ADD End

		// 市町区データList取得
		//########## 2014/01/21  謝超 UPD Start (案件番号C37075)オリジナル情報誌リプレース
		// @SuppressWarnings("unchecked") List<Map<String, Object>> SmkDataList = (List<Map<String, Object>>) UKG02033Services.getSmkData(paramMap).get("rst_smk_list");
		List<Map<String, Object>> SmkDataList = null;

		// 画面がUKG05022以外の場合
		if(!Consts.UKG05022_GAMEN_KB.equals(kibouJyokenBean.getGamenKubun())) {
			SmkDataList = (List<Map<String, Object>>) UKG02033Services.getSmkData(paramMap).get("rst_smk_list");

		// 画面がUKG05022の場合
		}else{
			SmkDataList = (List<Map<String, Object>>) UKG02033Services.getSmkDataSintyaku(paramMap).get("rst_smk_list");
		}

		//　希望条件の町データリスト
		List<CityBean> matiList = kibouJyokenBean.getJyusyoYuubinBangouList();
		//########## 2014/01/21  謝超 UPD End
		List<CityBigBean> listCityBig = new ArrayList<CityBigBean>();
		List<CityBean> listCity = null;
		CityBigBean bigBean = null;
		CityBean cityBean = null;
		String rowName = "";
		String staticRowName = "";

		for (int i = 0; i < SmkDataList.size(); i++) {
			// 市カナ取得
			String Cityk = String.valueOf(SmkDataList.get(i).get("CITY_K"));

			rowName = checkRow(Cityk);

			if (!rowName.equals(staticRowName)) {

				if (!"".equals(staticRowName)) {
					bigBean.setCitys(listCity);
					listCityBig.add(bigBean);
				}

				staticRowName = rowName;
				bigBean = new CityBigBean();
				listCity = new ArrayList<CityBean>();
				bigBean.setRow(staticRowName);

			}
			cityBean = new CityBean();
			cityBean.setCityname(String.valueOf(SmkDataList.get(i).get("CITY")));
			cityBean.setCityKana(String.valueOf(SmkDataList.get(i).get("CITY_K")));
			cityBean.setJusyoCd(String.valueOf(SmkDataList.get(i).get("JIS_JUSYO_CD")));
			cityBean.setBukkenCount(Integer.parseInt(SmkDataList.get(i).get("CNT").toString()));
			//########## 2014/01/21  謝超 UPD Start (案件番号C37075)オリジナル情報誌リプレース
			// cityBean.setCheckbox(checkjsCd(cityBean.getJusyoCd(), checkedDataList));
			// 市データのチェックボックスの状態をせって
			cityBean.setCheckbox(checkjsMatiCd(cityBean.getJusyoCd(), checkedDataList, matiList));
			//########## 2014/01/21  謝超 UPD End
			listCity.add(cityBean);
		}

		bigBean.setRow(staticRowName);
		bigBean.setCitys(listCity);
		listCityBig.add(bigBean);

		ukg02033Data = new HashMap<String, Object>();

		ukg02033Data.put("listKey", listCityBig);
		ukg02033Data.put("bukkenKb", kibouJyokenBean.getBukkenKb());
		//########## 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		// プリセットコード
		ukg02033Data.put("presetNo", presetNo);

		// 希望条件の町データリストを文字列　になる
		String strMatiList = "";
		List<CityBean> matiDataList = kibouJyokenBean.getJyusyoYuubinBangouList();
		if(null != matiDataList) {
			for (int i = 0; i < matiDataList.size(); i++) {
				strMatiList += "," + matiDataList.get(i).getJusyoCd() +
						"&" + matiDataList.get(i).getYuubinBangou() +
						"&" + matiDataList.get(i).getCityname();
			}

			if(!Util.isEmpty(strMatiList)){
				strMatiList = strMatiList.substring(1);
			}
		}
		ukg02033Data.put("strMatiList", strMatiList);
		//########## 2014/01/21  謝超 ADD End
		return "smkDataGet";

	}

	// 2014/03/24  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
	/**
	 * <pre>
	 * [説 明] 町のデータを取得
	 * @return 処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String matiListGet() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		// 検索条件を取得する
		HttpSession session = request.getSession();
		String presetNo = Util.toEmpty(request.getParameter("presetNo"));
		prefectureCd = Util.toEmpty(request.getParameter("prefectureCd"));
		String jisJusyoCd = Util.toEmpty(request.getParameter("jisJusyoCd"));
		KibouJyoukennBean kibouJyokenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN+presetNo);

		if (kibouJyokenBean == null) {
			kibouJyokenBean = new KibouJyoukennBean();
		}


		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		String kaisyacd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();

		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		if(!Consts.UKG05022_GAMEN_KB.equals(kibouJyokenBean.getGamenKubun())){
			String yatintype = "";
			String yatinTop = "0";
			String yatinLow = "0";
			String mensekiTop = "0";
			String mensekiLow = "0";

			if ("1".equals(kibouJyokenBean.getBukkenKb())) {

				yatintype = yatinSearchMod(kibouJyokenBean.getKyokiKanriHiFlg(), kibouJyokenBean.getParkHiFlg());
				if (Integer.parseInt(kibouJyokenBean.getYatinTopVal()) >= Integer.parseInt(kibouJyokenBean.getYatinLowVal())) {
					yatinTop = kibouJyokenBean.getYatinTopVal();
					yatinLow = kibouJyokenBean.getYatinLowVal();
				} else {
					yatinTop = kibouJyokenBean.getYatinLowVal();
					yatinLow = kibouJyokenBean.getYatinTopVal();
				}

				paramMap.put("param_reikinzerokb", kibouJyokenBean.getRekinZero());
				paramMap.put("param_sikihosyozerokb", kibouJyokenBean.getSikihosyoukinZero());
				paramMap.put("param_madoricd", kibouJyokenBean.getKiboMardoriList());
				paramMap.put("param_setubicd", kibouJyokenBean.getMustSetubi());

			} else if ("2".equals(kibouJyokenBean.getBukkenKb())) {

				yatintype = yatinSearchMod(kibouJyokenBean.getKyokiKanriHiFlg(), kibouJyokenBean.getParkHiFlg());
				if (Integer.parseInt(kibouJyokenBean.getYatinTopVal()) >= Integer.parseInt(kibouJyokenBean.getYatinLowVal())) {
					yatinTop = kibouJyokenBean.getYatinTopVal();
					yatinLow = kibouJyokenBean.getYatinLowVal();
				} else {
					yatinTop = kibouJyokenBean.getYatinLowVal();
					yatinLow = kibouJyokenBean.getYatinTopVal();
				}
			}

			if(!(Util.isEmpty(kibouJyokenBean.getKibouMensekiTopVal()) || Util.isEmpty(kibouJyokenBean.getKibouMensekiDownVal()))){
				if (Integer.parseInt(kibouJyokenBean.getKibouMensekiTopVal()) >= Integer.parseInt(kibouJyokenBean.getKibouMensekiDownVal())) {
					mensekiTop = kibouJyokenBean.getKibouMensekiTopVal();
					mensekiLow = kibouJyokenBean.getKibouMensekiDownVal();
				} else {
					mensekiTop = kibouJyokenBean.getKibouMensekiDownVal();
					mensekiLow = kibouJyokenBean.getKibouMensekiTopVal();
				}

				paramMap.put("param_kibouMensekiTop", mensekiTop);
				paramMap.put("param_kibouMensekiLow", mensekiLow);

			}
			paramMap.put("param_ekiminute", kibouJyokenBean.getEkiMiniute());
			paramMap.put("param_kibou_parking_flg", kibouJyokenBean.getParkMustFlg());
			paramMap.put("param_searchmod", kibouJyokenBean.getSearchMod());
			paramMap.put("param_tikunencd", kibouJyokenBean.getTikunenCd());
			paramMap.put("param_kaisyacd", kaisyacd);
			paramMap.put("param_tdfk_code", prefectureCd);
			paramMap.put("param_bukkenkb", kibouJyokenBean.getBukkenKb());
			paramMap.put("param_bukkensb", kibouJyokenBean.getBukkenSb());
			paramMap.put("param_yatintype", yatintype);
			paramMap.put("param_kibouyatintop", yatinTop);
			paramMap.put("param_kibouyatinlower", yatinLow);
			paramMap.put("param_uketuke_no", session.getAttribute(Consts.UKETUKENO));
			paramMap.put("param_shokihiyocd", kibouJyokenBean.getInitMoneyTotal());
			paramMap.put("param_nyukuukb", "0");
			paramMap.put("param_kanriKb", kibouJyokenBean.getKanriKubunList());
		}else{
			paramMap.put("param_kaisyacd", kaisyacd);
			paramMap.put("param_kuusituniti", PropertiesReader.getIntance().getProperty(Consts.UKG05022_KUUSHITUNITISUU));
		}
		paramMap.put("param_jis_jusyo_cd", jisJusyoCd);

		//受付コード
		paramMap.put("param_uketuke_no", "00000000");

		// 町データList取得
		List<Map<String, Object>> SmkDataList = null;
		if(!Consts.UKG05022_GAMEN_KB.equals(kibouJyokenBean.getGamenKubun())) {
			SmkDataList = (List<Map<String, Object>>) UKG02033Services.getSmkMatiData(paramMap).get("rst_smk_list");
		}else{
			SmkDataList = (List<Map<String, Object>>) UKG02033Services.getSmkMatiDataSintyaku(paramMap).get("rst_smk_list");
		}
		List<CityBean> matiList = kibouJyokenBean.getJyusyoYuubinBangouList();
		List<CityBigBean> listCityBig = new ArrayList<CityBigBean>();
		List<CityBean> listCity = null;
		CityBigBean bigBean = null;
		CityBean cityBean = null;
		String rowName = "";
		String staticRowName = "";

		for (int i = 0; i < SmkDataList.size(); i++) {
			// 町カナ取得
			String Cityk = String.valueOf(SmkDataList.get(i).get("TOWN_K"));

			rowName = checkRow(Cityk);

			if (!rowName.equals(staticRowName)) {

				if (!"".equals(staticRowName)) {
					bigBean.setCitys(listCity);
					listCityBig.add(bigBean);
				}

				staticRowName = rowName;
				bigBean = new CityBigBean();
				listCity = new ArrayList<CityBean>();
				bigBean.setRow(staticRowName);

			}
			cityBean = new CityBean();
			cityBean.setCityname(String.valueOf(SmkDataList.get(i).get("TOWN")));
			cityBean.setCityKana(String.valueOf(SmkDataList.get(i).get("TOWN_K")));
			cityBean.setYuubinBangou(String.valueOf(SmkDataList.get(i).get("YUBIN_NO")));
			cityBean.setBukkenCount(Integer.parseInt(SmkDataList.get(i).get("TOWN_COUNT").toString()));
			cityBean.setCheckbox(checkjsCd(cityBean.getYuubinBangou(), matiList));
			listCity.add(cityBean);
		}

		bigBean.setRow(staticRowName);
		bigBean.setCitys(listCity);
		listCityBig.add(bigBean);

		ukg02033Data = new HashMap<String, Object>();

		ukg02033Data.put("listKey", listCityBig);
		ukg02033Data.put("bukkenKb", kibouJyokenBean.getBukkenKb());
		return "smkMatiData";

	}
	// 2014/03/24  謝超 ADD End

	/**
	 * 設定ボタン押下処理
	 *
	 * @return
	 * @throws Exception
	 */
	public String saveCityList() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		String jusyo = (String) request.getParameter("jusyo");
		String cityName = (String) request.getParameter("cityName");
		// 2014/02/26  謝超 UPD Start (案件番号C37075)オリジナル情報誌リプレース
		//String kenName = (String) request.getParameter("kenName");
		//String kibouchiyiki = kenName + cityName.replace(",",","+kenName);
		String kibouchiyiki = cityName;
		// 2014/02/26  謝超 UPD End
		//########## 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		String presetNo = Util.toEmpty(request.getParameter("presetNo"));
		// 町デーだを取得(文字列)
		String matiList = Util.toEmpty(request.getParameter("matiList"));
		Map<String, Object> cityData = new HashMap<String, Object>();
		List<CityBean> matiDataList = new ArrayList<CityBean>();

		//　町デーだがルール以外の場合、リスト　に変える
		if(!Util.isEmpty(matiList)) {
			String matisArry[] = matiList.split(",");
			String matiArry[] = null;
			for (int i = 0; i < matisArry.length; i++) {
				matiArry = matisArry[i].split("&");
				CityBean cityBean = new CityBean();
				cityBean.setJusyoCd(matiArry[0]);
				cityBean.setYuubinBangou(matiArry[1]);
				cityBean.setCityname(matiArry[2]);
				matiDataList.add(cityBean);
			}
		}
		//########## 2014/01/21  謝超 ADD End

		List<CityBean> dataList = new ArrayList<CityBean>();
		String jusyoArry[] = jusyo.split(",");
		String cityNameArry[] = cityName.split(",");

		for (int i = 0; i < jusyoArry.length; i++) {
			CityBean cityBean = new CityBean();
			cityBean.setJusyoCd(jusyoArry[i]);
			cityBean.setCityname(cityNameArry[i]);
			dataList.add(cityBean);
			//########## 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
			cityData.put(jusyoArry[i], cityNameArry[i]);
			//########## 2014/01/21  謝超 ADD End
		}

		// 昇順ソート
		Collections.sort(dataList, CONST_COMPARATOR);
		// 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		// 昇順ソート
		Collections.sort(matiDataList, CONST_MATI_COMPARATOR);
		// 2014/01/21  謝超 ADD End

		HttpSession session = request.getSession();
		//########## 2014/01/21  謝超 UPD Start (案件番号C37075)オリジナル情報誌リプレース
		// KibouJyoukennBean kibouJyokenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);
		KibouJyoukennBean kibouJyokenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + presetNo);
		//########## 2014/01/21  謝超 UPD End
		if (kibouJyokenBean == null) {
			kibouJyokenBean = new KibouJyoukennBean();
		}
		// 2018/01/23 SYS_肖剣生 ADD START (案件C42036)デザイン変更
		if ("false".equals(request.getParameter("saveSession"))) {
			kibouJyokenBean = (KibouJyoukennBean) kibouJyokenBean.clone();
		}
		// 2018/01/23 SYS_肖剣生 ADD END (案件C42036)デザイン変更
		// 希望県コード
		kibouJyokenBean.setPrefectureCd(prefectureCd);
		// 希望条件指定コード
		kibouJyokenBean.setKibouJyoukennCode("2");
		// UKG02033Data
		kibouJyokenBean.setJyusyoList(dataList);
		//##########  2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		// 希望条件表示を作成、
		String tempMati = "";
		int tempMatiCount = 0;
		String oldJusyoCd = "";
		String newJusyoCd = "";
		String tempCityName = "";
		if(matiDataList.size() > 0) {
			oldJusyoCd = matiDataList.get(0).getJusyoCd();
		}
		for (int i = 0; i < matiDataList.size(); i++) {
			newJusyoCd = matiDataList.get(i).getJusyoCd();
			if(!newJusyoCd.equals(oldJusyoCd)) {
				tempCityName = (String)cityData.get(oldJusyoCd);
				if(!Util.isEmpty(tempMati)) {
					tempMati = tempMati.substring(1);
					if(i == matiDataList.size() - 1){
						tempMatiCount ++;
					}
					// 三つ町まで表示し、その後は「…」のように表示する。
					if(tempMatiCount > 3) {
						tempMati += "…";
					}
					// 市区郡内容  + '（' + 町リスト.町名(n) + '）'
					kibouchiyiki = kibouchiyiki.replace(tempCityName,tempCityName + "(" + tempMati + ")");
				}
				tempMati = "," + matiDataList.get(i).getCityname();
				if(i == matiDataList.size() - 1) {
					tempCityName = (String)cityData.get(newJusyoCd);
					tempMati = tempMati.substring(1);
					// 市区郡内容  + '（' + 町リスト.町名(n) + '）'
					kibouchiyiki = kibouchiyiki.replace(tempCityName,tempCityName + "(" + tempMati + ")");
				}
				tempMatiCount = 1;
				oldJusyoCd = newJusyoCd;
				continue;
			}
			if(newJusyoCd.equals(oldJusyoCd) && i == matiDataList.size() - 1) {
				tempCityName = (String)cityData.get(oldJusyoCd);
				if(!Util.isEmpty(tempMati)) {
					if(i == matiDataList.size() - 1){
						tempMatiCount ++;
					}
					// 三つ町まで表示し、その後は「…」のように表示する。
					// 2018/04/18 李強 UPD Start C42036-Bug161
					//if(tempMatiCount > 3) {
					if(tempMatiCount > 4) {
					// 2018/04/18 肖剣生 UPD End C42036-Bug161
						tempMati += "…";
					}else {
						tempMati += "," + matiDataList.get(i).getCityname();
					}
					tempMati = tempMati.substring(1);
					// 市区郡内容  + '（' + 町リスト.町名(n) + '）'
					kibouchiyiki = kibouchiyiki.replace(tempCityName,tempCityName + "(" + tempMati + ")");
				}
			}
			tempMatiCount ++ ;
			// 2018/04/18 李強 UPD Start C42036-Bug161
			//if(tempMatiCount > 3) {
			if(tempMatiCount > 4) {
			// 2018/04/18 肖剣生 UPD End C42036-Bug161
				continue;
			}
			tempMati += "," + matiDataList.get(i).getCityname();
			if( 1 == matiDataList.size()) {
				tempCityName = (String)cityData.get(oldJusyoCd);
				tempMati = tempMati.substring(1);
				if(!Util.isEmpty(tempMati)) {
					// 市区郡内容  + '（' + 町リスト.町名(n) + '）'
					kibouchiyiki = kibouchiyiki.replace(tempCityName,tempCityName + "(" + tempMati + ")");
				}
			}
		}
		kibouJyokenBean.setJusyohaniBean(null);
		kibouJyokenBean.setEkiList(null);
		kibouchiyiki = kibouchiyiki.replace(",", "、");
		//########## 2014/01/21  謝超 ADD End
		kibouJyokenBean.setKiboujyokenDisp(kibouchiyiki);
		//########## 2014/01/21  謝超 UPD Start (案件番号C37075)オリジナル情報誌リプレース
		// session.setAttribute(Consts.KIBOUJYOUKENBEAN, kibouJyokenBean);
		// 2018/01/23 SYS_肖剣生 UPD START (案件C42036)デザイン変更
		//session.setAttribute(Consts.KIBOUJYOUKENBEAN + presetNo, kibouJyokenBean);
		if ("true".equals(request.getParameter("saveSession"))) {
			session.setAttribute(Consts.KIBOUJYOUKENBEAN + presetNo, kibouJyokenBean);
		}
		// 2018/01/23 SYS_肖剣生 UPD END (案件C42036)デザイン変更
		//########## 2014/01/21  謝超 UPD End
		session.setAttribute("SHIKUGUN_CD", "");
		//########## 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		kibouJyokenBean.setJyusyoYuubinBangouList(matiDataList);
		//########## 2014/01/21  謝超 ADD End
		// 2018/01/23 SYS_肖剣生 ADD START (案件C42036)デザイン変更
		ukg02033Data = new HashMap<String, Object>();
		ukg02033Data.put("kibouchiyiki", kibouchiyiki.replace(",", "&nbsp;&nbsp;<span class=\"separtor\"></span>&nbsp;&nbsp;")
				.replace("、", "&nbsp;&nbsp;<span class=\"separtor\"></span>&nbsp;&nbsp;"));
		// 2018/01/23 SYS_肖剣生 ADD END (案件C42036)デザイン変更

		return "saveCityList";
	}

	// ########## 2014/01/21  謝超 UPD Start (案件番号C37075)オリジナル情報誌リプレース
	/**
	 * JIS住所コードが希望条件データ.UKG02033Dataの中に存在する場合、true。それ以外、false
	 * 
	 * @param jscd
	 * @param checkedDataList
	 * @return
	 */
//	private String checkjsCd(String jscd, // parasoft-suppress UC.ACC "C37075JTest対応"
//	                         List<CityBean> checkedDataList) { // parasoft-suppress UC.ACC "C37075JTest対応"
// // parasoft-suppress UC.ACC "C37075JTest対応"
//		if (checkedDataList == null || checkedDataList.size() == 0) {// parasoft-suppress UC.ACC "C37075JTest対応"
//			return "";// parasoft-suppress UC.ACC "C37075JTest対応"
//		}// parasoft-suppress UC.ACC "C37075JTest対応"
// // parasoft-suppress UC.ACC "C37075JTest対応"
//		for (int i = 0; i < checkedDataList.size(); i++) {// parasoft-suppress UC.ACC "C37075JTest対応"
//			if (jscd.equals(checkedDataList.get(i).getJusyoCd().trim())) {// parasoft-suppress UC.ACC "C37075JTest対応"
//				return "checked";// parasoft-suppress UC.ACC "C37075JTest対応"
//			}// parasoft-suppress UC.ACC "C37075JTest対応"
//		}// parasoft-suppress UC.ACC "C37075JTest対応"
//		return "";// parasoft-suppress UC.ACC "C37075JTest対応"
//	}// parasoft-suppress UC.ACC "C37075JTest対応"
	/**
	 * JIS住所コードが希望条件データ.UKG02033Dataの中に存在する場合、true。それ以外、false
	 *
	 * @param yuubinBangou
	 * @param checkedDataList
	 * @return
	 */
	private String checkjsCd(String yuubinBangou,
			List<CityBean> checkedDataList) {

		if (checkedDataList == null || checkedDataList.size() == 0) {
			return "";
		}

		for (int i = 0; i < checkedDataList.size(); i++) {
			if (!Util.isEmpty(checkedDataList.get(i).getYuubinBangou())) {
			if (yuubinBangou.equals(checkedDataList.get(i).getYuubinBangou().trim())) {
				return "checked";
			}
			}
		}
		return "";
	}
	//########## 2014/01/21  謝超 UPD End

	//########## 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
	/**
	 * JIS住所コードが希望条件データ.UKG02033Dataの中に存在する場合、1。
	 * JIS住所コードが希望条件データ.UKG02033Dataの中に存在しない、希望条件データ.UKG02033Data.希望町リストデータの中に存在する場合2。
	 * それ以外、0
	 *
	 * @param jscd
	 * @param checkedDataList
	 * @param matiList
	 * @return
	 */
	private String checkjsMatiCd(String jscd,
	                         List<CityBean> checkedDataList, List<CityBean> matiList) {
		String flag = "0";
		if(checkedDataList != null) {
			for (int i = 0; i < checkedDataList.size(); i++) {
				if (jscd.equals(checkedDataList.get(i).getJusyoCd().trim())) {
					flag = "1";
				}
			}
		}

		if(matiList != null) {
			for (int i = 0; i < matiList.size(); i++) {
				if (jscd.equals(matiList.get(i).getJusyoCd().trim())) {
					flag = "2";
				}
			}
		}

		return flag;
	}

	/**
	 * <pre>
	 * [機 能] ソートクラス
	 * [説 明] 昇順ソート項目を設定する。
	 * @author [作 成] 2014/01/21 SYS_謝超
	 * </pre>
	 */
	private static class MatiDataComparator implements Comparator<CityBean> {

		public int compare(CityBean o1,
		                   CityBean o2) {
			if(0 == o1.getJusyoCd().compareTo(o2.getJusyoCd())) {
				return -o1.getYuubinBangou().compareTo(o2.getYuubinBangou());
			}
			return -o1.getJusyoCd().compareTo(o2.getJusyoCd());
		}
	}
	//########## 2014/01/21  謝超 ADD End

	/**
	 * kanaチェック
	 *
	 * @param str
	 * @return
	 */
	private String checkRow(String str) {

		String[] check = new String[] { "アイウエオ", "カキクケコガギグゲゴ", "サシスセソザジズゼゾ", "タチツテトダヂヅデド", "ナニヌネノ", "ハヒフヘホバビブベボ",
		    "マミムメモ", "ヤイユエヨ", "ラリルレロ", "ワエヲン" };
		for (int i = 0; i < check.length; i++) {
			if (check[i].contains(str)) {
				String Strname = check[i].substring(0, 1);
				if ("ア".equals(Strname)) {
					Strname = "あ";
				} else if ("カ".equals(Strname)) {
					Strname = "か";
				} else if ("サ".equals(Strname)) {
					Strname = "さ";
				} else if ("タ".equals(Strname)) {
					Strname = "た";
				} else if ("ナ".equals(Strname)) {
					Strname = "な";
				} else if ("ハ".equals(Strname)) {
					Strname = "は";
				} else if ("マ".equals(Strname)) {
					Strname = "ま";
				} else if ("ヤ".equals(Strname)) {
					Strname = "や";
				} else if ("ラ".equals(Strname)) {
					Strname = "ら";
				} else if ("ワ".equals(Strname)) {
					Strname = "わ";
				}
				return Strname;
			}
		}
		return "";
	}

	/**
	 * <pre>
	 * [機 能] ソートクラス
	 * [説 明] 昇順ソート項目を設定する。
	 * @author [作 成] 2012/03/20 SYS_馮強華
	 * </pre>
	 */
	private static class DataComparator implements Comparator<CityBean> {

		public int compare(CityBean o1,
		                   CityBean o2) {

			return -o1.getJusyoCd().compareTo(o2.getJusyoCd());
		}
	}
}
