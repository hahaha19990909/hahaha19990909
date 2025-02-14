/**
 * @システム名: 受付システム
 * @ファイル名: UKG99007Action.java
 * @更新日付 : 2013/06/03
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.action;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG99007Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 物件情報詳細で表示する
 * [説 明] 物件情報詳細で表示する
 * @author SYS_郭凡
 * </pre>
 */
public class UKG99007Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = 4807617362968369317L;

	/** 物件情報詳細画面用データ */
	private Map<String, Object> data = null;

	/** 物件情報詳細サービス */
	private IUKG99007Service UKG99007Services;

	/**
	 * @param uKG99007Services
	 *            the uKG99007Services to set
	 */
	public void setUKG99007Services(IUKG99007Service uKG99007Services) {

		UKG99007Services = uKG99007Services;
	}

	/**
	 * @return the data
	 */
	public Map<String, Object> getData() {

		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Map<String, Object> data) {

		this.data = data;
	}

	/**
	 * <pre>
	 * [説 明] 物件情報詳細で表示する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);

		// 初期化のパラメター設定
		HttpServletRequest request = ServletActionContext.getRequest();
		String bukkenNo = String.valueOf(request.getParameter("bukkenNo"));
		String heyaNo = String.valueOf(request.getParameter("heyaNo"));
		if (!Util.isEmpty(heyaNo) && "ISO-8859-1".equals(Util.getEncoding(heyaNo))) {
			heyaNo = new String(heyaNo.getBytes("ISO-8859-1"), "UTF-8");
		}

		data = new HashMap<String, Object>();
		data.put("bukkenNo", bukkenNo);
		data.put("heyaNo", heyaNo);
		return successForward();
	}

	/**
	 * 初期化の物件情報の取得
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 */
	@SuppressWarnings("unchecked")
	public String initInfoGet() throws Exception {

		data = new HashMap<String, Object>();

		// 初期化のパラメター設定
		HttpServletRequest request = ServletActionContext.getRequest();
		// セッションのログイン情報取得する
		Map<String, Object> sessionLoginInfo = getLoginInfo();

		// ソート順設定する
		String bukkenNo = String.valueOf(request.getParameter("bukkenNo"));
		String heyaNo = String.valueOf(request.getParameter("heyaNo"));
		String kaisyaCd = sessionLoginInfo.get(Consts.KAISYA_CD).toString();
		String post = sessionLoginInfo.get(Consts.POST_CD).toString();
		// 初期データを取得する
		Map<String, Object> resultInfo = UKG99007Services.getInitInfo(bukkenNo, heyaNo, kaisyaCd, post);

		List<Map<String, Object>> initInfo = (List<Map<String, Object>>) resultInfo
				.get("RST_INITINFO");
		data.put("sysDate", Util.getSysDateYMD().replace("/", ""));
		if (initInfo == null || initInfo.size() == 0) {
			data.put("dataFound", "1");
			data.put("rstMsg", Util.getServerMsg("ERRS011", "対象の物件は入居中"));

			return "ajaxProcess";
		}

		// 外観イメッジ設定
		if (initInfo.get(0).get("GAIKANIMGPATH") != null
				&& !Util.isEmpty(initInfo.get(0).get("GAIKANIMGPATH")
						.toString())) {
			initInfo.get(0).put("GAIKANIMGPATH",
			                    Util.setImgPath((String) initInfo.get(0).get("GAIKANIMGPATH"), "1"));
		}

		data.put("initInfo", resultInfo.get("RST_INITINFO"));

		return "ajaxProcess";
	}
}