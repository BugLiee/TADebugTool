/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.utils.TAUtil;

/**
 * < Header >.
 *
 * @author bugliee
 * @create 2022/6/28
 * @since 1.0.0
 */
public class PopupHeaderView extends PopupWindow {

    private View mPopupView;
    private Activity mActivity;
    private int screenWidth = 0;
    private int screenHeight = 0;

    public PopupHeaderView(Activity activity) {
        mActivity = activity;
        init();
        setUp();
    }

    private void init(){
       mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.header_main, null);
        int[] screenSize = TAUtil.getDeviceSize(mActivity);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
    }

    private void setUp() {
        setContentView(mPopupView);
        setFocusable(true);
        setWidth(screenWidth);
        setHeight(screenHeight/3);
        setAnimationStyle(R.style.PopupHeaderViewStyle);
//        setOutsideTouchable(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().setAttributes(lp);
            }
        });

    }

}
