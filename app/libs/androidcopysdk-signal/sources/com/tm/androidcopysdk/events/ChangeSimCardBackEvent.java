package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/ChangeSimCardBackEvent.class */
public class ChangeSimCardBackEvent extends EventAbsObj {
    public ChangeSimCardBackEvent(Context context) {
        super(EventAbsObj.EventType.ChangedSimCardBackEvent, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.EVENTS_SIM_CARD_BACK : context.getString(R.string.events_sim_card_back), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }
}
