package com.tm.authenticatorsdk.selfAuthenticator;

import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: SelfAuthenticator.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 3, xi = 48, d1 = {"��\n\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010��\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"})
@DebugMetadata(f = "SelfAuthenticator.kt", l = {82, 91}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "com.tm.authenticatorsdk.selfAuthenticator.SelfAuthenticator$onFCMTokenArrived$1")
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/SelfAuthenticator$onFCMTokenArrived$1.class */
final class SelfAuthenticator$onFCMTokenArrived$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    int label;
    final /* synthetic */ String $dateFormatted;
    final /* synthetic */ String $token;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public SelfAuthenticator$onFCMTokenArrived$1(String $dateFormatted, String $token, Continuation<? super SelfAuthenticator$onFCMTokenArrived$1> continuation) {
        super(2, continuation);
        this.$dateFormatted = $dateFormatted;
        this.$token = $token;
    }

    @NotNull
    public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> continuation) {
        return new SelfAuthenticator$onFCMTokenArrived$1(this.$dateFormatted, this.$token, continuation);
    }

    @Nullable
    public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super Unit> continuation) {
        return create(p1, continuation).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x00ab A[Catch: Exception -> 0x017f, TRY_LEAVE, TryCatch #0 {Exception -> 0x017f, blocks: (B:5:0x0028, B:11:0x0060, B:13:0x00ab, B:19:0x00e0, B:21:0x012e, B:22:0x0139, B:23:0x0159, B:10:0x005a, B:18:0x00da), top: B:30:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x012e A[Catch: Exception -> 0x017f, TryCatch #0 {Exception -> 0x017f, blocks: (B:5:0x0028, B:11:0x0060, B:13:0x00ab, B:19:0x00e0, B:21:0x012e, B:22:0x0139, B:23:0x0159, B:10:0x005a, B:18:0x00da), top: B:30:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0139 A[Catch: Exception -> 0x017f, TryCatch #0 {Exception -> 0x017f, blocks: (B:5:0x0028, B:11:0x0060, B:13:0x00ab, B:19:0x00e0, B:21:0x012e, B:22:0x0139, B:23:0x0159, B:10:0x005a, B:18:0x00da), top: B:30:0x0009 }] */
    /* JADX WARN: Removed duplicated region for block: B:23:0x0159 A[Catch: Exception -> 0x017f, TRY_LEAVE, TryCatch #0 {Exception -> 0x017f, blocks: (B:5:0x0028, B:11:0x0060, B:13:0x00ab, B:19:0x00e0, B:21:0x012e, B:22:0x0139, B:23:0x0159, B:10:0x005a, B:18:0x00da), top: B:30:0x0009 }] */
    @org.jetbrains.annotations.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object invokeSuspend(@org.jetbrains.annotations.NotNull java.lang.Object r8) {
        /*
            Method dump skipped, instructions count: 453
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.authenticatorsdk.selfAuthenticator.SelfAuthenticator$onFCMTokenArrived$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
