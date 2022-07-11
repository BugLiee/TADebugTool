/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * < eventProps >.
 *
 * @author bugliee
 * @create 2022/7/5
 * @since 1.0.0
 */
public class MockEventPropsRecyclerViewAdapter extends RecyclerView.Adapter<MockEventPropsRecyclerViewAdapter.EventPropsViewHolder> {
    private Context mContext;
    private JSONObject props = new JSONObject();
    private List<String> keys = new ArrayList<>();
    private List<String> values = new ArrayList<>();

    public JSONObject getProps() {
        return props;
    }

    public MockEventPropsRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void addItem(JSONObject sourceProps) {
        try {
            TAUtil.mergeJSONObject(sourceProps, props, TimeZone.getTimeZone("GMT:+8:00"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Iterator<String> iterator = props.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            keys.add(key);
            values.add(props.optString(key));
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventPropsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_mock_event_props, parent, false);
        return new EventPropsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventPropsViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.titleTV.setText(keys.get(position));
        holder.valueTV.setText(values.get(position));
    }

    @Override
    public int getItemCount() {
        return props.length();
    }

    public void removeAll() {
        props = new JSONObject();
        keys.clear();
        values.clear();
        notifyDataSetChanged();
    }

    public static class EventPropsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        TextView valueTV;
        public EventPropsViewHolder(@NonNull View itemView) {
            super(itemView);
             titleTV = itemView.findViewById(R.id.mock_event_prop_item_key_tv);
             valueTV = itemView.findViewById(R.id.mock_event_prop_item_value_tv);
        }
    }
}

