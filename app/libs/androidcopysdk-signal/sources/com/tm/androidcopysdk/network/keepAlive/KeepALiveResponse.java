package com.tm.androidcopysdk.network.keepAlive;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/keepAlive/KeepALiveResponse.class */
public class KeepALiveResponse {
    @SerializedName("resultCode")
    @Expose
    private int resultCode;
    @SerializedName("resultDescription")
    @Expose
    private String resultDescription;
    @SerializedName("contentSize")
    @Expose
    private String contentSize;

    public int getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDescription() {
        return this.resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getContentSize() {
        return this.contentSize;
    }

    public void setContentSize(String contentSize) {
        this.contentSize = contentSize;
    }
}
