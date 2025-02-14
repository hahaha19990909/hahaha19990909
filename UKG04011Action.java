/**
 * @システム名: 受付システム
 * @ファイル名: UKG04011Action.java
 * @更新日付：: 2013/2/28
 * @Copyright: 2013 token corporation All right reserved
 * 更新履歴：2013/06/19	郭凡(SYS)		1.01		案件C37103-1（チラシ作成機能の追加-STEP2）
 * 更新履歴：2018/02/12	肖剣生(SYS)		1.02		案件C42036(デザイン変更新規作成)
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
import jp.co.token.uketuke.common.PropertiesReader;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG04011Service;
import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] チラシ作成(一覧)
 * [説 明] チラシ作成(一覧)アクション。
 * @author [作 成] 2013/02/28 謝超(SYS)
 * </pre>
 */
public class UKG04011Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = -5629458236532919212L;
	/** チラシ作成一覧サービス */
	private IUKG04011Service ukg04011Services = null;
	/** 画面のデータ */
	private Map<String,Object> ukg04011Data = new HashMap<String,Object>();

	/** 空室日数 */
	private final static String KUUSHITUNITISUU = PropertiesReader.getIntance().
			getProperty(Consts.UKG04011_KUUSHITUNITISUU);

	/**<pre>
	 * [説 明] チラシ作成一覧サービス対象を設定する。
	 * @param ukg04011Service サービス対象
	 * </pre>
	 */
	public void setUkg04011Services(IUKG04011Service ukg04011Services) {
		this.ukg04011Services = ukg04011Services;
	}

	/**
	 * [説 明] 画面のデータを取得する。
	 * @return ukg04011Data 画面のデータ
	 */
	public Map<String, Object> getUkg04011Data() {
		return ukg04011Data;
	}

	/**<pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg04011Data 画面のデータ
	 * </pre>
	 */
	public void setUkg04011Data(Map<String, Object> ukg04011Data) {
		this.ukg04011Data = ukg04011Data;
	}

	/**
	 * <pre>
	 * [説 明] 初期検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@Override
	@SuppressWarnings("unchecked")
	public String execute() throws Exception {

		// 同期フラグ設定
		setAsyncFlg(true);

		HttpServletRequest request = ServletActionContext.getRequest();

		// セッションのログイン情報取得する
		Map<String, Object> loginInfo = getLoginInfo();

		// 受付番号をセッションに格納する
		HttpSession session = request.getSession();

		String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));
		
		//2013/06/19   郭凡   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2）
		//自事業所チェックフラグ
		String  jiJigyousyoFlg = "0";
		if(session.getAttribute(Consts.UKG04011_JIJIGYOUSYO_FLG) != null){
			jiJigyousyoFlg = (String)session.getAttribute(Consts.UKG04011_JIJIGYOUSYO_FLG);
		}else{
			session.setAttribute(Consts.UKG04011_JIJIGYOUSYO_FLG, jiJigyousyoFlg);
		}
		ukg04011Data.put("jiJigyousyoFlg", jiJigyousyoFlg);
		//2013/06/19   郭凡   ADD END  案件C37103-1

		//ページＮｏ設定
		String strPageNum = "";
		//行No
		int startRowNum;
		int endRowNum;
		
		//2018/02/11   肖剣生(SYS)   UPD START 案件C42036(デザイン変更新規作成)
//		if(session.getAttribute(Consts.UKG04011_PAGE_NUM) != null){
//			strPageNum = (String)session.getAttribute(Consts.UKG04011_PAGE_NUM);
//			startRowNum = (Integer.parseInt(strPageNum) - 1)  *  Consts.FIFTTEN + 1;
//			endRowNum = startRowNum + Consts.FOURTTEN;
//		}else{
//			strPageNum = String.valueOf(Consts.ONE);
//			startRowNum = Consts.ONE;
//			endRowNum = Consts.FIFTTEN;
//		}
//		//ページＮｏ．
//		session.setAttribute("pageNum",strPageNum);
		startRowNum = Consts.ONE;
		endRowNum = Consts.MAX;
		//2018/02/11   肖剣生(SYS)   UPD END

		//ソート順
		String strSort = "";
		if(session.getAttribute(Consts.UKG04011_SORT_INFO) != null){
			// ソート順を設定する
			strSort = (String)session.getAttribute(Consts.UKG04011_SORT_INFO);
		}else{
			// ソート順を設定する
			strSort = Consts.UKG04011_SORT_JYUN;
			session.setAttribute(Consts.UKG04011_SORT_INFO, Consts.UKG04011_SORT_JYUN);
		}

		//2013/06/19   郭凡   UPD START 案件C37103-1（チラシ作成機能の追加-STEP2） 
		// チラシ作成データリストを取得する
		/**
		Map<String, Object> tirasiinfo = ukg04011Services
				.getTirasiInfo(kaisyacd, postcd, KUUSHITUNITISUU, strSort, startRowNum, endRowNum);
		*/
		Map<String, Object> tirasiinfo = ukg04011Services
				.getTirasiInfo(kaisyacd, postcd, KUUSHITUNITISUU, jiJigyousyoFlg, strSort, startRowNum, endRowNum);
		//2013/06/19    郭凡   UPD END  案件C37103-1

		List<Map<String, Object>> tirasiinfoList = changeBukkenNO((List<Map<String, Object>>)tirasiinfo.get("rst_list"));
		ukg04011Data.put("tirasiinfoList", tirasiinfoList);
		ukg04011Data.put("tirasiInfoCount", tirasiinfo.get("rst_recordcount"));

		return successForward();
	}

	/**
	 * <pre>
	 * [説 明] ページ切り替える。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String pageIndexChg() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		String condition = request.getParameter("condition");
		Map<String, String> paramMap = Util.getParamMap(condition);
		HttpSession session = request.getSession();

		// セッションのログイン情報取得する
		Map<String, Object> loginInfo = getLoginInfo();

		String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));

		//2018/02/11   肖剣生(SYS)   UPD START 案件C42036(デザイン変更新規作成)		
		// ページ切り替える機能用パラメータを取得する
//		int startRowNum = (Integer.parseInt(paramMap.get("pageNum")) - 1) * Consts.FIFTTEN + 1;
//		int endRowNum = startRowNum + Consts.FOURTTEN;		
		
		int startRowNum = Consts.ONE;
		int endRowNum = Consts.MAX;
		//2018/02/11   肖剣生(SYS)   UPD END

		// ソート順取得する
		String sorttirasi = String.valueOf(session.getAttribute(Consts.UKG04011_SORT_INFO));
		//一覧ページＮＵＭ
		session.setAttribute(Consts.UKG04011_PAGE_NUM, paramMap.get("pageNum"));
		
		//2013/06/19   郭凡   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2）
		//自事業所チェックフラグ
		String  jiJigyousyoFlg = (String)session.getAttribute(Consts.UKG04011_JIJIGYOUSYO_FLG);
		//2013/06/19   郭凡   ADD END  案件C37103-1
		
		//2013/06/19   郭凡   UPD START 案件C37103-1（チラシ作成機能の追加-STEP2） 
		// 業務処理（取得した条件で該当ページのデータを取得する）
		/**
		Map<String, Object> tirasiinfo = ukg04011Services
				.getTirasiInfo(kaisyacd, postcd, KUUSHITUNITISUU, sorttirasi, startRowNum, endRowNum);
		*/
		Map<String, Object> tirasiinfo = ukg04011Services
				.getTirasiInfo(kaisyacd, postcd, KUUSHITUNITISUU, jiJigyousyoFlg, sorttirasi, startRowNum, endRowNum);
		//2013/06/19    郭凡   UPD END  案件C37103-1
		
		List<Map<String, Object>> tirasiinfoList = changeBukkenNO((List<Map<String, Object>>)tirasiinfo.get("rst_list"));
		ukg04011Data.put("tirasiinfoList", tirasiinfoList);
		ukg04011Data.put("tirasiInfoCount", tirasiinfo.get("rst_recordcount"));

		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] ソート
	 * @return ソート処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String sort() throws Exception {

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		// セッションのログイン情報取得する
		Map<String, Object> loginInfo = getLoginInfo();

		String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));

		//行No
		//2018/02/11   肖剣生(SYS)   UPD START 案件C42036(デザイン変更新規作成)
