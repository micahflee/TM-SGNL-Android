package com.tm.androidcopysdk;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/HeaderObj.class */
public class HeaderObj {
    private String key;
    private String value;
    private String messageId;

    public HeaderObj(String key, String value, String messageId) {
        this.key = key;
        this.value = value;
        this.messageId = messageId;
    }

    public HeaderObj() {
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
