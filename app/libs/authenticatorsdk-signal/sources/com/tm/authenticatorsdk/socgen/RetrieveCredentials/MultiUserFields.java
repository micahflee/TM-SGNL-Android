package com.tm.authenticatorsdk.socgen.RetrieveCredentials;

import com.google.gson.annotations.SerializedName;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/RetrieveCredentials/MultiUserFields.class */
public class MultiUserFields {
    private String[] values;
    private String name;
    String options;
    String value;
    private long date = System.currentTimeMillis();
    @SerializedName("class")
    private String _class = "telemessage.web.services.UserField";

    public String[] getValues() {
        return this.values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }

    public String get_class() {
        return this._class;
    }

    public void set_class(String _class) {
        this._class = _class;
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

    public String getOptions() {
        return this.options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
