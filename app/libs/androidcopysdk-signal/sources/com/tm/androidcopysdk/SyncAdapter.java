package com.tm.androidcopysdk;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.annotation.RequiresApi;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.database.DBKeepAliveQueryHelper;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.database.UriUtils;
import com.tm.androidcopysdk.device.SdkMigrator;
import com.tm.androidcopysdk.network.Attachment;
import com.tm.androidcopysdk.network.BodyBase;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.network.MMSMessageRecorder;
import com.tm.androidcopysdk.network.NetworkManager;
import com.tm.androidcopysdk.network.SMSMessageRecord;
import com.tm.androidcopysdk.network.TeleMessageMMSArchive;
import com.tm.androidcopysdk.network.TelemessageArchiverMessage;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
import com.tm.logger.Log;
import com.tm.utils.Definitions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Response;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/SyncAdapter.class */
public class SyncAdapter extends AbstractThreadedSyncAdapter implements HandleResponseListener {
    ContentResolver mContentResolver;
    Context mContext;
    public static boolean doAgaing = false;
    public static int ioException = 0;
    public static boolean inWork = false;
    public static final String TIME_WHEN_WAS_DELETED_DB = "TIME_WHEN_WAS_DELETED_DB";
    public static final long MAX_TOTAL_ATTACHMENTS_SIZE_IN_MB = 25;
    public static final long MAX_TOTAL_ATTACHMENTS_SIZE = 26214400;
    private static final String TAG = "SyncAdapter";

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mContentResolver = context.getContentResolver();
        this.mContext = context;
        SdkMigrator.getInstance(context.getApplicationContext()).migrate();
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.mContentResolver = context.getContentResolver();
        this.mContext = context;
        SdkMigrator.getInstance(context.getApplicationContext()).migrate();
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith("unknown") || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) || "google_sdk".equals(Build.PRODUCT);
    }

    @Override // android.content.AbstractThreadedSyncAdapter
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Uri messageToUpdate;
        int count;
        inWork = true;
        BackupService.startWaiting = false;
        Log.d("Sync adapter", "onPerformSync");
        CommonUtils.isWifiOr3GConnection(this.mContext);
        if (CommonUtils.appIsWorking(this.mContext)) {
            boolean syncRegular = true;
            if (0 != 0 && !CommonUtils.isWifiConnected(this.mContext)) {
                syncRegular = false;
            }
            boolean doHistoric = false;
            boolean doHistoricNow = true;
            boolean startListen = false;
            Boolean isArchiveRequested = Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_HISTORY_ARCHIVE_CLICKED_KEY, false));
            if (isArchiveRequested.booleanValue()) {
                Boolean isArchiveHistoryDone = Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_HISTORY_ARCHIVE_DONE_KEY, false));
                if (!isArchiveHistoryDone.booleanValue()) {
                    doHistoric = true;
                    Boolean isSaveBatteryHistoricMessage = Boolean.valueOf(PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_SAVE_BATTERY_PREF_KEY, false));
                    if (isSaveBatteryHistoricMessage.booleanValue() && (!CommonUtils.isWifiConnected(this.mContext) || !CommonUtils.isCharging(this.mContext))) {
                        doHistoricNow = false;
                    }
                }
            }
            String baseurl = bundle.getString("baseurl");
            String keeperUrl = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("keeperUrl", null);
            if (!TextUtils.isEmpty(keeperUrl)) {
                baseurl = keeperUrl;
            }
            NetworkManager nm = new NetworkManager(this.mContext, baseurl);
            String myNumber = FlavorSettings.getInstance().getMSISDN(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("phonenumber", "999999999"));
            do {
                doAgaing = false;
                long installation_date = PrefManager.getLongPref(this.mContext.getApplicationContext(), PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, PreferenceManager.getDefaultSharedPreferences(this.mContext).getLong(PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, 0L));
                Log.d(TAG, "installation_date: " + installation_date);
                Log.d(TAG, "baseurl: " + baseurl);
                String[] condition = {String.valueOf(installation_date), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.name()};
                Cursor cur = this.mContentResolver.query(MessageContentProvider.CONTENT_URI, null, "date >= ? AND status = ? ", condition, "date DESC");
                Log.d(TAG, "count: " + cur.getCount());
                boolean z = 0 != (this.mContext.getApplicationInfo().flags & 2);
                cur.moveToFirst();
                if (cur.getCount() == 0) {
                    cur.close();
                } else if (!syncRegular) {
                    cur.close();
                    startListen = true;
                } else {
                    cur.moveToFirst();
                    boolean turnOnExceptions = false;
                    do {
                        String typestr = cur.getString(cur.getColumnIndex("type"));
                        if (MessageType.valueOf(typestr) == MessageType.SMS) {
                            BodyBase sr = Packager.packENATextMessage(cur, myNumber);
                            DBKeepAliveQueryHelper.updateSendToServerTime(this.mContext, ((TelemessageArchiverMessage) sr).getNativeId(), String.valueOf(System.currentTimeMillis()));
                            Log.d("network", "send message:" + ((TelemessageArchiverMessage) sr).getNativeId());
                            Response<Void> res = nm.start(sr, this, getContext().getApplicationContext(), false);
                            Log.d("network", "after sent object");
                            if (res != null && res.isSuccessful()) {
                                Log.d("network", "response.isSuccessful() = true");
                                new String[1][0] = Integer.toString(MessageContentProvider.MessageDeliveryStatus.Delivered.ordinal());
                                long id = cur.getLong(cur.getColumnIndex("_id"));
                                Uri messageToUpdate2 = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(id));
                                ContentValues cv = new ContentValues();
                                cv.put("status", MessageContentProvider.MessageDeliveryStatus.Delivered.toString());
                                this.mContentResolver.update(messageToUpdate2, cv, null, null);
                                DBKeepAliveQueryHelper.updateResponseCode(this.mContext, String.valueOf(id), 200);
                                DBKeepAliveQueryHelper.updatePendingReason(this.mContext, String.valueOf(id), "");
                                Log.d("send", "updating status id: " + messageToUpdate2.getPath());
                                deleteAfterArchived(cur);
                                changeWrongMMSMessageToSMS();
                                Log.d("send", "sms archived and deleted from DB");
                                CommonUtils.setCountArchivedMessages(this.mContext, MessageType.SMS);
                            } else {
                                turnOnExceptions = true;
                                long id2 = cur.getLong(cur.getColumnIndex("_id"));
                                Log.d("send", "failed to archive sms reason: " + (res != null ? res.raw() : "") + " id: " + id2);
                                DBKeepAliveQueryHelper.updateResponseCode(this.mContext, String.valueOf(id2), res == null ? -1 : res.code());
                                DBKeepAliveQueryHelper.updatePendingReason(this.mContext, String.valueOf(id2), (res == null || res.raw() == null) ? DBKeepAliveQueryHelper.MessagePendingReason.SERVER_REJECTED.getType() : res.raw().message());
                            }
                            if (res != null) {
                                EventBus.getDefault().post(new ArchiveMessageEvent(res.code()));
                            }
                        } else if (MessageType.MMS == MessageType.valueOf(typestr)) {
                            BodyBase sr2 = new TeleMessageMMSArchive();
                            Packager.packENAforMMS(this.mContext, cur, this.mContentResolver, (TeleMessageMMSArchive) sr2, myNumber);
                            long totalAttachmentSize = getTotalAttachmentSize(((TeleMessageMMSArchive) sr2).getAttachment());
                            if (totalAttachmentSize > MAX_TOTAL_ATTACHMENTS_SIZE && !StringsFlavorsUtils.isWPAApp()) {
                                Attachment[] arrayOfAttachments = ((TeleMessageMMSArchive) sr2).getAttachment();
                                sortFilesBySize(arrayOfAttachments);
                                int indexSeparator = getIndexSeparator(arrayOfAttachments);
                                Attachment[] attachmentsToSendWithFiles = (Attachment[]) Arrays.asList(arrayOfAttachments).subList(0, indexSeparator).toArray(new Attachment[indexSeparator]);
                                Attachment[] attachmentsToSendWithoutFiles = (Attachment[]) Arrays.asList(arrayOfAttachments).subList(indexSeparator, arrayOfAttachments.length).toArray(new Attachment[arrayOfAttachments.length - indexSeparator]);
                                StringBuilder newBodyWithFilesName = getFilesNameBody(attachmentsToSendWithoutFiles);
                                String currentBody = ((TeleMessageMMSArchive) sr2).getTextContent() != null ? ((TeleMessageMMSArchive) sr2).getTextContent() : "";
                                ((TeleMessageMMSArchive) sr2).setTextContent("Total attachments size " + (totalAttachmentSize / 1000000) + "MB exceeds the 25MB maximum attachment size limit. \nFiles: " + ((Object) newBodyWithFilesName) + "\nwere removed.\n\n" + currentBody);
                                ((TeleMessageMMSArchive) sr2).setAttachment(attachmentsToSendWithFiles);
                            }
                            DBKeepAliveQueryHelper.updateSendToServerTime(this.mContext, ((TelemessageArchiverMessage) sr2).getNativeId(), String.valueOf(System.currentTimeMillis()));
                            Log.d("network", "send message:" + ((TeleMessageMMSArchive) sr2).toString());
                            Response<Void> res2 = nm.start(sr2, this, getContext().getApplicationContext(), false);
                            Log.d("network", "after sent object 22");
                            if (res2 != null && res2.isSuccessful()) {
                                Log.d("network", "response.isSuccessful() = true");
                                long id3 = cur.getLong(cur.getColumnIndex("_id"));
                                new String[1][0] = Integer.toString(MessageContentProvider.MessageDeliveryStatus.Delivered.ordinal());
                                Uri messageToUpdate3 = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(id3));
                                ContentValues cv2 = new ContentValues();
                                cv2.put("status", MessageContentProvider.MessageDeliveryStatus.Delivered.toString());
                                this.mContentResolver.update(messageToUpdate3, cv2, null, null);
                                DBKeepAliveQueryHelper.updateResponseCode(this.mContext, String.valueOf(id3), 200);
                                DBKeepAliveQueryHelper.updatePendingReason(this.mContext, String.valueOf(id3), "");
                                Log.d("send", "mms archived , path: " + messageToUpdate3.getPath());
                                deleteAfterArchived(cur);
                                CommonUtils.setCountArchivedMessages(this.mContext, MessageType.MMS);
                            } else {
                                turnOnExceptions = true;
                                long id4 = cur.getLong(cur.getColumnIndex("_id"));
                                Log.d("send", "failed to archive sms reason: " + (res2 != null ? res2.raw() : "") + " id: " + id4);
                                DBKeepAliveQueryHelper.updateResponseCode(this.mContext, String.valueOf(id4), res2 == null ? -1 : res2.code());
                                DBKeepAliveQueryHelper.updatePendingReason(this.mContext, String.valueOf(id4), (res2 == null || res2.raw() == null) ? DBKeepAliveQueryHelper.MessagePendingReason.SERVER_REJECTED.getType() : res2.raw().message());
                            }
                            if (res2 != null) {
                                EventBus.getDefault().post(new ArchiveMessageEvent(res2.code()));
                            }
                        } else if (MessageType.CallLog == MessageType.valueOf(typestr)) {
                            BodyBase sr3 = Packager.packCallLog(cur, this.mContentResolver, this.mContext, new CallLogMessageRecorder(), myNumber);
                            long totalAttachmentSize2 = getTotalAttachmentSize(((CallLogMessageRecorder) sr3).getAttachment());
                            if (totalAttachmentSize2 > MAX_TOTAL_ATTACHMENTS_SIZE) {
                                StringBuilder newBodyWithFilesName2 = getFilesNameBody(((CallLogMessageRecorder) sr3).getAttachment());
                                String currentBody2 = ((CallLogMessageRecorder) sr3).getTextContent() != null ? ((CallLogMessageRecorder) sr3).getTextContent() : "";
                                ((CallLogMessageRecorder) sr3).setTextContent("Call recording size " + (totalAttachmentSize2 / 1000000) + "MB exceeds the 25MB maximum attachment size limit. \nFiles: " + ((Object) newBodyWithFilesName2) + "\nwere removed.\n\n" + currentBody2);
                                ((CallLogMessageRecorder) sr3).setAttachment(null);
                            }
                            Log.d("network", "send message:" + ((CallLogMessageRecorder) sr3).toString());
                            DBKeepAliveQueryHelper.updateSendToServerTime(this.mContext, ((CallLogMessageRecorder) sr3).getMessageId(), String.valueOf(System.currentTimeMillis()));
                            Response<Void> res3 = nm.start(sr3, this, getContext().getApplicationContext(), false);
                            Log.d("network", "after sent object 333");
                            if (res3 != null && res3.isSuccessful()) {
                                Log.d("network", "response.isSuccessful() = true");
                                new String[1][0] = Integer.toString(MessageContentProvider.MessageDeliveryStatus.Delivered.ordinal());
                                long id5 = cur.getLong(cur.getColumnIndex("_id"));
                                int aggregateIndex = cur.getInt(cur.getColumnIndex("multiple_index"));
                                DBKeepAliveQueryHelper.updateResponseCode(this.mContext, String.valueOf(id5), 200);
                                DBKeepAliveQueryHelper.updatePendingReason(this.mContext, String.valueOf(id5), "");
                                ContentValues cv3 = new ContentValues();
                                cv3.put("status", MessageContentProvider.MessageDeliveryStatus.Delivered.toString());
                                if (aggregateIndex > 0) {
                                    messageToUpdate = MessageContentProvider.CONTENT_URI_MESSAGES;
                                    String[] condition2 = {String.valueOf(aggregateIndex)};
                                    int count2 = this.mContentResolver.update(messageToUpdate, cv3, "multiple_index = ? ", condition2);
                                    count = count2 - 1;
                                } else {
                                    messageToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(id5));
                                    count = this.mContentResolver.update(messageToUpdate, cv3, null, null);
                                }
                                deleteAfterArchived(cur);
                                Log.d("send", "CallLog archived , path: " + messageToUpdate.getPath() + " num of rows:" + count);
                                CommonUtils.setArchivedMessagesCount(this.mContext, MessageType.CallLog, count);
                            } else {
                                turnOnExceptions = true;
                                long id6 = cur.getLong(cur.getColumnIndex("_id"));
                                Log.d("send", "failed to archive sms reason: " + (res3 != null ? res3.raw() : "") + " id: " + id6);
                                DBKeepAliveQueryHelper.updateResponseCode(this.mContext, String.valueOf(id6), res3 == null ? -1 : res3.code());
                                DBKeepAliveQueryHelper.updatePendingReason(this.mContext, String.valueOf(id6), (res3 == null || res3.raw() == null) ? DBKeepAliveQueryHelper.MessagePendingReason.SERVER_REJECTED.getType() : res3.raw().message());
                            }
                            if (res3 != null) {
                                EventBus.getDefault().post(new ArchiveMessageEvent(res3.code()));
                            }
                        }
                    } while (cur.moveToNext());
                    cur.close();
                    if (FlavorSettings.getInstance().isAppSupportReArchive()) {
                        deleteOldFiles();
                    }
                    Cursor cur2 = this.mContentResolver.query(MessageContentProvider.CONTENT_URI, null, "date >= ? AND status = ? ", condition, "date DESC");
                    Log.d("send", "found new messages to archive , requesting preform sync, count: " + cur2.getCount());
                    syncResult.fullSyncRequested = true;
                    syncResult.stats.numSkippedEntries = 1L;
                    cur2.close();
                    if (turnOnExceptions) {
                        syncResult.stats.numIoExceptions++;
                        ioException++;
                        PrefManager.setLongPref(this.mContext, "LastAlarmTime", System.currentTimeMillis());
                        AlarmService.startJobService(this.mContext);
                        doAgaing = false;
                        handleTooOldFailedMessage();
                    } else {
                        syncResult.stats.numIoExceptions = 0L;
                        ioException = 0;
                    }
                }
            } while (doAgaing);
            Log.d("syncAdapter", "isArchiveRequested: " + isArchiveRequested);
            if (doHistoric) {
                long installation_date2 = PrefManager.getLongPref(this.mContext.getApplicationContext(), PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, PreferenceManager.getDefaultSharedPreferences(this.mContext).getLong(PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, 0L));
                if (doHistoricNow) {
                    String[] condition_history = {String.valueOf(installation_date2), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDeliveredHistory.name()};
                    Cursor cur_history = this.mContentResolver.query(MessageContentProvider.CONTENT_URI_MESSAGES, null, "date < ? AND status = ? ", condition_history, "date ASC LIMIT 100");
                    cur_history.moveToFirst();
                    if (cur_history != null && cur_history.getCount() > 0) {
                        JSONArray array = new JSONArray();
                        do {
                            String typestr2 = cur_history.getString(cur_history.getColumnIndex("type"));
                            if (MessageType.valueOf(typestr2) == MessageType.SMS) {
                                BodyBase sr4 = Packager.packSMS(cur_history, new SMSMessageRecord(), myNumber);
                                Gson gson = new Gson();
                                String smsJson = gson.toJson((SMSMessageRecord) sr4);
                                JSONObject obj = null;
                                try {
                                    obj = new JSONObject(smsJson);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                array.put(obj);
                            } else if (MessageType.valueOf(typestr2) == MessageType.MMS) {
                                BodyBase sr5 = Packager.packMMS(this.mContext, cur_history, this.mContentResolver, new MMSMessageRecorder(), myNumber);
                                Gson gson2 = new Gson();
                                String mmsJson = gson2.toJson((MMSMessageRecorder) sr5);
                                JSONObject obj2 = null;
                                try {
                                    obj2 = new JSONObject(mmsJson);
                                } catch (JSONException e2) {
                                    e2.printStackTrace();
                                }
                                array.put(obj2);
                            } else if (MessageType.valueOf(typestr2) == MessageType.CallLog) {
                                BodyBase sr6 = Packager.packCallLog(cur_history, this.mContentResolver, this.mContext, new CallLogMessageRecorder(), myNumber);
                                Gson gson3 = new Gson();
                                String calllogJson = gson3.toJson((CallLogMessageRecorder) sr6);
                                JSONObject obj3 = null;
                                try {
                                    obj3 = new JSONObject(calllogJson);
                                } catch (JSONException e3) {
                                    e3.printStackTrace();
                                }
                                array.put(obj3);
                            }
                        } while (cur_history.moveToNext());
                        cur_history.close();
                        File arrayOut = new File(getContext().getExternalFilesDir(null).getPath().concat("/archive.zip"));
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(arrayOut);
                        } catch (FileNotFoundException e4) {
                            e4.printStackTrace();
                        }
                        ZipOutputStream zos = new ZipOutputStream(fos);
                        ZipEntry zeArray = new ZipEntry("array.txt");
                        try {
                            zos.putNextEntry(zeArray);
                            zos.write(array.toString().getBytes());
                            zos.closeEntry();
                            zos.close();
                        } catch (FileNotFoundException e5) {
                            e5.printStackTrace();
                        } catch (IOException e6) {
                            e6.printStackTrace();
                        }
                        BodyBase sr7 = new MMSMessageRecorder();
                        String startDate = null;
                        String endDate = null;
                        try {
                            new SimpleDateFormat("yyyyMMdd_HHmmss");
                            startDate = ((JSONObject) array.get(0)).getString(CallLogMessageRecorder.messageTime_key);
                            endDate = ((JSONObject) array.get(array.length() - 1)).getString(CallLogMessageRecorder.messageTime_key);
                        } catch (JSONException e7) {
                            e7.printStackTrace();
                        }
                        Response<Void> res4 = nm.start(Packager.packVirtualMMS(cur_history, this.mContentResolver, sr7, myNumber, arrayOut, startDate, endDate), this, getContext().getApplicationContext(), false);
                        Log.d("network", "after sent object 555");
                        if (res4 != null && res4.isSuccessful()) {
                            Log.d("network", "response.isSuccessful() = true");
                            new String[1][0] = Integer.toString(MessageContentProvider.MessageDeliveryStatus.Delivered.ordinal());
                            for (int count3 = 0; count3 < array.length(); count3++) {
                                try {
                                    Uri messageToUpdate4 = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(((JSONObject) array.get(count3)).get(CallLogMessageRecorder.messageId_key)).split("-")[0]);
                                    ContentValues cv4 = new ContentValues();
                                    cv4.put("status", MessageContentProvider.MessageDeliveryStatus.Delivered.toString());
                                    this.mContentResolver.update(messageToUpdate4, cv4, null, null);
                                    Log.d("send", ((JSONObject) array.get(count3)).get(CallLogMessageRecorder.messageContext_key) + " archived , path: " + messageToUpdate4.getPath());
                                } catch (JSONException e8) {
                                    e8.printStackTrace();
                                }
                            }
                        }
                        syncResult.fullSyncRequested = true;
                    } else {
                        boolean allHistoricMessagesSavedToDb = PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean(PrefManagerConstants.SHARED_PREFERENCE_ALL_HISTORIC_MESSAGES_SAVED_TO_DB_KEY, false);
                        if (allHistoricMessagesSavedToDb) {
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putBoolean(PrefManagerConstants.SHARED_PREFERENCE_HISTORY_ARCHIVE_DONE_KEY, true).commit();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            String dateString = format.format(new Date());
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("archive_time", "archived on " + dateString).commit();
                        }
                    }
                } else {
                    startListen = true;
                }
            }
            if (startListen) {
                Intent i = new Intent(this.mContext, NetworkAndChargingService.class);
                i.putExtra("state", 1);
                this.mContext.startService(i);
            }
            deleteDatabase();
        } else {
            Log.d("Sync adapter", "user not activated");
        }
        inWork = false;
        DataGrabber.isRun = false;
        CommonUtils.stopBackupService(this.mContext, false);
    }

    private int getIndexSeparator(Attachment[] arrayOfAttachments) {
        long totalAttachmentsSize = 0;
        for (int i = 0; i < arrayOfAttachments.length; i++) {
            totalAttachmentsSize += getAttachmentSize(arrayOfAttachments[i]);
            if (totalAttachmentsSize > MAX_TOTAL_ATTACHMENTS_SIZE) {
                return i;
            }
        }
        return 0;
    }

    private void sortFilesBySize(Attachment[] arrayOfAttachments) {
        List<Attachment> attachmentList = Arrays.asList(arrayOfAttachments);
        Comparator<Attachment> fileSizeComparator = new Comparator<Attachment>() { // from class: com.tm.androidcopysdk.SyncAdapter.1
            @Override // java.util.Comparator
            @RequiresApi(api = 19)
            public int compare(Attachment o1, Attachment o2) {
                return Long.compare(SyncAdapter.this.getAttachmentSize(o1), SyncAdapter.this.getAttachmentSize(o2));
            }
        };
        Collections.sort(attachmentList, fileSizeComparator);
    }

    private StringBuilder getFilesNameBody(Attachment[] array) {
        StringBuilder body = new StringBuilder();
        try {
            for (Attachment attachment : array) {
                body.append("\n").append(attachment.getName());
            }
        } catch (Exception e) {
        }
        Log.d(TAG, "getFilesNameBody = " + body.toString());
        return body;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getAttachmentSize(Attachment attachment) {
        long attachmentSize = 0;
        if (attachment != null) {
            try {
                InputStream input = getContext().getContentResolver().openInputStream(attachment.getUri());
                attachmentSize = 0 + IOUtils.toByteArray(UriUtils.getDecipheredInStream(getContext(), input)).length;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return attachmentSize;
    }

    private long getTotalAttachmentSize(Attachment[] array) {
        long totalSize = 0;
        if (array != null) {
            for (Attachment attachment : array) {
                try {
                    InputStream input = getContext().getContentResolver().openInputStream(attachment.getUri());
                    totalSize += IOUtils.toByteArray(UriUtils.getDecipheredInStream(getContext(), input)).length;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        Log.d(TAG, "getTotalAttachmentSize = " + totalSize);
        return totalSize;
    }

    private void deleteDatabase() {
        long int_day = PreferenceManager.getDefaultSharedPreferences(this.mContext).getLong(TIME_WHEN_WAS_DELETED_DB, 0L);
        if (int_day > 0 && (System.currentTimeMillis() - int_day) / 86400000 > 35) {
            Log.e(TAG, "<<<<<<<<<<\ntry to delete message... \n<>>>>>>>>>>>>");
            String[] strArr = {String.valueOf(int_day), MessageContentProvider.MessageDeliveryStatus.Delivered.name(), MessageContentProvider.MessageDeliveryStatus.Failed.name()};
        } else if (int_day == 0) {
            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
            ed.putLong(TIME_WHEN_WAS_DELETED_DB, System.currentTimeMillis());
            ed.apply();
        }
    }

    private void deleteAfterArchived(Cursor cursor) {
        String[] attachmentFromDB = cursor.getString(cursor.getColumnIndex("_data")) != null ? cursor.getString(cursor.getColumnIndex("_data")).split(";") : null;
        Log.d("File", " attachmentFromDB " + attachmentFromDB);
        long id = cursor.getLong(cursor.getColumnIndex("_id"));
        if (attachmentFromDB != null) {
            for (String attachment : attachmentFromDB) {
                if (attachment != null) {
                    File file = new File(getContext().getFilesDir() + "/" + id, attachment);
                    if (file.exists()) {
                        Log.d("File", " exist:  " + file.exists() + " id: " + id + " attachment: " + attachment);
                        if (!FlavorSettings.getInstance().isAppSupportReArchive()) {
                            file.delete();
                        }
                    }
                }
            }
            File dir = new File(getContext().getFilesDir() + "/" + id);
            if (dir.exists() && dir.isDirectory() && dir.list().length == 0 && !FlavorSettings.getInstance().isAppSupportReArchive()) {
                dir.delete();
                Log.d("File", " delete dir file for id = " + id);
            }
        }
    }

    private void deleteOldFiles() {
        new Thread(new Runnable() { // from class: com.tm.androidcopysdk.SyncAdapter.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    long halfYearInMill = TimeUnit.DAYS.toMillis(182L);
                    String[] condition_mms_to_delete = {String.valueOf(System.currentTimeMillis() - halfYearInMill), MessageType.MMS.name(), MessageContentProvider.MessageDeliveryStatus.Delivered.name()};
                    Cursor result = SyncAdapter.this.mContentResolver.query(MessageContentProvider.CONTENT_URI_MESSAGES, null, "date < ? AND type = ? AND status = ? AND _PATH IS NOT NULL AND LENGTH(_PATH) > 0", condition_mms_to_delete, "date ASC");
                    if (result != null) {
                        Log.d("rearchive_messages", "Should delete = " + result.getCount() + " attachments messages.");
                        int deletedFilesSize = 0;
                        while (result.moveToNext()) {
                            long id = result.getLong(result.getColumnIndex("_id"));
                            String[] attachmentFromDB = result.getString(result.getColumnIndex("_data")) != null ? result.getString(result.getColumnIndex("_data")).split(";") : null;
                            if (attachmentFromDB != null) {
                                for (String attachment : attachmentFromDB) {
                                    if (attachment != null) {
                                        File file = new File(SyncAdapter.this.getContext().getFilesDir() + "/" + id, attachment);
                                        if (file.exists()) {
                                            file.delete();
                                            if (!file.exists()) {
                                                Uri messageToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(id));
                                                ContentValues cv = new ContentValues();
                                                cv.put("_PATH", "");
                                                deletedFilesSize += SyncAdapter.this.mContentResolver.update(messageToUpdate, cv, null, null);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        result.close();
                        Log.d("rearchive_messages", "deleteOldFiles - updated and deleted files for = " + deletedFilesSize + " messages.");
                    }
                } catch (Exception e) {
                    Log.d("rearchive_messages", "delete old files exception = " + e.getMessage());
                }
            }
        }).start();
    }

    private void changeWrongMMSMessageToSMS() {
        if (!StringsFlavorsUtils.isWPAApp() || isAlreadyChanged()) {
            return;
        }
        try {
            long lastUpgradeTime = PrefManager.getLongPref(this.mContext, PrefManagerConstants.SHARED_PREFERENCE_LAST_UPGRADE_TIME, 1708905599000L);
            String[] conditionMmsToChange = {String.valueOf(lastUpgradeTime), MessageType.MMS.name(), MessageContentProvider.MessageDeliveryStatus.NotReadyToBeDelivered.name(), DBKeepAliveQueryHelper.MessagePendingReason.WAITING_TO_MEDIA.getType()};
            Cursor result = this.mContentResolver.query(MessageContentProvider.CONTENT_URI_MESSAGES, null, "date < ? AND type = ? AND status = ? and pendingReason = ?", conditionMmsToChange, "date ASC");
            if (result != null) {
                if (result.getCount() == 0) {
                    setAlreadyChanged();
                }
                Log.d("changeWrongMMSMessageToSMS", "should change = " + result.getCount() + " messages.");
                while (result.moveToNext()) {
                    long id = result.getLong(result.getColumnIndex("_id"));
                    Uri messageToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(id));
                    ContentValues cv = new ContentValues();
                    cv.put("type", MessageType.SMS.name());
                    cv.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.name());
                    cv.put("pendingReason", DBKeepAliveQueryHelper.MessagePendingReason.NO_ATTEMPT_YET.getType());
                    int count = this.mContentResolver.update(messageToUpdate, cv, null, null);
                    Log.d("changeWrongMMSMessageToSMS", "updated query for = " + count + " rows.");
                }
                result.close();
            }
        } catch (Exception e) {
            Log.d("changeWrongMMSMessageToSMS", "Change wrong mms message to sms old files exception = " + e.getMessage());
        }
    }

    private boolean isAlreadyChanged() {
        return PrefManager.getBooleanPref(this.mContext, "isAlreadyFinishedToClean", false);
    }

    private void setAlreadyChanged() {
        PrefManager.setBooleanPref(this.mContext, "isAlreadyFinishedToClean", true);
    }

    public static Bundle getBundleForRequestSync(Context context) {
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean("force", true);
        settingsBundle.putBoolean("expedited", true);
        settingsBundle.putString("baseurl", PreferenceManager.getDefaultSharedPreferences(context).getString("baseurl", Definitions.BaseUrl));
        return settingsBundle;
    }

    @Override // com.tm.androidcopysdk.HandleResponseListener
    public void backoffRetryOnFailedMessages() {
        Log.d("Sync Adaptor", "BackoffRetryOnFailedMessages");
    }

    private BodyBase formatNumbers(BodyBase message, MessageType type) {
        return message;
    }

    @Override // com.tm.androidcopysdk.HandleResponseListener
    public void reportOnSuccess() {
        Log.d("Sync Adaptor", "ReportOnSuccess");
    }

    @Override // com.tm.androidcopysdk.HandleResponseListener
    public void reportOnFailure() {
    }

    public static String getFormattedPhoneNumber(Context context, String number) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        return getFormattedPhoneNumber(context, number, tm.getSimCountryIso());
    }

    public static String getFormattedPhoneNumber(Context context, String number, String countryCode) {
        char[] charArray;
        StringBuilder onlyNumbers = new StringBuilder();
        for (char c : number.toCharArray()) {
            if (c >= '0' && c <= '9') {
                onlyNumbers.append(c);
            }
        }
        CommonUtils.CCODES code = CommonUtils.CCODES.getsCountryByName(countryCode);
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        if (onlyNumbers.length() > code.getMinLocal()) {
            if (onlyNumbers.length() > code.getMaxLocal()) {
                String newNumber = number;
                if (!number.startsWith("+")) {
                    newNumber = "+" + number;
                }
                try {
                    Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(newNumber, (String) null);
                    return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
                } catch (NumberParseException e) {
                    Log.v(TAG, "number = " + number, e);
                    return number;
                }
            }
            try {
                Phonenumber.PhoneNumber phoneNumber2 = phoneUtil.parse(number, countryCode.toUpperCase());
                return phoneUtil.format(phoneNumber2, PhoneNumberUtil.PhoneNumberFormat.E164);
            } catch (NumberParseException e2) {
                Log.v(TAG, "number = " + number + "; countryCode = " + countryCode, e2);
                return number;
            }
        }
        return number;
    }

    private void handleTooOldFailedMessage() {
        long old = System.currentTimeMillis() - 5184000000L;
        if (old < 0) {
            return;
        }
        String oldDate = String.valueOf(old);
        String[] condition = {oldDate, MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.name(), MessageContentProvider.MessageDeliveryStatus.WaitingToBeCompressed.name(), MessageContentProvider.MessageDeliveryStatus.WaitingToMultipleParticipants.name()};
        Cursor cur = this.mContentResolver.query(MessageContentProvider.CONTENT_URI, null, "date <= ? AND status in (?,?,?) ", condition, "date ASC");
        String ids = null;
        if (cur != null) {
            if (cur.getCount() > 0) {
                ArrayList<Long> list = new ArrayList<>();
                while (cur.moveToNext()) {
                    long id = cur.getLong(cur.getColumnIndex("_id"));
                    list.add(Long.valueOf(id));
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("in (");
                Iterator<Long> it = list.iterator();
                while (it.hasNext()) {
                    long id2 = it.next().longValue();
                    stringBuilder.append("" + id2 + ",");
                }
                stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
                stringBuilder.append(")");
                ids = stringBuilder.toString();
            }
            cur.close();
            if (!TextUtils.isEmpty(ids)) {
                Uri messageToUpdate = MessageContentProvider.CONTENT_URI_MESSAGES;
                String whereUpadte = "_id " + ids;
                ContentValues cv = new ContentValues();
                cv.put("status", MessageContentProvider.MessageDeliveryStatus.Failed.toString());
                int count = this.mContentResolver.update(messageToUpdate, cv, whereUpadte, null);
                Log.d(TAG, "handleTooOldFailedMessage sign " + count + " message as failed message ids " + ids);
            }
        }
    }
}
