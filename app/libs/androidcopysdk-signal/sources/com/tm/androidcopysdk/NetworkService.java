package com.tm.androidcopysdk;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import com.tm.androidcopysdk.events.EventService;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/NetworkService.class */
public class NetworkService extends Service {
    private NetworkReceiver mReceiver = null;
    public static final int DefaultValueState = 0;
    public static final int RegisterState = 1;
    public static final String NetworkServiceState = "state";

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

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startListening() {
        if (this.mReceiver == null) {
            this.mReceiver = new NetworkReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(this.mReceiver, filter);
        }
    }

    public void stopListening() {
        if (this.mReceiver != null) {
            unregisterReceiver(this.mReceiver);
            this.mReceiver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/NetworkService$NetworkReceiver.class */
    public class NetworkReceiver extends BroadcastReceiver {
        private NetworkReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (CommonUtils.isWifiOr3GConnection(context)) {
                EventService.startEventService(context);
                NetworkService.this.stopListening();
                NetworkService.this.stopSelf();
            }
        }
    }

    public static void startNetworkService(Context context) {
        Intent i = new Intent(context, NetworkService.class);
        i.putExtra("state", 1);
        context.startService(i);
    }
}
