/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

/**
 * < snackbar >.
 *
 * @author bugliee
 * @create 2022/7/6
 * @since 1.0.0
 */
public class SnackbarUtil {
    private static final int SHORT_DURATION = 300;
    private static final int MID_DURATION = 800;
    private static final int LONG_DURATION = 1800;
    private static View mView = null;

    public static void setContentView(View view) {
        mView = view;
    }

    public static void showSnackbarShort(String msg) {
        if (mView != null) {
            Snackbar.make(mView, msg, SHORT_DURATION).show();
        }
    }

    public static void showSnackbarLong(String msg) {
        if (mView != null) {
            Snackbar.make(mView, msg, LONG_DURATION).show();
        }
    }

    public static void showSnackbarMid(String msg) {
        if (mView != null) {
            Snackbar.make(mView, msg, MID_DURATION).show();
        }
    }
}
