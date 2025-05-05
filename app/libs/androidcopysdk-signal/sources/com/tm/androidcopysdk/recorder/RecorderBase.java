package com.tm.androidcopysdk.recorder;

import com.tm.androidcopysdk.recorder.mic.CallRecorderFromMicHelper;
import com.tm.logger.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/RecorderBase.class */
public abstract class RecorderBase implements IAudioRecorder {
    public static final int[] sampleRates = {44100, 22050, 11025, 8000};
    public static final String TAG = "RecorderBase";
    public static final boolean RECORDING_UNCOMPRESSED = true;
    public static final boolean RECORDING_COMPRESSED = false;
    protected static final int TIMER_INTERVAL = 120;
    protected State state;
    protected RandomAccessFile randomAccessWriter;
    protected int mBufferSize;
    protected int mAudioSource;
    protected int aFormat;
    protected int mPeriodInFrames;
    protected byte[] buffer;
    protected int payloadSize;
    protected String filePath = null;
    protected short nChannels = 1;
    protected int sRate = 8000;
    protected short mBitsPersample = 16;
    protected boolean isOut = true;

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/RecorderBase$CodeError.class */
    public enum CodeError {
        ERROR_BUFFER_SIZE,
        ERROR_BUFFER_SIZE_STEREO,
        ERROR_INITIALIZE_RECORDER
    }

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/RecorderBase$State.class */
    public enum State {
        INITIALIZING,
        READY,
        RECORDING,
        ERROR,
        STOPPED
    }

    @Override // com.tm.androidcopysdk.recorder.IAudioRecorder
    public abstract void prepare();

    @Override // com.tm.androidcopysdk.recorder.IAudioRecorder
    public abstract void start() throws Exception;

    @Override // com.tm.androidcopysdk.recorder.IAudioRecorder
    public abstract void reset();

    @Override // com.tm.androidcopysdk.recorder.IAudioRecorder
    public abstract void release();

    /* JADX INFO: Access modifiers changed from: protected */
    public void prepareRandomAccess() throws IOException {
        Log.d("NativeWavRecorder", "prepareRandomAccess");
        this.randomAccessWriter = new RandomAccessFile(this.filePath, "rw");
        this.randomAccessWriter.setLength(0L);
        this.randomAccessWriter.writeBytes("RIFF");
        this.randomAccessWriter.writeInt(0);
        this.randomAccessWriter.writeBytes("WAVE");
        this.randomAccessWriter.writeBytes("fmt ");
        this.randomAccessWriter.writeInt(Integer.reverseBytes(16));
        this.randomAccessWriter.writeShort(Short.reverseBytes((short) 1));
        this.randomAccessWriter.writeShort(Short.reverseBytes(this.nChannels));
        this.randomAccessWriter.writeInt(Integer.reverseBytes(this.sRate));
        this.randomAccessWriter.writeInt(Integer.reverseBytes(((this.sRate * this.nChannels) * this.mBitsPersample) / 8));
        this.randomAccessWriter.writeShort(Short.reverseBytes((short) ((this.nChannels * this.mBitsPersample) / 8)));
        this.randomAccessWriter.writeShort(Short.reverseBytes(this.mBitsPersample));
        this.randomAccessWriter.writeBytes("data");
        this.randomAccessWriter.writeInt(0);
        this.state = State.READY;
    }

    @Override // com.tm.androidcopysdk.recorder.IAudioRecorder
    public void stop() {
        CallRecorderFromMicHelper.getInstance().stopRec(this.isOut);
    }

    @Override // com.tm.androidcopysdk.recorder.IAudioRecorder
    public void setFile(String argPath) {
        Log.d(TAG, " setOutputFile:" + argPath);
        try {
            Log.d(TAG, "state = " + this.state.name());
            if (this.state == State.INITIALIZING) {
                this.filePath = argPath;
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(RecorderBase.class.getName(), e.getMessage());
            } else {
                Log.e(RecorderBase.class.getName(), "Unknown error occured while setting output path");
            }
            this.state = State.ERROR;
        }
    }

    @Override // com.tm.androidcopysdk.recorder.IAudioRecorder
    public void renameFile() {
        File file = new File(this.filePath);
        File dst = new File(this.filePath + "_" + String.valueOf(System.currentTimeMillis() + 150));
        file.renameTo(dst);
    }

    @Override // com.tm.androidcopysdk.recorder.IAudioRecorder
    public State getState() {
        return this.state;
    }
}
