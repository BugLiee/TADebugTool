/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.fragments;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import static com.thinkingdata.tadebugtool.common.TAConstants.ADVANCE_EVENT_LiST;
import static com.thinkingdata.tadebugtool.common.TAConstants.AUTO_TRACK_EVENT_LIST;
import static com.thinkingdata.tadebugtool.common.TAConstants.FILTER_EVENT_TYPE_ARRAY;
import static com.thinkingdata.tadebugtool.common.TAConstants.FILTER_TIME_ARRAY;
import static com.thinkingdata.tadebugtool.common.TAConstants.FILTER_TYPE_TIME_SORT;
import static com.thinkingdata.tadebugtool.common.TAConstants.PROFILE_EVENT_LiST;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAEvent;
import com.thinkingdata.tadebugtool.common.TAConstants;
import com.thinkingdata.tadebugtool.ui.adapter.EventListRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * < eventList >.
 *
 * @author bugliee
 * @create 2022/7/5
 * @since 1.0.0
 */
public class EventListFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventListRecyclerViewAdapter adapter;
    private List<TAEvent> allEvents = new ArrayList<>();
    private List<TAEvent> showEvents = new ArrayList<>();

    private String defaultEventType = FILTER_EVENT_TYPE_ARRAY[0];
    private boolean isReverse = false;

    private boolean timeFilter = false;
    private boolean propFilter = false;
    private boolean eventTypeFilter = false;

    private String timeFilterValue = FILTER_TIME_ARRAY[0];
    private String propFilterValue = "";
    private String eventTypeFilterValue = FILTER_EVENT_TYPE_ARRAY[0];

    private Activity mActivity;
    public EventListFragment() {
    }

    public void setActivity(Activity activity){
        mActivity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventlist, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.event_list_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if (adapter == null) {
            adapter = new EventListRecyclerViewAdapter(mActivity);
        }
        recyclerView.setAdapter(adapter);
    }


    public void notifyDataChange(List<TAEvent> list) {
        allEvents.clear();
        allEvents.addAll(list);
        filterShowEvents();
    }

    private void filterShowEvents() {
        showEvents.clear();
        showEvents.addAll(allEvents);
        if (timeFilter) {
            if (timeFilterValue.equals(FILTER_TIME_ARRAY[1])) {
                Collections.reverse(showEvents);
            }
        }

        if (propFilter) {
            if (propFilterValue.isEmpty()) {
                propFilter = false;
            }else {
                List<TAEvent> resultList = new ArrayList<>();
                for (TAEvent event : showEvents) {
                    if (event.getProps().contains(propFilterValue) || event.getPresetProps().contains(propFilterValue)) {
                        resultList.add(event);
                    }
                }
                if (resultList.size() < showEvents.size()) {
                    showEvents = resultList;
                }
            }
        }

        if (eventTypeFilter) {
            if (!defaultEventType.equals(eventTypeFilterValue)) {
                List<TAEvent> resultList = new ArrayList<>();
//            new String[]{"全量事件", "自动采集事件", "用户事件", "自定义事件"};
                if (eventTypeFilterValue.equals(FILTER_EVENT_TYPE_ARRAY[0])) {

                } else if (eventTypeFilterValue.equals(FILTER_EVENT_TYPE_ARRAY[1])) {
                    for (TAEvent event :showEvents) {
                        if (AUTO_TRACK_EVENT_LIST.contains(event.getEventName())) {
                            resultList.add(event);
                        }
                    }
                } else if (eventTypeFilterValue.equals(FILTER_EVENT_TYPE_ARRAY[2])) {
                    for (TAEvent event : showEvents) {
                        if (PROFILE_EVENT_LiST.contains(event.getEventType())) {
                            resultList.add(event);
                        }
                    }
                } else if (eventTypeFilterValue.equals(FILTER_EVENT_TYPE_ARRAY[3])) {
                    for (TAEvent event : showEvents) {
                        if (!PROFILE_EVENT_LiST.contains(event.getEventType()) && !AUTO_TRACK_EVENT_LIST.contains(event.getEventName()) && !ADVANCE_EVENT_LiST.contains(event.getEventType())) {
                            resultList.add(event);
                        }
                    }
                }
                if (resultList.size() < showEvents.size()) {
                    showEvents = resultList;
                }
            }
        }
        if (adapter == null) {
            adapter = new EventListRecyclerViewAdapter(mActivity);
        }
        adapter.notifyItemsChanged(showEvents);
    }

    public void notifyDataChange(int type, String value) {
        if (type == FILTER_TYPE_TIME_SORT) {
            timeFilter = true;
            timeFilterValue = value;
        } else if (type == TAConstants.FILTER_TYPE_PROP) {
            propFilter = true;
            propFilterValue = value;
        } else if (type == TAConstants.FILTER_TYPE_EVENT_TYPE) {
            eventTypeFilter = true;
            eventTypeFilterValue = value;
        }
        filterShowEvents();
    }

    public void initItemStateListener(EventListRecyclerViewAdapter.StateChangeListener listener){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (adapter != null) {
                    adapter.setStateChangeListener(listener);
                    cancel();
                }
            }
        },0,200);
    }

    public void resetFilter() {
        isReverse = false;
        timeFilter = false;
        propFilter = false;
        eventTypeFilter = false;
        timeFilterValue = FILTER_TIME_ARRAY[0];
        propFilterValue = "";
        eventTypeFilterValue = FILTER_EVENT_TYPE_ARRAY[0];
    }
}
