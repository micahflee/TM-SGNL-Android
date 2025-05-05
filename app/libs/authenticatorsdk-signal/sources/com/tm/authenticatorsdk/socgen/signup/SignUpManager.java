package com.tm.authenticatorsdk.socgen.signup;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import androidx.core.app.ActivityCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.tm.IHeaderInterceptor;
import com.tm.authenticatorsdk.socgen.AuthenticatorAPI;
import com.tm.authenticatorsdk.socgen.MessageEvent;
import com.tm.authenticatorsdk.socgen.RetrieveCredentials.RetrieveCredentialsReq;
import com.tm.authenticatorsdk.socgen.RetrieveCredentials.UserResponse;
import com.tm.authenticatorsdk.socgen.UI.SelfSignupActivity;
import com.tm.authenticatorsdk.socgen.signup.CryptoUtil;
import com.tm.logger.Log;
import com.tm.utils.ApplicationInterface;
import com.tm.utils.Definitions;
import com.tm.utils.FcmUtil;
import com.tm.utils.Util;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/signup/SignUpManager.class */
public class SignUpManager implements FcmUtil.FcmTokenCallBack {
    private static final String PRODUCT_ID = "16";
    public static final String INITIAL_DELAY_SHAREDPREFERENCE_KEY = "INITIAL_DELAY_SHAREDPREFERENCE_KEY";
    private static final String START_DATE = "START_DATE";
    private static final String TAG = "SignUpUserWithManager";
    Map<String, String> signUpmdmSettings;
    public AuthenticatorAPI TMAuthenticatorAPI;
    private CryptoUtil.SecretKeyMac secretKeyMac;
    private Context context;
    private Application application;
    private StopSelfSignupServiceCallBack stopSelfSignupServiceCallBack;
    private boolean isSignupResponseDone = false;
    private boolean isPinPushDone = false;
    private String pinCode = null;
    private Runnable delayRunnable = new Runnable() { // from class: com.tm.authenticatorsdk.socgen.signup.SignUpManager.1
        @Override // java.lang.Runnable
        public void run() {
            SignUpManager.this.rescheduleSelfSignupWorker();
        }
    };
    private Handler deleayHandler = new Handler();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/signup/SignUpManager$StopSelfSignupServiceCallBack.class */
    public interface StopSelfSignupServiceCallBack {
        void stopService();
    }

    public SignUpManager(Application application, Map<String, String> mdmSettings, StopSelfSignupServiceCallBack stopSelfSignupServiceCallBack) {
        this.signUpmdmSettings = mdmSettings;
        this.context = application.getApplicationContext();
        this.application = application;
        this.stopSelfSignupServiceCallBack = stopSelfSignupServiceCallBack;
    }

    public void getFcmToken() {
        Log.d(TAG, "getFcmToken requested");
        FcmUtil.getFcmToken(this.application, this, this.context);
        startTimeOut();
    }

    private void startTimeOut() {
        Log.d(TAG, "startTimeOut ");
        this.deleayHandler.postDelayed(this.delayRunnable, 45000L);
    }

    private void cancelTimeOut() {
        Log.d(TAG, "cancelTimeOut ");
        this.deleayHandler.removeCallbacks(this.delayRunnable);
    }

    private void createRetrofit(ApplicationInterface applicationInterface, String token) {
        Log.d(TAG, "createRetrofit ");
        this.secretKeyMac = CryptoUtil.generateSecretKeyMacSha512(this.signUpmdmSettings.get("SecretKey").getBytes());
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        String requestedDate = gatDateFormatted();
        IHeaderInterceptor headerInterceptor = applicationInterface.networkProvider().headersInterceptor();
        headerInterceptor.setDate(requestedDate);
        headerInterceptor.setUid(this.signUpmdmSettings.get("Uid"));
        String signature = getSignature(this.application, this.signUpmdmSettings.get("ManagerName"), this.signUpmdmSettings.get("Email"), PRODUCT_ID, this.signUpmdmSettings.get("Uid"), token, requestedDate);
        headerInterceptor.setSignature(signature);
        headerInterceptor.setToken(token);
        this.TMAuthenticatorAPI = (AuthenticatorAPI) applicationInterface.networkProvider().service(Definitions.getBase(), AuthenticatorAPI.class);
    }

