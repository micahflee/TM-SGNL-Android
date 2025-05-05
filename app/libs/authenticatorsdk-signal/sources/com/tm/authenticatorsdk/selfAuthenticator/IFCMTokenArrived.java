package com.tm.authenticatorsdk.selfAuthenticator;

import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import com.tm.utils.ApplicationInterface;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
/* compiled from: IFCMTokenArrived.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\u001c\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n��\bf\u0018��2\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H&¨\u0006\b"}, d2 = {"Lcom/tm/authenticatorsdk/selfAuthenticator/IFCMTokenArrived;", "", "onFCMTokenArrived", "", "applicationInterface", "Lcom/tm/utils/ApplicationInterface;", "token", "", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/IFCMTokenArrived.class */
public interface IFCMTokenArrived {
    void onFCMTokenArrived(@NotNull ApplicationInterface applicationInterface, @NotNull String str);
}
