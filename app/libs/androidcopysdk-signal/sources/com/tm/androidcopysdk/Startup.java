package com.tm.androidcopysdk;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.tm.androidcopysdk.events.PeriodicEventChecker;
import com.tm.androidcopysdk.network.appSettings.WorkerIntentService;
import com.tm.androidcopysdk.network.keepAlive.KeepWorkerIntentService;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.logger.Log;
import com.tm.utils.Util;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/Startup.class */
public class Startup extends BroadcastReceiver {
    public static boolean startUpisOn = false;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.d("StartupBroadcastReceiver", "startBackupService -1");
        Log.d("lior", " Startup " + intent.getAction());
        boolean isUpdate = intent.getAction().equalsIgnoreCase("android.intent.action.MY_PACKAGE_REPLACED") || intent.getAction().equalsIgnoreCase("android.intent.action.PACKAGE_REPLACED");
        boolean activated = CommonUtils.appIsWorking(context);
        if ((intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED") || isUpdate || intent.getAction().equalsIgnoreCase("android.intent.action.LOCKED_BOOT_COMPLETED")) && activated) {
            if (isUpdate) {
                Log.d("StartupBroadcastReceiver", "App was upgraded to new version at: " + System.currentTimeMillis());
                PrefManager.setLongPref(context, PrefManagerConstants.SHARED_PREFERENCE_LAST_UPGRADE_TIME, System.currentTimeMillis());
            }
            Log.d("StartupBroadcastReceiver", intent.getAction());
            if (!startUpisOn) {
                PrefManager.setBooleanPref(context, "last_startup", true);
            }
            startUpisOn = true;
            Log.d("StartupBroadcastReceiver", "startBackupService 0");
            Util.startBattery((Application) context.getApplicationContext());
            Log.d("StartupBroadcastReceiver", "startBackupService 1");
            CommonUtils.startBackupService(context);
            PeriodicEventChecker.startService(context, -1);
            WorkerIntentService.startJobService(context);
            if (Build.VERSION.SDK_INT >= 21) {
                KeepWorkerIntentService.startJobIntentService(context, true);
            }
            CompressionService.startJobIntentService(context);
        }
    }
}
