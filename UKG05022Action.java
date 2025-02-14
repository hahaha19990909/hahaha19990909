/**
 * @システム名: 受付システム
 * @ファイル名: UKG05022Action.java
 * @更新日付： 2014/1/8
 * @Copyright: 2014 token corporation All right reserved
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
import jp.co.token.uketuke.formbean.KibouJyoukennBean;
import jp.co.token.uketuke.service.IUKG05022Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 新着空室物件
 * [説 明] 新着空室物件アクション。
 * @author [作 成] 2014/1/8 SYS_温 (案件C37075)オリジナル情報誌リプレース
 * @author [変 更] 2018/02/14 SYS_孫博易 (案件C42036)デザイン変更
 * </pre>
 */
public class UKG05022Action extends BaseAction {

	/** クラスのシリアル化ID */
	private static final long serialVersionUID = 160936605194328679L;
	/** 新着空室物件一覧サービス */
	private IUKG05022Service ukg05022Services = null;
	/** 画面のデータ */
	private Map<String, Object> ukg05022Data = new HashMap<String, Object>();
	/** 空室日数 */
	private final static String KUUSHITUNITISUU = PropertiesReader.getIntance().
			getProperty(Consts.UKG05022_KUUSHITUNITISUU);

	/**<pre>
	 * [説 明] チラシ作成一覧サービス対象を設定する。
	 * @param ukg05022Service サービス対象
	 * </pre>
	 */
	public void setUkg05022Services(IUKG05022Service ukg05022Services) {
		this.ukg05022Services = ukg05022Services;
	}

	/**
	 * [説 明] 画面のデータを取得する。
	 * @return ukg05022Data 画面のデータ
	 */
	public Map<String, Object> getUkg05022Data() {
		return ukg05022Data;
	}

	/**<pre>
	 * [説 明] 画面のデータを設定する。
	 * @param ukg05022Data 画面のデータ
	 * </pre>
	 */
	public void setUkg05022Data(Map<String, Object> ukg05022Data) {
		this.ukg05022Data = ukg05022Data;
	}

