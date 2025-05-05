package com.tm.androidcopysdk.events;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/events/AlertEvent.class */
public class AlertEvent {
    String title;
    String body;
    int severity = 1;
    long alertTime;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getSeverity() {
        return this.severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public long getAlertTime() {
        return this.alertTime;
    }

    public void setAlertTime(long alertTime) {
        this.alertTime = alertTime;
    }
}
