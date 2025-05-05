package com.tm.androidcopysdk.recorder.igalCode.mic;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/igalCode/mic/IaglLibLoader.class */
public class IaglLibLoader {
    private static boolean lLib = false;

    public static synchronized void loadLib() {
        synchronized (IaglLibLoader.class) {
            if (!lLib) {
                System.loadLibrary("IgalRecorderLib");
                lLib = true;
            }
        }
    }
}
