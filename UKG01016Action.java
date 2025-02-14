/**
 *@システム名: 受付システム
 *@ファイル名: UKG01016Action.java
 *@更新日付：: 2017/07/31
 *@Copyright: 2011 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.DispFormat;
import jp.co.token.uketuke.service.IUKG01016Service;
import org.apache.struts2.ServletActionContext;

/**<pre>
 * [機 能] ホームメイト自動受付一覧
 * [説 明] ホームメイト自動受付一覧アクション
 * @author [作 成] 2017/07/31 MJEC)鈴村
 * </pre>
 */
public class UKG01016Action extends BaseAction {

    /** クラスのシリアル化ID */
    private static final long serialVersionUID = -647032445280094226L;
	/** ホームメイト自動受付一覧サービス */
	private IUKG01016Service ukg01016Services = null;
	/** 画面のデータ */
	private Map<String,Object> ukg01016Data = new HashMap<String,Object>();

	/**
	 * [説 明] 画面のデータを取得する。
	 * @return ukg01016Data 画面のデータ
	 */
	public Map<String, Object> getUkg01016Data() {
		return ukg01016Data;
	}

	/**<pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg01016Data 画面のデータ
	 * </pre>
	 */
	public void setUkg01016Data(Map<String, Object> ukg01016Data) {
		this.ukg01016Data = ukg01016Data;
	}

	/**<pre>
	 * [説 明] ホームメイト自動受付一覧サービス対象を設定する。
	 * @param ukg01016Service サービス対象
	 * </pre>
	 */
	public void setUkg01016Services(IUKG01016Service ukg01016Services) {
		this.ukg01016Services = ukg01016Services;
	}

	/**<pre>
	 * [説 明] ホームメイト自動受付一覧処理を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		// 同期フラグを設定する（同期モード）
		setAsyncFlg(true);

        // リクエスト情報を取得する
		HttpServletRequest request = ServletActionContext.getRequest();

        // ログイン情報を取得する
		Map<String, Object> loginInfo = getLoginInfo();

        // セッション情報を取得する
		HttpSession session = request.getSession();

        // ソート順が未設定の場合は初期値を設定する
        if (session.getAttribute(Consts.UKG01016_SORT_INFO) == null) {
            String sortkomoku = "UKETUKE_DATE DESC,UKETUKE_NO DESC";
            session.setAttribute(Consts.UKG01016_SORT_INFO, sortkomoku);
        }

        // SQLパラメータを定義する
		String kaisyacd    = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		String postcd      = request.getParameter("postCd");
		String authorityCd = (String) loginInfo.get(Consts.USER_AUTHORITY_CD);
        String sisyaBlkCd  = (String) loginInfo.get(Consts.SISYA_BLK_CD);
		String sortkomoku  = String.valueOf(session.getAttribute(Consts.UKG01016_SORT_INFO));

		// ホームメイト自動受付一覧データリストを取得する
		Map<String, Object> homemateInfoList = ukg01016Services.getHomemateInfo(kaisyacd, authorityCd, sisyaBlkCd, postcd, sortkomoku);
        ukg01016Data.put("homemateInfoList", dispUketukeNO((List<Map<String, Object>>) homemateInfoList.get("rst_list")));

        ukg01016Data.put("authorityCd", authorityCd);   // ユーザー権限コード
		ukg01016Data.put("postcd"     , postcd);        // 受付店舗コード
        ukg01016Data.put("sortkomoku" , sortkomoku);    // 表示順（ORDER BY句）

		return successForward();
	}

	/** <pre>
	 * [説 明] 受付№を変換する
	 * @return rstHomemateInfo ホームメイト自動受付一覧データ
	 * @throws Exception　処理異常
	 * </pre>
	 */
	private List<Map<String, Object>> dispUketukeNO(List<Map<String, Object>> rstHomemateInfo) {

	    if (rstHomemateInfo != null && rstHomemateInfo.size() > 0) {
			for (Map<String, Object> homemateinfo : rstHomemateInfo) {
				homemateinfo.put("UKETUKE_NO_DISPLAY", DispFormat.getUketukeNo((String) homemateinfo.get("UKETUKE_NO")));
			}
		}
	    return rstHomemateInfo;
    }

