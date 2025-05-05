package com.tm.androidcopysdk.events;

import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
/* compiled from: RecordingTerminatedDuringTheCallEvent.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018��2\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002¨\u0006\u0003"}, d2 = {"Lcom/tm/androidcopysdk/events/RecordingTerminatedDuringTheCallEvent;", "Lcom/tm/androidcopysdk/events/EventAbsObj;", "()V", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/RecordingTerminatedDuringTheCallEvent.class */
public final class RecordingTerminatedDuringTheCallEvent extends EventAbsObj {
    public RecordingTerminatedDuringTheCallEvent() {
        super(EventAbsObj.EventType.RecordingTerminatedDuringTheCallEvent, "Android 10 – Recording terminated during the call", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }
}
