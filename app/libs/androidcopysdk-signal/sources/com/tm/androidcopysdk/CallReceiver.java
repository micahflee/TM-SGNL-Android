package com.tm.androidcopysdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tm.androidcopysdk.utils.PrefManager;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/CallReceiver.class */
public class CallReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        boolean isActivate = PrefManager.getBooleanPref(context, R.string.activated, false);
        if (isActivate) {
            CommonUtils.startBackupService(context);
        }
    }
}
