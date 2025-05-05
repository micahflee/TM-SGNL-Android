package com.tm.androidcopysdk;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Process;
import android.preference.PreferenceManager;
import com.tm.androidcopysdk.Models.AudioSettings;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.recorder.IAudioRecorder;
import com.tm.androidcopysdk.recorder.RecorderBase;
import com.tm.androidcopysdk.recorder.RecorderFactory;
import com.tm.androidcopysdk.recorder.mic.RecorderHelper;
import com.tm.logger.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/CallRecorderService.class */
public class CallRecorderService extends Service {
    private String mFileName;
    private MediaRecorder mRecorder;
    private IAudioRecorder mWaveRecorder;
    private String mid;
    byte[] buffer;
    ByteBuffer myBuffer;
    private final IBinder mBinder = new LocalBinder();
    private boolean isStop = false;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override // android.app.Service
    public int onStartCommand(final Intent intent, int i, int j) {
        if (this.mRecorder == null && intent != null && intent.getAction() == "START_RECORD") {
            try {
                new Thread(new Runnable() { // from class: com.tm.androidcopysdk.CallRecorderService.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            AudioSettings audioSettings = AudioSettingsManager.getInstance(CallRecorderService.this).getAudioSettingsFromPref(CallRecorderService.this);
                            boolean isOut = intent.getBooleanExtra("isOut", true);
                            String delay = isOut ? audioSettings.getPauseBeforeRecOut() : audioSettings.getPauseBeforeRecInc();
                            int d = Integer.parseInt(delay);
                            if (d > 0) {
                                Thread.sleep(d * DefinitionsSDKKt.ID_BASE);
                            }
                            CallRecorderService.this.startRecord(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                return 1;
            } catch (Exception e) {
                e.printStackTrace();
                return 1;
            }
        }
        return 1;
    }

    private void stopRecord() {
        try {
            if (this.mWaveRecorder != null && this.mWaveRecorder.getState() == RecorderBase.State.RECORDING) {
                this.mWaveRecorder.stop();
                this.mWaveRecorder.renameFile();
                this.mWaveRecorder.reset();
                this.mWaveRecorder = null;
            }
            if (this.mRecorder != null) {
                this.mRecorder.stop();
                this.mRecorder = null;
            }
            this.isStop = true;
        } catch (IllegalStateException e) {
            Log.d("CallrecorderService", "can't stop recorder, probably not recording");
        }
        Process.killProcess(Process.myPid());
    }

    @Override // android.app.Service
    public void onDestroy() {
        stopRecord();
    }

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/CallRecorderService$LocalBinder.class */
    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        CallRecorderService getService() {
            return CallRecorderService.this;
        }
    }

    public String GetlastAudioPath() {
        if (this.mRecorder != null) {
            this.mRecorder.stop();
            return this.mFileName;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startRecord(Intent intent) throws Exception {
        Log.d("CallRecorderService", "start record");
        intent.getStringExtra("phone");
        this.mFileName = intent.getStringExtra("filename");
        Log.d("CallRecorderService", "startRecord");
        boolean isOut = intent.getBooleanExtra("isOut", true);
        if (Build.VERSION.SDK_INT > 21 || (Build.MANUFACTURER.contentEquals("samsung") && Build.VERSION.SDK_INT >= 21)) {
            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit();
            ed.putBoolean("isNewProt", false);
            ed.commit();
            AudioSettings audioSettings = AudioSettingsManager.getInstance(this).getAudioSettingsFromPref(this);
            Log.d("CallRecorderService", audioSettings.toString());
            this.mWaveRecorder = RecorderFactory.createAudioRecord(this, isOut, audioSettings);
            this.mWaveRecorder.setFile(this.mFileName);
            if (RecorderBase.State.INITIALIZING == this.mWaveRecorder.getState()) {
                this.mWaveRecorder.prepare();
                this.mWaveRecorder.start();
                RecorderHelper.getInstance().stopFixCallRecorder(isOut);
                Log.d("CallRecorderService", "startRecord ");
                return;
            } else if (RecorderBase.State.ERROR == this.mWaveRecorder.getState()) {
                Log.d("CallRecorderService", "startRecord after error");
                this.mWaveRecorder.release();
                this.mWaveRecorder = RecorderFactory.createAudioRecord(this, isOut, audioSettings);
                if (RecorderBase.State.INITIALIZING == this.mWaveRecorder.getState()) {
                    this.mWaveRecorder.setFile(this.mFileName);
                    this.mWaveRecorder.prepare();
                    this.mWaveRecorder.start();
                    RecorderHelper.getInstance().stopFixCallRecorder(isOut);
                    return;
                }
                throw new Exception("Failed to initialize an instance of the AudioRecord class.");
            } else {
                this.mWaveRecorder.stop();
                this.mWaveRecorder.reset();
                return;
            }
        }
        SharedPreferences.Editor ed2 = PreferenceManager.getDefaultSharedPreferences(this).edit();
        ed2.putBoolean("isNewProt", true);
        ed2.commit();
        this.mRecorder = new MediaRecorder();
        this.mRecorder.setAudioSource(4);
        this.mRecorder.setOutputFormat(2);
        this.mRecorder.setOutputFile(this.mFileName);
        this.mRecorder.setAudioEncoder(3);
        try {
            this.mRecorder.prepare();
            this.mRecorder.start();
        } catch (Exception e) {
            Log.e("CallrecorderService", "start() failed VOICE_CALL", e);
            try {
                this.mRecorder = new MediaRecorder();
                this.mRecorder.setAudioSource(7);
                this.mRecorder.setOutputFormat(2);
                this.mRecorder.setOutputFile(this.mFileName);
                this.mRecorder.setAudioEncoder(3);
                this.mRecorder.prepare();
                this.mRecorder.start();
            } catch (Exception e2) {
                Log.e("CallrecorderService", "start() failed VOICE_COMMUNICATION", e);
            }
        }
    }

    private void insertCallRecord(String mid) {
        if (!this.mFileName.isEmpty()) {
            try {
                FileInputStream stream = new FileInputStream(this.mFileName);
                byte[] byteArray = new byte[stream.available()];
                stream.read(byteArray);
                OutputStream media = getContentResolver().openOutputStream(MessageContentProvider.CONTENT_URI.buildUpon().appendPath("media").appendPath(mid).appendPath(this.mFileName).build(), "rw");
                media.write(byteArray);
                media.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }
}
