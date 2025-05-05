package com.tm.androidcopysdk.network;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/ReportLogs.class */
public class ReportLogs extends BodyBase {
    private final String mAuthenticationToken;
    private final String mSubject;
    private final String mUserPhone;
    private final String mUserName;
    private final String mFreeText;
    private final String mFirstName;
    private final String mLastName;
    private final String mEmail;

    public ReportLogs(String authenticationToken, String aUserPhone, String aSubject, String aUserName, String aFreeText, String mFirstName, String mLastName, String mEmail) {
        this.mAuthenticationToken = authenticationToken;
        this.mSubject = aSubject;
        this.mUserPhone = aUserPhone;
        this.mUserName = aUserName;
        this.mFreeText = aFreeText;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mEmail = mEmail;
    }

    public String getAuthenticationToken() {
        return this.mAuthenticationToken;
    }

    public String getUserPhone() {
        return this.mUserPhone;
    }

    public String getUserName() {
        return this.mUserName;
    }

    public String getFreeText() {
        return this.mFreeText;
    }

    public String getSubject() {
        return this.mSubject;
    }

    public String getmFirstName() {
        return this.mFirstName;
    }

    public String getmLastName() {
        return this.mLastName;
    }

    public String getmEmail() {
        return this.mEmail;
    }
}
