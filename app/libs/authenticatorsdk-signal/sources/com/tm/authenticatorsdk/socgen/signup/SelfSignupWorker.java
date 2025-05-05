package com.tm.authenticatorsdk.socgen.signup;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/signup/SelfSignupWorker.class */
public class SelfSignupWorker extends Worker {
    private static final String TAG = "SelfSignupWorker";
    private Context context;

    public SelfSignupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    public ListenableWorker.Result doWork() {
        Log.d(TAG, "doWork: start");
        Intent i = new Intent();
        i.setClass(this.context, SelfSignupService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            this.context.startForegroundService(i);
        } else {
            this.context.startService(i);
        }
        return ListenableWorker.Result.success();
    }
}
