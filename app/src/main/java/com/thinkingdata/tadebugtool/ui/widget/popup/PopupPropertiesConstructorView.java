/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.ui.widget.InputAppIDItemView;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import org.json.JSONObject;

/**
 * < Copy Message >.
 *
 * @author bugliee
 * @create 2022/6/29
 * @since 1.0.0
 */
public class PopupPropertiesConstructorView extends PopupWindow {
    private View mPopupView;
    private Activity mActivity;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private TextView positiveTV;
    private TextView titleTV;
    private EditText propKeyET;
    private EditText propValueET;

    private TextView addTV;
    private LinearLayout containerLL;
    private LinearLayout itemLL;

//    private String[] typeList = new String[]{"String", "Boolean", "Float", "Double", "Long", "Byte", "Char", "Short", "Integer", "Map", "Collection"};
    private String mType = "String";

    public interface OnPositiveListener {
        void onPositive(JSONObject props);
    }

    OnPositiveListener onPositiveListener;

    public void setOnPositiveListener(OnPositiveListener listener) {
        onPositiveListener = listener;
    }

    public PopupPropertiesConstructorView(Activity activity, String type) {
        mActivity = activity;
        mType = type;
        int[] screenSize = TAUtil.getDeviceSize(mActivity);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
        init();
    }

    private void init() {
        switch (mType) {
            case "Map":
                mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.popup_props_constructor_map_view, null);
                initMapView();
                break;
            case "Collection":
                mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.popup_props_constructor_collection_view, null);
                initCollectionView();
                break;
            default:
                mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.popup_props_constructor_normal_view, null);
        }
        setContentView(mPopupView);
        setAnimationStyle(R.style.PopupHeaderViewStyle);
        setFocusable(true);
        titleTV = mPopupView.findViewById(R.id.popup_prop_constructor_title_tv);
        titleTV.setText(mType);
        positiveTV = mPopupView.findViewById(R.id.popup_prop_constructor_positive_tv);
        propKeyET = mPopupView.findViewById(R.id.input_prop_key_et);
        propValueET = mPopupView.findViewById(R.id.input_prop_value_et);
        positiveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType.equals("Map")) {

                } else if (mType.equals("Collection")) {

                } else {
                    //normal
                    handleProp(propKeyET.getText().toString().trim(), propValueET.getText().toString().trim());
                }
            }
        });
    }

    private void initMapView() {
        addTV = mPopupView.findViewById(R.id.popup_prop_constructor_add_list_tv);

        addTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        itemLL = mPopupView.findViewById(R.id.popup_prop_constructor_input_item_ll);
        containerLL = mPopupView.findViewById(R.id.popup_prop_constructor_container_ll);

    }

    private void initCollectionView() {
        addTV = mPopupView.findViewById(R.id.popup_prop_constructor_add_list_tv);
        addTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        itemLL = mPopupView.findViewById(R.id.popup_prop_constructor_input_item_ll);
        containerLL = mPopupView.findViewById(R.id.popup_prop_constructor_container_ll);

    }

    public void addItem() {

        containerLL.addView(itemLL);
        update(getWidth(), getHeight() + itemLL.getHeight());
    }

    private void handleProp(String keyStr, String valueStr) {
        JSONObject props = new JSONObject();
        try {
            switch (mType) {
                case "String":
                    props.put(keyStr, valueStr);
                    break;
                case"Boolean":
                    props.put(keyStr, Boolean.parseBoolean(valueStr));
                    break;
                case"Float":
                    props.put(keyStr, Float.parseFloat(valueStr));
                    break;
                case"Double":
                    props.put(keyStr, Double.parseDouble(valueStr));
                    break;
                case"Long":
                    props.put(keyStr, Long.parseLong(valueStr));
                    break;
                case"Byte":
                    props.put(keyStr, Byte.parseByte(valueStr));
                    break;
                case"Char":
                    props.put(keyStr, valueStr.toCharArray()[0]);
                    break;
                case"Short":
                    props.put(keyStr, Short.valueOf(valueStr));
                    break;
                case"Integer":
                    props.put(keyStr, Integer.parseInt(valueStr));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        if (onPositiveListener != null) {
            if (props.length() == 0) {
                Toast.makeText(mActivity, "转换数据错误，请检查您的输入！", Toast.LENGTH_SHORT).show();
            } else {
                onPositiveListener.onPositive(props);
            }
        }
    }

    public void show(View parentView) {
        if (parentView != null) {
            setHeight(screenHeight / 3);
            setWidth(screenWidth - 200);
            showAtLocation(parentView, Gravity.CENTER, 0, 0);
        }
    }

}