	/** <pre>
	 * [説 明] ソート処理
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String sort() throws Exception {

	    // リクエスト情報を取得する
		HttpServletRequest request = ServletActionContext.getRequest();

		// ログイン情報を取得する
		Map<String, Object> loginInfo = getLoginInfo();

		// セッション情報を取得する
		HttpSession session = request.getSession();

		// SQLパラメータを定義する
		String kaisyacd    = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		String postcd      = request.getParameter("postCd");
		String authorityCd = (String)loginInfo.get(Consts.USER_AUTHORITY_CD);
        String sisyaBlkCd  = (String)loginInfo.get(Consts.SISYA_BLK_CD);
		String sortkomoku  = (String)session.getAttribute(Consts.UKG01016_SORT_INFO);

		// ▼ORDER BY句の作成開始▼

        String sortitem    = request.getParameter("sortitem");
        String sortjun     = request.getParameter("sortjun");
        String oldsortjyun = request.getParameter("oldsortjyun");

        // 作成例①
		// デフォルトの状態から受付№の昇順に変更
        // sortkomok  : UKETUKE_DATE DESC,UKETUKE_NO DESC
        // sortitem   : UKETUKE_NO
		// sortjun    : ASC
        // oldsortjyun: DESC

		// 作成例②
		// 作成例①の状態から受付№の降順に変更
		// sortkomok  : UKETUKE_NO ASC,UKETUKE_DATE DESC
        // sortitem   : UKETUKE_NO
        // sortjun    : DESC
        // oldsortjyun: ASC

		// ①oldsort: UKETUKE_NO DESC
        // ②oldsort: UKETUKE_NO ASC
        String oldsort = sortitem + " " + oldsortjyun;

        // ①newsort: UKETUKE_NO ASC
        // ②newsort: UKETUKE_NO DESC
        String newsort = sortitem + " " + sortjun;

		if (sortkomoku.endsWith(oldsort)) {
            // ①oldsort: ,UKETUKE_NO DESC
			oldsort = "," + oldsort;
		} else if (sortkomoku.startsWith(oldsort)) {
            // ②oldsort: UKETUKE_NO ASC,
			oldsort = oldsort + ",";
		}

        // ①sortkomoku: UKETUKE_DATE DESC
		// ②sortkomoku: UKETUKE_DATE DESC
		sortkomoku = sortkomoku.replace(oldsort, "").replace(",,", ",");

        // ①strbuildsort: UKETUKE_DATE DESC
		// ②strbuildsort: UKETUKE_DATE DESC
		StringBuilder strbuildsort = new StringBuilder(sortkomoku);

        // ①newsort: UKETUKE_NO ASC,
		// ②newsort: UKETUKE_NO DESC,
		newsort = newsort + ",";

        // ①strbuildsort: UKETUKE_NO ASC,UKETUKE_DATE DESC
        // ②strbuildsort: UKETUKE_NO DESC,UKETUKE_DATE DESC
		strbuildsort.insert(0, newsort);

        // ▲ORDER BY句の作成終了▲

		// 表示順をセッション情報に設定する
		session.setAttribute(Consts.UKG01016_SORT_INFO, strbuildsort.toString());

        // ホームメイト自動受付一覧データリストを取得する
		Map<String, Object> homemateInfoList = ukg01016Services.getHomemateInfo(kaisyacd, authorityCd, sisyaBlkCd, postcd, strbuildsort.toString());
        ukg01016Data.put("homemateInfoList", dispUketukeNO((List<Map<String, Object>>) homemateInfoList.get("rst_list")));

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
		session.removeAttribute(Consts.UKG01016_SORT_INFO);
		return "closePage";
	}
}
