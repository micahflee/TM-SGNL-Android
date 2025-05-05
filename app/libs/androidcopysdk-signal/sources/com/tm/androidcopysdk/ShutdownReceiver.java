package com.tm.androidcopysdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/ShutdownReceiver.class */
public class ShutdownReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.d("lior", "ShutdownReceiver " + intent.getAction());
        PrefManager.setBooleanPref(context, "last_startup", false);
    }
}
