/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.fragments;

import static com.thinkingdata.tadebugtool.common.TAConstants.FILTER_TYPE_EVENT_TYPE;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAEvent;
import com.thinkingdata.tadebugtool.bean.TAInstance;
import com.thinkingdata.tadebugtool.common.TAConstants;
import com.thinkingdata.tadebugtool.ui.adapter.HistoryRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupFilterView;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * < History >.
 *
 * @author bugliee
 * @create 2022/6/28
 * @since 1.0.0
 */
public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private CardView menuBtn;
    private CardView historyMenu;
    private LinearLayout rootLL;
    private HistoryRecyclerViewAdapter adapter;
    private boolean mMenuOpened;

    private HistoryHandler handler;

    private int screenWidth = 0;
    private int screenHeight = 0;
    private int originWidth = 0;
    private LinearLayout.LayoutParams menuLayoutParams;

    EventListFragment fragment;

    private ImageView filterIV;
    private ImageView filterTimeIV;
    private ImageView filterPropIV;
    private ImageView filterEventTypeIV;

    private int lastSelectPos = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new HistoryHandler();
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.history_menu_rv);
        menuBtn = view.findViewById(R.id.history_menu_btn);
        historyMenu = view.findViewById(R.id.history_menu);
        rootLL = view.findViewById(R.id.history_root_LL);

        int[] screenSize = TAUtil.getDeviceSize(getActivity());
        screenWidth = screenSize[0] - 200;
        screenHeight = screenSize[1];
        menuLayoutParams = (LinearLayout.LayoutParams) historyMenu.getLayoutParams();
        originWidth = menuLayoutParams.width;

        if (adapter == null) {
            adapter = new HistoryRecyclerViewAdapter(getContext());
        }
        List<TAInstance> instances = LitePal.findAll(TAInstance.class);
        List<String> names = new ArrayList<>();
        List<List<TAEvent>> events = new ArrayList<>();
        for (int i = 0; i < instances.size(); i++) {
            names.add(instances.get(i).getName());
            List<TAEvent> eventList = LitePal.where("instanceName = ?", instances.get(i).getName() + instances.get(i).getTime()).find(TAEvent.class);
            events.add(eventList);
        }
        adapter.notifyDataChanged(names);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        //        fragment. == TAInstance  new
        fragment = new EventListFragment();
        ft.replace(R.id.fragment_content_fl, fragment);
        ft.commitNow();
        if (events.size() > 0) {
            fragment.notifyDataChange(events.get(0));
        }
        //
        adapter.setItemClickListener(new HistoryRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if (lastSelectPos != position) {
                    // switch TAInstance
                    fragment.resetFilter();
                    filterView = null;
                    fragment.notifyDataChange(events.get(position));
                }
            }
        });


        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleAnimationTask(mMenuOpened);
            }
        });

        initFilter(view);
        return view;
    }

    private void initFilter(View view) {
        filterIV = view.findViewById(R.id.instance_filter);
        filterTimeIV = view.findViewById(R.id.filter_time_sort);
        filterPropIV = view.findViewById(R.id.filter_prop);
        filterEventTypeIV = view.findViewById(R.id.filter_event_type);

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

    private void notifyDataChange(int type, String value) {
        fragment.notifyDataChange(type, value);
    }

    PopupFilterView filterView;

    private void showOrHideFilterMenu() {
        RelativeLayout layout = (RelativeLayout) filterIV.getParent();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();
        if (layout.getHeight() == filterIV.getHeight()) {
            layoutParams.height = filterIV.getHeight() * 4;
        } else {
            layoutParams.height = filterIV.getHeight();
        }
        layout.setLayoutParams(layoutParams);
    }

    private Timer timer;

    private class AnimationTask extends TimerTask {

        private int distance = 0;

        AnimationTask(boolean isMenuOpened) {
            distance = (isMenuOpened ? -1 : 1) * (screenWidth - originWidth) / 30;
            handler.setDistance(distance);
            menuBtn.startAnimation(getOpenOrCloseAnimation(isMenuOpened));
        }

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
        }
    }

    private class HistoryHandler extends Handler {

        private int distance = 0;

        public void setDistance(int distance) {
            this.distance = distance;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                menuLayoutParams.width += distance;
                rootLL.updateViewLayout(historyMenu, menuLayoutParams);
                if (distance > 0 && menuLayoutParams.width > screenWidth) {
                    menuLayoutParams.width = screenWidth;
                    rootLL.updateViewLayout(historyMenu, menuLayoutParams);
                    timer.cancel();
                }
                if (distance < 0 && menuLayoutParams.width < originWidth) {
                    menuLayoutParams.width = originWidth;
                    rootLL.updateViewLayout(historyMenu, menuLayoutParams);
                    timer.cancel();
                }
            }
        }
    }
    private void scheduleAnimationTask(boolean isMenuOpened) {
        timer = new Timer();
        timer.schedule(new AnimationTask(isMenuOpened),0,10);
    }


    public RotateAnimation getOpenOrCloseAnimation(boolean isMenuOpened) {
        RotateAnimation animation = new RotateAnimation(isMenuOpened ? 180 : 0, isMenuOpened ? 0 : 180, menuBtn.getPivotX(), menuBtn.getPivotY());
        animation.setDuration(300);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mMenuOpened = !mMenuOpened;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }
}
