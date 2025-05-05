package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/MissingPermissionsEvent.class */
public class MissingPermissionsEvent extends EventAbsObj {
    public MissingPermissionsEvent(Context context) {
        super(EventAbsObj.EventType.MissingPermissionsEvent, "missingPermissions", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }
}
