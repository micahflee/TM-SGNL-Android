package com.tm.androidcopysdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/SMSIncomingReceiver.class */
public class SMSIncomingReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.d("lior", "---  --- onReceive SMS --- ---");
        if (CommonUtils.handleSmsPermissions(context)) {
            GetMessagesService.startJobIntentService(context, "com.tm.androidcopysdk.action.getSMS");
            CommonUtils.startBackupService(context);
        }
    }
}
