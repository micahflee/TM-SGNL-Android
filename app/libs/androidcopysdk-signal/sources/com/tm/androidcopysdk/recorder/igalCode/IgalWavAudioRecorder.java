package com.tm.androidcopysdk.recorder.igalCode;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.os.Build;
import com.tm.androidcopysdk.DefinitionsSDKKt;
import com.tm.androidcopysdk.recorder.igalCode.mic.IgalCallRecorderFromMicHelper;
import com.tm.logger.Log;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/igalCode/IgalWavAudioRecorder.class */
public class IgalWavAudioRecorder {
    private static final int[] sampleRates = {44100, 22050, 11025, 8000};
    public static final boolean RECORDING_UNCOMPRESSED = true;
    public static final boolean RECORDING_COMPRESSED = false;
    private static final int TIMER_INTERVAL = 120;
    private AudioRecord audioRecorder;
    private String filePath;
    private State state;
    private RandomAccessFile randomAccessWriter;
    private short nChannels;
    private int sRate;
    private short mBitsPersample;
    private int mBufferSize;
    private int mAudioSource;
    private int aFormat;
    private int mPeriodInFrames;
    private byte[] buffer;
    private int payloadSize;
    private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener() { // from class: com.tm.androidcopysdk.recorder.igalCode.IgalWavAudioRecorder.1
        @Override // android.media.AudioRecord.OnRecordPositionUpdateListener
        public void onPeriodicNotification(AudioRecord recorder) {
            if (State.RECORDING == IgalWavAudioRecorder.this.state) {
                IgalWavAudioRecorder.this.audioRecorder.read(IgalWavAudioRecorder.this.buffer, 0, IgalWavAudioRecorder.this.buffer.length);
                try {
                    IgalWavAudioRecorder.this.randomAccessWriter.write(IgalWavAudioRecorder.this.buffer);
                    IgalWavAudioRecorder.access$412(IgalWavAudioRecorder.this, IgalWavAudioRecorder.this.buffer.length);
                    if (IgalWavAudioRecorder.this.buffer.length > 0 && IgalWavAudioRecorder.this.payloadSize % (IgalWavAudioRecorder.this.buffer.length * 100) == 0) {
                        Log.d(IgalWavAudioRecorder.this.getClass().getName(), IgalWavAudioRecorder.this.state + ":" + IgalWavAudioRecorder.this.payloadSize);
                    }
                    return;
                } catch (IOException e) {
                    Log.e(IgalWavAudioRecorder.class.getName(), "Error occured in updateListener, recording is aborted", e);
                    e.printStackTrace();
                    return;
                }
            }
            Log.d(IgalWavAudioRecorder.this.getClass().getName(), "recorder stopped");
        }

        @Override // android.media.AudioRecord.OnRecordPositionUpdateListener
        public void onMarkerReached(AudioRecord recorder) {
        }
    };

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/igalCode/IgalWavAudioRecorder$State.class */
    public enum State {
        INITIALIZING,
        READY,
        RECORDING,
        ERROR,
        STOPPED
    }

    static /* synthetic */ int access$412(IgalWavAudioRecorder x0, int x1) {
        int i = x0.payloadSize + x1;
        x0.payloadSize = i;
        return i;
    }

    public static IgalWavAudioRecorder getInstanse(Context context, int audioSource) {
        IgalWavAudioRecorder result;
        int i = 0;
        do {
            result = new IgalWavAudioRecorder(context, audioSource, 8000, 16, 2);
            i++;
        } while ((i < sampleRates.length) & (result.getState() != State.INITIALIZING));
        return result;
    }

    public State getState() {
        return this.state;
    }

