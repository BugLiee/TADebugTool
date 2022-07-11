/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAInstance;
import com.thinkingdata.tadebugtool.ui.adapter.EventPropsRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.utils.Base64Coder;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

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

    ImageView bottomIV;
    ImageView appIconIV;
    TextView appNameTV;
    TextView packageNameTV;
    TextView deviceIDTV;
    TextView appIDTV;
    RecyclerView infoViewRV;

    private boolean bottomIVOpened = false;
    private HeaderHandler handler;

    public PopupHeaderView(Activity activity) {
        mActivity = activity;
        init();
        setUp();
    }

    private ClipboardManager clipboardManager;

    private void clipData(View view) {
        if (!(view instanceof TextView)) {
            return;
        }
        ((TextView) view).setTextColor(Color.GRAY);
        if (clipboardManager == null) {
            clipboardManager = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        ClipData clipData = ClipData.newPlainText("tatool text", ((TextView) view).getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ((TextView) view).setTextColor(Color.WHITE);
            }
        },300);
        SnackbarUtil.showSnackbarShort("copy success!");
    }

    private void init(){
        handler = new HeaderHandler();
        mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.header_main, null);
        bottomIV = mPopupView.findViewById(R.id.header_bottom_iv);
        infoViewRV = mPopupView.findViewById(R.id.header_info_rv);
        appIconIV = mPopupView.findViewById(R.id.header_app_icon_iv);
        appNameTV = mPopupView.findViewById(R.id.header_app_name_tv);
        packageNameTV = mPopupView.findViewById(R.id.header_package_name_tv);
        deviceIDTV = mPopupView.findViewById(R.id.header_deviceid_tv);
        appIDTV = mPopupView.findViewById(R.id.header_appid_tv);
        int[] screenSize = TAUtil.getDeviceSize(mActivity);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];

        bottomIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoViewRV.setVisibility(View.VISIBLE);
                scheduleAnimationTask();
            }
        });
        deviceIDTV.setClickable(true);

        deviceIDTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clipData(deviceIDTV);
                return false;
            }
        });

    }

    public RotateAnimation getOpenOrCloseAnimation() {
        RotateAnimation animation = new RotateAnimation(bottomIVOpened ? 180 : 0, bottomIVOpened ? 0 : 180, bottomIV.getPivotX(), bottomIV.getPivotY());
        animation.setDuration(300);
        animation.setFillAfter(true);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bottomIVOpened = !bottomIVOpened;
                if (!bottomIVOpened) {
                    infoViewRV.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }

    private Timer timer;

    private class AnimationTask extends TimerTask {
        private int count = 0;
        private int distance = 0;

        AnimationTask() {
            distance = (bottomIVOpened ? -1 : 1) * 600 / 30;
            handler.setDistance(distance);
            bottomIV.startAnimation(getOpenOrCloseAnimation());
        }

        @Override
        public void run() {
            if (count >= 30) {
                timer.cancel();
                return;
            }
            count++;
            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
        }
    }

    private class HeaderHandler extends Handler {

        private int distance = 0;

        public void setDistance(int distance) {
            this.distance = distance;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                update(getWidth(), getHeight() + distance);
            }
        }
    }

    private void scheduleAnimationTask() {
        timer = new Timer();
        timer.schedule(new AnimationTask(),0,10);
    }


    public void initRVData(TAInstance instance) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        infoViewRV.setLayoutManager(layoutManager);

        //从数据库取数据填充
        TreeMap<String, String> map = new TreeMap<>();
        appNameTV.setText(instance.getAppName());
        appIconIV.setBackground(TAUtil.byteToDrawable(Base64Coder.decode(instance.getAppIcon())));
        packageNameTV.setText(instance.getPackageName());
        appIDTV.setText(instance.getInstanceID());
        try {
            JSONObject presets = new JSONObject(instance.getPresetProps());
            deviceIDTV.setText(presets.optString("deviceID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //
        map.put("libVersion", instance.getLibVersion());
        map.put("timestamp", instance.getTimestamp());
        map.put("appVersionCode", instance.getAppVersionCode());
        map.put("presetProps", instance.getPresetProps());
        map.put("appVersionName", instance.getAppVersionName());
        map.put("enableLog", String.valueOf(instance.isEnableLog()));
        map.put("enableBGStart", String.valueOf(instance.isEnableBGStart()));
        map.put("disPresetProps", instance.getDisPresetProps());
        map.put("crashConfig", instance.getCrashConfig());
        map.put("mainProcessName", instance.getMainProcessName());
        map.put("retentionDays", String.valueOf(instance.getRetentionDays()));
        map.put("databaseLimit", String.valueOf(instance.getDatabaseLimit()));
        EventPropsRecyclerViewAdapter adapter = new EventPropsRecyclerViewAdapter(mActivity, map);
        infoViewRV.setAdapter(adapter);
    }

    private void setUp() {
        setContentView(mPopupView);
        setFocusable(true);
        setWidth(screenWidth);
        setHeight(screenHeight/4);
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
