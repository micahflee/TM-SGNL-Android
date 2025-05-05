package com.tm.authenticatorsdk.mamsdk.network;

import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.flow.FlowCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: AuthMDMRepository.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 3, xi = 48, d1 = {"��\u000e\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010��\u001a\u00020\u0001*\n\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/flow/FlowCollector;", "Lcom/tm/authenticatorsdk/mamsdk/network/ResponseBody;"})
@DebugMetadata(f = "AuthMDMRepository.kt", l = {14, 16, 20}, i = {BuildConfig.DEBUG}, s = {"L$0"}, n = {"$this$flow"}, m = "invokeSuspend", c = "com.tm.authenticatorsdk.mamsdk.network.AuthMDMRepository$ensureIP$2")
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/network/AuthMDMRepository$ensureIP$2.class */
public final class AuthMDMRepository$ensureIP$2 extends SuspendLambda implements Function2<FlowCollector<? super ResponseBody>, Continuation<? super Unit>, Object> {
    int label;
    private /* synthetic */ Object L$0;
    final /* synthetic */ AuthMDMRepository this$0;
    final /* synthetic */ RequestBody $requestBody;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public AuthMDMRepository$ensureIP$2(AuthMDMRepository $receiver, RequestBody $requestBody, Continuation<? super AuthMDMRepository$ensureIP$2> continuation) {
        super(2, continuation);
        this.this$0 = $receiver;
        this.$requestBody = $requestBody;
    }

    @NotNull
    public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> continuation) {
        Continuation<Unit> authMDMRepository$ensureIP$2 = new AuthMDMRepository$ensureIP$2(this.this$0, this.$requestBody, continuation);
        authMDMRepository$ensureIP$2.L$0 = value;
        return authMDMRepository$ensureIP$2;
    }

    @Nullable
    public final Object invoke(@NotNull FlowCollector<? super ResponseBody> flowCollector, @Nullable Continuation<? super Unit> continuation) {
        return create(flowCollector, continuation).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0073  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x009d  */
    @org.jetbrains.annotations.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object invokeSuspend(@org.jetbrains.annotations.NotNull java.lang.Object r7) {
        /*
            Method dump skipped, instructions count: 256
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.authenticatorsdk.mamsdk.network.AuthMDMRepository$ensureIP$2.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
