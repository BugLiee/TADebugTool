/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private List<EditText> items = new ArrayList<>();
    private JSONObject props = new JSONObject();

    private Spinner collectionTypeSpinner;

    private String collectionType[] = new String[]{"String", "Boolean", "Float", "Double", "Long", "Byte", "Char", "Short", "Integer"};

    private String mCollectionType = "String";
    //    private String[] typeList = new String[]{"String", "Boolean", "Float", "Double", "Long", "Byte", "Char", "Short", "Integer", "Map", "Collection"};
    private String mType = "String";

    private Map<String, String> map = new HashMap<>();
    private List<EditText> mapItemKeys = new ArrayList<>();
    private List<EditText> mapItemValues = new ArrayList<>();

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
        items.add(propValueET);
        positiveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                props = new JSONObject();
                String key = propKeyET.getText().toString().trim();
                if (TextUtils.isEmpty(key)) {
                    SnackbarUtil.showSnackbarShort("key不能为空，请检查属性配置！ ");
                    return;
                }
                if (mType.equals("Map")) {
                    //only string
                    for (int i = 0; i < mapItemKeys.size(); i++) {
                        map.put(mapItemKeys.get(i).getText().toString().trim(), mapItemValues.get(i).getText().toString().trim());
                    }
                    try {
                        props.put(key, new JSONObject(map));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (mType.equals("Collection")) {
                    try {
                        switch (mCollectionType) {
                            case "String":
                                List<String> listString = new ArrayList<>();
                                for (EditText view : items) {
                                    listString.add(view.getText().toString().trim());
                                }
                                props.put(key, new JSONArray(listString));
                                break;
                            case "Boolean":
                                List<Boolean> listBoolean = new ArrayList<>();
                                for (EditText view : items) {
                                    listBoolean.add(Boolean.parseBoolean(view.getText().toString().trim()));
                                }
                                props.put(key, new JSONArray(listBoolean));
                                break;
                            case "Float":
                                List<Float> listFloat = new ArrayList<>();
                                for (EditText view : items) {
                                    listFloat.add(Float.parseFloat(view.getText().toString().trim()));
                                }
                                props.put(key, new JSONArray(listFloat));
                                break;
                            case "Double":
                                List<Double> listDouble = new ArrayList<>();
                                for (EditText view : items) {
                                    listDouble.add(Double.parseDouble(view.getText().toString().trim()));
                                }
                                props.put(key, new JSONArray(listDouble));
                                break;
                            case "Long":
                                List<Long> listLong = new ArrayList<>();
                                for (EditText view : items) {
                                    listLong.add(Long.parseLong(view.getText().toString().trim()));
                                }
                                props.put(key, new JSONArray(listLong));
                                break;
                            case "Byte":
                                List<Byte> listByte = new ArrayList<>();
                                for (EditText view : items) {
                                    listByte.add(Byte.parseByte(view.getText().toString().trim()));
                                }
                                props.put(key, new JSONArray(listByte));
                                break;
                            case "Char":
                                List<String> listChar = new ArrayList<>();
                                for (EditText view : items) {
                                    listChar.add(String.valueOf(view.getText().toString().trim().charAt(0)));
                                }
                                props.put(key, new JSONArray(listChar));
                                break;
                            case "Short":
                                List<Short> listShort = new ArrayList<>();
                                for (EditText view : items) {
                                    listShort.add(Short.parseShort(view.getText().toString().trim()));
                                }
                                props.put(key, new JSONArray(listShort));
                                break;
                            case "Integer":
                                List<Integer> listInteger = new ArrayList<>();
                                for (EditText view : items) {
                                    listInteger.add(Integer.parseInt(view.getText().toString().trim()));
                                }
                                props.put(key, new JSONArray(listInteger));
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    handleProp(key, propValueET.getText().toString().trim());
                }

                if (onPositiveListener != null) {
                    if (props.length() == 0) {
                        SnackbarUtil.showSnackbarShort("转换数据错误，请检查您的输入！");
                    } else {
                        onPositiveListener.onPositive(props);
                    }
                }
            }
        });
    }

    private void initMapView() {
        addTV = mPopupView.findViewById(R.id.popup_prop_constructor_add_list_tv);

        addTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem2Map();
            }
        });
        itemLL = mPopupView.findViewById(R.id.popup_prop_constructor_input_item_ll);
        containerLL = mPopupView.findViewById(R.id.popup_prop_constructor_container_ll);
        //items
        mapItemKeys.add(mPopupView.findViewById(R.id.input_prop_item_key_et));
        mapItemValues.add(mPopupView.findViewById(R.id.input_prop_item_value_et));

    }

    private void addItem2Map() {
        if (mapItemKeys.size() < 10) {
            View layout = LayoutInflater.from(mActivity).inflate(R.layout.item_prop_map_input_view, null);
            containerLL.addView(layout);
            mapItemKeys.add(layout.findViewById(R.id.input_prop_item_key_et));
            mapItemValues.add(layout.findViewById(R.id.input_prop_item_value_et));
        } else {
            SnackbarUtil.showSnackbarShort("已达最大限制");
        }
        if (mapItemKeys.size() <= 3) {
            update(getWidth(), getHeight() + itemLL.getHeight());
        }
    }

    private void initCollectionView() {
        addTV = mPopupView.findViewById(R.id.popup_prop_constructor_add_list_tv);
        addTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem2List();
            }
        });
        itemLL = mPopupView.findViewById(R.id.popup_prop_constructor_input_item_ll);
        containerLL = mPopupView.findViewById(R.id.popup_prop_constructor_container_ll);
        collectionTypeSpinner = mPopupView.findViewById(R.id.popup_prop_constructor_collection_type_spinner);

        ArrayAdapter adapter = new ArrayAdapter(mActivity, R.layout.item_simple_spinner_simple, R.id.simple_spinner_item_tv, collectionType) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(mActivity).inflate(R.layout.item_simple_spinner_simple, parent, false);
                TextView tv = view.findViewById(R.id.simple_spinner_item_tv);
                tv.setText(collectionType[position]);
                tv.setTextColor(Color.GREEN);
                return view;
            }
        };

        collectionTypeSpinner.setAdapter(adapter);
        collectionTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCollectionType = collectionType[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void addItem2List() {
        if (items.size() < 10) {
            View layout = LayoutInflater.from(mActivity).inflate(R.layout.item_prop_collection_input_view, null);
            containerLL.addView(layout);
            items.add(layout.findViewById(R.id.input_prop_value_et));
        } else {
            SnackbarUtil.showSnackbarShort("已达最大限制");
        }
        if (items.size() <= 3) {
            update(getWidth(), getHeight() + itemLL.getHeight());
        }
    }

    private void handleProp(String keyStr, String valueStr) {
        try {
            switch (mType) {
                case "String":
                    props.put(keyStr, valueStr);
                    break;
                case "Boolean":
                    props.put(keyStr, Boolean.parseBoolean(valueStr));
                    break;
                case "Float":
                    props.put(keyStr, Float.parseFloat(valueStr));
                    break;
                case "Double":
                    props.put(keyStr, Double.parseDouble(valueStr));
                    break;
                case "Long":
                    props.put(keyStr, Long.parseLong(valueStr));
                    break;
                case "Byte":
                    props.put(keyStr, Byte.parseByte(valueStr));
                    break;
                case "Char":
                    props.put(keyStr, String.valueOf(valueStr.charAt(0)));
                    break;
                case "Short":
                    props.put(keyStr, Short.valueOf(valueStr));
                    break;
                case "Integer":
                    props.put(keyStr, Integer.parseInt(valueStr));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
