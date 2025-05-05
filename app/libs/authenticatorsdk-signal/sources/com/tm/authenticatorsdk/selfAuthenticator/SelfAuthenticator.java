package com.tm.authenticatorsdk.selfAuthenticator;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mindorks.retrofit.coroutines.data.api.ApiHelper;
import com.mindorks.retrofit.coroutines.data.api.ApiService;
import com.tm.INetworkProvider;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticatorKt;
import com.tm.authenticatorsdk.selfAuthenticator.model.RetrieveOTP;
import com.tm.authenticatorsdk.selfAuthenticator.model.UpdateUserResponse;
import com.tm.authenticatorsdk.selfAuthenticator.model.UserFields;
import com.tm.logger.Log;
import com.tm.utils.ApplicationInterface;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.Dispatchers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import retrofit2.Response;
/* compiled from: SelfAuthenticator.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\bÆ\u0002\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001c\u0010.\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040/2\u0006\u00100\u001a\u00020\u0004H\u0002J\u0014\u00101\u001a\u0002022\f\u00103\u001a\b\u0012\u0004\u0012\u00020504J\u000e\u00106\u001a\u0002052\u0006\u00107\u001a\u00020\u0004J\u0006\u00108\u001a\u000205J\u0016\u00109\u001a\u0002022\u0006\u0010:\u001a\u00020\u00042\u0006\u0010;\u001a\u00020<J\u0018\u0010=\u001a\u0002052\u0006\u0010>\u001a\u00020\u00042\u0006\u0010:\u001a\u00020\u0004H\u0002J\u001a\u0010?\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040@2\u0006\u0010A\u001a\u00020\u0004J\b\u0010B\u001a\u000202H\u0002J*\u0010C\u001a\u0002022\u0006\u0010D\u001a\u00020\u00102\u0006\u0010E\u001a\u00020\u00042\u0006\u0010F\u001a\u00020\u00042\n\b\u0002\u0010G\u001a\u0004\u0018\u00010\u001cJ\u0018\u0010H\u001a\u0002022\u0006\u0010I\u001a\u00020J2\u0006\u00107\u001a\u00020\u0004H\u0016J\u000e\u0010K\u001a\u0002022\u0006\u0010L\u001a\u00020\u0004J\u0016\u0010M\u001a\u0002022\u0006\u0010I\u001a\u00020J2\u0006\u0010N\u001a\u00020\u0016R\u0014\u0010\u0003\u001a\u00020\u0004X\u0086D¢\u0006\b\n��\u001a\u0004\b\u0005\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u0004X\u0086D¢\u0006\b\n��\u001a\u0004\b\b\u0010\u0006R\u001a\u0010\t\u001a\u00020\nX\u0086.¢\u0006\u000e\n��\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u000f\u001a\u00020\u0010X\u0086.¢\u0006\u000e\n��\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u001a\u0010\u0015\u001a\u00020\u0016X\u0086.¢\u0006\u000e\n��\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u001a\u0010\u001b\u001a\u00020\u001cX\u0086.¢\u0006\u000e\n��\u001a\u0004\b\u001d\u0010\u001e\"\u0004\b\u001f\u0010 R\u001a\u0010!\u001a\u00020\u0004X\u0086.¢\u0006\u000e\n��\u001a\u0004\b\"\u0010\u0006\"\u0004\b#\u0010$R\u001a\u0010%\u001a\u00020&X\u0086.¢\u0006\u000e\n��\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010+\u001a\u00020\u0004X\u0086.¢\u0006\u000e\n��\u001a\u0004\b,\u0010\u0006\"\u0004\b-\u0010$¨\u0006O"}, d2 = {"Lcom/tm/authenticatorsdk/selfAuthenticator/SelfAuthenticator;", "Lcom/tm/authenticatorsdk/selfAuthenticator/IFCMTokenArrived;", "()V", "RETERIVE_ONE_TIME_PIN_CODE_SUCCESSED_SUB_STRING", "", "getRETERIVE_ONE_TIME_PIN_CODE_SUCCESSED_SUB_STRING", "()Ljava/lang/String;", "TAG", "getTAG", "mApiHelper", "Lcom/mindorks/retrofit/coroutines/data/api/ApiHelper;", "getMApiHelper", "()Lcom/mindorks/retrofit/coroutines/data/api/ApiHelper;", "setMApiHelper", "(Lcom/mindorks/retrofit/coroutines/data/api/ApiHelper;)V", "mAuthenticationAppType", "Lcom/tm/authenticatorsdk/selfAuthenticator/AuthenticationAppType;", "getMAuthenticationAppType", "()Lcom/tm/authenticatorsdk/selfAuthenticator/AuthenticationAppType;", "setMAuthenticationAppType", "(Lcom/tm/authenticatorsdk/selfAuthenticator/AuthenticationAppType;)V", "mIAuthenticationStatus", "Lcom/tm/authenticatorsdk/selfAuthenticator/IAuthenticationStatus;", "getMIAuthenticationStatus", "()Lcom/tm/authenticatorsdk/selfAuthenticator/IAuthenticationStatus;", "setMIAuthenticationStatus", "(Lcom/tm/authenticatorsdk/selfAuthenticator/IAuthenticationStatus;)V", "mIEnsureIpDataArrived", "Lcom/tm/authenticatorsdk/selfAuthenticator/IEnsureIpDataArrived;", "getMIEnsureIpDataArrived", "()Lcom/tm/authenticatorsdk/selfAuthenticator/IEnsureIpDataArrived;", "setMIEnsureIpDataArrived", "(Lcom/tm/authenticatorsdk/selfAuthenticator/IEnsureIpDataArrived;)V", "mMobileNumber", "getMMobileNumber", "setMMobileNumber", "(Ljava/lang/String;)V", "mRetrieveOTP", "Lcom/tm/authenticatorsdk/selfAuthenticator/model/RetrieveOTP;", "getMRetrieveOTP", "()Lcom/tm/authenticatorsdk/selfAuthenticator/model/RetrieveOTP;", "setMRetrieveOTP", "(Lcom/tm/authenticatorsdk/selfAuthenticator/model/RetrieveOTP;)V", "mVersion", "getMVersion", "setMVersion", "createHeadersMapForRequests", "", "dateFormatted", "doOnEnsureResultSuccesses", "", "ensureResult", "Lretrofit2/Response;", "Lcom/google/gson/JsonArray;", "getEnsureIPBody", "token", "getOPTBody", "getUserCredentials", "oneTimePIN", "onCredentialsArrived", "Lcom/tm/authenticatorsdk/selfAuthenticator/IOnCredentialsArrived;", "getUserCredentialsBody", "value", "getUserNameAndPasswordFromResponse", "Lkotlin/Pair;", "responseBody", "initRetrieveOTPObject", "initSelfAuthenticator", "aAuthenticationAppType", "aMobileNumber", "aVersion", "aIEnsureIpDataArrived", "onFCMTokenArrived", "applicationInterface", "Lcom/tm/utils/ApplicationInterface;", "responseProcessMessage", "message", "startSelfAuthentication", "aIAuthenticationStatus", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/SelfAuthenticator.class */
public final class SelfAuthenticator implements IFCMTokenArrived {
    public static AuthenticationAppType mAuthenticationAppType;
    public static String mMobileNumber;
    public static String mVersion;
    public static IAuthenticationStatus mIAuthenticationStatus;
    public static IEnsureIpDataArrived mIEnsureIpDataArrived;
    public static RetrieveOTP mRetrieveOTP;
    public static ApiHelper mApiHelper;
    @NotNull
    public static final SelfAuthenticator INSTANCE = new SelfAuthenticator();
    @NotNull
    private static final String RETERIVE_ONE_TIME_PIN_CODE_SUCCESSED_SUB_STRING = "code : ";
    @NotNull
    private static final String TAG = "SelfAuthenticatorForTestM";