    public boolean signUpRequest(Context context) {
        Log.d(TAG, "signUpRequest");
        String countryString = getCountryCode(context);
        SignupRequest req = new SignupRequest();
        savePhoneNumber(this.signUpmdmSettings.get("Email"));
        UserFields userType = new UserFields();
        UserFields userName = new UserFields();
        UserFields app = new UserFields();
        UserFields managerName = new UserFields();
        UserFields country = new UserFields();
        userType.setName("USER_TYPE");
        userType.setValues("911");
        userName.setName("USERNAME");
        String correctUserNameFromEmail = this.signUpmdmSettings.get("Email").replace("@", "_");
        userName.setValues(correctUserNameFromEmail.replace(".", "_"));
        app.setName("APP");
        app.setValues("600701");
        managerName.setName("MANAGER_NAME");
        managerName.setValues(this.signUpmdmSettings.get("ManagerName"));
        country.setName("COUNTRY");
        country.setValues(countryString);
        UserFields[] fields = {userType, userName, app, managerName, country};
        req.setFields(fields);
        ArrayList<UserFields[]> field = new ArrayList<>(1);
        field.add(fields);
        return sendSignUpUserWithManager(field);
    }

    private boolean sendSignUpUserWithManager(ArrayList<UserFields[]> field) {
        Log.d(TAG, "sendSignUpUserWithManager");
        startTimeOut();
        this.TMAuthenticatorAPI.SignUpUserWithManager(field).enqueue(new Callback<List<SignupResponse>>() { // from class: com.tm.authenticatorsdk.socgen.signup.SignUpManager.2
            public void onResponse(Call<List<SignupResponse>> call, Response<List<SignupResponse>> response) {
                if (response.body() == null) {
                    SignUpManager.this.rescheduleSelfSignupWorker();
                    return;
                }
                Log.d(SignUpManager.TAG, "getResultCode: " + ((SignupResponse) ((List) response.body()).get(0)).getResultCode() + ", chosenUrl:");
                if (((SignupResponse) ((List) response.body()).get(0)).getResultCode() != 0 && ((SignupResponse) ((List) response.body()).get(0)).getResultCode() != 21 && ((SignupResponse) ((List) response.body()).get(0)).getResultCode() != 22 && ((SignupResponse) ((List) response.body()).get(0)).getResultCode() != 23) {
                    SignUpManager.this.rescheduleSelfSignupWorker();
                    Log.d(SignUpManager.TAG, "getResultCode() == : " + ((SignupResponse) ((List) response.body()).get(0)).getResultCode());
                    int result = Integer.valueOf(((SignupResponse) ((List) response.body()).get(0)).getResultCode()).intValue();
                    EventBus.getDefault().post(new MessageEvent(result, ((SignupResponse) ((List) response.body()).get(0)).getResultDescription()));
                    return;
                }
                Log.d(SignUpManager.TAG, "getResultCode() == : " + ((SignupResponse) ((List) response.body()).get(0)).getResultCode());
                SignUpManager.this.onEvent(new SelfSignUpEvent(null, false));
            }

            public void onFailure(Call<List<SignupResponse>> call, Throwable t) {
                Log.d(SignUpManager.TAG, "onFailure(), t == : " + t.toString());
                SignUpManager.this.rescheduleSelfSignupWorker();
            }
        });
        return false;
    }

    private String gatDateFormatted() {
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        String formattedDate = new SimpleDateFormat("EEE, dd LLL yyyy HH:mm:ss Z", Locale.ENGLISH).format(date);
        return formattedDate;
    }

    String getSignature(Application application, String managerUserName, String userEmail, String productId, String deviceUid, String pushNotificationToken, String requestDate) {
        String message = String.format("%s%s%s%s%s%s", managerUserName, userEmail, productId, deviceUid, pushNotificationToken, requestDate);
        return this.secretKeyMac.generateHexDigest(application, message);
    }

    public String getCountryCode(Context context) {
        String countryCode = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        try {
            countryCode = tm.getSimCountryIso();
        } catch (Exception e) {
            Log.d(TAG, "exception", e);
        }
        return countryCode;
    }

