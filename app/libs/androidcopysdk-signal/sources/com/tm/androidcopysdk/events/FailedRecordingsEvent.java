package com.tm.androidcopysdk.events;

import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
import kotlin.Metadata;
/* compiled from: FailedRecordingsEvent.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018��2\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002¨\u0006\u0003"}, d2 = {"Lcom/tm/androidcopysdk/events/FailedRecordingsEvent;", "Lcom/tm/androidcopysdk/events/EventAbsObj;", "()V", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/FailedRecordingsEvent.class */
public final class FailedRecordingsEvent extends EventAbsObj {
    public FailedRecordingsEvent() {
        super(EventAbsObj.EventType.FailedRecordingsEvent, StringsFlavorsUtils.FAILED_RECORDING_EVENT_TEXT, MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }
}
