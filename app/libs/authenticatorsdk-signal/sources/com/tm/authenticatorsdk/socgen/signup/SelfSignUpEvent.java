package com.tm.authenticatorsdk.socgen.signup;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/signup/SelfSignUpEvent.class */
public class SelfSignUpEvent {
    private String pin;
    private boolean isFromPush;

    public SelfSignUpEvent(String pin, boolean isFromPush) {
        this.pin = pin;
        this.isFromPush = isFromPush;
    }

    public String getPin() {
        return this.pin;
    }

    public boolean isFromPush() {
        return this.isFromPush;
    }
}
