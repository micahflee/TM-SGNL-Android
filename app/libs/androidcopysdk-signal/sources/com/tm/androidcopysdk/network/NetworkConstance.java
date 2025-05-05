package com.tm.androidcopysdk.network;

import com.tm.androidcopysdk.utils.FlavorSettings;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/NetworkConstance.class */
public class NetworkConstance {
    public static final String LOGS_AUTHENTICATION_BEARER = "Bearer";
    public static final String SEND_LOGS_REQUEST_FILE_TYPE = "application/zip";
    public static final String PREFERENCE_LOGS_AUTHENTICATION_KEY = "logAuthenticationKey";
    public static final String LOGS_SENT_SUCCESS = "email sent to";
    public static final String LOGS_SENT_AUTHENTICATION_ERROR = "jwt_auth_no_auth";
    public static final String SEND_LOGS_REQUEST_FILE_NAME = "log.zip";
    public static final String SEND_LOGS_REQUEST_FILE_KEY_NAME = "upload_file";
    public static final String SEND_LOGS_MAILS = FlavorSettings.getInstance().getLogEmail();
}
