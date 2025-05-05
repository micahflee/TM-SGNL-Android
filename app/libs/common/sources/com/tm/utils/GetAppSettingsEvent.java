package com.tm.utils;
/* loaded from: input.aar:classes.jar:com/tm/utils/GetAppSettingsEvent.class */
public class GetAppSettingsEvent {
    public String username;
    public String password;
    public boolean success;

    public GetAppSettingsEvent(String username, String password) {
        this.username = username;
        this.password = password;
        this.success = true;
    }

    public GetAppSettingsEvent() {
    }
}
