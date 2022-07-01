/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * < Behaviour >.
 *
 * @author bugliee
 * @create 2022/7/1
 * @since 1.0.0
 */
public class TADebugBehaviour extends LitePalSupport implements Serializable {

    @Column(defaultValue = "unknown", unique = true)
    private String instanceIDStr;

    @Column(defaultValue = "unknown", unique = true)
    private String timestamp;

    @Column(defaultValue = "unknown", unique = true)
    private String appVersionCode;

    public String getPresetProps() {
        return presetProps;
    }

    public void setPresetProps(String presetProps) {
        this.presetProps = presetProps;
    }

    @Column(defaultValue = "unknown", unique = true)
    private String presetProps;

    @Column(defaultValue = "unknown", unique = true)
    private String appVersionName;

    @Column(defaultValue = "Android", unique = true)
    private String os;

    @Column(defaultValue = "unknown", unique = true)
    private String osVersion;

    @Column(defaultValue = "unknown", unique = true)
    private String deviceID;

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getAppVersionName() {
        return appVersionName;
    }

    public void setAppVersionName(String appVersionName) {
        this.appVersionName = appVersionName;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getLibVersion() {
        return libVersion;
    }

    public void setLibVersion(String libVersion) {
        this.libVersion = libVersion;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isEnableLog() {
        return enableLog;
    }

    public void setEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
    }

    public boolean isEnableBGStart() {
        return enableBGStart;
    }

    public void setEnableBGStart(boolean enableBGStart) {
        this.enableBGStart = enableBGStart;
    }

    public String getDisPresetProps() {
        return disPresetProps;
    }

    public void setDisPresetProps(String disPresetProps) {
        this.disPresetProps = disPresetProps;
    }

    public String getCrashConfig() {
        return crashConfig;
    }

    public void setCrashConfig(String crashConfig) {
        this.crashConfig = crashConfig;
    }

    public int getRetentionDays() {
        return retentionDays;
    }

    public void setRetentionDays(int retentionDays) {
        this.retentionDays = retentionDays;
    }

    public int getDatabaseLimit() {
        return databaseLimit;
    }

    public void setDatabaseLimit(int databaseLimit) {
        this.databaseLimit = databaseLimit;
    }

    @Column(defaultValue = "unknown", unique = true)
    private String libVersion;

    @Column(defaultValue = "unknown", unique = true)
    private String appName;

    @Column(defaultValue = "unknown", unique = true)
    private String appIcon;

    @Column(defaultValue = "unknown", unique = true)
    private String packageName;

    @Column(unique = true)
    private boolean enableLog;

    @Column(unique = true)
    private boolean enableBGStart;

    @Column(defaultValue = "unknown", unique = true)
    private String disPresetProps;

    @Column(defaultValue = "unknown", unique = true)
    private String crashConfig;

    @Column(unique = true)
    private int retentionDays;

    @Column(unique = true)
    private int databaseLimit;

    public String getInstanceIDStr() {
        return instanceIDStr;
    }

    public void setInstanceIDStr(String instanceIDStr) {
        this.instanceIDStr = instanceIDStr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
