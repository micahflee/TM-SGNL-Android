package com.tm.authenticatorsdk.socgen;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/ErrorEvent.class */
public class ErrorEvent {
    private String msg;
    private String title;

    public ErrorEvent(String title, String msg) {
        this.title = title;
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getTitle() {
        return this.title;
    }
}
