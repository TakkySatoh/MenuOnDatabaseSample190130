package com.websarva.wings.android.menuondatabasesample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
//        テーブル作成用SQL文字列の作成
        StringBuilder sb = new StringBuilder();
        sb.append("create table menu_teishoku (");
        sb.append("_id integer primary key,");
        sb.append("category text,");
        sb.append("name text,");
        sb.append("price int,");
        sb.append("desc text");
        sb.append(");");
        String sql = sb.toString();
//        SQLを実行
        db.execSQL(sql);
//        テーブル作成用SQL文字列の作成
        sb = new StringBuilder();
        sb.append("create table menu_curry (");
        sb.append("_id integer primary key,");
        sb.append("category text,");
        sb.append("name text,");
        sb.append("price int,");
        sb.append("desc text");
        sb.append(");");
        sql = sb.toString();
//        SQLを実行
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
