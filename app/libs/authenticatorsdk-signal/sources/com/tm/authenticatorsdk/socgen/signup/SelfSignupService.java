package com.tm.authenticatorsdk.socgen.signup;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.tm.authenticatorsdk.R;
import com.tm.authenticatorsdk.socgen.AuthenticatorSDK;
import com.tm.authenticatorsdk.socgen.signup.SignUpManager;
import com.tm.logger.Log;
import com.tm.utils.Util;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/signup/SelfSignupService.class */
public class SelfSignupService extends Service implements SignUpManager.StopSelfSignupServiceCallBack {
    private static final String SELF_SIGNUP_SERVIC_NOTIFICATION_ID = "Chanel_1010102";
    private static final String SELF_SIGNUP_SERVIC_NOTIFICATION_CHANEL_NAME = "Signup notification chanel";

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(20200, createNotification());
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        SignUpManager signUpManager = new SignUpManager((Application) getApplicationContext(), Util.getMdmSettings((Application) getApplicationContext()), this);
        signUpManager.getFcmToken();
        return 1;
    }

    @Override // android.app.Service
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // com.tm.authenticatorsdk.socgen.signup.SignUpManager.StopSelfSignupServiceCallBack
    public void stopService() {
        stopSelf();
    }

    private Notification createNotification() {
        NotificationCompat.Builder mBuilder;
        Log.d("SelfSignupService", "createNotification");
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel mChannel = new NotificationChannel(SELF_SIGNUP_SERVIC_NOTIFICATION_ID, SELF_SIGNUP_SERVIC_NOTIFICATION_CHANEL_NAME, 4);
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        if (AuthenticatorSDK.getInstance(this).flavor == AuthenticatorSDK.FLAVOR_SETTINGS.SmarshArvhicer) {
            mBuilder = new NotificationCompat.Builder(this, SELF_SIGNUP_SERVIC_NOTIFICATION_ID).setContentTitle(getString(R.string.archiver_notification_title_samrsh)).setContentText(getString(R.string.message_in_process_notification_title_smarsh)).setSmallIcon(R.drawable.icon_aa).setAutoCancel(true).setOnlyAlertOnce(true);
        } else {
            mBuilder = new NotificationCompat.Builder(this, SELF_SIGNUP_SERVIC_NOTIFICATION_ID).setContentTitle(getString(R.string.archiver_notification_title)).setContentText(getString(R.string.message_in_process_notification_title)).setSmallIcon(R.drawable.icon_aa).setAutoCancel(true).setOnlyAlertOnce(true);
        }
        return mBuilder.build();
    }
}
