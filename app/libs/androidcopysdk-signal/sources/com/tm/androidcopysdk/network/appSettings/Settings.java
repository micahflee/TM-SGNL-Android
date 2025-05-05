package com.tm.androidcopysdk.network.appSettings;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/appSettings/Settings.class */
public class Settings {
    private boolean wifiOnly;
    private String[] deviceTypes;
    private String[] optionalSettings;
    private int archiveSince;
    private Boolean disableAppText;
    private Boolean audioCalls;
    private Boolean videoCalls;
    private Integer signatureType;
    private String signature;

    public boolean isWifiOnly() {
        return this.wifiOnly;
    }

    public String[] getDeviceTypes() {
        return this.deviceTypes;
    }

    public String[] getOptionalSettings() {
        return this.optionalSettings;
    }

    public long getArchiveSince() {
        return this.archiveSince;
    }

    public void setWifiOnly(boolean wifiOnly) {
        this.wifiOnly = wifiOnly;
    }

    public void setDeviceTypes(String[] deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public void setOptionalSettings(String[] optionalSettings) {
        this.optionalSettings = optionalSettings;
    }

    public void setArchiveSince(int archiveSince) {
        this.archiveSince = archiveSince;
    }

    public Boolean getDisableAppText() {
        return this.disableAppText;
    }

    public void setDisableAppText(Boolean disableAppText) {
        this.disableAppText = disableAppText;
    }

    public Boolean getAudioCalls() {
        return this.audioCalls;
    }

    public void setAudioCalls(Boolean audioCalls) {
        this.audioCalls = audioCalls;
    }

    public Boolean getVideoCalls() {
        return this.videoCalls;
    }

    public void setVideoCalls(Boolean videoCalls) {
        this.videoCalls = videoCalls;
    }

    public Integer getSignatureType() {
        return this.signatureType;
    }

    public void setSignatureType(Integer signatureType) {
        this.signatureType = signatureType;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
