/*2018/12/22 劉キ ADD Start (C41024-4)パノラマ画像対応*/
/**
 * @システム名: 受付システム
 * @ファイル名: UKG03015Action.java
 * @更新日付：: 2018/11/15
 * @Copyright: 2018 token corporation All right reserved
 * 更新履歴：2018/11/15 王一氷(SYS)  1.0  案件C41024-4_パノラマ画像対応
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
import jp.co.token.uketuke.service.IUKG99009Service;

/**
 * <pre>
 * [機 能] 物件情報(パノラマタブ)表示
 * [説 明] 物件情報(パノラマタブ)で表示する
 * @author [作 成]
 * </pre>
 */
public class UKG03015Action extends BaseAction {

    /**
     * クラスのシリアル化ID
     */
    private static final long serialVersionUID = -5458276230349927909L;

    /** ログオブジェクト. */
    protected Log log = LogFactory.getLog(UKG03015Action.class);

    /** 画像一覧画面用データ */
    private Map<String, Object> ukg03015Data = null;

    /** 物件情報詳細サービス */
    private IUKG99009Service UKG99009Services;

    /**
     * @param uKG99009Services the uKG99009Services to set
     */
    public void setUKG99009Services(IUKG99009Service uKG99009Services) {
        UKG99009Services = uKG99009Services;
    }

    /**
     * @return 画像一覧画面用データ
     */
    public Map<String, Object> getUkg03015Data() {
        return ukg03015Data;
    }

    /**
     * @param ukg03015Data 画像一覧画面用データ
     */
    public void setUkg03015Data(Map<String, Object> ukg03015Data) {

        this.ukg03015Data = ukg03015Data;
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

        log.debug("UKG03015Actionを呼び出し 開始");

        // 同期フラグ設定
        setAsyncFlg(true);

        HttpServletRequest request = ServletActionContext.getRequest();
        // 画像データを取得
        List<Map<String, Object>> imgList;
        // リクエストからパラメターを取得する
        String bukkenNo = request.getParameter("bukkenNo");
        String heyaNo = request.getParameter("heyaNo");
        Map<String, Object> imgListData = UKG99009Services.getPanoramaImageList(bukkenNo, heyaNo);
        imgList = (List<Map<String, Object>>) imgListData.get("rst_imgInfo");
        Util.setImgListPath(imgList);
        // メイン画像No
        int imgThmNo = 0;
        int imgTotalNum = imgList.size();
        imgThmNo = Integer.valueOf(request.getParameter("imgNo"));

        ukg03015Data = new HashMap<String, Object>();
        ukg03015Data.put("heyaNo", heyaNo);
        ukg03015Data.put("imgNo", imgThmNo);
        ukg03015Data.put("imgTotalNum", imgTotalNum);
        ukg03015Data.put("imgList", imgList);

        log.debug("UKG03015Actionを呼び出し 終了");

        return successForward();
    }
}
/*2018/12/22 劉キ ADD End (C41024-4)パノラマ画像対応*/