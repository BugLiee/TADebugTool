/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool;

import android.app.Application;

import org.litepal.LitePal;

/**
 * < application >.
 *
 * @author bugliee
 * @create 2022/7/1
 * @since 1.0.0
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
