package com.tm.androidcopysdk.recorder.mic;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/mic/CallRecorderFromMic.class */
public class CallRecorderFromMic {
    public static native int load();

    public static native int startRec(int i);

    public static native int startRec7();

    public static native int stopRec();

    static {
        LibLoader.loadLib();
    }
}
