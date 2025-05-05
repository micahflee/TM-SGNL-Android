package com.tm.authenticatorsdk.socgen.RetrieveCredentials;

import com.google.gson.annotations.SerializedName;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/RetrieveCredentials/UserResponse.class */
public class UserResponse {
    MultiUserFields[] userFields;
    int resultCode;
    @SerializedName("class")
    String _class = "telemessage.web.services.Response";
    String resultDescription;

    public MultiUserFields[] getFields() {
        return this.userFields;
    }

    public void setFields(MultiUserFields[] fields) {
        this.userFields = fields;
    }

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
