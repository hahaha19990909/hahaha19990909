/**
 * @システム名: 受付システム
 * @ファイル名: UKG02041Action.java
 * @更新日付：: 2011/12/26
* 更新履歴: 2014/06/23 趙雲剛(SYS)    1.01 	(案件C38117)セールスポイント編集機能の追加
 * @Copyright: 2011 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.BukkenDataBean;
import jp.co.token.uketuke.service.IUKG02041Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 次回来店・現地案内予約
 * [説 明] 次回来店・現地案内の予約登録を行う。
 * @author [作 成] 2011/11/14 戴氷馨(SYS)
 * [修 正] by 趙 2012/3/29 STEP2対応
 * </pre>
 */
public class UKG02041Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 668425562698478139L;

	/** 次回来店・現地案内予約サービス */
	private IUKG02041Service UKG02041Services;

	/** 案内区分 */
	private String annaiFlg;

	/** 予約日 */
	private String yoyakuDate;

	/** 予約時間 */
	private String yoyakuTime;

	/** 現地案内物件№ */
	private String bukkenNo;

	/** 現地案内部屋№ */
	private String heyaNo;

	/** 現地案内予約SEQ */
	private String seq;

	/** 物件/部屋リスト */
	private List<BukkenDataBean> bukennHayaList;

	/** 更新モード **/
	private String updateMode;

	/** 予約状況データ */
	private List<Map<String, Object>> yoyakuList;

	/** エラーメッセージフラグ */
	private String checkFlag;

	/** エラーメッセージ内容 */
	private String errorContent;

	/**
	 * <pre>
	 * [説 明] 初期処理。
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();

		if (!Util.isEmpty(heyaNo) && "ISO-8859-1".equals(Util.getEncoding(heyaNo))) {
			heyaNo = new String(heyaNo.getBytes("ISO-8859-1"), "UTF-8");
		}
		
		// 会社コードを取得
		String kaisyaCD = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 仲介担当者
		String chukaitantou = String.valueOf(session.getAttribute(Consts.CHUKAITANTOU));
		String uketukeno = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		if ("2".equals(annaiFlg) && "1".equals(updateMode)) {

			List<Map<String, Object>> annaiDate = UKG02041Services.getAnnaiInitData(kaisyaCD, uketukeno);
			if (annaiDate != null && annaiDate.size() > 0) {
				yoyakuDate = (String) annaiDate.get(0).get("YOYAKU_DATE");
				yoyakuTime = (String) annaiDate.get(0).get("YOYAKU_TIME");
			}
		}
			
		if (!Util.isEmpty(yoyakuDate)) {

			// 予約状況データを取得する
			yoyakuList = UKG02041Services.getYoyakuInfo(kaisyaCD, yoyakuDate, chukaitantou);
		}

		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] 予約状況データを取得する。
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	public String getYoyakuInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();

		// 会社コードを取得
		String kaisyaCD = String.valueOf(loginInfo.get(Consts.KAISYA_CD));

		// 仲介担当者
		String chukaitantou = String.valueOf(session.getAttribute(Consts.CHUKAITANTOU));

		// 予約状況データを取得する
		yoyakuList = UKG02041Services.getYoyakuInfo(kaisyaCD, yoyakuDate, chukaitantou);

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 予約データ追加処理
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String addYoyakuInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();
		Map<String, Object> resData = null;

		// 会社コードを取得
		String kaisyaCD = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 受付番号
		String uketukeNo = (String) session.getAttribute(Consts.UKETUKENO);
		// ########## 2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
		// 店舗コード
		//String postCd = (String.valueOf(loginInfo.get(Consts.POST_CD)));
		// セッションの受付店舗情報取得する
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		String postCd = String.valueOf(uketukePostInfo.get(Consts.POST_CD));
		// ########## 2014/06/23 趙雲剛 UPD End
		// 仲介担当者コード
		String chukaitantou = String.valueOf(session.getAttribute(Consts.CHUKAITANTOU));
		// 担当者コード
		String userCD = String.valueOf(loginInfo.get(Consts.USER_CD));
		if (!"1".equals(checkFlag)) {
			// 同一時間の予約チェック
			boolean checkResult = UKG02041Services.checkYoyakuTime(kaisyaCD, yoyakuDate + " " + yoyakuTime,
			                                                       chukaitantou);
			if (!checkResult) {

				checkFlag = "MSGS002";
				errorContent = Util.getServerMsg("MSGS002", "すでに別の予約が登録されています。");
				return "ajaxProcess";
			}
		}

		if ("1".equals(annaiFlg)) {
			// 予約データ追加処理
			resData = UKG02041Services.addYoyakuInfo(kaisyaCD, uketukeNo, yoyakuDate + " " + yoyakuTime, postCd,
			                                         chukaitantou, userCD);
		} else if ("2".equals(annaiFlg)) {
			// 現地案内データ追加処理

			bukennHayaList = (List<BukkenDataBean>) session.getAttribute(Consts.HAYA_LIST_DATA);
			resData = UKG02041Services.addAnnaiInfo(kaisyaCD, uketukeNo, updateMode, yoyakuDate + " " + yoyakuTime,
			                                        bukkenNo, heyaNo, seq, bukennHayaList, postCd, chukaitantou, userCD);
			seq = (String) resData.get("pv_seq");
		}
		// 処理結果を処理する
		return resultProcess(resData, session);
	}

	/**
	 * <pre>
	 * [説 明] 予約データ削除処理
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String delYoyakuInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		Map<String, Object> loginInfo = getLoginInfo();
		Map<String, Object> resData = null;

		// 会社コードを取得
		String kaisyaCD = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 受付番号
		String uketukeNo = (String) session.getAttribute(Consts.UKETUKENO);

		if ("1".equals(annaiFlg)) {
			// 予約データ削除処理
			resData = UKG02041Services.delYoyakuInfo(kaisyaCD, uketukeNo);
		} else if ("2".equals(annaiFlg)) {
			// 現地案内データ削除処理

			bukennHayaList = (List<BukkenDataBean>) session.getAttribute(Consts.HAYA_LIST_DATA);
			resData = UKG02041Services.delAnnaiInfo(kaisyaCD, uketukeNo, updateMode, bukkenNo, heyaNo, seq,
			                                        bukennHayaList);
			seq = null;
		}
		// 処理結果を処理する
		return resultProcess(resData, session);
	}

	/**
	 * <pre>
	 * [説 明] 結果を処理する
	 * @param resData 処理結果
	 * @param session セッション
	 * @return アクション処理結果
	 * </pre>
	 */
	private String resultProcess(Map<String, Object> resData,
	                             HttpSession session) {
		if (resData != null) {
			if ("0000000".equals(resData.get("rst_code"))) {
				session.getAttribute("BUKKEN_HEYA_LIST");
			} else {
				if ("ERRS002".equals(resData.get("rst_code"))) {
					checkFlag = "ERRS002";
					errorContent = Util.getServerMsg("ERRS002", "更新処理");
				} else {
					checkFlag = (String) resData.get("rst_code");
					errorContent = Util.getServerMsg((String) resData.get("rst_code"));
				}
			}
		}
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] サービス対象を設定する。
	 * @param services サービス対象
	 * </pre>
	 */
	public void setUKG02041Services(IUKG02041Service services) {

		UKG02041Services = services;
	}

	/**
	 * <pre>
	 * [説 明] 案内区分を取得する。
	 * @return 案内区分
	 * </pre>
	 */
	public String getAnnaiFlg() {

		return annaiFlg;
	}

	/**
	 * <pre>
	 * [説 明] 案内区分を設定する。
	 * @param yoyakuJissiFlag 案内区分
	 * </pre>
	 */
	public void setAnnaiFlg(String annaiFlg) {

		this.annaiFlg = annaiFlg;
	}

	/**
	 * <pre>
	 * [説 明] 予約日を取得する。
	 * @return  予約日
	 * </pre>
	 */
	public String getYoyakuDate() {

		return yoyakuDate;
	}

	/**
	 * <pre>
	 * [説 明] 予約日を設定する。
	 * @param yoyakuDate 予約日
	 * </pre>
	 */
	public void setYoyakuDate(String yoyakuDate) {

		this.yoyakuDate = yoyakuDate;
	}

	/**
	 * <pre>
	 * [説 明] 予約時間を取得する。
	 * @return  予約時間
	 * </pre>
	 */
	public String getYoyakuTime() {

		return yoyakuTime;
	}

	/**
	 * <pre>
	 * [説 明] 予約時間を設定する。
	 * @param yoyakuTime 予約時間
	 * </pre>
	 */
	public void setYoyakuTime(String yoyakuTime) {

		this.yoyakuTime = yoyakuTime;
	}

	/**
	 * <pre>
	 * [説 明] 現地案内物件№を取得する。
	 * @return 現地案内物件№
	 * </pre>
	 */
	public String getBukkenNo() {

		return bukkenNo;
	}

	/**
	 * <pre>
	 * [説 明] 現地案内物件№を設定する。
	 * @param yoyakuList 現地案内物件№
	 * </pre>
	 */
	public void setBukkenNo(String bukkenNo) {

		this.bukkenNo = bukkenNo;
	}

	/**
	 * <pre>
	 * [説 明] 現地案内部屋№を取得する。
	 * @return 現地案内部屋№
	 * </pre>
	 */
	public String getHeyaNo() {

		return heyaNo;
	}

	/**
	 * <pre>
	 * [説 明] 現地案内部屋№を設定する。
	 * @param yoyakuList 現地案内部屋№
	 * </pre>
	 */
	public void setHeyaNo(String heyaNo) {

		this.heyaNo = heyaNo;
	}

	/**
	 * <pre>
	 * [説 明] 現地案内予約SEQを取得する。
	 * @return 現地案内予約SEQ
	 * </pre>
	 */
	public String getSeq() {

		return seq;
	}

	/**
	 * <pre>
	 * [説 明] 現地案内予約SEQを設定する。
	 * @param seq　現地案内予約SEQ
	 * </pre>
	 */
	public void setSeq(String seq) {

		this.seq = seq;
	}

	/**
	 * <pre>
	 * [説 明] 物件/部屋リストを取得する。
	 * @return 物件/部屋リスト
	 * </pre>
	 */
	public List<BukkenDataBean> getBukennHayaList() {

		return bukennHayaList;
	}

	/**
	 * <pre>
	 * [説 明] 物件/部屋リストを設定する。
	 * @param bukennHayaList　物件/部屋リスト
	 * </pre>
	 */
	public void setBukennHayaList(List<BukkenDataBean> bukennHayaList) {

		this.bukennHayaList = bukennHayaList;
	}

	/**
	 * <pre>
	 * [説 明] 更新モードを取得する。
	 * @return 更新モード
	 * </pre>
	 */
	public String getUpdateMode() {

		return updateMode;
	}

	/**
	 * <pre>
	 * [説 明] 更新モードを設定する。
	 * @param updateMode 更新モード
	 * </pre>
	 */
	public void setUpdateMode(String updateMode) {

		this.updateMode = updateMode;
	}

	/**
	 * <pre>
	 * [説 明] 予約状況データを取得する。
	 * @return 予約状況データ
	 * </pre>
	 */
	public List<Map<String, Object>> getYoyakuList() {

		return yoyakuList;
	}

	/**
	 * <pre>
	 * [説 明] 予約状況データを設定する。
	 * @param yoyakuList 予約状況データ
	 * </pre>
	 */
	public void setYoyakuList(List<Map<String, Object>> yoyakuList) {

		this.yoyakuList = yoyakuList;
	}

	/**
	 * <pre>
	 * [説 明] エラーメッセージフラグを取得する。
	 * @return エラーメッセージフラグ
	 * </pre>
	 */
	public String getCheckFlag() {

		return checkFlag;
	}

	/**
	 * <pre>
	 * [説 明] エラーメッセージフラグを設定する。
	 * @param checkFlag エラーメッセージフラグ
	 * </pre>
	 */
	public void setCheckFlag(String checkFlag) {

		this.checkFlag = checkFlag;
	}

	/**
	 * <pre>
	 * [説 明] エラーメッセージ内容を取得する。
	 * @return エラーメッセージ内容
	 * </pre>
	 */
	public String getErrorContent() {

		return errorContent;
	}

	/**
	 * <pre>
	 * [説 明] エラーメッセージ内容を設定する。
	 * @param errorContent エラーメッセージ内容
	 * </pre>
	 */
	public void setErrorContent(String errorContent) {

		this.errorContent = errorContent;
	}
}
