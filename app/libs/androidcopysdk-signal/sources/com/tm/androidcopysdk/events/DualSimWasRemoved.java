package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.utils.StringsFlavorsUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/DualSimWasRemoved.class */
public class DualSimWasRemoved extends EventAbsObj {
    public DualSimWasRemoved(Context context) {
        super(EventAbsObj.EventType.ChangedSimCardBackEvent, StringsFlavorsUtils.isWPAApp() ? StringsFlavorsUtils.EVENTS_DUAL_SIM_CARD_BACK : context.getString(R.string.events_dual_sim_card_back), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }
}
