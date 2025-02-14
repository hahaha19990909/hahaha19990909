/**
 * @システム名: 受付システム
 * @ファイル名: UKG04010Action.java
 * @更新日付： 2013/06/20
 * 更新履歴: 2013/06/20  SYS_趙雲剛    (案件C37103-1)チラシ作成機能の追加-STEP2
 * 更新履歴：2014/06/10	郭凡(SYS)   (案件番号C38117)セールスポイント編集機能の追加
 * 更新履歴：2018/01/05   肖剣生(SYS)  案件C42036(チラシレイアウト変更)
 * 更新履歴：2018/08/15   劉キ(SYS)  (案件C42015-1)周辺物件案内カードの追加
 * 更新履歴：2020/04/14   程旋(SYS)  C44030_お部屋探しNaviマルチブラウザ対応
 * 更新履歴：2020/04/09   劉恆毅(SYS) C44017-1_チラシ型物件案内カードの物件画像選択機能
 * @Copyright: 2013 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.service.IUKG04010Service;

/**
 * <pre>
 * [機 能] チラシ作成
 * [説 明] チラシ作成アクション。
 * @author [作 成] 2013/03/08 趙雲剛(SYS)
 * </pre>
 */
public class UKG04010Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -5629458236532919212L;

	/** チラシ作成画面用データ */
	private Map<String, Object> ukg04010Data = null;

	// 2013/06/20   趙雲剛   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2）
	/** チラシ作成一覧サービスを取得する */
	private IUKG04010Service ukg04010Services = null;

	/**<pre>
	 * [説 明] チラシ作成一覧サービス対象を設定する。
	 * @param ukg04011Service サービス対象
	 * </pre>
	 */
	public void setUKG04010Services(IUKG04010Service ukg04010Services) {
		this.ukg04010Services = ukg04010Services;
	}
	// 2013/06/20   趙雲剛   ADD END

	/**
	 * チラシ作成画面用データ
	 *
	 * @return ukg04010Data　画面データ
	 */
	public Map<String, Object> getUkg04010Data() {
		return ukg04010Data;
	}

	/**
	 * チラシ作成画面用データ
	 *
	 * @param ukg04010Data 画面データ
	 */
	public void setUkg04010Data(Map<String, Object> ukg04010Data) {
		this.ukg04010Data = ukg04010Data;
	}

	/**
	 * <pre>
	 * [説 明] 初期検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);

		//2013/06/19   郭凡   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2）
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		//セッションの自事業所チェックフラグをクリアする
		session.removeAttribute(Consts.UKG04011_JIJIGYOUSYO_FLG);
		//2013/06/19   郭凡   ADD END

		// 2013/06/20   趙雲剛   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2）
		ukg04010Data = new HashMap<String, Object>();
		// チラシタイプ取得
		List<Map<String, Object>> chirashiOneBukken = (List<Map<String, Object>>) ukg04010Services.getCommonData("094").get("rst_list");
		List<Map<String, Object>> chirashiFukusuuBukken = (List<Map<String, Object>>) ukg04010Services.getCommonData("095").get("rst_list");

        // 2018/08/15  劉キ   ADD START (案件C42015-1)周辺物件案内カードの追加
        //チラシタイプ情報（物件掲載用）
        List<Map<String, Object>> shuuhennBukken = (List<Map<String, Object>>) ukg04010Services.getCommonData("117").get("rst_list");
        //物件紹介区分取得
        List<Map<String, Object>> bukkenShoukaiKubunn = (List<Map<String, Object>>) ukg04010Services.getCommonData("118").get("rst_list");
        //2018/08/15  劉キ  ADD END

		//2018/01/05 肖剣生　ADD　START　案件C42036(チラシレイアウト変更)
		String[] tempName = null;
		String tempCd = null;
		String tempStr = "1,2,6,13,14";
		for(int j = 0; j < chirashiOneBukken.size(); j++){
			tempName = chirashiOneBukken.get(j).get("KOUMOKU_SB_NAME").toString().split("\\(");
			tempCd = chirashiOneBukken.get(j).get("KOUMOKU_SB_CD").toString();
			if(tempName.length > 1 && tempStr.contains(tempCd)){
				chirashiOneBukken.get(j).put("KOUMOKU_SB_NAME1", tempName[0].toString());
				chirashiOneBukken.get(j).put("KOUMOKU_SB_NAME2", tempName[1].substring(0, tempName[1].length()-1));
			}else{
				chirashiOneBukken.get(j).put("KOUMOKU_SB_NAME1", chirashiOneBukken.get(j).get("KOUMOKU_SB_NAME").toString());
			}
		}

		tempName = null;
		for(int k = 0; k < chirashiFukusuuBukken.size(); k++){
			tempName = chirashiFukusuuBukken.get(k).get("KOUMOKU_SB_NAME").toString().split("\\(");
			chirashiFukusuuBukken.get(k).put("KOUMOKU_SB_NAME1", tempName[0].toString());
			chirashiFukusuuBukken.get(k).put("KOUMOKU_SB_NAME2", tempName[1].substring(0, tempName[1].length()-1));
		}
		//2018/01/05 肖剣生　ADD　END	案件C42036(チラシレイアウト変更)

		// 色コード取得
		List<Map<String, Object>> iroList = (List<Map<String, Object>>) ukg04010Services.getCommonData("089").get("rst_list");
		// 2014/06/10   郭凡 ADD START 案件C38117(セールスポイント編集機能の追加)
		List<Map<String, Object>> checkList = (List<Map<String, Object>>) ukg04010Services.getCommonData("107").get("rst_list");
		StringBuilder checkData = new StringBuilder();
		for(int i = 0; i < checkList.size(); i++){
			checkData.append(checkList.get(i).get("KOUMOKU_SB_NAME"));
			checkData.append(",");
		}
		ukg04010Data.put("checkData", checkData.toString());
		// 2014/06/10   郭凡 ADD END
		ukg04010Data.put("chirashiOneBukken", chirashiOneBukken);
		ukg04010Data.put("chirashiFukusuuBukken", chirashiFukusuuBukken);

        // 2018/08/15  劉キ   ADD START (案件C42015-1)周辺物件案内カードの追加
        ukg04010Data.put("shuuhennBukken", shuuhennBukken);
        ukg04010Data.put("bukkenShoukaiKubunn", bukkenShoukaiKubunn);
        //2018/08/15  劉キ  ADD END

		ukg04010Data.put("iroList", iroList);
		ukg04010Data.put("UKG04010_MAXSENTAKUSUU",PropertiesReader.getIntance().getProperty(Consts.UKG04010_MAXSENTAKUSUU));
		ukg04010Data.put("UKG04010_FUKUSHUMAXSENTAKUSUU",PropertiesReader.getIntance().getProperty(Consts.UKG04010_FUKUSHUMAXSENTAKUSUU));
		Map<String, Object> loginInfo = getLoginInfo();
		// 会社コードを取得
		ukg04010Data.put("kaisyaCd", loginInfo.get(Consts.KAISYA_CD));
		// 担当者コードを取得
		ukg04010Data.put("tantouCd", loginInfo.get(Consts.USER_CD));
		ukg04010Data.put("postCd", loginInfo.get(Consts.POST_CD));
		ukg04010Data.put("userCd", loginInfo.get(Consts.LOGIN_USER_CD));
		// 2014/09/09   郭凡 ADD START 案件C38117(セールスポイント編集機能の追加)
		String authorityCd = (String) loginInfo.get(Consts.USER_AUTHORITY_CD);
		ukg04010Data.put("AUTHORITY_CD", authorityCd);
		// 2014/09/09   郭凡 ADD END
		// リクエストURL,設定ファイル(uketuke.properties).tirasiURL
		String printActionId = PropertiesReader.getIntance().getProperty("tirasiURL");
		ukg04010Data.put("printActionId", printActionId);
		String tirashiTitleName = PropertiesReader.getIntance().getProperty(Consts.TIRASHI_PDF_WINDOWSHOWINGNAME);
		ukg04010Data.put("tirashiTitleName", tirashiTitleName);
		String tirashiDownloadName = PropertiesReader.getIntance().getProperty(Consts.TIRASHI_PDF_DOWNLOADNAME);
		ukg04010Data.put("tirashiDownloadName", tirashiDownloadName);
		// 2013/06/20   趙雲剛   ADD END
		/*2020/04/14 程旋 ADD START C44030_お部屋探しNaviマルチブラウザ対応*/
		String chouhyouServer = PropertiesReader.getIntance().getProperty(Consts.CHOUHYOUSERVER);
		ukg04010Data.put("chouhyouServer", chouhyouServer);
		/*2020/04/14 程旋 ADD END C44030_お部屋探しNaviマルチブラウザ対応*/
		return successForward();
	}

	// 2013/06/20   趙雲剛   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2）
	/**
	 * <pre>
	 * [説 明] INFOログを出力する
	 * @return ログを出力結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String printLog() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		String reiautoCode = request.getParameter("reiautoCode");
		String checkedColorCd = request.getParameter("checkedColorCd");
		String userId = request.getParameter("userId");
		String bukkenHeyaData = request.getParameter("bukkenHeyaData");
		String bukkenheyaList[] = bukkenHeyaData.trim().split(",");
		String[] reiauto = reiautoCode.split(",");
		for (int i = 0; i < reiauto.length; i++) {
			for (int j = 0; j < bukkenheyaList.length; j++) {
				String bukkenheya[] = bukkenheyaList[j].split("_");
				String message = "[チラシ作成]チラシ印刷 ユーザID：" + userId + "  物件No:"
						+ bukkenheya[0] + " 号室:" + bukkenheya[1] + " チラシタイプ：["
						+ reiauto[i] + "] 色：[" + checkedColorCd + "]";
				log.info(message);
			}
		}
		return "ajaxProcess";
	}
	// 2013/06/20   趙雲剛   ADD END
	
    //2020/03/31 劉恆毅 ADD Start C44017-1_チラシ型物件案内カードの物件画像選択機能
    /**
     * <pre>
     * [説 明] 内観、外観画像を取得
     * @return 内観、外観画像
     * @throws Exception　処理異常
     * </pre>
     */
    public String getGazou()  throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        ukg04010Data = new HashMap<String, Object>();
        
        String bukkenno = request.getParameter("bukkenno");
        String heyano = request.getParameter("heyano");
        
        //内観、外観画像データを取得
        Map<String, Object> gazouList =  ukg04010Services.getGazou(bukkenno, heyano);
        //内観画像リスト設定
        ukg04010Data.put("naikanGazouList", gazouList.get("RES_NAIKAN_GAZOU_LIST"));
        //外観画像リスト設定
        ukg04010Data.put("gaikanGazouList", gazouList.get("RES_GAIKAN_GAZOU_LIST"));
        return "ajaxProcess";
    }
    //2020/03/31 劉恆毅 ADD End C44017-1_チラシ型物件案内カードの物件画像選択機能
}