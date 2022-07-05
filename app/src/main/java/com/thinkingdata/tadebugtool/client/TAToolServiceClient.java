/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.client;

import static com.thinkingdata.tadebugtool.utils.EventUtil.defaultTag;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.thinkingdata.tadebugtool.bean.TAEvent;
import com.thinkingdata.tadebugtool.ui.widget.FloatLayout;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupLogView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                handleMsg(mLogStr);
            }
        }
    }

    private static Pattern pattern = Pattern.compile("\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s+\\d+\\s+\\d+" + "\\s[VIDWE]\\s" + defaultTag + "(SDK)*\\.*\\S*?:\\s");
    private static Pattern prefixPattern = Pattern.compile("\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s+\\d+\\s+\\d+" + "\\s[VIDWE]\\s" + defaultTag + "(SDK)*\\.*\\S*?:\\s\\{");
    private static Pattern suffixPattern = Pattern.compile("\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s+\\d+\\s+\\d+" + "\\s[VIDWE]\\s" + defaultTag + "(SDK)*\\.*\\S*?:\\s\\}");

    private boolean isLoop = false;
    private JSONObject cEventJson;
    private String lastMsg = "";

    private void handleMsg(String msg) {
        String content;
        if (!isLoop) {
            content = lastMsg + msg;
        } else {
            content = msg;
        }
        boolean findEnd = false;
        Matcher matcherPrefix = prefixPattern.matcher(content);
        if (matcherPrefix.find()) {
            //找到头
            String findStr = matcherPrefix.group();
            String subContent = content.substring(content.indexOf(findStr));
            //时间
            String timeStr = subContent.substring(0, 18);
            //尝试找结尾
            Matcher matcherSuffix = suffixPattern.matcher(subContent);
            if (matcherSuffix.find()) {
                String endStr = matcherSuffix.group();
                int pos = subContent.indexOf(endStr);
                //lastMsg
                lastMsg = subContent.substring(pos + endStr.length());
                //找到结尾 ，添加 "}"
                subContent = subContent.substring(0, pos);
                subContent += "}";
                findEnd = true;
            }
            //剔除上报成功事件
            if (findEnd && !subContent.contains("#flush_time")) {
                //替换 时间和tag
                subContent = subContent.replace(findStr.replace("{", ""), "");
                try {
                    cEventJson = new JSONObject(subContent);
                    //存数据库
                    TAEvent event = new TAEvent();
                    event.setEventID(cEventJson.optString("#uuid"));
                    event.setEventName(cEventJson.optString("#event_name"));
                    event.setEventType(cEventJson.optString("#type"));
                    event.setDistinctID(cEventJson.optString("#distinct_id"));
                    event.setAccountID(cEventJson.optString("#account_id"));
                    event.setProps(cEventJson.optString("#properties"));
                    event.setTimeStamp(timeStr);
                    event.setInstanceName(FloatLayout.mAppID);
                    event.save();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                isLoop = true;
                handleMsg(lastMsg);
            }
        } else {
            isLoop = false;
        }
    }

    private String mLogStr;

}
