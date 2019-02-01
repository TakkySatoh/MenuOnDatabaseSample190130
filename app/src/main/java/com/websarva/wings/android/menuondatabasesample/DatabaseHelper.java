package com.websarva.wings.android.menuondatabasesample;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    /**
     * データベース名、バージョン情報を、それぞれ定数として定義
     */
    private static final String DATABASE_NAME = "menulist.db";
    private static final int DATABASE_VERSION = 1;

    /**
     * コンストラクタの設定
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
//        テーブル作成用SQL文字列の作成 (定食メニューについて)
            StringBuilder sb = new StringBuilder();
            sb.append("create table if not exists menu_teishoku (");
            sb.append("_id integer primary key,");
            sb.append("name text,");
            sb.append("price integer,");
            sb.append("desc text");
            sb.append(");");
            String sqlTeishoku = sb.toString();
//        テーブル作成用SQL文字列の作成 (カレーメニューについて)
            sb = new StringBuilder();
            sb.append("create table if not exists menu_curry (");
            sb.append("_id integer primary key,");
            sb.append("name text,");
            sb.append("price integer,");
            sb.append("desc text");
            sb.append(");");
            String sqlCurry = sb.toString();
//        SQLを実行
            db.execSQL(sqlTeishoku);
            db.execSQL(sqlCurry);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d("TAG","DB create Error: "+e.toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
