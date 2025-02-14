/** 
 *@システム名: 受付システム 
 *@ファイル名: UKG99001Action.java 
 *@更新日付：: 2011/12/26 
 *@Copyright: 2011 token corporation All right reserved  
 */
package jp.co.token.uketuke.action;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG99001Service;

import org.apache.struts2.ServletActionContext;

/**<pre>
 * [機 能] 顧客検索
 * [説 明] 検索条件によって、顧客情報を検索し、表示する。
 * @author [作 成] 2011/10/26 SYS_賈
 * 履  歴:[修 正]  2014/05/07   趙雲剛(SYS)     オリジナル情報誌リプレース(案件C37075)
 * 履  歴:[修 正]  2018/01/25   肖剣生(SYS)     デザイン変更新規作成(案件C42036)
 * </pre>
 */
public class UKG99001Action extends BaseAction {
	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;
	/** 顧客検索画面用データ */
	private Map<String, Object> ukg99001Data = new HashMap<String, Object>();
	/** 顧客検索サービス */
	private IUKG99001Service UKG99001Services;

	//2018/02/11   肖剣生(SYS) ADD START 案件C42036(デザイン変更新規作成)
	private static final int MAX_RECORD_NUM = 999;
	//2018/02/11   肖剣生(SYS) ADD END 案件C42036(デザイン変更新規作成)
	
	/**
	 * 顧客検索画面用データ 
	 * @return
	 */
	public Map<String, Object> getUkg99001Data() {
		return ukg99001Data;
	}
	
	/**
	 * 顧客検索画面用データ 
	 * @param ukg99001Data
	 */
	public void setUkg99001Data(Map<String, Object> ukg99001Data) {
		this.ukg99001Data = ukg99001Data;
	}

	/**
	 * 顧客検索サービス
	 * @param services
	 */
	public void setUKG99001Services(IUKG99001Service services) {
		UKG99001Services = services;
	}

	/** <pre>
	 * [説 明] 検索処理。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		//同期フラグ設定
		setAsyncFlg(true);
		
		HttpServletRequest request = ServletActionContext.getRequest();
        String kokakuSbFlg = request.getParameter("kokakuSbFlg");
        
        //ソート順取得する
		HttpSession session = request.getSession();		
		session.setAttribute(Consts.UKG99001_SORT_INFO, Consts.UKG99001_SORT_JYUN);
		
        Map<String, Object> initInfo = UKG99001Services.getInit("005", "001");
        
        ukg99001Data.put("kokakusbList", (List<Map<String, Object>>) initInfo.get("rst_kokyakusb_list"));
		ukg99001Data.put("nenngouList", (List<Map<String, Object>>) initInfo.get("rst_negetu_list"));
		ukg99001Data.put("kokakuSbFlg", kokakuSbFlg);

		return successForward();
	}


	/**
	 * 検索ボタン押下処理
	 * kokyakuJyouho()
	 * @throws SQLException		SQL例外
	 * @throws Exception		一般例外
	 */
	@SuppressWarnings("unchecked")
	public String kokyakuJyouho() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        
		//セッションのログイン情報取得する
        Map<String, Object> loginInfo = getLoginInfo();
		
        String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
        
        String rdKokyakuSb1 = request.getParameter("rdKokyakuSb");
        String kokyaku = request.getParameter("kokyaku");
        String chkKokyaku = request.getParameter("chkKokyaku");
        String txtKokyakuKana = request.getParameter("txtKokyakuKana");
        String chkKokyakuKana = request.getParameter("chkKokyakuKana");
        String birthday = request.getParameter("birthday");
        String txtTel = request.getParameter("txtTel").replace("-", "");
        String txtMobile = request.getParameter("txtMobile").replace("-", "");

        //ソート順取得する
		HttpSession session = request.getSession();		
		String sort = String.valueOf(session.getAttribute(Consts.UKG99001_SORT_INFO));
        // 顧客情報データを取得する
        Map<String, Object> kokyakuJyouhoData = UKG99001Services.getKokyakuJyouho(kaisyacd,
																					rdKokyakuSb1,
																					kokyaku,
																					chkKokyaku,
																					txtKokyakuKana,
																					chkKokyakuKana,
																					birthday,
																					txtTel,
																					txtMobile,
																					"",
																					"",
																					1,
		//2018/02/11   肖剣生(SYS)   UPD START 案件C42036(デザイン変更新規作成)
//																					10
																					MAX_RECORD_NUM,
		//2018/02/11   肖剣生(SYS)   UPD END
																					sort);
		ukg99001Data.put("kokyakuJyouhoList", (List<Map<String, Object>>) kokyakuJyouhoData.get("pcurList"));

