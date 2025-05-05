package com.tm.androidcopysdk.recorder.mic;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/mic/LibLoader.class */
public class LibLoader {
    private static boolean lLib = false;

    public static synchronized void loadLib() {
        synchronized (LibLoader.class) {
            if (!lLib) {
                System.loadLibrary("MyRecorderLib");
                lLib = true;
            }
        }
    }
}
