/**
 * @システム名: 受付システム
 * @ファイル名: UKG04012Action.java
 * @更新日付：: 2013/03/08
 * @Copyright: 2013 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG04012Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] チラシ作成(地図検索)
 * [説 明] チラシ作成(地図検索)アクション。
 * @author [作 成] 2013/03/08 趙雲剛(SYS)
 * </pre>
 */
public class UKG04012Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -5629458236532919212L;

	/** 地図で表示する画面用データ */
	private Map<String, Object> ukg04012Data = null;
	
	/** チラシ作成(地図検索)サービス */
	private IUKG04012Service ukg04012Services;
	
	/**
	 * 地図から検索画面用データ
	 * 
	 * @return ukg04012Data
	 */
	public Map<String, Object> getUkg04012Data() {
		return ukg04012Data;
	}

	/**
	 * 地図から検索画面用データ
	 * 
	 * @param ukg04012Data
	 */
	public void setUkg04012Data(Map<String, Object> ukg04012Data) {
		this.ukg04012Data = ukg04012Data;
	}

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param ukg04012Services サービス対象
	 * </pre>
	 */
	public void setUKG04012Services(IUKG04012Service ukg04012Services) {

		this.ukg04012Services = ukg04012Services;
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
		Map<String, Object> loginInfo = getLoginInfo();
		ukg04012Data = new HashMap<String, Object>();
		
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		String postCd = String.valueOf(loginInfo.get(Consts.POST_CD));
		// 緯度経度の取得
		List<Map<String, Object>> postionList = (List<Map<String, Object>>) ukg04012Services.searchCenterPosition(kaisyaCd, postCd).get("rst_position_list");
		ukg04012Data.put("centerIdo", postionList.get(0).get("I_DO"));
		ukg04012Data.put("centerKeido", postionList.get(0).get("KEI_DO"));
		
		// 都道府県コードがログイン情報.店舗所在都道府県コードを設定する
		String jusyo_cd = (String) loginInfo.get(Consts.POST_JUSYO_CD);
		if("01".equals(jusyo_cd)){
			ukg04012Data.put("jusyo_cd", "01a");
		}else{
			ukg04012Data.put("jusyo_cd", jusyo_cd);
		}
		// 都道府県リストを取得する
		List<Map<String, Object>> JusyoDataList = (List<Map<String, Object>>) ukg04012Services.getJusyoData("081");
		ukg04012Data.put("JUSYO_LIST", JusyoDataList);

		// メプ定数を取得する
		ukg04012Data.put("MAP_SERVLETURL",PropertiesReader.getIntance().getProperty(Consts.MAP_SERVLETURL));
		ukg04012Data.put("MAP_KEY",PropertiesReader.getIntance().getProperty(Consts.MAP_KEY));
		ukg04012Data.put("MAP_VERSION",PropertiesReader.getIntance().getProperty(Consts.MAP_VERSION));
		ukg04012Data.put("MAP_INITSCALE",PropertiesReader.getIntance().getProperty(Consts.UKG04012_MAP_INITSCALE));
		ukg04012Data.put("MAP_MINSCALE",PropertiesReader.getIntance().getProperty(Consts.UKG04012_MAP_MINSCALE));
		
		return successForward();
	}
	
	/**
	 * <pre>
	 * [説 明] 対象エリアの空室物件を検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String kuusituBukken() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> loginInfo = getLoginInfo();
		ukg04012Data = new HashMap<String, Object>();
		
		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		String maxx = request.getParameter("maxx");
		String maxy = request.getParameter("maxy");
		String minx = request.getParameter("minx");
		String miny = request.getParameter("miny");
		
		// 物件情報取得処理(A004)
		Map<String, Object> resul = ukg04012Services.getBukkenInfo(kaisyaCd, maxx, maxy, minx, miny);
		List<Map<String, Object>> bukkenList = (List<Map<String, Object>>) resul.get("rst_bukkensit_list");
		ukg04012Data.put("bukken",bukkenList);
		
		return "ajaxProcess";
	}
	
	/**
	 * 地図アイコンクリック時処理
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
    public String fetchBukenInfo() throws Exception{
		HttpServletRequest request = ServletActionContext.getRequest();
		ukg04012Data = new HashMap<String, Object>();
		String bukkenNo = request.getParameter("bukkenNo");
		// 物件データを取得する
		Map<String, Object> bukenInfoData = ukg04012Services.popBukenInfo(bukkenNo);
		
		// 物件情報
		List<Map<String, Object>> out_bukenInfo = (List<Map<String, Object>>) bukenInfoData.get("out_bukenInfo");
		// 設定ファイルからイメージPathを取得する
		for (Map<String, Object> bukenInfo : out_bukenInfo) {
			
			bukenInfo.put("GK_IMAGE_PATH", "imgIO.imgIO?type=1&path="+bukenInfo.get("GK_IMAGE_PATH"));
			bukenInfo.put("NK_IMAGE_PATH", Util.wmfToSVG((String)bukenInfo.get("NK_IMAGE_PATH"),82f,61f));
		}
		ukg04012Data.put("out_bukenInfo", out_bukenInfo);
		ukg04012Data.put("sysdate", Util.getSysDate());
		if (out_bukenInfo == null || out_bukenInfo.size() == 0) {			
			ukg04012Data.put("rst_msg", Util.getServerMsg("ERRS006", "空室物件"));
		}
		return "ajaxProcess";
	}
}
