package com.tm.androidcopysdk.recorder;

import com.tm.androidcopysdk.recorder.RecorderBase;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/IAudioRecorder.class */
public interface IAudioRecorder {
    void prepare();

    void start() throws Exception;

    void stop();

    void setFile(String str);

    void reset();

    void release();

    void renameFile();

    RecorderBase.State getState();
}
