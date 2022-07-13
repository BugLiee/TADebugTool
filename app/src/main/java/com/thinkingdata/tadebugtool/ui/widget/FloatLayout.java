/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget;

import static com.thinkingdata.tadebugtool.common.TAConstants.KEY_TA_TOOL_ACTION;
import static com.thinkingdata.tadebugtool.common.TAConstants.VIEW_RADIUS;
import static com.thinkingdata.tadebugtool.utils.EventUtil.taTags;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.thinkingdata.tadebugtool.MainActivity;
import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAInstance;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupInfoView;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupLogView;
import com.thinkingdata.tadebugtool.utils.Base64Coder;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import cn.thinkingdata.android.aidl.ITAToolServer;

/**
 * < Float Layout >.
 *
 * @author bugliee
 * @create 2022/4/27
 * @since 1.0.0
 */
@SuppressLint("ViewConstructor")
public class FloatLayout extends FrameLayout {

    @SuppressLint("StaticFieldLeak")
    private static volatile FloatLayout instance = null;
    private final Activity mActivity;
    private CardView[] menuItems;

    private PopupInfoView popupInfoView = null;
    private PopupLogView popupLogView = null;

    CardView rootItem;
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager.LayoutParams mMenuLayoutParams;
    private WindowManager mWindowManager;
    private int radius = VIEW_RADIUS;
    private int rotation = 0;
    private int screenWidth;
    private int screenHeight;
    private float originX = 0f;
    private float originY = 0f;
    private RootItemOnTouchListener rootItemOnTouchListener;
    private int ANIMATION_DISTANCE = 5;
    // 0:up  1:down  2:move
    private int status = 0;
    //left 0 right 1
    private int location = 0;
    //close -1 open 1
    private int showItems = -1;
    //
    private boolean clickRoot = false;
    private MyHandler mHandler = null;
    private LinearLayout menuLayout;

    private String packageName = "";
    private String appName = "";
    private Drawable appIcon;

    private List<String> appIDs = new ArrayList<>();


    private boolean isAdvance;

    public void setAdvance() {
        isAdvance = true;
    }

    public interface ServiceConnectedListener{
        void onConnected(boolean isConnected);
    }

    private ServiceConnectedListener serviceConnectedListener;

    public void setServiceConnectedListener(ServiceConnectedListener serviceConnectedListener) {
        this.serviceConnectedListener = serviceConnectedListener;
    }

    private FloatLayout(@NonNull Activity activity) {
        super(activity);
        mActivity = activity;
    }

    public static FloatLayout getInstance(Activity activity) {
        if (instance == null) {
            synchronized (FloatLayout.class) {
                if (instance == null) {
                    instance = new FloatLayout(activity);
                }
            }
        }
        return instance;
    }

