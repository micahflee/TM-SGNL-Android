package com.tm.androidcopysdk.network.keepAlive;

import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.tm.androidcopysdk.AndroidCopySDK;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.DefinitionsSDKKt;
import com.tm.androidcopysdk.SDKSettings;
import com.tm.androidcopysdk.database.DBKeepAliveQueryHelper;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.network.NetworkManager;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.logger.Log;
import com.tm.utils.Definitions;
import com.tm.utils.FcmUtil;
import java.util.ArrayList;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import retrofit2.Response;
@RequiresApi(api = 21)
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/keepAlive/KeepWorkerIntentService.class */
public class KeepWorkerIntentService extends JobService {
    public static final String ACTION_EVENT = "com.tm.androidcopysdk.event.action.worker_server.keep_alive";
    public static final String INTENT_EXTRA_KEY = "shouldForceKeepAlive";
    private static final String TAG = "KeepWorkerIntentService";
    private static int REQUEST_CODE = 57;
    private static final int NOTIFICATION_ID = 1135;
    private boolean shouldForce = false;
    public JobParameters jobParameters = null;
    private PendingIntent mPendingIntent;

    public KeepWorkerIntentService() {
        Log.d(TAG, "KeepWorkerIntentService constructor");
    }

    @Override // android.app.job.JobService
    @RequiresApi(api = 22)
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob");
        if (FlavorSettings.getInstance().getFlavorName().equalsIgnoreCase("WPA")) {
            return true;
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (params.getExtras() != null && params.getExtras().getBoolean(INTENT_EXTRA_KEY)) {
            this.shouldForce = params.getExtras().getBoolean(INTENT_EXTRA_KEY);
        } else {
            this.shouldForce = false;
        }
        this.jobParameters = params;
        scheduleNextRequest();
        return true;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    public static void startJobIntentService(Context context, boolean shouldForce) {
        if (FlavorSettings.getInstance().getFlavorName().equalsIgnoreCase("WPA")) {
            return;
        }
        Log.d(TAG, "startService");
        if (!CommonUtils.isMyServiceRunning(context, KeepWorkerIntentService.class)) {
            scheduleJob(context, -1L, shouldForce);
        } else {
            Log.d(TAG, "already run !!!");
        }
    }

    private void handleKeepAlive() {
        Log.d(TAG, "handleKeepAlive");
        new Thread(new Runnable() { // from class: com.tm.androidcopysdk.network.keepAlive.KeepWorkerIntentService.1
            @Override // java.lang.Runnable
            public void run() {
                boolean wait = FcmUtil.getInstallId(KeepWorkerIntentService.this.getApplicationContext());
                if (wait) {
                    try {
                        Log.d(KeepWorkerIntentService.TAG, "wait for install ID");
                        Thread.sleep(2000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                NetworkManager nm = new NetworkManager(KeepWorkerIntentService.this.getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(KeepWorkerIntentService.this.getApplicationContext()).getString("baseurl", Definitions.BaseUrl));
                KeepAliveRequest sr = new KeepAliveRequest(FlavorSettings.getInstance().getAppIdForKeepAlive(), CommonUtils.getAppVersionName(KeepWorkerIntentService.this.getApplicationContext()), PreferenceManager.getDefaultSharedPreferences(KeepWorkerIntentService.this.getApplicationContext()).getString("install_identifier", ""), PrefManager.getStringPref(KeepWorkerIntentService.this.getApplicationContext(), FcmUtil.FCM_TOKEN_KEY));
                Log.d(KeepWorkerIntentService.TAG, "request KeepAlive: " + new Gson().toJson(sr));
                Response<JsonElement> res = nm.start(sr, null, KeepWorkerIntentService.this.getApplicationContext(), true);
                if (res == null) {
                    Log.d(KeepWorkerIntentService.TAG, "response is null");
                } else if (res.isSuccessful()) {
                    Log.d(KeepWorkerIntentService.TAG, "response.isSuccessful() = true");
                } else {
                    Log.d(KeepWorkerIntentService.TAG, "response.isSuccessful() = false");
                }
                KeepWorkerIntentService.this.setLastTime();
                KeepWorkerIntentService.this.scheduleNextRequest();
            }
        }).start();
    }

    private void handleKeepAliveV3() {
        new Thread(new Runnable() { // from class: com.tm.androidcopysdk.network.keepAlive.KeepWorkerIntentService.2
            @Override // java.lang.Runnable
            public void run() {
                String fcmToken = KeepWorkerIntentService.this.getApplicationContext().getSharedPreferences("marchiver", 0).getString(PrefManagerConstants.SHARED_PREFERENCE_SAVED_TOKEN_KEY, "");
                String installId = KeepWorkerIntentService.this.getApplicationContext().getSharedPreferences("marchiver", 0).getString(PrefManagerConstants.SHARED_PREFERENCE_SAVED_INSTANCE_ID, "");
                NetworkManager nm = new NetworkManager(KeepWorkerIntentService.this.getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(KeepWorkerIntentService.this.getApplicationContext()).getString("baseurl", Definitions.BaseUrl));
                KeepAliveRequestV3 sr = new KeepAliveRequestV3(KeepWorkerIntentService.this.getApplicationContext(), FlavorSettings.getInstance().getAppIdForKeepAlive(), CommonUtils.getAppVersionName(KeepWorkerIntentService.this.getApplicationContext()), installId, fcmToken);
                Response<JsonElement> res = nm.start(sr, null, KeepWorkerIntentService.this.getApplicationContext(), true);
                KeepWorkerIntentService.this.getApplicationContext().getSharedPreferences("marchiver", 0).edit().putLong(PrefManagerConstants.SHARED_PREFERENCE_KEEP_ALIVE_LAST_KEEP_ALIVE_ATTEMPT_TIMESTAMP_KEY, System.currentTimeMillis()).apply();
                if (res == null) {
                    Log.d(KeepWorkerIntentService.TAG, "handleKeepAliveV3 response is null");
                } else if (res.isSuccessful()) {
                    if (res.isSuccessful() && ((JsonElement) res.body()).getAsJsonObject().get("resultCode").getAsInt() == 0) {
                        Log.d(KeepWorkerIntentService.TAG, "handleKeepAliveV3 response is successful");
                        KeepWorkerIntentService.updateMessagesStatusAsSentToKeepAlive(sr.getKeepAliveArrayMessage(), KeepWorkerIntentService.this.getApplicationContext());
                        EventBus.getDefault().post(new KeepAliveEvent(res.code()));
                    }
                } else if (res.code() != 0) {
                    EventBus.getDefault().post(new KeepAliveEvent(res.code()));
                }
            }
        }).start();
    }

    public static void handleKeepAliveV3(final Context context) {
        new Thread(new Runnable() { // from class: com.tm.androidcopysdk.network.keepAlive.KeepWorkerIntentService.3
            @Override // java.lang.Runnable
            public void run() {
                String fcmToken = context.getSharedPreferences("marchiver", 0).getString(PrefManagerConstants.SHARED_PREFERENCE_SAVED_TOKEN_KEY, "");
                String installId = context.getSharedPreferences("marchiver", 0).getString(PrefManagerConstants.SHARED_PREFERENCE_SAVED_INSTANCE_ID, "");
                NetworkManager nm = new NetworkManager(context, PreferenceManager.getDefaultSharedPreferences(context).getString("baseurl", Definitions.BaseUrl));
                KeepAliveRequestV3 sr = new KeepAliveRequestV3(context, FlavorSettings.getInstance().getAppIdForKeepAlive(), CommonUtils.getAppVersionName(context), installId, fcmToken);
                Response<JsonElement> res = nm.start(sr, null, context, true);
                context.getSharedPreferences("marchiver", 0).edit().putLong(PrefManagerConstants.SHARED_PREFERENCE_KEEP_ALIVE_LAST_KEEP_ALIVE_ATTEMPT_TIMESTAMP_KEY, System.currentTimeMillis()).apply();
                if (res == null) {
                    Log.d(KeepWorkerIntentService.TAG, "handleKeepAliveV3 response is null");
                } else if (res.isSuccessful()) {
                    if (res.isSuccessful() && ((JsonElement) res.body()).getAsJsonObject().get("resultCode").getAsInt() == 0) {
                        Log.d(KeepWorkerIntentService.TAG, "handleKeepAliveV3 response is successful");
                        KeepWorkerIntentService.updateMessagesStatusAsSentToKeepAlive(sr.getKeepAliveArrayMessage(), context);
                        EventBus.getDefault().post(new KeepAliveEvent(res.code()));
                    }
                } else if (res.code() != 0) {
                    EventBus.getDefault().post(new KeepAliveEvent(res.code()));
                }
            }
        }).start();
    }

    @RequiresApi(api = 23)
    public static void scheduleJob(Context context, long delay, boolean shouldForce) {
        cancelJob(context);
        Log.d(TAG, "scheduleJob delay :" + delay);
        long start = (delay <= -1 || shouldForce) ? 3000L : delay;
        ComponentName serviceComponent = new ComponentName(context, KeepWorkerIntentService.class);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putBoolean(INTENT_EXTRA_KEY, shouldForce);
        JobInfo.Builder builder = new JobInfo.Builder(DefinitionsSDKKt.KEEP_WORKER_INTENT_SERVICE_JOB_ID, serviceComponent);
        builder.setMinimumLatency(start);
        builder.setOverrideDeadline(start + 5000);
        builder.setRequiredNetworkType(1);
        builder.setExtras(bundle);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateMessagesStatusAsSentToKeepAlive(JSONArray keepAliveArrayMessage, Context applicationContext) {
        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < keepAliveArrayMessage.length(); i++) {
            try {
                if (keepAliveArrayMessage.getJSONObject(i).getString("messageStatus").equals("Archived")) {
                    ids.add(keepAliveArrayMessage.getJSONObject(i).getString(CallLogMessageRecorder.messageId_key));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int res = DBKeepAliveQueryHelper.Companion.updateMessagesListAsAlreadySentToKeepAlive(applicationContext, (String[]) ids.toArray(new String[ids.size()]));
        Log.d(TAG, "updateMessagesListAsAlreadySentToKeepAlive to " + res);
    }

    private void postAlertDelay(long delay) {
        Log.w(TAG, "postAlertDelay");
        if (!FlavorSettings.getInstance().isKeepAliveV3()) {
            scheduleJob(getApplicationContext(), delay - System.currentTimeMillis(), false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scheduleNextRequest() {
        long last = getLastTime();
        long timeout = Long.parseLong(AndroidCopySDK.getInstance(this).getSdkSettings().getSetting(SDKSettings.GET_KEEP_ALIVE_INTERVAL)) * 60000;
        boolean isActivate = PrefManager.getBooleanPref((Context) this, PrefManagerConstants.SHARED_PREFERENCE_ACTIVATED_AA_KEY, false);
        if (isActivate) {
            Log.d(TAG, "isActivate = " + isActivate);
            long currentTime = System.currentTimeMillis();
            if (this.shouldForce || currentTime - last > timeout) {
                this.shouldForce = false;
                if (FlavorSettings.getInstance().isKeepAliveV3()) {
                    handleKeepAliveV3();
                    return;
                } else {
                    handleKeepAlive();
                    return;
                }
            }
            postAlertDelay(last + timeout);
            jobFinished(this.jobParameters, false);
            return;
        }
        Log.d(TAG, "isActivate = " + isActivate);
        postAlertDelay(System.currentTimeMillis() + timeout);
        jobFinished(this.jobParameters, false);
    }

    public void setLastTime() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putLong("cLastAliveTime", System.currentTimeMillis()).apply();
    }

    public long getLastTime() {
        return PreferenceManager.getDefaultSharedPreferences(this).getLong("cLastAliveTime", 0L);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public synchronized void onEvent(KeepAliveEvent event) {
        Log.d(TAG, "keep worker service - KeepAliveEvent  called");
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @RequiresApi(api = 21)
    public static void cancelJob(Context mContext) {
        JobScheduler scheduler = (JobScheduler) mContext.getSystemService("jobscheduler");
        scheduler.cancel(DefinitionsSDKKt.KEEP_WORKER_INTENT_SERVICE_JOB_ID);
        Log.i(TAG, "Cancelled Job if pending with ID: 966");
    }
}
