package com.tm.authenticatorsdk.mamsdk;

import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
/* compiled from: MDMAuthenticator.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 2, xi = 48, d1 = {"��\u0012\n��\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\n\"\u000e\u0010��\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n��\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n��\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n��\"\u0019\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005¢\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0006\u0010\u0007\"\u000e\u0010\t\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n��\"\u000e\u0010\n\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n��\"\u000e\u0010\u000b\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n��\"\u000e\u0010\f\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n��\"\u000e\u0010\r\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n��\"\u000e\u0010\u000e\u001a\u00020\u0001X\u0086T¢\u0006\u0002\n��¨\u0006\u000f"}, d2 = {"APP_ID", "", "DEVICE_TYPE", "MANAGER_ID", "MSAL_SCOPES", "", "getMSAL_SCOPES", "()[Ljava/lang/String;", "[Ljava/lang/String;", "NAME", "SECRET_KEY", "SIGNAL", "TAG", "WHATSAPP_ARCHIVER", "err_auth", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/MDMAuthenticatorKt.class */
public final class MDMAuthenticatorKt {
    @NotNull
    public static final String TAG = "MDMAuthenticator";
    @NotNull
    public static final String err_auth = "Authentication error: ";
    @NotNull
    private static final String[] MSAL_SCOPES = {"https://graph.microsoft.com/User.Read"};
    @NotNull
    public static final String MANAGER_ID = "managerID";
    @NotNull
    public static final String SECRET_KEY = "secretKey";
    @NotNull
    public static final String WHATSAPP_ARCHIVER = "32";
    @NotNull
    public static final String SIGNAL = "64";
    @NotNull
    public static final String NAME = "name";
    @NotNull
    public static final String DEVICE_TYPE = "GCM";
    @NotNull
    public static final String APP_ID = "600911";

    @NotNull
    public static final String[] getMSAL_SCOPES() {
        return MSAL_SCOPES;
    }
}
