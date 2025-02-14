CREATE OR REPLACE PACKAGE BODY PKG_UKG02040 IS
  -- ---------------------------------------------------------
  -- システム名       ： 受付紹介システム
  -- パッケージID     ： PKG_UKG02040
  -- パッケージ名     ： 商談履歴
  -- 宣言内容         ： 商談履歴処理
  -- 初版作成日/作成者： 2012/03/22 Ver.1.0.0 SYS_馮強華
  -- 変更情報         ： 2017/07/31 Ver.1.0.1 MJEC)鈴村 (案件C41059)中止他決顧客の検索対応
  -- 変更情報         ： 2018/05/11 Ver.1.0.2 SYS_肖剣生 (案件C42036)デザイン変更対応
  -- 変更情報         ： 2019/07/04 Ver.1.0.3 SYS_熊振 C43100_お客様項目の入力簡素化対応
  -- 変更情報         ： 2019/09/25 Ver.1.0.4 SYS_熊振 C43089_来店日時の入力チェック対応
  -- 変更情報         ： 2020/06/24 Ver.1.0.5 SYS_劉恆毅 C43040-2_法人の入力項目追加対応
  -- 変更情報         ：　2021/01/18 Ver.1.0.6 SYS_南柯創　C45011_物件チラシメール送信
  -- 変更情報         ：　2021/03/29 Ver.1.0.7 SYS_楊振華 C45032_オンライン仲介システムの導入
  -- ---------------------------------------------------------

  -- *********************************
  --    定数宣言
  -- *********************************
  MSG_LOCKFAIL CONSTANT CHAR(7) := 'ERRS003';
  MSG_SUCCESS  CONSTANT CHAR(7) := '0000000';
  MSG_UNKNOWN  CONSTANT CHAR(7) := '1111111';
  -- ---------------------------------------------------------
    -- システム名       ： 受付紹介システム
    -- プロシージャID   ： PRC_GET_INIT_INFO
    -- プロシージャ名   ： 初期紹介情報取得
    -- 引数             ： param_kaisyacd      IN  VARCHAR2   -- 会社コード
    --                  ： param_uketukeno     IN  VARCHAR2   -- 受付No
    --                  ： param_sortitem      IN  VARCHAR2   -- ソート順
    --                  ： out_kibobkInfo      OUT  CURSOR    -- 制御情報
    --                  ： out_bukencount      OUT  NUMBER    -- 紹介データ数
    --                  ： out_bukenInfo       OUT  CURSOR    -- 紹介情報
    --                  ： rst_code            OUT  VARCHAR2  -- メッセージコード
    --                  ： rst_msg             OUT  VARCHAR2  -- メッセージ
    -- 処理概要         ： 紹介情報
    -- 初版作成日/作成者： 2012/03/28 Ver.1.0.0 SYS_馮強華
    -- 変更情報         ： 2017/07/31 Ver.1.0.1 MJEC)鈴村 (案件C41059)中止他決顧客の検索対応
    -- 変更情報         ： 2018/05/11 Ver.1.0.2 SYS_肖剣生 (案件C42036)デザイン変更対応
    -- ---------------------------------------------------------
    PROCEDURE PRC_GET_INIT_INFO
    (
        param_kaisyacd  IN VARCHAR2
       ,param_uketukeno IN VARCHAR2
       ,param_sortitem  IN VARCHAR2
       ,out_kibobkInfo  OUT typCSR
       ,out_bukencount  OUT NUMBER
       ,out_bukenInfo   OUT typCSR
       ,rst_code        OUT VARCHAR2
       ,rst_msg         OUT VARCHAR2
    ) IS
    BEGIN

        rst_code := MSG_SUCCESS;

        --制御データを取得する。
        OPEN out_kibobkInfo FOR
            SELECT KB.KIBOU_BUKKEN_KB            AS KIBOU_BUKKEN_KB --希望物件区分
                  ,KB.KIBOU_JYOUKEN_RIREKI_SEQ   AS KIBOU_JYOUKEN_RIREKI_SEQ
                  ,KB.KIBOU_HEYA_SETUBI_CD_HISSU AS KIBOU_HEYA_SETUBI_CD_HISSU
                  ,KB.KIBOU_HEYA_SETUBI_CD_NO_HISSU AS KIBOU_HEYA_SETUBI_CD_NO_HISSU
                  ,KB.KIBOU_JYOUKEN_SITEI_CD     AS KIBOU_JYOUKEN_SITEI_CD
                  ,RT.KOKYAKU_INFO_FLG           AS KOKYAKU_INFO_FLG
                  ,RT.ANKETO_FLG                 AS ANKETO_FLG
                  ,RT.KIBOU_FLG                  AS KIBOU_FLG
                  ,RT.UKETUKE_KB                 AS UKETUKE_KB
                  ,RT.KOKYAKU_SB                 AS KOKYAKU_SB
                  --2017/11/06 MJEC)鈴村 ADD Start (案件C38101-1)ホームメイト自動受付対応
                  ,RT.SAKUSEI_KB                 AS SAKUSEI_KB
                  --2017/11/06 MJEC)鈴村 ADD End
                  --2017/08/02 MJEC)鈴村 ADD Start (案件C41059)中止他決顧客の検索対応
                  ,RT.SINCHOKU_CD                AS SINCHOKU_KB
                  --2017/08/02 MJEC)鈴村 ADD End
              FROM UD_RAITEN_UKETUKE RT
              LEFT JOIN UD_KIBOU_JYOUKEN KB
                ON (KB.KAISYA_CD = RT.KAISYA_CD AND KB.UKETUKE_NO = RT.UKETUKE_NO AND KB.LOGICAL_DEL_FLG = '0')
             WHERE RT.KAISYA_CD = param_kaisyacd
               AND RT.UKETUKE_NO = param_uketukeno
               AND RT.LOGICAL_DEL_FLG = '0'
               AND (KB.KIBOU_JYOUKEN_RIREKI_SEQ IS NULL OR
                   KB.KIBOU_JYOUKEN_RIREKI_SEQ = (SELECT MAX(KB.KIBOU_JYOUKEN_RIREKI_SEQ) AS maxKibouRirekiSEQ
                                                     FROM UD_KIBOU_JYOUKEN KB
                                                    WHERE KB.KAISYA_CD = RT.KAISYA_CD
                                                      AND KB.UKETUKE_NO = RT.UKETUKE_NO
                                                      AND KB.LOGICAL_DEL_FLG = '0'
                                                    GROUP BY KB.KAISYA_CD
                                                            ,KB.UKETUKE_NO));
        --紹介情報を取得
        PRO_BUKEN_INFO_INIT(param_kaisyacd
                           ,param_uketukeno
                           ,'1'
                           ,1
                           -- 2018/05/11 UPD START SYS_肖剣生 (案件C42036)デザイン変更対応
                           --,20
                           ,9999
                           -- 2018/05/11 UPD END SYS_肖剣生 (案件C42036)デザイン変更対応
                           ,param_sortitem
                           ,out_bukencount
                           ,out_bukenInfo
                           ,rst_code
                           ,rst_msg);


    EXCEPTION
        WHEN OTHERS THEN
            rst_code := MSG_UNKNOWN;
            rst_msg  := SQLERRM;
    END;
    -- ---------------------------------------------------------
    -- システム名          ： 受付紹介システム
    -- プロシージャID      ： PRO_BUKEN_INFO_INIT
    -- プロシージャ名      ： 物件データを取得
    -- 引数            ： param_kaisyacd         IN   VARCHAR2   -- 会社コード
    --                 ： param_uketukeno        IN   VARCHAR2   -- 受付番号
    --                 ： param_searchitem       IN   VARCHAR2   -- 検索項目
    --                 ： param_startindex       IN   VARCHAR2   -- STARTデータ行番号
    --                 ： param_endindex         IN   VARCHAR2   -- ENDデータ行行番号
    --                 ： param_sortitem         IN   VARCHAR2   -- ソート項目
    --                 ： out_mitorokucount      OUT  NUMBER     -- 物件データ件数
    --                 ： out_mitorokuinfo       OUT  typCSR     -- 物件データ
    --                 ： rst_code               OUT  VARCHAR2   -- エラーメッセージ
    --                 ： rst_msg                OUT  VARCHAR2   -- メッセージID
    -- 戻り値           ： 0000000  ： 成功
    --                ： ERR9999  ： アプリケーションエラー
    -- 処理概要           ： 物件データを取得
    -- 初版作成日/作成者  ： 2011/10/19 Ver.1.0.0 SYS_馮強華
    -- 変更情報           ：2019/07/04 Ver.1.0.1 SYS_熊振 C43100_お客様項目の入力簡素化対応
    -- 変更情報           : 2021/01/14 Ver.1.0.2 SYS_南柯創 C45011_物件チラシメール送信
    -- ---------------------------------------------------------
    PROCEDURE PRO_BUKEN_INFO_INIT
    (
        param_kaisyacd   IN VARCHAR2 -- 会社コード
       ,param_uketukeno  IN VARCHAR2 -- 受付番号
       ,param_searchitem IN VARCHAR2 -- 検索項目
       ,param_startindex IN NUMBER -- STARTデータ行番号
       ,param_endindex   IN NUMBER -- ENDデータ行行番号
       ,param_sortitem   IN VARCHAR2 -- ソート項目
       ,out_bukencount   OUT NUMBER -- 物件データ件数
       ,out_bukenInfo    OUT typCSR -- 物件データ
       ,rst_code         OUT VARCHAR2
       ,rst_msg          OUT VARCHAR2
    ) IS
        -- 2019/07/04 SYS_熊振  ADD Start C43100_お客様項目の入力簡素化対応
        l_sql      VARCHAR2(3500); -- SQL文
        -- 2019/07/04 SYS_熊振  ADD End C43100_お客様項目の入力簡素化対応
        l_coutSql  VARCHAR2(1500); -- SQL文
        l_SqlW     VARCHAR2(1000); -- SQL文
        l_sqlOrder VARCHAR2(500); -- SQL文
        l_sqlIndex VARCHAR2(500); -- SQL文
    BEGIN

        --物件データ検索
        BEGIN

            l_sqlOrder := l_sqlOrder || ' ORDER BY  ' || param_sortitem;

            l_sql := 'SELECT *  FROM ( ';
            l_sql := l_sql || 'SELECT U.*,ROWNUM ROWINDEX FROM ( ';
            l_sql := l_sql || ' SELECT  ';
            l_sql := l_sql || ' T.HOMEMATE_BUKKEN_NO AS HOMEMATE_BUKKEN_NO ';
            l_sql := l_sql || ' ,BK.Bukken_Name   AS BUKKEN_NAME ';
            -- 2019/07/04 SYS_熊振  ADD Start C43100_お客様項目の入力簡素化対応
            l_sql := l_sql || ' ,UH.WARIBIKI_SERVICE_NAME   AS WARIBIKI_SERVICE_NAME ';
            -- 2019/07/04 SYS_熊振  ADD End C43100_お客様項目の入力簡素化対応
            l_sql := l_sql || ' ,T.HEYA_NO AS HEYA_NO ';
            l_sql := l_sql || ' ,TO_CHAR(T.SHOUKAI_DATE, ''yyyy/MM/dd'') AS SHOUKAI_DATE ';
            l_sql := l_sql || ' ,T.SHOUKAI_DATE  AS ORDER_SHOUKAI_DATE ';
            l_sql := l_sql || ' ,BK.ESTATE_BUKKEN_NO AS ESTATE_BUKKEN_NO ';
            l_sql := l_sql || ' ,T.OKINIIRI AS OKINIIRI ';
            l_sql := l_sql || ' ,CASE  ';
            l_sql := l_sql || ' WHEN T.NO_DISP_FLG = ''0'' THEN ''visible''   ';
            l_sql := l_sql || ' WHEN T.NO_DISP_FLG = ''1'' THEN ''unvisible''   ';
            l_sql := l_sql || ' END AS DISP_CD  ';
            l_sql := l_sql || ' ,T.MOUSIKOMI_KB AS MOUSIKOMI_KB  ';
            l_sql := l_sql || ' ,T.TIRASHI_PRINT_DATE  AS TIRASHI_PRINT_DATE ';
			-- 2021/01/04 add start C45011_物件チラシメール送信　南柯創(SYS)
            l_sql := l_sql || ' ,T.MAIL_SOUSHIN_DATE AS MAILSOUSHIN_DATE';
            -- 2021/01/04 add end C45011_物件チラシメール送信　南柯創(SYS)
            l_sql := l_sql || ' ,T.MITUMORISHO_FLG AS MITUMORISHO_FLG ';
            l_sql := l_sql || ' ,NVL(T.SHOUDAN_MEMO,'''')  AS SHOUDAN_MEMO ';
            l_sql := l_sql || ' ,T.GENTI_ANNAI_FLG AS GENTI_ANNAI_FLG ';
            l_sql := l_sql || ' ,HY.NYUKYO_SB_CD AS NYUKYO_SB_CD ';
            l_sql := l_sql || ' ,DECODE(HY.HEYA_SB_CD, ''5'', HY.Madori_Sb ';
            l_sql := l_sql || ' || TOKEN.GET_WM_COMMON(''121'', HY.Madorisb_Cd), ';
            l_sql := l_sql || ' TOKEN.GET_WM_COMMON(''122'', HY.Heya_Sb_Cd)) AS MADORI ';
            l_sql := l_sql || ' ,PKG_UKG02040.GET_WM_COMMON_DISP_SORT(''122'', HY.Heya_Sb_Cd)  AS DISP_SORT ';
            l_sql := l_sql || ' ,TO_CHAR(HY.YATIN,''99,999,999'') AS YATIN ';
            l_sql := l_sql || ' ,TO_CHAR(T.UPD_DATE ,''YYYY/MM/DD HH24:MI:SS'') AS UPD_DATE ';

            l_SqlW := l_SqlW || ' FROM UD_SHOUKAI_BUKKEN T ';
            l_SqlW := l_SqlW || ' , TOKEN.WD_BUKKEN BK ';
            l_SqlW := l_SqlW || ' , TOKEN.WD_HEYA HY ';
            -- 2019/07/04 SYS_熊振  ADD Start C43100_お客様項目の入力簡素化対応
            l_SqlW := l_SqlW || ' , UD_KUUSITU_HEYA UH ';
            l_SqlW := l_SqlW || ' WHERE T.LOGICAL_DEL_FLG = ''0'' ';
            l_SqlW := l_SqlW || ' AND T.HOMEMATE_BUKKEN_NO = UH.HOMEMATE_BUKKEN_NO(+) ';
            l_SqlW := l_SqlW || ' AND T.HEYA_NO = UH.HEYA_NO(+) ';
            --l_SqlW := l_SqlW || ' AND UH.NYU_KUU_KB =''0'' ';
            -- 2019/07/04 SYS_熊振  ADD End C43100_お客様項目の入力簡素化対応
            l_SqlW := l_SqlW || ' AND BK.LOGICAL_DEL_FLG = ''0'' ';
            l_SqlW := l_SqlW || ' AND HY.LOGICAL_DEL_FLG = ''0'' ';

            l_SqlW := l_SqlW || ' AND BK.HOMEMATE_BUKKEN_NO = T.HOMEMATE_BUKKEN_NO  ';
            l_SqlW := l_SqlW || ' AND HY.HOMEMATE_BUKKEN_NO = BK.HOMEMATE_BUKKEN_NO ';
            l_SqlW := l_SqlW || ' AND HY.HEYA_NO = T.HEYA_NO ';

            IF param_searchitem <> '2'
            THEN
                l_SqlW := l_SqlW || '  AND  T.NO_DISP_FLG = ''0''  ';
            END IF;

            IF param_searchitem = '3'
            THEN
                l_SqlW := l_SqlW || ' AND T.OKINIIRI IN ( ''1'',''2'',''3'')';
            END IF;

            IF param_searchitem = '4'
            THEN
                l_SqlW := l_SqlW || ' AND T.GENTI_ANNAI_FLG = ''1'' ';
            END IF;

            IF param_searchitem = '5'
            THEN
                l_SqlW := l_SqlW || ' AND T.MOUSIKOMI_KB  = ''1''';
            END IF;

            IF param_searchitem = '6'
            THEN
                l_SqlW := l_SqlW || ' AND T.MITUMORISHO_FLG  = ''1''';
            END IF;

            IF param_searchitem = '7'
            THEN
                l_SqlW := l_SqlW || ' AND T.TIRASHI_PRINT_DATE IS NOT NULL';
            END IF;

            l_SqlW     := l_SqlW || '  AND T.BUKKEN_PARKING_KB = ''1''';
            l_SqlW     := l_SqlW || '  AND T.KAISYA_CD = ''' || param_kaisyacd || '''';
            l_SqlW     := l_SqlW || ' AND T.UKETUKE_NO = ''' || param_uketukeno || '''';
            l_sql      := l_sql || l_SqlW || l_sqlOrder || ') U ';
            l_sqlIndex := l_sqlIndex || '  WHERE ROWNUM <=  ' || param_endindex || ')';
            l_sqlIndex := l_sqlIndex || ' WHERE ROWINDEX >=  ' || param_startindex;
            l_sql      := l_sql || l_sqlIndex;
            dbms_output.put_line(l_sql);

            OPEN out_bukenInfo FOR l_sql;

        END;

        --物件データ件数
        BEGIN
            l_coutSql := ' SELECT ';
            l_coutSql := l_coutSql || ' COUNT(T.UKETUKE_NO) ';
            l_coutSql := l_coutSql || l_SqlW;
            EXECUTE IMMEDIATE l_coutSql
                INTO out_bukencount;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                out_bukencount := 0;
        END;
        rst_code := MSG_SUCCESS;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := MSG_UNKNOWN;
            rst_msg  := SQLERRM;

    END PRO_BUKEN_INFO_INIT;

    -- ---------------------------------------------------------
    -- システム名            ： 受付紹介システム
    -- プロシージャID         ： GET_WM_COMMON_DISP_SORT
    -- プロシージャ名         ： 表示順取得する
    -- 引数               ： P_JOUHOU_SB_CD     IN VARCHAR2,        -- 情報区分コード
    --                    ： P_KOUMOKU_SB_CD    IN VARCHAR2         -- 項目区分コード
    -- 初版作成日/作成者    ： 2011/10/24 Ver.1.0.0 SYS_馮強華
    -- 変更情報
    -- ---------------------------------------------------------
    FUNCTION GET_WM_COMMON_DISP_SORT
    (
        P_JOUHOU_SB_CD  IN VARCHAR2
       ,P_KOUMOKU_SB_CD IN VARCHAR2
    ) RETURN VARCHAR2 IS
        RETV VARCHAR2(60);
    BEGIN
        RETV := NULL;

        SELECT DISP_SORT
          INTO RETV
          FROM TOKEN.WM_COMMON
         WHERE JOUHOU_SB_CD = P_JOUHOU_SB_CD
           AND KOUMOKU_SB_CD = P_KOUMOKU_SB_CD
           AND LOGICAL_DEL_FLG <> '1';

        RETURN(RETV);

    EXCEPTION
        WHEN OTHERS THEN
            NULL;
            RETURN(NULL);
    END GET_WM_COMMON_DISP_SORT;

    -- ---------------------------------------------------------
    -- システム名          ： 受付紹介システム
    -- プロシージャID      ： PRO_DELETE_YOYAKU
    -- プロシージャ名      ： 商談履歴登録処理
    -- 引数            ： param_kaisyacd         IN   VARCHAR2   -- 会社コード
    --                 ： param_tantyousya       IN   VARCHAR2   -- 担当者コード
    --                 ： param_uketukeno        IN   VARCHAR2   -- 受付番号
    --                 ： param_shoudankb        IN   VARCHAR2   -- 商談区分
    --                 ： param_shoudandate      IN   VARCHAR2   -- 商談日
    --                 ： param_shoudancmnt      IN   VARCHAR2   -- 商談内容
    --                 ： param_seq              IN   VARCHAR2   -- 画面のSEQ
    --                 ： param_updateflg        IN   VARCHAR2   -- 登録フラグ
    --                 ： param_update           IN   VARCHAR2   -- 画面格納日付
    --                 ： out_rst_msg            OUT  VARCHAR2   -- エラーメッセージ
    --                 ： out_rst_code          OUT  VARCHAR2   -- メッセージID
    -- 戻り値          ： 0000000  ： 成功
    --                    ： ERR9999  ： アプリケーションエラー
    -- 処理概要           ： 物件データを取得
    -- 初版作成日/作成者  ： 2012/03/28 Ver.1.0.0 SYS_馮強華
    -- 変更情報           ：
    -- ---------------------------------------------------------
    PROCEDURE PRO_UPDATE_SHOUDAN
    (
        param_kaisyacd    IN VARCHAR2 -- 会社コード
       ,param_tantyousya  IN VARCHAR2 -- 担当者コード
       ,param_uketukeno   IN VARCHAR2 -- 受付番号
       ,param_shoudankb   IN VARCHAR2 -- 商談区分
       ,param_shoudandate IN VARCHAR2 -- 商談日
       ,param_shoudancmnt IN VARCHAR2 -- 商談内容
       ,param_seq         IN VARCHAR2 -- 画面のSEQ
       ,param_updateflg   IN VARCHAR2 -- 登録フラグ
       ,param_update      IN VARCHAR2 -- 画面格納日付
       ,rst_code          OUT VARCHAR2 -- エラーメッセージ
       ,rst_msg           OUT VARCHAR2 -- メッセージID
    ) IS
        shoudanSeql NUMBER; -- MAXSEQ
        shoudanflg  VARCHAR2(2);
        nowupdate   VARCHAR2(30); -- 排他日付
        tpcusor     typCSR;

    BEGIN

        --来店受付テーブルの商談フラグを取得する
        SELECT SHOUDAN_FLG
          INTO shoudanflg
          FROM UD_RAITEN_UKETUKE
         WHERE KAISYA_CD = param_kaisyacd
           AND UKETUKE_NO = param_uketukeno;

        --商談履歴登録フラグが'0'(未登録)の場合
        IF shoudanflg = '0'
           OR shoudanflg IS NULL
        THEN

            UPDATE UD_RAITEN_UKETUKE
               SET SHOUDAN_FLG   = '1'
                  ,UPD_DATE      = SYSDATE
                  ,UPD_TANTOU_CD = param_tantyousya
             WHERE KAISYA_CD = param_kaisyacd
               AND UKETUKE_NO = param_uketukeno;

        END IF;

        --商談履歴情報新規登録
        IF param_updateflg = '1'
        THEN

            BEGIN

                --商談履歴テーブルをロックする
                OPEN tpcusor FOR
                    SELECT UKETUKE_NO
                      FROM UD_SHOUDAN_NAIYOU
                     WHERE KAISYA_CD = param_kaisyacd
                       AND UKETUKE_NO = param_uketukeno
                       FOR UPDATE NOWAIT;

            EXCEPTION
                WHEN OTHERS THEN
                    ROLLBACK;
                    --ロックが取得できなかった場合
                    rst_code := MSG_LOCKFAIL;
                    rst_msg  := SQLERRM;
                    RETURN;
            END;

            --最大SEQ + 1
            SELECT NVL(MAX(SHOUDAN_SEQ), 0) + 1
              INTO shoudanSeql
              FROM UD_SHOUDAN_NAIYOU
             WHERE KAISYA_CD = param_kaisyacd
               AND UKETUKE_NO = param_uketukeno;

            INSERT INTO UD_SHOUDAN_NAIYOU
                (KAISYA_CD
                ,UKETUKE_NO
                ,SHOUDAN_SEQ
                ,SHOUDAN_KB
                ,SHOUDAN_DATE
                ,SHOUDAN_NAIYOU
                ,APP_DATE
                ,APP_TANTOU_CD
                ,UPD_DATE
                ,UPD_TANTOU_CD
                ,LOGICAL_DEL_FLG
                ,LOGICAL_DEL_DATE
                ,DEL_TANTOU_CD)
            VALUES
                (param_kaisyacd
                ,param_uketukeno
                ,shoudanSeql
                ,param_shoudankb
                ,TO_DATE(param_shoudandate, 'YYYY-MM-DD HH24:MI:SS')
                ,param_shoudancmnt
                ,SYSDATE
                ,param_tantyousya
                ,SYSDATE
                ,param_tantyousya
                ,'0'
                ,NULL
                ,NULL);
        ELSE

            --排他チェックを行う
            SELECT TO_CHAR(upd_date, 'YYYY/MM/DD HH24:MI:SS')
              INTO nowupdate
              FROM UD_SHOUDAN_NAIYOU
             WHERE KAISYA_CD = param_kaisyacd
               AND UKETUKE_NO = param_uketukeno
               AND SHOUDAN_SEQ = param_seq;

            IF nowupdate != param_update
            THEN
                ROLLBACK;
                rst_code := 'ERRS003';
                RETURN;
            END IF;

            --商談履歴情報修正登録
            UPDATE UD_SHOUDAN_NAIYOU T
               SET SHOUDAN_KB     = param_shoudankb
                  ,SHOUDAN_DATE   = TO_DATE(param_shoudandate, 'YYYY-MM-DD HH24:MI:SS')
                  ,SHOUDAN_NAIYOU = param_shoudancmnt
                  ,UPD_DATE       = SYSDATE
                  ,UPD_TANTOU_CD  = param_tantyousya
             WHERE KAISYA_CD = param_kaisyacd
               AND UKETUKE_NO = param_uketukeno
               AND SHOUDAN_SEQ = param_seq;
        END IF;

        --来店受付データ更新処理(最終追客日更新)
        UPDATE UD_RAITEN_UKETUKE T
           SET T.LAST_TUIKYAKU_DATE = TO_DATE(param_shoudandate, 'YYYY-MM-DD HH24:MI:SS')
              ,T.UPD_DATE           = SYSDATE
              ,T.UPD_TANTOU_CD      = param_tantyousya
         WHERE KAISYA_CD = param_kaisyacd
           AND UKETUKE_NO = param_uketukeno
           AND  (T.LAST_TUIKYAKU_DATE IS NULL OR T.LAST_TUIKYAKU_DATE < TO_DATE(param_shoudandate, 'YYYY-MM-DD HH24:MI:SS'));

        COMMIT;
        rst_code := msg_success;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            rst_code := 'ERRS002';
            rst_msg  := SQLERRM;
    END PRO_UPDATE_SHOUDAN;

    -- ---------------------------------------------------------
    -- システム名          ： 受付紹介システム
    -- プロシージャID       ： PRO_UPDATE_SHOKAIBUKEN
    -- プロシージャ名       ： お気にいりを更新する
    -- 引数             ： param_kaisyacd         IN   VARCHAR2    -- 会社コード
    --                 ： param_tantyousya       IN   VARCHAR2    -- 担当者コード
    --                 ： param_uketukeno        IN   VARCHAR2    -- 受付番号
    --                 ： param_bukenno          IN   VARCHAR2    -- 物件番号
    --                 ： param_heyano           IN   VARCHAR2    -- 部屋番号
    --                 ： param_okiniiri         IN   VARCHAR2    -- お気に入り
    --                 ： out_update             IN   VARCHAR2    -- 排他日付
    --                 ： rst_code              OUT  VARCHAR2    -- エラーメッセージ
    --                 ： rst_msg                OUT  VARCHAR2     -- メッセージID
    -- 戻り値          ： 0000000  ： 成功
    --                    ： ERR9999  ： アプリケーションエラー
    -- 処理概要           ： 物件データを取得
    -- 初版作成日/作成者  ： 2012/03/28 Ver.1.0.0 SYS_馮強華
    -- 変更情報           ：
    -- ---------------------------------------------------------
    PROCEDURE PRO_UPDATE_SHOKAIBUKEN
    (
        param_kaisyacd   IN VARCHAR2 -- 会社コード
       ,param_tantyousya IN VARCHAR2 -- 担当者コード
       ,param_uketukeno  IN VARCHAR2 -- 受付番号
       ,param_bukenno    IN VARCHAR2 -- 物件番号
       ,param_heyano     IN VARCHAR2 -- 部屋番号
       ,param_okiniiri   IN VARCHAR2 -- お気に入り
       ,out_update       OUT VARCHAR2 -- 排他日付
       ,rst_code         OUT VARCHAR2 -- エラーメッセージ
       ,rst_msg          OUT VARCHAR2 -- メッセージID
    ) IS

    BEGIN

        out_update := TO_CHAR(SYSDATE, 'YYYY/MM/DD HH24:MI:SS');
        --お気にいり星選択してデータ更新
        PKG_UD_SHOUKAI_BUKKEN.PRC_UPD_OKINIIRI(param_kaisyacd
                                              ,param_uketukeno
                                              ,param_bukenno
                                              ,param_heyano
                                              ,param_okiniiri
                                              ,param_tantyousya
                                              ,SYSDATE
                                              ,rst_code
                                              ,rst_msg);
        IF rst_code = msg_success
        THEN
            COMMIT;
        ELSE
            rst_code := 'ERRS002';
            ROLLBACK;
        END IF;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            rst_code := 'ERRS002';
            rst_msg  := SQLERRM;
    END PRO_UPDATE_SHOKAIBUKEN;

    -- ---------------------------------------------------------
    -- システム名       ： 受付紹介システム
    -- プロシージャID   ： PRC_GET_KOKYAKU_INFO
    -- プロシージャ名   ： お客様情報取得
    -- 引数             ： param_kaisya_cd      IN VARCHAR2           -- 会社コード
    --                  ： param_uketuke_no     IN VARCHAR2           -- 受付№
    --                  ： rst_sintyokuInfo     OUT typSINTYOKUINFO   -- 結果セット
    --                  ： rst_record           OUT typKOKYAKUINFO    -- 結果セット
    --                  ： rst_code             OUT VARCHAR2          -- 結果区分
    --                  ： rst_msg              OUT VARCHAR2          -- 結果区分
    -- 初版作成日/作成者： 2011/10/24 Ver.1.0.0 SYS_馮強華
    -- 変更情報         ： 2017/07/31 Ver.1.0.1 MJEC)長倉 (案件C41059)中止他決顧客の検索対応
    -- 変更情報         ： 2021/03/29 Ver.1.0.2 SYS_楊振華 (案件45032)オンライン仲介システムの導入
    -- ---------------------------------------------------------
    PROCEDURE PRC_GET_KOKYAKU_INFO
    (
        param_kaisya_cd  IN VARCHAR2
       ,param_uketuke_no IN VARCHAR2
       ,rst_sintyokuInfo OUT typCSR
       ,rst_record       OUT typCSR
       ,rst_code         OUT VARCHAR2
       ,rst_msg          OUT VARCHAR2

    ) IS
        sinchokuKb CHAR(1);
    BEGIN


        BEGIN
            SELECT UR.SINCHOKU_CD
              INTO sinchokuKb
              FROM UD_RAITEN_UKETUKE UR
              LEFT JOIN UD_RAITEN_YOYAKU YK
                ON (UR.KAISYA_CD = YK.KAISYA_CD AND UR.UKETUKE_NO = YK.UKETUKE_NO AND YK.LOGICAL_DEL_FLG = '0' AND
                   YK.Yoyaku_Date >= SYSDATE)
             WHERE UR.KAISYA_CD = param_kaisya_cd
               AND UR.UKETUKE_NO = param_uketuke_no
               AND UR.LOGICAL_DEL_FLG = '0';

        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                sinchokuKb := 0;
        END;


        OPEN rst_record FOR
            SELECT UR.UKETUKE_NO AS UKETUKE_NO
                  ,TO_CHAR(UR.UKETUKE_DATE, 'YYYY/MM/DD') AS UKETUKE_DATE
                  ,UR.KOKYAKU_NAME AS KOKYAKU_NAME
                  --2019/06/24 ADD START 程旋(SYS) C43100_お客様項目の入力簡素化対応
                  ,UR.KOKYAKU_NAME_K AS KOKYAKU_NAME_K
                  ,UR.TEL_NO AS TEL_NO
                  ,UR.E_MAIL_ADDRESS AS E_MAIL_ADDRESS
                  ,UR.KOKYAKU_INFO_FLG AS KOKYAKU_INFO_FLG
                  ,UR.KIBOU_FLG        AS KIBOU_FLG
                  ,UR.SAKUSEI_KB       AS SAKUSEI_KB
                  --2019/06/24 ADD END 程旋(SYS) C43100_お客様項目の入力簡素化対応
                  ,UR.SINCHOKU_CD AS SINCHOKU_KB
                  ,PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME('008', UR.SINCHOKU_CD) AS SINCHOKU_NAME
                  ,TO_CHAR(YK.YOYAKU_DATE, 'YYYY/MM/DD HH24:MI') AS YOYAKU_DATE
                  ,TO_CHAR(UR.TUIKYAKU_ALARM_DATE, 'YYYY/MM/DD') AS TUIKYAKU_ALARM_DATE
                  ,UR.TUIKYAKU_ALARM_NAIYOU AS TUIKYAKU_ALARM_NAIYOU
                  ,UR.MEMO AS MEMO
                  ,TO_CHAR(YK.UPD_DATE, 'YYYY/MM/DD HH24:MI:SS') AS YK_UPD_DATE
                  ,TO_CHAR(YK.YOYAKU_DATE, 'YYYY/MM/DD') AS RAITEN_YOYAKU_DAY
                  ,TO_CHAR(YK.YOYAKU_DATE, 'HH24:MI') AS RAITEN_YOYAKU_TIME
                  ,TO_CHAR(UR.UPD_DATE, 'YYYY/MM/DD HH24:MI:SS') AS RAITEN_UPD_DATE
                  -- 2021/03/29 ADD START 楊振華(SYS) C45032_オンライン仲介システムの導入
                  ,TYK.ONLINE_TYUKAI_YOYAKU_DATE AS ONLINE_TYUKAI_YOYAKU_DATE
                  ,TO_CHAR(TYK.ONLINE_TYUKAI_YOYAKU_DATE, 'YYYY/MM/DD') AS ONLINE_TYUKAI_YOYAKU_DATE_DAY
                  ,TO_CHAR(TYK.ONLINE_TYUKAI_YOYAKU_DATE, 'HH24:MI') AS ONLINE_TYUKAI_YOYAKU_DATE_TIME
                  ,TYK.VIDEO_URL AS VIDEO_URL
                  ,TYK.UPD_DATE AS UPD_DATE
                  -- 2021/03/29 ADD END 楊振華(SYS) C45032_オンライン仲介システムの導入

              FROM UD_RAITEN_UKETUKE UR
              LEFT JOIN UD_RAITEN_YOYAKU YK
                ON (UR.KAISYA_CD = YK.KAISYA_CD AND UR.UKETUKE_NO = YK.UKETUKE_NO AND YK.Yoyaku_Date >= SYSDATE AND
                   YK.LOGICAL_DEL_FLG = '0')
              -- 2021/03/29 ADD START 楊振華(SYS) C45032_オンライン仲介システムの導入
              LEFT JOIN UD_ONLINE_TYUKAII_YOYAKU TYK
                ON (UR.KAISYA_CD = TYK.KAISYA_CD AND UR.UKETUKE_NO = TYK.UKETUKE_NO AND TYK.ONLINE_TYUKAI_YOYAKU_DATE >= SYSDATE AND
                   TYK.LOGICAL_DEL_FLG = '0')
              -- 2021/03/29 ADD END 楊振華(SYS) C45032_オンライン仲介システムの導入
             WHERE UR.KAISYA_CD = param_kaisya_cd
               AND UR.UKETUKE_NO = param_uketuke_no
               AND UR.LOGICAL_DEL_FLG = '0';

        PKG_COMMON.PRC_GET_COMMON_LIST('039', rst_sintyokuInfo, rst_code, rst_msg);

        --仮押え
        IF sinchokuKb = '2'
        THEN
            PKG_COMMON.PRC_GET_COMMON_LIST('040', rst_sintyokuInfo, rst_code, rst_msg);
        END IF;

        --申込み
        IF sinchokuKb = '3'
        THEN
            PKG_COMMON.PRC_GET_COMMON_LIST('041', rst_sintyokuInfo, rst_code, rst_msg);
        END IF;

        --2017/07/31 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
        --他決
        IF sinchokuKb = '6'
        THEN
            PKG_COMMON.PRC_GET_COMMON_LIST('115', rst_sintyokuInfo, rst_code, rst_msg);
        END IF;

        --中止
        IF sinchokuKb = '7'
        THEN
            PKG_COMMON.PRC_GET_COMMON_LIST('116', rst_sintyokuInfo, rst_code, rst_msg);
        END IF;
        --2017/07/31 MJEC)長倉 ADD End

        rst_code := MSG_SUCCESS;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := MSG_UNKNOWN;
            rst_msg  := SQLERRM;
    END PRC_GET_KOKYAKU_INFO;

    -- ---------------------------------------------------------
    -- システム名         ： 受付紹介システム
    -- プロシージャID      ： PRC_UPD_MEMO_INFO
    -- プロシージャ名      ： メモを更新する
    -- 引数             ： param_kaisya_cd            IN  VARCHAR2            -- 会社コード
    --                 ： param_uketuke_no            IN  VARCHAR2           -- 受付番号
    --                 ： param_bukken_no             IN  VARCHAR2           -- 物件番号
    --                 ： param_heya_no              IN  VARCHAR2           -- 部屋№
    --                 ： param_update              IN  VARCHAR2           -- 部屋№
    --                 ： param_tantou_cd         IN  VARCHAR2           -- 担当者
    --                 ： param_memo             IN  VARCHAR2           -- メモ
    --                 ： rst_update                 IN  VARCHAR2            -- 排他日付
    --                 ： rst_code                 OUT  VARCHAR2          -- メッセージコード
    --                 ： rst_msg                  OUT  VARCHAR2          -- メッセージ
    -- 処理概要          ： メモを更新する
    -- 初版作成日/作成者 ： 2012/03/22 Ver.1.0.0 SYS_馮強華
    -- ---------------------------------------------------------
    PROCEDURE PRC_UPD_MEMO_INFO
    (
        param_kaisya_cd  IN VARCHAR2
       ,param_uketuke_no IN VARCHAR2
       ,param_bukken_no  IN VARCHAR2
       ,param_heya_no    IN VARCHAR2
       ,param_update     IN VARCHAR2
       ,param_tantou_cd  IN VARCHAR2
       ,param_memo       IN VARCHAR2
       ,rst_update       OUT VARCHAR2
       ,rst_code         OUT VARCHAR2
       ,rst_msg          OUT VARCHAR2
    ) IS
        nowupdate VARCHAR2(30);
    BEGIN
        --排他チェックを行う
        SELECT TO_CHAR(upd_date, 'YYYY/MM/DD HH24:MI:SS')
          INTO nowupdate
          FROM UD_SHOUKAI_BUKKEN T
         WHERE KAISYA_CD = param_kaisya_cd
           AND UKETUKE_NO = param_uketuke_no
           AND T.HOMEMATE_BUKKEN_NO = param_bukken_no
           AND T.HEYA_NO = param_heya_no
           AND T.LOGICAL_DEL_FLG = '0';

        IF nowupdate != param_update
        THEN
            rst_code := 'ERRS009';
            RETURN;
        END IF;

        rst_update := TO_CHAR(SYSDATE, 'YYYY/MM/DD HH24:MI:SS');
        UPDATE UD_SHOUKAI_BUKKEN
           SET SHOUDAN_MEMO  = param_memo
              , --メモ
               UPD_DATE      = SYSDATE
              , --更新日付
               UPD_TANTOU_CD = param_tantou_cd --更新担当者コード
         WHERE KAISYA_CD = param_kaisya_cd
           AND UKETUKE_NO = param_uketuke_no
           AND HOMEMATE_BUKKEN_NO = param_bukken_no
           AND HEYA_NO = param_heya_no;

        COMMIT;
        rst_code := msg_success;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := 'ERRS002';
            rst_msg  := SQLERRM;
            ROLLBACK;
    END;

    -- ---------------------------------------------------------
    -- システム名       ： 受付紹介システム
    -- プロシージャID   ： PRC_UPD_SINCHOUKU_INFO
    -- プロシージャ名   ： 進捗を更新する
    -- 引数             ： param_kaisya_cd          IN  VARCHAR2           -- 会社コード
    --                  ： param_uketuke_no         IN  VARCHAR2           -- 受付番号
    --                  ： param_tantou_cd          IN  VARCHAR2           -- 担当者
    --                  ： param_sinchouku_cd       IN  VARCHAR2           -- 進捗コード
    --                  ： param_update             IN  VARCHAR2           -- 排他日付
    --2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
    --                  ： param_shoudancmnt        IN  VARCHAR2           -- 商談理由
    --2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
    --                  ： rst_update               IN  VARCHAR2           -- 排他日付
    --                  ： rst_code                 OUT  VARCHAR2          -- メッセージコード
    --                  ： rst_msg                  OUT  VARCHAR2          -- メッセージ
    -- 処理概要         ： 進捗を更新する
    -- 初版作成日/作成者： 2012/03/22 Ver.1.0.0 SYS_馮強華
    -- 変更情報　       ： 2017/07/31 Ver.1.0.1 MJEC)長倉 (案件C41059)中止他決顧客の検索対応
    --                ： 2020/06/24 Ver.1.0.2 劉恆毅(SYS) C43040-2_法人の入力項目追加対応
    -- ---------------------------------------------------------
    PROCEDURE PRC_UPD_SINCHOUKU_INFO
    (
        param_kaisya_cd    IN VARCHAR2
       ,param_uketuke_no   IN VARCHAR2
       ,param_tantou_cd    IN VARCHAR2
       ,param_sinchouku_cd IN VARCHAR2
       ,param_update       IN VARCHAR2
       --2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
       ,param_shoudancmnt  IN VARCHAR2
       --2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
       ,rst_update         OUT VARCHAR2
       ,rst_code           OUT VARCHAR2
       ,rst_msg            OUT VARCHAR2
    ) IS
        nowupdate VARCHAR2(30);
    BEGIN
        --排他チェックを行う
        SELECT TO_CHAR(upd_date, 'YYYY/MM/DD HH24:MI:SS')
          INTO nowupdate
          FROM UD_RAITEN_UKETUKE T
         WHERE KAISYA_CD = param_kaisya_cd
           AND UKETUKE_NO = param_uketuke_no
           AND T.LOGICAL_DEL_FLG = '0';

        IF nowupdate != param_update
        THEN
            rst_code := 'ERRS009';
            RETURN;
        END IF;

        rst_update := TO_CHAR(SYSDATE, 'YYYY/MM/DD HH24:MI:SS');

        --2017/07/31 MJEC)長倉 UPD Start (案件C41059)中止他決顧客の検索対応
        IF param_sinchouku_cd = '1'
        THEN
            PKG_KOKYAKU.PRC_SHOUDAN_RESUMPTION(param_kaisya_cd
                                              ,param_uketuke_no
                                              ,param_tantou_cd
                                              ,param_sinchouku_cd
                                              ,'0'
                                              ,SYSDATE
                                              ,rst_code
                                              ,rst_msg);
        ELSE
            PKG_KOKYAKU.PRC_SHOUDAN_STOP(param_kaisya_cd

                                        ,param_uketuke_no
                                        ,param_tantou_cd
                                        ,param_sinchouku_cd
                                        ,'0'
                                        ,SYSDATE
       --2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
                                        ,param_shoudancmnt
       --2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
                                        ,rst_code
                                        ,rst_msg);
        END IF;
        --2017/07/31 MJEC)長倉 UPD End

        IF rst_code = '1111111'
        THEN
            ROLLBACK;
        ELSE
            COMMIT;
            rst_code := msg_success;
        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := 'ERRS002';
            rst_msg  := SQLERRM;
            ROLLBACK;
    END;

    -- ---------------------------------------------------------
    -- システム名         ： 受付紹介システム
    -- プロシージャID      ： PRC_UPD_ALARM_INFO
    -- プロシージャ名      ： 来店受付データ更新(アラーム解除)
    -- 引数             ： param_kaisya_cd            IN  VARCHAR2            -- 会社コード
    --                 ： param_uketuke_no            IN  VARCHAR2           -- 受付番号
    --                 ： param_tantou_cd            IN  VARCHAR2           -- 担当者
    --                 ： param_update             IN  VARCHAR2           -- 排他日付
    --                 ： rst_update                 IN  VARCHAR2            -- 排他日付
    --                 ： rst_code                 OUT  VARCHAR2          -- メッセージコード
    --                 ： rst_msg                  OUT  VARCHAR2          -- メッセージ
    -- 処理概要          ： 来店受付データ更新(アラーム解除)
    -- 初版作成日/作成者 ： 2012/03/22 Ver.1.0.0 SYS_馮強華
    -- ---------------------------------------------------------
    PROCEDURE PRC_UPD_ALARM_INFO
    (
        param_kaisya_cd  IN VARCHAR2
       ,param_uketuke_no IN VARCHAR2
       ,param_tantou_cd  IN VARCHAR2
       ,param_update     IN VARCHAR2
       ,rst_update       OUT VARCHAR2
       ,rst_code         OUT VARCHAR2
       ,rst_msg          OUT VARCHAR2
    ) IS
        nowupdate VARCHAR2(30);
        upSysdate DATE;
    BEGIN
        --排他チェックを行う
        SELECT TO_CHAR(upd_date, 'YYYY/MM/DD HH24:MI:SS')
          INTO nowupdate
          FROM UD_RAITEN_UKETUKE T
         WHERE KAISYA_CD = param_kaisya_cd
           AND UKETUKE_NO = param_uketuke_no
           AND T.LOGICAL_DEL_FLG = '0';

        IF nowupdate != param_update
        THEN
            rst_code := 'ERRS009';
            RETURN;
        END IF;

        upSysdate  := SYSDATE;
        rst_update := TO_CHAR(upSysdate, 'YYYY/MM/DD HH24:MI:SS');

        UPDATE UD_RAITEN_UKETUKE T
           SET T.LAST_TUIKYAKU_DATE    = upSysdate
              ,T.TUIKYAKU_ALARM_DATE   = NULL
              ,T.TUIKYAKU_ALARM_NAIYOU = NULL
              ,T.UPD_DATE              = upSysdate
              ,T.UPD_TANTOU_CD         = param_tantou_cd
         WHERE KAISYA_CD = param_kaisya_cd
           AND UKETUKE_NO = param_uketuke_no;

        COMMIT;
        rst_code := msg_success;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := 'ERRS002';
            rst_msg  := SQLERRM;
            ROLLBACK;
    END;

    -- ---------------------------------------------------------
    -- システム名         ： 受付紹介システム
    -- プロシージャID      ： PRC_UPD_RAITENMEMO_INFO
    -- プロシージャ名      ： メモを更新する
    -- 引数             ： param_kaisya_cd            IN  VARCHAR2            -- 会社コード
    --                 ： param_uketuke_no            IN  VARCHAR2           -- 受付番号
    --                 ： param_update              IN  VARCHAR2           -- 日付
    --                 ： param_tantou_cd         IN  VARCHAR2           -- 担当者
    --                 ： param_memo             IN  VARCHAR2           -- メモ
    --                 ： rst_update                 IN  VARCHAR2            -- 排他日付
    --                 ： rst_code                 OUT  VARCHAR2          -- メッセージコード
    --                 ： rst_msg                  OUT  VARCHAR2          -- メッセージ
    -- 処理概要          ： メモを更新する
    -- 初版作成日/作成者 ： 2012/03/22 Ver.1.0.0 SYS_馮強華
    -- 変更情報          ： 2019/06/24 Ver.1.0.1 SYS_カク年昇 C43100_お客様項目の入力簡素化対応
    -- ---------------------------------------------------------
    PROCEDURE PRC_UPD_RAITENMEMO_INFO
    (
        param_kaisya_cd  IN VARCHAR2
       ,param_uketuke_no IN VARCHAR2
       ,param_update     IN VARCHAR2
       ,param_tantou_cd  IN VARCHAR2
       ,param_memo       IN VARCHAR2
       --2019/06/24 ADD START カク年昇(SYS) C43100_お客様項目の入力簡素化対応
       ,param_kokyakuName      IN VARCHAR2
       ,param_kokyakuNameKana  IN VARCHAR2
       ,param_telNo            IN VARCHAR2
       ,param_emailNo          IN VARCHAR2
       ,param_flg              IN VARCHAR2
       ,param_sinpoflg         IN VARCHAR2
       --2019/06/24 ADD END カク年昇(SYS) C43100_お客様項目の入力簡素化対応
       ,rst_update       OUT VARCHAR2
       ,rst_code         OUT VARCHAR2
       ,rst_msg          OUT VARCHAR2
    ) IS
        nowupdate VARCHAR2(30);
  --2019/06/24 ADD START カク年昇(SYS) C43100_お客様項目の入力簡素化対応
  SQL1 VARCHAR2(1000);
  --2019/06/24 ADD END カク年昇(SYS) C43100_お客様項目の入力簡素化対応
    BEGIN
        --排他チェックを行う
        SELECT TO_CHAR(upd_date, 'YYYY/MM/DD HH24:MI:SS')
          INTO nowupdate
          FROM UD_RAITEN_UKETUKE T
         WHERE KAISYA_CD = param_kaisya_cd
           AND UKETUKE_NO = param_uketuke_no
           AND T.LOGICAL_DEL_FLG = '0';

        IF nowupdate != param_update
        THEN
            rst_code := 'ERRS009';
            RETURN;
        END IF;

        rst_update := TO_CHAR(SYSDATE, 'YYYY/MM/DD HH24:MI:SS');

     --2019/06/24 ADD START カク年昇(SYS) C43100_お客様項目の入力簡素化対応
     IF param_flg != '2' THEN
     --2019/06/24 ADD END カク年昇(SYS) C43100_お客様項目の入力簡素化対応
        UPDATE UD_RAITEN_UKETUKE
           SET MEMO          = param_memo
              , --メモ
               UPD_DATE      = SYSDATE
              , --更新日付
               UPD_TANTOU_CD = param_tantou_cd --更新担当者コード
         WHERE KAISYA_CD = param_kaisya_cd
           AND UKETUKE_NO = param_uketuke_no;

        COMMIT;
        rst_code := msg_success;
     --2019/06/24 ADD START カク年昇(SYS) C43100_お客様項目の入力簡素化対応
     END IF;
     IF param_flg = '2' THEN
        SQL1 := 'UPDATE UD_RAITEN_UKETUKE SET ';
        SQL1 := SQL1 ||'KOKYAKU_NAME      = ''' || param_kokyakuName || ''',';--お客様名
        SQL1 := SQL1 ||'KOKYAKU_NAME_K    = ''' || param_kokyakuNameKana || ''','; --お客様名カナ
        SQL1 := SQL1 ||'TEL_NO            = ''' || param_telNo || ''',';--電話番号
        SQL1 := SQL1 ||'KOKYAKU_INFO_FLG  = ''1'',';--顧客情報登録フラ
        SQL1 := SQL1 ||'E_MAIL_ADDRESS    = ''' || param_emailNo || ''',';--メールアドレス
        IF param_sinpoflg = '2' THEN
        SQL1 := SQL1 ||'SINCHOKU_CD       = ''1'',';--進捗状況コード
        END IF;
        SQL1 := SQL1 ||'UPD_DATE          = SYSDATE, ';--更新日付
        SQL1 := SQL1 ||'UPD_TANTOU_CD = ''' || param_tantou_cd || '''';--更新担当者コード
        SQL1 := SQL1 ||'WHERE KAISYA_CD = ''' || param_kaisya_cd || '''';
        SQL1 := SQL1 ||'AND UKETUKE_NO = ''' || param_uketuke_no || '''';
        EXECUTE IMMEDIATE SQL1;
        COMMIT;
        rst_code := msg_success;
     END IF;
     --2019/06/24 ADD END カク年昇(SYS) C43100_お客様項目の入力簡素化対応

    EXCEPTION
        WHEN OTHERS THEN
            rst_code := 'ERRS002';
            rst_msg  := SQLERRM;
            ROLLBACK;
    END;

    -- ---------------------------------------------------------
    -- システム名         ： 受付紹介システム
    -- プロシージャID      ： PRC_GET_SHOUDAN_INFO
    -- プロシージャ名      ： 商談状況情報取得
    -- 引数            ： pvKaisyaCd                         IN  VARCHAR2          -- 会社コード
    --                 ： pvUketukeNo                       IN  VARCHAR2         -- 受付No
    --                 ： param_post_cd                     IN  VARCHAR2         -- 店舗コード
    --                 ： param_tantou_cd                   IN  VARCHAR2         -- 担当者コード
    --                 ： out_okiniiri_bukencount          OUT NUMBER         -- お気に入り物件数
    --                 ： out_tirashi_printcount           OUT NUMBER         -- チラシ印刷数
    --                 ： out_sinchaku_bukencount          OUT NUMBER         -- 新着物件数
    --                 ： out_kariosae_bukenInfo           OUT  CURSOR            -- 仮押え物件データ
    --                 ： out_genti_annai                  OUT  CURSOR          -- 現地案内予約情報
    --                 ： rst_code                         OUT  VARCHAR2     -- メッセージコード
    --                 ： rst_msg                          OUT  VARCHAR2     -- メッセージ
    -- 処理概要           ： 商談状況情報取得
    -- 初版作成日/作成者 ： 2012/03/28 Ver.1.0.0 SYS_馮強華
    -- 変更情報           ：
    -- ---------------------------------------------------------
    PROCEDURE PRC_GET_SHOUDAN_INFO
    (
        param_kaisya_cd         IN VARCHAR2
       ,param_uketuke_no        IN VARCHAR2
       ,param_post_cd           IN VARCHAR2
       ,param_tantou_cd         IN VARCHAR2
       ,out_okiniiri_bukencount OUT NUMBER
       ,out_tirashi_printcount  OUT NUMBER
       ,out_sinchaku_bukencount OUT NUMBER
       ,out_kariosae_bukenInfo  OUT typCSR
       ,out_genti_annai         OUT typCSR
       ,rst_code                OUT VARCHAR2
       ,rst_msg                 OUT VARCHAR2
    ) IS

    BEGIN

        rst_code := MSG_SUCCESS;

        --お気に入り物件数
        BEGIN
            SELECT COUNT(UKETUKE_NO)
              INTO out_okiniiri_bukencount
              FROM (
                SELECT T.*
                FROM UD_SHOUKAI_BUKKEN T
                MINUS
                SELECT T.*
                FROM UD_SHOUKAI_BUKKEN T
                     ,TOKEN.WD_HEYA HY
               WHERE T.HOMEMATE_BUKKEN_NO = HY.HOMEMATE_BUKKEN_NO
                 AND T.HEYA_NO = HY.HEYA_NO
                 AND HY.NYUKYO_SB_CD=2
                ) T
                ,TOKEN.WD_BUKKEN   BK
                ,TOKEN.WD_HEYA     HY
             WHERE T.LOGICAL_DEL_FLG = '0'
               AND BK.LOGICAL_DEL_FLG = '0'
               AND HY.LOGICAL_DEL_FLG = '0'
               AND T.HOMEMATE_BUKKEN_NO = BK.HOMEMATE_BUKKEN_NO
               AND BK.HOMEMATE_BUKKEN_NO = HY.HOMEMATE_BUKKEN_NO
               AND T.HEYA_NO = HY.HEYA_NO
               AND T.KAISYA_CD = param_kaisya_cd
               AND T.UKETUKE_NO = param_uketuke_no
               AND T.OKINIIRI IN ('1', '2', '3')
               AND T.BUKKEN_PARKING_KB = '1'
               AND T.NO_DISP_FLG = '0'
             GROUP BY T.KAISYA_CD
                     ,T.UKETUKE_NO;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                out_okiniiri_bukencount := 0;
        END;

        BEGIN
            --チラシ印刷数
            SELECT COUNT(UKETUKE_NO)
              INTO out_tirashi_printcount
              FROM UD_SHOUKAI_BUKKEN T
                  ,TOKEN.WD_BUKKEN   BK
                  ,TOKEN.WD_HEYA     HY
             WHERE T.LOGICAL_DEL_FLG = '0'
               AND BK.LOGICAL_DEL_FLG = '0'
               AND HY.LOGICAL_DEL_FLG = '0'
               AND T.HOMEMATE_BUKKEN_NO = BK.HOMEMATE_BUKKEN_NO
               AND BK.HOMEMATE_BUKKEN_NO = HY.HOMEMATE_BUKKEN_NO
               AND T.HEYA_NO = HY.HEYA_NO
               AND T.KAISYA_CD = param_kaisya_cd
               AND T.UKETUKE_NO = param_uketuke_no
               AND T.TIRASHI_PRINT_DATE IS NOT NULL
               AND T.BUKKEN_PARKING_KB = '1'
               AND T.NO_DISP_FLG = '0'
             GROUP BY T.KAISYA_CD
                     ,T.UKETUKE_NO;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                out_tirashi_printcount := 0;
        END;



        --新着物件数
        BEGIN
            SELECT V.SINTYAKU
              INTO out_sinchaku_bukencount
              FROM (SELECT S.KAISYA_CD KAISYA_CD
                          ,S.UKETUKE_NO UKETUKE_NO
                          ,COUNT(S.UKETUKE_NO) SINTYAKU
                      FROM UD_SINTYAKU_BUKKEN S
                          ,TOKEN.WD_BUKKEN    BK
                          ,TOKEN.WD_HEYA      H
                     WHERE S.KAISYA_CD = param_kaisya_cd
                       AND S.HOMEMATE_BUKKEN_NO = BK.HOMEMATE_BUKKEN_NO
                       AND S.HOMEMATE_BUKKEN_NO = H.HOMEMATE_BUKKEN_NO
                       AND S.HEYA_NO = H.HEYA_NO
                       AND S.SINTYAKU_AL_KAIYJO_FLG = '0'
                       AND H.NYUKYO_SB_CD IN ('1', '3')
                       AND H.LOGICAL_DEL_FLG = '0'
                       AND BK.LOGICAL_DEL_FLG = '0'
                     GROUP BY S.KAISYA_CD
                             ,S.UKETUKE_NO) V
                  ,UD_RAITEN_UKETUKE R
             WHERE V.KAISYA_CD = R.KAISYA_CD
               AND V.UKETUKE_NO = R.UKETUKE_NO
               AND R.UKETUKE_NO = param_uketuke_no
               AND R.SINCHOKU_CD = '1'
               AND R.UKETUKE_POST_CD = param_post_cd
               AND R.CHUKAI_TANTOU_CD = param_tantou_cd
               AND R.LOGICAL_DEL_FLG = '0';
        EXCEPTION
            WHEN OTHERS THEN
                out_sinchaku_bukencount := 0;
        END;

        --仮押え物件データ取得
        OPEN out_kariosae_bukenInfo FOR
          SELECT U.BUKKEN_NAME,
                 U.HEYA_NO,
                 U.YUSEN_JYUNI,
                 U.KARIOSAE_YUKO_KIGEN,
                 U.KARIOSAEJI_JYOUTAI,
                 U.KIGENGIRE_FLG,
                 U.UPD_DATE,
                 U.HOMEMATE_BUKKEN_NO
            FROM (SELECT T.BUKKEN_NAME AS BUKKEN_NAME,
                         T.HEYA_NO AS HEYA_NO,
                         T.KARIOSAE_JYUNI AS YUSEN_JYUNI,
                         TO_CHAR(T.KARIOSAE_YUKO_KIGEN, 'YYYY/MM/DD') AS KARIOSAE_YUKO_KIGEN,
                         T.KARIOSAE_KB AS KARIOSAEJI_JYOUTAI,
                         T.KIGENGIRE_FLG AS KIGENGIRE_FLG,
                         TO_CHAR(T.UPD_DATE_SHOUKAI_BUKKEN,
                                 'YYYY/MM/DD HH24:MI:SS') AS UPD_DATE,
                         T.HOMEMATE_BUKKEN_NO AS HOMEMATE_BUKKEN_NO
                    FROM UV_KARIOSAE_BUKKEN T
                   WHERE T.KENSAKU_KAISYA_CD = param_kaisya_cd
                     AND T.KENSAKU_UKETUKE_NO = param_uketuke_no
                     AND T.UKETUKE_NO = param_uketuke_no
                  UNION ALL
                  SELECT T.BUKKEN_NAME AS BUKKEN_NAME,
                         T.HEYA_NO AS HEYA_NO,
                         T.KARIOSAE_JYUNI AS YUSEN_JYUNI,
                         TO_CHAR(T.KARIOSAE_YUKO_KIGEN, 'YYYY/MM/DD') AS KARIOSAE_YUKO_KIGEN,
                         T.KARIOSAE_KB AS KARIOSAEJI_JYOUTAI,
                         T.KIGENGIRE_FLG AS KIGENGIRE_FLG,
                         TO_CHAR(T.UPD_DATE_SHOUKAI_BUKKEN,
                                 'YYYY/MM/DD HH24:MI:SS') AS UPD_DATE,
                         T.HOMEMATE_BUKKEN_NO AS HOMEMATE_BUKKEN_NO
                    FROM UV_KARIOSAE_BUKKEN T
                   WHERE T.KENSAKU_KAISYA_CD = param_kaisya_cd
                     AND T.KENSAKU_UKETUKE_NO = param_uketuke_no
                     AND T.KARIOSAE_KB = '0') U
           ORDER BY U.HOMEMATE_BUKKEN_NO, U.HEYA_NO;

        --現地案内予約データ取得
        OPEN out_genti_annai FOR
            SELECT AY.SEQ AS SEQ --SEQ
                  ,TO_CHAR(AY.GENTI_ANNAI_YOYAKU_DATE, 'YYYY/MM/DD HH24:MI') AS YOYAKU_DATE --現地案内予約日
                  ,TO_CHAR(AY.GENTI_ANNAI_YOYAKU_DATE, 'HH24:MI') AS YOYAKU_TIME -- 現地案内予約時間
                  ,AY.GENTI_ANNAI_FLG AS GENTI_ANNAI_FLG --実施済みフラグ
                  ,WD.BUKKEN_NAME || '(' || SB.HEYA_NO || '号室)' AS BUKENAME --物件名
                  ,AY.GENTI_ANNAI_YOYAKU_DATE AS GENTI_ANNAI_YOYAKU_DATE --予約日
                  ,SB.HOMEMATE_BUKKEN_NO AS HOMEMATE_BUKKEN_NO --物件No
                  ,SB.HEYA_NO AS HEYA_NO --部屋No
                  ,TO_CHAR(AY.UPD_DATE, 'YYYY/MM/DD HH24:MI:SS') AS UPD_DATE --更新日
                  --2019/07/19 ADD START 熊振(SYS) C43089_来店日時の入力チェック対応
                  ,RU.RAITEN_DATE AS RAITEN_DATE                 --来店日時
                  --2019/07/19 ADD END 熊振(SYS) C43089_来店日時の入力チェック対応
              FROM UD_GENTI_ANNAI_YOYAKU AY
                  ,UD_SHOUKAI_BUKKEN     SB
                  ,TOKEN.WD_BUKKEN       WD
                  ,TOKEN.WD_HEYA         HY
                   --2019/07/19 ADD START 熊振(SYS) C43089_来店日時の入力チェック対応
                  ,UD_RAITEN_UKETUKE     RU
                  --2019/07/19 ADD END 熊振(SYS) C43089_来店日時の入力チェック対応
             WHERE AY.KAISYA_CD = param_kaisya_cd
               AND AY.UKETUKE_NO = param_uketuke_no
               --2019/07/19 ADD START 熊振(SYS) C43089_来店日時の入力チェック対応
               AND RU.KAISYA_CD = AY.KAISYA_CD
               AND RU.UKETUKE_NO = AY.UKETUKE_NO
               AND RU.LOGICAL_DEL_FLG =0
               --2019/07/19 ADD END 熊振(SYS) C43089_来店日時の入力チェック対応
               AND AY.KAISYA_CD = SB.KAISYA_CD
               AND AY.UKETUKE_NO = SB.UKETUKE_NO
               AND AY.HOMEMATE_BUKKEN_NO = SB.HOMEMATE_BUKKEN_NO
               AND SB.HOMEMATE_BUKKEN_NO = WD.HOMEMATE_BUKKEN_NO
               AND WD.HOMEMATE_BUKKEN_NO = HY.HOMEMATE_BUKKEN_NO
               AND AY.HEYA_NO = SB.HEYA_NO
               AND SB.HEYA_NO = HY.HEYA_NO
               AND AY.LOGICAL_DEL_FLG = '0'
               AND WD.LOGICAL_DEL_FLG = '0'
               AND HY.LOGICAL_DEL_FLG = '0'
               AND SB.BUKKEN_PARKING_KB = '1'
             ORDER BY GENTI_ANNAI_YOYAKU_DATE DESC
                     ,HOMEMATE_BUKKEN_NO      ASC
                     ,HEYA_NO                 ASC;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := MSG_UNKNOWN;
            rst_msg  := SQLERRM;
    END;

    -- ---------------------------------------------------------
    -- システム名         ： 受付紹介システム
    -- プロシージャID      ： PRC_UPD_KARIOSAE
    -- プロシージャ名      ： 仮押え物件欄確認処理
    -- 引数             ： param_kaisya_cd            IN  VARCHAR2            -- 会社コード
    --                 ： param_uketuke_no            IN  VARCHAR2           -- 受付番号
    --                 ： param_buken_no             IN  VARCHAR2           -- 物件番号
    --                 ： param_heya_no             IN  VARCHAR2           -- 部屋番号
    --                 ： param_update              IN  VARCHAR2           -- 日付
    --                 ： param_tantou_cd         IN  VARCHAR2           -- 担当者
    --                 ： rst_code                 OUT  VARCHAR2          -- メッセージコード
    --                 ： rst_msg                  OUT  VARCHAR2          -- メッセージ
    -- 処理概要          ： 仮押え物件欄確認処理
    -- 初版作成日/作成者 ： 2012/03/22 Ver.1.0.0 SYS_馮強華
    -- ---------------------------------------------------------
    PROCEDURE PRC_UPD_KARIOSAE
    (
        param_kaisya_cd  IN VARCHAR2
       ,param_uketuke_no IN VARCHAR2
       ,param_buken_no   IN VARCHAR2
       ,param_heya_no    IN VARCHAR2
       ,param_tantou_cd  IN VARCHAR2
       ,param_update     IN VARCHAR2
       ,rst_code         OUT VARCHAR2
       ,rst_msg          OUT VARCHAR2
    ) IS
        nowupdate VARCHAR2(30);
    BEGIN
        --排他チェックを行う
        SELECT TO_CHAR(upd_date, 'YYYY/MM/DD HH24:MI:SS')
          INTO nowupdate
          FROM UD_SHOUKAI_BUKKEN T
         WHERE KAISYA_CD = param_kaisya_cd
           AND UKETUKE_NO = param_uketuke_no
           AND T.HOMEMATE_BUKKEN_NO = param_buken_no
           AND T.Heya_No = param_heya_no
           AND T.LOGICAL_DEL_FLG = '0';

        IF nowupdate != param_update
        THEN
            rst_code := 'ERRS009';
            RETURN;
        END IF;

        UPDATE UD_SHOUKAI_BUKKEN T
           SET MOUSIKOMI_KB  = '0'
              , --申込み区分
               UPD_DATE      = SYSDATE
              , --更新日付
               UPD_TANTOU_CD = param_tantou_cd --更新担当者コード
         WHERE KAISYA_CD = param_kaisya_cd
           AND UKETUKE_NO = param_uketuke_no
           AND T.HOMEMATE_BUKKEN_NO = param_buken_no
           AND T.Heya_No = param_heya_no
           AND T.LOGICAL_DEL_FLG = '0';

        UPDATE UD_RAITEN_UKETUKE T
           SET SINCHOKU_CD   = '1', --進捗状況コード
               UPD_DATE      = SYSDATE, --更新日付
               UPD_TANTOU_CD = param_tantou_cd --更新担当者コード
         WHERE KAISYA_CD = param_kaisya_cd
           AND UKETUKE_NO = param_uketuke_no
           AND not exists (SELECT 1
                  FROM UD_SHOUKAI_BUKKEN B
                 WHERE B.KAISYA_CD = T.KAISYA_CD
                   AND B.UKETUKE_NO = T.UKETUKE_NO
                   AND B.MOUSIKOMI_KB = '1'
                   AND B.LOGICAL_DEL_FLG = '0');
        COMMIT;
        rst_code := msg_success;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := 'ERRS002';
            rst_msg  := SQLERRM;
            ROLLBACK;
    END;

    -- ---------------------------------------------------------
    -- システム名         ： 受付紹介システム
    -- プロシージャID      ： PRC_UPD_GENTIANNAI
    -- プロシージャ名      ： 現地案内実施処理
    -- 引数             ： param_kaisya_cd           IN  VARCHAR2     -- 会社コード
    --                 ： param_uketuke_no          IN  VARCHAR2     -- 受付番号
    --                 ： param_yoyaku_cd           IN  VARCHAR2     -- 予約番号
    --                 ： param_tantou_cd           IN  VARCHAR2     -- 担当者
    --                 ： param_update              IN  VARCHAR2     -- 日付
    --                 ： rst_code                  OUT  VARCHAR2    -- メッセージコード
    --                 ： rst_msg                   OUT  VARCHAR2    -- メッセージ
    -- 処理概要          ： 現地案内実施処理
    -- 初版作成日/作成者 ： 2012/03/22 Ver.1.0.0 SYS_馮強華
    -- ---------------------------------------------------------
    PROCEDURE PRC_UPD_GENTIANNAI
    (
        param_kaisya_cd  IN VARCHAR2
       ,param_uketuke_no IN VARCHAR2
       ,param_tantou_cd  IN VARCHAR2
       ,param_gentidate  IN VARCHAR2
       ,param_bukkenNo   IN VARCHAR2
       ,param_heyaNo     IN VARCHAR2
       ,param_Seq        IN VARCHAR2
       ,param_update     IN VARCHAR2
       ,rst_code         OUT VARCHAR2
       ,rst_msg          OUT VARCHAR2
    ) IS
        nowupdate VARCHAR2(30);

        bukkennoList PKG_COMMON.tyStrSplit;
        heyanoList   PKG_COMMON.tyStrSplit;
        seqList      PKG_COMMON.tyStrSplit;
        updateList   PKG_COMMON.tyStrSplit;
    BEGIN

        bukkennoList := PKG_COMMON.FNC_SPLIT(param_bukkenNo, ',');
        heyanoList   := PKG_COMMON.FNC_SPLIT(param_heyaNo, ',');
        seqList      := PKG_COMMON.FNC_SPLIT(param_Seq, ',');
        updateList   := PKG_COMMON.FNC_SPLIT(param_update, ',');

        FOR i IN 1 .. bukkennoList.COUNT
        LOOP
            --排他チェックを行う
            SELECT TO_CHAR(upd_date, 'YYYY/MM/DD HH24:MI:SS')
              INTO nowupdate
              FROM UD_GENTI_ANNAI_YOYAKU T
             WHERE KAISYA_CD = param_kaisya_cd
               AND UKETUKE_NO = param_uketuke_no
               AND T.HOMEMATE_BUKKEN_NO = bukkennoList(i)
               AND T.HEYA_NO = heyanoList(i)
               AND T.SEQ = seqList(i)
               AND T.LOGICAL_DEL_FLG = '0';

            IF nowupdate != updateList(i)
            THEN
                ROLLBACK;
                rst_code := 'ERRS009';
                RETURN;
            END IF;


            --来店予約データを更新する。
            UPDATE UD_GENTI_ANNAI_YOYAKU T
               SET T.GENTI_ANNAI_FLG = '1'
                  ,UPD_DATE          = SYSDATE
                  ,UPD_TANTOU_CD     = param_tantou_cd --更新担当者コード
             WHERE KAISYA_CD = param_kaisya_cd
               AND UKETUKE_NO = param_uketuke_no
               AND T.HOMEMATE_BUKKEN_NO = bukkennoList(i)
               AND T.HEYA_NO = heyanoList(i)
               AND T.SEQ = seqList(i);

            --紹介物件データ更新(現地案内実施)
            UPDATE UD_SHOUKAI_BUKKEN T
               SET T.GENTI_ANNAI_FLG = '1'
                  , --''1'(実施済)
                   UPD_DATE          = SYSDATE
                  , --更新日付
                   UPD_TANTOU_CD     = param_tantou_cd --更新担当者コード
             WHERE KAISYA_CD = param_kaisya_cd
               AND UKETUKE_NO = param_uketuke_no
               AND T.HOMEMATE_BUKKEN_NO = bukkennoList(i)
               AND T.HEYA_NO = heyanoList(i)
               AND T.GENTI_ANNAI_FLG = '0';

            --来店受付データ更新(最終追客日更新)
            UPDATE UD_RAITEN_UKETUKE T
               SET T.LAST_TUIKYAKU_DATE = TO_DATE(param_gentidate, 'YYYY-MM-DD HH24:MI:SS')
                  ,UPD_DATE             = SYSDATE
                  , --更新日付
                   UPD_TANTOU_CD        = param_tantou_cd --更新担当者コード
             WHERE KAISYA_CD = param_kaisya_cd
               AND UKETUKE_NO = param_uketuke_no
               AND  (T.LAST_TUIKYAKU_DATE IS NULL OR T.LAST_TUIKYAKU_DATE < TO_DATE(param_gentidate, 'YYYY-MM-DD HH24:MI:SS'));

        END LOOP;

        COMMIT;
        rst_code := msg_success;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := 'ERRS002';
            rst_msg  := SQLERRM;
            ROLLBACK;
    END;

    -- ---------------------------------------------------------
    -- システム名         ： 受付紹介システム
    -- プロシージャID      ： PRC_GET_SHOUDANRIREKI
    -- プロシージャ名      ： 商談履歴取得処理
    -- 引数             ： param_kaisya_cd            IN  VARCHAR2         -- 会社コード
    --                 ： param_uketuke_no           IN  VARCHAR2         -- 受付番号
    --                 ： out_syodanNaiyo            OUT  typCSR           -- 商談内容
    --2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
    --                 ： out_taketuTyusi            OUT  typCSR           -- 他決/中止内容
    --2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
    --                 ： out_tirashiPrint           OUT  typCSR         -- チラシ印刷内容
    --                 ： out_mitumori               OUT  typCSR            -- 見積書内容
    --                 ： out_raitenyoyaku           OUT  typCSR           -- 来店内容
    --                 ： rst_code                   OUT  VARCHAR2         -- メッセージコード
    --                 ： rst_msg                    OUT  VARCHAR2         -- メッセージ
    -- 処理概要          ： 商談履歴取得処理
    -- 初版作成日/作成者 ： 2012/03/22 Ver.1.0.0 SYS_馮強華
    --          変更情報: 2020/06/24 Ver.1.0.1 劉恆毅(SYS) C43040-2_法人の入力項目追加対応
    -- 変更情報        :  2021/01/14 Ver.1.0.2 南柯創(SYS) C45011_物件チラシメール送信
    -- ---------------------------------------------------------
    PROCEDURE PRC_GET_SHOUDANRIREKI
    (
        param_kaisya_cd  IN VARCHAR2
       ,param_uketuke_no IN VARCHAR2
       ,out_syodanNaiyo  OUT typCSR
       --2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
       ,out_taketuTyusi  OUT typCSR
       --2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
       ,out_tirashiPrint OUT typCSR
       ,out_mitumori     OUT typCSR
       ,out_raitenyoyaku OUT typCSR
     --2021/01/04 add start C45011_物件チラシメール送信　南柯創(SYS)
       ,out_mailSoushin  OUT typCSR
       --2021/01/04 add end C45011_物件チラシメール送信 南柯創(SYS)
       ,rst_code         OUT VARCHAR2
       ,rst_msg          OUT VARCHAR2
    ) IS
    BEGIN

        --商談履歴情報を取得
        OPEN out_syodanNaiyo FOR
        --商談内容データ
            SELECT T.SHOUDAN_SEQ AS SHOUDAN_SEQ
                  ,T.SHOUDAN_KB AS SHOUDAN_KB
                  ,TO_CHAR(T.SHOUDAN_DATE, 'YYYY/MM/dd HH24:MI') AS SHOUDAN_DATE
                  ,TO_CHAR(T.SHOUDAN_DATE, 'HH24:MI') AS SHOUDAN_TIME
                  ,T.SHOUDAN_NAIYOU AS SHOUDAN_NAIYOU
                  ,TO_CHAR(T.UPD_DATE, 'YYYY/MM/DD HH24:MI:SS') AS UPD_DATE
                  ,'0' AS SHOUDAN_SYUBETU_KB
                  ,TO_CHAR(T.SHOUDAN_DATE, 'YYYY/MM/dd') AS ORDER_SHOUDAN_DATE
              FROM UD_SHOUDAN_NAIYOU T
             WHERE T.LOGICAL_DEL_FLG = '0'
               AND T.KAISYA_CD = param_kaisya_cd
               AND T.UKETUKE_NO = param_uketuke_no
               AND T.SHOUDAN_KB IS NOT NULL 
             ORDER BY T.SHOUDAN_DATE DESC;
             
        --2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
        --他決/中止履歴情報を取得
        OPEN out_taketuTyusi FOR
        --他決/中止内容データ
            SELECT T.SHOUDAN_SEQ AS SHOUDAN_SEQ
                  ,T.SHOUDAN_KB AS SHOUDAN_KB
                  ,TO_CHAR(T.SHOUDAN_DATE, 'YYYY/MM/dd HH24:MI') AS SHOUDAN_DATE
                  ,TO_CHAR(T.SHOUDAN_DATE, 'HH24:MI') AS SHOUDAN_TIME
                  ,T.SHOUDAN_NAIYOU AS SHOUDAN_NAIYOU
                  ,TO_CHAR(T.UPD_DATE, 'YYYY/MM/DD HH24:MI:SS') AS UPD_DATE
                  ,'4' AS SHOUDAN_SYUBETU_KB
                  ,TO_CHAR(T.SHOUDAN_DATE, 'YYYY/MM/dd') AS ORDER_SHOUDAN_DATE
              FROM UD_SHOUDAN_NAIYOU T
             WHERE T.LOGICAL_DEL_FLG = '0'
               AND T.KAISYA_CD = param_kaisya_cd
               AND T.UKETUKE_NO = param_uketuke_no
               AND T.SHOUDAN_KB IS NULL 
             ORDER BY T.SHOUDAN_DATE DESC;
        --2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
        
        --.紹介物件データ(チラシ印刷データ作成用)
        OPEN out_tirashiPrint FOR
            SELECT '' AS SHOUDAN_SEQ
                  ,'' AS SHOUDAN_KB
                  ,TO_CHAR(BK.TIRASHI_PRINT_DATE, 'YYYY/MM/dd HH24:MI') AS SHOUDAN_DATE
                  ,TO_CHAR(BK.TIRASHI_PRINT_DATE, 'HH24:MI') AS SHOUDAN_TIME
                  ,WD.BUKKEN_NAME || '(' || BK.HEYA_NO || '号室)' AS SHOUDAN_NAIYOU
                  ,TO_CHAR(BK.UPD_DATE, 'YYYY/MM/DD HH24:MI:SS') AS UPD_DATE
                  ,'1' AS SHOUDAN_SYUBETU_KB
                  ,TO_CHAR(BK.TIRASHI_PRINT_DATE, 'YYYY/MM/dd') AS ORDER_SHOUDAN_DATE
              FROM UD_SHOUKAI_BUKKEN BK
                  ,TOKEN.WD_BUKKEN   WD
                  ,TOKEN.WD_HEYA     HY
             WHERE BK.LOGICAL_DEL_FLG = '0'
               AND WD.LOGICAL_DEL_FLG = '0'
               AND HY.LOGICAL_DEL_FLG = '0'
               AND BK.HOMEMATE_BUKKEN_NO = WD.HOMEMATE_BUKKEN_NO
               AND WD.HOMEMATE_BUKKEN_NO = HY.HOMEMATE_BUKKEN_NO
               AND BK.HEYA_NO = HY.HEYA_NO
               AND BK.KAISYA_CD = param_kaisya_cd
               AND BK.UKETUKE_NO = param_uketuke_no
               AND BK.BUKKEN_PARKING_KB = '1'
               AND BK.TIRASHI_PRINT_DATE IS NOT NULL
               AND BK.NO_DISP_FLG = '0'
             ORDER BY ORDER_SHOUDAN_DATE    DESC
                     ,SHOUDAN_TIME          ASC
                     ,WD.HOMEMATE_BUKKEN_NO ASC;
--2021/01/04  add start C45011_物件チラシメール送信 南柯創(SYS)
        --.紹介物件データ(メール送信データ作成用)
        OPEN out_mailSoushin FOR
            SELECT '' AS SHOUDAN_SEQ
                  ,'' AS SHOUDAN_KB
                  ,TO_CHAR(BK.MAIL_SOUSHIN_DATE, 'YYYY/MM/dd HH24:MI') AS SHOUDAN_DATE
                  ,TO_CHAR(BK.MAIL_SOUSHIN_DATE, 'HH24:MI') AS SHOUDAN_TIME
                  ,WD.BUKKEN_NAME || '(' || BK.HEYA_NO || '号室)' AS SHOUDAN_NAIYOU
                  ,TO_CHAR(BK.UPD_DATE, 'YYYY/MM/DD HH24:MI:SS') AS UPD_DATE
                  ,'5' AS SHOUDAN_SYUBETU_KB
                  ,TO_CHAR(BK.MAIL_SOUSHIN_DATE, 'YYYY/MM/dd') AS ORDER_SHOUDAN_DATE
              FROM UD_SHOUKAI_BUKKEN BK
                  ,TOKEN.WD_BUKKEN   WD
                  ,TOKEN.WD_HEYA     HY
             WHERE BK.LOGICAL_DEL_FLG = '0'
               AND WD.LOGICAL_DEL_FLG = '0'
               AND HY.LOGICAL_DEL_FLG = '0'
               AND BK.HOMEMATE_BUKKEN_NO = WD.HOMEMATE_BUKKEN_NO
               AND WD.HOMEMATE_BUKKEN_NO = HY.HOMEMATE_BUKKEN_NO
               AND BK.HEYA_NO = HY.HEYA_NO
               AND BK.KAISYA_CD = param_kaisya_cd
               AND BK.UKETUKE_NO = param_uketuke_no
               AND BK.BUKKEN_PARKING_KB = '1'
               AND BK.MAIL_SOUSHIN_DATE IS NOT NULL
               AND BK.NO_DISP_FLG = '0'
             ORDER BY ORDER_SHOUDAN_DATE    DESC
                     ,SHOUDAN_TIME          ASC
                     ,BK.HOMEMATE_BUKKEN_NO ASC
                     ,BK.HEYA_NO ASC;
--2021/01/04  add end C45011_物件チラシメール送信 南柯創(SYS)
        --見積書データ(見積書発行済データ取得用)
        OPEN out_mitumori FOR
            SELECT '' AS SHOUDAN_SEQ
                  ,'' AS SHOUDAN_KB
                  ,TO_CHAR(MR.MITUMORI_PRINT_DATE, 'YYYY/MM/dd HH24:MI') AS SHOUDAN_DATE
                  ,TO_CHAR(MR.MITUMORI_PRINT_DATE, 'HH24:MI') AS SHOUDAN_TIME
                  ,WD.BUKKEN_NAME || '(' || MR.HEYA_NO || '号室)' AS SHOUDAN_NAIYOU
                  ,TO_CHAR(BK.UPD_DATE, 'YYYY/MM/DD HH24:MI:SS') AS UPD_DATE
                  ,'2' AS SHOUDAN_SYUBETU_KB
                  ,TO_CHAR(MR.MITUMORI_PRINT_DATE, 'YYYY/MM/dd') AS ORDER_SHOUDAN_DATE
              FROM UD_SHOUKAI_BUKKEN BK
                  ,TOKEN.WD_BUKKEN   WD
                  ,TOKEN.WD_HEYA     HY
                  ,UD_MITUMORI       MR
                  ,(SELECT MR.KAISYA_CD,
                        MR.UKETUKE_NO,
                        TO_CHAR(MR.MITUMORI_PRINT_DATE, 'YYYY/MM/DD'),
                        MR.HOMEMATE_BUKKEN_NO,
                        MR.HEYA_NO,
                        MAX(MR.MITUMORI_SEQ) AS MITUMORI_SEQ
                   FROM UD_MITUMORI MR
                  WHERE MR.LOGICAL_DEL_FLG = '0'
                    AND MR.UKETUKE_NO = param_uketuke_no
                  GROUP BY MR.KAISYA_CD,
                           MR.UKETUKE_NO,
                           TO_CHAR(MR.MITUMORI_PRINT_DATE, 'YYYY/MM/DD'),
                           MR.HOMEMATE_BUKKEN_NO,
                           MR.HEYA_NO) MR1
             WHERE BK.LOGICAL_DEL_FLG = '0'
               AND WD.LOGICAL_DEL_FLG = '0'
               AND HY.LOGICAL_DEL_FLG = '0'
               AND BK.HOMEMATE_BUKKEN_NO = WD.HOMEMATE_BUKKEN_NO
               AND WD.HOMEMATE_BUKKEN_NO = HY.HOMEMATE_BUKKEN_NO
               AND BK.KAISYA_CD = param_kaisya_cd
               AND BK.UKETUKE_NO = param_uketuke_no
               AND BK.KAISYA_CD = MR.KAISYA_CD
               AND BK.UKETUKE_NO = MR.UKETUKE_NO
               AND BK.HOMEMATE_BUKKEN_NO = MR.HOMEMATE_BUKKEN_NO
               AND BK.HEYA_NO = MR.HEYA_NO
               AND MR.HEYA_NO = HY.HEYA_NO
               AND BK.BUKKEN_PARKING_KB = '1'
               AND BK.NO_DISP_FLG = '0'
               AND BK.MITUMORISHO_FLG = '1'
               AND MR.LOGICAL_DEL_FLG = '0'
               AND MR.KAISYA_CD = MR1.KAISYA_CD
               AND MR.UKETUKE_NO = MR1.UKETUKE_NO
               AND MR.MITUMORI_SEQ = MR1.MITUMORI_SEQ
             ORDER BY ORDER_SHOUDAN_DATE    DESC
                     ,SHOUDAN_TIME          ASC
                     ,MR.HOMEMATE_BUKKEN_NO ASC;

        --現地案内予約データ(現地案内実施済データ取得用)
        OPEN out_raitenyoyaku FOR
            SELECT '' AS SHOUDAN_SEQ
                  ,'' AS SHOUDAN_KB
                  ,TO_CHAR(AY.GENTI_ANNAI_YOYAKU_DATE, 'YYYY/MM/dd HH24:MI') AS SHOUDAN_DATE --現地案内実施済年月日
                  ,TO_CHAR(AY.GENTI_ANNAI_YOYAKU_DATE, 'HH24:MI') AS SHOUDAN_TIME -- 現地案内実施済時間
                  ,WD.BUKKEN_NAME || '(' || BK.HEYA_NO || '号室)' AS SHOUDAN_NAIYOU --物件名
                  ,TO_CHAR(AY.UPD_DATE, 'YYYY/MM/DD HH24:MI:SS') AS UPD_DATE
                  ,'3' AS SHOUDAN_SYUBETU_KB --商談内容登録区分
                  ,TO_CHAR(AY.Genti_Annai_Yoyaku_Date, 'YYYY/MM/dd') AS ORDER_SHOUDAN_DATE
              FROM UD_GENTI_ANNAI_YOYAKU AY
                  ,UD_SHOUKAI_BUKKEN     BK
                  ,TOKEN.WD_BUKKEN       WD
                  ,TOKEN.WD_HEYA         HY
             WHERE AY.KAISYA_CD = BK.KAISYA_CD
               AND AY.UKETUKE_NO = BK.UKETUKE_NO
               AND AY.HOMEMATE_BUKKEN_NO = BK.HOMEMATE_BUKKEN_NO
               AND AY.HEYA_NO = BK.HEYA_NO
               AND BK.HEYA_NO = HY.HEYA_NO
               AND BK.HOMEMATE_BUKKEN_NO = WD.HOMEMATE_BUKKEN_NO
               AND WD.HOMEMATE_BUKKEN_NO = HY.HOMEMATE_BUKKEN_NO
               AND WD.LOGICAL_DEL_FLG = '0'
               AND HY.LOGICAL_DEL_FLG = '0'
               AND AY.KAISYA_CD = param_kaisya_cd
               AND AY.UKETUKE_NO = param_uketuke_no
               AND AY.LOGICAL_DEL_FLG = '0'
               AND AY.GENTI_ANNAI_FLG = '1'
               AND BK.BUKKEN_PARKING_KB = '1'
             ORDER BY ORDER_SHOUDAN_DATE    DESC
                     ,SHOUDAN_TIME          ASC
                     ,BK.HOMEMATE_BUKKEN_NO ASC
                     ,BK.HEYA_NO            ASC;
        rst_code := msg_success;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := 'ERRS002';
            rst_msg  := SQLERRM;
    END;

    -- ---------------------------------------------------------
    -- システム名         ： 受付紹介システム
    -- プロシージャID      ： PRC_ADD_SINCHAKU
    -- プロシージャ名      ： 希望条件検索ワークに該当受付№の新着物件情報を追加
    -- 引数            ： pv_session_id              IN  VARCHAR2     -- セッションID
    --                ： pv_kaisya_cd               IN  VARCHAR2     -- 会社コード
    --                ： pv_uketuke_cd              IN  VARCHAR2     -- 受付№
    --                ： pv_nowdate                IN  DATE         -- 現在日時
    --                ： rst_code                 OUT  VARCHAR2　　       -- メッセージコード
    --                ： rst_msg                  OUT  VARCHAR2　　  -- 結果区分(0000000:成功 1111111:失敗 ERRS999:エラー)
    -- 処理概要        ： 希望条件検索ワークに該当受付№の新着物件情報を追加する
    -- 初版作成日/作成者 ：  2012/07/09 Ver.1.0.0 SYS_馮強華
    -- 変更情報          ：
    -- ---------------------------------------------------------
    PROCEDURE PRC_ADD_SINCHAKU
    (
        pv_session_id IN VARCHAR2
       ,pv_kaisya_cd  IN VARCHAR2
       ,pv_uketuke_cd IN VARCHAR2
       ,pv_nowdate    IN DATE
       ,rst_code         OUT VARCHAR2
       ,rst_msg       OUT VARCHAR2
    ) IS

    BEGIN
        rst_msg := msg_success;
        PKG_UW_KIBOU_KENSAKU.PRC_ADD_SINCHAKU(pv_session_id, pv_kaisya_cd, pv_uketuke_cd, pv_nowdate, rst_code, rst_msg);
        IF rst_code = MSG_SUCCESS
        THEN
            COMMIT;
        ELSE
            ROLLBACK;

        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            rst_msg := SQLERRM;
            ROLLBACK;
    END;

    -- ---------------------------------------------------------
    -- システム名         ： 受付紹介システム
    -- プロシージャID      ： PRC_ADD_SINCHAKU
    -- プロシージャ名      ： 希望条件検索ワークに該当受付№のチラシ印刷物件追加処理
    -- 引数            ： pv_session_id              IN  VARCHAR2     -- セッションID
    --                ： pv_kaisya_cd               IN  VARCHAR2     -- 会社コード
    --                ： pv_uketuke_cd              IN  VARCHAR2     -- 受付№
    --                ： pv_nowdate                IN  DATE         -- 現在日時
    --                ： rst_code                 OUT  VARCHAR2　　       -- メッセージコード
    --                ： rst_msg                  OUT  VARCHAR2　　  -- 結果区分(0000000:成功 1111111:失敗 ERRS999:エラー)
    -- 処理概要        ： 希望条件検索ワークに該当受付№のチラシ印刷物件追加する
    -- 初版作成日/作成者 ：  2013/03/06  Ver.1.0.0 SYS_楊きょう
    -- 変更情報          ：
    -- ---------------------------------------------------------
    PROCEDURE PRC_ADD_TIRASHI
    (
        pv_session_id IN VARCHAR2
       ,pv_kaisya_cd  IN VARCHAR2
       ,pv_uketuke_cd IN VARCHAR2
       ,pv_nowdate    IN DATE
       ,rst_code      OUT VARCHAR2
       ,rst_msg       OUT VARCHAR2
    ) IS

    BEGIN
        rst_msg := msg_success;
        PKG_UW_KIBOU_KENSAKU.PRC_ADD_TIRASHI(pv_session_id, pv_kaisya_cd, pv_uketuke_cd, pv_nowdate, rst_code, rst_msg);
        IF rst_code = MSG_SUCCESS
        THEN
            COMMIT;
        ELSE
            ROLLBACK;

        END IF;
    EXCEPTION
        WHEN OTHERS THEN
            rst_msg := SQLERRM;
            ROLLBACK;
    END;
    --2019/09/25 ADD START 熊振(SYS) C43089_来店日時の入力チェック対応
    -- ---------------------------------------------------------
    -- システム名         ： 来店日時の入力チェック対応
    -- プロシージャID      ： PRC_UPD_Raiten_Date_INFO
    -- プロシージャ名      ： 来店日時更新する
    -- 引数             ： param_kaisya_cd            IN  VARCHAR2            -- 会社コード
    --                 ： param_uketuke_no            IN  VARCHAR2           -- 受付番号
    --                 ： param_raiten_Date_Ano       IN  VARCHAR2           -- 来店日時
    --                 ： rst_code                 OUT  VARCHAR2          -- メッセージコード
    --                 ： rst_msg                  OUT  VARCHAR2          -- メッセージ
    -- 処理概要          ： 来店日時更新する
    -- 初版作成日/作成者 ： 2019/09/25 Ver.1.0.0 SYS_熊振
    -- ---------------------------------------------------------
    PROCEDURE PRC_UPD_RAITEN_DATE_INFO
    (
        param_kaisya_cd  IN VARCHAR2
       ,param_uketuke_no IN VARCHAR2
       ,param_raiten_Date_Ano IN VARCHAR2
       ,rst_code         OUT VARCHAR2
       ,rst_msg          OUT VARCHAR2
       
    ) IS

    BEGIN
        UPDATE UD_RAITEN_UKETUKE
               SET RAITEN_DATE   =  TO_DATE(param_raiten_Date_Ano,'yyyy/MM/dd hh24:mi:ss')
             WHERE KAISYA_CD = param_kaisya_cd
               AND UKETUKE_NO = param_uketuke_no;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := 'ERRS002';
            rst_msg  := SQLERRM;
            ROLLBACK;
    END;
    --2019/09/25 ADD END 熊振(SYS) C43089_来店日時の入力チェック対応
END PKG_UKG02040;
/