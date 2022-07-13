/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAMockEvent;
import com.thinkingdata.tadebugtool.ui.adapter.MockEventListRecyclerViewAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * < Extension >.
 *
 * @author bugliee
 * @create 2022/6/28
 * @since 1.0.0
 */
public class MockHistoryFragment extends Fragment {

    RecyclerView mockHistoryEventRV;

    MockEventListRecyclerViewAdapter mockEventListRecyclerViewAdapter;

    SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mock_history, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mockHistoryEventRV = view.findViewById(R.id.mock_history_rv);
        mockHistoryEventRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (mockEventListRecyclerViewAdapter == null) {
            mockEventListRecyclerViewAdapter = new MockEventListRecyclerViewAdapter(getActivity());
        }
        mockHistoryEventRV.setAdapter(mockEventListRecyclerViewAdapter);
        refreshData();

        swipeRefreshLayout = view.findViewById(R.id.swipe_fresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },800);
            }
        });
    }

    private void refreshData() {
        List<TAMockEvent> list = new ArrayList<>(LitePal.findAll(TAMockEvent.class));
        mockEventListRecyclerViewAdapter.notifyDataChanged(list);
    }
}
