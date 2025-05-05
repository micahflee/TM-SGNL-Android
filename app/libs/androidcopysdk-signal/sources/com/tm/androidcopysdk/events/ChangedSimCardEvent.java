package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/ChangedSimCardEvent.class */
public class ChangedSimCardEvent extends EventAbsObj {
    public ChangedSimCardEvent(Context context) {
        super(EventAbsObj.EventType.ChangedSimCardEvent, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.EVENTS_SIM_CARD_CHANGE : context.getString(R.string.events_sim_card_change), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }
}
