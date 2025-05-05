package com.tm.androidcopysdk.network;

import android.content.Context;
import android.text.TextUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/UpdateVersionRequest.class */
public class UpdateVersionRequest extends BodyBase {
    AuthententicationDetails2 authententicationDetails = new AuthententicationDetails2();
    String params;

    public UpdateVersionRequest(Context context, String username, String password, String version) {
        if (!TextUtils.isEmpty(username)) {
            setUserName(username);
        }
        if (!TextUtils.isEmpty(password)) {
            setPassword(password);
        }
        this.params = version;
    }

    public void setUserName(String userName) {
        this.authententicationDetails.setUsername(userName);
    }

    public void setPassword(String password) {
        this.authententicationDetails.setPassword(password);
    }

    public void setClass(String className) {
        this.authententicationDetails.setClass_name(className);
    }

    public void setId(String id) {
        this.authententicationDetails.identifier = id;
    }
}
