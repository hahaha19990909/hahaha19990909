/**
 * @システム名: 受付システム
 * @ファイル名: IUKG02034Dao.java
 * @更新日付：: 2012/05/08
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴: 2014/01/07  SYS_ 郭凡    (案件C37075)オリジナル情報誌リプレース
 * 更新履歴：  2014/06/10	 SYS_ 郭凡   (案件番号C38117)セールスポイント編集機能の追加
 * 更新履歴：  2018/02/24	 肖剣生(SYS)   (案件C42036)デザイン変更新規作成
 */
package jp.co.token.uketuke.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.EkiBean;
import jp.co.token.uketuke.formbean.JusyohaniBean;
import jp.co.token.uketuke.formbean.KibouJyoukennBean;
import jp.co.token.uketuke.formbean.KibouRosennBean;
import jp.co.token.uketuke.service.IUKG02034Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 希望路線の設定
 * [説 明] 希望路線を設定する。
 * @author [作 成] 2012/05/08 SYS_趙
 * 変更履歴: 2014/01/16 SYS_胡 Jtest対応
 * </pre>
 */
public class UKG02034Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -6119667419986223779L;

	/** 希望路線の設定画面用データ */
	private KibouRosennBean ukg02034Data;

	/** 希望路線の設定サービス */
	private IUKG02034Service UKG02034Services;

	/**
	 * <pre>
	 * [説 明] 希望路線の設定
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {

		ukg02034Data = new KibouRosennBean();
		HttpServletRequest request = ServletActionContext.getRequest();
		// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース
//		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);
		KibouJyoukennBean kibouJyoukennBean = null;
		String presetNo = Util.toEmpty(request.getParameter("presetNo"));
		kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN+presetNo);
		// 2014/01/07  郭凡 UPD End

		// ログイン情報
		Map<String, Object> sessionLoginInfo = getLoginInfo();

		// 都道府県リストを取得する
		ukg02034Data.setJusyoData(UKG02034Services.getJusyoData());

		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		// 希望条件データ.希望県コードが設定されている場合、希望条件データ.希望県コードを設定する。
		String jusyo_cd = "";
		if("".equals(presetNo) || presetNo == null){
			jusyo_cd = (String) uketukePostInfo.get(Consts.POST_JUSYO_CD);
		}else{
			jusyo_cd = (String) sessionLoginInfo.get(Consts.POST_JUSYO_CD);
		}
		// ########## 2014/06/10 郭凡 UPD End
		if (!Util.isEmpty(kibouJyoukennBean.getPrefectureCd())) {
			jusyo_cd = kibouJyoukennBean.getPrefectureCd();
		}
		if ("3".equals(kibouJyoukennBean.getKibouJyoukennCode())) {
			// 希望条件データ.希望条件指定コードが'3'(希望駅))の場合

			// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース
			// 路線情報を取得する
//			Map<String, Object> initInfo = UKG02034Services.getEkiInfo(null,
//                    (String) sessionLoginInfo.get(Consts.KAISYA_CD),
//                    kibouJyoukennBean);
			Map<String, Object> initInfo = null;
			if(Consts.UKG05022_GAMEN_KB.equals(kibouJyoukennBean.getGamenKubun())){
				initInfo = UKG02034Services.getShicyakuEkiInfo(null,
						(String) sessionLoginInfo.get(Consts.KAISYA_CD),
						kibouJyoukennBean);
			}else {
				initInfo = UKG02034Services.getEkiInfo(null,
						(String) sessionLoginInfo.get(Consts.KAISYA_CD),
						kibouJyoukennBean);
			}
			// 2014/01/07  郭凡 UPD End
			List<Map<String, Object>> ekiMaps = (List<Map<String, Object>>) initInfo.get("rst_ekiList");
			List<Map<String, Object>> ekiListMap = new ArrayList<Map<String,Object>>();
			String lastEkiCd = null;
			// 2014/01/16 Start by SYS_胡  Jtest対応
			//List<Map<String, Object>> rossenList = null;
			List<Map<String, Object>> rossenList = new ArrayList<Map<String, Object>>();
			// 2014/01/16 End by SYS_胡  Jtest対応
			for (Map<String, Object> ekiMap : ekiMaps) {
				String ekiCd = (String)ekiMap.get("EKI_CD");
				if (ekiCd.equals(lastEkiCd)) {
					Map<String, Object> rossenInfo = new HashMap<String, Object>();
					rossenInfo.put("ROSEN_CD", ekiMap.get("ROSEN_CD"));
					rossenInfo.put("ROSEN_NAME", ekiMap.get("ROSEN_NAME"));
					rossenList.add(rossenInfo);
					continue;
				}
				rossenList = new ArrayList<Map<String, Object>>();
				Map<String, Object> ekiInfo = new HashMap<String, Object>();
				Map<String, Object> rossenInfo = new HashMap<String, Object>();
				rossenInfo.put("ROSEN_CD", ekiMap.get("ROSEN_CD"));
				rossenInfo.put("ROSEN_NAME", ekiMap.get("ROSEN_NAME"));
				rossenList.add(rossenInfo);
				ekiInfo.put("ROSEN_LIST",rossenList);
				ekiInfo.put("EKI_NAME", ekiMap.get("EKI_NAME"));
				ekiInfo.put("CNT", ekiMap.get("CNT"));
				ekiInfo.put("EKI_CD", ekiCd);
				ekiInfo.put("KOUMOKU_SB_NAME", ekiMap.get("KOUMOKU_SB_NAME"));
				ekiListMap.add(ekiInfo);
				lastEkiCd = ekiCd;
			}
			String[] ekis = kibouJyoukennBean.getKiboujyokenDisp().split("、");
			List<Map<String, Object>> ekiListSort = new ArrayList<Map<String, Object>>();
			if (null != ekis && ekis.length > 0) {
				for (String ekiName : ekis) {
					for (Map<String, Object> eki : ekiListMap) {
						String ekiKennName = (String) eki.get("EKI_NAME");
						if (ekiName.equals(ekiKennName)) {
							ekiListSort.add(eki);
						}
					}
				}
			}
			if(ekiListSort.isEmpty()){
				ukg02034Data.setEkiList(ekiListMap);
			}else{

				ukg02034Data.setEkiList(ekiListSort);
			}

			// 県コードを検索する
			if(ekiListMap.size() > 0){

				String firstEkiCd = (String) ukg02034Data.getEkiList().get(0).get("EKI_CD");

				jusyo_cd = UKG02034Services.getRefectureCd(firstEkiCd);

				ukg02034Data.setKibouJyoukennCode(kibouJyoukennBean.getKibouJyoukennCode());
			}
		}

		// 店舗所在都道府県コード
		ukg02034Data.setPrefectureCd(jusyo_cd);

		// 希望物件区分
		ukg02034Data.setBukkenKb(kibouJyoukennBean.getBukkenKb());

		// 都道府県路線マップマスタ検索
		ukg02034Data.setRosenData(UKG02034Services.getRosenData(jusyo_cd));

		// 駅選択最大数
		ukg02034Data.setMaxSetteisuu(PropertiesReader.getIntance().getProperty(Consts.UKG02034_MAXSETTEISUU));

		// 路線サーブレットURL
		ukg02034Data.setRosenServleturl(PropertiesReader.getIntance().getProperty(Consts.ROSEN_SERVLETURL));

		// 路線キーコード
		ukg02034Data.setRosenKeycode(PropertiesReader.getIntance().getProperty(Consts.ROSEN_KEYCODE));
		// 2014/01/07  郭凡 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		ukg02034Data.setPresetNo(presetNo);
		// 2014/01/07  郭凡 ADD End
		
		//2018/02/24	 肖剣生(SYS)  ADD Start (案件C42036)デザイン変更新規作成
		if(Consts.UKG05021_GAMEN_KB.equals(kibouJyoukennBean.getGamenKubun()) || Consts.UKG05022_GAMEN_KB.equals(kibouJyoukennBean.getGamenKubun())){
			return "success_ff_popup";
		}else{
			return successForward();			
		}
		//2018/02/24	 肖剣生(SYS)  End
	}

	/**
	 * <pre>
	 * [説 明] 都道府県を変更
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String changJusyo() throws Exception {

		ukg02034Data = new KibouRosennBean();
		HttpServletRequest request = ServletActionContext.getRequest();

		String jusyo_cd = (String) request.getParameter("jusyo_cd");

		// 都道府県リストを取得する
		ukg02034Data.setRosenData(UKG02034Services.getRosenData(jusyo_cd));

		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] 希望物件数を検索する 。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String ekiBukkenCount() throws Exception {

		ukg02034Data = new KibouRosennBean();
		HttpServletRequest request = ServletActionContext.getRequest();
		// ログイン情報
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		String ekiCd = (String) request.getParameter("ekiCd");
		String bukkenKb = (String) request.getParameter("bukkenKb");
		if(!"3".equals(bukkenKb)){
			// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース
//			KibouJyoukennBean kibouJyoukennBeanc = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);
			KibouJyoukennBean kibouJyoukennBeanc = null;
			String presetNo = Util.toEmpty(request.getParameter("hidPresetNo"));
			kibouJyoukennBeanc = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN+presetNo);
			// 2014/01/07  郭凡 UPD End
			KibouJyoukennBean kibouJyokenBean = (KibouJyoukennBean) kibouJyoukennBeanc.clone();
			JusyohaniBean jusyohaniBean = kibouJyokenBean.getJusyohaniBean();
			if (jusyohaniBean == null) {
				jusyohaniBean = new JusyohaniBean();
			}
			// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース
			// 路線情報を取得する
//			Map<String, Object> initInfo = UKG02034Services.getEkiInfo(ekiCd,
//                    (String) sessionLoginInfo.get(Consts.KAISYA_CD),
//                    kibouJyokenBean);
			Map<String, Object> initInfo = null;
			if("UKG05022".equals(kibouJyoukennBeanc.getGamenKubun())){
				initInfo = UKG02034Services.getShicyakuEkiInfo(ekiCd,
                        (String) sessionLoginInfo.get(Consts.KAISYA_CD),
                        kibouJyokenBean);
			}else {
				initInfo = UKG02034Services.getEkiInfo(ekiCd,
                        (String) sessionLoginInfo.get(Consts.KAISYA_CD),
                        kibouJyokenBean);
			}
			// 2014/01/07  郭凡 UPD End
			List<Map<String, Object>> ekiList = (List<Map<String, Object>>) initInfo.get("rst_ekiList");
			if (ekiList != null && ekiList.size() > 0) {
				ukg02034Data.setRoseBukkenCount(new BigDecimal((String) ekiList.get(0).get("CNT")));
			} else {
				ukg02034Data.setRoseBukkenCount(Consts.CON_ZERO);
			}
		}

		// 路線リストを取得する 。
		ukg02034Data.setEkiList(UKG02034Services.getRossenList(ekiCd));

		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] 希望条件に設定する 。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String ekiSetting() throws Exception {

		ukg02034Data = new KibouRosennBean();
		HttpServletRequest request = ServletActionContext.getRequest();
		String prefectureCd = request.getParameter("prefectureCd");
		// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース
//		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN);
		KibouJyoukennBean kibouJyoukennBean = null;
		String presetNo = Util.toEmpty(request.getParameter("hidPresetNo"));
		kibouJyoukennBean = (KibouJyoukennBean) request.getSession().getAttribute(Consts.KIBOUJYOUKENBEAN+presetNo);
		// 2014/01/07  郭凡 UPD End
		if (kibouJyoukennBean == null) {
			kibouJyoukennBean = new KibouJyoukennBean();
		}
		kibouJyoukennBean.setPrefectureCd(prefectureCd);
		// 駅データ作成
		String[] eki = ((String) request.getParameter("ekilist")).split("\\|");
		List<EkiBean> ekilist = new ArrayList<EkiBean>();
		String kiboujyokennDisp = "";
		for (String ek : eki) {
			String[] ki = ek.split("@")[0].split("_");
			EkiBean ekiBean = new EkiBean();
			ekiBean.setEkiCd(ki[0]);
			ekiBean.setEkiName(ki[1]);
			List<Map<String,String>> rossenList = new ArrayList<Map<String,String>>();
			for(String rossen : ek.split("@")[1].split("&")){
				Map<String,String> rosseninfo = new HashMap<String, String>();
				rosseninfo.put("ROSEN_CD", rossen.split("_")[0]);
				rosseninfo.put("ROSEN_NAME", rossen.split("_")[1]);
				rossenList.add(rosseninfo);
			}
			ekiBean.setRossenList(rossenList);
			ekilist.add(ekiBean);
			kiboujyokennDisp += ki[1] + ",";
		}
		if (ekilist.size() > 0) {
			// 希望条件データ
			//Collections.sort(ekilist);
			kibouJyoukennBean.setPrefectureCd(prefectureCd);
			kibouJyoukennBean.setKibouJyoukennCode("3");
			kibouJyoukennBean.setEkiList(ekilist);
			kibouJyoukennBean.setKiboujyokenDisp(kiboujyokennDisp.substring(0, kiboujyokennDisp.length() - 1));
			kibouJyoukennBean.setJyusyoList(null);
			// 2014/01/07  郭凡 UPD Start (案件番号C37075)オリジナル情報誌リプレース
			// セッション情報登録
//			request.getSession().setAttribute(Consts.KIBOUJYOUKENBEAN, kibouJyoukennBean);
			kibouJyoukennBean.setJyusyoList(null);
			kibouJyoukennBean.setJyusyoYuubinBangouList(null);
			kibouJyoukennBean.setJusyohaniBean(null);
			request.getSession().setAttribute(Consts.KIBOUJYOUKENBEAN+presetNo, kibouJyoukennBean);
			// 2014/01/07  郭凡 UPD End
		}
		request.getSession().setAttribute("SHIKUGUN_CD", "");
		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] 県コードを検索する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String refectureCd() throws Exception {

		ukg02034Data = new KibouRosennBean();
		HttpServletRequest request = ServletActionContext.getRequest();

		String ekiCd = (String) request.getParameter("ekiCd");

		// 県コードを検索する
		ukg02034Data.setPrefectureCd(UKG02034Services.getRefectureCd(ekiCd));

		return "ajax";
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを取得する。
	 * @return 画面のデータ
	 * </pre>
	 */
	public KibouRosennBean getUkg02034Data() {

		return ukg02034Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg02034Data 画面のデータ
	 * </pre>
	 */
	public void setUkg02034Data(KibouRosennBean ukg02034Data) {

		this.ukg02034Data = ukg02034Data;
	}

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param services サービス対象
	 * </pre>
	 */
	public void setUKG02034Services(IUKG02034Service uKG02034Services) {

		UKG02034Services = uKG02034Services;
	}
}
