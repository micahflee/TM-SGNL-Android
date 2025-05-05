package com.tm.androidcopysdk.events;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/InstallEventAPI.class */
public class InstallEventAPI {
    long installTime;
    String version;

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getInstallTime() {
        return this.installTime;
    }

    public void setInstallTime(long installTime) {
        this.installTime = installTime;
    }
}
