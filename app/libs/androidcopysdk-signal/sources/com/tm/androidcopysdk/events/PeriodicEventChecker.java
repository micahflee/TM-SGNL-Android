package com.tm.androidcopysdk.events;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import com.tm.androidcopysdk.AndroidCopySDK;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.DataGrabber;
import com.tm.androidcopysdk.DefinitionsSDKKt;
import com.tm.androidcopysdk.SDKSettings;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/PeriodicEventChecker.class */
public class PeriodicEventChecker extends JobIntentService {
    private static final String TAG = "PeriodicEventChecker";
    public static final int IDLE_EVENT_CHECKER = 10;
    public static final int LOW_STORAGE_EVENT_CHECKER = 11;
    public static final int STOP = 12;
    public static final String EXTRA_DATA = "extra";
    private PendingIntent mPendingIntent;

    protected void onHandleWork(@NonNull Intent intent) {
        int state = intent.getIntExtra(EXTRA_DATA, 0);
        Log.v(TAG, "onHandleWork ; state = " + state);
        switch (state) {
            case 10:
                handleIdleEventChecker();
                break;
            case LOW_STORAGE_EVENT_CHECKER /* 11 */:
                handleLowStorageEventChecker();
                break;
            default:
                scheduleNextRequest();
                break;
        }
        Log.v(TAG, "onHandleWork ; stop");
    }

    private void scheduleNextRequest() {
        handleIdleEventChecker();
        handleLowStorageEventChecker();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void postAlertDelay(int eventCheckerType, long delay, long interval) {
        Intent i = new Intent((Context) this, (Class<?>) PeriodicEventChecker.class);
        i.putExtra(EXTRA_DATA, eventCheckerType);
        AlarmManager am = (AlarmManager) getSystemService("alarm");
        if (Build.VERSION.SDK_INT >= 31) {
            this.mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 167772160);
        } else {
            this.mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, 134217728);
        }
        am.cancel(this.mPendingIntent);
        am.setRepeating(0, delay, interval, this.mPendingIntent);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void handleIdleEventChecker() {
        Log.d(TAG, "handleIdleEventChecker");
        long idleLastTimeEvent = PreferenceManager.getDefaultSharedPreferences(this).getLong(PrefManagerConstants.SHARED_PREFERENCE_LAST_IDLE_TIME_EVENT_KEY, 0L);
        long currentTime = System.currentTimeMillis();
        String val = AndroidCopySDK.getInstance(this).getSdkSettings().getSetting(SDKSettings.IDLE_EVENT_INTERVAL);
        long interval = 86400000;
        if (!TextUtils.isEmpty(val)) {
            interval = Long.valueOf(val).longValue();
        }
        long lastMsgArchiveTime = DataGrabber.lastMessageTime(this);
        if (currentTime - lastMsgArchiveTime > interval) {
            if (currentTime - idleLastTimeEvent > interval) {
                Log.d(TAG, "should send idle event");
                PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(PrefManagerConstants.SHARED_PREFERENCE_LAST_IDLE_TIME_EVENT_KEY, currentTime).commit();
                CommonUtils.addEvent(this, EventAbsObj.EventType.IdleEvent);
                postAlertDelay(10, interval + currentTime, interval);
                return;
            }
            postAlertDelay(10, interval + idleLastTimeEvent, interval);
            return;
        }
        postAlertDelay(10, interval + lastMsgArchiveTime, interval);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void handleLowStorageEventChecker() {
        Log.d(TAG, "handleLowStorageEventChecker");
        long lowStorageChecker = PreferenceManager.getDefaultSharedPreferences(this).getLong(PrefManagerConstants.SHARED_PREFERENCE_LAST_LOW_STORAGE_CHECKER_KEY, 0L);
        long currentTime = System.currentTimeMillis();
        String val = AndroidCopySDK.getInstance(this).getSdkSettings().getSetting(SDKSettings.LOW_STORAGE_EVENT_INTERVAL_MILLIS);
        long interval = 86400000;
        if (!TextUtils.isEmpty(val)) {
            interval = Long.valueOf(val).longValue();
        }
        if (currentTime - lowStorageChecker > interval) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(PrefManagerConstants.SHARED_PREFERENCE_LAST_LOW_STORAGE_CHECKER_KEY, currentTime).commit();
            int freeStorageThreshold = Integer.valueOf(AndroidCopySDK.getInstance(this).getSdkSettings().getSetting(SDKSettings.LOW_STORAGE_FREE_SPACE_THRESHOLD_MEGA)).intValue();
            Log.d(TAG, "freeStorageThreshold: " + freeStorageThreshold);
            long freeSpaceInMegabytes = Environment.getDataDirectory().getUsableSpace() / 1048576;
            Log.d(TAG, "freeSpaceInMegabytes: " + freeSpaceInMegabytes);
            if (freeSpaceInMegabytes <= freeStorageThreshold) {
                Log.d(TAG, "should send low storage event");
                CommonUtils.addEvent(this, EventAbsObj.EventType.LowStorageEvent);
            }
            Log.d(TAG, "interval in milliseconds: " + interval);
            postAlertDelay(11, interval + currentTime, interval);
            return;
        }
        postAlertDelay(11, interval + lowStorageChecker, interval);
    }

    private static void enqueueWork(Context periodicEventChecker, Intent compIntent) {
        enqueueWork(periodicEventChecker, PeriodicEventChecker.class, DefinitionsSDKKt.PERIODIC_EVENT_CHECKER_SERVICE_JOB_ID, compIntent);
    }

    public static void startService(Context context, int operation) {
        Log.d(TAG, "startService");
        if (FlavorSettings.getInstance().supportNativeMsg()) {
            Intent intent = new Intent(context, PeriodicEventChecker.class);
            intent.putExtra(EXTRA_DATA, operation);
            enqueueWork(context, intent);
            return;
        }
        Log.d("EventService", "this flavor not support events !  ! ! ! !");
    }
}
