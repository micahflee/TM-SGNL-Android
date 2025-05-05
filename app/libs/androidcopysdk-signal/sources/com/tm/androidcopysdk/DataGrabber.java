package com.tm.androidcopysdk;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.MimeTypeMap;
import androidx.core.app.ActivityCompat;
import androidx.documentfile.provider.DocumentFile;
import com.tm.androidcopysdk.AndroidCopySettings;
import com.tm.androidcopysdk.Models.MessageDetailsArchive;
import com.tm.androidcopysdk.Models.RecFileExt;
import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.database.DBKeepAliveQueryHelper;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.database.UriUtils;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.model.ArchiveRecipient;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.Contact;
import com.tm.androidcopysdk.utils.ContactsUtils;
import com.tm.androidcopysdk.utils.DualSimUtils;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.PrefManagerConstants;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import com.tm.logger.Log;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/DataGrabber.class */
public class DataGrabber {
    private static final String TAG = "DataGrabber";
    public static final String DATE_OF_MESSAGE = "DATE_OF_MESSAGE";
    public static final int RE_ARCHIVE_MAX_DAYS_BEFORE = 182;
    public static final int NO_EXTRA_DATA = 0;
    public static final int MULTIPLE_FLAG = 1;
    public static final int MULTIPLE_PARENT = 2;
    Context mContext;
    Handler mHandler;
    SMSMessageContentObserver smsObserver;
    MMSMessageContentObserver mmsObserver;
    CallLogObserver callLogObserver;
    CallListener mCallrecorder;
    SMSIncomingReceiver smsIncomingReceiver;
    Thread mThread;
    long type;
    static DataGrabber mInstance;
    private TelephonyManager telephony;
    public static boolean isRun = false;

