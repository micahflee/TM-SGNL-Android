package com.tm.androidcopysdk.network;

import android.content.Context;
import android.preference.PreferenceManager;
import com.tm.androidcopysdk.network.Base.ServerBaseResponse;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import com.tm.logger.Log;
import com.tm.utils.Definitions;
import com.tm.utils.FcmUtil;
import java.util.ArrayList;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/TMUpdateVersionManager.class */
public class TMUpdateVersionManager {
    private static final String TAG = "TMUpdateVersionRequest";
    String _versionToSend;
    private boolean doRequest = true;
    private static TMUpdateVersionManager _instance = null;
    private Context _context;

    private TMUpdateVersionManager(Context context) {
        this._context = context;
    }

    public static synchronized TMUpdateVersionManager getInstance(Context context) {
        if (_instance == null) {
            _instance = new TMUpdateVersionManager(context);
        }
        return _instance;
    }

    public synchronized void sendingVersionNumberToServer() {
        boolean isAppActivated = PrefManager.getBooleanPref(this._context, PrefManagerConstants.SHARED_PREFERENCE_ACTIVATED_AA_KEY, false);
        if (!isAppActivated) {
            return;
        }
        String currentVersion = getAppVersionName();
        String lastSentVersion = PrefManager.getStringPref(this._context, "pref_version_number", "");
        if (!lastSentVersion.equalsIgnoreCase(currentVersion)) {
            String userName = TMCredentialsStore.getInstance(this._context).userName(this._context);
            String password = TMCredentialsStore.getInstance(this._context).password(this._context);
            NetworkManager nm = new NetworkManager(this._context, PreferenceManager.getDefaultSharedPreferences(this._context).getString("baseurl", Definitions.BaseUrl));
            UpdateVersionRequest updateVersionRequest = new UpdateVersionRequest(this._context, userName, password, currentVersion);
            updateVersionRequest.setId(PrefManager.getStringPref(this._context, FcmUtil.FCM_TOKEN_KEY));
            updateVersionRequest.setClass("telemessage.web.services.IPDeviceAuthenticationDetails");
            Log.d(TAG, "request TMUpdateVersionManager: ");
            retrofit2.Response<ArrayList<ServerBaseResponse>> res = nm.start(updateVersionRequest, null, this._context, false);
            Log.d(TAG, "response updateVersionRequest: " + res);
            if (res != null && res.isSuccessful() && res.body() != null && ((ServerBaseResponse) ((ArrayList) res.body()).get(0)).getResultCode() == 0) {
                PrefManager.setStringPref(this._context, "pref_version_number", currentVersion);
            } else {
                Log.d(TAG, "response updateVersionRequest: res is null! ! !");
            }
        }
    }

    private String getAppVersionName() {
        try {
            return this._context.getApplicationContext().getPackageManager().getPackageInfo(this._context.getApplicationContext().getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "";
        }
    }
}
