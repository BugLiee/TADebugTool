/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.thinkingdata.tadebugtool.R;

/**
 * < input view >.
 *
 * @author bugliee
 * @create 2022/7/1
 * @since 1.0.0
 */
public class InputAppIDItemView extends LinearLayout {

    private int cId;
    private TextView removeTV;
    private EditText editText;

    public interface OnRemoveClickListener{
        void onClick(int id);
    }

    OnRemoveClickListener onRemoveClickListener;

    public void setOnRemoveClickListener(OnRemoveClickListener onRemoveClickListener) {
        this.onRemoveClickListener = onRemoveClickListener;
    }

    public InputAppIDItemView(Context context) {
        super(context);
        init(context);
    }

    public InputAppIDItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InputAppIDItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_input_appid, this, true);
        removeTV = view.findViewById(R.id.input_remove_tv);
        editText = view.findViewById(R.id.input_appid_et);
    }

    public void setCId(int cId) {
        this.cId = cId;
    }

    public void setup(OnRemoveClickListener listener) {
        setOnRemoveClickListener(listener);
        removeTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(cId);
                }
            }
        });
    }

    public String getETContent() {
        return editText.getText().toString().trim();
    }

}
