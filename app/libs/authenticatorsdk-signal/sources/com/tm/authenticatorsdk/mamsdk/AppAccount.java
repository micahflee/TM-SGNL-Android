package com.tm.authenticatorsdk.mamsdk;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import java.util.Map;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/AppAccount.class */
public class AppAccount {
    private final String mUPN;
    private final String mAADID;
    private final String mTenantID;
    private final String mAuthority;
    private final Map<String, ?> mClaims;
    private static final String UPN_KEY = "mamsampleappaccount.upn";
    private static final String AADID_KEY = "mamsampleappaccount.aadid";
    private static final String TENANTID_KEY = "mamsampleappaccount.tenantid";
    private static final String AUTHORITY_KEY = "mamsampleappaccount.authority";
    private static final String CLAIMS = "mamsampleappaccount.claims";

    public AppAccount(@NonNull String upn, @NonNull String aadid, @NonNull String tenantid, @NonNull String authority, @NonNull Map<String, ?> claims) {
        this.mUPN = upn;
        this.mAADID = aadid;
        this.mTenantID = tenantid;
        this.mAuthority = authority;
        this.mClaims = claims;
    }

    public String getUPN() {
        return this.mUPN;
    }

    public String getAADID() {
        return this.mAADID;
    }

    public String getTenantID() {
        return this.mTenantID;
    }

    public String getAuthority() {
        return this.mAuthority;
    }

    public void saveToSettings(SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(UPN_KEY, this.mUPN);
        editor.putString(AADID_KEY, this.mAADID);
        editor.putString(TENANTID_KEY, this.mTenantID);
        editor.putString(AUTHORITY_KEY, this.mAuthority);
        editor.putString(AUTHORITY_KEY, this.mAuthority);
        editor.apply();
    }

    public static AppAccount readFromSettings(SharedPreferences sharedPref) {
        String aadid;
        String tenantid;
        String authority;
        Map<String, ?> claims;
        String upn = sharedPref.getString(UPN_KEY, null);
        if (upn == null || (aadid = sharedPref.getString(AADID_KEY, null)) == null || (tenantid = sharedPref.getString(TENANTID_KEY, null)) == null || (authority = sharedPref.getString(AUTHORITY_KEY, null)) == null || (claims = sharedPref.getAll()) == null) {
            return null;
        }
        return new AppAccount(upn, aadid, tenantid, authority, claims);
    }

    public static void clearFromSettings(SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(UPN_KEY);
        editor.remove(AADID_KEY);
        editor.remove(TENANTID_KEY);
        editor.remove(AUTHORITY_KEY);
        editor.remove(CLAIMS);
        editor.apply();
    }
}
