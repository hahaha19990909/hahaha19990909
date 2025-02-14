CREATE OR REPLACE PACKAGE BODY PKG_UKG01010 IS
  -- ---------------------------------------------------------
  -- システム名         ： 受付紹介システム
  -- パッケージID       ： PKG_UKG01010
  -- パッケージ名       ： トップページ
  -- 宣言内容           ： トップページデータを取得する
  -- 初版作成日/作成者 ： 2011/10/19 Ver.1.0.0 SYS_馮強華
  -- 更新履歴         ： 2013/01/25 Ver.1.0.1 SYS_林森   (案件番号C37058)受付システム要望対応
  --                  ： 2013/05/16 Ver.1.0.2 SYS_郭凡  (案件C37109-1)受付システムSTEP2要望対応
  -- 更新履歴         ： 2013/06/06 Ver.1.0.3 SQLチューニング対応
  -- 更新履歴         ： 2013/10/25 Ver.1.0.4 変数の定義をビューの元テーブルに変更
  -- 更新履歴         ： 2014/05/13 Ver.1.0.5 SYS_胡   (案件C38100)トップページで既存個人を検索（他）対応
  -- 更新履歴         ： 2014/06/23 Ver.1.0.6 SYS_趙雲剛(案件C38117)セールスポイント編集機能の追加
  -- 変更情報         ： 2015/01/27 Ver.1.0.7 SYS_郭凡(案件C39042)トップページレイアウト修正
  -- 更新履歴         ： 2015/03/04 var.1.0.8 h-nakano(案件C39075)法人仲介課依頼時は、法人＆法人企業クラブとする
  -- 更新履歴         ： 2017/08/04 Ver.1.0.9 MJEC)長倉 (案件C38101-1)ホームメイト自動受付対応 (案件C41059)中止他決顧客の検索対応
  -- 更新履歴         ： 2017/09/12 var.1.1.0 SYS_張虎強(案件C41004)法人仲介担当者登録機能追加対応
  -- 更新履歴         ： 2018/01/09 var.1.1.1 SYS_肖剣生(案件C42036_デザイン変更）
  -- 更新履歴         ： 2019/08/15 Ver.1.1.2 SYS_カク年昇(案件C43100)お客様項目の入力簡素化対応
  -- 更新履歴         ： 2019.10.30 Ver.1.1.3 h-nakano(案件C43089)レスポンス障害対応
  -- 更新情報         ： 2020/06/24 Ver.1.1.4 SYS_劉恆毅 C43040-2_法人の入力項目追加対応
  -- 更新情報         ： 2021/04/10 Ver.1.1.5 許亜静 C45032_オンライン仲介システムの導入
  
  -- ---------------------------------------------------------

  -- *********************************
  --    定数宣言
  -- *********************************
  MSG_UNKNOWN  CONSTANT CHAR(7) := 'ERRS999';
  MSG_LOCKFAIL CONSTANT CHAR(7) := 'ERR0001';
  MSG_SAIBAN   CONSTANT CHAR(7) := 'ERR0002';
  MSG_SUCCESS  CONSTANT CHAR(7) := '0000000';
  MSG_UPDERR   CONSTANT CHAR(7) := 'ERRS010';
  --2013/05/16 郭凡 ADD Start (案件C37109-1)受付システムSTEP2要望対応
  MSG_EXCLUSIVE CONSTANT CHAR(7) := 'ERRS003';
  --2013/05/16 郭凡 ADD End
  -- ---------------------------------------------------------
  -- システム名         　： 受付紹介システム
  -- プロシージャID      　： PRO_INIT
  -- プロシージャ名      　： トップページ初期化データ取得
  -- 引数             　： PARAM_KAISYACD      IN   VARCHAR2      -- 会社コード
  --                　  ： PARAM_POSTCD        IN   VARCHAR2     -- 店舗コード
  --                　  ： PARAM_SEARCHPOSTCD  IN   VARCHAR2     -- 検索店舗コード
  --                    ： PARAM_AUTHORITY_CD   IN   VARCHAR2    -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
  --                   ： PARAM_SISYA_BLK_CD   IN   VARCHAR2    -- 支社ブロックコード
  --                   ：PARAM_TANTOUCD       IN   VARCHAR2     --担当コード
  --                   ：PARAM_SEARCHITEM     IN   VARCHAR2     -- 検索項目
  --                   ： param_kanaindex1   IN   VARCHAR2   　　　-- カナインデックス1
  --                  ： param_kanaindex2   IN   VARCHAR2   　   -- カナインデックス2
  --                　  ： PARAM_STARTINDEX    IN   VARCHAR2     -- STARTデータ行番号
  --                　  ： PARAM_ENDINDEX      IN   VARCHAR2     -- ENDデータ行行番号
  --                    ： PARAM_TAKETUCYUSHIFLG IN  VARCHAR2    -- 他決・中止チェックボックスフラグ
  --                　  ： PARAM_SORTITEM      IN   VARCHAR2     -- ソート項目
  --                    ： OUT_RECORDCOUNT     OUT  VARCHAR2     -- 商談中の顧客一覧総件数
  --                    ： OUT_UKETUKEHOHO     OUT  TYPCSR       -- 受付方法項目
  --                    ： OUT_TANTOULIST      OUT  TYPCSR       -- 担当者リスト
  --                    ： OUT_KOKYAKUINFO     OUT  TYPCSR       -- 商談中の顧客一覧
  --                    ： rst_msg         OUT  VARCHAR2     -- エラーメッセージ
  --                    ： rst_code       OUT  VARCHAR2     -- メッセージID
  -- 戻り値             ： 0000000  ： 成功
  --                    ： ERR9999  ： アプリケーションエラー
  -- 処理概要           ： トップページ初期化データ取得
  -- 初版作成日/作成者  ： 2011/10/19 Ver.1.0.0 SYS_馮強華
  -- 変更情報           ：2013/01/25 Ver.1.0.1 SYS_林森 (案件C37058)受付システム要望対応
  --                    ：2014/05/13 Ver.1.0.5 SYS_胡   (案件C38100)トップページで既存個人を検索（他）対応
  --                    ：2014/06/23 Ver.1.0.3 SYS_趙雲剛(案件C38117)セールスポイント編集機能の追加
  --                    ：2017/08/04 Ver.1.0.4 MJEC)長倉 (案件C41059)中止他決顧客の検索対応
  --                    ：2017/09/12 Ver.1.0.5 SYS_張虎強(案件C41004)法人仲介担当者登録機能追加対応
  -- ---------------------------------------------------------
  PROCEDURE PRO_INIT
  (
    PARAM_KAISYACD   IN VARCHAR2 -- 会社コード
     ,PARAM_POSTCD     IN VARCHAR2 -- 店舗コード
     --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
     ,PARAM_SEARCHPOSTCD IN VARCHAR2 -- 検索店舗コード
     ,PARAM_AUTHORITY_CD IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
     ,PARAM_SISYA_BLK_CD IN VARCHAR2 -- 支社ブロックコード
     --2014/06/23 趙雲剛 ADD End
     --2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
     ,PARAM_TANTOUCD   IN VARCHAR2--担当コード
     ,PARAM_SEARCHITEM IN VARCHAR2 -- 検索項目
     ,PARAM_KANAINDEX1 IN VARCHAR2 -- カナインデックス1
     ,PARAM_KANAINDEX2 IN VARCHAR2 -- カナインデックス2
     --2013/01/25 林森 ADD END
     ,PARAM_STARTINDEX IN NUMBER -- STARTデータ行番号
     ,PARAM_ENDINDEX   IN NUMBER -- ENDデータ行行番号
     --2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
     ,PARAM_TAKETUCYUSHIFLG IN VARCHAR2
     --2017/08/04 MJEC)長倉 ADD End
     ,PARAM_SORTITEM   IN VARCHAR2 -- ソート項目
     ,OUT_RECORDCOUNT  OUT NUMBER -- 商談中の顧客一覧総件数
     ,OUT_UKETUKEHOHO  OUT TYPCSR -- 受付方法項目
     ,OUT_TANTOULIST   OUT TYPCSR -- 担当者リスト
     --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
     ,OUT_HOUJINTANTOULIST   OUT TYPCSR -- 法人担当者リスト
     --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
     ,OUT_KOKYAKUINFO  OUT TYPCSR -- 商談中の顧客一覧
     --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
     ,OUT_SAKUSEIINFO  OUT TYPCSR  -- 受付情報作成区分の顧客一覧
     --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
     --2014/05/13 胡 ADD Start (案件C38100)トップページで既存個人を検索（他）対応
     ,OUT_GENGOULIST    OUT typCSR
     --2014/05/13 胡 ADD END
     ,rst_msg          OUT VARCHAR2 -- エラーメッセージ
     ,rst_code         OUT VARCHAR2 -- メッセージID
  ) IS
  --2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
  WHERETANTOU       VARCHAR2(50); -- SQL文
  --2013/01/25 林森 ADD END
  BEGIN
    WHERETANTOU := NULL;
    --2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
    IF PARAM_TANTOUCD IS NOT NULL
    THEN
      WHERETANTOU := 'TANTOU_CD$' || PARAM_TANTOUCD;
    END IF;
    --2013/01/25 林森 ADD END

    --受付方法項目
    PKG_COMMON.PRC_GET_COMMON_LIST('003', OUT_UKETUKEHOHO, rst_code, rst_msg);

    --担当者リスト取得
    PKG_COMMON.PRC_GET_SAME_POST_TANTOU(PARAM_KAISYACD, PARAM_POSTCD, OUT_TANTOULIST, rst_msg);
    
    --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
    --法人担当者リスト取得
    PKG_COMMON.PRC_GET_HOUJIN_TANTOU(PARAM_KAISYACD, OUT_HOUJINTANTOULIST, rst_msg);
    --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
    
    --商談中の顧客一覧データ取得
    PRO_KOKYAKUINFO(PARAM_KAISYACD
             --2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
             --,PARAM_POSTCD 
             ,PARAM_SEARCHPOSTCD
             ,PARAM_AUTHORITY_CD
             ,PARAM_SISYA_BLK_CD
             --2014/06/23 趙雲剛 UPD End
             --2013/01/25 林森 UPD Start (案件C37058)受付システム要望対応
             --,''
             ,PARAM_SEARCHITEM
             --2013/01/25 林森 UPD END
             --2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
             ,WHERETANTOU
             --2013/01/25 林森 ADD END
             --2013/01/25 林森 UPD Start (案件C37058)受付システム要望対応
             --,''
             --,''
             ,PARAM_KANAINDEX1
             ,PARAM_KANAINDEX2
             --2013/01/25 林森 UPD END
             ,PARAM_STARTINDEX
             ,PARAM_ENDINDEX
             --2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
             ,PARAM_TAKETUCYUSHIFLG
             --2017/08/04 MJEC)長倉 ADD End
             ,PARAM_SORTITEM
             ,OUT_RECORDCOUNT
             ,OUT_KOKYAKUINFO
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             ,OUT_SAKUSEIINFO
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             ,rst_msg
             ,rst_code);

    --2014/05/13 胡 ADD Start (案件C38100)トップページで既存個人を検索（他）対応
    PKG_UM_COMMON.PRC_GET_UM_COMMON_ALL('001', '', OUT_GENGOULIST, rst_code);
    --2014/05/13 胡 ADD End
     
    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_INIT;

  -- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_GETYOYAKULIST
  -- プロシージャ名      ： 予約データリスト取得
  -- 引数             ： param_kaisyacd          　IN   VARCHAR2      -- 会社コード
  --                 ： param_postcd     　    　IN   VARCHAR2      -- 店舗コード
  --                 ： param_authority_cd   IN   VARCHAR2    -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
  --                 ： param_sisya_blk_cd   IN   VARCHAR2    -- 支社ブロックコード
  --                 ： param_tdate             IN   VARCHAR2       -- 日付
  --                 ： out_raitenyoyakulist   OUT  typCSR    　　　　-- 来店予約データリスト
  --                 ： out_bukenyoyakulist    OUT  typCSR    　　　　-- 現地案内データリスト
  --                 ： out_onlineyoyakulist   OUT  typCSR        -- オンライン仲介データリスト
  --                 ： rst_msg    OUT  VARCHAR2             -- エラーメッセージ
  --                 ： rst_code   OUT  VARCHAR2  　          -- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要          ： 予約データリスト取得
  -- 初版作成日/作成者  ： 2011/10/19 Ver.1.0.0 SYS_馮強華
  -- 変更情報           ：2014/06/23 Ver.1.0.6 SYS_趙雲剛(案件C38117)セールスポイント編集機能の追加
  -- 変更情報           ：2015/01/27 Ver.1.0.7 SYS_郭凡(案件C39042)トップページレイアウト修正
  -- 変更情報           ：2021/04/10 Ver.1.1.5 許亜静 C45032_オンライン仲介システムの導入
  -- ---------------------------------------------------------
  PROCEDURE PRO_GETYOYAKULIST
  (
    PARAM_KAISYACD       IN VARCHAR2 -- 会社コード
     ,PARAM_POSTCD         IN VARCHAR2 -- 店舗コード
     --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
     ,PARAM_AUTHORITY_CD IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
     ,PARAM_SISYA_BLK_CD IN VARCHAR2 -- 支社ブロックコード
     --2014/06/23 趙雲剛 ADD End
     ,PARAM_TDATE          IN VARCHAR2 -- 日付
     ,OUT_RAITENYOYAKULIST OUT TYPCSR -- 来店予約データリスト
     ,OUT_BUKENYOYAKULIST  OUT TYPCSR -- 現地案内データリスト
     --2021/04/10 許亜静 ADD START C45032_オンライン仲介システムの導入
     ,OUT_ONLINEYOYAKULIST OUT TYPCSR -- オンライン仲介データリスト
     --2021/04/10 許亜静 ADD END C45032_オンライン仲介システムの導入
     ,rst_msg              OUT VARCHAR2 -- エラーメッセージ
     ,rst_code             OUT VARCHAR2 -- メッセージID
  ) IS

  BEGIN
  --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
    IF PARAM_POSTCD IS NOT NULL THEN
  --2014/06/23 趙雲剛 ADD End
          --来店予約データを取得
          OPEN OUT_RAITENYOYAKULIST FOR
            SELECT TO_CHAR(UD.YOYAKU_DATE, 'HH24:MI') YOYAKUDATE
                ,TO_CHAR(UD.YOYAKU_DATE, 'yyyyMMddHH24MISS') COMPAREDATE
                ,UMV.KOKYAKU_NAME KOYAKUNM
                ,UD.YOYAKU_DATE
                ,UD.UKETUKE_NO
                ,UD.CHUKAI_TANTOU_CD
                --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
                ,UMV.UKETUKE_POST_CD
                --2014/06/23 趙雲剛 ADD End
                --2015/01/27 郭凡 ADD Start (案件C39042)トップページレイアウト修正
                ,WMT.TANTOU_NAME TANTOU_NAME
                --2015/01/27 郭凡 ADD End
              FROM UD_RAITEN_YOYAKU UD
             INNER JOIN UMV_RAITEN_UKETUKE UMV
              ON (UD.KAISYA_CD = UMV.KAISYA_CD AND UD.UKETUKE_NO = UMV.UKETUKE_NO)
             --2015/01/27 郭凡 UPD Start (案件C39042)トップページレイアウト修正
             --INNER JOIN TOKEN.WM_TANTOU WMT
             --ON (UMV.KAISYA_CD = WMT.KAISYA_CD AND UMV.CHUKAI_TANTOU_CD = WMT.TANTOU_CD)
             LEFT JOIN TOKEN.WM_TANTOU WMT
              ON (UD.KAISYA_CD = WMT.KAISYA_CD AND UD.CHUKAI_TANTOU_CD = WMT.TANTOU_CD)
             --2015/01/27 郭凡 UPD End
             WHERE UD.KAISYA_CD = PARAM_KAISYACD
               AND UD.UKETUKE_POST_CD = PARAM_POSTCD
               AND TO_CHAR(UD.YOYAKU_DATE, 'YYYYMMDD') = PARAM_TDATE
               AND UD.LOGICAL_DEL_FLG = '0'
               AND UMV.SINCHOKU_CD IN ('1', '2', '3')
             ORDER BY UD.YOYAKU_DATE ASC;

          --現地案内データを取得
          OPEN OUT_BUKENYOYAKULIST FOR
            SELECT TO_CHAR(UD.GENTI_ANNAI_YOYAKU_DATE, 'HH24:MI') YOYAKUDATE
                ,TO_CHAR(UD.GENTI_ANNAI_YOYAKU_DATE, 'yyyyMMddHH24MISS') COMPAREDATE
                ,UMV.KOKYAKU_NAME KOYAKUNM
                ,UD.GENTI_ANNAI_YOYAKU_DATE YOYAKU_DATE
                ,UD.UKETUKE_NO
                ,UD.CHUKAI_TANTOU_CD
                --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
                ,UMV.UKETUKE_POST_CD
                --2014/06/23 趙雲剛 ADD End
                --2015/01/27 郭凡 ADD Start (案件C39042)トップページレイアウト修正
                ,WMT.TANTOU_NAME TANTOU_NAME
                --2015/01/27 郭凡 ADD End
              FROM UD_GENTI_ANNAI_YOYAKU UD
             INNER JOIN UMV_RAITEN_UKETUKE UMV
              ON (UD.KAISYA_CD = UMV.KAISYA_CD AND UD.UKETUKE_NO = UMV.UKETUKE_NO)
             --2015/01/27 郭凡 ADD Start (案件C39042)トップページレイアウト修正
             LEFT JOIN TOKEN.WM_TANTOU WMT
              ON (UD.KAISYA_CD = WMT.KAISYA_CD AND UD.CHUKAI_TANTOU_CD = WMT.TANTOU_CD)
             --2015/01/27 郭凡 ADD End
             WHERE UD.KAISYA_CD = PARAM_KAISYACD
               AND UD.UKETUKE_POST_CD = PARAM_POSTCD
               AND TO_CHAR(UD.GENTI_ANNAI_YOYAKU_DATE, 'YYYYMMDD') = PARAM_TDATE
               AND UD.LOGICAL_DEL_FLG = '0'
               AND UMV.SINCHOKU_CD IN ('1', '2', '3')
             --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
             --GROUP BY　UD.KAISYA_CD, UD.GENTI_ANNAI_YOYAKU_DATE, UMV.KOKYAKU_NAME, UD.CHUKAI_TANTOU_CD, UD.UKETUKE_NO 
              --2015/01/27 郭凡 UPD Start (案件C39042)トップページレイアウト修正
             --GROUP BY　UD.KAISYA_CD, UD.GENTI_ANNAI_YOYAKU_DATE, UMV.KOKYAKU_NAME, UD.CHUKAI_TANTOU_CD, UD.UKETUKE_NO ,UMV.UKETUKE_POST_CD
             GROUP BY　UD.KAISYA_CD, UD.GENTI_ANNAI_YOYAKU_DATE, UMV.KOKYAKU_NAME, UD.CHUKAI_TANTOU_CD, UD.UKETUKE_NO ,UMV.UKETUKE_POST_CD,WMT.TANTOU_NAME
             --2015/01/27 郭凡 UPD End
             --2014/06/23 趙雲剛 ADD End
             ORDER BY UD.GENTI_ANNAI_YOYAKU_DATE ASC;
     --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
     --2021/04/10 許亜静 ADD START C45032_オンライン仲介システムの導入
          --オンライン仲介予約データを取得
          OPEN OUT_ONLINEYOYAKULIST FOR
            SELECT TO_CHAR(UOTY.ONLINE_TYUKAI_YOYAKU_DATE, 'HH24:MI') YOYAKUDATE
                ,TO_CHAR(UOTY.ONLINE_TYUKAI_YOYAKU_DATE, 'yyyyMMddHH24MISS') COMPAREDATE
                ,UMV.KOKYAKU_NAME KOYAKUNM
                ,UOTY.ONLINE_TYUKAI_YOYAKU_DATE
                ,UOTY.UKETUKE_NO
                ,UOTY.CHUKAI_TANTOU_CD                
                ,UMV.UKETUKE_POST_CD
                ,WMT.TANTOU_NAME TANTOU_NAME
              FROM UD_ONLINE_TYUKAII_YOYAKU UOTY
             INNER JOIN UMV_RAITEN_UKETUKE UMV
              ON (UOTY.KAISYA_CD = UMV.KAISYA_CD AND UOTY.UKETUKE_NO = UMV.UKETUKE_NO)
             LEFT JOIN TOKEN.WM_TANTOU WMT
              ON (UOTY.KAISYA_CD = WMT.KAISYA_CD AND UOTY.CHUKAI_TANTOU_CD = WMT.TANTOU_CD)
             WHERE UOTY.KAISYA_CD = PARAM_KAISYACD
               AND UOTY.UKETUKE_POST_CD = PARAM_POSTCD
               AND TO_CHAR(UOTY.ONLINE_TYUKAI_YOYAKU_DATE, 'YYYYMMDD') = PARAM_TDATE
               AND UOTY.LOGICAL_DEL_FLG = '0'
               AND UMV.SINCHOKU_CD IN ('1', '2', '3')
             ORDER BY UOTY.ONLINE_TYUKAI_YOYAKU_DATE ASC;
     --2021/04/10 許亜静 ADD END C45032_オンライン仲介システムの導入
       ELSE
          --来店予約データを取得
          OPEN OUT_RAITENYOYAKULIST FOR
            SELECT TO_CHAR(UD.YOYAKU_DATE, 'HH24:MI') YOYAKUDATE
                ,TO_CHAR(UD.YOYAKU_DATE, 'yyyyMMddHH24MISS') COMPAREDATE
                ,UMV.KOKYAKU_NAME KOYAKUNM
                ,UD.YOYAKU_DATE
                ,UD.UKETUKE_NO
                ,UD.CHUKAI_TANTOU_CD
                ,UMV.UKETUKE_POST_CD
                --2015/01/27 郭凡 ADD Start (案件C39042)トップページレイアウト修正
                ,WMT.TANTOU_NAME TANTOU_NAME
                --2015/01/27 郭凡 ADD End
              FROM UD_RAITEN_YOYAKU UD
             INNER JOIN UMV_RAITEN_UKETUKE UMV
              ON (UD.KAISYA_CD = UMV.KAISYA_CD AND UD.UKETUKE_NO = UMV.UKETUKE_NO)
             INNER JOIN TOKEN.WM_POST WP
              ON (UMV.KAISYA_CD = WP.KAISYA_CD AND UMV.UKETUKE_POST_CD = WP.POST_CD)
             --2015/01/27 郭凡 ADD Start (案件C39042)トップページレイアウト修正
             LEFT JOIN TOKEN.WM_TANTOU WMT
              ON (UD.KAISYA_CD = WMT.KAISYA_CD AND UD.CHUKAI_TANTOU_CD = WMT.TANTOU_CD)
             --2015/01/27 郭凡 ADD End
             WHERE UD.KAISYA_CD = PARAM_KAISYACD
               AND TO_CHAR(UD.YOYAKU_DATE, 'YYYYMMDD') = PARAM_TDATE
               AND UD.LOGICAL_DEL_FLG = '0'
               AND ((PARAM_AUTHORITY_CD = '1' AND WP.SISYA_BLK_CD = PARAM_SISYA_BLK_CD) OR PARAM_AUTHORITY_CD = '2') 
               AND WP.OUTPUT_ORDER <> '999'
               AND WP.OUTPUT_ORDER IS NOT NULL
               AND WP.LOGICAL_DEL_FLG = '0'
               AND UMV.SINCHOKU_CD IN ('1', '2', '3')
             ORDER BY UD.YOYAKU_DATE ASC;

          --現地案内データを取得
          OPEN OUT_BUKENYOYAKULIST FOR
            SELECT TO_CHAR(UD.GENTI_ANNAI_YOYAKU_DATE, 'HH24:MI') YOYAKUDATE
                ,TO_CHAR(UD.GENTI_ANNAI_YOYAKU_DATE, 'yyyyMMddHH24MISS') COMPAREDATE
                ,UMV.KOKYAKU_NAME KOYAKUNM
                ,UD.GENTI_ANNAI_YOYAKU_DATE YOYAKU_DATE
                ,UD.UKETUKE_NO
                ,UD.CHUKAI_TANTOU_CD
                ,UMV.UKETUKE_POST_CD
                --2015/01/27 郭凡 ADD Start (案件C39042)トップページレイアウト修正
                ,WMT.TANTOU_NAME TANTOU_NAME
                --2015/01/27 郭凡 ADD End
              FROM UD_GENTI_ANNAI_YOYAKU UD
             INNER JOIN UMV_RAITEN_UKETUKE UMV
              ON (UD.KAISYA_CD = UMV.KAISYA_CD AND UD.UKETUKE_NO = UMV.UKETUKE_NO)
             INNER JOIN TOKEN.WM_POST WP
              ON (UMV.KAISYA_CD = WP.KAISYA_CD AND UMV.UKETUKE_POST_CD = WP.POST_CD)
             --2015/01/27 郭凡 ADD Start (案件C39042)トップページレイアウト修正
             LEFT JOIN TOKEN.WM_TANTOU WMT
              ON (UD.KAISYA_CD = WMT.KAISYA_CD AND UD.CHUKAI_TANTOU_CD = WMT.TANTOU_CD)
             --2015/01/27 郭凡 ADD End
             WHERE UD.KAISYA_CD = PARAM_KAISYACD
               AND TO_CHAR(UD.GENTI_ANNAI_YOYAKU_DATE, 'YYYYMMDD') = PARAM_TDATE
               AND UD.LOGICAL_DEL_FLG = '0'
               AND ((PARAM_AUTHORITY_CD = '1' AND WP.SISYA_BLK_CD = PARAM_SISYA_BLK_CD) OR PARAM_AUTHORITY_CD = '2') 
               AND WP.OUTPUT_ORDER <> '999'
               AND WP.OUTPUT_ORDER IS NOT NULL
               AND WP.LOGICAL_DEL_FLG = '0'
               AND UMV.SINCHOKU_CD IN ('1', '2', '3')
             --2015/01/27 郭凡 UPD Start (案件C39042)トップページレイアウト修正
             --GROUP BY　UD.KAISYA_CD, UD.GENTI_ANNAI_YOYAKU_DATE, UMV.KOKYAKU_NAME, UD.CHUKAI_TANTOU_CD, UD.UKETUKE_NO ,UMV.UKETUKE_POST_CD
             GROUP BY　UD.KAISYA_CD, UD.GENTI_ANNAI_YOYAKU_DATE, UMV.KOKYAKU_NAME, UD.CHUKAI_TANTOU_CD, UD.UKETUKE_NO ,UMV.UKETUKE_POST_CD,WMT.TANTOU_NAME
             --2015/01/27 郭凡 UPD End
             ORDER BY UD.GENTI_ANNAI_YOYAKU_DATE ASC;
          --2021/04/10 許亜静 ADD START C45032_オンライン仲介システムの導入    
          --来店予約データを取得
          OPEN OUT_ONLINEYOYAKULIST FOR
            SELECT TO_CHAR(UOTY.ONLINE_TYUKAI_YOYAKU_DATE, 'HH24:MI') YOYAKUDATE
                ,TO_CHAR(UOTY.ONLINE_TYUKAI_YOYAKU_DATE, 'yyyyMMddHH24MISS') COMPAREDATE
                ,UMV.KOKYAKU_NAME KOYAKUNM
                ,UOTY.ONLINE_TYUKAI_YOYAKU_DATE
                ,UOTY.UKETUKE_NO
                ,UOTY.CHUKAI_TANTOU_CD
                ,UMV.UKETUKE_POST_CD
                ,WMT.TANTOU_NAME TANTOU_NAME
              FROM UD_ONLINE_TYUKAII_YOYAKU UOTY
             INNER JOIN UMV_RAITEN_UKETUKE UMV
              ON (UOTY.KAISYA_CD = UMV.KAISYA_CD AND UOTY.UKETUKE_NO = UMV.UKETUKE_NO)
             INNER JOIN TOKEN.WM_POST WP
              ON (UMV.KAISYA_CD = WP.KAISYA_CD AND UMV.UKETUKE_POST_CD = WP.POST_CD)
             LEFT JOIN TOKEN.WM_TANTOU WMT
              ON (UOTY.KAISYA_CD = WMT.KAISYA_CD AND UOTY.CHUKAI_TANTOU_CD = WMT.TANTOU_CD)
             WHERE UOTY.KAISYA_CD = PARAM_KAISYACD
               AND TO_CHAR(UOTY.ONLINE_TYUKAI_YOYAKU_DATE, 'YYYYMMDD') = PARAM_TDATE
               AND UOTY.LOGICAL_DEL_FLG = '0'
               AND ((PARAM_AUTHORITY_CD = '1' AND WP.SISYA_BLK_CD = PARAM_SISYA_BLK_CD) OR PARAM_AUTHORITY_CD = '2') 
               AND WP.OUTPUT_ORDER <> '999'
               AND WP.OUTPUT_ORDER IS NOT NULL
               AND WP.LOGICAL_DEL_FLG = '0'
               AND UMV.SINCHOKU_CD IN ('1', '2', '3')
             ORDER BY UOTY.ONLINE_TYUKAI_YOYAKU_DATE ASC;
            --2021/04/10 許亜静 ADD END C45032_オンライン仲介システムの導入  
       END IF;
  --2014/06/23 趙雲剛 ADD End
    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GETYOYAKULIST;

  -- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_GETUKETUKEINFO
  -- プロシージャ名      ： 受付状況データ取得
  -- 引数             ： param_kaisyacd    　  IN   VARCHAR2         -- 会社コード
  --                 ： param_postcd     　　IN   VARCHAR2        　 -- 店舗コード
  --                 ： param_tantoucd     IN   VARCHAR2        　   -- 担当者コード
  --                 ： param_tdate         IN   VARCHAR2         　 -- 日付
  --                 ： out_uketukeInfo             OUT  VARCHAR2  　-- 受付客数情報
  --                 ： out_oshirase             OUT  VARCHAR2  　　 -- お知らせ情報
  --                 ： rst_msg             OUT  VARCHAR2  　　  -- エラーメッセージ
  --                 ： rst_code           OUT  VARCHAR2  　    -- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要          ： 受付状況データ取得
  -- 初版作成日/作成者  ： 2011/10/19 Ver.1.0.0 SYS_馮強華
  -- 初版作成日/作成者  ： 2013/06/06 Ver.1.0.2 SQLチューニング対応
  -- 更新履歴         ： 2013/10/25 Ver.1.0.4 変数の定義をビューの元テーブルに変更
  --                  ：2014/06/23 Ver.1.0.6 SYS_趙雲剛(案件C38117)セールスポイント編集機能の追加
  --                  : 2019.10.30 h-nakano(案件C43089)レスポンス障害対応
  -- ---------------------------------------------------------
  PROCEDURE PRO_GETUKETUKEINFO
  (
    PARAM_KAISYACD  IN VARCHAR2 -- 会社コード
     ,PARAM_POSTCD    IN VARCHAR2 -- 店舗コード
     ,PARAM_TANTOUCD  IN VARCHAR2 -- 担当者コード
     ,PARAM_TDATE     IN VARCHAR2 -- 日付
     ,OUT_UKETUKEINFO OUT TYPCSR -- 受付客数情報
   --2014/06/23 趙雲剛 DEL Start (案件C38117)セールスポイント編集機能の追加
     --,OUT_OSHIRASE    OUT TYPCSR -- お知らせ情報
   --2014/06/23 趙雲剛 DEL End
     ,rst_msg         OUT VARCHAR2 -- エラーメッセージ
     ,rst_code        OUT VARCHAR2 -- メッセージID
  ) IS
