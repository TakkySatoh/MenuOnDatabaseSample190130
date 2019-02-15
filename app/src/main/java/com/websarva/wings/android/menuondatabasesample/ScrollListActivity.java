package com.websarva.wings.android.menuondatabasesample;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScrollListActivity extends AppCompatActivity {

    private TextView mTvMenuCategory1;
    private TextView mTvMenuCategory2;
    private List<Map<String, Object>> mMenuList;
    private List<Map<String, Object>> mTeishokuMenuList;
    private List<Map<String, Object>> mCurryMenuList;
    private RecyclerView mRvTeishokuMenu;
    private RecyclerView mRvCurryMenu;
    private RecyclerListAdapter mTeishokuAdapter;
    private RecyclerListAdapter mCurryAdapter;
    private ItemTouchHelper mTeishokuHelper;
    private ItemTouchHelper mCurryHelper;
    private String mFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbarの画面部品を取得
        Toolbar toolbar = findViewById(R.id.toolbar);
//        Toolbarインスタンスに対し、ロゴとして表示する画像を設定
        toolbar.setLogo(R.mipmap.ic_launcher);
//        Toolbarインスタンスをアクションバーに設定
//        (※事前にstyles.xmlのstyleタグ中「parent」属性を"NoActionBar"に設定しないとエラーが出る)
        setSupportActionBar(toolbar);

//        CollapsingToolbar導入時は、タイトルに装飾を施すため、以下の設定を記述する
//        CollapsingToolbarLayoutの画面部品を取得
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbarLayout);
//        CollapsingToolbarLayoutインスタンスに対し、タイトル文字列、タイトル文字色(通常時/縮小時)を設定
//        (※CollapsingToolbarLayoutのsetTitle()は引数の型に"CharSequense"の指定あり ⇒R値で指定した値からString文字列を取り出してから渡す)
        toolbarLayout.setTitle(getString(R.string.toolbar_title));
        toolbarLayout.setExpandedTitleColor(Color.WHITE);
        toolbarLayout.setCollapsedTitleTextColor(Color.LTGRAY);

//        RecyclerViewの画面部品をインスタンス化
        mTvMenuCategory1 = findViewById(R.id.tvMenuCategory1);
        mTvMenuCategory2 = findViewById(R.id.tvMenuCategory2);
        mRvTeishokuMenu = findViewById(R.id.rvTeishokuMenu);
        mRvCurryMenu = findViewById(R.id.rvCurryMenu);

//        LinearLayoutManagerのインスタンスを各RecyclerViewごとに生成
//        RecyclerViewインスタンスのレイアウトをLinearLayoutへ設定
        LinearLayoutManager layout = new LinearLayoutManager(ScrollListActivity.this);
        mRvTeishokuMenu.setLayoutManager(layout);
//        リストに対し区切り線を設定
        DividerItemDecoration decoration = new DividerItemDecoration(ScrollListActivity.this, layout.getOrientation());
        mRvTeishokuMenu.addItemDecoration(decoration);
        layout = new LinearLayoutManager(ScrollListActivity.this);
        mRvCurryMenu.setLayoutManager(layout);
//        リストに対し区切り線を設定
        decoration = new DividerItemDecoration(ScrollListActivity.this, layout.getOrientation());
        mRvCurryMenu.addItemDecoration(decoration);

        String[] items = getResources().getStringArray(R.array.menu_list);
//        switch(item) {
//            case "定食":
//                mFlag = "T";
//                mTeishokuMenuList = getMenuList(mFlag);
//                break;
//            case "カレー":
//                mFlag = "C";
//                mCurryMenuList = getMenuList(mFlag);
//                break;
//        }
        mTvMenuCategory1.setText(items[0]);
//        メニューリスト生成用アダプタのインスタンスを生成し、RecyclerViewへリストを登録
//        メニューリスト生成用アダプタは内部クラスとして別途定義
        mFlag = "T";
        mTeishokuMenuList = getMenuList(mFlag);
        mTeishokuAdapter = new RecyclerListAdapter(this, mTeishokuMenuList);
        mRvTeishokuMenu.setAdapter(mTeishokuAdapter);
//        ItemTouchHelperインスタンスを新規生成
//        引数にネストクラス「Callback」のインスタンスを新規生成の上指定
        mTeishokuHelper = new ItemTouchHelper(new ItemTouchHelperCallback(mTeishokuAdapter));
//        ItemTouchHelper(のインスタンス)をRecyclerViewインスタンスへ追加
        mTeishokuHelper.attachToRecyclerView(mRvTeishokuMenu);

        mTvMenuCategory2.setText(items[1]);
//        メニューリスト生成用アダプタのインスタンスを生成し、RecyclerViewへリストを登録
//        メニューリスト生成用アダプタは内部クラスとして別途定義
        mFlag = "C";
        mCurryMenuList = getMenuList(mFlag);
        mCurryAdapter = new RecyclerListAdapter(this, mCurryMenuList);
        mRvCurryMenu.setAdapter(mCurryAdapter);
//        ItemTouchHelperインスタンスを新規生成
//        引数にネストクラス「Callback」のインスタンスを新規生成の上指定
        mCurryHelper = new ItemTouchHelper(new ItemTouchHelperCallback(mCurryAdapter));
//        ItemTouchHelper(のインスタンス)をRecyclerViewインスタンスへ追加
        mCurryHelper.attachToRecyclerView(mRvCurryMenu);


        Spinner spinner = (Spinner) toolbar.findViewById(R.id.spMenu);
        spinner.setAdapter(ArrayAdapter.createFromResource(this, R.array.menu_list, android.R.layout.simple_spinner_dropdown_item));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 処理
                String item = parent.getItemAtPosition(position).toString();
                final NestedScrollView nsvMenu = (NestedScrollView) findViewById(R.id.nsvMenu);
                switch (item) {
                    case "定食":
                        nsvMenu.post(new Runnable() {
                            public void run() {
                                nsvMenu.fullScroll(nsvMenu.FOCUS_UP);
                            }
                        });
                        break;
//                mFlag = "T";
//                mTeishokuMenuList = getMenuList(mFlag);
//                break;
                    case "カレー":
                        nsvMenu.post(new Runnable() {
                            public void run() {
                                nsvMenu.scrollTo((int) mTvMenuCategory2.getX(), (int) mTvMenuCategory2.getY());
                            }
                        });
//                mFlag = "C";
//                mCurryMenuList = getMenuList(mFlag);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * データベースよりメニューリストを生成
     *
     * @param flag (String型、メニュー種別を決定)
     * @return mMenuList (List型、メニューリストを呼び出し元へ返す)
     */
    private List<Map<String, Object>> getMenuList(String flag) {
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

    /**
     * 子ビュー右端のハンドルアイコンタップ時の挙動を定義
     *
     * @param viewHolder 子ビュー本体
     * @param flag メニュー種類
     */
    public void startDragOnHandleTouched(RecyclerView.ViewHolder viewHolder, String flag) {
        switch (flag) {
//            ItemTouchHelper#startDrag()を呼び出し
//            flagの内容により、startDrag()を呼び出すItemTouchHelperのインスタンスを変える
            case "T":
                mTeishokuHelper.startDrag(viewHolder);
                break;
            case "C":
                mCurryHelper.startDrag(viewHolder);
                break;
        }
    }

}
