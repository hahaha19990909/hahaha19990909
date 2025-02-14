/**
 * @システム名: 受付システム
 * @ファイル名: ICookiesApiService.java
 * @更新日付: 2019/07/03
 * @Copyright: 2017 token corporation All right reserved
 */
package jp.co.token.uketuke.service;


/**
 * <pre>
 * [機 能] Cookie認証APIサービス
 * [説 明] API実行処理
 * @author [作 成] 2019/07/03 程旋(SYS) C43100_お客様項目の入力簡素化対応
 * </pre>
 */
public interface ICookiesApiService {

    /**
     * <pre>
      * [機 能] クッキー認証APIをCallし、結果を返却する
      * [説 明]
      * [備 考]
      * @param aTarget 処理対象
      * @param aRequest リクエストパラメータ(Bean)
      * @param aResponseClass レスポンスクラス
      * @return Call結果の格納されているレスポンスBean
     * </pre>
     */
    public <T, E> E callCookieNisnsyo(String aTarget, T aRequest, Class<E> aResponseClass)throws Exception;
}
