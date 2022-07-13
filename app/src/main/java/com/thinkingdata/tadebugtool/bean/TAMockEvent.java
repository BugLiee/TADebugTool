/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * < Event >.
 *
 * @author bugliee
 * @create 2022/7/1
 * @since 1.0.0
 */
public class TAMockEvent extends LitePalSupport implements Serializable {

    @Column(defaultValue = "unknown")
    private String instanceName;
    @Column(defaultValue = "unknown")
    private String serverUrl;
    @Column(defaultValue = "unknown")
    private String eventName;
    @Column(defaultValue = "unknown")
    private String props;
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
    @Column(defaultValue = "unknown")
    private String BFEventID;
    @Column(defaultValue = "unknown")
    private String firstCheckID;

    public String getPresetProps() {
        return presetProps;
    }

    public void setPresetProps(String presetProps) {
        this.presetProps = presetProps;
    }

    public String getBFEventID() {
        return BFEventID;
    }

    public void setBFEventID(String BFEventID) {
        this.BFEventID = BFEventID;
    }

    public String getFirstCheckID() {
        return firstCheckID;
    }

    public void setFirstCheckID(String firstCheckID) {
        this.firstCheckID = firstCheckID;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
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
