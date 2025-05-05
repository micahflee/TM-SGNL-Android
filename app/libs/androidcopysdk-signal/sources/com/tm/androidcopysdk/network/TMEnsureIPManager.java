package com.tm.androidcopysdk.network;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.logger.Log;
import com.tm.utils.Definitions;
import java.util.ArrayList;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/TMEnsureIPManager.class */
public class TMEnsureIPManager {
    private static TMEnsureIPManager _instance = null;
    private static final String TAG = " TMEnsureIPManager";

    private TMEnsureIPManager() {
    }

    public static synchronized TMEnsureIPManager getInstance() {
        if (_instance == null) {
            _instance = new TMEnsureIPManager();
        }
        return _instance;
    }

    public void postEnsure(Context context, String userName, String password) {
        NetworkManager nm = new NetworkManager(context, PreferenceManager.getDefaultSharedPreferences(context).getString("baseurl", Definitions.BaseUrl));
        EnsureRequest ensureRequest = new EnsureRequest(context, userName, password);
        Log.d(TAG, "request ensuerIP: " + new Gson().toJson(ensureRequest));
        retrofit2.Response<ArrayList<UpdateUserResponse>> res = nm.start(ensureRequest, null, context, false);
        Log.d(TAG, "response ensuerIP: DONE!!!!!");
        Log.d("lior", "response ensuerIP: DONE!!!!!");
        if (res != null && res.isSuccessful() && ((UpdateUserResponse) ((ArrayList) res.body()).get(0)).getResultCode().intValue() == 0) {
            PrefManager.setBooleanPref(context, "finish_ensure", true);
            PrefManager.setStringPref(context, "pref_version_number", EnsureRequest.getAppVersionName(context));
            String myName = "";
            String first = "";
            String last = "";
            String email = "";
            for (UserFields fields : ((UpdateUserResponse) ((ArrayList) res.body()).get(0)).getUserFields()) {
                if (fields != null && !TextUtils.isEmpty(fields.getName()) && fields.getName().equalsIgnoreCase("first_name")) {
                    myName = fields.getValues()[0] + " " + myName;
                    first = fields.getValues()[0];
                }
                if (fields != null && !TextUtils.isEmpty(fields.getName()) && fields.getName().equalsIgnoreCase("last_name")) {
                    myName = myName + fields.getValues()[0];
                    last = fields.getValues()[0];
                }
                if (fields != null && !TextUtils.isEmpty(fields.getName()) && fields.getName().equalsIgnoreCase("EMAIL")) {
                    email = fields.getValues()[0];
                }
            }
            saveUserNameData(context, first, last, email);
        }
    }

    public static void saveUserNameData(Context context, String first, String last, String email) {
        Log.d("lior", "pref_my_name :" + first + " " + last);
        Log.d("lior", "pref_my_name_contact :" + first + "," + last);
        PrefManager.setStringPref(context, "pref_my_name", first + " " + last);
        PrefManager.setStringPref(context, "pref_my_name_contact", first + "," + last);
        PrefManager.setStringPref(context, "pref_my_first_name", first);
        PrefManager.setStringPref(context, "pref_my_last_name", last);
        PrefManager.setStringPref(context, "pref_my_email", email);
    }
}
