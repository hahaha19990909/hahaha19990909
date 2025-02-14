/**
 *@システム名: 受付システム
 *@ファイル名: UKG01017Action.java
 *@更新日付：: 2017/07/31
 *@Copyright: 2011 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG01017Service;
import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] ホームメイト自動受付キャンセル理由登録
 * [説 明] ホームメイト自動受付キャンセル理由登録アクション
 * @author [作 成] 2017/07/31 MJEC)鈴村
 * </pre>
 */
public class UKG01017Action extends BaseAction {

    /** クラスのシリアル化ID */
    private static final long serialVersionUID = 4991088497337174540L;

	/** ホームメイト自動受付キャンセル理由登録サービス */
	private IUKG01017Service ukg01017Services = null;

	/** 画面データ保持用 */
	private Map<String, Object> ukg01017Data = new HashMap<String, Object>();

	/** 受付番号 **/
	private String uketukeNo;
	/** 未登録一覧から更新日付 **/
	private String update;
	/** 希望条件入力フラグ **/
	private String kibouFlg;

	/**
	 * 受付番号
	 *
	 * @return 受付番号
	 */
	public String getUketukeNo() {
		return uketukeNo;
	}

	/**
	 * 受付番号
	 *
	 * @param uketukeNo
	 *            受付番号
	 */
	public void setUketukeNo(String uketukeNo) {
		this.uketukeNo = uketukeNo;
	}

	/**
	 * 更新日付
	 *
	 * @return 更新日付
	 */
	public String getUpdate() {
		return update;
	}

	/**
	 * 更新日付
	 *
	 * @param update
	 */
	public void setUpdate(String update) {
		this.update = update;
	}

	/**
	 * 希望条件入力フラグ
	 *
	 * @return
	 */
	public String getKibouFlg() {
		return kibouFlg;
	}

	/**
	 * 希望条件入力フラグ
	 *
	 * @param kibouFlg
	 */
	public void setKibouFlg(String kibouFlg) {
		this.kibouFlg = kibouFlg;
	}

	/**
	 * [説 明] 画面のデータを取得する。
	 *
	 * @return ukg01017Data 画面のデータ
	 */
	public Map<String, Object> getUkg01017Data() {
		return ukg01017Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg01017TempData 画面のデータ
	 * </pre>
	 */
	public void setUkg01017Data(Map<String, Object> ukg01017TempData) {
		ukg01017Data = ukg01017TempData;
	}

	/**
	 * <pre>
	 * [説 明] ホームメイト自動受付キャンセル理由登録サービス対象を設定する。
	 * @param ukg01017Service サービス対象
	 * </pre>
	 */
	public void setUkg01017Services(IUKG01017Service ukg01017Serv) {
		ukg01017Services = ukg01017Serv;
	}

	/**
	 * <pre>
	 * [説 明] ホームメイト自動受付キャンセル理由登録処理を行う。
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
		String uketukeNo = request.getParameter("uketukeNo");
		String update = request.getParameter("updDate");
		String kibouFlg = request.getParameter("kibouFlg");

		// ホームメイト自動受付一覧画面から受け取った値を設定する
		ukg01017Data.put("uketukeNo", uketukeNo);
		ukg01017Data.put("update"   , update);
		ukg01017Data.put("kibouFlg" , kibouFlg);

		// キャンセル理由の選択肢を取得する
		ukg01017Data.put("canclInfo", ukg01017Services.getInitInfo("114"));

		// 選択肢の件数によってHTML要素の高さを計算する
		List<Map<String, Object>> dataList = ukg01017Services.getInitInfo("114");
		int divDetailHeight = (dataList.size() / 2 + dataList.size() % 2) * 28;
		int pageHeight = divDetailHeight + 75;
		ukg01017Data.put("pageHeight", pageHeight);
		ukg01017Data.put("divDetailHeight", divDetailHeight);

		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] ホームメイト自動受付キャンセル理由登録処理を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String cancleInfo() throws Exception {

        // リクエスト情報を取得する
		HttpServletRequest request = ServletActionContext.getRequest();

        // ログイン情報を取得する
		Map<String, Object> loginInfo = getLoginInfo();

		// 登録値を設定する
		String uketukeNo    = request.getParameter("uketukeNo");
		String update       = request.getParameter("updDate");
		String kibouFlg     = request.getParameter("kibouFlg");
		String cancleReason = request.getParameter("cancleReason");

		// 登録処理を実行する
		Map<String, Object> canclInfo = ukg01017Services.upCancleInfo(
			  String.valueOf(loginInfo.get(Consts.KAISYA_CD))
			, uketukeNo.trim()
			, String.valueOf(loginInfo.get(Consts.USER_CD))
			, cancleReason
			, kibouFlg
			, update
		);

		// 更新処理失敗の場合はエラーメッセージを表示する
        String errMsg = "";
		if ("ERRS003".equals(canclInfo.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS003");
		} else if ("ERRS004".equals(canclInfo.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS004", "キャンセル処理");
		} else if ("ERRS999".equals(canclInfo.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS002", "キャンセル処理");
		}
		ukg01017Data.put("errMsg", errMsg);

		return "canclesuccess";
	}
}
