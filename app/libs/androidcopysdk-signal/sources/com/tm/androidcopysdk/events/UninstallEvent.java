package com.tm.androidcopysdk.events;

import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/UninstallEvent.class */
public class UninstallEvent extends EventAbsObj {
    public UninstallEvent() {
        super(EventAbsObj.EventType.UninstallEvent, "uninstall", MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }
}
