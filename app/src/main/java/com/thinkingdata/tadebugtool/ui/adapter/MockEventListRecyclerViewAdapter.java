/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAMockEvent;
import com.thinkingdata.tadebugtool.common.TAConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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


    public void notifyDataChanged(List<TAMockEvent> list) {
        mList.clear();
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
        String eventName = mList.get(position).getEventName();
        String eventType = mList.get(position).getEventType();
        String time = mList.get(position).getTime();
        holder.titleTV.setText(eventName.equals("unknown") ? eventType : eventName);
        holder.timeTV.setText(time);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                //??????????????????
                holder.recyclerView.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
            }
        });
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        TAMockEvent event = mList.get(position);
        TreeMap<String, String> props = new TreeMap<>();
        //update or overwrite
        String eventID = event.getBFEventID();
        if (!TextUtils.isEmpty(eventID) && !eventID.equals("unknown")) {
            props.put("eventID", eventID);
        }
        if (!TextUtils.isEmpty(eventName) && !eventName.equals("unknown")) {
            props.put("eventName", eventName);
        }
        String accountID = event.getAccountID();
        if (!TextUtils.isEmpty(accountID) && !accountID.equals("unknown")) {
            props.put("accountID", accountID);
        }
        String distinctID = event.getDistinctID();
        if (!TextUtils.isEmpty(distinctID) && !distinctID.equals("unknown")) {
            props.put("distinctID", distinctID);
        }
        String mProps = event.getProps();
        if (!TextUtils.isEmpty(mProps) && !mProps.equals("unknown") && !mProps.equals("{}")) {
            props.put("props", mProps);
        }
        String presetProps = event.getPresetProps();
        if (!TextUtils.isEmpty(presetProps) && !presetProps.equals("unknown") && !presetProps.equals("{}")) {
            props.put("presetProps", presetProps);
        }

        String firstCheckID = event.getFirstCheckID();
        if (!TextUtils.isEmpty(firstCheckID) && !firstCheckID.equals("unknown")) {
            props.put("firstCheckID", firstCheckID);
        }
        props.put("eventType", eventType);
        props.put("time", time);
        holder.recyclerView.setAdapter(new EventPropsRecyclerViewAdapter(mActivity, props));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class EventListViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV;
        TextView timeTV;
        RecyclerView recyclerView;
        public EventListViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.event_title_tv);
            timeTV = itemView.findViewById(R.id.event_time_tv);
            recyclerView = itemView.findViewById(R.id.event_prop_rv);
        }
    }
}
