/**
 * @システム名: 受付システム
 * @ファイル名:
 * @更新日付：
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.CGIUtil;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.formbean.EkiInfoBean;
import jp.co.token.uketuke.formbean.KibouJyoukennBean;
import jp.co.token.uketuke.formbean.RosenCopBean;
import jp.co.token.uketuke.formbean.RosenInfoBean;
import jp.co.token.uketuke.service.IUKG02035Service;

import org.apache.struts2.ServletActionContext;


/**
 * <pre>
 * [機 能] 通勤・通学の設定
 * [説 明] 通勤・通学の設定
 * @author [作 成] 2012/05/08 SYS_趙
 * </pre>
 */
public class UKG02035Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -8765596806569290256L;

	/** 通勤・通学時間の設定画面用データ */
	private Map<String, Object> ukg02035Data = null;

	private IUKG02035Service service = null;

	private static final String VAL_RANGE_START = "0";

	/**
	 * 通勤・通学時間の設定画面用データを取得
	 *
	 * @return the ukg02035Data
	 */
	public Map<String, Object> getUkg02035Data() {

		return ukg02035Data;
	}

	/**
	 * 通勤・通学時間の設定画面用データを設定
	 *
	 * @param ukg02035Data
	 *            通勤・通学時間の設定画面用データ
	 */
	public void setUkg02035Data(Map<String, Object> ukg02035Data) {

		this.ukg02035Data = ukg02035Data;
	}

	/**
	 * 通勤・通学時間の設定画面サービスを設定
	 *
	 * @param ukg02035Services
	 *            the ukg02035Services to set
	 */
	public void setUKG02035Services(IUKG02035Service ukg02035Services) {

		service = ukg02035Services;
	}

	/**
	 * <pre>
	 * [説 明] 通勤・通学時間の設定画面で表示
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		// 時間リスト
		ukg02035Data = new HashMap<String, Object>();
		ukg02035Data.put("timeList", service.getTimeList());
		// 乗り換え許容回数リスト
		ukg02035Data.put("norikaeList", service.getNorikaeKaisuu());

		HttpServletRequest request = ServletActionContext.getRequest();
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);

		// 希望条件コードの処理
		ukg02035Data.put("kibouJyoukenCd", kibouJyoukennBean.getKibouJyoukennCode());

		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] 駅名自動完了リストの設定
	 * </pre>
	 *
	 * @throws UnsupportedEncodingException
	 */
	public String searchEkiMei() throws UnsupportedEncodingException {

		HttpServletRequest request = ServletActionContext.getRequest();
		String inputData = new String(request.getParameter("q").getBytes("ISO8859-1"), "UTF-8");

		if (log.isDebugEnabled()) {
			log.debug("キーワード:" + inputData);
		}

		List<String> ekiList = rosenNameListGet(inputData);

		ukg02035Data = new HashMap<String, Object>();
		ukg02035Data.put("ekiList", ekiList);

		return "ajaxEkiMei";
	}

	/**
	 * 駅名の取得
	 *
	 * @param toEkiName
	 *            入力されて駅名
	 * @return
	 */
	private List<String> rosenNameListGet(String toEkiName) throws UnsupportedEncodingException {

		// 駅名リストの取得
		List<String> list = new ArrayList<String>();
		// 探索する最寄り駅の名称
		String val_from = "";
		val_from = "val_from=" + URLEncoder.encode(toEkiName, "Windows-31J") + "&";
		String val_htmb = "val_htmb=" + "estate_range";
		String urlStr_part = val_from + val_htmb;

		String line = CGIUtil.rosenProcess(urlStr_part);

		if (log.isDebugEnabled() && line != null) {
			log.debug(line);
		}

		// タイプは1の場合、駅名を選択、2の場合、駅コードを選択
		if(null != line){
			int returnType = Integer.valueOf(line.substring(0, 1));

			String result[] = line.split(",");

			int resultCount = 0;
			if (result.length >= 2) {
				resultCount = Integer.valueOf(result[1]).intValue();
			}

			// 駅名の場合、駅名リストを戻し
			List<String> listTemporary = new ArrayList<String>();
			if(returnType == 2){
				listTemporary.add(toEkiName);
			}
			if ((returnType == 1) && (resultCount > 0)) {

				for (int i = 2; i < result.length; i++) {
					listTemporary.add(result[i]);
				}
			}
			int autoCompleteMax = 8;
			try {
				autoCompleteMax = Integer.parseInt(PropertiesReader.getIntance().getProperty("autoCompleteMax"));
			} catch (Exception e) {
				autoCompleteMax = 8;
			}
			if (listTemporary.size() <= autoCompleteMax) {
				list = listTemporary;
			}else {
				for (int i = 0; i <= autoCompleteMax; i++) {
					if (i == autoCompleteMax) {
						list.add("・・・・・・");
					}else {
						list.add(listTemporary.get(i));
					}
				}
			}
		}

		return list;
	}

	/**
	 * <pre>
	 * [説 明] 駅リストの設定
	 * </pre>
	 *
	 * @throws Exception
	 */
	public String ekiInfoListGet() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		// 駅名
		String ekiName = request.getParameter("ekiName");
		// 選択時間
		String timeSelect = request.getParameter("timeSelect");
		// 回数
		String kaisu = request.getParameter("kaisu");

		if (log.isDebugEnabled()) {
			log.debug("駅名⇒" + ekiName + "｜選択時間⇒" + timeSelect + "｜乗り換える回数⇒" + kaisu);
		}

		rosenList(ekiName, timeSelect, kaisu);

		return "ajaxProcess";
	}

	/**
	 * 路線リストの選択
	 *
	 * @param ekiName
	 *            駅名
	 * @param timeSel
	 *            時間
	 * @param kaisu
	 *            回数
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void rosenList(String ekiName,
	                       String timeSel,
	                       String kaisu) throws Exception {

		// 固定値（所要時間探索機能を指定）
		String val_htmb = "val_htmb=estate_range&";

		// 探索する最寄り駅の名称
		String val_from = "";
		val_from = "val_from=" + URLEncoder.encode(ekiName, "Windows-31J") + "&";

		// 所要時間探索の開始値（分）
		String val_range_start = "val_range_start=" + VAL_RANGE_START + "&";

		// 所要時間探索の終了値（分）
		String val_range_end = "val_range_end=" + timeSel + "&";
		String val_transfer_count = "";
		if ("0".equals(kaisu)) {
			val_transfer_count = "val_transfer_count=" + kaisu + "&";
		}else if("1".equals(kaisu) || "2".equals(kaisu) || "3".equals(kaisu)){
			val_transfer_count = "val_transfer_count=" + (Integer.valueOf(kaisu)-1) + "&";
		}

		// 所要時間探索結果の出力順。降順の時は０、昇順の時は１を指定
		String ValSortType = "1";
		String val_sort_type = "val_sort_type=" + ValSortType + "&";

		// 1 - 駅名が決定
		String val_type = "val_type=1&";

		String val_limit_count = "val_limit_count="
		    + PropertiesReader.getIntance().getProperty("UKG02035.maxKensakuSuu");

		// CGIの処理
		String urlStr_part = val_htmb
		    + val_from + val_range_start + val_range_end + val_transfer_count + val_sort_type + val_type
		    + val_limit_count;

		String line = CGIUtil.rosenProcess(urlStr_part);

		// debug
		if (log.isDebugEnabled() && line != null) {
			log.debug(line);
		}

		// タイプは1の場合、駅名を取得、2の場合、所要時間探索の駅コード
		int startType = Integer.valueOf(line.substring(0, 1)).intValue();
		String result[] = line.split(",");

		// 駅名
		int resultCount = 0;
		if (result.length > 2) {
			resultCount = Integer.valueOf(result[1]).intValue();
		}

		StringBuffer strEkiCd = new StringBuffer("");
		List<String> listEkiCd = new ArrayList<String>();
		List<Integer> listTime = new ArrayList<Integer>();

		if ((startType == 2 && resultCount >= 0)) {
			strEkiCd.append("'"+result[2]+"'");  // 引用符つける(C39042)

			listEkiCd.add(result[2]);
			listTime.add(0);
			if(!"0".equals(kaisu)){
				for (int i = 3; i < result.length; i++) {
					strEkiCd.append(",");
					strEkiCd.append("'"+result[i].substring(0, 5)+"'"); // 引用符つける(C39042)

					listEkiCd.add(result[i].substring(0, 5));
					listTime.add(Integer.parseInt(result[i].substring(5, 8)));
				}
			}
		}
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("PVEKICDS", strEkiCd.toString());

		Map<String, Object> mapEkiInfo = service.rosenInfoList(paramMap);

		// debug
		if (log.isDebugEnabled() && line != null) {
			log.debug(mapEkiInfo);
		}

		ukg02035Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();

		// 路線情報の設定
		List<RosenCopBean> rosenCopList = rosenList((List<Map<String, Object>>) mapEkiInfo.get("RST_ROSEN"), listEkiCd,
		                                            listTime);
		ukg02035Data.put("rosenList", rosenCopList);

		request.getSession().setAttribute(Consts.ROSEN_COP_LIST, rosenCopList); // parasoft-suppress PB.API.ONS "36029JTest対応"
	}

	/**
	 * 路線リストの組み合わせ
	 *
	 * @param rosenList
	 * @return
	 */
	private List<RosenCopBean> rosenList(List<Map<String, Object>> rosenList,
	                                     List<String> listEkiCd,
	                                     List<Integer> listTime) {

		RosenCopBean rosenCopBean = null;
		RosenInfoBean rosenInfoBean = null;
		EkiInfoBean ekiInfoBean = null;
		List<RosenInfoBean> rosenData = null;
		List<EkiInfoBean> ekiData = null;
		List<RosenCopBean> rosenCopList = new ArrayList<RosenCopBean>();

		String rsCopName = "";
		String rosenCd = "";
		String ekiCd = "";
		for (Map<String, Object> map : rosenList) {
			if (!rsCopName.equals(map.get("ROSENCOPNAME"))) {
				rsCopName = (String) map.get("ROSENCOPNAME");
				rosenCopBean = new RosenCopBean();
				rosenCopBean.setRosenCopName(rsCopName);
				rosenCopBean.setRosenCopStatus("0");
				rosenData = new ArrayList<RosenInfoBean>();
				rosenCopBean.setRosenData(rosenData);
				rosenCopList.add(rosenCopBean);

				rosenCd = (String) map.get("ROSENCD");
				rosenInfoBean = new RosenInfoBean();
				ekiData = new ArrayList<EkiInfoBean>();
				rosenInfoBean.setRosenCd(rosenCd);
				rosenInfoBean.setRosenName((String) map.get("ROSENNAME"));
				rosenInfoBean.setEkiInfoList(ekiData);
				rosenData.add(rosenInfoBean);

				ekiCd = (String) map.get("EKICD");
				ekiInfoBean = new EkiInfoBean();
				ekiInfoBean.setEkiCd(ekiCd);
				ekiInfoBean.setRosenCd(rosenCd);
				getEkiMadeTime(ekiInfoBean, listEkiCd, listTime);
				ekiData.add(ekiInfoBean);
				continue;
			}
			if (!rosenCd.equals(map.get("ROSENCD"))) {
				rosenCd = (String) map.get("ROSENCD");
				rosenInfoBean = new RosenInfoBean();
				ekiData = new ArrayList<EkiInfoBean>();
				rosenInfoBean.setRosenCd(rosenCd);
				rosenInfoBean.setRosenStatus("0");
				rosenInfoBean.setRosenName((String) map.get("ROSENNAME"));
				rosenInfoBean.setEkiInfoList(ekiData);
				if (null != rosenData) {
					rosenData.add(rosenInfoBean);
				}

				ekiCd = (String) map.get("EKICD");
				ekiInfoBean = new EkiInfoBean();
				ekiInfoBean.setEkiCd(ekiCd);
				ekiInfoBean.setRosenCd(rosenCd);
				getEkiMadeTime(ekiInfoBean, listEkiCd, listTime);
				ekiData.add(ekiInfoBean);
				continue;
			}
			if (!ekiCd.equals(map.get("EKICD"))) {
				ekiCd = (String) map.get("EKICD");
				ekiInfoBean = new EkiInfoBean();
				ekiInfoBean.setEkiCd(ekiCd);
				ekiInfoBean.setRosenCd(rosenCd);
				getEkiMadeTime(ekiInfoBean, listEkiCd, listTime);
				if (null != ekiData) {
					ekiData.add(ekiInfoBean);
				}
				continue;
			}

		}
		return rosenCopList;
	}

	/**
	 * 駅まで時間の取得
	 *
	 * @param ekiInfoBean
	 * @param listEkiCd
	 * @param listTime
	 */
	private void getEkiMadeTime(EkiInfoBean ekiInfoBean,
	                            List<String> listEkiCd,
	                            List<Integer> listTime) {

		for (int i = 0; i < listEkiCd.size(); i++) {
			if (ekiInfoBean.getEkiCd().equals(listEkiCd.get(i))) {
				ekiInfoBean.setEkiMadeTime(listTime.get(i).toString());
			}
		}

	}

	/**
	 * <pre>
	 * [説 明] 駅リストの設定
	 * </pre>
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
    public String btnSettei() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		// 駅名
		String ekiName = request.getParameter("ekiName");
		// 選択時間
		String timeSelect = request.getParameter("timeSelect");
		// 回数
		String kaisu = request.getParameter("kaisu");
		String kaisutext = request.getParameter("kaisutext");

		// 選択されたデータ
		String chkListStr = request.getParameter("chkListStr");
		String chkBusFukumi = request.getParameter("chkBusFukumi");

		List<RosenCopBean> rosenCopList = (List<RosenCopBean>) request.getSession().getAttribute(Consts.ROSEN_COP_LIST);
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);
		kibouJyoukennBean.setKibouJyoukennCode("4");
		kibouJyoukennBean.setTuukinEkiName(ekiName);
		kibouJyoukennBean.setTuukinTimeSel(timeSelect);
		kibouJyoukennBean.setTuukinKaisu(kaisu);
		kibouJyoukennBean.setTuukinKaisuName(kaisutext);
		kibouJyoukennBean.setBusCheck(chkBusFukumi);
		kibouJyoukennBean.setKiboujyokenDisp("");
		kibouJyoukennBean.setRosenCopBeanList(rosenCopList);
		List<EkiInfoBean> ekiInfoList = new ArrayList<EkiInfoBean>();
		//クリア上回路線情報
		for(int i = 0; i < rosenCopList.size(); i++){

			List<RosenInfoBean> rosenInfoBeans = rosenCopList.get(i).getRosenData();
			for (int j = 0; j < rosenInfoBeans.size(); j++) {
				RosenInfoBean rosenInfoBean = rosenInfoBeans.get(j);
				if (null != rosenInfoBean) {
					rosenInfoBean.setRosenStatus("0");
				}
			}
		}
		int rosenCopIdx = 0;
		int rosenIdx = 0;
		String[] chkIdx = chkListStr.split("/");
		String[] chkRosenId;
		for (int i = 0; i < chkIdx.length; i++) {
			chkRosenId = chkIdx[i].split(",");
			rosenCopIdx = Integer.parseInt(chkRosenId[0]);
			rosenIdx = Integer.parseInt(chkRosenId[1]);
			rosenCopList.get(rosenCopIdx).getRosenData().get(rosenIdx).setRosenStatus("1");
			ekiInfoList.addAll(rosenCopList.get(rosenCopIdx).getRosenData().get(rosenIdx).getEkiInfoList());
		}
		kibouJyoukennBean.setEkiInfoList(ekiInfoList);

		String ekiInfoStr = "";
		for (EkiInfoBean ekiInfoBean : ekiInfoList) {
			if (!ekiInfoStr.contains(ekiInfoBean.getEkiCd())) {
				ekiInfoStr += ekiInfoBean.getRosenCd() + ",";
				ekiInfoStr += ekiInfoBean.getEkiCd() + ",";
				ekiInfoStr += ekiInfoBean.getEkiMadeTime() + "/";
			}
		}
		if (ekiInfoStr.length() > 0) {
			ekiInfoStr = ekiInfoStr.substring(0, ekiInfoStr.length() - 1);
		}

		// セッション情報取得
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("KAISYACD", getLoginInfo().get(Consts.KAISYA_CD));
		paramMap.put("UKETUKENO", session.getAttribute(Consts.UKETUKENO));
		paramMap.put("EKIINFO", ekiInfoStr);

		service.saveStationData(paramMap);
		session.setAttribute(Consts.KIBOUJYOUKENBEAN, kibouJyoukennBean);

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 駅リストの設定
	 * </pre>
	 *
	 * @throws Exception
	 */
	public String sessionInfoGet() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);

		ukg02035Data = new HashMap<String, Object>();
		ukg02035Data.put("ekiName", kibouJyoukennBean.getTuukinEkiName());
		ukg02035Data.put("timeSelect", kibouJyoukennBean.getTuukinTimeSel());
		ukg02035Data.put("kaisu", kibouJyoukennBean.getTuukinKaisu());
		ukg02035Data.put("chkBusFukumi", kibouJyoukennBean.getBusCheck());
		ukg02035Data.put("rosenList", kibouJyoukennBean.getRosenCopBeanList());

		return "ajaxProcess";
	}

}
