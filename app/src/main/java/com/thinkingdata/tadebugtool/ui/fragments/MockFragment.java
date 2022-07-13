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
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAMockEvent;
import com.thinkingdata.tadebugtool.ui.adapter.ExtensionTagFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * < eventList >.
 *
 * @author bugliee
 * @create 2022/7/5
 * @since 1.0.0
 */
public class MockFragment extends Fragment {

    private TabLayout tabLayout;
    private TabLayoutMediator mediator;
    private ViewPager2 viewPager;

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> tabs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mock, container, false);
        initFragments(view);
        return view;
    }

    private void initFragments(View view) {
        tabLayout = view.findViewById(R.id.mock_tl);
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
        tabLayout.setSelectedTabIndicator(null);
        viewPager = view.findViewById(R.id.mock_container_vp);
        viewPager.setOffscreenPageLimit(1);
        tabs.clear();
        fragments.clear();
        tabs.add("模拟事件");
        tabs.add("历史记录");
        MockContentFragment mockContentFragment = new MockContentFragment();
        MockHistoryFragment mockHistoryFragment = new MockHistoryFragment();

        mockContentFragment.setMockEventTrackListener(new MockContentFragment.MockEventTrackListener() {
            @Override
            public void onTrack(TAMockEvent event) {
                //更新数据
                mockHistoryFragment.addItem(event);
            }
        });

        fragments.add(mockContentFragment);
        fragments.add(mockHistoryFragment);
        viewPager.setAdapter(new ExtensionTagFragmentPagerAdapter(getChildFragmentManager(), getLifecycle(), fragments));
        mediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabs.get(position));
            }
        });
        mediator.attach();
    }

}
