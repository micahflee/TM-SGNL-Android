package com.tm.authenticatorsdk.mamsdk;

import android.app.Activity;
import android.content.Context;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalIntuneAppProtectionPolicyRequiredException;
import com.microsoft.identity.client.exception.MsalUserCancelException;
import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.client.notification.MAMNotificationReceiverRegistry;
import com.microsoft.intune.mam.policy.MAMEnrollmentManager;
import com.microsoft.intune.mam.policy.appconfig.MAMAppConfig;
import com.microsoft.intune.mam.policy.appconfig.MAMAppConfigManager;
import com.microsoft.intune.mam.policy.notification.MAMEnrollmentNotification;
import com.microsoft.intune.mam.policy.notification.MAMNotification;
import com.microsoft.intune.mam.policy.notification.MAMNotificationType;
import com.mindorks.retrofit.coroutines.data.api.ApiHelper;
import com.mindorks.retrofit.coroutines.data.api.ApiService;
import com.tm.INetworkProvider;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import com.tm.authenticatorsdk.mamsdk.network.AuthMDMRepository;
import com.tm.authenticatorsdk.mamsdk.network.MDMAuthenticationAPI;
import com.tm.authenticatorsdk.mamsdk.network.RequestBody;
import com.tm.authenticatorsdk.selfAuthenticator.AuthenticatorConstants;
import com.tm.authenticatorsdk.selfAuthenticator.FCMUtils;
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus;
import com.tm.authenticatorsdk.selfAuthenticator.IFCMTokenArrived;
import com.tm.authenticatorsdk.selfAuthenticator.SelfAuthenticator;
import com.tm.authenticatorsdk.socgen.signup.CryptoUtil;
import com.tm.logger.Log;
import com.tm.utils.ApplicationInterface;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.Dispatchers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: MDMAuthenticator.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0006\bÆ\u0002\u0018��2\u00020\u0001:\u0001GB\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0016\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020-J\b\u0010.\u001a\u00020/H\u0002J\u0006\u00100\u001a\u00020\nJ@\u00101\u001a\u00020\n2\u0006\u00102\u001a\u00020\n2\u0006\u00103\u001a\u00020\n2\u0006\u00104\u001a\u00020\n2\u0006\u00105\u001a\u00020\n2\u0006\u00106\u001a\u00020\n2\u0006\u00107\u001a\u00020\n2\u0006\u00108\u001a\u00020\nH\u0002J\u000e\u00109\u001a\u00020/2\u0006\u0010:\u001a\u00020;J\u0018\u0010<\u001a\u00020)2\u0006\u0010*\u001a\u00020+2\u0006\u0010=\u001a\u00020\nH\u0002J\u0018\u0010>\u001a\u00020)2\u0006\u0010*\u001a\u00020+2\u0006\u00106\u001a\u00020\nH\u0016J\u0010\u0010?\u001a\u00020)2\u0006\u0010:\u001a\u00020;H\u0002J\u000e\u0010@\u001a\u00020)2\u0006\u0010A\u001a\u00020BJ&\u0010C\u001a\u00020)2\u0006\u0010A\u001a\u00020B2\u0006\u0010D\u001a\u00020\n2\u0006\u0010E\u001a\u00020\n2\u0006\u0010F\u001a\u00020\u000fR\u001c\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n��R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n��R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n��R\u001c\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0014\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n��R\u000e\u0010\u0015\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n��R\u000e\u0010\u0016\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n��R\u001c\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001a\u0010\u001d\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!R\u000e\u0010\"\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n��R\u000e\u0010#\u001a\u00020$X\u0082\u000e¢\u0006\u0002\n��R\u001a\u0010%\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b&\u0010\u001f\"\u0004\b'\u0010!¨\u0006H"}, d2 = {"Lcom/tm/authenticatorsdk/mamsdk/MDMAuthenticator;", "Lcom/tm/authenticatorsdk/selfAuthenticator/IFCMTokenArrived;", "()V", "appConfig", "Lcom/microsoft/intune/mam/policy/appconfig/MAMAppConfig;", "getAppConfig", "()Lcom/microsoft/intune/mam/policy/appconfig/MAMAppConfig;", "setAppConfig", "(Lcom/microsoft/intune/mam/policy/appconfig/MAMAppConfig;)V", "appVersion", "", "mAadId", "mEnrollmentManager", "Lcom/microsoft/intune/mam/policy/MAMEnrollmentManager;", "mIMDMAuthenticator", "Lcom/tm/authenticatorsdk/mamsdk/IMDMAuthenticator;", "getMIMDMAuthenticator", "()Lcom/tm/authenticatorsdk/mamsdk/IMDMAuthenticator;", "setMIMDMAuthenticator", "(Lcom/tm/authenticatorsdk/mamsdk/IMDMAuthenticator;)V", "mRequestDate", "mSecretKey", "mUpn", "mUserAccount", "Lcom/tm/authenticatorsdk/mamsdk/AppAccount;", "getMUserAccount", "()Lcom/tm/authenticatorsdk/mamsdk/AppAccount;", "setMUserAccount", "(Lcom/tm/authenticatorsdk/mamsdk/AppAccount;)V", MDMAuthenticatorKt.MANAGER_ID, "getManagerID", "()Ljava/lang/String;", "setManagerID", "(Ljava/lang/String;)V", "mobile", "requestBody", "Lcom/tm/authenticatorsdk/mamsdk/network/RequestBody;", "upn", "getUpn", "setUpn", "continueIntuneSelfAuthentication", "", "applicationInterface", "Lcom/tm/utils/ApplicationInterface;", "aIAuthenticationStatus", "Lcom/tm/authenticatorsdk/selfAuthenticator/IAuthenticationStatus;", "getAppConfigurationData", "", "getDateFormatted", "getSignature", MDMAuthenticatorKt.NAME, "email", "productID", "UID", "token", "requestDate", MDMAuthenticatorKt.SECRET_KEY, "isMDM", "context", "Landroid/content/Context;", "manageNetwork", "fcmToken", "onFCMTokenArrived", "prepareMAMComponents", "signOutUser", "activity", "Landroid/app/Activity;", "startMDMAuthenticator", "mobileNum", "version", "listener", "AuthCallback", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/MDMAuthenticator.class */
public final class MDMAuthenticator implements IFCMTokenArrived {
    @Nullable
    private static AppAccount mUserAccount;
    @Nullable
    private static IMDMAuthenticator mIMDMAuthenticator;
    @Nullable
    private static MAMEnrollmentManager mEnrollmentManager;
    @Nullable
    private static MAMAppConfig appConfig;
    @NotNull
    public static final MDMAuthenticator INSTANCE = new MDMAuthenticator();
    @NotNull
    private static RequestBody requestBody = new RequestBody(null, null, null, null, null, null, null, null, null, null, null, 2047, null);
    @NotNull
    private static String mSecretKey = "";
    @NotNull
    private static String mUpn = "";
    @NotNull
    private static String mAadId = "";
    @NotNull
    private static String mRequestDate = "";
    @NotNull
    private static String mobile = "";
    @NotNull
    private static String appVersion = "";
    @NotNull
    private static String upn = "";
    @NotNull
    private static String managerID = "";

    /* compiled from: MDMAuthenticator.kt */
    @Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 3, xi = 48)
    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/MDMAuthenticator$WhenMappings.class */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[MAMEnrollmentManager.Result.values().length];
            try {
                iArr[MAMEnrollmentManager.Result.AUTHORIZATION_NEEDED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[MAMEnrollmentManager.Result.NOT_LICENSED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[MAMEnrollmentManager.Result.ENROLLMENT_SUCCEEDED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[MAMEnrollmentManager.Result.ENROLLMENT_FAILED.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[MAMEnrollmentManager.Result.WRONG_USER.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[MAMEnrollmentManager.Result.UNENROLLMENT_SUCCEEDED.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[MAMEnrollmentManager.Result.UNENROLLMENT_FAILED.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[MAMEnrollmentManager.Result.PENDING.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[MAMEnrollmentManager.Result.COMPANY_PORTAL_REQUIRED.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    private MDMAuthenticator() {
    }

    @Nullable
    public final AppAccount getMUserAccount() {
        return mUserAccount;
    }

    public final void setMUserAccount(@Nullable AppAccount appAccount) {
        mUserAccount = appAccount;
    }

    @Nullable
    public final IMDMAuthenticator getMIMDMAuthenticator() {
        return mIMDMAuthenticator;
    }

    public final void setMIMDMAuthenticator(@Nullable IMDMAuthenticator iMDMAuthenticator) {
        mIMDMAuthenticator = iMDMAuthenticator;
    }

    @Nullable
    public final MAMAppConfig getAppConfig() {
        return appConfig;
    }

    public final void setAppConfig(@Nullable MAMAppConfig mAMAppConfig) {
        appConfig = mAMAppConfig;
    }

    @NotNull
    public final String getUpn() {
        return upn;
    }

    public final void setUpn(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        upn = str;
    }

    @NotNull
    public final String getManagerID() {
        return managerID;
    }

    public final void setManagerID(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        managerID = str;
    }

    private final void prepareMAMComponents(Context context) {
        MAMEnrollmentManager mgr = (MAMEnrollmentManager) MAMComponents.get(MAMEnrollmentManager.class);
        Intrinsics.checkNotNull(mgr);
        mgr.registerAuthenticationCallback(new AuthenticationCallback(context));
        Object obj = MAMComponents.get(MAMNotificationReceiverRegistry.class);
        Intrinsics.checkNotNull(obj);
        ((MAMNotificationReceiverRegistry) obj).registerReceiver(MDMAuthenticator::prepareMAMComponents$lambda$0, MAMNotificationType.MAM_ENROLLMENT_RESULT);
        mEnrollmentManager = (MAMEnrollmentManager) MAMComponents.get(MAMEnrollmentManager.class);
    }

    private static final boolean prepareMAMComponents$lambda$0(MAMNotification notification) {
        if (notification instanceof MAMEnrollmentNotification) {
            MAMEnrollmentManager.Result result = ((MAMEnrollmentNotification) notification).getEnrollmentResult();
            switch (result == null ? -1 : WhenMappings.$EnumSwitchMapping$0[result.ordinal()]) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case AppSignatureHelper.NUM_HASHED_BYTES /* 9 */:
                    Log.d("Enrollment Receiver", result.name());
                    return true;
                default:
                    Log.d("Enrollment Receiver", result.name());
                    return true;
            }
        }
        Log.d("Enrollment Receiver", "Unexpected notification type received");
        return true;
    }

    public final void startMDMAuthenticator(@NotNull Activity activity, @NotNull String mobileNum, @NotNull String version, @NotNull IMDMAuthenticator listener) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Intrinsics.checkNotNullParameter(mobileNum, "mobileNum");
        Intrinsics.checkNotNullParameter(version, "version");
        Intrinsics.checkNotNullParameter(listener, "listener");
        mobile = StringsKt.removePrefix(mobileNum, "+");
        appVersion = version;
        Log.d(MDMAuthenticatorKt.TAG, "appVersion: " + appVersion);
        mIMDMAuthenticator = listener;
        prepareMAMComponents(activity);
        if (isMDM(activity)) {
            new Thread(() -> {
                startMDMAuthenticator$lambda$1(r2);
            }).start();
            return;
        }
        IMDMAuthenticator iMDMAuthenticator = mIMDMAuthenticator;
        Intrinsics.checkNotNull(iMDMAuthenticator);
        iMDMAuthenticator.failureMDMAuth("not mdm");
    }

    private static final void startMDMAuthenticator$lambda$1(Activity $activity) {
        Intrinsics.checkNotNullParameter($activity, "$activity");
        Log.d(MDMAuthenticatorKt.TAG, "Starting interactive auth");
        try {
            String loginHint = null;
            MDMAuthenticator mDMAuthenticator = INSTANCE;
            if (mUserAccount != null) {
                MDMAuthenticator mDMAuthenticator2 = INSTANCE;
                AppAccount appAccount = mUserAccount;
                Intrinsics.checkNotNull(appAccount);
                loginHint = appAccount.getUPN();
            }
            MSALUtil.acquireToken($activity, MDMAuthenticatorKt.getMSAL_SCOPES(), loginHint, new AuthCallback());
        } catch (InterruptedException e) {
            Log.d(MDMAuthenticatorKt.TAG, MDMAuthenticatorKt.err_auth + e);
        } catch (MsalException e2) {
            Log.d(MDMAuthenticatorKt.TAG, MDMAuthenticatorKt.err_auth + e2);
        }
    }

    public final boolean isMDM(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        Log.d(MDMAuthenticatorKt.TAG, "start isMDM");
        return false;
    }

    /* compiled from: MDMAuthenticator.kt */
    @Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\u0018��2\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0016J\u0012\u0010\u0005\u001a\u00020\u00042\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J\u0012\u0010\b\u001a\u00020\u00042\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016¨\u0006\u000b"}, d2 = {"Lcom/tm/authenticatorsdk/mamsdk/MDMAuthenticator$AuthCallback;", "Lcom/microsoft/identity/client/AuthenticationCallback;", "()V", "onCancel", "", "onError", "exception", "Lcom/microsoft/identity/client/exception/MsalException;", "onSuccess", "authenticationResult", "Lcom/microsoft/identity/client/IAuthenticationResult;", "authenticatorsdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/MDMAuthenticator$AuthCallback.class */
    public static final class AuthCallback implements com.microsoft.identity.client.AuthenticationCallback {
        public void onSuccess(@Nullable IAuthenticationResult authenticationResult) {
            Log.d(MDMAuthenticatorKt.TAG, "AuthenticationCallback -> onSuccess");
            Intrinsics.checkNotNull(authenticationResult);
            IAccount account = authenticationResult.getAccount();
            Intrinsics.checkNotNullExpressionValue(account, "getAccount(...)");
            Intrinsics.checkNotNullExpressionValue(authenticationResult.getScope(), "getScope(...)");
            MDMAuthenticator mDMAuthenticator = MDMAuthenticator.INSTANCE;
            String username = account.getUsername();
            Intrinsics.checkNotNullExpressionValue(username, "getUsername(...)");
            mDMAuthenticator.setUpn(username);
            String aadId = account.getId();
            Intrinsics.checkNotNullExpressionValue(aadId, "getId(...)");
            String tenantId = account.getTenantId();
            Intrinsics.checkNotNullExpressionValue(tenantId, "getTenantId(...)");
            String authorityURL = account.getAuthority();
            Intrinsics.checkNotNullExpressionValue(authorityURL, "getAuthority(...)");
            Map claims = account.getClaims();
            String message = "Authentication succeeded for user " + MDMAuthenticator.INSTANCE.getUpn();
            Log.d(MDMAuthenticatorKt.TAG, message);
            Log.d(MDMAuthenticatorKt.TAG, "aadId: " + aadId);
            Log.d(MDMAuthenticatorKt.TAG, "tenantId: " + tenantId);
            Log.d(MDMAuthenticatorKt.TAG, "authorityURL: " + authorityURL);
            Log.d(MDMAuthenticatorKt.TAG, "claims: " + claims);
            MDMAuthenticator mDMAuthenticator2 = MDMAuthenticator.INSTANCE;
            String upn = MDMAuthenticator.INSTANCE.getUpn();
            Intrinsics.checkNotNull(claims);
            mDMAuthenticator2.setMUserAccount(new AppAccount(upn, aadId, tenantId, authorityURL, claims));
            Log.d(MDMAuthenticatorKt.TAG, "mUserAccount: " + MDMAuthenticator.INSTANCE.getMUserAccount());
            Log.d(MDMAuthenticatorKt.TAG, "mEnrollmentManager: " + MDMAuthenticator.mEnrollmentManager);
            try {
                MAMEnrollmentManager mAMEnrollmentManager = MDMAuthenticator.mEnrollmentManager;
                Intrinsics.checkNotNull(mAMEnrollmentManager);
                mAMEnrollmentManager.registerAccountForMAM(MDMAuthenticator.INSTANCE.getUpn(), aadId, tenantId, authorityURL);
            } catch (Exception e) {
                Log.d(MDMAuthenticatorKt.TAG, "mEnrollmentManager.registerAccountForMAM Exception: " + e);
            }
            MDMAuthenticator mDMAuthenticator3 = MDMAuthenticator.INSTANCE;
            MDMAuthenticator.mUpn = MDMAuthenticator.INSTANCE.getUpn();
            MDMAuthenticator mDMAuthenticator4 = MDMAuthenticator.INSTANCE;
            MDMAuthenticator.mAadId = aadId;
            MDMAuthenticator mDMAuthenticator5 = MDMAuthenticator.INSTANCE;
            MDMAuthenticator.mRequestDate = MDMAuthenticator.INSTANCE.getDateFormatted();
            MDMAuthenticator mDMAuthenticator6 = MDMAuthenticator.INSTANCE;
            MDMAuthenticator.requestBody = new RequestBody(null, MDMAuthenticator.INSTANCE.getUpn(), MDMAuthenticator.mobile, aadId, MDMAuthenticator.mRequestDate, null, MDMAuthenticatorKt.DEVICE_TYPE, null, null, MDMAuthenticatorKt.APP_ID, MDMAuthenticator.appVersion, 417, null);
            IMDMAuthenticator mIMDMAuthenticator = MDMAuthenticator.INSTANCE.getMIMDMAuthenticator();
            Intrinsics.checkNotNull(mIMDMAuthenticator);
            mIMDMAuthenticator.successMDMAuth();
        }

        public void onError(@Nullable MsalException exception) {
            Log.d(MDMAuthenticatorKt.TAG, "authentication failed " + exception);
            if (exception instanceof MsalIntuneAppProtectionPolicyRequiredException) {
                Intrinsics.checkNotNull(exception, "null cannot be cast to non-null type com.microsoft.identity.client.exception.MsalIntuneAppProtectionPolicyRequiredException");
                MsalIntuneAppProtectionPolicyRequiredException appException = (MsalIntuneAppProtectionPolicyRequiredException) exception;
                String upn = appException.getAccountUpn();
                String aadid = appException.getAccountUserId();
                String tenantId = appException.getTenantId();
                String authorityURL = appException.getAuthorityUrl();
                MDMAuthenticator.INSTANCE.setMUserAccount(new AppAccount(upn, aadid, tenantId, authorityURL, MapsKt.emptyMap()));
                Log.d(MDMAuthenticatorKt.TAG, "MsalIntuneAppProtectionPolicyRequiredException received.");
                StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
                Object[] objArr = {upn, aadid, tenantId, authorityURL};
                String format = String.format("Data from broker: UPN: %s; AAD ID: %s; Tenant ID: %s; Authority: %s", Arrays.copyOf(objArr, objArr.length));
                Intrinsics.checkNotNullExpressionValue(format, "format(...)");
                Log.d(MDMAuthenticatorKt.TAG, format);
            } else if (exception instanceof MsalUserCancelException) {
                Log.d(MDMAuthenticatorKt.TAG, "User cancelled sign-in request");
            } else {
                Log.d(MDMAuthenticatorKt.TAG, "Exception occurred - check logcat");
            }
            IMDMAuthenticator mIMDMAuthenticator = MDMAuthenticator.INSTANCE.getMIMDMAuthenticator();
            Intrinsics.checkNotNull(mIMDMAuthenticator);
            mIMDMAuthenticator.failureMDMAuth("microsoft error: " + exception);
        }

        public void onCancel() {
            Log.d(MDMAuthenticatorKt.TAG, "User cancelled auth attempt");
            IMDMAuthenticator mIMDMAuthenticator = MDMAuthenticator.INSTANCE.getMIMDMAuthenticator();
            Intrinsics.checkNotNull(mIMDMAuthenticator);
            mIMDMAuthenticator.failureMDMAuth("onCancel");
        }
    }

    private final boolean getAppConfigurationData() {
        Log.d(MDMAuthenticatorKt.TAG, "getAppConfigurationData start");
        MAMAppConfigManager configManager = (MAMAppConfigManager) MAMComponents.get(MAMAppConfigManager.class);
        Intrinsics.checkNotNull(configManager);
        appConfig = configManager.getAppConfig(upn);
        MAMAppConfig mAMAppConfig = appConfig;
        Intrinsics.checkNotNull(mAMAppConfig);
        List list = mAMAppConfig.getFullData();
        MAMAppConfig mAMAppConfig2 = appConfig;
        Intrinsics.checkNotNull(mAMAppConfig2);
        managerID = String.valueOf(mAMAppConfig2.getStringForKey(MDMAuthenticatorKt.MANAGER_ID, MAMAppConfig.StringQueryType.Any));
        MAMAppConfig mAMAppConfig3 = appConfig;
        Intrinsics.checkNotNull(mAMAppConfig3);
        mSecretKey = String.valueOf(mAMAppConfig3.getStringForKey(MDMAuthenticatorKt.SECRET_KEY, MAMAppConfig.StringQueryType.Any));
        Log.d(MDMAuthenticatorKt.TAG, "-------------list.size(): " + list.size() + " managerID: " + managerID + "\n mSecretKey: " + mSecretKey + "  --------------");
        if (!mSecretKey.equals(null) && !Intrinsics.areEqual(mSecretKey, "null")) {
            if (!(mSecretKey.length() == 0)) {
                return true;
            }
        }
        Log.d(MDMAuthenticatorKt.TAG, "mSecretKey null. should stop auth and continue signal flow");
        Log.d(MDMAuthenticatorKt.TAG, "mSecretKey.isEmpty() so we add hardcoded. mSecretKey is " + mSecretKey);
        IMDMAuthenticator iMDMAuthenticator = mIMDMAuthenticator;
        Intrinsics.checkNotNull(iMDMAuthenticator);
        iMDMAuthenticator.failureMDMAuth("managerID is empty");
        return false;
    }

    private final void manageNetwork(ApplicationInterface applicationInterface, String fcmToken) {
        Log.d(MDMAuthenticatorKt.TAG, "manageNetwork start");
        String mBaseUrl = AuthenticatorConstants.Companion.getBASE_URL_MDM();
        INetworkProvider networkProvider = applicationInterface.networkProvider();
        AuthMDMRepository authMDMRepository = new AuthMDMRepository((MDMAuthenticationAPI) networkProvider.service(mBaseUrl, MDMAuthenticationAPI.class));
        requestBody.setAppIdentifier(fcmToken);
        requestBody.setToken(fcmToken);
        if (getAppConfigurationData()) {
            requestBody.setManagerName(managerID);
            requestBody.setSignature(getSignature(managerID, mUpn, MDMAuthenticatorKt.SIGNAL, mAadId, fcmToken, mRequestDate, mSecretKey));
            networkProvider.headersInterceptor().setSignature(FCMUtils.Companion.getSignature(SelfAuthenticator.INSTANCE.getMMobileNumber(), fcmToken, mRequestDate));
            Log.d(MDMAuthenticatorKt.TAG, "RetrofitBuilder Initialized");
            SelfAuthenticator.INSTANCE.setMApiHelper(new ApiHelper((ApiService) networkProvider.service((String) AuthenticatorConstants.Companion.getBASE_URL().getFirst(), ApiService.class)));
            Log.d(MDMAuthenticatorKt.TAG, "mApiHelper Initialized");
            BuildersKt.launch$default(CoroutineScopeKt.CoroutineScope(Dispatchers.getIO()), (CoroutineContext) null, (CoroutineStart) null, new MDMAuthenticator$manageNetwork$1(authMDMRepository, null), 3, (Object) null);
        }
    }

    private final String getSignature(String name, String email, String productID, String UID, String token, String requestDate, String secretKey) {
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        Object[] objArr = {name, email, productID, UID, token, requestDate};
        String stc = String.format("%s%s%s%s%s%s", Arrays.copyOf(objArr, objArr.length));
        Intrinsics.checkNotNullExpressionValue(stc, "format(...)");
        String signature = CryptoUtil.encodeHmac512(secretKey, stc);
        Log.d(MDMAuthenticatorKt.TAG, "getSignature -> stc = " + stc);
        Log.d(MDMAuthenticatorKt.TAG, "getSignature -> requestDate = " + requestDate + "  signature = " + signature);
        Intrinsics.checkNotNull(signature);
        return signature;
    }

    @NotNull
    public final String getDateFormatted() {
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        String format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH).format(date);
        Intrinsics.checkNotNullExpressionValue(format, "format(...)");
        return format;
    }

    public final void signOutUser(@NotNull Activity activity) {
        Intrinsics.checkNotNullParameter(activity, "activity");
        Log.d(MDMAuthenticatorKt.TAG, "signOutUser");
        AppAccount effectiveAccount = mUserAccount;
        Thread thread = new Thread(() -> {
            signOutUser$lambda$2(r2, r3);
        });
        thread.start();
    }

    private static final void signOutUser$lambda$2(AppAccount $effectiveAccount, Activity $activity) {
        Intrinsics.checkNotNullParameter($activity, "$activity");
        if ($effectiveAccount != null) {
            try {
                MSALUtil.signOutAccount($activity, $effectiveAccount.getAADID());
            } catch (MsalException e) {
                if ($effectiveAccount != null) {
                    Log.d(MDMAuthenticatorKt.TAG, "Failed to sign out user " + $effectiveAccount.getAADID() + ' ' + e);
                }
            } catch (InterruptedException e2) {
                if ($effectiveAccount != null) {
                    Log.d(MDMAuthenticatorKt.TAG, "Failed to sign out user " + $effectiveAccount.getAADID() + ' ' + e2);
                }
            }
        }
        if ($effectiveAccount != null) {
            MAMEnrollmentManager mAMEnrollmentManager = mEnrollmentManager;
            Intrinsics.checkNotNull(mAMEnrollmentManager);
            mAMEnrollmentManager.unregisterAccountForMAM($effectiveAccount.getUPN());
        }
        AppSettings.clearAccount($activity);
        MDMAuthenticator mDMAuthenticator = INSTANCE;
        mUserAccount = null;
    }

    public final void continueIntuneSelfAuthentication(@NotNull ApplicationInterface applicationInterface, @NotNull IAuthenticationStatus aIAuthenticationStatus) {
        Intrinsics.checkNotNullParameter(applicationInterface, "applicationInterface");
        Intrinsics.checkNotNullParameter(aIAuthenticationStatus, "aIAuthenticationStatus");
        SelfAuthenticator.INSTANCE.setMIAuthenticationStatus(aIAuthenticationStatus);
        Log.d(MDMAuthenticatorKt.TAG, "before getFCMToken");
        FCMUtils.Companion.getFCMToken(applicationInterface, this);
        Log.d(MDMAuthenticatorKt.TAG, "after getFCMToken");
    }

    @Override // com.tm.authenticatorsdk.selfAuthenticator.IFCMTokenArrived
    public void onFCMTokenArrived(@NotNull ApplicationInterface applicationInterface, @NotNull String token) {
        Intrinsics.checkNotNullParameter(applicationInterface, "applicationInterface");
        Intrinsics.checkNotNullParameter(token, "token");
        Log.d(MDMAuthenticatorKt.TAG, "onFCMTokenArrived token = " + token);
        if (token.length() > 0) {
            manageNetwork(applicationInterface, token);
        } else {
            SelfAuthenticator.INSTANCE.responseProcessMessage("The token is empty");
        }
    }
}
