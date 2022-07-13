/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.ui.adapter.TypeMenuRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.ui.widget.CollectionTypeView;
import com.thinkingdata.tadebugtool.ui.widget.MapTypeView;
import com.thinkingdata.tadebugtool.ui.widget.NormalTypeView;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * < Copy Message >.
 *
 * @author bugliee
 * @create 2022/6/29
 * @since 1.0.0
 */
public class PopupPropertiesConstructorView extends PopupWindow {
    private View mPopupView;
    private final Activity mActivity;
    private int screenWidth = 0;
    private int screenHeight = 0;


    private TextView positiveTV;

    private TextView addTV;

    private JSONObject props = null;

    private final String[] collectionType = new String[]{"String", "Boolean", "Float", "Double", "Long", "Byte", "Char", "Short", "Integer"};
    private int currentCollectionPos = 0;

    private final String[] typeList = new String[]{"String", "Boolean", "Float", "Double", "Long", "Byte", "Char", "Short", "Integer", "Map", "Collection"};

    private RecyclerView typeMenuRV;

    NormalTypeView normalTypView;
    MapTypeView mapTypeView;
    CollectionTypeView collectionTypeView;

    private FrameLayout contentFL;
    private int lastSelectPos = 0;

    public interface OnPositiveListener {
        void onPositive(JSONObject props);
    }

    OnPositiveListener onPositiveListener;

    public void setOnPositiveListener(OnPositiveListener listener) {
        onPositiveListener = listener;
    }

    public PopupPropertiesConstructorView(Activity activity) {
        mActivity = activity;
        int[] screenSize = TAUtil.getDeviceSize(mActivity);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
        init();
    }

    private void initLayouts() {
        normalTypView = new NormalTypeView(mActivity);
        mapTypeView = new MapTypeView(mActivity);
        collectionTypeView = new CollectionTypeView(mActivity, collectionType);
        collectionTypeView.setTypeSelectListener(new CollectionTypeView.OnTypeSelectListener() {
            @Override
            public void onSelect(int pos) {
                currentCollectionPos = pos;
            }
        });
        contentFL.addView(normalTypView);
    }

