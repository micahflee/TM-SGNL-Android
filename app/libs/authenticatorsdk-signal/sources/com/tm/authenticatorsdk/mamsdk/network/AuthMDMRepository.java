package com.tm.authenticatorsdk.mamsdk.network;

import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: AuthMDMRepository.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��$\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018��2\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u001e\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u00062\u0006\u0010\b\u001a\u00020\tH\u0086@¢\u0006\u0002\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n��¨\u0006\u000b"}, d2 = {"Lcom/tm/authenticatorsdk/mamsdk/network/AuthMDMRepository;", "", "mdmAuthenticationAPI", "Lcom/tm/authenticatorsdk/mamsdk/network/MDMAuthenticationAPI;", "(Lcom/tm/authenticatorsdk/mamsdk/network/MDMAuthenticationAPI;)V", "ensureIP", "Lkotlinx/coroutines/flow/Flow;", "Lcom/tm/authenticatorsdk/mamsdk/network/ResponseBody;", "requestBody", "Lcom/tm/authenticatorsdk/mamsdk/network/RequestBody;", "(Lcom/tm/authenticatorsdk/mamsdk/network/RequestBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/network/AuthMDMRepository.class */
public final class AuthMDMRepository {
    @NotNull
    private final MDMAuthenticationAPI mdmAuthenticationAPI;

    public AuthMDMRepository(@NotNull MDMAuthenticationAPI mdmAuthenticationAPI) {
        Intrinsics.checkNotNullParameter(mdmAuthenticationAPI, "mdmAuthenticationAPI");
        this.mdmAuthenticationAPI = mdmAuthenticationAPI;
    }

    @Nullable
    public final Object ensureIP(@NotNull RequestBody requestBody, @NotNull Continuation<? super Flow<ResponseBody>> continuation) {
        return FlowKt.flow(new AuthMDMRepository$ensureIP$2(this, requestBody, null));
    }
}
