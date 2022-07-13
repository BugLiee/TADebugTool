/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.utils.TAUtil;

/**
 * < Copy Message >.
 *
 * @author bugliee
 * @create 2022/6/29
 * @since 1.0.0
 */
public class PopupPropertiesTypeChooserView extends PopupWindow {

    private View mPopupView;
    private Activity mActivity;

    private int screenWidth = 0;
    private int screenHeight = 0;

    private Spinner spinner;
    private TextView positiveTV;

    private String[] typeList = new String[]{"String", "Boolean", "Float", "Double", "Long", "Byte", "Char", "Short", "Integer", "Map", "Collection"};
    private String mType = typeList[0];

    public interface OnPositiveListener {
        void onPositive(String type);
    }

    OnPositiveListener onPositiveListener;

    public void setOnPositiveListener(OnPositiveListener listener) {
        onPositiveListener = listener;
    }

    public PopupPropertiesTypeChooserView(Activity activity) {
        mActivity = activity;
        init();
    }

    private void init() {
        mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.popup_prop_type_chooser, null);
        spinner = mPopupView.findViewById(R.id.popup_prop_type_chooser_spinner);
        setContentView(mPopupView);
        setAnimationStyle(R.style.PopupHeaderViewStyle);
        setFocusable(true);
        int[] screenSize = TAUtil.getDeviceSize(mActivity);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
        positiveTV = mPopupView.findViewById(R.id.prop_type_chooser_positive_tv);
        setUp();
    }

    private void setUp() {
        ArrayAdapter adapter = new ArrayAdapter(mActivity, R.layout.item_prop_type_chooser_simple, R.id.prop_type_chooser_spinner_item_tv, typeList);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!mType.equals(typeList[position])) {
                    mType = typeList[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        positiveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPositiveListener != null) {
                    onPositiveListener.onPositive(mType);
                }
            }
        });
    }

    public void show(View parentView) {
        if (parentView != null) {
            setHeight(screenHeight / 6);
            setWidth(screenWidth - 200);
            showAtLocation(parentView, Gravity.CENTER, 0, 0);

        }
    }
}
