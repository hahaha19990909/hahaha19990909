/**
 * @システム名: 受付システム
 * @ファイル名: UKG99002Action.java
 * @更新日付：: 2012/05/08
 * @Copyright: 2012 token corporation All right reserved
 * 更新履歴：2013/02/16	趙雲剛(SYS)	  1.01	(案件番号C37058)受付システム要望対応
 *           2014/01/09 宮本          1.02  Jtestソース抑制(対応番号C37058-00006)
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG99002Service;

import org.apache.struts2.ServletActionContext;

/**
 * <pre>
 * [機 能] 画像一覧表示
 * [説 明] 画像一覧で表示する
 * @author [作 成]
 * </pre>
 */
public class UKG99002Action extends BaseAction {

	/**
	 * クラスのシリアル化ID
	 */
	private static final long serialVersionUID = -5458276230349927909L;

	/** 画像一覧画面用データ */
	private Map<String, Object> ukg99002Data = null;
	
    private IUKG99002Service UKG99002Services;
    
    /**
     * @param uKG99002Services
     *            the uKG99002Services to set
     */
    public void setUKG99002Services(IUKG99002Service uKG99002Services) {

        UKG99002Services = uKG99002Services;
    }

	/**
	 * @return 画像一覧画面用データ
	 */
	public Map<String, Object> getUkg99002Data() {

		return ukg99002Data;
	}

	/**
	 * @param ukg99002Data
	 *            　画像一覧画面用データ
	 */
	public void setUkg99002Data(Map<String, Object> ukg99002Data) {

		this.ukg99002Data = ukg99002Data;
	}

	/**
	 * <pre>
	 * [説 明] 画像一覧で表示する。
	 * @return アクション処理結果
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
		String flag = request.getParameter("flag");
		// 画像データを取得
		List<Map<String, Object>> imgList;

		//2019/08/08  SYS_李強   ADD Start (案件C41024)パノラマ画像対応
		if(Util.isEmpty(flag)){
//			imgList = (List<Map<String, Object>>) session.getAttribute("imgInfoList");
			flag = "";
//		}else{
//			imgList = (List<Map<String, Object>>) session.getAttribute("imgInfoList_right");
		}
		
		// リクエストからパラメターを取得する
        String bukkenNo = (String)session.getAttribute("bukkenNo");
        String heyaNo = (String)session.getAttribute("heyaNo");
        // 2018/11/15 王一氷  UPD START 案件C41024-4_パノラマ画像対応
        /*Map<String, Object> imgListData = UKG99006Services.imageLazyLoad(
                bukkenNo, heyaNo);*/
        Map<String, Object> imgListData = UKG99002Services.getImageList(bukkenNo, heyaNo);
        // 2018/11/15 王一氷  UPD END 案件C41024-4_パノラマ画像対応
        imgList = (List<Map<String, Object>>) imgListData
                .get("rst_imgInfo");
        Util.setImgListPath(imgList);
		//2019/08/08  SYS_李強   ADD Start (案件C41024)パノラマ画像対応

		// 画像区分
		String imgKuBun = request.getParameter("imageKbn");
		// メイン画像No
		int imgThmNo = 0;
	    int imgTotalNum = imgList.size();
		if ("0".equals(imgKuBun)) {
			// 選択画像No
			imgThmNo = Integer.valueOf(request.getParameter("imgNo"));
		} else if ("1".equals(imgKuBun) || "3".equals(imgKuBun)) {
			for (Map<String, Object> imgData : imgList) {
				if (imgData.get("IMGKBN").equals(imgKuBun)) {
					imgThmNo = imgList.indexOf(imgData)+1;
					break;
				}
			}
		}

		ukg99002Data = new HashMap<String, Object>();
		ukg99002Data.put("imgNo", imgThmNo);
		ukg99002Data.put("imgTotalNum", imgTotalNum);
		ukg99002Data.put("imgList", imgList);
		ukg99002Data.put("flag", flag);
		return successForward();
	}

	// ########## 2013/02/16 趙雲剛 DEL Start (案件C37058)受付システム要望対応
	/**
	 * <pre>
	 * [説 明] 
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */
	/**
    C37058-00006抑制のため全コメント削除
	*/
	// ########## 2013/02/16 趙雲剛 DEL End
	/**
	 * <pre>
	 * [説 明] wmfを作成する
	 * @return アクション処理結果
	 * @throws Exception　処理異常
	 * </pre>
	 */

    public String bigImgLoad() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		ukg99002Data = new HashMap<String, Object>();
		float  imgWidtd = 600f;
		float  imgHeight = 383f;
		String path = request.getParameter("src");
		if (!Util.isEmpty(path) && "ISO-8859-1".equals(Util.getEncoding(path))) {
			path = new String(path.getBytes("ISO-8859-1"), "UTF-8");
		}
		ukg99002Data.put("src", Util.wmfToSVG(path,imgWidtd,imgHeight));
		return "ajaxProcess";
	}
}
