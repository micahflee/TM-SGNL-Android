package com.tm.androidcopysdk.network.appSettings;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Pair;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.work.Configuration;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tm.androidcopysdk.AndroidCopySDK;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.DataGrabber;
import com.tm.androidcopysdk.DefinitionsSDKKt;
import com.tm.androidcopysdk.GetMessagesService;
import com.tm.androidcopysdk.MessageType;
import com.tm.androidcopysdk.Models.SignatureInfo;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.SDKSettings;
import com.tm.androidcopysdk.SettingsEvent;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.network.EnsureRequest;
import com.tm.androidcopysdk.network.NetworkManager;
import com.tm.androidcopysdk.network.TMEnsureIPManager;
import com.tm.androidcopysdk.network.TMUpdateVersionManager;
import com.tm.androidcopysdk.network.appSettings.UpdateEvent;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import com.tm.logger.Log;
import com.tm.utils.ApplicationInterface;
import com.tm.utils.Definitions;
import com.tm.utils.FcmUtil;
import com.tm.utils.GetAppSettingsEvent;
import com.tm.utils.PermissionEvent;
import com.tm.utils.TypeEvent;
import com.tm.utils.Util;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;
import retrofit2.Response;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/appSettings/WorkerIntentService.class */
public class WorkerIntentService extends JobService implements FcmUtil.FcmTokenCallBack {
    public static final String ACTION_EVENT = "com.tm.androidcopysdk.event.action.worker_server";
    public static final String ACTION_GET_APP_SETTINGS = "com.tm.androidcopy.getappsettingsnotification.action.open_notification";
    private static final String TAG = "WorkerIntentService";
    private List<String> listPermissionsNeeded;
    public static final String INTENT_EXTRA_KEY = "shouldForceWorkerIntentService";
    private static final int NOTIFICATION_ID = 1131;
    private PendingIntent mPendingIntent;
    private static int REQUEST_CODE = 55;
    public static String RECORDED_CALLS_NOTIFICATION_ID = "RECORDED_CALLS_NOTIFICATION_ID";
    public static String RECORDED_CALLS_CHANNEL_NAME = "RECORDED_CALLS_CHANNEL_NAME";
    public static String DEVICE_TYPE_SMS = "SMS";
    public static String DEVICE_TYPE_MMS = "MMS";
    public static String DEVICE_TYPE_VOICE_CALL_CALLLOG_AND_RECORDING = "VOICE_CALL_CALLLOG_AND_RECORDING";
    public static String DEVICE_TYPE_VOICE_CALL_CALLLOG = "VOICE_CALL_CALLLOG";
    public static String DEVICE_TYPE_AUDIO_CALL = "audioCalls";
    public static String DEVICE_TYPE_VIDEO_CALL = "videoCalls";
    public static String SIGNATURE_TYPE = "signatureType";
    public static String SIGNATURE_TEXT = "signature";
    public static String CALLLOGS = "CallLogs";
    public static String FIRST_SENT_TIME = "FIRST_SENT_TIME";
    public static String LAST_SENT_TIME = "LAST_SENT_TIME";
    private String tmpU = "";
    private String tmpS = "";
    private JobParameters params = null;
    private boolean shouldForce = false;

    public WorkerIntentService() {
        Configuration.Builder builder = new Configuration.Builder();
        builder.setJobSchedulerJobIdRange((int) DefinitionsSDKKt.WORKER_INTENT_SERVICE_JOB_ID_START_RANGE, (int) DefinitionsSDKKt.WORKER_INTENT_SERVICE_JOB_ID_END_RANGE);
    }

