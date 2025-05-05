package com.tm.androidcopysdk;

import android.content.Context;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.tm.androidcopysdk.AndroidCopySettings;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.logger.Log;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/GetMessagesService.class */
public class GetMessagesService extends Worker {
    public static final String ACTION_SMS = "com.tm.androidcopysdk.action.getSMS";
    public static final String ACTION_MMS = "com.tm.androidcopysdk.action.getMMS";
    public static final String ACTION_CALLLOG = "com.tm.androidcopysdk.action.getCallLog";
    public static final String TASK_ID = "task_is";
    private static final String EXTRA_PARAM1 = "com.tm.androidcopysdk.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.tm.androidcopysdk.extra.PARAM2";
    public static final int MAX_QUEUE_MMS = 2;
    private static int numOfQueueIntentsMMS = 0;
    public static boolean isWork = false;
    private static Stack<String> stack = new Stack<>();

    public static int getNumOfQueueIntentsMMS() {
        return numOfQueueIntentsMMS;
    }

    public static void setNumOfQueueIntentsMMS(int numOfQueueIntentsMMS2) {
        numOfQueueIntentsMMS = numOfQueueIntentsMMS2;
    }

    public GetMessagesService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.d("GetMessagesService -Worker", "GetMessagesService");
    }

    @NonNull
    public ListenableWorker.Result doWork() {
        isWork = true;
        Log.d("GetMessagesService -Worker", "start doWork");
        String action = getInputData().getString(TASK_ID);
        try {
            startAction(action);
            while (!stack.isEmpty()) {
                startAction(stack.pop());
            }
        } catch (Exception e) {
            Log.e("GetMessagesService", "Exception", e);
        }
        isWork = false;
        return ListenableWorker.Result.success();
    }

    private void startAction(String action) {
        if (CommonUtils.appIsWorking(getApplicationContext()) && FlavorSettings.getInstance().supportNativeMsg()) {
            if ("com.tm.androidcopysdk.action.getSMS".equals(action) && isSMSEnabled() && CommonUtils.handleSmsPermissions(getApplicationContext())) {
                handleActionSMS();
            } else if ("com.tm.androidcopysdk.action.getMMS".equals(action) && isMMSEnabled() && CommonUtils.handleSmsPermissions(getApplicationContext())) {
                handleActionMMS();
                numOfQueueIntentsMMS--;
            } else if ("com.tm.androidcopysdk.action.getCallLog".equals(action) && isCallLogEnabled() && CommonUtils.handleCallLogPermissions(getApplicationContext())) {
                handleActionCallLog();
            }
        }
    }

    public static void startJobIntentService(Context context, String action) {
        if (isWork) {
            stack.push(action);
            return;
        }
        isWork = true;
        Data inputData = new Data.Builder().putString(TASK_ID, action).build();
        OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(GetMessagesService.class);
        if (Build.VERSION.SDK_INT >= 31) {
            builder.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST);
        }
        builder.setInputData(inputData);
        WorkManager.getInstance(context).enqueue(builder.build());
    }

    private boolean isSMSEnabled() {
        Set<String> types = new HashSet<>(1);
        Set<String> types1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getStringSet("type", types);
        for (String s1 : types1) {
            if (s1.contentEquals(AndroidCopySettings.DataType.SMS.name())) {
                return true;
            }
        }
        return false;
    }

    private boolean isMMSEnabled() {
        Set<String> types = new HashSet<>(1);
        Set<String> types1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getStringSet("type", types);
        for (String s1 : types1) {
            if (s1.contentEquals(AndroidCopySettings.DataType.MMS.name())) {
                return true;
            }
        }
        return false;
    }

    private boolean isCallLogEnabled() {
        Set<String> types = new HashSet<>(1);
        Set<String> types1 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getStringSet("type", types);
        for (String s1 : types1) {
            if (s1.contentEquals(AndroidCopySettings.DataType.CallLogs.name())) {
                return true;
            }
        }
        return false;
    }

    private void handleActionSMS() {
        DataGrabber.getInstance(getApplicationContext()).getSMSMessages();
    }

    private void handleActionCallLog() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DataGrabber.getInstance(getApplicationContext()).getCallLogMessages();
    }

    private void handleActionMMS() {
        try {
            Thread.sleep(1000L);
            DataGrabber.getInstance(getApplicationContext()).getMMSMessages();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e2) {
            e2.printStackTrace();
        }
    }
}