//		int startRowNum = Consts.ONE;
//		int endRowNum = Consts.FIFTTEN;
		
		int startRowNum = Consts.ONE;
		int endRowNum = Consts.MAX;
		//2018/02/11   肖剣生(SYS)   UPD END

		// ソート順取得する
		String oldSort = request.getParameter("oldSort").replaceAll("[|]","");
		String newSort = request.getParameter("newSort");
		String sorttirasi = (String)session.getAttribute(Consts.UKG04011_SORT_INFO);
		
		//2013/06/19   郭凡   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2）
		if(!Util.isEmpty(oldSort) && !Util.isEmpty(newSort)){
		//2013/06/19    郭凡   ADD END  案件C37103-1	
			if(sorttirasi.startsWith(oldSort)) {
				sorttirasi = sorttirasi.replaceAll(oldSort, newSort);
			} else {
				if(sorttirasi.endsWith(oldSort)) {
					sorttirasi = sorttirasi.replaceAll("," + oldSort, "");
				} else {
					sorttirasi = sorttirasi.replaceAll(oldSort + ",", "");
				}
			}
			sorttirasi = newSort + "," + sorttirasi;
		//2013/06/19   郭凡   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2）
		}
		//2013/06/19    郭凡   ADD END  案件C37103-1	
		session.setAttribute(Consts.UKG04011_SORT_INFO, sorttirasi);
		
		//2013/06/19   郭凡   ADD START 案件C37103-1（チラシ作成機能の追加-STEP2）
		//自事業所チェックフラグ
		String  jiJigyousyoFlg = request.getParameter("jiJigyousyoFlg");
		session.setAttribute(Consts.UKG04011_JIJIGYOUSYO_FLG, jiJigyousyoFlg);
		//2013/06/19   郭凡   ADD END  案件C37103-1
		
		//2013/06/19   郭凡   UPD START 案件C37103-1（チラシ作成機能の追加-STEP2） 
		// 業務処理（取得した条件で該当ページのデータを取得する）
		/**
		Map<String, Object> tirasiinfo = ukg04011Services
				.getTirasiInfo(kaisyacd, postcd, KUUSHITUNITISUU, sorttirasi, startRowNum, endRowNum);
		*/
		Map<String, Object> tirasiinfo = ukg04011Services
				.getTirasiInfo(kaisyacd, postcd, KUUSHITUNITISUU, jiJigyousyoFlg, sorttirasi, startRowNum, endRowNum);
		//2013/06/19    郭凡   UPD END  案件C37103-1

		List<Map<String, Object>> tirasiinfoList = changeBukkenNO((List<Map<String, Object>>)tirasiinfo.get("rst_list"));
		ukg04011Data.put("tirasiinfoList", tirasiinfoList);
		ukg04011Data.put("tirasiInfoCount", tirasiinfo.get("rst_recordcount"));

		return "ajaxProcess";
	}

	/**
	 *　物件№を変換する
	 * @param rstTirasiinfo チラシ作成データ
	 * @return rstTirasiinfo チラシ作成データ
	 */
	private List<Map<String, Object>> changeBukkenNO(List<Map<String, Object>> rstTirasiinfo) {

		if (rstTirasiinfo != null && rstTirasiinfo.size() > 0) {
			for (Map<String, Object> tirasiinfo : rstTirasiinfo) {
				//2013/06/19   郭凡   UPD START 案件C37103-1（チラシ作成機能の追加-STEP2）
				//tirasiinfo.put("HOMEMATE_BUKKEN_NO_DISPLAY", DispFormat.getBukkenOtoiawaseNo((String) tirasiinfo.get("ESTATE_BUKKEN_NO"), (String) tirasiinfo.get("HEYA_NO")));
				tirasiinfo.put("HOMEMATE_BUKKEN_NO_DISPLAY", DispFormat.getEstateBukkenNo((String) tirasiinfo.get("ESTATE_BUKKEN_NO")));
				//2013/06/19   郭凡   UPD END  案件C37103-1
			}
		}
		return rstTirasiinfo;
	}
}
