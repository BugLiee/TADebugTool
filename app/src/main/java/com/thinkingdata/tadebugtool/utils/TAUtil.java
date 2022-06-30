/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.utils;

import static com.thinkingdata.tadebugtool.common.TAConstants.KEY_TA_TOOL_ACTION;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * < TATool >.
 *
 * @author bugliee
 * @create 2022/4/16
 * @since 1.0.0
 */
public class TAUtil {

    public static String VERSION = "1.0.0";

    public static final int FLOAT_PERMISSION_REQUEST_CODE = 9527;

    /**
     * < check user-permission [android.permission.SYSTEM_ALERT_WINDOW]>.
     *
     * @author bugliee
     * @create 2022/4/16
     * @param context Context
     * @return {@link boolean}
     */
    public static boolean checkFloatingPermission(Context context) {
        // < api 19 Android 4.4
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return true;
        }
        // < api 23 Android 6.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class<?> clazz = Class.forName("android.content.Context");
                Field opsService = clazz.getDeclaredField("APP_OPS_SERVICE");
                opsService.setAccessible(true);
                Object obj = opsService.get(clazz);
                obj = clazz.getMethod("getSystemService", String.class).invoke(context, String.valueOf(obj));
                clazz = Class.forName("android.app.AppOpsManager");
                Field modeAllowed = clazz.getDeclaredField("MODE_ALLOWED");
                modeAllowed.setAccessible(true);
                Field opStr = clazz.getDeclaredField("OPSTR_SYSTEM_ALERT_WINDOW");
                opStr.setAccessible(true);
                Method checkOp = clazz.getMethod("checkOp", String.class, int.class, String.class);
                int result = (int) checkOp.invoke(obj, String.valueOf(opStr), Binder.getCallingUid(), context.getPackageName());
                return result == modeAllowed.getInt(clazz);
            } catch (Exception e) {
                return false;
            }
        } else {
            // Android 6.0 <= ? < Android 8.0 api 26
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                return Settings.canDrawOverlays(context);
            }
            // >= api 26 Android 8.0
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (appOpsManager != null) {
                int mode;
                // < api 29 Android 10.0
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName());
                } else {
                    mode = appOpsManager.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, Binder.getCallingUid(), context.getPackageName());
                }
                return Settings.canDrawOverlays(context) || mode == AppOpsManager.MODE_ALLOWED;
            }
            return false;
        }
    }

    /**
     * < request float permission >.
     *
     * @author bugliee
     * @create 2022/4/16
     * @param activity Activity
     */
    public static void requestFloatingPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, FLOAT_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * < getDeviceSize >.
     *
     * @author bugliee
     * @create 2022/4/16
     * @param context Context
     * @return {@link int[]}
     */
    public static int[] getDeviceSize(Context context) {
        int[] size = new int[2];
        try {
            int screenWidth, screenHeight;
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();
            int rotation = display.getRotation();
            Point point = new Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(point);
            } else {
                display.getSize(point);
            }
            size[0] = point.x;
            size[1] = point.y;
            //绝对宽高 不受横竖屏影响
//            screenWidth = point.x;
//            screenHeight = point.y;
//            size[0] = rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180 ? screenWidth : screenHeight;
//            size[1] = rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180 ? screenHeight : screenWidth;

        } catch (Exception e) {
            if (context.getResources() != null) {
                final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                size[0] = displayMetrics.widthPixels;
                size[1] = displayMetrics.heightPixels;
            }
        }
        return size;
    }

    /**
     * < getLayoutParams >.
     *
     * @author bugliee
     * @create 2022/4/27
     * @param context Context
     * @return {@link WindowManager.LayoutParams}
     */
    @SuppressLint("RtlHardcoded")
    public static WindowManager.LayoutParams getLayoutParams(Context context) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        if (!(context instanceof Activity)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                // < Android 4.4
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                // Android 4.4 <= sdk < Android 7.1
                // MIUI
                if ("Xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                } else {
                    layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                }
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                // Android 7.1 <= sdk < Android 8.0
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                // Android 8.0 <= sdk < Android 12
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                // >=Android 12
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                // Android 12 如果不能响应touch，设置alpha <= 0.8f
                // InputManager.getMaximumObscuringOpacityForTouch() 默认0.8f
                layoutParams.alpha = 0.8f;
            }
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return layoutParams;
    }

    /**
     * < getStatusBarHeight >.
     *
     * @author bugliee
     * @create 2022/4/27
     * @param activity Activity
     * @return {@link int}
     */
    public static int getStatusBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }


    /**
     * < queryAllPackages >.
     *
     * @author bugliee
     * @create 2022/6/21
     * @param activity Activity
     * @return {@link List}
     */
    public static List<ResolveInfo> queryAllPackages(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(KEY_TA_TOOL_ACTION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return activity.getPackageManager().queryIntentServices(intent, PackageManager.MATCH_ALL);
        } else {
            return activity.getPackageManager().queryIntentServices(intent, 0);
        }
    }

    /**
     * 判断当前应用是否在前台.
     * */
    public static boolean isForeground(Context context) {
        ActivityManager activityManager
                = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses
                = activityManager.getRunningAppProcesses();
        String processName = "";
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            processName = appProcess.processName;
            int p = processName.indexOf(":");
            if (p != -1) {
                processName = processName.substring(0, p);
            }
            if (processName.equals(context.getPackageName())) {
                return appProcess.importance
                        == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        || appProcess.importance
                        == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
            }
        }
        return false;
    }

    /**
     * < 通过APP包名启动APP >.
     *
     * @param mActivity   Activity
     * @param packageName APP包名
     * @author bugliee
     * @create 2022/6/28
     */
    public static void startAppWithPackageName(Activity mActivity, String packageName) {
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageName);
        List<ResolveInfo> infoList = mActivity.getPackageManager().queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = infoList.iterator().next();
        if (resolveinfo != null) {
            String pName = resolveinfo.activityInfo.packageName;
            String className = resolveinfo.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            intent.setComponent(new ComponentName(pName, className));
            mActivity.startActivity(intent);
        }
    }

    public boolean checkAppIDWithUrl(String appID, String url) {

        return true;
    }
}
