package com.websarva.wings.android.menuondatabasesample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * データベース名、バージョン情報を、それぞれ定数として定義
     */
    private static final String DATABASE_NAME = "menulist.db";
    private static final String CSV_FILE_NAME = "menulist.csv";
    private static final int DATABASE_VERSION = 1;

    private final Context context;
    private final String TABLE_TEISHOKU = "menu_teishoku";
    private final String TABLE_CURRY = "menu_curry";
    private final String FLAG_TEISHOKU = "T";
    private final String FLAG_CURRY = "C";

    /**
     * コンストラクタの設定
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
//        テーブル作成用SQL文字列の作成 (定食メニューについて)
            String sqlTeishoku = createMenuTable(TABLE_TEISHOKU);
//        テーブル作成用SQL文字列の作成 (カレーメニューについて)
            String sqlCurry = createMenuTable(TABLE_CURRY);
//        SQLを実行
            db.execSQL(sqlTeishoku);
            db.execSQL(sqlCurry);

//        作成したテーブルへCSVファイルよりメニューデータをインポート
//        Assetsディレクトリ上のCSVファイルへアクセスし、データをListへ格納
            List<Map<String, Object>> menuList = new CSVReader().setMenuList(this.context, CSV_FILE_NAME);
            Map<String, Object> menu;
//            格納したListすべてに対し、以下の処理を実施
            for (int i = 0; i < menuList.size(); i++) {
//                List中の要素より"category"タグの付与された文字をString型変数"flag"へ格納
//                flagに合わせてテーブル名を定数より格納する変数"tbName"も併せて用意
                menu = menuList.get(i);
                String flag = (String) menu.get("category");
                String tbName;
//                flagの種別に合わせて、各category毎のメニューをそれぞれに対応するテーブルへ格納
                switch (flag) {
                    case FLAG_TEISHOKU:
                        tbName = TABLE_TEISHOKU;
                        list2Table(db, menu, tbName);
                        break;
                    case FLAG_CURRY:
                        tbName = TABLE_CURRY;
                        list2Table(db, menu, tbName);
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("TAG", "DB create Error: " + e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 更新対象のテーブル
        final String targetTable = "table1";
        db.beginTransaction();
        try {
// 元カラム一覧
            final List<String> columns = getColumns(db, targetTable);
// 初期化
            db.execSQL("ALTER TABLE " + targetTable + " RENAME TO temp_"
                    + targetTable);
            onCreate(db);
// 新カラム一覧
            final List<String> newColumns = getColumns(db, targetTable);

// 変化しないカラムのみ抽出
            columns.retainAll(newColumns);

// 共通データを移す。(OLDにしか存在しないものは捨てられ, NEWにしか存在しないものはNULLになる)
            final String cols = join(columns, ",");
            db.execSQL(String.format(
                    "INSERT INTO %s (%s) SELECT %s from temp_%s", targetTable,
                    cols, cols, targetTable));
// 終了処理
            db.execSQL("DROP TABLE temp_" + targetTable);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 指定したテーブルのカラム名リストを取得する。
     *
     * @param db        SQLiteDatabase
     * @param tableName 呼び出し元テーブル名
     * @return カラム名のリスト
     */
    private static List<String> getColumns(SQLiteDatabase db, String tableName) {
        List<String> ar = null;
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
            if (c != null) {
                ar = new ArrayList<String>(Arrays.asList(c.getColumnNames()));
            }
        } finally {
            if (c != null)
                c.close();
        }
        return ar;
    }

    /**
     * 文字列を任意の区切り文字で連結する。
     *
     * @param list  文字列のリスト
     * @param delim 区切り文字
     * @return 連結後の文字列
     */
    private static String join(List<String> list, String delim) {
        final StringBuilder buf = new StringBuilder();
        final int num = list.size();
        for (int i = 0; i < num; i++) {
            if (i != 0)
                buf.append(delim);
            buf.append((String) list.get(i));
        }
        return buf.toString();
    }

    /**
     * テーブル生成用SQL文の生成
     *
     * @param tableName 作成予定のテーブル名
     * @return SQL文
     */
    private String createMenuTable(String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("create table if not exists " + tableName + " (");
        sb.append("_id integer primary key,");
        sb.append("name text,");
        sb.append("price integer,");
        sb.append("desc text");
        sb.append(");");
        return sb.toString();
    }

    /**
     * List中のデータをテーブルへ格納
     *
     * @param db     SQLiteDatabase
     * @param menu   CSVファイルよりインポートしたメニューの格納先List
     * @param tbName ターゲットのテーブル名
     */
    private void list2Table(SQLiteDatabase db, Map<String, Object> menu, String tbName) {
        ContentValues values = new ContentValues();
        values.put("name", (String) menu.get("name"));
        values.put("price", (Integer) menu.get("price"));
        values.put("desc", (String) menu.get("desc"));
        long id = db.insert(tbName, null, values);
//                            Log.d("TAG", "Insert TAG: " + id);
    }

}
