package com.tm.androidcopysdk.network.appSettings;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/appSettings/UpdateEvent.class */
public class UpdateEvent {
    public EVENTS_TYPE type;

    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/appSettings/UpdateEvent$EVENTS_TYPE.class */
    public enum EVENTS_TYPE {
        authProcess,
        suspension,
        activated
    }

    public UpdateEvent(EVENTS_TYPE type) {
        this.type = type;
    }
}
