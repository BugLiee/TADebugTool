/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAEvent;
import com.thinkingdata.tadebugtool.bean.TAMockEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * < eventList >.
 *
 * @author bugliee
 * @create 2022/7/5
 * @since 1.0.0
 */
public class MockEventListRecyclerViewAdapter extends RecyclerView.Adapter<MockEventListRecyclerViewAdapter.EventListViewHolder> {
    private Activity mActivity;
    private List<TAMockEvent> mList = new ArrayList<>();

    public MockEventListRecyclerViewAdapter(Activity activity) {
        mActivity = activity;
    }


    public void addItem(List<TAMockEvent> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EventListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_event_list, parent, false);
        return new EventListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventListViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.textView.setText(mList.get(position).getEventName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                //展开或者关闭
                holder.recyclerView.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
            }
        });
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        TAMockEvent event = mList.get(position);
        TreeMap<String, String> props = new TreeMap<>();
        props.put("eventName", event.getEventName());
        props.put("eventType", event.getEventType());
        props.put("accountID", event.getAccountID());
        props.put("distinctID", event.getDistinctID());
        props.put("props", event.getProps());
        props.put("timeStamp", event.getTimeStamp());
        holder.recyclerView.setAdapter(new EventPropsRecyclerViewAdapter(mActivity, props));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class EventListViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RecyclerView recyclerView;
        public EventListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.event_title_tv);
            recyclerView = itemView.findViewById(R.id.event_prop_rv);
        }
    }
}
