/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * < CollectionTypeView >.
 *
 * @author bugliee
 * @create 2022/6/29
 * @since 1.0.0
 */
public class CollectionTypeView extends FrameLayout {
    private Spinner spinner;
    private Context mContext;
    private String[] mTypes;
    private EditText keyET;

    private LinearLayout containerLL;
    private List<LinearLayout> inputItemList = new ArrayList<>();

    public interface OnTypeSelectListener{
        void onSelect(int pos);
    }

    OnTypeSelectListener typeSelectListener;

    public void setTypeSelectListener(OnTypeSelectListener typeSelectListener) {
        this.typeSelectListener = typeSelectListener;
    }

    public CollectionTypeView(@NonNull Context context, String[] types) {
        super(context);
        mContext = context;
        mTypes = types;
        View view = LayoutInflater.from(context).inflate(R.layout.view_props_constructor_collection, this, true);

        initView(view);
    }

    private void initView(View view) {
        spinner = view.findViewById(R.id.popup_prop_constructor_collection_type_spinner);
        initSpinner();

        containerLL = view.findViewById(R.id.popup_prop_constructor_container_ll);
        inputItemList.add((LinearLayout) view.findViewById(R.id.popup_prop_constructor_input_item_ll));

        keyET = view.findViewById(R.id.input_prop_key_et);
    }

    private void initSpinner() {
        ArrayAdapter adapter = new ArrayAdapter(mContext, R.layout.item_simple_spinner_simple, R.id.simple_spinner_item_tv, mTypes) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_simple_spinner_simple, parent, false);
                TextView tv = view.findViewById(R.id.simple_spinner_item_tv);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(0xFF6F6459);
                tv.setText(mTypes[position]);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_simple_spinner_simple, parent, false);
                TextView tv = view.findViewById(R.id.simple_spinner_item_tv);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(0xFF6F6459);
                tv.setText(mTypes[position]);
                return view;
            }
        };
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (typeSelectListener != null) {
                    typeSelectListener.onSelect(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setAdapter(adapter);
    }

    public void addItem() {
        if (inputItemList.size() < 10) {
            LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.item_prop_collection_input_view, null);
            inputItemList.add(layout);
            containerLL.addView(layout);
        } else {
            SnackbarUtil.showSnackBarMid("最大限制为10个");
        }
    }

    public List<String> getValues() {
        List<String> list = new ArrayList<>();
        for (LinearLayout layout : inputItemList) {
            EditText valueET = layout.findViewById(R.id.input_prop_value_et);
            String value = valueET.getText().toString().trim();
            if (TextUtils.isEmpty(value)) {
                SnackbarUtil.showSnackBarShort("value 不能为空 >>");
                return null;
            } else {
                list.add(value);
            }
        }
        return list;
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