--2014/06/23 趙雲剛 DEL Start (案件C38117)セールスポイント編集機能の追加
/*  
  CSRUKETUKENO        TYPCSR;
-- UPD_START 2013/10/25 変数の定義をビューの元テーブルに変更
--  LC_UKETUKE_NO       UMV_RAITEN_UKETUKE.UKETUKE_NO%TYPE;
  LC_UKETUKE_NO       UD_RAITEN_UKETUKE.UKETUKE_NO%TYPE;
-- UPD_END 2013/10/25 変数の定義をビューの元テーブルに変更

  LC_CNT              NUMBER(10) := 0;
  LC_SQL1_CNT         NUMBER(10) := 0;
  LC_SQL2_CNT         NUMBER(10) := 0;
  LC_SQL3_CNT         NUMBER(10) := 0;
  --2013/05/30 C37109 既存不具合解消のため　 UPD START
  --PV_UKETUKENO        VARCHAR2(1000) := '';
  --L_SQL               VARCHAR2(2000); -- SQL文
    PV_UKETUKENO        VARCHAR2(10000) := '';
    L_SQL               VARCHAR2(12000); -- SQL文
  --2013/05/30  C37109 既存不具合解消のため　 UPD END
*/
--2014/06/23 趙雲剛 DEL End
  BEGIN

    OPEN OUT_UKETUKEINFO FOR
      SELECT *
        FROM 
-- MOD START 2019.10.30 h-nakano(案件C43089)レスポンス障害対応
           --受付客数(担当者当月,担当者、店舗受付客数、店舗の当月の受付客数)
           (SELECT 
              NVL(SUM(CASE WHEN UMV.CHUKAI_TANTOU_CD = PARAM_TANTOUCD AND TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMM') = PARAM_TDATE THEN 1 ELSE 0 END),0) AS UKETUKEDAYMONTH,
              NVL(SUM(CASE WHEN UMV.CHUKAI_TANTOU_CD = PARAM_TANTOUCD THEN 1 ELSE 0 END),0) AS UKETUKEDAYTANTOU,
              NVL(SUM(CASE WHEN TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMM') = PARAM_TDATE THEN 1 ELSE 0 END),0) AS UKETUKEMONTHKENSU,
              NVL(SUM(CASE WHEN UMV.UKETUKE_NO IS NOT NULL THEN 1 ELSE 0 END),0) AS UKETUKEDAYKENSU
            FROM UD_RAITEN_UKETUKE UMV
           WHERE UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             AND UMV.LOGICAL_DEL_FLG = '0'
             AND UMV.SINCHOKU_CD IN ('1', '2', '3')),
           -- 新着物件数(担当)、新着物件数（店舗）
           (SELECT 
              NVL(SUM(CASE WHEN UMV.CHUKAI_TANTOU_CD = PARAM_TANTOUCD  THEN SUB.NEWCOUNT ELSE 0 END),0) AS NEWTANTOUCNT,
              NVL(SUM(CASE WHEN SUB.NEWCOUNT IS NOT NULL THEN SUB.NEWCOUNT ELSE 0 END),0) AS NEWPOSTCNT
            FROM UD_RAITEN_UKETUKE UMV
              ,(SELECT US.KAISYA_CD AS KAISYACD
                  ,US.UKETUKE_NO AS UKETUKENO
                  ,COUNT(US.UKETUKE_NO) AS NEWCOUNT
                FROM UD_SINTYAKU_BUKKEN US
                  ,UD_KUUSITU_HEYA      TD
               WHERE US.KAISYA_CD = PARAM_KAISYACD
               AND US.HOMEMATE_BUKKEN_NO = TD.HOMEMATE_BUKKEN_NO
                 AND US.HEYA_NO = TD.HEYA_NO
                 AND TD.NYU_KUU_KB = '0'
                 AND US.SINTYAKU_AL_KAIYJO_FLG = '0'
               GROUP BY US.KAISYA_CD
                   ,US.UKETUKE_NO) SUB
           WHERE UMV.KAISYA_CD = SUB.KAISYACD
             AND UMV.UKETUKE_NO = SUB.UKETUKENO
             AND UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             AND UMV.LOGICAL_DEL_FLG = '0'
             AND UMV.SINCHOKU_CD = '1'),
          --追客件数(担当者別,店舗別)
          (SELECT 
              NVL(SUM(CASE WHEN UR.CHUKAI_TANTOU_CD = PARAM_TANTOUCD THEN 1 ELSE 0 END),0) AS TUIKYAKUTANTOUKENSU,
              NVL(SUM(CASE WHEN UT.UKETUKE_NO IS NOT NULL THEN 1 ELSE 0 END),0) AS TUIKYAKUTENPOKENSU
            FROM UD_AL_TUIKYAKU     UT
              ,UD_RAITEN_UKETUKE UR
           WHERE UT.KAISYA_CD = UR.KAISYA_CD
             AND UT.UKETUKE_NO = UR.UKETUKE_NO
             AND UR.SINCHOKU_CD IN ('1', '2', '3')
             AND (UR.SAKUSEI_KB <> '3' OR UR.SAKUSEI_KB IS NULL)
             AND ((TO_CHAR(UR.LAST_TUIKYAKU_DATE, 'YYYYMMDD') <> TO_CHAR(SYSDATE, 'YYYYMMDD') AND
               UR.TUIKYAKU_ALARM_DATE IS NULL) OR UR.TUIKYAKU_ALARM_DATE IS NOT NULL)
             AND UR.KAISYA_CD = PARAM_KAISYACD
             AND UR.UKETUKE_POST_CD = PARAM_POSTCD 
             AND UR.LOGICAL_DEL_FLG = '0'
             AND UR.LOGICAL_DEL_FLG = '0'),
           -- 仮押さえ件数(店舗)と仮押さえ件数(担当者) をまとめて取得
          (SELECT 
             NVL(SUM(CASE WHEN SB.UKETUKE_NO IS NOT NULL THEN 1 ELSE 0 END),0) AS KARIOSAETENPOKENSU,
             NVL(SUM(CASE WHEN RU.CHUKAI_TANTOU_CD = PARAM_TANTOUCD THEN 1 ELSE 0 END),0) AS KARIOSAETANTOUKENSU
           FROM 
                  UD_RAITEN_UKETUKE RU
             JOIN UD_SHOUKAI_BUKKEN  SB
               ON (RU.KAISYA_CD = SB.KAISYA_CD AND RU.UKETUKE_NO = SB.UKETUKE_NO) 
             LEFT JOIN (
               SELECT WYU.* FROM (
                 SELECT WY.*, ROW_NUMBER() OVER(PARTITION BY WY.KAISYA_CD, WY.UKETUKE_CD, 
                          WY.HOMEMATE_BUKKEN_NO, WY.HEYA_NO, WY.BUKKEN_PARKING_KB ORDER BY UKETUKE_SEQ DESC) AS ROW_NUM 
                 FROM TOKEN.WD_YOYAKU WY) WYU
               WHERE WYU.ROW_NUM = 1
             ) YO 
             ON SB.HOMEMATE_BUKKEN_NO = YO.HOMEMATE_BUKKEN_NO 
             AND SB.HEYA_NO = YO.HEYA_NO 
             AND SB.BUKKEN_PARKING_KB = YO.BUKKEN_PARKING_KB 
           WHERE
                RU.LOGICAL_DEL_FLG = '0' 
             AND SB.LOGICAL_DEL_FLG = '0' 
             AND SB.MOUSIKOMI_KB = '1'
             AND YO.BUKKEN_PARKING_KB = '1'
             AND YO.JOKYO_KB = '1' 
             AND (RU.SAKUSEI_KB <> '3' OR RU.SAKUSEI_KB IS NULL)
             AND RU.KAISYA_CD = PARAM_KAISYACD
             AND RU.UKETUKE_POST_CD = PARAM_POSTCD );
-- MOD END  2019.10.30 h-nakano h-(案件C43089)レスポンス障害対応
/*
           --担当者の当月の受付客数
           (SELECT COUNT(UMV.UKETUKE_NO) UKETUKEDAYMONTH
            FROM UMV_RAITEN_UKETUKE UMV
           WHERE UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             AND UMV.CHUKAI_TANTOU_CD = PARAM_TANTOUCD
             AND TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMM') = PARAM_TDATE
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND UMV.SINCHOKU_CD IN ('1', '2', '3'))
          ,
           --担当者の現在の受付客数
           (SELECT COUNT(UMV.UKETUKE_NO) UKETUKEDAYTANTOU
            FROM UMV_RAITEN_UKETUKE UMV
           WHERE UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             AND UMV.CHUKAI_TANTOU_CD = PARAM_TANTOUCD
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND UMV.SINCHOKU_CD IN ('1', '2', '3'))
          ,

           --店舗の現在の受付客数
           (SELECT COUNT(UMV.UKETUKE_NO) AS UKETUKEDAYKENSU
            FROM UMV_RAITEN_UKETUKE UMV
           WHERE UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND UMV.SINCHOKU_CD IN ('1', '2', '3'))
          ,

           --店舗の当月の受付客数
           (SELECT COUNT(UMV.UKETUKE_NO) 　AS UKETUKEMONTHKENSU
            FROM UMV_RAITEN_UKETUKE UMV
           WHERE UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             AND TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMM') = PARAM_TDATE
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND UMV.SINCHOKU_CD IN ('1', '2', '3'))
          ,
           -- 新着担当者物件数
           (SELECT SUM(SUB.NEWCOUNT) AS NEWTANTOUCNT
            FROM UMV_RAITEN_UKETUKE UMV
              ,(SELECT US.KAISYA_CD AS KAISYACD
                  ,US.UKETUKE_NO AS UKETUKENO
                  ,COUNT(US.UKETUKE_NO) AS NEWCOUNT
                FROM UD_SINTYAKU_BUKKEN US
--UPD_START 2013.06.06 SQLチューニング
--                  ,TOKEN.WD_HEYA      TD
                  ,UD_KUUSITU_HEYA      TD
               WHERE US.KAISYA_CD = PARAM_KAISYACD
--UPD_END 2013.06.06 SQLチューニング
               AND US.HOMEMATE_BUKKEN_NO = TD.HOMEMATE_BUKKEN_NO
                 AND US.HEYA_NO = TD.HEYA_NO
--UPD_START 2013.06.06 SQLチューニング
         AND TD.NYU_KUU_KB = '0'
--                 AND TD.NYUKYO_SB_CD IN ('1', '3')
--                 AND TD.LOGICAL_DEL_FLG = '0'
--UPD_END 2013.06.06 SQLチューニング

                 AND US.SINTYAKU_AL_KAIYJO_FLG = '0'
               GROUP BY US.KAISYA_CD
                   ,US.UKETUKE_NO) SUB
           WHERE UMV.KAISYA_CD = SUB.KAISYACD
             AND UMV.UKETUKE_NO = SUB.UKETUKENO
             AND UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             AND UMV.CHUKAI_TANTOU_CD = PARAM_TANTOUCD
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             --2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
             --AND UMV.SINCHOKU_CD IN ('1','2', '3'))
             AND UMV.SINCHOKU_CD = '1')
             --2014/06/23 趙雲剛 UPD End
          ,
           -- 新着担当者物件数
           (SELECT SUM(SUB.NEWCOUNT) AS NEWPOSTCNT
            FROM UMV_RAITEN_UKETUKE UMV
              ,(SELECT US.KAISYA_CD AS KAISYACD
                  ,US.UKETUKE_NO AS UKETUKENO
                  ,COUNT(US.UKETUKE_NO) AS NEWCOUNT
                FROM UD_SINTYAKU_BUKKEN US
--UPD_START 2013.06.06 SQLチューニング
--                  ,TOKEN.WD_HEYA      TD
                  ,UD_KUUSITU_HEYA      TD
               WHERE US.KAISYA_CD = PARAM_KAISYACD
--UPD_END 2013.06.06 SQLチューニング
               AND US.HOMEMATE_BUKKEN_NO = TD.HOMEMATE_BUKKEN_NO
                 AND US.HEYA_NO = TD.HEYA_NO

--UPD_START 2013.06.06 SQLチューニング
         AND TD.NYU_KUU_KB = '0'
--                 AND TD.NYUKYO_SB_CD IN ('1', '3')
--                 AND TD.LOGICAL_DEL_FLG = '0'
--UPD_END 2013.06.06 SQLチューニング

                 AND US.SINTYAKU_AL_KAIYJO_FLG = '0'
               GROUP BY US.KAISYA_CD
                   ,US.UKETUKE_NO) SUB
           WHERE UMV.KAISYA_CD = SUB.KAISYACD
             AND UMV.UKETUKE_NO = SUB.UKETUKENO
             AND UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             --2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
             --AND UMV.SINCHOKU_CD IN ('1','2', '3'))
             AND UMV.SINCHOKU_CD = '1')
             --2014/06/23 趙雲剛 UPD End
          ,
          --追客件数の担当者別対応
          (SELECT COUNT(UT.UKETUKE_NO) AS TUIKYAKUTANTOUKENSU
            FROM UD_AL_TUIKYAKU     UT
              ,UMV_RAITEN_UKETUKE UR
           WHERE UT.KAISYA_CD = UR.KAISYA_CD
             AND UT.UKETUKE_NO = UR.UKETUKE_NO
             AND UR.SINCHOKU_CD IN ('1', '2', '3')
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UR.SAKUSEI_KB <> '3' OR UR.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND ((TO_CHAR(UR.LAST_TUIKYAKU_DATE, 'YYYYMMDD') <> TO_CHAR(SYSDATE, 'YYYYMMDD') AND
               UR.TUIKYAKU_ALARM_DATE IS NULL) OR UR.TUIKYAKU_ALARM_DATE IS NOT NULL)
             AND UR.KAISYA_CD = PARAM_KAISYACD
             AND UR.UKETUKE_POST_CD = PARAM_POSTCD --　仲介店舗コードが存在しない、一応受付店舗コードを使用
             AND UR.CHUKAI_TANTOU_CD = PARAM_TANTOUCD
             AND UR.LOGICAL_DEL_FLG = '0')
          ,

           -- 追客件数の店舗別対応
           (SELECT COUNT(UT.UKETUKE_NO) AS TUIKYAKUTENPOKENSU
            FROM UD_AL_TUIKYAKU    UT
              ,UD_RAITEN_UKETUKE UR1
           WHERE UT.KAISYA_CD = UR1.KAISYA_CD
             AND UT.UKETUKE_NO = UR1.UKETUKE_NO
             AND UR1.KAISYA_CD = PARAM_KAISYACD
             AND UR1.SINCHOKU_CD IN ('1', '2', '3')
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UR1.SAKUSEI_KB <> '3' OR UR1.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND ((TO_CHAR(UR1.LAST_TUIKYAKU_DATE, 'YYYYMMDD') <> TO_CHAR(SYSDATE, 'YYYYMMDD') AND
               UR1.TUIKYAKU_ALARM_DATE IS NULL) OR UR1.TUIKYAKU_ALARM_DATE IS NOT NULL)
             AND UR1.UKETUKE_POST_CD = PARAM_POSTCD --　仲介店舗コードが存在しない、一応受付店舗コードを使用
             AND UR1.LOGICAL_DEL_FLG = '0')
          ,
           -- 仮押さえ件数担当者
           (SELECT COUNT(US.UKETUKE_NO) AS KARIOSAETANTOUKENSU
            FROM UD_SHOUKAI_BUKKEN US
              ,UD_RAITEN_UKETUKE UR
              ,UV_KARIOSAE_BUKKEN UK
           WHERE US.KAISYA_CD = UR.KAISYA_CD
             AND US.KAISYA_CD = PARAM_KAISYACD
             AND US.UKETUKE_NO = UR.UKETUKE_NO
             AND US.MOUSIKOMI_KB = '1'
             AND US.LOGICAL_DEL_FLG = '0'
             AND UR.UKETUKE_POST_CD = PARAM_POSTCD
             AND UR.CHUKAI_TANTOU_CD = PARAM_TANTOUCD
             AND UR.LOGICAL_DEL_FLG = '0'
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UR.SAKUSEI_KB <> '3' OR UR.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND UK.KENSAKU_KAISYA_CD = US.KAISYA_CD
             AND UK.UKETUKE_NO = US.UKETUKE_NO
             AND UK.KENSAKU_UKETUKE_NO = US.UKETUKE_NO
             AND UK.HOMEMATE_BUKKEN_NO = US.HOMEMATE_BUKKEN_NO
             AND UK.HEYA_NO = US.HEYA_NO)
          ,

           -- 仮押さえ件数店舗
           (SELECT COUNT(US.UKETUKE_NO) AS KARIOSAETENPOKENSU
            FROM UD_SHOUKAI_BUKKEN US
              ,UD_RAITEN_UKETUKE UR
              ,UV_KARIOSAE_BUKKEN UK
           WHERE US.KAISYA_CD = UR.KAISYA_CD
             AND US.KAISYA_CD = PARAM_KAISYACD
             AND US.UKETUKE_NO = UR.UKETUKE_NO
             AND US.MOUSIKOMI_KB = '1'
             AND US.LOGICAL_DEL_FLG = '0'
             AND UR.UKETUKE_POST_CD = PARAM_POSTCD
             AND UR.LOGICAL_DEL_FLG = '0'
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UR.SAKUSEI_KB <> '3' OR UR.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND UK.KENSAKU_KAISYA_CD = US.KAISYA_CD
             AND UK.UKETUKE_NO = US.UKETUKE_NO
             AND UK.KENSAKU_UKETUKE_NO = US.UKETUKE_NO
             AND UK.HOMEMATE_BUKKEN_NO = US.HOMEMATE_BUKKEN_NO
             AND UK.HEYA_NO = US.HEYA_NO);
*/
    -- PROCEDURE PRO_OSHIRASE (param_kaisyacd, param_tantoucd, out_oshirase, rst_msg, rst_code);
--2014/06/23 趙雲剛 DEL Start (案件C38117)セールスポイント編集機能の追加
/*
    OPEN CSRUKETUKENO FOR
      SELECT UMV.UKETUKE_NO
      FROM UMV_RAITEN_UKETUKE UMV
     WHERE UMV.KAISYA_CD = PARAM_KAISYACD
       AND UMV.SINCHOKU_CD IN ('1','2','3')
       AND UMV.UKETUKE_POST_CD = PARAM_POSTCD --　仲介店舗コードが存在しない、一応受付店舗コードを使用
       AND UMV.CHUKAI_TANTOU_CD = PARAM_TANTOUCD
       AND UMV.LOGICAL_DEL_FLG = '0';

    LOOP
      FETCH CSRUKETUKENO
        INTO LC_UKETUKE_NO;
      EXIT WHEN CSRUKETUKENO%NOTFOUND;

      PV_UKETUKENO := PV_UKETUKENO || ',''' || LC_UKETUKE_NO || '''';
      LC_CNT := LC_CNT + 1;
    END LOOP;

    IF LC_CNT = 0
    THEN
        OPEN OUT_OSHIRASE FOR SELECT 1 FROM DUAL;
    ELSE
        IF PV_UKETUKENO IS NOT NULL AND LENGTH(PV_UKETUKENO) > 0
        THEN
          PV_UKETUKENO := SUBSTR(PV_UKETUKENO, 2, LENGTH(PV_UKETUKENO));
        ELSE
          PV_UKETUKENO := NULL;
        END IF;

        L_SQL := 'SELECT COUNT(*)';
        L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV1';
        L_SQL := L_SQL || ' WHERE UV1.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
        L_SQL := L_SQL || '   AND UV1.UKETUKE_NO = UV1.KENSAKU_UKETUKE_NO';
        L_SQL := L_SQL || '   AND UV1.UKETUKE_NO IN (' || PV_UKETUKENO || ')';
        L_SQL := L_SQL || '   AND UV1.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
        L_SQL := L_SQL || '   AND UV1.KIGENGIRE_FLG = ''1''';
        L_SQL := L_SQL || ' ORDER BY UV1.KENSAKU_UKETUKE_NO';
        EXECUTE IMMEDIATE L_SQL INTO LC_SQL1_CNT;

        L_SQL := 'SELECT COUNT(*)';
        L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV2';
        L_SQL := L_SQL || ' WHERE UV2.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
        L_SQL := L_SQL || '   AND UV2.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
        L_SQL := L_SQL || '   AND UV2.UKETUKE_NO = UV2.KENSAKU_UKETUKE_NO';
        L_SQL := L_SQL || '   AND UV2.UKETUKE_NO IN (' || PV_UKETUKENO || ')';
        L_SQL := L_SQL || '   AND UV2.KARIOSAE_JYUNI = ''1''';
        L_SQL := L_SQL || '   AND UV2.KARIOSAEJI_JYUNI <> ''1''';
        L_SQL := L_SQL || ' ORDER BY UV2.UKETUKE_NO';
        EXECUTE IMMEDIATE L_SQL INTO LC_SQL2_CNT;

        L_SQL := 'SELECT COUNT(*)';
        L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV3';
        L_SQL := L_SQL || ' WHERE UV3.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
        L_SQL := L_SQL || '   AND UV3.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
        L_SQL := L_SQL || '   AND UV3.KARIOSAE_KB = ''0''';
        L_SQL := L_SQL || '   AND UV3.KENSAKU_UKETUKE_NO IN (' || PV_UKETUKENO || ')';
        L_SQL := L_SQL || ' ORDER BY UV3.KENSAKU_UKETUKE_NO';
        EXECUTE IMMEDIATE L_SQL INTO LC_SQL3_CNT;
        IF LC_SQL1_CNT > 0
        THEN
            --SQL1
            L_SQL := ' SELECT UV1.KOKYAKU_NAME KOKYAKUNAME,';
            L_SQL := L_SQL || '        UV1.KENSAKU_UKETUKE_NO UKETUKENO,';
            L_SQL := L_SQL || '        UV1.KENSAKU_CHUKAI_TANTOU_CD CHUKAITANTOUCD,';
            L_SQL := L_SQL || '        UV1.HOMEMATE_BUKKEN_NO BUKKENNO,';
            L_SQL := L_SQL || '        UV1.HEYA_NO HEYANO,';
            L_SQL := L_SQL || '        UV1.BUKKEN_PARKING_KB PARKINGKBN,';
            L_SQL := L_SQL || '        TO_CHAR(UV1.UPD_DATE_SHOUKAI_BUKKEN, ''YYYYMMDDHH24MI'') UPDDATE,';
            L_SQL := L_SQL || '        ''1'' OSHIRASEKBN';
            L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV1';
            L_SQL := L_SQL || ' WHERE UV1.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
            L_SQL := L_SQL || '   AND UV1.UKETUKE_NO = UV1.KENSAKU_UKETUKE_NO';
            L_SQL := L_SQL || '   AND UV1.UKETUKE_NO IN (' || PV_UKETUKENO || ')';
            L_SQL := L_SQL || '   AND UV1.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
            L_SQL := L_SQL || '   AND UV1.KIGENGIRE_FLG = ''1''';
            L_SQL := L_SQL || ' ORDER BY UV1.KENSAKU_UKETUKE_NO';
            OPEN OUT_OSHIRASE FOR L_SQL;
        ELSIF LC_SQL2_CNT > 0
        THEN
            --SQL2
            L_SQL := ' SELECT UV2.KOKYAKU_NAME KOKYAKUNAME';
            L_SQL := L_SQL || '     ,UV2.UKETUKE_NO UKETUKENO';
            L_SQL := L_SQL || '     ,UV2.KENSAKU_CHUKAI_TANTOU_CD CHUKAITANTOUCD';
            L_SQL := L_SQL || '     ,UV2.HOMEMATE_BUKKEN_NO BUKKENNO';
            L_SQL := L_SQL || '     ,UV2.HEYA_NO HEYANO';
            L_SQL := L_SQL || '     ,UV2.BUKKEN_PARKING_KB PARKINGKBN';
            L_SQL := L_SQL || '     ,TO_CHAR(UV2.UPD_DATE_SHOUKAI_BUKKEN, ''YYYYMMDDHH24MI'') UPDDATE';
            L_SQL := L_SQL || '     ,''2'' OSHIRASEKBN';
            L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV2';
            L_SQL := L_SQL || ' WHERE UV2.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
            L_SQL := L_SQL || '   AND UV2.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
            L_SQL := L_SQL || '   AND UV2.UKETUKE_NO = UV2.KENSAKU_UKETUKE_NO';
            L_SQL := L_SQL || '   AND UV2.UKETUKE_NO IN (' || PV_UKETUKENO || ')';
            L_SQL := L_SQL || '   AND UV2.KARIOSAE_JYUNI = ''1''';
            L_SQL := L_SQL || '   AND UV2.KARIOSAEJI_JYUNI <> ''1''';
            L_SQL := L_SQL || ' ORDER BY UV2.UKETUKE_NO';
            OPEN OUT_OSHIRASE FOR L_SQL;
        ELSIF LC_SQL3_CNT > 0
        THEN
            --SQL3
            L_SQL := ' SELECT UV3.KARIOSAEKAIJYO_KOKYAKU_NAME KOKYAKUNAME';
            L_SQL := L_SQL || '    ,UV3.KENSAKU_UKETUKE_NO UKETUKENO';
            L_SQL := L_SQL || '    ,UV3.KENSAKU_CHUKAI_TANTOU_CD CHUKAITANTOUCD';
            L_SQL := L_SQL || '    ,UV3.HOMEMATE_BUKKEN_NO BUKKENNO';
            L_SQL := L_SQL || '    ,UV3.HEYA_NO HEYANO';
            L_SQL := L_SQL || '    ,UV3.BUKKEN_PARKING_KB PARKINGKBN';
            L_SQL := L_SQL || '    ,TO_CHAR(UV3.UPD_DATE_SHOUKAI_BUKKEN, ''YYYYMMDDHH24MI'') UPDDATE';
            L_SQL := L_SQL || '    ,''3'' OSHIRASEKBN';
            L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV3';
            L_SQL := L_SQL || ' WHERE UV3.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
            L_SQL := L_SQL || '   AND UV3.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
            L_SQL := L_SQL || '   AND UV3.KARIOSAE_KB = ''0''';
            L_SQL := L_SQL || '   AND UV3.KENSAKU_UKETUKE_NO IN (' || PV_UKETUKENO || ')';
            L_SQL := L_SQL || ' ORDER BY UV3.KENSAKU_UKETUKE_NO';
            OPEN OUT_OSHIRASE FOR L_SQL;
        ELSE
            OPEN OUT_OSHIRASE FOR SELECT 1 FROM DUAL;
        END IF;
    END IF;
*/
--2014/06/23 趙雲剛 DEL End
    rst_code := MSG_SUCCESS;

  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GETUKETUKEINFO;

--2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
  -- ---------------------------------------------------------
  -- システム名      ： 受付紹介システム
  -- プロシージャID  ： PRO_GET_ALL_UKETUKE_INFO
  -- プロシージャ名  ： 店舗受付状況データ取得
  -- 引数            ： param_kaisyacd    　  IN   VARCHAR2         -- 会社コード
  --                 ： param_authority_cd   IN   VARCHAR2    -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
  --                 ： param_sisya_blk_cd   IN   VARCHAR2    -- 支社ブロックコード
  --                 ： param_postcd     　　IN   VARCHAR2        　 -- 店舗コード
  --                 ： param_tantoucd      IN   VARCHAR2        　   -- 担当者コード
  --                 ： param_tdate         IN   VARCHAR2         　 -- 日付
  --                 ： out_uketukeInfo             OUT  VARCHAR2  　-- 受付客数情報
  --                 ： out_oshirase             OUT  VARCHAR2  　　 -- お知らせ情報
  --                 ： rst_msg             OUT  VARCHAR2  　　  -- エラーメッセージ
  --                 ： rst_code           OUT  VARCHAR2  　    -- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要        ： 受付状況データ取得
  -- 初版作成日/作成者 ： 2014/06/23 Ver.1.0.0 SYS_趙雲剛
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRO_GET_ALL_UKETUKE_INFO
  (
     PARAM_KAISYACD  IN VARCHAR2 -- 会社コード
    ,PARAM_AUTHORITY_CD IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
    ,PARAM_SISYA_BLK_CD IN VARCHAR2 -- 支社ブロックコード
    ,PARAM_POSTCD    IN VARCHAR2 -- 店舗コード
    ,PARAM_TANTOUCD  IN VARCHAR2 -- 担当者コード
    ,PARAM_TDATE     IN VARCHAR2 -- 日付
    ,OUT_UKETUKEINFO OUT TYPCSR -- 受付客数情報
    ,OUT_OSHIRASE    OUT TYPCSR -- お知らせ情報
    ,rst_msg         OUT VARCHAR2 -- エラーメッセージ
    ,rst_code        OUT VARCHAR2 -- メッセージID
  ) IS
  BEGIN
  --受付客数情報を取得する
  IF PARAM_AUTHORITY_CD = '0' THEN
    --通常ユーザー
    PRO_GETUKETUKEINFO(PARAM_KAISYACD, PARAM_POSTCD, PARAM_TANTOUCD, PARAM_TDATE, OUT_UKETUKEINFO, rst_msg, rst_code);
  ELSIF (PARAM_AUTHORITY_CD = '1' OR PARAM_AUTHORITY_CD = '2') AND PARAM_POSTCD IS NULL THEN
    --店舗コード指定ない
    PRO_GET_KAISYA_UKETUKEINFO(PARAM_KAISYACD, PARAM_AUTHORITY_CD, PARAM_SISYA_BLK_CD, PARAM_TDATE, OUT_UKETUKEINFO, rst_msg, rst_code);
  ELSE
    --店舗コード指定
    PRO_GET_POST_UKETUKEINFO(PARAM_KAISYACD, PARAM_POSTCD, PARAM_TDATE, OUT_UKETUKEINFO, rst_msg, rst_code);
  END IF;
  --お知らせ情報データを取得する
  IF PARAM_POSTCD IS NOT NULL THEN
    PRO_GET_OSHIRASE_INFO(PARAM_KAISYACD ,PARAM_AUTHORITY_CD ,PARAM_SISYA_BLK_CD ,PARAM_POSTCD ,PARAM_TANTOUCD,OUT_OSHIRASE,rst_msg,rst_code);
  ELSE 
    OPEN OUT_OSHIRASE FOR SELECT 1 FROM DUAL WHERE 1<>1;
  END IF;
  END PRO_GET_ALL_UKETUKE_INFO;
  
  -- ---------------------------------------------------------
  -- システム名      ： 受付紹介システム
  -- プロシージャID  ： PRO_GET_POST_UKETUKEINFO
  -- プロシージャ名  ： 店舗受付状況データ取得(店舗コード指定)
  -- 引数            ： param_kaisyacd    　  IN   VARCHAR2         -- 会社コード 
  --                 ： param_postcd     　　 IN   VARCHAR2        　 -- 店舗コード
  --                 ： param_tdate           IN   VARCHAR2         　 -- 日付
  --                 ： out_uketukeInfo       OUT  VARCHAR2  　-- 受付客数情報
  --                 ： rst_msg               OUT  VARCHAR2  　　  -- エラーメッセージ
  --                 ： rst_code              OUT  VARCHAR2  　    -- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要        ： 受付状況データ取得
  -- 初版作成日/作成者 ： 2014/06/23 Ver.1.0.0 SYS_趙雲剛
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRO_GET_POST_UKETUKEINFO
  (
    PARAM_KAISYACD  IN VARCHAR2 -- 会社コード
    ,PARAM_POSTCD    IN VARCHAR2 -- 店舗コード
    ,PARAM_TDATE     IN VARCHAR2 -- 日付
    ,OUT_UKETUKEINFO OUT TYPCSR -- 受付客数情報
    ,rst_msg         OUT VARCHAR2 -- エラーメッセージ
    ,rst_code        OUT VARCHAR2 -- メッセージID
  ) IS
  BEGIN
    OPEN OUT_UKETUKEINFO FOR
      SELECT *
        FROM
-- MOD START 2019.10.30 h-nakano(案件C43089)レスポンス障害対応
           --受付客数(店舗受付客数、店舗の当月の受付客数)
           (SELECT 
              NVL(SUM(CASE WHEN TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMM') = PARAM_TDATE THEN 1 ELSE 0 END),0) AS UKETUKEMONTHKENSU,
              NVL(SUM(CASE WHEN UMV.UKETUKE_NO IS NOT NULL THEN 1 ELSE 0 END),0) AS UKETUKEDAYKENSU
            FROM UD_RAITEN_UKETUKE UMV
           WHERE UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             AND UMV.LOGICAL_DEL_FLG = '0'
             AND UMV.SINCHOKU_CD IN ('1', '2', '3')),
           (SELECT 
              NVL(SUM(CASE WHEN SUB.NEWCOUNT IS NOT NULL THEN SUB.NEWCOUNT ELSE 0 END),0) AS NEWPOSTCNT
            FROM UD_RAITEN_UKETUKE UMV
              ,(SELECT US.KAISYA_CD AS KAISYACD
                  ,US.UKETUKE_NO AS UKETUKENO
                  ,COUNT(US.UKETUKE_NO) AS NEWCOUNT
                FROM UD_SINTYAKU_BUKKEN US
                  ,UD_KUUSITU_HEYA      TD
               WHERE US.KAISYA_CD = PARAM_KAISYACD
               AND US.HOMEMATE_BUKKEN_NO = TD.HOMEMATE_BUKKEN_NO
                 AND US.HEYA_NO = TD.HEYA_NO
                 AND TD.NYU_KUU_KB = '0'
                 AND US.SINTYAKU_AL_KAIYJO_FLG = '0'
               GROUP BY US.KAISYA_CD
                   ,US.UKETUKE_NO) SUB
           WHERE UMV.KAISYA_CD = SUB.KAISYACD
             AND UMV.UKETUKE_NO = SUB.UKETUKENO
             AND UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             AND UMV.LOGICAL_DEL_FLG = '0'
             AND UMV.SINCHOKU_CD = '1'),

          --追客件数(店舗別)
          (SELECT 
              NVL(SUM(CASE WHEN UT.UKETUKE_NO IS NOT NULL THEN 1 ELSE 0 END),0) AS TUIKYAKUTENPOKENSU
            FROM UD_AL_TUIKYAKU     UT
              ,UD_RAITEN_UKETUKE UR
           WHERE UT.KAISYA_CD = UR.KAISYA_CD
             AND UT.UKETUKE_NO = UR.UKETUKE_NO
             AND UR.SINCHOKU_CD IN ('1', '2', '3')
             AND (UR.SAKUSEI_KB <> '3' OR UR.SAKUSEI_KB IS NULL)
             AND ((TO_CHAR(UR.LAST_TUIKYAKU_DATE, 'YYYYMMDD') <> TO_CHAR(SYSDATE, 'YYYYMMDD') AND
               UR.TUIKYAKU_ALARM_DATE IS NULL) OR UR.TUIKYAKU_ALARM_DATE IS NOT NULL)
             AND UR.KAISYA_CD = PARAM_KAISYACD
             AND UR.UKETUKE_POST_CD = PARAM_POSTCD 
             AND UR.LOGICAL_DEL_FLG = '0'
             AND UR.LOGICAL_DEL_FLG = '0'),
           -- 仮押さえ件数(店舗)をまとめて取得
          (SELECT 
             NVL(SUM(CASE WHEN SB.UKETUKE_NO IS NOT NULL THEN 1 ELSE 0 END),0) AS KARIOSAETENPOKENSU
           FROM 
                  UD_RAITEN_UKETUKE RU
             JOIN UD_SHOUKAI_BUKKEN  SB
               ON (RU.KAISYA_CD = SB.KAISYA_CD AND RU.UKETUKE_NO = SB.UKETUKE_NO) 
             LEFT JOIN (
               SELECT WYU.* FROM (
                 SELECT WY.*, ROW_NUMBER() OVER(PARTITION BY WY.KAISYA_CD, WY.UKETUKE_CD, 
                          WY.HOMEMATE_BUKKEN_NO, WY.HEYA_NO, WY.BUKKEN_PARKING_KB ORDER BY UKETUKE_SEQ DESC) AS ROW_NUM 
                 FROM TOKEN.WD_YOYAKU WY) WYU
               WHERE WYU.ROW_NUM = 1
             ) YO 
             ON SB.HOMEMATE_BUKKEN_NO = YO.HOMEMATE_BUKKEN_NO 
             AND SB.HEYA_NO = YO.HEYA_NO 
             AND SB.BUKKEN_PARKING_KB = YO.BUKKEN_PARKING_KB 
           WHERE
                RU.LOGICAL_DEL_FLG = '0' 
             AND SB.LOGICAL_DEL_FLG = '0' 
             AND SB.MOUSIKOMI_KB = '1'
             AND YO.BUKKEN_PARKING_KB = '1'
             AND YO.JOKYO_KB = '1' 
             AND (RU.SAKUSEI_KB <> '3' OR RU.SAKUSEI_KB IS NULL)
             AND RU.KAISYA_CD = PARAM_KAISYACD
             AND RU.UKETUKE_POST_CD = PARAM_POSTCD );
-- MOD END  2019.10.30 h-nakano h-(案件C43089)レスポンス障害対応
/*
           --店舗の現在の受付客数
           (SELECT COUNT(UMV.UKETUKE_NO) AS UKETUKEDAYKENSU
            FROM UMV_RAITEN_UKETUKE UMV
           WHERE UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND UMV.SINCHOKU_CD IN ('1', '2', '3'))
          ,

           --店舗の当月の受付客数
           (SELECT COUNT(UMV.UKETUKE_NO) 　AS UKETUKEMONTHKENSU
            FROM UMV_RAITEN_UKETUKE UMV
           WHERE UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             AND TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMM') = PARAM_TDATE
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND UMV.SINCHOKU_CD IN ('1', '2', '3'))
          ,
           -- 新着店舗物件数
           (SELECT SUM(SUB.NEWCOUNT) AS NEWPOSTCNT
            FROM UMV_RAITEN_UKETUKE UMV
              ,(SELECT US.KAISYA_CD AS KAISYACD
                  ,US.UKETUKE_NO AS UKETUKENO
                  ,COUNT(US.UKETUKE_NO) AS NEWCOUNT
                FROM UD_SINTYAKU_BUKKEN US
                  ,UD_KUUSITU_HEYA      TD
               WHERE US.KAISYA_CD = PARAM_KAISYACD
                 AND US.HOMEMATE_BUKKEN_NO = TD.HOMEMATE_BUKKEN_NO
                 AND US.HEYA_NO = TD.HEYA_NO
                 AND TD.NYU_KUU_KB = '0'
                 AND US.SINTYAKU_AL_KAIYJO_FLG = '0'
               GROUP BY US.KAISYA_CD
                   ,US.UKETUKE_NO) SUB
           WHERE UMV.KAISYA_CD = SUB.KAISYACD
             AND UMV.UKETUKE_NO = SUB.UKETUKENO
             AND UMV.KAISYA_CD = PARAM_KAISYACD
             AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             --2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
             --AND UMV.SINCHOKU_CD IN ('1','2', '3'))
             AND UMV.SINCHOKU_CD = '1')
             --2014/06/23 趙雲剛 UPD End
          ,
           -- 追客件数の店舗別対応
           (SELECT COUNT(UT.UKETUKE_NO) AS TUIKYAKUTENPOKENSU
            FROM UD_AL_TUIKYAKU    UT
              ,UD_RAITEN_UKETUKE UR1
           WHERE UT.KAISYA_CD = UR1.KAISYA_CD
             AND UT.UKETUKE_NO = UR1.UKETUKE_NO
             AND ((TO_CHAR(UR1.LAST_TUIKYAKU_DATE, 'YYYYMMDD') <> TO_CHAR(SYSDATE, 'YYYYMMDD') AND
               UR1.TUIKYAKU_ALARM_DATE IS NULL) OR UR1.TUIKYAKU_ALARM_DATE IS NOT NULL)
             AND UR1.KAISYA_CD = PARAM_KAISYACD
             AND UR1.SINCHOKU_CD IN ('1', '2', '3')
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UR1.SAKUSEI_KB <> '3' OR UR1.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND UR1.UKETUKE_POST_CD = PARAM_POSTCD
             AND UR1.LOGICAL_DEL_FLG = '0')
          ,
           -- 仮押さえ件数店舗
           (SELECT COUNT(US.UKETUKE_NO) AS KARIOSAETENPOKENSU
            FROM UD_SHOUKAI_BUKKEN US
              ,UD_RAITEN_UKETUKE UR
              ,UV_KARIOSAE_BUKKEN UK
           WHERE US.KAISYA_CD = UR.KAISYA_CD
             AND US.UKETUKE_NO = UR.UKETUKE_NO
             AND UK.KENSAKU_KAISYA_CD = US.KAISYA_CD
             AND UK.UKETUKE_NO = US.UKETUKE_NO
             AND UK.KENSAKU_UKETUKE_NO = US.UKETUKE_NO
             AND UK.HOMEMATE_BUKKEN_NO = US.HOMEMATE_BUKKEN_NO
             AND UK.HEYA_NO = US.HEYA_NO
             AND US.MOUSIKOMI_KB = '1'
             AND US.LOGICAL_DEL_FLG = '0'
             AND UR.LOGICAL_DEL_FLG = '0'
             AND US.KAISYA_CD = PARAM_KAISYACD
             --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
             AND (UR.SAKUSEI_KB <> '3' OR UR.SAKUSEI_KB IS NULL)
             --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
             AND UR.UKETUKE_POST_CD = PARAM_POSTCD);
*/
    rst_code := MSG_SUCCESS;

  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GET_POST_UKETUKEINFO;
  
  -- ---------------------------------------------------------
  -- システム名      ： 受付紹介システム
  -- プロシージャID  ： PRO_GET_KAISYA_UKETUKEINFO
  -- プロシージャ名  ： 店舗受付状況データ取得(店舗コード指定なし)
  -- 引数            ： param_kaisyacd    　    IN   VARCHAR2     -- 会社コード
  --                 ： param_authority_cd      IN   VARCHAR2    -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者)
  --                 ： param_sisya_blk_cd      IN   VARCHAR2    -- 支社ブロックコード
  --                 ： param_tdate             IN   VARCHAR2    -- 日付
  --                 ： out_uketukeInfo         OUT  VARCHAR2  　-- 受付客数情報
  --                 ： rst_msg                 OUT  VARCHAR2  　-- エラーメッセージ
  --                 ： rst_code                OUT  VARCHAR2  　-- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要        ： 受付状況データ取得
  -- 初版作成日/作成者 ： 2014/06/23 Ver.1.0.0 SYS_趙雲剛
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRO_GET_KAISYA_UKETUKEINFO
  (
     PARAM_KAISYACD  IN VARCHAR2 -- 会社コード
     ,PARAM_AUTHORITY_CD IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者)
     ,PARAM_SISYA_BLK_CD IN VARCHAR2 -- 支社ブロックコード
     ,PARAM_TDATE     IN VARCHAR2 -- 日付
     ,OUT_UKETUKEINFO OUT TYPCSR -- 受付客数情報
     ,rst_msg         OUT VARCHAR2 -- エラーメッセージ
     ,rst_code        OUT VARCHAR2 -- メッセージID
  ) IS
  L_UKETUKEDAYKENSU integer;
  L_UKETUKEMONTHKENSU integer;
  BEGIN
      
  --店舗の現在の受付客数
  SELECT COUNT(UMV.UKETUKE_NO) AS UKETUKEDAYKENSU
    INTO L_UKETUKEDAYKENSU
      FROM UMV_RAITEN_UKETUKE UMV
      JOIN TOKEN.WM_POST WP
        ON (UMV.KAISYA_CD = WP.KAISYA_CD AND
           UMV.UKETUKE_POST_CD = WP.POST_CD)
     WHERE UMV.KAISYA_CD = PARAM_KAISYACD
       AND ((PARAM_AUTHORITY_CD = '1' AND
           WP.SISYA_BLK_CD = PARAM_SISYA_BLK_CD) OR
           PARAM_AUTHORITY_CD = '2')
       AND WP.OUTPUT_ORDER <> '999'
       AND WP.OUTPUT_ORDER IS NOT NULL
       AND WP.LOGICAL_DEL_FLG = '0'
       AND UMV.LOGICAL_DEL_FLG = '0'
       --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
       AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
       --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
       AND UMV.SINCHOKU_CD IN ('1', '2', '3');
    
  --店舗の当月の受付客数  
  SELECT COUNT(DECODE(TO_CHAR(UMV.UKETUKE_DATE,'YYYYMM'), PARAM_TDATE, 1, NULL)) AS UKETUKEMONTHKENSU
    INTO L_UKETUKEMONTHKENSU
   FROM UMV_RAITEN_UKETUKE UMV
      JOIN TOKEN.WM_POST WP
        ON (UMV.KAISYA_CD = WP.KAISYA_CD AND
           UMV.UKETUKE_POST_CD = WP.POST_CD)
     WHERE UMV.KAISYA_CD = PARAM_KAISYACD
       AND ((PARAM_AUTHORITY_CD = '1' AND
           WP.SISYA_BLK_CD = PARAM_SISYA_BLK_CD) OR
           PARAM_AUTHORITY_CD = '2')
       AND WP.OUTPUT_ORDER <> '999'
       AND WP.OUTPUT_ORDER IS NOT NULL
       AND WP.LOGICAL_DEL_FLG = '0'
       AND UMV.LOGICAL_DEL_FLG = '0'
       --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
       AND (UMV.SAKUSEI_KB <> '3' OR UMV.SAKUSEI_KB IS NULL)
       --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
       AND UMV.SINCHOKU_CD IN ('1', '2', '3');
  OPEN OUT_UKETUKEINFO FOR
    SELECT L_UKETUKEDAYKENSU    AS UKETUKEDAYKENSU,
           L_UKETUKEMONTHKENSU  AS UKETUKEMONTHKENSU
      FROM DUAL;
  rst_code := MSG_SUCCESS;

  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GET_KAISYA_UKETUKEINFO;
  --2014/06/23 趙雲剛 ADD End

  -- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_GETMITOROKUINFO
  -- プロシージャ名      ： 未登録データデータを取得
  -- 引数             ： param_kaisyacd    　　 IN   VARCHAR2     -- 会社コード
  --                 ： param_authority_cd   IN   VARCHAR2    -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
  --                 ： param_sisya_blk_cd   IN   VARCHAR2    -- 支社ブロックコード
  --                 ： param_postcd     　 IN   VARCHAR2        　 -- 店舗コード
  --                 ： out_mitorokucount 　OUT  NUMBER     　　　　-- 未登録データ件数
  --                 ： out_mitorokuinfo 　OUT  typCSR     　　　　-- 未登録データ
  --                 ： rst_msg    　　　OUT  VARCHAR2  　　-- エラーメッセージ
  --                 ： rst_code  　　 OUT  VARCHAR2  　-- メッセージID
    -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要          ： 未登録データデータを取得
  -- 初版作成日/作成者  ： 2011/10/19 Ver.1.0.0 SYS_馮強華
  -- 変更情報          ： 2012/01/13 Ver.1.0.0 SYS_趙雲剛 bug024対応
  -- 変更情報           ：2014/06/23 Ver.1.0.1 SYS_趙雲剛(案件C38117)セールスポイント編集機能の追加
  -- 変更情報         ： 2017/08/04 Ver.1.0.2 MJEC)長倉 (案件C38101-1)ホームメイト自動受付対応 
  --                   ：2017/09/13 Ver.1.0.3 SYS_張虎強(案件C41004)法人仲介担当者登録機能追加対応
  --                   ：2019/08/15 Ver.1.0.4 SYS_カク年昇(案件C43100)お客様項目の入力簡素化対応
  -- ---------------------------------------------------------
  PROCEDURE PRO_GETMITOROKUINFO
  (
    PARAM_KAISYACD    IN VARCHAR2 -- 会社コード
     --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
     ,PARAM_AUTHORITY_CD IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
     ,PARAM_SISYA_BLK_CD IN VARCHAR2 -- 支社ブロックコード
     --2014/06/23 趙雲剛 ADD End
     ,PARAM_POSTCD      IN VARCHAR2 -- 店舗コード
     ,OUT_MITOROKUCOUNT OUT NUMBER -- 未登録データ件数
     ,OUT_MITOROKUINFO  OUT TYPCSR -- 未登録データ
     ,rst_msg           OUT VARCHAR2 -- エラーメッセージ
     ,rst_code          OUT VARCHAR2 -- メッセージID
  ) IS

  BEGIN
  --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
  IF PARAM_POSTCD IS NOT NULL THEN
  --2014/06/23 趙雲剛 ADD End
      --未登録データ件数
      BEGIN
        SELECT COUNT(DISTINCT(UMV.UKETUKE_NO))
          INTO OUT_MITOROKUCOUNT
          FROM UMV_RAITEN_UKETUKE UMV
          --2014/06/23 趙雲剛 DEL Start (案件C38117)セールスポイント編集機能の追加
          --  ,TOKEN.WM_TANTOU    WMT
          --2014/06/23 趙雲剛 DEL End
         WHERE UMV.KAISYA_CD = PARAM_KAISYACD
           AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
           --2014/06/23 趙雲剛 DEL Start (案件C38117)セールスポイント編集機能の追加
           --AND UMV.KAISYA_CD = WMT.KAISYA_CD
           --AND UMV.CHUKAI_TANTOU_CD = WMT.TANTOU_CD
           --2014/06/23 趙雲剛 DEL End
           --2017/008/04 MJEC)長倉 ADD Start (案件C38101-1)ホームメイト自動受付対応
           --受付情報作成区分が'0'の場合
           AND UMV.SAKUSEI_KB = '0'
           --2017/08/04 MJEC)長倉 ADD End
           --2019/08/15 SYS_カク年昇 UPD START (案件C43100)お客様項目の入力簡素化対応
           --AND (UMV.KOKYAKU_INFO_FLG = '0' OR (UMV.ANKETO_FLG = '0' AND UMV.UKETUKE_KB = '1') OR
           AND (UMV.KOKYAKU_INFO_FLG = '0' OR  
           --2019/08/15 SYS_カク年昇 UPD END (案件C43100)お客様項目の入力簡素化対応
             UMV.KIBOU_FLG = '0');
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          OUT_MITOROKUCOUNT := 0;
      END;

      --未登録データデータを取得
      OPEN OUT_MITOROKUINFO FOR
        SELECT DISTINCT (UKETUKE_NO) AS UKETUKE_NO
                 ,UKETUKE_DATE
                 ,UKETUKE_TIME
                 ,SORT_UKETUKEDATE
                 ,UKETUKE_TANTOU_CD
           --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
                 ,UKETUKE_POST_CD
         --2014/06/23 趙雲剛 ADD End
          FROM (SELECT ROWNUM ROWINDEX
                ,T.*
              FROM (SELECT UMV.UKETUKE_NO
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYY/MM/DD') UKETUKE_DATE
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'HH24:MI') UKETUKE_TIME
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMMDDHH24MI') SORT_UKETUKEDATE
                    ,UMV.UKETUKE_TANTOU_CD
          --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
                    ,UMV.UKETUKE_POST_CD
          --2014/06/23 趙雲剛 ADD End
                  FROM UMV_RAITEN_UKETUKE UMV
                  --2014/06/23 趙雲剛 DEL Start (案件C38117)セールスポイント編集機能の追加
                  --  ,TOKEN.WM_TANTOU    WMT
                  --2014/06/23 趙雲剛 DEL End
                 WHERE UMV.KAISYA_CD = PARAM_KAISYACD
                   AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
                   --2014/06/23 趙雲剛 DEL Start (案件C38117)セールスポイント編集機能の追加
                   --AND UMV.KAISYA_CD = WMT.KAISYA_CD
                   --AND UMV.CHUKAI_TANTOU_CD = WMT.TANTOU_CD
                   --2014/06/23 趙雲剛 DEL End
                   --2017/008/04 MJEC)長倉 ADD Start (案件C38101-1)ホームメイト自動受付対応
                   --受付情報作成区分が'0'の場合
                   AND UMV.SAKUSEI_KB = '0'
                   --2017/08/04 MJEC)長倉 ADD End
                   --2019/08/15 SYS_カク年昇 UPD START (案件C43100)お客様項目の入力簡素化対応
                   --AND (UMV.KOKYAKU_INFO_FLG = '0' OR (UMV.ANKETO_FLG = '0' AND UMV.UKETUKE_KB = '1') OR
                   AND (UMV.KOKYAKU_INFO_FLG = '0' OR  
                   --2019/08/15 SYS_カク年昇 UPD END (案件C43100)お客様項目の入力簡素化対応
                     UMV.KIBOU_FLG = '0')
                 ORDER BY SORT_UKETUKEDATE DESC
                     ,UMV.UKETUKE_NO   DESC) T)
         WHERE ROWINDEX <= 5
         ORDER BY SORT_UKETUKEDATE DESC
             ,UKETUKE_NO       DESC;
  --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
  ELSE
      --未登録データ件数
      BEGIN
        SELECT COUNT(DISTINCT(UMV.UKETUKE_NO))
          INTO OUT_MITOROKUCOUNT
          FROM UMV_RAITEN_UKETUKE UMV
            ,TOKEN.WM_POST    WP
         WHERE UMV.KAISYA_CD = PARAM_KAISYACD
           AND UMV.KAISYA_CD = WP.KAISYA_CD
           AND UMV.UKETUKE_POST_CD = WP.POST_CD
           AND ((PARAM_AUTHORITY_CD = '1' AND WP.SISYA_BLK_CD = PARAM_SISYA_BLK_CD) OR PARAM_AUTHORITY_CD = '2') 
           AND WP.OUTPUT_ORDER <> '999'
           AND WP.OUTPUT_ORDER IS NOT NULL
           AND WP.LOGICAL_DEL_FLG = '0'
           --2017/008/04 MJEC)長倉 ADD Start (案件C38101-1)ホームメイト自動受付対応
           --受付情報作成区分が'0'の場合
           AND UMV.SAKUSEI_KB = '0'
           --2017/08/04 MJEC)長倉 ADD End
           --2019/08/15 SYS_カク年昇 UPD START (案件C43100)お客様項目の入力簡素化対応
           --AND (UMV.KOKYAKU_INFO_FLG = '0' OR (UMV.ANKETO_FLG = '0' AND UMV.UKETUKE_KB = '1') OR
           AND (UMV.KOKYAKU_INFO_FLG = '0' OR  
           --2019/08/15 SYS_カク年昇 UPD END (案件C43100)お客様項目の入力簡素化対応
             UMV.KIBOU_FLG = '0');
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          OUT_MITOROKUCOUNT := 0;
      END;
      --未登録データデータを取得
      OPEN OUT_MITOROKUINFO FOR
        SELECT DISTINCT (UKETUKE_NO) AS UKETUKE_NO
                 ,UKETUKE_DATE
                 ,UKETUKE_TIME
                 ,SORT_UKETUKEDATE
                 ,UKETUKE_TANTOU_CD
                 ,UKETUKE_POST_CD
          FROM (SELECT ROWNUM ROWINDEX
                ,T.*
              FROM (SELECT UMV.UKETUKE_NO
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYY/MM/DD') UKETUKE_DATE
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'HH24:MI') UKETUKE_TIME
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMMDDHH24MI') SORT_UKETUKEDATE
                    ,UMV.UKETUKE_TANTOU_CD
                    ,UMV.UKETUKE_POST_CD
                  FROM UMV_RAITEN_UKETUKE UMV
                    ,TOKEN.WM_POST    WP
                 WHERE UMV.KAISYA_CD = PARAM_KAISYACD
                   AND UMV.KAISYA_CD = WP.KAISYA_CD
                   AND UMV.UKETUKE_POST_CD = WP.POST_CD
                   --2017/008/04 MJEC)長倉 ADD Start (案件C38101-1)ホームメイト自動受付対応
                   --受付情報作成区分が'0'の場合
                   AND UMV.SAKUSEI_KB = '0'
                   --2017/08/04 MJEC)長倉 ADD End
                   --2019/08/15 SYS_カク年昇 UPD START (案件C43100)お客様項目の入力簡素化対応
                   --AND (UMV.KOKYAKU_INFO_FLG = '0' OR (UMV.ANKETO_FLG = '0' AND UMV.UKETUKE_KB = '1') OR
                   AND (UMV.KOKYAKU_INFO_FLG = '0' OR  
                   --2019/08/15 SYS_カク年昇 UPD END (案件C43100)お客様項目の入力簡素化対応
                     UMV.KIBOU_FLG = '0')
                   AND ((PARAM_AUTHORITY_CD = '1' AND WP.SISYA_BLK_CD = PARAM_SISYA_BLK_CD) OR PARAM_AUTHORITY_CD = '2') 
                   AND WP.OUTPUT_ORDER <> '999'
                   AND WP.OUTPUT_ORDER IS NOT NULL
                   AND WP.LOGICAL_DEL_FLG = '0'
                 ORDER BY SORT_UKETUKEDATE DESC
                     ,UMV.UKETUKE_NO   DESC) T)
         WHERE ROWINDEX <= 5
         ORDER BY SORT_UKETUKEDATE DESC
             ,UKETUKE_NO       DESC;

  END IF;
  --2014/06/23 趙雲剛 ADD End
    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GETMITOROKUINFO;
  
