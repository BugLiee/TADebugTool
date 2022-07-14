/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import static com.thinkingdata.tadebugtool.common.TAConstants.KEY_TA_TOOL_ACTION;
import static com.thinkingdata.tadebugtool.common.TAConstants.VIEW_RADIUS;
import static com.thinkingdata.tadebugtool.utils.TAUtil.VERSION;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;

import cn.thinkingdata.android.aidl.ITAToolServer;

/**
 * < PopupWindow >.
 *
 * @author bugliee
 * @create 2022/6/14
 * @since 1.0.0
 */
public class PopupLogView  extends CardView {

    private static volatile PopupLogView instance = null;

    private int screenWidth;
    private int screenHeight;
    private boolean enableTransLog = true;
    private boolean showDefineTagView = false;
    private EditText defineTagET;
    private TextView defineTagPositiveTV;
    private TextView defineTagNegativeTV;
    private LinearLayout defineTagLL;
    private String logLevel = ":V";
    private String cmdOrder = "logcat -s *:V";
    private String logTag = "ALL";
    private final String[] taTags = new String[]{
            "ThinkingAnalytics"
            ,"ThinkingAnalyticsSDK"
            ,"ThinkingAnalytics.SystemInformation"
            ,"ThinkingAnalytics.TAEncryptUtils"
            ,"ThinkingAnalytics.DataHandle"
            ,"ThinkingAnalytics.DatabaseAdapter"
            ,"ThinkingAnalytics.PathFinder"
            ,"ThinkingAnalytics.TDConfig"
            ,"ThinkingAnalytics.TDPresetProperties"
            ,"ThinkingAnalytics.ThinkingDataActivityLifecycleCallbacks"
            ,"ThinkingAnalytics.ThinkingDataRuntimeBridge"
            ,"ThinkingAnalytics.TAPushLifecycle"
            ,"ThinkingAnalytics.process"
            ,"ThinkingAnalytics.TAEncryptUtils"
            ,"ThinkingAnalytics.ThinkingDataEncrypt"
            ,"ThinkingAnalytics.SyncData"
            ,"ThinkingAnalytics.HttpService"
            ,"ThinkingAnalytics.PropertyUtils"
            ,"ThinkingAnalytics.TAReflectUtils"
            ,"ThinkingAnalytics.NTP"
            ,"ThinkingAnalytics.TDNTPClient"
            ,"ThinkingAnalytics.ExceptionHandler"
            ,"ThinkingAnalytics.TDUniqueEvent"
            ,"ThinkingAnalytics.TDWebAppInterface"
    };

    private TextView logTagBtn;
    private TextView pauseLogBtn;
    private final Context mContext;
    private String packageName = "";

