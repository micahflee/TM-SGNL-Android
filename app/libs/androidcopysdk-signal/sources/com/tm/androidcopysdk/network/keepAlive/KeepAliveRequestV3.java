package com.tm.androidcopysdk.network.keepAlive;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tm.androidcopysdk.database.DBKeepAliveQueryHelper;
import com.tm.androidcopysdk.network.BodyBase;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.logger.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/keepAlive/KeepAliveRequestV3.class */
public class KeepAliveRequestV3 extends BodyBase {
    public static final String TAG = "KeepAliveRequestV3";
    private String mAppId;
    private String installId;
    private String appVersion;
    private String deviceToken;
    private String requestSequence = null;
    private JSONArray mKeepAliveArrayMessage = null;
    private String appInfo = null;
    private String mRequestBody = null;

    public KeepAliveRequestV3(Context aContext, String appId, String versionName, String installationId, String deviceToken) {
        this.installId = null;
        this.appVersion = null;
        this.deviceToken = null;
        if (TextUtils.isEmpty(appId)) {
            this.mAppId = appId;
        }
        if (!TextUtils.isEmpty(installationId)) {
            this.installId = installationId;
        }
        if (!TextUtils.isEmpty(versionName)) {
            this.appVersion = versionName;
        }
        if (!TextUtils.isEmpty(deviceToken)) {
            this.deviceToken = deviceToken;
        }
        initOtherKeepAliveParam(aContext);
    }

    private void initOtherKeepAliveParam(Context aContext) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        long lastKeepAliveAttemptTimestampLong = aContext.getSharedPreferences("marchiver", 0).getLong(PrefManagerConstants.SHARED_PREFERENCE_KEEP_ALIVE_LAST_KEEP_ALIVE_ATTEMPT_TIMESTAMP_KEY, 2L);
        Date currentTimeDate = new Date();
        String gmtTime = sdf.format(currentTimeDate);
        if (!isSameDay(currentTimeDate, new Date(lastKeepAliveAttemptTimestampLong))) {
            aContext.getSharedPreferences("marchiver", 0).edit().putLong(PrefManagerConstants.SHARED_PREFERENCE_KEEP_ALIVE_NUMBER_OF_KEEP_ALIVES_SENT_TODAY_KEY, 1L).apply();
        }
        long keepAliveSentTodayCounter = aContext.getSharedPreferences("marchiver", 0).getLong(PrefManagerConstants.SHARED_PREFERENCE_KEEP_ALIVE_NUMBER_OF_KEEP_ALIVES_SENT_TODAY_KEY, 1L);
        this.requestSequence = gmtTime + keepAliveSentTodayCounter;
        this.mKeepAliveArrayMessage = DBKeepAliveQueryHelper.Companion.getAllMessagesForSendingToKeepAlive(aContext);
        this.mRequestBody = String.format("{\"appId\":\"600801\", \"requestSequence\":\"%s\", \"appVersion\":\"%s\", \"deviceToken\":\"%s\", \"installId\":\"%s\" , %s}", this.requestSequence, this.appVersion, this.deviceToken, this.installId, getAppInfo(aContext, this.mKeepAliveArrayMessage));
        Log.d(TAG, "keep alive V3 request: " + this.mRequestBody);
        aContext.getSharedPreferences("marchiver", 0).edit().putLong(PrefManagerConstants.SHARED_PREFERENCE_KEEP_ALIVE_NUMBER_OF_KEEP_ALIVES_SENT_TODAY_KEY, keepAliveSentTodayCounter + 1).apply();
    }

    public JSONArray getKeepAliveArrayMessage() {
        return this.mKeepAliveArrayMessage;
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date1).equals(sdf.format(date2));
    }

    private String getAppInfo(Context aContext, JSONArray keepAliveArrayMessage) {
        JSONObject json = new JSONObject();
        try {
            json.put("appStatus", getAppStatus(aContext));
            json.put(PrefManagerConstants.SHARED_PREFERENCE_KEEP_ENCRYPTION_APP_STATUS_KEY, aContext.getSharedPreferences("marchiver", 0).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_KEEP_ENCRYPTION_APP_STATUS_KEY, true) ? "OK" : "fail");
            json.put(PrefManagerConstants.SHARED_PREFERENCE_DID_DNS_FAIL_KEY, aContext.getSharedPreferences("marchiver", 0).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_DID_DNS_FAIL_KEY, true) ? "fail" : "OK");
            json.put("itemCounter", String.valueOf(keepAliveArrayMessage.length()));
            json.put("communicationMetaDataList", keepAliveArrayMessage);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        return String.format(Locale.US, "\"appInfo\": " + json.toString(), new Object[0]);
    }

    private String getAppStatus(Context aContext) {
        String appStatus = "OK";
        boolean appStatusArchiving = aContext.getSharedPreferences("marchiver", 0).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_KEEP_ENCRYPTION_APP_STATUS_KEY, true) && aContext.getSharedPreferences("marchiver", 0).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_PINNING_APP_STATUS_KEY, true);
        boolean appStatusLocked = aContext.getSharedPreferences("marchiver", 0).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_WAS_USER_DEACTIVATED_KEY, false);
        boolean appStatus401 = aContext.getSharedPreferences("marchiver", 0).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_DID_GET_401_KEY, false);
        boolean appStatusSignedOut = aContext.getSharedPreferences("marchiver", 0).getString(PrefManagerConstants.SHARED_PREFERENCE_MOBILE_NUMBER_KEY, null) == null;
        if (appStatus401) {
            appStatus = "App_Unauthorized";
        } else if (!appStatusArchiving) {
            appStatus = "Not_Archiving";
        } else if (appStatusLocked) {
            appStatus = "App_Locked";
        } else if (appStatusSignedOut) {
            appStatus = "App_Signed_Out";
        }
        return appStatus;
    }

    public String getAppId() {
        return this.mAppId;
    }

    public String getInstallId() {
        return this.installId;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public String getDeviceToken() {
        return this.deviceToken;
    }

    public String getBodyAsString() {
        return this.mRequestBody;
    }

    public JsonObject getBodyAsJson() {
        return ((JsonElement) new Gson().fromJson(this.mRequestBody, JsonElement.class)).getAsJsonObject();
    }
}
