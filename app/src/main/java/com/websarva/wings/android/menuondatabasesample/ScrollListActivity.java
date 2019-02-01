package com.websarva.wings.android.menuondatabasesample;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.support.v7.widget.helper.ItemTouchHelper.DOWN;
import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;

public class ScrollListActivity extends AppCompatActivity {

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
        RecyclerView lvMenu = findViewById(R.id.lvMenu);
//        LinearLayoutManagerのインスタンスを生成し、RecyclerViewインスタンスのレイアウトをLinearLayoutへ設定
        LinearLayoutManager layout = new LinearLayoutManager(ScrollListActivity.this);
        lvMenu.setLayoutManager(layout);

//        定食メニューのリストデータを生成
//        CSVファイルに格納されているデータを読み出し(CSVReaderクラスに手順記述)、メニュー種別(引数に設定)に沿って、Listに格納
        String flag = "T";
        List<Map<String, Object>> menuList = getMenuList(flag);


//        メニューリスト生成用アダプタのインスタンスを生成し、RecyclerViewへリストを登録
//        メニューリスト生成用アダプタは内部クラスとして別途定義
        final RecyclerListAdapter adapter = new RecyclerListAdapter(menuList);
        lvMenu.setAdapter(adapter);

//        リストに対し区切り線を設定
        DividerItemDecoration decoration = new DividerItemDecoration(ScrollListActivity.this, layout.getOrientation());
        lvMenu.addItemDecoration(decoration);

//        ItemTouchHelperインスタンスを新規生成
//        引数にネストクラス「Callback」のインスタンスを新規生成の上指定
        ItemTouchHelper helper = new ItemTouchHelper(new Callback() {
//          Callbackの抽象メソッド3種をオーバーライド

            //          これより以下のメソッドの稼働条件を設定
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//                稼働条件 … ViewHolderのインスタンスが上または下方向にドラッグされた場合
                return makeMovementFlags(ItemTouchHelper.UP | DOWN, 0);
            }

            //            ViewHolderがドラッグされた場合の動作
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                ViewHolderの要素(Map型インスタンス)をListより取り出し、その要素を削除
//                削除したインスタンスを一時変数に格納の上、ドロップした箇所に挿入
                Map<String, Object> menu = adapter._listData.remove(viewHolder.getAdapterPosition());
                adapter._listData.add(target.getAdapterPosition(), menu);
//                ViewHolderの移動内容を通知
                final int fromPosition = viewHolder.getAdapterPosition();
                final int toPosition = target.getAdapterPosition();
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            //            ドラッグされている最中のViewHolderの挙動
            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
//                ViewHolderがドラッグされた状態 ＝ actionStateの値が「2」の時、以下の処理を実施
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
//                    ViewHolderが保持するitemView(画面部品)に対し、透過度を「0.5」(半透明)に設定
                    viewHolder.itemView.setAlpha(0.5f);
                }
            }

            //            ドロップされた直後のViewHolderの挙動
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//                オーバーライド元のメソッドを呼び出し、"ViewHolderがnullとなった時"の処理を実施
                super.clearView(recyclerView, viewHolder);
//                ViewHolderが保持するitemViewに対し、透過度を「1.0」(不透明)に設定
                viewHolder.itemView.setAlpha(1.0f);
            }


            //            ViewHolderがスワイプされた場合の動作
//            (※今回は動作なしのため、未記述)
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            }
        });