	/**
	 * <pre>
	 * [説 明] 初期検索を行う。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	 public String execute() throws Exception {
		// 同期フラグ設定
		 setAsyncFlg(true);
		// セッションを取得
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpSession session = request.getSession();

		 //セッションのログイン情報取得する
		 Map<String, Object> loginInfo = getLoginInfo();
		 //会社コード
		 String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		 //店鋪コード
		 String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));

		 String strPresetNo = Util.toEmpty(request.getParameter("presetNo"));
		 //希望条件データを取得
		 KibouJyoukennBean kibouJyoukennBean = getKibojyoukenData();
		 session.setAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo, kibouJyoukennBean);
		 //ソート順
		 session.setAttribute(Consts.UKG05022_SORT_INFO,null);
		 String strSortJun = SortJun(session);
		 //初期化ページ
		 String currentPageNum = String.valueOf(Consts.ONE);
		 //新着空室物件データリストを取得する
		 Map<String, Object> shincyakuInfo = ukg05022Services.getshincyakuInfoService(kaisyacd,postcd,kibouJyoukennBean,KUUSHITUNITISUU,strSortJun,currentPageNum);
		 session.setAttribute(Consts.UKG05022_PAGE_NUM,currentPageNum);
		 //フォーマット物件No
		 List<Map<String, Object>> shincyakuInfoList = changeBukkenNO((List<Map<String, Object>>)shincyakuInfo.get("rst_list"));
		 ukg05022Data.put("shincyakuInfoList", shincyakuInfoList);
		 ukg05022Data.put("shincyakuInfoCount", shincyakuInfo.get("rst_recordcount"));
		 ukg05022Data.put("kibouJyoukennBean", kibouJyoukennBean);
		 ukg05022Data.put("strPresetNo", strPresetNo);
		// 2018/02/14 SYS_孫博易 ADD START (案件番号C42036)デザイン変更
		String prefectureCd = kibouJyoukennBean.getPrefectureCd();
		if (prefectureCd == null || "".equals(prefectureCd)) {
			Map<String, Object> uketukePostInfo = getUketukePostInfo();
			prefectureCd = (String) uketukePostInfo.get(Consts.POST_JUSYO_CD);
		}
		ukg05022Data.put("prefectureCd", prefectureCd);
		// 2018/02/14 SYS_孫博易 ADD END (案件番号C42036)デザイン変更
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
		 //セッションを取得
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpSession session = request.getSession();

		 //ページに分ける条件を取得
		 String condition = request.getParameter("condition");
		 Map<String, String> paramMap = Util.getParamMap(condition);
		 String currentPageNum = paramMap.get("pageNum");

		 //一覧ページＮＵＭ
		 session.setAttribute(Consts.UKG05022_PAGE_NUM, currentPageNum);
		 //セッションのログイン情報取得する
		 Map<String, Object> loginInfo = getLoginInfo();
		 //会社コード
		 String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		 //店鋪コード
		 String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));
		 // ソート順取得する
		 String sortItem = String.valueOf(session.getAttribute(Consts.UKG05022_SORT_INFO));
		 // 希望条件データを取得
		 String strPresetNo = paramMap.get("strPresetNo");
		 KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
		 //新着空室物件データリストを取得する
		 Map<String, Object> shincyakuInfo = ukg05022Services.getshincyakuInfoService(kaisyacd, postcd,kibouJyoukennBean,KUUSHITUNITISUU,sortItem,currentPageNum);
		 //フォーマット物件No
		 List<Map<String, Object>> shincyakuInfoList = changeBukkenNO((List<Map<String, Object>>)shincyakuInfo.get("rst_list"));
		 ukg05022Data.put("shincyakuInfoList", shincyakuInfoList);
		 ukg05022Data.put("shincyakuInfoCount", shincyakuInfo.get("rst_recordcount"));
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
		//セッションを取得
 		HttpServletRequest request = ServletActionContext.getRequest();
 		HttpSession session = request.getSession();

 		//セッションのログイン情報取得する
 		Map<String, Object> loginInfo = getLoginInfo();
 		//会社コード
 		String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
 		//店鋪コード
 		String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));
 		//希望条件データを取得
 		String strPresetNo = Util.toEmpty(request.getParameter("strPresetNo"));
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
 		//新しいソート順を取得する
 		String oldSort = request.getParameter("oldSort").replaceAll("[|]", "");
 		//古いソート順を取得する
 		String newSort = request.getParameter("newSort");
 		String sorttirasi = (String)session.getAttribute(Consts.UKG05022_SORT_INFO);
 		//ソート順処理
 		if(!Util.isEmpty(oldSort) && !Util.isEmpty(newSort)){
 			if(sorttirasi.startsWith(oldSort)){
 				sorttirasi = sorttirasi.replaceAll(oldSort, newSort);
 			}else{
 				if(sorttirasi.endsWith(oldSort)){
 					sorttirasi = sorttirasi.replaceAll("," + oldSort, "");
 				}else{
 					sorttirasi = sorttirasi.replaceAll(oldSort + ",", "");
 				}
 	 			sorttirasi = newSort + "," + sorttirasi;
 			}
 		}
 		session.setAttribute(Consts.UKG05022_SORT_INFO, sorttirasi);
 		//初期化ページ
		String currentPageNum = String.valueOf(Consts.ONE);
		//新着空室物件データリストを取得する
		Map<String, Object> shincyakuInfo = ukg05022Services.getshincyakuInfoService(kaisyacd, postcd, kibouJyoukennBean, KUUSHITUNITISUU, sorttirasi, currentPageNum);
		//フォーマット物件No
		List<Map<String, Object>> shincyakuInfoList = changeBukkenNO((List<Map<String, Object>>) shincyakuInfo.get("rst_list"));
 		ukg05022Data.put("shincyakuInfoList", shincyakuInfoList);
 		ukg05022Data.put("shincyakuInfoCount", shincyakuInfo.get("rst_recordcount"));
 		return "ajaxProcess";
 	}

 	/**
	 *　物件№を変換する
	 * @param shincyakuInfo 新着空室物件データ
	 * @return shincyakuInfo 新着空室物件データ
	 */
	private List<Map<String, Object>> changeBukkenNO(List<Map<String, Object>> rstXinzheInfo) {
	    if (rstXinzheInfo != null && rstXinzheInfo.size() > 0) {
			for (Map<String, Object> tirasiinfo : rstXinzheInfo) {
				tirasiinfo.put("ESTATE_BUKKEN_NO", DispFormat.getEstateBukkenNo((String) tirasiinfo.get("ESTATE_BUKKEN_NO")));
			}
		}
	    return rstXinzheInfo;
    }

	/**
	 * <pre>
	 * [説 明] 希望条件データを取得
	 * @return 希望条件データ
	 * </pre>
	 */
	private KibouJyoukennBean getKibojyoukenData () {
		//セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		//引数を取得
		String strPresetNo = request.getParameter("presetNo");
		//セッションのログイン情報取得する
		Map<String, Object> mapLoginInfo = getLoginInfo();
		//会社コード
		String strKaisyaCd = (String)mapLoginInfo.get(Consts.KAISYA_CD);
		//店鋪コード
		String strPostCd = (String)mapLoginInfo.get(Consts.POST_CD);
		//希望条件データを設定
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
		if (null == kibouJyoukennBean) {
			kibouJyoukennBean = new KibouJyoukennBean();
		}
		//会社を設定
		kibouJyoukennBean.setKaisyaCd(strKaisyaCd);
		//店鋪を設定
		kibouJyoukennBean.setPostCd(strPostCd);
		//画面区分を設定
		kibouJyoukennBean.setGamenKubun(Consts.UKG05022_GAMEN_KB);

		return kibouJyoukennBean;
	}

