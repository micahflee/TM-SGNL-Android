package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/UpdateVersionEvent.class */
public class UpdateVersionEvent extends EventAbsObj {
    public UpdateVersionEvent(Context context) {
        super(EventAbsObj.EventType.UpdateVersion, StringsFlavorsUtils.isWPAApp() ? "Android Archiver App Version Updated (%s)" : context.getString(R.string.events_update), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }

    @Override // com.tm.androidcopysdk.events.EventAbsObj
    public String getStringEvent(Context context) {
        return String.format(StringsFlavorsUtils.isWPAApp() ? "Android Archiver App Version Updated (%s)" : context.getString(R.string.events_installation_upgrade), CommonUtils.versionNumber(context));
    }
}
