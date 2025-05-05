package com.tm.androidcopysdk;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/NetworkAndChargingService.class */
public class NetworkAndChargingService extends Service {
    private WifiReceiver mWifiReceiver = null;
    private ChargingReceiver mChargingReceiver = null;
    public static final int waitingForWifiOrChargingState = 1;
    public static final int DefaultValueState = 0;
    public static final String NetworkAndChargingState = "state";

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("state")) {
            switch (intent.getIntExtra("state", 0)) {
                case 1:
                    startListening();
                    return 2;
                default:
                    return 2;
            }
        }
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleBatteryChanged(Context context) {
        Log.d("NetworkAndChargingService", "handleBatteryChanged");
        Boolean isSaveBatteryPrefOn = Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_SAVE_BATTERY_PREF_KEY, false));
        if ((CommonUtils.isWifiConnected(context) && CommonUtils.isCharging(context)) || !isSaveBatteryPrefOn.booleanValue()) {
            CommonUtils.startSyncAccount(context);
            stopListening();
            stopSelf();
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        stopListening();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWifiChanged(Context context) {
        Log.d("NetworkAndChargingService", "handleWifiChanged");
        if (CommonUtils.isWifiConnected(context)) {
            CommonUtils.startSyncAccount(context);
            stopListening();
            stopSelf();
        }
    }

    @Override // android.app.Service
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startListening() {
        if (this.mWifiReceiver == null) {
            this.mWifiReceiver = new WifiReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
            registerReceiver(this.mWifiReceiver, filter);
        }
        Boolean isArchiveHistoricRequested = Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_HISTORY_ARCHIVE_CLICKED_KEY, false));
        if (this.mChargingReceiver == null && isArchiveHistoricRequested.booleanValue()) {
            Boolean isArchiveHistoryDone = Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_HISTORY_ARCHIVE_DONE_KEY, false));
            if (!isArchiveHistoryDone.booleanValue()) {
                this.mChargingReceiver = new ChargingReceiver();
                IntentFilter filter2 = new IntentFilter();
                filter2.addAction("android.intent.action.BATTERY_CHANGED");
                registerReceiver(this.mChargingReceiver, filter2);
            }
        }
    }

    public void stopListening() {
        if (this.mWifiReceiver != null) {
            unregisterReceiver(this.mWifiReceiver);
            this.mWifiReceiver = null;
        }
        if (this.mChargingReceiver != null) {
            unregisterReceiver(this.mChargingReceiver);
            this.mChargingReceiver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/NetworkAndChargingService$WifiReceiver.class */
    public class WifiReceiver extends BroadcastReceiver {
        private WifiReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.net.wifi.supplicant.CONNECTION_CHANGE") && intent.getBooleanExtra("connected", false) && CommonUtils.isCharging(context)) {
                NetworkAndChargingService.this.handleWifiChanged(context);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/NetworkAndChargingService$ChargingReceiver.class */
    public class ChargingReceiver extends BroadcastReceiver {
        private ChargingReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                int status = intent.getIntExtra("status", -1);
                if ((status == 2 || status == 5) && CommonUtils.isWifiConnected(context)) {
                    NetworkAndChargingService.this.handleBatteryChanged(context);
                }
            }
        }
    }
}
