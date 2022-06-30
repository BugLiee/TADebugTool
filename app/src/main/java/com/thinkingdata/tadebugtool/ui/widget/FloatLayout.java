/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget;

import static com.thinkingdata.tadebugtool.common.TAConstants.VIEW_RADIUS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
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
import com.thinkingdata.tadebugtool.utils.TAUtil;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupInfoView;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupLogView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * < Float Layout >.
 *
 * @author bugliee
 * @create 2022/4/27
 * @since 1.0.0
 */
public class FloatLayout extends FrameLayout {

    private static FloatLayout instance = null;
    private String packageName = "";

    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager.LayoutParams mMenuLayoutParams;
    private final Activity mActivity;
    private WindowManager mWindowManager;
    private int radius = VIEW_RADIUS;
    private int rotation = 0;
    private int screenWidth;
    private int screenHeight;
    private float originX = 0f;
    private float originY = 0f;

    private RootItemOnTouchListener rootItemOnTouchListener;

    private final long ANIMATION_DURATION = 400;
    private final long ANIMATION_PERIOD_DEFAULT = 5;
    private final long ANIMATION_DISTANCE_LIMIT_DEFAULT = 400;
    private int ANIMATION_DISTANCE = 5;

    // 0:up  1:down  2:move
    private int status = 0;
    //left 0 right 1
    private int location = 0;
    //close -1 open 1
    private int showItems = -1;
    //
    private boolean clickRoot = false;
    CardView rootItem;

    private final CardView[] menuItems = new CardView[4];
    private MyHandler mHandler = null;
    private LinearLayout menuLayout;

    private PopupInfoView popupInfoView = null;
    private PopupLogView popupLogView = null;

    //内部解锁相关
    //锁屏9键坐标
    private int[] loc1 = new int[2];
    private int[] loc2 = new int[2];
    private int[] loc3 = new int[2];
    private int[] loc4 = new int[2];
    private int[] loc5 = new int[2];
    private int[] loc6 = new int[2];
    private int[] loc7 = new int[2];
    private int[] loc8 = new int[2];
    private int[] loc9 = new int[2];

    private boolean enableLog = false;
    List<Integer> keyList = new ArrayList<>();
    List<Integer> secureList = new ArrayList<>();
    List<int[]> sourceList = new ArrayList<>();

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

    public void init(String packageName) {
        mHandler = new MyHandler();
        this.packageName = packageName;
        mWindowManager = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        rotation = mWindowManager.getDefaultDisplay().getRotation();
        initScreenSize();
        radius = Math.min(screenWidth, screenHeight) / 15;
        initLayoutParams();
        initMenu();
        initRootItem();
    }


