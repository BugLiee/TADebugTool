/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * < HistoryContent >.
 *
 * @author bugliee
 * @create 2022/6/29
 * @since 1.0.0
 */
public class MapTypeView extends FrameLayout {
    private Context mContext;
    private EditText keyET;

    private LinearLayout containerLL;
    private List<LinearLayout> inputItemList = new ArrayList<>();

    public MapTypeView(@NonNull Context context) {
        super(context);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_props_constructor_map, this, true);
        initView(view);
    }

    private void initView(View view) {
        containerLL = view.findViewById(R.id.popup_prop_constructor_container_ll);
        inputItemList.add((LinearLayout) view.findViewById(R.id.popup_prop_constructor_input_item_ll));
        keyET = view.findViewById(R.id.input_prop_key_et);
    }

    public void addItem() {
        if (inputItemList.size() < 10) {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_prop_map_input_view, null);
            inputItemList.add(layout);
            containerLL.addView(layout);
        } else {
            SnackbarUtil.showSnackBarMid("最大限制为10个");
        }
    }

    public TreeMap<String,String> getValues() {
        TreeMap<String,String> map = new TreeMap<>();
        for (LinearLayout layout : inputItemList) {
            EditText keyET = layout.findViewById(R.id.input_prop_item_key_et);
            EditText valueET= layout.findViewById(R.id.input_prop_item_value_et);
            String key = keyET.getText().toString().trim();
            String value = valueET.getText().toString().trim();
            if (TextUtils.isEmpty(key)) {
                SnackbarUtil.showSnackBarShort("key 不能为空 >>");
                return null;
            } else if (TextUtils.isEmpty(value)) {
                SnackbarUtil.showSnackBarShort("value 不能为空 >>");
                return null;
            } else {
                map.put(key, value);
            }
        }
        return map;
    }

    public String getKey() {
        String key = keyET.getText().toString().trim();
        if (TextUtils.isEmpty(key)) {
            SnackbarUtil.showSnackBarShort("key 不能为空 >>");
            return null;
        }
        return key;
    }
}