    public static DataGrabber getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataGrabber();
            mInstance.mContext = context;
            mInstance.Setup();
            mInstance.init();
        }
        return mInstance;
    }

    private void Setup() {
    }

    public void resetGrabber() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        init();
    }

    private void init() {
        Log.d(TAG, "init");
        if (FlavorSettings.getInstance().supportNativeMsg()) {
            this.mThread = new Thread();
            if (this.smsObserver != null) {
                this.mContext.getContentResolver().unregisterContentObserver(this.smsObserver);
            }
            this.smsObserver = new SMSMessageContentObserver(null);
            if (this.mmsObserver != null) {
                this.mContext.getContentResolver().unregisterContentObserver(this.mmsObserver);
            }
            this.mmsObserver = new MMSMessageContentObserver(null);
            if (this.mCallrecorder != null) {
                this.telephony = (TelephonyManager) this.mContext.getSystemService("phone");
                this.telephony.listen(this.mCallrecorder, 0);
                this.mCallrecorder = null;
            }
            this.mCallrecorder = new CallListener(this.mContext);
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        }
        udpateCurrentSettings();
    }

    private static String getLastMessageByType(Context context, MessageType type) {
        long int_day = PrefManager.getLongPref(context.getApplicationContext(), PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, PreferenceManager.getDefaultSharedPreferences(context).getLong(PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, System.currentTimeMillis()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String date = PreferenceManager.getDefaultSharedPreferences(context).getString(type.name() + DATE_OF_MESSAGE, null);
        if (date != null) {
            Log.d("settings", "time of message: " + sdf.format(new Date(Long.valueOf(date).longValue())));
            return date;
        }
        String currentTime = String.valueOf(int_day);
        return currentTime;
    }

    public static long lastMessageTime(Context context) {
        String sms = getLastMessageByType(context, MessageType.SMS);
        String mms = getLastMessageByType(context, MessageType.MMS);
        String callLog = getLastMessageByType(context, MessageType.CallLog);
        long smsTime = Long.valueOf(sms).longValue();
        long mmsTime = Long.valueOf(mms).longValue();
        long callLogTime = Long.valueOf(callLog).longValue();
        long ret = smsTime;
        if (mmsTime > smsTime) {
            ret = mmsTime;
        }
        if (callLogTime > ret) {
            ret = callLogTime;
        }
        return ret;
    }

    private boolean startAggregateCalls(String date, long endPoint) {
        return Long.valueOf(date).longValue() > endPoint;
    }

    private long calculateStart(Cursor cursor) {
        String date0 = cursor.getString(cursor.getColumnIndex("date"));
        cursor.getString(cursor.getColumnIndex("duration"));
        long ret = Long.valueOf(date0).longValue();
        int index = cursor.getPosition();
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("date"));
            cursor.getString(cursor.getColumnIndex("duration"));
            if (Long.valueOf(date).longValue() > ret) {
                break;
            }
            ret = Math.min(ret, Long.valueOf(date).longValue());
        }
        cursor.moveToPosition(index);
        return ret;
    }

    private long calculateEndPoint(Cursor cursor) {
        String date0 = cursor.getString(cursor.getColumnIndex("date"));
        String duration0 = cursor.getString(cursor.getColumnIndex("duration"));
        long ret = Long.valueOf(date0).longValue() + (1000 * Long.valueOf(duration0).longValue());
        int index = cursor.getPosition();
        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String duration = cursor.getString(cursor.getColumnIndex("duration"));
            if (Long.valueOf(date).longValue() > ret) {
                break;
            }
            ret = Math.max(ret, Long.valueOf(date).longValue() + (1000 * Long.valueOf(duration).longValue()));
        }
        cursor.moveToPosition(index);
        return ret;
    }

    private int createAggregateId() {
        int aggregateId = PreferenceManager.getDefaultSharedPreferences(this.mContext).getInt("aggregateId", 1);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        editor.putInt("aggregateId", aggregateId + 1).apply();
        return aggregateId;
    }

    private boolean isAggregate(Cursor cursor) {
        String date0 = cursor.getString(cursor.getColumnIndex("date"));
        String duration0 = cursor.getString(cursor.getColumnIndex("duration"));
        long current = Long.valueOf(date0).longValue() + (1000 * Long.valueOf(duration0).longValue());
        int index = cursor.getPosition();
        boolean ret = false;
        if (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex("date"));
            ret = Long.valueOf(date).longValue() <= current;
        }
        cursor.moveToPosition(index);
        return ret;
    }

    public synchronized void setArchiveCallLogMessages(String messagesId, String subject, String messageContent, List<CallObj> calls, List<RecFileExt> listFileName, boolean needCompression) {
        for (int i = 0; i < calls.size(); i++) {
            ContentValues cv = new ContentValues();
            Log.d(TAG, "Archive call list(" + i + "):" + calls.get(i).toString());
            callIsEmpty(calls.get(i).getDuration());
            List<RecFileExt> cleanFileNames = cleanUpFiles(listFileName);
            if (cleanFileNames != null && cleanFileNames.size() > 0) {
                StringBuilder stringUri = new StringBuilder();
                for (int j = 0; j < cleanFileNames.size(); j++) {
                    String retFileName = Uri.parse(cleanFileNames.get(j).getName()).getLastPathSegment();
                    if (j == 0) {
                        stringUri.append(retFileName);
                    } else {
                        stringUri.append(";").append(retFileName);
                    }
                }
                Log.d(TAG, "hasRecording");
                Log.d(TAG, stringUri.toString());
                Uri path = calls.get(i).writeToDB(this.mContext, MessageContentProvider.MessageDeliveryStatus.CallLogNotReadyToBeDelivered, stringUri.toString());
                String mid = path.getLastPathSegment();
                if (mid.equalsIgnoreCase("-1")) {
                    Log.e(TAG, " ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
                    Log.e(TAG, " call with message id:" + messagesId + " failed to insert");
                    Log.e(TAG, " ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
                    return;
                }
                List<File> fileListToDelete = new ArrayList<>();
                boolean isNeedToConvert = false;
                for (RecFileExt retFileName2 : cleanFileNames) {
                    if (!TextUtils.isEmpty(retFileName2.getName())) {
                        File file = new File(retFileName2.getName() + retFileName2.getExt());
                        byte[] bytesArray = new byte[(int) file.length()];
                        try {
                            FileInputStream fis = new FileInputStream(file);
                            fis.read(bytesArray);
                            fis.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                        OutputStream media = null;
                        try {
                            media = this.mContext.getContentResolver().openOutputStream(MessageContentProvider.CONTENT_URI.buildUpon().appendPath("media").appendPath(mid).appendPath(Uri.parse(retFileName2.getName()).getLastPathSegment()).build(), "rw");
                            if (!needCompression) {
                                media = UriUtils.getDecipheredOutStream(this.mContext, media);
                            }
                        } catch (FileNotFoundException e3) {
                            e3.printStackTrace();
                        }
                        try {
                            media.write(bytesArray);
                            media.close();
                        } catch (IOException e4) {
                            e4.printStackTrace();
                        }
                        if (retFileName2.getExt().toLowerCase().contains(".m4a") || retFileName2.getExt().toLowerCase().contains("wav")) {
                            isNeedToConvert = true;
                        } else {
                            CommonUtils.exportAndEncrypt(this.mContext, mid, file);
                        }
                        fileListToDelete.add(file);
                    }
                }
                if (isNeedToConvert && needCompression) {
                    Log.d(TAG, "isNeedToConvert && needCompression = true");
                    cv.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeCompressed.toString());
                } else {
                    Log.d(TAG, "isNeedToConvert or needCompression = false");
                    cv.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.toString());
                }
                for (File f : fileListToDelete) {
                    boolean deleted = f.delete();
                    if (deleted) {
                        Log.d(TAG, "file deleted: " + f.getName());
                    } else {
                        Log.d(TAG, "could not delete: " + f.getName());
                    }
                }
                Log.d("CallLogs attachment", "updated status id: " + mid + ", NAME_STATUS to " + cv.get("status"));
                Uri msgToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(mid));
                updateCallLogsDetails(messagesId, subject, messageContent, cv, calls.get(i).callMode == 0 ? DBKeepAliveQueryHelper.MessageType.VOICE_CALL : DBKeepAliveQueryHelper.MessageType.VIDEO_CALL);
                this.mContext.getContentResolver().update(msgToUpdate, cv, null, null);
                if (needCompression) {
                    CompressionService.startJobIntentService(this.mContext);
                }
            } else {
                Uri path2 = calls.get(i).writeToDB(this.mContext, 0, MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, null);
                String mid2 = path2.getLastPathSegment();
                if (mid2.equalsIgnoreCase("-1")) {
                    Log.e(TAG, " * * * * * * * * * * * * * * *");
                    Log.e(TAG, " call with message id:" + messagesId + " failed to insert");
                    Log.e(TAG, " ! ! ! ! ! ! ! ! ! ! ! ! ! ! !");
                    return;
                }
                Uri msgToUpdate2 = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(mid2));
                updateCallLogsDetails(messagesId, subject, messageContent, cv, DBKeepAliveQueryHelper.MessageType.CALL_LOG);
                this.mContext.getContentResolver().update(msgToUpdate2, cv, null, null);
            }
        }
        if (calls.size() > 0) {
            CommonUtils.startBackupService(this.mContext);
        }
    }

    private void updateCallLogsDetails(String messagesId, String subject, String messageContent, ContentValues cv, DBKeepAliveQueryHelper.MessageType messageType) {
        cv.put("keep_alive_sent", (Integer) 0);
        cv.put("messageType", messageType.getType());
        cv.put("rearchivingFlag", (Integer) 0);
        if (!TextUtils.isEmpty(messagesId)) {
            cv.put("orig_id", CommonUtils.getUniqueId(messagesId));
            cv.put("native_message_id", messagesId);
        }
        if (!TextUtils.isEmpty(messageContent)) {
            cv.put("body", messageContent);
        }
        if (!TextUtils.isEmpty(subject)) {
            cv.put("subject", subject);
        }
    }

    public synchronized void getCallLogMessages() {
        if (handleCallLogPermissions()) {
            Log.d("info", "getCallLogMessages start");
            System.currentTimeMillis();
            String time = getLastMessageByType(this.mContext, MessageType.CallLog);
            Uri.parse("content://call_log/calls");
            if (ActivityCompat.checkSelfPermission(this.mContext, "android.permission.READ_CALL_LOG") == 0) {
                Cursor cur = this.mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, "date > ?", new String[]{time}, "date ASC");
                printCursor(cur);
                if (cur != null && cur.getCount() > 0) {
                    List<CallObj> list = getAllCalls(cur);
                    Log.d(TAG, "call list size = " + list.size());
                    if (PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("recording", false)) {
                        for (int i = 0; i < list.size(); i++) {
                            Log.d(TAG, "call list(" + i + "):" + list.get(i).toString());
                            boolean emptyCall = callIsEmpty(list.get(i).getDuration());
                            List<RecFileExt> listFileName = emptyCall ? null : getFileName(list.get(i));
                            if (emptyCall) {
                                Uri path = list.get(i).writeToDB(this.mContext, 0, MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, null);
                                Log.d(TAG, "Call log with no recording(empty call) , recording enabled , uri:" + path.getEncodedPath().toString());
                            } else if (listFileName != null && listFileName.size() > 0) {
                                StringBuilder stringUri = new StringBuilder();
                                for (int j = 0; j < listFileName.size(); j++) {
                                    String lastPathSegment = Uri.parse(listFileName.get(j).getName()).getLastPathSegment();
                                    if (j == 0) {
                                        stringUri.append(lastPathSegment);
                                    } else {
                                        stringUri.append(";").append(lastPathSegment);
                                    }
                                }
                                boolean isNew = PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("isNewProt", false);
                                Log.d(TAG, "hasRecording");
                                Uri path2 = list.get(i).writeToDB(this.mContext, MessageContentProvider.MessageDeliveryStatus.CallLogNotReadyToBeDelivered, stringUri.toString());
                                String mid = path2.getLastPathSegment();
                                new ByteArrayOutputStream();
                                List<File> fileListToDelete = new ArrayList<>();
                                boolean isNeedToConvert = false;
                                for (RecFileExt retFileName : listFileName) {
                                    File file = new File(retFileName.getName());
                                    byte[] bytesArray = new byte[(int) file.length()];
                                    try {
                                        FileInputStream fis = new FileInputStream(file);
                                        fis.read(bytesArray);
                                        fis.close();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e2) {
                                        e2.printStackTrace();
                                    }
                                    OutputStream media = null;
                                    try {
                                        media = this.mContext.getContentResolver().openOutputStream(MessageContentProvider.CONTENT_URI.buildUpon().appendPath("media").appendPath(mid).appendPath(Uri.parse(retFileName.getName()).getLastPathSegment()).build(), "rw");
                                        if (isNew) {
                                            media = UriUtils.getDecipheredOutStream(this.mContext, media);
                                        }
                                    } catch (FileNotFoundException e3) {
                                        e3.printStackTrace();
                                    }
                                    try {
                                        media.write(bytesArray);
                                        media.close();
                                    } catch (IOException e4) {
                                        e4.printStackTrace();
                                    }
                                    if (retFileName.getExt().toLowerCase().contains(".m4a") || retFileName.getExt().toLowerCase().contains("wav")) {
                                        isNeedToConvert = true;
                                    } else {
                                        CommonUtils.exportAndEncrypt(this.mContext, mid, file);
                                    }
                                    fileListToDelete.add(file);
                                }
                                Uri msgToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(mid));
                                ContentValues cv = new ContentValues();
                                if (isNeedToConvert) {
                                    Log.d(TAG, "isNeedToConvert = true");
                                    cv.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeCompressed.toString());
                                } else {
                                    Log.d(TAG, "isNeedToConvert = false");
                                    cv.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.toString());
                                }
                                this.mContext.getContentResolver().update(msgToUpdate, cv, null, null);
                                Log.d("CallLogs attachment", "updated status id: " + mid + ", to WaitingToBeCompressed");
                                for (File f : fileListToDelete) {
                                    boolean deleted = f.delete();
                                    if (deleted) {
                                        Log.d(TAG, "file deleted: " + f.getName());
                                    } else {
                                        Log.d(TAG, "could not delete: " + f.getName());
                                    }
                                }
                                Log.d("CallLogs attachment", "isNewProt = " + isNew);
                                if (!isNew) {
                                    CompressionService.startJobIntentService(this.mContext);
                                } else {
                                    Uri messageToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(mid));
                                    ContentValues cv1 = new ContentValues();
                                    cv1.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.toString());
                                    this.mContext.getContentResolver().update(messageToUpdate, cv1, null, null);
                                    Log.d("CallLogs attachment", "updated status id: " + mid + ", to WaitingToBeDelivered");
                                }
                            } else {
                                Uri path3 = list.get(i).writeToDB(this.mContext, MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, null);
                                Log.d(TAG, "Call log with no recording  ,file not found recording enabled , uri:" + path3.getEncodedPath().toString());
                            }
                        }
                        if (list.size() > 0) {
                            deleteOldFiles(list.get(0));
                        }
                    } else {
                        for (int i2 = 0; i2 < list.size(); i2++) {
                            Log.d(TAG, " recording disable insert call object:" + list.get(i2).toString());
                            Uri path4 = list.get(i2).writeToDB(this.mContext, MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, null);
                            Log.d(TAG, "Call log with no recording , recording disabled, uri:" + path4.getEncodedPath().toString());
                        }
                    }
                    if (cur.getCount() > 0) {
                        cur.moveToLast();
                        String lastDate = cur.getString(cur.getColumnIndex("date"));
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
                        editor.putString(MessageType.CallLog.name() + DATE_OF_MESSAGE, lastDate).apply();
                    }
                    cur.close();
                }
                Log.d("info", "getCallLogMessages end");
            }
        }
    }

    private String getLastDate(List<CallObj> list, String old) {
        String dateToSave = null;
        try {
            if (!TextUtils.isEmpty(old)) {
                if (Long.valueOf(old).longValue() > 0) {
                    dateToSave = old;
                }
            }
        } catch (NumberFormatException e) {
            Log.d(TAG, "no old time ");
        }
        for (CallObj callObj : list) {
            String date = callObj.getLastDate();
            if (dateToSave == null) {
                dateToSave = date;
            } else if (Long.valueOf(dateToSave).longValue() < Long.valueOf(date).longValue()) {
                dateToSave = date;
            }
        }
        return dateToSave;
    }

    private List<CallObj> getAllAggregates(Cursor cur, long startPoint, long endPoint, int aggregateId) {
        List<CallObj> list = new ArrayList<>();
        List<CallObj> ret = new ArrayList<>();
        do {
            String date = cur.getString(cur.getColumnIndex("date"));
            if (Long.valueOf(date).longValue() >= endPoint || Long.valueOf(date).longValue() < startPoint) {
                break;
            }
            CallObj callObj = getCallObj(cur);
            boolean hasCall = (TextUtils.isEmpty(callObj.getDuration()) || callObj.getDuration().equalsIgnoreCase("0")) ? false : true;
            if (hasCall) {
                callObj.setAggregateIndex(aggregateId);
                callObj.setExtraData(1);
                list.add(callObj);
            } else {
                ret.add(callObj);
            }
        } while (cur.moveToNext());
        if (list.size() > 1) {
            String duration = "" + (((int) (endPoint - startPoint)) / DefinitionsSDKKt.ID_BASE);
            MultipleCall multipleCall = new MultipleCall("" + aggregateId, "MULTIPLE CALLS", "" + startPoint, list.get(0).getType(), duration, aggregateId, 2, list.get(0).myNumber);
            multipleCall.addList(list);
            ret.add(0, multipleCall);
        } else if (list.size() > 0) {
            list.get(0).setAggregateIndex(0);
            list.get(0).setExtraData(0);
            ret.add(0, list.get(0));
        }
        return ret;
    }

    private CallObj getCallObj(Cursor cur) {
        String myNumber;
        int presentationIndex;
        int presentation;
        Log.d(TAG, "start calls cursor:");
        for (int i = 0; i < cur.getColumnCount(); i++) {
            Log.d("TAG", cur.getColumnName(i) + ":" + cur.getString(i));
        }
        if (DualSimUtils.dualSimIsOn(this.mContext)) {
            int colum = cur.getColumnIndex("subscription_id");
            if (colum > -1) {
                String simId = cur.getString(cur.getColumnIndex("subscription_id"));
                String phoneAccount = "";
                int colum2 = cur.getColumnIndex("phone_account_address");
                if (colum2 > -1) {
                    phoneAccount = cur.getString(cur.getColumnIndex("phone_account_address"));
                }
                myNumber = DualSimUtils.getPhoneByid(this.mContext, simId, phoneAccount);
            } else {
                myNumber = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("phonenumber", "999999999");
            }
        } else {
            myNumber = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("phonenumber", "999999999");
        }
        String myNumber2 = CommonUtils.formatNumber(myNumber);
        String id = cur.getString(cur.getColumnIndex("_id"));
        String date = cur.getString(cur.getColumnIndex("date"));
        String number = cur.getString(cur.getColumnIndex("number"));
        String type = cur.getString(cur.getColumnIndex("type"));
        String duration = cur.getString(cur.getColumnIndex("duration"));
        String lastModified = "";
        if (Build.VERSION.SDK_INT >= 24 && cur.getColumnIndex("last_modified") != -1) {
            lastModified = cur.getString(cur.getColumnIndex("last_modified"));
        }
        boolean z = (TextUtils.isEmpty(duration) || duration.equalsIgnoreCase("0")) ? false : true;
        boolean isPrivate = false;
        if ((Integer.valueOf(type).intValue() == 1 || Integer.valueOf(type).intValue() >= 3) && (presentationIndex = cur.getColumnIndex("presentation")) != -1 && ((presentation = cur.getInt(presentationIndex)) == 2 || presentation == 3)) {
            isPrivate = true;
            number = ArchiveRecipient.DEFAULT;
        }
        String formatNum = isPrivate ? number : CommonUtils.formatNumber(number);
        CallObj ret = new CallObj(id, formatNum, date, type, duration, lastModified, myNumber2);
        ret.setName(ContactsUtils.getContactName(this.mContext, number));
        return ret;
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x0059, code lost:
        if (com.tm.androidcopysdk.utils.DualSimUtils.isSimIDActive(r8.mContext, r0) == false) goto L10;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.util.List<com.tm.androidcopysdk.CallObj> getAllCalls(android.database.Cursor r9) {
        /*
            Method dump skipped, instructions count: 243
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.DataGrabber.getAllCalls(android.database.Cursor):java.util.List");
    }

    public static String getChangedType(String type) {
        if (type.equals(String.valueOf(1))) {
            type = "1";
        } else if (type.equals(String.valueOf(2))) {
            type = "1";
        } else if (type.equals(String.valueOf(3)) || type.equals(String.valueOf(8))) {
            type = "2";
        } else if (type.equals(String.valueOf(4))) {
            type = "4";
        } else if (type.equals(String.valueOf(5)) || type.equals(String.valueOf(9))) {
            type = "5";
        } else if (type.equals(String.valueOf(6))) {
            type = "7";
        } else if (type.equals(String.valueOf(7))) {
            type = "8";
        } else {
            Log.d(TAG, "Not correct callType!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
        return type;
    }

    public static String convertIntToCallStatus(int intType) {
        String ret = "" + intType;
        switch (intType) {
            case 1:
                ret = "Call OK";
                break;
            case 2:
                ret = "Missed Call";
                break;
            case 3:
                ret = "Failed Call";
                break;
            case 4:
                ret = "Voice Mail";
                break;
            case 5:
                ret = "Rejected Call";
                break;
            case MyAudioFormat.AUDIO_SOURCE_VOICE_RECOGNITION /* 6 */:
                ret = "Busy Call";
                break;
            case MyAudioFormat.AUDIO_SOURCE_VOICE_COMMUNICATION /* 7 */:
                ret = "Blocked Call";
                break;
            case 8:
                ret = "Answered Externally";
                break;
        }
        return ret;
    }

    public synchronized boolean getHistoryCallLogMessages(Intent intent) {
        if (handleCallLogPermissions()) {
            boolean isAllCallLogSavedToDb = false;
            Log.d("info", "getCallLogMessages start");
            String time = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("lastHistoric", "0");
            String firsttimer = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("firstHistoric", "0");
            Uri.parse("content://call_log/calls");
            if (ActivityCompat.checkSelfPermission(this.mContext, "android.permission.READ_CALL_LOG") == 0) {
                Cursor cur = this.mContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, "date < ? AND date > ?", new String[]{time, firsttimer}, "date ASC ");
                if (cur != null && cur.getCount() > 0 && cur.moveToFirst()) {
                    do {
                        String id = cur.getString(cur.getColumnIndex("_id"));
                        String number = cur.getString(cur.getColumnIndex("number"));
                        String date = cur.getString(cur.getColumnIndex("date"));
                        String type = cur.getString(cur.getColumnIndex("type"));
                        String duration = cur.getString(cur.getColumnIndex("duration"));
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("duration", duration);
                        contentValues.put("date", date);
                        contentValues.put("orig_id", CommonUtils.getUniqueId(id));
                        contentValues.put("call_type", type);
                        if (Integer.valueOf(type).intValue() == 2) {
                            contentValues.put("address_to", number);
                        }
                        if (Integer.valueOf(type).intValue() == 1 || Integer.valueOf(type).intValue() >= 3) {
                        }
                        contentValues.put("address_from", number);
                        contentValues.put("type", String.valueOf(MessageType.CallLog));
                        contentValues.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDeliveredHistory.name());
                        if (PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("recording", false) && ActivityCompat.checkSelfPermission(this.mContext, "android.permission.RECORD_AUDIO") == 0) {
                            if (waitingForRecording(intent, duration)) {
                                if (System.currentTimeMillis() - Long.valueOf(date).longValue() > 30000) {
                                    this.mContext.getContentResolver().insert(MessageContentProvider.CONTENT_URI, contentValues);
                                    Log.d(TAG, "waited more than 30sec for recording voice call, giving up, archiving wihtout voice");
                                } else {
                                    Log.d(TAG, "waiting for recording voice call");
                                }
                            } else if (failedtoRecord(intent, duration)) {
                                Log.d(TAG, "Failed to record voice call");
                                this.mContext.getContentResolver().insert(MessageContentProvider.CONTENT_URI, contentValues);
                            } else if (hasRecording(intent, duration)) {
                                contentValues.put("_data", Uri.parse(intent.getStringExtra("path")).getLastPathSegment());
                                Uri uriInsert = this.mContext.getContentResolver().insert(MessageContentProvider.CONTENT_URI, contentValues);
                                String mid = uriInsert.getLastPathSegment();
                                String filename = intent.getStringExtra("path");
                                new ByteArrayOutputStream();
                                File file = new File(filename);
                                byte[] bytesArray = new byte[(int) file.length()];
                                try {
                                    FileInputStream fis = new FileInputStream(file);
                                    fis.read(bytesArray);
                                    fis.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                                OutputStream media = null;
                                try {
                                    media = this.mContext.getContentResolver().openOutputStream(MessageContentProvider.CONTENT_URI.buildUpon().appendPath("media").appendPath(mid).appendPath(Uri.parse(filename).getLastPathSegment()).build(), "rw");
                                } catch (FileNotFoundException e3) {
                                    e3.printStackTrace();
                                }
                                try {
                                    media.write(bytesArray);
                                    media.close();
                                } catch (IOException e4) {
                                    e4.printStackTrace();
                                }
                                Boolean deleted = Boolean.valueOf(file.delete());
                                if (deleted.booleanValue()) {
                                    Log.d(TAG, "file deleted: " + file.getName());
                                } else {
                                    Log.d(TAG, "could not delete: " + file.getName());
                                }
                            } else {
                                this.mContext.getContentResolver().insert(MessageContentProvider.CONTENT_URI, contentValues);
                                Log.d(TAG, "Call log with no recroding");
                            }
                        } else {
                            this.mContext.getContentResolver().insert(MessageContentProvider.CONTENT_URI, contentValues);
                            Log.d(TAG, "Call log with no recroding");
                        }
                    } while (cur.moveToNext());
                } else {
                    isAllCallLogSavedToDb = true;
                }
                SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
                ed.putString("firstHistoric", time);
                ed.commit();
                cur.close();
                Log.d("info", "getCallLogMessages end");
                return isAllCallLogSavedToDb;
            }
            return false;
        }
        return false;
    }

    private boolean hasRecording(Intent intent, String duration) {
        return intent.getStringExtra("path") != null && Integer.valueOf(duration).intValue() > 0;
    }

    private boolean waitingForRecording(Intent intent, String duration) {
        return intent.getStringExtra("path") == null && Integer.valueOf(duration).intValue() > 0;
    }

    private void deleteOldFiles(CallObj callObj) {
        String startTime = callObj.getDate();
        Long startCall = Long.valueOf(Long.parseLong(startTime));
        File directory = this.mContext.getFilesDir();
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].exists() && !files[i].isDirectory()) {
                String fileName = files[i].getName();
                Log.d("Files", "FileName:" + fileName);
                String[] times = fileName.split("_");
                if (times.length == 2) {
                    try {
                        Log.d("Files", "FileName[0]:" + times[0]);
                        Log.d("Files", "FileName[1]:" + times[1]);
                        Long endFileTime = Long.valueOf(Long.parseLong(times[1]));
                        Log.d(TAG, "getFileName startCall(0)-fileLong  =" + (startCall.longValue() - endFileTime.longValue()));
                        if (endFileTime.longValue() < startCall.longValue()) {
                            files[i].delete();
                            Log.d(TAG, "delete old file " + fileName);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "not valid name");
                    }
                } else {
                    Log.d(TAG, "not valid name!");
                }
            }
        }
    }

    private List<RecFileExt> getFileName(CallObj callObj) {
        List<RecFileExt> retFile;
        boolean isDefaultRecorder = false;
        if (AudioSettingsManager.getInstance(this.mContext).getAudioSettings().getndk_in().equalsIgnoreCase("default_app") || !CommonUtils.getSavedCallFolderPref(this.mContext).isEmpty()) {
            isDefaultRecorder = true;
        }
        if (isDefaultRecorder) {
            if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
                retFile = getSamsungDefaultRecordedFiles(callObj);
                if (retFile == null || retFile.isEmpty()) {
                    retFile = getDefaultRecordedFiles(callObj);
                }
            } else {
                retFile = getDefaultRecordedFiles(callObj);
            }
        } else {
            retFile = getRecordedFiles(callObj);
        }
        if (retFile != null) {
            Log.d(TAG, "getFileName:" + retFile.toString());
        } else {
            Log.d(TAG, "getFileName: empty");
        }
        return retFile;
    }

    private boolean callIsEmpty(String duration) {
        return Integer.valueOf(duration).intValue() == 0;
    }

    private boolean failedtoRecord(Intent intent, String duration) {
        return intent.getStringExtra("path") != null && intent.getStringExtra("path").contentEquals("no_record") && Integer.valueOf(duration).intValue() > 0;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x0221, code lost:
        if (r34 == false) goto L26;
     */
    /* JADX WARN: Removed duplicated region for block: B:140:0x0982  */
    /* JADX WARN: Removed duplicated region for block: B:141:0x098b  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x0996  */
    /* JADX WARN: Removed duplicated region for block: B:149:0x09d9  */
    /* JADX WARN: Removed duplicated region for block: B:157:0x0b15  */
    /* JADX WARN: Removed duplicated region for block: B:158:0x0b1b  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x0c84  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized void getMMSMessages() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 3285
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.DataGrabber.getMMSMessages():void");
    }

    private File fixFile(String type, String fileNameold, InputStream in) {
        Log.d("fix_file", "file in:" + fileNameold);
        String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
        Log.d("fix_file", "file ext(new):" + ext + " type - " + type);
        String fileName = this.mContext.getFilesDir().getAbsolutePath();
        File out = new File(fileName + "/" + fileNameold + "." + ext);
        CommonUtils.copyFileByInputStream(in, out);
        Log.d("fix_file", "file out " + out.getName());
        if (out.exists()) {
            return out;
        }
        return null;
    }

    private String getConcatTypes(String parts, HashMap<String, String> hTypes) {
        String ret = "";
        String[] p = parts.split(";");
        for (int i = 0; i < p.length; i++) {
            ret = ret + hTypes.get(p[i]) + ";";
        }
        return ret.substring(0, ret.lastIndexOf(";"));
    }

    private String getConcatPartsName(Set<String> keyset, Set<String> keyset2) {
        String ret = "";
        if (keyset != null && !keyset.isEmpty()) {
            ret = join(keyset, ";");
        }
        if (keyset2 != null && !keyset2.isEmpty()) {
            if (!TextUtils.isEmpty(ret)) {
                ret = ret + ";";
            }
            ret = ret + join(keyset2, ";");
        }
        return ret;
    }

    public static String join(Set<String> set, String sep) {
        String result = null;
        if (set != null) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = set.iterator();
            if (it.hasNext()) {
                sb.append(it.next());
            }
            while (it.hasNext()) {
                sb.append(sep).append(it.next());
            }
            result = sb.toString();
        }
        return result;
    }

    private String getMmsText(String id) {
        Uri partURI = Uri.parse("content://mms/part/" + id);
        InputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = this.mContext.getContentResolver().openInputStream(partURI);
            if (is != null) {
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader reader = new BufferedReader(isr);
                for (String temp = reader.readLine(); temp != null; temp = reader.readLine()) {
                    sb.append(temp);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
        return sb.toString();
    }

    private Bitmap getMmsImage(String _id) {
        Uri partURI = Uri.parse("content://mms/part/" + _id);
        InputStream is = null;
        Bitmap bitmap = null;
        Bitmap scaled = null;
        try {
            is = this.mContext.getContentResolver().openInputStream(partURI);
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap.getByteCount() > 204800) {
                scaled = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() / 4, bitmap.getHeight() / 4, true);
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
        return scaled != null ? scaled : bitmap;
    }

    private String getMmsAttach(String _id) {
        Uri partURI = Uri.parse("content://mms/part/" + _id);
        InputStream is = null;
        byte[] bytes = new byte[0];
        try {
            is = this.mContext.getContentResolver().openInputStream(partURI);
            bytes = IOUtils.toByteArray(is);
        } catch (IOException e) {
            if (is == null) {
                return null;
            }
            try {
                is.close();
            } catch (IOException e2) {
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
                throw th;
            }
            return null;
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e4) {
            }
            return Base64.encodeToString(bytes, 0);
        }
        return null;
    }

    public void getMutexMessages() {
        this.mThread.getState();
        if (!this.mThread.isAlive()) {
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.mThread = new Thread(new Runnable() { // from class: com.tm.androidcopysdk.DataGrabber.1
                @Override // java.lang.Runnable
                public void run() {
                    DataGrabber.this.getSMSMessages();
                }
            });
            this.mThread.start();
        }
    }

    public synchronized Cursor getSMSMessages() {
        String str;
        String myNumber;
        String from;
        Contact fromName;
        String tmpTo;
        if (handleSmsPermissions()) {
            Log.d("info", "getsmsmessage start");
            long int_day = PrefManager.getLongPref(this.mContext.getApplicationContext(), PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, PreferenceManager.getDefaultSharedPreferences(this.mContext).getLong(PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, System.currentTimeMillis()));
            Uri uriSMSURI = Uri.parse("content://sms/");
            Cursor cur = this.mContext.getContentResolver().query(uriSMSURI, null, "date > ?", new String[]{Long.toString(int_day)}, "date ASC");
            if (cur != null) {
                int dupCount = 0;
                while (cur.moveToNext()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                    boolean multi = false;
                    String protocol = cur.getString(cur.getColumnIndex("protocol"));
                    cur.getString(cur.getColumnIndex("person"));
                    String address = cur.getString(cur.getColumnIndex("address"));
                    String body = cur.getString(cur.getColumnIndex("body"));
                    String id = cur.getString(cur.getColumnIndex("_id"));
                    String date1 = cur.getString(cur.getColumnIndex("date"));
                    String[] selectionargs_d = {MessageType.SMS.name(), id, MessageType.SMS.name(), "%_" + id};
                    Cursor checkCursor = this.mContext.getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "(type = ? and native_message_id = ?) OR (type = ? and (native_message_id LIKE ?) )", selectionargs_d, null);
                    boolean newMessage = true;
                    String bodyForLog = "";
                    if (!TextUtils.isEmpty(body)) {
                        bodyForLog = body.trim().split(" ")[0];
                    }
                    if (checkCursor != null) {
                        if (checkCursor.getCount() > 0) {
                            Log.d(TAG, "search dup: search:" + id + " b:" + bodyForLog + "***** count:" + checkCursor.getCount());
                            while (true) {
                                if (!checkCursor.moveToNext()) {
                                    break;
                                }
                                String dbDate = checkCursor.getString(checkCursor.getColumnIndex("date"));
                                if (dbDate.equalsIgnoreCase(date1)) {
                                    Log.d(TAG, "message id =" + id + " date =" + sdf.format(new Date(Long.valueOf(date1).longValue())) + " already exist in db");
                                    dupCount++;
                                    newMessage = false;
                                    break;
                                }
                            }
                            if (newMessage) {
                                String id2 = date1 + "_" + id;
                                Log.d(TAG, "message id(" + id + ") got new ID =" + id2);
                                id = id2;
                            }
                            checkCursor.close();
                            if (!newMessage) {
                            }
                        } else {
                            Log.d(TAG, "probably new:" + id + " b:" + bodyForLog + "*****");
                            checkCursor.close();
                        }
                    }
                    if (cur.getColumnIndex("sim_slot") > -1) {
                        Log.d(TAG, "sim slot = " + cur.getString(cur.getColumnIndex("sim_slot")));
                    } else {
                        Log.d(TAG, "sim slot = not support");
                    }
                    String date_sent = cur.getString(cur.getColumnIndex("date_sent"));
                    Log.d(TAG, "\n\n***********************");
                    Log.d(TAG, "id = " + id);
                    Log.d(TAG, "text - " + bodyForLog + "*****");
                    Log.d(TAG, "address - " + address);
                    Log.d(TAG, "date = " + date1 + " time of message: " + sdf.format(new Date(Long.valueOf(date1).longValue())));
                    Log.d(TAG, "date sent= " + date_sent + "time of message: " + sdf.format(new Date(Long.valueOf(date_sent).longValue())));
                    Log.d(TAG, "***********************\n\n");
                    String subject = cur.getString(cur.getColumnIndex("subject"));
                    String[] addList = address.split(" ");
                    String multiRecipient = "";
                    if (CommonUtils.isValidMobileNumber(address)) {
                        String address2 = CommonUtils.formatNumber(CommonUtils.getInternationalNumber(this.mContext, address));
                        address = TextUtils.isEmpty(address2) ? ArchiveRecipient.DEFAULT : CommonUtils.formatNumber(address2);
                    } else if (addList != null && addList.length > 1) {
                        boolean isValid = true;
                        int i = 0;
                        while (true) {
                            if (i < addList.length) {
                                if (CommonUtils.isValidMobileNumber(addList[i])) {
                                    i++;
                                } else {
                                    isValid = false;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (isValid) {
                            for (int i2 = 0; i2 < addList.length; i2++) {
                                if (CommonUtils.isValidMobileNumber(addList[i2])) {
                                    String aTmp = CommonUtils.getInternationalNumber(this.mContext, addList[i2]);
                                    str = multiRecipient + CommonUtils.formatNumber(aTmp) + ",";
                                } else {
                                    str = multiRecipient + addList[i2] + ",";
                                }
                                multiRecipient = str;
                            }
                            multiRecipient = multiRecipient.substring(0, multiRecipient.length() - 1);
                            multi = true;
                        }
                    }
                    if (DualSimUtils.checkForDualSimDevice(this.mContext)) {
                        if (DualSimUtils.dualSimIsOn(this.mContext)) {
                            int colum = cur.getColumnIndex("sim_slot");
                            if (colum > -1) {
                                Log.d(TAG, "get number by slot id");
                                int simSlot = Integer.parseInt(cur.getString(cur.getColumnIndex("sim_slot")));
                                myNumber = DualSimUtils.getPhoneBySlot(this.mContext, simSlot > -1 ? simSlot : 0);
                            } else {
                                int colum2 = cur.getColumnIndex("sub_id");
                                if (colum2 > -1) {
                                    Log.d(TAG, "get number by sub_id");
                                    int subId = Integer.parseInt(cur.getString(cur.getColumnIndex("sub_id")));
                                    myNumber = DualSimUtils.getNumberBySubId(this.mContext, subId);
                                } else {
                                    Log.d(TAG, "dual sim -> cant find sim id -> put my default numer");
                                    myNumber = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("phonenumber", "999999999");
                                }
                            }
                        } else {
                            int colum3 = cur.getColumnIndex("sim_slot");
                            int simSlot2 = 0;
                            if (colum3 > -1) {
                                simSlot2 = Integer.parseInt(cur.getString(cur.getColumnIndex("sim_slot")));
                            }
                            if (DualSimUtils.isSimSlotActive(this.mContext, simSlot2)) {
                                myNumber = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("phonenumber", "999999999");
                            } else {
                                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
                                editor.putString(MessageType.SMS.name() + DATE_OF_MESSAGE, date1).apply();
                            }
                        }
                    } else {
                        myNumber = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("phonenumber", "999999999");
                    }
                    if (!myNumber.contains("+") && !myNumber.contains("@")) {
                        myNumber = "+" + myNumber;
                    }
                    String[] toArray = new String[1];
                    Contact[] toName = new Contact[1];
                    if (protocol != null && protocol.contentEquals("0")) {
                        from = address;
                        if (CommonUtils.isValidMobileNumber(address)) {
                            fromName = ContactsUtils.getContactName(this.mContext, address);
                        } else {
                            Contact c = new Contact(address, "");
                            fromName = c;
                        }
                        toArray[0] = myNumber;
                        toName[0] = Contact.getMyContact(this.mContext);
                    } else {
                        from = myNumber;
                        fromName = Contact.getMyContact(this.mContext);
                        if (multi) {
                            String[] recList = multiRecipient.split(",");
                            int count = recList.length;
                            toArray = new String[count];
                            toName = new Contact[count];
                            for (int i3 = 0; i3 < recList.length; i3++) {
                                toArray[i3] = recList[i3];
                                toName[i3] = ContactsUtils.getContactName(this.mContext, recList[i3]);
                                Log.d(TAG, "toArray:" + recList[i3] + "  toName:" + toName[i3].toString());
                            }
                        } else {
                            toArray[0] = address;
                            toName[0] = ContactsUtils.getContactName(this.mContext, address);
                        }
                    }
                    if (TextUtils.isEmpty(subject)) {
                        String tmpFrom = from.startsWith("+") ? from.substring(1) : from;
                        if (multi) {
                            tmpTo = multiRecipient;
                        } else {
                            tmpTo = toArray[0].startsWith("+") ? toArray[0].substring(1) : toArray[0];
                        }
                        subject = "SMS from " + tmpFrom + " to " + tmpTo;
                    }
                    Log.d("grabber", " isAddToDB true");
                    if (1 != 0) {
                        setMessage(protocol, toArray, from, body, id, date1, subject, myNumber, CHAT_MODE.chat, null, null, fromName, from, toName, toArray);
                    }
                    SharedPreferences.Editor editor2 = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
                    editor2.putString(MessageType.SMS.name() + DATE_OF_MESSAGE, date1).apply();
                }
                Log.d(TAG, dupCount + " SMS duplicate messages were found");
            }
            Log.d("info", "getsmsmessage end");
            cur.close();
            return null;
        }
        return null;
    }

    public void finalize() {
        this.mContext.getContentResolver().unregisterContentObserver(this.smsObserver);
        this.mContext.getContentResolver().unregisterContentObserver(this.mmsObserver);
    }

    public void checkForNewMessagesToCopy() throws IOException {
        if ((this.type & AndroidCopySettings.DataType.SMS.getStatusDataType()) != 0) {
            GetMessagesService.startJobIntentService(this.mContext, "com.tm.androidcopysdk.action.getSMS");
        }
        if ((this.type & AndroidCopySettings.DataType.MMS.getStatusDataType()) != 0 && GetMessagesService.getNumOfQueueIntentsMMS() <= 2 && CommonUtils.handleSmsPermissions(this.mContext)) {
            GetMessagesService.setNumOfQueueIntentsMMS(GetMessagesService.getNumOfQueueIntentsMMS() + 1);
            GetMessagesService.startJobIntentService(this.mContext, "com.tm.androidcopysdk.action.getMMS");
        }
        if ((this.type & AndroidCopySettings.DataType.CallLogs.getStatusDataType()) != 0) {
            Intent service = new Intent(this.mContext, GetMessagesService.class);
            service.setAction("com.tm.androidcopysdk.action.getCallLog");
            this.mContext.startService(service);
        }
    }

    public void reRegisterObserver() {
        init();
    }

    private void udpateCurrentSettings() {
        Set<String> types = new HashSet<>(1);
        Set<String> types1 = PreferenceManager.getDefaultSharedPreferences(this.mContext).getStringSet("type", types);
        AndroidCopySettings mSettings = new AndroidCopySettings();
        long types_val = 0;
        for (String s1 : types1) {
            if (types_val == 0) {
                types_val = AndroidCopySettings.DataType.valueOf(s1).getStatusDataType();
            } else {
                types_val |= AndroidCopySettings.DataType.valueOf(s1).getStatusDataType();
            }
        }
        mSettings.setType(types_val);
        String saving = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("wifi3g", "WIFI3G");
        mSettings.setData(AndroidCopySettings.DataSaving.valueOf(saving));
        EventBus.getDefault().post(new SettingsEvent(mSettings));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/DataGrabber$SMSMessageContentObserver.class */
    public class SMSMessageContentObserver extends ContentObserver {
        public SMSMessageContentObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (!DataGrabber.isRun) {
                DataGrabber.isRun = true;
                try {
                    new Thread(new Runnable() { // from class: com.tm.androidcopysdk.DataGrabber.SMSMessageContentObserver.1
                        @Override // java.lang.Runnable
                        public void run() {
                            try {
                                Log.d(DataGrabber.TAG, "sleep 2500 to get the SMS");
                                Thread.sleep(2500L);
                            } catch (Exception e) {
                                DataGrabber.isRun = false;
                                e.printStackTrace();
                            }
                            try {
                                DataGrabber.isRun = false;
                                GetMessagesService.startJobIntentService(DataGrabber.this.mContext, "com.tm.androidcopysdk.action.getSMS");
                            } catch (Exception e2) {
                                Log.e(DataGrabber.TAG, "onChange , next time", e2);
                                DataGrabber.isRun = false;
                            }
                        }
                    }).start();
                } catch (Exception e) {
                    Log.e(DataGrabber.TAG, "onChange", e);
                    DataGrabber.isRun = false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/DataGrabber$MMSMessageContentObserver.class */
    public class MMSMessageContentObserver extends ContentObserver {
        public MMSMessageContentObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            if (GetMessagesService.getNumOfQueueIntentsMMS() <= 2 && CommonUtils.handleSmsPermissions(DataGrabber.this.mContext)) {
                GetMessagesService.setNumOfQueueIntentsMMS(GetMessagesService.getNumOfQueueIntentsMMS() + 1);
                GetMessagesService.startJobIntentService(DataGrabber.this.mContext, "com.tm.androidcopysdk.action.getMMS");
            }
        }

        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return false;
        }
    }

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/DataGrabber$CallLogObserver.class */
    private class CallLogObserver extends ContentObserver {
        public CallLogObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            GetMessagesService.startJobIntentService(DataGrabber.this.mContext, "com.tm.androidcopysdk.action.getCallLog");
        }

        @Override // android.database.ContentObserver
        public boolean deliverSelfNotifications() {
            return false;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onEvent(SettingsEvent event) {
        this.type = event.settings != null ? event.settings.getType() : 0L;
        Log.d(TAG, " type = " + this.type);
        boolean supportNativ = FlavorSettings.getInstance().supportNativeMsg();
        if (supportNativ) {
            if ((this.type & AndroidCopySettings.DataType.SMS.getStatusDataType()) != 0) {
                Log.d(TAG, " type = " + this.type + " SMS");
                this.mContext.getContentResolver().registerContentObserver(Uri.parse("content://sms"), true, this.smsObserver);
                new IntentFilter().addAction("android.provider.Telephony.SMS_RECEIVED");
                PackageManager pm = this.mContext.getPackageManager();
                ComponentName componentName = new ComponentName(this.mContext, SMSIncomingReceiver.class);
                pm.setComponentEnabledSetting(componentName, 1, 1);
            } else {
                this.mContext.getContentResolver().unregisterContentObserver(this.smsObserver);
                new IntentFilter().addAction("android.provider.Telephony.SMS_RECEIVED");
                PackageManager pm2 = this.mContext.getPackageManager();
                ComponentName componentName2 = new ComponentName(this.mContext, SMSIncomingReceiver.class);
                pm2.setComponentEnabledSetting(componentName2, 2, 1);
            }
        }
        if (supportNativ) {
            if ((this.type & AndroidCopySettings.DataType.MMS.getStatusDataType()) != 0) {
                Log.d(TAG, " type = " + this.type + " MMS");
                this.mContext.getContentResolver().registerContentObserver(Uri.parse("content://mms-sms/conversations"), true, this.mmsObserver);
                new IntentFilter().addAction("android.provider.Telephony.WAP_PUSH_RECEIVED");
                PackageManager pm3 = this.mContext.getPackageManager();
                ComponentName componentName3 = new ComponentName(this.mContext, MMSIncomingReceiver.class);
                pm3.setComponentEnabledSetting(componentName3, 1, 1);
            } else {
                this.mContext.getContentResolver().unregisterContentObserver(this.mmsObserver);
                new IntentFilter().addAction("android.provider.Telephony.WAP_PUSH_RECEIVED");
                PackageManager pm4 = this.mContext.getPackageManager();
                ComponentName componentName4 = new ComponentName(this.mContext, MMSIncomingReceiver.class);
                pm4.setComponentEnabledSetting(componentName4, 2, 1);
            }
        }
        if (supportNativ) {
            if ((this.type & AndroidCopySettings.DataType.CallLogs.getStatusDataType()) != 0) {
                this.telephony = (TelephonyManager) this.mContext.getSystemService("phone");
                this.telephony.listen(this.mCallrecorder, 32);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.PHONE_STATE");
                intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
                PackageManager pm5 = this.mContext.getPackageManager();
                ComponentName componentName5 = new ComponentName(this.mContext, CallReceiver.class);
                pm5.setComponentEnabledSetting(componentName5, 1, 1);
                return;
            }
            this.telephony = (TelephonyManager) this.mContext.getSystemService("phone");
            this.telephony.listen(this.mCallrecorder, 0);
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.intent.action.PHONE_STATE");
            intentFilter2.addAction("android.intent.action.NEW_OUTGOING_CALL");
            PackageManager pm6 = this.mContext.getPackageManager();
            ComponentName componentName6 = new ComponentName(this.mContext, CallReceiver.class);
            pm6.setComponentEnabledSetting(componentName6, 2, 1);
        }
    }

    public synchronized boolean getHistorySMSMessages() {
        boolean isAllSmsSavedToDb = false;
        Log.d("info", "gethistoricsmsmessage start");
        String lasttime = PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("firstHistoric", "0");
        long lastLongtime = Long.valueOf(lasttime).longValue();
        Uri uriSMSURI = Uri.parse("content://sms/");
        Cursor cur = this.mContext.getContentResolver().query(uriSMSURI, new String[]{"_id", "address", "person", "date", "body", "subject", "protocol"}, "date > ? ", new String[]{Long.toString(lastLongtime)}, "date ASC  LIMIT 100");
        Log.d("historic", "Got " + cur.getCount() + " messages from db");
        if (cur != null && cur.getCount() > 0 && cur.moveToFirst()) {
            while (cur.moveToNext()) {
                String protocol = cur.getString(cur.getColumnIndex("protocol"));
                cur.getString(cur.getColumnIndex("person"));
                String address = cur.getString(cur.getColumnIndex("address"));
                String body = cur.getString(cur.getColumnIndex("body"));
                String id = cur.getString(cur.getColumnIndex("_id"));
                String date1 = cur.getString(cur.getColumnIndex("date"));
                String subject = cur.getString(cur.getColumnIndex("subject"));
                ContentValues contentValues = new ContentValues();
                contentValues.put("body", body);
                contentValues.put("date", date1);
                contentValues.put("orig_id", CommonUtils.getUniqueId(id));
                contentValues.put("native_message_id", id);
                contentValues.put("type", MessageType.SMS.name());
                contentValues.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDeliveredHistory.name());
                if (protocol != null && protocol.contentEquals("0")) {
                    contentValues.put("address_from", address);
                } else {
                    contentValues.put("address_to", address);
                }
                if (subject != null) {
                    contentValues.put("subject", subject);
                }
                Uri path = this.mContext.getContentResolver().insert(MessageContentProvider.CONTENT_URI, contentValues);
                Log.d("info", "insert message body and id and time , path: " + path.getPath() + " date: " + date1 + " id: " + id + "   sub=" + subject);
            }
            cur.moveToLast();
            String date = cur.getString(cur.getColumnIndex("date"));
            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
            ed.putString("firstHistoric", lasttime);
            if (cur.getCount() > 0) {
                ed.putString("lastHistoric", date);
            } else {
                long int_day = PreferenceManager.getDefaultSharedPreferences(this.mContext).getLong(PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, System.currentTimeMillis());
                ed.putString("lastHistoric", String.valueOf(int_day));
            }
            ed.commit();
            GetOldMessagesService.startJobIntentService(this.mContext);
        } else {
            isAllSmsSavedToDb = true;
        }
        Log.d("info", "getsmsmessage end");
        cur.close();
        return isAllSmsSavedToDb;
    }

    /* JADX WARN: Removed duplicated region for block: B:89:0x045e  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x0535 A[Catch: FileNotFoundException -> 0x05be, LOOP:2: B:96:0x052b->B:98:0x0535, LOOP_END, TryCatch #0 {FileNotFoundException -> 0x05be, blocks: (B:95:0x04f5, B:96:0x052b, B:98:0x0535), top: B:109:0x04f5 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized boolean getHistoryMMSMessages() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 1523
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.DataGrabber.getHistoryMMSMessages():boolean");
    }

    private String getLastHistoyrMessageByType(MessageType type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        Cursor cur = this.mContext.getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "type == ? ", new String[]{type.toString()}, "date DESC");
        if (cur != null && cur.getCount() > 0) {
            cur.moveToFirst();
            String date = cur.getString(cur.getColumnIndex("date"));
            cur.getString(cur.getColumnIndex("status"));
            cur.close();
            Log.d("settings", "time of message: " + sdf.format(new Date(Long.valueOf(date).longValue())));
            return date;
        }
        cur.close();
        long int_day = PreferenceManager.getDefaultSharedPreferences(this.mContext).getLong(PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, System.currentTimeMillis());
        String currentTime = String.valueOf(int_day);
        return currentTime;
    }

    private static void putLastMessageByType(Context context, MessageType type) {
        Log.d("lior", " type = " + type.name() + "  ");
        long int_day = PreferenceManager.getDefaultSharedPreferences(context).getLong(PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, System.currentTimeMillis());
        String deliver = MessageContentProvider.MessageDeliveryStatus.Delivered.name();
        Cursor cur = context.getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "type == ? AND date > ?", new String[]{type.toString(), String.valueOf(int_day)}, "date DESC");
        int count = 0;
        if (cur != null) {
            if (cur.moveToNext()) {
                String date = cur.getString(cur.getColumnIndex("date"));
                Log.d("lior", type.name() + DATE_OF_MESSAGE + "  " + date);
                PreferenceManager.getDefaultSharedPreferences(context).edit().putString(type.name() + DATE_OF_MESSAGE, date).commit();
                cur.moveToPosition(-1);
            }
            while (cur.moveToNext()) {
                String status = cur.getString(cur.getColumnIndex("status"));
                if (status.equals(deliver)) {
                    count++;
                }
            }
            cur.close();
        }
        CommonUtils.setCountArchivedMessages(context, type, count);
    }

    public static void putLastMsgDateToSharedPrefs(Context context) {
        putLastMessageByType(context, MessageType.SMS);
        putLastMessageByType(context, MessageType.MMS);
        putLastMessageByType(context, MessageType.CallLog);
    }

    public static boolean hasWitingMessages(Context context) {
        long installation_date = PreferenceManager.getDefaultSharedPreferences(context).getLong(PrefManagerConstants.SHARED_PREFERENCE_INSTALLATION_DATE_KEY, 0L);
        String[] condition = {String.valueOf(installation_date), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.name()};
        Cursor cur = context.getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "date >= ? AND status = ? ", condition, "date DESC");
        boolean ret = cur != null && cur.getCount() > 0;
        cur.close();
        return ret;
    }

    private boolean handleSmsPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            int readSmsPermission = ActivityCompat.checkSelfPermission(this.mContext, "android.permission.READ_SMS");
            if (readSmsPermission == 0) {
                return true;
            }
            return false;
        }
        return true;
    }

    private boolean handleCallLogPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            PreferenceManager.getDefaultSharedPreferences(this.mContext).getBoolean("recording", false);
            int readCallLogsPermission = ActivityCompat.checkSelfPermission(this.mContext, "android.permission.READ_CALL_LOG");
            ActivityCompat.checkSelfPermission(this.mContext, "android.permission.RECORD_AUDIO");
            if (readCallLogsPermission == 0) {
                return true;
            }
            return false;
        }
        return true;
    }

    public DocumentFile[] checkUriFolderFile() {
        Uri uri = Uri.parse(CommonUtils.getSavedUriFolderPref(this.mContext));
        DocumentFile dfile = DocumentFile.fromTreeUri(this.mContext, uri);
        DocumentFile[] fileList = dfile.listFiles();
        return fileList;
    }

    private List<RecFileExt> getDefaultRecordedFiles(CallObj callObj) {
        File callFile;
        Log.d(TAG, "getDefaultRecordedFiles started");
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String startTime = callObj.getDate();
        String lastModifiedString = callObj.getLastModified();
        long lastModified = -1;
        Long duration = Long.valueOf(Long.parseLong(callObj.getDuration()));
        Long endCall = Long.valueOf(Long.parseLong(startTime) + (1000 * duration.longValue()));
        Long startCall = Long.valueOf(Long.parseLong(startTime));
        if (!lastModifiedString.isEmpty()) {
            lastModified = Long.parseLong(lastModifiedString);
        }
        if (!CommonUtils.getSavedCallFolderPref(this.mContext).isEmpty()) {
            callFile = Environment.getExternalStoragePublicDirectory(CommonUtils.getSavedCallFolderPref(this.mContext));
        } else {
            callFile = Environment.getExternalStoragePublicDirectory(AudioSettingsManager.getInstance(this.mContext).getAudioSettings().getPath_to_recorded_files());
        }
        List<RecFileExt> retFile = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 33) {
            DocumentFile[] fileList = checkUriFolderFile();
            Log.d(TAG, "getFileName  startTime:" + startTime + " duration:" + duration + " end:" + endCall);
            Log.d(TAG, "getFileName  file size = " + fileList.length);
            if (fileList != null) {
                for (DocumentFile file : fileList) {
                    Cursor cursor = null;
                    try {
                        try {
                            cursor = this.mContext.getContentResolver().query(file.getUri(), null, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                String displayName = cursor.getString(cursor.getColumnIndex("_display_name"));
                                String lastMod = cursor.getString(cursor.getColumnIndex("last_modified"));
                                Date lastModDateOfFile = new Date(Long.parseLong(lastMod));
                                Log.d("Files", "FileName:" + displayName);
                                String mimeType = cursor.getString(cursor.getColumnIndex("mime_type"));
                                Log.d(TAG, "mimeType startsWith = " + mimeType + " , lastModDate = " + lastModDateOfFile + "lastModifiedCallObject = " + lastModified);
                                if (!TextUtils.isEmpty(mimeType) && mimeType.startsWith("audio") && lastModDateOfFile.getTime() > startCall.longValue() && (Math.abs(lastModDateOfFile.getTime() - lastModified) <= 1500 || lastModDateOfFile.getTime() < lastModified || lastModified == -1)) {
                                    Log.d("duration", "durationCall = " + duration.toString());
                                    String baseFileName = callFile + "/" + displayName;
                                    String ext = baseFileName.substring(baseFileName.lastIndexOf("."));
                                    String generatedFileName = generateFileName();
                                    CommonUtils.copyFromStorage(this.mContext, file.getUri(), new File(generatedFileName));
                                    retFile.add(new RecFileExt(generatedFileName, ext));
                                }
                            }
                            if (cursor != null) {
                                cursor.close();
                            }
                        } catch (Exception e2) {
                            Log.e(TAG, "############", e2);
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    } catch (Throwable th) {
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                }
            }
        } else {
            File[] files = (callFile == null || !callFile.exists()) ? null : callFile.listFiles();
            if (files != null && files.length > 0) {
                Log.d(TAG, "getFileName  startTime:" + startTime + " duration:" + duration + " end:" + endCall);
                Log.d(TAG, "getFileName  file size = " + files.length);
                for (int i = 0; i < files.length; i++) {
                    if (files[i].exists() && !files[i].isDirectory()) {
                        String fileName = files[i].getName();
                        Log.d("Files", "FileName:" + fileName);
                        Date lastModDateOfFile2 = new Date(files[i].lastModified());
                        String mimeType2 = URLConnection.guessContentTypeFromName(files[i].getAbsolutePath());
                        Log.d(TAG, "mimeType startsWith = " + mimeType2 + " , lastModDate = " + lastModDateOfFile2 + "lastModifiedCallObject = " + lastModified);
                        if (!TextUtils.isEmpty(mimeType2) && mimeType2.startsWith("audio") && lastModDateOfFile2.getTime() > startCall.longValue() && (Math.abs(lastModDateOfFile2.getTime() - lastModified) <= 1500 || lastModDateOfFile2.getTime() < lastModified || lastModified == -1)) {
                            Log.d("duration", "durationCall = " + duration.toString());
                            String baseFileName2 = callFile + "/" + fileName;
                            String ext2 = baseFileName2.substring(baseFileName2.lastIndexOf("."));
                            String generatedFileName2 = generateFileName();
                            copyRecordedSamsungFile(baseFileName2, generatedFileName2);
                            retFile.add(new RecFileExt(generatedFileName2, ext2));
                        }
                    }
                }
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e3) {
                    e3.printStackTrace();
                }
                Log.d(TAG, "Default recordings list size = " + retFile.size());
                if (retFile.size() > 0) {
                    List<String> list = new ArrayList<>();
                    for (RecFileExt f : retFile) {
                        list.add(f.getName());
                    }
                    checkFileSize(duration, list);
                }
            }
        }
        return retFile;
    }

    private List<RecFileExt> getSamsungDefaultRecordedFiles(CallObj callObj) {
        String startTime;
        File callFile;
        String[] times;
        boolean hasFile;
        Log.d(TAG, "getSamsungDefaultRecordedFiles started");
        try {
            Thread.sleep(1500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (callObj instanceof MultipleCall) {
            Log.d(TAG, "getSamsungDefaultRecordedFiles");
            Log.d(TAG, "start call from obj=" + callObj.getDate() + "  start from Listener=" + CallListener.startCall);
            startTime = "" + CallListener.startCall;
        } else {
            Log.d(TAG, "getSamsungDefaultRecordedFiles regular call");
            startTime = callObj.getDate();
        }
        String lastModifiedString = callObj.getLastModified();
        long lastModified = -1;
        Long duration = Long.valueOf(Long.parseLong(callObj.getDuration()));
        Long endCall = Long.valueOf(Long.parseLong(startTime) + (1000 * duration.longValue()));
        Long startCall = Long.valueOf(Long.parseLong(startTime));
        if (!lastModifiedString.isEmpty()) {
            lastModified = Long.parseLong(lastModifiedString);
        }
        if (!CommonUtils.getSavedCallFolderPref(this.mContext).isEmpty()) {
            callFile = Environment.getExternalStoragePublicDirectory(CommonUtils.getSavedCallFolderPref(this.mContext));
        } else {
            callFile = Environment.getExternalStoragePublicDirectory(AudioSettingsManager.getInstance(this.mContext).getAudioSettings().getPath_to_recorded_files());
        }
        List<RecFileExt> retFile = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= 29) {
            DocumentFile[] fileList = checkUriFolderFile();
            Log.d(TAG, "android 12/3: getFileName  startTime:" + startTime + " duration:" + duration + " end:" + endCall);
            Log.d(TAG, "android 12/3: getFileName  file size = " + fileList.length);
            if (fileList != null) {
                for (DocumentFile file : fileList) {
                    Cursor cursor = null;
                    try {
                        try {
                            cursor = this.mContext.getContentResolver().query(file.getUri(), null, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                String displayName = cursor.getString(cursor.getColumnIndex("_display_name"));
                                String lastMod = cursor.getString(cursor.getColumnIndex("last_modified"));
                                Date lastModDateOfFile = new Date(Long.parseLong(lastMod));
                                Log.d("Files", "FileName:" + displayName);
                                String mimeType = cursor.getString(cursor.getColumnIndex("mime_type"));
                                String startFileTime = "";
                                if (displayName.substring(0, displayName.lastIndexOf(".")).split("_").length == 3) {
                                    try {
                                        String[] startFileTimes = (times[1] + "/" + times[2]).split("\\.");
                                        startFileTime = startFileTimes[0];
                                    } catch (Exception e2) {
                                        Log.d(TAG, "not valid name");
                                    }
                                } else {
                                    Log.d(TAG, "not valid name!");
                                }
                                long startFileTimeInMS = 0;
                                SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd/HHmmss");
                                try {
                                    Date mDate = sdf.parse(startFileTime);
                                    startFileTimeInMS = mDate.getTime();
                                } catch (ParseException e3) {
                                    e3.printStackTrace();
                                }
                                Log.d(TAG, "getFileName fileNameStart=" + startFileTime + " start_call= " + startCall + "startFileTimeInMS :" + startFileTimeInMS + " startFileTimeInMS-startCall=" + (startFileTimeInMS - startCall.longValue()));
                                if (startFileTimeInMS > 0) {
                                    hasFile = !TextUtils.isEmpty(mimeType) && mimeType.startsWith("audio") && startFileTimeInMS > startCall.longValue() && startFileTimeInMS < endCall.longValue();
                                } else {
                                    hasFile = !TextUtils.isEmpty(mimeType) && mimeType.startsWith("audio") && lastModDateOfFile.getTime() > startCall.longValue() && (Math.abs(lastModDateOfFile.getTime() - lastModified) <= 1500 || lastModDateOfFile.getTime() < lastModified || lastModified == -1);
                                }
                                if (hasFile) {
                                    Log.d(TAG, "mimeType startsWith = " + mimeType + " , lastModDate = " + lastModDateOfFile + "lastModifiedCallObject = " + lastModified);
                                    Log.d("duration", "durationCall = " + duration.toString());
                                    String baseFileName = callFile + "/" + displayName;
                                    String ext = baseFileName.substring(baseFileName.lastIndexOf("."));
                                    String generatedFileName = generateFileName();
                                    CommonUtils.copyFromStorage(this.mContext, file.getUri(), new File(generatedFileName));
                                    retFile.add(new RecFileExt(generatedFileName, ext));
                                }
                            }
                            if (cursor != null) {
                                cursor.close();
                            }
                        } catch (Exception e4) {
                            Log.e(TAG, "############", e4);
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    } catch (Throwable th) {
                        if (cursor != null) {
                            cursor.close();
                        }
                        throw th;
                    }
                }
            }
        } else {
            File[] files = (callFile == null || !callFile.exists()) ? null : callFile.listFiles();
            if (files != null && files.length > 0) {
                Log.d(TAG, "android 10: getFileName  startTime:" + startTime + " duration:" + duration + " end:" + endCall);
                Log.d(TAG, "android 10: getFileName  file size = " + files.length);
                for (int i = 0; i < files.length; i++) {
                    Log.d(TAG, "files[i].exists() =" + files[i].exists() + " ,, !files[i].isDirectory()= " + (!files[i].isDirectory()));
                    if (TextUtils.isEmpty(files[i].getName())) {
                        Log.d(TAG, "files[i].name = empty");
                    } else {
                        Log.d(TAG, "files[i].name =" + files[i].getName());
                    }
                    if (files[i].exists() && !files[i].isDirectory()) {
                        String fileName = files[i].getName();
                        Log.d("Files", "FileName:" + fileName);
                        String[] times2 = fileName.split("_");
                        if (times2.length == 3) {
                            try {
                                Log.d("Files", "FileName[0]:" + times2[0]);
                                Log.d("Files", "FileName[1]:" + times2[1]);
                                Log.d("Files", "FileName[2]:" + times2[2]);
                                String[] startFileTimes2 = (times2[1] + "/" + times2[2]).split("\\.");
                                String startFileTime2 = startFileTimes2[0];
                                long startFileTimeInMS2 = 0;
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyMMdd/HHmmss");
                                try {
                                    Date mDate2 = sdf2.parse(startFileTime2);
                                    startFileTimeInMS2 = mDate2.getTime();
                                    Log.d(TAG, "Date in milli :: " + startFileTimeInMS2);
                                } catch (ParseException e5) {
                                    e5.printStackTrace();
                                }
                                Log.d(TAG, "getFileName fileNameStart=" + startFileTime2 + " start_call= " + startCall + " ");
                                Log.d(TAG, "getFileName startCall +duration =" + endCall);
                                Log.d(TAG, "startFileTimeInMS :" + startFileTimeInMS2 + " startFileTimeInMS-startCall=" + (startFileTimeInMS2 - startCall.longValue()));
                                if (startFileTimeInMS2 > startCall.longValue() && (startFileTimeInMS2 < lastModified || lastModified == -1)) {
                                    Log.d(TAG, "durationCall = " + duration.toString());
                                    String baseFileName2 = Environment.getExternalStoragePublicDirectory(AudioSettingsManager.getInstance(this.mContext).getAudioSettings().getPath_to_recorded_files()) + "/" + fileName;
                                    String ext2 = baseFileName2.substring(baseFileName2.lastIndexOf("."));
                                    String generatedFileName2 = generateFileName();
                                    copyRecordedSamsungFile(baseFileName2, generatedFileName2);
                                    retFile.add(new RecFileExt(generatedFileName2, ext2));
                                }
                            } catch (Exception e6) {
                                Log.d(TAG, "not valid name");
                            }
                        } else {
                            Log.d(TAG, "not valid name!");
                        }
                    }
                }
                try {
                    Thread.sleep(2000L);
                } catch (InterruptedException e7) {
                    e7.printStackTrace();
                }
                Log.d(TAG, "samsung recordings list size = " + retFile.size());
                if (retFile.size() > 0) {
                    List<String> list = new ArrayList<>();
                    for (RecFileExt f : retFile) {
                        list.add(f.getName());
                    }
                    checkFileSize(duration, list);
                }
            } else {
                Log.d(TAG, "getFileName, files (callFile) is null or empty ");
            }
        }
        return retFile;
    }

    private void checkFileSize(Long duration, List<String> files) {
        long fileDuration = 0;
        for (String pathToFile : files) {
            File fileFrom = new File(pathToFile);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this.mContext, Uri.fromFile(fileFrom));
            String time = retriever.extractMetadata(9);
            fileDuration += Long.parseLong(time) / 1000;
            try {
                retriever.release();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        if (fileDuration < duration.longValue() - 1) {
            CommonUtils.addRecordingTerminatedDuringTheCallEvent(this.mContext, EventAbsObj.EventType.RecordingTerminatedDuringTheCallEvent, String.format(this.mContext.getString(R.string.recording_was_terminated), TMCredentialsStore.getInstance(this.mContext).userName(this.mContext), PreferenceManager.getDefaultSharedPreferences(this.mContext).getString("phonenumber", "999999999"), getTimeFromDuration(duration), getTimeFromDuration(Long.valueOf(fileDuration))));
            CommonUtils.showGeneralNotification(this.mContext, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_RECORDING_WAS_TERMINATED : this.mContext.getString(R.string.notification_recording_was_terminated), StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.NOTIFICATION_RECORDING_WAS_TERMINATED_URL : this.mContext.getString(R.string.notification_recording_was_terminated_url));
        }
        Log.d(TAG, "duration of call = " + duration + ", duration of file = " + fileDuration);
    }

    private String getTimeFromDuration(Long seconds) {
        Long.valueOf(seconds.longValue() / 86400);
        int numberOfHours = (int) ((seconds.longValue() % 86400) / 3600);
        int numberOfMinutes = (int) (((seconds.longValue() % 86400) % 3600) / 60);
        int numberOfSeconds = (int) (((seconds.longValue() % 86400) % 3600) % 60);
        String hour = twoDigitString(numberOfHours);
        String min = twoDigitString(numberOfMinutes);
        String sec = twoDigitString(numberOfSeconds);
        String formattedTime = hour + ":" + min + ":" + sec;
        return formattedTime;
    }

    private String twoDigitString(int number) {
        if (number == 0) {
            return "00";
        }
        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    private String generateFileName() {
        String fileName = this.mContext.getFilesDir().getAbsolutePath() + "/" + String.valueOf(System.currentTimeMillis() + 350);
        Log.d("CallListener", " generateFileName: " + fileName);
        return fileName;
    }

    private void copyRecordedSamsungFile(String from, String to) {
        File fileFrom = new File(from);
        File fileTo = new File(to);
        try {
            FileUtils.copyFile(fileFrom, fileTo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteFile(File file) {
        Log.d(TAG, "delete original file :" + file.getName());
        boolean res = file.delete();
        Log.d(TAG, "delete original file :" + file.getName() + "  res:" + res);
    }

    private List<RecFileExt> getRecordedFiles(CallObj callObj) {
        String startTime = callObj.getDate();
        Long duration = Long.valueOf(Long.parseLong(callObj.getDuration()));
        Long endCall = Long.valueOf(Long.parseLong(startTime) + (1000 * duration.longValue()));
        Long startCall = Long.valueOf(Long.parseLong(startTime));
        File directory = this.mContext.getFilesDir();
        File[] files = directory.listFiles();
        Log.d(TAG, "getFileName  startTime:" + startTime + " duration:" + duration + " end:" + endCall);
        Log.d(TAG, "getFileName  file size = " + files.length);
        List<RecFileExt> retFile = new ArrayList<>();
        int i = 0;
        while (true) {
            if (i >= files.length) {
                break;
            }
            if (files[i].exists() && !files[i].isDirectory()) {
                String fileName = files[i].getName();
                Log.d("Files", "FileName:" + fileName);
                String[] times = fileName.split("_");
                if (times.length == 2) {
                    try {
                        Log.d("Files", "FileName[0]:" + times[0]);
                        Log.d("Files", "FileName[1]:" + times[1]);
                        Long startFileTime = Long.valueOf(Long.parseLong(times[0]));
                        Long endFileTime = Long.valueOf(Long.parseLong(times[1]));
                        Log.d(TAG, "getFileName fileNameStart=" + startFileTime + " start_call= " + startCall + " ");
                        Log.d(TAG, "getFileName startCall +duration =" + endCall + " fileNameEnd =" + endFileTime);
                        Log.d(TAG, "getFileName startCall endCall-startFileTime=" + (endCall.longValue() - startFileTime.longValue()));
                        Log.d(TAG, "getFileName startCall endFileTime-endCall=" + (endFileTime.longValue() - endCall.longValue()));
                        if (startFileTime.longValue() < endCall.longValue() && endCall.longValue() < endFileTime.longValue()) {
                            retFile.add(new RecFileExt(this.mContext.getFilesDir().getAbsolutePath() + "/" + fileName, ".wav"));
                            break;
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "not valid name");
                    }
                } else {
                    Log.d(TAG, "not valid name!");
                }
            }
            i++;
        }
        return retFile;
    }

    public void printCursor(Cursor cur) {
        if (cur != null) {
            while (cur.moveToNext()) {
                for (int i = 0; i < cur.getColumnCount(); i++) {
                    try {
                        Log.d(TAG, cur.getColumnName(i) + " :" + cur.getString(i));
                    } catch (Exception e) {
                        Log.d(TAG, cur.getColumnName(i) + " not a string");
                    }
                }
                Log.d(TAG, "*********************");
            }
            cur.moveToFirst();
            cur.moveToPrevious();
        }
    }

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/DataGrabber$CHAT_MODE.class */
    public enum CHAT_MODE {
        chat(0),
        group(1),
        broadcast(2);
        
        int id;

        CHAT_MODE(int i) {
            this.id = i;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public int getId() {
            return this.id;
        }

        public static CHAT_MODE fromId(int id) {
            switch (id) {
                case 0:
                    return chat;
                case 1:
                    return group;
                case 2:
                    return broadcast;
                default:
                    return chat;
            }
        }
    }

    public void insertHeadersInfo(List<HeaderObj> headerObjList, String messageId) {
        if (headerObjList == null || headerObjList.size() == 0) {
            Log.d(TAG, "insertHeadersInfo list is empty // messageId = " + messageId);
            return;
        }
        Log.d(TAG, "insertHeadersInfo list size == " + headerObjList.size());
        for (HeaderObj header : headerObjList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHeadersTable.HeadersEntry.COLUMN_NAME_KEY, header.getKey());
            contentValues.put(DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE, header.getValue());
            contentValues.put(DBHeadersTable.HeadersEntry.COLUMN_NAME_MSG_ID, messageId);
            this.mContext.getContentResolver().insert(MessageContentProvider.CONTENT_URI_HEADERS, contentValues);
        }
    }

    public void updateMessageTypeForKeepAlive(String messageId, String messageType) {
        String[] condition = {messageId};
        ContentValues cv = new ContentValues();
        Uri messageToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, messageId);
        cv.put("messageType", messageType);
        this.mContext.getContentResolver().update(messageToUpdate, cv, "native_message_id = ? ", condition);
    }

    public synchronized void setMessage(String protocol, String[] toPhonesArray, String fromPhoneNumber, String body, String id, String date1, String subject, String myNumber, CHAT_MODE chatMode, String chatName, String chatId, Contact fromName, String fromValue, Contact[] toNameArray, String[] toPhoneNumberArrayValue, List<HeaderObj> headerObjList) {
        Log.d("info", "setTextMessage and headers start");
        insertHeadersInfo(headerObjList, id);
        setMessage(protocol, toPhonesArray, fromPhoneNumber, body, id, date1, subject, myNumber, chatMode, chatName, chatId, fromName, fromValue, toNameArray, toPhoneNumberArrayValue);
    }

    public synchronized void setMessage(String protocol, String[] toPhonesArray, String fromPhoneNumber, String body, String id, String date1, String subject, String myNumber, CHAT_MODE chatMode, String chatName, String chatId, Contact fromName, String fromValue, Contact[] toNameArray, String[] toPhoneNumberArrayValue) {
        setMessage(new MessageDetailsArchive(protocol, toPhonesArray, fromPhoneNumber, body, id, date1, subject, myNumber, chatMode, chatName, chatId, fromName, fromValue, toNameArray, toPhoneNumberArrayValue, FlavorSettings.getInstance().getMessageContext()));
    }

    public synchronized void setMessage(MessageDetailsArchive messageDetailsArchive) {
        Log.d("info", "setTextMessage start");
        String lasttime = getLastMessageByType(this.mContext, MessageType.SMS);
        Long.valueOf(lasttime).longValue();
        ContentValues contentValues = prepareValues(messageDetailsArchive.getProtocol(), messageDetailsArchive.getToPhonesArray(), messageDetailsArchive.getFromPhoneNumber(), messageDetailsArchive.getBody(), messageDetailsArchive.getId(), messageDetailsArchive.getDate(), messageDetailsArchive.getSubject(), messageDetailsArchive.getMyNumber(), messageDetailsArchive.getChatMode(), messageDetailsArchive.getChatName(), messageDetailsArchive.getChatId(), messageDetailsArchive.getFromName(), messageDetailsArchive.getFromValue(), messageDetailsArchive.getToNameArray(), messageDetailsArchive.getToPhoneNumberArrayValue(), messageDetailsArchive.getMessageType());
        contentValues.put("type", MessageType.SMS.name());
        Uri path = this.mContext.getContentResolver().insert(MessageContentProvider.CONTENT_URI, contentValues);
        Log.d("grabber", "insert message and id and time , id: " + path.getPath() + " " + messageDetailsArchive.getDate() + " " + messageDetailsArchive.getId() + "  sub = " + messageDetailsArchive.getSubject());
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        editor.putString(MessageType.SMS.name() + DATE_OF_MESSAGE, messageDetailsArchive.getDate()).apply();
        Log.d("info", "setTextMessage end");
        CommonUtils.startBackupService(this.mContext);
    }

    ContentValues prepareValues(String protocol, String[] toArray, String from, String body, String id, String date1, String subject, String myNumber, CHAT_MODE chatMode, String chatName, String chatId, Contact fromName, String fromValue, Contact[] toName, String[] toValue, DBKeepAliveQueryHelper.MessageType messageType) {
        String str;
        ContentValues contentValues = new ContentValues();
        contentValues.put("body", body);
        contentValues.put("date", date1);
        contentValues.put("orig_id", CommonUtils.getUniqueId(id));
        contentValues.put("native_message_id", id);
        contentValues.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.name());
        contentValues.put("chat_mode", Integer.valueOf(chatMode.getId()));
        contentValues.put("keep_alive_sent", (Integer) 0);
        contentValues.put("messageType", messageType.getType());
        contentValues.put("rearchivingFlag", (Integer) 0);
        Log.d(TAG, "id = " + id);
        String toPhoneListOrNamesIfNotExsist = CommonUtils.resolveJoinedPhoneNumbers(toArray, toName);
        boolean isValidPhoneNumber = CommonUtils.isValidPhoneNumber(from);
        String from2 = isValidPhoneNumber ? CommonUtils.formatNumber(from) : fromName.toString();
        if (protocol != null && protocol.contentEquals("0")) {
            contentValues.put("address_from", from2);
            contentValues.put("address_to", toPhoneListOrNamesIfNotExsist);
            contentValues.put("direction", "In");
        } else {
            contentValues.put("address_to", toPhoneListOrNamesIfNotExsist);
            contentValues.put("direction", "Out");
            contentValues.put("address_from", from2);
        }
        if (subject != null) {
            contentValues.put("subject", subject);
        }
        if (!TextUtils.isEmpty(chatName)) {
            contentValues.put("chat_name", chatName);
        }
        if (!TextUtils.isEmpty(chatId)) {
            contentValues.put("chat_id", chatId);
        }
        if (!Contact.isEmpty(fromName)) {
            contentValues.put("from_name", fromName.toString());
        }
        if (!TextUtils.isEmpty(from2)) {
            contentValues.put("from_value", from2);
        }
        String toContactName = "";
        if (toName.length == 0) {
            toContactName = "";
        } else {
            for (Contact strTo : toName) {
                if (Contact.isEmpty(strTo)) {
                    str = toContactName + " ;";
                } else {
                    str = toContactName + strTo.toString() + ";";
                }
                toContactName = str;
            }
        }
        String toContactName2 = toContactName.substring(0, toContactName.lastIndexOf(";"));
        if (!TextUtils.isEmpty(toContactName2)) {
            contentValues.put("to_name", toContactName2);
        }
        if (!TextUtils.isEmpty(toPhoneListOrNamesIfNotExsist)) {
            contentValues.put("to_value", toPhoneListOrNamesIfNotExsist);
        }
        return contentValues;
    }

    public synchronized void setMmsMessage(String protocol, String[] toArray, String from, String body, String id, String date1, String subject, String myNumber, CHAT_MODE chatMode, String chatName, String chatId, Contact fromName, String fromValue, Contact[] toName, String[] toValue, File file) {
        File[] files = {file};
        setMmsMessage(protocol, toArray, from, body, id, date1, subject, myNumber, chatMode, chatName, chatId, fromName, fromValue, toName, toValue, files);
    }

    public synchronized String setMmsMessage(String protocol, String[] toArray, String from, String body, String id, String date1, String subject, String myNumber, CHAT_MODE chatMode, String chatName, String chatId, Contact fromName, String fromValue, Contact[] toName, String[] toValue, File[] files, List<HeaderObj> headerObjList) {
        Log.d("info", "setMMSMessage and headers start");
        insertHeadersInfo(headerObjList, id);
        String ret = setMmsMessage(protocol, toArray, from, body, id, date1, subject, myNumber, chatMode, chatName, chatId, fromName, fromValue, toName, toValue, files);
        return ret;
    }

    public synchronized String setMmsMessage(String protocol, String[] toArray, String from, String body, String id, String date1, String subject, String myNumber, CHAT_MODE chatMode, String chatName, String chatId, Contact fromName, String fromValue, Contact[] toName, String[] toValue, File[] files) {
        Log.d("info", "getmmsmessage start");
        Log.d("DataGrabber msgId", " msgId: " + id);
        if (protocol != null && protocol.contentEquals("0")) {
            Log.d(TAG, "MMSMonitor :: Type == Outgoing MMS");
        } else {
            Log.d(TAG, "MMSMonitor :: Type == Incoming MMS");
        }
        if (!myNumber.contains("+") && !myNumber.contains("@")) {
            myNumber = "+" + myNumber;
        }
        ContentValues contentValues = prepareValues(protocol, toArray, from, body, id, date1, subject, myNumber, chatMode, chatName, chatId, fromName, fromValue, toName, toValue, FlavorSettings.getInstance().getMultimediaMessageContext());
        contentValues.put("type", String.valueOf(MessageType.MMS));
        String fileName = "";
        String type = "";
        String path = "";
        for (File f : files) {
            fileName = fileName + f.getName() + ";";
            type = type + CommonUtils.getMimeType(f.getPath()) + ";";
            path = path + f.getAbsolutePath() + ";";
        }
        if (1 != 0) {
            Log.d(TAG, "hasAttachment file names: " + fileName);
            Log.d(TAG, "_data  " + fileName);
            contentValues.put("_data", fileName);
            contentValues.put("mime", type);
            contentValues.put("_PATH", path);
            contentValues.put("status", MessageContentProvider.MessageDeliveryStatus.NotReadyToBeDelivered.name());
            contentValues.put("pendingReason", DBKeepAliveQueryHelper.MessagePendingReason.WAITING_TO_MEDIA.getType());
        }
        Log.d("grabber", "id: " + id);
        boolean isAddToDB = true;
        String[] selectionargs = {MessageType.MMS.name(), id};
        Cursor cursorFromDb = this.mContext.getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "type = ? and native_message_id = ?", selectionargs, null);
        if (cursorFromDb != null) {
            while (true) {
                if (!cursorFromDb.moveToNext()) {
                    break;
                }
                Log.d("grabber", "dbId: " + cursorFromDb.getString(cursorFromDb.getColumnIndex("native_message_id")));
                String DATA_NATIVE = cursorFromDb.getString(cursorFromDb.getColumnIndex("_data_native"));
                if (DATA_NATIVE != null && id.equals(DATA_NATIVE)) {
                    isAddToDB = false;
                    break;
                }
            }
            cursorFromDb.close();
        }
        Log.d(TAG, "isAddToDB" + isAddToDB);
        String mid = "-1";
        if (isAddToDB) {
            String bodyLog = (TextUtils.isEmpty(body) || body.trim().length() <= 2) ? body : body.substring(0, 2) + "**********";
            Log.d("BodyMMS", "BodyMMS: " + bodyLog);
            Uri uriInsert = this.mContext.getContentResolver().insert(MessageContentProvider.CONTENT_URI, contentValues);
            mid = uriInsert.getLastPathSegment();
            Log.d(TAG, "before getting media mid: " + mid);
            Log.d(TAG, "insert: id " + id + " body: " + bodyLog);
            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
            ed.putString("lastMMSID", id);
            ed.commit();
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
            editor.putString(MessageType.MMS.name() + DATE_OF_MESSAGE, date1).apply();
            Log.d(TAG, "time" + date1);
        }
        CommonUtils.startBackupService(this.mContext);
        Log.d("info", "getmmsmessage end");
        return mid;
    }

    public synchronized void updateFileMms(String dFile, boolean needCompress) {
        Log.d(TAG, "--- updateFileMms ---" + dFile);
        boolean copyWasSuccess = false;
        if (TextUtils.isEmpty(dFile)) {
            Log.d(TAG, "updateFileMms dFile isEmpty");
            return;
        }
        Log.d(TAG, "fileName " + dFile);
        Log.d(TAG, "fileName " + dFile);
        Cursor cursor = this.mContext.getContentResolver().query(MessageContentProvider.CONTENT_URI_MESSAGES, null, "_data like '%" + dFile + ";%'  AND status =  ? ", new String[]{MessageContentProvider.MessageDeliveryStatus.NotReadyToBeDelivered.name()}, null);
        int index = 0;
        if (cursor == null) {
            Log.d(TAG, "Failed to found :" + dFile);
        } else if (cursor.getCount() <= 0) {
            Log.d(TAG, "file not found (" + cursor.getCount() + ")");
            cursor.close();
        } else {
            while (cursor.moveToNext()) {
                String status = cursor.getString(cursor.getColumnIndex("status"));
                int mid = cursor.getInt(cursor.getColumnIndex("_id"));
                String filePath = cursor.getString(cursor.getColumnIndex("_PATH"));
                String updatedFilesList = cursor.getString(cursor.getColumnIndex("updated_files"));
                String fileAttchments = cursor.getString(cursor.getColumnIndex("_data"));
                int fileUpdated = cursor.getInt(cursor.getColumnIndex("extra_data"));
                String body = cursor.getString(cursor.getColumnIndex("body"));
                if (updatedFilesList == null) {
                    updatedFilesList = "";
                } else {
                    Log.d(TAG, "updatedFilesList size = " + updatedFilesList.split(";").length);
                }
                if (status.equalsIgnoreCase(MessageContentProvider.MessageDeliveryStatus.NotReadyToBeDelivered.name())) {
                    Log.d(TAG, "searchFileName :" + dFile + "  mid=" + mid);
                    String[] path = filePath.split(";");
                    String[] attchments = fileAttchments.split(";");
                    if (!TextUtils.isEmpty(updatedFilesList) && updatedFilesList.split(";").length >= attchments.length) {
                        Log.d(TAG, "updatedFilesList = " + updatedFilesList);
                    }
                    if (TextUtils.isEmpty(updatedFilesList) || !updatedFilesList.contains(dFile)) {
                        for (String s : attchments) {
                            if (s.equalsIgnoreCase(dFile)) {
                                break;
                            }
                            index++;
                        }
                        if (index >= path.length) {
                            Log.d(TAG, "index  =" + index + " attachments size = " + attchments.length + " file cant found");
                        } else {
                            String filePath2 = path[index];
                            if (filePath2.endsWith(";")) {
                                filePath2 = filePath2.substring(0, filePath2.lastIndexOf(";"));
                            }
                            File file = new File(filePath2);
                            String type = CommonUtils.getMimeType(filePath2);
                            Bitmap bitmap = null;
                            if (file.exists() && needCompress && ("image/jpeg".equals(type) || "image/bmp".equals(type) || "image/jpg".equals(type) || "image/png".equals(type))) {
                                bitmap = getMmsTelegramImage(file);
                                if (bitmap == null) {
                                }
                            }
                            if (bitmap != null) {
                                try {
                                    Log.d(TAG, "bitmap size: " + bitmap.getByteCount());
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    OutputStream media = UriUtils.getDecipheredOutStream(this.mContext, this.mContext.getContentResolver().openOutputStream(MessageContentProvider.CONTENT_URI.buildUpon().appendPath("media").appendPath("" + mid).appendPath(dFile).build(), "rw"));
                                    media.write(byteArray);
                                    Log.d(TAG, "mid: " + mid + " Size:" + byteArray.length + ", was written to media");
                                    media.close();
                                    copyWasSuccess = true;
                                    bitmap.recycle();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                                Log.d("hBitmap", "cleared inside");
                            } else {
                                try {
                                    OutputStream media2 = UriUtils.getDecipheredOutStream(this.mContext, this.mContext.getContentResolver().openOutputStream(MessageContentProvider.CONTENT_URI.buildUpon().appendPath("media").appendPath("" + mid).appendPath(dFile).build(), "rw"));
                                    InputStream is = null;
                                    try {
                                        is = new FileInputStream(file);
                                        IOUtils.copy(is, media2);
                                        copyWasSuccess = true;
                                        if (is != null) {
                                            try {
                                                is.close();
                                            } catch (IOException e3) {
                                            }
                                        }
                                    } catch (IOException e4) {
                                        Log.e(TAG, "IOException", e4);
                                        if (is != null) {
                                            try {
                                                is.close();
                                            } catch (IOException e5) {
                                            }
                                        }
                                    } catch (Exception e6) {
                                        Log.e(TAG, e6.toString());
                                        if (is != null) {
                                            try {
                                                is.close();
                                            } catch (IOException e7) {
                                            }
                                        }
                                    }
                                    Log.d(TAG, "mid: " + mid + ", was written to media");
                                    media2.close();
                                } catch (FileNotFoundException e8) {
                                    e8.printStackTrace();
                                } catch (IOException e9) {
                                    e9.printStackTrace();
                                }
                                Log.d("hBitmap", "cleared inside");
                            }
                            Uri messageToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(mid));
                            ContentValues cv = new ContentValues();
                            String updateBody = "media was deleted before obtained for archive\n" + dFile;
                            if (!copyWasSuccess) {
                                cv.put("body", TextUtils.isEmpty(body) ? updateBody : body + "\n" + updateBody);
                            }
                            if (fileUpdated + 1 == attchments.length) {
                                Log.d(TAG, "update mid= " + mid + " status to WaitingToBeDelivered");
                                cv.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.toString());
                            } else {
                                int fileUpdated2 = fileUpdated + 1;
                                Log.d(TAG, "update mid= " + mid + " EXTRA_DATA to " + fileUpdated2);
                                cv.put("extra_data", Integer.valueOf(fileUpdated2));
                            }
                            cv.put("updated_files", updatedFilesList + dFile + ";");
                            this.mContext.getContentResolver().update(messageToUpdate, cv, null, null);
                            Log.d(TAG, "updated status id: " + mid + ", to WaitingToBeDelivered");
                        }
                    } else {
                        Log.d(TAG, "File already exsist = " + dFile + "  updatedFilesList = " + updatedFilesList);
                    }
                } else {
                    Log.d(TAG, "message already updated status=" + status);
                }
            }
            cursor.close();
            CommonUtils.startBackupService(this.mContext);
        }
    }

    public void updateFileMms(String dFile, InputStream inputStream, boolean needCompress, List<HeaderObj> headerObjList) {
        Log.d(TAG, "--- updateFileMms with headers - headers size = " + headerObjList.size());
        Cursor cursor = this.mContext.getContentResolver().query(MessageContentProvider.CONTENT_URI_MESSAGES, null, "_data like '%" + dFile + ";%'  AND status =  ? ", new String[]{MessageContentProvider.MessageDeliveryStatus.NotReadyToBeDelivered.name()}, null);
        int mid = -1;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                mid = cursor.getInt(cursor.getColumnIndex("_id"));
            }
        }
        Log.d(TAG, "find message id for file = " + dFile + " the id is = " + mid);
        if (mid != -1) {
            insertHeadersInfo(headerObjList, mid + "");
        }
        Log.d(TAG, "updateFileMms inside header method.");
        updateFileMms(dFile, inputStream, needCompress);
    }

    public void updateFileMms(String dFile, InputStream inputStream, boolean needCompress) {
        Log.d(TAG, "--- updateFileMms ---" + dFile);
        if (TextUtils.isEmpty(dFile)) {
            Log.d(TAG, "updateFileMms dFile isEmpty");
            return;
        }
        Log.d(TAG, "fileName " + dFile);
        Log.d(TAG, "fileName " + dFile);
        Cursor cursor = this.mContext.getContentResolver().query(MessageContentProvider.CONTENT_URI_MESSAGES, null, "_data like '%" + dFile + ";%'  AND status =  ? ", new String[]{MessageContentProvider.MessageDeliveryStatus.NotReadyToBeDelivered.name()}, null);
        int index = 0;
        if (cursor == null) {
            Log.d(TAG, "Failed to found :" + dFile);
        } else if (cursor.getCount() <= 0) {
            Log.d(TAG, "file not found (" + cursor.getCount() + ")");
            cursor.close();
        } else {
            while (cursor.moveToNext()) {
                String status = cursor.getString(cursor.getColumnIndex("status"));
                int mid = cursor.getInt(cursor.getColumnIndex("_id"));
                String filePath = cursor.getString(cursor.getColumnIndex("_PATH"));
                String fileAttchments = cursor.getString(cursor.getColumnIndex("_data"));
                String updatedFilesList = cursor.getString(cursor.getColumnIndex("updated_files"));
                int fileUpdated = cursor.getInt(cursor.getColumnIndex("extra_data"));
                if (updatedFilesList == null) {
                    updatedFilesList = "";
                } else {
                    Log.d(TAG, "updatedFilesList size = " + updatedFilesList.split(";").length);
                }
                if (status.equalsIgnoreCase(MessageContentProvider.MessageDeliveryStatus.NotReadyToBeDelivered.name())) {
                    Log.d(TAG, "searchFileName :" + dFile + "  mid=" + mid);
                    String[] path = filePath.split(";");
                    String[] attchments = fileAttchments.split(";");
                    if (!TextUtils.isEmpty(updatedFilesList) && updatedFilesList.split(";").length >= attchments.length) {
                        Log.d(TAG, "updatedFilesList = " + updatedFilesList);
                    }
                    if (TextUtils.isEmpty(updatedFilesList) || !updatedFilesList.contains(dFile)) {
                        for (String s : attchments) {
                            if (s.equalsIgnoreCase(dFile)) {
                                break;
                            }
                            index++;
                        }
                        if (index >= path.length) {
                            Log.d(TAG, "index  =" + index + " attachments size = " + attchments.length + " file cant found");
                        } else {
                            String filePath2 = path[index];
                            if (filePath2.endsWith(";")) {
                                filePath2 = filePath2.substring(0, filePath2.lastIndexOf(";"));
                            }
                            File file = new File(filePath2);
                            String type = CommonUtils.getMimeType(filePath2);
                            Bitmap bitmap = null;
                            if (file.exists() && needCompress && ("image/jpeg".equals(type) || "image/bmp".equals(type) || "image/jpg".equals(type) || "image/png".equals(type))) {
                                bitmap = getMmsTelegramImage(file);
                                if (bitmap == null) {
                                    continue;
                                }
                            }
                            if (bitmap != null) {
                                try {
                                    Log.d(TAG, "bitmap size: " + bitmap.getByteCount());
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();
                                    OutputStream media = UriUtils.getDecipheredOutStream(this.mContext, this.mContext.getContentResolver().openOutputStream(MessageContentProvider.CONTENT_URI.buildUpon().appendPath("media").appendPath("" + mid).appendPath(dFile).build(), "rw"));
                                    media.write(byteArray);
                                    Log.d(TAG, "mid: " + mid + " Size:" + byteArray.length + ", was written to media");
                                    media.close();
                                    bitmap.recycle();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                                Log.d("hBitmap", "cleared inside");
                            } else {
                                try {
                                    OutputStream media2 = UriUtils.getDecipheredOutStream(this.mContext, this.mContext.getContentResolver().openOutputStream(MessageContentProvider.CONTENT_URI.buildUpon().appendPath("media").appendPath("" + mid).appendPath(dFile).build(), "rw"));
                                    try {
                                        try {
                                            IOUtils.copy(inputStream, media2);
                                            if (inputStream != null) {
                                                try {
                                                    inputStream.close();
                                                } catch (IOException e3) {
                                                }
                                            }
                                        } catch (IOException e4) {
                                            Log.e(TAG, "IOException", e4);
                                            if (inputStream != null) {
                                                try {
                                                    inputStream.close();
                                                } catch (IOException e5) {
                                                }
                                            }
                                        }
                                        Log.d(TAG, "mid: " + mid + ", was written to media");
                                        media2.close();
                                    } catch (Throwable th) {
                                        if (inputStream != null) {
                                            try {
                                                inputStream.close();
                                            } catch (IOException e6) {
                                            }
                                        }
                                        throw th;
                                        break;
                                    }
                                } catch (FileNotFoundException e7) {
                                    e7.printStackTrace();
                                } catch (IOException e8) {
                                    e8.printStackTrace();
                                }
                                Log.d("hBitmap", "cleared inside");
                            }
                            Uri messageToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(mid));
                            ContentValues cv = new ContentValues();
                            if (fileUpdated + 1 == attchments.length) {
                                Log.d(TAG, "update mid= " + mid + " status to WaitingToBeDelivered");
                                cv.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.toString());
                            } else {
                                int fileUpdated2 = fileUpdated + 1;
                                Log.d(TAG, "update mid= " + mid + " EXTRA_DATA to " + fileUpdated2);
                                cv.put("extra_data", Integer.valueOf(fileUpdated2));
                            }
                            cv.put("updated_files", updatedFilesList + dFile + ";");
                            this.mContext.getContentResolver().update(messageToUpdate, cv, null, null);
                            Log.d(TAG, "updated status id: " + mid + ", to WaitingToBeDelivered");
                        }
                    } else {
                        Log.d(TAG, "File already exsist = " + dFile + "  updatedFilesList = " + updatedFilesList);
                    }
                } else {
                    Log.d(TAG, "message already updated status=" + status);
                }
            }
            cursor.close();
            CommonUtils.startBackupService(this.mContext);
        }
    }

    public int reArchiveMessagesViaIds(long fromDate, long toDate, Long requestRearchiveTime) {
        long fromDate2 = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(182L);
        if (toDate < fromDate2) {
            Log.d(TAG, "reArchiveMessagesViaIds please note! toDate < fromDate!");
            return 0;
        }
        Log.d(TAG, "reArchiveMessagesViaIds called - db qwery from = " + fromDate2 + "  to = " + toDate);
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.toString());
        contentValues.put("keep_alive_sent", (Integer) 0);
        contentValues.put("rearchivingFlag", (Integer) 1);
        contentValues.put("serverResponse", (Integer) (-1));
        contentValues.put("sentToServerTime", "0");
        if (requestRearchiveTime != null) {
            contentValues.put("requestRearchiveTime", String.valueOf(requestRearchiveTime));
        }
        String[] condition = {String.valueOf(fromDate2), String.valueOf(toDate)};
        return this.mContext.getContentResolver().update(MessageContentProvider.CONTENT_URI_MESSAGES, contentValues, "date > ? AND date < ?", condition);
    }

    private Bitmap getMmsTelegramImage(File file) {
        InputStream is = null;
        Bitmap bitmap = null;
        Bitmap scaled = null;
        try {
            is = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap.getByteCount() > 204800) {
                scaled = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        } catch (IOException e2) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e3) {
                }
            }
        } catch (Throwable th) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
        if (scaled != null) {
            return scaled;
        }
        return bitmap;
    }

    private void printForTest() {
        Cursor c = this.mContext.getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "date >= 0 AND status = 'WaitingToBeDelivered' ", null, "date DESC");
        while (c.moveToNext()) {
            Log.d("lior", c.getString(c.getColumnIndex("body")));
            Log.d("lior", c.getString(c.getColumnIndex("status")));
            Log.d("lior", "****\n\n");
        }
    }

    private List<RecFileExt> cleanUpFiles(Collection<RecFileExt> files) {
        if (files == null) {
            return null;
        }
        List<RecFileExt> results = new ArrayList<>();
        for (RecFileExt file : files) {
            if (!TextUtils.isEmpty(file.getName()) && !TextUtils.isEmpty(file.getExt())) {
                results.add(file);
            }
        }
        return results;
    }
}