--2017/008/04 MJEC)長倉 ADD Start (案件C38101-1)ホームメイト自動受付対応
-- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_GETHOMEMATEINFO
  -- プロシージャ名      ： ホームメイト自動受付データを取得
  -- 引数             ： param_kaisyacd    　　 IN   VARCHAR2     -- 会社コード
  --                 ： param_authority_cd   IN   VARCHAR2    -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
  --                 ： param_sisya_blk_cd   IN   VARCHAR2    -- 支社ブロックコード
  --                 ： param_postcd     　 IN   VARCHAR2        　 -- 店舗コード
  --                 ： out_mitorokucount 　OUT  NUMBER     　　　　-- ホームメイト自動受付データ件数
  --                 ： out_mitorokuinfo 　OUT  typCSR     　　　　-- ホームメイト自動受付データ
  --                 ： rst_msg    　　　OUT  VARCHAR2  　　-- エラーメッセージ
  --                 ： rst_code  　　 OUT  VARCHAR2  　-- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要          ： ホームメイト自動受付データを取得
  -- 初版作成日/作成者 ： 2017/08/04 Ver.1.0.0 MJEC)長倉 (案件C38101-1)ホームメイト自動受付対応 
  -- ---------------------------------------------------------
  PROCEDURE PRO_GETHOMEMATEINFO
  (
    PARAM_KAISYACD    IN VARCHAR2 -- 会社コード
     ,PARAM_AUTHORITY_CD IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
     ,PARAM_SISYA_BLK_CD IN VARCHAR2 -- 支社ブロックコード
     ,PARAM_POSTCD      IN VARCHAR2 -- 店舗コード
     ,OUT_HOMEMATECOUNT OUT NUMBER -- ホームメイト自動受付データ件数
     ,OUT_HOMEMATEINFO  OUT TYPCSR -- ホームメイト自動受付データ
     ,rst_msg           OUT VARCHAR2 -- エラーメッセージ
     ,rst_code          OUT VARCHAR2 -- メッセージID
  ) IS

  BEGIN
  --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
  IF PARAM_POSTCD IS NOT NULL THEN
  --2014/06/23 趙雲剛 ADD End
      --ホームメイト自動受付データ件数
      BEGIN
        SELECT COUNT(DISTINCT(UMV.UKETUKE_NO))
          INTO OUT_HOMEMATECOUNT
          FROM UMV_RAITEN_UKETUKE UMV
         WHERE UMV.KAISYA_CD = PARAM_KAISYACD
           AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
           AND UMV.SAKUSEI_KB IN ('1','2')
           AND UMV.CHUKAI_TANTOU_CD IS NULL;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          OUT_HOMEMATECOUNT := 0;
      END;

      --ホームメイト自動受付データを取得
      OPEN OUT_HOMEMATEINFO FOR
        SELECT DISTINCT (UKETUKE_NO) AS UKETUKE_NO
                 ,UKETUKE_DATE
                 ,UKETUKE_TIME
                 ,SORT_UKETUKEDATE
                 ,UKETUKE_TANTOU_CD
                 ,UKETUKE_POST_CD
                 ,SAKUSEI_KB
          FROM (SELECT ROWNUM ROWINDEX
                ,T.*
              FROM (SELECT UMV.UKETUKE_NO
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYY/MM/DD') UKETUKE_DATE
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'HH24:MI') UKETUKE_TIME
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMMDDHH24MI') SORT_UKETUKEDATE
                    ,UMV.UKETUKE_TANTOU_CD
                    ,UMV.UKETUKE_POST_CD
                    ,UMV.SAKUSEI_KB
                  FROM UMV_RAITEN_UKETUKE UMV
                 WHERE UMV.KAISYA_CD = PARAM_KAISYACD
                   AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
                   AND UMV.SAKUSEI_KB IN ('1','2')
                   AND UMV.CHUKAI_TANTOU_CD IS NULL
                 ORDER BY SORT_UKETUKEDATE DESC
                     ,UMV.UKETUKE_NO   DESC) T)
         WHERE ROWINDEX <= 5
         ORDER BY SORT_UKETUKEDATE DESC
             ,UKETUKE_NO       DESC;
  ELSE
      --ホームメイト自動登録データ件数
      BEGIN
        SELECT COUNT(DISTINCT(UMV.UKETUKE_NO))
          INTO OUT_HOMEMATECOUNT
          FROM UMV_RAITEN_UKETUKE UMV
            ,TOKEN.WM_POST    WP
         WHERE UMV.KAISYA_CD = PARAM_KAISYACD
           AND UMV.KAISYA_CD = WP.KAISYA_CD
           AND UMV.UKETUKE_POST_CD = WP.POST_CD
           AND ((PARAM_AUTHORITY_CD = '1' AND WP.SISYA_BLK_CD = PARAM_SISYA_BLK_CD) OR PARAM_AUTHORITY_CD = '2') 
           AND WP.OUTPUT_ORDER <> '999'
           AND WP.OUTPUT_ORDER IS NOT NULL
           AND WP.LOGICAL_DEL_FLG = '0'
           AND UMV.SAKUSEI_KB IN ('1','2')
           AND UMV.CHUKAI_TANTOU_CD IS NULL;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          OUT_HOMEMATECOUNT := 0;
      END;
      --ホームメイト自動受付データを取得
      OPEN OUT_HOMEMATEINFO FOR
        SELECT DISTINCT (UKETUKE_NO) AS UKETUKE_NO
                 ,UKETUKE_DATE
                 ,UKETUKE_TIME
                 ,SORT_UKETUKEDATE
                 ,UKETUKE_TANTOU_CD
                 ,UKETUKE_POST_CD
                 ,SAKUSEI_KB
          FROM (SELECT ROWNUM ROWINDEX
                ,T.*
              FROM (SELECT UMV.UKETUKE_NO
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYY/MM/DD') UKETUKE_DATE
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'HH24:MI') UKETUKE_TIME
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMMDDHH24MI') SORT_UKETUKEDATE
                    ,UMV.UKETUKE_TANTOU_CD
                    ,UMV.UKETUKE_POST_CD
                    ,UMV.SAKUSEI_KB
                  FROM UMV_RAITEN_UKETUKE UMV
                    ,TOKEN.WM_POST    WP
                 WHERE UMV.KAISYA_CD = PARAM_KAISYACD
                   AND UMV.KAISYA_CD = WP.KAISYA_CD
                   AND UMV.UKETUKE_POST_CD = WP.POST_CD
                   AND UMV.SAKUSEI_KB IN ('1','2')
                   AND UMV.CHUKAI_TANTOU_CD IS NULL
                   AND ((PARAM_AUTHORITY_CD = '1' AND WP.SISYA_BLK_CD = PARAM_SISYA_BLK_CD) OR PARAM_AUTHORITY_CD = '2') 
                   AND WP.OUTPUT_ORDER <> '999'
                   AND WP.OUTPUT_ORDER IS NOT NULL
                   AND WP.LOGICAL_DEL_FLG = '0'
                 ORDER BY SORT_UKETUKEDATE DESC
                     ,UMV.UKETUKE_NO   DESC) T)
         WHERE ROWINDEX <= 5
         ORDER BY SORT_UKETUKEDATE DESC
             ,UKETUKE_NO       DESC;

  END IF;
    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GETHOMEMATEINFO;
 --2017/008/04 MJEC)長倉 ADD End

  -- 2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
  -- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_GETHOUJINUKETSUKEDATA
  -- プロシージャ名      ： 検索用の法人仲介受付データを取得
  -- 引数             ： param_kaisyacd    　IN   VARCHAR2        -- 会社コード
  --                 ： param_shownum     　IN   VARCHAR2        -- つづきを表示件数
  --                 ： param_sisya_blk_cd IN   VARCHAR2        -- 支社ブロックコード          
  --                 ： out_uketsukecount 　OUT  NUMBER     　　　　 -- 受付データ件数
  --                 ： out_uketsukeinfo 　 OUT  typCSR     　　　　 -- 受付データ
  --                 ： rst_msg    　　　    OUT  VARCHAR2  　　     -- エラーメッセージ
  --                 ： rst_code  　　      OUT  VARCHAR2  　      -- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要          ： 法人仲介受付データを取得
  -- 初版作成日/作成者  ： 2017/09/14 Ver.1.0.0 SYS_張虎強
  -- 変更情報        ：[修 正] by 劉恒毅(SYS) 2020/08/20 C43040-2_法人の入力項目追加対応
  -- ---------------------------------------------------------
  PROCEDURE PRO_GETHOUJINUKETSUKEDATA
  (
    PARAM_KAISYACD    IN VARCHAR2 -- 会社コード
     ,PARAM_SHOWNUM    IN NUMBER   -- つづきを表示件数
     ,PARAM_SISYA_BLK_CD IN VARCHAR2 -- 支社ブロックコード
     ,PARAM_AUTHORITY_CD IN VARCHAR2 --ユーザー権限コード
     ,PARAM_POSTCD      IN VARCHAR2 -- 店舗コード
     ,OUT_UKETSUKECOUNT OUT NUMBER -- 法人仲介受付データ件数
     ,OUT_UKETSUKEINFO  OUT TYPCSR -- 法人仲介受付データ
     ,rst_msg           OUT VARCHAR2 -- エラーメッセージ
     ,rst_code          OUT VARCHAR2 -- メッセージID
  ) IS
  ROWINDEXNUM   NUMBER;
  BEGIN
    IF PARAM_POSTCD IS NOT NULL THEN 
      BEGIN
        SELECT COUNT(DISTINCT(UMV.UKETUKE_NO))
          INTO OUT_UKETSUKECOUNT
          FROM UMV_RAITEN_UKETUKE UMV
         INNER JOIN TOKEN.WM_POST WMP
            ON UMV.KAISYA_CD = WMP.KAISYA_CD
           AND UMV.UKETUKE_POST_CD = WMP.POST_CD
         WHERE UMV.KAISYA_CD = PARAM_KAISYACD
           AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
           AND (UMV.CHUKAI_TANTOU_CD IS NULL OR UMV.KIBOU_FLG = '0')
           --ADD 劉恒毅 START 2020/08/20 C43040-2_法人の入力項目追加対応
           AND UMV.SINCHOKU_CD = '0'
           --ADD 劉恒毅 END 2020/08/20 C43040-2_法人の入力項目追加対応
           AND UMV.SAKUSEI_KB = '4'
           AND WMP.LOGICAL_DEL_FLG = '0';
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          OUT_UKETSUKECOUNT := 0;
      END;
      IF PARAM_SHOWNUM <> 0 THEN
        ROWINDEXNUM := PARAM_SHOWNUM;
      ELSE
        ROWINDEXNUM := 3;
      END IF;
      --法人仲介受付データを取得
      OPEN OUT_UKETSUKEINFO FOR
        SELECT DISTINCT (UKETUKE_NO) AS UKETUKE_NO
                 ,UKETUKE_DATE
                 ,UKETUKE_TIME
                 ,SORT_UKETUKEDATE
                 ,UKETUKE_TANTOU_CD
                 ,UKETUKE_POST_CD
          FROM (SELECT ROWNUM ROWINDEX
                ,T.*
              FROM (SELECT UMV.UKETUKE_NO
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYY/MM/DD') UKETUKE_DATE
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'HH24:MI') UKETUKE_TIME
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMMDDHH24MI') SORT_UKETUKEDATE
                    ,UMV.UKETUKE_TANTOU_CD
                    ,UMV.UKETUKE_POST_CD
                  FROM UMV_RAITEN_UKETUKE UMV
                 INNER JOIN TOKEN.WM_POST WMP
                    ON UMV.KAISYA_CD = WMP.KAISYA_CD
                   AND UMV.UKETUKE_POST_CD = WMP.POST_CD
                 WHERE UMV.KAISYA_CD = PARAM_KAISYACD
                   AND UMV.UKETUKE_POST_CD = PARAM_POSTCD
                   AND (UMV.CHUKAI_TANTOU_CD IS NULL OR UMV.KIBOU_FLG = '0')
                   --ADD 劉恒毅 START 2020/08/20 C43040-2_法人の入力項目追加対応
                   AND UMV.SINCHOKU_CD = '0'
                   --ADD 劉恒毅 END 2020/08/20 C43040-2_法人の入力項目追加対応
                   AND UMV.SAKUSEI_KB = '4'
                   AND WMP.LOGICAL_DEL_FLG = '0'
                 ORDER BY SORT_UKETUKEDATE DESC
                     ,UMV.UKETUKE_NO   DESC) T)
         WHERE ROWINDEX <= ROWINDEXNUM
         ORDER BY SORT_UKETUKEDATE DESC
             ,UKETUKE_NO       DESC;
    ELSE
      BEGIN
        SELECT COUNT(DISTINCT(UMV.UKETUKE_NO))
          INTO OUT_UKETSUKECOUNT
          FROM UMV_RAITEN_UKETUKE UMV
         INNER JOIN TOKEN.WM_POST WMP
            ON UMV.KAISYA_CD = WMP.KAISYA_CD
           AND UMV.UKETUKE_POST_CD = WMP.POST_CD
         WHERE UMV.KAISYA_CD = PARAM_KAISYACD
           AND ((PARAM_AUTHORITY_CD = '1' AND WMP.SISYA_BLK_CD = PARAM_SISYA_BLK_CD) OR PARAM_AUTHORITY_CD = '2')
           AND (UMV.CHUKAI_TANTOU_CD IS NULL OR UMV.KIBOU_FLG = '0')
           --ADD 劉恒毅 START 2020/08/20 C43040-2_法人の入力項目追加対応
           AND UMV.SINCHOKU_CD = '0'
           --ADD 劉恒毅 END 2020/08/20 C43040-2_法人の入力項目追加対応
           AND UMV.SAKUSEI_KB = '4'
           AND WMP.OUTPUT_ORDER <> '999'
           AND WMP.OUTPUT_ORDER IS NOT NULL
           AND WMP.LOGICAL_DEL_FLG = '0';
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
          OUT_UKETSUKECOUNT := 0;
      END;
      IF PARAM_SHOWNUM <> 0 THEN
        ROWINDEXNUM := PARAM_SHOWNUM;
      ELSE
        ROWINDEXNUM := 3;
      END IF;
      --法人仲介受付データを取得
      OPEN OUT_UKETSUKEINFO FOR
        SELECT DISTINCT (UKETUKE_NO) AS UKETUKE_NO
                 ,UKETUKE_DATE
                 ,UKETUKE_TIME
                 ,SORT_UKETUKEDATE
                 ,UKETUKE_TANTOU_CD
                 ,UKETUKE_POST_CD
          FROM (SELECT ROWNUM ROWINDEX
                ,T.*
              FROM (SELECT UMV.UKETUKE_NO
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYY/MM/DD') UKETUKE_DATE
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'HH24:MI') UKETUKE_TIME
                    ,TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMMDDHH24MI') SORT_UKETUKEDATE
                    ,UMV.UKETUKE_TANTOU_CD
                    ,UMV.UKETUKE_POST_CD
                  FROM UMV_RAITEN_UKETUKE UMV
                 INNER JOIN TOKEN.WM_POST WMP
                    ON UMV.KAISYA_CD = WMP.KAISYA_CD
                   AND UMV.UKETUKE_POST_CD = WMP.POST_CD
                 WHERE UMV.KAISYA_CD = PARAM_KAISYACD
                   AND (UMV.CHUKAI_TANTOU_CD IS NULL OR UMV.KIBOU_FLG = '0')
                   --ADD 劉恒毅 START 2020/08/20 C43040-2_法人の入力項目追加対応
                   AND UMV.SINCHOKU_CD = '0'
                   --ADD 劉恒毅 END 2020/08/20 C43040-2_法人の入力項目追加対応
                   AND UMV.SAKUSEI_KB = '4'
                   AND ((PARAM_AUTHORITY_CD = '1' AND WMP.SISYA_BLK_CD = PARAM_SISYA_BLK_CD) OR PARAM_AUTHORITY_CD = '2')
                   AND WMP.OUTPUT_ORDER <> '999'
                   AND WMP.OUTPUT_ORDER IS NOT NULL
                   AND WMP.LOGICAL_DEL_FLG = '0'
                 ORDER BY SORT_UKETUKEDATE DESC
                     ,UMV.UKETUKE_NO   DESC) T)
         WHERE ROWINDEX <= ROWINDEXNUM
         ORDER BY SORT_UKETUKEDATE DESC
             ,UKETUKE_NO       DESC;
    END IF;
      rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GETHOUJINUKETSUKEDATA;
  --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応

  -- 2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
  -- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_GETHOUJINIRAIINFO
  -- プロシージャ名      ： 法人仲介依頼状況データを取得
  -- 引数             ： param_kaisyacd    　IN   VARCHAR2        -- 会社コード
  --                 ： param_sisya_blk_cd  IN VARCHAR2         -- 支社ブロックコード
  --                 ： param_authority_cd  IN   VARCHAR2       -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者 3:法人) 
  --                 ： param_tdate         IN   VARCHAR2       -- 日付
  --                 ： out_uketsukecount 　OUT  NUMBER     　　　　 -- 法人仲介依頼件数
  --                 ： rst_msg    　　　    OUT  VARCHAR2  　　     -- エラーメッセージ
  --                 ： rst_code  　　      OUT  VARCHAR2  　      -- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要          ： 法人仲介依頼状況データを取得
  -- 初版作成日/作成者  ： 2017/09/14 Ver.1.0.0 SYS_張虎強
  -- ---------------------------------------------------------
  PROCEDURE PRO_GETHOUJINIRAIINFO
  (
    PARAM_KAISYACD        IN VARCHAR2 -- 会社コード
     ,PARAM_SISYABLKCD    IN VARCHAR2 -- 支社ブロックコード
     ,PARAM_AUTHORITY_CD  IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者 3:法人) 
     ,PARAM_TDATE         IN VARCHAR2 -- 日付
     ,OUT_HOUJINIRAICOUNT  OUT NUMBER   -- 法人仲介依頼件数
     ,rst_msg            OUT VARCHAR2 -- エラーメッセージ
     ,rst_code           OUT VARCHAR2 -- メッセージID
  ) IS
  BEGIN
    IF PARAM_AUTHORITY_CD = '1' THEN
      BEGIN
        SELECT COUNT(DISTINCT UMVB.UKETUKE_NO)
          INTO OUT_HOUJINIRAICOUNT
          FROM TOKEN.WM_POST WP
          JOIN UMV_RAITEN_UKETUKE UMVA
            ON (WP.KAISYA_CD = UMVA.KAISYA_CD AND
                WP.POST_CD = UMVA.UKETUKE_POST_CD)
          JOIN UMV_RAITEN_UKETUKE UMVB
            ON (UMVA.KAISYA_CD = UMVB.KAISYA_CD AND 
                UMVA.HOUJIN_IRAIMOTO_UKETUKE_NO = UMVB.UKETUKE_NO)
         WHERE WP.KAISYA_CD = PARAM_KAISYACD
           AND WP.SISYA_BLK_CD = PARAM_SISYABLKCD
           AND WP.OUTPUT_ORDER <> '999'
           AND WP.OUTPUT_ORDER IS NOT NULL
           AND WP.LOGICAL_DEL_FLG = '0'
           AND UMVA.SAKUSEI_KB = '4'
           AND TO_CHAR(UMVB.UKETUKE_DATE, 'YYYYMM') = PARAM_TDATE
           AND UMVB.SAKUSEI_KB = '3';
      EXCEPTION
         WHEN NO_DATA_FOUND THEN
           OUT_HOUJINIRAICOUNT := 0;
      END;
    ELSE
      BEGIN
        SELECT COUNT(UMV.UKETUKE_NO)
          INTO OUT_HOUJINIRAICOUNT
          FROM UMV_RAITEN_UKETUKE UMV
         WHERE UMV.KAISYA_CD = PARAM_KAISYACD
           AND TO_CHAR(UMV.UKETUKE_DATE, 'YYYYMM') = PARAM_TDATE
           AND UMV.SAKUSEI_KB = '3';
      EXCEPTION
         WHEN NO_DATA_FOUND THEN
           OUT_HOUJINIRAICOUNT := 0;
      END;
    END IF;
         
    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GETHOUJINIRAIINFO;
  --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応

  -- 2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
  -- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_GETHOUJINIRAIDATA
  -- プロシージャ名      ： 法人仲介依頼状況データを取得
  -- 引数             ： param_kaisyacd    　IN   VARCHAR2          -- 会社コード
  --                 ： param_sisya_blk_cd  IN VARCHAR2           -- 支社ブロックコード
  --                 ： param_authority_cd  IN   VARCHAR2         -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者 3:法人) 
  --                 ： param_tdate         IN   VARCHAR2         -- 日付
  --                 ： out_houjiniraifdata 　OUT  TYPCSR     　　　　 -- 法人仲介依頼件数
  --                 ： rst_msg    　　　      OUT  VARCHAR2  　　     -- エラーメッセージ
  --                 ： rst_code  　　        OUT  VARCHAR2  　      -- メッセージID
  -- 戻り値           ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要          ： 法人仲介依頼状況データを取得
  -- 初版作成日/作成者  ： 2017/09/14 Ver.1.0.0 SYS_張虎強
  -- ---------------------------------------------------------
  PROCEDURE PRO_GETHOUJINIRAIDATA
  (
    PARAM_KAISYACD        IN VARCHAR2 -- 会社コード
     ,PARAM_SISYABLKCD    IN VARCHAR2 -- 支社ブロックコード
     ,PARAM_AUTHORITY_CD  IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者 3:法人) 
     ,PARAM_TDATE         IN VARCHAR2 -- 日付
     ,OUT_HOUJINIRAIDATA   OUT TYPCSR -- 法人仲介担当者データ
     ,rst_msg            OUT VARCHAR2 -- エラーメッセージ
     ,rst_code           OUT VARCHAR2 -- メッセージID
  ) IS
   HOUJINIRAIDATA_SQL         VARCHAR(2000); -- SQL文
   
  BEGIN
    IF PARAM_AUTHORITY_CD = '1' THEN
      BEGIN
        HOUJINIRAIDATA_SQL := 'SELECT COUNT(DISTINCT UMVB.UKETUKE_NO) AS UKETUKE_NO,';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' UMVB.APP_TANTOU_CD AS TANTOU_CD,';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' WMT.TANTOU_NAME AS TANTOU_NAME';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' FROM TOKEN.WM_POST WP';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' JOIN UMV_RAITEN_UKETUKE UMVA';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' ON (WP.KAISYA_CD = UMVA.KAISYA_CD';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' AND WP.POST_CD = UMVA.UKETUKE_POST_CD)';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' JOIN UMV_RAITEN_UKETUKE UMVB';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' ON (UMVA.KAISYA_CD = UMVB.KAISYA_CD AND';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' UMVA.HOUJIN_IRAIMOTO_UKETUKE_NO = UMVB.UKETUKE_NO)';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' JOIN TOKEN.WM_TANTOU WMT';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' ON (WMT.KAISYA_CD = UMVB.KAISYA_CD AND ';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' WMT.TANTOU_CD = UMVB.APP_TANTOU_CD)';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' WHERE WP.KAISYA_CD = '''||PARAM_KAISYACD||'''';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' AND WP.SISYA_BLK_CD = '''||PARAM_SISYABLKCD||'''';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' AND WP.OUTPUT_ORDER <> ''999''';
            HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' AND WP.OUTPUT_ORDER IS NOT NULL';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' AND WP.LOGICAL_DEL_FLG = ''0''';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' AND UMVA.SAKUSEI_KB = ''4''';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' AND TO_CHAR(UMVB.UKETUKE_DATE, ''YYYYMM'') = '''||PARAM_TDATE||'''';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' AND UMVB.SAKUSEI_KB = ''3''';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' GROUP BY UMVB.APP_TANTOU_CD,WMT.TANTOU_NAME';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' ORDER BY UMVB.APP_TANTOU_CD';
        OPEN OUT_HOUJINIRAIDATA FOR HOUJINIRAIDATA_SQL;
        rst_code := MSG_SUCCESS;
      EXCEPTION
        WHEN OTHERS THEN
          rst_code := MSG_UNKNOWN;
          rst_msg  := SQLERRM;
      END;
    ELSE
      BEGIN
        HOUJINIRAIDATA_SQL := 'SELECT COUNT(UMV.UKETUKE_NO) AS UKETUKE_NO,';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' UMV.APP_TANTOU_CD AS TANTOU_CD,';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' WMT.TANTOU_NAME AS TANTOU_NAME';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' FROM UMV_RAITEN_UKETUKE UMV';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' JOIN TOKEN.WM_TANTOU WMT';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' ON (WMT.KAISYA_CD = UMV.KAISYA_CD AND';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' WMT.TANTOU_CD = UMV.APP_TANTOU_CD)';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' WHERE UMV.KAISYA_CD = '''||PARAM_KAISYACD||'''';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' AND TO_CHAR(UMV.UKETUKE_DATE, ''YYYYMM'') = '''||PARAM_TDATE||'''';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' AND UMV.SAKUSEI_KB = ''3''';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' GROUP BY UMV.APP_TANTOU_CD,WMT.TANTOU_NAME';
        HOUJINIRAIDATA_SQL :=  HOUJINIRAIDATA_SQL || ' ORDER BY UMV.APP_TANTOU_CD';
        OPEN OUT_HOUJINIRAIDATA FOR HOUJINIRAIDATA_SQL;
        rst_code := MSG_SUCCESS;
      EXCEPTION
        WHEN OTHERS THEN
          rst_code := MSG_UNKNOWN;
          rst_msg  := SQLERRM;
      END;
    END IF;
         
    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GETHOUJINIRAIDATA;
  --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応

  -- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_KOKYAKUINFO
  -- プロシージャ名      ： 商談中の顧客一覧データ取得
  -- 引数            ： param_kaisyacd   　  IN  VARCHAR2     -- 会社コード
  --                 ： param_postcd     　　IN   VARCHAR2    -- 店舗コード
  --                 ： param_authority_cd   IN   VARCHAR2    -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
  --                 ： param_sisya_blk_cd   IN   VARCHAR2    -- 支社ブロックコード
  --                 ： param_searchitem   IN   NUMBER     　-- 検索項目
  --                 ： param_kanaindex1   IN   VARCHAR2   　　-- カナインデックス1
  --                 ： param_kanaindex2   IN   VARCHAR2   　-- カナインデックス2
  --                 ： param_startindex   IN   NUMBER     　-- STARTデータ行番号
  --                 ： param_endindex     IN   NUMBER   　　　-- ENDデータ行行番号
  --                 ： param_taketucyushiflg IN  VARCHAR2   -- 他決・中止チェックボックスフラグ
  --                 ： param_sortitem     IN   VARCHAR2   　-- ソート項目
  --                 ： out_recordcount    OUT  NUMBER   　　　-- 総件数
  --                 ： out_kokyakuinfo    OUT  typCSR     　-- 商談中の顧客一覧
  --                 ： rst_msg        OUT  VARCHAR2　  　-- エラーメッセージ
  --                 ： rst_code      OUT  VARCHAR2　  　-- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要          ： 商談中の顧客一覧データ取得
  -- 初版作成日/作成者  ： 2011/10/19 Ver.1.0.0 SYS_馮強華
  -- 変更情報          ： 2012/01/13 Ver.1.0.0 SYS_趙雲剛 bug025対応
  --                       ： 2012/01/30 Ver.1.0.0 SYS_馮強華 bug072対応
  --                      ：2012/02/07 Ver.1.0.0 SYS_馮強華 bug176対応
  --                      ：2013/01/25 Ver.1.0.0 SYS_林森 (案件C37058)受付システム要望対応
  --                      ：2013/06/06 Ver.1.0.2 SQLチューニング対応
  --                      ：2014/06/23 Ver.1.0.6 SYS_趙雲剛(案件C38117)セールスポイント編集機能の追加
  --                      ：2015/01/27 Ver.1.0.7 SYS_郭凡(案件C39042)トップページレイアウト修正
  --                      ：2017/08/04 Ver.1.0.8 MJEC)長倉 (案件C41059)中止他決顧客の検索対応
  --                      ：2017/09/13 Ver.1.0.9 SYS_張虎強(案件C41004)法人仲介担当者登録機能追加対応
  --                     ： 2018/01/09 var.1.1.0 SYS_肖剣生(案件C42036_デザイン変更）
  --                     ： 2018/10/22 var.1.1.1 SYS_王一氷（案件C43040-1_法人の入力項目追加対応）
  -- ---------------------------------------------------------
  PROCEDURE PRO_KOKYAKUINFO
  (
    PARAM_KAISYACD   IN VARCHAR2 -- 会社コード
     ,PARAM_POSTCD     IN VARCHAR2 -- 店舗コード
     --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
     ,PARAM_AUTHORITY_CD IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
     ,PARAM_SISYA_BLK_CD IN VARCHAR2 -- 支社ブロックコード
     --2014/06/23 趙雲剛 ADD End
     ,PARAM_SEARCHITEM IN VARCHAR2 -- 検索項目
     --2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
     ,PARAM_WHERETANTOU IN VARCHAR2 -- 検索条件
     --2013/01/25 林森 ADD END
     ,PARAM_KANAINDEX1 IN VARCHAR2 -- カナインデックス1
     ,PARAM_KANAINDEX2 IN VARCHAR2 -- カナインデックス2
     ,PARAM_STARTINDEX IN NUMBER -- STARTデータ行番号
     ,PARAM_ENDINDEX   IN NUMBER -- ENDデータ行行番号
     --2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
     ,PARAM_TAKETUCYUSHIFLG IN VARCHAR2
     --2017/08/04 MJEC)長倉 ADD End
     ,PARAM_SORTITEM   IN VARCHAR2 -- ソート項目
     ,OUT_RECORDCOUNT  OUT NUMBER -- 総件数
     ,OUT_KOKYAKUINFO  OUT TYPCSR -- 商談中の顧客一覧
     --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
     ,OUT_SAKUSEIINFO  OUT TYPCSR  -- 受付情報作成区分の顧客一覧
     --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
     ,rst_msg          OUT VARCHAR2 -- エラーメッセージ
     ,rst_code         OUT VARCHAR2 -- メッセージID
  ) IS
    --2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
    --L_SQL       VARCHAR2(2000); -- SQL文
    --L_COUTSQL   VARCHAR2(1500); -- SQL文
    --L_WHERE     VARCHAR2(1000); -- SQL文検索条件
     --2015/01/27 郭凡 UPD Start (案件C39042)トップページレイアウト修正
    --L_SQL         VARCHAR2(5000); -- SQL文
    L_SQL         VARCHAR2(10000); -- SQL文
    --2015/01/27 郭凡 UPD End
    L_SQL_TANNTOU VARCHAR2(200); -- SQL文
    L_SQL_POST    VARCHAR2(1000); -- SQL文
    L_SQL_TANNTOUCD  VARCHAR2(100); -- SQL文
    L_COUTSQL     VARCHAR2(3500); -- SQL文
    L_WHERE       VARCHAR2(3000); -- SQL文検索条件
    --2014/06/23 趙雲剛 UPD End
    SEARCHITEM  VARCHAR2(20); -- 検索項目
    SEARCHVALUE VARCHAR2(50); -- 検索内容
    --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
    SAKUSEI_KB_STR VARCHAR2(2000);  --受付情報作成区分が'3'の受付№
    H_SQL          VARCHAR2(10000);  -- SQL文
    SAKUSEI_KB_STRA VARCHAR2(2000);
    L_SQL_COUNT  VARCHAR2(200); -- SQL文
    --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
    
  BEGIN
    --商談中の顧客一覧
    --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
    IF PARAM_AUTHORITY_CD <> '3' THEN
    --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
      L_SQL   := 'SELECT * FROM(';
      L_SQL   := L_SQL || ' SELECT ROWNUM ROWINDEX, t.* FROM';
      L_SQL   := L_SQL || ' (SELECT ';
      L_SQL   := L_SQL || '         TO_CHAR(UMV.UKETUKE_DATE,''YYYY/MM/DD'') UKETUKE_DATE '; --表示用受付日
      L_SQL   := L_SQL || '        ,TO_CHAR(UMV.UKETUKE_DATE,''YYYY/MM/DD HH24:MI:SS'') ORD_UKETUKE_DATE '; --ソート用受付日
     --2013/05/16 郭凡 ADD Start (案件C37109-1)受付システムSTEP2要望対応
      L_SQL   := L_SQL || '        ,TO_CHAR(UMV.UPD_DATE,''YYYY/MM/DD HH24:MI:SS'') UPD_DATE '; --更新日付
     --2013/05/16 郭凡 ADD End
      L_SQL   := L_SQL || '        ,UMV.UKETUKE_NO           UKETUKE_NO'; --受付№
      -- 2018/10/22 王一氷 UPD START 案件C43040-1_法人の入力項目追加対応
      --L_SQL   := L_SQL || '        ,UMV.Kokyaku_Name Kokyaku_Name'; --顧客名
      L_SQL   := L_SQL || '        ,(CASE WHEN UMV.Sakusei_Kb  = ''4'' THEN WMK.KIGYO_NAME ELSE UMV.Kokyaku_Name END) AS Kokyaku_Name'; --顧客名
      -- 2018/10/22 王一氷 UPD END 案件C43040-1_法人の入力項目追加対応
      L_SQL   := L_SQL || '        ,UMV.Kokyaku_Sb           Kokyaku_Sb'; --顧客種別
      --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
      L_SQL   := L_SQL || '        ,UMV.UKETUKE_POST_CD      UKETUKE_POST_CD'; --受付店舗コード
      --2014/06/23 趙雲剛 ADD End
      L_SQL   := L_SQL || '        ,UMV.Chukai_Tantou_Cd     Chukai_Tantou_Cd'; --仲介担当者コード
      L_SQL   := L_SQL || '        ,WMT.TANTOU_NAME          Chukai_Tantou_Name'; --仲介担当者名
      L_SQL   := L_SQL || '        ,UMV.Sinchoku_Cd          Sinchoku_Cd'; --進捗状況コード
      L_SQL   := L_SQL || '        ,UMV.Sinchoku_Name        Sinchoku_Name'; --進捗区分名称
      L_SQL   := L_SQL || '        ,UMV.Kokyaku_Ryaku_Name_k Kokyaku_Ryaku_Name_k'; --顧客略称名カナ
      L_SQL   := L_SQL || '        ,UMV.KANA_IDX_1           KANA_IDX_1'; --カナINDEX１
      L_SQL   := L_SQL || '        ,NVL(SUB.NEWCOUNT,0)      SHINKI_KENSU'; --新着物件数
      L_SQL   := L_SQL || '        ,UT.UKETUKE_NO            TUIKYAKU'; --追客アラーム
      L_SQL   := L_SQL || '        ,TO_CHAR(UMV.LAST_TUIKYAKU_DATE,''YYYY/MM/DD'') LAST_TUIKYAKU_DATE'; --最終対客日
       --2015/01/27 郭凡 ADD Start (案件C39042)トップページレイアウト修正
       L_SQL   := L_SQL || '        ,YATIN.KIBOU_JYOUKEN_RIREKI_SEQ SEQ';  
       L_SQL   := L_SQL || '        ,YATIN.KIBOU_BUKKEN_KB KIBOU_BUKKEN_KB';
       L_SQL   := L_SQL || '        ,YATIN.KIBOU_JYOUKEN_SITEI_CD KIBOU_JYOUKEN_SITEI_CD';
       L_SQL   := L_SQL || '        ,PKG_UKG01010.FNC_GET_MADORI_BUKKEN_SB(UMV.KAISYA_CD, UMV.UKETUKE_NO, YATIN.KIBOU_JYOUKEN_RIREKI_SEQ, YATIN.KIBOU_BUKKEN_KB) MADORI';  
       L_SQL   := L_SQL || '        ,PKG_UKG01010.FNC_GET_KIBOU_JYOUKEN(UMV.KAISYA_CD, UMV.UKETUKE_NO, YATIN.KIBOU_JYOUKEN_RIREKI_SEQ, YATIN.KIBOU_JYOUKEN_SITEI_CD) SITEI_AREA';    
       L_SQL   := L_SQL || '        ,UMV.MEMO                          MEMO'; 
       L_SQL   := L_SQL || '        ,YATIN.KIBOU_YATIN_FROM            KIBOU_YATIN_FROM'; 
       L_SQL   := L_SQL || '        ,YATIN.KIBOU_YATIN_TO              KIBOU_YATIN_TO'; 
       L_SQL   := L_SQL || '        ,TO_CHAR(YATIN.KIBOU_NYUKYO_DATE,''YYYY"年"MM"月"'')     KIBOU_NYUKYO_DATE'; 
       --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
       L_SQL   := L_SQL || '        ,UMV.SAKUSEI_KB           SAKUSEI_KB'; --受付情報作成区分
       --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
       --2018/01/09 肖剣生 ADD START (案件C42036デザイン変更）
       L_SQL   := L_SQL || '        ,NVL(TO_CHAR(UMV.SHOUDAN_DATE,''YYYY/MM/DD''), ''　'')  SHOUDAN_DATE'; --最終商談日
       --2018/01/09 肖剣生 ADD END (案件C42036デザイン変更）
       -- 2018/10/22 王一氷 ADD START 案件C43040-1_法人の入力項目追加対応
       L_SQL   := L_SQL || '        ,(CASE WHEN UMV.Sakusei_Kb  = ''4'' THEN UMV.Kokyaku_Name ELSE '''' END ) AS KIGYO_NAME';  --法人仲介契約者
       L_SQL   := L_SQL || '        ,UMV.NYUKYOSYA_NAME                NYUKYOSYA_NAME';  --法人仲介契約者
       -- 2018/10/22 王一氷 ADD END 案件C43040-1_法人の入力項目追加対応
       --2015/01/27 郭凡 ADD End
      L_SQL   := L_SQL || ' FROM UMV_RAITEN_UKETUKE UMV';
      L_SQL   := L_SQL || ' LEFT JOIN UD_AL_TUIKYAKU UT';
      L_SQL   := L_SQL || ' ON (UMV.Kaisya_Cd  = UT.KAISYA_CD AND UMV.UKETUKE_NO = UT.UKETUKE_NO)';
      L_SQL   := L_SQL || ' LEFT JOIN (SELECT US.KAISYA_CD AS KAISYACD';
      L_SQL   := L_SQL || '    ,US.UKETUKE_NO AS UKETUKENO';
      L_SQL   := L_SQL || '    ,COUNT(US.UKETUKE_NO) AS NEWCOUNT';
      L_SQL   := L_SQL || '  FROM UD_SINTYAKU_BUKKEN US';
  --UPD_START 2013.06.06_SQLチューニング対応
  --    L_SQL   := L_SQL || '    ,TOKEN.WD_HEYA      TD';
      L_SQL   := L_SQL || '    ,UD_KUUSITU_HEYA      TD';
      L_SQL   := L_SQL || ' WHERE US.KAISYA_CD = ''' || PARAM_KAISYACD || '''';
  --UPD_END 2013.06.06_SQLチューニング対応
      L_SQL   := L_SQL || ' AND US.HOMEMATE_BUKKEN_NO = TD.HOMEMATE_BUKKEN_NO';
      L_SQL   := L_SQL || '   AND US.HEYA_NO = TD.HEYA_NO';

  --UPD_START 2013.06.06_SQLチューニング対応
  --    L_SQL   := L_SQL || '   AND TD.NYUKYO_SB_CD IN (''1'', ''3'')';
  --    L_SQL   := L_SQL || '   AND TD.LOGICAL_DEL_FLG = ''0''';
      L_SQL   := L_SQL || '   AND TD.NYU_KUU_KB IN (''0'')';
  --UPD_END 2013.06.06_SQLチューニング対応

      L_SQL   := L_SQL || '   AND US.SINTYAKU_AL_KAIYJO_FLG = ''0''';
      L_SQL   := L_SQL || ' GROUP BY US.KAISYA_CD';
      L_SQL   := L_SQL || '     ,US.UKETUKE_NO) SUB';
      L_SQL   := L_SQL || ' ON (UMV.Kaisya_Cd  = SUB.KAISYACD AND UMV.UKETUKE_NO = SUB.UKETUKENO)';
      -- 2018/10/22 王一氷 ADD START 案件C43040-1_法人の入力項目追加対応
      L_SQL   := L_SQL || ' LEFT JOIN TOKEN.WM_KIGYO WMK ON WMK.KIGYO_CD = UMV.HOUJIN_KIGYOU_CD AND WMK.LOGICAL_DEL_FLG = ''0''';
      -- 2018/10/22 王一氷 ADD END 案件C43040-1_法人の入力項目追加対応
     --2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
     --2015/01/27 郭凡 ADD Start (案件C39042)トップページレイアウト修正
      --1-4-1-4.希望家賃の取得
      L_SQL   := L_SQL || 'LEFT JOIN( ';
      L_SQL   := L_SQL || '          SELECT B.KAISYA_CD, ';
      L_SQL   := L_SQL || '                 B.UKETUKE_NO, ';
      L_SQL   := L_SQL || '                 B.KIBOU_JYOUKEN_RIREKI_SEQ, ';
      L_SQL   := L_SQL || '                 B.KIBOU_NYUKYO_DATE AS KIBOU_NYUKYO_DATE, ';
      L_SQL   := L_SQL || '                 B.KIBOU_BUKKEN_KB AS KIBOU_BUKKEN_KB, ';   
      L_SQL   := L_SQL || '                 B.KIBOU_JYOUKEN_SITEI_CD AS KIBOU_JYOUKEN_SITEI_CD, ';
      L_SQL   := L_SQL || '                 CASE ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''1'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''013'', B.KIBOU_YATIN_FROM) ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''2'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''014'', B.KIBOU_YATIN_FROM) ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''3'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''022'', B.KIBOU_YATIN_FROM) ';
      L_SQL   := L_SQL || '                 END AS KIBOU_YATIN_FROM, ';
      L_SQL   := L_SQL || '                 CASE ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''1'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''013'', B.KIBOU_YATIN_TO) ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''2'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''014'', B.KIBOU_YATIN_TO) ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''3'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''022'', B.KIBOU_YATIN_TO) ';
      L_SQL   := L_SQL || '                 END AS KIBOU_YATIN_TO ';
      L_SQL   := L_SQL || '            FROM (SELECT T.KAISYA_CD, ';
      L_SQL   := L_SQL || '                         T.UKETUKE_NO, ';
      L_SQL   := L_SQL || '                         MAX(T.KIBOU_JYOUKEN_RIREKI_SEQ) SEQ ';
      L_SQL   := L_SQL || '                    FROM UD_KIBOU_JYOUKEN T ';
      L_SQL   := L_SQL || '                    WHERE T.LOGICAL_DEL_FLG = ''0'' ';     
      L_SQL   := L_SQL || '                   GROUP BY T.KAISYA_CD, T.UKETUKE_NO) A, ';
      L_SQL   := L_SQL || '                 UD_KIBOU_JYOUKEN B ';
      L_SQL   := L_SQL || '           WHERE A.KAISYA_CD = B.KAISYA_CD ';
      L_SQL   := L_SQL || '             AND A.UKETUKE_NO = B.UKETUKE_NO ';
      L_SQL   := L_SQL || '            AND A.SEQ = B.KIBOU_JYOUKEN_RIREKI_SEQ '; 
      L_SQL   := L_SQL || ') YATIN ';
      L_SQL   := L_SQL || 'ON (UMV.KAISYA_CD  = YATIN.KAISYA_CD AND UMV.UKETUKE_NO = YATIN.UKETUKE_NO) ';
     --2015/01/27 郭凡 ADD End
      --L_SQL   := L_SQL || ' ,TOKEN.WM_TANTOU WMT';
      L_SQL_TANNTOU   := ' LEFT JOIN TOKEN.WM_TANTOU WMT ON (UMV.KAISYA_CD  = WMT.KAISYA_CD AND UMV.CHUKAI_TANTOU_CD  = WMT.TANTOU_CD) ';
      L_SQL_POST := '';
      IF  PARAM_AUTHORITY_CD = '1' OR PARAM_AUTHORITY_CD = '2' THEN
        L_SQL_POST := L_SQL_POST || ' JOIN TOKEN.WM_POST WMP ON (';
        L_SQL_POST := L_SQL_POST || ' UMV.KAISYA_CD  = WMP.KAISYA_CD';
        L_SQL_POST := L_SQL_POST || ' AND UMV.UKETUKE_POST_CD  = WMP.POST_CD';
        L_SQL_POST := L_SQL_POST || ' AND WMP.OUTPUT_ORDER <> ''999''';
        L_SQL_POST := L_SQL_POST || ' AND WMP.OUTPUT_ORDER IS NOT NULL';
        L_SQL_POST := L_SQL_POST || ' AND WMP.LOGICAL_DEL_FLG = ''0'' ';
        IF  PARAM_AUTHORITY_CD = '1' THEN
        L_SQL_POST := L_SQL_POST || ' AND WMP.SISYA_BLK_CD = ''' || PARAM_SISYA_BLK_CD || '''';
        END IF;
        L_SQL_POST := L_SQL_POST || ' ) ';
      END IF;
    --2014/06/23 趙雲剛 UPD End
      L_WHERE := L_WHERE || ' WHERE UMV.Kaisya_Cd = ''' || PARAM_KAISYACD || '''';
      
     --2018/03/02 H.Sato UPD START
      ----2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
      --IF PARAM_AUTHORITY_CD = '0' THEN
      --  L_WHERE := L_WHERE || ' AND (UMV.SAKUSEI_KB <> ''3'' OR UMV.SAKUSEI_KB IS NULL) ';
      --END IF;
      ----2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
        L_WHERE := L_WHERE || ' AND UMV.SAKUSEI_KB <> ''3''';
      --2018/03/02 H.Sato UPD END

      --2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加
      --L_WHERE := L_WHERE || ' AND UMV.Uketuke_Post_Cd = ''' || PARAM_POSTCD || '''';
      IF PARAM_POSTCD IS NOT NULL THEN
          L_WHERE := L_WHERE || ' AND UMV.Uketuke_Post_Cd = ''' || PARAM_POSTCD || '''';
      END IF;
      --2014/06/23 趙雲剛 UPD End
      
      --2017/10/31 張虎強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
      --L_WHERE := L_WHERE || ' AND UMV.SINCHOKU_CD IN(''1'',''2'',''3'') ';
      --2018/03/02 H.Sato DEL START
      --IF PARAM_AUTHORITY_CD = '1' OR PARAM_AUTHORITY_CD = '2' THEN
      --  IF PARAM_SINCHOKUFLG = '1' THEN
      --    L_WHERE := L_WHERE || ' AND ((UMV.SAKUSEI_KB = ''4'' AND UMV.SINCHOKU_CD IN(''1'',''2'',''3'',''6'',''7'')) OR (UMV.SAKUSEI_KB = ''3'' AND UMV.SINCHOKU_CD = ''0''))';
      --  ELSE
      --    L_WHERE := L_WHERE || ' AND ((UMV.SAKUSEI_KB = ''4'' AND UMV.SINCHOKU_CD IN(''1'',''2'',''3'')) OR (UMV.SAKUSEI_KB = ''3'' AND UMV.SINCHOKU_CD = ''0''))';
      --  END IF;
      --ELSE
      --2018/03/02 H.Sato DEL END
      IF PARAM_TAKETUCYUSHIFLG = '1' THEN
        L_WHERE := L_WHERE || ' AND UMV.SINCHOKU_CD IN(''1'',''2'',''3'',''6'',''7'') ';
      ELSE
        L_WHERE := L_WHERE || ' AND UMV.SINCHOKU_CD IN(''1'',''2'',''3'') ';
      END IF;
      --2018/03/02 H.Sato DEL START
      --END IF;
      --2018/03/02 H.Sato DEL END

      --2017/10/31 張虎強 UPD END (案件C41004)法人仲介担当者登録機能追加対応
      
      --2014/06/23 趙雲剛 DEL Start (案件C38117)セールスポイント編集機能の追加
      --L_WHERE := L_WHERE || ' AND UMV.KAISYA_CD  = WMT.KAISYA_CD';
      --L_WHERE := L_WHERE || ' AND UMV.CHUKAI_TANTOU_CD  = WMT.TANTOU_CD';
      --2014/06/23 趙雲剛 DEL End
      IF PARAM_SEARCHITEM IS NOT NULL
      THEN
        SEARCHITEM  := SUBSTR(PARAM_SEARCHITEM, 0, INSTR(PARAM_SEARCHITEM, '$', 1, 1) - 1);
        SEARCHVALUE := SUBSTR(PARAM_SEARCHITEM, INSTR(PARAM_SEARCHITEM, '$', 1, 1) + 1, LENGTH(PARAM_SEARCHITEM));

        CASE
        --受付No.検索
          WHEN SEARCHITEM = 'UKETUKE_NO' THEN
            L_WHERE := L_WHERE || '  AND UMV.UKETUKE_NO LIKE ''' || SEARCHVALUE || '%''';
            --顧客名検索
          WHEN SEARCHITEM = 'KOKYAKU_NAME' THEN
            -- 2018/10/22 王一氷 UPD START 案件C43040-1_法人の入力項目追加対応
            --L_WHERE := L_WHERE || ' AND UMV.KOKYAKU_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || ' AND ( UMV.KOKYAKU_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  OR UMV.NYUKYOSYA_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  OR WMK.KIGYO_NAME LIKE ''%' || SEARCHVALUE || '%'')';
            -- 2018/10/22 王一氷 UPD END 案件C43040-1_法人の入力項目追加対応
            --顧客名カナ検索
          WHEN SEARCHITEM = 'KOKYAKU_NAME_K' THEN
            L_WHERE := L_WHERE || ' AND UMV.KOKYAKU_NAME_K LIKE ''%' || SEARCHVALUE || '%''';
            --法人検索
          WHEN SEARCHITEM = 'KOKYAKU_SB1' THEN
            -- 2018/10/22 王一氷 UPD START 案件C43040-1_法人の入力項目追加対応
            --L_WHERE := L_WHERE || ' AND UMV.KOKYAKU_NAME LIKE ''%' || SEARCHVALUE || '%''';;
            L_WHERE := L_WHERE || ' AND ( UMV.KOKYAKU_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  OR UMV.NYUKYOSYA_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  OR WMK.KIGYO_NAME LIKE ''%' || SEARCHVALUE || '%'')';
            -- 2018/10/22 王一氷 UPD END 案件C43040-1_法人の入力項目追加対応
            L_WHERE := L_WHERE || '  AND UMV.KOKYAKU_SB = ''2''';
            --個人検索
          WHEN SEARCHITEM = 'KOKYAKU_SB2' THEN
            L_WHERE := L_WHERE || ' AND UMV.KOKYAKU_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  AND UMV.KOKYAKU_SB = ''1''';
            --法人全検索
          WHEN SEARCHITEM = 'KOKYAKU_SB1_ALL' THEN
            L_WHERE := L_WHERE || '  AND UMV.KOKYAKU_SB = ''2''';
            --個人全検索
          WHEN SEARCHITEM = 'KOKYAKU_SB2_ALL' THEN
            L_WHERE := L_WHERE || '  AND UMV.KOKYAKU_SB = ''1''';
            --電話番号検索
          WHEN SEARCHITEM = 'TEL_NO' THEN
            L_WHERE := L_WHERE || ' AND ( UMV.KENSAKU_TEL_NO LIKE ''' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  OR UMV.KENSAKU_KEITAI_NO LIKE ''' || SEARCHVALUE || '%'')';
        END CASE;
      END IF;

      --2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
      IF PARAM_WHERETANTOU IS NOT NULL
      THEN
        SEARCHITEM  := SUBSTR(PARAM_WHERETANTOU, 0, INSTR(PARAM_WHERETANTOU, '$', 1, 1) - 1);
        SEARCHVALUE := SUBSTR(PARAM_WHERETANTOU, INSTR(PARAM_WHERETANTOU, '$', 1, 1) + 1, LENGTH(PARAM_WHERETANTOU));
         CASE
          --担当者検索
          WHEN SEARCHITEM = 'TANTOU_CD' THEN
            --2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加  
            --L_WHERE := L_WHERE || '  AND WMT.TANTOU_CD = ''' || SEARCHVALUE || '''';
            L_SQL_TANNTOUCD := '  AND WMT.TANTOU_CD = ''' || SEARCHVALUE || '''';
            --2014/06/23 趙雲剛 UPD End
         END CASE;
      END IF;
      --2013/01/25 林森 ADD END

      -- カナインデックス1
      IF PARAM_KANAINDEX1 IS NOT NULL
         AND PARAM_KANAINDEX1 != '全て'
      THEN
        L_WHERE := L_WHERE || ' AND UMV.KANA_IDX_1 = ''' || PARAM_KANAINDEX1 || '''';
      END IF;
      -- カナインデックス2
      IF PARAM_KANAINDEX2 IS NOT NULL
         AND PARAM_KANAINDEX2 != '全て'
         AND PARAM_KANAINDEX2 != '戻る'
      THEN
        L_WHERE := L_WHERE || ' AND UMV.KANA_IDX_2 = ''' || PARAM_KANAINDEX2 || '''';
      END IF;
      --2014/06/23 趙雲剛 UPD Start (案件C38117)セールスポイント編集機能の追加  
      --L_SQL := L_SQL || L_WHERE;
      L_SQL := L_SQL || L_SQL_POST || L_SQL_TANNTOU || L_WHERE || NVL(L_SQL_TANNTOUCD,'');
      --2014/06/23 趙雲剛 UPD End
      L_SQL := L_SQL || ' ORDER BY  ' || PARAM_SORTITEM || ') t ) tr';
      L_SQL := L_SQL || ' WHERE tr.ROWINDEX >=  ' || PARAM_STARTINDEX;
      L_SQL := L_SQL || ' AND tr.ROWINDEX  <  ' || PARAM_ENDINDEX;
      OPEN OUT_KOKYAKUINFO FOR L_SQL;
    --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
      OPEN OUT_SAKUSEIINFO FOR SELECT * FROM UMV_RAITEN_UKETUKE WHERE 1 = 2;
    ELSE
      L_SQL   := 'SELECT * FROM(';
      L_SQL   := L_SQL || ' SELECT ROWNUM ROWINDEX, t.* FROM';
      L_SQL   := L_SQL || ' (SELECT ';
      L_SQL   := L_SQL || '         TO_CHAR(UMV.UKETUKE_DATE,''YYYY/MM/DD'') UKETUKE_DATE '; --表示用受付日
      L_SQL   := L_SQL || '        ,TO_CHAR(UMV.UKETUKE_DATE,''YYYY/MM/DD HH24:MI:SS'') ORD_UKETUKE_DATE '; --ソート用受付日
      L_SQL   := L_SQL || '        ,TO_CHAR(UMV.UPD_DATE,''YYYY/MM/DD HH24:MI:SS'') UPD_DATE '; --更新日付
      L_SQL   := L_SQL || '        ,UMV.UKETUKE_NO           UKETUKE_NO'; --受付№
      -- 2018/10/22 王一氷 UPD START 案件C43040-1_法人の入力項目追加対応
      --L_SQL   := L_SQL || '        ,UMV.Kokyaku_Name         Kokyaku_Name'; --顧客名
      L_SQL   := L_SQL || '        ,WMK.KIGYO_NAME       Kokyaku_Name'; --顧客名
      -- 2018/10/22 王一氷 UPD END 案件C43040-1_法人の入力項目追加対応
      L_SQL   := L_SQL || '        ,UMV.Kokyaku_Sb           Kokyaku_Sb'; --顧客種別
      L_SQL   := L_SQL || '        ,UMV.UKETUKE_POST_CD      UKETUKE_POST_CD'; --受付店舗コード
      L_SQL   := L_SQL || '        ,UMV.Chukai_Tantou_Cd     Chukai_Tantou_Cd'; --仲介担当者コード
      L_SQL   := L_SQL || '        ,WMT.TANTOU_NAME          Chukai_Tantou_Name'; --仲介担当者名
      L_SQL   := L_SQL || '        ,CASE WHEN UMV.SAKUSEI_KB = ''3''';
      L_SQL   := L_SQL || '           THEN PKG_HOUJIN_IRAISAKI.FNC_GET_HOUJIN_SINCHOKU_NAME(UMV.Kaisya_Cd,UMV.HOUJIN_IRAIMOTO_UKETUKE_NO)';
      L_SQL   := L_SQL || '         ELSE UMV.Sinchoku_Name ';
      L_SQL   := L_SQL || '         END AS Sinchoku_Name'; --進捗区分名称
      L_SQL   := L_SQL || '        ,UMV.Kokyaku_Ryaku_Name_k Kokyaku_Ryaku_Name_k'; --顧客略称名カナ
      L_SQL   := L_SQL || '        ,UMV.KANA_IDX_1           KANA_IDX_1'; --カナINDEX１
       L_SQL   := L_SQL || '        ,YATIN.KIBOU_JYOUKEN_RIREKI_SEQ SEQ';  
       L_SQL   := L_SQL || '        ,YATIN.KIBOU_BUKKEN_KB KIBOU_BUKKEN_KB';
       L_SQL   := L_SQL || '        ,YATIN.KIBOU_JYOUKEN_SITEI_CD KIBOU_JYOUKEN_SITEI_CD';
       L_SQL   := L_SQL || '        ,PKG_UKG01010.FNC_GET_MADORI_BUKKEN_SB(UMV.KAISYA_CD, UMV.UKETUKE_NO, YATIN.KIBOU_JYOUKEN_RIREKI_SEQ, YATIN.KIBOU_BUKKEN_KB) MADORI';  
       L_SQL   := L_SQL || '        ,PKG_UKG01010.FNC_GET_KIBOU_JYOUKEN(UMV.KAISYA_CD, UMV.UKETUKE_NO, YATIN.KIBOU_JYOUKEN_RIREKI_SEQ, YATIN.KIBOU_JYOUKEN_SITEI_CD) SITEI_AREA';    
       L_SQL   := L_SQL || '        ,UMV.MEMO                          MEMO'; 
       L_SQL   := L_SQL || '        ,YATIN.KIBOU_YATIN_FROM            KIBOU_YATIN_FROM'; 
       L_SQL   := L_SQL || '        ,YATIN.KIBOU_YATIN_TO              KIBOU_YATIN_TO'; 
       L_SQL   := L_SQL || '        ,TO_CHAR(YATIN.KIBOU_NYUKYO_DATE,''YYYY"年"MM"月"'')     KIBOU_NYUKYO_DATE'; 
       L_SQL   := L_SQL || '        ,UMV.SAKUSEI_KB                    SAKUSEI_KB'; --受付情報作成区分
       --2018/01/09 肖剣生 ADD START (案件C42036デザイン変更）
       L_SQL   := L_SQL || '        ,NVL(TO_CHAR(UMV.SHOUDAN_DATE,''YYYY/MM/DD''), ''　'')  SHOUDAN_DATE'; --最終商談日
       --2018/01/09 肖剣生 ADD END (案件C42036デザイン変更）
       -- 2018/10/22 王一氷 ADD START 案件C43040-1_法人の入力項目追加対応
       L_SQL   := L_SQL || '        ,UMV.Kokyaku_Name                  KIGYO_NAME';     --法人仲介契約者
       L_SQL   := L_SQL || '        ,UMV.NYUKYOSYA_NAME                NYUKYOSYA_NAME';  --法人仲介契約者
       -- 2018/10/22 王一氷 ADD END 案件C43040-1_法人の入力項目追加対応
      L_SQL   := L_SQL || ' FROM UMV_RAITEN_UKETUKE UMV';
      --1-4-1-4.希望家賃の取得
      L_SQL   := L_SQL || ' LEFT JOIN( ';
      L_SQL   := L_SQL || '          SELECT B.KAISYA_CD, ';
      L_SQL   := L_SQL || '                 B.UKETUKE_NO, ';
      L_SQL   := L_SQL || '                 B.KIBOU_JYOUKEN_RIREKI_SEQ, ';
      L_SQL   := L_SQL || '                 B.KIBOU_NYUKYO_DATE AS KIBOU_NYUKYO_DATE, ';  
      L_SQL   := L_SQL || '                 B.KIBOU_BUKKEN_KB AS KIBOU_BUKKEN_KB, ';   
      L_SQL   := L_SQL || '                 B.KIBOU_JYOUKEN_SITEI_CD AS KIBOU_JYOUKEN_SITEI_CD, ';
      L_SQL   := L_SQL || '                 CASE ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''1'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''013'', B.KIBOU_YATIN_FROM) ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''2'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''014'', B.KIBOU_YATIN_FROM) ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''3'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''022'', B.KIBOU_YATIN_FROM) ';
      L_SQL   := L_SQL || '                 END AS KIBOU_YATIN_FROM, ';
      L_SQL   := L_SQL || '                 CASE ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''1'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''013'', B.KIBOU_YATIN_TO) ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''2'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''014'', B.KIBOU_YATIN_TO) ';
      L_SQL   := L_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''3'' THEN ';
      L_SQL   := L_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''022'', B.KIBOU_YATIN_TO) ';
      L_SQL   := L_SQL || '                 END AS KIBOU_YATIN_TO ';
      L_SQL   := L_SQL || '            FROM (SELECT T.KAISYA_CD, ';
      L_SQL   := L_SQL || '                         T.UKETUKE_NO, ';
      L_SQL   := L_SQL || '                         MAX(T.KIBOU_JYOUKEN_RIREKI_SEQ) SEQ ';
      L_SQL   := L_SQL || '                    FROM UD_KIBOU_JYOUKEN T ';
      L_SQL   := L_SQL || '                    WHERE T.LOGICAL_DEL_FLG = ''0'' ';     
      L_SQL   := L_SQL || '                   GROUP BY T.KAISYA_CD, T.UKETUKE_NO) A, ';
      L_SQL   := L_SQL || '                 UD_KIBOU_JYOUKEN B ';
      L_SQL   := L_SQL || '           WHERE A.KAISYA_CD = B.KAISYA_CD ';
      L_SQL   := L_SQL || '             AND A.UKETUKE_NO = B.UKETUKE_NO ';
      L_SQL   := L_SQL || '            AND A.SEQ = B.KIBOU_JYOUKEN_RIREKI_SEQ '; 
      L_SQL   := L_SQL || ') YATIN ';
      L_SQL   := L_SQL || 'ON (UMV.KAISYA_CD  = YATIN.KAISYA_CD AND UMV.UKETUKE_NO = YATIN.UKETUKE_NO) ';
      L_SQL   := L_SQL || ' LEFT JOIN (SELECT COUNT(UMVC.HOUJIN_IRAIMOTO_UKETUKE_NO) AS UMVC_UKETUKE_COUNT ';
      L_SQL   := L_SQL || '                   ,UMVC.HOUJIN_IRAIMOTO_UKETUKE_NO AS UMVC_HOUJIN_UKETUKE_NO ';
      L_SQL   := L_SQL || '                   ,UMVC.KAISYA_CD AS UMVC_KAISYA_CD ';
      L_SQL   := L_SQL || '              FROM UMV_RAITEN_UKETUKE UMVC';
      L_SQL   := L_SQL || '             WHERE UMVC.SAKUSEI_KB = ''4''';
      --2018/03/05 H.Sato UPD START
      --L_SQL   := L_SQL || '               AND UMVC.SINCHOKU_CD IN (''0'',''1'',''2'',''3'')';
      IF PARAM_TAKETUCYUSHIFLG = '1' THEN 
        L_SQL   := L_SQL || '               AND UMVC.SINCHOKU_CD IN (''0'',''1'',''2'',''3'',''6'',''7'')';
      ELSE 
        L_SQL   := L_SQL || '               AND UMVC.SINCHOKU_CD IN (''0'',''1'',''2'',''3'')';
      END IF;
      --2018/03/05 H.Sato UPD END
      L_SQL   := L_SQL || '             GROUP BY UMVC.HOUJIN_IRAIMOTO_UKETUKE_NO,UMVC.KAISYA_CD) UC';
      L_SQL   := L_SQL || '        ON UC.UMVC_HOUJIN_UKETUKE_NO = UMV.UKETUKE_NO';
      L_SQL   := L_SQL || '       AND UC.UMVC_KAISYA_CD = UMV.KAISYA_CD';
      -- 2018/10/22 王一氷 ADD START 案件C43040-1_法人の入力項目追加対応
      L_SQL   := L_SQL || ' LEFT JOIN TOKEN.WM_KIGYO WMK ON WMK.KIGYO_CD = UMV.HOUJIN_KIGYOU_CD AND WMK.LOGICAL_DEL_FLG = ''0''';
      -- 2018/10/22 王一氷 ADD END 案件C43040-1_法人の入力項目追加対応
      L_SQL_TANNTOU   := ' LEFT JOIN TOKEN.WM_TANTOU WMT ON (UMV.KAISYA_CD  = WMT.KAISYA_CD AND UMV.CHUKAI_TANTOU_CD  = WMT.TANTOU_CD) ';
      L_WHERE := L_WHERE || ' WHERE UMV.Kaisya_Cd = ''' || PARAM_KAISYACD || '''';

      IF PARAM_SEARCHITEM IS NOT NULL
      THEN
        SEARCHITEM  := SUBSTR(PARAM_SEARCHITEM, 0, INSTR(PARAM_SEARCHITEM, '$', 1, 1) - 1);
        SEARCHVALUE := SUBSTR(PARAM_SEARCHITEM, INSTR(PARAM_SEARCHITEM, '$', 1, 1) + 1, LENGTH(PARAM_SEARCHITEM));

        CASE
        --受付No.検索
          WHEN SEARCHITEM = 'UKETUKE_NO' THEN
            L_WHERE := L_WHERE || '  AND UMV.UKETUKE_NO LIKE ''' || SEARCHVALUE || '%''';
            --顧客名検索
          WHEN SEARCHITEM = 'KOKYAKU_NAME' THEN
            -- 2018/10/22 王一氷 UPD START 案件C43040-1_法人の入力項目追加対応
            --L_WHERE := L_WHERE || ' AND UMV.KOKYAKU_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || ' AND ( UMV.KOKYAKU_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  OR UMV.NYUKYOSYA_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  OR WMK.KIGYO_NAME LIKE ''%' || SEARCHVALUE || '%'')';
            -- 2018/10/22 王一氷 UPD END 案件C43040-1_法人の入力項目追加対応
            --顧客名カナ検索
          WHEN SEARCHITEM = 'KOKYAKU_NAME_K' THEN
            L_WHERE := L_WHERE || ' AND UMV.KOKYAKU_NAME_K LIKE ''%' || SEARCHVALUE || '%''';
            --法人検索
          WHEN SEARCHITEM = 'KOKYAKU_SB1' THEN
            -- 2018/10/22 王一氷 UPD START 案件C43040-1_法人の入力項目追加対応
            --L_WHERE := L_WHERE || ' AND UMV.KOKYAKU_NAME LIKE ''%' || SEARCHVALUE || '%''';;
            L_WHERE := L_WHERE || ' AND ( UMV.KOKYAKU_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  OR UMV.NYUKYOSYA_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  OR WMK.KIGYO_NAME LIKE ''%' || SEARCHVALUE || '%'')';
            -- 2018/10/22 王一氷 UPD END 案件C43040-1_法人の入力項目追加対応
            L_WHERE := L_WHERE || '  AND UMV.KOKYAKU_SB = ''2''';
            --個人検索
          WHEN SEARCHITEM = 'KOKYAKU_SB2' THEN
            L_WHERE := L_WHERE || ' AND UMV.KOKYAKU_NAME LIKE ''%' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  AND UMV.KOKYAKU_SB = ''1''';
            --法人全検索
          WHEN SEARCHITEM = 'KOKYAKU_SB1_ALL' THEN
            L_WHERE := L_WHERE || '  AND UMV.KOKYAKU_SB = ''2''';
            --個人全検索
          WHEN SEARCHITEM = 'KOKYAKU_SB2_ALL' THEN
            L_WHERE := L_WHERE || '  AND UMV.KOKYAKU_SB = ''1''';
            --電話番号検索
          WHEN SEARCHITEM = 'TEL_NO' THEN
            L_WHERE := L_WHERE || ' AND ( UMV.KENSAKU_TEL_NO LIKE ''' || SEARCHVALUE || '%''';
            L_WHERE := L_WHERE || '  OR UMV.KENSAKU_KEITAI_NO LIKE ''' || SEARCHVALUE || '%'')';
        END CASE;
        IF PARAM_TAKETUCYUSHIFLG = '1' THEN 
          L_WHERE := L_WHERE || ' AND ((UMV.SAKUSEI_KB = ''4'' AND UMV.SINCHOKU_CD IN(''1'',''2'',''3'',''6'',''7'')) OR (UMV.SAKUSEI_KB = ''3'' AND UMV.SINCHOKU_CD = ''0''))';
        ELSE 
          L_WHERE := L_WHERE || ' AND ((UMV.SAKUSEI_KB = ''4'' AND UMV.SINCHOKU_CD IN(''1'',''2'',''3'')) OR (UMV.SAKUSEI_KB = ''3'' AND UMV.SINCHOKU_CD = ''0''))';
        END IF;
      ELSE
        L_WHERE := L_WHERE || ' AND (UMV.SAKUSEI_KB = ''3'' AND UMV.SINCHOKU_CD = ''0'') ';
      END IF;
      
      IF PARAM_WHERETANTOU IS NOT NULL
      THEN
        SEARCHITEM  := SUBSTR(PARAM_WHERETANTOU, 0, INSTR(PARAM_WHERETANTOU, '$', 1, 1) - 1);
        SEARCHVALUE := SUBSTR(PARAM_WHERETANTOU, INSTR(PARAM_WHERETANTOU, '$', 1, 1) + 1, LENGTH(PARAM_WHERETANTOU));
         CASE
          --担当者検索
          WHEN SEARCHITEM = 'TANTOU_CD' THEN
            L_SQL_TANNTOUCD := '  AND WMT.TANTOU_CD = ''' || SEARCHVALUE || '''';
         END CASE;
      END IF;
      
      -- カナインデックス1
      IF PARAM_KANAINDEX1 IS NOT NULL
         AND PARAM_KANAINDEX1 != '全て'
      THEN
        L_WHERE := L_WHERE || ' AND UMV.KANA_IDX_1 = ''' || PARAM_KANAINDEX1 || '''';
      END IF;
      -- カナインデックス2
      IF PARAM_KANAINDEX2 IS NOT NULL
         AND PARAM_KANAINDEX2 != '全て'
         AND PARAM_KANAINDEX2 != '戻る'
      THEN
        L_WHERE := L_WHERE || ' AND UMV.KANA_IDX_2 = ''' || PARAM_KANAINDEX2 || '''';
      END IF;
      L_SQL_COUNT := ' AND UC.UMVC_UKETUKE_COUNT IS NOT NULL';
      L_SQL := L_SQL || L_SQL_TANNTOU || L_WHERE || L_SQL_COUNT || NVL(L_SQL_TANNTOUCD,'');
      L_SQL := L_SQL || ' ORDER BY  ' || PARAM_SORTITEM || ') t ) tr';
      L_SQL := L_SQL || ' WHERE tr.ROWINDEX >=  ' || PARAM_STARTINDEX;
      L_SQL := L_SQL || ' AND tr.ROWINDEX  <  ' || PARAM_ENDINDEX;
      dbms_output.put_line('L_WHERE*******************：' || L_WHERE);
      dbms_output.put_line('L_SQL*******************：' || L_SQL);

      OPEN OUT_KOKYAKUINFO FOR L_SQL;
      IF OUT_KOKYAKUINFO IS NOT NULL THEN
              DECLARE RST_CSR TYPCSR;
              TYPE REC_HOUJIN IS RECORD(
                           ROWINDEX             VARCHAR2(30)
                  ,UKETUKE_DATE         VARCHAR2(600)
                  ,ORD_UKETUKE_DATE     VARCHAR2(600)
                  ,UPD_DATE             VARCHAR2(600)
                  ,UKETUKE_NO           VARCHAR2(80)
                  ,Kokyaku_Name         VARCHAR2(600)
                  ,Kokyaku_Sb           VARCHAR2(20)
                  ,UKETUKE_POST_CD      VARCHAR2(30)
                  ,Chukai_Tantou_Cd     VARCHAR2(60)
                  ,Chukai_Tantou_Name   VARCHAR2(60)
                  ,Sinchoku_Name        VARCHAR2(20)
                  ,Kokyaku_Ryaku_Name_k VARCHAR2(600)
                  ,KANA_IDX_1           VARCHAR2(20)
                  ,KIBOU_JYOUKEN_RIREKI_SEQ NUMBER(30)
                  ,KIBOU_BUKKEN_KB      VARCHAR2(20)
                  ,KIBOU_JYOUKEN_SITEI_CD CHAR(10)
                  ,MADORI               VARCHAR2(10000)
                  ,SITEI_AREA           VARCHAR2(10000)
                  ,MEMO                 VARCHAR2(8000)
                  ,KIBOU_YATIN_FROM     VARCHAR2(20)
                  ,KIBOU_YATIN_TO       VARCHAR2(20)
                  ,KIBOU_NYUKYO_DATE    VARCHAR2(600)
                  ,SAKUSEI_KB           CHAR(10)
                  --2018/01/09 肖剣生 ADD START (案件C42036デザイン変更）
                  ,SHOUDAN_DATE         VARCHAR2(12) --最終商談日
                  --2018/01/09 肖剣生 ADD END (案件C42036デザイン変更）
                  --2018/10/22 王一氷 ADD START (法人の入力項目追加対応(C43040)）
                  ,NYUKYOSYA_NAME           VARCHAR2(60) --法人仲介契約者
                  ,KIGYO_NAME               VARCHAR2(60)); --法人仲介契約者
                  --2018/10/22 王一氷 ADD END (法人の入力項目追加対応(C43040)）
                  ROW_HOUJIN REC_HOUJIN;
              BEGIN
                SAKUSEI_KB_STRA := '';
                SAKUSEI_KB_STR := '';
                OPEN RST_CSR FOR L_SQL;
                LOOP
                  FETCH RST_CSR INTO ROW_HOUJIN;
                  IF RST_CSR% NOTFOUND THEN
                    EXIT;
                  ELSIF ROW_HOUJIN.SAKUSEI_KB = '3' THEN
                    SAKUSEI_KB_STRA := SAKUSEI_KB_STRA || ',' || '''' || ROW_HOUJIN.UKETUKE_NO || '''';
                  END IF;
                END LOOP;
                SAKUSEI_KB_STR := '''' || SAKUSEI_KB_STRA || ',' || '''';
                CLOSE RST_CSR; 
              END;
              IF SAKUSEI_KB_STR IS NOT NULL THEN 
                
              H_SQL   := H_SQL || 'SELECT UMV.UKETUKE_NO AS UKETUKE_NO'; --受付№
              H_SQL   := H_SQL || '      ,PKG_UKG01010.FNC_GET_MADORI_BUKKEN_SB(UMV.KAISYA_CD, UMV.UKETUKE_NO, YATIN.KIBOU_JYOUKEN_RIREKI_SEQ, YATIN.KIBOU_BUKKEN_KB) AS MADORI ';
              H_SQL   := H_SQL || '      ,PKG_UKG01010.FNC_GET_KIBOU_JYOUKEN(UMV.KAISYA_CD, UMV.UKETUKE_NO, YATIN.KIBOU_JYOUKEN_RIREKI_SEQ, YATIN.KIBOU_JYOUKEN_SITEI_CD) AS SITEI_AREA ';
              H_SQL   := H_SQL || '      ,YATIN.KIBOU_JYOUKEN_RIREKI_SEQ AS SEQ ';
              H_SQL   := H_SQL || '      ,YATIN.KIBOU_BUKKEN_KB AS KIBOU_BUKKEN_KB ';
              H_SQL   := H_SQL || '      ,YATIN.KIBOU_JYOUKEN_SITEI_CD AS KIBOU_JYOUKEN_SITEI_CD ';
              H_SQL   := H_SQL || '      ,YATIN.KIBOU_YATIN_FROM AS KIBOU_YATIN_FROM ';
              H_SQL   := H_SQL || '      ,YATIN.KIBOU_YATIN_TO AS KIBOU_YATIN_TO ';
              H_SQL   := H_SQL || '      ,UMV.HOUJIN_IRAIMOTO_UKETUKE_NO AS HOUJIN_IRAIMOTO_UKETUKE_NO ';
              H_SQL   := H_SQL || ' FROM UMV_RAITEN_UKETUKE UMV';
              --1-4-1-4.希望家賃の取得
              H_SQL   := H_SQL || ' LEFT JOIN( ';
              H_SQL   := H_SQL || '          SELECT B.KAISYA_CD, ';
              H_SQL   := H_SQL || '                 B.UKETUKE_NO, ';
              H_SQL   := H_SQL || '                 B.KIBOU_JYOUKEN_SITEI_CD, ';
              H_SQL   := H_SQL || '                 B.KIBOU_JYOUKEN_RIREKI_SEQ, ';   
              H_SQL   := H_SQL || '                 B.KIBOU_NYUKYO_DATE AS KIBOU_NYUKYO_DATE, ';  
              H_SQL   := H_SQL || '                 B.KIBOU_BUKKEN_KB AS KIBOU_BUKKEN_KB, ';   
              H_SQL   := H_SQL || '                 CASE ';
              H_SQL   := H_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''1'' THEN ';
              H_SQL   := H_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''013'', B.KIBOU_YATIN_FROM) ';
              H_SQL   := H_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''2'' THEN ';
              H_SQL   := H_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''014'', B.KIBOU_YATIN_FROM) ';
              H_SQL   := H_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''3'' THEN ';
              H_SQL   := H_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''022'', B.KIBOU_YATIN_FROM) ';
              H_SQL   := H_SQL || '                 END AS KIBOU_YATIN_FROM, ';
              H_SQL   := H_SQL || '                 CASE ';
              H_SQL   := H_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''1'' THEN ';
              H_SQL   := H_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''013'', B.KIBOU_YATIN_TO) ';
              H_SQL   := H_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''2'' THEN ';
              H_SQL   := H_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''014'', B.KIBOU_YATIN_TO) ';
              H_SQL   := H_SQL || '                   WHEN B.KIBOU_BUKKEN_KB = ''3'' THEN ';
              H_SQL   := H_SQL || '                    PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME(''022'', B.KIBOU_YATIN_TO) ';
              H_SQL   := H_SQL || '                 END AS KIBOU_YATIN_TO ';
              H_SQL   := H_SQL || '            FROM (SELECT T.KAISYA_CD, ';
              H_SQL   := H_SQL || '                         T.UKETUKE_NO, ';
              H_SQL   := H_SQL || '                         MAX(T.KIBOU_JYOUKEN_RIREKI_SEQ) SEQ ';                  
              H_SQL   := H_SQL || '                    FROM UD_KIBOU_JYOUKEN T ';
              H_SQL   := H_SQL || '                    WHERE T.LOGICAL_DEL_FLG = ''0'' ';     
              H_SQL   := H_SQL || '                   GROUP BY T.KAISYA_CD, T.UKETUKE_NO) A, ';
              H_SQL   := H_SQL || '                 UD_KIBOU_JYOUKEN B ';
              H_SQL   := H_SQL || '           WHERE A.KAISYA_CD = B.KAISYA_CD ';
              H_SQL   := H_SQL || '             AND A.UKETUKE_NO = B.UKETUKE_NO ';
              H_SQL   := H_SQL || '            AND A.SEQ = B.KIBOU_JYOUKEN_RIREKI_SEQ '; 
              H_SQL   := H_SQL || ') YATIN ';
              H_SQL   := H_SQL || 'ON (UMV.KAISYA_CD  = YATIN.KAISYA_CD AND UMV.UKETUKE_NO = YATIN.UKETUKE_NO) ';
              H_SQL   := H_SQL || ' WHERE UMV.Kaisya_Cd = ''' || PARAM_KAISYACD || '''';
              H_SQL   := H_SQL || '   AND UMV.HOUJIN_IRAIMOTO_UKETUKE_NO IN ('''||SAKUSEI_KB_STR||''')';
              H_SQL   := H_SQL || '   AND UMV.SAKUSEI_KB = ''4''';
              H_SQL   := H_SQL || '   AND YATIN.KIBOU_JYOUKEN_SITEI_CD IS NOT NULL ';
              H_SQL   := H_SQL || '   AND UMV.KIBOU_FLG = ''1'' ';
              H_SQL   := H_SQL || ' ORDER BY UMV.UKETUKE_NO DESC';
              OPEN OUT_SAKUSEIINFO FOR H_SQL;                
              ELSE
                OPEN OUT_SAKUSEIINFO FOR SELECT * FROM UMV_RAITEN_UKETUKE WHERE 1 = 2;
              END IF;    
       END IF;           
    END IF;
    
    --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
      
    
    --商談中の顧客一覧総件数
    BEGIN
      --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
      IF PARAM_AUTHORITY_CD <> '3' THEN
	      --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
	      L_COUTSQL := ' SELECT ';
	      L_COUTSQL := L_COUTSQL || ' COUNT(UMV.UKETUKE_NO) ';
	      --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加  
	      --L_COUTSQL := L_COUTSQL || ' FROM UMV_RAITEN_UKETUKE UMV, TOKEN.WM_TANTOU WMT';
	      --L_COUTSQL := L_COUTSQL || L_WHERE;
	      L_COUTSQL := L_COUTSQL || ' FROM UMV_RAITEN_UKETUKE UMV';
        -- 2018/10/22 王一氷 ADD START 案件C43040-1_法人の入力項目追加対応
        L_COUTSQL   := L_COUTSQL || ' LEFT JOIN TOKEN.WM_KIGYO WMK ON WMK.KIGYO_CD = UMV.HOUJIN_KIGYOU_CD AND WMK.LOGICAL_DEL_FLG = ''0''';
        -- 2018/10/22 王一氷 ADD END 案件C43040-1_法人の入力項目追加対応
	      IF L_SQL_TANNTOUCD IS NOT NULL THEN
	        L_COUTSQL := L_COUTSQL || L_SQL_POST || L_SQL_TANNTOU;
	        L_COUTSQL := L_COUTSQL || L_WHERE  || L_SQL_TANNTOUCD;
	      ELSE 
	        L_COUTSQL := L_COUTSQL || L_SQL_POST;
	        L_COUTSQL := L_COUTSQL || L_WHERE;
	      END IF;
	      --2014/06/23 趙雲剛 ADD End
      --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
      ELSE 
	        L_COUTSQL := ' SELECT ';
	        L_COUTSQL := L_COUTSQL || ' COUNT(UMV.UKETUKE_NO) ';
	        L_COUTSQL := L_COUTSQL || ' FROM UMV_RAITEN_UKETUKE UMV';
          -- 2018/10/22 王一氷 ADD START 案件C43040-1_法人の入力項目追加対応
          L_COUTSQL   := L_COUTSQL || ' LEFT JOIN TOKEN.WM_KIGYO WMK ON WMK.KIGYO_CD = UMV.HOUJIN_KIGYOU_CD AND WMK.LOGICAL_DEL_FLG = ''0''';
          -- 2018/10/22 王一氷 ADD END 案件C43040-1_法人の入力項目追加対応
	        L_COUTSQL := L_COUTSQL || ' LEFT JOIN (SELECT COUNT(UMVC.HOUJIN_IRAIMOTO_UKETUKE_NO) AS UMVC_UKETUKE_COUNT ';
	        L_COUTSQL := L_COUTSQL || '                  ,UMVC.HOUJIN_IRAIMOTO_UKETUKE_NO AS UMVC_HOUJIN_UKETUKE_NO';
	        L_COUTSQL := L_COUTSQL || '                  ,UMVC.KAISYA_CD AS UMVC_KAISYA_CD';
	        L_COUTSQL := L_COUTSQL || '              FROM UMV_RAITEN_UKETUKE UMVC ';
	        L_COUTSQL := L_COUTSQL || '             WHERE UMVC.SAKUSEI_KB = ''4''';
		--2018/03/19 H.Sato UPD START
	        --L_COUTSQL := L_COUTSQL || '               AND UMVC.SINCHOKU_CD IN (''0'',''1'',''2'',''3'')';
		IF PARAM_TAKETUCYUSHIFLG = '1' THEN 
	        	L_COUTSQL := L_COUTSQL || '               AND UMVC.SINCHOKU_CD IN (''0'',''1'',''2'',''3'',''6'',''7'')';
		ELSE 
	        	L_COUTSQL := L_COUTSQL || '               AND UMVC.SINCHOKU_CD IN (''0'',''1'',''2'',''3'')';
		END IF;
		--2018/03/19 H.Sato UPD END
	        L_COUTSQL := L_COUTSQL || '             GROUP BY UMVC.HOUJIN_IRAIMOTO_UKETUKE_NO,UMVC.KAISYA_CD) UC';
	        L_COUTSQL := L_COUTSQL || '         ON UC.UMVC_HOUJIN_UKETUKE_NO = UMV.UKETUKE_NO';
	        L_COUTSQL := L_COUTSQL || '        AND UC.UMVC_KAISYA_CD = UMV.KAISYA_CD';
	        
	        L_SQL_COUNT := ' AND UC.UMVC_UKETUKE_COUNT IS NOT NULL';
	        IF L_SQL_TANNTOUCD IS NOT NULL THEN
	          L_COUTSQL := L_COUTSQL || L_SQL_POST || L_SQL_TANNTOU;
	          L_COUTSQL := L_COUTSQL || L_WHERE  || L_SQL_TANNTOUCD || L_SQL_COUNT;
	        ELSE 
	          L_COUTSQL := L_COUTSQL || L_SQL_POST;
	          L_COUTSQL := L_COUTSQL || L_WHERE || L_SQL_COUNT;
	        END IF;
      END IF;
      --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応 
      EXECUTE IMMEDIATE L_COUTSQL
        INTO OUT_RECORDCOUNT;

    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        OUT_RECORDCOUNT := 0;
    END;

    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_KOKYAKUINFO;

  -- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_GEKISONHOJIN
  -- プロシージャ名      ： 既存法人一覧データ取得
  -- 引数             ： param_kaisyacd    　　IN   VARCHAR2     -- 会社コード
  --                 ： param_postcd     　IN   VARCHAR2      -- 店舗コード
  --                 ： param_kanaindex1  IN   VARCHAR2      -- カナインデックス1
  --                 ： param_kanaindex2  IN   VARCHAR2      -- カナインデックス2
  --                 ： out_hojininfo 　   OUT  typCSR     　　　 -- 既存法人一覧データ
  --                 ： rst_msg    　　　OUT  VARCHAR2(7)  　-- エラーメッセージ
  --                 ： rst_code  　　 OUT  VARCHAR2(7)  　-- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要          ： 既存法人一覧データ取得
  -- 初版作成日/作成者  ： 2011/10/19 Ver.1.0.0 SYS_馮強華
  -- ---------------------------------------------------------
  PROCEDURE PRO_GEKISONHOJIN
  (
    PARAM_KAISYACD   IN VARCHAR2 -- 会社コード
     ,PARAM_POSTCD     IN VARCHAR2 -- 店舗コード
     ,PARAM_KANAINDEX1 IN VARCHAR2 -- カナインデックス1
     ,PARAM_KANAINDEX2 IN VARCHAR2 -- カナインデックス2
     ,OUT_HOJININFO    OUT TYPCSR -- 未登録データ
     ,rst_msg          OUT VARCHAR2 -- エラーメッセージ
     ,rst_code         OUT VARCHAR2 -- メッセージID
  ) IS

    L_SQL VARCHAR2(1000); -- SQL文
  BEGIN
    --既存法人一覧データを取得
    L_SQL := '  SELECT ';
    L_SQL := L_SQL || '       UD.Kokyaku_Cd ';
    L_SQL := L_SQL || '      ,UD.Kokyaku_Name ';
    L_SQL := L_SQL || '      ,UD.KOKYAKU_RYAKU_NAME_K ';
    L_SQL := L_SQL || '      ,UD.KANA_IDX_1 ';
    L_SQL := L_SQL || '  FROM UD_KOKYAKU_RIREKI UD ';
    L_SQL := L_SQL || ' WHERE UD.Kaisya_Cd = ''' || PARAM_KAISYACD || '''';
    L_SQL := L_SQL || '   AND UD.CHUKAI_POST_CD = ''' || PARAM_POSTCD || '''';
    L_SQL := L_SQL || '   AND UD.KOKYAKU_SB = ''2''';

    -- カナインデックス1
    IF PARAM_KANAINDEX1 IS NOT NULL
       AND PARAM_KANAINDEX1 != '全て'
    THEN
      L_SQL := L_SQL || ' AND UD.KANA_IDX_1 = ''' || PARAM_KANAINDEX1 || '''';
    END IF;
    -- カナインデックス2
    IF PARAM_KANAINDEX2 IS NOT NULL
       AND PARAM_KANAINDEX2 != '全て'
       AND PARAM_KANAINDEX2 != '戻る'
    THEN
      L_SQL := L_SQL || ' AND UD.KANA_IDX_2 = ''' || PARAM_KANAINDEX2 || '''';
    END IF;

    L_SQL := L_SQL || ' ORDER BY KOKYAKU_RYAKU_NAME_K ASC ';

    OPEN OUT_HOJININFO FOR L_SQL;

    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GEKISONHOJIN;

  -- ---------------------------------------------------------
  -- システム名      ： 受付紹介システム
  -- プロシージャID  ： PRO_GETUKETUKENO
  -- プロシージャ名  ： 受付番号取得
  -- 引数            ： param_kaisyacd           IN   VARCHAR2 　 　-- 会社コード
  --                 ： param_userid             IN   VARCHAR2      --  担当者コード
  --                 ： param_postcd             IN   VARCHAR2      --  店舗コード
  --                 ： param_kokyakucd          IN   VARCHAR2      --  顧客コード
  --                 ： param_chukaitantoucd     IN   VARCHAR2      --  仲介担当者コード
  --                 ： param_uketukehoho        IN   VARCHAR2      --  受付方法
  --                 ： param_kokyakusb          IN   VARCHAR2      --  顧客種別
  --                 ： out_uketukeno            OUT  VARCHAR2      --  受付№
  --                 ： rst_msg              OUT  VARCHAR2  　　-- エラーメッセージ
  --                 ： rst_code            OUT  VARCHAR2      -- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --              ： ERR9999  ： アプリケーションエラー
  -- 処理概要          ： 受付番号取得
  -- 初版作成日/作成者  ： 2011/10/19 Ver.1.0.0 SYS_馮強華
  -- 変更情報           ： 2014/05/13 Ver.1.0.5 SYS_胡   (案件C38100)トップページで既存個人を検索（他）対応
  -- 変更情報           ： 2014/06/23 Ver.1.0.6 SYS_趙雲剛(案件C38117)セールスポイント編集機能の追加
  -- 更新履歴           ： 2015/03/04 var.1.0.8 h-nakano(案件C39075)法人仲介課依頼時は、法人＆法人企業クラブとする
  -- 更新履歴           ： 2018/04/13 var.1.0.9 SYS_肖剣生(案件C42036_デザイン変更）
  -- ---------------------------------------------------------
  PROCEDURE PRO_GETUKETUKENO
  (
    PARAM_KAISYACD       IN VARCHAR2 --会社コード
     ,PARAM_USERID         IN VARCHAR2 -- 担当者コード
     ,PARAM_POSTCD         IN VARCHAR2 -- 店舗コード
     ,PARAM_KOKYAKUCD      IN VARCHAR2 -- 顧客コード
     ,PARAM_CHUKAITANTOUCD IN VARCHAR2 -- 仲介担当者コード
     ,PARAM_UKETUKEHOHO    IN VARCHAR2 --受付方法
     ,PARAM_KOKYAKUSB      IN VARCHAR2 --顧客種別
     ,OUT_UKETUKENO        OUT VARCHAR2 --受付№
     ,rst_msg              OUT VARCHAR2 -- エラーメッセージ
     ,rst_code             OUT VARCHAR2 -- メッセージID
  ) IS

    NUMKAIKEIKI        NUMBER; -- 会計期
    NUMKAIKEIKI_YERAR  NUMBER;
    NUMKAIKEIKI_MONTH  NUMBER;
    NUMTEMPSERIAL_NO   NUMBER; -- シリアル番号
    NUMSERIAL_NOFLG    NUMBER; -- シリアル番号
    VARSERIAL_NO       VARCHAR2(8); -- シリアル番号
    RAITENDATE         DATE;
    TELDATE            DATE;
    EMAILDATE          DATE;
    HOUJIN_CHUKAI_DATE DATE;
    KOKYAKUSB          VARCHAR2(2); -- 顧客種別
    AKETOID            VARCHAR2(4);
-- S 2015.3.4 h-nakano C39075 法人仲介課依頼時は、法人＆法人企業クラブとする
    HOUJINKB           VARCHAR2(1); -- 法人区分
-- E 2015.3.4 h-nakano C39075 法人仲介課依頼時は、法人＆法人企業クラブとする

    --検索引数を使用して、顧客履歴ﾃｰﾌﾞﾙを検索
    CURSOR C_RIREKI IS
      SELECT *
        FROM UD_KOKYAKU_RIREKI UD
       WHERE UD.KAISYA_CD = PARAM_KAISYACD
         AND UD.KOKYAKU_CD = PARAM_KOKYAKUCD;

    REC UD_KOKYAKU_RIREKI%ROWTYPE;

  BEGIN
    -- 初期化
    NUMKAIKEIKI_YERAR := NULL;
    NUMKAIKEIKI_MONTH := NULL;
    NUMTEMPSERIAL_NO  := 0;
    VARSERIAL_NO      := NULL;

    --会計期を取得
    SELECT TO_NUMBER(SUBSTR(KOUMOKU_SB_NAME, 0, 4))
        ,TO_NUMBER(SUBSTR(KOUMOKU_SB_NAME, 6, 2))
      INTO NUMKAIKEIKI_YERAR
        ,NUMKAIKEIKI_MONTH
      FROM TOKEN.WM_COMMON
     WHERE JOUHOU_SB_CD = '152'
       AND KOUMOKU_SB_CD = '1'
       AND LOGICAL_DEL_FLG = '0';
    IF (NUMKAIKEIKI_MONTH > TO_NUMBER(TO_CHAR(SYSDATE, 'mm')))
    THEN
      NUMKAIKEIKI := TO_NUMBER(TO_CHAR(SYSDATE, 'yyyy')) - NUMKAIKEIKI_YERAR;
    ELSE
      NUMKAIKEIKI := TO_NUMBER(TO_CHAR(SYSDATE, 'yyyy')) - NUMKAIKEIKI_YERAR + 1;
    END IF;

    --シリアル番号を取得
    BEGIN
      SELECT SERIAL_NO
        INTO NUMTEMPSERIAL_NO
        FROM TOKEN.WM_SERIAL_NO
       WHERE KAISYA_CD = PARAM_KAISYACD
         AND TABLE_SIKIBETU = 'WM_RAITEN_KOKYAKU'
         AND FIELD_SIKIBETU = NUMKAIKEIKI
         FOR UPDATE NOWAIT;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        NUMTEMPSERIAL_NO := 0;
        NUMSERIAL_NOFLG  := 0;
      WHEN OTHERS THEN
        --ロックが取得できなかった場合
        rst_code := MSG_LOCKFAIL;
        rst_msg  := SQLERRM;
        RETURN;
    END;

    --取得したシリアル番号が最大値(999999）の場合
    IF NUMTEMPSERIAL_NO = 999999
    THEN
      rst_code := MSG_SAIBAN;
      ROLLBACK;
      RETURN;
    END IF;

    --取得したシリアル番号 + 1
    NUMTEMPSERIAL_NO := NUMTEMPSERIAL_NO + 1;

    --受付番号作のためシリアル番号は6桁になるまで左を0埋めする
    VARSERIAL_NO := SUBSTR('00000', 0, (6 - LENGTH(TO_CHAR(NUMTEMPSERIAL_NO)))) || TO_CHAR(NUMTEMPSERIAL_NO);
    --受付番号
    OUT_UKETUKENO := TO_CHAR(NUMKAIKEIKI) || VARSERIAL_NO;

    --シリアル番号不存在の場合、登録する
    IF NUMSERIAL_NOFLG = 0
    THEN
      INSERT INTO TOKEN.WM_SERIAL_NO
        (TABLE_SIKIBETU
        ,KAISYA_CD
        ,FIELD_SIKIBETU
        ,SERIAL_NO
        ,BIKOU
        ,START_NO
        ,END_NO
        ,UPD_DATE
        ,UPD_TANTOU_CD)
      VALUES
        ('WM_RAITEN_KOKYAKU'
        ,PARAM_KAISYACD
        ,TO_CHAR(NUMKAIKEIKI)
        ,NUMTEMPSERIAL_NO
        ,''
        ,1
        ,999999
        ,SYSDATE
        ,PARAM_USERID);
    ELSE
      --シリアル番号存在の場合、更新する
      UPDATE TOKEN.WM_SERIAL_NO
         SET SERIAL_NO     = NUMTEMPSERIAL_NO
          ,UPD_DATE      = SYSDATE
          ,UPD_TANTOU_CD = PARAM_USERID
       WHERE KAISYA_CD = PARAM_KAISYACD
         AND TABLE_SIKIBETU = 'WM_RAITEN_KOKYAKU'
         AND FIELD_SIKIBETU = NUMKAIKEIKI;
    END IF;

    RAITENDATE         := NULL;
    TELDATE            := NULL;
    EMAILDATE          := NULL;
    HOUJIN_CHUKAI_DATE := NULL;

    CASE
      WHEN PARAM_UKETUKEHOHO = '1' THEN
        RAITENDATE := SYSDATE;
      WHEN PARAM_UKETUKEHOHO = '2' THEN
        TELDATE := SYSDATE;
      WHEN PARAM_UKETUKEHOHO = '3' THEN
        EMAILDATE := SYSDATE;
      WHEN PARAM_UKETUKEHOHO = '4' THEN
        HOUJIN_CHUKAI_DATE := SYSDATE;
    END CASE;

    --2014/05/13 胡 UPD Start (案件C38100)トップページで既存個人を検索（他）対応
    --IF PARAM_KOKYAKUSB = '1'
    IF PARAM_KOKYAKUSB = '1' OR PARAM_KOKYAKUSB = '4'
    --2014/05/13 胡 UPD End
    THEN
      KOKYAKUSB := '1';
    ELSE
      KOKYAKUSB := '2';
    END IF;

-- S 2015.3.4 h-nakano C39075 法人仲介課依頼時は、法人＆法人企業クラブとする
    IF PARAM_UKETUKEHOHO = '4'
    THEN
        KOKYAKUSB := '2';
        HOUJINKB := '2';
    ELSE 
        HOUJINKB := '';
    END IF;
-- E 2015.3.4 h-nakano C39075 法人仲介課依頼時は、法人＆法人企業クラブとする

    --2014/05/13 胡 UPD Start (案件C38100)トップページで既存個人を検索（他）対応
    --IF PARAM_KOKYAKUSB = '3'
    IF PARAM_KOKYAKUSB = '3' OR PARAM_KOKYAKUSB = '4'
    --2014/05/13 胡 UPD End
    THEN
      AKETOID := '27';
      OPEN C_RIREKI;
      LOOP
        FETCH C_RIREKI
          INTO REC;
        EXIT WHEN C_RIREKI%NOTFOUND;

        INSERT INTO UD_RAITEN_UKETUKE
          (KAISYA_CD
          ,UKETUKE_NO
          ,RAITEN_DATE
          ,TEL_DATE
          ,MAIL_DATE
          ,HOUJIN_CHUKAI_DATE
          ,UKETUKE_KB
          ,UKETUKE_DATE
          ,UKETUKE_POST_CD
          ,UKETUKE_TANTOU_CD
          ,CHUKAI_TANTOU_CD
          ,KOKYAKU_INFO_FLG
          ,ANKETO_FLG
          ,KIBOU_FLG
          ,SHOUKAI_BUKKEN_FLG
          ,SHOUDAN_FLG
          ,CANCEL_DATE
          ,CANCEL_CD
          ,SINCHOKU_CD
          ,KOKYAKU_SB
          ,HOUJIN_KB
          ,KOKYAKU_CD
          ,KOKYAKU_NAME
          ,KOKYAKU_NAME_K
          ,KANA_IDX_1
          ,KANA_IDX_2
          ,KOKYAKU_RYAKU_NAME
          ,KOKYAKU_RYAKU_NAME_K
          ,SEIBETU_KB_CD
          ,BIRTH_DAY
          ,YUBIN_NO
          ,JIS_JUSYO_CD
          ,JUSYO1
          ,JUSYO2
          ,JUSYO3
          ,JUSYO4
          ,TEL_NO
          ,TEL_BIKOU
          ,KEITAI_NO
          ,KEITAI_BIKOU
          ,FAX_NO
          ,FAX_BIKOU
          ,E_MAIL_ADDRESS
          ,KEITAI_MAIL_ADDRESS
          ,MEMO
          ,APP_DATE
          ,APP_TANTOU_CD
          ,UPD_DATE
          ,UPD_TANTOU_CD
          ,LOGICAL_DEL_FLG
          ,LOGICAL_DEL_DATE
          ,DEL_TANTOU_CD
          ,LAST_TUIKYAKU_DATE)
        VALUES
          (PARAM_KAISYACD
          ,OUT_UKETUKENO
          ,RAITENDATE
          ,TELDATE
          ,EMAILDATE
          ,HOUJIN_CHUKAI_DATE
          ,PARAM_UKETUKEHOHO
          ,SYSDATE
          ,PARAM_POSTCD
          ,PARAM_USERID
          ,PARAM_CHUKAITANTOUCD
          ,'1'
          ,'0'
          ,'0'
          ,'0'
          ,'0'
          ,NULL
          ,''
          ,'0'
          ,KOKYAKUSB
          ,HOUJINKB --,''2015.3.4 h-nakano C39075
          ,REC.KOKYAKU_CD
          ,REC.KOKYAKU_NAME
          ,REC.KOKYAKU_NAME_K
          ,REC.KANA_IDX_1
          ,REC.KANA_IDX_2
          ,REC.KOKYAKU_RYAKU_NAME
          ,REC.KOKYAKU_RYAKU_NAME_K
          ,REC.SEIBETU_KB_CD
          ,REC.BIRTH_DAY
          ,REC.YUBIN_NO
          ,REC.JIS_JUSYO_CD
          ,REC.JUSYO1
          ,REC.JUSYO2
          ,REC.JUSYO3
          ,REC.JUSYO4
          ,REC.TEL_NO
          ,''
          ,REC.KEITAI_NO
          ,''
          ,REC.FAX_NO
          ,''
          ,REC.E_MAIL_ADDRESS
          ,REC.KEITAI_MAIL_ADRESS
          ,''
          ,SYSDATE
          ,PARAM_USERID
          ,SYSDATE
          ,PARAM_USERID
          ,'0'
          ,NULL
          ,NULL
          ,SYSDATE);
      END LOOP;
      CLOSE C_RIREKI;
    ELSE
      AKETOID := '26';
      INSERT INTO UD_RAITEN_UKETUKE
        (KAISYA_CD
        ,UKETUKE_NO
        ,RAITEN_DATE
        ,TEL_DATE
        ,MAIL_DATE
        ,HOUJIN_CHUKAI_DATE
        ,UKETUKE_KB
        ,UKETUKE_DATE
        ,UKETUKE_POST_CD
        ,UKETUKE_TANTOU_CD
        ,CHUKAI_TANTOU_CD
        ,KOKYAKU_INFO_FLG
        ,ANKETO_FLG
        ,KIBOU_FLG
        ,SHOUKAI_BUKKEN_FLG
        ,SHOUDAN_FLG
        ,CANCEL_DATE
        ,CANCEL_CD
        ,SINCHOKU_CD
        ,KOKYAKU_SB
        ,HOUJIN_KB
        ,KOKYAKU_CD
        ,KOKYAKU_NAME
        ,KOKYAKU_NAME_K
        ,KANA_IDX_1
        ,KANA_IDX_2
        ,KOKYAKU_RYAKU_NAME
        ,KOKYAKU_RYAKU_NAME_K
        ,SEIBETU_KB_CD
        ,BIRTH_DAY
        ,YUBIN_NO
        ,JIS_JUSYO_CD
        ,JUSYO1
        ,JUSYO2
        ,JUSYO3
        ,JUSYO4
        ,TEL_NO
        ,TEL_BIKOU
        ,KEITAI_NO
        ,KEITAI_BIKOU
        ,FAX_NO
        ,FAX_BIKOU
        ,E_MAIL_ADDRESS
        ,KEITAI_MAIL_ADDRESS
        ,MEMO
        --2018/04/13 肖剣生 ADD START (案件C42036デザイン変更）
        ,SAKUSEI_KB
        --2018/04/13 肖剣生 ADD END (案件C42036デザイン変更）
        ,APP_DATE
        ,APP_TANTOU_CD
        ,UPD_DATE
        ,UPD_TANTOU_CD
        ,LOGICAL_DEL_FLG
        ,LOGICAL_DEL_DATE
        ,DEL_TANTOU_CD
        ,LAST_TUIKYAKU_DATE)
      VALUES
        (PARAM_KAISYACD
        ,OUT_UKETUKENO
        ,RAITENDATE
        ,TELDATE
        ,EMAILDATE
        ,HOUJIN_CHUKAI_DATE
        ,PARAM_UKETUKEHOHO
        ,SYSDATE
        ,PARAM_POSTCD
        ,PARAM_USERID
        ,PARAM_CHUKAITANTOUCD
        ,'0'
        ,'0'
        ,'0'
        ,'0'
        ,'0'
        ,NULL
        ,''
        ,'0'
        ,KOKYAKUSB
        ,HOUJINKB --,''  2015.3.4 h-nakano C39075
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        ,''
        --2018/04/13 肖剣生 ADD START (案件C42036デザイン変更）
        ,'0'
        --2018/04/13 肖剣生 ADD END (案件C42036デザイン変更）
        ,SYSDATE
        ,PARAM_USERID
        ,SYSDATE
        ,PARAM_USERID
        ,'0'
        ,NULL
        ,NULL
        ,SYSDATE);
    END IF;
    -- アンケートデータ追加
    INSERT INTO UD_ANKETO
      (KAISYA_CD
      ,UKETUKE_NO
      ,ANKETO_ID
      ,ANKETO_ANSWER
      ,APP_DATE
      ,APP_TANTOU_CD
      ,UPD_DATE
      ,UPD_TANTOU_CD)
    VALUES
      (PARAM_KAISYACD
      ,OUT_UKETUKENO
      ,'25'
      ,AKETOID
      ,SYSDATE
      ,PARAM_USERID
      ,SYSDATE
      ,PARAM_USERID);
    rst_code := MSG_SUCCESS;
    COMMIT;

  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
      ROLLBACK;

  END PRO_GETUKETUKENO;

  -- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_UPDSYOUKAIINFO
  -- プロシージャ名      ： 紹介物件データ更新
  -- 引数                ： param_uketukeno          　IN   VARCHAR2      -- 受付№
  --                     ： param_bukkenno     　    　IN   VARCHAR2      -- 物件№
  --                     ： param_heyano               IN   VARCHAR2      -- 部屋№
  --                     ： param_parkingkbn         　IN  VARCHAR2    　 -- 駐車場区分
  --                     ： param_upddate            　IN  VARCHAR2    　 -- 更新日付
  --                     ： param_tantoucd           　IN  VARCHAR2    　 -- ログインユーザコード
  --                     ： param_kaisyacd           　IN  VARCHAR2    　 -- ログインユーザ会社コード
  --                     ： rst_msg              　OUT  VARCHAR2      -- エラーメッセージ
  --                     ： rst_code            　OUT  VARCHAR2  　  -- メッセージID
  -- 戻り値      　　    ： 0000000  ： 成功
  --           　　      ： ERR9999  ： アプリケーションエラー
  -- 処理概要        　  ： 予約データリスト取得
  -- 初版作成日/作成者   ： 2011/10/19 Ver.1.0.0 SYS_馮強華
  -- 変更情報       　   ：
  -- ---------------------------------------------------------
  PROCEDURE PRO_UPDSYOUKAIINFO
  (
    PARAM_UKETUKENO  IN VARCHAR2 -- 受付番号
     ,PARAM_BUKKENNO   IN VARCHAR2 -- 物件№
     ,PARAM_HEYANO     IN VARCHAR2 -- 部屋番号
     ,PARAM_PARKINGKBN IN VARCHAR2 -- 駐車場区分
     ,PARAM_UPDDATE    IN VARCHAR2 -- 更新日付
     ,PARAM_TANTOUCD   IN VARCHAR2 -- ログインユーザコード
     ,PARAM_KAISYACD   IN VARCHAR2 -- ログインユーザ会社コード
     ,rst_msg          OUT VARCHAR2 -- エラーメッセージ
     ,rst_code         OUT VARCHAR2 -- メッセージID
  ) IS

    VARUPDDATE VARCHAR2(20);
    CURSOR l_crs IS
       SELECT TO_CHAR(US.UPD_DATE, 'YYYYMMDDHH24MI')
        INTO VARUPDDATE
        FROM UD_SHOUKAI_BUKKEN US
       WHERE US.KAISYA_CD = PARAM_KAISYACD
         AND US.UKETUKE_NO = PARAM_UKETUKENO
         AND US.HOMEMATE_BUKKEN_NO = PARAM_BUKKENNO
         AND US.HEYA_NO = PARAM_HEYANO
         AND US.BUKKEN_PARKING_KB = PARAM_PARKINGKBN;
  BEGIN

    --紹介データ更新
    OPEN l_crs;
    LOOP
        FETCH l_crs
            INTO VARUPDDATE;
        EXIT WHEN l_crs%NOTFOUND;
    END LOOP;

    IF PARAM_UPDDATE <> VARUPDDATE
    THEN
      rst_code := MSG_UPDERR;
      RETURN;
    END IF;

    UPDATE UD_SHOUKAI_BUKKEN US
       SET US.KARIOSAEJI_JYUNI          = '1'
        ,US.KARIOSAE_YUKOU_START_DATE = SYSDATE
        ,US.UPD_DATE                  = SYSDATE
        ,US.UPD_TANTOU_CD             = PARAM_TANTOUCD
     WHERE US.KAISYA_CD = PARAM_KAISYACD
       AND US.UKETUKE_NO = PARAM_UKETUKENO
       AND US.HOMEMATE_BUKKEN_NO = PARAM_BUKKENNO
       AND US.HEYA_NO = PARAM_HEYANO;

    COMMIT;
    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      ROLLBACK;
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_UPDSYOUKAIINFO;

--2013/01/25 林森 ADD Start (案件C37058)受付システム要望対応
  -- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_GETUKETUKEKUMISUU
  -- プロシージャ名      ： 担当者受付組数取得
  -- 引数               ： PARAM_KAISYACD           IN  VARCHAR2    　 -- ログインユーザ会社コード
  --                   ： PARAM_POSTCD     　    　  IN   VARCHAR2     -- ログイン情報.店舗コード
  --                   ： PARAM_TANTOUCDS    　    　IN   VARCHAR2     -- ログイン情報.店舗コード.担当者リス
  --                   ： OUT_MONTHKUMISUU         OUT  TYPCSR       -- 当月の受付組数
  --                   ： OUT_KUMISUU              OUT  TYPCSR       -- 保有組数
  --                   ： rst_msg              　  OUT  VARCHAR2      -- エラーメッセージ
  --                   ： rst_code            　   OUT  VARCHAR2  　  -- メッセージID
  -- 戻り値      　　     ： 0000000  ： 成功
  --           　　      ： ERR9999  ： アプリケーションエラー
  -- 処理概要        　  ： 担当者受付組数取得
  -- 初版作成日/作成者   ： 2013/01/25 Ver.1.0.0 SYS_林森
  -- 変更情報       　   ：
  -- ---------------------------------------------------------
  PROCEDURE PRO_GETUKETUKEKUMISUU
  (
     PARAM_KAISYACD       IN VARCHAR2 -- ログインユーザ会社コード
     ,PARAM_POSTCD        IN VARCHAR2 -- ログイン情報.店舗コード
     ,PARAM_TANTOUCDS     IN VARCHAR2 -- ログイン情報.店舗コード.担当者リス
     ,OUT_MONTHKUMISUU OUT TYPCSR -- 当月の受付組数
     ,OUT_KUMISUU      OUT TYPCSR -- 保有組数
     ,rst_msg          OUT VARCHAR2 -- エラーメッセージ
     ,rst_code         OUT VARCHAR2 -- メッセージID
  ) IS

   MONTHKUMISUU_SQL       VARCHAR2(2000); -- SQL文
   KUMISUU_SQL            VARCHAR2(2000); -- SQL文
  BEGIN

    KUMISUU_SQL   := ' SELECT 　URU.CHUKAI_TANTOU_CD AS TANTOUCD,COUNT(URU.UKETUKE_NO) AS KUMISUU';
    KUMISUU_SQL   := KUMISUU_SQL || ' FROM UMV_RAITEN_UKETUKE URU';
    KUMISUU_SQL   := KUMISUU_SQL || ' WHERE URU.KAISYA_CD ='''||PARAM_KAISYACD||'''';
    KUMISUU_SQL   := KUMISUU_SQL || ' AND URU.UKETUKE_POST_CD ='''||PARAM_POSTCD||'''';
    KUMISUU_SQL   := KUMISUU_SQL || ' AND URU.SINCHOKU_CD  IN (''1'',''2'',''3'')';
    KUMISUU_SQL   := KUMISUU_SQL || ' AND URU.CHUKAI_TANTOU_CD IN ('''||PARAM_TANTOUCDS||''')';
    --2017/09/12 張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応
    KUMISUU_SQL   := KUMISUU_SQL || ' AND (URU.SAKUSEI_KB <> ''3'' OR URU.SAKUSEI_KB IS NULL) ';
    --2017/09/12 張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応
    MONTHKUMISUU_SQL := KUMISUU_SQL;
    MONTHKUMISUU_SQL := MONTHKUMISUU_SQL || ' AND TO_CHAR(URU.UKETUKE_DATE,''yyyyMM'') = TO_CHAR(SYSDATE,''yyyyMM'')';

    KUMISUU_SQL      := KUMISUU_SQL || ' GROUP BY URU.CHUKAI_TANTOU_CD';
    MONTHKUMISUU_SQL := MONTHKUMISUU_SQL || ' GROUP BY URU.CHUKAI_TANTOU_CD';
    --当月の受付組数取得
    OPEN OUT_MONTHKUMISUU FOR MONTHKUMISUU_SQL;
    --保有組数取得
    OPEN OUT_KUMISUU FOR KUMISUU_SQL;

    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GETUKETUKEKUMISUU;
--2013/01/25 林森 ADD End

--2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
  -- ---------------------------------------------------------
  -- システム名          ： 受付紹介システム
  -- プロシージャID      ： PRO_GET_POST_UKETUKEKUMISUU
  -- プロシージャ名      ： 店舗受付組数取得
  -- 引数               ： PARAM_KAISYACD           IN  VARCHAR2    　 -- ログインユーザ会社コード
  --                    ： param_postcd     　　 IN   VARCHAR2        　 -- 店舗コード
  --                    ： PARAM_AUTHORITY_CD   IN   VARCHAR2    -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
  --                   ： PARAM_SISYA_BLK_CD   IN   VARCHAR2    -- 支社ブロックコード
  --                   ： OUT_MONTHKUMISUU         OUT  TYPCSR       -- 当月の受付組数
  --                   ： OUT_KUMISUU              OUT  TYPCSR       -- 保有組数
  --                   ： rst_msg              　  OUT  VARCHAR2      -- エラーメッセージ
  --                   ： rst_code            　   OUT  VARCHAR2  　  -- メッセージID
  -- 戻り値      　　     ： 0000000  ： 成功
  --           　　      ： ERR9999  ： アプリケーションエラー
  -- 処理概要        　  ： 担当者受付組数取得
  -- 初版作成日/作成者   ： 2014/06/23 Ver.1.0.0 SYS_趙雲剛
  -- 変更情報       　   ：
  -- ---------------------------------------------------------
  PROCEDURE PRO_GET_POST_UKETUKEKUMISUU
  (
     PARAM_KAISYACD       IN VARCHAR2 -- ログインユーザ会社コード
     ,PARAM_POSTCD    IN VARCHAR2 -- 店舗コード
     ,PARAM_AUTHORITY_CD  IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
     ,PARAM_SISYA_BLK_CD  IN VARCHAR2 -- 支社ブロックコード
     ,OUT_MONTHKUMISUU    OUT TYPCSR -- 当月の受付組数
     ,OUT_KUMISUU         OUT TYPCSR -- 保有組数
     ,rst_msg             OUT VARCHAR2 -- エラーメッセージ
     ,rst_code            OUT VARCHAR2 -- メッセージID
  ) IS

   MONTHKUMISUU_SQL       VARCHAR2(200); -- SQL文
   KUMISUU_AFTER_SQL       VARCHAR2(2000); -- SQL文
   KUMISUU_SQL            VARCHAR2(2000); -- SQL文
  BEGIN

   IF PARAM_POSTCD IS NOT NULL THEN
        KUMISUU_SQL   := '  SELECT TB.TANTOU_CD AS CODE,';
        KUMISUU_SQL   := KUMISUU_SQL || '    TB.TANTOU_NAME AS NAME, ';
        KUMISUU_SQL   := KUMISUU_SQL || '    COUNT(URU.UKETUKE_NO) AS KUMISUU ';
        KUMISUU_SQL   := KUMISUU_SQL || ' FROM TOKEN.WM_TANTOU_POST TA ';
        KUMISUU_SQL   := KUMISUU_SQL || ' JOIN TOKEN.WM_TANTOU TB ';
        KUMISUU_SQL   := KUMISUU_SQL || '   ON (TA.KAISYA_CD = TB.KAISYA_CD AND TA.TANTOU_CD = TB.TANTOU_CD) ';
        KUMISUU_SQL   := KUMISUU_SQL || ' LEFT JOIN UMV_RAITEN_UKETUKE URU ';
        KUMISUU_SQL   := KUMISUU_SQL || '   ON (URU.KAISYA_CD = TA.KAISYA_CD AND URU.UKETUKE_POST_CD = TA.POST_CD AND URU.CHUKAI_TANTOU_CD = TB.TANTOU_CD ';
        
        MONTHKUMISUU_SQL := ' AND TO_CHAR(URU.UKETUKE_DATE,''yyyyMM'') = TO_CHAR(SYSDATE,''yyyyMM'')';
        --2017/09/12 張虎強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
        --KUMISUU_AFTER_SQL   := '   AND URU.SINCHOKU_CD IN (''1'', ''2'', ''3'') AND URU.LOGICAL_DEL_FLG = ''0'') ';
        KUMISUU_AFTER_SQL   := '   AND URU.SINCHOKU_CD IN (''1'', ''2'', ''3'') AND URU.LOGICAL_DEL_FLG = ''0'' AND (URU.SAKUSEI_KB <> ''3'' OR URU.SAKUSEI_KB IS NULL))';
        --2017/09/12 張虎強 UPD END (案件C41004)法人仲介担当者登録機能追加対応
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || ' WHERE TA.KAISYA_CD = '''||PARAM_KAISYACD||'''';
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || '   AND TA.POST_CD = '''||PARAM_POSTCD||'''';
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || '   AND TA.LOGICAL_DEL_FLG = ''0''';
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || '   AND TB.LOGICAL_DEL_FLG = ''0''';
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || ' GROUP BY TB.TANTOU_CD, TB.TANTOU_NAME';
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || '  ORDER BY TB.TANTOU_CD';
    ELSE
        KUMISUU_SQL   := ' SELECT WP.POST_CD AS CODE,';
        KUMISUU_SQL   := KUMISUU_SQL || '  WP.POST_NAME AS NAME,';
        KUMISUU_SQL   := KUMISUU_SQL || '  COUNT(URU.UKETUKE_NO) AS KUMISUU';
        KUMISUU_SQL   := KUMISUU_SQL || '  FROM TOKEN.WM_POST WP';
        KUMISUU_SQL   := KUMISUU_SQL || '  LEFT JOIN UMV_RAITEN_UKETUKE URU';
        KUMISUU_SQL   := KUMISUU_SQL || '    ON (URU.KAISYA_CD = WP.KAISYA_CD AND URU.UKETUKE_POST_CD = WP.POST_CD ';
        
        MONTHKUMISUU_SQL := ' AND TO_CHAR(URU.UKETUKE_DATE,''yyyyMM'') = TO_CHAR(SYSDATE,''yyyyMM'')';
        --2017/09/12 張虎強 UPD START (案件C41004)法人仲介担当者登録機能追加対応
        --KUMISUU_AFTER_SQL   := '  AND   URU.SINCHOKU_CD IN (''1'', ''2'', ''3'') AND URU.LOGICAL_DEL_FLG = ''0'')';
        KUMISUU_AFTER_SQL   := '  AND   URU.SINCHOKU_CD IN (''1'', ''2'', ''3'') AND URU.LOGICAL_DEL_FLG = ''0'' AND (URU.SAKUSEI_KB <> ''3'' OR URU.SAKUSEI_KB IS NULL))';
        --2017/09/12 張虎強 UPD END (案件C41004)法人仲介担当者登録機能追加対応
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || '  WHERE WP.KAISYA_CD = '''||PARAM_KAISYACD||'''';
        IF PARAM_AUTHORITY_CD = '1' THEN
           KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || ' AND WP.SISYA_BLK_CD ='''||PARAM_SISYA_BLK_CD||'''';
        END IF;
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || ' AND WP.OUTPUT_ORDER <> ''999''';
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || ' AND WP.OUTPUT_ORDER IS NOT NULL';
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || ' AND WP.LOGICAL_DEL_FLG = ''0''';
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || '  GROUP BY WP.POST_CD, WP.POST_NAME, WP.OUTPUT_ORDER';
        KUMISUU_AFTER_SQL   := KUMISUU_AFTER_SQL || '  ORDER BY WP.OUTPUT_ORDER';
    END IF;
    --当月の受付組数取得
    OPEN OUT_MONTHKUMISUU FOR KUMISUU_SQL || MONTHKUMISUU_SQL || KUMISUU_AFTER_SQL;
    
    --保有組数取得
    OPEN OUT_KUMISUU FOR KUMISUU_SQL || KUMISUU_AFTER_SQL;
    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
  END PRO_GET_POST_UKETUKEKUMISUU;