    private PopupLogView(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void init(String packageName) {
        this.packageName = packageName;
        LayoutInflater.from(mContext).inflate(R.layout.popup_log, this, true);
        TextView startLinkBtn = this.findViewById(R.id.start_link);
        logTagBtn = this.findViewById(R.id.log_tag);
        pauseLogBtn = this.findViewById(R.id.pause_recover);
        TextView clearLogBtn = this.findViewById(R.id.clear_log);
        TextView exportLogBtn = this.findViewById(R.id.export_log);

        Spinner logLevelSpinner = this.findViewById(R.id.log_level);
        String[] levelItems = mContext.getResources().getStringArray(R.array.log_level);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, R.layout.item_log_level, levelItems);
        adapter.setDropDownViewResource(R.layout.item_log_level);
        logLevelSpinner.setAdapter(adapter);
        logLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    cmdOrder = cmdOrder.replace(logLevel, ":V");
                    logLevel = ":V";
                } else if (position == 1) {
                    cmdOrder = cmdOrder.replace(logLevel, ":D");
                    logLevel = ":D";
                } else if (position == 2) {
                    cmdOrder = cmdOrder.replace(logLevel, ":I");
                    logLevel = ":I";
                } else if (position == 3) {
                    cmdOrder = cmdOrder.replace(logLevel, ":W");
                    logLevel = ":W";
                } else if (position == 4) {
                    cmdOrder = cmdOrder.replace(logLevel, ":E");
                    logLevel = ":E";
                } else if (position == 5) {
                    cmdOrder = cmdOrder.replace(logLevel, ":F");
                    logLevel = ":F";
                } else if (position == 6) {
                    cmdOrder = cmdOrder.replace(logLevel, ":S");
                    logLevel = ":S";
                }
                if (mTAToolServer != null) {
                    try {
                        logTV.post(() -> logTV.setText(""));
                        //             logcat -s *:V
                        mTAToolServer.trackLog(cmdOrder);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    SnackbarUtil.showSnackBarMid("please link first. ", instance);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        logTV = this.findViewById(R.id.log_tv);
        logTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        pauseLogBtn.setOnClickListener(v -> {
            if (mTAToolServer != null) {
                try {
                    enableTransLog = !enableTransLog;
                    mTAToolServer.enableTransLog(enableTransLog);

                    if (enableTransLog) {
                        pauseLogBtn.post(() -> pauseLogBtn.setText("暂停日志"));
                    } else {
                        pauseLogBtn.post(() -> pauseLogBtn.setText("接收日志"));
                    }

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });


        clearLogBtn.setOnClickListener(v -> logTV.post(new Runnable() {
            @Override
            public void run() {
                logTV.setText("");
            }
        }));
        startLinkBtn.setOnClickListener(v -> {
            if (mTAToolServer == null) {
                SnackbarUtil.showSnackBarMid("start link app , please wait 1-2 second. ", instance);
                Intent intent = new Intent();
                intent.setAction(KEY_TA_TOOL_ACTION);
                intent.setPackage(packageName);
                mContext.getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            } else {
                SnackbarUtil.showSnackBarMid("link succeeded. ", instance);
            }
        });
        logTagBtn.setOnClickListener(v -> {
            if (mTAToolServer != null) {
                if (logTag.equals("ALL")) {
                    logTag = "ONLY_TA";
                    //only ta log
                    StringBuilder mOrder = new StringBuilder("logcat -s");
                    for (String tag : taTags) {
                        String addStr = " " + tag + logLevel;
                        mOrder.append(addStr);
                    }

                    if ( !cmdOrder.equals(mOrder.toString())) {
                        cmdOrder = mOrder.toString();
                        try {
                            mTAToolServer.trackLog(cmdOrder);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    logTagBtn.post(() -> {
                        logTagBtn.setText("无TAG");
                        logTV.setText("");
                    });
                } else {
                    logTag = "ALL";
                    //all log
                    String mOrder = "logcat -s *" + logLevel;
                    if ( !cmdOrder.equals(mOrder)) {
                        cmdOrder = mOrder;
                        try {
                            mTAToolServer.trackLog(cmdOrder);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    logTagBtn.post(() -> {
                        logTagBtn.setText("仅TA日志");
                        logTV.setText("");
                    });
                }
            } else {
                SnackbarUtil.showSnackBarMid("please link first. ", instance);
            }

        });
        logTagBtn.setOnLongClickListener(v -> {
            //长按自定义TAG
            if (!showDefineTagView) {
                showDefineTagView = true;
                defineTagLL = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.view_define_tag, null);
                defineTagET = defineTagLL.findViewById(R.id.define_tag_et);
                defineTagPositiveTV = defineTagLL.findViewById(R.id.define_tag_positive_tv);
                defineTagNegativeTV = defineTagLL.findViewById(R.id.define_tag_negative_tv);
                defineTagNegativeTV.setOnClickListener(v1 -> {
                    instance.removeView(defineTagLL);
                    showDefineTagView = false;
                    defineTagPositiveTV = null;
                    defineTagNegativeTV = null;
                    defineTagET = null;
                    defineTagLL = null;
                });
                defineTagPositiveTV.setOnClickListener(v1 -> {
                    String tagStr = defineTagET.getText().toString();
                    if (!tagStr.isEmpty()) {
                        logTagBtn.post(() -> {
                            instance.removeView(defineTagLL);
                            showDefineTagView = false;
                            defineTagPositiveTV = null;
                            defineTagNegativeTV = null;
                            defineTagET = null;
                            defineTagLL = null;
                            logTag = "defineTag";
                            logTagBtn.setText(logTag);
                            logTV.setText("");
                            String[] tags = tagStr.split(",");
                            StringBuilder mOrder = new StringBuilder("logcat -s");
                            for (String tag : tags) {
                                String addStr = " " + tag + logLevel;
                                mOrder.append(addStr);
                            }

                            if ( !cmdOrder.equals(mOrder.toString())) {
                                cmdOrder = mOrder.toString();
                                try {
                                    if (mTAToolServer != null) {
                                        mTAToolServer.trackLog(cmdOrder);
                                    }
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                WindowManager.LayoutParams p = TAUtil.getLayoutParams(mContext.getApplicationContext());
                p.x = 0;
                p.y = screenHeight / 2;
                p.width = screenWidth;
                p.height = VIEW_RADIUS * 2;
                p.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                instance.addView(defineTagLL, p);
                defineTagET.setSelected(true);
            }
            return true;
        });
        exportLogBtn.setOnClickListener(v -> {
            if (logTV != null ) {
                String text = logTV.getText().toString();
                if (text.isEmpty()) {
                    SnackbarUtil.showSnackBarMid("log is empty!", instance);
                } else {
                    exportCurrentLog(logTV.getText().toString());
                }
            } else {
                SnackbarUtil.showSnackBarMid("logTV is null!", instance);
            }
        });
        instance = this;

        int[] screenSize = TAUtil.getDeviceSize(mContext);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
    }

    private void exportCurrentLog(String log) {
        final String filePath = String.format(Locale.US, "%s/%s_%d_%s_%s_%s",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "log", new Date().getTime(),
                VERSION, packageName, ".txt");
        try {
            new Thread() {
                @Override
                public void run() {
                    FileOutputStream os = null;
                    try {
                        File logFile = new File(filePath);
                        if (!logFile.exists()) {
                            logFile.createNewFile();
                        }
                        os = new FileOutputStream(logFile);
                        os.write(log.getBytes("UTF-8"));
                        os.flush();
                        SnackbarUtil.showSnackBarMid("export succeed , path : " + filePath, instance);
                    } catch (Exception e) {
                        e.printStackTrace();
                        SnackbarUtil.showSnackBarMid("export failed, please check log !", instance);
                    } finally {
                        if (os != null) {
                            try {
                                os.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ITAToolServer mTAToolServer;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTAToolServer = ITAToolServer.Stub.asInterface(service);
            try {
                mTAToolServer.enableLog(true);
                mTAToolServer.trackLog(cmdOrder);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTAToolServer = null;
        }
    };

    private TextView logTV;

    public void unbindThis() {
        if (mTAToolServer != null) {
            try {
                mTAToolServer.unbindThis();
                mTAToolServer = null;
                mContext.getApplicationContext().unbindService(serviceConnection);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }

    public String getLogTVText() {
        String text = "";
        if (logTV != null) {
            text = logTV.getText().toString().trim();
        }
        return text;
    }

    public void setLogTVText(String str) {
        if (logTV != null) {
            logTV.setText(str);
        }
    }

    public void scrollLogTVToBottom() {
        logTV.post(() -> {
            int scrollAmount = logTV.getLayout().getLineTop(logTV.getLineCount()) - logTV.getHeight();
            if (scrollAmount > 0)
                logTV.scrollTo(0, scrollAmount);
            else
                logTV.scrollTo(0, 0);
        });
    }

    public static PopupLogView getInstance(Context context) {
        if (instance == null) {
            synchronized (PopupLogView.class){
                if (instance == null) {
                    instance = new PopupLogView(context);
                }
            }
        }
        return instance;
    }

}
