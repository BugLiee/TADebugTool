/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.bean;

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

    @Column(defaultValue = "unknown")
    private String timestamp;
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
    private boolean usable;
    @Column(defaultValue = "unknown")
    private String unUsableReason;
    @Column(defaultValue = "unknown")
    private String eventIDStr;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getEventIDStr() {
        return eventIDStr;
    }

    public void setEventIDStr(String eventIDStr) {
        this.eventIDStr = eventIDStr;
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