--2014/06/23 趙雲剛 ADD End
 
--2013/05/16 郭凡 ADD Start (案件C37109-1)受付システムSTEP2要望対応
  -- ---------------------------------------------------------
  -- システム名         ： 受付紹介システム
  -- プロシージャID      ： PRC_CHUUSHI_TAKETU_PROCESS
  -- プロシージャ名      ： 中止、他決ボタン押下時処理
  -- 引数            ： pv_kaisya_cd             IN  VARCHAR2          -- 会社コード
  --                 ： pv_uketuke_no           IN   VARCHAR2　     　   -- 仲受付no.
  --                 ： pv_tantou               IN   VARCHAR2　     　   -- 担当者コード
  --                 ： pv_update_date          IN   VARCHAR2　     　   -- 更新日付
  --                 ： pv_type                 IN   VARCHAR2　     　   -- 処理種類
  --2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
  --                 ： pv_shoudancmnt          IN  VARCHAR2           -- 商談理由
  --2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
  --                 ： rst_code                OUT  VARCHAR2　　       -- メッセージコード
  --                 ： rst_msg                 OUT  VARCHAR2　　       -- メッセージ
  -- 処理概要         ： 追客アラーム情報
  -- 初版作成日/作成者 ： 2013/05/16 Ver.1.0.0 SYS_郭凡
  -- 変更情報          ： 2017/08/04 Ver.1.0.1 MJEC)長倉 (案件C41059)中止他決顧客の検索対応
  -- 変更情報         ： 2020/06/24 Ver.1.0.2 SYS_劉恆毅 C43040-2_法人の入力項目追加対応
  -- ---------------------------------------------------------
  PROCEDURE PRC_CHUUSHI_TAKETU_PROCESS
  (
    pv_kaisya_cd   IN VARCHAR2
     ,pv_uketuke_no  IN VARCHAR2
     ,pv_tantou      IN VARCHAR2
     ,pv_update_date IN VARCHAR2
     ,pv_type        IN VARCHAR2
  --2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
     ,pv_shoudancmnt IN VARCHAR2
  --2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
     ,rst_code       OUT VARCHAR2
     ,rst_msg        OUT VARCHAR2
  ) IS
    upd_date   CHAR(19);
    pv_nowdate DATE;
  BEGIN
    rst_code   := MSG_SUCCESS;
    pv_nowdate := SYSDATE;
    BEGIN
    SELECT TO_CHAR(UPD_DATE, 'YYYY/MM/DD HH24:MI:SS')
      INTO upd_date
      FROM UMV_RAITEN_UKETUKE
     WHERE KAISYA_CD = pv_kaisya_cd
       AND UKETUKE_NO = pv_uketuke_no;
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
        upd_date := null;
    END;
    IF upd_date = pv_update_date
        THEN
            IF pv_type = 'chuushi'
            THEN
                PKG_KOKYAKU.PRC_SHOUDAN_STOP(pv_kaisya_cd
                                            ,pv_uketuke_no
                                            ,pv_tantou
                                            ,7
                                            ,0
                                            ,pv_nowdate
  --2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
                                            ,pv_shoudancmnt
  --2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
                                            ,rst_code
                                            ,rst_msg);
            ELSIF pv_type = 'taketu'
            THEN
                PKG_KOKYAKU.PRC_SHOUDAN_STOP(pv_kaisya_cd
                                            ,pv_uketuke_no
                                            ,pv_tantou
                                            ,6
                                            ,0
                                            ,pv_nowdate
  --2020/06/24 劉恆毅(SYS) ADD Start C43040-2_法人の入力項目追加対応
                                            ,pv_shoudancmnt
  --2020/06/24 劉恆毅(SYS) ADD End C43040-2_法人の入力項目追加対応
                                            ,rst_code
                                            ,rst_msg);
            --2017/08/04 MJEC)長倉 ADD Start (案件C41059)中止他決顧客の検索対応
            ELSIF pv_type = 'syoudanchu'
            THEN
                PKG_KOKYAKU.PRC_SHOUDAN_RESUMPTION(pv_kaisya_cd
                                                  ,pv_uketuke_no
                                                  ,pv_tantou
                                                  ,1
                                                  ,0
                                                  ,pv_nowdate
                                                  ,rst_code
                                                  ,rst_msg);
            --2017/08/04 MJEC)長倉 ADD End
            END IF;
           
            IF RST_CODE = '0000000'
            THEN
                COMMIT;
            ELSIF RST_CODE = '1111111'
            THEN
                ROLLBACK;
            END IF;
        ELSE
            rst_code := MSG_EXCLUSIVE;
            ROLLBACK;
        END IF;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            rst_code := MSG_UNKNOWN;
            rst_msg  := SQLERRM;
            dbms_output.put_line('エラーコード：' || rst_code);
            dbms_output.put_line('エラーメッセージ：' || rst_msg);
    END;
  --2013/05/16 郭凡 ADD End
  
    --2014/06/23 趙雲剛 ADD Start (案件C38117)セールスポイント編集機能の追加
    -- ---------------------------------------------------------
    -- システム名        ： 受付紹介システム
    -- プロシージャID   ： PRC_GET_POST_LIST
    -- プロシージャ名    ： 店舗一覧取得
    -- 引数           ： param_kaisya_cd     IN VARCHAR2,        -- 会社コード
    --                ： param_authority_cd  IN VARCHAR2         -- ユーザー権限コード
    --                ： param_sisya_blk_cd  IN VARCHAR2         -- 支社ブロックコード
    --                ： rstpostlist            OUT typCSR          -- 結果セット
    --                ： rst_code            OUT VARCHAR2        -- 結果区分
    -- 初版作成日/作成者  ： 2014/06/23 Ver.1.0.0 SYS_趙雲剛
    -- 変更情報
    -- ---------------------------------------------------------
    PROCEDURE PRC_GET_POST_LIST
    (
        param_kaisya_cd    IN VARCHAR2    -- 会社コード
       ,param_authority_cd IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
       ,param_sisya_blk_cd IN VARCHAR2 -- 支社ブロックコード
       ,rstpostlist        OUT typCSR
       ,rst_code           OUT VARCHAR2
    ) IS
    L_SQL            VARCHAR2(2000); -- SQL文
    BEGIN
        L_SQL := L_SQL || 'SELECT WP.POST_CD     AS POST_CD ';
        L_SQL := L_SQL || '      ,WP.POST_NAME   AS POST_NAME ';
        L_SQL := L_SQL || '  FROM TOKEN.WM_POST WP ';
        L_SQL := L_SQL || ' WHERE WP.KAISYA_CD = ''' || param_kaisya_cd || '''';
        IF  PARAM_AUTHORITY_CD = '1' THEN
          L_SQL := L_SQL || ' AND WP.SISYA_BLK_CD = ''' || param_sisya_blk_cd || '''';
        END IF;
        L_SQL := L_SQL || '   AND WP.OUTPUT_ORDER <> ''999''';
        L_SQL := L_SQL || '   AND WP.OUTPUT_ORDER IS NOT NULL';
        L_SQL := L_SQL || '   AND WP.LOGICAL_DEL_FLG = ''0''';
        L_SQL := L_SQL || ' ORDER BY WP.OUTPUT_ORDER';
        OPEN rstpostlist FOR L_SQL;
        rst_code := MSG_SUCCESS;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := MSG_UNKNOWN;
    END PRC_GET_POST_LIST;
    
    -- ---------------------------------------------------------
    -- システム名        ： 受付紹介システム
    -- プロシージャID   ： PRC_GET_POST_INFO
    -- プロシージャ名   ： 店舗情報取得
    -- 引数           ： param_kaisya_cd     IN VARCHAR2,        -- 会社コード
    --                ： param_post_cd       IN VARCHAR2         -- 店舗コード
    --                ： rstpostlist            OUT typCSR       -- 結果セット
    --                ： rst_code            OUT VARCHAR2        -- 結果区分
    -- 初版作成日/作成者  ： 2014/06/23 Ver.1.0.0 SYS_趙雲剛
    -- 変更情報
    -- ---------------------------------------------------------
    PROCEDURE PRC_GET_POST_INFO
    (
        param_kaisya_cd    IN VARCHAR2    -- 会社コード
       ,param_post_cd      IN VARCHAR2    -- 店舗コード
       ,rstpostinfo        OUT typCSR
       ,rst_code           OUT VARCHAR2
    ) IS
    BEGIN
        OPEN rstpostinfo FOR 
          SELECT PKG_JUSYO.FNC_GET_POST_KEN(param_kaisya_cd, param_post_cd) AS POST_JUSYO_CD,
          PKG_JUSYO.FNC_GET_POST_CITY(param_kaisya_cd, param_post_cd) AS POST_SHIKUGUN_CD
          FROM DUAL;
        rst_code := MSG_SUCCESS;
    EXCEPTION
        WHEN OTHERS THEN
            rst_code := MSG_UNKNOWN;
    END PRC_GET_POST_INFO;
    
  -- ---------------------------------------------------------
  -- システム名      ： 受付紹介システム
  -- プロシージャID  ： PRO_GET_OSHIRASE_INFO
  -- プロシージャ名  ： お知らせ情報データ取得
  -- 引数            ： param_kaisyacd    　  IN   VARCHAR2         -- 会社コード
  --                 ： param_authority_cd   IN   VARCHAR2    -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
  --                 ： param_sisya_blk_cd   IN   VARCHAR2    -- 支社ブロックコード
  --                 ： param_postcd     　　IN   VARCHAR2        　 -- 店舗コード
  --                 ： param_tantoucd      IN   VARCHAR2        　   -- 担当者コード
  --                 ： out_oshirase             OUT  VARCHAR2  　　 -- お知らせ情報
  --                 ： rst_msg             OUT  VARCHAR2  　　  -- エラーメッセージ
  --                 ： rst_code           OUT  VARCHAR2  　    -- メッセージID
  -- 戻り値          ： 0000000  ： 成功
  --                 ： ERR9999  ： アプリケーションエラー
  -- 処理概要        ： 受付状況データ取得
  -- 初版作成日/作成者 ： 2014/06/23 Ver.1.0.0 SYS_趙雲剛
  -- 変更情報          ：
  -- ---------------------------------------------------------
  PROCEDURE PRO_GET_OSHIRASE_INFO
  (
     PARAM_KAISYACD  IN VARCHAR2 -- 会社コード
    ,PARAM_AUTHORITY_CD IN VARCHAR2 -- ユーザー権限コード(0:通常 1:ブロック 2:本社担当者) 
    ,PARAM_SISYA_BLK_CD IN VARCHAR2 -- 支社ブロックコード
    ,PARAM_POSTCD    IN VARCHAR2 -- 店舗コード
    ,PARAM_TANTOUCD  IN VARCHAR2 -- 担当者コード
    ,OUT_OSHIRASE    OUT TYPCSR -- お知らせ情報
    ,rst_msg         OUT VARCHAR2 -- エラーメッセージ
    ,rst_code        OUT VARCHAR2 -- メッセージID
  ) IS
  LC_SQL1_CNT         NUMBER(10) := 0;
  LC_SQL2_CNT         NUMBER(10) := 0;
  LC_SQL3_CNT         NUMBER(10) := 0;
  LC_SQLUKETUKENO     VARCHAR2(10000) := '';
  L_SQL               VARCHAR2(12000); -- SQL文
  BEGIN
    
      LC_SQLUKETUKENO := '(SELECT UMV.UKETUKE_NO ';
      LC_SQLUKETUKENO := LC_SQLUKETUKENO || ' FROM UMV_RAITEN_UKETUKE UMV ';
      IF PARAM_POSTCD IS NULL THEN
         LC_SQLUKETUKENO := LC_SQLUKETUKENO || ' JOIN TOKEN.WM_POST WP ON (';
         LC_SQLUKETUKENO := LC_SQLUKETUKENO || ' UMV.KAISYA_CD = WP.KAISYA_CD ';
        LC_SQLUKETUKENO := LC_SQLUKETUKENO || '  AND UMV.UKETUKE_POST_CD = WP.POST_CD ';
        IF PARAM_AUTHORITY_CD = '1' THEN
          LC_SQLUKETUKENO := LC_SQLUKETUKENO || '  AND WP.SISYA_BLK_CD = ''' || PARAM_SISYA_BLK_CD || '''';
        END IF;
        LC_SQLUKETUKENO := LC_SQLUKETUKENO || '  AND WP.OUTPUT_ORDER <> ''999'' ';
        LC_SQLUKETUKENO := LC_SQLUKETUKENO || '  AND WP.OUTPUT_ORDER IS NOT NULL ';
        LC_SQLUKETUKENO := LC_SQLUKETUKENO || '  AND WP.LOGICAL_DEL_FLG = ''0'' ';
        LC_SQLUKETUKENO := LC_SQLUKETUKENO || '  ) ';
      END IF;
      LC_SQLUKETUKENO := LC_SQLUKETUKENO || ' WHERE UMV.KAISYA_CD = ''' || PARAM_KAISYACD || '''';
      LC_SQLUKETUKENO := LC_SQLUKETUKENO || '  AND UV.UKETUKE_NO = UMV.UKETUKE_NO ';
      IF PARAM_POSTCD IS NOT NULL THEN
        LC_SQLUKETUKENO := LC_SQLUKETUKENO || '  AND UMV.UKETUKE_POST_CD = '''|| PARAM_POSTCD || '''';--　仲介店舗コードが存在しない、一応受付店舗コードを使用
        IF PARAM_AUTHORITY_CD = '0' AND PARAM_TANTOUCD IS NOT NULL THEN
           LC_SQLUKETUKENO := LC_SQLUKETUKENO || ' AND UMV.CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD || '''';
        END IF;
      END IF;
      LC_SQLUKETUKENO := LC_SQLUKETUKENO || '  AND UMV.SINCHOKU_CD IN (''1'',''2'',''3'') ';
      LC_SQLUKETUKENO := LC_SQLUKETUKENO || '  AND UMV.LOGICAL_DEL_FLG = ''0'') ';
      

      L_SQL := 'SELECT COUNT(*)';
      L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV';
      L_SQL := L_SQL || ' WHERE UV.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
      L_SQL := L_SQL || '   AND UV.UKETUKE_NO = UV.KENSAKU_UKETUKE_NO';
      L_SQL := L_SQL || '   AND EXISTS ' || LC_SQLUKETUKENO;
      IF PARAM_AUTHORITY_CD = '0' AND PARAM_TANTOUCD IS NOT NULL THEN
         L_SQL := L_SQL || '   AND UV.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
      END IF;
      L_SQL := L_SQL || '   AND UV.KIGENGIRE_FLG = ''1''';
      L_SQL := L_SQL || ' ORDER BY UV.KENSAKU_UKETUKE_NO';
      EXECUTE IMMEDIATE L_SQL INTO LC_SQL1_CNT;
      IF LC_SQL1_CNT > 0
      THEN
          --SQL1
          L_SQL := ' SELECT UV.KOKYAKU_NAME KOKYAKUNAME,';
          L_SQL := L_SQL || '        UV.KENSAKU_UKETUKE_NO UKETUKENO,';
          L_SQL := L_SQL || '        UV.KENSAKU_CHUKAI_POST_CD UKETUKE_POST_CD,';
          L_SQL := L_SQL || '        UV.KENSAKU_CHUKAI_TANTOU_CD CHUKAITANTOUCD,';
          L_SQL := L_SQL || '        UV.HOMEMATE_BUKKEN_NO BUKKENNO,';
          L_SQL := L_SQL || '        UV.HEYA_NO HEYANO,';
          L_SQL := L_SQL || '        UV.BUKKEN_PARKING_KB PARKINGKBN,';
          L_SQL := L_SQL || '        TO_CHAR(UV.UPD_DATE_SHOUKAI_BUKKEN, ''YYYYMMDDHH24MI'') UPDDATE,';
          L_SQL := L_SQL || '        ''1'' OSHIRASEKBN';
          L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV';
          L_SQL := L_SQL || ' WHERE UV.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
          L_SQL := L_SQL || '   AND UV.UKETUKE_NO = UV.KENSAKU_UKETUKE_NO';
          L_SQL := L_SQL || '   AND EXISTS ' || LC_SQLUKETUKENO;
          IF PARAM_AUTHORITY_CD = '0' AND PARAM_TANTOUCD IS NOT NULL THEN
             L_SQL := L_SQL || '   AND UV.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
          END IF;
          L_SQL := L_SQL || '   AND UV.KIGENGIRE_FLG = ''1''';
          L_SQL := L_SQL || ' ORDER BY UV.KENSAKU_UKETUKE_NO';
          OPEN OUT_OSHIRASE FOR L_SQL;
          rst_code := MSG_SUCCESS;
          RETURN;
      END IF;

      L_SQL := 'SELECT COUNT(*)';
      L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV';
      L_SQL := L_SQL || ' WHERE UV.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
      IF PARAM_AUTHORITY_CD = '0' AND PARAM_TANTOUCD IS NOT NULL THEN
         L_SQL := L_SQL || '   AND UV.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
      END IF;
      L_SQL := L_SQL || '   AND UV.UKETUKE_NO = UV.KENSAKU_UKETUKE_NO';
      L_SQL := L_SQL || '   AND EXISTS ' || LC_SQLUKETUKENO;
      L_SQL := L_SQL || '   AND UV.KARIOSAE_JYUNI = ''1''';
      L_SQL := L_SQL || '   AND UV.KARIOSAEJI_JYUNI <> ''1''';
      L_SQL := L_SQL || ' ORDER BY UV.UKETUKE_NO';
      EXECUTE IMMEDIATE L_SQL INTO LC_SQL2_CNT;
      IF LC_SQL2_CNT > 0
      THEN
          --SQL2
          L_SQL := ' SELECT UV.KOKYAKU_NAME KOKYAKUNAME';
          L_SQL := L_SQL || '     ,UV.UKETUKE_NO UKETUKENO';
          L_SQL := L_SQL || '     ,UV.KENSAKU_CHUKAI_POST_CD UKETUKE_POST_CD';
          L_SQL := L_SQL || '     ,UV.KENSAKU_CHUKAI_TANTOU_CD CHUKAITANTOUCD';
          L_SQL := L_SQL || '     ,UV.HOMEMATE_BUKKEN_NO BUKKENNO';
          L_SQL := L_SQL || '     ,UV.HEYA_NO HEYANO';
          L_SQL := L_SQL || '     ,UV.BUKKEN_PARKING_KB PARKINGKBN';
          L_SQL := L_SQL || '     ,TO_CHAR(UV.UPD_DATE_SHOUKAI_BUKKEN, ''YYYYMMDDHH24MI'') UPDDATE';
          L_SQL := L_SQL || '     ,''2'' OSHIRASEKBN';
          L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV';
          L_SQL := L_SQL || ' WHERE UV.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
          IF PARAM_AUTHORITY_CD = '0' AND PARAM_TANTOUCD IS NOT NULL THEN
             L_SQL := L_SQL || '   AND UV.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
          END IF;
          L_SQL := L_SQL || '   AND UV.UKETUKE_NO = UV.KENSAKU_UKETUKE_NO';
          L_SQL := L_SQL || '   AND EXISTS ' || LC_SQLUKETUKENO;
          L_SQL := L_SQL || '   AND UV.KARIOSAE_JYUNI = ''1''';
          L_SQL := L_SQL || '   AND UV.KARIOSAEJI_JYUNI <> ''1''';
          L_SQL := L_SQL || ' ORDER BY UV.UKETUKE_NO';
          OPEN OUT_OSHIRASE FOR L_SQL;
          rst_code := MSG_SUCCESS;
          RETURN;
      END IF;

      L_SQL := 'SELECT COUNT(*)';
      L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV';
      L_SQL := L_SQL || ' WHERE UV.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
      IF PARAM_AUTHORITY_CD = '0' AND PARAM_TANTOUCD IS NOT NULL THEN
         L_SQL := L_SQL || '   AND UV.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
      END IF;
      L_SQL := L_SQL || '   AND UV.KARIOSAE_KB = ''0''';
      L_SQL := L_SQL || '   AND EXISTS ' || LC_SQLUKETUKENO;
      L_SQL := L_SQL || ' ORDER BY UV.KENSAKU_UKETUKE_NO';
      EXECUTE IMMEDIATE L_SQL INTO LC_SQL3_CNT;
      IF LC_SQL3_CNT > 0
      THEN
          --SQL3
          L_SQL := ' SELECT UV.KARIOSAEKAIJYO_KOKYAKU_NAME KOKYAKUNAME';
          L_SQL := L_SQL || '    ,UV.KENSAKU_UKETUKE_NO UKETUKENO';
          L_SQL := L_SQL || '    ,UV.KENSAKU_CHUKAI_POST_CD UKETUKE_POST_CD';
          L_SQL := L_SQL || '    ,UV.KENSAKU_CHUKAI_TANTOU_CD CHUKAITANTOUCD';
          L_SQL := L_SQL || '    ,UV.HOMEMATE_BUKKEN_NO BUKKENNO';
          L_SQL := L_SQL || '    ,UV.HEYA_NO HEYANO';
          L_SQL := L_SQL || '    ,UV.BUKKEN_PARKING_KB PARKINGKBN';
          L_SQL := L_SQL || '    ,TO_CHAR(UV.UPD_DATE_SHOUKAI_BUKKEN, ''YYYYMMDDHH24MI'') UPDDATE';
          L_SQL := L_SQL || '    ,''3'' OSHIRASEKBN';
          L_SQL := L_SQL || '  FROM UV_KARIOSAE_BUKKEN UV';
          L_SQL := L_SQL || ' WHERE UV.KENSAKU_KAISYA_CD = '''|| PARAM_KAISYACD ||'''';
          IF PARAM_AUTHORITY_CD = '0' AND PARAM_TANTOUCD IS NOT NULL THEN
             L_SQL := L_SQL || '   AND UV.KENSAKU_CHUKAI_TANTOU_CD = '''|| PARAM_TANTOUCD ||'''';
          END IF;
          L_SQL := L_SQL || '   AND UV.KARIOSAE_KB = ''0''';
          L_SQL := L_SQL || '   AND EXISTS ' || LC_SQLUKETUKENO;
          L_SQL := L_SQL || ' ORDER BY UV.KENSAKU_UKETUKE_NO';
          OPEN OUT_OSHIRASE FOR L_SQL;
      ELSE
          OPEN OUT_OSHIRASE FOR SELECT 1 FROM DUAL;
      END IF;
       rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
    END PRO_GET_OSHIRASE_INFO;
    --2014/06/23 趙雲剛 ADD End
    
  -- ---------------------------------------------------------
  -- システム名        ： 受付紹介システム
  -- プロシージャID    ： FNC_GET_MADORI_BUKKEN_SB
  -- プロシージャ名    ： 間取りの取得
  -- 引数              ： PARAM_KAISYA_CD        IN VARCHAR2   -- 会社コード
  --                   ： PARAM_UKETUKE_NO       IN VARCHAR2   --仲介受付no.
  --                   ： PARAM_MAX_SEQ          IN VARCHAR2   --MAXseq.  
  --                   ： PARAM_KIBOU_BUKKEN_SB  IN VARCHAR2   --希望条件区分フラグ
  -- 処理概要          ： 間取りと希望地域の取得
  -- 初版作成日/作成者 ： 2015/02/06 Ver.1.0.0 SYS_郭
  -- 変更情報          ：
  -- ---------------------------------------------------------
 FUNCTION FNC_GET_MADORI_BUKKEN_SB(PARAM_KAISYA_CD  IN VARCHAR2,
                                                PARAM_UKETUKE_NO IN VARCHAR2,
                                                PARAM_MAX_SEQ IN VARCHAR2,
                                                PARAM_KIBOU_BUKKEN_SB IN VARCHAR2)
  RETURN VARCHAR2 IS
  RESULT VARCHAR2(1000);

  BEGIN

  --希望物件区分が1(居住用)の場合、間取り取得
    IF PARAM_KIBOU_BUKKEN_SB = '1' THEN

      FOR CUR IN (SELECT KIBOU_MADORI.MADORI
                    FROM (SELECT KAISYA_CD,
                                 UKETUKE_NO,
                                 PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME('045',
                                                                       KIBOU_MADORI_SB) MADORI,
                                 KIBOU_MADORI_SB
                            FROM UD_KIBOU_MADORI
                           WHERE KAISYA_CD = PARAM_KAISYA_CD
                             AND UKETUKE_NO = PARAM_UKETUKE_NO
                             AND KIBOU_JYOUKEN_RIREKI_SEQ = PARAM_MAX_SEQ
                           ORDER BY TO_NUMBER(KIBOU_MADORI_SB)) KIBOU_MADORI) LOOP
        --間取り結果セット
        RESULT := RESULT || CUR.MADORI || '、';
      
      END LOOP;

    RESULT := RTRIM(RESULT, '、');

  --希望物件区分が2(事業用)の場合、物件種別取得
    ELSIF PARAM_KIBOU_BUKKEN_SB = '2' THEN

      FOR CUR IN (SELECT KIBOU_BUKKEN.BUKKEN_SB_NAME
                    FROM (SELECT KAISYA_CD,
                                 UKETUKE_NO,
                                 PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME('002',
                                                                       KIBOU_BUKKEN_SB) BUKKEN_SB_NAME,
                                 KIBOU_BUKKEN_SB
                            FROM UD_KIBOU_BUKKEN_SB
                           WHERE KAISYA_CD = PARAM_KAISYA_CD
                             AND UKETUKE_NO = PARAM_UKETUKE_NO
                             AND KIBOU_JYOUKEN_RIREKI_SEQ = PARAM_MAX_SEQ
                           ORDER BY TO_NUMBER(KIBOU_BUKKEN_SB)) KIBOU_BUKKEN) LOOP
        --物件種別結果セット
        RESULT := RESULT || CUR.BUKKEN_SB_NAME || '/';
      
      END LOOP;

      RESULT := RTRIM(RESULT, '/');
  
    IF RESULT IS NULL THEN
      RESULT := '事業用';
    ELSE
      RESULT := '事業用(' || RESULT || ')';
    END IF;

  --希望物件区分が3(駐車場)の場合、物件種別取得
    ELSIF PARAM_KIBOU_BUKKEN_SB = '3' THEN

      FOR CUR IN (SELECT KIBOU_BUKKEN.BUKKEN_SB_NAME
                    FROM (SELECT KAISYA_CD,
                                 UKETUKE_NO,
                                 PKG_UM_COMMON.FNC_GET_KOUMOKU_SB_NAME('024',
                                                                       KIBOU_BUKKEN_SB) BUKKEN_SB_NAME,
                                 KIBOU_BUKKEN_SB
                            FROM UD_KIBOU_BUKKEN_SB
                           WHERE KAISYA_CD = PARAM_KAISYA_CD
                             AND UKETUKE_NO = PARAM_UKETUKE_NO
                             AND KIBOU_JYOUKEN_RIREKI_SEQ = PARAM_MAX_SEQ
                           ORDER BY TO_NUMBER(KIBOU_BUKKEN_SB)) KIBOU_BUKKEN) LOOP
        --物件種別結果セット
        RESULT := RESULT || CUR.BUKKEN_SB_NAME || '/';
      
      END LOOP;

      RESULT := RTRIM(RESULT, '/');
  
    IF RESULT IS NULL THEN
      RESULT := '駐車場';
    ELSE
      RESULT := '駐車場(' || RESULT || ')';
    END IF;
      
  END IF;

    RETURN(RESULT);

  END FNC_GET_MADORI_BUKKEN_SB; 
 
  -- ---------------------------------------------------------
  -- システム名        ： 受付紹介システム
  -- プロシージャID    ： FNC_GET_KIBOU_JYOUKEN
  -- プロシージャ名    ： 間取りの取得
  -- 引数              ： PARAM_KAISYA_CD      IN VARCHAR2    -- 会社コード
  --                   ： PARAM_UKETUKE_NO     IN VARCHAR2    --仲介受付no.
  --                   ： PARAM_MAX_SEQ        IN IN VARCHAR2 --MAXseq.  
  --                   ： PARAM_KIBOU_JYOUKEN_CD  IN VARCHAR2 --希望条件フラグ
  -- 処理概要          ： 希望地域の取得
  -- 初版作成日/作成者 ： 2015/02/06 Ver.1.0.0 SYS_郭
  -- 変更情報          ：
  -- ---------------------------------------------------------
 FUNCTION FNC_GET_KIBOU_JYOUKEN(PARAM_KAISYA_CD  IN VARCHAR2,
                                                PARAM_UKETUKE_NO IN VARCHAR2,
                                                PARAM_MAX_SEQ IN VARCHAR2,
                                                PARAM_KIBOU_JYOUKEN_CD IN VARCHAR2)
 RETURN VARCHAR2 IS
  RESULT VARCHAR2(1000);
  --希望地域エリア
  KIBOU_AREA_NAME UD_KIBOU_AREA.SITEI_AREA_NAME%TYPE;
  --希望目的駅
  KIBOU_MOKUTEKI_EKI UD_KIBOU_TUUKIN_TIME.KIBOU_MOKUTEKI_EKI_NAME%TYPE;
  --希望時間
  KIBOU_TIME UD_KIBOU_TUUKIN_TIME.KIBOU_TIME_CD%TYPE;
  --希望施設
  KIBOU_SISETU_NAME UD_KIBOU_SISETU.SISETU_NAME%TYPE;

