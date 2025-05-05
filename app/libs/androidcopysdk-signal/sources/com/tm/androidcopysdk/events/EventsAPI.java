package com.tm.androidcopysdk.events;

import com.tm.androidcopysdk.network.AuthenticationDetails;
import com.tm.androidcopysdk.network.BodyBase;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/EventsAPI.class */
public class EventsAPI extends BodyBase {
    AuthenticationDetails authenticationDetails;
    EventsAPIobj events;

    public EventsAPIobj getEvents() {
        return this.events;
    }

    public void setEvents(EventsAPIobj events) {
        this.events = events;
    }

    public AuthenticationDetails getAuthenticationDetails() {
        return this.authenticationDetails;
    }

    public void setAuthenticationDetails(AuthenticationDetails authenticationDetails) {
        this.authenticationDetails = authenticationDetails;
    }
}
