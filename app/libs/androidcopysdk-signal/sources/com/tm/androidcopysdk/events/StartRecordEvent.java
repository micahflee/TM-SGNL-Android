package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/StartRecordEvent.class */
public class StartRecordEvent extends EventAbsObj {
    public StartRecordEvent(Context context) {
        super(EventAbsObj.EventType.StartRecord, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.EVENTS_RECORDING_START : context.getString(R.string.events_recording_start), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }
}
