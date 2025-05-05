package com.tm.androidcopysdk.utils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/MDMReceiver.class */
public class MDMReceiver extends BroadcastReceiver {
    private String TAG = "MDMReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.d(this.TAG, " onReceive");
        Intent intent1 = new Intent("android.intent.action.mdm_receiver_intent");
        ComponentName componentName = new ComponentName("com.tm.archiverrunner", "com.tm.archiverrunner.receivers.MDMReceiverAndroidArchiver");
        intent1.setComponent(componentName);
        context.sendBroadcast(intent1);
    }
}
