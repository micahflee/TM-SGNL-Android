package com.tm.androidcopysdk.network;

import com.google.gson.annotations.SerializedName;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/Reciepients.class */
public class Reciepients {
    @SerializedName("class")
    String class_name = "telemessage.web.services.Recipient";
    String value;
    String addrestype;

    public String getClass_name() {
        return this.class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAddrestype() {
        return this.addrestype;
    }

    public void setAddrestype(String addrestype) {
        this.addrestype = addrestype;
    }
}
