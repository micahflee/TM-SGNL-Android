package com.tm.androidcopysdk.utils;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.tm.androidcopysdk.BackupService;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/IsAliveReceiver.class */
public class IsAliveReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.d("IsAliveReceiver", " onReceive");
        if (CommonUtils.appIsWorking(context)) {
            CommonUtils.startBackupService(context);
            if (CommonUtils.isMyServiceRunning(context, BackupService.class)) {
                Log.d("IsAliveReceiver", "send intent -  service is running");
                Intent intent1 = new Intent("android.intent.action.is_need_start_aa");
                ComponentName componentName = new ComponentName("com.tm.archiverrunner", "com.tm.archiverrunner.receivers.IsArchiverAliveReceiver");
                intent1.setComponent(componentName);
                context.sendBroadcast(intent1);
                return;
            }
            return;
        }
        Log.d("IsAliveReceiver", "serivce is down");
    }
}
