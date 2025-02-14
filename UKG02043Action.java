/**
 * @システム名: 受付システム
 * @ファイル名: UKG02043Action.java
 * @更新日付：: 2012/03/06
 * @Copyright: 2012 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.exception.DataNotFoundException;
import jp.co.token.uketuke.service.IUKG02043Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 追客アラーム通知設定
 * [説 明] 追客アラーム通知設定登録を行う。
 * @author [作 成] 2012/03/05 馮強華(SYS)
 * </pre>
 */
public class UKG02043Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -6043451048857858500L;

	/** 追客アラーム通知設定サービス */
	private IUKG02043Service UKG02043Services;

	/** 追客アラーム通知設定用データ */
	private Map<String, Object> ukg02043Data = null;

	// 追客アラーム通知日
	private String alarmdate = null;

	// 追客アラームメモ
	private String alarmmemo = null;

	// 排他時間
	private String haitadate = null;

	// エラーメッセージ
	private String errMsg = null;

	// 過去日付チェック
	private String checkNowdate = null;

	/**
	 * 追客アラーム通知設定サービス
	 * 
	 * @param services
	 */
	public void setUKG02043Services(IUKG02043Service services) {

		UKG02043Services = services;
	}

	/**
	 * 追客アラーム通知設定用データ
	 * 
	 * @return
	 */
	public Map<String, Object> getUkg02043Data() {

		return ukg02043Data;
	}

	/**
	 * 追客アラーム通知設定用データ
	 * 
	 * @param ukg02043Data
	 */
	public void setUkg02043Data(Map<String, Object> ukg02043Data) {

		this.ukg02043Data = ukg02043Data;
	}

	/**
	 * 追客アラーム通知日
	 * 
	 * @return
	 */
	public String getAlarmdate() {

		return alarmdate;
	}

	/**
	 * 追客アラーム通知日
	 * 
	 * @param alarmdate
	 */
	public void setAlarmdate(String alarmdate) {

		this.alarmdate = alarmdate;
	}

	/**
	 * 追客アラームメモ
	 * 
	 * @return
	 */
	public String getAlarmmemo() {

		return alarmmemo;
	}

	/**
	 * 追客アラームメモ
	 * 
	 * @param alarmmemo
	 */
	public void setAlarmmemo(String alarmmemo) {

		this.alarmmemo = alarmmemo;
	}

	/**
	 * 排他時間
	 * 
	 * @return
	 */
	public String getHaitadate() {

		return haitadate;
	}

	/**
	 * 排他時間
	 * 
	 * @param haitadate
	 */
	public void setHaitadate(String haitadate) {

		this.haitadate = haitadate;
	}

	/**
	 * エラーメッセージ
	 * 
	 * @return
	 */
	public String getErrMsg() {

		return errMsg;
	}

	/**
	 * エラーメッセージ
	 * 
	 * @param errMsg
	 */
	public void setErrMsg(String errMsg) {

		this.errMsg = errMsg;
	}

	/**
	 * システム日付
	 * 
	 * @return
	 */
	public String getCheckNowdate() {

		return checkNowdate;
	}

	/**
	 * <pre>
	 * [説 明] 初期処理。
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();

		// 会社コードを取得
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 受付番号
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));

		Map<String, Object> result = UKG02043Services.initAlarmInfo(kaisyaCd, uketukeNo);

		// 検索用来店受付データ
		List<Map<String, Object>> alarmInfo = (List<Map<String, Object>>) result.get("rst_alarinfo");
		if (alarmInfo == null || alarmInfo.size() == 0) {
			// データが取得できた場合
			throw new DataNotFoundException();
		} else {
			// データが取得できなかった場合
			for (Map<String, Object> x : alarmInfo) {
				alarmdate = Util.toEmpty(String.valueOf(x.get("TUIKYAKU_ALARM_DATE")));
				alarmmemo = Util.toEmpty(String.valueOf(x.get("TUIKYAKU_ALARM_NAIYOU")));
				haitadate = String.valueOf(x.get("UPD_DATE"));
			}
		}

		// 現在日時取得
		checkNowdate = Util.getSysDateYMD();

		return successForward();
	}

	/**
	 * アラーム通知情報の登録処理
	 * 
	 * @return
	 * @throws Exception
	 */
	public String updAlarmInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();

		HttpSession session = request.getSession();
		// 受付番号
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));

		Map<String, Object> loginInfo = getLoginInfo();
		// 担当者コード
		String tantousya = String.valueOf(loginInfo.get(Consts.USER_CD));
		String kaisyaCD = String.valueOf(loginInfo.get(Consts.KAISYA_CD));

		Map<String, Object> result = UKG02043Services.updAlarmInfo(kaisyaCD, tantousya, uketukeNo, alarmdate,
		                                                           alarmmemo, haitadate);

		errMsg = "";
		if (Consts.ERRS009.equals(result.get("rst_code"))) {
			// 排他エラーメッセージフラグ
			errMsg = Util.getServerMsg(Consts.ERRS009, "追客アラーム通知情報");
		} else if (Consts.ERRS002.equals(result.get("rst_code"))) {
			// 失敗エラーメッセージフラグ
			errMsg = Util.getServerMsg(Consts.ERRS002, "追客アラーム通知情報の登録");
		} else {
			haitadate = String.valueOf(result.get("rst_haitadate"));
		}

		return "updAlarmInfo";
	}
}
