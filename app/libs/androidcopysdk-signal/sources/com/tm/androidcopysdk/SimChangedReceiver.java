package com.tm.androidcopysdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.core.app.ActivityCompat;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.DualSimUtils;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.logger.Log;
import java.util.ArrayList;
import java.util.List;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/SimChangedReceiver.class */
public class SimChangedReceiver extends BroadcastReceiver {
    public static final boolean HIDE_SIM_LOGS = true;
    public static final boolean LOG_OUT_SIM_EVENT = false;
    public static final String CORRECT_SIM_SERIAL_NUMBER_KEY = "CORRECT_SIM_SERIAL_NUMBER_KEY";
    private static final String TAG = "SimChangedReceiver";
    private static final int NOTIFICATION_ID = 2;
    private static String lastAction = "";
    private static boolean startWait = false;
    int durationSleep = 7000;

    @Override // android.content.BroadcastReceiver
    public synchronized void onReceive(Context context, Intent intent) {
        String correctSimSerialNumber = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).getString(CORRECT_SIM_SERIAL_NUMBER_KEY, null);
        Log.d(TAG, "--> SIM state changed <--");
        Log.d("SimChangedExtras", intent.getStringExtra("ss"));
        Log.d("SimChangedExtras", "correctSimSerialNumber: " + correctSimSerialNumber);
        Log.d("lior", intent.getStringExtra("ss"));
        Log.d(TAG, "exit");
    }

    /* renamed from: com.tm.androidcopysdk.SimChangedReceiver$1  reason: invalid class name */
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/SimChangedReceiver$1.class */
    class AnonymousClass1 implements Runnable {
        final /* synthetic */ Context val$context;
        final /* synthetic */ String val$correctSimSerialNumber;

        AnonymousClass1(Context context, String str) {
            this.val$context = context;
            this.val$correctSimSerialNumber = str;
        }

        @Override // java.lang.Runnable
        public void run() {
            boolean unused = SimChangedReceiver.startWait = false;
            Log.d("SimChangedExtras", "lastAction -  " + SimChangedReceiver.lastAction);
            Log.d("lior", "lastAction -  " + SimChangedReceiver.lastAction);
            if (Build.VERSION.SDK_INT < 23 || (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this.val$context, "android.permission.READ_PHONE_STATE") == 0)) {
                if (DualSimUtils.dualSimIsOn(this.val$context.getApplicationContext())) {
                    SimChangedReceiver.checkDualSimEvent(this.val$context, SimChangedReceiver.lastAction);
                } else {
                    SimChangedReceiver.checkSimEvent(this.val$context, SimChangedReceiver.lastAction, this.val$correctSimSerialNumber);
                }
            }
        }
    }

    private static boolean handlePermissions(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.d(TAG, "handlePermissions was called");
            List<String> listPermissionsNeeded = new ArrayList<>();
            int readPhoneStatePermission = ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE");
            if (readPhoneStatePermission != 0) {
                listPermissionsNeeded.add("android.permission.READ_PHONE_STATE");
            }
            if (!listPermissionsNeeded.isEmpty()) {
                return false;
            }
            return true;
        }
        return true;
    }

    public static void checkSimEvent(Context context, String action, String correctSimSerialNumber) {
        Log.d(TAG, "checkSimEvent :" + action + " " + correctSimSerialNumber);
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService("phone");
        String newSimSerialNumber = null;
        if (telephonyManager != null) {
            if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") != 0) {
                if (handlePermissions(context)) {
                    try {
                        newSimSerialNumber = telephonyManager.getSimSerialNumber();
                    } catch (Exception e) {
                        Log.d("android 10", "does not meet the requirements to access device identifiers", e);
                    }
                    Log.d(TAG, "--> newSimSerialNumber <-- *****" + newSimSerialNumber);
                }
            } else {
                try {
                    newSimSerialNumber = telephonyManager.getSimSerialNumber();
                } catch (Exception e2) {
                    Log.d("android 10", "does not meet the requirements to access device identifiers", e2);
                }
                Log.d(TAG, "--> newSimSerialNumber <-- ------" + newSimSerialNumber);
            }
            if (newSimSerialNumber == null && Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, "android.permission.READ_PHONE_STATE") == 0) {
                SubscriptionManager subscriptionManager = SubscriptionManager.from(context.getApplicationContext());
                List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();
                if (subsInfoList != null) {
                    for (SubscriptionInfo subscriptionInfo : subsInfoList) {
                        if (!TextUtils.isEmpty(subscriptionInfo.getIccId())) {
                            newSimSerialNumber = subscriptionInfo.getIccId();
                            if (newSimSerialNumber.equalsIgnoreCase(correctSimSerialNumber)) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        Log.d(TAG, "checkSimEvent : TextUtils.isEmpty(newSimSerialNumber)  " + TextUtils.isEmpty(newSimSerialNumber));
        Log.d(TAG, "checkSimEvent : TextUtils.isEmpty(correctSimSerialNumber)  " + TextUtils.isEmpty(correctSimSerialNumber));
        if (!TextUtils.isEmpty(newSimSerialNumber) && !TextUtils.isEmpty(correctSimSerialNumber)) {
            Log.d(TAG, "checkSimEvent : !correctSimSerialNumber.equalsIgnoreCase(newSimSerialNumber)  " + (!correctSimSerialNumber.equalsIgnoreCase(newSimSerialNumber)));
        }
        if ((action.equalsIgnoreCase("READY") || action.equalsIgnoreCase("LOADED")) && TextUtils.isEmpty(correctSimSerialNumber) && TextUtils.isEmpty(newSimSerialNumber) && PrefManager.getBooleanPref(context, "sim_flag_out", false)) {
            Log.d(TAG, "!!!!  logout was cancel   !!!!!!");
            Log.d("SimChanged!!!!!!", "simChanged!!!!!!!! correctSerialNumber: " + correctSimSerialNumber + " new serial number : " + newSimSerialNumber);
        }
        if ((action.equalsIgnoreCase("READY") || action.equalsIgnoreCase("LOADED")) && TextUtils.isEmpty(correctSimSerialNumber) && TextUtils.isEmpty(newSimSerialNumber) && PrefManager.getBooleanPref(context, "sim_flag_out", false)) {
            Log.d(TAG, "--> SIM BACK (but no history) <--");
            Log.d(TAG, "addEvent(context, EventAbsObj.EventType.ChangedSimCardBackEvent)");
            PrefManager.setBooleanPref(context, "sim_flag_out", false);
            CommonUtils.addEvent(context, EventAbsObj.EventType.ChangedSimCardBackEvent);
        } else if ((action.equalsIgnoreCase("READY") || action.equalsIgnoreCase("LOADED")) && !TextUtils.isEmpty(correctSimSerialNumber) && !TextUtils.isEmpty(newSimSerialNumber) && correctSimSerialNumber.equalsIgnoreCase(newSimSerialNumber)) {
            Log.d(TAG, "--> SIM BACK <--" + newSimSerialNumber);
            PrefManager.setStringPref(context.getApplicationContext(), CORRECT_SIM_SERIAL_NUMBER_KEY, correctSimSerialNumber);
            Log.d(TAG, "addEvent(context, EventAbsObj.EventType.ChangedSimCardBackEvent) 22222");
            CommonUtils.addEvent(context, EventAbsObj.EventType.ChangedSimCardBackEvent);
        } else if (action.equalsIgnoreCase("ABSENT") && (TextUtils.isEmpty(newSimSerialNumber) || (!TextUtils.isEmpty(newSimSerialNumber) && !TextUtils.isEmpty(correctSimSerialNumber) && !correctSimSerialNumber.equalsIgnoreCase(newSimSerialNumber)))) {
            Log.d(TAG, "--> SIM REMOVE <--");
            Log.d(TAG, "addEvent(context, EventAbsObj.EventType.ChangedSimCardBackEvent) 3333");
            CommonUtils.addEvent(context, EventAbsObj.EventType.SimRemovedWithoutLogout);
        } else if (action.equalsIgnoreCase("backup") && !TextUtils.isEmpty(newSimSerialNumber) && !TextUtils.isEmpty(correctSimSerialNumber) && !correctSimSerialNumber.equalsIgnoreCase(newSimSerialNumber)) {
            Log.d(TAG, "!!!!  logout was cancel   !!!!!!  33333");
        }
    }

    public static void checkDualSimEvent(Context context, String action) {
        boolean validate = DualSimUtils.checkCurrentState(context);
        Log.d(TAG, "checkDualSimEvent: " + action + " " + validate);
        if (!validate) {
            DualSimUtils.getDescriptionForDualSim(context);
            if (action.equalsIgnoreCase("READY") || action.equalsIgnoreCase("LOADED")) {
                Log.d(TAG, "checkDualSimEvent: ******");
                Log.d(TAG, "!!!!  logout was cancel   !!!!!!  44444");
                Log.d(TAG, "checkDualSimEvent!!!!!!!! checkDualSimEvent 222222");
            } else if (action.equalsIgnoreCase("ABSENT")) {
                Log.d(TAG, "--> SIM REMOVE 1111222 <--");
                CommonUtils.addEvent(context, EventAbsObj.EventType.SimRemovedWithoutLogout);
            } else if (action.equalsIgnoreCase("backup")) {
                Log.d(TAG, "!!!!  logout was cancel   !!!!!!  55555");
            }
        } else if (action.equalsIgnoreCase("READY") || action.equalsIgnoreCase("LOADED")) {
            Log.d(TAG, "checkDualSimEvent: <><><><>");
        }
        Log.d(TAG, "end checkDualSimEvent");
    }
}