    private void init() {
        mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.popup_props_constructor_view, null);
        setContentView(mPopupView);
        setAnimationStyle(R.style.PopupHeaderViewStyle);
        setFocusable(true);
        initView();
        initLayouts();
    }


    private void initView() {
        typeMenuRV = mPopupView.findViewById(R.id.popup_props_constructor_rv);
        TypeMenuRecyclerViewAdapter adapter = new TypeMenuRecyclerViewAdapter(mActivity, typeList);
        LinearLayoutManager manager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        typeMenuRV.setLayoutManager(manager);
        typeMenuRV.setAdapter(adapter);
        contentFL = mPopupView.findViewById(R.id.popup_props_constructor_fl);
        adapter.setItemClickListener(new TypeMenuRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int pos) {
                if (lastSelectPos != pos) {
                    lastSelectPos = pos;
                    contentFL.removeAllViews();
                    switch (lastSelectPos) {
                        case 9:
                            contentFL.addView(mapTypeView);
                            break;
                        case 10:
                            contentFL.addView(collectionTypeView);
                            break;
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                        default:
                            contentFL.addView(normalTypView);
                            break;
                    }
                }
            }
        });

        addTV = mPopupView.findViewById(R.id.prop_input_item_add_tv);
        addTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectPos == typeList.length - 1) {
                    collectionTypeView.addItem();

                }else if(lastSelectPos == typeList.length - 2) {
                    mapTypeView.addItem();
                }else {
                    //do nothing
                }
            }
        });

        positiveTV = mPopupView.findViewById(R.id.prop_input_item_add_positive_tv);
        positiveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (lastSelectPos) {
                    case 9:
                        handleMapProp(mapTypeView.getKey(),mapTypeView.getValues());
                        break;
                    case 10:
                        handleListProp(collectionTypeView.getKey(),collectionTypeView.getValues());
                        break;
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    default:
                        handleProp(normalTypView.getKey(), normalTypView.getValue());
                        break;
                }
                if (onPositiveListener != null) {
                    try {
                        onPositiveListener.onPositive(new JSONObject(props.toString()));
                    } catch (JSONException e) {
//                        e.printStackTrace();
                    }
                }
                props = null;
                dismiss();
            }
        });
    }

    private void handleListProp(String key, List<String> values) {
        props = new JSONObject();
        if (key == null && values == null) {
            return;
        }
        try {
            switch (currentCollectionPos) {
                case 0:
                    props.put(key, new JSONArray(values));
                    break;
                case 1:
                    List<Boolean> booleanList = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                        booleanList.add(Boolean.parseBoolean(values.get(i)));
                    }
                    props.put(key, new JSONArray(booleanList));
                    break;
                case 2:
                    List<Float> floatList = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                        floatList.add(Float.parseFloat(values.get(i)));
                    }
                    props.put(key, new JSONArray(floatList));
                    break;
                case 3:
                    List<Double> doubleList = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                        doubleList.add(Double.parseDouble(values.get(i)));
                    }
                    props.put(key, new JSONArray(doubleList));
                    break;
                case 4:
                    List<Long> longList = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                        longList.add(Long.parseLong(values.get(i)));
                    }
                    props.put(key, new JSONArray(longList));
                    break;
                case 5:
                    List<Byte> byteList = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                        byteList.add(Byte.parseByte(values.get(i)));
                    }
                    props.put(key, new JSONArray(byteList));
                    break;
                case 6:
                    List<Character> charList = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                        charList.add(values.get(i).charAt(0));
                    }
                    props.put(key, new JSONArray(charList));
                    break;
                case 7:
                    List<Short> shortList = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                        shortList.add(Short.parseShort(values.get(i)));
                    }
                    props.put(key, new JSONArray(shortList));
                    break;
                case 8:
                    List<Integer> integerList = new ArrayList<>();
                    for (int i = 0; i < values.size(); i++) {
                        integerList.add(Integer.parseInt(values.get(i)));
                    }
                    props.put(key, new JSONArray(integerList));
                    break;
            }
        } catch (Exception e) {
            SnackbarUtil.showSnackBarMid("转换List类型失败", mPopupView);
        }
    }

    private void handleMapProp(String key, TreeMap<String, String> values) {
        props = new JSONObject();
        if (key == null && values == null) {
            return;
        }
        try {
            props.put(key, new JSONObject(values));
        } catch (Exception e) {
            SnackbarUtil.showSnackBarMid("转换Map类型失败", mPopupView);
        }
    }

    private void handleProp(String keyStr, String valueStr) {
        props = new JSONObject();
        if (keyStr == null && valueStr == null) {
            return;
        }
        try {
            switch (lastSelectPos) {
                case 0:
                    props.put(keyStr, valueStr);
                    break;
                case 1:
                    props.put(keyStr, Boolean.parseBoolean(valueStr));
                    break;
                case 2:
                    props.put(keyStr, Float.parseFloat(valueStr));
                    break;
                case 3:
                    props.put(keyStr, Double.parseDouble(valueStr));
                    break;
                case 4:
                    props.put(keyStr, Long.parseLong(valueStr));
                    break;
                case 5:
                    props.put(keyStr, Byte.parseByte(valueStr));
                    break;
                case 6:
                    props.put(keyStr, Character.valueOf(valueStr.charAt(0)));
                    break;
                case 7:
                    props.put(keyStr, Short.valueOf(valueStr));
                    break;
                case 8:
                    props.put(keyStr, Integer.parseInt(valueStr));
                    break;
            }
        } catch (Exception e) {
            SnackbarUtil.showSnackBarMid("转换类型失败", mPopupView);
        }
    }

    public void show(View parentView) {
        if (parentView != null) {
            setHeight(screenHeight / 3);
            setWidth(screenWidth - 200);
            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            lp.alpha = 0.8f;
            mActivity.getWindow().setAttributes(lp);
            showAtLocation(parentView, Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 1f;
        mActivity.getWindow().setAttributes(lp);
    }
}
