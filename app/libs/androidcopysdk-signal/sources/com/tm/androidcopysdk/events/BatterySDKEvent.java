package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/BatterySDKEvent.class */
public class BatterySDKEvent extends EventAbsObj {
    public BatterySDKEvent(Context context) {
        super(EventAbsObj.EventType.BatterySDKEvent, context.getString(R.string.events_battery_sdk), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }
}
