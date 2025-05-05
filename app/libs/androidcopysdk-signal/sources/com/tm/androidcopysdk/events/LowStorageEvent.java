package com.tm.androidcopysdk.events;

import android.content.Context;
import android.os.Environment;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/LowStorageEvent.class */
public class LowStorageEvent extends EventAbsObj {
    public LowStorageEvent(Context context) {
        super(EventAbsObj.EventType.LowStorageEvent, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.EVENTS_LOW_STORAGE : context.getString(R.string.events_low_storage), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
        long freeSpaceInMegabytes = Environment.getDataDirectory().getUsableSpace() / 1048576;
        setDescription(getDescription() + " (" + freeSpaceInMegabytes + "MB)");
    }
}
