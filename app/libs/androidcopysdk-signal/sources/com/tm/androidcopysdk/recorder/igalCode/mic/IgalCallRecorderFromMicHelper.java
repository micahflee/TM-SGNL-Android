package com.tm.androidcopysdk.recorder.igalCode.mic;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/igalCode/mic/IgalCallRecorderFromMicHelper.class */
public class IgalCallRecorderFromMicHelper {

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/igalCode/mic/IgalCallRecorderFromMicHelper$Singleton.class */
    static final class Singleton {
        static final IgalCallRecorderFromMicHelper INSTANCE = new IgalCallRecorderFromMicHelper();

        private Singleton() {
        }
    }

    public static IgalCallRecorderFromMicHelper getInstance() {
        return Singleton.INSTANCE;
    }

    public void initialize() {
        IgalCallRecorderFromMic.load();
    }

    public void startRec(int i) {
        IgalCallRecorderFromMic.startRec(i);
        try {
            Thread.sleep(300L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopRec() {
        try {
            Thread.sleep(120L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        IgalCallRecorderFromMic.stopRec();
    }
}
