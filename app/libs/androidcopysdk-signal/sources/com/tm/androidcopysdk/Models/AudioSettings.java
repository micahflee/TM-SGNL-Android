package com.tm.androidcopysdk.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/Models/AudioSettings.class */
public class AudioSettings {
    @SerializedName("api")
    @Expose
    private String api;
    @SerializedName("audioSourceInc")
    @Expose
    private String audioSourceInc;
    @SerializedName("audioSourceOut")
    @Expose
    private String audioSourceOut;
    @SerializedName("formatFile")
    @Expose
    private String formatFile;
    @SerializedName("gid")
    @Expose
    private Integer gid;
    @SerializedName("manufacturer")
    @Expose
    private String manufacturer;
    @SerializedName("model")
    @Expose
    private String model;
    @SerializedName("pauseBeforeRecInc")
    @Expose
    private String pauseBeforeRecInc;
    @SerializedName("pauseBeforeRecOut")
    @Expose
    private String pauseBeforeRecOut;
    @SerializedName("qualityAac")
    @Expose
    private String qualityAac;
    @SerializedName("qualityWav")
    @Expose
    private String qualityWav;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("ndk_in")
    @Expose
    private String ndk_in;
    @SerializedName("ndk_out")
    @Expose
    private String ndk_out;
    @SerializedName("path_to_recorded_files")
    @Expose
    private String path_to_recorded_files;
    @SerializedName("isSupported")
    @Expose
    private boolean isSupported;

    public String getApi() {
        return this.api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public String getAudioSourceInc() {
        return this.audioSourceInc;
    }

    public void setAudioSourceInc(String audioSourceInc) {
        this.audioSourceInc = audioSourceInc;
    }

    public String getAudioSourceOut() {
        return this.audioSourceOut;
    }

    public void setAudioSourceOut(String audioSourceOut) {
        this.audioSourceOut = audioSourceOut;
    }

    public String getFormatFile() {
        return this.formatFile;
    }

    public void setFormatFile(String formatFile) {
        this.formatFile = formatFile;
    }

    public Integer getGid() {
        return this.gid;
    }

    public void setGid(Integer gid) {
        this.gid = gid;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return this.model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getPauseBeforeRecInc() {
        return this.pauseBeforeRecInc;
    }

    public void setPauseBeforeRecInc(String pauseBeforeRecInc) {
        this.pauseBeforeRecInc = pauseBeforeRecInc;
    }

    public String getPauseBeforeRecOut() {
        return this.pauseBeforeRecOut;
    }

    public void setPauseBeforeRecOut(String pauseBeforeRecOut) {
        this.pauseBeforeRecOut = pauseBeforeRecOut;
    }

    public String getQualityAac() {
        return this.qualityAac;
    }

    public void setQualityAac(String qualityAac) {
        this.qualityAac = qualityAac;
    }

    public String getQualityWav() {
        return this.qualityWav;
    }

    public void setQualityWav(String qualityWav) {
        this.qualityWav = qualityWav;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getndk_in() {
        return this.ndk_in;
    }

    public void setndk_in(String ndk_in) {
        this.ndk_in = ndk_in;
    }

    public String getndk_out() {
        return this.ndk_out;
    }

    public void setndk_out(String ndk_out) {
        this.ndk_out = ndk_out;
    }

    public String toString() {
        return this.manufacturer + " " + this.model + " d_in:" + this.pauseBeforeRecInc + " d_out:" + this.pauseBeforeRecOut + " ndk:" + this.ndk_in;
    }

    public String getPath_to_recorded_files() {
        return this.path_to_recorded_files;
    }

    public void setPath_to_recorded_files(String path_to_recorded_files) {
        this.path_to_recorded_files = path_to_recorded_files;
    }

    public boolean getIsSupported() {
        return this.isSupported;
    }

    public void setIsSupported(boolean supported) {
        this.isSupported = supported;
    }
}
