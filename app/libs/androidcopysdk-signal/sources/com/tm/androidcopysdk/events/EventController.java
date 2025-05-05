package com.tm.androidcopysdk.events;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.tm.androidcopysdk.HandleResponseListener;
import com.tm.androidcopysdk.Packager;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.network.NetworkManager;
import com.tm.androidcopysdk.network.SMSEVENTMessageRecord;
import com.tm.logger.Log;
import com.tm.utils.Definitions;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import retrofit2.Response;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/EventController.class */
public class EventController implements HandleResponseListener {
    public static final String TAG = "EventController";
    private static EventController _instance;
    private Context context;

    private EventController(Context context) {
        this.context = context;
    }

    public static EventController getInstance(Context context) {
        if (_instance == null) {
            _instance = new EventController(context);
        }
        return _instance;
    }

    public void InsertEvent(EventAbsObj event) {
        String uuid = UUID.randomUUID().toString();
        ContentValues contentValues = new ContentValues();
        contentValues.put("date", Long.valueOf(event.getDate()));
        contentValues.put("description", event.getDescription());
        contentValues.put("status", Integer.valueOf(event.getStatus().ordinal()));
        contentValues.put("type", Integer.valueOf(event.getType().ordinal()));
        contentValues.put("orig_id", uuid);
        Uri path = this.context.getContentResolver().insert(MessageContentProvider.CONTENT_URI_EVENTS, contentValues);
        updateIdObj(event, path, uuid);
        Log.d(TAG, "InsertEvent:" + event.getType().name() + " " + path.getPath());
    }

    public void getAllEvent() {
        Cursor cursor = this.context.getContentResolver().query(MessageContentProvider.CONTENT_URI_EVENTS, null, null, null, null);
        Log.d(TAG, "getAllEvent: count:" + cursor.getCount());
        while (cursor != null && cursor.moveToNext()) {
            Log.d(TAG, "event: " + cursor.getString(cursor.getColumnIndex("description")));
        }
    }

