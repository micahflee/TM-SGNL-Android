package com.tm.androidcopysdk.recorder;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/MyAudioFormat.class */
public class MyAudioFormat {
    public static final int AUDIO_SOURCE_DEFAULT = 0;
    public static final int AUDIO_SOURCE_MIC = 1;
    public static final int AUDIO_SOURCE_VOICE_UPLINK = 2;
    public static final int AUDIO_SOURCE_VOICE_DOWNLINK = 3;
    public static final int AUDIO_SOURCE_VOICE_CALL = 4;
    public static final int AUDIO_SOURCE_CAMCORDER = 5;
    public static final int AUDIO_SOURCE_VOICE_RECOGNITION = 6;
    public static final int AUDIO_SOURCE_VOICE_COMMUNICATION = 7;
    public static final int AUDIO_FORMAT_INVALID = -1;
    public static final int AUDIO_FORMAT_DEFAULT = 0;
    public static final int AUDIO_FORMAT_PCM = 0;
    public static final int AUDIO_FORMAT_MP3 = 16777216;
    public static final int AUDIO_FORMAT_AMR_NB = 33554432;
    public static final int AUDIO_FORMAT_AMR_WB = 50331648;
    public static final int AUDIO_FORMAT_AAC = 67108864;
    public static final int AUDIO_FORMAT_HE_AAC_V1 = 83886080;
    public static final int AUDIO_FORMAT_HE_AAC_V2 = 100663296;
    public static final int AUDIO_FORMAT_VORBIS = 117440512;
    public static final int AUDIO_FORMAT_MAIN_MASK = -16777216;
    public static final int AUDIO_FORMAT_SUB_MASK = 16777215;
    public static final int AUDIO_FORMAT_PCM_16_BIT = 1;
    public static final int AUDIO_FORMAT_PCM_8_BIT = 2;
    public static final int AUDIO_FORMAT_PCM_32_BIT = 3;
    public static final int AUDIO_FORMAT_PCM_8_24_BIT = 4;
    public static final int AUDIO_INPUT_FLAG_NONE = 0;
    public static final int AUDIO_INPUT_FLAG_FAST = 1;
    public static final int SYNC_EVENT_SAME = -1;
    public static final int SYNC_EVENT_NONE = 0;
    public static final int AUDIO_CHANNEL_OUT_FRONT_LEFT = 1;
    public static final int AUDIO_CHANNEL_OUT_FRONT_RIGHT = 2;
    public static final int AUDIO_CHANNEL_OUT_FRONT_CENTER = 4;
    public static final int AUDIO_CHANNEL_OUT_LOW_FREQUENCY = 8;
    public static final int AUDIO_CHANNEL_OUT_BACK_LEFT = 16;
    public static final int AUDIO_CHANNEL_OUT_BACK_RIGHT = 32;
    public static final int AUDIO_CHANNEL_OUT_FRONT_LEFT_OF_CENTER = 64;
    public static final int AUDIO_CHANNEL_OUT_FRONT_RIGHT_OF_CENTER = 128;
    public static final int AUDIO_CHANNEL_OUT_BACK_CENTER = 256;
    public static final int AUDIO_CHANNEL_OUT_SIDE_LEFT = 512;
    public static final int AUDIO_CHANNEL_OUT_SIDE_RIGHT = 1024;
    public static final int AUDIO_CHANNEL_OUT_TOP_CENTER = 2048;
    public static final int AUDIO_CHANNEL_OUT_TOP_FRONT_LEFT = 4096;
    public static final int AUDIO_CHANNEL_OUT_TOP_FRONT_CENTER = 8192;
    public static final int AUDIO_CHANNEL_OUT_TOP_FRONT_RIGHT = 16384;
    public static final int AUDIO_CHANNEL_OUT_TOP_BACK_LEFT = 32768;
    public static final int AUDIO_CHANNEL_OUT_TOP_BACK_CENTER = 65536;
    public static final int AUDIO_CHANNEL_OUT_TOP_BACK_RIGHT = 131072;
    public static final int AUDIO_CHANNEL_OUT_MONO = 1;
    public static final int AUDIO_CHANNEL_OUT_STEREO = 3;
    public static final int AUDIO_CHANNEL_OUT_QUAD = 51;
    public static final int AUDIO_CHANNEL_OUT_SURROUND = 263;
    public static final int AUDIO_CHANNEL_OUT_5POINT1 = 63;
    public static final int AUDIO_CHANNEL_OUT_7POINT1 = 1599;
    public static final int AUDIO_CHANNEL_OUT_ALL = 262143;
    public static final int AUDIO_CHANNEL_IN_LEFT = 4;
    public static final int AUDIO_CHANNEL_IN_RIGHT = 8;
    public static final int AUDIO_CHANNEL_IN_FRONT = 16;
    public static final int AUDIO_CHANNEL_IN_BACK = 32;
    public static final int AUDIO_CHANNEL_IN_LEFT_PROCESSED = 64;
    public static final int AUDIO_CHANNEL_IN_RIGHT_PROCESSED = 128;
    public static final int AUDIO_CHANNEL_IN_FRONT_PROCESSED = 256;
    public static final int AUDIO_CHANNEL_IN_BACK_PROCESSED = 512;
    public static final int AUDIO_CHANNEL_IN_PRESSURE = 1024;
    public static final int AUDIO_CHANNEL_IN_X_AXIS = 2048;
    public static final int AUDIO_CHANNEL_IN_Y_AXIS = 4096;
    public static final int AUDIO_CHANNEL_IN_Z_AXIS = 8192;
    public static final int AUDIO_CHANNEL_IN_VOICE_UPLINK = 16384;
    public static final int AUDIO_CHANNEL_IN_VOICE_DNLINK = 32768;
    public static final int AUDIO_CHANNEL_IN_MONO = 16;
    public static final int AUDIO_CHANNEL_IN_STEREO = 12;
    public static final int AUDIO_CHANNEL_IN_FRONT_BACK = 48;
    public static final int AUDIO_CHANNEL_IN_ALL = 65532;

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/recorder/MyAudioFormat$transfer_type.class */
    public enum transfer_type {
        TRANSFER_DEFAULT,
        TRANSFER_CALLBACK,
        TRANSFER_OBTAIN,
        TRANSFER_SYNC
    }
}