//        ItemTouchHelper(のインスタンス)をRecyclerViewインスタンスへ追加
        helper.attachToRecyclerView(lvMenu);
    }

    /**
     * データベースよりメニューリストを生成
     *
     * @param flag (String型、メニュー種別を決定)
     * @return menuList (List型、メニューリストを呼び出し元へ返す)
     */
    private List<Map<String, Object>> getMenuList(String flag) {
//        メニューデータ格納先のList、メニューデータの各要素の格納先Mapの各インスタンスを定義
        List<Map<String, Object>> menuList = new ArrayList<>();
        Map<String, Object> menu = new HashMap<>();
//        テーブル名格納用String変数を定義し、引数「flag」の文字に応じて文字列を代入
//        所定の文字列を取得できなかった場合に備え、エラーメッセージの返却も定義
        String tbName = "";
        if (flag.equals("T")) {
            tbName = "menu_teishoku";
        } else if (flag.equals("C")) {
            tbName = "menu_curry";
        } else {
//        メニューのRecyclerViewにエラーメッセージを表示するため、各要素を代入したListを生成しreturn
            menu.put("name", "ファイル形式が正しくありません。");
            menu.put("price", 000);
            menuList.add(menu);
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
//                Cursorインスタンスの内部テーブルがなくなるまで、以下の処理を実施
            if (cursor != null) {
                do {
                    cursor.moveToFirst();
//                    Cursorインスタンスの要素数が0 (テーブル作成直後) の場合、以下の処理を実施
                    if (cursor.getCount() == 0) {
//                        List型メニューリストをassetsディレクトリ内のCSVファイルよりロードの上生成
                        menuList = new CSVReader().setMenuList(getApplicationContext(), flag);
//                        ContentValuesインスタンスを生成し、テーブルに格納する要素をメニューリストより個別にput
//                        メニューリストのサイズ分だけ繰り返し行う
                        ContentValues values = new ContentValues();
                        for (int i = 0; i < menuList.size(); i++) {
                            values.put("name", (String) menuList.get(i).get("name"));
                            values.put("price", (Integer) menuList.get(i).get("price"));
                            values.put("desc", (String) menuList.get(i).get("desc"));
                            long id = db.insert(tbName, null, values);
//                            Log.d("TAG", "Insert TAG: " + id);
                        }
                        cursor.moveToFirst();
                        cursor = db.query(tbName, null, null, null, null, null, null);
                    }
//                    Cursorインスタンスの要素数が0でない場合は、以下の処理を実施
                    menu = new HashMap<>();
                    // 各定食メニューをHashMapに登録し、その後menuListの各要素へ登録
                    menu.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
                    menu.put("name", cursor.getString(cursor.getColumnIndex("name")));
                    menu.put("price", cursor.getInt(cursor.getColumnIndex("price")));
                    menu.put("desc", cursor.getString(cursor.getColumnIndex("desc")));
                    menuList.add(menu);
                }while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "DB Error: " + e.toString());
        }
//        生成したメニューリストを呼び出し元へreturn
        return menuList;
    }

    /**
     * RecyclerViewの各項目を表示するViewを保持するクラス (ビューホルダ)
     */
    private class RecyclerListViewHolder extends RecyclerView.ViewHolder {
        //        リスト1行あたりのデータ表示用画面部品をフィールドで宣言
        public TextView _tvMenuName;
        public TextView _tvMenuPrice;

        /**
         * コンストラクタ
         *
         * @param itemView リスト1行当たりの画面部品
         */
        public RecyclerListViewHolder(View itemView) {
//            親クラスのコンストラクタを呼び出し
            super(itemView);
//            親クラスのビューより、フィールド宣言実施のクラスに相当する画面部品を取得し、インスタンス化
            _tvMenuName = itemView.findViewById(R.id.tvMenuName);
            _tvMenuPrice = itemView.findViewById(R.id.tvMenuPrice);
        }
    }

    /**
     * RecyclerViewへのデータ登録用アダプタクラス
     */
    private class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListViewHolder> {

        //        リストデータのインスタンスをフィールドで宣言
        private List<Map<String, Object>> _listData;

        /**
         * コンストラクタ
         *
         * @param listData リストデータ
         */
        public RecyclerListAdapter(List<Map<String, Object>> listData) {
            _listData = listData;
        }

        /**
         * リストの一項目ごとのレイアウトを定義するメソッド
         *
         * @return RecyclerListViewHolderインスタンス (RecyclerView.ViewHolderの子クラス)
         */
        @Override
        public RecyclerListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            レイアウトインフレータをアクティビティより取得し、リストの各項目の画面構成を定義(インフレート)
//            定義した画面構成は、View型インスタンスとして生成する
            LayoutInflater inflater = LayoutInflater.from(ScrollListActivity.this);
            View view = inflater.inflate(R.layout.row, parent, false);
//            定義した画面構成のViewインスタンスに対し、リスナを設定する
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    タップされたLinearLayout内にあるRecyclerViewに対し、タップされた位置にあるメニュー名を表示するTextViewを取得
//                    (※必ず、onClick()メソッドの引数として定義したViewインスタンスに対してfindViewById()を行うこと！)
                    TextView tvMenuName = v.findViewById(R.id.tvMenuName);
//                    取得したTextViewよりメニュー名の文字列を抽出し、文字列XMLファイル中の定型文と結合
//                    結合した文字列をToast形式でポップアップ表示する
                    String menuName = tvMenuName.getText().toString();
                    String msg = getString(R.string.msg_header) + menuName;
                    Toast.makeText(ScrollListActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });
//            生成したView型インスタンスを、RecyclerView.ViewHolderの子クラスのインスタンスへ渡す
//            (RecyclerView.ViewHolderの子クラス … RecyclerViewの各項目を表示するViewを保持するクラス)
            return new RecyclerListViewHolder(view);
        }

        /**
         * リストの一項目ごとのデータを取得し、RecyclerView.ViewHolderの子クラスへデータを引き渡すメソッド
         */
        @Override
        public void onBindViewHolder(RecyclerListViewHolder holder, int position) {
//            リストデータより、一行当たりのデータを取得
            Map<String, Object> item = _listData.get(position);
//            取得したデータに格納されている項目中、"name"タグの付与されたString文字列、"price"タグの付与されたint型数値を抽出
//            int型数値はString型文字列へ変換する (画面表示のため)
            String menuName = (String) item.get("name");
            int menuPrice = (Integer) item.get("price");
            String menuPriceStr = String.format("%,3d", menuPrice);
//            ビューホルダに対し、上記文字列を各表示先画面部品へ表示する設定を実施
            holder._tvMenuName.setText(menuName);
            holder._tvMenuPrice.setText(menuPriceStr);
        }

        /**
         * リストの項目数量をカウントするメソッド
         *
         * @return リストデータの項目数 (int型)
         */
        @Override
        public int getItemCount() {
            return _listData.size();
        }
    }

}
