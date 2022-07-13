/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.bean;

import android.icu.util.Calendar;
import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

/**
 * < Event >.
 *
 * @author bugliee
 * @create 2022/7/1
 * @since 1.0.0
 */
public class TAEvent extends LitePalSupport implements Serializable {

    public TAEvent(JSONObject eventJson) {
        setInstanceName(eventJson.optString("instanceName"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setTime(Calendar.getInstance().get(java.util.Calendar.YEAR) + " " + eventJson.optString("time"));
        } else {
            setTime((new Date().getYear() + 1900) + " " + eventJson.optString("time"));
        }

        setEventID(eventJson.optString("#uuid"));
        setEventName(eventJson.optString("#event_name"));
        setEventType(eventJson.optString("#type"));
        setDistinctID(eventJson.optString("#distinct_id"));
        setAccountID(eventJson.optString("#account_id"));
        setBFEventID(eventJson.optString("#event_id"));
        setFirstCheckID(eventJson.optString("#first_check_id"));
        try {
            JSONObject props = new JSONObject(eventJson.optString("properties"));
            JSONObject presetProps = new JSONObject();
            JSONObject userProps = new JSONObject();
            Iterator<String> iterator = props.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (key.startsWith("#")) {
                    presetProps.put(key, props.opt(key));
                } else {
                    userProps.put(key, props.opt(key));
                }
            }
            //预置属性
            setPresetProps(presetProps.toString());
            //传入 或 自定义属性
            setProps(userProps.toString());
        } catch (JSONException e) {
//            e.printStackTrace();
        }

    }

    public String getBFEventID() {
        return BFEventID;
    }

    public void setBFEventID(String BFEventID) {
        this.BFEventID = BFEventID;
    }

    @Column(defaultValue = "unknown")
    private String BFEventID;

    public String getFirstCheckID() {
        return firstCheckID;
    }

    public void setFirstCheckID(String firstCheckID) {
        this.firstCheckID = firstCheckID;
    }

    @Column(defaultValue = "unknown")
    private String firstCheckID;

    @Column(defaultValue = "unknown")
    private String eventID;
    @Column(defaultValue = "unknown")
    private String instanceName;
    @Column(defaultValue = "unknown")
    private String eventName;
    @Column(defaultValue = "unknown")
    private String props;

    public String getPresetProps() {
        return presetProps;
    }

    public void setPresetProps(String presetProps) {
        this.presetProps = presetProps;
    }

    @Column(defaultValue = "unknown")
    private String presetProps;
    @Column(defaultValue = "unknown")
    private String distinctID;
    @Column(defaultValue = "unknown")
    private String accountID;
    @Column(defaultValue = "track")
    private String eventType;
    @Column(defaultValue = "unknown")
    private String time;

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getProps() {
        return props;
    }

    public void setProps(String props) {
        this.props = props;
    }

    public String getDistinctID() {
        return distinctID;
    }

    public void setDistinctID(String distinctID) {
        this.distinctID = distinctID;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
