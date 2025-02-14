/**
 *@システム名: 受付システム
 *@ファイル名: UKG02020Action.java
 *@更新日付：: 2011/12/26
 *@Copyright: 2011 token corporation All right reserved
 * 更新履歴： 2014/06/10	郭凡(SYS)       1.03  (案件番号C38117)セールスポイント編集機能の追加
 */
package jp.co.token.uketuke.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.DispFormat;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.exception.DataNotFoundException;
import jp.co.token.uketuke.formbean.AnketoDispData;
import jp.co.token.uketuke.formbean.AnketoDispDataBig;
import jp.co.token.uketuke.service.IUKG02020Service;
import org.apache.struts2.ServletActionContext;
/**<pre>
 * [機 能] アンケート初期化
 * [説 明] アンケート初期化
 * @author [作 成] 2011/10/22 A.Wanibe(SYS)
 * </pre>
 */
public class UKG02020Action extends BaseAction {
	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 6045632979053562959L;

	/** アンケート用画面のデータ */
	private Map<String, Object> ukg02020Data = null;

	/** アンケートサービス */
	private IUKG02020Service UKG02020Services;

	/**
	 * サービス設定
	 *
	 * @param uKG02020Services
	 */
	public void setUKG02020Services(IUKG02020Service uKG02020Services) {
		UKG02020Services = uKG02020Services;
	}

	/**
	 * 画面データ取得
	 *
	 * @return　画面データ
	 */
	public Map<String, Object> getUkg02020Data() {
		return ukg02020Data;
	}

	/**
	 * 画面データ設定
	 *
	 * @param ukg02020Data
	 */
	public void setUkg02020Data(Map<String, Object> ukg02020Data) {
		this.ukg02020Data = ukg02020Data;
	}

