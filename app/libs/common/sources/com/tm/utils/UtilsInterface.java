package com.tm.utils;

import android.content.Context;
import java.util.Map;
/* loaded from: input.aar:classes.jar:com/tm/utils/UtilsInterface.class */
public interface UtilsInterface {
    Class getWorkerIntentClass();

    Class getKeepAliveIntentClass();

    void signOut(Context context);

    Class getMainActivityClass();

    String getBitRate();

    boolean isPlayVersion();

    boolean isDefaultRecordingApp(Context context);

    boolean isSocgenVersion();

    boolean supportSettings();

    boolean supportGetAppSettings();

    boolean isSelfSignup();

    Map<String, String> getMdmSettings(Context context);

    String encodeHexString(byte[] bArr);

    void updateMdmPhoneNumberIfNeed(Context context, String str, String str2);

    void updateMdmFirstNameLastNameIfNeed(Context context, String str, String str2);

    void initPushFcmListener(Context context);

    void startBattery(Context context);
}
