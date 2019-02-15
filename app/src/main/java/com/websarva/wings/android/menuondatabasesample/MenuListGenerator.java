package com.websarva.wings.android.menuondatabasesample;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuListGenerator {
    List<Map<String, Object>> menuList = new ArrayList<>();
    Map<String, Object> menu = new HashMap<>();
    InputStream is;
    InputStreamReader isr;
    BufferedReader br;

    /**
     * データベースよりメニューリストを生成
     *
     * @param flag (String型、メニュー種別を決定)
     * @return mMenuList (List型、メニューリストを呼び出し元へ返す)
     */
    public static List<Map<String, Object>> getMenuList(String flag) {
//        メニューデータ格納先のList、メニューデータの各要素の格納先Mapの各インスタンスを定義
        List<Map<String, Object>> menuList = new ArrayList<>();
        Map<String, Object> menu = new HashMap<>();
//        テーブル名格納用String変数を定義し、引数「flag」の文字に応じて文字列を代入
//        所定の文字列を取得できなかった場合に備え、エラーメッセージの返却も定義
        String tbName;
        String category;
        switch (flag) {
            case "T":
                tbName = "menu_teishoku";
                category = "T";
                break;
            case "C":
                tbName = "menu_curry";
                category = "C";
                break;
            default:
//        メニューのRecyclerViewにエラーメッセージを表示するため、各要素を代入したListを生成しフィールド変数へ格納
//        メニューの生成不可通知を呼び出し元へreturn
                menu.put("name", "ファイル形式が正しくありません。");
                menu.put("price", 0);
                menuList.add(menu);
//                mMenuList = menuList;
                return menuList;
        }
//        DatabaseHelperインスタンス、Cursorインスタンスを生成
        DatabaseHelper dbh = new DatabaseHelper(getApplicationContext());
        Cursor cursor;
//        データベースへの接続を開始
        try (SQLiteDatabase db = dbh.getWritableDatabase()) {
//            データベースよりtbNameに格納したテーブル名と等しいテーブルを探し出し、全データをCursorインスタンスへ格納
            cursor = db.query(tbName, null, null, null, null, null, null);
//            Cursorインスタンスがnullでないことを確認し、以下の処理を実施
            if (cursor != null) {
//            Cursorインスタンスの内部テーブルがなくなるまで、以下の処理を実施
                while (cursor.moveToNext()) {
//                    Cursorインスタンスの要素数が0でない場合は、以下の処理を実施
                    menu = new HashMap<>();
                    // 各定食メニューをHashMapに登録し、その後menuListの各要素へ登録
                    menu.put("category", category);
                    menu.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
                    menu.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    menu.put("price", cursor.getInt(cursor.getColumnIndex("price")));
                    menu.put("desc", cursor.getString(cursor.getColumnIndex("desc")));
                    menuList.add(menu);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "DB Error: " + e.toString());
        }
//        生成したメニューリストをフィールド変数へ格納
//        this.mMenuList = menuList;

//        メニューの正常生成通知を呼び出し元へreturn
        return menuList;
    }

    public List<Map<String, Object>> setMenuList(Context context, String csvFileName) {
//        メニュー記載のCSVファイルをassetsディレクトリより取得するインスタンスを呼び出し
        AssetManager am = context.getResources().getAssets();
        try {
//            各ストリームリーダをオープンし、
            is = am.open(csvFileName);
            isr = new InputStreamReader(is, "Shift_JIS");
            br = new BufferedReader(isr);

            // 読み込み行
            String line;

            // 1行ずつ読み込みを行う
            line = br.readLine();
            // 行の内容をカンマで分割し、1列ごとに配列に格納する
            String[] array = line.split(",", 0);
            // データ数、CSVファイル1行目の各要素が以下の条件を満たしていない場合に、以下の処理を実施
            if (array.length != 5 | !array[0].equals("_id") | !array[1].equals("category") | !array[2].equals("name") | !array[3].equals("price") | !array[4].equals("desc")) {
//                メニューのRecyclerViewにエラーメッセージを表示するため、各要素を代入したListを生成しreturn
                menu.put("name", "ファイル形式が正しくありません。");
                menu.put("price", 0);
                menuList.add(menu);
                return menuList;
            }

            // 行が空欄になるまで、以下の処理を実行
            while ((line = br.readLine()) != null) {
                // 行の内容をカンマで分割し、1列ごとに配列に格納する
                array = line.split(",", 0);
//                メニュー種別判定: 配列要素1の文字列が引数"flag"と一致する場合のみ、以下の処理を実施
//                if (array[1].equals(flag)) {
                // データ数をチェックしたあと、SightseeingSpotのインスタンスを生成し、tempSpotArray配列へ代入
                menu = new HashMap<>();
                // 各定食メニューをHashMapに登録し、その後menuListの各要素へ登録
                menu.put("category", array[1]);
                menu.put("name", array[2]);
                menu.put("price", Integer.parseInt(array[3]));
                menu.put("desc", array[4]);
                menuList.add(menu);
//                }
                // 次の行を読む
//                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                isr.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return menuList;
    }
}
