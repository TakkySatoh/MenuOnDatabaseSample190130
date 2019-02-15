package com.websarva.wings.android.menuondatabasesample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * RecyclerViewへのデータ登録用アダプタクラス
 */
public class RecyclerListAdapter extends RecyclerView.Adapter<RecyclerListAdapter.RecyclerListViewHolder> {

    //        リストデータのインスタンスをフィールドで宣言

    private ScrollListActivity mActivity;
    private List<Map<String, Object>> _menuList;

    /**
     * コンストラクタ
     *
     * @param listData リストデータ
     */
    public RecyclerListAdapter(ScrollListActivity scrollListActivity, List<Map<String, Object>> listData) {
        mActivity = scrollListActivity;
        _menuList = listData;
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
        LayoutInflater inflater = LayoutInflater.from(mActivity);
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
                String msg = mActivity.getString(R.string.msg_header) + menuName;
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
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
    public void onBindViewHolder(final RecyclerListViewHolder holder, int position) {
//            リストデータより、一行当たりのデータを取得
        Map<String, Object> item = _menuList.get(position);
//            取得したデータに格納されている項目中、"name"タグの付与されたString文字列、"price"タグの付与されたint型数値を抽出
//            int型数値はString型文字列へ変換する (画面表示のため)
        String menuName = (String) item.get("name");
        int menuPrice = (Integer) item.get("price");
        String menuPriceStr = String.format("%,3d", menuPrice);
//            ビューホルダに対し、上記文字列を各表示先画面部品へ表示する設定を実施
        holder._tvMenuName.setText(menuName);
        holder._tvMenuPrice.setText(menuPriceStr);
//            試験実装1: ビューホルダ内のアイコンをタップするとリスト入れ替え発生
//            final RecyclerListViewHolder holder = new RecyclerListViewHolder(view);
        final String flag = (String) item.get("category");
        holder._icHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mActivity.startDragOnHandleTouched(holder, flag);
                }
                return false;
            }
        });
    }

    /**
     * リストの項目数量をカウントするメソッド
     *
     * @return リストデータの項目数 (int型)
     */
    @Override
    public int getItemCount() {
        return _menuList.size();
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        Map<String, Object> menu = _menuList.remove(fromPosition);
        _menuList.add(toPosition, menu);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


    /**
     * RecyclerViewの各項目を表示するViewを保持するクラス (ビューホルダ)
     */
    public class RecyclerListViewHolder extends RecyclerView.ViewHolder {
        //        リスト1行あたりのデータ表示用画面部品をフィールドで宣言
        public TextView _tvMenuName;
        public TextView _tvMenuPrice;
        public ImageView _icHandle;

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
            _icHandle = itemView.findViewById(R.id.icHandle);
        }
    }
}


