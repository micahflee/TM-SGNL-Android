package com.tm.androidcopysdk;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.tm.INetworkProvider;
import com.tm.androidcopysdk.BackupService;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.Models.SignatureInfo;
import com.tm.androidcopysdk.database.SQLFactory;
import com.tm.androidcopysdk.device.SdkMigrator;
import com.tm.androidcopysdk.network.AuthenticationDetails;
import com.tm.androidcopysdk.network.GetAuthenticationToken;
import com.tm.androidcopysdk.network.NetworkConstance;
import com.tm.androidcopysdk.network.NetworkManager;
import com.tm.androidcopysdk.network.ReportLogs;
import com.tm.androidcopysdk.network.SSLFactoryUtils;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import com.tm.logger.Log;
import com.tm.utils.Util;
import java.io.IOException;
import java.util.TimeZone;
import okhttp3.ResponseBody;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Response;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/AndroidCopySDK.class */
public class AndroidCopySDK implements Application.ActivityLifecycleCallbacks {
    Context mContext;
    StatusListener clientStatusListener;
    AndroidCopySettings mSettings;
    SDKSettings sdkSettings;
    BackupService mBackupService;
    static AndroidCopySDK mInstance;
    boolean mBound = false;
    private ServiceConnection mConnection = new ServiceConnection() { // from class: com.tm.androidcopysdk.AndroidCopySDK.3
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName className, IBinder service) {
            BackupService.LocalBinder binder = (BackupService.LocalBinder) service;
            AndroidCopySDK.this.mBackupService = binder.getService();
            AndroidCopySDK.this.mBound = true;
            AndroidCopySDK.this.mBackupService.init();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName arg0) {
            AndroidCopySDK.this.mBound = false;
            AndroidCopySDK.this.mBackupService.unbindService(this);
            EventBus.getDefault().unregister(this);
        }
    };

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/AndroidCopySDK$Status.class */
    public enum Status {
        idle,
        synching,
        starting,
        fail,
        retry,
        unknown
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
    }

    public static synchronized AndroidCopySDK getInstance(Context context) {
        if (mInstance == null) {
            SdkMigrator.getInstance(context.getApplicationContext()).migrate();
            mInstance = new AndroidCopySDK();
            mInstance.mContext = context.getApplicationContext();
            mInstance.registerShutDown();
            mInstance.sdkSettings = new SDKSettings(context.getApplicationContext());
        }
        return mInstance;
    }

    public void registerShutDown() {
        Log.d("lior", "registerShutDown ");
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.ACTION_SHUTDOWN");
        filter.addAction("android.intent.action.QUICKBOOT_POWEROFF");
        this.mContext.registerReceiver(new BroadcastReceiver() { // from class: com.tm.androidcopysdk.AndroidCopySDK.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                Log.d("lior", "ShutdownReceiver <><><><>::  " + intent.getAction());
                PrefManager.setBooleanPref(context, "last_startup", false);
            }
        }, filter);
    }

    public void updateSettings(Context context) {
        mInstance.sdkSettings = new SDKSettings(context);
    }

    private boolean check(String username, String password) {
        return (username == null || password == null || password.isEmpty() || username.isEmpty()) ? false : true;
    }

    public void activityResume() {
        ((Application) this.mContext).registerActivityLifecycleCallbacks(this);
        CommonUtils.startBackupService(this.mContext);
        if (!this.mBound) {
            Intent intent = new Intent(this.mContext, BackupService.class);
            this.mContext.bindService(intent, this.mConnection, 1);
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void signupSucess(String username, String password) {
        MessageType[] values;
        if (FlavorSettings.getInstance().isAppServerNeedAuthorization() && !check(username, password)) {
            return;
        }
        Log.d("androidcopysdk", "init");
        String oldP = TMCredentialsStore.getInstance(this.mContext).password(this.mContext);
        String oldN = TMCredentialsStore.getInstance(this.mContext).userName(this.mContext);
        if (TextUtils.isEmpty(oldN) && TextUtils.isEmpty(oldP)) {
            TMCredentialsStore.getInstance(this.mContext).setUserName(this.mContext, username);
            TMCredentialsStore.getInstance(this.mContext).setPassword(this.mContext, password);
            SQLFactory.changeDataBasePassword(this.mContext);
            CommonUtils.startBackupService(this.mContext);
            INetworkProvider provider = this.mContext.getApplicationContext().networkProvider();
            provider.headersInterceptor().setAuthentication(TMCredentialsStore.getInstance(this.mContext).userName(this.mContext), TMCredentialsStore.getInstance(this.mContext).password(this.mContext));
        } else if (!oldN.equalsIgnoreCase(username) || !oldP.equalsIgnoreCase(password)) {
            UpgradeVersionIntentService.encryptAllFiles(this.mContext, false);
            TMCredentialsStore.getInstance(this.mContext).setUserName(this.mContext, username);
            TMCredentialsStore.getInstance(this.mContext).setPassword(this.mContext, password);
            SQLFactory.changeDataBasePassword(this.mContext);
            UpgradeVersionIntentService.encryptAllFiles(this.mContext, true);
            if (!oldN.equalsIgnoreCase(username) || !Util.isSocgenVersion((Application) this.mContext.getApplicationContext())) {
                CommonUtils.setCountArchivedMessages(this.mContext, MessageType.SMS, 0);
                CommonUtils.setCountArchivedMessages(this.mContext, MessageType.MMS, 0);
                CommonUtils.setCountArchivedMessages(this.mContext, MessageType.CallLog, 0);
                PreferenceManager.getDefaultSharedPreferences(this.mContext).edit().remove(PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY).commit();
                try {
                    PrefManager.removePref(this.mContext, PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY);
                } catch (Exception e) {
                    Log.e("androidcopysdk", "failed to remove pref !!!!!! !!!!! !!!!! !!!!!\n!!!!!! !!!!! \n!!!!!! !!!! !!!!! !!!!!!\n!!!!!! !!!!!! !!!!!", e);
                }
                for (MessageType type : MessageType.values()) {
                    PreferenceManager.getDefaultSharedPreferences(this.mContext).edit().putString(type.name() + DataGrabber.DATE_OF_MESSAGE, null).commit();
                }
                INetworkProvider provider2 = this.mContext.getApplicationContext().networkProvider();
                provider2.headersInterceptor().setAuthentication(TMCredentialsStore.getInstance(this.mContext).userName(this.mContext), TMCredentialsStore.getInstance(this.mContext).password(this.mContext));
            }
            GetMessagesService.setNumOfQueueIntentsMMS(0);
        }
        PrefManager.setStringPref(this.mContext, "username", "");
        PrefManager.setStringPref(this.mContext, "password", "");
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !CommonUtils.appIsWorking(this.mContext)) {
            PrefManager.setBooleanPref(this.mContext, PrefManagerConstants.SHARED_PREFERENCE_ACTIVATED_AA_KEY, true);
            Log.d("AndroidCopySDK", "ACTIVATED_AA_KEY is true now. archive is on");
            CommonUtils.appIsWorking(this.mContext);
        }
        if (FlavorSettings.getInstance().isNeedToUpdateActivityAfterUserCredentialsUpdated()) {
            activityResume();
        }
    }

    public void changeInstallationTimeToGCM() {
        long installationTime = PrefManager.getLongPref(this.mContext, PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, 0L);
        if (installationTime == 0) {
            PrefManager.setLongPref(this.mContext, PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, convertUTCTimestampToDeviceLocalTime(System.currentTimeMillis()));
        } else {
            PrefManager.setLongPref(this.mContext, PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, convertUTCTimestampToDeviceLocalTime(installationTime));
        }
    }

    public long convertUTCTimestampToDeviceLocalTime(long msgDate) {
        int offset = TimeZone.getDefault().getRawOffset() + TimeZone.getDefault().getDSTSavings();
        return msgDate - offset;
    }

    public void savePhoneNumber(String phoneNumber) {
        savePhoneNumber(phoneNumber, null);
    }

    public void savePhoneNumber(String phoneNumber, @Nullable Runnable callback) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        SharedPreferences.Editor editor = preferences.edit();
        String existingPhoneNumber = preferences.getString("phonenumber", null);
        editor.putString("phonenumber", phoneNumber);
        editor.commit();
        boolean supportChangeNumber = FlavorSettings.getInstance().supportChangeNumber();
        if (supportChangeNumber && !TextUtils.isEmpty(existingPhoneNumber) && !existingPhoneNumber.equals(phoneNumber)) {
            onPhoneNumberChanged(existingPhoneNumber, phoneNumber, callback);
        } else if (callback != null) {
            callback.run();
        }
    }

    private void onPhoneNumberChanged(String oldNumber, String newNumber, @Nullable final Runnable callback) {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() { // from class: com.tm.androidcopysdk.AndroidCopySDK.2
            public void onComplete(@NonNull Task<Void> task) {
                if (callback != null) {
                    callback.run();
                }
            }
        });
    }

    public void setLogAuthenticationToken(String token) {
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        ed.putString(NetworkConstance.PREFERENCE_LOGS_AUTHENTICATION_KEY, token);
        ed.commit();
    }

    public String getLogAuthenticationToken() {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(NetworkConstance.PREFERENCE_LOGS_AUTHENTICATION_KEY, "");
    }

    public void prefromBackup() {
        if (this.mBound) {
            this.mBackupService.backupData();
        }
    }

    public void setPreference(AndroidCopySettings settings) {
        this.mSettings = settings;
        EventBus.getDefault().post(new SettingsEvent(settings));
    }

    public AndroidcopyInfo getInfo() {
        AndroidcopyInfo info = new AndroidcopyInfo();
        info.setTotalSMS(CommonUtils.getCountArchivedMessages(this.mContext, MessageType.SMS));
        info.setTotalMMS(CommonUtils.getCountArchivedMessages(this.mContext, MessageType.MMS));
        info.setTotalCallLog(CommonUtils.getCountArchivedMessages(this.mContext, MessageType.CallLog));
        return info;
    }

    public void regsiterForStatus(StatusListener listener) {
        this.clientStatusListener = listener;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        if (this.clientStatusListener != null) {
            this.clientStatusListener.onStatus(event.message, event.message);
        }
    }

    public void finalize() {
    }

    public SDKSettings getSdkSettings() {
        if (this.sdkSettings == null) {
            Log.d("AndroidCopySDK", "sdkSettings == null -> init again");
            this.sdkSettings = new SDKSettings(this.mContext);
        }
        return this.sdkSettings;
    }

    public AuthenticationDetails getAuthenticationDetails() {
        AuthenticationDetails authenticationDetails = new AuthenticationDetails();
        authenticationDetails.setUsername(TMCredentialsStore.getInstance(this.mContext).userName(this.mContext));
        authenticationDetails.setPassword(TMCredentialsStore.getInstance(this.mContext).password(this.mContext));
        return authenticationDetails;
    }

    public void sentLogs(final Activity activity, final ISendLogCallback aISendLogCallback, final String phoneNumber, final String emailSubject, final String userFieldName, final String freeText, final String firstName, final String lastName, final String email, final String authenticationUserName, final String pass) {
        new Thread(new Runnable() { // from class: com.tm.androidcopysdk.AndroidCopySDK.4
            @Override // java.lang.Runnable
            public void run() {
                if (!AndroidCopySDK.this.getLogAuthenticationToken().isEmpty()) {
                    NetworkManager nm = new NetworkManager(AndroidCopySDK.mInstance.mContext.getApplicationContext(), NetworkManager.LOGS_BASE_URL);
                    final Response<String> res = nm.start(new ReportLogs("Bearer " + AndroidCopySDK.this.getLogAuthenticationToken(), phoneNumber, emailSubject, userFieldName, freeText, firstName, lastName, email), null, AndroidCopySDK.mInstance.mContext, false);
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() { // from class: com.tm.androidcopysdk.AndroidCopySDK.4.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (res == null) {
                                    AndroidCopySDK.this.sendingLogsFailed(aISendLogCallback);
                                } else if (res.isSuccessful()) {
                                    if (!AndroidCopySDK.this.isInvalidToken((String) res.body())) {
                                        if (AndroidCopySDK.this.isResponseSuccess((String) res.body())) {
                                            if (aISendLogCallback != null) {
                                                aISendLogCallback.sendLogSucceed();
                                            }
                                            Toast.makeText(AndroidCopySDK.mInstance.mContext, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.LOG_REPORT_SENT : AndroidCopySDK.mInstance.mContext.getString(R.string.log_report_sent), 0).show();
                                            return;
                                        } else if (aISendLogCallback != null) {
                                            AndroidCopySDK.this.sendingLogsFailed(aISendLogCallback);
                                            return;
                                        } else {
                                            return;
                                        }
                                    }
                                    AndroidCopySDK.this.getAuthenticationToken(activity, aISendLogCallback, phoneNumber, emailSubject, userFieldName, freeText, firstName, lastName, email, authenticationUserName, pass);
                                } else if (aISendLogCallback != null) {
                                    AndroidCopySDK.this.sendingLogsFailed(aISendLogCallback);
                                }
                            }
                        });
                        return;
                    }
                    return;
                }
                AndroidCopySDK.this.getAuthenticationToken(activity, aISendLogCallback, phoneNumber, emailSubject, userFieldName, freeText, firstName, lastName, email, authenticationUserName, pass);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isResponseSuccess(String body) {
        return body.contains(NetworkConstance.LOGS_SENT_SUCCESS);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInvalidToken(String body) {
        return body.contains(NetworkConstance.LOGS_SENT_AUTHENTICATION_ERROR);
    }

    public void getAuthenticationToken(final Activity activity, final ISendLogCallback aISendLogCallback, final String phoneNumber, final String subject, final String userFieldName, final String freeText, final String firstName, final String lastName, final String email, final String user, final String pass) {
        new Thread(new Runnable() { // from class: com.tm.androidcopysdk.AndroidCopySDK.5
            @Override // java.lang.Runnable
            public void run() {
                NetworkManager nm = new NetworkManager(AndroidCopySDK.mInstance.mContext.getApplicationContext(), NetworkManager.LOGS_BASE_URL);
                final Response<ResponseBody> res = nm.start(new GetAuthenticationToken(user, pass), null, AndroidCopySDK.mInstance.mContext, false);
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() { // from class: com.tm.androidcopysdk.AndroidCopySDK.5.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (res == null) {
                                AndroidCopySDK.this.sendingLogsFailed(aISendLogCallback);
                            } else if (!res.isSuccessful()) {
                                AndroidCopySDK.this.sendingLogsFailed(aISendLogCallback);
                            } else {
                                Log.d("send log", "sendingLogsSucces");
                                try {
                                    if (res.body() != null) {
                                        String token = AndroidCopySDK.this.getTokenFromResponseBody(((ResponseBody) res.body()).string());
                                        if (token != null) {
                                            AndroidCopySDK.this.setLogAuthenticationToken(token);
                                            AndroidCopySDK.this.sentLogs(activity, aISendLogCallback, phoneNumber, subject, userFieldName, freeText, firstName, lastName, email, user, pass);
                                        }
                                    } else {
                                        AndroidCopySDK.this.sendingLogsFailed(aISendLogCallback);
                                    }
                                } catch (IOException e) {
                                    AndroidCopySDK.this.sendingLogsFailed(aISendLogCallback);
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendingLogsFailed(ISendLogCallback aISendLogCallback) {
        Log.d("send log", "sendingLogsFailed");
        if (aISendLogCallback != null) {
            aISendLogCallback.sendLogFailure();
        }
        Toast.makeText(mInstance.mContext, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.LOG_REPORT_NO_NETWORK : mInstance.mContext.getString(R.string.log_report_no_network), 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getTokenFromResponseBody(String body) {
        try {
            JSONObject jsonObj = new JSONObject(body);
            JSONObject jo = jsonObj.getJSONObject("data");
            if (jo != null) {
                return jo.getString("token");
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CommonUtils.RECORDING_TYPE getRecordingStatus() {
        return CommonUtils.getRecordingStatus(this.mContext);
    }

    public void initCertificatePinningApp(String... args) {
        SSLFactoryUtils.setCertArray(this.mContext, args);
    }

    public AndroidCopySettings getmSettings() {
        return this.mSettings;
    }

    public boolean isSupportAudioCalls() {
        return PrefManager.getBooleanPref(this.mContext, PrefManagerConstants.SHARED_PREFERENCE_IS_SUPPORT_AUDIO_CALLS, true);
    }

    public boolean isSupportVideoCalls() {
        return PrefManager.getBooleanPref(this.mContext, PrefManagerConstants.SHARED_PREFERENCE_IS_SUPPORT_VIDEO_CALLS, true);
    }

    public void setSignatureFromGetAppSettings(SignatureInfo signatureInfo) {
        Gson gson = new Gson();
        String signatureString = gson.toJson(signatureInfo, SignatureInfo.class);
        PrefManager.setStringPref(this.mContext, PrefManagerConstants.SHARED_PREFERENCE_SIGNATURE, signatureString);
    }

    public SignatureInfo getSignatureFromGetAppSettings() {
        String signatureString = PrefManager.getStringPref(this.mContext, PrefManagerConstants.SHARED_PREFERENCE_SIGNATURE, "");
        Gson gson = new Gson();
        if (signatureString.isEmpty()) {
            return null;
        }
        return (SignatureInfo) gson.fromJson(signatureString, SignatureInfo.class);
    }
}
