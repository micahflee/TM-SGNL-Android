package com.tm.androidcopysdk.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/A2PUtils.class */
public final class A2PUtils {
    private static String A2PUtil;

    public static String getIMEI(Context context) {
        try {
            String IMEIString = PreferenceManager.getDefaultSharedPreferences(context).getString(PrefManagerConstants.SHARED_PREFERENCE_AA_IMEI_STRING_AA_KEY, "");
            if (TextUtils.isEmpty(IMEIString)) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
                if (CommonUtils.checkPermission(context, "android.permission.READ_PHONE_STATE")) {
                    try {
                        IMEIString = telephonyManager.getDeviceId();
                    } catch (Exception e) {
                        Log.d("A2P", "no permission", e);
                    }
                }
                if (!TextUtils.isEmpty(IMEIString)) {
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PrefManagerConstants.SHARED_PREFERENCE_AA_IMEI_STRING_AA_KEY, IMEIString).commit();
                }
            }
            return IMEIString;
        } catch (Exception e2) {
            Log.e("A2PUtils", "Exception in getIMEI: " + e2.getMessage());
            return "";
        }
    }

    public static void calculateIMEIValue(Context context) {
        String str = PreferenceManager.getDefaultSharedPreferences(context).getString(PrefManagerConstants.SHARED_PREFERENCE_AA_IMEI_STRING_AA_KEY, "");
        if (TextUtils.isEmpty(str)) {
            str = getIMEI(context);
            if (TextUtils.isEmpty(str)) {
                str = "FAILED TO GET IMEI";
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PrefManagerConstants.SHARED_PREFERENCE_AA_IMEI_STRING_AA_KEY, str).commit();
        }
        A2PUtil = "";
        for (int i = 0; i < 10; i++) {
            A2PUtil += str.toUpperCase();
        }
    }

    public static String getA2PUtil(Context context) {
        calculateIMEIValue(context);
        return A2PUtil;
    }
}
