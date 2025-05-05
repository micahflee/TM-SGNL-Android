package com.tm.authenticatorsdk.socgen.signup;

import com.google.gson.annotations.Expose;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/signup/SignupRequest.class */
public class SignupRequest {
    @Expose(serialize = true)
    UserFields[] fields;

    public UserFields[] getFields() {
        return this.fields;
    }

    public void setFields(UserFields[] fields) {
        this.fields = fields;
    }
}
