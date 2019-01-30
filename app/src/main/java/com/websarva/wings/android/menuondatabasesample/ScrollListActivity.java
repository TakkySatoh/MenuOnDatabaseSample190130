package com.websarva.wings.android.menuondatabasesample;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
        List<Map<String, Object>> menuList = createTeishokuList();
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
     * リストビューに表示させる定食系メニューのリストを生成
     *
     * @return 定食系メニューのリスト
     */
    @NonNull
    private List<Map<String, Object>> createTeishokuList() {
        // SimpleAdapterで使用するListオブジェクトを用意
        List<Map<String, Object>> menuList = new ArrayList<>();
        // 各定食メニューのデータを格納するMapオブジェクトを用意
        Map<String, Object> menu = new HashMap<>();
        // 各定食メニューをHashMapに登録し、その後menuListの各要素へ登録
        menu.put("name", "から揚げ定食");
        menu.put("price", 800);
        menu.put("desc", "若鳥のから揚げにサラダ、ご飯とお味噌汁が付きます。");
        menuList.add(menu);
        menu = new HashMap<>();
        menu.put("name", "ハンバーグ定食");
        menu.put("price", 850);
        menu.put("desc", "手ごねハンバーグにサラダ、ご飯とお味噌汁が付きます。");
        menuList.add(menu);
        menu = new HashMap<>();
        menu.put("name", "生姜焼き定食");
        menu.put("price", 850);
        menu.put("desc", "すりおろし生姜を使った生姜焼きにサラダ、ご飯とお味噌汁が付きます。");
        menuList.add(menu);
        menu = new HashMap<>();
        menu.put("name", "ステーキ定食");
        menu.put("price", 1000);
        menu.put("desc", "国産牛のステーキにサラダ、ご飯とお味噌汁が付きます。");
        menuList.add(menu);
        menu = new HashMap<>();
        menu.put("name", "野菜炒め定食");
        menu.put("price", 750);
        menu.put("desc", "季節の野菜炒めにサラダ、ご飯とお味噌汁が付きます。");
        menuList.add(menu);
        menu = new HashMap<>();
        menu.put("name", "とんかつ定食");
        menu.put("price", 900);
        menu.put("desc", "ロースとんかつにサラダ、ご飯とお味噌汁が付きます。");
        menuList.add(menu);
        menu = new HashMap<>();
        menu.put("name", "メンチカツ定食");
        menu.put("price", 850);
        menu.put("desc", "手ごねミンチカツにサラダ、ご飯とお味噌汁が付きます。");
        menuList.add(menu);
        menu = new HashMap<>();
        menu.put("name", "チキンカツ定食");
        menu.put("price", 900);
        menu.put("desc", "ボリュームたっぷりチキンカツにサラダ、ご飯とお味噌汁が付きます。");
        menuList.add(menu);
        menu = new HashMap<>();
        menu.put("name", "コロッケ定食");
        menu.put("price", 850);
        menu.put("desc", "北海道ポテトコロッケにサラダ、ご飯とお味噌汁が付きます。");
        menuList.add(menu);
        menu = new HashMap<>();
        menu.put("name", "焼き魚定食");
        menu.put("price", 900);
        menu.put("desc", "鰆の塩焼きにサラダ、ご飯とお味噌汁が付きます。");
        menuList.add(menu);
        menu = new HashMap<>();
        menu.put("name", "焼肉定食");
        menu.put("price", 950);
        menu.put("desc", "特性たれの焼肉にサラダ、ご飯とお味噌汁が付きます。");
        menuList.add(menu);
        // 作成したmenuListを呼び出し元へreturn
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
            String menuPriceStr = String.valueOf(menuPrice);
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