	/**
	 * <pre>
	 * [説 明]アンケート画面初期化処理
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		// 同期フラグ設定
		setAsyncFlg(true);
		ukg02020Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession sessionInfo = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();
		// 受付No.
		String uketukeNo;
		// 遷移元画面を判定uketukeNo
		if ("UKG01011".equals(request.getParameter("historyPageId"))) {
			// 遷移前画面が受付未登録一覧
			uketukeNo = request.getParameter("uketukeNo");
			ukg02020Data.put("beforeGamen", "UKG01011");

			// セッションに受付未登録一覧遷移フラグを設定する。
			sessionInfo.setAttribute(Consts.UKETUKENO, uketukeNo);
			sessionInfo.setAttribute(Consts.UKG01011_FLG, "UKG01011");
		} else {
			uketukeNo = String.valueOf(sessionInfo.getAttribute(Consts.UKETUKENO));
			ukg02020Data.put("beforeGamen", "");
		}

		if ("UKG01011".equals(sessionInfo.getAttribute(Consts.UKG01011_FLG))) {
			ukg02020Data.put("beforeGamen", "UKG01011");
		}
		//########## 2017/08/16 MJEC)鈴村 ADD Start (案件C38101-1)ホームメイト自動受付対応
		// ホームメイト自動受付一覧フラグがonの場合、セッション情報を初期化する。
		if (sessionInfo.getAttribute(Consts.UKG01016_FLG) != null) {
			sessionInfo.setAttribute(Consts.UKG01016_FLG, null);
		}
		//########## 2017/08/16 MJEC)鈴村 ADD End
		// セッション情報取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));

		// 会社コードと受付Noをセッションに設定する。
		ukg02020Data.put("kaisyaCd", kaisyaCd);
		ukg02020Data.put("uketukeNo", uketukeNo);

		// アンケートデータ取得
		Map<String, Object> anketoData = UKG02020Services.getAnketoData(
				uketukeNo, kaisyaCd);
		// お客様情報取得
		Map<String, Object> kokyakuData = UKG02020Services.getKokyakuData(
				uketukeNo, kaisyaCd);
		// 顧客データ取得
		List<HashMap<String, Object>> kok = (List<HashMap<String, Object>>) kokyakuData.get("rst_record");
		// 顧客データが取得できない場合
		if (kok.size() == 0) {
			throw new DataNotFoundException();
		}
		// 顧客データ設定
		kok.get(0).put("UKETUKENO", DispFormat.getUketukeNo(kok.get(0).get("UKETUKENO").toString()));
		ukg02020Data.put("kokyakuData", kok.get(0));
		// アンケートデータ取得
		List<Map<String, Object>> anketoDbData = (List<Map<String, Object>>) anketoData
				.get("rst_record");
		// アンケートデータ整形
		List<AnketoDispDataBig> dispAnketo = UKG02020Services
				.getDispAnketo(anketoDbData);
		// アンケートデータ（保存用）作成
		ArrayList<AnketoDispData> beforeAnketo = (ArrayList<AnketoDispData>) UKG02020Services
				.getBeforeAnketo(dispAnketo);
		// アンケートデータ取得
		ukg02020Data.put("anketoData", dispAnketo);
		// ########## 2014/06/10 郭凡 ADD Start (案件C38117)セールスポイント編集機能の追加
		// ログイン情報
		Map<String, Object> sessionLoginInfo = getLoginInfo();
		String authorityCd = (String) sessionLoginInfo.get(Consts.USER_AUTHORITY_CD);
		ukg02020Data.put("authorityCd", authorityCd);
		// ########## 2014/06/10 郭凡 ADD End
		// アンケートデータをセッションに保存
		sessionInfo.setAttribute(Consts.UKG02020_ANKETO_DATA, beforeAnketo);
		// 顧客情報をセッションに保存
		sessionInfo.setAttribute(Consts.UKG02020_KOKYAKU_DATA, kok.get(0));

		return successForward();
	}

	/**
	 * 登録ボタン押下処理 save()
	 *
	 * @throws Exception
	 *             一般例外
	 */
	public String save() throws Exception {
		//処理成功フラグ　true 成功　false 失敗
		boolean successFlg = true;
		ukg02020Data = new HashMap<String, Object>();

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession sessionInfo = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();

		// リクエストデータを成型する。
		Map<String, Object> a = UKG02020Services.createRequestData(request
				.getParameter("requestString"));


		@SuppressWarnings("unchecked")
		ArrayList<AnketoDispData> returnAnketo = (ArrayList<AnketoDispData>) sessionInfo
				.getAttribute(Consts.UKG02020_ANKETO_DATA);

		@SuppressWarnings("unchecked")
		Map<String, Object> kokyakuData = (Map<String, Object>) sessionInfo
				.getAttribute(Consts.UKG02020_KOKYAKU_DATA);

		// アンケートフラグ取得
		String tourokuFlgAnketo;
		if (sessionInfo.getAttribute(Consts.UKG02020_ANKETO_FLG) != null) {
			tourokuFlgAnketo = (String) sessionInfo.getAttribute(Consts.UKG02020_ANKETO_FLG);
		} else {
			tourokuFlgAnketo = (String) kokyakuData.get("ANKETOFLG");
		}

		// 顧客情報フラグ取得
		String tourokuFlgKokyaku = (String) kokyakuData.get("KOKYAKUINFOFLG");
		// 希望条件登録フラグ取得
		String tourokuFlgKibou = (String) kokyakuData.get("KIBOUFLG");

		// セッション情報取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		String tantouCd = String.valueOf(loginInfo.get(Consts.USER_CD));
		String uketukeNo = (String) kokyakuData.get("UKETUKENO");
		uketukeNo = uketukeNo.replaceAll("-", "");
		String message = "";
		// 排他チェック
		if (UKG02020Services.isHaita(returnAnketo, kaisyaCd, uketukeNo)) {
			// 更新処理

			// 挿入用データ作成
			List<Map<String, Object>> insertData = UKG02020Services
					.makeAnketoData(a, returnAnketo, kaisyaCd, uketukeNo,
							tantouCd);
			if (UKG02020Services.setAnketoData(insertData, tourokuFlgAnketo,
					tourokuFlgKibou, tourokuFlgKokyaku)) {
				// 更新成功

				// 更新成功時は登録したデータを画面のデータに設定する。

				UKG02020Services.setAnketoUpdData(insertData, returnAnketo);
				// セッションに情報を登録
				sessionInfo.setAttribute(Consts.UKG02020_ANKETO_DATA, returnAnketo);

				message = Util.getServerMsg("MSGS001", "登録");
			} else {
				// 更新エラー　メッセージ表示
				message = Util.getServerMsg("ERRS002", "更新処理");
				successFlg = false;

			}
		} else {
			// 排他チェックエラー　メッセージ表示
			message = Util.getServerMsg("ERRS003");
			successFlg = false;

		}
		// メッセージをセットする
		ukg02020Data.put("messageID", message);
		ukg02020Data.put("savaSuccess", successFlg);

		return "success_ff";
	}
}
