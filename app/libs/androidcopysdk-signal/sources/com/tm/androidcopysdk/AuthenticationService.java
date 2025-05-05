package com.tm.androidcopysdk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/AuthenticationService.class */
public class AuthenticationService extends Service {
    private StubAuthenticator mAuthenticator;

    @Override // android.app.Service
    public void onCreate() {
        this.mAuthenticator = new StubAuthenticator(this);
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mAuthenticator.getIBinder();
    }
}
