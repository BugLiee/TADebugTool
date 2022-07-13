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

    public static void showSnackBarShort(String msg) {
        if (mView != null) {
            Snackbar.make(mView, msg, SHORT_DURATION).show();
        }
    }

    public static void showSnackBarShort(String msg, View rooView) {
        if (rooView != null) {
            Snackbar.make(rooView, msg, SHORT_DURATION).show();
        }
    }

    public static void showSnackBarLong(String msg) {
        if (mView != null) {
            Snackbar.make(mView, msg, LONG_DURATION).show();
        }
    }

    public static void showSnackBarLong(String msg, View rooView) {
        if (rooView != null) {
            Snackbar.make(rooView, msg, LONG_DURATION).show();
        }
    }

    public static void showSnackBarMid(String msg) {
        if (mView != null) {
            Snackbar.make(mView, msg, MID_DURATION).show();
        }
    }

    public static void showSnackBarMid(String msg, View rooView) {
        if (rooView != null) {
            Snackbar.make(rooView, msg, MID_DURATION).show();
        }
    }
}
