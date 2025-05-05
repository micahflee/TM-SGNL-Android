package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/StopRecordEvent.class */
public class StopRecordEvent extends EventAbsObj {
    public StopRecordEvent(Context context) {
        super(EventAbsObj.EventType.StopRecord, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.EVENTS_RECORDING_STOP : context.getString(R.string.events_recording_stop), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }
}