BEGIN

  --希望地域(エリア指定)
  IF PARAM_KIBOU_JYOUKEN_CD = '1' THEN
  
    SELECT SITEI_AREA_NAME
      INTO KIBOU_AREA_NAME
      FROM UD_KIBOU_AREA
     WHERE KAISYA_CD = PARAM_KAISYA_CD
       AND UKETUKE_NO = PARAM_UKETUKE_NO
       AND KIBOU_JYOUKEN_RIREKI_SEQ = PARAM_MAX_SEQ;
    --希望地域(エリア指定)結果セット
    RESULT := KIBOU_AREA_NAME;
  
    --希望地域(市区郡指定) 
  ELSIF PARAM_KIBOU_JYOUKEN_CD = '2' THEN
  
    FOR CUR IN (SELECT KIBOU_CITY.JUSYO
                  FROM (SELECT U.KAISYA_CD,
                               U.UKETUKE_NO,
                               M.PREFECTURE || M.CITY AS JUSYO,
                               M.JIS_JUSYO_CD
                          FROM UD_KIBOU_CITY U, S21MB.M_JUSYO M
                         WHERE U.KAISYA_CD = PARAM_KAISYA_CD
                           AND U.UKETUKE_NO = PARAM_UKETUKE_NO
                           AND U.KIBOU_JYOUKEN_RIREKI_SEQ = PARAM_MAX_SEQ
                           AND U.JIS_JUSYO_CD = M.JIS_JUSYO_CD
                         GROUP BY U.KAISYA_CD,
                                  U.UKETUKE_NO,
                                  M.JIS_JUSYO_CD,
                                  M.PREFECTURE,
                                  M.CITY) KIBOU_CITY
                 ORDER BY TO_NUMBER(KIBOU_CITY.JIS_JUSYO_CD)) LOOP
      --希望地域(市区郡指定)結果セット
      RESULT := RESULT || CUR.JUSYO || '、';
    
    END LOOP;
  
    --希望駅指定
  ELSIF PARAM_KIBOU_JYOUKEN_CD = '3' THEN
  
    FOR CUR IN (SELECT KIBOU_STATION.EKINAME
                  FROM (SELECT KAISYA_CD,
                               UKETUKE_NO,
                               KIBOU_EKI_CD,
                               PKG_STATION.FNC_GET_EKI_NAME_NO_ROSEN(KIBOU_EKI_CD) AS EKINAME
                          FROM UD_KIBOU_STATION
                         WHERE KAISYA_CD = PARAM_KAISYA_CD
                           AND UKETUKE_NO = PARAM_UKETUKE_NO
                           AND KIBOU_JYOUKEN_RIREKI_SEQ = PARAM_MAX_SEQ
                           AND KIBOU_ROSEN_CD = '1'
                         ORDER BY KIBOU_EKI_CD) KIBOU_STATION) LOOP
      --希望駅指定結果セット
      RESULT := RESULT || CUR.EKINAME || '、';
    
    END LOOP;
  
    --通勤通学時間 
  ELSIF PARAM_KIBOU_JYOUKEN_CD = '4' THEN
  
    --希望通勤・通学時間データ
    SELECT KIBOU_MOKUTEKI_EKI_NAME AS MOKUTEKIEKICD --希望目的駅           
          ,KIBOU_TIME_CD AS KIBOUTIMECD --希望時間
      INTO KIBOU_MOKUTEKI_EKI, KIBOU_TIME
      FROM UD_KIBOU_TUUKIN_TIME
     WHERE KAISYA_CD = PARAM_KAISYA_CD
       AND UKETUKE_NO = PARAM_UKETUKE_NO
       AND KIBOU_JYOUKEN_RIREKI_SEQ = PARAM_MAX_SEQ;
  
    --通勤通学時間結果セット
    RESULT := RESULT || KIBOU_MOKUTEKI_EKI || '駅まで' || KIBOU_TIME || '分以内';
  
    --学校・施設・バス停等
  ELSIF PARAM_KIBOU_JYOUKEN_CD = '5' THEN
  
    SELECT SISETU_NAME
      INTO KIBOU_SISETU_NAME
      FROM UD_KIBOU_SISETU
     WHERE KAISYA_CD = PARAM_KAISYA_CD
       AND UKETUKE_NO = PARAM_UKETUKE_NO
       AND KIBOU_JYOUKEN_RIREKI_SEQ = PARAM_MAX_SEQ;
    --希望施設結果セット
    RESULT := KIBOU_SISETU_NAME;
  
  END IF;

  RESULT := RTRIM(RESULT, '、');

  --希望条件の戻り値を戻る
  RETURN(RESULT);

  END FNC_GET_KIBOU_JYOUKEN; 
  
