package com.tm.authenticatorsdk.socgen.RetrieveOneTimePin;

import com.google.gson.annotations.SerializedName;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/RetrieveOneTimePin/Response.class */
public class Response {
    int resultCode;
    @SerializedName("class")
    String _class = "telemessage.web.services.Response";
    String resultDescription;

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

    public String get_class() {
        return this._class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }
}
