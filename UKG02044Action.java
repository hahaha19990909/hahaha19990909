/**
 * @システム名: オンライン仲介システム
 * @ファイル名: UKG02044Action.java
 * @更新日付：: 2021/04/14
 * @Copyright: 2021 token corporation All right reserved
 */
package jp.co.token.uketuke.action;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.UnicodeInputStream;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.BukkenDataBean;
import jp.co.token.uketuke.formbean.OnlineMailBean;
import jp.co.token.uketuke.service.IUKG02044Service;
import net.sf.json.JSONObject;

/**
 * <pre>
 * [機 能] オンライン仲介予約
 * [説 明] オンライン仲介予約の予約登録を行う。
 * @author [作 成] 2021/04/14 楊振華(SYS)
 * </pre>
 */
public class UKG02044Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 668425562698478139L;

	/** 次回来店・現地案内予約サービス */
	private IUKG02044Service UKG02044Services;

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

	public static String subject = "";

    public static String mailText = "";


	public static String fromAddress = "";

    public static String toAddress = "";

    public static String ccAddress = "";

    public static String bccAddress = "";

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
		if ("2".equals(updateMode)) {

			List<Map<String, Object>> annaiDate = UKG02044Services.getAnnaiInitData(kaisyaCD, uketukeno);
			if (annaiDate != null && annaiDate.size() > 0) {
				yoyakuDate = (String) annaiDate.get(0).get("YOYAKU_DATE");
				yoyakuTime = (String) annaiDate.get(0).get("YOYAKU_TIME");
				session.setAttribute("yoyakuDateOld", yoyakuDate + " " + yoyakuTime + ":00");
			}
		}

		if (!Util.isEmpty(yoyakuDate)) {

			// 予約状況データを取得する
			yoyakuList = UKG02044Services.getYoyakuInfo(kaisyaCD, yoyakuDate, chukaitantou);
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
		yoyakuList = UKG02044Services.getYoyakuInfo(kaisyaCD, yoyakuDate, chukaitantou);

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 予約データ追加処理
	 * @return アクション処理結果
	 * @throws Exception 処理異常
	 * </pre>
	 */
	public String addYoyakuInfo() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		// 修正前オンライン仲介予約日
		String yoyakuDateOld = (String)session.getAttribute("yoyakuDateOld");
		Map<String, Object> loginInfo = getLoginInfo();
		Map<String, Object> resData = null;
		// オンライン仲介予約日
		String yoyakuDateDayTime = yoyakuDate + " " + yoyakuTime + ":00";
		// 送信オンライン仲介予約日
		String mailDate = getWeek(yoyakuDate, yoyakuTime);

		// 会社コードを取得
		String kaisyaCD = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 受付番号
		String uketukeNo = (String) session.getAttribute(Consts.UKETUKENO);
		// 店舗コード
		// セッションの受付店舗情報取得する
		Map<String, Object> uketukePostInfo = getUketukePostInfo();
		String postCd = String.valueOf(uketukePostInfo.get(Consts.POST_CD));
		// 仲介担当者コード
		String chukaitantou = String.valueOf(session.getAttribute(Consts.CHUKAITANTOU));

		OnlineMailBean bean = UKG02044Services.getOnlineMail(kaisyaCD, uketukeNo, chukaitantou, postCd);
		if (!"1".equals(checkFlag)) {
            // 同一時間の予約チェック
            boolean checkResult = UKG02044Services.checkYoyakuTime(kaisyaCD, yoyakuDate + " " + yoyakuTime,
                                                                   chukaitantou);
            if (!checkResult) {

                checkFlag = "MSGS002";
                errorContent = Util.getServerMsg("MSGS002", "すでに別の予約が登録されています。");
                return "ajaxProcess";
            }
		}
		// メール送信情報Bean.お客様メールアドレスがNULLの場合、ダイアログでメッセージを表示して処理を中断する。
		if (Util.isEmpty(bean.getKokyakuMail())) {
		    checkFlag = "ERRC001";
		    errorContent = Util.getServerMsg("ERRC001", "お客様メールアドレス");
		    return "ajaxProcess";
		}
		// メール送信情報Bean.お客様名がNULLの場合、ダイアログでメッセージを表示して処理を中断する。
		if (Util.isEmpty(bean.getKokyakuName())) {
		    checkFlag = "ERRC001";
            errorContent = Util.getServerMsg("ERRC001", "お客様名");
            return "ajaxProcess";
        }

		String accessKey = "";
		if ("2".equals(updateMode)) {
		    accessKey = UKG02044Services.getAccessKey(kaisyaCD, uketukeNo, yoyakuDateOld);
		}

		// ビデオ通話URL生成APIを呼び出す
		String kaisyaCdChange = kaisyaCD;
		if ("0001".equals(kaisyaCD)) {
		    kaisyaCdChange = "10";
        }
		String jsonStr = "{\"GYOUMU_ID\" : \"TKM01010\",\"USER_ID\" : \"" + chukaitantou + "\", \"START_DATE\" : \"" + yoyakuDateDayTime + "\", \"ROOM_ID\" : \"" + accessKey + "\", \"KAISYA_CD\" : \"" +
		        kaisyaCdChange + "\", \"KOKYAKU_CD\" : \"" + uketukeNo + "\", \"PASSWORD\" : \"\", \"KOKYAKU_NAME\" : \"" + bean.getKokyakuName() + "\"}";
		URL url = new URL(PropertiesReader.getIntance().getProperty("online.meeting.api.url"));
		String api = Util.callApiByPost(jsonStr, url);
		JSONObject json = JSONObject.fromObject(api);
		if ("00000".equals(json.getString("code"))) {
		    String roomId = json.getString("roomId");
	        String tokenOnlineUrl = json.getString("tokenOnlineUrl");
	        tokenOnlineUrl += "&mode=2&userid=" + chukaitantou;
	        String customerOnlineUrl = json.getString("customerOnlineUrl");
	        customerOnlineUrl += "&mode=1";
	        // (新規)の場合
	        if ("1".equals(updateMode)) {
	            resData = UKG02044Services.addYoyakuInfo(kaisyaCD, uketukeNo, yoyakuDateDayTime, roomId, tokenOnlineUrl, postCd,
	                    chukaitantou);
	        } else if ("2".equals(updateMode)) { // (更新)の場合
	            resData = UKG02044Services.updYoyakuInfo(kaisyaCD, uketukeNo, yoyakuDateOld, yoyakuDateDayTime, tokenOnlineUrl,
	                                                  chukaitantou);
	        }

	        if ("0000000".equals(resData.get("rst_code"))) {
	            // 送信処理を呼び出す
	            if (!Util.isEmpty(bean.getKokyakuMail())) {
	                soushinMeruKokyaku(bean.getKokyakuName(), bean.getPostName(), mailDate, bean.getTantouName(), bean.getPostTel(), bean.getKokyakuMail(), customerOnlineUrl);
	            }
	            if (!Util.isEmpty(bean.getTyukaiMail())) {

	                soushinMeruTyukai(bean.getKokyakuName(), mailDate, tokenOnlineUrl, bean.getTyukaiMail());
	            }
	        }
		} else {
		    checkFlag = "ERRS002";
            errorContent = Util.getServerMsg("ERRS002", "ビデオ通話URL生成API連携");
            return "ajaxProcess";
		}

		// 処理結果を処理する
		return resultProcess(resData, session);
	}

	/**
	 * 送信オンライン仲介予約日を取得
	 * @param yoyakuDate 予約時間
	 * @return
	 * @throws ParseException
	 */
	public String getWeek(String yoyakuDate, String yoyakuTime) throws ParseException {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
	     Date date = formatter.parse(yoyakuDate);
	     String[] weekDays = { "(日)", "(月)", "(火)", "(水)", "(木)", "(金)", "(土)" };
	     Calendar cal = Calendar.getInstance();
	     cal.setTime(date);
	     int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
	     if (w < 0)
	         w = 0;
	     String yoyaku = "";
	     String[] yyyymmdd = yoyakuDate.split("/");
	     yoyaku = yyyymmdd[0] + "年";
	     yoyaku += yyyymmdd[1] + "月";
	     yoyaku += yyyymmdd[2] + "日";
	     yoyaku += " " + weekDays[w];
	     yoyaku += "　" + yoyakuTime;
	    return yoyaku;
	}

	/**
	 * お客様メール送信
	 * @param kokyakuName 仲介部員メール送信
	 * @param postName 仲介店舗名
	 * @param uketuke_Date 仲介店舗名
	 * @param tantouName 仲介店舗名
	 * @param postTel 仲介店舗名
	 * @param kokyakuMail お客様メールアドレス
	 * @param phoneUrl お客様メールアドレス
	 * @throws Exception
	 */
    public void soushinMeruKokyaku(String kokyakuName, String postName, String uketuke_Date, String tantouName,  String postTel, String kokyakuMail, String phoneUrl) throws Exception {

        // メールの送信元アドレス
        String mailSMTP = PropertiesReader.getIntance().getProperty("mailSMTP");
        // メール送信元のポート
        String mailPORT = PropertiesReader.getIntance().getProperty("mailPORT");
        // 送信元アドレス
        String sendAddress = PropertiesReader.getIntance().getProperty("sendAddress");
        // メールの送信元の認証パスワード
        String sendPassword = PropertiesReader.getIntance().getProperty("sendPassword");
        // お客様メール本文
        String onlineKokyakuMailPath = PropertiesReader.getIntance().getProperty("onlineKokyakuMailPath");


        Transport transport = null;
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", mailSMTP);
        props.put("mail.smtp.port", mailPORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.checkserveridentity", "false");
        props.put("mail.smtp.ssl.trust", mailSMTP);
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);

            try {
                // メールを設定
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(sendAddress));
                msg.setRecipients(Message.RecipientType.BCC, kokyakuMail);
                msg.setSubject(MimeUtility.encodeText(PropertiesReader.getIntance().getProperty("online.mail.title"), "UTF-8", "B"));
                msg.setSentDate(new Date());

                // メール内容を設定
                MimeBodyPart mbp1 = new MimeBodyPart();
                InputStreamReader frd = new InputStreamReader(new UnicodeInputStream(new FileInputStream(onlineKokyakuMailPath), "UTF-8"), "UTF-8");
                BufferedReader bfr = new BufferedReader(frd);
                StringBuffer sb = new StringBuffer();
                String str = "";
                while ((str = bfr.readLine()) != null) {
                    sb.append(str);
                    sb.append("\n");
                }
                String msgContent = sb.toString().replace("{KOKYAKU_NAME}", kokyakuName).replace("{UKETUKE_DATE}", uketuke_Date).replace("{URL}", phoneUrl)
                        .replace("{POST_NAME}", postName).replace("{POST_TEL}", postTel).replace("{TANTOU_NAME}", tantouName);
                mbp1.setText(msgContent, "iso-2022-jp");
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(mbp1);
                msg.setContent(mp);

                // メールを送信
                transport = session.getTransport("smtp");
                if ("true".equals(props.get("mail.smtp.auth"))) {
                    transport.connect(mailSMTP, sendAddress, sendPassword);
                } else {
                    transport.connect();
                }
                transport.sendMessage(msg, msg.getAllRecipients());

            } catch (Exception e) {
                throw new Exception(e);
            } finally {
                transport.close();
            }
        }

    /**
     * 仲介部員メール送信
     * @param kokyakuName お客様名
     * @param uketuke_Date 予約時間
     * @param phoneUrl 東建社員用ビデオ通話URL
     * @param tyukaiMail 仲介部員メールアドレス
     * @throws Exception
     */
    public void soushinMeruTyukai(String kokyakuName, String uketuke_Date, String phoneUrl, String tyukaiMail) throws Exception {

        // メールの送信元アドレス
        String mailSMTP = PropertiesReader.getIntance().getProperty("mailSMTP");
        // メール送信元のポート
        String mailPORT = PropertiesReader.getIntance().getProperty("mailPORT");
        // 送信元アドレス
        String sendAddress = PropertiesReader.getIntance().getProperty("sendAddress");
        // メールの送信元の認証パスワード
        String sendPassword = PropertiesReader.getIntance().getProperty("sendPassword");
        // 仲介部員メール本文
        String onlineTyukaiMailPath = PropertiesReader.getIntance().getProperty("onlineTyukaiMailPath");


        Transport transport = null;
        Properties props = System.getProperties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.host", mailSMTP);
        props.put("mail.smtp.port", mailPORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.checkserveridentity", "false");
        props.put("mail.smtp.ssl.trust", mailSMTP);
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);

            try {
                // メールを設定
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom(new InternetAddress(sendAddress));
                msg.setRecipients(Message.RecipientType.BCC, tyukaiMail);
                msg.setSubject(MimeUtility.encodeText(PropertiesReader.getIntance().getProperty("online.mail.title"), "UTF-8", "B"));
                msg.setSentDate(new Date());

                // メール内容を設定
                MimeBodyPart mbp1 = new MimeBodyPart();
                InputStreamReader fr = new InputStreamReader(new UnicodeInputStream(new FileInputStream(onlineTyukaiMailPath), "UTF-8"), "UTF-8");
                BufferedReader br = new BufferedReader(fr);
                StringBuffer sb = new StringBuffer();
                String str = "";
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                    sb.append("\n");
                }
                String msgContent = sb.toString().replace("{KOKYAKU_NAME}", kokyakuName).replace("{UKETUKE_DATE}", uketuke_Date).replace("{URL}", phoneUrl);
                mbp1.setText(msgContent, "iso-2022-jp");
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(mbp1);
                msg.setContent(mp);

                // メールを送信
                transport = session.getTransport("smtp");
                if ("true".equals(props.get("mail.smtp.auth"))) {
                    transport.connect(mailSMTP, sendAddress, sendPassword);
                } else {
                    transport.connect();
                }
                transport.sendMessage(msg, msg.getAllRecipients());

            } catch (Exception e) {
                throw new Exception(e);
            } finally {
                transport.close();
            }
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
	public void setUKG02044Services(IUKG02044Service services) {

	    UKG02044Services = services;
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
