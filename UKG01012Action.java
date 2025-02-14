/** 
 *@システム名: 受付システム 
 *@ファイル名: UKG01012Action.java 
 *@更新日付：: 2011/12/26 
 *@Copyright: 2011 token corporation All right reserved  
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG01012Service;

/**
 * <pre>
 * [機 能] 受付キャンセル理由登録。
 * [説 明] 受付キャンセル理由登録アクション。
 * @author [作 成] 2011/10/25 馮強華(SYS)
 * [修 正] by 馮強華 2012/01/30 bug105対応
 * </pre>
 */
public class UKG01012Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 6045633980257211842L;

	/** 受付キャンセル理由登録サービス */
	private IUKG01012Service ukg01012Services = null;

	/** 画面データ保持用 */
	private Map<String, Object> ukg01012Data = new HashMap<String, Object>();

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
	 * @return ukg01012Data 画面のデータ
	 */
	public Map<String, Object> getUkg01012Data() {
		return ukg01012Data;
	}

	/**
	 * <pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg01012TempData 画面のデータ
	 * </pre>
	 */
	public void setUkg01012Data(Map<String, Object> ukg01012TempData) {
		ukg01012Data = ukg01012TempData;
	}

	/**
	 * <pre>
	 * [説 明] 受付キャンセル理由登録サービス対象を設定する。
	 * @param ukg01011Service サービス対象
	 * </pre>
	 */
	public void setUkg01012Services(IUKG01012Service ukg01012Serv) {
		ukg01012Services = ukg01012Serv;
	}

	/**
	 * <pre>
	 * [説 明] 受付キャンセル理由登録処理を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {
		// 同期フラグ設定
		setAsyncFlg(true);

		HttpServletRequest request = ServletActionContext.getRequest();
		String uketukeNo = request.getParameter("uketukeNo");
		String update = request.getParameter("updDate");
		String kibouFlg = request.getParameter("kibouFlg");

		ukg01012Data.put("uketukeNo", uketukeNo);
		ukg01012Data.put("update", update);
		ukg01012Data.put("kibouFlg", kibouFlg);

		ukg01012Data.put("canclInfo", ukg01012Services.getInitInfo("007"));

		// 36014_bug105対応 2012/01/30 Start by 馮強華
		List<Map<String, Object>> dataList = ukg01012Services.getInitInfo("007");
		int divDetailHeight = (dataList.size() / 2 + dataList.size() % 2) * 28;
		int pageHeight = divDetailHeight + 75;
		ukg01012Data.put("pageHeight", pageHeight);
		ukg01012Data.put("divDetailHeight", divDetailHeight);
		// 36014_bug105対応 2012/01/30 End by 馮強華

		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] 受付キャンセル理由登録処理を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String cancleInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		
		// セッションのログイン情報取得する
		Map<String, Object> loginInfo = getLoginInfo();
		
		String uketukeNo = request.getParameter("uketukeNo");
		String update = request.getParameter("updDate");
		String kibouFlg = request.getParameter("kibouFlg");
		String cancleReason = request.getParameter("cancleReason");
		// 登録処理を実行する
		Map<String, Object> canclInfo = ukg01012Services.upCancleInfo(
				String.valueOf(loginInfo.get(Consts.KAISYA_CD)), uketukeNo.trim(),
				String.valueOf(loginInfo.get(Consts.USER_CD)), cancleReason, kibouFlg, update);

		String errMsg = "";
		// 更新処理失敗の場合
		if ("ERRS003".equals(canclInfo.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS003");
		} else if ("ERRS004".equals(canclInfo.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS004", "キャンセル処理");
		} else if ("ERRS999".equals(canclInfo.get("rst_code"))) {
			errMsg = Util.getServerMsg("ERRS002", "キャンセル処理");
		}

		// エラーメッセージをセットする
		ukg01012Data.put("errMsg", errMsg);

		return "canclesuccess";
	}
}
