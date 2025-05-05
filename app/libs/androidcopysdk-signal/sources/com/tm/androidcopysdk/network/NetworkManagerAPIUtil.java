package com.tm.androidcopysdk.network;

import android.content.Context;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/NetworkManagerAPIUtil.class */
public class NetworkManagerAPIUtil {
    public static String getPostMessageURLByAPIVersion(Context aContext) {
        String userName = TMCredentialsStore.getInstance(aContext).userName(aContext);
        String password = TMCredentialsStore.getInstance(aContext).password(aContext);
        if (userName.isEmpty() || password.isEmpty()) {
            return FlavorSettings.getInstance().getPostMessageUrlWithoutAuthentication();
        }
        return FlavorSettings.getInstance().getPostMessageUrl();
    }
}
