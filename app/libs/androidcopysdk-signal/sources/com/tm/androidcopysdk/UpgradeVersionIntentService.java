package com.tm.androidcopysdk;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import com.tm.INetworkProvider;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.database.SQLFactory;
import com.tm.androidcopysdk.events.EventService;
import com.tm.androidcopysdk.network.appSettings.WorkerIntentService;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import com.tm.logger.Log;
import java.io.File;
import java.io.IOException;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/UpgradeVersionIntentService.class */
public class UpgradeVersionIntentService extends JobIntentService {
    private static final String TAG = "UpgradeVersionIntentService";

    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG, "onHandleIntent start");
        handleUpdate();
    }

    private static void enqueueWork(Context upgradeVersionIntentService, Intent compIntent) {
        enqueueWork(upgradeVersionIntentService, UpgradeVersionIntentService.class, DefinitionsSDKKt.UPGRADE_VERSION_SERVICE_JOB_ID, compIntent);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void handleUpdate() {
        TMCredentialsStore.getInstance(this).loadFirstTimeSync(this);
        if (PrefManager.getBooleanPref((Context) this, PrefManagerConstants.SHARED_PREFERENCE_ACTIVATED_AA_KEY, false)) {
            String userName = PrefManager.getStringPref((Context) this, "username", "");
            String password = PrefManager.getStringPref((Context) this, "password", "");
            TMCredentialsStore.getInstance(this).setUserName(this, userName);
            TMCredentialsStore.getInstance(this).setPassword(this, password);
            SQLFactory.changeDataBasePassword(this);
            encryptAllFiles(this, true);
            PrefManager.setStringPref((Context) this, "username", "");
            PrefManager.setStringPref((Context) this, "password", "");
            CommonUtils.startBackupService(this);
            WorkerIntentService.startJobService(this);
            INetworkProvider provider = getApplication().networkProvider();
            provider.headersInterceptor().setAuthentication(TMCredentialsStore.getInstance(this).userName(this), TMCredentialsStore.getInstance(this).password(this));
        }
        PrefManager.setBooleanPref((Context) this, PrefManagerConstants.SHARED_PREFERENCE_UPGRADE_ENCRYPTION_AA_KEY, true);
        EventService.startEventService(this);
    }

    public static void encryptAllFiles(Context context, boolean write) {
        long installation_date = PrefManager.getLongPref(context.getApplicationContext(), PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, PreferenceManager.getDefaultSharedPreferences(context).getLong(PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, 0L));
        String[] condition = {String.valueOf(installation_date), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.name(), MessageType.MMS.name(), MessageType.CallLog.name()};
        Cursor cur = context.getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "date >= ? AND status = ? AND (type = ? OR type = ?)", condition, "date DESC");
        if (cur != null) {
            while (cur.moveToNext()) {
                String mid = cur.getString(cur.getColumnIndex("_id"));
                String filename = cur.getString(cur.getColumnIndex("_data"));
                if (!TextUtils.isEmpty(filename)) {
                    String srcPath = context.getFilesDir() + "/" + mid + "/" + Uri.parse(filename).getLastPathSegment();
                    String renamePath = srcPath + "(1)";
                    File srcFile = new File(srcPath);
                    File renameFile = new File(renamePath);
                    if (srcFile.renameTo(renameFile)) {
                        File srcFile2 = new File(srcPath);
                        if (write) {
                            try {
                                CommonUtils.copy(context, renameFile, srcFile2, true, false);
                            } catch (IOException e) {
                                Log.e(TAG, "failed to encrypt", e);
                            }
                        } else {
                            CommonUtils.copy(context, renameFile, srcFile2, false, true);
                        }
                        if (!renameFile.delete()) {
                            Log.e(TAG, "failed to delete file ;renamePath  = " + renamePath);
                        }
                    } else {
                        Log.e(TAG, "failed to renamed file ;srcPath  = " + srcPath);
                    }
                }
            }
            cur.close();
        }
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, UpgradeVersionIntentService.class);
        enqueueWork(context, intent);
    }
}
