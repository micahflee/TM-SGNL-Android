package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/InstallEvent.class */
public class InstallEvent extends EventAbsObj {
    public InstallEvent(Context context) {
        super(EventAbsObj.EventType.InstallEvent, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.EVENTS_INSTALLATION : context.getString(R.string.events_installation), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }

    @Override // com.tm.androidcopysdk.events.EventAbsObj
    public String getStringEvent(Context context) {
        return String.format(StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.EVENTS_INSTALLATION : context.getString(R.string.events_installation), CommonUtils.versionNumber(context));
    }
}
