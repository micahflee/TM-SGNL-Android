package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/IdleEvent.class */
public class IdleEvent extends EventAbsObj {
    public IdleEvent(Context context) {
        super(EventAbsObj.EventType.IdleEvent, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.EVENTS_HEARTBEAT : context.getString(R.string.events_heartbeat), MessageContentProvider.MessageDeliveryStatus.EventDeliveredAsSms, System.currentTimeMillis());
    }
}
