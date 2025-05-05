package com.tm.androidcopysdk.recorder;

import com.tm.androidcopysdk.recorder.mic.LibLoader;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/AudioRecordNative.class */
public class AudioRecordNative {
    public static native boolean nativeInit();

    public static native boolean nativeDestroy();

    public static native int nativeStart();

    public static native boolean nativeCreate(int i, int i2, int i3, int i4, int i5);

    public static native int nativeStop();

    public static native byte[] nativeRead(byte[] bArr, int i);

    static {
        LibLoader.loadLib();
        nativeInit();
    }

    public AudioRecordNative(int audiosource, int samplerate, int audioformat, int channels, int size) {
        nativeCreate(audiosource, samplerate, audioformat, channels, size);
    }

    public int start() {
        return nativeStart();
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
}
