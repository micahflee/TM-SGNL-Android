package com.tm.androidcopysdk;

import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import androidx.annotation.RequiresApi;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.logger.Log;
@RequiresApi(api = 21)
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/AlarmService.class */
public class AlarmService extends JobService {
    public static final String ACTION_EVENT = "com.tm.androidcopysdk.AlarmEvent.keeper";
    private static final String TAG = "AlarmService";
    private static int REQUEST_CODE = 77;
    private static PendingIntent mPendingIntent;

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onHandleWork");
        PrefManager.setLongPref(getApplicationContext(), "LastAlarmTime", System.currentTimeMillis());
        if (FlavorSettings.getInstance().supportNativeMsg()) {
            CommonUtils.startSyncAccount(getApplicationContext());
            return false;
        }
        CommonUtils.startBackupService(getApplicationContext());
        return false;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public static void startJobService(Context context) {
        Log.d(TAG, "startJobService");
        if (Build.VERSION.SDK_INT >= 23) {
            scheduleJob(context);
        }
    }

    @RequiresApi(api = 23)
    public static void scheduleJob(Context context) {
        long min;
        Log.d(TAG, "startJobService scheduleJob");
        long last = PrefManager.getLongPref(context, "LastAlarmTime", 0L);
        long timeout = Long.parseLong(AndroidCopySDK.getInstance(context.getApplicationContext()).getSdkSettings().getSetting(SDKSettings.ALARM_APP_INTERVAL)) * 60000;
        long currentTime = System.currentTimeMillis();
        Log.d(TAG, " last = " + last + " :: timeout = " + timeout);
        Log.d(TAG, " currentTime = " + currentTime);
        Log.d(TAG, "(currentTime - last) > timeout == " + (currentTime - last > timeout));
        if (last == 0) {
            Log.d(TAG, "startBackupService ---->");
            PrefManager.setLongPref(context, "LastAlarmTime", System.currentTimeMillis());
            min = timeout;
        } else if (currentTime - last > timeout) {
            Log.d(TAG, "startBackupService AlarmService---->");
            PrefManager.setLongPref(context, "LastAlarmTime", System.currentTimeMillis());
            min = 0;
        } else {
            min = timeout - (currentTime - last);
            Log.d(TAG, "startBackupService AlarmService!!!! min =" + min);
        }
        ComponentName serviceComponent = new ComponentName(context, AlarmService.class);
        JobInfo.Builder builder = new JobInfo.Builder(705, serviceComponent);
        builder.setMinimumLatency(min);
        builder.setOverrideDeadline(min + 60000);
        builder.setRequiredNetworkType(2);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }
}
