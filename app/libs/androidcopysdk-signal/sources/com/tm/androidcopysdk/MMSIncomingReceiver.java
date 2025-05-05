package com.tm.androidcopysdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/MMSIncomingReceiver.class */
public class MMSIncomingReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (CommonUtils.handleSmsPermissions(context)) {
            CommonUtils.startBackupService(context);
            if (GetMessagesService.getNumOfQueueIntentsMMS() <= 2) {
                GetMessagesService.setNumOfQueueIntentsMMS(GetMessagesService.getNumOfQueueIntentsMMS() + 1);
                GetMessagesService.startJobIntentService(context, "com.tm.androidcopysdk.action.getMMS");
            }
        }
    }
}
