package com.tm.androidcopysdk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/SyncService.class */
public class SyncService extends Service {
    private static SyncAdapter sSyncAdapter = null;
    private static final Object sSyncAdapterLock = new Object();

    @Override // android.app.Service
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
