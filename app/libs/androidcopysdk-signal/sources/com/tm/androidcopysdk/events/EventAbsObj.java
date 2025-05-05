package com.tm.androidcopysdk.events;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.logger.Log;
import java.util.Locale;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/EventAbsObj.class */
public abstract class EventAbsObj {
    protected EventType type;
    protected String description;
    protected MessageContentProvider.MessageDeliveryStatus status;
    private long date;
    private int id = 0;
    private String uid;

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/EventAbsObj$EventType.class */
    public enum EventType {
        IdleEvent,
        InstallEvent,
        UninstallEvent,
        ArchiveEvent,
        StartRecord,
        StopRecord,
        UpdateVersion,
        ChangedSimCardEvent,
        ChangedSimCardBackEvent,
        LowStorageEvent,
        MissingPermissionsEvent,
        FailedToConvert2MP3,
        UpdateUserEvent,
        FailedRecordingsEvent,
        AutoRecordingIsOff,
        RecordingTerminatedDuringTheCallEvent,
        NoSupportForCallRecordingEvent,
        ChangedDualSimCardEvent,
        SimRemovedWithoutLogout,
        BatterySDKEvent,
        WrongFolderEvent
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public EventAbsObj(EventType type, String description, MessageContentProvider.MessageDeliveryStatus status, long date) {
        this.type = type;
        this.description = description;
        this.status = status;
        this.date = date;
    }

    public EventType getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }

    public MessageContentProvider.MessageDeliveryStatus getStatus() {
        return this.status;
    }

    public long getDate() {
        return this.date;
    }

    public void setStatus(MessageContentProvider.MessageDeliveryStatus status) {
        this.status = status;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStringEvent(Context context) {
        return "";
    }

    public void updateVersionNumber(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getStringEvent(context));
        stringBuilder.append(" \r\n");
        stringBuilder.append(Build.MANUFACTURER);
        stringBuilder.append(" \r\n");
        stringBuilder.append(Build.MODEL);
        stringBuilder.append(" \r\n");
        stringBuilder.append(Build.VERSION.RELEASE);
        stringBuilder.append(" \r\n");
        stringBuilder.append(Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName());
        stringBuilder.append(" \r\n");
        String regionSim = "";
        String regionNet = "";
        TelephonyManager teleMgr = (TelephonyManager) context.getSystemService("phone");
        if (teleMgr != null) {
            try {
                regionSim = teleMgr.getSimCountryIso();
                regionNet = teleMgr.getNetworkCountryIso();
            } catch (Exception e) {
                Log.d("eventAbs", "no permission", e);
            }
        }
        if (!TextUtils.isEmpty(regionSim)) {
            stringBuilder.append(regionSim.toUpperCase());
        } else if (!TextUtils.isEmpty(regionNet)) {
            stringBuilder.append(regionNet.toUpperCase());
        } else {
            stringBuilder.append("missing country");
        }
        stringBuilder.append(" \r\n");
        stringBuilder.append(Locale.getDefault().getDisplayLanguage());
        this.description = stringBuilder.toString();
    }
}
