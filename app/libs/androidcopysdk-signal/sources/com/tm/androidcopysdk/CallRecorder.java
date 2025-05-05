package com.tm.androidcopysdk;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import com.tm.logger.Log;
import java.io.IOException;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/CallRecorder.class */
public class CallRecorder {
    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String mFileName = null;
    private final Context mContext;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {"android.permission.RECORD_AUDIO"};

    public CallRecorder(Context context) {
        this.mContext = context;
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        this.mPlayer = new MediaPlayer();
        try {
            this.mPlayer.setDataSource(mFileName);
            this.mPlayer.prepare();
            this.mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        this.mPlayer.release();
        this.mPlayer = null;
    }

    private void startRecording() {
        mFileName = this.mContext.getExternalCacheDir().getAbsolutePath();
        mFileName += "/audiorecord.3gp";
        this.mRecorder = new MediaRecorder();
        this.mRecorder.setAudioSource(4);
        this.mRecorder.setOutputFormat(1);
        this.mRecorder.setOutputFile(mFileName);
        this.mRecorder.setAudioEncoder(1);
        try {
            this.mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        this.mRecorder.start();
    }

    private void stopRecording() {
        this.mRecorder.stop();
        this.mRecorder.release();
        this.mRecorder = null;
    }
}
