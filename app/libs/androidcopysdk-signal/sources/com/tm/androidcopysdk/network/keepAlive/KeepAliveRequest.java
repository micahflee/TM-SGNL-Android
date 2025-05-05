package com.tm.androidcopysdk.network.keepAlive;

import android.text.TextUtils;
import com.tm.androidcopysdk.network.BodyBase;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/keepAlive/KeepAliveRequest.class */
public class KeepAliveRequest extends BodyBase {
    private String appId;
    private String installId;
    private String appVersion;
    private String deviceToken;

    public KeepAliveRequest(String appId) {
        this.installId = null;
        this.appVersion = null;
        this.deviceToken = null;
        if (TextUtils.isEmpty(appId)) {
            this.appId = "600701";
        } else {
            this.appId = appId;
        }
    }

    public KeepAliveRequest(String appId, String versionName, String installationId, String deviceToken) {
        this.installId = null;
        this.appVersion = null;
        this.deviceToken = null;
        if (TextUtils.isEmpty(appId)) {
            this.appId = "600701";
        } else {
            this.appId = appId;
        }
        if (!TextUtils.isEmpty(installationId)) {
            this.installId = installationId;
        }
        if (!TextUtils.isEmpty(versionName)) {
            this.appVersion = versionName;
        }
        if (!TextUtils.isEmpty(deviceToken)) {
            this.deviceToken = deviceToken;
        }
    }

    public String getAppId() {
        return this.appId;
    }

    public String getInstallId() {
        return this.installId;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public String getDeviceToken() {
        return this.deviceToken;
    }
}
