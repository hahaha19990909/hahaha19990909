/**
 * @システム名: 受付システム
 * @ファイル名: UKG03014Action.java
 * @更新日付：: 2018/01/12
 * @Copyright: 2018 token corporation All right reserved
 * 更新履歴：2018/01/12 張陽陽(SYS)        1.0        案件C42036デザイン変更新規作成
 */
package jp.co.token.uketuke.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;
import jp.co.token.fw.base.BaseAction;
import jp.co.token.uketuke.common.Util;
import jp.co.token.uketuke.service.IUKG99002Service;

/**
 * <pre>
 * [機 能] 画像一覧表示
 * [説 明] 画像一覧で表示する
 * @author [作 成]
 * </pre>
 */
public class UKG03014Action extends BaseAction {

    /**
     * クラスのシリアル化ID
     */
    private static final long serialVersionUID = -5458276230349927909L;

    /** ログオブジェクト. */
    protected Log log = LogFactory.getLog(UKG03014Action.class);

    /** 画像一覧画面用データ */
    private Map<String, Object> ukg03014Data = null;

    // 2018/08/15 SYS_孫博易 ADD START   (案件番号C42036)デザイン変更 bug29
    /** 物件情報詳細サービス */
    // 2018/12/26 王一氷  UPD START 案件C41024-4_パノラマ画像対応
    /*private IUKG99006Service UKG99006Services;*/
    
    /**
     * @param uKG99006Services
     *            the uKG99006Services to set
     */
    /*public void setUKG99006Services(IUKG99006Service uKG99006Services) {

        UKG99006Services = uKG99006Services;
    }*/
    private IUKG99002Service UKG99002Services;
    
    /**
     * @param uKG99002Services
     *            the uKG99002Services to set
     */
    public void setUKG99002Services(IUKG99002Service uKG99002Services) {

        UKG99002Services = uKG99002Services;
    }
    // 2018/12/26 王一氷  UPD END 案件C41024-4_パノラマ画像対応
    // 2018/08/15 SYS_孫博易 ADD END   (案件番号C42036)デザイン変更 bug29
    
    /**
     * @return 画像一覧画面用データ
     */
    public Map<String, Object> getUkg03014Data() {

        return ukg03014Data;
    }

    /**
     * @param ukg03014Data 画像一覧画面用データ
     */
    public void setUkg03014Data(Map<String, Object> ukg03014Data) {

        this.ukg03014Data = ukg03014Data;
    }

    /**
     * <pre>
     * [説 明] 画像一覧で表示する。
     * @return アクション処理結果
     * @throws Exception 処理異常
     * </pre>
     */
    @SuppressWarnings("unchecked")
    @Override
    public String execute() throws Exception {

        log.debug("UKG03014Actionを呼び出し 開始");

        // 同期フラグ設定
        setAsyncFlg(true);

        HttpServletRequest request = ServletActionContext.getRequest();
        // 2018/12/26  薛  DEL START 案件C41024-4_パノラマ画像対応
        /*String flag = request.getParameter("flag");*/
        // 2018/12/26  薛  DEL END 案件C41024-4_パノラマ画像対応
        // 画像データを取得
        List<Map<String, Object>> imgList;
        // 2018/08/15 SYS_孫博易 UPDATE START   (案件番号C42036)デザイン変更 bug29
//        if(Util.isEmpty(flag)){
//            imgList = (List<Map<String, Object>>) session.getAttribute("imgInfoList");
//            flag = "";
//        }else{
//            imgList = (List<Map<String, Object>>) session.getAttribute("imgInfoList_right");
//        }
        // リクエストからパラメターを取得する
        String bukkenNo = request.getParameter("bukkenNo");
        String heyaNo = request.getParameter("heyaNo");
        // 2018/11/15 王一氷  UPD START 案件C41024-4_パノラマ画像対応
        /*Map<String, Object> imgListData = UKG99006Services.imageLazyLoad(
                bukkenNo, heyaNo);*/
        Map<String, Object> imgListData = UKG99002Services.getImageList(bukkenNo, heyaNo);
        // 2018/11/15 王一氷  UPD END 案件C41024-4_パノラマ画像対応
        imgList = (List<Map<String, Object>>) imgListData
                .get("rst_imgInfo");
        Util.setImgListPath(imgList);
        // 2018/08/15 SYS_孫博易 UPDATE START   (案件番号C42036)デザイン変更 bug29
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

        ukg03014Data = new HashMap<String, Object>();
        ukg03014Data.put("imgNo", imgThmNo);
        ukg03014Data.put("imgTotalNum", imgTotalNum);
        ukg03014Data.put("imgList", imgList);
        // 2018/12/26  薛  DEL START 案件C41024-4_パノラマ画像対応
        /*ukg03014Data.put("flag", flag);*/
        // 2018/12/26  薛  DEL END 案件C41024-4_パノラマ画像対応

        log.debug("UKG03014Actionを呼び出し 終了");

        return successForward();
    }

    /**
     * <pre>
     * [説 明] wmfを作成する
     * @return アクション処理結果
     * @throws Exception 処理異常
     * </pre>
     */
    public String bigImgLoad() throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        ukg03014Data = new HashMap<String, Object>();
        float  imgWidtd = 600f;
        float  imgHeight = 383f;
        String path = request.getParameter("src");
        if (!Util.isEmpty(path) && "ISO-8859-1".equals(Util.getEncoding(path))) {
            path = new String(path.getBytes("ISO-8859-1"), "UTF-8");
        }
        ukg03014Data.put("src", Util.wmfToSVG(path,imgWidtd,imgHeight));
        return "ajaxProcess";
    }
}