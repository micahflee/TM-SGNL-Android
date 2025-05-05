package com.tm.androidcopysdk.network;

import com.google.gson.annotations.SerializedName;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/FileMessages.class */
public class FileMessages {
    @SerializedName("class")
    String class_name = "telemessage.web.services.FileMessage";
    String fileName;
    String mimeType;
    String value;

    public String getClass_name() {
        return this.class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
