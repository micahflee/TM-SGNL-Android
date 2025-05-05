package com.tm.authenticatorsdk.socgen.RetrieveCredentials;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/RetrieveCredentials/RetrieveCredentialsReq.class */
public class RetrieveCredentialsReq {
    String value;
    int searchBy;
    int retrieveMode;
    String oneTimePIN;

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSearchBy() {
        return this.searchBy;
    }

    public void setSearchBy(int searchBy) {
        this.searchBy = searchBy;
    }

    public int getRetrieveMode() {
        return this.retrieveMode;
    }

    public void setRetrieveMode(int retrieveMode) {
        this.retrieveMode = retrieveMode;
    }

    public String getOneTimePIN() {
        return this.oneTimePIN;
    }

    public void setOneTimePIN(String oneTimePIN) {
        this.oneTimePIN = oneTimePIN;
    }
}
