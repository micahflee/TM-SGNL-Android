package com.tm.authenticatorsdk.socgen.signup;

import com.google.gson.annotations.SerializedName;
import java.util.List;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/signup/SignupResponse.class */
public class SignupResponse {
    @SerializedName("class")
    String _class;
    @SerializedName("contentSize")
    String contentSize;
    @SerializedName("resultCode")
    int resultCode;
    @SerializedName("resultDescription")
    String resultDescription;
    @SerializedName("userFields")
    List<UserFields> userFields;

    public String get_class() {
        return this._class;
    }

    public void set_class(String _class) {
        this._class = _class;
    }

    public String getContentSize() {
        return this.contentSize;
    }

    public void setContentSize(String contentSize) {
        this.contentSize = contentSize;
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

    public List<UserFields> getUserFields() {
        return this.userFields;
    }

    public void setUserFields(List<UserFields> userFields) {
        this.userFields = userFields;
    }
}
