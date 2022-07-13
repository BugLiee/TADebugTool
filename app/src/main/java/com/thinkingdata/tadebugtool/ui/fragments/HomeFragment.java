/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.fragments;

import static com.thinkingdata.tadebugtool.common.TAConstants.FILTER_TYPE_EVENT_TYPE;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAEvent;
import com.thinkingdata.tadebugtool.bean.TAInstance;
import com.thinkingdata.tadebugtool.common.TAConstants;
import com.thinkingdata.tadebugtool.ui.adapter.EventListRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.ui.adapter.TagFragmentPagerAdapter;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupFilterView;

import org.litepal.LitePal;

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
    private ImageView filterIV;
    private ImageView filterTimeIV;
    private ImageView filterPropIV;
    private ImageView filterEventTypeIV;
    private ImageView filterResetIV;
    private ImageView filterRefreshIV;

    private List<EventListFragment> fragments = new ArrayList<>();
    private List<String> tabs = new ArrayList<>();

    private List<TAEvent> events = new ArrayList<>();

    private EventListFragment currentFragment;

    private boolean isConnected = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    public void notifyStateChange(boolean isConnected) {
        tabs.clear();
        mediator.detach();
        this.isConnected = isConnected;
        if (isConnected) {
            tabLayout.setTabTextColors(Color.BLACK, Color.GREEN);
            tabs.add("当前实例 ... 已连接");
        } else {
            tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
            tabs.add("当前实例");
        }
        mediator.attach();
    }

    public void notifyDataChange(List<TAEvent> list) {
        events = list;
        currentFragment.notifyDataChange(list);
    }

    public void notifyDataChange(int type, String value) {
        currentFragment.notifyDataChange(type, value);
    }

    public void initItemStateListener(EventListRecyclerViewAdapter.StateChangeListener listener){
        currentFragment.initItemStateListener(listener);
    }

    private void initView(View view) {
        tabLayout = view.findViewById(R.id.home_tl);
        tabLayout.setSelectedTabIndicator(null);
        viewPager = view.findViewById(R.id.home_vg);
        viewPager.setOffscreenPageLimit(1);
        tabs.clear();
        fragments.clear();
        if (isConnected) {
            tabLayout.setTabTextColors(Color.BLACK, Color.GREEN);
            tabs.add("当前实例 ... 已连接");
        } else {
            tabLayout.setTabTextColors(Color.BLACK, Color.WHITE);
            tabs.add("当前实例");
        }
//        tabs.add("历史记录");
        currentFragment = new EventListFragment();
        currentFragment.setActivity(getActivity());
        fragments.add(currentFragment);
//        fragments.add(new EventListFragment());
        viewPager.setAdapter(new TagFragmentPagerAdapter(getChildFragmentManager(), getLifecycle(), fragments));

        // smoothScroll 设置false 实现懒加载，避免多个Fragment切换时多次创建Fragment
        mediator = new TabLayoutMediator(tabLayout, viewPager, true, false, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabs.get(position));
            }
        });
        mediator.attach();

        //
        filterIV = view.findViewById(R.id.home_instance_filter);
        filterTimeIV = view.findViewById(R.id.filter_time_sort);
        filterPropIV = view.findViewById(R.id.filter_prop);
        filterEventTypeIV = view.findViewById(R.id.filter_event_type);
        filterResetIV = view.findViewById(R.id.filter_reset);
        filterRefreshIV = view.findViewById(R.id.filter_refresh);

        filterIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOrHideFilterMenu();
            }
        });
        filterTimeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopupWindow(TAConstants.FILTER_TYPE_TIME_SORT);
            }
        });
        filterPropIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopupWindow(TAConstants.FILTER_TYPE_PROP);
            }
        });
        filterEventTypeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopupWindow(FILTER_TYPE_EVENT_TYPE);
            }
        });
        filterResetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFragment.resetFilter();
                filterView = null;
                currentFragment.notifyDataChange(TAConstants.FILTER_TYPE_TIME_SORT, TAConstants.FILTER_TIME_ARRAY[0]);
            }
        });
        filterRefreshIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<TAInstance> instances = LitePal.findAll(TAInstance.class);
                if (instances.size() > 0) {
                    TAInstance instance = instances.get(instances.size() - 1);
                    String instanceName = instance.getName() + instance.getTime();
                    List<TAEvent> events = LitePal.where("instanceName = ?", instanceName).find(TAEvent.class);
                    notifyDataChange(events);
                }
            }
        });

        List<TAInstance> instances = LitePal.findAll(TAInstance.class);
        if (instances.size() > 0) {
            TAInstance instance = instances.get(instances.size() - 1);
            String instanceName = instance.getName() + instance.getTime();
            List<TAEvent> events = LitePal.where("instanceName = ?", instanceName).find(TAEvent.class);
            notifyDataChange(events);
        }
    }


    private void showFilterPopupWindow(int type) {
        if (filterView == null) {
            filterView = new PopupFilterView(getActivity());
            filterView.setOnSubmitClickListener(new PopupFilterView.OnSubmitClickListener() {
                @Override
                public void onClick(int type, String value) {
                    notifyDataChange(type, value);
                }
            });
        }
        filterView.setType(type);
        filterView.show();
    }

    PopupFilterView filterView;

    private void showOrHideFilterMenu() {
        RelativeLayout layout = (RelativeLayout) filterIV.getParent();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
        if (layout.getHeight() == filterIV.getHeight()) {
            layoutParams.height = filterIV.getHeight() * 6;
        } else {
            layoutParams.height = filterIV.getHeight();
        }
        layout.setLayoutParams(layoutParams);
    }
}
