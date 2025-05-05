package com.tm.androidcopysdk.events;

import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/UpdateUserEvent.class */
public class UpdateUserEvent extends EventAbsObj {
    public UpdateUserEvent() {
        super(EventAbsObj.EventType.UpdateUserEvent, "", MessageContentProvider.MessageDeliveryStatus.EventDeliveredAsSms, System.currentTimeMillis());
    }
}
