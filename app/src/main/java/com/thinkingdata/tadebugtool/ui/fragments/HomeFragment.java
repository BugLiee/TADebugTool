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
import com.thinkingdata.tadebugtool.ui.adapter.TagFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * < Home >.
 *
 * @author bugliee
 * @create 2022/6/28
 * @since 1.0.0
 */
public class HomeFragment extends Fragment {
    private TabLayout tabLayout;
    private TabLayoutMediator mediator;
    private ViewPager2 viewPager;

    private List<EventListFragment> fragments = new ArrayList<>();
    private List<String> tabs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tabLayout = view.findViewById(R.id.home_tl);
        tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
        tabLayout.setSelectedTabIndicator(null);
        viewPager = view.findViewById(R.id.home_vg);
        viewPager.setOffscreenPageLimit(1);
        tabs.clear();
        fragments.clear();
        tabs.add("我是实例1");
        tabs.add("我是实例2");
        fragments.add(new EventListFragment());
        fragments.add(new EventListFragment());
        viewPager.setAdapter(new TagFragmentPagerAdapter(getChildFragmentManager(), getLifecycle(), fragments));
        mediator = new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabs.get(position));
            }
        });
        mediator.attach();
    }


}
