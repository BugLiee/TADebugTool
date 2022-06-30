/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.ui.widget.ResizeableLayout;
import com.thinkingdata.tadebugtool.utils.TAUtil;
import com.thinkingdata.tadebugtool.ui.adapter.AppInfoRecyclerViewAdapter;

/**
 * < PopoupWindow >.
 *
 * @author bugliee
 * @create 2022/6/21
 * @since 1.0.0
 */
public class PopupAppListView extends PopupWindow {

    private ResizeableLayout mPopupView;
    private RecyclerView recyclerView;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private Activity mActivity;

    public PopupAppListView(Activity activity, AppInfoRecyclerViewAdapter adapter) {
        mActivity = activity;
        init(adapter);
        setup();
    }

    private void init(AppInfoRecyclerViewAdapter adapter) {
        mPopupView = (ResizeableLayout)LayoutInflater.from(mActivity).inflate(R.layout.popup_app_list, null);
        recyclerView = mPopupView.findViewById(R.id.app_info_list_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        int[] screenSize = TAUtil.getDeviceSize(mActivity);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
    }



    private void setup() {
        setContentView(mPopupView);
        setWidth(screenWidth);
        setHeight(screenHeight * 2 / 3);
        setFocusable(true);
        setAnimationStyle(R.style.PopupAppListViewStyle);
        setOutsideTouchable(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().setAttributes(lp);
            }
        });

        mPopupView.setShowHideLayoutListener(new ResizeableLayout.ShowHideLayoutListener() {
            @Override
            public void callback() {
                dismiss();
            }
        });
    }
}
