package com.tm.androidcopysdk.events;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.tm.androidcopysdk.R;
import com.tm.androidcopysdk.database.DBMessagesHelperSignal;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.events.EventAbsObj;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/EventFactory.class */
public class EventFactory {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.tm.androidcopysdk.events.EventFactory$1  reason: invalid class name */
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/EventFactory$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType = new int[EventAbsObj.EventType.values().length];

        static {
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.IdleEvent.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.StartRecord.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.StopRecord.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.InstallEvent.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.UninstallEvent.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.UpdateVersion.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.ChangedSimCardEvent.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.ChangedSimCardBackEvent.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.LowStorageEvent.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.MissingPermissionsEvent.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.FailedToConvert2MP3.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.UpdateUserEvent.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.FailedRecordingsEvent.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.AutoRecordingIsOff.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.RecordingTerminatedDuringTheCallEvent.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.NoSupportForCallRecordingEvent.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.ChangedDualSimCardEvent.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.SimRemovedWithoutLogout.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.BatterySDKEvent.ordinal()] = 19;
            } catch (NoSuchFieldError e19) {
            }
            try {
                $SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[EventAbsObj.EventType.WrongFolderEvent.ordinal()] = 20;
            } catch (NoSuchFieldError e20) {
            }
        }
    }

    public static EventAbsObj createEvent(Context context, EventAbsObj.EventType eventType) {
        EventAbsObj ret;
        switch (AnonymousClass1.$SwitchMap$com$tm$androidcopysdk$events$EventAbsObj$EventType[eventType.ordinal()]) {
            case 1:
                ret = new IdleEvent(context);
                break;
            case 2:
                ret = new StartRecordEvent(context);
                break;
            case 3:
                ret = new StopRecordEvent(context);
                break;
            case 4:
                ret = new InstallEvent(context);
                break;
            case 5:
                ret = new UninstallEvent();
                break;
            case MyAudioFormat.AUDIO_SOURCE_VOICE_RECOGNITION /* 6 */:
                ret = new UpdateVersionEvent(context);
                break;
            case MyAudioFormat.AUDIO_SOURCE_VOICE_COMMUNICATION /* 7 */:
                ret = new ChangedSimCardEvent(context);
                break;
            case 8:
                ret = new ChangeSimCardBackEvent(context);
                break;
            case 9:
                ret = new LowStorageEvent(context);
                break;
            case 10:
                ret = new MissingPermissionsEvent(context);
                break;
            case PeriodicEventChecker.LOW_STORAGE_EVENT_CHECKER /* 11 */:
                ret = new ConvertedFailed(context);
                break;
            case 12:
                ret = new UpdateUserEvent();
                break;
            case DBMessagesHelperSignal.DATABASE_VERSION /* 13 */:
                ret = new FailedRecordingsEvent();
                break;
            case 14:
                ret = new AutoRecordingIsOff();
                String name = PrefManager.getStringPref(context, "pref_my_name", "");
                if (TextUtils.isEmpty(name)) {
                    name = TMCredentialsStore.getInstance(context).userName(context);
                }
                String des = String.format(context.getString(R.string.auto_recording_is_off), name, PreferenceManager.getDefaultSharedPreferences(context).getString("phonenumber", "999999999"), Build.VERSION.RELEASE);
                ret.setDescription(des);
                break;
            case 15:
                ret = new RecordingTerminatedDuringTheCallEvent();
                break;
            case 16:
                ret = new NoSupportForCallRecordingEvent();
                break;
            case 17:
                ret = new DualSimWasRemoved(context);
                break;
            case 18:
                ret = new SimRemovedWithoutLogout(context);
                break;
            case 19:
                ret = new BatterySDKEvent(context);
                break;
            case 20:
                ret = new MissingFolderEvent(context);
                break;
            default:
                ret = null;
                break;
        }
        return ret;
    }

    public static EventAbsObj getEventByCursor(Context context, Cursor cursor) {
        EventAbsObj.EventType type = EventAbsObj.EventType.values()[cursor.getInt(cursor.getColumnIndex("type"))];
        EventAbsObj event = createEvent(context, type);
        event.setStatus(MessageContentProvider.MessageDeliveryStatus.values()[cursor.getInt(cursor.getColumnIndex("status"))]);
        event.setId(cursor.getInt(cursor.getColumnIndex("_id")));
        event.setDescription(cursor.getString(cursor.getColumnIndex("description")));
        event.setUid(cursor.getString(cursor.getColumnIndex("orig_id")));
        return event;
    }
}
