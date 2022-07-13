/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAMockEvent;
import com.thinkingdata.tadebugtool.ui.adapter.ExtensionTagFragmentPagerAdapter;
import com.thinkingdata.tadebugtool.ui.adapter.MockEventListRecyclerViewAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

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
        List<TAMockEvent> list = new ArrayList<>(LitePal.findAll(TAMockEvent.class));
        mockEventListRecyclerViewAdapter.addItem(list);
    }


    public void addItem(TAMockEvent event) {
        List<TAMockEvent> events = new ArrayList<>();
        events.add(event);
        mockEventListRecyclerViewAdapter.addItem(events);
    }
}
