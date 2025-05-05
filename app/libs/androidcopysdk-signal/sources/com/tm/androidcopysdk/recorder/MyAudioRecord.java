package com.tm.androidcopysdk.recorder;

import android.media.AudioRecord;
import android.os.Build;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/MyAudioRecord.class */
public class MyAudioRecord {
    public static native boolean nativeInit(int i);

    public static native boolean nativeDestroy();

    public static native int nativeStart();

    public static native int nativeCreate(int i, int i2, int i3, int i4, int i5);

    public static native int nativeStop();

    public static native int nativeSet(AudioRecord audioRecord);

    public static native int nativeOff();

    public static native byte[] nativeRead(byte[] bArr, int i);

    public static native int nativeGetBufferSize(int i, int i2, int i3);

    public static native int nativeGetMinFrame(int i, int i2, int i3);

    public static native int nativeGetParams();

    public MyAudioRecord(int audiosource, int samplerate, int audioformat, int channels, int size) {
        nativeCreate(audiosource, samplerate, audioformat, channels, size);
    }

    public static int getFrameCount(int samplerate, int audioformat, int channels) {
        nativeInit(Build.VERSION.SDK_INT);
        int result = nativeGetMinFrame(samplerate, audioformat, channels);
        return result;
    }

    public int start() {
        return nativeStart();
    }

    public static void set(AudioRecord record) {
        nativeInit(Build.VERSION.SDK_INT);
        nativeSet(record);
    }

    public static void set2(AudioRecord record) {
        nativeSet(record);
    }

    public static void off() {
        nativeOff();
    }

    public int stop() {
        return nativeStop();
    }

    public byte[] read(byte[] buffer, int buffersize) {
        return nativeRead(buffer, buffersize);
    }

    public boolean destroy() {
        return nativeDestroy();
    }

    static {
        try {
            System.loadLibrary("MyRecorderLib");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e.toString());
        }
    }
}