    @Override // android.app.job.JobService
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob");
        this.params = params;
        String user = "";
        if (Build.VERSION.SDK_INT >= 22) {
            user = params.getExtras().getString("username");
            if (params.getExtras() != null && params.getExtras().getBoolean(INTENT_EXTRA_KEY)) {
                Log.d(TAG, "params.getExtras().getBoolean(INTENT_EXTRA_KEY): " + params.getExtras().getBoolean(INTENT_EXTRA_KEY));
                this.shouldForce = params.getExtras().getBoolean(INTENT_EXTRA_KEY);
            }
        }
        if (!TextUtils.isEmpty(user)) {
            startHandleGetAppSetting();
            return true;
        }
        scheduleNextRequest();
        return true;
    }

    @Override // android.app.job.JobService
    public boolean onStopJob(JobParameters params) {
        scheduleNextRequest();
        return false;
    }

    public static void startJobIntentService(Context context) {
        startJobIntentService(context, false);
    }

    public static void startJobIntentService(Context context, boolean shouldForce) {
        Log.d(TAG, "startJobIntentService- getAppSettings");
        scheduleJob(context, -1L, shouldForce);
    }

    @RequiresApi(api = 23)
    public static void scheduleJob(Context context, long delay, boolean shouldForce) {
        Log.d(TAG, "scheduleJob delay :" + delay);
        PersistableBundle bundle = new PersistableBundle();
        bundle.putBoolean(INTENT_EXTRA_KEY, shouldForce);
        long start = (delay <= -1 || shouldForce) ? 5000L : delay;
        ComponentName serviceComponent = new ComponentName(context, WorkerIntentService.class);
        JobInfo.Builder builder = new JobInfo.Builder(DefinitionsSDKKt.WORKER_INTENT_SERVICE_JOB_ID, serviceComponent);
        builder.setMinimumLatency(start);
        builder.setOverrideDeadline(start + 5000);
        builder.setRequiredNetworkType(1);
        builder.setExtras(bundle);
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(JobScheduler.class);
        jobScheduler.schedule(builder.build());
    }

    public static void startJobService(Context context) {
        Log.d(TAG, "startJobService");
        if (FlavorSettings.getInstance().supportNativeMsg() || !FlavorSettings.getInstance().oldVersion()) {
            if (!CommonUtils.isMyServiceRunning(context, WorkerIntentService.class)) {
                scheduleJob(context, -1L, false);
                return;
            } else {
                Log.d(TAG, "already run !!!");
                return;
            }
        }
        Log.d(TAG, "this flavor not support getAppSettings from the sdk!  ! ! ! !");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleGetAppSetting(Intent intent) {
        boolean isRecording;
        String signatureText;
        Log.d(TAG, "handleGetAppSetting");
        CommonUtils.isWifiOr3GConnection(this);
        boolean isFromSignIn = (this.params.getExtras() == null || this.params.getExtras().getString("username") == null) ? false : true;
        String userName = isFromSignIn ? this.params.getExtras().getString("username") : TMCredentialsStore.getInstance(this).userName(this);
        String password = isFromSignIn ? this.params.getExtras().getString("password") : TMCredentialsStore.getInstance(this).password(this);
        if (TextUtils.isEmpty(userName)) {
            Log.d(TAG, "user is empty");
            return;
        }
        boolean ensure_was_done = PrefManager.getBooleanPref(this, "finish_ensure");
        String currentVersion = EnsureRequest.getAppVersionName(this);
        String lastSentVersion = PrefManager.getStringPref(this, "pref_version_number", "");
        boolean ensure_was_done2 = (ensure_was_done && lastSentVersion.equalsIgnoreCase(currentVersion)) || !FlavorSettings.getInstance().oldVersion();
        if (!ensure_was_done2) {
            boolean hasToken = !TextUtils.isEmpty(PrefManager.getStringPref(this, FcmUtil.FCM_TOKEN_KEY));
            if (hasToken) {
                TMEnsureIPManager.getInstance().postEnsure(this, userName, password);
            } else if (!hasToken) {
                this.tmpU = userName;
                this.tmpS = password;
                FcmUtil.getFcmToken(getApplication(), this, this);
                try {
                    Log.d("lior", "wait 1500");
                    Thread.sleep(1500L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (ensure_was_done2) {
            TMUpdateVersionManager.getInstance(this).sendingVersionNumberToServer();
        }
        NetworkManager nm = new NetworkManager(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(this).getString("baseurl", Definitions.BaseUrl));
        ServerAppSettingsRequest sr = new ServerAppSettingsRequest(userName, password);
        Response<JsonElement> res = nm.start(sr, null, getApplicationContext(), false);
        Log.d(TAG, "response AppSetting: " + res);
        boolean resetLastTime = true;
        boolean scheduleNextRequest = false;
        boolean getAppSettingsSuccess = false;
        if (res == null) {
            scheduleNextRequest = true;
        } else if (res.isSuccessful()) {
            Log.d("lior", "get app settings : response.isSuccessful() = true");
            Log.d(TAG, "res.body(): " + ((JsonElement) res.body()).toString());
            Log.d("network", "response.isSuccessful() = true");
            getAppSettingsSuccess = true;
            scheduleNextRequest = true;
            JsonArray jsonArray = ((JsonObject) res.body()).get("settings").getAsJsonObject().get("deviceTypes").getAsJsonArray();
            Log.d(TAG, jsonArray.toString());
            Set<String> types = new HashSet<>(1);
            if (Util.supportGetAppSettings(getApplication()) || FlavorSettings.getInstance().supportSettings()) {
                Set<String> types1 = new HashSet<>(1);
                Set<String> typesFromSharedPreference = PreferenceManager.getDefaultSharedPreferences(this).getStringSet("type", types1);
                boolean isRecordingFromSharedPreference = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("recording", false);
                ArrayList<String> deviceTypes = new ArrayList<>();
                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        deviceTypes.add(jsonArray.get(i).getAsString());
                        Log.d(TAG, "deviceTypes add : " + jsonArray.get(i).getAsString());
                    }
                }
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                boolean disableText = false;
                if (((JsonObject) res.body()).get("settings").getAsJsonObject().has("disableAppText")) {
                    disableText = ((JsonObject) res.body()).get("settings").getAsJsonObject().get("disableAppText").getAsBoolean();
                    Log.d(TAG, "disableText == " + disableText);
                }
                if (FlavorSettings.getInstance().oldVersion() && !disableText && deviceTypes.contains(DEVICE_TYPE_VOICE_CALL_CALLLOG_AND_RECORDING)) {
                    if (!typesFromSharedPreference.contains(CALLLOGS)) {
                        editor.putString(MessageType.CallLog.name() + DataGrabber.DATE_OF_MESSAGE, String.valueOf(System.currentTimeMillis()));
                    }
                    types.add(CALLLOGS);
                    Log.d(TAG, " recording = " + (!Util.isPlayVersion(getApplication()) || Util.isDefaultRecordingApp(getApplication())));
                    editor.putBoolean("recording", !Util.isPlayVersion(getApplication()) || Util.isDefaultRecordingApp(getApplication()));
                    isRecording = !(Util.isPlayVersion(getApplication()) || Util.isDefaultRecordingApp(getApplication())) || Util.isDefaultRecordingApp(getApplication());
                } else if (!FlavorSettings.getInstance().oldVersion() && deviceTypes.contains(DEVICE_TYPE_VOICE_CALL_CALLLOG_AND_RECORDING)) {
                    if (!typesFromSharedPreference.contains(CALLLOGS)) {
                        editor.putString(MessageType.CallLog.name() + DataGrabber.DATE_OF_MESSAGE, String.valueOf(System.currentTimeMillis()));
                    }
                    types.add(CALLLOGS);
                    editor.putBoolean("recording", true);
                    isRecording = true;
                    editor.putInt("recording_type", 2);
                    Log.d(TAG, "recording = true. recording_type = 2");
                } else {
                    isRecording = false;
                    Log.d(TAG, "----> recording = false  <-----");
                    editor.putBoolean("recording", false);
                }
                if (FlavorSettings.getInstance().oldVersion() && !disableText && deviceTypes.contains(DEVICE_TYPE_VOICE_CALL_CALLLOG)) {
                    if (!typesFromSharedPreference.contains(CALLLOGS)) {
                        editor.putString(MessageType.CallLog.name() + DataGrabber.DATE_OF_MESSAGE, String.valueOf(System.currentTimeMillis()));
                    }
                    types.add(CALLLOGS);
                    Log.d(TAG, "----> recording = false");
                    editor.putBoolean("recording", false);
                    isRecording = false;
                } else if (!FlavorSettings.getInstance().oldVersion() && deviceTypes.contains(DEVICE_TYPE_VOICE_CALL_CALLLOG)) {
                    if (!typesFromSharedPreference.contains(CALLLOGS)) {
                        editor.putString(MessageType.CallLog.name() + DataGrabber.DATE_OF_MESSAGE, String.valueOf(System.currentTimeMillis()));
                    }
                    types.add(CALLLOGS);
                    Log.d(TAG, "----> recording = false");
                    editor.putBoolean("recording", false);
                    isRecording = false;
                    editor.putInt("recording_type", 1);
                }
                if (!FlavorSettings.getInstance().oldVersion() && !deviceTypes.contains(DEVICE_TYPE_VOICE_CALL_CALLLOG) && !deviceTypes.contains(DEVICE_TYPE_VOICE_CALL_CALLLOG_AND_RECORDING)) {
                    Log.d(TAG, "----> recording = false");
                    editor.putBoolean("recording", false);
                    isRecording = false;
                    editor.putInt("recording_type", 0);
                }
                if (!FlavorSettings.getInstance().oldVersion()) {
                    JsonObject settings = ((JsonObject) res.body()).get("settings").getAsJsonObject();
                    JsonElement audioCallsJson = settings.get(DEVICE_TYPE_AUDIO_CALL);
                    if (audioCallsJson != null) {
                        boolean audioCalls = audioCallsJson.getAsBoolean();
                        Log.d(TAG, "audioCalls = " + audioCalls);
                        PrefManager.setBooleanPref(this, PrefManagerConstants.SHARED_PREFERENCE_IS_SUPPORT_AUDIO_CALLS, audioCalls);
                    }
                    JsonElement videoCallsJson = settings.get(DEVICE_TYPE_VIDEO_CALL);
                    if (videoCallsJson != null) {
                        boolean videoCalls = videoCallsJson.getAsBoolean();
                        PrefManager.setBooleanPref(this, PrefManagerConstants.SHARED_PREFERENCE_IS_SUPPORT_VIDEO_CALLS, videoCalls);
                        Log.d(TAG, "videoCalls = " + videoCalls);
                    }
                }
                if (!FlavorSettings.getInstance().oldVersion()) {
                    JsonObject settings2 = ((JsonObject) res.body()).get("settings").getAsJsonObject();
                    JsonElement signatureTypeJson = settings2.get(SIGNATURE_TYPE);
                    JsonElement signatureTextJson = settings2.get(SIGNATURE_TEXT);
                    if (signatureTypeJson != null && signatureTextJson != null) {
                        int signatureType = signatureTypeJson.getAsInt();
                        Log.d(TAG, "signatureType = " + signatureType);
                        if (!signatureTextJson.isJsonNull()) {
                            signatureText = signatureTextJson.getAsString();
                        } else {
                            signatureText = "";
                        }
                        Log.d(TAG, "signatureText.isEmpty() = " + signatureText.isEmpty());
                        SignatureInfo signatureInfo = new SignatureInfo(SignatureInfo.SignatureMode.values()[signatureType], signatureText);
                        AndroidCopySDK.getInstance(this).setSignatureFromGetAppSettings(signatureInfo);
                    }
                }
                if (deviceTypes.contains(DEVICE_TYPE_SMS)) {
                    if (!typesFromSharedPreference.contains(DEVICE_TYPE_SMS)) {
                        editor.putString(MessageType.SMS.name() + DataGrabber.DATE_OF_MESSAGE, String.valueOf(System.currentTimeMillis()));
                    }
                    types.add("SMS");
                }
                if (deviceTypes.contains(DEVICE_TYPE_MMS)) {
                    if (!typesFromSharedPreference.contains(DEVICE_TYPE_MMS)) {
                        editor.putString("lastMMSID", "");
                        editor.putString(MessageType.MMS.name() + DataGrabber.DATE_OF_MESSAGE, String.valueOf(System.currentTimeMillis()));
                    }
                    types.add("MMS");
                }
                if (FlavorSettings.getInstance().oldVersion()) {
                    sendEventAndNotificationIfNeed(types, isRecording);
                    if (isRecording && isRecording != isRecordingFromSharedPreference) {
                        CommonUtils.addNoSupportForCallRecordingEvent(this, EventAbsObj.EventType.NoSupportForCallRecordingEvent, String.format(getString(R.string.no_support_for_call_recording), TMCredentialsStore.getInstance(this).userName(this), PreferenceManager.getDefaultSharedPreferences(this).getString("phonenumber", "999999999"), String.valueOf(Build.VERSION.SDK_INT), Build.MANUFACTURER + " " + Build.MODEL));
                    }
                }
                if (!CommonUtils.equalsSet(typesFromSharedPreference, types)) {
                    editor.putStringSet("type", types);
                    editor.commit();
                    Log.d(TAG, "commit 1");
                    EventBus.getDefault().post(new TypeEvent());
                } else if (isRecording != isRecordingFromSharedPreference) {
                    editor.commit();
                    Log.d(TAG, "commit 2");
                } else if (editor != null) {
                    editor.commit();
                }
                SharedPreferences.Editor editor2 = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor2.putBoolean("finish_get_app_setting", true);
                editor2.commit();
                Log.d(TAG, "commit 3");
                Log.d("deviceTypes", deviceTypes.toString());
            } else {
                if (Util.isSocgenVersion(getApplication())) {
                    types.add(CALLLOGS);
                    sendEventAndNotificationIfNeed(types, true);
                }
                EventBus.getDefault().post(new PermissionEvent());
            }
            Log.d(TAG, "ACTIVATED_USER is true now.");
            CommonUtils.setActivatedUser(this, true);
            Log.d(TAG, "ACTIVATED_AA_KEY is true now. archive is on");
            PrefManager.setBooleanPref((Context) this, PrefManagerConstants.SHARED_PREFERENCE_ACTIVATED_AA_KEY, true);
            EventBus.getDefault().post(new UpdateEvent(UpdateEvent.EVENTS_TYPE.activated));
            CommonUtils.appIsWorking(this);
            PrefManager.setLongPref(this, "LastSettingsTimeSuspend", 0L);
        } else {
            if (res.raw().code() == 401 && !isFromSignIn) {
                Log.d("network", "error code =" + res.raw().code());
                Log.d(TAG, "ACTIVATED_AA_KEY is false now. archive is off");
                PrefManager.setBooleanPref((Context) this, PrefManagerConstants.SHARED_PREFERENCE_ACTIVATED_AA_KEY, false);
                try {
                    if (res.errorBody() != null) {
                        String errorString = res.errorBody().string();
                        if (!errorString.isEmpty()) {
                            Log.d(TAG, errorString);
                            JSONObject jsonObject = new JSONObject(errorString);
                            int resultCode = jsonObject.getInt("resultCode");
                            Log.e(TAG, "Result Code: " + resultCode);
                            if (resultCode == 45 && FlavorSettings.getInstance().isSupportSuspendUser()) {
                                long currentTime = System.currentTimeMillis();
                                Log.e(TAG, "currentTime: " + currentTime);
                                if (PrefManager.getLongPref(this, "LastSettingsTimeSuspend", 0L) == 0) {
                                    PrefManager.setLongPref(this, "LastSettingsTimeSuspend", currentTime);
                                }
                                PrefManager.setLongPref(this, "LastSettingsTimeSuspend", currentTime);
                                EventBus.getDefault().post(new UpdateEvent(UpdateEvent.EVENTS_TYPE.authProcess));
                                scheduleNextRequest();
                            }
                            resetLastTime = false;
                            Util.signOut(getApplication());
                            CommonUtils.stopBackupService(getApplicationContext(), true);
                        }
                    }
                } catch (Exception e2) {
                    Log.e(TAG, "*************", e2);
                }
            }
            Log.d("network", "response.isSuccessful() = false");
        }
        if (isFromSignIn) {
            if (getAppSettingsSuccess) {
                String userName2 = isFromSignIn ? this.params.getExtras().getString("username") : TMCredentialsStore.getInstance(this).userName(this);
                String password2 = isFromSignIn ? this.params.getExtras().getString("password") : TMCredentialsStore.getInstance(this).password(this);
                EventBus.getDefault().post(new GetAppSettingsEvent(userName2, password2));
            } else {
                EventBus.getDefault().post(new GetAppSettingsEvent());
                resetLastTime = false;
                scheduleNextRequest = false;
            }
            initPushFcmListener();
        }
        Util.updateMdmPhoneNumberIfNeed(getApplication(), userName, password);
        Util.updateMdmFirstNameLastNameIfNeed(getApplication(), userName, password);
        if (resetLastTime) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putLong("LastSettingsTime", System.currentTimeMillis()).commit();
        }
        if (scheduleNextRequest) {
            scheduleNextRequest();
        }
        if (CommonUtils.ifNeedFolderEvent(this) && !isFromSignIn) {
            showWizardNotification();
            sendFolderEvent();
        }
        EventBus.getDefault().post(new SettingsEvent(AndroidCopySDK.getInstance(this).getmSettings()));
        if (CommonUtils.handleSmsPermissions(this)) {
            GetMessagesService.startJobIntentService(this, "com.tm.androidcopysdk.action.getSMS");
            GetMessagesService.startJobIntentService(this, "com.tm.androidcopysdk.action.getMMS");
        }
        if (CommonUtils.handleCallLogPermissions(this)) {
            GetMessagesService.startJobIntentService(this, "com.tm.androidcopysdk.action.getCallLog");
        }
    }

    private void sendFolderEvent() {
        Log.d(TAG, "sendFolderEvent");
        long lastEventTime = PrefManager.getLongPref(this, "last_folder_event", 0L);
        if (System.currentTimeMillis() - lastEventTime > 86400000) {
            Log.d(TAG, "sendFolderEvent WrongFolderEvent");
            CommonUtils.addEvent(getApplicationContext(), EventAbsObj.EventType.WrongFolderEvent);
            PrefManager.setLongPref(this, "last_folder_event", System.currentTimeMillis());
        }
    }

    private void showWizardNotification() {
        Log.d(TAG, "showWizardNotification");
        Class activityClass = Util.getMainActivityClass((Application) getApplicationContext());
        PendingIntent pending = null;
        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            if (Build.VERSION.SDK_INT >= 31) {
                pending = PendingIntent.getActivity(getApplicationContext(), 0, intent, 167772160);
            } else {
                pending = PendingIntent.getActivity(getApplicationContext(), 0, intent, 134217728);
            }
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel mChannel = new NotificationChannel(RECORDED_CALLS_NOTIFICATION_ID, RECORDED_CALLS_CHANNEL_NAME, 4);
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, RECORDED_CALLS_NOTIFICATION_ID).setContentTitle(StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_TITLE : getString(R.string.notification_title)).setContentText(StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.RECORDED_ACTIVITY_TEXT_SDK : getString(R.string.recorded_activity_text_sdk)).setSmallIcon(R.drawable.icon_aa).setAutoCancel(true);
            if (pending != null) {
                mBuilder.setContentIntent(pending);
            }
            Log.d(TAG, "showWizardNotification notify 1010101");
            notificationManager.notify(1010101, mBuilder.build());
        }
    }

    private void initPushFcmListener() {
        Util.initPushFcmListener(getApplication());
    }

    private void sendEventAndNotificationIfNeed(Set<String> types, boolean isRecording) {
        if (!handlePermissions(types, isRecording)) {
            if (getFirstSentTime() == 0) {
                createPermissionNotification(getPermissionNeededList());
                setFirstSentTime(System.currentTimeMillis());
                setSentTime(System.currentTimeMillis());
                return;
            } else if (getFirstSentTime() > 0 && System.currentTimeMillis() - getFirstSentTime() < 86400000) {
                sendMissingPermissionEvent(getPermissionNeededList());
                createPermissionNotification(getPermissionNeededList());
                setSentTime(System.currentTimeMillis());
                return;
            } else if (getFirstSentTime() > 0 && System.currentTimeMillis() - getFirstSentTime() >= 86400000 && System.currentTimeMillis() - getSentTime() > 86400000) {
                sendMissingPermissionEvent(getPermissionNeededList());
                createPermissionNotification(getPermissionNeededList());
                setSentTime(System.currentTimeMillis());
                return;
            } else {
                return;
            }
        }
        setFirstSentTime(0L);
        setSentTime(0L);
    }

    private void setSentTime(long lastSentTime) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(LAST_SENT_TIME, lastSentTime).commit();
    }

    private void createPermissionNotification(List<String> listPermissionsNeeded) {
        if (listPermissionsNeeded != null && !listPermissionsNeeded.isEmpty()) {
            String permission = "";
            if (listPermissionsNeeded.size() == 1) {
                if (listPermissionsNeeded.get(0).equals("android.permission.READ_CALL_LOG") || listPermissionsNeeded.get(0).equals("android.permission.READ_PHONE_STATE")) {
                    permission = StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.PHONE : getString(R.string.phone);
                } else if (listPermissionsNeeded.get(0).equals("android.permission.READ_SMS")) {
                    permission = StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.SMS_MMS : getString(R.string.sms_mms);
                } else if (listPermissionsNeeded.get(0).equals("android.permission.READ_EXTERNAL_STORAGE") && Build.VERSION.SDK_INT < 33) {
                    permission = StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.STORAGE : getString(R.string.storage);
                } else {
                    permission = "";
                }
            }
            String message = "Missing " + permission + " permissions. Tap to enable permissions";
            CommonUtils.showPermissionsNotification(this, message, NOTIFICATION_ID, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.CHANEL_ID : getString(R.string.chanel_id), StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.CHANEL_NAME : getString(R.string.chanel_name), "NOTIFICATION_DATA_GRABER_ID_KEY", 23);
            EventBus.getDefault().post(new PermissionEvent());
        }
    }

    private void sendMissingPermissionEvent(List<String> listPermissionsNeeded) {
        if (listPermissionsNeeded != null) {
            for (String needPermission : listPermissionsNeeded) {
                String permission = "Missing ";
                if (needPermission.equals("android.permission.READ_PHONE_STATE")) {
                    permission = permission + (StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.PHONE : getString(R.string.phone));
                } else if (needPermission.equals("android.permission.READ_CALL_LOG")) {
                    permission = permission + (StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.CALLLOGS : getString(R.string.calllogs));
                } else if (needPermission.equals("android.permission.READ_SMS")) {
                    permission = permission + (StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.SMS_MMS : getString(R.string.sms_mms));
                } else if (needPermission.equals("android.permission.READ_EXTERNAL_STORAGE") && Build.VERSION.SDK_INT < 33) {
                    permission = permission + (StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.STORAGE : getString(R.string.storage));
                }
                CommonUtils.addMissingPermissionsEvent(this, EventAbsObj.EventType.MissingPermissionsEvent, permission + " Permission");
            }
        }
    }

    private boolean handlePermissions(Set<String> types, boolean isNeedRecording) {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> listPermissionsNeeded = new ArrayList<>();
            int readCallLogsPermission = ActivityCompat.checkSelfPermission(this, "android.permission.READ_CALL_LOG");
            int readSmsPermission = ActivityCompat.checkSelfPermission(this, "android.permission.READ_SMS");
            int readStoragePermissions = ActivityCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE");
            ActivityCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE");
            for (String type : types) {
                if (type.contains(CALLLOGS) && readCallLogsPermission != 0 && FlavorSettings.getInstance().supportNativeMsg()) {
                    listPermissionsNeeded.add("android.permission.READ_CALL_LOG");
                }
                if (type.contains(CALLLOGS) && isNeedRecording && readStoragePermissions != 0 && CommonUtils.isDefaultRecorder(this)) {
                    if (Build.VERSION.SDK_INT < 33) {
                        listPermissionsNeeded.add("android.permission.READ_EXTERNAL_STORAGE");
                    } else {
                        Log.d(TAG, "android 13 no need READ_EXTERNAL_STORAGE");
                    }
                }
                if (type.contains(DEVICE_TYPE_SMS) || type.contains(DEVICE_TYPE_MMS)) {
                    if (readSmsPermission != 0) {
                        if (!listPermissionsNeeded.contains("android.permission.READ_SMS")) {
                            listPermissionsNeeded.add("android.permission.READ_SMS");
                        }
                    } else {
                        CommonUtils.setMissPermissionFlag(getApplicationContext(), true);
                    }
                }
            }
            if (readSmsPermission != 0 && !listPermissionsNeeded.contains("android.permission.READ_PHONE_STATE")) {
                listPermissionsNeeded.add("android.permission.READ_PHONE_STATE");
            }
            if (!listPermissionsNeeded.isEmpty()) {
                setPermissionNeededList(listPermissionsNeeded);
                return false;
            }
            setPermissionNeededList(null);
            return true;
        }
        setPermissionNeededList(null);
        return true;
    }

    private void setPermissionNeededList(List<String> listPermissionsNeeded) {
        this.listPermissionsNeeded = listPermissionsNeeded;
    }

    private List<String> getPermissionNeededList() {
        return this.listPermissionsNeeded;
    }

    private void postAlertDelay(long delay) {
        scheduleJob(getApplicationContext(), delay - System.currentTimeMillis(), false);
    }

    private void scheduleNextRequest() {
        Log.d(TAG, "scheduleNextRequest. shouldForce: " + this.shouldForce);
        CommonUtils.isWifiOr3GConnection(this);
        long last = PreferenceManager.getDefaultSharedPreferences(this).getLong("LastSettingsTime", 0L);
        long timeout = Long.parseLong(AndroidCopySDK.getInstance(this).getSdkSettings().getSetting(SDKSettings.GET_APP_SETTINGS_INTERVAL)) * 60000;
        long currentTime = System.currentTimeMillis();
        Log.d(TAG, "last1: " + last);
        Log.d(TAG, "regular timeout: " + timeout);
        Pair<Long, Long> times = getSuspendTimeout(last, timeout, currentTime);
        long last2 = ((Long) times.first).longValue();
        long timeout2 = ((Long) times.second).longValue();
        Log.d(TAG, "currentTime - last) > timeout: " + currentTime + "-" + last2 + ">" + timeout2);
        Log.d(TAG, "currentTime - last) = " + currentTime + "-" + last2 + ">" + (currentTime - last2));
        Log.d(TAG, "currentTime - last) > timeout: " + (currentTime - last2 > timeout2));
        if (this.shouldForce || currentTime - last2 > timeout2) {
            this.shouldForce = false;
            startHandleGetAppSetting();
            return;
        }
        postAlertDelay(last2 + timeout2);
    }

    public void startHandleGetAppSetting() {
        Log.d(TAG, "startHandleGetAppSetting");
        new Thread(new Runnable() { // from class: com.tm.androidcopysdk.network.appSettings.WorkerIntentService.1
            @Override // java.lang.Runnable
            public void run() {
                WorkerIntentService.this.handleGetAppSetting(null);
                if (Build.VERSION.SDK_INT >= 21) {
                    WorkerIntentService.this.jobFinished(WorkerIntentService.this.params, false);
                }
            }
        }).start();
    }

    private Pair<Long, Long> getSuspendTimeout(long last, long timeout, long currentTime) {
        if (!CommonUtils.isActivatedUser(this)) {
            Log.d(TAG, "this log is to check that the currency of getAppSettings change in suspend user");
            last = PrefManager.getLongPref(this, "LastSettingsTimeSuspend", last);
            Log.d(TAG, "lastSuspend:" + last);
            long firstTimeSuspend = PrefManager.getLongPref(this, "LastSettingsTimeSuspend", 0L);
            if (firstTimeSuspend != 0 && currentTime - firstTimeSuspend >= 86400000) {
                Log.d(TAG, "currentTime - lastSuspend >  Definitions.MSECONDS_IN_DAY * 1 " + (currentTime - last > 86400000));
                timeout = Long.parseLong(AndroidCopySDK.getInstance(this).getSdkSettings().getSetting(SDKSettings.GET_APP_SETTINGS_INTERVAL_SUSPEND)) * 86400000;
                Log.d(TAG, "suspend timeout " + timeout);
            }
            Log.d(TAG, "timeoutSuspend: " + timeout);
            Log.d(TAG, "lastSuspend: " + last);
        }
        return new Pair<>(Long.valueOf(last), Long.valueOf(timeout));
    }

    public void setFirstSentTime(long firstSentTime) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putLong(FIRST_SENT_TIME, firstSentTime).commit();
    }

    public long getFirstSentTime() {
        return PreferenceManager.getDefaultSharedPreferences(this).getLong(FIRST_SENT_TIME, 0L);
    }

    public long getSentTime() {
        return PreferenceManager.getDefaultSharedPreferences(this).getLong(LAST_SENT_TIME, 0L);
    }

    public void getFcmTokenCallBack(ApplicationInterface applicationInterface, String token) {
        Log.d(TAG, "getFcmTokenCallBack");
        if (TextUtils.isEmpty(this.tmpU) && TextUtils.isEmpty(TMCredentialsStore.getInstance(this).userName(this))) {
            Log.d(TAG, "there no user name , waiting for signin");
            return;
        }
        final String userName = !TextUtils.isEmpty(this.tmpU) ? this.tmpU : TMCredentialsStore.getInstance(this).userName(this);
        final String password = !TextUtils.isEmpty(this.tmpS) ? this.tmpS : TMCredentialsStore.getInstance(this).password(this);
        this.tmpU = null;
        this.tmpS = null;
        new Thread(new Runnable() { // from class: com.tm.androidcopysdk.network.appSettings.WorkerIntentService.2
            @Override // java.lang.Runnable
            public void run() {
                TMEnsureIPManager.getInstance().postEnsure(WorkerIntentService.this, userName, password);
            }
        }).start();
    }

    @RequiresApi(api = 21)
    public static void cancelJob(Context mContext) {
        JobScheduler scheduler = (JobScheduler) mContext.getSystemService("jobscheduler");
        scheduler.cancel(DefinitionsSDKKt.WORKER_INTENT_SERVICE_JOB_ID);
        Log.i(TAG, "Cancelled Job if pending with ID: 2002");
    }
}
