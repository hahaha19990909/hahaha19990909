/** 
 *@システム名: 受付システム 
 *@ファイル名: UKG01011Action.java 
 *@更新日付：: 2011/12/26 
 *@Copyright: 2011 token corporation All right reserved  
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.service.IUKG01011Service;

/**<pre>
 * [機 能] 受付未登録一覧。
 * [説 明] 受付未登録一覧アクション。
 * @author [作 成] 2011/10/21 戴氷馨(SYS)
 * @author [修 正] by 趙雲剛 2012/01/16 bug028対応
 * @author [修 正] by 趙雲剛 2014/06/23 (案件C38117)セールスポイント編集機能の追加
 * </pre>
 */
public class UKG01011Action extends BaseAction{
	
	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 6045633971045212147L;
	/** 受付未登録一覧サービス */
	private IUKG01011Service ukg01011Services = null;
	/** 画面のデータ */
	private Map<String,Object> ukg01011Data = new HashMap<String,Object>();
	
	/**
	 * [説 明] 画面のデータを取得する。
	 * @return ukg01011Data 画面のデータ
	 */
	public Map<String, Object> getUkg01011Data() {
		return ukg01011Data;
	}
	
	/**<pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg01011Data 画面のデータ
	 * </pre>
	 */
	public void setUkg01011Data(Map<String, Object> ukg01011Data) {
		this.ukg01011Data = ukg01011Data;
	}
	
	/**<pre>
	 * [説 明] 受付未登録一覧サービス対象を設定する。
	 * @param ukg01011Service サービス対象
	 * </pre>
	 */
	public void setUkg01011Services(IUKG01011Service ukg01011Services) {
		this.ukg01011Services = ukg01011Services;
	}
	
	/**<pre>
	 * [説 明] 受付未登録一覧処理を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {
		// 同期フラグ設定
		setAsyncFlg(true);

		HttpServletRequest request = ServletActionContext.getRequest();

		// セッションのログイン情報取得する
		Map<String, Object> loginInfo = getLoginInfo();

		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyacd = String
				.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		//String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));
		String postcd = request.getParameter("postCd");
		String authorityCd = (String) loginInfo.get(Consts.USER_AUTHORITY_CD);
        String sisyaBlkCd = (String) loginInfo.get(Consts.SISYA_BLK_CD);
		// ########## 2014/06/23 趙雲剛 UPD End
				
		// Nullの場合、ソート順を設定する
		if (session.getAttribute(Consts.UKG01011_SORT_INFO) == null) {
			
			//36014_bug028対応 2012/01/16 Start by 趙雲剛
			String sortkomoku = "UKETUKE_DATE DESC,UKETUKE_NO DESC,CHUKAI_TANTOU_CD ASC";
			//36014_bug028対応 2012/01/16 End by 趙雲剛
			session.setAttribute(Consts.UKG01011_SORT_INFO, sortkomoku);
		}
		// セッション情報からソート順を取得する
		String sortkomoku = String.valueOf(session.getAttribute(Consts.UKG01011_SORT_INFO));

		// 初期化ソート順を設定する
		ukg01011Data.put("sortkomoku", sortkomoku);

		// 未登録データリストを取得する
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		//Map<String, Object> mitorokuinfoList = ukg01011Services
		//		.getMitorokuInfo(kaisyacd, postcd, sortkomoku);
		Map<String, Object> mitorokuinfoList = ukg01011Services
				.getMitorokuInfo(kaisyacd, authorityCd, sisyaBlkCd, postcd, sortkomoku);
		ukg01011Data.put("postcd", postcd);
		ukg01011Data.put("authorityCd", authorityCd);
		// ########## 2014/06/23 趙雲剛 UPD End
		ukg01011Data.put("mitorokuinfoList", mitorokuinfoList.get("rst_list"));

		return successForward();
	}	
	
	/** <pre>
	 * [説 明] ソート
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String sort() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String sortitem = request.getParameter("sortitem");
		String sortjun = request.getParameter("sortjun");
		String oldsortjyun = request.getParameter("oldsortjyun");

		String oldsort = sortitem + " " + oldsortjyun;
		String newsort = sortitem + " " + sortjun;

		// セッションのログイン情報取得する
		Map<String, Object> loginInfo = getLoginInfo();

		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		//String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));
		String postcd = request.getParameter("postCd");
		String authorityCd = (String) loginInfo.get(Consts.USER_AUTHORITY_CD);
        String sisyaBlkCd = (String) loginInfo.get(Consts.SISYA_BLK_CD);
		// ########## 2014/06/23 趙雲剛 UPD End
		String sortkomoku = (String) session.getAttribute(Consts.UKG01011_SORT_INFO);

		if (sortkomoku.endsWith(oldsort)) {
			oldsort = "," + oldsort;
		} else if (sortkomoku.startsWith(oldsort)) {
			oldsort = oldsort + ",";
		}

		sortkomoku = sortkomoku.replace(oldsort, "").replace(",,", ",");
		StringBuilder strbuildsort = new StringBuilder(sortkomoku);
		newsort = newsort + ",";
		strbuildsort.insert(0, newsort);

		session.setAttribute(Consts.UKG01011_SORT_INFO, strbuildsort.toString());

		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		//Map<String, Object> mitorokuinfoList = ukg01011Services.getMitorokuInfo(kaisyacd, postcd, strbuildsort.toString());
		Map<String, Object> mitorokuinfoList = ukg01011Services.getMitorokuInfo(kaisyacd, authorityCd, sisyaBlkCd, postcd, strbuildsort.toString());
		// ########## 2014/06/23 趙雲剛 UPD End
		ukg01011Data.put("mitorokuinfoList", mitorokuinfoList.get("rst_list"));

		return "sort";
	}
		
	/** <pre>
	 * [説 明]セッションにソート順をクリアする
	 * @return closePage
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String closePage( ) {
		HttpServletRequest request = ServletActionContext.getRequest();
		//セッションにソート順をクリアする
		HttpSession session = request.getSession();
		session.removeAttribute(Consts.UKG01011_SORT_INFO);
		return "closePage";
	}
}
