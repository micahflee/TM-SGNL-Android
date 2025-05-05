package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/ConvertedFailed.class */
public class ConvertedFailed extends EventAbsObj {
    public ConvertedFailed(Context context) {
        super(EventAbsObj.EventType.FailedToConvert2MP3, "Failed to convert file to MP3", MessageContentProvider.MessageDeliveryStatus.EventDeliveredAsSms, System.currentTimeMillis());
    }
}