	/**
	 * <pre>
	 * [説 明] 希望条件を取得。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	public String kogamenDataSet() throws Exception {
		//セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		String strPresetNo = Util.toEmpty(request.getParameter("strPresetNo"));

		// 希望条件データを取得
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
		session.setAttribute(Consts.KIBOUJYOUKENBEAN, kibouJyoukennBean);
		ukg05022Data.put("kibouJyoukennBean", kibouJyoukennBean);
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 希望条件をクリア。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	@SuppressWarnings("unchecked")
	public String clearKiboJyouken() throws Exception {
		//セッションを取得
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		//引数を取得
		String strPresetNo = Util.toEmpty(request.getParameter("strPresetNo"));
		String kiboCd = Util.toEmpty(request.getParameter("kiboCd"));

		//希望条件データを取得
		KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
		//希望条件をクリア
		kibouJyoukennBean.setKibouJyoukennCode(null);
		kibouJyoukennBean.setKiboujyokenDisp("");
		kibouJyoukennBean.setPrefectureCd("");
		if("1".equals(kiboCd)) {
			kibouJyoukennBean.setJusyohaniBean(null);
		}else if("2".equals(kiboCd)) {
			kibouJyoukennBean.setJyusyoList(null);
			kibouJyoukennBean.setJyusyoYuubinBangouList(null);
		}else if("3".equals(kiboCd)) {
			kibouJyoukennBean.setEkiList(null);
		}
		session.setAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo, kibouJyoukennBean);

		//セッションのログイン情報取得する
		Map<String, Object> loginInfo = getLoginInfo();
		//会社コード
		String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		//店鋪コード
		String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));

		session.setAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo, kibouJyoukennBean);
		//ソート順
		String strSort = SortJun(session);
		//初期化ページ
		String currentPageNum = String.valueOf(Consts.ONE);
		// 新着空室物件データリストを取得する
		Map<String, Object> shincyakuInfo = ukg05022Services.getshincyakuInfoService(kaisyacd, postcd,kibouJyoukennBean,KUUSHITUNITISUU,strSort,currentPageNum);
		session.setAttribute(Consts.UKG05022_PAGE_NUM,currentPageNum);
		//フォーマット物件No
		List<Map<String, Object>> shincyakuInfoList = changeBukkenNO((List<Map<String, Object>>)shincyakuInfo.get("rst_list"));
		ukg05022Data.put("shincyakuInfoList", shincyakuInfoList);
		ukg05022Data.put("shincyakuInfoCount", shincyakuInfo.get("rst_recordcount"));
		ukg05022Data.put("kibouJyoukennBean", kibouJyoukennBean);
		ukg05022Data.put("strPresetNo", strPresetNo);
		return "ajaxProcess";
	}

	/**
	 * <pre>
	 * [説 明] 希望条件検索を行う
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * <pre>
	 */
	@SuppressWarnings("unchecked")
	public String searchBukken() throws Exception {
		//セッションを取得
		 HttpServletRequest request = ServletActionContext.getRequest();
		 HttpSession session = request.getSession();

		 //セッションのログイン情報取得する
		 Map<String, Object> loginInfo = getLoginInfo();
		 //会社コード
		 String kaisyacd = String.valueOf(loginInfo.get(Consts.KAISYA_CD));
		 //店鋪コード
		 String postcd = String.valueOf(loginInfo.get(Consts.POST_CD));

		 String strPresetNo = Util.toEmpty(request.getParameter("strPresetNo"));
		 KibouJyoukennBean kibouJyoukennBean = (KibouJyoukennBean) session.getAttribute(Consts.KIBOUJYOUKENBEAN + strPresetNo);
		 //ソート順
		 String strSearchSort = SortJun(session);
		 //初期化ページ
		 String currentPageNum = String.valueOf(Consts.ONE);
		 //新着空室物件データリストを取得する
		 Map<String, Object> shincyakuInfo = ukg05022Services.getshincyakuInfoService(kaisyacd, postcd,kibouJyoukennBean,KUUSHITUNITISUU,strSearchSort,currentPageNum);
		 session.setAttribute(Consts.UKG05022_PAGE_NUM,currentPageNum);
		 //フォーマット物件No
		 List<Map<String, Object>> shincyakuInfoList = changeBukkenNO((List<Map<String, Object>>)shincyakuInfo.get("rst_list"));
		 ukg05022Data.put("shincyakuInfoList", shincyakuInfoList);
		 ukg05022Data.put("shincyakuInfoCount", shincyakuInfo.get("rst_recordcount"));
		 ukg05022Data.put("kibouJyoukennBean", kibouJyoukennBean);
		 return "ajaxProcess";
	}

	 /**
	 * <pre>
	 * [説 明] 初期ソート順共通。
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	 public String SortJun(HttpSession session) throws Exception{
		 //ソート順
		 String strSort = "";
		 if(session.getAttribute(Consts.UKG05022_SORT_INFO) != null){
			// ソート順を設定する
			 strSort = (String)session.getAttribute(Consts.UKG05022_SORT_INFO);
		 }else{
			 // ソート順を設定する
			 strSort = Consts.UKG05022_SORT_JYUN;
			 session.setAttribute(Consts.UKG05022_SORT_INFO, Consts.UKG05022_SORT_JYUN);
		 }
		 return strSort;
	 }
}
