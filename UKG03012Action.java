/**
 * @システム名: 受付システム
 * @ファイル名: UKG03012Action.java
 * @更新日付：: 2012/5/18
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴 ： 2013/05/17  郭凡(SYS)	   (案件C37109-1)受付システムSTEP2要望対応
 * 更新履歴 ： 2018/02/01  張陽陽(SYS)	   案件C42036デザイン変更新規作成
 * 更新履歴 ： 2021/01/04 楊棟(SYS)    C45019_社長指示デザイン修正
 */
package jp.co.token.uketuke.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Consts;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.formbean.UKG03012Bean;
import jp.co.token.uketuke.service.IUKG03012Service;

/**
 * <pre>
 * [機 能] 物件情報(希望条件)
 * [説 明] 物件情報(希望条件)を設定
 * @author[作 成] 2012/05/20 SYS_楊
 * </pre>
 */
public class UKG03012Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 7612098219834262561L;

	/** 物件情報(希望条件)サービス */
	private IUKG03012Service UKG03012Services;

	/** 物件情報(希望条件) */
	private UKG03012Bean ukg03012Bean = null;


	/**
	 * @return　物件情報(希望条件)画面
	 */
	public UKG03012Bean getUkg03012Bean() {

		return ukg03012Bean;
	}

	/**
	 * 　物件情報(希望条件)画面
	 *
	 * @param ukg03012Bean
	 */
	public void setUkg03012Bean(UKG03012Bean ukg03012Bean) {

		this.ukg03012Bean = ukg03012Bean;
	}

	/**
	 * 物件情報(希望条件)サービス
	 * @param uKG03012Services
	 *
	 */
	public void setUKG03012Services(IUKG03012Service uKG03012Services) {

		UKG03012Services = uKG03012Services;
	}

	/**
	 * <pre>
	 * [説 明] 物件情報(希望条件)画面のデータ設定
	 * @return 処理結果
	 * @throws Exception　処理異常
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
		// 会社コード
		String kaisyaCd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		// 受付№
		String uketukeNo = String.valueOf(session.getAttribute(Consts.UKETUKENO));
		// 希望条件履歴SEQ
		String kibouRirekiSeq = request.getParameter("kibouRirekiSeq");
		// 希望条件登録フラグ
		String torokuFlg = request.getParameter("torokuFlg");

		// 画面状況データ
		ukg03012Bean = (UKG03012Bean) session.getAttribute(Consts.UKG03012BEAN_SESSION_KEY);

		if (!("0".equals(torokuFlg) || ukg03012Bean != null)) {

			ukg03012Bean = new UKG03012Bean();
			List<Map<String, Object>> kibouJyoukenList = new ArrayList<Map<String, Object>>();
			// データ取得
			Map<String, Object> kibouJyoukenInfo = UKG03012Services.getKibouJyoukenInfo(kaisyaCd, uketukeNo, kibouRirekiSeq);

			// 希望条件データ取得
			List<Map<String, Object>> kibouJyouken = (List<Map<String, Object>>) kibouJyoukenInfo.get("out_kiboujyouken");
			// 希望物件区分コード
			String kibouBukkenKb = (String) kibouJyoukenInfo.get("out_kibou_bukken_kd");
			// 希望条件指定コード
			String kibouJyoukenCd = (String) kibouJyoukenInfo.get("out_kibou_jyouken_cd");

			// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
			ukg03012Bean.setKibouJyoukenCd(kibouJyoukenCd);
			List<Map<String, Object>> tokuteiShisetsu = (List<Map<String, Object>>) kibouJyoukenInfo.get("out_tokuteiShisetsu");
			List<Map<String, Object>> kibouJoukenList = new ArrayList<Map<String, Object>>();
			Map<String, Object> dataTitleMap = new HashMap<String, Object>();
			List<Map<String, Object>> SonotaDataTitleList = new ArrayList<Map<String, Object>>();
			Map<String, Object> SonotaMap = new HashMap<String, Object>();
			// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正

			if (!Util.isEmpty(kibouJyoukenCd)) {

				List<Map<String, Object>> kibouJyoukenSonota = (List<Map<String, Object>>) kibouJyoukenInfo.get("out_kibousonota");
				// 2021/01/04 UPDATE START 楊棟(SYS) C45019_社長指示デザイン修正
				SonotaDataTitleList = new ArrayList<Map<String, Object>>();
				SonotaMap = new HashMap<String, Object>();
				// 2021/01/04 UPDATE END 楊棟(SYS) C45019_社長指示デザイン修正
				if (kibouJyoukenSonota.size() > 0) {
					if ("4".equals(kibouJyoukenCd)) {
						// 路線名
						List<Map<String, Object>> kibouRosen = (List<Map<String, Object>>) kibouJyoukenInfo.get("out_kibourosen");
						for (int i = 0; i < kibouJyoukenSonota.size(); i++) {
							Map<String, Object> sonotaData = (Map<String, Object>)kibouJyoukenSonota.get(i);

							String strRose1 = sonotaData.get("MOKUTEKIEKICD") + "駅まで" + sonotaData.get("KIBOUTIMECD") + "分以内";
							// 2021/01/04 UPDATE START 楊棟(SYS) C45019_社長指示デザイン修正
							dataTitleMap = new HashMap<String, Object>();
							Map<String, Object> chiikiMap = new HashMap<String, Object>();
							// 2021/01/04 UPDATE END 楊棟(SYS) C45019_社長指示デザイン修正
							dataTitleMap.put("TITLE", strRose1);
							dataTitleMap.put("WIDTH", "295px");
							if (i == 0) {
								// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
								dataTitleMap.put("ITEM","希望通勤・通学時間");
								dataTitleMap.put("DATA",strRose1);
								//dataTitleMap.put("DATA","> 希望通勤・通学時間 ： " + strRose1);
							} else if (i > 0) {
								//dataTitleMap.put("DATA","　　　　　　　　　　　　　　　　 " + strRose1);
								dataTitleMap.put("DATA",strRose1);
								// 2018/02/02 張陽陽(SYS)  UPD END   案件C42036デザイン変更新規作成
							}
							SonotaDataTitleList.add(dataTitleMap);
							// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
							// 地域
							chiikiMap.put("chiiki", strRose1);
							kibouJoukenList.add(chiikiMap);
							// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正

							String strRose2 = "乗り換え許容回数" + sonotaData.get("NORIKAEKAISUUCD") + " (" + sonotaData.get("BUSJYOUSHAKB") + ")";
							dataTitleMap = new HashMap<String, Object>();
							// 2021/01/04 UPDATE START 楊棟(SYS) C45019_社長指示デザイン修正
							chiikiMap = new HashMap<String, Object>();
							// 2021/01/04 UPDATE END 楊棟(SYS) C45019_社長指示デザイン修正
							dataTitleMap.put("TITLE", strRose2);
							dataTitleMap.put("WIDTH", "295px");
							// 2013/04/19 SYS_温 UPD Start (案件C37058)受付システム要望対応 // parasoft-suppress UC.ACC "C37109-1JTest対応"
							//if (i == 0) { // parasoft-suppress UC.ACC "C37109-1JTest対応"
							//	dataTitleMap.put("DATA","> 希望通勤・通学時間 ： " + strRose2);
							//} else if (i > 0) {
							//	dataTitleMap.put("DATA","　　　　　　　　　　　　" + strRose2);
							//}
							// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
							//dataTitleMap.put("DATA","　　　　　　　　　　　　　　　　 " + strRose2);
							dataTitleMap.put("DATA",strRose2);
							// 2018/02/02 張陽陽(SYS)  UPD END   案件C42036デザイン変更新規作成
							// 2013/04/19 SYS_温 UPD End
							SonotaDataTitleList.add(dataTitleMap);
							// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
							// 地域
							chiikiMap.put("chiiki", strRose2);
							kibouJoukenList.add(chiikiMap);
							// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正

							if (kibouRosen.size() > 0) {
								for (Map<String, Object> rosenDataMap : kibouRosen) {
									// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
									chiikiMap = new HashMap<String, Object>();
									// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
									dataTitleMap = new HashMap<String, Object>();
									dataTitleMap.put("TITLE", rosenDataMap.get("ROSENNAME"));
									dataTitleMap.put("WIDTH", "295px"); // parasoft-suppress UC.ACC "C37109-1JTest対応"
									// 2013/04/19 SYS_温 UPD Start (案件C37058)受付システム要望対応 // parasoft-suppress UC.ACC "C37109-1JTest対応"
									//if (i == 0) { // parasoft-suppress UC.ACC "C37109-1JTest対応"
									//	dataTitleMap.put("DATA","> 希望通勤・通学時間 ： " + rosenDataMap.get("ROSENNAME"));
									//} else if (i > 0) {
									//	dataTitleMap.put("DATA","　　　　　　　　　　　　" + rosenDataMap.get("ROSENNAME"));
									//}
									// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
									//dataTitleMap.put("DATA","　　　　　　　　　　　　　　　　 " + rosenDataMap.get("ROSENNAME"));
									dataTitleMap.put("DATA",rosenDataMap.get("ROSENNAME"));
									// 2018/02/02 張陽陽(SYS)  UPD END   案件C42036デザイン変更新規作成
									// 2013/04/19 SYS_温 UPD End
									SonotaDataTitleList.add(dataTitleMap);
									// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
									// 地域
									chiikiMap.put("chiiki", rosenDataMap.get("ROSENNAME"));
									kibouJoukenList.add(chiikiMap);
									// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
								}
							}
						}
						SonotaMap.put("name", "> 希望通勤・通学時間 ： ");
						// 2018/02/02 張陽陽(SYS)  ADD START 案件C42036デザイン変更新規作成
						SonotaMap.put("ITEM", "希望通勤・通学時間");
						// 2018/02/02 張陽陽(SYS)  ADD END   案件C42036デザイン変更新規作成
					} else {
						for (int i = 0; i < kibouJyoukenSonota.size(); i++) {
							Map<String, Object> dataMap = (Map<String, Object>)kibouJyoukenSonota.get(i);
							// 2021/01/04 UPDATE START 楊棟(SYS) C45019_社長指示デザイン修正
							dataTitleMap = new HashMap<String, Object>();
							Map<String, Object> chiikiMap = new HashMap<String, Object>();
							// 2021/01/04 UPDATE END 楊棟(SYS) C45019_社長指示デザイン修正
							if ("1".equals(kibouJyoukenCd)) {
								dataTitleMap.put("TITLE", dataMap.get("SITEIAREANAME")+" "+"周辺");
								dataTitleMap.put("WIDTH", "295px");
								if (i == 0) {
									// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
									dataTitleMap.put("ITEM","希望地域");
									//dataTitleMap.put("DATA","> 希 望 地 域 ： " + dataMap.get("SITEIAREANAME")+" "+"周辺");
									dataTitleMap.put("DATA",dataMap.get("SITEIAREANAME")+" "+"周辺");
								} else if (i > 0) {
									dataTitleMap.put("DATA",dataMap.get("SITEIAREANAME")+" "+"周辺");
									// 2018/02/02 張陽陽(SYS)  UPD END  案件C42036デザイン変更新規作成
								}
								SonotaDataTitleList.add(dataTitleMap);
								// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
								// 地域
								chiikiMap.put("chiiki", dataMap.get("SITEIAREANAME")+" "+"周辺");
								kibouJoukenList.add(chiikiMap);
								// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
							} else if ("2".equals(kibouJyoukenCd)) {
								dataTitleMap.put("TITLE", dataMap.get("KIBOUCITYNAME"));
								dataTitleMap.put("WIDTH", "295px");
								if (i == 0) {
									// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
									dataTitleMap.put("ITEM","希望地域");
									//dataTitleMap.put("DATA","> 希 望 地 域 ： " + dataMap.get("KIBOUCITYNAME"));
									dataTitleMap.put("DATA",dataMap.get("KIBOUCITYNAME"));
								} else if (i > 0) {
									dataTitleMap.put("DATA",dataMap.get("KIBOUCITYNAME"));
									// 2018/02/02 張陽陽(SYS)  UPD END  案件C42036デザイン変更新規作成
								}
								SonotaDataTitleList.add(dataTitleMap);
								// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
								// 地域
								chiikiMap.put("chiiki", dataMap.get("KIBOUCITYNAME"));
								kibouJoukenList.add(chiikiMap);
								// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
							} else if ("3".equals(kibouJyoukenCd)) {
								dataTitleMap.put("TITLE", dataMap.get("EKINAME"));
								dataTitleMap.put("WIDTH", "295px");
								if (i == 0) {
									// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
									dataTitleMap.put("ITEM","希望駅");
									//dataTitleMap.put("DATA","> 希　望　駅 ： " + dataMap.get("EKINAME"));
									dataTitleMap.put("DATA",dataMap.get("EKINAME"));
								} else if (i > 0) {
									dataTitleMap.put("DATA",dataMap.get("EKINAME"));
									// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
								}
								SonotaDataTitleList.add(dataTitleMap);
								// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
								// 地域
								chiikiMap.put("chiiki", dataMap.get("EKINAME"));
								kibouJoukenList.add(chiikiMap);
								// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
							}
							// 2021/01/13 UPDATE START 楊棟(SYS) C45019_社長指示デザイン修正
							/*else if ("5".equals(kibouJyoukenCd)) {
								if("1".equals(dataMap.get("SISETUKB"))) {
									// 2013/03/12 SYS_胡涛 UPD START 案件C37058
									//SonotaDataList.add(dataMap.get("SISETUNAME") + " (学区)");
									String strSisetuName = "";
									if("31".equals(dataMap.get("SISETU_BUNRUI_CD")) ||
											"61".equals(dataMap.get("SISETU_BUNRUI_CD"))){
										strSisetuName = dataMap.get("SISETUNAME").toString() + " (学区)";
									} else {
										strSisetuName = dataMap.get("SISETUNAME").toString();
									}
									// 2013/03/12 SYS_胡涛 UPD END
									dataTitleMap.put("TITLE", strSisetuName);
									dataTitleMap.put("WIDTH", "295px");
									if (i == 0) {
										// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
										dataTitleMap.put("ITEM","希望施設");
										//dataTitleMap.put("DATA","> 希 望 施 設 ： " + strSisetuName);
										dataTitleMap.put("DATA",strSisetuName);
									} else if (i > 0) {
										dataTitleMap.put("DATA",strSisetuName);
										// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
									}
									SonotaDataTitleList.add(dataTitleMap);
								}else{
									String kyori = (String)dataMap.get("SISETUKYORI");
									kyori = kyori.replaceAll(" ", "");
									String strSisetuName = "";
									strSisetuName = dataMap.get("SISETUNAME") + " (" + kyori + ")";
									dataTitleMap.put("TITLE", strSisetuName);
									dataTitleMap.put("WIDTH", "295px");
									if (i == 0) {
										// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
										dataTitleMap.put("ITEM","希望施設");
										//dataTitleMap.put("DATA","> 希 望 施 設 ： " + strSisetuName);
										dataTitleMap.put("DATA",strSisetuName);
									} else if (i > 0) {
										dataTitleMap.put("DATA",strSisetuName);
										// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
									}
									SonotaDataTitleList.add(dataTitleMap);
									if("6".equals(dataMap.get("SISETUKB")) && !Util.isEmpty((String)dataMap.get("BAS_NAME"))){
										String strBasName = "";
										if (((String)dataMap.get("BAS_NAME")).endsWith("停")) {
											strBasName = dataMap.get("BAS_NAME").toString();
										} else {
											strBasName = dataMap.get("BAS_NAME").toString() + " 停";
										}
										dataTitleMap = new HashMap<String, Object>();
										dataTitleMap.put("TITLE", strBasName);
										dataTitleMap.put("WIDTH", "295px");
										// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
										//dataTitleMap.put("DATA","　　　　　　　　" + strBasName);
										dataTitleMap.put("DATA",strBasName);
										// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
										SonotaDataTitleList.add(dataTitleMap);
									}
								}
							}*/
							// 2021/01/13 UPDATE END 楊棟(SYS) C45019_社長指示デザイン修正
						}
					}
					// 2021/01/14 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					SonotaMap.put("dataList", SonotaDataTitleList);
					kibouJyoukenList.add(SonotaMap);
					// 2021/01/14 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				}
			}

			// 物件種別
			List<Map<String, Object>> kibouBukkenSyubetsu = (List<Map<String, Object>>) kibouJyoukenInfo.get("out_bukkensyubetu");
			// 2021/01/04 UPDATE START 楊棟(SYS) C45019_社長指示デザイン修正
			SonotaDataTitleList = new ArrayList<Map<String, Object>>();
			Map<String, Object> kibouBukkenSyubetsuMap = new HashMap<String, Object>();
			Map<String, Object> bukkenShubetsuMap = new HashMap<String, Object>();
			StringBuffer kiboubukkenname = new StringBuffer();
			// 2021/01/04 UPDATE END 楊棟(SYS) C45019_社長指示デザイン修正
			if (kibouBukkenSyubetsu.size() > 0) {
				// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
				for(int j = 0; j < kibouBukkenSyubetsu.size(); j++) {
					kiboubukkenname.append(kibouBukkenSyubetsu.get(j).get("KIBOUBUKKENNAME"));
					if (j < kibouBukkenSyubetsu.size() - 1) {
						kiboubukkenname.append("、");
					} else {
						break;
					}
				}
				// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				Iterator<Map<String, Object>> bukkenSyubetsuIter = kibouBukkenSyubetsu.iterator();
				while (bukkenSyubetsuIter.hasNext()) {
					StringBuffer strBuffer = new StringBuffer();
					int i = 0;
					// 2021/01/04 UPDATE START 楊棟(SYS) C45019_社長指示デザイン修正
					dataTitleMap = new HashMap<String, Object>();
					// 2021/01/04 UPDATE END 楊棟(SYS) C45019_社長指示デザイン修正
					while(i < 2) {
						strBuffer.append(bukkenSyubetsuIter.next().get("KIBOUBUKKENNAME"));
						if (bukkenSyubetsuIter.hasNext()) {
							strBuffer.append("、");
							i++;
						}else {
							break;
						}
					}
					dataTitleMap.put("TITLE", strBuffer);
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					//dataTitleMap.put("DATA","　　　　　　　　　　 " + strBuffer);
					dataTitleMap.put("DATA",strBuffer);
					// 2018/02/02 張陽陽(SYS)  UPD END   案件C42036デザイン変更新規作成
					SonotaDataTitleList.add(dataTitleMap);
				}
				// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
				// 物件種別
				bukkenShubetsuMap.put("bukkenShubetsu", kiboubukkenname);
				kibouJoukenList.add(bukkenShubetsuMap);
				// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
			}
			if (SonotaDataTitleList.size() > 0) {
				// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
				SonotaDataTitleList.get(0).put("ITEM","物件種別");
				//SonotaDataTitleList.get(0).put("DATA", "> 物 件 種 別 ： " + SonotaDataTitleList.get(0).get("TITLE").toString());
				SonotaDataTitleList.get(0).put("DATA",SonotaDataTitleList.get(0).get("TITLE").toString());
				// 2018/02/02 張陽陽(SYS)  UPD END   案件C42036デザイン変更新規作成
				kibouBukkenSyubetsuMap.put("dataList", SonotaDataTitleList);
				kibouJyoukenList.add(kibouBukkenSyubetsuMap);
			}
			// 築年数
			List<Map<String, Object>>  tikunenSuuDataList = new ArrayList<Map<String, Object>> ();
			Map<String, Object> tikunenSuuMap = new HashMap<String, Object>();

			// 家賃
			List<Map<String, Object>>  yatinDataList = new ArrayList<Map<String, Object>> ();
			Map<String, Object> yatinMap = new HashMap<String, Object>();
			// 礼金・保証金
			List<Map<String, Object>>  neikinHosyoukinDataList = new ArrayList<Map<String, Object>> ();
			Map<String, Object> neikinHosyoukinMap = new HashMap<String, Object>();

			// 初期(入居)費用
			List<Map<String, Object>>  initMoneyDataList = new ArrayList<Map<String, Object>> ();
			Map<String, Object> initMoneyMap = new HashMap<String, Object>();

			// 希望専有面積
			List<Map<String, Object>>  senyuuMensekiDataList = new ArrayList<Map<String, Object>> ();
			Map<String, Object> senyuuMensekiMap = new HashMap<String, Object>();

			// 駅からの徒歩分
			List<Map<String, Object>>  stationWalkMinuteDataList = new ArrayList<Map<String, Object>> ();
			Map<String, Object> stationWalkMinuteMap = new HashMap<String, Object>();

			// 駐車場
			List<Map<String, Object>>  cyuuSyaJyouDataList = new ArrayList<Map<String, Object>> ();
			Map<String, Object> cyuuSyaJyouMap = new HashMap<String, Object>();

			// 入居人数
			List<Map<String, Object>>  ninZuuDataList = new ArrayList<Map<String, Object>> ();
			Map<String, Object> ninZuuMap = new HashMap<String, Object>();

			// 法人希望部屋数
			List<Map<String, Object>>  houjinHeyakazuDataList = new ArrayList<Map<String, Object>> ();
			Map<String, Object> houjinHeyakazuMap = new HashMap<String, Object>();

			// 入居時期
			List<Map<String, Object>>  nyuukyoJikiDataList = new ArrayList<Map<String, Object>> ();
			Map<String, Object> nyuukyoJikiMap = new HashMap<String, Object>();


			for (int i = 0; i < kibouJyouken.size(); i++) {
				Map<String, Object> dataMap = kibouJyouken.get(i);
				// 2021/01/04 UPDATE START 楊棟(SYS) C45019_社長指示デザイン修正
				dataTitleMap = new HashMap<String, Object>();
				Map<String, Object> chikunensuuMap = new HashMap<String, Object>();
				// 2021/01/04 UPDATE END 楊棟(SYS) C45019_社長指示デザイン修正
				// 築年数
				if (dataMap.get("KIBOUTIKUNENSUU") != null) {
					dataTitleMap.put("TITLE", dataMap.get("KIBOUTIKUNENSUU"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","築年数");
					//dataTitleMap.put("DATA","> 築　年　数 ： " + dataMap.get("KIBOUTIKUNENSUU"));
					dataTitleMap.put("DATA",dataMap.get("KIBOUTIKUNENSUU"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					tikunenSuuDataList.add(dataTitleMap);

					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					// 築年数
					chikunensuuMap.put("chikunensuu", dataMap.get("KIBOUTIKUNENSUU"));
					kibouJoukenList.add(chikunensuuMap);
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				}
				// 家賃
				if (dataMap.get("YATINFROM") != null && dataMap.get("YATINTO") != null) {
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					Map<String, Object> yachinMap = new HashMap<String, Object>();
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					dataTitleMap = new HashMap<String, Object>();
					dataTitleMap.put("TITLE", dataMap.get("YATINFROM") + "～" + dataMap.get("YATINTO"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","家賃");
					//dataTitleMap.put("DATA","> 家　　賃 ： " + dataMap.get("YATINFROM") + "～" + dataMap.get("YATINTO"));
					dataTitleMap.put("DATA",dataMap.get("YATINFROM") + "～" + dataMap.get("YATINTO"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					yatinDataList.add(dataTitleMap);
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					// 家賃
					yachinMap.put("yachin", dataTitleMap.get("DATA"));
					kibouJoukenList.add(yachinMap);
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正

					if (dataMap.get("INCKYOUKAN") != null && dataMap.get("INCPARKING") != null) {
						// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
						yachinMap = new HashMap<String, Object>();
						// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
						dataTitleMap = new HashMap<String, Object>();
						dataTitleMap.put("TITLE", "(" + dataMap.get("INCKYOUKAN") + "、" + dataMap.get("INCPARKING") + ")");
						dataTitleMap.put("WIDTH", "295px");
						// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
						//dataTitleMap.put("DATA","　　　　　　　" + "(" + dataMap.get("INCKYOUKAN") + "、" + dataMap.get("INCPARKING") + ")");
						dataTitleMap.put("DATA","(" + dataMap.get("INCKYOUKAN") + "、" + dataMap.get("INCPARKING") + ")");
						// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
						yatinDataList.add(dataTitleMap);
						// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
						// 家賃
						yachinMap.put("yachin", dataTitleMap.get("DATA"));
						kibouJoukenList.add(yachinMap);
						// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					} else {
						if (dataMap.get("INCKYOUKAN") != null) {
							// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
							yachinMap = new HashMap<String, Object>();
							// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
							dataTitleMap = new HashMap<String, Object>();
							dataTitleMap.put("TITLE", "(" + dataMap.get("INCKYOUKAN") + ")");
							dataTitleMap.put("WIDTH", "295px");
							// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
							//dataTitleMap.put("DATA","　　　　　　　" + "(" + dataMap.get("INCKYOUKAN") + ")");
							dataTitleMap.put("DATA","(" + dataMap.get("INCKYOUKAN") + ")");
							// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
							yatinDataList.add(dataTitleMap);
							// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
							// 家賃
							yachinMap.put("yachin", dataTitleMap.get("DATA"));
							kibouJoukenList.add(yachinMap);
							// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
						}
						if (dataMap.get("INCPARKING") != null) {
							// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
							yachinMap = new HashMap<String, Object>();
							// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
							dataTitleMap = new HashMap<String, Object>();
							dataTitleMap.put("TITLE", "(" + dataMap.get("INCPARKING") + ")");
							dataTitleMap.put("WIDTH", "295px");
							// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
							//dataTitleMap.put("DATA","　　　　　　　" + "(" + dataMap.get("INCPARKING") + ")");
							dataTitleMap.put("DATA","(" + dataMap.get("INCPARKING") + ")");
							// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
							yatinDataList.add(dataTitleMap);
							// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
							// 家賃
							yachinMap.put("yachin", dataTitleMap.get("DATA"));
							kibouJoukenList.add(yachinMap);
							// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
						}
					}
				}
				// 礼金・保証金
				if (!Util.isEmpty((String)dataMap.get("REIKINZERO")) && !Util.isEmpty((String)dataMap.get("SIKIHOSYOKINZERO"))) {
					dataTitleMap = new HashMap<String, Object>();
					dataTitleMap.put("TITLE", dataMap.get("REIKINZERO") + "、" + dataMap.get("SIKIHOSYOKINZERO"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","礼金・保証金");
					//dataTitleMap.put("DATA","> 礼金・保証金 ： " + dataMap.get("REIKINZERO") + "、" + dataMap.get("SIKIHOSYOKINZERO"));
					dataTitleMap.put("DATA",dataMap.get("REIKINZERO") + "、" + dataMap.get("SIKIHOSYOKINZERO"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					neikinHosyoukinDataList.add(dataTitleMap);
				} else if (!Util.isEmpty((String)dataMap.get("REIKINZERO")) && Util.isEmpty((String)dataMap.get("SIKIHOSYOKINZERO"))) {
					dataTitleMap = new HashMap<String, Object>();
					dataTitleMap.put("TITLE", dataMap.get("REIKINZERO") + "");
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","礼金・保証金");
					//dataTitleMap.put("DATA","> 礼金・保証金 ： " + dataMap.get("REIKINZERO") + "");
					dataTitleMap.put("DATA",dataMap.get("REIKINZERO") + "");
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					neikinHosyoukinDataList.add(dataTitleMap);
				} else if (Util.isEmpty((String)dataMap.get("REIKINZERO")) && !Util.isEmpty((String)dataMap.get("SIKIHOSYOKINZERO"))) {
					dataTitleMap = new HashMap<String, Object>();
					dataTitleMap.put("TITLE", dataMap.get("SIKIHOSYOKINZERO") + "");
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","礼金・保証金");
					//dataTitleMap.put("DATA","> 礼金・保証金 ： " + dataMap.get("SIKIHOSYOKINZERO") + "");
					dataTitleMap.put("DATA",dataMap.get("SIKIHOSYOKINZERO") + "");
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					neikinHosyoukinDataList.add(dataTitleMap);
				}
				// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
				// 初期(入居)費用
				/*if (dataMap.get("KIBOUINITMONEY") != null) {
					dataTitleMap = new HashMap<String, Object>();
					dataTitleMap.put("TITLE", dataMap.get("KIBOUINITMONEY"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","初期(入居)費用");
					//dataTitleMap.put("DATA","> 初期(入居)費用 ： " + dataMap.get("KIBOUINITMONEY"));
					dataTitleMap.put("DATA",dataMap.get("KIBOUINITMONEY"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					initMoneyDataList.add(dataTitleMap);
				}*/
				// 2018/02/02 張陽陽(SYS)  UPD END   案件C42036デザイン変更新規作成
				// 希望専有面積
				//2013/05/17    郭凡(SYS)  UPD START 案件C37109-1
				//if ("2".equals(kibouBukkenKb)
				//    && dataMap.get("KIBOUMENSEKIFROM") != null && dataMap.get("KIBOUMENSEKITO") != null) {
				  if ("1".equals(kibouBukkenKb) || "2".equals(kibouBukkenKb)) {
				   if(dataMap.get("KIBOUMENSEKIFROM") != null && dataMap.get("KIBOUMENSEKITO") != null) {
				//2013/05/17    郭凡(SYS)  UPD END 案件C37109-1
					dataTitleMap = new HashMap<String, Object>();
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					Map<String, Object> kibouSenyuuMensekiMap = new HashMap<String, Object>();
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					dataTitleMap.put("TITLE", dataMap.get("KIBOUMENSEKIFROM") + "～" + dataMap.get("KIBOUMENSEKITO"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","希望専有面積");
					//dataTitleMap.put("DATA","> 希望専有面積 ： " + dataMap.get("KIBOUMENSEKIFROM") + "～" + dataMap.get("KIBOUMENSEKITO"));
					dataTitleMap.put("DATA",dataMap.get("KIBOUMENSEKIFROM") + "～" + dataMap.get("KIBOUMENSEKITO"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					senyuuMensekiDataList.add(dataTitleMap);
				  //2013/05/17    郭凡(SYS)  UPD START 案件C37109-1
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					// 希望専有面積
					kibouSenyuuMensekiMap.put("kibouSenyuuMenseki", dataMap.get("KIBOUMENSEKIFROM") + "～" + dataMap.get("KIBOUMENSEKITO"));
					kibouJoukenList.add(kibouSenyuuMensekiMap);
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				   }
				  //2013/05/17    郭凡(SYS)  UPD END 案件C37109-1
				}
				// 駅からの徒歩分
				if (dataMap.get("STATIONWALKMINUTE") != null) {
					dataTitleMap = new HashMap<String, Object>();
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					Map<String, Object> kachiBuMap = new HashMap<String, Object>();
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					dataTitleMap.put("TITLE", dataMap.get("STATIONWALKMINUTE"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","駅からの徒歩分");
					//dataTitleMap.put("DATA","> 駅からの徒歩分 ： " + dataMap.get("STATIONWALKMINUTE"));
					dataTitleMap.put("DATA", dataMap.get("STATIONWALKMINUTE"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					stationWalkMinuteDataList.add(dataTitleMap);
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					// 駅からの徒歩分
					kachiBuMap.put("kachiBu", dataMap.get("STATIONWALKMINUTE"));
					kibouJoukenList.add(kachiBuMap);
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				}
				// 駐車場
				if ("1".equals(dataMap.get("KIBOUPARKINGFLG"))) {
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					Map<String, Object> chuushajouMap = new HashMap<String, Object>();
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					dataTitleMap = new HashMap<String, Object>();
					dataTitleMap.put("TITLE", dataMap.get("KIBOUPARKING") + "" + dataMap.get("KIBOUPARKINGSUU"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","駐車場");
					//dataTitleMap.put("DATA","> 駐　 車　場 ： " + dataMap.get("KIBOUPARKING") + "" + dataMap.get("KIBOUPARKINGSUU"));
					dataTitleMap.put("DATA",dataMap.get("KIBOUPARKING") + "" + dataMap.get("KIBOUPARKINGSUU"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					cyuuSyaJyouDataList.add(dataTitleMap);
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					// 駐車場
					chuushajouMap.put("chuushajou", dataMap.get("KIBOUPARKING") + "" + dataMap.get("KIBOUPARKINGSUU"));
					kibouJoukenList.add(chuushajouMap);
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				} else if ("0".equals(dataMap.get("KIBOUPARKINGFLG"))) {
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					Map<String, Object> chuushajouMap = new HashMap<String, Object>();
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					dataTitleMap = new HashMap<String, Object>();
					dataTitleMap.put("TITLE", dataMap.get("KIBOUPARKING"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","駐車場");
					//dataTitleMap.put("DATA","> 駐　 車　場 ： " + dataMap.get("KIBOUPARKING"));
					dataTitleMap.put("DATA",dataMap.get("KIBOUPARKING"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					cyuuSyaJyouDataList.add(dataTitleMap);
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					// 駐車場
					chuushajouMap.put("chuushajou", dataMap.get("KIBOUPARKING"));
					kibouJoukenList.add(chuushajouMap);
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				}
				// 入居人数
				if (!Util.isEmpty((String)dataMap.get("KIBOUOTONA")) && !Util.isEmpty((String)dataMap.get("KIBOUKODOMO"))) {
					dataTitleMap = new HashMap<String, Object>();
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					Map<String, Object> nyuukyoNinzuuMap = new HashMap<String, Object>();
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					dataTitleMap.put("TITLE", "大人" + dataMap.get("KIBOUOTONA") + "、" + "子供" + (String) dataMap.get("KIBOUKODOMO"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","入居人数");
					//dataTitleMap.put("DATA","> 入 居 人 数 ： " + "大人" + dataMap.get("KIBOUOTONA") + "、" + "子供" + (String) dataMap.get("KIBOUKODOMO"));
					dataTitleMap.put("DATA","大人" + dataMap.get("KIBOUOTONA") + "、" + "子供" + (String) dataMap.get("KIBOUKODOMO"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					ninZuuDataList.add(dataTitleMap);
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					// 入居人数
					nyuukyoNinzuuMap.put("nyuukyoNinzuu", "大人" + dataMap.get("KIBOUOTONA") + "、" + "子供" + (String) dataMap.get("KIBOUKODOMO"));
					kibouJoukenList.add(nyuukyoNinzuuMap);
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				} else if(!Util.isEmpty((String)dataMap.get("KIBOUOTONA")) && Util.isEmpty((String)dataMap.get("KIBOUKODOMO"))){
					dataTitleMap = new HashMap<String, Object>();
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					Map<String, Object> nyuukyoNinzuuMap = new HashMap<String, Object>();
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					dataTitleMap.put("TITLE", "大人" + dataMap.get("KIBOUOTONA"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","入居人数");
					//dataTitleMap.put("DATA","> 入 居 人 数 ： " + "大人" + dataMap.get("KIBOUOTONA"));
					dataTitleMap.put("DATA","大人" + dataMap.get("KIBOUOTONA"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					ninZuuDataList.add(dataTitleMap);
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					// 入居人数
					nyuukyoNinzuuMap.put("nyuukyoNinzuu", "大人" + dataMap.get("KIBOUOTONA"));
					kibouJoukenList.add(nyuukyoNinzuuMap);
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				}
				// 法人希望部屋数
				if ("1".equals(dataMap.get("HOUJINKIBOUHEYASUUFLG"))) {
					dataTitleMap = new HashMap<String, Object>();
					dataTitleMap.put("TITLE", dataMap.get("HOUJINKIBOUHEYASUU"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","法人希望部屋数");
					//dataTitleMap.put("DATA","> 法人希望部屋数 ： " + dataMap.get("HOUJINKIBOUHEYASUU"));
					dataTitleMap.put("DATA",dataMap.get("HOUJINKIBOUHEYASUU"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					houjinHeyakazuDataList.add(dataTitleMap);
				} else if ("2".equals(dataMap.get("HOUJINKIBOUHEYASUUFLG"))) {
					dataTitleMap = new HashMap<String, Object>();
					dataTitleMap.put("TITLE", dataMap.get("KIBOUHEYASUU"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","法人希望部屋数");
					//dataTitleMap.put("DATA","> 法人希望部屋数 ： " + dataMap.get("KIBOUHEYASUU"));
					dataTitleMap.put("DATA",dataMap.get("KIBOUHEYASUU"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					houjinHeyakazuDataList.add(dataTitleMap);
				}
				// 入居時期
				if(!Util.isEmpty((String)dataMap.get("KIBOUNYUKYODATE")) && !Util.isEmpty((String)dataMap.get("KIBOUJIKI"))) {
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					Map<String, Object> tejuneriaNyuukyoJikiMap = new HashMap<String, Object>();
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					String  nyukyouDate = ((String)dataMap.get("KIBOUNYUKYODATE")).substring(0, 4) + "年";
					if(Integer.valueOf(((String)dataMap.get("KIBOUNYUKYODATE")).substring(4, 6)) < 10) {
						nyukyouDate += ((String)dataMap.get("KIBOUNYUKYODATE")).substring(5, 6)+ "月";
					}else{
						nyukyouDate += ((String)dataMap.get("KIBOUNYUKYODATE")).substring(4, 6)+ "月";
					}
					String  nyukyouJiki = (String)dataMap.get("KIBOUJIKI");
					dataTitleMap = new HashMap<String, Object>();
					dataTitleMap.put("TITLE", nyukyouDate+""+nyukyouJiki);
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","入居時期");
					//dataTitleMap.put("DATA","> 入 居 時 期 ： " + nyukyouDate+""+nyukyouJiki);
					dataTitleMap.put("DATA",nyukyouDate+""+nyukyouJiki);
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					nyuukyoJikiDataList.add(dataTitleMap);
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					// 入居時期
					tejuneriaNyuukyoJikiMap.put("nyuukyoJiki", nyukyouDate+""+nyukyouJiki);
					kibouJoukenList.add(tejuneriaNyuukyoJikiMap);
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				}else if(!Util.isEmpty((String)dataMap.get("KIBOUNYUKYODATE")) && Util.isEmpty((String)dataMap.get("KIBOUJIKI"))) {

					if(Integer.valueOf(((String)dataMap.get("KIBOUNYUKYODATE")).substring(4, 6)) < 10) {
						// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
						Map<String, Object> tejuneriaNyuukyoJikiMap = new HashMap<String, Object>();
						// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
						dataTitleMap = new HashMap<String, Object>();
						dataTitleMap.put("TITLE", ((String)dataMap.get("KIBOUNYUKYODATE")).substring(0, 4) + "年" +((String)dataMap.get("KIBOUNYUKYODATE")).substring(5, 6)+ "月");
						dataTitleMap.put("WIDTH", "295px");
						// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
						dataTitleMap.put("ITEM","入居時期");
						//dataTitleMap.put("DATA","> 入 居 時 期 ： " + ((String)dataMap.get("KIBOUNYUKYODATE")).substring(0, 4) + "年" +((String)dataMap.get("KIBOUNYUKYODATE")).substring(5, 6)+ "月");
						dataTitleMap.put("DATA",((String)dataMap.get("KIBOUNYUKYODATE")).substring(0, 4) + "年" +((String)dataMap.get("KIBOUNYUKYODATE")).substring(5, 6)+ "月");
						// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
						nyuukyoJikiDataList.add(dataTitleMap);
						// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
						// 入居時期
						tejuneriaNyuukyoJikiMap.put("nyuukyoJiki", ((String)dataMap.get("KIBOUNYUKYODATE")).substring(0, 4) + "年" +((String)dataMap.get("KIBOUNYUKYODATE")).substring(5, 6)+ "月");
						kibouJoukenList.add(tejuneriaNyuukyoJikiMap);
						// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					}else{
						// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
						Map<String, Object> tejuneriaNyuukyoJikiMap = new HashMap<String, Object>();
						// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
						dataTitleMap = new HashMap<String, Object>();
						dataTitleMap.put("TITLE", ((String)dataMap.get("KIBOUNYUKYODATE")).substring(0, 4) + "年" +((String)dataMap.get("KIBOUNYUKYODATE")).substring(4, 6)+ "月");
						dataTitleMap.put("WIDTH", "295px");
						// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
						dataTitleMap.put("ITEM","入居時期");
						//dataTitleMap.put("DATA","> 入 居 時 期 ： " + ((String)dataMap.get("KIBOUNYUKYODATE")).substring(0, 4) + "年" +((String)dataMap.get("KIBOUNYUKYODATE")).substring(4, 6)+ "月");
						dataTitleMap.put("DATA",((String)dataMap.get("KIBOUNYUKYODATE")).substring(0, 4) + "年" +((String)dataMap.get("KIBOUNYUKYODATE")).substring(4, 6)+ "月");
						// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
						nyuukyoJikiDataList.add(dataTitleMap);
						// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
						// 入居時期
						tejuneriaNyuukyoJikiMap.put("nyuukyoJiki", ((String)dataMap.get("KIBOUNYUKYODATE")).substring(0, 4) + "年" +((String)dataMap.get("KIBOUNYUKYODATE")).substring(4, 6)+ "月");
						kibouJoukenList.add(tejuneriaNyuukyoJikiMap);
						// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					}
				}else if(Util.isEmpty((String)dataMap.get("KIBOUNYUKYODATE")) && !Util.isEmpty((String)dataMap.get("KIBOUJIKI"))) {
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					Map<String, Object> tejuneriaNyuukyoJikiMap = new HashMap<String, Object>();
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
					dataTitleMap = new HashMap<String, Object>();
					dataTitleMap.put("TITLE", dataMap.get("KIBOUJIKI"));
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					dataTitleMap.put("ITEM","入居時期");
					//dataTitleMap.put("DATA","> 入 居 時 期 ： " + dataMap.get("KIBOUJIKI"));
					dataTitleMap.put("DATA",dataMap.get("KIBOUJIKI"));
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					nyuukyoJikiDataList.add(dataTitleMap);
					// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
					// 入居時期
					tejuneriaNyuukyoJikiMap.put("nyuukyoJiki", dataMap.get("KIBOUJIKI"));
					kibouJoukenList.add(tejuneriaNyuukyoJikiMap);
					// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				}
			}
			if (tikunenSuuDataList.size() > 0) {
				tikunenSuuMap.put("dataList", tikunenSuuDataList);
				kibouJyoukenList.add(tikunenSuuMap);
			}
			if (yatinDataList.size() > 0) {
				yatinMap.put("dataList", yatinDataList);
				kibouJyoukenList.add(yatinMap);
			}
			// 礼金・保証金
			if (neikinHosyoukinDataList.size() > 0) {
				neikinHosyoukinMap.put("dataList", neikinHosyoukinDataList);
				kibouJyoukenList.add(neikinHosyoukinMap);
			}
			// 初期(入居)費用
			if (initMoneyDataList.size() > 0) {
				initMoneyMap.put("dataList", initMoneyDataList);
				kibouJyoukenList.add(initMoneyMap);
			}
			// 間取り
			List<Map<String, Object>> madoriList = (List<Map<String, Object>>) kibouJyoukenInfo.get("out_madori");
			Map<String, Object> madoriMap = new HashMap<String, Object>();
			List<Map<String, Object>> madoriDataList = new ArrayList<Map<String, Object>>();
			// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
			Map<String, Object> tejuneriaMadoriMap = new HashMap<String, Object>();
			StringBuffer kiboumadoriName = new StringBuffer();
			// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
			if (madoriList.size() > 0) {
				// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
				for(int j = 0; j < madoriList.size(); j++) {
					kiboumadoriName.append(madoriList.get(j).get("KIBOUMADORINAME"));
					if (j < madoriList.size() - 1) {
						kiboumadoriName.append("、");
					} else {
						break;
					}
				}
				// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
				Iterator<Map<String, Object>> madoriMapIter = madoriList.iterator();
				while (madoriMapIter.hasNext()) {
					int i = 0;
					StringBuffer strBuffer = new StringBuffer();
					// 2021/01/04 UPDATE START 楊棟(SYS) C45019_社長指示デザイン修正
					dataTitleMap = new HashMap<String, Object>();
					// 2021/01/04 UPDATE END 楊棟(SYS) C45019_社長指示デザイン修正
					while (i < 5) {
						strBuffer.append(madoriMapIter.next().get("KIBOUMADORINAME"));
						if (madoriMapIter.hasNext()) {
							strBuffer.append("、");
							i++;
						} else {
							break;
						}
					}
					dataTitleMap.put("TITLE", strBuffer);
					dataTitleMap.put("WIDTH", "295px");
					// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
					//dataTitleMap.put("DATA","　 　　　　　　　　 " + strBuffer);
					dataTitleMap.put("DATA",strBuffer);
					// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
					madoriDataList.add(dataTitleMap);
				}
				// 2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
				// 間取り
				tejuneriaMadoriMap.put("tejuneriaMadori", kiboumadoriName);
				kibouJoukenList.add(tejuneriaMadoriMap);
				// 2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
			}
			if (madoriDataList.size() > 0) {
				// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
				madoriDataList.get(0).put("ITEM","間取り");
				//madoriDataList.get(0).put("DATA", "> 間　 取　り ： " + madoriDataList.get(0).get("TITLE").toString());
				madoriDataList.get(0).put("DATA", madoriDataList.get(0).get("TITLE").toString());
				// 2018/02/02 張陽陽(SYS)  UPD END 案件C42036デザイン変更新規作成
				madoriMap.put("dataList", madoriDataList);
				kibouJyoukenList.add(madoriMap);
			}
			// 希望専有面積
			//2013/05/17    郭凡(SYS)  UPD START 案件C37109-1
			//if ("2".equals(kibouBukkenKb) && senyuuMensekiDataList.size() > 0) {
			if (senyuuMensekiDataList != null && senyuuMensekiDataList.size() > 0) {
			//2013/05/17    郭凡(SYS)  UPD END 案件C37109-1
				senyuuMensekiMap.put("dataList", senyuuMensekiDataList);
				kibouJyoukenList.add(senyuuMensekiMap);
			}
			// 駅からの徒歩分
			if (stationWalkMinuteDataList.size() > 0) {
				stationWalkMinuteMap.put("dataList", stationWalkMinuteDataList);
				kibouJyoukenList.add(stationWalkMinuteMap);
			}
			// 駐車場
			if (cyuuSyaJyouDataList.size() > 0) {
				cyuuSyaJyouMap.put("dataList", cyuuSyaJyouDataList);
				kibouJyoukenList.add(cyuuSyaJyouMap);
			}
			// 入居人数
			if (ninZuuDataList != null && ninZuuDataList.size() > 0) {
				ninZuuMap.put("dataList", ninZuuDataList);
				kibouJyoukenList.add(ninZuuMap);
			}
			// 法人希望部屋数
			if (houjinHeyakazuDataList.size() > 0) {
				houjinHeyakazuMap.put("dataList", houjinHeyakazuDataList);
				kibouJyoukenList.add(houjinHeyakazuMap);
			}
			// 入居時期
			if (nyuukyoJikiDataList.size() > 0) {
				nyuukyoJikiMap.put("dataList", nyuukyoJikiDataList);
				kibouJyoukenList.add(nyuukyoJikiMap);
			}
			// 2021/01/13 ADD START 楊棟(SYS) C45019_社長指示デザイン修正
			if (tokuteiShisetsu.size() > 0) {
				SonotaDataTitleList = new ArrayList<Map<String, Object>>();
				SonotaMap = new HashMap<String, Object>();
				// [希望周辺施設]
				List<Map<String, Object>> JusyoDataList = (List<Map<String, Object>>) UKG03012Services.getJusyoData("122");
				List<String> shuuhenShisetsulist = Util.convertShiSeTsu(kibouJyouken.get(0).get("KIBOU_SISETU_CD").toString(), JusyoDataList);
				String  shuuhenShisetsu = "";
				if(null!=shuuhenShisetsulist) {
					shuuhenShisetsu  =  StringUtils.join(shuuhenShisetsulist,"、");
				}
				for (int i = 0; i < tokuteiShisetsu.size(); i++) {
					dataTitleMap = new HashMap<String, Object>();
					Map<String, Object> dataMap = (Map<String, Object>)tokuteiShisetsu.get(i);
					Map<String, Object> shisetsuMap = new HashMap<String, Object>();

					if("1".equals(dataMap.get("SISETUKB"))) {
						String strSisetuName = "";
						if("31".equals(dataMap.get("SISETU_BUNRUI_CD")) ||
								"61".equals(dataMap.get("SISETU_BUNRUI_CD"))){
							strSisetuName = dataMap.get("SISETUNAME").toString() + " (学区)";
						} else {
							strSisetuName = dataMap.get("SISETUNAME").toString();
						}
						dataTitleMap.put("TITLE", strSisetuName + "、" + shuuhenShisetsu);
						dataTitleMap.put("WIDTH", "295px");
						if (i == 0) {
							dataTitleMap.put("ITEM","希望施設");
							dataTitleMap.put("DATA",strSisetuName + "、" + shuuhenShisetsu);
						} else if (i > 0) {
							dataTitleMap.put("DATA",strSisetuName + "、" + shuuhenShisetsu);
						}
						SonotaDataTitleList.add(dataTitleMap);

						// [特定の施設]
						shisetsuMap.put("tokuteiShisetsu", strSisetuName);

					}else{
						String kyori = (String)dataMap.get("SISETUKYORI");
						kyori = kyori.replaceAll(" ", "");
						String strSisetuName = "";
						strSisetuName = dataMap.get("SISETUNAME") + " (" + kyori + ")";
						dataTitleMap.put("TITLE", strSisetuName + shuuhenShisetsu);
						dataTitleMap.put("WIDTH", "295px");
						if (i == 0) {
							dataTitleMap.put("ITEM","希望施設");
							dataTitleMap.put("DATA",strSisetuName + shuuhenShisetsu);
						} else if (i > 0) {
							dataTitleMap.put("DATA",strSisetuName + shuuhenShisetsu);
						}
						SonotaDataTitleList.add(dataTitleMap);

						// [特定の施設]
						shisetsuMap.put("tokuteiShisetsu", strSisetuName);
					}

					shisetsuMap.put("shuuhenShisetsu", shuuhenShisetsu);
					kibouJoukenList.add(shisetsuMap);
				}
				SonotaMap.put("dataList", SonotaDataTitleList);
				kibouJyoukenList.add(SonotaMap);
			}
			// 2021/01/13 ADD END 楊棟(SYS) C45019_社長指示デザイン修正
			ukg03012Bean.setKiboujyouken(kibouJyoukenList);

			// こだわり条件
			Map<String, Object> kodawariJyoukenData = UKG03012Services.getKodawariJyoukenInfo(kaisyaCd, uketukeNo,
			                                                                                  kibouRirekiSeq);
			List<Map<String, Object>> kodawariJyouken = (List<Map<String, Object>>) kodawariJyoukenData.get("out_kodawari");
			if (kodawariJyouken != null && kodawariJyouken.size() > 0) {
				// こだわり条件(必須)
				List<String> kodawariHissuInfo = new ArrayList<String>();
				// こだわり条件
				List<String> kodawariJyoukenInfo = new ArrayList<String>();
				kodawariCheck(kodawariJyouken, kodawariHissuInfo, kodawariJyoukenInfo);
				ukg03012Bean.setKodawariJyoukenHissu(kodawariHissuInfo);
				ukg03012Bean.setKodawariJyouken(kodawariJyoukenInfo);

				/*2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正*/
				StringBuffer hissuJouken = new StringBuffer();
				StringBuffer fuhitsuyouJouken = new StringBuffer();
				Map<String, Object> joukenMap = new HashMap<String, Object>();
				for(int j = 0; j < kodawariHissuInfo.size(); j++) {
					hissuJouken.append(kodawariHissuInfo.get(j));
					if ((j < kodawariHissuInfo.size() - 1 && kodawariHissuInfo.size() > 1) || (kodawariHissuInfo.size() > 0 && kodawariJyoukenInfo.size() > 0)) {
						hissuJouken.append("、");
					}
				}
				for(int j = 0; j < kodawariJyoukenInfo.size(); j++) {
					fuhitsuyouJouken.append(kodawariJyoukenInfo.get(j));
					if (j < kodawariJyoukenInfo.size() - 1 && kodawariJyoukenInfo.size() > 1) {
						fuhitsuyouJouken.append("、");
					}
				}
				joukenMap.put("hissuJouken", hissuJouken);
				joukenMap.put("fuhitsuyouJouken", fuhitsuyouJouken);
				kibouJoukenList.add(joukenMap);
				/*2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正*/
			}

			/*2021/01/04 ADD START 楊棟(SYS) C45019_社長指示デザイン修正*/
			ukg03012Bean.setKibouJouken(kibouJoukenList);
			/*2021/01/04 ADD END 楊棟(SYS) C45019_社長指示デザイン修正*/
		}
		if (ukg03012Bean == null) {
			ukg03012Bean = new UKG03012Bean();
		}
		ukg03012Bean.setTourokuFlg(torokuFlg);

		// 画面状況データ
		session.setAttribute(Consts.UKG03012BEAN_SESSION_KEY, ukg03012Bean);
		return successForward();
	}

	/**
	 * <pre>
	 * [説 明]こだわり条件(必須)リストとこだわり条件リストの変数を作成
	 * @param kodawariJyouken    こだわリ条件データ
	 * @param kodawariHissuInfo  こだわり条件(必須)リスト
	 * @param kodawariJyoukenInfo  こだわり条件リスト
	 * </pre>
	 */
	private void kodawariCheck(List<Map<String, Object>> kodawariJyouken,
	                           List<String> kodawariHissuInfo,
	                           List<String> kodawariJyoukenInfo) {

		Iterator<Map<String, Object>> iter = kodawariJyouken.iterator();
		while (iter.hasNext()) {
			Map<String, Object> mapData = iter.next();
			if ("1".equals(mapData.get("SETUBIHISSUKB"))) {
				// 2018/02/02 張陽陽(SYS)  UPD START 案件C42036デザイン変更新規作成
				//kodawariHissuInfo.add("> " + mapData.get("SETUBISBNAME"));
				kodawariHissuInfo.add("" + mapData.get("SETUBISBNAME"));
			} else if ("0".equals(mapData.get("SETUBIHISSUKB"))) {
				//kodawariJyoukenInfo.add("> " + mapData.get("SETUBISBNAME"));
				kodawariJyoukenInfo.add("" + mapData.get("SETUBISBNAME"));
				// 2018/02/02 張陽陽(SYS)  UPD END  案件C42036デザイン変更新規作成
			}
		}
	}
}
