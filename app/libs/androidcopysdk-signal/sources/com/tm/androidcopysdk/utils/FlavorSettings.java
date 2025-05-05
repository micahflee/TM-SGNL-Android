package com.tm.androidcopysdk.utils;

import com.tm.androidcopysdk.CommonUtils;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/FlavorSettings.class */
public class FlavorSettings extends FlavorSettingsBase {
    private static FlavorSettings _insatnce;

    private FlavorSettings() {
    }

    public static FlavorSettings getInstance() {
        if (_insatnce == null) {
            _insatnce = new FlavorSettings();
        }
        return _insatnce;
    }

    public String getProviderName() {
        return "com.tm.androidcopysdk.MessageContentProvider.signal";
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public String getLogEmail() {
        return "4e742a11.telemessage.com@emea.teams.ms,Client_App_Logs@telemessage.com";
    }

    public String getAccountType() {
        return "androidcopy.tm.com.signal";
    }

    public String getAccountName() {
        return "TM AndroidCopy signal";
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public String getPostSettingsUrl() {
        return "api/rest/archive/getappsettings/signal";
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public String getPostMessageUrl() {
        return "basicsecured/api/rest/archive/telemessageIncomingMessage/signalV2";
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public String getPostMessageUrlWithoutAuthentication() {
        return "api/rest/archive/telemessageIncomingMessage/signal";
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public boolean isAppServerNeedAuthorization() {
        return true;
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public String getMSISDN(String myNumber) {
        if (myNumber.startsWith("+")) {
            myNumber = myNumber.replace("+", "");
        }
        return myNumber;
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public boolean supportSQLCipher() {
        return false;
    }

    public boolean spportSettings() {
        return false;
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public boolean supportNativeMsg() {
        return false;
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public String getAppIdForKeepAlive() {
        return "600911";
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public boolean isSupportSuspendUser() {
        return false;
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public String getLogsBody() {
        return "Signal Android Logs " + CommonUtils.getCurrentDate();
    }

    @Override // com.tm.androidcopysdk.utils.FlavorSettingsBase
    public boolean usingCustomSqlCipherVersion() {
        return true;
    }
}
