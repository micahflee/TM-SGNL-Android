package com.tm.androidcopysdk.network;

import com.google.gson.annotations.SerializedName;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/Properities.class */
public class Properities {
    String name;
    String value;
    @SerializedName("class")
    String class_name = "telemessage.web.services.Property";

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getClass_name() {
        return this.class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }
}
