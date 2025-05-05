package com.tm.androidcopysdk;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import com.tm.logger.Log;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/BackupJobIntentService.class */
public class BackupJobIntentService extends JobIntentService {
    public static boolean stopWork = false;

    public BackupJobIntentService() {
        Log.d("BackupJobIntentService", " BackupJobIntentService init");
    }

    protected void onHandleWork(@NonNull Intent intent) {
        Log.d("BackupJobIntentService", "BackupJobIntentService start doWork");
        stopWork = true;
        EventBus.getDefault().register(this);
        CommonUtils.startSyncAccount(getApplicationContext());
        int count = 0;
        while (stopWork) {
            count++;
            try {
                Thread.sleep(150L);
            } catch (InterruptedException e) {
                Log.e("BackupJobIntentService", "InterruptedException", e);
            }
            if (count >= 1600) {
                count = 0;
                stopWork = false;
                Log.d("BackupJobIntentService", "Result.failure()");
            }
        }
        Log.d("BackupJobIntentService", "sleep 150 count = " + count);
        EventBus.getDefault().unregister(this);
        Log.d("BackupJobIntentService", "finish");
    }

    public static void startJobIntentService(Context context) {
        Log.d("BackupJobIntentService", "BackupJobIntentService");
        Intent service = new Intent(context, BackupJobIntentService.class);
        enqueueWork(context, service);
    }

    private static void enqueueWork(Context context, Intent service) {
        enqueueWork(context, BackupJobIntentService.class, DefinitionsSDKKt.BACKUP_JOB_SERVICE_JOB_ID, service);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(StopBackupWorker message) {
        Log.d("BackupJobIntentService", "onMessageEvent");
        stopWork = false;
    }
}