--2017/09/19  張虎強 ADD START (案件C41004)法人仲介担当者登録機能追加対応  
  -- ---------------------------------------------------------
  -- システム名            ： 受付紹介システム
  -- プロシージャID         ： PRC_GET_HOUJIN_INFO
  -- プロシージャ名         ： 法人仲介担当者データ取得
  -- 引数              　： param_kigyou_cd         　IN VARCHAR2     　-- 会社コード
  --                  　： param_kigyou_nm          　IN VARCHAR2     -- 顧客種別
  --                  　： pcurList                  IN typCSR       -- 検索結果
  --                  　： rst_code                  OUT VARCHAR2    -- エラーキー値
  --                  　： rst_msg                   OUT VARCHAR2    -- エラーメッセージ
  -- 初版作成日/作成者    ： 2017/09/19 Ver.1.0.0 SYS_張虎強
  -- 変更情報            ： 
  -- ---------------------------------------------------------
  PROCEDURE PRC_GET_HOUJIN_INFO
  (
    param_kigyou_cd          IN VARCHAR2,
    param_kigyou_nm          IN VARCHAR2,
    pcurList                 OUT typCSR,
    rst_code                 OUT VARCHAR2,
    rst_msg                  OUT VARCHAR2
  ) IS

    mySql  VARCHAR2(2800) := '';
    mySqlW VARCHAR2(2500) := '';
    mySqlR VARCHAR2(2500) := '';
  BEGIN
    mySql := 'SELECT WMK.KIGYO_CD AS KIGYO_CD,';
    mySql := mySql || ' WMK.KIGYO_NAME AS KIGYO_NAME ';
    mySql := mySql || ' FROM TOKEN.WM_KIGYO WMK';
    --企業コード、企業名
    IF param_kigyou_cd IS NOT NULL AND param_kigyou_nm IS NOT NULL THEN
      mySqlW := ' WHERE WMK.KIGYO_CD LIKE ''' || param_kigyou_cd || '%''';
      mySqlW := mySqlW || ' AND WMK.KIGYO_NAME LIKE ''%' || param_kigyou_nm || '%''';
    --企業名
    ELSIF param_kigyou_cd IS NULL THEN
     --2018/03/22 H.Sato UPD START
     -- mySqlW := ' WHERE WMK.KIGYO_NAME LIKE ''%' || param_kigyou_nm || '%''';
      mySqlW := ' WHERE UTL_I18N.TRANSLITERATE(UPPER(TO_MULTI_BYTE(WMK.KIGYO_NAME)),''kana_fwkatakana'') ';
      mySqlW := mySqlW || ' LIKE ''%'' || UTL_I18N.TRANSLITERATE(UPPER(TO_MULTI_BYTE(''' || param_kigyou_nm || ''')),''kana_fwkatakana'') || ''%''';
     --2018/03/22 H.Sato UPD END
    --企業コード
    ELSIF param_kigyou_nm IS NULL THEN
      mySqlW := ' WHERE WMK.KIGYO_CD LIKE ''' || param_kigyou_cd || '%''';
    END IF;
    mySqlR := ' AND WMK.LOGICAL_DEL_FLG = ''0''';
    mySqlR := mySqlR || ' ORDER BY WMK.KIGYO_CD';

    mySql := mySql || mySqlW || mySqlR;
    OPEN pcurList FOR mySql;

    rst_code := MSG_SUCCESS;
  EXCEPTION
    WHEN OTHERS THEN
      rst_code := MSG_UNKNOWN;
      rst_msg  := SQLERRM;
      dbms_output.put_line('エラーコード：' || rst_code);
      dbms_output.put_line('エラーメッセージ：' || rst_msg);
  END PRC_GET_HOUJIN_INFO;
--2017/09/19  張虎強 ADD END (案件C41004)法人仲介担当者登録機能追加対応

END PKG_UKG01010;
/