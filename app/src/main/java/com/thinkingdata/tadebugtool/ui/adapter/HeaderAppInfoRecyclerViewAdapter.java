/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

/**
 * < adapter >.
 *
 * @author bugliee
 * @create 2022/7/4
 * @since 1.0.0
 */
public class HeaderAppInfoRecyclerViewAdapter extends RecyclerView.Adapter<HeaderAppInfoRecyclerViewAdapter.HeaderRVHolder> {

    private TreeMap<String, String> itemList = new TreeMap<>();
    private List<String> keyList = new ArrayList<>();
    private List<String> valueList = new ArrayList<>();
    private Map<String, String[]> spinnerValues = new HashMap<>();
    private JSONObject presetJson = null;
    private int presetJsonPos = -1;
    private List<String> presetJsonKeys = new ArrayList<>();
    private List<String> presetJsonValues = new ArrayList<>();

    private Activity mActivity;
    private View mView;

    public HeaderAppInfoRecyclerViewAdapter(Activity activity, TreeMap<String, String> list ) {
        mActivity = activity;
        itemList.putAll(list);
        for (Map.Entry<String, String> entry : itemList.entrySet()) {
            keyList.add(entry.getKey());
            valueList.add(entry.getValue());
        }
    }

    @NonNull
    @Override

    public HeaderRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(mActivity).inflate(R.layout.item_app_info_header, parent, false);
        return new HeaderRVHolder(mView, mActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull HeaderRVHolder holder, int position) {
        String keyStr = keyList.get(position);
        holder.textView.setText(keyStr);
        String[] strings;
        String valueStr = valueList.get(position);
        spinnerValues.clear();
        strings = valueStr.split(",");
        spinnerValues.put(keyStr, strings);
        try {
            presetJson = new JSONObject(valueStr);
            Iterator<String> iterator = presetJson.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                presetJsonKeys.add(key);
                presetJsonValues.add(presetJson.optString(key));
            }
            presetJsonPos = position;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int pos = position;
        ArrayAdapter adapter = new ArrayAdapter(holder.mContext, R.layout.item_app_info_spinner_simple, R.id.app_info_spinner_item_tv, strings) {
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(holder.mContext).inflate(R.layout.item_app_info_spinner_simple, parent, false);
                TextView tv = view.findViewById(R.id.app_info_spinner_item_tv);
                if (presetJson != null && pos == presetJsonPos) {
                    tv.setText("预置属性");
                } else if (strings.length > 1) {
                    tv.setText("数组");
                } else {
                    tv.setText(strings[position]);
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view;
                if (presetJson != null && pos == presetJsonPos) {
                    view = LayoutInflater.from(holder.mContext).inflate(R.layout.item_app_info_spinner_kv, parent, false);
                    TextView keyTV = view.findViewById(R.id.app_info_spinner_item_key_tv);
                    keyTV.setText(presetJsonKeys.get(position));
                    TextView valueTV = view.findViewById(R.id.app_info_spinner_item_value_tv);
                    valueTV.setText(presetJsonValues.get(position));
                    valueTV.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            clipData(valueTV);
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        Method method = holder.spinner.getClass().getDeclaredMethod("onDetachedFromWindow");
                                        method.setAccessible(true);
                                        try {
                                            method.invoke(holder.spinner);
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        } catch (InvocationTargetException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 500);
                            return false;
                        }
                    });
                } else {
                    view = LayoutInflater.from(holder.mContext).inflate(R.layout.item_app_info_spinner_simple, parent, false);
                    TextView tv = view.findViewById(R.id.app_info_spinner_item_tv);
                    tv.setText(strings[position]);
                    tv.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            clipData(tv);
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    try {
                                        Method method = holder.spinner.getClass().getDeclaredMethod("onDetachedFromWindow");
                                        method.setAccessible(true);
                                        try {
                                            method.invoke(holder.spinner);
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        } catch (InvocationTargetException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (NoSuchMethodException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, 500);

                            return false;
                        }
                    });
                }
                return view;

            }
        };

        holder.spinner.setAdapter(adapter);
    }

    private ClipboardManager clipboardManager;

    private void clipData(View view) {
        if (!(view instanceof TextView)) {
            return;
        }
        ((TextView) view).setTextColor(Color.GRAY);
        if (clipboardManager == null) {
            clipboardManager = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        ClipData clipData = ClipData.newPlainText("tatool text", ((TextView) view).getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((TextView) view).setTextColor(Color.WHITE);
            }
        }, 300);
        Toast.makeText(mActivity, "copy success!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class HeaderRVHolder extends RecyclerView.ViewHolder {
        TextView textView;
        Spinner spinner;
        Context mContext;
        public HeaderRVHolder(@NonNull View itemView, Context context) {
            super(itemView);
            mContext = context;
            textView = itemView.findViewById(R.id.app_info_header_item_tv);
            spinner = itemView.findViewById(R.id.app_info_header_item_spinner);
        }
    }
}
