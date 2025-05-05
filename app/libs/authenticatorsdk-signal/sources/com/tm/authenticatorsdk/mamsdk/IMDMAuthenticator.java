package com.tm.authenticatorsdk.mamsdk;

import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
/* compiled from: IMDMAuthenticator.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\u0018\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\bf\u0018��2\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0003H&¨\u0006\u0007"}, d2 = {"Lcom/tm/authenticatorsdk/mamsdk/IMDMAuthenticator;", "", "failureMDMAuth", "", "reason", "", "successMDMAuth", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/IMDMAuthenticator.class */
public interface IMDMAuthenticator {
    void failureMDMAuth(@NotNull String str);

    void successMDMAuth();
}
