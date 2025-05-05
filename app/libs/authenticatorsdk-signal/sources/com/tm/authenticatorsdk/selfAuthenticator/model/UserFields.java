package com.tm.authenticatorsdk.selfAuthenticator.model;

import com.google.gson.annotations.SerializedName;
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticatorKt;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/model/UserFields.class */
public class UserFields {
    private String[] values;
    @SerializedName(MDMAuthenticatorKt.NAME)
    private String name;
    @SerializedName("value")
    private String value;
    private long date = System.currentTimeMillis();
    @SerializedName("class")
    private String _class = "telemessage.web.services.UserField";

    public String[] getValues() {
        return this.values;
    }

    public void setValues(String values) {
        this.values = new String[1];
        this.values[0] = values;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String _getClass() {
        return this._class;
    }

    public void setClass(String _class) {
        this._class = _class;
    }

    public long getDate() {
        return this.date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String toString() {
        return "ClassPojo [values = " + this.values + ", name = " + this.name + ", class = " + this._class + ", date = " + this.date + "]";
    }
}
