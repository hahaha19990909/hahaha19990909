/**
 * @システム名: 受付システム
 * @ファイル名: UKG02031Action.java
 * @更新日付：
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴: 2014/01/21  SYS_ 謝超    (案件C37075)オリジナル情報誌リプレース
 * 更新履歴： 2014/06/10	郭凡(SYS)       1.01  (案件番号C38117)セールスポイント編集機能の追加
 * 更新履歴：  2018/02/24	 肖剣生(SYS)   (案件C42036)デザイン変更新規作成
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.components.ElseIf;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.KibouJyoukennBean;
import jp.co.token.uketuke.service.IUKG02031Service;

/**
 * <pre>
 * [機 能] 希望地域の設定(市区郡選択)
 * [説 明]希望地域の設定(市区郡選択)する。
 * @author [作 成] 2012/05/08 SYS_郭
 * 更新履歴: 2014/01/21  SYS_ 謝超    (案件C37075)オリジナル情報誌リプレース
 * </pre>
 */
public class UKG02031Action extends BaseAction {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4778159824126799116L;

	/** 希望地域の設定(市区郡選択)画面用データ */
	private Map<String, Object> ukg02031Data = null;

	/** 希望地域の設定(市区郡選択)サービス */
	private IUKG02031Service UKG02031Services;

	/** 希望条件指定コード */
	private String kibouJyoukennCode;
	
	private String kibouprefectureCd;
	
	
    public String getKibouprefectureCd() {
		return kibouprefectureCd;
	}


	public void setKibouprefectureCd(String kibouprefectureCd) {
		this.kibouprefectureCd = kibouprefectureCd;
	}


	/**<pre>
     * [説 明] 希望条件指定コードを取得する。
     * @return 希望条件指定コード
     * </pre>
     */
    public String getKibouJyoukennCode() {
    
    	return kibouJyoukennCode;
    }

	
    /**<pre>
     * [説 明] 希望条件指定コードを設定する。
     * @param kibouJyoukennCode　希望条件指定コード
     * </pre>
     */
    public void setKibouJyoukennCode(String kibouJyoukennCode) {
    
    	this.kibouJyoukennCode = kibouJyoukennCode;
    }
    
	/**
	 * <pre>
	 * [説 明] 希望地域の設定(市区郡選択)
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		// 2014/01/21  謝超 UPD Start (案件番号C37075)オリジナル情報誌リプレース
		//KibouJyoukennBean kibouJyokenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN);
		// プリセットコード
		String presetNo = Util.toEmpty(request.getParameter("presetNo"));
		KibouJyoukennBean kibouJyokenBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN+presetNo);
		// 2014/01/21  謝超 UPD End
		if (kibouJyokenBean == null){
			kibouJyokenBean = new KibouJyoukennBean();
		}
		
		String prefectureCd = null;
		
		if (!Util.isEmpty(kibouJyokenBean.getPrefectureCd())) {
			prefectureCd = kibouJyokenBean.getPrefectureCd();
		}
		
		//希望条件指定コード
		kibouJyoukennCode = kibouJyokenBean.getKibouJyoukennCode();
		
		// ログイン情報
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		// ########## 2014/06/10 郭凡ADD Start (案件C38117)セールスポイント編集機能の追加
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		// ########## 2014/06/10 郭凡 ADD End
		// 都道府県リストを取得する
		List<Map<String, Object>> JusyoDataList = (List<Map<String, Object>>) UKG02031Services.getJusyoData("081");
		List<Map<String, Object>> JusyoDataList2 = (List<Map<String, Object>>) UKG02031Services.getJusyoData("010");
		ukg02031Data = new HashMap<String, Object>();
		ukg02031Data.put("JUSYO_LIST", JusyoDataList);
		ukg02031Data.put("JUSYO_LIST2", JusyoDataList2);
		ukg02031Data.put("btn_flg", request.getParameter("btn_flg"));
		// 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		ukg02031Data.put("pageid", request.getParameter("pageid"));
		// 2014/01/21  謝超 ADD End
		if (prefectureCd == null) {
		// ########## 2014/06/10 郭凡 UPD Start (案件C38117)セールスポイント編集機能の追加
			if ("".equals(presetNo) || presetNo == null) {
				prefectureCd = (String)uketukePostInfo.get(Consts.POST_JUSYO_CD);
			} else {
				prefectureCd = (String)sessionLoginInfo.get(Consts.POST_JUSYO_CD);
			}
		// ########## 2014/06/10 郭凡 UPD End
		}
		// 店舗所在都道府県コード
		ukg02031Data.put("POST_JUSYO_CD", prefectureCd);
		ukg02031Data.put("MAP_SERVLETURL", PropertiesReader.getIntance().getProperty(Consts.MAP_SERVLETURL));
		ukg02031Data.put("MAP_KEY", PropertiesReader.getIntance().getProperty(Consts.MAP_KEY));
		ukg02031Data.put("MAP_VERSION", PropertiesReader.getIntance().getProperty(Consts.MAP_VERSION));
		// 2014/01/21  謝超 ADD Start (案件番号C37075)オリジナル情報誌リプレース
		ukg02031Data.put("presetNo", presetNo);
		// 2014/01/21  謝超 ADD End
		
		//2018/02/24	 肖剣生(SYS)  ADD Start (案件C42036)デザイン変更新規作成
		if (Consts.UKG05021_GAMEN_KB.equals(kibouJyokenBean.getGamenKubun()) || Consts.UKG05022_GAMEN_KB.equals(kibouJyokenBean.getGamenKubun())) {
			return "success_ff_popup";
		} else {
			return successForward();			
		}
		//2018/02/24	 肖剣生(SYS)  End
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを取得する。
	 * @return 画面のデータ
	 * </pre>
	 */
	public Map<String, Object> getUkg02031Data() {

		return ukg02031Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg02031Data 画面のデータ
	 * </pre>
	 */
	public void setUkg02031Data(Map<String, Object> ukg02031Data) {

		this.ukg02031Data = ukg02031Data;
	}

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param services サービス対象
	 * </pre>
	 */
	public void setUKG02031Services(IUKG02031Service uKG02031Services) {

		UKG02031Services = uKG02031Services;
	}
}
