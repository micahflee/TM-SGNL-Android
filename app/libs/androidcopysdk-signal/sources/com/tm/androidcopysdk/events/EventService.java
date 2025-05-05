package com.tm.androidcopysdk.events;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.DefinitionsSDKKt;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.logger.Log;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/EventService.class */
public class EventService extends JobIntentService {
    public static final String ACTION_EVENT = "com.tm.androidcopysdk.event.action.send_event";

    /* JADX WARN: Multi-variable type inference failed */
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent != null && CommonUtils.appIsWorking(this)) {
            String action = intent.getAction();
            if (ACTION_EVENT.equals(action)) {
                EventController.getInstance(this).sendEvents();
            }
        }
    }

    public static void startEventService(Context context) {
        Log.d("EventService", "startEventService");
        if (FlavorSettings.getInstance().supportNativeMsg()) {
            Intent service = new Intent(context, EventService.class);
            service.setAction(ACTION_EVENT);
            enqueueWork(context, service);
            return;
        }
        Log.d("EventService", "this flavor not support events !  ! ! ! !");
    }

    private static void enqueueWork(Context eventService, Intent compIntent) {
        enqueueWork(eventService, EventService.class, DefinitionsSDKKt.EVENT_SERVICE_JOB_ID, compIntent);
    }
}
