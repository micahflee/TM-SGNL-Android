package com.tm.androidcopysdk.recorder.mic;

import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/mic/CallRecorderFromMicHelper.class */
public class CallRecorderFromMicHelper {

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/mic/CallRecorderFromMicHelper$Singleton.class */
    static final class Singleton {
        static final CallRecorderFromMicHelper INSTANCE = new CallRecorderFromMicHelper();

        private Singleton() {
        }
    }

    public static CallRecorderFromMicHelper getInstance() {
        return Singleton.INSTANCE;
    }

    public void initialize() {
        CallRecorderFromMic.load();
    }

    public void startRec(int i) {
        CallRecorderFromMic.startRec(i);
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopRec(boolean isOut) {
        try {
            Thread.sleep(isOut ? 200L : 200L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("AndroidAudioRecord", "start stopRecFix");
        CallRecorderFromMic.stopRec();
    }

    public void startRec7() {
        CallRecorderFromMic.startRec7();
        try {
            Thread.sleep(100L);
            Log.d("AndroidAudioRecord", "end sleep 150");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopRec7() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CallRecorderFromMic.stopRec();
    }
}
