package com.tm.authenticatorsdk.selfAuthenticator;

import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
/* compiled from: IOnCredentialsArrived.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\u0018\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0004\bf\u0018��2\u00020\u0001J(\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u0005H&¨\u0006\t"}, d2 = {"Lcom/tm/authenticatorsdk/selfAuthenticator/IOnCredentialsArrived;", "", "onCredentialsArrived", "", "userName", "", "password", "environmentProduction", "environmentKeeper", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/IOnCredentialsArrived.class */
public interface IOnCredentialsArrived {
    void onCredentialsArrived(@NotNull String str, @NotNull String str2, @NotNull String str3, @NotNull String str4);
}