		ukg99001Data.put("totalRowCount", String.valueOf(kokyakuJyouhoData.get("out_recordcount")));

		return "kokyakuJyouho";
	}
	/** <pre>
	 * [説 明] ソート
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String sort() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		Map<String, Object> loginInfo = getLoginInfo();
        String rdKokyakuSb = request.getParameter("rdKokyakuSb");
        String kokyaku = request.getParameter("kokyaku");
        String chkKokyaku = request.getParameter("chkKokyaku");
        String txtKokyakuKana = request.getParameter("txtKokyakuKana");
        String chkKokyakuKana = request.getParameter("chkKokyakuKana");
        String birthday = request.getParameter("birthday");
        
        String txtTel = request.getParameter("txtTel").replace("-", "");
        String txtMobile = request.getParameter("txtMobile").replace("-", "");
        
        String oyaKana = request.getParameter("kana1");
        String koKana = request.getParameter("kana2");
        String sortitem = request.getParameter("sortitem");
        String sortjun = request.getParameter("sortjun");
        String oldsortjyun = request.getParameter("oldsortjyun");
        
		String oldsort = sortitem + " " + oldsortjyun;
		String newsort = sortitem + " " + sortjun;
		
		String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		
		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();
		String sortkomoku = (String) session.getAttribute(Consts.UKG99001_SORT_INFO);
		if (sortkomoku.endsWith(oldsort)) {
			oldsort = "," + oldsort;
		} else if (sortkomoku.startsWith(oldsort)) {
			oldsort = oldsort + ",";
		}

		sortkomoku = sortkomoku.replace(oldsort, "").replace(",,", ",");
		StringBuilder strbuildsort = new StringBuilder(sortkomoku);
		newsort = newsort + ",";
		strbuildsort.insert(0, newsort);
		sortkomoku = strbuildsort.toString();
		 
        //ソート順設定する
		session.setAttribute(Consts.UKG99001_SORT_INFO,sortkomoku);
        
		Map<String, Object> kokyakuJyouhoData = UKG99001Services.getKokyakuJyouho(kaisyacd,
																					rdKokyakuSb,
																					kokyaku,
																					chkKokyaku,
																					txtKokyakuKana,
																					chkKokyakuKana,
																					birthday,
																					txtTel,
																					txtMobile,
																					oyaKana,
																					koKana,
																					1,
//2018/02/11   肖剣生(SYS)   UPD START 案件C42036(デザイン変更新規作成)
//																					10
																					MAX_RECORD_NUM,
//2018/02/11   肖剣生(SYS)   UPD END
																					sortkomoku);

		ukg99001Data.put("kokyakuJyouhoList", (List<Map<String, Object>>) kokyakuJyouhoData.get("pcurList"));
		ukg99001Data.put("totalRowCount", String.valueOf(kokyakuJyouhoData.get("out_recordcount")));
		
		return "sort";
	}

	/** <pre>
	 * [説 明] ページ切り替える。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String pageIndexChg() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
        String condition = request.getParameter("condition");

        //ページ切り替える機能用パラメータを取得する
        Map<String, String> paramMap = Util.getParamMap(condition);

        String rdKokyakuSb2 = String.valueOf(paramMap.get("rdKokyakuSb"));
        String strKokyaku = String.valueOf(paramMap.get("kokyaku"));
        String chkKokyaku = String.valueOf(paramMap.get("chkKokyaku"));
        String txtKokyakuKana = String.valueOf(paramMap.get("txtKokyakuKana"));
        String chkKokyakuKana = String.valueOf(paramMap.get("chkKokyakuKana"));
        String txtTel = String.valueOf(paramMap.get("txtTel")).replace("-", "");
        String txtMobile = String.valueOf(paramMap.get("txtMobile")).replace("-", "");
        String birthday = paramMap.get("birthday");
        
        String oyaKana = paramMap.get("kana1");
        String koKana = paramMap.get("kana2");
        

		//セッションのログイン情報取得する
        Map<String, Object> loginInfo = getLoginInfo();

		String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));

        int startRowNum = (Integer.parseInt(paramMap.get("pageNum")) - 1) * 10 + 1;
        int endRowNum = startRowNum - 1 + 10;

        //ソート順取得する
		HttpSession session = request.getSession();
		String sortkomoku = String.valueOf(session.getAttribute(Consts.UKG99001_SORT_INFO));
		
        //業務処理（取得した条件で該当ページのデータを取得する）
        Map<String, Object> kokyakuJyouhoData = UKG99001Services.getKokyakuJyouho(kaisyacd,
        																			rdKokyakuSb2,
        																			strKokyaku,
        																			chkKokyaku,
        																			txtKokyakuKana,
        																			chkKokyakuKana,
        																			birthday,
        																			txtTel,
        																			txtMobile,
        																			oyaKana,
        																			koKana,
        																			startRowNum,
        																			endRowNum,
        																			sortkomoku);

		//サービスから取得したデータを画面に設定する
        ukg99001Data.put("kokyakuJyouhoList", (List<Map<String, Object>>) kokyakuJyouhoData.get("pcurList"));

		return "pageIndexChg";
	}

	/** <pre>
	 * [説 明] カナインデックス切り替える。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String kanaIndexChg() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
        String condition = request.getParameter("condition"); //[kana1=ア,kana2=イ]の条件を取得する
        //カナインデックス替える機能用パラメータを取得する
        Map<String, String> paramMap = Util.getParamMap(condition);

        String oyaKana = paramMap.get("kana1");
        String koKana = paramMap.get("kana2");

        String kyaKuSB = String.valueOf(paramMap.get("rdKokyakuSb"));
        String kokyaku = String.valueOf(paramMap.get("kokyaku"));
        String chkKokyaku = String.valueOf(paramMap.get("chkKokyaku"));
        String txtKokyakuKana = String.valueOf(paramMap.get("txtKokyakuKana"));
        String chkKokyakuKana = String.valueOf(paramMap.get("chkKokyakuKana"));
        String txtTel = String.valueOf(paramMap.get("txtTel")).replace("-", "");
        String txtMobile = String.valueOf(paramMap.get("txtMobile")).replace("-", "");
        String birthday = paramMap.get("birthday");

		//セッションのログイン情報取得する
        Map<String, Object> loginInfo = getLoginInfo();

		String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));

        //ソート順取得する
		HttpSession session = request.getSession();
		String sortkomoku = String.valueOf(session.getAttribute(Consts.UKG99001_SORT_INFO));
		
        //業務処理（取得した条件で該当ページのデータを取得する）
        Map<String, Object> kokyakuJyouhoData = UKG99001Services.getKokyakuJyouho(kaisyacd,
        																			kyaKuSB,
        																			kokyaku,
        																			chkKokyaku,
        																			txtKokyakuKana,
        																			chkKokyakuKana,
        																			birthday,
        																			txtTel,
        																			txtMobile,
        																			oyaKana,
        																			koKana,
        																			1,
//2018/02/11   肖剣生(SYS)   UPD START 案件C42036(デザイン変更新規作成)
//																					10
        																			MAX_RECORD_NUM,
//2018/02/11   肖剣生(SYS)   UPD END
        																			sortkomoku);
        
		//サービスから取得したデータを画面に設定する
        ukg99001Data.put("kokyakuJyouhoList", (List<Map<String, Object>>) kokyakuJyouhoData.get("pcurList"));
        ukg99001Data.put("totalRowCount", String.valueOf(kokyakuJyouhoData.get("out_recordcount")));

        //プロパティファイルよりカナインデックス表示数を取得する
        // ########## 2014/05/07 趙雲剛 UPD Start (案件C37075)オリジナル情報誌リプレース
		//String indexKirikae = PropertiesReader.getIntance("/uketuke.properties").getProperty("indexKirikae");
		String indexKirikae = PropertiesReader.getIntance().getProperty("indexKirikae");
		// ########## 2014/05/07 趙雲剛 UPD End
        //親カナ、子カナインデックス作成フラグ設定
        if (Util.isEmpty(koKana)) {
        	if ("全て".equals(oyaKana)) {
        		ukg99001Data.put("reDrawFlg", "0");
			} else {
				if (Integer.valueOf(String.valueOf(ukg99001Data.get("totalRowCount"))) > Integer.valueOf(indexKirikae)
						&& !"ン".equals(oyaKana)) {
					ukg99001Data.put("reDrawFlg", "1");
				} else {
					ukg99001Data.put("reDrawFlg", "0");
				}

			}

        } else if ("戻る".equals(koKana)) {
        	ukg99001Data.put("reDrawFlg", "0");
        } else {
        	ukg99001Data.put("reDrawFlg", "1");
        }
		return "kanaIndexChg";
	}
}
