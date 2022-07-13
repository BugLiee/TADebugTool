/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.bean;

import org.json.JSONObject;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * < Instance >.
 *
 * @author bugliee
 * @create 2022/7/1
 * @since 1.0.0
 */
public class TAInstance extends LitePalSupport implements Serializable {

    public TAInstance(JSONObject instanceJSon) {
        setTime(instanceJSon.optString("time"));
        setInstanceID(instanceJSon.optString("instanceID"));
        setDistinctID(instanceJSon.optString("distinctID"));
        setAccountID(instanceJSon.optString("accountID"));
        setName(instanceJSon.optString("name"));
        setUrl(instanceJSon.optString("url"));
        setAppVersionCode(instanceJSon.optString("appVersionCode"));
        setAppVersionName(instanceJSon.optString("appVersionName"));
        setPresetProps(instanceJSon.optString("presetProps"));
        setLibVersion(instanceJSon.optString("libVersion"));
        setAppName(instanceJSon.optString("appName"));
        setAppIcon(instanceJSon.optString("appIcon"));
        setPackageName(instanceJSon.optString("packageName"));
        setUsable(instanceJSon.optBoolean("usable"));
        setUnUsableReason(instanceJSon.optString("unUsableReason"));
        setMultiProcess(instanceJSon.optBoolean("isMultiProcess"));
        setTrackState(instanceJSon.optString("trackState"));
        setSdkMode(instanceJSon.optString("sdkMode"));
        setAutoTrackList(instanceJSon.optString("autoTrackList"));
        setEnabledEncrypt(instanceJSon.optBoolean("enabledEncrypt"));
        setEncryptPublicKey(instanceJSon.optString("encryptPublicKey"));
        setSymmetricEncryption(instanceJSon.optString("symmetricEncryption"));
        setAsymmetricEncryption(instanceJSon.optString("asymmetricEncryption"));
        setEncryptVersion(instanceJSon.optString("encryptVersion"));
        setSuperProps(instanceJSon.optString("superProps"));
        setFlushInterval(instanceJSon.optInt("flushInterval"));
        setFlushBulkSize(instanceJSon.optInt("flushBulkSize"));
        setEnableLog(instanceJSon.optBoolean("enableLog"));
        setEnableBGStart(instanceJSon.optBoolean("enableBGStart"));
        setSetCalibrateTime(instanceJSon.optBoolean("setCalibrateTime"));
        setDisPresetProps(instanceJSon.optString("disPresetProps"));
        setCrashConfig(instanceJSon.optString("crashConfig"));
        setMainProcessName(instanceJSon.optString("mainProcessName"));
        setRetentionDays(instanceJSon.optInt("retentionDays"));
        setDatabaseLimit(instanceJSon.optInt("databaseLimit"));
        setTimeZone(instanceJSon.optString("timeZone"));
    }

    @Column(defaultValue = "GMT+08:00")
    private String timeZone;

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    @Column(defaultValue = "unknown")
    private String time;
    @Column(defaultValue = "unknown")
    private String instanceID;
    @Column(defaultValue = "unknown")
    private String distinctID;
    @Column(defaultValue = "unknown")
    private String accountID;
    @Column(defaultValue = "unknown")
    private String name;
    @Column(defaultValue = "unknown")
    private String url;
    @Column(defaultValue = "unknown")
    private String appVersionCode;
    @Column(defaultValue = "unknown")
    private String appVersionName;
    @Column(defaultValue = "unknown")
    private String presetProps;
    @Column(defaultValue = "unknown")
    private String libVersion;
    @Column(defaultValue = "unknown")
    private String appName;
    @Column(defaultValue = "unknown")
    private String appIcon;
    @Column(defaultValue = "unknown")
    private String packageName;
    private boolean usable;
    @Column(defaultValue = "unknown")
    private String unUsableReason;
    private boolean isMultiProcess;
    @Column(defaultValue = "NORMAL")
    private String trackState;
    @Column(defaultValue = "NORMAL")
    private String sdkMode;
    @Column(defaultValue = "unknown")
    private String autoTrackList;
    private boolean enabledEncrypt;
    @Column(defaultValue = "unknown")
    private String encryptPublicKey;
    @Column(defaultValue = "unknown")
    private String symmetricEncryption;
    @Column(defaultValue = "unknown")
    private String asymmetricEncryption;
    @Column(defaultValue = "unknown")
    private String encryptVersion;
    @Column(defaultValue = "unknown")
    private String superProps;
    private int flushInterval;
    private int flushBulkSize;
    private boolean enableLog;
    private boolean enableBGStart;
    private boolean setCalibrateTime;
    @Column(defaultValue = "unknown")
    private String disPresetProps;
    @Column(defaultValue = "unknown")
    private String crashConfig;
    @Column(defaultValue = "unknown")
    private String mainProcessName;
    private int retentionDays;
    private int databaseLimit;

