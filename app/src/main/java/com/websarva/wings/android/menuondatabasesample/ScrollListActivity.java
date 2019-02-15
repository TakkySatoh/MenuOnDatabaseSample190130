package com.websarva.wings.android.menuondatabasesample;

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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
        mTeishokuMenuList = MenuListGenerator.getMenuList(mFlag);
        mTeishokuAdapter = new RecyclerListAdapter(ScrollListActivity.this, mTeishokuMenuList);
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
        mCurryMenuList = MenuListGenerator.getMenuList(mFlag);
        mCurryAdapter = new RecyclerListAdapter(ScrollListActivity.this, mCurryMenuList);
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
