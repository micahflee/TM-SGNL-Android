package com.tm.androidcopysdk.network.appSettings;

import com.google.gson.JsonObject;
import com.tm.androidcopysdk.network.BodyBase;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/appSettings/ServerAppSettingsRequest.class */
public class ServerAppSettingsRequest extends BodyBase {
    private String username;
    private String password;

    public ServerAppSettingsRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public JsonObject getSettingsAsJson() {
        JsonObject jsonSetting = new JsonObject();
        jsonSetting.addProperty("username", this.username);
        jsonSetting.addProperty("password", this.password);
        return jsonSetting;
    }
}
