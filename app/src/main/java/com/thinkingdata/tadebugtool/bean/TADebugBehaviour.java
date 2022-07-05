/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.bean;

import org.json.JSONObject;
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

    @Column(defaultValue = "unknown")
    private String instanceIDStr;
    @Column(defaultValue = "unknown")
    private String timestamp;
    @Column(defaultValue = "unknown")
    private String appVersionCode;
    @Column(defaultValue = "unknown")
    private String presetProps;
    @Column(defaultValue = "unknown")
    private String appVersionName;
    @Column(defaultValue = "unknown")
    private String libVersion;
    @Column(defaultValue = "unknown")
    private String appName;
    @Column(defaultValue = "unknown")
    private String appIcon;
    @Column(defaultValue = "unknown")
    private String packageName;
    private boolean enableLog;
    private boolean enableBGStart;
    @Column(defaultValue = "unknown")
    private String disPresetProps;
    @Column(defaultValue = "unknown")
    private String crashConfig;
    @Column(defaultValue = "unknown")
    private String mainProcessName;
    private int retentionDays;
    private int databaseLimit;

    public TADebugBehaviour(JSONObject jsonObject) {
        setAppIcon(jsonObject.optString("appIcon"));
        setInstanceIDStr(jsonObject.optString("instanceIDStr"));
        setTimestamp(jsonObject.optString("timestamp"));
        setAppName(jsonObject.optString("appName"));
        setAppVersionName(jsonObject.optString("appVersionName"));
        setPresetProps(jsonObject.optString("presetProps"));
        setAppVersionCode(jsonObject.optString("appVersionCode"));
        setLibVersion(jsonObject.optString("libVersion"));
        setPackageName(jsonObject.optString("packageName"));
        setMainProcessName(jsonObject.optString("mainProcessName"));
        setCrashConfig(jsonObject.optString("crashConfig"));
        setDisPresetProps(jsonObject.optString("disPresetProps"));
        setDatabaseLimit(jsonObject.optInt("databaseLimit"));
        setRetentionDays(jsonObject.optInt("retentionDays"));
        setEnableLog(jsonObject.optBoolean("enableLog"));
        setEnableBGStart(jsonObject.optBoolean("enableBGStart"));
    }

    public String getMainProcessName() {
        return mainProcessName;
    }

    public void setMainProcessName(String mainProcessName) {
        this.mainProcessName = mainProcessName;
    }

    public String getPresetProps() {
        return presetProps;
    }

    public void setPresetProps(String presetProps) {
        this.presetProps = presetProps;
    }

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
