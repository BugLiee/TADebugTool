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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAEvent;

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
public class EventListRecyclerViewAdapter extends RecyclerView.Adapter<EventListRecyclerViewAdapter.EventListViewHolder> {
    private Activity mActivity;
    private List<TAEvent> mList = new ArrayList<>();

    public interface StateChangeListener{
       void onStateChange(boolean isClosed);
    }

    StateChangeListener stateChangeListener;


    public void setStateChangeListener(StateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }

    public EventListRecyclerViewAdapter(Activity activity) {
        mActivity = activity;
    }


    public void notifyItemsChanged(List<TAEvent> list) {
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
        TAEvent event = mList.get(position);
        String eventName = event.getEventName();
        String eventType = event.getEventType();
        String time = event.getTime();
        holder.textView.setText(TextUtils.isEmpty(eventName) ? eventType : eventName);
        holder.timeTV.setText(time);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                //展开或者关闭
                holder.recyclerView.setVisibility(v.isSelected() ? View.VISIBLE : View.GONE);
                if (stateChangeListener != null) {
                    stateChangeListener.onStateChange(!v.isSelected());
                }
            }
        });
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
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
        props.put("UUID", event.getEventID());
        EventPropsRecyclerViewAdapter adapter = new EventPropsRecyclerViewAdapter(mActivity, props);
        holder.recyclerView.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class EventListViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView timeTV;
        RecyclerView recyclerView;
        LinearLayout linearLayout;
        public EventListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.event_title_tv);
            recyclerView = itemView.findViewById(R.id.event_prop_rv);
            linearLayout = itemView.findViewById(R.id.event_title_top_ll);
            timeTV = itemView.findViewById(R.id.event_time_tv);
        }
    }
}