    private List<EventAbsObj> getAllWaitingEvents() {
        String[] condition = {String.valueOf(MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered.ordinal()), String.valueOf(MessageContentProvider.MessageDeliveryStatus.EventDeliveredAsSms.ordinal()), String.valueOf(MessageContentProvider.MessageDeliveryStatus.EventDeliveredAsApi.ordinal())};
        Cursor cursor = this.context.getContentResolver().query(MessageContentProvider.CONTENT_URI_EVENTS, null, "status = ? OR status = ? OR status = ?", condition, null);
        List<EventAbsObj> list = new ArrayList<>();
        while (cursor != null) {
            try {
                try {
                    if (!cursor.moveToNext() || cursor.isAfterLast()) {
                        break;
                    }
                    EventAbsObj event = EventFactory.getEventByCursor(this.context, cursor);
                    list.add(event);
                } catch (Exception e) {
                    Log.e(TAG, "getAllWaitingEvents", e);
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return list;
    }

    private void updateIdObj(EventAbsObj eventAbsObj, Uri path, String uid) {
        if (path != null && path.getPath().lastIndexOf("/") > 0) {
            int id = Integer.valueOf(path.getLastPathSegment()).intValue();
            eventAbsObj.setId(id);
            eventAbsObj.setUid(uid);
        }
    }

    public int updateEvent(EventAbsObj event, MessageContentProvider.MessageDeliveryStatus deliveryStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", Integer.valueOf(deliveryStatus.ordinal()));
        String[] condition = {String.valueOf(event.getId())};
        int count = this.context.getContentResolver().update(MessageContentProvider.CONTENT_URI_EVENTS, contentValues, "_id = ? ", condition);
        Log.d(TAG, "updateEvent: event type:+" + event.getType().name() + " obj id = " + event.getId() + " count update =" + count + ", status = " + deliveryStatus.name());
        return count;
    }

    public void removeDeliveredEvents() {
        String where = "status=" + MessageContentProvider.MessageDeliveryStatus.Delivered.ordinal();
        int count = this.context.getContentResolver().delete(MessageContentProvider.CONTENT_URI_EVENTS, where, null);
        Log.d(TAG, "removeDeliveredEvents: count:" + count);
    }

    public void sendEvents() {
        List<EventAbsObj> list = getAllWaitingEvents();
        if (!list.isEmpty()) {
            requestEvent(list);
        }
    }

    private boolean requestEvent(List<EventAbsObj> list) {
        Log.d(TAG, "count: " + list.size());
        String baseurl = PreferenceManager.getDefaultSharedPreferences(this.context).getString("baseurl", Definitions.BaseUrl);
        String keeperUrl = PreferenceManager.getDefaultSharedPreferences(this.context).getString("keeperUrl", null);
        NetworkManager nm_sms = new NetworkManager(this.context.getApplicationContext(), TextUtils.isEmpty(keeperUrl) ? baseurl : keeperUrl);
        NetworkManager nm_api = new NetworkManager(this.context.getApplicationContext(), baseurl);
        String myNumber = PreferenceManager.getDefaultSharedPreferences(this.context).getString("phonenumber", "999999999");
        if (!myNumber.contains("+") && !myNumber.contains("@")) {
            myNumber = "+" + myNumber;
        }
        for (EventAbsObj event : list) {
            boolean success = true;
            MessageContentProvider.MessageDeliveryStatus eventDeliveryStatus = event.getStatus();
            MessageContentProvider.MessageDeliveryStatus eventDeliveryStatusLocal = eventDeliveryStatus;
            if (eventDeliveryStatusLocal == MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered) {
                SMSEVENTMessageRecord sr = Packager.packEventToSMS(this.context, event, myNumber);
                Log.d(TAG, "request for event as as sms: " + sr.getSubject() + "  " + sr.getTextContent());
                Log.d(TAG, "send message:" + sr.getTextContent());
                Response<Void> res = nm_sms.start(sr, this, this.context.getApplicationContext(), false);
                if (res != null && res.isSuccessful()) {
                    Log.d(TAG, "response.isSuccessful() = true");
                    if (eventDeliveryStatusLocal == MessageContentProvider.MessageDeliveryStatus.WaitingToBeDelivered) {
                        updateEvent(event, MessageContentProvider.MessageDeliveryStatus.EventDeliveredAsSms);
                        eventDeliveryStatusLocal = MessageContentProvider.MessageDeliveryStatus.EventDeliveredAsSms;
                    }
                    Log.d(TAG, "event " + event.getDescription() + " archived as SMS");
                } else {
                    success = false;
                }
            }
            if (success && eventDeliveryStatusLocal == MessageContentProvider.MessageDeliveryStatus.EventDeliveredAsSms) {
                EventsAPI eApi = Packager.packEventToAPI(this.context, event);
                Log.d(TAG, "request for event as new api: " + eApi.getEvents().toString());
                Response<Void> res2 = nm_api.start(eApi, this, this.context.getApplicationContext(), false);
                if (res2 != null && res2.isSuccessful()) {
                    boolean success2 = success & true;
                    Log.d(TAG, "response.isSuccessful() = true");
                    if (success2 && eventDeliveryStatusLocal == MessageContentProvider.MessageDeliveryStatus.EventDeliveredAsSms) {
                        updateEvent(event, MessageContentProvider.MessageDeliveryStatus.Delivered);
                    }
                    Log.d(TAG, "event " + event.getDescription() + " archived as new API");
                }
            }
        }
        return true;
    }

    @Override // com.tm.androidcopysdk.HandleResponseListener
    public void backoffRetryOnFailedMessages() {
    }

    @Override // com.tm.androidcopysdk.HandleResponseListener
    public void reportOnSuccess() {
    }

    @Override // com.tm.androidcopysdk.HandleResponseListener
    public void reportOnFailure() {
    }
}
