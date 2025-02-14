/**
 * @システム名: 受付システム
 * @ファイル名: UKG05031Action.java
 * @更新日付：: 2014/1/21
 * @Copyright: 2014 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG05031Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] まとめて掲載する
 * [説 明] まとめて掲載する。
 * @author [作 成] 2014/1/21 SYS_郭凡 (案件C37075)オリジナル情報誌リプレース
 * </pre>
 */

public class UKG05031Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 4678620113887096916L;

	/** 複数掲載サービス */
	private IUKG05031Service ukg05031Services = null;

	/** 画面のデータ */
	private Map<String, Object> ukg05031Data = new HashMap<String, Object>();

	/**<pre>
	 * [説 明] チラシ作成一覧サービス対象を設定する。
	 * @param ukg05031Service サービス対象
	 * </pre>
	 */
	public void setUkg05031Services(IUKG05031Service ukg05031Services) {
		this.ukg05031Services = ukg05031Services;
	}

	/**
	 * [説 明] 画面のデータを取得する。
	 * @return ukg05031Data 画面のデータ
	 */
	public Map<String, Object> getUkg05031Data() {
		return ukg05031Data;
	}

	/**<pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg05031Data 画面のデータ
	 * </pre>
	 */
	public void setUkg05031Data(Map<String, Object> ukg05031Data) {
		this.ukg05031Data = ukg05031Data;
	}

	/**
	 * <pre>
	 * [説 明] 初期検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);
		HttpServletRequest request = ServletActionContext.getRequest();
		// 初期データ取得
		String bukkenNo = Util.toEmpty(request.getParameter("bukkenNo"));
		//部屋リスト
		String heyaNoList = Util.toEmpty(request.getParameter("heyaList"));
		heyaNoList = new String(heyaNoList.getBytes("ISO-8859-1"), "UTF-8");
		// 代表部屋
		String daiHyoHeyaNo = heyaNoList.split(",")[0];
		//物件名
		String bukkenName = Util.toEmpty(request.getParameter("bukkenName"));
		bukkenName = new String(bukkenName.getBytes("ISO-8859-1"), "UTF-8");
		//間取り
		String madori = Util.toEmpty(request.getParameter("madori"));
		madori = new String(madori.getBytes("ISO-8859-1"), "UTF-8");
		// DBから部屋データ取得
		Map<String, Object> fukusuInfo = ukg05031Services.getfukusukohyoInfoService(bukkenNo, madori, daiHyoHeyaNo);
		List<Map<String, Object>> fukusuList = (List<Map<String, Object>>) fukusuInfo.get("rst_list");
		Iterator<Map<String, Object>> iterator = fukusuList.iterator();
		String DBkaraHeyaNo = "";
		// 部屋No取得
		while(iterator.hasNext()){
			Map<String, Object> map = iterator.next();
			 DBkaraHeyaNo += (String)map.get("HEYA_NO") + ",";
		}
		//複数部屋がある場合設定する
		if(DBkaraHeyaNo.length() > 0){
			DBkaraHeyaNo = DBkaraHeyaNo.substring(0, DBkaraHeyaNo.length()-1);
		}
		//データ設定
		ukg05031Data.put("DBkaraHeyaNo", DBkaraHeyaNo);
		ukg05031Data.put("fukusuList", fukusuList);
		ukg05031Data.put("bukkenName", bukkenName);
		ukg05031Data.put("heyaNolist", heyaNoList);
		ukg05031Data.put("bukkenNo", bukkenNo);
		ukg05031Data.put("daiHyoHeyaNo", daiHyoHeyaNo);

		return successForward();
	}
}