    class RootItemOnTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            rootItem.setOnTouchListener(null);
            clickRoot = true;
            return false;
        }
    }


    private void initRootItem() {
        rootItem = new CardView(mActivity);
        rootItem.setRadius(radius / 2);
        rootItem.setBackgroundResource(R.mipmap.ic_root);
        rootItemOnTouchListener = new RootItemOnTouchListener();
        rootItem.setOnTouchListener(rootItemOnTouchListener);
        this.addView(rootItem, radius * 2, radius * 2);
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
        //解锁相关
        //初始化9键
        initSecureKey();
        //menu layout params
        mMenuLayoutParams = mLayoutParams;
    }

    private void initSecureKey() {
        int oWidth = radius * 2;
        int centerLocX = screenWidth / 2;
        int centerLocY = screenHeight / 2;

        loc1[0] = centerLocX - oWidth * 2;
        loc1[1] = centerLocY - oWidth * 2;
        loc2[0] = centerLocX;
        loc2[1] = centerLocY - oWidth * 2;
        loc3[0] = centerLocX + oWidth * 2;
        loc3[1] = centerLocY - oWidth * 2;
        loc4[0] = centerLocX - oWidth *2;
        loc4[1] = centerLocY;
        loc5[0] = centerLocX;
        loc5[1] = centerLocY;
        loc6[0] = centerLocX + oWidth *2;
        loc6[1] = centerLocY;
        loc7[0] = centerLocX - oWidth *2;
        loc7[1] = centerLocY + oWidth * 2;
        loc8[0] = centerLocX;
        loc8[1] = centerLocY + oWidth * 2;
        loc9[0] = centerLocX + oWidth * 2;
        loc9[1] = centerLocY + oWidth * 2;

        sourceList.add(loc1);
        sourceList.add(loc2);
        sourceList.add(loc3);
        sourceList.add(loc4);
        sourceList.add(loc5);
        sourceList.add(loc6);
        sourceList.add(loc7);
        sourceList.add(loc8);
        sourceList.add(loc9);

        secureList.add(4);
        secureList.add(1);
        secureList.add(2);
        secureList.add(3);
        secureList.add(6);
        secureList.add(9);
        secureList.add(8);
        secureList.add(7);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initMenu() {
        menuLayout = new LinearLayout(mActivity);
        menuLayout.setBackgroundColor(Color.TRANSPARENT);

        CardView blankItem = new CardView(mActivity);
        blankItem.setRadius(radius / 2);
        blankItem.setCardBackgroundColor(Color.TRANSPARENT);
        blankItem.setVisibility(INVISIBLE);

        CardView logItem = new CardView(mActivity);
        logItem.setRadius(radius / 2);
//        logItem.setCardBackgroundColor(Color.YELLOW);
        TextView logItemTV = new TextView(mActivity);
//        logItemTV.setText("LOG");
        logItemTV.setGravity(Gravity.CENTER);
        logItem.setBackgroundResource(R.mipmap.ic_log);
        logItem.addView(logItemTV);
        logItem.setVisibility(INVISIBLE);

        CardView infoItem = new CardView(mActivity);
        infoItem.setRadius(radius / 2);
//        infoItem.setCardBackgroundColor(Color.BLACK);
        TextView infoItemTV = new TextView(mActivity);
//        infoItemTV.setText("INFO");
        infoItemTV.setGravity(Gravity.CENTER);
        infoItem.setBackgroundResource(R.mipmap.ic_info);
        infoItem.addView(infoItemTV);
        infoItem.setVisibility(INVISIBLE);

        CardView settingsItem = new CardView(mActivity);
        settingsItem.setRadius(radius / 2);
//        settingsItem.setCardBackgroundColor(Color.RED);
        TextView settingsItemTV = new TextView(mActivity);
//        settingsItemTV.setText("SET");
        settingsItemTV.setGravity(Gravity.CENTER);
        settingsItem.setBackgroundResource(R.mipmap.ic_settings);
        settingsItem.addView(settingsItemTV);
        settingsItem.setVisibility(INVISIBLE);

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
                    popupInfoView.setRadius(radius / 2);

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

        settingsItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupInfoView != null) {
                    popupInfoView.unbindThis();
                    mWindowManager.removeView(popupInfoView);
                    popupInfoView = null;
                }
                if (popupLogView != null) {
                    popupLogView.unbindThis();
                    mWindowManager.removeView(popupLogView);
                    popupLogView = null;
                }
                clickRoot = true;
                performShowOrHide();
                //等待动画执行结束，延迟300 +100 ms
                rootItem.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWindowManager.removeView(instance);
                        instance = null;
                        //start Activity
                        Intent intent = new Intent(mActivity, MainActivity.class);
                        mActivity.startActivity(intent);
                    }
                }, 400);
            }
        });


        menuItems[0] = blankItem;
        menuItems[1] = logItem;
        menuItems[2] = infoItem;
        menuItems[3] = settingsItem;
    }

    private void initScreenSize() {
        int[] screenSize = TAUtil.getDeviceSize(mActivity);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
    }

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
                    //解锁
                    if (!enableLog) {
                        if (keyList.size() == 8) {
                            enableLog = true;
                            for (int i = 0; i < keyList.size(); i++) {
                                if (!keyList.get(i).equals(secureList.get(i))) {
                                    enableLog = false;
                                    break;
                                }
                            }
                        }
                        keyList.clear();
                        if (enableLog) {
                            Log.d("bugliee", "恭喜你解锁成功！");
                        }
                    }
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
                        timer.schedule(task, 0, Math.abs(distance) < ANIMATION_DISTANCE_LIMIT_DEFAULT ? ANIMATION_PERIOD_DEFAULT : ANIMATION_DURATION / times);
                    }
                }
                status = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX();
                float y = event.getRawY();
                checkKeyLoc(x, y);
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

    private void checkKeyLoc(float x, float y) {
        for (int i = 0; i < sourceList.size(); i++) {
            int[] loc = sourceList.get(i);
            if (x >= loc[0] - radius && x <= loc[0] + radius && y >= loc[1] - radius && y <= loc[1] + radius) {
                if (!keyList.contains(i + 1)) {
                    keyList.add(i + 1);
                    break;
                }
            }
        }
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

                if (popupLogView != null) {
                    popupLogView.unbindThis();
                    mWindowManager.removeView(popupLogView);
                    popupLogView = null;
                }
            }
        }
    }
}
