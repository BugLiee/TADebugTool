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

import androidx.annotation.NonNull;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;

/**
 * < NormalTypeView >.
 *
 * @author bugliee
 * @create 2022/6/29
 * @since 1.0.0
 */
public class NormalTypeView extends FrameLayout {
    private EditText keyET;
    private EditText valueET;
    private Context mContext;


    public NormalTypeView(@NonNull Context context) {
        super(context);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_props_constructor_normal, this, true);
        initView(view);
    }

    private void initView(View view) {
        keyET = view.findViewById(R.id.input_prop_key_et);
        valueET = view.findViewById(R.id.input_prop_value_et);
    }


    public String getKey() {
        String key = keyET.getText().toString().trim();
        if (TextUtils.isEmpty(key)) {
            SnackbarUtil.showSnackBarShort("key 不能为空 >>");
            return null;
        }
        return key;
    }

    public String getValue() {
        String value = valueET.getText().toString().trim();
        if (TextUtils.isEmpty(value)) {
            SnackbarUtil.showSnackBarShort("value 不能为空 >>");
            return null;
        }
        return value;
    }

}
