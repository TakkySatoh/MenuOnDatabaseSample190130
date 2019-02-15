package com.websarva.wings.android.menuondatabasesample;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReader {
    List<Map<String, Object>> menuList = new ArrayList<>();
    Map<String, Object> menu = new HashMap<>();
    InputStream is;
    InputStreamReader isr;
    BufferedReader br;

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
