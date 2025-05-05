package com.tm.androidcopysdk.network;

import android.content.Context;
import android.text.TextUtils;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.utils.FcmUtil;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/EnsureRequest.class */
public class EnsureRequest extends BodyBase {
    AuthententicationDetails authententicationDetails = new AuthententicationDetails();
    String[] prams;

    public EnsureRequest(Context context, String username, String password) {
        if (!TextUtils.isEmpty(username)) {
            setUserName(username);
        }
        if (!TextUtils.isEmpty(password)) {
            setPassword(password);
        }
        String token = PrefManager.getStringPref(context, FcmUtil.FCM_TOKEN_KEY);
        this.prams = new String[7];
        this.prams[0] = "GCM";
        this.prams[1] = token;
        this.prams[2] = token;
        this.prams[3] = username;
        this.prams[4] = null;
        this.prams[5] = getAppVersionName(context);
        this.prams[6] = "ARCHIVER_CLIENT";
    }

    public void setUserName(String userName) {
        this.authententicationDetails.setUsername(userName);
    }

    public void setPassword(String password) {
        this.authententicationDetails.setPassword(password);
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getApplicationContext().getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "";
        }
    }
}
