CREATE OR REPLACE PACKAGE BODY PKG_UKG02041 IS
  -- ---------------------------------------------------------
  -- システム名         ： 受付紹介システム
  -- パッケージID       ： PKG_UKG02041
  -- パッケージ名       ： 次回来店・現地案内予約
  -- 宣言内容         ： 次回来店・現地案内の予約登録を行う
  -- 初版作成日/作成者 ： 2011/11/14 Ver.1.0.0 SYS_戴氷馨
  -- [修 正] by 趙雲剛 2012/03/29 step2対応
  -- [修 正] by カク年昇(SYS) 2019/06/24 C43089_来店日時の入力チェック対応
  -- [修 正] by 楊振華(SYS) 2021/04/02 C45032_オンライン仲介システムの導入
  -- ---------------------------------------------------------

  -- *********************************
  --    定数宣言
  -- *********************************
  MSG_UNKNOWN CONSTANT CHAR(7) := 'ERRS999';
  MSG_SUCCESS CONSTANT CHAR(7) := '0000000';

  MSG_CHECK   CONSTANT CHAR(7) := 'MSGS002'; --同一時間の予約チェック
  MSG_UPDFAIL CONSTANT CHAR(7) := 'ERRS002'; --ロック

  -- ---------------------------------------------------------
  -- システム名         ： 受付紹介システム
  -- プロシージャID      ： PRC_GET_ANNAI_INIT_DATA
  -- プロシージャ名      ： 予約日初期値取得
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2          -- 会社コード
  --                 ： pv_uketuke_no           IN  VARCHAR2          -- 受付№
  --                 ： rst_annai_data          OUT  CURSOR　     　    -- 予約状況データ
  --                 ： rst_code                 OUT  VARCHAR2　　   -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　   -- メッセージ
  -- 処理概要          ： 予約状況データを取得
  -- 初版作成日/作成者 ： 2011/11/14 Ver.1.0.0 SYS_戴氷馨
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
        FROM (SELECT TO_CHAR(AN.GENTI_ANNAI_YOYAKU_DATE, 'YYYY/MM/DD') YOYAKU_DATE
              ,TO_CHAR(AN.GENTI_ANNAI_YOYAKU_DATE, 'HH24:MI') YOYAKU_TIME
            FROM UD_GENTI_ANNAI_YOYAKU AN
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
  -- システム名         ： 受付紹介システム
  -- プロシージャID      ： PRC_GET_INIT_INFO
  -- プロシージャ名      ： 予約状況データを取得
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2          -- 会社コード
  --                 ： pv_yoyaku_date           IN  VARCHAR2          -- 予約日
  --                 ： pv_chukaitantou          IN  VARCHAR2　     　    -- 仲介担当者コード
  --                 ： rst_yoyaku_list          OUT  CURSOR　     　    -- 予約状況データ
  --                 ： rst_code                 OUT  VARCHAR2　　   -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　   -- メッセージ
  -- 処理概要          ： 予約状況データを取得
  -- 初版作成日/作成者 ： 2011/11/14 Ver.1.0.0 SYS_戴氷馨
  -- 変更情報          ： 2021/04/02 Ver.1.0.1 SYS_楊振華 C45032_オンライン仲介システムの導入
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
  BEGIN
    rst_code := MSG_SUCCESS;

    OPEN rst_yoyaku_list FOR
    -- 2021/04/02 UPD START 楊振華(SYS) C45032_オンライン仲介システムの導入
      /*SELECT U.YOYAKU_DATE AS YOYAKU_DATE
				  ,U.YOYAKU_SB   AS YOYAKU_SB
			  FROM (SELECT TO_CHAR(YOYAKU_DATE, 'HH24:MI') AS YOYAKU_DATE
						  , --受付時間
						   '来　店' AS YOYAKU_SB --予約区分
					  FROM UD_RAITEN_YOYAKU
					 WHERE KAISYA_CD = pv_kaisya_cd --会社コード
					   AND TO_CHAR(YOYAKU_DATE, 'YYYY/MM/DD') = pv_yoyaku_date --予約日
					   AND CHUKAI_TANTOU_CD = pv_chukaitantou --仲介担当者コード
					   AND LOGICAL_DEL_FLG = '0'
					UNION ALL
					SELECT TO_CHAR(GENTI_ANNAI_YOYAKU_DATE, 'HH24:MI') AS YOYAKU_DATE
						  , --受付時間
						   '現地案内' AS YOYAKU_SB --予約区分
					  FROM UD_GENTI_ANNAI_YOYAKU
					 WHERE KAISYA_CD = pv_kaisya_cd --会社コード
					   AND TO_CHAR(GENTI_ANNAI_YOYAKU_DATE, 'YYYY/MM/DD') = pv_yoyaku_date --予約日
					   AND GENTI_ANNAI_FLG = '0'
					   AND CHUKAI_TANTOU_CD = pv_chukaitantou --仲介担当者コード
					   AND LOGICAL_DEL_FLG = '0'
					 GROUP BY KAISYA_CD
							 ,UKETUKE_NO
							 ,GENTI_ANNAI_YOYAKU_DATE) U
			 ORDER BY U.YOYAKU_DATE;*/
      SELECT U.YOYAKU_DATE AS YOYAKU_DATE
          ,U.YOYAKU_SB   AS YOYAKU_SB
          ,U.KOKYAKU_NAME AS KOKYAKU_NAME
        FROM (SELECT TO_CHAR(URY.YOYAKU_DATE, 'HH24:MI') AS YOYAKU_DATE --受付時間
              ,URU.KOKYAKU_NAME AS KOKYAKU_NAME --顧客名
              ,'来　店' AS YOYAKU_SB --予約区分
            FROM UD_RAITEN_YOYAKU URY
            LEFT JOIN UD_RAITEN_UKETUKE URU
              ON URY.KAISYA_CD = URU.KAISYA_CD
             AND URY.UKETUKE_NO = URU.UKETUKE_NO
             AND URU.KAISYA_CD = pv_kaisya_cd
             AND URU.LOGICAL_DEL_FLG = '0'
           WHERE URY.KAISYA_CD = pv_kaisya_cd --会社コード
             AND TO_CHAR(URY.YOYAKU_DATE, 'YYYY/MM/DD') = pv_yoyaku_date --予約日
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
             AND TO_CHAR(UGAY.GENTI_ANNAI_YOYAKU_DATE, 'YYYY/MM/DD') = pv_yoyaku_date --予約日
             AND UGAY.GENTI_ANNAI_FLG = '0'
             AND UGAY.CHUKAI_TANTOU_CD = pv_chukaitantou --仲介担当者コード
             AND UGAY.LOGICAL_DEL_FLG = '0'
           GROUP BY UGAY.KAISYA_CD
               ,UGAY.UKETUKE_NO
               ,URU.KOKYAKU_NAME
               ,UGAY.GENTI_ANNAI_YOYAKU_DATE
           UNION ALL
           SELECT TO_CHAR(UOTY.ONLINE_TYUKAI_YOYAKU_DATE, 'HH24:MI') AS YOYAKU_DATE --予約時間
              ,URU.KOKYAKU_NAME AS KOKYAKU_NAME -- 顧客名
              ,'オンライン仲介' AS YOYAKU_SB --予約区分
             FROM UD_ONLINE_TYUKAII_YOYAKU UOTY
             LEFT JOIN UD_RAITEN_UKETUKE URU
               ON UOTY.KAISYA_CD = URU.KAISYA_CD
              AND UOTY.UKETUKE_NO = URU.UKETUKE_NO
              AND URU.KAISYA_CD = pv_kaisya_cd
              AND URU.LOGICAL_DEL_FLG = '0'
           WHERE UOTY.KAISYA_CD = pv_kaisya_cd --会社コード
             AND TO_CHAR(UOTY.ONLINE_TYUKAI_YOYAKU_DATE, 'YYYY/MM/DD') = pv_yoyaku_date --予約日 
             AND UOTY.CHUKAI_TANTOU_CD = pv_chukaitantou --仲介担当者コード
             AND UOTY.LOGICAL_DEL_FLG = '0'
               ) U
       ORDER BY U.YOYAKU_DATE;
    -- 2021/04/02 UPD END 楊振華(SYS) C45032_オンライン仲介システムの導入
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
      dbms_output.put_line('エラーコード：' || rst_code);
      dbms_output.put_line('エラーメッセージ：' || rst_msg);
  END;

  -- ---------------------------------------------------------
  -- システム名         ： 受付紹介システム
  -- プロシージャID      ： PRC_CHECK_YOYAKU_TIME
  -- プロシージャ名      ： 同一時間の予約チェック
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2         -- 会社コード
  --                 ： pv_yoyaku_date           IN  VARCHAR2         -- 予約日
  --                 ： pv_chukaitantou          IN  VARCHAR2　     　  -- 仲介担当者コード
  --                 ： rst_code                 OUT  VARCHAR2　　      -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　      -- メッセージ
  -- 処理概要          ： 予約状況データを取得
  -- 初版作成日/作成者 ： 2011/11/14 Ver.1.0.0 SYS_戴氷馨
  -- 変更情報          ： 2021/04/02 Ver.1.0.1 SYS_楊振華 C45032_オンライン仲介システムの導入
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
    -- 2021/04/02 ADD START 楊振華(SYS) C45032_オンライン仲介システムの導入
    SELECT COUNT(UKETUKE_NO)
      INTO yoyaku_kennsuu
      FROM UD_ONLINE_TYUKAII_YOYAKU
     WHERE KAISYA_CD = pv_kaisya_cd --会社コード
       AND TO_CHAR(ONLINE_TYUKAI_YOYAKU_DATE, 'YYYY/MM/DD HH24:MI') =
         TO_CHAR(TO_DATE(pv_yoyaku_date, 'YYYY/MM/DD HH24:MI'), 'YYYY/MM/DD HH24:MI') --予約日
       AND CHUKAI_TANTOU_CD = pv_chukaitantou --仲介担当者コード
       AND LOGICAL_DEL_FLG = '0';
    -- 2021/04/02 ADD END 楊振華(SYS) C45032_オンライン仲介システムの導入
    -- 2021/04/02 UPD START 楊振華(SYS) C45032_オンライン仲介システムの導入
    --IF raitenn_kennsuu + raitenn_kennsuu > 0
    IF raitenn_kennsuu + annai_kennsuu + yoyaku_kennsuu > 0
    -- 2021/04/02 UPD END 楊振華(SYS) C45032_オンライン仲介システムの導入
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

  -- ---------------------------------------------------------
  -- システム名         ： 受付紹介システム
  -- プロシージャID      ： PRC_ADD_YOYAKU_INFO
  -- プロシージャ名      ： 次回来店・現地案内予約
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2            -- 会社コード
  --                 ： pv_uketuke_no            IN  VARCHAR2           -- 受付番号
  --                 ： pv_yoyaku_date           IN  VARCHAR2           -- 予約日
  --                 ： pv_post_cd               IN  VARCHAR2           -- 店舗コード
  --                 ： pv_chukaitantou          IN  VARCHAR2           -- 仲介担当者コード
  --                 ： pv_tantou_cd             IN  VARCHAR2　     　    -- 担当者コード
  --                 ： rst_code                 OUT  VARCHAR2　　        -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　        -- メッセージ
  -- 処理概要          ： 予約データ追加処理
  -- 初版作成日/作成者 ： 2012/06/07 Ver.1.0.0 SYS_趙
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRC_ADD_YOYAKU_INFO
  (
    pv_kaisya_cd    IN VARCHAR2
     ,pv_uketuke_no   IN VARCHAR2
     ,pv_yoyaku_date  IN VARCHAR2
     ,pv_post_cd      IN VARCHAR2
     ,pv_chukaitantou IN VARCHAR2
     ,pv_tantou_cd    IN VARCHAR2
     ,rst_code        OUT VARCHAR2
     ,rst_msg         OUT VARCHAR2
  ) IS
    sys_date DATE;
  BEGIN
    sys_date := SYSDATE;
    rst_code := MSG_SUCCESS;
    DELETE FROM UD_RAITEN_YOYAKU
     WHERE KAISYA_CD = pv_kaisya_cd
       AND UKETUKE_NO = pv_uketuke_no
       AND LOGICAL_DEL_FLG = '0';

    --来店予約データ追加処理
    INSERT INTO UD_RAITEN_YOYAKU
      (KAISYA_CD
      ,UKETUKE_NO
      ,YOYAKU_DATE
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
      ,TO_DATE(pv_yoyaku_date, 'YYYY/MM/DD HH24:MI')
      ,pv_post_cd
      ,pv_chukaitantou
      ,sys_date
      ,pv_tantou_cd
      ,sys_date
      ,pv_tantou_cd
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
  -- システム名         ： 受付紹介システム
  -- プロシージャID      ： PRC_ADD_ANNAI_YOYAKU
  -- プロシージャ名      ： 次回来店・現地案内予約
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2            -- 会社コード
  --                 ： pv_uketuke_no            IN  VARCHAR2           -- 受付番号
  --                 ： pv_update_mode           IN  VARCHAR2　     　    -- 更新モード
  --                 ： pv_yoyaku_date           IN  VARCHAR2           -- 予約日
  --                 ： pv_bukken_no             IN  VARCHAR2           -- 物件№
  --                 ： pv_heya_no               IN  VARCHAR2           -- 部屋№
  --                 ： pv_seq                   IN  VARCHAR2           -- 現地案内予約SEQ
  --                 ： pv_bukken_list_bukken_no IN  VARCHAR2           -- 物件/部屋リスト.物件№
  --                 ： pv_bukken_list_heya_no   IN  VARCHAR2           -- 物件/部屋リスト.部屋№
  --                 ： pv_bukken_list_seq       IN  VARCHAR2           -- 物件/部屋リスト.SEQ
  --                 ： pv_post_cd               IN  VARCHAR2           -- 店舗コード
  --                 ： pv_chukaitantou          IN  VARCHAR2           -- 仲介担当者コード
  --                 ： pv_tantou_cd             IN  VARCHAR2　     　    -- 担当者コード
  --                 ： rst_code                 OUT  VARCHAR2　　        -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　        -- メッセージ
  -- 処理概要          ： 予約データ追加処理
  -- 初版作成日/作成者 ： 2012/06/07 Ver.1.0.0 SYS_趙
  -- 変更情報          ： 2019/07/15 Ver.1.0.1 SYS_カク年昇 C43089_来店日時の入力チェック対応
  -- ---------------------------------------------------------
  PROCEDURE PRC_ADD_ANNAI_YOYAKU
  (
    pv_kaisya_cd             IN VARCHAR2
     ,pv_uketuke_no            IN VARCHAR2
     ,pv_update_mode           IN VARCHAR2
     ,pv_yoyaku_date           IN VARCHAR2
     ,pv_bukken_no             IN VARCHAR2
     ,pv_heya_no               IN VARCHAR2
     ,pv_seq                   IN OUT VARCHAR2
     ,pv_bukken_list_bukken_no IN VARCHAR2
     ,pv_bukken_list_heya_no   IN VARCHAR2
     ,pv_bukken_list_seq       IN VARCHAR2
     ,pv_post_cd               IN VARCHAR2
     ,pv_chukaitantou          IN VARCHAR2
     ,pv_tantou_cd             IN VARCHAR2
     ,rst_code                 OUT VARCHAR2
     ,rst_msg                  OUT VARCHAR2
  ) IS
    sys_date     DATE;
    seq          UD_GENTI_ANNAI_YOYAKU.SEQ%TYPE;
    bukkennoList PKG_COMMON.tyStrSplit;
    heyanoList   PKG_COMMON.tyStrSplit;
    seqList      PKG_COMMON.tyStrSplit;
    seqCursor    typCSR;
    --2019/06/24 ADD START カク年昇(SYS) C43089_来店日時の入力チェック対応
    yoyaku_date  VARCHAR2(2);
    --2019/06/24 ADD END カク年昇(SYS) C43089_来店日時の入力チェック対応
  BEGIN
    sys_date := SYSDATE;
    rst_code := MSG_SUCCESS;
    --引数.更新モード = '1'(新規)の場合
    IF pv_update_mode = '1'
    THEN
      --最大SEQ + 1 取得
      OPEN seqCursor FOR
        SELECT NVL(MAX(A.SEQ), 0) + 1
          FROM UD_GENTI_ANNAI_YOYAKU A
         WHERE A.KAISYA_CD = pv_kaisya_cd
           AND A.UKETUKE_NO = pv_uketuke_no
           AND A.HOMEMATE_BUKKEN_NO = pv_bukken_no
           AND A.HEYA_NO = pv_heya_no
           AND A.LOGICAL_DEL_FLG = '0'
         GROUP BY A.KAISYA_CD
             ,A.UKETUKE_NO
             ,A.HOMEMATE_BUKKEN_NO
             ,A.HEYA_NO;
      LOOP
        FETCH seqCursor
          INTO seq;
        EXIT WHEN seqCursor%NOTFOUND;
      END LOOP;

      IF seq IS NULL
      THEN
        seq := 1;
      END IF;

      --2019/06/24 ADD START カク年昇(SYS) C43089_来店日時の入力チェック対応
      yoyaku_date := '1';
      IF TO_DATE(pv_yoyaku_date, 'YYYY/MM/DD HH24:MI') > SYSDATE
      THEN
        yoyaku_date := '0';
      END IF;
      --2019/06/24 ADD END カク年昇(SYS) C43089_来店日時の入力チェック対応

      --現地案内予約データ追加処理
      INSERT INTO UD_GENTI_ANNAI_YOYAKU
        (KAISYA_CD
        ,UKETUKE_NO
        ,HOMEMATE_BUKKEN_NO
        ,HEYA_NO
        ,SEQ
        ,GENTI_ANNAI_YOYAKU_DATE
        ,GENTI_ANNAI_FLG
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
        ,pv_bukken_no
        ,pv_heya_no
        ,seq
        ,TO_DATE(pv_yoyaku_date, 'YYYY/MM/DD HH24:MI')
        --2019/06/24 UPD START カク年昇(SYS) C43089_来店日時の入力チェック対応
        --,'0'
        ,yoyaku_date
        --2019/06/24 UPD END カク年昇(SYS) C43089_来店日時の入力チェック対応
        ,pv_post_cd
        ,pv_chukaitantou
        ,sys_date
        ,pv_tantou_cd
        ,sys_date
        ,pv_tantou_cd
        ,'0'
        ,NULL
        ,NULL);
      pv_seq := seq;
    ELSIF pv_update_mode = '2'
    THEN
      --引数.更新モード = '2'(更新)の場合
      --現地案内予約データ更新処理
      UPDATE UD_GENTI_ANNAI_YOYAKU A
         SET A.GENTI_ANNAI_YOYAKU_DATE = TO_DATE(pv_yoyaku_date, 'YYYY/MM/DD HH24:MI')
          ,A.UPD_DATE                = sys_date
          ,A.UPD_TANTOU_CD           = pv_tantou_cd
                  --2019/09/25 ADD START カク年昇(SYS) C43089_来店日時の入力チェック対応
                  ,A.GENTI_ANNAI_FLG =
                  CASE WHEN TO_DATE(pv_yoyaku_date, 'YYYY/MM/DD HH24:MI') < SYSDATE THEN '1' ELSE A.GENTI_ANNAI_FLG END
                  --2019/09/25 ADD END カク年昇(SYS) C43089_来店日時の入力チェック対応
       WHERE A.KAISYA_CD = pv_kaisya_cd
         AND A.UKETUKE_NO = pv_uketuke_no
         AND A.HOMEMATE_BUKKEN_NO = pv_bukken_no
         AND A.HEYA_NO = pv_heya_no
         AND A.SEQ = pv_seq;
    ELSIF pv_update_mode = '3'
    THEN
      --引数.更新モード = '3'(複数更新)の場合
      --現地案内予約データ更新処理
      bukkennoList := PKG_COMMON.FNC_SPLIT(pv_bukken_list_bukken_no, '|');
      heyanoList   := PKG_COMMON.FNC_SPLIT(pv_bukken_list_heya_no, '|');
      seqList      := PKG_COMMON.FNC_SPLIT(pv_bukken_list_seq, '|');
      FOR i IN 1 .. bukkennoList.COUNT
      LOOP
        UPDATE UD_GENTI_ANNAI_YOYAKU A
           SET A.GENTI_ANNAI_YOYAKU_DATE = TO_DATE(pv_yoyaku_date, 'YYYY/MM/DD HH24:MI')
            ,A.UPD_DATE                = sys_date
            ,A.UPD_TANTOU_CD           = pv_tantou_cd
                      --2019/09/25 ADD START カク年昇(SYS) C43089_来店日時の入力チェック対応
                      ,A.GENTI_ANNAI_FLG =
                      CASE WHEN TO_DATE(pv_yoyaku_date, 'YYYY/MM/DD HH24:MI') < SYSDATE THEN '1' ELSE A.GENTI_ANNAI_FLG END
            --2019/09/25 ADD END カク年昇(SYS) C43089_来店日時の入力チェック対応
         WHERE A.KAISYA_CD = pv_kaisya_cd
           AND A.UKETUKE_NO = pv_uketuke_no
           AND A.HOMEMATE_BUKKEN_NO = bukkennoList(i)
           AND A.HEYA_NO = heyanoList(i)
           AND A.SEQ = seqList(i);
      END LOOP;

    END IF;
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
  -- システム名         ： 受付紹介システム
  -- プロシージャID      ： PRC_DEL_YOYAKU_INFO
  -- プロシージャ名      ： 次回来店・現地案内予約
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2            -- 会社コード
  --                 ： pv_uketuke_no            IN  VARCHAR2           -- 受付番号
  --                 ： rst_code                 OUT  VARCHAR2　　        -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　        -- メッセージ
  -- 処理概要         ： 来店予約データ論理削除処理
  -- 初版作成日/作成者 ： 2012/06/07 Ver.1.0.0 SYS_趙
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRC_DEL_YOYAKU_INFO
  (
    pv_kaisya_cd  IN VARCHAR2
     ,pv_uketuke_no IN VARCHAR2
     ,rst_code      OUT VARCHAR2
     ,rst_msg       OUT VARCHAR2
  ) IS
  BEGIN
    rst_code := MSG_SUCCESS;
    DELETE FROM UD_RAITEN_YOYAKU
     WHERE KAISYA_CD = pv_kaisya_cd
       AND UKETUKE_NO = pv_uketuke_no
       AND LOGICAL_DEL_FLG = '0';
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
  -- システム名         ： 受付紹介システム
  -- プロシージャID      ： PRC_DEL_ANNAI_YOYAKU
  -- プロシージャ名      ： 次回来店・現地案内予約
  -- 引数             ： pv_kaisya_cd            IN  VARCHAR2            -- 会社コード
  --                 ： pv_uketuke_no            IN  VARCHAR2           -- 受付番号
  --                 ： pv_update_mode           IN  VARCHAR2　     　    -- 更新モード
  --                 ： pv_bukken_no             IN  VARCHAR2           -- 物件№
  --                 ： pv_heya_no               IN  VARCHAR2           -- 部屋№
  --                 ： pv_seq                   IN  VARCHAR2           -- 現地案内予約SEQ
  --                 ： pv_bukken_list_bukken_no IN  VARCHAR2           -- 物件/部屋リスト.物件№
  --                 ： pv_bukken_list_heya_no   IN  VARCHAR2           -- 物件/部屋リスト.部屋№
  --                 ： pv_bukken_list_seq       IN  VARCHAR2           -- 物件/部屋リスト.SEQ
  --                 ： rst_code                 OUT  VARCHAR2　　        -- メッセージコード
  --                 ： rst_msg                  OUT  VARCHAR2　　        -- メッセージ
  -- 処理概要          ： 現地案内予約データ削除処理
  -- 初版作成日/作成者 ： 2012/06/07 Ver.1.0.0 SYS_趙
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRC_DEL_ANNAI_YOYAKU
  (
    pv_kaisya_cd             IN VARCHAR2
     ,pv_uketuke_no            IN VARCHAR2
     ,pv_update_mode           IN VARCHAR2
     ,pv_bukken_no             IN VARCHAR2
     ,pv_heya_no               IN VARCHAR2
     ,pv_seq                   IN VARCHAR2
     ,pv_bukken_list_bukken_no IN VARCHAR2
     ,pv_bukken_list_heya_no   IN VARCHAR2
     ,pv_bukken_list_seq       IN VARCHAR2
     ,rst_code                 OUT VARCHAR2
     ,rst_msg                  OUT VARCHAR2
  ) IS
    bukkennoList PKG_COMMON.tyStrSplit;
    heyanoList   PKG_COMMON.tyStrSplit;
    seqList      PKG_COMMON.tyStrSplit;
  BEGIN
    rst_code := MSG_SUCCESS;
    IF pv_update_mode = '2'
    THEN
      --引数.更新モード = '2'(更新)の場合
      --現地案内予約データ更新処理
      DELETE FROM UD_GENTI_ANNAI_YOYAKU A
       WHERE A.KAISYA_CD = pv_kaisya_cd
         AND A.UKETUKE_NO = pv_uketuke_no
         AND A.HOMEMATE_BUKKEN_NO = pv_bukken_no
         AND A.HEYA_NO = pv_heya_no
         AND A.SEQ = pv_seq;
    ELSIF pv_update_mode = '3'
    THEN
      --引数.更新モード = '3'(複数更新)の場合
      --現地案内予約データ更新処理
      bukkennoList := PKG_COMMON.FNC_SPLIT(pv_bukken_list_bukken_no, '|');
      heyanoList   := PKG_COMMON.FNC_SPLIT(pv_bukken_list_heya_no, '|');
      seqList      := PKG_COMMON.FNC_SPLIT(pv_bukken_list_seq, '|');
      FOR i IN 1 .. bukkennoList.COUNT
      LOOP
        DELETE FROM UD_GENTI_ANNAI_YOYAKU A
         WHERE A.KAISYA_CD = pv_kaisya_cd
           AND A.UKETUKE_NO = pv_uketuke_no
           AND A.HOMEMATE_BUKKEN_NO = bukkennoList(i)
           AND A.HEYA_NO = heyanoList(i)
           AND A.SEQ = seqList(i);
      END LOOP;
    END IF;
    COMMIT;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UPDFAIL;
      rst_msg  := SQLERRM;
      dbms_output.put_line('エラーコード：' || rst_code);
      dbms_output.put_line('エラーメッセージ：' || rst_msg);
      ROLLBACK;
  END;
END PKG_UKG02041;
/