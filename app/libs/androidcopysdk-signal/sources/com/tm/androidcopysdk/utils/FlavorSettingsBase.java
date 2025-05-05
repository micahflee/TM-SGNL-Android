package com.tm.androidcopysdk.utils;

import android.content.Context;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.HeaderObj;
import com.tm.androidcopysdk.database.DBKeepAliveQueryHelper;
import java.util.ArrayList;
import java.util.List;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/FlavorSettingsBase.class */
public abstract class FlavorSettingsBase {
    public boolean supportNativeMsg() {
        return false;
    }

    public boolean supportSimEvent() {
        return false;
    }

    public String getPostMessageUrl() {
        return "basicsecured/api/rest/archive/telemessageIncomingMessageV2";
    }

    public String getPostMessageUrlWithoutAuthentication() {
        return "api/rest/archive/telemessageIncomingMessage";
    }

    public String getPostSettingsUrl() {
        return "api/rest/archive/getappsettings";
    }

    public boolean supportSettings() {
        return true;
    }

    public boolean oldVersion() {
        return false;
    }

    public boolean isAppServerNeedAuthorization() {
        return true;
    }

    public boolean isAppSupportSSLFactory() {
        return false;
    }

    public boolean isNeedToUpdateActivityAfterUserCredentialsUpdated() {
        return false;
    }

    public String getMSISDN(String myNumber) {
        if (!myNumber.contains("+") && !myNumber.contains("@")) {
            myNumber = "+" + myNumber;
        }
        return myNumber;
    }

    public boolean supportSQLCipher() {
        return true;
    }

    public List<HeaderObj> getHeaders(Context context, String messageId) {
        return new ArrayList();
    }

    public String getLogEmail() {
        return "1a68bb3b.telemessage.com@emea.teams.ms,Client_App_Logs@telemessage.com";
    }

    public String getLogsBody() {
        return "AndroidArchiver Android Logs " + CommonUtils.getCurrentDate();
    }

    public String getAppIdForKeepAlive() {
        return "600701";
    }

    public String getFlavorName() {
        return "";
    }

    public boolean isNeedToCreateUniqueMessagesId() {
        return false;
    }

    public boolean isKeepAliveV3() {
        return false;
    }

    public boolean isAppSupportReArchive() {
        return false;
    }

    public boolean isMobileNumberNeedToConvertToGlobal() {
        return true;
    }

    public boolean isSupportSuspendUser() {
        return false;
    }

    public DBKeepAliveQueryHelper.MessageType getMultimediaMessageContext() {
        return DBKeepAliveQueryHelper.MessageType.APP_MESSAGE;
    }

    public DBKeepAliveQueryHelper.MessageType getMessageContext() {
        return DBKeepAliveQueryHelper.MessageType.APP_MESSAGE;
    }

    public boolean supportChangeNumber() {
        return false;
    }

    public boolean usingCustomSqlCipherVersion() {
        return false;
    }
}
