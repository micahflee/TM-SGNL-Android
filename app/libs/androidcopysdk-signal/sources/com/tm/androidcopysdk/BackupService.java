package com.tm.androidcopysdk;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncInfo;
import android.content.SyncStatusObserver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.tm.androidcopysdk.AndroidCopySDK;
import com.tm.androidcopysdk.AndroidCopySettings;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventService;
import com.tm.androidcopysdk.events.PeriodicEventChecker;
import com.tm.androidcopysdk.network.appSettings.WorkerIntentService;
import com.tm.androidcopysdk.network.keepAlive.KeepWorkerIntentService;
import com.tm.androidcopysdk.utils.DualSimUtils;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.StartConverEvent;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
import com.tm.logger.Log;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/BackupService.class */
public class BackupService extends Service implements SyncStatusObserver {
    public static final String TAG = "BackupService";
    private Uri mUri;
    private Uri eUri;
    DataGrabber mDataGrabber;
    public static final String SCHEME = "content";
    private Handler notificationHandler;
    private Runnable notificationRunnable;
    Account mAccount;
    private static AndroidCopySettings.DataSaving saving = AndroidCopySettings.DataSaving.WIFI3G;
    private static AndroidCopySettings.Range range = AndroidCopySettings.Range.now;
    public static final String AUTHORITY = FlavorSettings.getInstance().getProviderName();
    public static final String ACCOUNT_TYPE = FlavorSettings.getInstance().getAccountType();
    public static final String ACCOUNT = FlavorSettings.getInstance().getAccountName();
    public static boolean startWaiting = false;
    private boolean mInit = false;
    private final IBinder mBinder = new LocalBinder();
    private final Random mGenerator = new Random();

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (Build.VERSION.SDK_INT >= 29) {
            startForeground(7711, showPermissionsNotification(this, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_TEXT_FOR_BACKUP_SERVICE : getString(R.string.notification_text_for_backup_service), true), 1);
        } else {
            startForeground(7711, showPermissionsNotification(this, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_TEXT_FOR_BACKUP_SERVICE : getString(R.string.notification_text_for_backup_service), true));
        }
        AudioSettingsManager.getInstance(this);
    }

    public static Notification showPermissionsNotification(Context context, String message, boolean showIcon) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        String channelId = StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.CHANEL_ID_BACKUP_SERVICE : context.getString(R.string.chanel_id_backup_service);
        String channelName = StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.CHANEL_NAME_BACKUP : context.getString(R.string.chanel_name_backup);
        int importance = 4;
        if (!FlavorSettings.getInstance().supportNativeMsg()) {
            importance = 2;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            mChannel.setShowBadge(false);
            mChannel.setImportance(importance);
            mChannel.setLockscreenVisibility(1);
            if (Build.VERSION.SDK_INT >= 33) {
                mChannel.setBlockable(false);
            }
            notificationManager.createNotificationChannel(mChannel);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            Notification.Builder mBuilder = new Notification.Builder(context, channelId).setContentTitle(StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_TITLE : context.getString(R.string.notification_title)).setContentText(message).setOngoing(true).setOnlyAlertOnce(true);
            if (showIcon) {
                mBuilder.setSmallIcon(R.drawable.icon_aa);
            }
            if (Build.VERSION.SDK_INT >= 31) {
                mBuilder.setForegroundServiceBehavior(1);
            }
            return mBuilder.build();
        }
        return new NotificationCompat.Builder(context, channelId).setContentTitle(StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_TITLE : context.getString(R.string.notification_title)).setContentText(message).setPriority(1).setChannelId(channelId).setOnlyAlertOnce(true).build();
    }

    @Override // android.content.SyncStatusObserver
    public void onStatusChanged(int i) {
        if (i == 4) {
            List<SyncInfo> info = ContentResolver.getCurrentSyncs();
            if (info.size() == 0) {
                EventBus.getDefault().post(new MessageEvent(AndroidCopySDK.Status.idle.name()));
            } else {
                EventBus.getDefault().post(new MessageEvent(AndroidCopySDK.Status.synching.name()));
            }
        }
        if (i == 2) {
            List<SyncInfo> info2 = ContentResolver.getCurrentSyncs();
            if (info2.size() == 0) {
                backupData();
            }
            EventBus.getDefault().post(new MessageEvent(AndroidCopySDK.Status.starting.name()));
        }
        if (i == 1) {
            EventBus.getDefault().post(new MessageEvent(AndroidCopySDK.Status.idle.name()));
        }
    }

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/BackupService$TableObserver.class */
    public class TableObserver extends ContentObserver {
        public TableObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri changeUri) {
            BackupService.this.backupData();
        }
    }

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/BackupService$EventTableObserver.class */
    public class EventTableObserver extends ContentObserver {
        public EventTableObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri changeUri) {
            Log.d(BackupService.TAG, "onChange :" + (changeUri != null ? changeUri.getPath() : ""));
            EventService.startEventService(BackupService.this);
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent != null && intent.hasExtra("stop")) {
            Log.d(TAG, "onStartCommand - stop");
            startWaiting = false;
            stopHandler();
            stopForeground(true);
            stopSelf();
            return 2;
        }
        startWaiting = true;
        Log.d(TAG, "onStartCommand - strart");
        this.mAccount = CreateSyncAccount(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        init();
        ContentResolver.addStatusChangeListener(4, this);
        new Thread(new Runnable() { // from class: com.tm.androidcopysdk.BackupService.1
            @Override // java.lang.Runnable
            public void run() {
                Looper.prepare();
                new Handler().postDelayed(new Runnable() { // from class: com.tm.androidcopysdk.BackupService.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        Log.d(BackupService.TAG, "start waiting");
                        Log.d(BackupService.TAG, "post delayed 60 startWaiting+" + BackupService.startWaiting);
                        if (!BackupService.startWaiting || (BackupService.startWaiting && !SyncAdapter.inWork)) {
                            Log.d(BackupService.TAG, "onStartCommand - stop 2");
                            BackupService.startWaiting = false;
                            SyncAdapter.inWork = false;
                            BackupService.this.stopHandler();
                            BackupService.this.stopForeground(true);
                            BackupService.this.stopSelf();
                        }
                    }
                }, 60000L);
            }
        }).start();
        Log.d(TAG, "startBackUp");
        backupData();
        startHandler();
        PeriodicEventChecker.startService(this, -1);
        WorkerIntentService.startJobService(this);
        KeepWorkerIntentService.startJobIntentService(this, false);
        CompressionService.startJobIntentService(this);
        return 1;
    }

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/BackupService$LocalBinder.class */
    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public BackupService getService() {
            return BackupService.this;
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    public int getRandomNumber() {
        return this.mGenerator.nextInt(100);
    }

    public void backupData() {
        Log.d(TAG, "backupData()");
        CommonUtils.startSyncAccount(this);
    }

    public void maintenance() {
        Log.d(TAG, " -- maintenance --  -- maintenance --");
        if (this.mDataGrabber == null) {
            this.mDataGrabber = DataGrabber.getInstance(getApplicationContext());
        } else {
            this.mDataGrabber.resetGrabber();
        }
        try {
            this.mDataGrabber.checkForNewMessagesToCopy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        EventService.startEventService(this);
    }

    public void init() {
        Log.d(TAG, "init ---->");
        this.mInit = true;
        this.mDataGrabber = DataGrabber.getInstance(getApplicationContext());
        this.mUri = new Uri.Builder().scheme(SCHEME).authority(MessageContentProvider.PROVIDER_NAME).path("messages").build();
        TableObserver observer = new TableObserver(new Handler());
        getContentResolver().registerContentObserver(this.mUri, true, observer);
        try {
            this.mDataGrabber.checkForNewMessagesToCopy();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.eUri = new Uri.Builder().scheme(SCHEME).authority(MessageContentProvider.PROVIDER_NAME).path("events").build();
        EventTableObserver eventTableObserver = new EventTableObserver(new Handler());
        getContentResolver().registerContentObserver(this.eUri, true, eventTableObserver);
        EventService.startEventService(this);
        String correctSimSerialNumber = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(SimChangedReceiver.CORRECT_SIM_SERIAL_NUMBER_KEY, null);
        if (FlavorSettings.getInstance().supportNativeMsg() && FlavorSettings.getInstance().supportSimEvent()) {
            if (Build.VERSION.SDK_INT < 23 || (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, "android.permission.READ_PHONE_STATE") == 0)) {
                if (DualSimUtils.dualSimIsOn(getApplicationContext())) {
                    SimChangedReceiver.checkDualSimEvent(this, "backup");
                } else {
                    SimChangedReceiver.checkSimEvent(this, "backup", correctSimSerialNumber);
                }
            }
        }
    }

    public static Account CreateSyncAccount(Context context) {
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService("account");
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            Log.d("aaaaaa", "new Account ! ! !");
            return newAccount;
        }
        Log.d("aaaaaa", "failed to add account");
        return newAccount;
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(SettingsEvent event) {
        if (event.settings != null && event.settings.getData() != null) {
            saving = event.settings.getData();
        }
        if (event.settings != null && event.settings.getRange() != null) {
            range = event.settings.getRange();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public synchronized void onEvent(StartConverEvent event) {
        Log.d("onMessageEvent", "onMessageEvent StartConverEvent ");
        try {
            Thread.sleep(2000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GetMessagesService.startJobIntentService(this, "com.tm.androidcopysdk.action.getCallLog");
    }

    private void startHandler() {
        if (FlavorSettings.getInstance().supportNativeMsg()) {
            Log.d(TAG, "start notification handler");
            this.notificationHandler = new Handler();
            this.notificationRunnable = new Runnable() { // from class: com.tm.androidcopysdk.BackupService.2
                @Override // java.lang.Runnable
                public void run() {
                    if (BackupService.this.notificationHandler != null) {
                        boolean isAlive = false;
                        if (Build.VERSION.SDK_INT >= 23) {
                            isAlive = BackupService.this.checkNotification();
                        }
                        long delay = isAlive ? 600000L : 60000L;
                        BackupService.this.notificationHandler.postDelayed(this, delay);
                    }
                }
            };
            this.notificationHandler.post(this.notificationRunnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @RequiresApi(api = 23)
    public boolean checkNotification() {
        boolean isAlive = false;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService("notification");
        StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (Build.VERSION.SDK_INT >= 18 && notification.getId() == 7711) {
                isAlive = true;
            }
        }
        Log.d(TAG, "checkNotification - " + isAlive);
        if (!isAlive) {
            mNotificationManager.notify(7711, showPermissionsNotification(this, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_TEXT_FOR_BACKUP_SERVICE : getString(R.string.notification_text_for_backup_service), true));
        }
        maintenance();
        return isAlive;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopHandler() {
        if (this.notificationHandler != null) {
            Log.d(TAG, "stopHandler ");
            this.notificationHandler.removeCallbacks(this.notificationRunnable);
            this.notificationHandler = null;
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        Log.d(TAG, " /////   //// onDestroy ////  ////");
        stopHandler();
        startWaiting = false;
    }
}
