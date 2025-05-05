package com.tm.androidcopysdk.network;

import com.google.gson.annotations.SerializedName;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/AuthententicationDetails.class */
public class AuthententicationDetails {
    @SerializedName("class")
    String class_name = "telemessage.web.services.AuthenticationDetails";
    String password;
    String username;

    public String getClass_name() {
        return this.class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
