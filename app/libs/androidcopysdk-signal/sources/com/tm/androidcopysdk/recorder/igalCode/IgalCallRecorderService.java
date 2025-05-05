package com.tm.androidcopysdk.recorder.igalCode;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.tm.androidcopysdk.AudioSettingsManager;
import com.tm.androidcopysdk.DefinitionsSDKKt;
import com.tm.androidcopysdk.Models.AudioSettings;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.recorder.igalCode.IgalWavAudioRecorder;
import com.tm.logger.Log;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/igalCode/IgalCallRecorderService.class */
public class IgalCallRecorderService extends Service {
    private String mFileName;
    private MediaRecorder mRecorder;
    private IgalWavAudioRecorder mWaveRecorder;
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
    public int onStartCommand(Intent intent, int i, int j) {
        if (this.mRecorder == null && intent != null && intent.getAction() == "START_RECORD") {
            AudioSettings audioSettings = AudioSettingsManager.getInstance(this).getAudioSettingsFromPref(this);
            boolean isOut = intent.getBooleanExtra("isOut", true);
            String delay = isOut ? audioSettings.getPauseBeforeRecOut() : audioSettings.getPauseBeforeRecInc();
            int d = Integer.parseInt(delay);
            if (d > 0) {
                try {
                    Thread.sleep(d * DefinitionsSDKKt.ID_BASE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            startRecord(intent);
            return 1;
        }
        return 1;
    }

    @Override // android.app.Service
    public void onDestroy() {
        try {
            if (this.mWaveRecorder != null) {
                this.mWaveRecorder.stop();
                this.mWaveRecorder.renameFile();
                this.mWaveRecorder.reset();
            }
            if (this.mRecorder != null) {
                this.mRecorder.stop();
                this.mRecorder = null;
            }
            this.isStop = true;
        } catch (IllegalStateException e) {
            Log.d("CallrecorderService", "can't stop recorder, probably not recording");
        }
    }

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/igalCode/IgalCallRecorderService$LocalBinder.class */
    public class LocalBinder extends Binder {
        public LocalBinder() {
        }

        IgalCallRecorderService getService() {
            return IgalCallRecorderService.this;
        }
    }

    public String GetlastAudioPath() {
        if (this.mRecorder != null) {
            this.mRecorder.stop();
            return this.mFileName;
        }
        return null;
    }

    private void startRecord(Intent intent) {
        Log.d("IgalCallRecorderService", "start record");
        intent.getStringExtra("phone");
        this.mFileName = intent.getStringExtra("filename");
        boolean isOut = intent.getBooleanExtra("isOut", true);
        if (Build.VERSION.SDK_INT > 21 || (Build.MANUFACTURER.contentEquals("samsung") && Build.VERSION.SDK_INT >= 21)) {
            SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(this).edit();
            ed.putBoolean("isNewProt", false);
            ed.commit();
            AudioSettings audioSettings = AudioSettingsManager.getInstance(this).getAudioSettingsFromPref(this);
            String callType = isOut ? audioSettings.getAudioSourceOut() : audioSettings.getAudioSourceInc();
            int audioSource = Integer.parseInt(callType);
            this.mWaveRecorder = IgalWavAudioRecorder.getInstanse(this, audioSource);
            this.mWaveRecorder.setOutputFile(this.mFileName);
            if (IgalWavAudioRecorder.State.INITIALIZING == this.mWaveRecorder.getState()) {
                this.mWaveRecorder.prepare();
                this.mWaveRecorder.start();
                return;
            } else if (IgalWavAudioRecorder.State.ERROR == this.mWaveRecorder.getState()) {
                this.mWaveRecorder.release();
                this.mWaveRecorder = IgalWavAudioRecorder.getInstanse(this, audioSource);
                this.mWaveRecorder.setOutputFile(this.mFileName);
                return;
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
