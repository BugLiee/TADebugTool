/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
import com.thinkingdata.tadebugtool.ui.adapter.GuidanceRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * < Guidance >.
 *
 * @author bugliee
 * @create 2022/6/28
 * @since 1.0.0
 */
public class GuidanceFragment extends Fragment {
    private RecyclerView recyclerView;
    private CardView menuBtn;
    private CardView guidanceMenu;
    private LinearLayout rootLL;
    private boolean mMenuOpened;

    private GuidanceHandler handler;

    private int screenWidth = 0;
    private int screenHeight = 0;
    private int originWidth = 0;
    private LinearLayout.LayoutParams menuLayoutParams;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        handler = new GuidanceHandler();
        View view = inflater.inflate(R.layout.fragment_guidance, container, false);
        recyclerView = view.findViewById(R.id.guidance_menu_rv);
        menuBtn = view.findViewById(R.id.guidance_menu_btn);
        guidanceMenu = view.findViewById(R.id.guidance_menu);
        rootLL = view.findViewById(R.id.guidance_root_LL);
        List<String> list = new ArrayList<>();
        list.add("这是第1张");
        list.add("这是第2张");
        list.add("这是第3张");
        list.add("这是第4张");
        list.add("这是第5张");
        list.add("这是第6张");
        list.add("这是第7张");
        list.add("这是第8张");
        list.add("这是第9张");
        list.add("这是第10张");
        list.add("这是第11张");
        list.add("这是第12张");
        list.add("这是第13张");
        list.add("这是第14张");
        list.add("这是第15张");

        int[] screenSize = TAUtil.getDeviceSize(getActivity());
        screenWidth = screenSize[0] - 200;
        screenHeight = screenSize[1];
        menuLayoutParams = (LinearLayout.LayoutParams) guidanceMenu.getLayoutParams();
        originWidth = menuLayoutParams.width;
        GuidanceRecyclerViewAdapter adapter = new GuidanceRecyclerViewAdapter(getContext(), list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        GuidanceContentFragment fragment = new GuidanceContentFragment();
        ft.add(R.id.fragment_content_fl, fragment);
        ft.commitNow();
        adapter.setItemClickListener(new GuidanceRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                fragment.setTextViewContent("我是第 " + ++position + " 个假的fragment");
            }
        });


        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleAnimationTask(mMenuOpened);
            }
        });
        return view;
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

    private class GuidanceHandler extends Handler {

        private int distance = 0;

        public void setDistance(int distance) {
            this.distance = distance;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                menuLayoutParams.width += distance;
                rootLL.updateViewLayout(guidanceMenu, menuLayoutParams);
                if (distance > 0 && menuLayoutParams.width > screenWidth) {
                    menuLayoutParams.width = screenWidth;
                    rootLL.updateViewLayout(guidanceMenu, menuLayoutParams);
                    timer.cancel();
                }
                if (distance < 0 && menuLayoutParams.width < originWidth) {
                    menuLayoutParams.width = originWidth;
                    rootLL.updateViewLayout(guidanceMenu, menuLayoutParams);
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
