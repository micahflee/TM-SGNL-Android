package com.tm.authenticatorsdk.socgen;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/MessageEvent.class */
public class MessageEvent {
    public final String resultdescription;
    public final int resultcode;
    public String username;
    public String password;

    public MessageEvent(int code, String description) {
        this.username = null;
        this.password = null;
        this.resultcode = code;
        this.resultdescription = description;
    }

    public MessageEvent(int code, String description, String user, String pass) {
        this.username = null;
        this.password = null;
        this.resultcode = code;
        this.resultdescription = description;
        this.username = user;
        this.password = pass;
    }
}