    public String getMobileNumberFromSim(Context context) {
        String mobileNumber = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        if ((ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_NUMBERS") == 0 || ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") == 0) && tm != null) {
            try {
                mobileNumber = tm.getLine1Number();
            } catch (Exception e) {
                Log.d(TAG, "########", e);
            }
        }
        return mobileNumber;
    }

    public void getFcmTokenCallBack(ApplicationInterface applicationInterface, String token) {
        Log.d(TAG, "getFcmTokenCallBack ");
        cancelTimeOut();
        createRetrofit(applicationInterface, token);
        signUpRequest(this.context);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SelfSignUpEvent event) {
        if (event.isFromPush()) {
            Log.d(TAG, "SelfSignUpEvent FromPush()");
            this.pinCode = event.getPin();
            this.isPinPushDone = true;
        } else {
            Log.d(TAG, "SelfSignUpEvent Not FromPush()");
            this.isSignupResponseDone = true;
        }
        if (this.isSignupResponseDone && this.isPinPushDone) {
            Log.d(TAG, "SelfSignUpEvent isSignupResponseDone && isPinPushDone true");
            this.isSignupResponseDone = false;
            this.isPinPushDone = false;
            cancelTimeOut();
            doRetreiveCredentialsRequest(this.pinCode);
        }
    }

    private void doRetreiveCredentialsRequest(String pinCode) {
        Log.d(TAG, "doRetreiveCredentialsRequest");
        RetrieveCredentialsReq cred = new RetrieveCredentialsReq();
        cred.setOneTimePIN(pinCode);
        cred.setRetrieveMode(1);
        cred.setSearchBy(1);
        cred.setValue(this.signUpmdmSettings.get("Email"));
        Object[] re = {cred.getValue(), Integer.valueOf(cred.getSearchBy()), Integer.valueOf(cred.getRetrieveMode()), cred.getOneTimePIN()};
        this.TMAuthenticatorAPI.RetrieveCredentials(re).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<UserResponse>>() { // from class: com.tm.authenticatorsdk.socgen.signup.SignUpManager.3
            public void onCompleted() {
                Log.d("activation", "done");
                SignUpManager.this.stopSelfSignupServiceCallBack.stopService();
            }

            public void onError(Throwable e) {
                Log.d("signup", e.getMessage());
                EventBus.getDefault().post(new MessageEvent(-1, e.getMessage()));
                SignUpManager.this.rescheduleSelfSignupWorker();
            }

            public void onNext(List<UserResponse> userResponses) {
                Log.d("signup", userResponses.get(0).toString());
                if (userResponses.get(0).getResultCode() == 0) {
                    Util.handleAppSettings((Application) SignUpManager.this.context.getApplicationContext(), userResponses.get(0).getFields()[1].getValue(), userResponses.get(0).getFields()[0].getValue());
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SignUpManager.this.context).edit();
                    editor.putInt(SignUpManager.INITIAL_DELAY_SHAREDPREFERENCE_KEY, 0).apply();
                    Util.handleKeepAlive((Application) SignUpManager.this.context.getApplicationContext());
                    return;
                }
                SignUpManager.this.rescheduleSelfSignupWorker();
                int resultCode = Integer.valueOf(userResponses.get(0).getResultCode()).intValue();
                EventBus.getDefault().post(new MessageEvent(resultCode, userResponses.get(0).getResultDescription()));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void rescheduleSelfSignupWorker() {
        Log.d(TAG, "rescheduleSelfSignupWorker");
        int initialDelay = getInitialDelay();
        this.isSignupResponseDone = false;
        this.isPinPushDone = false;
        cancelTimeOut();
        if (initialDelay <= 10) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.context).edit();
            editor.putLong(START_DATE, System.currentTimeMillis()).apply();
        }
        long startDate = PreferenceManager.getDefaultSharedPreferences(this.context).getLong(START_DATE, System.currentTimeMillis());
        long now = System.currentTimeMillis();
        if (now - startDate < TimeUnit.DAYS.toMillis(60L)) {
            Log.d(TAG, "startSelfSignupWorker less then 60 days");
            startSelfSignupWorker(initialDelay);
        }
        this.stopSelfSignupServiceCallBack.stopService();
    }

    public static void startSelfSignupWorker(int initialDelay) {
        Log.d(TAG, "startSelfSignupWorker");
        WorkManager.getInstance().cancelAllWorkByTag(SelfSignupActivity.SELF_SIGNUP_WORKER_TAG);
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest selfSignupWorkRequest = new OneTimeWorkRequest.Builder(SelfSignupWorker.class).setConstraints(constraints).setInitialDelay(initialDelay, TimeUnit.SECONDS).addTag(SelfSignupActivity.SELF_SIGNUP_WORKER_TAG).build();
        WorkManager.getInstance().enqueue(selfSignupWorkRequest);
    }

    private int getInitialDelay() {
        int initialDelay = PreferenceManager.getDefaultSharedPreferences(this.context).getInt(INITIAL_DELAY_SHAREDPREFERENCE_KEY, 3);
        if (initialDelay < 3600) {
            int newInitialDelay = initialDelay == 0 ? 3 : initialDelay * 2;
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.context).edit();
            editor.putInt(INITIAL_DELAY_SHAREDPREFERENCE_KEY, newInitialDelay < 3600 ? newInitialDelay : 3600).apply();
        }
        Log.d(TAG, "getInitialDelay " + Integer.toString(initialDelay));
        return initialDelay;
    }

    private void savePhoneNumber(String phoneNumber) {
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this.context).edit();
        ed.putString("phonenumber", phoneNumber);
        ed.commit();
    }
}
