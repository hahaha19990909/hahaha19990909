CREATE OR REPLACE PACKAGE BODY PKG_UKG02044 IS
  -- ---------------------------------------------------------
  -- システム名         ： オンライン仲介システム
  -- パッケージID       ： PKG_UKG02044
  -- パッケージ名       ： オンライン仲介予約
  -- 宣言内容         ： オンライン仲介実施予定日登録を行う
  -- 初版作成日/作成者 ： 2021/04/06 Ver.1.0.0 SYS_楊振華
  -- ---------------------------------------------------------

  -- *********************************
  --    定数宣言
  -- *********************************
  MSG_UNKNOWN CONSTANT CHAR(7) := 'ERRS999';
  MSG_SUCCESS CONSTANT CHAR(7) := '0000000';

  MSG_CHECK   CONSTANT CHAR(7) := 'MSGS002'; --同一時間の予約チェック
  MSG_UPDFAIL CONSTANT CHAR(7) := 'ERRS002'; --ロック


  -- ---------------------------------------------------------
  -- システム名         ： オンライン仲介システム
  -- プロシージャID      ： PRC_GET_ANNAI_INIT_DATA
  -- プロシージャ名      ： 予約日初期値取得
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2          -- 会社コード
  --                 ： pv_uketuke_no            IN  VARCHAR2          -- 受付№
  --                 ： rst_annai_data           OUT  CURSOR　     　    -- 予約状況データ
  --                 ： rst_code                 OUT  VARCHAR2　　       -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　       -- メッセージ
  -- 処理概要          ： 予約日初期値を取得
  -- 初版作成日/作成者 ： 2021/04/06 Ver.1.0.0 SYS_楊振華
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRC_GET_ANNAI_INIT_DATA
  (
    pv_kaisya_cd   IN VARCHAR2
     ,pv_uketuke_no  IN VARCHAR2
     ,rst_annai_data OUT typCSR
     ,rst_code       OUT VARCHAR2
     ,rst_msg        OUT VARCHAR2
  ) IS
  BEGIN
    rst_code := MSG_SUCCESS;

    OPEN rst_annai_data FOR
      SELECT U.*
        FROM (SELECT TO_CHAR(AN.ONLINE_TYUKAI_YOYAKU_DATE, 'YYYY/MM/DD') YOYAKU_DATE
              ,TO_CHAR(AN.ONLINE_TYUKAI_YOYAKU_DATE, 'HH24:MI') YOYAKU_TIME
            FROM UD_ONLINE_TYUKAII_YOYAKU AN
           WHERE AN.KAISYA_CD = pv_kaisya_cd
               AND AN.UKETUKE_NO = pv_uketuke_no
               AND AN.LOGICAL_DEL_FLG = '0'
           ORDER BY AN.UPD_DATE DESC) U
       WHERE ROWNUM < 2;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
      dbms_output.put_line('エラーコード：' || rst_code);
      dbms_output.put_line('エラーメッセージ：' || rst_msg);
  END;
  
  -- ---------------------------------------------------------
  -- システム名         ： オンライン仲介システム
  -- プロシージャID      ： PRC_GET_INIT_INFO
  -- プロシージャ名      ： 予約履歴j情報の取得
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2          -- 会社コード
  --                 ： pv_yoyaku_date           IN  VARCHAR2          -- 予約日
  --                 ： pv_chukaitantou          IN  VARCHAR2　     　   -- 仲介担当者コード
  --                 ： rst_yoyaku_list          OUT  CURSOR　     　    -- 予約状況データ
  --                 ： rst_code                 OUT  VARCHAR2　　       -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　       -- メッセージ
  -- 処理概要          ： 予約状況データを取得
  -- 初版作成日/作成者 ： 2021/04/06 Ver.1.0.0 SYS_楊振華
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRC_GET_YOYAKU_INFO
  (
    pv_kaisya_cd    IN VARCHAR2
     ,pv_yoyaku_date  IN VARCHAR2
     ,pv_chukaitantou IN VARCHAR2
     ,rst_yoyaku_list OUT typCSR
     ,rst_code        OUT VARCHAR2
     ,rst_msg         OUT VARCHAR2
  ) IS
  yoyaku_date DATE; -- 予約日 
  BEGIN
    rst_code := MSG_SUCCESS;
    yoyaku_date := pv_yoyaku_date;
    IF yoyaku_date IS NULL
      THEN
        yoyaku_date := TRUNC(SYSDATE);
    END IF;

    OPEN rst_yoyaku_list FOR
      SELECT U.YOYAKU_DATE AS YOYAKU_DATE
          ,U.YOYAKU_SB   AS YOYAKU_SB
          ,U.KOKYAKU_NAME AS KOKYAKU_NAME
        FROM (SELECT TO_CHAR(UOTY.ONLINE_TYUKAI_YOYAKU_DATE, 'HH24:MI') AS YOYAKU_DATE --予約時間
             ,URU.KOKYAKU_NAME AS KOKYAKU_NAME -- 顧客名
             ,'オンライン仲介' AS YOYAKU_SB --予約区分
           FROM UD_ONLINE_TYUKAII_YOYAKU UOTY
           LEFT JOIN UD_RAITEN_UKETUKE URU
             ON UOTY.KAISYA_CD = URU.KAISYA_CD
            AND UOTY.UKETUKE_NO = URU.UKETUKE_NO
            AND URU.KAISYA_CD = pv_kaisya_cd
            AND URU.LOGICAL_DEL_FLG = '0'
          WHERE UOTY.KAISYA_CD = pv_kaisya_cd --会社コード
            AND TRUNC(UOTY.ONLINE_TYUKAI_YOYAKU_DATE) = pv_yoyaku_date --予約日 
            AND UOTY.CHUKAI_TANTOU_CD = pv_chukaitantou --仲介担当者コード
            AND UOTY.LOGICAL_DEL_FLG = '0'
        UNION ALL
        SELECT TO_CHAR(URY.YOYAKU_DATE, 'HH24:MI') AS YOYAKU_DATE --受付時間
              ,URU.KOKYAKU_NAME AS KOKYAKU_NAME --顧客名
              ,'来　店' AS YOYAKU_SB --予約区分
            FROM UD_RAITEN_YOYAKU URY
            LEFT JOIN UD_RAITEN_UKETUKE URU
              ON URY.KAISYA_CD = URU.KAISYA_CD
             AND URY.UKETUKE_NO = URU.UKETUKE_NO
             AND URU.KAISYA_CD = pv_kaisya_cd
             AND URU.LOGICAL_DEL_FLG = '0'
           WHERE URY.KAISYA_CD = pv_kaisya_cd --会社コード
             AND TRUNC(URY.YOYAKU_DATE) = pv_yoyaku_date --予約日
             AND URY.CHUKAI_TANTOU_CD = pv_chukaitantou --仲介担当者コード
             AND URY.LOGICAL_DEL_FLG = '0'
          UNION ALL
          SELECT TO_CHAR(UGAY.GENTI_ANNAI_YOYAKU_DATE, 'HH24:MI') AS YOYAKU_DATE --案内時間
              ,URU.KOKYAKU_NAME AS KOKYAKU_NAME -- 顧客名
              ,'現地案内' AS YOYAKU_SB --予約区分
            FROM UD_GENTI_ANNAI_YOYAKU UGAY
            LEFT JOIN UD_RAITEN_UKETUKE URU
              ON UGAY.KAISYA_CD = URU.KAISYA_CD
             AND UGAY.UKETUKE_NO = URU.UKETUKE_NO
             AND URU.KAISYA_CD = pv_kaisya_cd
             AND URU.LOGICAL_DEL_FLG = '0'
           WHERE UGAY.KAISYA_CD = pv_kaisya_cd --会社コード
             AND TRUNC(UGAY.GENTI_ANNAI_YOYAKU_DATE) = pv_yoyaku_date --予約日
             AND UGAY.GENTI_ANNAI_FLG = '0'
             AND UGAY.CHUKAI_TANTOU_CD = pv_chukaitantou --仲介担当者コード
             AND UGAY.LOGICAL_DEL_FLG = '0'
           GROUP BY UGAY.KAISYA_CD
               ,URU.KOKYAKU_NAME
               ,UGAY.UKETUKE_NO
               ,UGAY.GENTI_ANNAI_YOYAKU_DATE
               ) U
       ORDER BY U.YOYAKU_DATE;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
      dbms_output.put_line('エラーコード：' || rst_code);
      dbms_output.put_line('エラーメッセージ：' || rst_msg);
  END;

  -- ---------------------------------------------------------
  -- システム名         ： オンライン仲介システム
  -- プロシージャID      ： PRC_CHECK_YOYAKU_TIME
  -- プロシージャ名      ： 同一時間の予約チェック
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2         -- 会社コード
  --                 ： pv_uketuke_no            IN  VARCHAR2         -- 受付№
  --                 ： pv_chukaitantou          IN  VARCHAR2　     　  -- 仲介担当者コード
  --                 ： pv_post_cd               IN  VARCHAR2         -- 店舗コード
  --                 ： rst_yoyaku               OUT  CURSOR　     　   -- 予約状況データ
  --                 ： rst_code                 OUT  VARCHAR2　　      -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　      -- メッセージ
  -- 処理概要          ： 予約状況データを取得
  -- 初版作成日/作成者 ： 2021/04/06 Ver.1.0.0 SYS_楊振華
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRC_GET_ONLINE_MAIL
  (
    pv_kaisya_cd      IN VARCHAR2
     ,pv_uketuke_no   IN VARCHAR2
     ,pv_chukaitantou IN VARCHAR2
     ,pv_post_cd      IN VARCHAR2
     ,rst_yoyaku      OUT typCSR
     ,rst_code        OUT VARCHAR2
     ,rst_msg         OUT VARCHAR2
  ) IS
  BEGIN
    rst_code := MSG_SUCCESS;
    OPEN rst_yoyaku FOR
    SELECT URU.KOKYAKU_NAME AS KOKYAKU_NAME
          ,URU.E_MAIL_ADDRESS AS E_MAIL_ADDRESS
          ,WP.POST_NAME AS POST_NAME
          ,WT.TANTOU_NAME AS TANTOU_NAME
          ,WP.TEL_NO AS TEL_NO
          ,TCMS.E_MAIL_ADDRESS AS SYAIN_E_MAIL_ADDRESS
      FROM TOKEN.WM_TANTOU WT
      LEFT JOIN UD_RAITEN_UKETUKE URU
        ON WT.KAISYA_CD = URU.KAISYA_CD
       AND URU.KAISYA_CD = pv_kaisya_cd
       AND UKETUKE_NO = pv_uketuke_no
       AND URU.LOGICAL_DEL_FLG = '0'
      INNER JOIN TK_COMMON.M_SYAIN TCMS
        ON WT.USER_ID = TCMS.TANTOU_CD
      INNER JOIN TOKEN.WM_TANTOU_POST WTP
        ON WT.TANTOU_CD = WTP.TANTOU_CD
       AND WTP.POST_CD = pv_post_cd
       AND WTP.LOGICAL_DEL_FLG = '0'
      INNER JOIN TOKEN.WM_POST WP
        ON WTP.KAISYA_CD = WP.KAISYA_CD
       AND WTP.POST_CD = WP.POST_CD
       AND WP.LOGICAL_DEL_FLG = '0'
      WHERE WT.TANTOU_CD = pv_chukaitantou
        AND WT.LOGICAL_DEL_FLG = '0';
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
      dbms_output.put_line('エラーコード：' || rst_code);
      dbms_output.put_line('エラーメッセージ：' || rst_msg);
  END;
  
  -- ---------------------------------------------------------
  -- システム名         ： オンライン仲介システム
  -- プロシージャID      ： PRC_ADD_YOYAKU_INFO
  -- プロシージャ名      ： 次回来店・現地案内予約
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2            -- 会社コード
  --                 ： pv_uketuke_no            IN  VARCHAR2           -- 受付番号
  --                 ： pv_yoyaku_date           IN  VARCHAR2           -- 予約日
  --                 ： rst_yoyaku               OUT  CURSOR　     　     -- 予約状況データ
  --                 ： rst_code                 OUT  VARCHAR2　　        -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　        -- メッセージ
  -- 処理概要          ： 予約データ追加処理
  -- 初版作成日/作成者 ： 2021/04/06 Ver.1.0.0 SYS_楊振華
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE GET_ACCESS_KEY
  (
    pv_kaisya_cd      IN VARCHAR2
     ,pv_uketuke_no   IN VARCHAR2
     ,pv_yoyaku_date  IN VARCHAR2
     ,rst_yoyaku      OUT typCSR
     ,rst_code        OUT VARCHAR2
     ,rst_msg         OUT VARCHAR2
  ) IS
  yoyaku_date Date;
  BEGIN
    yoyaku_date := to_date(pv_yoyaku_date,'yyyy/MM/dd hh24:mi:ss');
    OPEN rst_yoyaku FOR
    SELECT ACCESS_KEY
      FROM UD_ONLINE_TYUKAII_YOYAKU
      WHERE KAISYA_CD = pv_kaisya_cd
        AND UKETUKE_NO = pv_uketuke_no
        AND ONLINE_TYUKAI_YOYAKU_DATE = yoyaku_date
        AND LOGICAL_DEL_FLG = '0';
  END;

  -- ---------------------------------------------------------
  -- システム名         ： オンライン仲介システム
  -- プロシージャID      ： PRC_ADD_YOYAKU_INFO
  -- プロシージャ名      ： 次回来店・現地案内予約
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2            -- 会社コード
  --                 ： pv_uketuke_no            IN  VARCHAR2           -- 受付NO.
  --                 ： pv_yoyaku_date           IN  VARCHAR2           -- オンライン仲介予約日
  --                 ： pv_access_key            IN  VARCHAR2           -- アクセスキー
  --                 ： pv_video_url             IN  VARCHAR2           -- ビデオ通話URL
  --                 ： pv_uketuke_post_cd       IN  VARCHAR2　     　    -- 受付店舗コード
  --                 ： pv_chukai_tantou_cd      IN  VARCHAR2           -- 仲介担当者コード
  --                 ： rst_code                 OUT  VARCHAR2　　        -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　        -- メッセージ
  -- 処理概要          ： オンライン仲介予約データ追加処理
  -- 初版作成日/作成者 ： 2021/04/06 Ver.1.0.0 SYS_楊振華
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRC_ADD_YOYAKU_INFO
  (
    pv_kaisya_cd    IN VARCHAR2
     ,pv_uketuke_no   IN VARCHAR2
     ,pv_yoyaku_date  IN VARCHAR2
     ,pv_access_key   IN VARCHAR2
     ,pv_video_url    IN VARCHAR2
     ,pv_uketuke_post_cd  IN VARCHAR2
     ,pv_chukai_tantou_cd IN VARCHAR2
     ,rst_code        OUT VARCHAR2
     ,rst_msg         OUT VARCHAR2
  ) IS
    sys_date DATE;
  BEGIN
    sys_date := SYSDATE;
    rst_code := MSG_SUCCESS;

    --オンライン仲介予約データ追加処理
    INSERT INTO UD_ONLINE_TYUKAII_YOYAKU
      (KAISYA_CD
      ,UKETUKE_NO
      ,ONLINE_TYUKAI_YOYAKU_DATE
      ,ACCESS_KEY
      ,VIDEO_URL
      ,UKETUKE_POST_CD
      ,CHUKAI_TANTOU_CD
      ,APP_DATE
      ,APP_TANTOU_CD
      ,UPD_DATE
      ,UPD_TANTOU_CD
      ,LOGICAL_DEL_FLG
      ,LOGICAL_DEL_DATE
      ,DEL_TANTOU_CD)
    VALUES
      (pv_kaisya_cd
      ,pv_uketuke_no
      ,to_date(pv_yoyaku_date,'yyyy/MM/dd hh24:mi:ss')
      ,pv_access_key
      ,pv_video_url
      ,pv_uketuke_post_cd
      ,pv_chukai_tantou_cd
      ,sys_date
      ,pv_chukai_tantou_cd
      ,sys_date
      ,pv_chukai_tantou_cd
      ,'0'
      ,NULL
      ,NULL);
    COMMIT;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UPDFAIL;
      rst_msg  := SQLERRM;
      dbms_output.put_line('エラーコード：' || rst_code);
      dbms_output.put_line('エラーメッセージ：' || rst_msg);
      ROLLBACK;
  END;

  -- ---------------------------------------------------------
  -- システム名         ： オンライン仲介システム
  -- プロシージャID      ： PRC_ADD_ANNAI_YOYAKU
  -- プロシージャ名      ： 予約データ更新処理
  -- 引数             ： pv_kaisya_cd              IN  VARCHAR2            -- 会社コード
  --                 ： pv_uketuke_no              IN  VARCHAR2           -- 受付NO. 
  --                 ： pv_yoyaku_date_old         IN  VARCHAR2　     　    -- 修正前オンライン仲介予約日
  --                 ： pv_yoyaku_date             IN  VARCHAR2           -- オンライン仲介予約日
  --                 ： pv_video_url               IN  VARCHAR2           -- ビデオ通話URL
  --                 ： pv_chukai_tantou_cd        IN  VARCHAR2           -- 仲介担当者コード
  --                 ： rst_code                   OUT VARCHAR2　　         -- メッセージコード
  --                 ： rst_msg                    OUT VARCHAR2　　         -- メッセージ
  -- 処理概要          ： オンライン仲介予約データ更新処理
  -- 初版作成日/作成者 ： 2021/04/06 Ver.1.0.0 SYS_楊振華
  -- ---------------------------------------------------------
  PROCEDURE PRC_UPD_YOYAKU_INFO
  (
    pv_kaisya_cd             IN  VARCHAR2
     ,pv_uketuke_no          IN  VARCHAR2
     ,pv_yoyaku_date_old     IN  VARCHAR2
     ,pv_yoyaku_date         IN  VARCHAR2
     ,pv_video_url           IN  OUT VARCHAR2
     ,pv_chukai_tantou_cd    IN  VARCHAR2
     ,rst_code               OUT VARCHAR2
     ,rst_msg                OUT VARCHAR2
  ) IS
    sys_date     DATE;
  BEGIN
    sys_date := SYSDATE;
    rst_code := MSG_SUCCESS;
    UPDATE UD_ONLINE_TYUKAII_YOYAKU
    SET ONLINE_TYUKAI_YOYAKU_DATE = to_date(pv_yoyaku_date,'yyyy/MM/dd hh24:mi:ss')
       ,VIDEO_URL = pv_video_url
       ,UPD_DATE = sys_date
       ,UPD_TANTOU_CD = pv_chukai_tantou_cd
     WHERE KAISYA_CD = pv_kaisya_cd
       AND UKETUKE_NO = pv_uketuke_no
       AND ONLINE_TYUKAI_YOYAKU_DATE = to_date(pv_yoyaku_date_old,'yyyy/MM/dd hh24:mi:ss');
    COMMIT;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UPDFAIL;
      rst_msg  := SQLERRM;
      dbms_output.put_line('エラーコード：' || rst_code);
      dbms_output.put_line('エラーメッセージ：' || rst_msg);
      ROLLBACK;
  END;
  
  -- ---------------------------------------------------------
  -- システム名         ： オンライン仲介システム
  -- プロシージャID      ： PRC_CHECK_YOYAKU_TIME
  -- プロシージャ名      ： 同一時間の予約チェック
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2         -- 会社コード
  --                 ： pv_yoyaku_date           IN  VARCHAR2         -- 予約日
  --                 ： pv_chukaitantou          IN  VARCHAR2　     　  -- 仲介担当者コード
  --                 ： rst_code                 OUT  VARCHAR2　　      -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　      -- メッセージ
  -- 処理概要          ： 予約状況データを取得
  -- 初版作成日/作成者 ： 2021/04/06 Ver.1.0.0 SYS_楊振華
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRC_CHECK_YOYAKU_TIME
  (
    pv_kaisya_cd    IN VARCHAR2
     ,pv_yoyaku_date  IN VARCHAR2
     ,pv_chukaitantou IN VARCHAR2
     ,rst_code        OUT VARCHAR2
     ,rst_msg         OUT VARCHAR2
  ) IS
    raitenn_kennsuu NUMBER(11);
    annai_kennsuu   NUMBER(11);
    yoyaku_kennsuu  NUMBER(11);
  BEGIN
    rst_code := MSG_SUCCESS;
    SELECT COUNT(UKETUKE_NO)
      INTO raitenn_kennsuu
      FROM UD_RAITEN_YOYAKU
     WHERE KAISYA_CD = pv_kaisya_cd --会社コード
       AND TO_CHAR(YOYAKU_DATE, 'YYYY/MM/DD HH24:MI') =
         TO_CHAR(TO_DATE(pv_yoyaku_date, 'YYYY/MM/DD HH24:MI'), 'YYYY/MM/DD HH24:MI') --予約日
       AND CHUKAI_TANTOU_CD = pv_chukaitantou --仲介担当者コード
       AND LOGICAL_DEL_FLG = '0';
    SELECT COUNT(UKETUKE_NO)
      INTO annai_kennsuu
      FROM UD_GENTI_ANNAI_YOYAKU
     WHERE KAISYA_CD = pv_kaisya_cd --会社コード
       AND TO_CHAR(GENTI_ANNAI_YOYAKU_DATE, 'YYYY/MM/DD HH24:MI') =
         TO_CHAR(TO_DATE(pv_yoyaku_date, 'YYYY/MM/DD HH24:MI'), 'YYYY/MM/DD HH24:MI') --予約日
       AND CHUKAI_TANTOU_CD = pv_chukaitantou --仲介担当者コード
       AND LOGICAL_DEL_FLG = '0';
    SELECT COUNT(UKETUKE_NO)
      INTO yoyaku_kennsuu
      FROM UD_ONLINE_TYUKAII_YOYAKU
     WHERE KAISYA_CD = pv_kaisya_cd --会社コード
       AND TO_CHAR(ONLINE_TYUKAI_YOYAKU_DATE, 'YYYY/MM/DD HH24:MI') =
         TO_CHAR(TO_DATE(pv_yoyaku_date, 'YYYY/MM/DD HH24:MI'), 'YYYY/MM/DD HH24:MI') --予約日
       AND CHUKAI_TANTOU_CD = pv_chukaitantou --仲介担当者コード
       AND LOGICAL_DEL_FLG = '0';
    IF raitenn_kennsuu + annai_kennsuu + yoyaku_kennsuu > 0
    THEN
      rst_code := MSG_CHECK;
    END IF;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
      dbms_output.put_line('エラーコード：' || rst_code);
      dbms_output.put_line('エラーメッセージ：' || rst_msg);
  END;
END PKG_UKG02044;
/