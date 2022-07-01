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
public class TAEvent extends LitePalSupport implements Serializable {

    @Column(defaultValue = "unknown", unique = true)
    private String eventID;

    @Column(defaultValue = "unknown", unique = true)
    private String instanceName;

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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Column(defaultValue = "unknown", unique = true)
    private String eventName;

    @Column(defaultValue = "unknown", unique = true)
    private String props;

    @Column(defaultValue = "unknown", unique = true)
    private String distinctID;

    @Column(defaultValue = "unknown", unique = true)
    private String accountID;

    @Column(defaultValue = "track", unique = true)
    private String eventType;

    @Column(defaultValue = "unknown", unique = true)
    private String timeStamp;
}
