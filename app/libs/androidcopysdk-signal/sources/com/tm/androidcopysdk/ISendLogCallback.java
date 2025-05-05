package com.tm.androidcopysdk;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
/* compiled from: ISendLogCallback.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u0002\n\u0002\b\u0002\bf\u0018��2\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0003H&¨\u0006\u0005"}, d2 = {"Lcom/tm/androidcopysdk/ISendLogCallback;", "", "sendLogFailure", "", "sendLogSucceed", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/ISendLogCallback.class */
public interface ISendLogCallback {
    void sendLogSucceed();

    void sendLogFailure();
}