    public void init(String packageName, String appName, Drawable appIcon, List<String> appIDs) {
        if (isAdvance) {
            menuItems = new CardView[4];

        } else {
            menuItems = new CardView[2];

        }
        this.packageName = packageName;
        this.appName = appName;
        this.appIcon = appIcon;
        this.appIDs.clear();
        if (appIDs != null) {
            this.appIDs.addAll(appIDs);
        }
        mHandler = new MyHandler();
        mWindowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        rotation = mWindowManager.getDefaultDisplay().getRotation();
        initScreenSize();
        radius = Math.min(screenWidth, screenHeight) / 15;
        initLayoutParams();
        initMenu();
        initRootItem();

        if (!isAdvance){
            Intent intent = new Intent();
            intent.setAction(KEY_TA_TOOL_ACTION);
            intent.setPackage(packageName);
            mActivity.getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initRootItem() {
        rootItem = new CardView(mActivity);
        rootItem.setRadius(radius / 2f);
        rootItem.setBackgroundResource(R.mipmap.ic_root);
        rootItemOnTouchListener = new RootItemOnTouchListener();
        rootItem.setOnTouchListener(rootItemOnTouchListener);
        this.addView(rootItem, radius * 2, radius * 2);
    }

    public void attachToWindow() {
        mWindowManager.addView(menuLayout, mMenuLayoutParams);
        mWindowManager.addView(this, mLayoutParams);
    }

    private void initLayoutParams() {
        mLayoutParams = TAUtil.getLayoutParams(mActivity.getApplicationContext());
        mLayoutParams.width = radius * 2;
        setBackgroundColor(Color.TRANSPARENT);
        mLayoutParams.height = radius * 2;
        mLayoutParams.x = radius;
        mLayoutParams.y = radius * 5 / 2;
        //menu layout params
        mMenuLayoutParams = mLayoutParams;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMenu() {
        menuLayout = new LinearLayout(mActivity);
        menuLayout.setBackgroundColor(Color.TRANSPARENT);

        CardView blankItem = new CardView(mActivity);
        blankItem.setRadius(radius / 2f);
        blankItem.setCardBackgroundColor(Color.TRANSPARENT);
        blankItem.setVisibility(INVISIBLE);

        if (isAdvance) {
            CardView logItem = new CardView(mActivity);
            logItem.setRadius(radius / 2f);
            TextView logItemTV = new TextView(mActivity);
            logItemTV.setGravity(Gravity.CENTER);
            logItem.setBackgroundResource(R.mipmap.ic_log);
            logItem.addView(logItemTV);
            logItem.setVisibility(INVISIBLE);

            CardView infoItem = new CardView(mActivity);
            infoItem.setRadius(radius / 2f);
            TextView infoItemTV = new TextView(mActivity);
            infoItemTV.setGravity(Gravity.CENTER);
            infoItem.setBackgroundResource(R.mipmap.ic_info);
            infoItem.addView(infoItemTV);
            infoItem.setVisibility(INVISIBLE);

            logItem.setOnClickListener(v -> {
                if (popupInfoView != null) {
                    popupInfoView.unbindThis();
                    mWindowManager.removeView(popupInfoView);
                    popupInfoView = null;
                }
                if (popupLogView == null) {
                    popupLogView = PopupLogView.getInstance(mActivity);
                    popupLogView.init(packageName);
                    popupLogView.setRadius(radius / 2);

                    WindowManager.LayoutParams p = TAUtil.getLayoutParams(mActivity.getApplicationContext());
//                    p.x = mLayoutParams.x + radius * 2;
                    p.x = 0;
                    p.y = mLayoutParams.y + radius * 3;
//                    p.width = screenWidth - p.x * 2;
                    p.width = screenWidth;
                    p.height = screenHeight > screenWidth ? p.width * 3 / 2 : p.width * 2 / 3;
                    p.height = Math.min(p.height, screenHeight - p.y);
                    p.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                    mWindowManager.addView(popupLogView, p);
                } else {
                    popupLogView.unbindThis();
                    mWindowManager.removeView(popupLogView);
                    popupLogView = null;
                }
            });
            infoItem.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupLogView != null) {
                        popupLogView.unbindThis();
                        mWindowManager.removeView(popupLogView);
                        popupLogView = null;
                    }
                    if (popupInfoView == null) {
                        popupInfoView = PopupInfoView.getInstance(mActivity);
                        popupInfoView.init(packageName);
                        popupInfoView.setRadius(radius / 2f);

                        WindowManager.LayoutParams p = TAUtil.getLayoutParams(mActivity.getApplicationContext());
                        p.x = 0;
                        p.y = mLayoutParams.y + radius * 3;
                        p.width = screenWidth;
                        p.height = screenHeight > screenWidth ? p.width * 3 / 2 : p.width * 2 / 3;
                        p.height = Math.min(p.height, screenHeight - p.y);
                        p.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                        mWindowManager.addView(popupInfoView, p);
                    } else {
                        popupInfoView.unbindThis();
                        mWindowManager.removeView(popupInfoView);
                        popupInfoView = null;
                    }
                }
            });

            menuItems[1] = logItem;
            menuItems[2] = infoItem;
        }

        CardView settingsItem = new CardView(mActivity);
        settingsItem.setRadius(radius / 2f);
        TextView settingsItemTV = new TextView(mActivity);
        settingsItemTV.setGravity(Gravity.CENTER);
        settingsItem.setBackgroundResource(R.mipmap.ic_settings);
        settingsItem.addView(settingsItemTV);
        settingsItem.setVisibility(INVISIBLE);

        settingsItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRoot = true;
                performShowOrHide();
                //等待动画执行结束，延迟300 +100 ms
                rootItem.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        unbindThis("");
                    }
                }, 400);
            }
        });
        if (isAdvance) {
            menuItems[0] = blankItem;
            menuItems[3] = settingsItem;
        } else {
            menuItems[0] = blankItem;
            menuItems[1] = settingsItem;
        }

    }

    private void initScreenSize() {
        int[] screenSize = TAUtil.getDeviceSize(mActivity);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                status = 1;
                originX = event.getRawX();
                originY = event.getRawY();

                break;
            case MotionEvent.ACTION_UP:
                //relayout self
                if (status == 1) {
                    performShowOrHide();
                } else {
                    rootItem.setOnTouchListener(rootItemOnTouchListener);
                    if (status == 2 && showItems < 0) {
                        int cx = mLayoutParams.x;
                        int distance;
                        int leftDistance;
                        if (cx + radius >= screenWidth / 2) {
                            distance = screenWidth - (rotation == 0 ? 2 * radius : 5 * radius) - radius - cx;
                            ANIMATION_DISTANCE = Math.abs(ANIMATION_DISTANCE);
                        } else {
                            distance = radius - cx;
                            ANIMATION_DISTANCE = -Math.abs(ANIMATION_DISTANCE);
                        }
                        // set location
                        if (distance > 0) {
                            location = 1;
                        } else if (distance < 0) {
                            location = 0;
                        }

                        leftDistance = distance % Math.abs(ANIMATION_DISTANCE);
                        int times = distance / ANIMATION_DISTANCE + (leftDistance == 0 ? 0 : 1);
                        Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            int count = 0;

                            @Override
                            public void run() {
                                if (count == times) {
                                    timer.cancel();
                                    return;
                                }
                                count++;
                                if (count == times) {
                                    mLayoutParams.x += leftDistance;
                                } else {
                                    mLayoutParams.x += ANIMATION_DISTANCE;
                                }
                                mMenuLayoutParams = mLayoutParams;
                                mHandler.sendEmptyMessage(0);
                            }
                        };
                        long ANIMATION_DURATION = 400;
                        long ANIMATION_PERIOD_DEFAULT = 5;
                        long ANIMATION_DISTANCE_LIMIT_DEFAULT = 400;
                        timer.schedule(task, 0, Math.abs(distance) < ANIMATION_DISTANCE_LIMIT_DEFAULT ? ANIMATION_PERIOD_DEFAULT : ANIMATION_DURATION / times);
                    }
                }
                status = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX();
                float y = event.getRawY();
                //这里转int，避免出现int 和 float的计算，因为layoutParam.x/y 是int类型
                int dx = (int) (x - originX);
                int dy = (int) (y - originY);
                if (Math.abs(dx) < 5) {
                    dx = 0;
                }
                if (Math.abs(dy) < 5) {
                    dy = 0;
                }
                //location
                if (x + radius >= screenWidth / 2.0) {
                    location = 1;
                } else {
                    location = 0;
                }
                //
                if (dx == 0 && dy == 0) {
                    return super.onTouchEvent(event);
                }
                status = 2;
                if (showItems > 0) {
                    return super.onTouchEvent(event);
                }
                clickRoot = false;
                mLayoutParams.x += dx;
                mLayoutParams.y += dy;
                //横屏时多了导航栏 所以判断rotation
                if (mLayoutParams.x + radius > (screenWidth - (rotation == 0 ? 2 * radius : 5 * radius)) || mLayoutParams.x + radius < 2 * radius) {
                    mLayoutParams.x -= dx;
                }
                if (mLayoutParams.y + radius > (screenHeight - (2.5 + 2.5) * radius) || mLayoutParams.y + radius < 2.5 * radius) {
                    mLayoutParams.y -= dy;
                }

                mMenuLayoutParams = mLayoutParams;
                mWindowManager.updateViewLayout(this, mLayoutParams);
                mWindowManager.updateViewLayout(menuLayout, mMenuLayoutParams);
                originX = x;
                originY = y;
                break;
        }

        return super.onTouchEvent(event);
    }

    private void performShowOrHide() {
        if (clickRoot) {
            clickRoot = false;
            if (location == 0) {
                menuLayout.setLayoutDirection(LAYOUT_DIRECTION_LTR);
                //left
                RotateAnimation animation;
                Timer timer = new Timer();
                if (showItems < 0) {
                    animation = new RotateAnimation(0, VIEW_RADIUS, rootItem.getPivotX(), rootItem.getPivotY());
                    animation.setDuration(300);
                    animation.setInterpolator(new OvershootInterpolator());
                    animation.setFillAfter(true);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            mMenuLayoutParams.width += radius * 2 * 3.5;
                            mWindowManager.updateViewLayout(menuLayout, mMenuLayoutParams);
                            timer.schedule(new MyTimeTask(1), 240);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            timer.cancel();
                            showItems = -showItems;
                            rootItem.setOnTouchListener(rootItemOnTouchListener);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    animation = new RotateAnimation(VIEW_RADIUS, 0, rootItem.getPivotX(), rootItem.getPivotY());
                    animation.setDuration(300);
                    animation.setInterpolator(new OvershootInterpolator());
                    animation.setFillAfter(true);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            timer.schedule(new MyTimeTask(-1), 240);
                        }

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            timer.cancel();
                            showItems = -showItems;
                            rootItem.setOnTouchListener(rootItemOnTouchListener);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
                rootItem.startAnimation(animation);
            } else {
                //right
                menuLayout.setLayoutDirection(LAYOUT_DIRECTION_RTL);
                RotateAnimation animation;
                Timer timer = new Timer();
                if (showItems < 0) {
                    animation = new RotateAnimation(0, -VIEW_RADIUS, rootItem.getPivotX(), rootItem.getPivotY());
                    animation.setDuration(300);
                    animation.setInterpolator(new OvershootInterpolator());
                    animation.setFillAfter(true);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            mMenuLayoutParams.x -= radius * 2 * 3.5;
                            mMenuLayoutParams.width += radius * 2 * 3.5;
                            mWindowManager.updateViewLayout(menuLayout, mMenuLayoutParams);
                            timer.schedule(new MyTimeTask(2), 240);
                        }

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            showItems = -showItems;
                            rootItem.setOnTouchListener(rootItemOnTouchListener);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    animation = new RotateAnimation(-VIEW_RADIUS, 0, rootItem.getPivotX(), rootItem.getPivotY());
                    animation.setDuration(300);
                    animation.setInterpolator(new OvershootInterpolator());
                    animation.setFillAfter(true);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            timer.schedule(new MyTimeTask(-2), 240);
                        }

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            showItems = -showItems;
                            rootItem.setOnTouchListener(rootItemOnTouchListener);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }
                rootItem.startAnimation(animation);
            }
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public Animation getOpenAnimation(int index, boolean ltf) {
        Animation animation = new TranslateAnimation((ltf ? -1 : 1) * radius * 2 * index, 0, 0, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(300);
        return animation;
    }

    public Animation getCloseAnimation(int index, boolean ltf) {
        Animation animation = new TranslateAnimation(0, (ltf ? -1 : 1) * radius * 2 * index, 0, 0);
//        animation.setInterpolator(new OvershootInterpolator());
        animation.setFillAfter(true);
        animation.setDuration(300);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                menuLayout.removeAllViews();
                if (index == menuItems.length - 1) {
                    mMenuLayoutParams.width -= (radius * 2 * 3 + radius);
                    if (!ltf) {
                        mMenuLayoutParams.x += radius * 2 * 3.5;
                    }
                    mWindowManager.updateViewLayout(menuLayout, mMenuLayoutParams);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return animation;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (status == 1 || status == 0) {
            //移动时不判断当前窗口横竖屏
            int mRotation = mWindowManager.getDefaultDisplay().getRotation();
            if (rotation != mRotation && (rotation == 0 || mRotation == 0)) {
                //重设布局
                int beforeLocation = location;
                location = 0;
                menuLayout.removeAllViews();
                rotation = mRotation;
                int change = screenWidth;
                screenWidth = screenHeight;
                screenHeight = change;
                mLayoutParams.x = radius;
                mLayoutParams.y = radius * 5 / 2;
                mLayoutParams.width = radius * 2;
                mLayoutParams.height = radius * 2;
                mMenuLayoutParams = mLayoutParams;
                mWindowManager.updateViewLayout(this, mLayoutParams);
                mWindowManager.updateViewLayout(menuLayout, mMenuLayoutParams);

                Animation animation = new RotateAnimation(beforeLocation == 0 ? VIEW_RADIUS : -VIEW_RADIUS, 0, rootItem.getPivotX(), rootItem.getPivotY());
                animation.setDuration(300);
                animation.setInterpolator(new OvershootInterpolator());
                animation.setFillAfter(true);
                rootItem.startAnimation(animation);
                showItems = -1;
            }
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                mWindowManager.updateViewLayout(instance, mLayoutParams);
                mWindowManager.updateViewLayout(menuLayout, mMenuLayoutParams);
            } else if (msg.what == 1) {
                for (int i = 0; i < menuItems.length; i++) {
                    menuLayout.addView(menuItems[i], radius * 2, radius * 2);
                    if (i > 0) {
                        menuItems[i].setVisibility(VISIBLE);
                        menuItems[i].startAnimation(getOpenAnimation(i, true));
                    }
                }
            } else if (msg.what == -1) {
                for (int i = 0; i < menuItems.length; i++) {
                    if (i > 0) {
                        menuItems[i].startAnimation(getCloseAnimation(i, true));
                    }
                }
            } else if (msg.what == 2) {
                for (int i = 0; i < menuItems.length; i++) {
                    menuLayout.addView(menuItems[i], radius * 2, radius * 2);
                    if (i > 0) {
                        menuItems[i].setVisibility(VISIBLE);
                        menuItems[i].startAnimation(getOpenAnimation(i, false));
                    }
                }
            } else if (msg.what == -2) {
                for (int i = 0; i < menuItems.length; i++) {
                    if (i > 0) {
                        menuItems[i].startAnimation(getCloseAnimation(i, false));
                    }
                }
            }
        }
    }

    class RootItemOnTouchListener implements OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            rootItem.setOnTouchListener(null);
            clickRoot = true;
            return false;
        }
    }

    class MyTimeTask extends TimerTask {
        int what = -99;

        MyTimeTask(int what) {
            this.what = what;
        }

        @Override
        public void run() {
            mHandler.sendEmptyMessage(what);
        }
    }

    public static ITAToolServer mTAToolServer;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTAToolServer = ITAToolServer.Stub.asInterface(service);
            if (serviceConnectedListener != null) {
                serviceConnectedListener.onConnected(true);
            }

            StringBuilder appIDStr = new StringBuilder();
            for (int i = 0; i < appIDs.size(); i++) {
                appIDStr.append(appIDs.get(i));
                if (i < appIDs.size()-1) {
                    appIDStr.append(",");
                }
            }
            try {
                //检查appID并获取全局配置，预置属性等
                String ret = mTAToolServer.checkAppID(appIDStr.toString());
                try {
                    //构建实体进行持久化
                    JSONObject jsonObject = new JSONObject(ret);
                    /*jsonObject.put("instanceIDStr", appIDStr.toString());
                    jsonObject.put("timestamp", ts);
                    jsonObject.put("packageName", packageName);
                    jsonObject.put("appName", appName);
                    jsonObject.put("appIcon", new String(Base64Coder.encode(TAUtil.drawableToByte(appIcon))));
                    TADebugBehaviour behaviour = new TADebugBehaviour(jsonObject);
                    behaviour.save();*/
                    //读取sdk等配置信息并关联到此次行为持久化
                    requestSDKInfo(jsonObject);
                } catch (JSONException e) {
                    unbindThis("请确认您的appID是否正确，或者移除不可用的ID : " + ret + " 后重新尝试!");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTAToolServer = null;
            if (serviceConnectedListener != null) {
                serviceConnectedListener.onConnected(false);
            }
        }
    };

    public static String mAppID = "";
    public static String mAppName = "";

    private void requestSDKInfo(JSONObject publicConfig) {
        for (String appID : appIDs) {
            try {
                mAppID = appID;
                String retStr = mTAToolServer.getAppAndSDKInfoWithAppID(appID);
                try {
                    JSONObject instanceJSon = new JSONObject(retStr);
                    String timestamp = String.valueOf(System.currentTimeMillis());
                    mAppName = instanceJSon.optString("name") + timestamp;
                    TAUtil.mergeJSONObject(publicConfig, instanceJSon, TimeZone.getTimeZone("GMT:+8:00"));
                    instanceJSon.put("timestamp", timestamp);
                    instanceJSon.put("instanceID", mAppID);
                    //检查匹配和跨域问题
                    String url = instanceJSon.optString("url");

//                    TAUtil.checkAppIDAndUrl(mAppID, url.contains("/sync") ? url.substring(0, url.lastIndexOf("/sync")) : url);
                    instanceJSon.put("usable", true);
                    instanceJSon.put("unUsableReason", "我跨域了");

                    instanceJSon.put("packageName", packageName);
                    instanceJSon.put("appName", appName);
                    instanceJSon.put("appIcon", new String(Base64Coder.encode(TAUtil.drawableToByte(appIcon))));

                    TAInstance instance = new TAInstance(instanceJSon);
                    instance.save();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                //
                //开始收集SDK日志
                StringBuilder mOrder = new StringBuilder("logcat -s");
                for (String tag : taTags) {
                    String addStr = " " + tag + ":V";
                    mOrder.append(addStr);
                }
                mTAToolServer.trackLog(mOrder.toString());
                mTAToolServer.enableTransLog(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

    }

    public void unbindThis(String msg) {
        if (mTAToolServer != null) {
            try {
                mTAToolServer.unbindThis();
                mTAToolServer = null;
                mActivity.getApplicationContext().unbindService(serviceConnection);
                serviceConnection.onServiceDisconnected(null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mWindowManager.removeView(instance);
        instance = null;
        //start Activity
        Intent intent = new Intent(mActivity, MainActivity.class);
        if (!TextUtils.isEmpty(msg)) {
            intent.putExtra("errmsg", msg);
        }
        mActivity.startActivity(intent);
    }
}
