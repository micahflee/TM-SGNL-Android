package com.tm.androidcopysdk.recorder.igalCode.mic;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/igalCode/mic/IgalCallRecorderFromMic.class */
public class IgalCallRecorderFromMic {
    public static native int load();

    public static native int startRec(int i);

    public static native int stopRec();

    static {
        IaglLibLoader.loadLib();
    }
}
