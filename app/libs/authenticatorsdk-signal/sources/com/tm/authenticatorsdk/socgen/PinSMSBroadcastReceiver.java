package com.tm.authenticatorsdk.socgen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.common.api.Status;
import com.tm.authenticatorsdk.BuildConfig;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/PinSMSBroadcastReceiver.class */
public class PinSMSBroadcastReceiver extends BroadcastReceiver {
    private OnAuthNumberReceivedListener listener;
    public static final String SMSRetrievedAction = "com.google.android.gms.auth.api.phone.SMS_RETRIEVED";
    private String TAG = "PinSMSBroadcastReceiver";

    public PinSMSBroadcastReceiver(OnAuthNumberReceivedListener listener) {
        this.listener = listener;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (SMSRetrievedAction.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get("com.google.android.gms.auth.api.phone.EXTRA_STATUS");
            switch (status.getStatusCode()) {
                case BuildConfig.DEBUG /* 0 */:
                    String message = (String) extras.get("com.google.android.gms.auth.api.phone.EXTRA_SMS_MESSAGE");
                    this.listener.onAuthNumberReceived(message);
                    Log.d(this.TAG, "MySMSBroadcastReceiver : onReceiver(CommonStatusCodes.SUCCESS)");
                    return;
                case 15:
                    Log.d(this.TAG, "MySMSBroadcastReceiver : onReceiver(CommonStatusCodes.TIMEOUT)");
                    return;
                default:
                    return;
            }
        }
    }
}
