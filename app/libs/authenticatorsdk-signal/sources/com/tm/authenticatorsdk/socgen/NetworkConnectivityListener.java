package com.tm.authenticatorsdk.socgen;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import com.tm.logger.Log;
import java.util.HashMap;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/NetworkConnectivityListener.class */
public class NetworkConnectivityListener {
    private static final String TAG = "NetworkConnectivityList";
    private static final boolean DEBUG = false;
    private Context mContext;
    private boolean mListening;
    private boolean mAirPlaneMode;
    private String mReason;
    private boolean mIsFailover;
    private NetworkInfo mNetworkInfo;
    private NetworkInfo mOtherNetworkInfo;
    private HashMap<Handler, Integer> mHandlers = new HashMap<>();
    private State mState = State.UNKNOWN;
    private ConnectivityBroadcastReceiver mReceiver = new ConnectivityBroadcastReceiver();

    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/NetworkConnectivityListener$State.class */
    private enum State {
        UNKNOWN,
        CONNECTED,
        NOT_CONNECTED
    }

    @SuppressLint({"InlinedApi", "NewApi"})
    public static boolean isAirplaneModeOn(Context context) {
        return Build.VERSION.SDK_INT < 17 ? Settings.System.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0 : Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0;
    }

    @SuppressLint({"InlinedApi", "NewApi"})
    public static boolean isRadioModeOn(Context context) {
        return Build.VERSION.SDK_INT < 17 ? Settings.System.getInt(context.getContentResolver(), "cell", 0) != 0 : Settings.Global.getInt(context.getContentResolver(), "cell", 0) != 0;
    }

    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/NetworkConnectivityListener$ConnectivityBroadcastReceiver.class */
    private class ConnectivityBroadcastReceiver extends BroadcastReceiver {
        private ConnectivityBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            NetworkConnectivityListener.this.mAirPlaneMode = NetworkConnectivityListener.isAirplaneModeOn(context);
            String action = intent.getAction();
            Log.d("ConnectivityBroadcast", "On onReceive");
            if (!action.equals("android.net.conn.CONNECTIVITY_CHANGE") || !NetworkConnectivityListener.this.mListening) {
                Log.w(NetworkConnectivityListener.TAG, "onReceived() " + NetworkConnectivityListener.this.mState.toString() + " and " + intent);
                return;
            }
            boolean noConnectivity = intent.getBooleanExtra("noConnectivity", false);
            NetworkConnectivityListener.this.mState = noConnectivity ? State.NOT_CONNECTED : State.CONNECTED;
            NetworkConnectivityListener.this.mNetworkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            NetworkConnectivityListener.this.mOtherNetworkInfo = (NetworkInfo) intent.getParcelableExtra("otherNetwork");
            NetworkConnectivityListener.this.mReason = intent.getStringExtra("reason");
            NetworkConnectivityListener.this.mIsFailover = intent.getBooleanExtra("isFailover", false);
            for (Handler target : NetworkConnectivityListener.this.mHandlers.keySet()) {
                Message message = Message.obtain(target, ((Integer) NetworkConnectivityListener.this.mHandlers.get(target)).intValue());
                target.sendMessage(message);
            }
        }
    }

    public synchronized void startListening(Context context) {
        if (!this.mListening) {
            this.mContext = context;
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            context.registerReceiver(this.mReceiver, filter);
            this.mListening = true;
        }
    }

    public synchronized void stopListening() {
        if (this.mListening) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mContext = null;
            this.mNetworkInfo = null;
            this.mOtherNetworkInfo = null;
            this.mIsFailover = false;
            this.mReason = null;
            this.mListening = false;
        }
    }

    public void registerHandler(Handler target, int what) {
        this.mHandlers.put(target, Integer.valueOf(what));
    }

    public void unregisterHandler(Handler target) {
        this.mHandlers.remove(target);
    }

    public State getState() {
        return this.mState;
    }

    public NetworkInfo getNetworkInfo() {
        return this.mNetworkInfo;
    }

    public NetworkInfo getOtherNetworkInfo() {
        return this.mOtherNetworkInfo;
    }

    public boolean isFailover() {
        return this.mIsFailover;
    }

    public String getReason() {
        return this.mReason;
    }

    public boolean isAirPlaneMode() {
        return this.mAirPlaneMode;
    }
}
