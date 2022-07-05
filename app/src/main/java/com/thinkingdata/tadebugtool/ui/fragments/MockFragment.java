/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.fragments;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAEvent;
import com.thinkingdata.tadebugtool.ui.adapter.EventListRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.ui.adapter.MockEventPropsRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupPropertiesConstructorView;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupPropertiesTypeChooserView;

import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * < eventList >.
 *
 * @author bugliee
 * @create 2022/7/5
 * @since 1.0.0
 */
public class MockFragment extends Fragment {
    RadioGroup radioGroup;
    RecyclerView propsRV;
    TextView currentPropsTV;
    TextView addPropsTV;

    PopupPropertiesTypeChooserView chooserView;
    PopupPropertiesConstructorView constructorView;

    MockEventPropsRecyclerViewAdapter mockEventPropsRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mock, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        radioGroup = view.findViewById(R.id.mock_rg);
        radioGroup.check(radioGroup.getChildAt(0).getId());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("bugliee", "onCheckedChanged ==> " + checkedId);
            }
        });
        currentPropsTV = view.findViewById(R.id.mock_current_prop_tv);
        currentPropsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (propsRV.getVisibility() != View.VISIBLE) {
                    propsRV.setVisibility(View.VISIBLE);
                } else {
                    propsRV.setVisibility(View.GONE);
                }
            }
        });
        propsRV = view.findViewById(R.id.mock_props_rv);
        propsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        if (mockEventPropsRecyclerViewAdapter == null) {
            mockEventPropsRecyclerViewAdapter = new MockEventPropsRecyclerViewAdapter(getActivity());
        }
        propsRV.setAdapter(mockEventPropsRecyclerViewAdapter);

        addPropsTV = view.findViewById(R.id.mock_add_prop_tv);
        addPropsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooserView == null) {
                    chooserView = new PopupPropertiesTypeChooserView(getActivity());
                }
                chooserView.setOnPositiveListener(new PopupPropertiesTypeChooserView.OnPositiveListener() {
                    @Override
                    public void onPositive(String type) {
                        chooserView.dismiss();
                        //显示对应类型的构建界面
                        constructorView = new PopupPropertiesConstructorView(getActivity(), type);
                        constructorView.setOnPositiveListener(new PopupPropertiesConstructorView.OnPositiveListener() {
                            @Override
                            public void onPositive(JSONObject props) {
                                constructorView.dismiss();
                                mockEventPropsRecyclerViewAdapter.addItem(props);
                            }
                        });
                        constructorView.show(getView());
                    }
                });
                chooserView.show(getView());
            }
        });
    }
}
