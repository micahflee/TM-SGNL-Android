package com.tm.androidcopysdk.events;

import android.content.Context;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/MissingFolderEvent.class */
public class MissingFolderEvent extends EventAbsObj {
    public MissingFolderEvent(Context context) {
        super(EventAbsObj.EventType.WrongFolderEvent, context.getString(R.string.missing_folder_event), MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered, System.currentTimeMillis());
    }
}
