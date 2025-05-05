package com.tm.androidcopysdk;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.tm.androidcopysdk.events.EventService;
import com.tm.logger.Log;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/BackupWorker.class */
public class BackupWorker extends Worker {
    public static boolean stopWork = false;

    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d("BackupWorker", "BackupWorker");
    }

    @NonNull
    public ListenableWorker.Result doWork() {
        Log.d("BackupWorker", "start doWork");
        stopWork = true;
        EventBus.getDefault().register(this);
        CommonUtils.startSyncAccount(getApplicationContext());
        if (Build.VERSION.SDK_INT >= 23) {
            EventService.startEventService(getApplicationContext());
        }
        int count = 0;
        while (stopWork) {
            count++;
            try {
                Thread.sleep(150L);
            } catch (InterruptedException e) {
                Log.e("BackupWorker", "InterruptedException", e);
            }
            if (count >= 1600) {
                stopWork = false;
                Log.d("BackupWorker", "Result.failure()");
                return ListenableWorker.Result.failure();
            }
        }
        Log.d("BackupWorker", "sleep 150 count = " + count);
        EventBus.getDefault().unregister(this);
        Log.d("BackupWorker", "finish");
        stopWork = false;
        return ListenableWorker.Result.success();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(StopBackupWorker message) {
        Log.d("BackupWorker", "onMessageEvent BackupWorker");
        stopWork = false;
    }
}
