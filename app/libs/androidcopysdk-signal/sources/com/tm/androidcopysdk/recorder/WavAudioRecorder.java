package com.tm.androidcopysdk.recorder;

import android.content.Context;
import android.media.AudioRecord;
import com.tm.androidcopysdk.DefinitionsSDKKt;
import com.tm.androidcopysdk.recorder.RecorderBase;
import com.tm.androidcopysdk.recorder.mic.CallRecorderFromMicHelper;
import com.tm.logger.Log;
import java.io.File;
import java.io.IOException;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/WavAudioRecorder.class */
public class WavAudioRecorder extends RecorderBase {
    protected AudioRecord audioRecorder;
    private AudioRecord.OnRecordPositionUpdateListener updateListener = new AudioRecord.OnRecordPositionUpdateListener() { // from class: com.tm.androidcopysdk.recorder.WavAudioRecorder.1
        @Override // android.media.AudioRecord.OnRecordPositionUpdateListener
        public void onPeriodicNotification(AudioRecord recorder) {
            if (RecorderBase.State.RECORDING != WavAudioRecorder.this.state) {
                Log.d(WavAudioRecorder.this.getClass().getName(), "recorder stopped");
                return;
            }
            WavAudioRecorder.this.audioRecorder.read(WavAudioRecorder.this.buffer, 0, WavAudioRecorder.this.buffer.length);
            try {
                WavAudioRecorder.this.randomAccessWriter.write(WavAudioRecorder.this.buffer);
                WavAudioRecorder.this.payloadSize += WavAudioRecorder.this.buffer.length;
                if (WavAudioRecorder.this.buffer.length > 0 && WavAudioRecorder.this.payloadSize % (WavAudioRecorder.this.buffer.length * 100) == 0) {
                    Log.d(WavAudioRecorder.this.getClass().getName(), WavAudioRecorder.this.state + ":" + WavAudioRecorder.this.payloadSize);
                }
            } catch (IOException e) {
                Log.e(WavAudioRecorder.class.getName(), "Error occured in updateListener, recording is aborted", e);
                e.printStackTrace();
            }
        }

        @Override // android.media.AudioRecord.OnRecordPositionUpdateListener
        public void onMarkerReached(AudioRecord recorder) {
        }
    };

    public static WavAudioRecorder getInstanse(Context context, int audioSource) {
        WavAudioRecorder result;
        int i = 0;
        do {
            result = new WavAudioRecorder(context, audioSource, 8000, 16, 2);
            i++;
        } while ((i < sampleRates.length) & (result.getState() != RecorderBase.State.INITIALIZING));
        return result;
    }

    public WavAudioRecorder(Context context, int audioSource, int sampleRate, int channelConfig, int audioFormat) {
        this.audioRecorder = null;
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
            this.mPeriodInFrames = (sampleRate * 120) / DefinitionsSDKKt.ID_BASE;
            this.mBufferSize = (((this.mPeriodInFrames * 2) * this.nChannels) * this.mBitsPersample) / 8;
            if (this.mBufferSize < AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)) {
                this.mBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
                this.mPeriodInFrames = this.mBufferSize / (((2 * this.mBitsPersample) * this.nChannels) / 8);
                Log.w(WavAudioRecorder.class.getName(), "Increasing buffer size to " + Integer.toString(this.mBufferSize));
            }
            this.audioRecorder = new AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, this.mBufferSize);
            CallRecorderFromMicHelper.getInstance().initialize();
            CallRecorderFromMicHelper.getInstance().startRec(this.audioRecorder.getAudioSessionId());
            if (this.audioRecorder.getState() != 1) {
                throw new Exception("AudioRecord initialization failed");
            }
            this.audioRecorder.setRecordPositionUpdateListener(this.updateListener);
            this.audioRecorder.setPositionNotificationPeriod(this.mPeriodInFrames);
            this.filePath = null;
            this.state = RecorderBase.State.INITIALIZING;
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(WavAudioRecorder.class.getName(), e.getMessage());
            } else {
                Log.e(WavAudioRecorder.class.getName(), "Unknown error occured while initializing recording");
            }
            this.state = RecorderBase.State.ERROR;
        }
    }

    @Override // com.tm.androidcopysdk.recorder.RecorderBase, com.tm.androidcopysdk.recorder.IAudioRecorder
    public void prepare() {
        try {
            if (this.state == RecorderBase.State.INITIALIZING) {
                prepareRandomAccess();
            } else {
                Log.e(WavAudioRecorder.class.getName(), "prepare() method called on illegal state");
                release();
                this.state = RecorderBase.State.ERROR;
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(WavAudioRecorder.class.getName(), e.getMessage());
            } else {
                Log.e(WavAudioRecorder.class.getName(), "Unknown error occured in prepare()");
            }
            this.state = RecorderBase.State.ERROR;
        }
        this.buffer = new byte[((this.mPeriodInFrames * this.mBitsPersample) / 8) * this.nChannels];
    }

    @Override // com.tm.androidcopysdk.recorder.RecorderBase, com.tm.androidcopysdk.recorder.IAudioRecorder
    public void release() {
        if (this.state == RecorderBase.State.RECORDING) {
            stop();
        } else if (this.state == RecorderBase.State.READY) {
            try {
                this.randomAccessWriter.close();
            } catch (IOException e) {
                Log.e(WavAudioRecorder.class.getName(), "I/O exception occured while closing output file");
            }
            new File(this.filePath).delete();
        }
        if (this.audioRecorder != null) {
            this.audioRecorder.release();
        }
    }

    @Override // com.tm.androidcopysdk.recorder.RecorderBase, com.tm.androidcopysdk.recorder.IAudioRecorder
    public void reset() {
        try {
            if (this.state != RecorderBase.State.ERROR) {
                release();
                this.filePath = null;
                this.state = RecorderBase.State.READY;
            }
        } catch (Exception e) {
            Log.e(WavAudioRecorder.class.getName(), e.getMessage());
            this.state = RecorderBase.State.ERROR;
        }
    }

    @Override // com.tm.androidcopysdk.recorder.RecorderBase, com.tm.androidcopysdk.recorder.IAudioRecorder
    public void start() {
        if (this.state == RecorderBase.State.READY) {
            this.payloadSize = 0;
            this.audioRecorder.startRecording();
            this.audioRecorder.read(this.buffer, 0, this.buffer.length);
            this.state = RecorderBase.State.RECORDING;
            return;
        }
        Log.e(WavAudioRecorder.class.getName(), "start() called on illegal state");
        this.state = RecorderBase.State.ERROR;
    }

    @Override // com.tm.androidcopysdk.recorder.RecorderBase, com.tm.androidcopysdk.recorder.IAudioRecorder
    public void stop() {
        if (this.state == RecorderBase.State.RECORDING) {
            this.state = RecorderBase.State.STOPPED;
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
                Log.e(WavAudioRecorder.class.getName(), "I/O exception occured while closing output file");
                this.state = RecorderBase.State.ERROR;
            }
        } else {
            Log.e(WavAudioRecorder.class.getName(), "stop() called on illegal state");
            this.state = RecorderBase.State.ERROR;
        }
        super.stop();
    }
}
