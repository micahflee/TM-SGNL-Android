package com.tm.androidcopysdk.network;

import com.google.gson.annotations.SerializedName;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/Message.class */
public class Message {
    @SerializedName("class")
    String class_name = "telemessage.web.services.Message";
    String textMessage;
    String subject;

    public String getClass_name() {
        return this.class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getTextMessage() {
        return this.textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
