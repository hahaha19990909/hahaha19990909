/**
 * @システム名: 受付システム
 * @ファイル名: UKG99003Action.java
 * @更新日付：: 2012/06/01
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴：2013/03/14	郭凡(SYS)		1.01		(案件番号C37074)チラシ作成機能の追加
 * 変更履歴: 2019/07/03	SYS_程旋    	1.02        C43100_お客様項目の入力簡素化対応
 *          2019/01/06  陳龍龍(SYS)    1.03         C44033-1_ホームメイトリサーチと施設情報の連携
 */
package jp.co.token.uketuke.action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.exception.DataNotFoundException;
import jp.co.token.uketuke.formbean.IconBean;
import jp.co.token.uketuke.formbean.MapBoundBox;
import jp.co.token.uketuke.service.IUKG99003Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 地図情報で表示する
 * [説 明] 地図情報で表示する
 * @author [作 成]
 * </pre>
 */
public class UKG99003Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;

	/** 地図情報画面用データ */
	private Map<String, Object> ukg99003Data;

	/** 顧客検索サービス */
	private IUKG99003Service UKG99003Services;
	// ########## 2013/03/14 郭凡 ADD Start (案件番号C37074)チラシ作成機能の追加
	/** 自動車 */
	private final static String TOHOROOTTIME = PropertiesReader.getIntance().getProperty(Consts.TOHO_ROOT_TIME);

	/** 徒歩 */
	private final static String AUTOROOTTIME = PropertiesReader.getIntance().getProperty(Consts.AUTO_ROOT_TIME);
	// ########## 2013/03/14 郭凡 ADD End 
	
	/**
	 * 地図情報画面用データ
	 *
	 * @return ukg01013Data
	 */
	public Map<String, Object> getUkg99003Data() {

		return ukg99003Data;
	}

	/**
	 * 地図情報画面用データ
	 *
	 * @param ukg01013Data
	 */
	public void setUkg99003Data(Map<String, Object> ukg99003Data) {

		this.ukg99003Data = ukg99003Data;
	}

	/**
	 * 顧客検索サービス
	 *
	 * @param services
	 */
	public void setUKG99003Services(IUKG99003Service services) {

		UKG99003Services = services;
	}

	/**
	 * <pre>
	 * [説 明] 地図情報で表示する。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);
		ukg99003Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		String bukken_no = request.getParameter("bukken_no");
//		2019/07/03 程旋 ADD START C43100_お客様項目の入力簡素化対応
		String heya_no = request.getParameter("heya_no");
		Map<String, Object> initData = UKG99003Services.getInit(bukken_no,heya_no);
//		2019/07/03 程旋 ADD END C43100_お客様項目の入力簡素化対応
		List<Map<String, String>> kuusitu_bukken = (List<Map<String, String>>) initData.get("rst_kuusitu_bukken");
		if (kuusitu_bukken != null && kuusitu_bukken.size() > 0) {
			// 周辺施設一覧データ取得
			Map<String, String> bukkeninfo = kuusitu_bukken.get(0);
			bukkeninfo.put("SYUHEN_IMAGE1_KYORI", convert(bukkeninfo.get("SYUHEN_IMAGE1_KYORI")));
			bukkeninfo.put("SYUHEN_IMAGE2_KYORI", convert(bukkeninfo.get("SYUHEN_IMAGE2_KYORI")));
			bukkeninfo.put("SYUHEN_IMAGE3_KYORI", convert(bukkeninfo.get("SYUHEN_IMAGE3_KYORI")));
			bukkeninfo.put("SYUHEN_IMAGE4_KYORI", convert(bukkeninfo.get("SYUHEN_IMAGE4_KYORI")));
			bukkeninfo.put("SYUHEN_IMAGE5_KYORI", convert(bukkeninfo.get("SYUHEN_IMAGE5_KYORI")));
			ukg99003Data.put("kuusitu_bukken", bukkeninfo);
		} else {
			throw new DataNotFoundException();
		}

		ukg99003Data.put("syuuhenn_list", initData.get("rst_syuuhenn_list"));
		ukg99003Data.put("rossen_list", initData.get("rst_rossen_list"));
		// 地図縮尺度
		String initScale = PropertiesReader.getIntance().getProperty(Consts.UKG99003_MAP_INITSCALE);
		String minScale = PropertiesReader.getIntance().getProperty(Consts.UKG99003_MAP_MINSCALE);
		ukg99003Data.put("INITSCALE", initScale);
		ukg99003Data.put("MINSCALE", minScale);
		ukg99003Data.put("MAP_SERVLETURL", PropertiesReader.getIntance().getProperty(Consts.MAP_SERVLETURL));
		ukg99003Data.put("MAP_KEY", PropertiesReader.getIntance().getProperty(Consts.MAP_KEY));
		ukg99003Data.put("MAP_VERSION", PropertiesReader.getIntance().getProperty(Consts.MAP_VERSION));
		// ########## 2013/03/14 郭凡 ADD Start (案件番号C37074)チラシ作成機能の追加
		/** 自動車 */
		ukg99003Data.put("TOHOROOTTIME", TOHOROOTTIME);
		/** 徒歩 */
		ukg99003Data.put("AUTOROOTTIME", AUTOROOTTIME);
		// ########## 2013/03/14 郭凡 ADD End
		
		// ########## 2018/10/24 ADD Start SYS_肖剣生 (案件番号C42036)不具合対応No79 Googleマップ対応
		/**航空写真のURL*/
		ukg99003Data.put("aerialURL", PropertiesReader.getIntance().getProperty(Consts.aerialURL));
		/**ストリートビューのURL*/
		ukg99003Data.put("streetviewURL", PropertiesReader.getIntance().getProperty(Consts.streetviewURL));
		/**Googleマップ有効・無効*/
		ukg99003Data.put("googleMaps", PropertiesReader.getIntance().getProperty(Consts.googleMaps));
		/**Googleマップウィンドウ幅*/
		ukg99003Data.put("googleMapsWindow_w", PropertiesReader.getIntance().getProperty(Consts.googleMapsWindow_w));
		/**Googleマップウィンドウ高*/
		ukg99003Data.put("googleMapsWindow_h", PropertiesReader.getIntance().getProperty(Consts.googleMapsWindow_h));
        /* 2020/01/10 ADD START 陳龍龍(SYS) C44033-1_ホームメイトリサーチと施設情報の連携*/
        String kategorihidReqValue = request.getParameter("kategorihidReqValue");
        if (kategorihidReqValue == null) {
            kategorihidReqValue = "";
        }
        ukg99003Data.put("kategorihidReqValue", kategorihidReqValue);
        ukg99003Data.put("hidBukkenNo", bukken_no);
        /*2020/01/10 ADD END 陳龍龍(SYS) C44033-1_ホームメイトリサーチと施設情報の連携*/
		// ########## 2018/10/24 ADD End SYS_肖剣生 (案件番号C42036)不具合対応No79 Googleマップ対応
		
		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] 駅情報を取得する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String ekiProcess() throws Exception {
		ukg99003Data = new HashMap<String, Object>();
		MapBoundBox boundBox = new MapBoundBox();
		HttpServletRequest request = ServletActionContext.getRequest();
		boundBox.setMaxx(request.getParameter("maxx"));
		boundBox.setMaxy(request.getParameter("maxy"));
		boundBox.setMinx(request.getParameter("minx"));
		boundBox.setMiny(request.getParameter("miny"));
        /* 2020/01/10 UPD START 陳龍龍(SYS) C44033-1_ホームメイトリサーチと施設情報の連携 */
        /*List<IconBean> iconList = UKG99003Services.getEki(boundBox);*/
        String ekiCode = request.getParameter("ekiStrCode");
        List<IconBean> iconList = UKG99003Services.getEki(boundBox,ekiCode);
        /* 2020/01/10 UPD END 陳龍龍(SYS) C44033-1_ホームメイトリサーチと施設情報の連携 */
		ukg99003Data.put("ICON_LIST", iconList);
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 範囲の駅を検索する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String busstopProcess() throws Exception {
		ukg99003Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		MapBoundBox boundBox = new MapBoundBox();
		boundBox.setMaxx(request.getParameter("maxx"));
		boundBox.setMaxy(request.getParameter("maxy"));
		boundBox.setMinx(request.getParameter("minx"));
		boundBox.setMiny(request.getParameter("miny"));
		ukg99003Data.put("EKI_STOP", UKG99003Services.getEkiStop(boundBox, request.getParameter("categoryIdList")));
		return "ajaxProcess";
	}
    /* 2020/01/10 DEL START 陳龍龍(SYS) C44033-1_ホームメイトリサーチと施設情報の連携 */
	/**
	 * <pre>
	 * [説 明] 病院情報を取得する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	/*public String hospitalProcess() throws Exception {
		ukg99003Data = new HashMap<String, Object>();
		HttpServletRequest request = ServletActionContext.getRequest();
		MapBoundBox boundBox = new MapBoundBox();
		boundBox.setMaxx(request.getParameter("maxx"));
		boundBox.setMaxy(request.getParameter("maxy"));
		boundBox.setMinx(request.getParameter("minx"));
		boundBox.setMiny(request.getParameter("miny"));
		ukg99003Data.put("HOSPITAL_LIST", UKG99003Services.getHospital(boundBox));
		return "ajaxProcess";
	}*/
    /* 2020/01/10 DEL END 陳龍龍(SYS) C44033-1_ホームメイトリサーチと施設情報の連携 */

	/**
	 * <pre>
	 * [説 明] 値を転変
	 * @param value 値
	 * @return 範囲の駅データ
	 * </pre>
	 */
	private String convert(String value) throws Exception {

		if (Util.isEmpty(value)) {
			return "";
		}
		BigDecimal decimalvalue = new BigDecimal(value);
		BigDecimal decimalTemporary = new BigDecimal(Double.valueOf(1000f));
		if (decimalvalue.compareTo(decimalTemporary) > 0) {
			BigDecimal decimalTemporary1 = new BigDecimal(Double.valueOf(100));
			BigDecimal decimalTemporary2 = new BigDecimal(Double.valueOf(10));
			return String.format("%.1f", (new BigDecimal(Double.valueOf(Math.ceil(decimalvalue.divide(decimalTemporary1).intValue()))).divide(decimalTemporary2)))
			    + "km";
		}
		return value + "m";
	}
    /* 2020/01/10 ADD START 陳龍龍(SYS) C44033-1_ホームメイトリサーチと施設情報の連携 */
    /**
     * <pre>
     * [説 明] その他情報を取得する
     * @return アクション処理結果
     * @throws Exception　処理異常
     * </pre>
     */
    public String sonotaProcess() throws Exception {
        ukg99003Data = new HashMap<String, Object>();
        MapBoundBox boundBox = new MapBoundBox();
        HttpServletRequest request = ServletActionContext.getRequest();
        boundBox.setMaxx(request.getParameter("maxx"));
        boundBox.setMaxy(request.getParameter("maxy"));
        boundBox.setMinx(request.getParameter("minx"));
        boundBox.setMiny(request.getParameter("miny"));
        String shisetuCode = request.getParameter("sonotaStrCode");
        ukg99003Data.put("SONOTA_SHISETU_LIST", UKG99003Services.getSoNota(boundBox, shisetuCode));
        return "ajaxProcess";
    }
    /* 2020/01/10 ADD END 陳龍龍(SYS) C44033-1_ホームメイトリサーチと施設情報の連携 */
}
