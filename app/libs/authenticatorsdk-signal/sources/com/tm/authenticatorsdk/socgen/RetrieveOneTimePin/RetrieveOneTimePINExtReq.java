package com.tm.authenticatorsdk.socgen.RetrieveOneTimePin;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/RetrieveOneTimePin/RetrieveOneTimePINExtReq.class */
public class RetrieveOneTimePINExtReq {
    String value;
    Integer type;
    Integer retrieveMode;
    String hashOTP;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return this.type.intValue();
    }

    public void setType(int type) {
        this.type = Integer.valueOf(type);
    }

    public int getRetrieveMode() {
        return this.retrieveMode.intValue();
    }

    public void setRetrieveMode(int retrieveMode) {
        this.retrieveMode = Integer.valueOf(retrieveMode);
    }

    public String getHashOTP() {
        return this.hashOTP;
    }

    public void setHashOTP(String hashOTP) {
        this.hashOTP = hashOTP;
    }
}
