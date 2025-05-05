package com.tm.authenticatorsdk.selfAuthenticator.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/model/UpdateUserResponse.class */
public class UpdateUserResponse {
    @SerializedName("class")
    @Expose
    private String _class;
    @SerializedName("contentSize")
    @Expose
    private Object contentSize;
    @SerializedName("resultCode")
    @Expose
    private Integer resultCode;
    @SerializedName("resultDescription")
    @Expose
    private String resultDescription;
    @SerializedName("userFields")
    @Expose
    private List<UserFields> userFields = null;

    public String getClass_() {
        return this._class;
    }

    public void setClass_(String _class) {
        this._class = _class;
    }

    public Object getContentSize() {
        return this.contentSize;
    }

    public void setContentSize(Object contentSize) {
        this.contentSize = contentSize;
    }

    public Integer getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(Integer resultCode) {
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
