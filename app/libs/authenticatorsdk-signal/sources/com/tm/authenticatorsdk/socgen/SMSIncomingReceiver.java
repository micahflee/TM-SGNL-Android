package com.tm.authenticatorsdk.socgen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import com.tm.authenticatorsdk.R;
import org.greenrobot.eventbus.EventBus;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/SMSIncomingReceiver.class */
public class SMSIncomingReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] smsMessageArr = new SmsMessage[0];
        SmsMessage[] msgs = getMessagesFromIntent(intent);
        if (msgs == null || msgs.length == 0 || msgs[0] == null) {
            return;
        }
        String msgBody = msgs[0].getMessageBody();
        String[] bodyParts = msgBody.split(": ");
        if (bodyParts == null || bodyParts.length != 2) {
            return;
        }
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
        ed.putString(context.getString(R.string.signin_sms_activation_key), bodyParts[1]);
        Boolean.valueOf(ed.commit());
        String key = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.signin_sms_activation_key), "");
        EventBus.getDefault().post(new PinCodeEvent(key));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static final SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        byte[] bArr = new byte[messages.length];
        for (int i = 0; i < messages.length; i++) {
            bArr[i] = (byte[]) messages[i];
        }
        byte[] bArr2 = new byte[bArr.length];
        int pduCount = bArr2.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];
        for (int i2 = 0; i2 < pduCount; i2++) {
            bArr2[i2] = bArr[i2];
            msgs[i2] = SmsMessage.createFromPdu(bArr2[i2]);
        }
        return msgs;
    }
}