    public boolean isSetCalibrateTime() {
        return setCalibrateTime;
    }

    public void setSetCalibrateTime(boolean setCalibrateTime) {
        this.setCalibrateTime = setCalibrateTime;
    }

    public String getAppVersionCode() {
        return appVersionCode;
    }

    public void setAppVersionCode(String appVersionCode) {
        this.appVersionCode = appVersionCode;
    }

    public String getPresetProps() {
        return presetProps;
    }

    public void setPresetProps(String presetProps) {
        this.presetProps = presetProps;
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

    public String getMainProcessName() {
        return mainProcessName;
    }

    public void setMainProcessName(String mainProcessName) {
        this.mainProcessName = mainProcessName;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInstanceID() {
        return instanceID;
    }

    public void setInstanceID(String instanceID) {
        this.instanceID = instanceID;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isUsable() {
        return usable;
    }

    public void setUsable(boolean usable) {
        this.usable = usable;
    }

    public String getUnUsableReason() {
        return unUsableReason;
    }

    public void setUnUsableReason(String unUsableReason) {
        this.unUsableReason = unUsableReason;
    }

    public boolean isMultiProcess() {
        return isMultiProcess;
    }

    public void setMultiProcess(boolean multiProcess) {
        isMultiProcess = multiProcess;
    }

    public String getTrackState() {
        return trackState;
    }

    public void setTrackState(String trackState) {
        this.trackState = trackState;
    }

    public String getSdkMode() {
        return sdkMode;
    }

    public void setSdkMode(String sdkMode) {
        this.sdkMode = sdkMode;
    }

    public String getAutoTrackList() {
        return autoTrackList;
    }

    public void setAutoTrackList(String autoTrackList) {
        this.autoTrackList = autoTrackList;
    }

    public boolean isEnabledEncrypt() {
        return enabledEncrypt;
    }

    public void setEnabledEncrypt(boolean enabledEncrypt) {
        this.enabledEncrypt = enabledEncrypt;
    }

    public String getEncryptPublicKey() {
        return encryptPublicKey;
    }

    public void setEncryptPublicKey(String encryptPublicKey) {
        this.encryptPublicKey = encryptPublicKey;
    }

    public String getSymmetricEncryption() {
        return symmetricEncryption;
    }

    public void setSymmetricEncryption(String symmetricEncryption) {
        this.symmetricEncryption = symmetricEncryption;
    }

    public String getAsymmetricEncryption() {
        return asymmetricEncryption;
    }

    public void setAsymmetricEncryption(String asymmetricEncryption) {
        this.asymmetricEncryption = asymmetricEncryption;
    }

    public String getEncryptVersion() {
        return encryptVersion;
    }

    public void setEncryptVersion(String encryptVersion) {
        this.encryptVersion = encryptVersion;
    }

    public String getSuperProps() {
        return superProps;
    }

    public void setSuperProps(String superProps) {
        this.superProps = superProps;
    }

    public int getFlushInterval() {
        return flushInterval;
    }

    public void setFlushInterval(int flushInterval) {
        this.flushInterval = flushInterval;
    }

    public int getFlushBulkSize() {
        return flushBulkSize;
    }

    public void setFlushBulkSize(int flushBulkSize) {
        this.flushBulkSize = flushBulkSize;
    }

}
