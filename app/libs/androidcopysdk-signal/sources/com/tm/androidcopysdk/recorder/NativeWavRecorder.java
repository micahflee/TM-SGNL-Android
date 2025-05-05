package com.tm.androidcopysdk.recorder;

import android.content.Context;
import android.media.AudioRecord;
import com.tm.androidcopysdk.recorder.RecorderBase;
import com.tm.androidcopysdk.recorder.mic.RecorderHelper;
import com.tm.logger.Log;
import java.io.File;
import java.io.IOException;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/NativeWavRecorder.class */
public class NativeWavRecorder extends RecorderBase {
    protected AudioRecordNative audioRecorder;
    Thread audioRecordingThread;
    int bufferSizeInBytes;

    public static NativeWavRecorder getInstanse(Context context, int audioSource) {
        NativeWavRecorder ret = null;
        try {
            ret = new NativeWavRecorder(context, audioSource, 8000, 1, 16);
        } catch (Exception e) {
            e.printStackTrace();
        }
        RecorderHelper.getInstance().startFixCallRecorder7(context);
        return ret;
    }

    public NativeWavRecorder(Context context, int audioSource, int sampleRate, int channelConfig, int audioEncoding) throws Exception {
        this.bufferSizeInBytes = 0;
        Log.d("NativeWavRecorder", "NativeWavRecorder ");
        this.bufferSizeInBytes = AudioRecord.getMinBufferSize(8000, 16, 2);
        if (this.bufferSizeInBytes == -1 || this.bufferSizeInBytes == -2) {
            throw new Exception("Failed to get the minimum buffer size." + RecorderBase.CodeError.ERROR_BUFFER_SIZE);
        }
        try {
            this.audioRecorder = new AudioRecordNative(audioSource, 8000, 1, 16, this.bufferSizeInBytes);
            this.state = RecorderBase.State.INITIALIZING;
        } catch (Exception e) {
            this.state = RecorderBase.State.ERROR;
        }
        this.buffer = new byte[this.bufferSizeInBytes];
        Log.d("NativeWavRecorder", "bufferSizeInBytes =  " + this.bufferSizeInBytes);
    }

    @Override // com.tm.androidcopysdk.recorder.RecorderBase, com.tm.androidcopysdk.recorder.IAudioRecorder
    public void prepare() {
        Log.d(RecorderBase.TAG, "prepare()");
        try {
            if (this.state == RecorderBase.State.INITIALIZING) {
                prepareRandomAccess();
            } else {
                Log.e(NativeWavRecorder.class.getName(), "prepare() method called on illegal state");
                release();
                this.state = RecorderBase.State.ERROR;
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                Log.e(NativeWavRecorder.class.getName(), e.getMessage());
            } else {
                Log.e(NativeWavRecorder.class.getName(), "Unknown error occured in prepare()");
            }
            this.state = RecorderBase.State.ERROR;
        }
        Log.d(RecorderBase.TAG, "state =" + this.state.name());
    }

    @Override // com.tm.androidcopysdk.recorder.RecorderBase, com.tm.androidcopysdk.recorder.IAudioRecorder
    public void start() throws Exception {
        Log.d(RecorderBase.TAG, "start() ," + this.state.name());
        try {
            this.state = RecorderBase.State.RECORDING;
            this.audioRecorder.start();
        } catch (Exception e) {
            this.state = RecorderBase.State.STOPPED;
            Log.e("NativeWavRecorder", "failed to start", e);
        }
        Log.d(RecorderBase.TAG, "start() ," + this.state.name());
        if (this.state == RecorderBase.State.RECORDING) {
            this.audioRecordingThread = new Thread(new Runnable() { // from class: com.tm.androidcopysdk.recorder.NativeWavRecorder.1
                @Override // java.lang.Runnable
                public void run() {
                    NativeWavRecorder.this.handleThread();
                }
            });
            this.audioRecordingThread.start();
        }
    }

    @Override // com.tm.androidcopysdk.recorder.RecorderBase, com.tm.androidcopysdk.recorder.IAudioRecorder
    public void stop() {
        if (this.audioRecorder == null) {
            this.state = RecorderBase.State.ERROR;
            Log.e(WavAudioRecorder.class.getName(), "stop() called on illegal state");
            return;
        }
        this.state = RecorderBase.State.STOPPED;
        this.audioRecorder.stop();
        this.audioRecorder = null;
        this.audioRecordingThread = null;
        try {
            this.randomAccessWriter.seek(4L);
            this.randomAccessWriter.writeInt(Integer.reverseBytes(36 + this.payloadSize));
            this.randomAccessWriter.seek(40L);
            this.randomAccessWriter.writeInt(Integer.reverseBytes(this.payloadSize));
            this.randomAccessWriter.close();
        } catch (IOException e) {
            Log.e(WavAudioRecorder.class.getName(), "I/O exception occured while closing output file");
            this.state = RecorderBase.State.ERROR;
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
            this.audioRecorder = null;
        }
    }

    public void handleThread() {
        if (RecorderBase.State.STOPPED == this.state) {
            Log.d(RecorderBase.TAG, "recorder stopped");
            return;
        }
        while (this.state == RecorderBase.State.RECORDING) {
            this.buffer = this.audioRecorder.read(this.buffer, this.buffer.length > 0 ? this.buffer.length : this.bufferSizeInBytes);
            try {
                this.randomAccessWriter.write(this.buffer);
                this.payloadSize += this.buffer.length;
                if (this.buffer.length > 0 && this.payloadSize % (this.buffer.length * 100) == 0) {
                    Log.d(getClass().getName(), this.state + ":" + this.payloadSize);
                }
            } catch (IOException e) {
                Log.e(NativeWavRecorder.class.getName(), "Error occured in updateListener, recording is aborted", e);
                e.printStackTrace();
            }
        }
    }
}