    public IgalWavAudioRecorder(Context context, int audioSource, int sampleRate, int channelConfig, int audioFormat) {
        this.audioRecorder = null;
        this.filePath = null;
        try {
            if (audioFormat == 2) {
                this.mBitsPersample = (short) 16;
            } else {
                this.mBitsPersample = (short) 8;
            }
            this.nChannels = (short) 1;
            this.mAudioSource = audioSource;
            this.sRate = sampleRate;
            this.aFormat = audioFormat;
            this.mPeriodInFrames = (sampleRate * TIMER_INTERVAL) / DefinitionsSDKKt.ID_BASE;
            this.mBufferSize = (((this.mPeriodInFrames * 2) * this.nChannels) * this.mBitsPersample) / 8;
            if (this.mBufferSize < AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)) {
                this.mBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                this.mPeriodInFrames = this.mBufferSize / (((2 * this.mBitsPersample) * this.nChannels) / 8);
                Log.w(IgalWavAudioRecorder.class.getName(), "Increasing buffer size to " + Integer.toString(this.mBufferSize));
            }
            if (this.mAudioSource == 7) {
                IgalAudioRecord.set(null);
            }
            final AudioManager audioManager = (AudioManager) context.getSystemService("audio");
            boolean fix = false;
            if (!audioManager.isSpeakerphoneOn() && Build.MANUFACTURER.contentEquals("samsung")) {
                fix = true;
            }
            final boolean doFix = fix;
            this.audioRecorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, this.mBufferSize);
            if (this.mAudioSource == 7) {
                IgalAudioRecord.set2(this.audioRecorder);
                new Thread(new Runnable() { // from class: com.tm.androidcopysdk.recorder.igalCode.IgalWavAudioRecorder.2
                    @Override // java.lang.Runnable
                    public void run() {
                        if (doFix) {
                            audioManager.setSpeakerphoneOn(true);
                        }
                        for (int i = 0; i < 300; i++) {
                            if (doFix && i == 3) {
                                audioManager.setSpeakerphoneOn(false);
                            }
                            IgalAudioRecord.set2(IgalWavAudioRecorder.this.audioRecorder);
                        }
                    }
                }).start();
            } else {
                IgalCallRecorderFromMicHelper.getInstance().initialize();
                IgalCallRecorderFromMicHelper.getInstance().startRec(this.audioRecorder.getAudioSessionId());
            }
            if (this.audioRecorder.getState() != 1) {
                throw new Exception("AudioRecord initialization failed");
            }
            this.audioRecorder.setRecordPositionUpdateListener(this.updateListener);
            this.audioRecorder.setPositionNotificationPeriod(this.mPeriodInFrames);
            this.filePath = null;
            this.state = State.INITIALIZING;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(IgalWavAudioRecorder.class.getName(), e.getMessage());
            } else {
                Log.e(IgalWavAudioRecorder.class.getName(), "Unknown error occured while initializing recording");
            }
            this.state = State.ERROR;
        }
    }

    public void setOutputFile(String argPath) {
        try {
            if (this.state == State.INITIALIZING) {
                this.filePath = argPath;
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(IgalWavAudioRecorder.class.getName(), e.getMessage());
            } else {
                Log.e(IgalWavAudioRecorder.class.getName(), "Unknown error occured while setting output path");
            }
            this.state = State.ERROR;
        }
    }

    public void prepare() {
        try {
            if (this.state == State.INITIALIZING) {
                if ((this.audioRecorder.getState() == 1) & (this.filePath != null)) {
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
                    this.buffer = new byte[((this.mPeriodInFrames * this.mBitsPersample) / 8) * this.nChannels];
                    this.state = State.READY;
                } else {
                    Log.e(IgalWavAudioRecorder.class.getName(), "prepare() method called on uninitialized recorder");
                    this.state = State.ERROR;
                }
            } else {
                Log.e(IgalWavAudioRecorder.class.getName(), "prepare() method called on illegal state");
                release();
                this.state = State.ERROR;
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(IgalWavAudioRecorder.class.getName(), e.getMessage());
            } else {
                Log.e(IgalWavAudioRecorder.class.getName(), "Unknown error occured in prepare()");
            }
            this.state = State.ERROR;
        }
    }

    public void release() {
        if (this.state == State.RECORDING) {
            stop();
        } else if (this.state == State.READY) {
            try {
                this.randomAccessWriter.close();
            } catch (IOException e) {
                Log.e(IgalWavAudioRecorder.class.getName(), "I/O exception occured while closing output file");
            }
            new File(this.filePath).delete();
        }
        if (this.audioRecorder != null) {
            this.audioRecorder.release();
        }
    }

    public void reset() {
        try {
            if (this.state != State.ERROR) {
                release();
                this.filePath = null;
                this.audioRecorder = new AudioRecord(this.mAudioSource, this.sRate, this.nChannels, this.aFormat, this.mBufferSize);
                if (this.audioRecorder.getState() != 1) {
                    throw new Exception("AudioRecord initialization failed");
                }
                this.audioRecorder.setRecordPositionUpdateListener(this.updateListener);
                this.audioRecorder.setPositionNotificationPeriod(this.mPeriodInFrames);
                this.state = State.INITIALIZING;
            }
        } catch (Exception e) {
            Log.e(IgalWavAudioRecorder.class.getName(), e.getMessage());
            this.state = State.ERROR;
        }
    }

    public void start() {
        if (this.state == State.READY) {
            this.payloadSize = 0;
            this.audioRecorder.startRecording();
            this.audioRecorder.read(this.buffer, 0, this.buffer.length);
            this.state = State.RECORDING;
            return;
        }
        Log.e(IgalWavAudioRecorder.class.getName(), "start() called on illegal state");
        this.state = State.ERROR;
    }

    public void renameFile() {
        File file = new File(this.filePath);
        File dst = new File(this.filePath + "_" + String.valueOf(System.currentTimeMillis() + 150));
        file.renameTo(dst);
    }

    public void stop() {
        if (this.state == State.RECORDING) {
            this.state = State.STOPPED;
            this.audioRecorder.stop();
            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                this.randomAccessWriter.seek(4L);
                this.randomAccessWriter.writeInt(Integer.reverseBytes(36 + this.payloadSize));
                this.randomAccessWriter.seek(40L);
                this.randomAccessWriter.writeInt(Integer.reverseBytes(this.payloadSize));
                this.randomAccessWriter.close();
            } catch (IOException e2) {
                Log.e(IgalWavAudioRecorder.class.getName(), "I/O exception occured while closing output file");
                this.state = State.ERROR;
            }
        } else {
            Log.e(IgalWavAudioRecorder.class.getName(), "stop() called on illegal state");
            this.state = State.ERROR;
        }
        if (this.mAudioSource == 7) {
            IgalAudioRecord.off();
        } else {
            IgalCallRecorderFromMicHelper.getInstance().stopRec();
        }
    }
}
