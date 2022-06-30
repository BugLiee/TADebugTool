/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.client;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thinkingdata.tadebugtool.ui.widget.popup.PopupLogView;

import cn.thinkingdata.android.aidl.ITAToolClient;

public class TAToolServiceClient extends Service {

    PopupLogView popupLogView = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        popupLogView = PopupLogView.getInstance(getApplicationContext());
        return iBinder;
    }

    IBinder iBinder = new ITAToolClient.Stub() {
        @Override
        public void sendLog(String logStr) throws RemoteException {
            mLogStr = logStr;
            myHandler.sendEmptyMessage(0);
        }
    };

    private final MyHandler myHandler = new MyHandler();

    class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String strLogs = popupLogView.getLogTVText();
                if (strLogs.isEmpty()) {
                    strLogs = mLogStr;
                } else {
                    strLogs += "\r\n" + mLogStr;
                }
                popupLogView.setLogTVText(strLogs);
                //log View滚动到底部
                popupLogView.scrollLogTVToBottom();

            }
        }
    }

    private String mLogStr;

}
