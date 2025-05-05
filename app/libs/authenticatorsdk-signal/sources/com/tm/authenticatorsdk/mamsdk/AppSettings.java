package com.tm.authenticatorsdk.mamsdk;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/AppSettings.class */
public class AppSettings {
    private static final String SETTINGS_PATH = "com.microsoft.intune.samples.taskr.appsettings";

    private AppSettings() {
    }

    public static void saveAccount(@NonNull Context appContext, @NonNull AppAccount account) {
        SharedPreferences prefs = getPrefs(appContext);
        account.saveToSettings(prefs);
    }

    public static AppAccount getAccount(@NonNull Context appContext) {
        SharedPreferences prefs = getPrefs(appContext);
        return AppAccount.readFromSettings(prefs);
    }

    public static void clearAccount(Context appContext) {
        SharedPreferences prefs = getPrefs(appContext);
        AppAccount.clearFromSettings(prefs);
    }

    private static SharedPreferences getPrefs(Context appContext) {
        return appContext.getSharedPreferences(SETTINGS_PATH, 0);
    }
}
