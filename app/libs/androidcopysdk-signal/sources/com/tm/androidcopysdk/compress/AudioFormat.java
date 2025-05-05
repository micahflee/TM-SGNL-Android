package com.tm.androidcopysdk.compress;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/compress/AudioFormat.class */
public enum AudioFormat {
    AAC,
    MP3,
    M4A,
    WMA,
    WAV,
    FLAC;

    public String getFormat() {
        return name().toLowerCase();
    }
}
