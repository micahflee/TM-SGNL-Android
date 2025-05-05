package com.tm.androidcopysdk;

import android.accounts.Account;
import android.app.ActivityManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.OutOfQuotaPolicy;
import androidx.work.WorkManager;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tm.androidcopysdk.database.SQLFactory;
import com.tm.androidcopysdk.database.UriUtils;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.events.EventController;
import com.tm.androidcopysdk.events.EventFactory;
import com.tm.androidcopysdk.events.PeriodicEventChecker;
import com.tm.androidcopysdk.utils.Contact;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import com.tm.androidcopysdk.version_aoutomatic_rollout.VersionAutomaticRollOutManagerKt;
import com.tm.logger.Log;
import com.tm.utils.Definitions;
import com.tm.utils.Util;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.greenrobot.eventbus.EventBus;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/CommonUtils.class */
public class CommonUtils {
    private static final String COUNT_OF_MESSAGES = "COUNT_OF_MESSAGES";
    private static final int REQUEST_CODE = 12;
    private static final int GENERAL_NOTIFICATION_ID = 1132;
    public static final String SAVED_FOLDER_KEY_PREF = "SAVED_FOLDER_KEY_PREF";
    public static final String SAVED_URI_KEY_PREF = "SAVED_URI_KEY_PREF";
    public static final String WIZARD_SHOWN = "wizard_shown";
    static String TAG = "CommonUtils";
    static final int bufferSize = 256000;

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/CommonUtils$RECORDING_TYPE.class */
    public enum RECORDING_TYPE {
        NONE,
        LOGS,
        RECORDING
    }

