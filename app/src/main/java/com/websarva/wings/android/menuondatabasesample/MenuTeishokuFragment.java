package com.websarva.wings.android.menuondatabasesample;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class MenuTeishokuFragment extends Fragment {

    private ScrollListActivity mActivity;
    private List<Map<String,Object>> mMenuList;
    private ItemTouchHelper mItemTouchHelper;

    public MenuTeishokuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvMenuCategory = mActivity.findViewById(R.id.tvMenuCategory);
        RecyclerView rvMenu = mActivity.findViewById(R.id.rvMenu);

        mMenuList = MenuListGenerator.getMenuList("T");

        RecyclerListAdapter adapter = new RecyclerListAdapter(mActivity,mMenuList);
    }
}
