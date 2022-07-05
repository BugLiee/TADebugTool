/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * < eventProps >.
 *
 * @author bugliee
 * @create 2022/7/5
 * @since 1.0.0
 */
public class EventPropsRecyclerViewAdapter extends RecyclerView.Adapter<EventPropsRecyclerViewAdapter.EventPropsViewHolder> {
    private Context mContext;
    private TreeMap<String, String> props = new TreeMap<>();
    private List<String> keys = new ArrayList<>();
    private List<String> values = new ArrayList<>();

    public EventPropsRecyclerViewAdapter(Context context, TreeMap<String, String> map) {
        mContext = context;
        props.putAll(map);
        for (Map.Entry<String, String> entry : props.entrySet()) {
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }
    }

    @NonNull
    @Override
    public EventPropsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_event_props, parent, false);
        return new EventPropsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventPropsViewHolder holder, int position) {
        holder.titleTV.setText(keys.get(position));
        holder.valueTV.setText(values.get(position));
    }

    @Override
    public int getItemCount() {
        return props.size();
    }

    public static class EventPropsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        TextView valueTV;
        public EventPropsViewHolder(@NonNull View itemView) {
            super(itemView);
             titleTV = itemView.findViewById(R.id.event_prop_item_key_tv);
             valueTV = itemView.findViewById(R.id.event_prop_item_value_tv);
        }
    }
}