    public static boolean isWifiOr3GConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService("connectivity");
        boolean isWifi = false;
        boolean is3g = false;
        if (Build.VERSION.SDK_INT >= 21) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
                is3g = networkInfo.getType() == 0;
                isWifi = networkInfo.getType() == 1;
            }
        } else {
            isWifi = manager.getNetworkInfo(1).isConnectedOrConnecting();
            is3g = manager.getNetworkInfo(0).isConnectedOrConnecting();
        }
        Log.d("network test:", " wifi is connected:" + isWifi + "  3G is connected:" + is3g);
        return is3g || isWifi;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService("connectivity");
        if (Build.VERSION.SDK_INT >= 21) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnectedOrConnecting() && networkInfo.getType() == 1;
        }
        return manager.getNetworkInfo(1).isConnectedOrConnecting();
    }

    public static boolean isCharging(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            BatteryManager batteryManager = (BatteryManager) context.getSystemService("batterymanager");
            return batteryManager.isCharging();
        }
        IntentFilter filter = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        Intent intent = context.registerReceiver(null, filter);
        int status = intent.getIntExtra("status", -1);
        if (status == 2 || status == 5) {
            return true;
        }
        return false;
    }

    public static void startSyncAccount(Context context) {
        if (SyncAdapter.ioException > 0) {
            Log.e("DEBUG DEBUG startSyncAccount", "\n********************************************");
            Log.e("DEBUG DEBUG startSyncAccount", "there are ioException, wait for start again");
            Log.e("DEBUG DEBUG startSyncAccount", "********************************************\n");
        } else if (SyncAdapter.inWork) {
            SyncAdapter.doAgaing = true;
        } else {
            Log.d("DEBUG DEBUG startSyncAccount", "DEBUG startSyncAccount !!!!!!!!!!! !!!!!!! DEBUG");
            Account account = BackupService.CreateSyncAccount(context);
            Bundle bundle = SyncAdapter.getBundleForRequestSync(context);
            ContentResolver.requestSync(account, BackupService.AUTHORITY, bundle);
        }
    }

    public static void setArchivedMessagesCount(Context context, MessageType messageType, int add) {
        int count = getCountArchivedMessages(context, messageType);
        setCountArchivedMessages(context, messageType, count + add);
    }

    public static void setCountArchivedMessages(Context context, MessageType messageType) {
        int count = getCountArchivedMessages(context, messageType);
        setCountArchivedMessages(context, messageType, count + 1);
    }

    public static void setCountArchivedMessages(Context context, MessageType messageType, int count) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(messageType.name() + COUNT_OF_MESSAGES, count).apply();
    }

    public static int getCountArchivedMessages(Context context, MessageType messageType) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(messageType.name() + COUNT_OF_MESSAGES, 0);
    }

    public static String getUniqueId(String id) {
        if (FlavorSettings.getInstance().isNeedToCreateUniqueMessagesId()) {
            return id + "-" + UUID.randomUUID().toString();
        }
        return id;
    }

    public static void copyFromStorage(Context context, Uri src, File dst) throws IOException {
        InputStream in = context.getContentResolver().openInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            byte[] buf = new byte[409600];
            while (true) {
                int len = in.read(buf);
                if (len > 0) {
                    out.write(buf, 0, len);
                } else {
                    out.close();
                    return;
                }
            }
        } finally {
            in.close();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static void copy(Context context, File src, File dst, boolean enc, boolean dec) throws IOException {
        InputStream in = new FileInputStream(src);
        if (dec) {
            in = UriUtils.getDecipheredInStream(context, in);
        }
        try {
            OutputStream out = new FileOutputStream(dst);
            if (enc) {
                out = UriUtils.getDecipheredOutStream(context, out);
            }
            byte[] buf = new byte[409600];
            while (true) {
                int len = in.read(buf);
                if (len > 0) {
                    out.write(buf, 0, len);
                } else {
                    out.close();
                    return;
                }
            }
        } finally {
            in.close();
        }
    }

    public static File exportFile(Context context, File src, File dest, File expFile, boolean enc) throws IOException {
        Log.d("exportFile", "start export file from " + src.getAbsolutePath() + " to " + expFile.getAbsolutePath());
        if (!dest.exists() && !dest.mkdir()) {
            Log.e("exportFile", "couldn't create directory in external storage");
            return null;
        }
        copy(context, src, expFile, enc, false);
        Log.d("exportFile", "src File size: " + src.length());
        return expFile;
    }

    public static int convertInputStreamToString64(InputStream input, StringBuilder stringBuilder) throws IOException {
        int i = 0;
        while (true) {
            int total = i;
            byte[] bytes = readBytes(input);
            if (bytes != null) {
                String str = Base64.encodeToString(bytes, 2);
                stringBuilder.append(str);
                i = total + bytes.length;
            } else {
                return total;
            }
        }
    }

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int len = inputStream.read(buffer);
        if (len != -1) {
            byteBuffer.write(buffer, 0, len);
            return byteBuffer.toByteArray();
        }
        return null;
    }

    public static String getFormattedDateFromTimestamp(long timestampInMilliSeconds) {
        Date date = new Date();
        date.setTime(timestampInMilliSeconds);
        String formattedDate = new SimpleDateFormat("MMM d, yyyy").format(date);
        return formattedDate;
    }

    public static void addEvent(Context context, EventAbsObj.EventType eventType) {
        if (appIsWorking(context)) {
            EventAbsObj eventAbsObj = EventFactory.createEvent(context, eventType);
            EventController.getInstance(context).InsertEvent(eventAbsObj);
            startBackupService(context);
            PeriodicEventChecker.startService(context, -1);
        }
    }

    public static void onlyInsertEvent(Context context, EventAbsObj.EventType eventType) {
        EventAbsObj eventAbsObj = EventFactory.createEvent(context, eventType);
        EventController.getInstance(context).InsertEvent(eventAbsObj);
    }

    public static void addUpdateVersionEvent(Context context, EventAbsObj.EventType type) {
        EventAbsObj event = EventFactory.createEvent(context, type);
        event.updateVersionNumber(context);
        EventController.getInstance(context).InsertEvent(event);
    }

    public static void addDualSimEvent(Context context, String message) {
        EventAbsObj event = EventFactory.createEvent(context, EventAbsObj.EventType.ChangedDualSimCardEvent);
        event.setDescription(message);
        EventController.getInstance(context).InsertEvent(event);
        startBackupService(context);
    }

    public static void addMissingPermissionsEvent(Context context, EventAbsObj.EventType type, String missingPermissions) {
        EventAbsObj event = EventFactory.createEvent(context, type);
        event.setDescription(missingPermissions);
        EventController.getInstance(context).InsertEvent(event);
        startBackupService(context);
    }

    public static void addFailedRecordingsEvent(Context context, EventAbsObj.EventType type, String failedRecordingsDescription) {
        EventAbsObj event = EventFactory.createEvent(context, type);
        event.setDescription(failedRecordingsDescription);
        EventController.getInstance(context).InsertEvent(event);
        startBackupService(context);
    }

    public static void addRecordingTerminatedDuringTheCallEvent(Context context, EventAbsObj.EventType type, String recordingTerminatedDuringTheCallDescription) {
        EventAbsObj event = EventFactory.createEvent(context, type);
        event.setDescription(recordingTerminatedDuringTheCallDescription);
        EventController.getInstance(context).InsertEvent(event);
        startBackupService(context);
    }

    public static void addNoSupportForCallRecordingEvent(Context context, EventAbsObj.EventType type, String noSupportForCallRecordingEventDescription) {
    }

    public static void updateUserEvent(Context context, boolean isSuccess, Map<String, String> updateData) {
        EventAbsObj event = EventFactory.createEvent(context, EventAbsObj.EventType.UpdateUserEvent);
        String description = "Is success to update = " + Boolean.toString(isSuccess);
        for (Map.Entry<String, String> entry : updateData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            description = description + " " + key + " " + value;
        }
        event.setDescription(description);
        EventController.getInstance(context).InsertEvent(event);
        startBackupService(context);
    }

    public static boolean checkPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == 0;
    }

    public static boolean appIsWorking(Context context) {
        boolean ac = PrefManager.getBooleanPref(context, PrefManagerConstants.SHARED_PREFERENCE_ACTIVATED_AA_KEY, false);
        Log.d(TAG, "appIsWorking? " + ac);
        return ac;
    }

    public static boolean isUserArchive(Context context) {
        return !appIsWorking(context) && FlavorSettings.getInstance().isSupportSuspendUser();
    }

    public static boolean isActivatedUser(Context context) {
        boolean isActivatedUser = PrefManager.getBooleanPref(context, PrefManagerConstants.SHARED_PREFERENCE_ACTIVATED_USER, false);
        Log.d(TAG, "isActivatedUser " + isActivatedUser);
        return isActivatedUser;
    }

    public static void setActivatedUser(Context context, boolean isActivatedUser) {
        Log.d(TAG, "setActivatedUser " + isActivatedUser);
        PrefManager.setBooleanPref(context, PrefManagerConstants.SHARED_PREFERENCE_ACTIVATED_USER, isActivatedUser);
    }

    public static String versionNumber(Context context) {
        String versionNumber = "";
        try {
            versionNumber = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionNumber;
    }

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/CommonUtils$CCODES.class */
    public enum CCODES {
        IL(0, "IL", "972", 8, 10),
        FR(1, "FR", "33", 9, 10),
        US(2, "US", "1", 9, 10),
        CA(3, "CA", "1", 9, 10),
        GB(4, "GB", "44", 9, 11),
        DE(5, "DE", "49", 10, 12),
        IT(6, "IT", "39", 8, 11),
        AD(7, "AD", "376", 5, 6),
        ES(8, "ES", "34", 9, 10),
        DEFAULT(9, "", "", 8, 11);
        
        private final int id;
        private final String country;
        private final String prefix;
        private final int minLocal;
        private final int maxLocal;

        CCODES(int id, String country, String prefix, int minLocal, int maxLocal) {
            this.id = id;
            this.country = country;
            this.prefix = prefix;
            this.minLocal = minLocal;
            this.maxLocal = maxLocal;
        }

        public int getId() {
            return this.id;
        }

        public String getCountry() {
            return this.country;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public int getMinLocal() {
            return this.minLocal;
        }

        public int getMaxLocal() {
            return this.maxLocal;
        }

        public static CCODES getsCountryByName(String country) {
            CCODES[] values;
            if (country != null) {
                for (CCODES ccodes : values()) {
                    if (ccodes.getCountry().equalsIgnoreCase(country)) {
                        return ccodes;
                    }
                }
            }
            return DEFAULT;
        }
    }

    public static void showPermissionsNotification(Context context, String message, int notificationId, String channelId, String channelName, String intentExtraKey, int intentExtraNotificationId) {
        Class activityClass = Util.getMainActivityClass((Application) context.getApplicationContext());
        PendingIntent pending = null;
        if (activityClass != null) {
            Intent intent = new Intent(context, activityClass);
            intent.putExtra(intentExtraKey, intentExtraNotificationId);
            if (Build.VERSION.SDK_INT >= 31) {
                pending = PendingIntent.getActivity(context, 0, intent, 167772160);
            } else {
                pending = PendingIntent.getActivity(context, 0, intent, 134217728);
            }
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, 4);
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId).setContentTitle(StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_TITLE : context.getString(R.string.notification_title)).setContentText(message).setSmallIcon(R.drawable.icon_aa).setAutoCancel(true).setOnlyAlertOnce(true);
        if (pending != null) {
            mBuilder.setContentIntent(pending);
        }
        notificationManager.notify(notificationId, mBuilder.build());
    }

    public static void showGeneralNotification(Context context, String message, String link) {
        PendingIntent pending;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel mChannel = new NotificationChannel(StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.CHANEL_ID_GENERAL : context.getString(R.string.chanel_id_general), StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.CHANEL_GENERAL : context.getString(R.string.chanel_general), 4);
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.CHANEL_ID_GENERAL : context.getString(R.string.chanel_id_general)).setContentTitle(StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_TITLE : context.getString(R.string.notification_title)).setContentText(message).setSmallIcon(R.drawable.icon_aa).setAutoCancel(true).setOnlyAlertOnce(true);
        if (link != null) {
            Intent notificationIntent = new Intent("android.intent.action.VIEW");
            notificationIntent.setData(Uri.parse(link));
            if (Build.VERSION.SDK_INT >= 31) {
                pending = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, 167772160);
            } else {
                pending = PendingIntent.getActivity(context.getApplicationContext(), 0, notificationIntent, 134217728);
            }
            mBuilder.setContentIntent(pending);
        }
        notificationManager.notify(GENERAL_NOTIFICATION_ID, mBuilder.build());
    }

    public static boolean handleSmsPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            int readSmsPermission = ActivityCompat.checkSelfPermission(context, "android.permission.READ_SMS");
            if (readSmsPermission == 0) {
                return true;
            }
            return false;
        }
        return true;
    }

    public static boolean handleCallLogPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            PreferenceManager.getDefaultSharedPreferences(context).getBoolean("recording", false);
            int readCallLogsPermission = ActivityCompat.checkSelfPermission(context, "android.permission.READ_CALL_LOG");
            ActivityCompat.checkSelfPermission(context, "android.permission.RECORD_AUDIO");
            if (readCallLogsPermission == 0) {
                return true;
            }
            return false;
        }
        return true;
    }

    public static void startBackupService(Context context) {
        Log.d("start service", "start startBackupService");
        Log.d("start service", "reset SyncAdapter.ioException = 0");
        SyncAdapter.ioException = 0;
        if (!appIsWorking(context)) {
            Log.d("start service", "no user !!!! exit");
        } else if (!FlavorSettings.getInstance().supportNativeMsg() && Build.VERSION.SDK_INT > 30) {
            Log.d("start service", "try to start worker");
            if (!FlavorSettings.getInstance().getFlavorName().equalsIgnoreCase("WPA")) {
                if (!BackupWorker.stopWork) {
                    OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(BackupWorker.class);
                    builder.setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST);
                    WorkManager.getInstance(context).enqueue(builder.build());
                }
            } else if (!BackupJobIntentService.stopWork) {
                BackupJobIntentService.startJobIntentService(context);
            }
        } else if (!isMyServiceRunning(context, BackupService.class)) {
            Log.d("start service", "startBackupService - help");
            Intent i = new Intent();
            i.setClass(context, BackupService.class);
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(i);
            } else {
                context.startService(i);
            }
        } else {
            Log.d("start service", "already run ... ");
        }
    }

    public static void stopBackupService(Context context, boolean enforce) {
        if (!FlavorSettings.getInstance().supportNativeMsg() || enforce) {
            Log.d("lior stop service", "stop startBackupService");
            EventBus.getDefault().post(new StopBackupWorker());
            if (Build.VERSION.SDK_INT >= 21) {
            }
            if (isMyServiceRunning(context, BackupService.class)) {
                Log.d("stop service", "stopBackupService - help");
                Intent i = new Intent();
                i.setClass(context, BackupService.class);
                i.putExtra("stop", true);
                context.startService(i);
                if (Build.VERSION.SDK_INT >= 21) {
                }
            }
        }
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService("activity");
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String resolveJoinedPhoneNumbers(String[] phoneNumbers, Contact[] names) {
        if (phoneNumbers == null || phoneNumbers.length == 0) {
            return "EMPTY_ADDRESS";
        }
        List<String> mappedPhoneNumbers = new ArrayList<>();
        int i = 0;
        while (i < phoneNumbers.length) {
            String phoneNumber = phoneNumbers[i];
            Contact name = names.length > i ? names[i] : null;
            mappedPhoneNumbers.add(resolvePhoneNumberOrName(phoneNumber, name));
            i++;
        }
        return TextUtils.join(";", mappedPhoneNumbers);
    }

    public static String resolvePhoneNumberOrName(String phoneNumber, Contact name) {
        return resolvePhoneNumberOrName(phoneNumber, name == null ? null : name.toString());
    }

    public static String resolvePhoneNumberOrName(String phoneNumber, String name) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return name != null ? name : "";
        } else if (isValidPhoneNumber(phoneNumber)) {
            return formatNumber(phoneNumber);
        } else {
            return name != null ? name : "";
        }
    }

    public static String formatNumber(String phone) {
        char c;
        String ret = phone.replace("+", "");
        String ret2 = ret.replace(" ", "").replace("-", "").replace("(", "").replace(")", "").replace(".", "").toLowerCase();
        StringBuilder s = new StringBuilder();
        char[] charArray = ret2.toCharArray();
        int length = charArray.length;
        for (int i = 0; i < length && (c = charArray[i]) != ';' && c != '#' && c != ',' && c != '/' && c != 'e' && c != 'x'; i++) {
            s.append(c);
        }
        return s.toString();
    }

    public static String formatNumberWithoutPlus(String phone) {
        char c;
        String ret = phone.replace("+", "");
        String ret2 = ret.replace(" ", "").replace("-", "").replace("(", "").replace(")", "").replace(".", "").toLowerCase();
        StringBuilder s = new StringBuilder();
        char[] charArray = ret2.toCharArray();
        int length = charArray.length;
        for (int i = 0; i < length && (c = charArray[i]) != ';' && c != '#' && c != ',' && c != '/' && c != 'e' && c != 'x'; i++) {
            s.append(c);
        }
        return s.toString();
    }

    public static boolean isDefaultRecorder(Context context) {
        boolean ret = false;
        if (AudioSettingsManager.getInstance(context).getAudioSettings().getndk_in().equalsIgnoreCase("default_app")) {
            ret = true;
        }
        return ret;
    }

    public static void removeSavedCallFolder(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove(SAVED_FOLDER_KEY_PREF).apply();
    }

    public static void setSavedCallFolderPref(Context context, String folder) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SAVED_FOLDER_KEY_PREF, folder).apply();
    }

    public static void setSavedUriFolderPref(Context context, String uri) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SAVED_URI_KEY_PREF, uri).apply();
    }

    public static String getSavedCallFolderPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SAVED_FOLDER_KEY_PREF, "");
    }

    public static String getSavedUriFolderPref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SAVED_URI_KEY_PREF, "");
    }

    public static boolean ifNeedFolderEvent(Context context) {
        if (FlavorSettings.getInstance().oldVersion() && getSavedCallFolderPref(context).isEmpty() && PrefManager.getBooleanPref(context, "recording") && Build.VERSION.SDK_INT >= 28) {
            return true;
        }
        return false;
    }

    public static boolean ifNeedToShowWizard(Context context) {
        if (FlavorSettings.getInstance().oldVersion() && !PrefManager.getBooleanPref(context, WIZARD_SHOWN) && PrefManager.getBooleanPref(context, "recording") && Build.VERSION.SDK_INT >= 28) {
            return true;
        }
        return false;
    }

    public static File exportFileAndDec(Context context, File src, File dest, File expFile, boolean enc) throws IOException {
        Log.d("exportFile", "start decANDexportFile file from " + src.getAbsolutePath() + " to " + expFile.getAbsolutePath());
        if (!dest.exists() && !dest.mkdir()) {
            Log.e("exportFile", "couldn't create directory in external storage");
            return null;
        }
        copy(context, src, expFile, enc, true);
        Log.d("exportFile", "src File size: " + src.length());
        return expFile;
    }

    public static void exportAndEncrypt(Context context, String msgId, File convertedFile) {
        String destPath = context.getFilesDir() + "/" + msgId + "/";
        File dst = new File(destPath);
        File expFile = new File(destPath + convertedFile.getName().replaceAll(".mp3", ""));
        try {
            exportFile(context, convertedFile, dst, expFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNeedToShowMinVersionDialog(Context context) {
        boolean appActivated = PrefManager.getBooleanPref(context, PrefManagerConstants.SHARED_PREFERENCE_ACTIVATED_AA_KEY, false);
        String minVersion = PrefManager.getStringPref(context, VersionAutomaticRollOutManagerKt.MIN_VERSION_KEY);
        boolean isEnabledDistributionOfNewVersiom = PrefManager.getBooleanPref(context, VersionAutomaticRollOutManagerKt.IS_ENABLED_DISTRIBUTION_OF_NEW_VERSION);
        if (!TextUtils.isEmpty(minVersion)) {
            Log.d("MinMaxVersionDialog", "!TextUtils.isEmpty(minVersion) = " + (!TextUtils.isEmpty(minVersion)) + "  BuildConfig.VERSION_CODE : " + BuildConfig.VERSION_CODE + "____ Integer.parseInt(minVersion) = " + minVersion + "  appActivated = " + appActivated + " isEnabledDistributionOfNewVersiom = " + isEnabledDistributionOfNewVersiom);
        }
        try {
            Integer.parseInt(minVersion);
        } catch (NumberFormatException e) {
            Log.d("MinMaxVersionDialog", "wrong minVersion :" + minVersion);
            minVersion = "";
        }
        boolean ret = isEnabledDistributionOfNewVersiom && !TextUtils.isEmpty(minVersion) && BuildConfig.VERSION_CODE < ((long) Integer.parseInt(minVersion)) && appActivated;
        Log.d("MinMaxVersionDialog ", "isNeedToShowMaxVersionDialog == " + ret);
        return ret;
    }

    public static boolean isNeedToShowMaxVersionDialog(Context context) {
        boolean appActivated = PrefManager.getBooleanPref(context, PrefManagerConstants.SHARED_PREFERENCE_ACTIVATED_AA_KEY, false);
        String maxVersion = PrefManager.getStringPref(context, VersionAutomaticRollOutManagerKt.MAX_VERSION_KEY);
        boolean isNeedToShow = PrefManager.getBooleanPref(context, VersionAutomaticRollOutManagerKt.IS_NEED_TO_SHOW_MAX_VERSION);
        boolean isEnabledDistributionOfNewVersiom = PrefManager.getBooleanPref(context, VersionAutomaticRollOutManagerKt.IS_ENABLED_DISTRIBUTION_OF_NEW_VERSION);
        if (!TextUtils.isEmpty(maxVersion)) {
            Log.d("MinMaxVersionDialog", "!TextUtils.isEmpty(maxVersion) = " + (!TextUtils.isEmpty(maxVersion)) + " BuildConfig.VERSION_CODE :" + BuildConfig.VERSION_CODE + " Integer.parseInt(maxVersion) = " + maxVersion + "  appActivated = " + appActivated + "  isNeedToShow = " + isNeedToShow + "  isEnabledDistributionOfNewVersiom = " + isEnabledDistributionOfNewVersiom);
        }
        try {
            Integer.parseInt(maxVersion);
        } catch (NumberFormatException e) {
            Log.d("MinMaxVersionDialog", "wrong maxVersion :" + maxVersion);
            maxVersion = "";
        }
        boolean ret = isEnabledDistributionOfNewVersiom && !TextUtils.isEmpty(maxVersion) && BuildConfig.VERSION_CODE < ((long) Integer.parseInt(maxVersion)) && appActivated && isNeedToShow;
        Log.d("MinMaxVersionDialog ", "isNeedToShowMaxVersionDialog == " + ret);
        return ret;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static void setUrl(Context context, String chosenUrl, String keeperUrl) {
        if (TextUtils.isEmpty(chosenUrl)) {
            Definitions.setBaseUrl(Definitions.BaseUrl);
        } else {
            Definitions.setBaseUrl(chosenUrl);
        }
        SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
        if (!TextUtils.isEmpty(keeperUrl)) {
            ed.putString("keeperUrl", keeperUrl);
        } else {
            ed.putString("keeperUrl", Definitions.ArchiveUrl);
        }
        if (!Definitions.getBase().contentEquals("https://rest.telemessage.com")) {
            ed.putString("baseurl", chosenUrl);
        }
        ed.commit();
    }

    public static void setSqlInfo(Context context, String sqlInfo) {
        String password = TMCredentialsStore.getInstance(context).password(context);
        if (!TextUtils.isEmpty(password)) {
            SQLFactory.getSQLinfo(context, password);
        }
    }

    public static String getInternationalNumber(Context context, String number) {
        if (TextUtils.isEmpty(number)) {
            Log.e("ContactsUtils", "getInternationalNumber call with empty number ");
            return number;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        String countryCode = tm.getSimCountryIso();
        Log.d("ContactsUtils", "getLocalNumber countryCode: " + countryCode);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber numberProto = phoneUtil.parse(number, countryCode.toUpperCase());
            if (numberProto == null) {
                Log.e("ContactsUtils", "numberProto is null for number = " + number);
                return number;
            }
            boolean keepGoing = true;
            try {
                PhoneNumberUtil.PhoneNumberType phoneNumberType = phoneUtil.getNumberType(numberProto);
                keepGoing = phoneNumberType == PhoneNumberUtil.PhoneNumberType.UNKNOWN;
            } catch (Exception e) {
                Log.e("ContactsUtils", "Exception was thrown: " + e.toString(), e);
            }
            Log.d("phoneUtils", "1) " + keepGoing);
            if (keepGoing) {
                try {
                    numberProto = phoneUtil.parse(number, Locale.getDefault().getCountry());
                } catch (NumberParseException e2) {
                    e2.printStackTrace();
                }
                try {
                    PhoneNumberUtil.PhoneNumberType phoneNumberType2 = phoneUtil.getNumberType(numberProto);
                    keepGoing = phoneNumberType2 == PhoneNumberUtil.PhoneNumberType.UNKNOWN;
                } catch (Exception e3) {
                    Log.e("ContactsUtils", "Exception was thrown2222: " + e3.toString(), e3);
                }
                Log.d("phoneUtils", "2) " + keepGoing);
                if (keepGoing) {
                    try {
                        numberProto = phoneUtil.parse("+" + number, Locale.getDefault().getCountry());
                    } catch (NumberParseException e4) {
                        e4.printStackTrace();
                    }
                    PhoneNumberUtil.PhoneNumberType phoneNumberType3 = phoneUtil.getNumberType(numberProto);
                    keepGoing = phoneNumberType3 == PhoneNumberUtil.PhoneNumberType.UNKNOWN;
                    Log.d("phoneUtils", "3) " + keepGoing);
                }
            }
            Log.d("phoneUtils", "4) " + keepGoing);
            String ret = numberProto != null ? phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL) : number;
            if (!TextUtils.isEmpty(ret)) {
                Log.d("ContactsUtils", "getINTERNATIONALNumber: " + ret);
                Log.d("ContactsUtils", numberProto.toString());
            } else {
                ret = number;
                Log.d("ContactsUtils", "getINTERNATIONALNumber: failed");
            }
            return ret;
        } catch (NumberParseException e5) {
            Log.e("ContactsUtils", "NumberParseException was thrown: " + e5.toString(), e5);
            return number;
        }
    }

    public static <T> boolean equalsSet(Collection<T> lhs, Collection<T> rhs) {
        return lhs.size() == rhs.size() && lhs.containsAll(rhs) && rhs.containsAll(lhs);
    }

    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile("^([0-9\\+]|\\(\\d{1,3}\\))[0-9\\-\\. ]{3,15}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getApplicationContext().getPackageManager().getPackageInfo(context.getApplicationContext().getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "";
        }
    }

    public static RECORDING_TYPE getRecordingStatus(Context context) {
        RECORDING_TYPE ret;
        int tmp = PrefManager.getIntPref(context, "recording_type", 0);
        Log.d("getRecordingStatus", "getRecordingStatus = " + tmp);
        if (tmp == 0 || tmp > 2) {
            ret = RECORDING_TYPE.NONE;
        } else if (tmp == 1) {
            ret = RECORDING_TYPE.LOGS;
        } else {
            ret = RECORDING_TYPE.RECORDING;
        }
        return ret;
    }

    public static void setMissPermissionFlag(Context context, boolean flag) {
        Log.d("start_archive_flag", "setMissPermissionFlag");
        boolean state = PrefManager.getBooleanPref(context.getApplicationContext(), PrefManagerConstants.SHARED_PREFERENCE_START_ARCHIVE_KEY, false);
        if (!state) {
            Log.d("start_archive_flag", "start save sms permission -> " + flag);
            PrefManager.setBooleanPref(context.getApplicationContext(), PrefManagerConstants.SHARED_PREFERENCE_START_ARCHIVE_KEY, flag);
            long currentTime = System.currentTimeMillis();
            Log.d("start_archive_flag", "set stop time to: " + currentTime);
            if (flag) {
                PrefManager.setLongPref(context.getApplicationContext(), PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, currentTime);
            }
        }
    }

    public static boolean getMissPermissionFlag(Context context, boolean def) {
        return PrefManager.getBooleanPref(context.getApplicationContext(), PrefManagerConstants.SHARED_PREFERENCE_START_ARCHIVE_KEY, def);
    }

    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(System.currentTimeMillis());
        String date = DateFormat.format("dd-MM-yyyy HH:mm", cal).toString();
        return date;
    }

    public static boolean copyFileByInputStream(InputStream in, File out) {
        Log.d("fix_file", "copyFileByInputStream");
        try {
            return copyFileByInputStream(in, new FileOutputStream(out));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyFileByInputStream(InputStream input, OutputStream output) {
        Log.d("fix_file", "copyFileByInputStream2");
        try {
            try {
                byte[] buf = new byte[1024];
                while (true) {
                    int len = input.read(buf);
                    if (len <= 0) {
                        break;
                    }
                    output.write(buf, 0, len);
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (Exception e) {
                        return true;
                    }
                }
                if (output != null) {
                    output.close();
                }
                return true;
            } catch (Exception e2) {
                Log.e("fix_file", "copyFileByInputStream", e2);
                if (input != null) {
                    try {
                        input.close();
                    } catch (Exception e3) {
                        return false;
                    }
                }
                if (output != null) {
                    output.close();
                }
                return false;
            }
        } catch (Throwable th) {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception e4) {
                    throw th;
                }
            }
            if (output != null) {
                output.close();
            }
            throw th;
        }
    }

    public static boolean isValidMobileNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(phone, Locale.getDefault().getCountry());
            PhoneNumberUtil.PhoneNumberType phoneNumberType = phoneNumberUtil.getNumberType(phoneNumber);
            return phoneNumberType == PhoneNumberUtil.PhoneNumberType.MOBILE;
        } catch (Exception e) {
            return false;
        }
    }
}
