package com.tm.androidcopysdk;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import com.tm.androidcopysdk.AndroidCopySettings;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/GetOldMessagesService.class */
public class GetOldMessagesService extends JobIntentService {
    public static final String ACTION_SMS = "com.tm.androidcopysdk.action.getSMS";
    public static final String ACTION_MMS = "com.tm.androidcopysdk.action.getMMS";
    public static final String ACTION_CALLLOG = "com.tm.androidcopysdk.action.getCallLog";
    private static final String EXTRA_PARAM1 = "com.tm.androidcopysdk.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.tm.androidcopysdk.extra.PARAM2";

    /* JADX WARN: Multi-variable type inference failed */
    protected void onHandleWork(@NonNull Intent intent) {
        boolean allSmsSavedToDb = false;
        boolean allMmsSavedToDb = false;
        boolean allCallogSavedToDb = false;
        if (intent != null) {
            intent.getAction();
            if (isSMSEnabled()) {
                allSmsSavedToDb = handleActionSMS();
            }
            if (isMMSEnabled()) {
                allMmsSavedToDb = handleActionMMS();
            }
            if (isCallLogEnabled()) {
                allCallogSavedToDb = handleActionCallLog(intent);
            }
        }
        if (allSmsSavedToDb && allMmsSavedToDb && allCallogSavedToDb) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(PrefManagerConstants.SHARED_PREFERENCE_ALL_HISTORIC_MESSAGES_SAVED_TO_DB_KEY, true).commit();
            CommonUtils.startSyncAccount(this);
        }
    }

    public static void enqueueWork(Context getOldMessagesService, Intent compIntent) {
        enqueueWork(getOldMessagesService, GetOldMessagesService.class, DefinitionsSDKKt.GET_OLD_MESSAGES_SERVICE_JOB_ID, compIntent);
    }

    public static void startJobIntentService(Context context) {
        Intent compIntent = new Intent(context, GetOldMessagesService.class);
        enqueueWork(context, compIntent);
    }

    public static void startActionSMS(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GetOldMessagesService.class);
        intent.setAction("com.tm.androidcopysdk.action.getSMS");
        enqueueWork(context, intent);
    }

    public static void startActionCallLog(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GetOldMessagesService.class);
        intent.setAction("com.tm.androidcopysdk.action.getCallLog");
        enqueueWork(context, intent);
    }

    public static void startActionMMS(Context context, String param1, String param2) {
        Intent intent = new Intent(context, GetOldMessagesService.class);
        intent.setAction("com.tm.androidcopysdk.action.getMMS");
        enqueueWork(context, intent);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean isSMSEnabled() {
        Set<String> types = new HashSet<>(1);
        Set<String> types1 = PreferenceManager.getDefaultSharedPreferences(this).getStringSet("type", types);
        for (String s1 : types1) {
            if (s1.contentEquals(AndroidCopySettings.DataType.SMS.name())) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean isMMSEnabled() {
        Set<String> types = new HashSet<>(1);
        Set<String> types1 = PreferenceManager.getDefaultSharedPreferences(this).getStringSet("type", types);
        for (String s1 : types1) {
            if (s1.contentEquals(AndroidCopySettings.DataType.MMS.name())) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean isCallLogEnabled() {
        Set<String> types = new HashSet<>(1);
        Set<String> types1 = PreferenceManager.getDefaultSharedPreferences(this).getStringSet("type", types);
        for (String s1 : types1) {
            if (s1.contentEquals(AndroidCopySettings.DataType.CallLogs.name())) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean handleActionSMS() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return DataGrabber.getInstance(this).getHistorySMSMessages();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean handleActionCallLog(Intent intent) {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return DataGrabber.getInstance(this).getHistoryCallLogMessages(intent);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean handleActionMMS() {
        try {
            Thread.sleep(1000L);
            return DataGrabber.getInstance(this).getHistoryMMSMessages();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
