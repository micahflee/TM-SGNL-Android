package com.tm.androidcopysdk.network.appSettings;

import com.tm.androidcopysdk.network.Base.ServerBaseResponse;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/appSettings/ServerAppSettingsResponse.class */
public class ServerAppSettingsResponse extends ServerBaseResponse {
    private Settings settings;

    public Settings getSettings() {
        return this.settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
