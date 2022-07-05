/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.fragments;

import static androidx.recyclerview.widget.RecyclerView.VERTICAL;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAEvent;
import com.thinkingdata.tadebugtool.ui.adapter.EventListRecyclerViewAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * < eventList >.
 *
 * @author bugliee
 * @create 2022/7/5
 * @since 1.0.0
 */
public class EventListFragment extends Fragment {
    private RecyclerView recyclerView;

    EventListFragment() {

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        List<TAEvent> list = new ArrayList<>(LitePal.findAll(TAEvent.class));

        recyclerView.setAdapter(new EventListRecyclerViewAdapter(getActivity(), list));
    }
}
