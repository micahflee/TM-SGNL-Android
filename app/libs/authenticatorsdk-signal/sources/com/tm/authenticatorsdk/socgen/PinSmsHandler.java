package com.tm.authenticatorsdk.socgen;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.TextUtils;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/PinSmsHandler.class */
public class PinSmsHandler {
    private Context mContext;
    private String TAG;
    private PinSMSBroadcastReceiver pinSMSBroadcastReceiver;
    private IReadPinFromSmsInterface iReadPinFromSmsInterface;
    private String result = null;

    public String getResult() {
        return this.result;
    }

    public PinSmsHandler(Context context, String calledFromActivity, IReadPinFromSmsInterface iReadPinFromSmsInterface) {
        this.TAG = "PinSmsHandler";
        this.mContext = context;
        this.iReadPinFromSmsInterface = iReadPinFromSmsInterface;
        this.TAG = calledFromActivity;
    }

    public void startListening() {
        initSmsRetriever();
        registerSmsReceiver();
    }

    private void initSmsRetriever() {
        SmsRetrieverClient client = SmsRetriever.getClient(this.mContext);
        Task<Void> task = client.startSmsRetriever();
        task.addOnSuccessListener(new OnSuccessListener<Void>() { // from class: com.tm.authenticatorsdk.socgen.PinSmsHandler.1
            public void onSuccess(Void aVoid) {
                Log.d(PinSmsHandler.this.TAG, "smsRetrieverCall SUCCESS");
            }
        });
        task.addOnFailureListener(new OnFailureListener() { // from class: com.tm.authenticatorsdk.socgen.PinSmsHandler.2
            public void onFailure(Exception e) {
                Log.d(PinSmsHandler.this.TAG, "smsRetrieverCall FAIL");
            }
        });
    }

    private void registerSmsReceiver() {
        if (this.pinSMSBroadcastReceiver == null) {
            this.pinSMSBroadcastReceiver = new PinSMSBroadcastReceiver(new OnAuthNumberReceivedListener() { // from class: com.tm.authenticatorsdk.socgen.PinSmsHandler.3
                @Override // com.tm.authenticatorsdk.socgen.OnAuthNumberReceivedListener
                public void onAuthNumberReceived(String authNumber) {
                    Log.d(PinSmsHandler.this.TAG, "RECEIVED String : " + authNumber);
                    final String pin = PinSmsHandler.this.getPINFromMessage(authNumber);
                    if (!TextUtils.isEmpty(pin)) {
                        Log.d(PinSmsHandler.this.TAG, "PIN From SMS : " + pin);
                        Handler mainHandler = new Handler(PinSmsHandler.this.mContext.getMainLooper());
                        Runnable myRunnable = new Runnable() { // from class: com.tm.authenticatorsdk.socgen.PinSmsHandler.3.1
                            @Override // java.lang.Runnable
                            public void run() {
                                PinSmsHandler.this.iReadPinFromSmsInterface.fillPinValue(pin);
                            }
                        };
                        mainHandler.post(myRunnable);
                        PinSmsHandler.this.mContext.unregisterReceiver(PinSmsHandler.this.pinSMSBroadcastReceiver);
                    }
                }
            });
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(PinSMSBroadcastReceiver.SMSRetrievedAction);
        this.mContext.registerReceiver(this.pinSMSBroadcastReceiver, filter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getPINFromMessage(String message) {
        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this.mContext);
        String hashCode = appSignatureHelper.getAppSignatures().get(0);
        String[] messageToArray = message.split(" ");
        int messageToArrayLength = messageToArray.length;
        if (messageToArrayLength > 2 && messageToArray[messageToArrayLength - 1].equals(hashCode)) {
            return messageToArray[messageToArrayLength - 2];
        }
        return null;
    }
}
