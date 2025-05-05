package com.tm.androidcopysdk;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import com.tm.androidcopysdk.compress.AndroidAudioConverter;
import com.tm.androidcopysdk.compress.AudioFormat;
import com.tm.androidcopysdk.compress.IConvertCallback;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.logger.Log;
import com.tm.utils.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/CompressionService.class */
public class CompressionService extends JobIntentService {
    private static final String TAG = "CompressionService";
    private static final int MAX_NUMBER_OF_RETRIES = 2;
    public static final String ACTION_COMPRESS = "com.tm.androidcopysdk.action.compress";
    private static boolean mInWork;
    private static boolean restart = false;
    public static int id = DefinitionsSDKKt.COMPRESSION_SERVICE_JOB_ID;

    protected void onHandleWork(@NonNull Intent intent) {
        if (intent != null) {
            Log.d(TAG, "onHandleIntent start");
            String action = intent.getAction();
            if (ACTION_COMPRESS.equals(action)) {
                Log.d(TAG, "onHandleIntent: mInWork = " + mInWork);
                if (!mInWork) {
                    restart = false;
                }
                handleListCompression();
            }
        }
    }

    private boolean checkMultiple(int index) {
        boolean ret = false;
        String[] condition = {String.valueOf(index)};
        Cursor cur = getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "extra_data = ?", condition, null);
        if (cur != null) {
            ret = cur.getCount() > 1;
            cur.close();
        }
        return ret;
    }

    private boolean isOutgoingMultipleCall(int index) {
        boolean ret = false;
        String[] condition = {String.valueOf(index)};
        Cursor cur = getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "extra_data = ?", condition, "date ASC");
        if (cur != null) {
            cur.moveToFirst();
            ret = !TextUtils.isEmpty(cur.getString(cur.getColumnIndex("address_to")));
            cur.close();
        }
        return ret;
    }

    private long calculateMultipleDuration(int index) {
        String[] condition = {String.valueOf(index)};
        Cursor cur = getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "extra_data = ?", condition, "date ASC");
        long startTime = 0;
        long endTime = 0;
        if (cur != null) {
            while (cur.moveToNext()) {
                String date = cur.getString(cur.getColumnIndex("date"));
                startTime = startTime == 0 ? Long.valueOf(date).longValue() : Math.min(startTime, Long.valueOf(date).longValue());
                String duration = cur.getString(cur.getColumnIndex("duration"));
                long tmpEnd = Long.valueOf(date).longValue() + (1000 * Long.valueOf(duration).longValue());
                endTime = Math.max(endTime, tmpEnd);
            }
            cur.close();
        }
        return ((int) (endTime - startTime)) / DefinitionsSDKKt.ID_BASE;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void handleCompression() {
        Log.d(TAG, "handleCompression mInWork = true");
        mInWork = true;
        String filePath = null;
        String mid = null;
        boolean isOutgoingCall = true;
        boolean handleFinish = false;
        long callDuration = 0;
        long timeBeforeAnswerSec = 0;
        String[] condition = {MessageContentProvider.MessageDeliveryStatus.WaitingToBeCompressed.name()};
        Cursor cur = getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "status = ?  and comp_retries < 2", condition, "comp_retries");
        try {
            if (cur != null) {
                try {
                    if (cur.moveToNext()) {
                        mid = cur.getString(cur.getColumnIndex("_id"));
                        String filename = cur.getString(cur.getColumnIndex("_data"));
                        if (!TextUtils.isEmpty(filename)) {
                            String srcPath = getFilesDir() + "/" + mid + "/" + Uri.parse(filename).getLastPathSegment();
                            File src = new File(srcPath);
                            Log.d(TAG, "src file size: " + src.length());
                            Log.d(TAG, "src file path " + src.getAbsolutePath());
                            String destPath = getFilesDir() + "/" + mid + "/";
                            File dst = new File(destPath);
                            File expFile = new File(destPath + src.getName() + ".wav");
                            try {
                                CommonUtils.exportFile(this, src, dst, expFile, false);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String duration = cur.getString(cur.getColumnIndex("duration"));
                            String directionOut = cur.getString(cur.getColumnIndex("address_to"));
                            filePath = getFilesDir() + "/" + mid + "/" + filename + ".wav";
                            isOutgoingCall = !TextUtils.isEmpty(directionOut);
                            callDuration = Long.parseLong(duration);
                            timeBeforeAnswerSec = getFileDurationSeconds(src) - callDuration;
                            Log.d(TAG, "filePath:  " + filePath);
                            Log.d(TAG, "callDuration:  " + callDuration + " ; timeBeforeAnswerSec = " + timeBeforeAnswerSec);
                        }
                    } else {
                        mInWork = false;
                    }
                    cur.close();
                } catch (Exception e2) {
                    Log.e(TAG, "error", e2);
                    handleFinish = true;
                    cur.close();
                }
                if (mInWork) {
                    if (handleFinish) {
                        handleFailure(filePath, mid);
                        handleFinish(mid);
                        return;
                    }
                    compressWavFile(filePath, mid, isOutgoingCall, callDuration, timeBeforeAnswerSec, true);
                }
            }
        } catch (Throwable th) {
            cur.close();
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void handleListCompression() {
        Log.d(TAG, "handleListCompression mInWork = true");
        mInWork = true;
        restart = false;
        List<String> filePath = new ArrayList<>();
        String mid = null;
        boolean isOutgoingCall = true;
        boolean handleFinish = false;
        long callDuration = 0;
        long timeBeforeAnswerSec = 0;
        String[] condition = {MessageContentProvider.MessageDeliveryStatus.WaitingToBeCompressed.name()};
        Cursor cur = getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "status = ?  and comp_retries < 2", condition, "comp_retries");
        try {
            if (cur != null) {
                try {
                    if (cur.moveToNext()) {
                        mid = cur.getString(cur.getColumnIndex("_id"));
                        String filename = cur.getString(cur.getColumnIndex("_data"));
                        if (!TextUtils.isEmpty(filename)) {
                            String srcPath = Uri.parse(filename).getLastPathSegment();
                            List<String> flistFileName = Arrays.asList(srcPath.split(";"));
                            for (String name : flistFileName) {
                                File src = new File(getFilesDir() + "/" + mid + "/" + name);
                                Log.d(TAG, "src file size: " + src.length());
                                Log.d(TAG, "src file path " + src.getAbsolutePath());
                                String destPath = getFilesDir() + "/" + mid + "/";
                                File dst = new File(destPath);
                                File expFile = new File(destPath + src.getName() + ".wav");
                                try {
                                    CommonUtils.exportFile(this, src, dst, expFile, false);
                                } catch (IOException e) {
                                    Log.e(TAG, " IOException :", e);
                                }
                                filePath.add(expFile.getPath());
                                String duration = cur.getString(cur.getColumnIndex("duration"));
                                callDuration = Long.parseLong(duration);
                                if (flistFileName.size() > 1) {
                                    timeBeforeAnswerSec = -1;
                                } else {
                                    timeBeforeAnswerSec = getFileDurationSeconds(src) - callDuration;
                                }
                            }
                            String directionOut = cur.getString(cur.getColumnIndex("address_to"));
                            isOutgoingCall = !TextUtils.isEmpty(directionOut);
                            Log.d(TAG, "filePath:  " + filePath);
                            Log.d(TAG, "callDuration:  " + callDuration + " ; timeBeforeAnswerSec = " + timeBeforeAnswerSec);
                        }
                    } else {
                        Log.d(TAG, "cur is empty");
                        mInWork = false;
                        stopJob();
                    }
                    cur.close();
                } catch (Exception e2) {
                    Log.e(TAG, "error", e2);
                    handleFinish = true;
                    cur.close();
                }
                if (mInWork) {
                    if (filePath != null && filePath.size() > 0) {
                        int i = 0;
                        while (i < filePath.size()) {
                            if (handleFinish) {
                                handleFailure(filePath.get(i), mid);
                                handleFinish(mid);
                            } else {
                                compressWavFile(filePath.get(i), mid, isOutgoingCall, callDuration, timeBeforeAnswerSec, i == filePath.size() - 1);
                            }
                            i++;
                        }
                        return;
                    }
                    Log.d(TAG, " call stop job");
                    stopJob();
                    return;
                }
                return;
            }
            Log.d(TAG, "cur is NULL");
            mInWork = false;
            stopSelf();
        } catch (Throwable th) {
            cur.close();
            throw th;
        }
    }

    private static long getFileDurationSeconds(File file) throws Exception {
        long ret;
        if (file != null && file.exists() && file.length() > 0) {
            boolean tryInputstream = false;
            FileInputStream inputStream = null;
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            try {
                mediaMetadataRetriever.setDataSource(file.getAbsolutePath());
            } catch (Exception ex) {
                tryInputstream = true;
                ex.printStackTrace();
            }
            if (tryInputstream) {
                try {
                    inputStream = new FileInputStream(file.getAbsolutePath());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    inputStream = null;
                }
                if (inputStream != null) {
                    mediaMetadataRetriever.setDataSource(inputStream.getFD());
                }
            }
            String durationStr = mediaMetadataRetriever.extractMetadata(9);
            if (TextUtils.isEmpty(durationStr)) {
                ret = 0;
            } else {
                try {
                    ret = Long.parseLong(durationStr) / 1000;
                } catch (NumberFormatException e2) {
                    Log.e(TAG, "getFileDurationSeconds", e2);
                    ret = 0;
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            return ret;
        }
        return 0L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public void handleFailure(String filePath, String msgId) {
        File file = new File(filePath);
        String[] condition = {"" + msgId};
        Cursor cur = getContentResolver().query(MessageContentProvider.CONTENT_URI, null, "_id = ?", condition, null);
        if (cur.moveToNext()) {
            int retries = cur.getInt(cur.getColumnIndex("comp_retries"));
            String statusToUpdate = retries >= 1 ? MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.toString() : MessageContentProvider.MessageDeliveryStatus.WaitingToBeCompressed.toString();
            if (retries >= 1) {
                String destPath = getFilesDir() + "/" + msgId + "/";
                File dst = new File(destPath);
                File expFile = new File(destPath + file.getName().replaceAll(".wav", ""));
                try {
                    CommonUtils.exportFile(this, file, dst, expFile, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Uri messageToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(msgId));
            ContentValues cv = new ContentValues();
            cv.put("status", statusToUpdate);
            cv.put("comp_retries", Integer.valueOf(retries + 1));
            int count = getContentResolver().update(messageToUpdate, cv, null, null);
            Log.d(TAG, count + "   updated status id: " + msgId + ", to " + statusToUpdate);
            CommonUtils.addEvent(this, EventAbsObj.EventType.FailedToConvert2MP3);
        }
        cur.close();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void compressWavFile(final String filePath, final String msgId, boolean isOutgoingCall, long durationSec, long timeBeforeAnswerSec, final boolean isNeedToWaitToBeDeliverres) {
        File file = new File(filePath);
        Log.d(TAG, "file " + file.getName() + ", size before compression: " + file.length());
        IConvertCallback callback = new IConvertCallback() { // from class: com.tm.androidcopysdk.CompressionService.1
            @Override // com.tm.androidcopysdk.compress.IConvertCallback
            public void onSuccess(File convertedFile) {
                Log.d(CompressionService.TAG, "file " + convertedFile.getName() + ", size after compression: " + convertedFile.length());
                CommonUtils.exportAndEncrypt(CompressionService.this.getBaseContext(), msgId, convertedFile);
                Uri messageToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, String.valueOf(msgId));
                if (isNeedToWaitToBeDeliverres) {
                    ContentValues cv = new ContentValues();
                    cv.put("status", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.toString());
                    CompressionService.this.getBaseContext().getContentResolver().update(messageToUpdate, cv, null, null);
                    CommonUtils.startBackupService(CompressionService.this.getBaseContext());
                    Log.d(CompressionService.TAG, "updated status id: " + msgId + ", to WaitingToBeDelivered");
                    CompressionService.this.handleFinish(msgId);
                }
            }

            @Override // com.tm.androidcopysdk.compress.IConvertCallback
            public void onFailure(Exception error) {
                Log.e(CompressionService.TAG, "error in compression: ", error);
                CompressionService.this.handleFailure(filePath, msgId);
                CompressionService.this.handleFinish(msgId);
            }
        };
        String bitRate = Util.getBitRate(getApplication());
        AndroidAudioConverter.with(this).setFile(file).setFormat(AudioFormat.MP3).setCallback(callback).convert(isOutgoingCall, durationSec, timeBeforeAnswerSec, bitRate != null ? bitRate : "32k");
    }

    private void deleteFiles(String externalPath) {
        File externalFilesDirectory = new File(externalPath);
        File[] filesToDelete = externalFilesDirectory.listFiles();
        for (File f : filesToDelete) {
            String name = f.getName();
            if (name.contains(".mp3") || name.contains(".wav")) {
                if (f.delete()) {
                    Log.d(TAG, f + " file deleted");
                } else {
                    Log.d(TAG, f + " could not be deleted");
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFinish(String msgId) {
        Log.d(TAG, "handleFinish " + msgId);
        restart = true;
        deleteFiles(getFilesDir() + "/" + msgId + "/");
        stopJob();
        Log.d(TAG, "handleFinish end");
    }

    private static void enqueueWork(Context compressionService, Intent compIntent) {
        Log.d(TAG, "enqueueWork ");
        enqueueWork(compressionService, CompressionService.class, DefinitionsSDKKt.COMPRESSION_SERVICE_JOB_ID, compIntent);
    }

    public static void startJobIntentService(Context context) {
        Log.d(TAG, "startService");
        Intent compIntent = new Intent(context, CompressionService.class);
        compIntent.setAction(ACTION_COMPRESS);
        enqueueWork(context, compIntent);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy restart = " + restart);
        if (restart) {
            restart = false;
            Intent compIntent = new Intent((Context) this, (Class<?>) CompressionService.class);
            compIntent.setAction(ACTION_COMPRESS);
            enqueueWork(this, compIntent);
        }
    }

    private void stopJob() {
        Log.d(TAG, "stopJob ");
        mInWork = false;
        stopSelf();
    }
}