    private SelfAuthenticator() {
    }

    @NotNull
    public final String getRETERIVE_ONE_TIME_PIN_CODE_SUCCESSED_SUB_STRING() {
        return RETERIVE_ONE_TIME_PIN_CODE_SUCCESSED_SUB_STRING;
    }

    @NotNull
    public final AuthenticationAppType getMAuthenticationAppType() {
        AuthenticationAppType authenticationAppType = mAuthenticationAppType;
        if (authenticationAppType != null) {
            return authenticationAppType;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mAuthenticationAppType");
        return null;
    }

    public final void setMAuthenticationAppType(@NotNull AuthenticationAppType authenticationAppType) {
        Intrinsics.checkNotNullParameter(authenticationAppType, "<set-?>");
        mAuthenticationAppType = authenticationAppType;
    }

    @NotNull
    public final String getMMobileNumber() {
        String str = mMobileNumber;
        if (str != null) {
            return str;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mMobileNumber");
        return null;
    }

    public final void setMMobileNumber(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        mMobileNumber = str;
    }

    @NotNull
    public final String getMVersion() {
        String str = mVersion;
        if (str != null) {
            return str;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mVersion");
        return null;
    }

    public final void setMVersion(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        mVersion = str;
    }

    @NotNull
    public final IAuthenticationStatus getMIAuthenticationStatus() {
        IAuthenticationStatus iAuthenticationStatus = mIAuthenticationStatus;
        if (iAuthenticationStatus != null) {
            return iAuthenticationStatus;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mIAuthenticationStatus");
        return null;
    }

    public final void setMIAuthenticationStatus(@NotNull IAuthenticationStatus iAuthenticationStatus) {
        Intrinsics.checkNotNullParameter(iAuthenticationStatus, "<set-?>");
        mIAuthenticationStatus = iAuthenticationStatus;
    }

    @NotNull
    public final IEnsureIpDataArrived getMIEnsureIpDataArrived() {
        IEnsureIpDataArrived iEnsureIpDataArrived = mIEnsureIpDataArrived;
        if (iEnsureIpDataArrived != null) {
            return iEnsureIpDataArrived;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mIEnsureIpDataArrived");
        return null;
    }

    public final void setMIEnsureIpDataArrived(@NotNull IEnsureIpDataArrived iEnsureIpDataArrived) {
        Intrinsics.checkNotNullParameter(iEnsureIpDataArrived, "<set-?>");
        mIEnsureIpDataArrived = iEnsureIpDataArrived;
    }

    @NotNull
    public final RetrieveOTP getMRetrieveOTP() {
        RetrieveOTP retrieveOTP = mRetrieveOTP;
        if (retrieveOTP != null) {
            return retrieveOTP;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mRetrieveOTP");
        return null;
    }

    public final void setMRetrieveOTP(@NotNull RetrieveOTP retrieveOTP) {
        Intrinsics.checkNotNullParameter(retrieveOTP, "<set-?>");
        mRetrieveOTP = retrieveOTP;
    }

    @NotNull
    public final ApiHelper getMApiHelper() {
        ApiHelper apiHelper = mApiHelper;
        if (apiHelper != null) {
            return apiHelper;
        }
        Intrinsics.throwUninitializedPropertyAccessException("mApiHelper");
        return null;
    }

    public final void setMApiHelper(@NotNull ApiHelper apiHelper) {
        Intrinsics.checkNotNullParameter(apiHelper, "<set-?>");
        mApiHelper = apiHelper;
    }

    @NotNull
    public final String getTAG() {
        return TAG;
    }

    public static /* synthetic */ void initSelfAuthenticator$default(SelfAuthenticator selfAuthenticator, AuthenticationAppType authenticationAppType, String str, String str2, IEnsureIpDataArrived iEnsureIpDataArrived, int i, Object obj) {
        if ((i & 8) != 0) {
            iEnsureIpDataArrived = null;
        }
        selfAuthenticator.initSelfAuthenticator(authenticationAppType, str, str2, iEnsureIpDataArrived);
    }

    public final void initSelfAuthenticator(@NotNull AuthenticationAppType aAuthenticationAppType, @NotNull String aMobileNumber, @NotNull String aVersion, @Nullable IEnsureIpDataArrived aIEnsureIpDataArrived) {
        Intrinsics.checkNotNullParameter(aAuthenticationAppType, "aAuthenticationAppType");
        Intrinsics.checkNotNullParameter(aMobileNumber, "aMobileNumber");
        Intrinsics.checkNotNullParameter(aVersion, "aVersion");
        setMAuthenticationAppType(aAuthenticationAppType);
        setMMobileNumber(aMobileNumber);
        setMVersion(aVersion);
        initRetrieveOTPObject();
        if (aIEnsureIpDataArrived != null) {
            setMIEnsureIpDataArrived(aIEnsureIpDataArrived);
        }
    }

    private final void initRetrieveOTPObject() {
        setMRetrieveOTP(new RetrieveOTP(getMMobileNumber(), 0, getMAuthenticationAppType().getRetrieveModePushBitMaskCode()));
    }

    public final void startSelfAuthentication(@NotNull ApplicationInterface applicationInterface, @NotNull IAuthenticationStatus aIAuthenticationStatus) {
        Intrinsics.checkNotNullParameter(applicationInterface, "applicationInterface");
        Intrinsics.checkNotNullParameter(aIAuthenticationStatus, "aIAuthenticationStatus");
        setMIAuthenticationStatus(aIAuthenticationStatus);
        Log.d("SelfAuthenticatorM", "before getFCMToken");
        FCMUtils.Companion.getFCMToken(applicationInterface, this);
        Log.d("SelfAuthenticatorM", "after getFCMToken");
    }

    @Override // com.tm.authenticatorsdk.selfAuthenticator.IFCMTokenArrived
    public void onFCMTokenArrived(@NotNull ApplicationInterface applicationInterface, @NotNull String token) {
        Intrinsics.checkNotNullParameter(applicationInterface, "applicationInterface");
        Intrinsics.checkNotNullParameter(token, "token");
        Log.d("SelfAuthenticatorM", "onFCMTokenArrived token = " + token);
        if (token.length() > 0) {
            INetworkProvider networkProvider = applicationInterface.networkProvider();
            String dateFormatted = FCMUtils.Companion.getDateFormatted();
            networkProvider.headersInterceptor().setSignature(FCMUtils.Companion.getSignature(getMMobileNumber(), token, dateFormatted));
            ApiService service = (ApiService) networkProvider.service((String) AuthenticatorConstants.Companion.getBASE_URL().getFirst(), ApiService.class);
            Log.d("SelfAuthenticatorM", "RetrofitBuilder Initialized");
            try {
                setMApiHelper(new ApiHelper(service));
                Log.d("SelfAuthenticatorM", "mApiHelper Initialized");
                BuildersKt.launch$default(CoroutineScopeKt.CoroutineScope(Dispatchers.getIO()), (CoroutineContext) null, (CoroutineStart) null, new SelfAuthenticator$onFCMTokenArrived$1(dateFormatted, token, null), 3, (Object) null);
                return;
            } catch (Exception aAnyException) {
                responseProcessMessage("Exception " + aAnyException.getMessage());
                Log.d("SelfAuthenticatorM", "UnknownServiceException = " + aAnyException.getMessage());
                return;
            }
        }
        responseProcessMessage("The token is empty");
    }

    public final void doOnEnsureResultSuccesses(@NotNull Response<JsonArray> response) {
        Intrinsics.checkNotNullParameter(response, "ensureResult");
        Gson gson = new Gson();
        Object body = response.body();
        Intrinsics.checkNotNull(body);
        JsonObject jsonObject = ((JsonArray) body).get(0);
        Intrinsics.checkNotNull(jsonObject, "null cannot be cast to non-null type com.google.gson.JsonObject");
        UpdateUserResponse updateUserResponseArray = (UpdateUserResponse) gson.fromJson(jsonObject.toString(), UpdateUserResponse.class);
        String myName = "";
        String first = "";
        String last = "";
        String email = "";
        for (UserFields fields : updateUserResponseArray.getUserFields()) {
            if (fields != null && fields.getValues()[0] != null) {
                if (!TextUtils.isEmpty(fields.getName()) && StringsKt.equals(fields.getName(), "FIRST_NAME", true)) {
                    myName = fields.getValues()[0] + ' ';
                    String str = fields.getValues()[0];
                    Intrinsics.checkNotNullExpressionValue(str, "get(...)");
                    first = str;
                }
                if (!TextUtils.isEmpty(fields.getName()) && StringsKt.equals(fields.getName(), "LAST_NAME", true)) {
                    myName = myName + fields.getValues()[0];
                    String str2 = fields.getValues()[0];
                    Intrinsics.checkNotNullExpressionValue(str2, "get(...)");
                    last = str2;
                }
                if (!TextUtils.isEmpty(fields.getName()) && StringsKt.equals(fields.getName(), "EMAIL", true)) {
                    String str3 = fields.getValues()[0];
                    Intrinsics.checkNotNullExpressionValue(str3, "get(...)");
                    email = str3;
                }
            }
        }
        Log.d("MNMNAAAMMAMAMA1", "pref_my_name :" + myName);
        Log.d("MNMNAAAMMAMAMA1", "pref_my_name_contact :" + first + ',' + last + ',' + email);
        if (mIEnsureIpDataArrived != null) {
            getMIEnsureIpDataArrived().onEnsureIpDataArrived(first, last, email);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Map<String, String> createHeadersMapForRequests(String dateFormatted) {
        Map headersMap = new LinkedHashMap();
        headersMap.put("mobile", getMMobileNumber());
        headersMap.put("request-date", dateFormatted);
        return headersMap;
    }

    public final void responseProcessMessage(@NotNull String message) {
        Intrinsics.checkNotNullParameter(message, "message");
        getMIAuthenticationStatus().authenticationProcessMessage(message);
    }

    public final void getUserCredentials(@NotNull String oneTimePIN, @NotNull IOnCredentialsArrived onCredentialsArrived) {
        Intrinsics.checkNotNullParameter(oneTimePIN, "oneTimePIN");
        Intrinsics.checkNotNullParameter(onCredentialsArrived, "onCredentialsArrived");
        Ref.ObjectRef credentialsPair = new Ref.ObjectRef();
        credentialsPair.element = new Pair("", "");
        BuildersKt.launch$default(CoroutineScopeKt.CoroutineScope(Dispatchers.getIO()), (CoroutineContext) null, (CoroutineStart) null, new SelfAuthenticator$getUserCredentials$1(oneTimePIN, credentialsPair, onCredentialsArrived, null), 3, (Object) null);
    }

    @NotNull
    public final JsonArray getEnsureIPBody(@NotNull String token) {
        Intrinsics.checkNotNullParameter(token, "token");
        JsonArray ensureBody = new JsonArray();
        JsonElement jsonArray = new JsonArray();
        jsonArray.add(MDMAuthenticatorKt.DEVICE_TYPE);
        jsonArray.add(token);
        jsonArray.add(token);
        jsonArray.add("null");
        jsonArray.add(String.valueOf(INSTANCE.getMAuthenticationAppType().getAAppServerId()));
        jsonArray.add(INSTANCE.getMVersion());
        jsonArray.add(INSTANCE.getMAuthenticationAppType().getAAppType());
        ensureBody.add(jsonArray);
        Log.d("SelfAuthenticatorM", "ensureBody = " + ensureBody);
        return ensureBody;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final JsonArray getUserCredentialsBody(String value, String oneTimePIN) {
        JsonArray getUserCredentialBody = new JsonArray();
        getUserCredentialBody.add(value);
        getUserCredentialBody.add((Number) 0);
        getUserCredentialBody.add((Number) 1);
        getUserCredentialBody.add(oneTimePIN);
        return getUserCredentialBody;
    }

    @NotNull
    public final Pair<String, String> getUserNameAndPasswordFromResponse(@NotNull String responseBody) {
        Intrinsics.checkNotNullParameter(responseBody, "responseBody");
        JSONArray password = null;
        JSONArray userName = null;
        JSONArray responseJsonArr = new JSONArray(responseBody);
        Object obj = responseJsonArr.getJSONObject(0).get("userFields");
        Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type org.json.JSONArray");
        JSONArray array = (JSONArray) obj;
        int length = array.length();
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            JSONObject obj2 = array.getJSONObject(i);
            if (!Intrinsics.areEqual(obj2.getString(MDMAuthenticatorKt.NAME), "PASSWORD")) {
                i++;
            } else {
                password = obj2.getJSONArray("values");
                break;
            }
        }
        int i2 = 0;
        while (true) {
            if (i2 >= length) {
                break;
            }
            JSONObject obj3 = array.getJSONObject(i2);
            if (!Intrinsics.areEqual(obj3.getString(MDMAuthenticatorKt.NAME), "USERNAME")) {
                i2++;
            } else {
                userName = obj3.getJSONArray("values");
                break;
            }
        }
        if (array.length() > 0) {
            JSONArray jSONArray = userName;
            Intrinsics.checkNotNull(jSONArray);
            String string = jSONArray.getString(0);
            JSONArray jSONArray2 = password;
            Intrinsics.checkNotNull(jSONArray2);
            return new Pair<>(string, jSONArray2.getString(0));
        }
        return new Pair<>("", "");
    }

    @NotNull
    public final JsonArray getOPTBody() {
        JsonArray optBody = new JsonArray();
        optBody.add(INSTANCE.getMMobileNumber());
        optBody.add(Integer.valueOf(INSTANCE.getMAuthenticationAppType().getAAppServerId()));
        optBody.add(Integer.valueOf(INSTANCE.getMAuthenticationAppType().getRetrieveModePushBitMaskCode()));
        return optBody;
    }
}
