package com.tm.authenticatorsdk.mamsdk;

import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import com.tm.authenticatorsdk.mamsdk.network.AuthMDMRepository;
import com.tm.authenticatorsdk.mamsdk.network.ResponseBody;
import com.tm.logger.Log;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.FlowCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: MDMAuthenticator.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 3, xi = 48, d1 = {"��\n\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010��\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"})
@DebugMetadata(f = "MDMAuthenticator.kt", l = {313, 320}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "com.tm.authenticatorsdk.mamsdk.MDMAuthenticator$manageNetwork$1")
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/MDMAuthenticator$manageNetwork$1.class */
public final class MDMAuthenticator$manageNetwork$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    int label;
    final /* synthetic */ AuthMDMRepository $authMDMRepository;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public MDMAuthenticator$manageNetwork$1(AuthMDMRepository $authMDMRepository, Continuation<? super MDMAuthenticator$manageNetwork$1> continuation) {
        super(2, continuation);
        this.$authMDMRepository = $authMDMRepository;
    }

    @NotNull
    public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> continuation) {
        return new MDMAuthenticator$manageNetwork$1(this.$authMDMRepository, continuation);
    }

    @Nullable
    public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super Unit> continuation) {
        return create(p1, continuation).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0076  */
    @org.jetbrains.annotations.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.Object invokeSuspend(@org.jetbrains.annotations.NotNull java.lang.Object r7) {
        /*
            r6 = this;
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            r9 = r0
            r0 = r6
            int r0 = r0.label
            switch(r0) {
                case 0: goto L24;
                case 1: goto L43;
                case 2: goto L78;
                default: goto Lb4;
            }
        L24:
            r0 = r7
            kotlin.ResultKt.throwOnFailure(r0)
            r0 = r6
            com.tm.authenticatorsdk.mamsdk.network.AuthMDMRepository r0 = r0.$authMDMRepository     // Catch: java.lang.Exception -> L82
            com.tm.authenticatorsdk.mamsdk.network.RequestBody r1 = com.tm.authenticatorsdk.mamsdk.MDMAuthenticator.access$getRequestBody$p()     // Catch: java.lang.Exception -> L82
            r2 = r6
            kotlin.coroutines.Continuation r2 = (kotlin.coroutines.Continuation) r2     // Catch: java.lang.Exception -> L82
            r3 = r6
            r4 = 1
            r3.label = r4     // Catch: java.lang.Exception -> L82
            java.lang.Object r0 = r0.ensureIP(r1, r2)     // Catch: java.lang.Exception -> L82
            r1 = r0
            r2 = r9
            if (r1 != r2) goto L49
            r1 = r9
            return r1
        L43:
            r0 = r7
            kotlin.ResultKt.throwOnFailure(r0)     // Catch: java.lang.Exception -> L82
            r0 = r7
        L49:
            kotlinx.coroutines.flow.Flow r0 = (kotlinx.coroutines.flow.Flow) r0     // Catch: java.lang.Exception -> L82
            com.tm.authenticatorsdk.mamsdk.MDMAuthenticator$manageNetwork$1$1 r1 = new com.tm.authenticatorsdk.mamsdk.MDMAuthenticator$manageNetwork$1$1     // Catch: java.lang.Exception -> L82
            r2 = r1
            r3 = 0
            r2.<init>(r3)     // Catch: java.lang.Exception -> L82
            kotlin.jvm.functions.Function3 r1 = (kotlin.jvm.functions.Function3) r1     // Catch: java.lang.Exception -> L82
            kotlinx.coroutines.flow.Flow r0 = kotlinx.coroutines.flow.FlowKt.catch(r0, r1)     // Catch: java.lang.Exception -> L82
            com.tm.authenticatorsdk.mamsdk.MDMAuthenticator$manageNetwork$1$2 r1 = new com.tm.authenticatorsdk.mamsdk.MDMAuthenticator$manageNetwork$1$2     // Catch: java.lang.Exception -> L82
            r2 = r1
            r3 = 0
            r2.<init>(r3)     // Catch: java.lang.Exception -> L82
            kotlin.jvm.functions.Function2 r1 = (kotlin.jvm.functions.Function2) r1     // Catch: java.lang.Exception -> L82
            r2 = r6
            kotlin.coroutines.Continuation r2 = (kotlin.coroutines.Continuation) r2     // Catch: java.lang.Exception -> L82
            r3 = r6
            r4 = 2
            r3.label = r4     // Catch: java.lang.Exception -> L82
            java.lang.Object r0 = kotlinx.coroutines.flow.FlowKt.collectLatest(r0, r1, r2)     // Catch: java.lang.Exception -> L82
            r1 = r0
            r2 = r9
            if (r1 != r2) goto L7e
            r1 = r9
            return r1
        L78:
            r0 = r7
            kotlin.ResultKt.throwOnFailure(r0)     // Catch: java.lang.Exception -> L82
            r0 = r7
        L7e:
            goto Lb0
        L82:
            r8 = move-exception
            java.lang.String r0 = "MDMAuthenticator"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r2 = r1
            r2.<init>()
            java.lang.String r2 = "exception is: "
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r8
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            com.tm.logger.Log.d(r0, r1)
            r0 = r8
            r0.printStackTrace()
            com.tm.authenticatorsdk.mamsdk.MDMAuthenticator r0 = com.tm.authenticatorsdk.mamsdk.MDMAuthenticator.INSTANCE
            com.tm.authenticatorsdk.mamsdk.IMDMAuthenticator r0 = r0.getMIMDMAuthenticator()
            r1 = r0
            kotlin.jvm.internal.Intrinsics.checkNotNull(r1)
            java.lang.String r1 = "Exception!! server telemessage-intune error"
            r0.failureMDMAuth(r1)
        Lb0:
            kotlin.Unit r0 = kotlin.Unit.INSTANCE
            return r0
        Lb4:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            r1 = r0
            java.lang.String r2 = "call to 'resume' before 'invoke' with coroutine"
            r1.<init>(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.authenticatorsdk.mamsdk.MDMAuthenticator$manageNetwork$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: MDMAuthenticator.kt */
    @Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 3, xi = 48, d1 = {"��\u0014\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u0003\u0010��\u001a\u00020\u0001*\n\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u00022\u0006\u0010\u0004\u001a\u00020\u0005H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/flow/FlowCollector;", "Lcom/tm/authenticatorsdk/mamsdk/network/ResponseBody;", "e", ""})
    @DebugMetadata(f = "MDMAuthenticator.kt", l = {}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "com.tm.authenticatorsdk.mamsdk.MDMAuthenticator$manageNetwork$1$1")
    /* renamed from: com.tm.authenticatorsdk.mamsdk.MDMAuthenticator$manageNetwork$1$1  reason: invalid class name */
    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/MDMAuthenticator$manageNetwork$1$1.class */
    public static final class AnonymousClass1 extends SuspendLambda implements Function3<FlowCollector<? super ResponseBody>, Throwable, Continuation<? super Unit>, Object> {
        int label;
        /* synthetic */ Object L$0;

        AnonymousClass1(Continuation<? super AnonymousClass1> continuation) {
            super(3, continuation);
        }

        @Nullable
        public final Object invoke(@NotNull FlowCollector<? super ResponseBody> flowCollector, @NotNull Throwable p2, @Nullable Continuation<? super Unit> continuation) {
            AnonymousClass1 anonymousClass1 = new AnonymousClass1(continuation);
            anonymousClass1.L$0 = p2;
            return anonymousClass1.invokeSuspend(Unit.INSTANCE);
        }

        @Nullable
        public final Object invokeSuspend(@NotNull Object obj) {
            IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (this.label) {
                case BuildConfig.DEBUG /* 0 */:
                    ResultKt.throwOnFailure(obj);
                    Throwable e = (Throwable) this.L$0;
                    Log.d(MDMAuthenticatorKt.TAG, "exception is: " + e);
                    IMDMAuthenticator mIMDMAuthenticator = MDMAuthenticator.INSTANCE.getMIMDMAuthenticator();
                    Intrinsics.checkNotNull(mIMDMAuthenticator);
                    mIMDMAuthenticator.failureMDMAuth("coroutine catch: !! server telemessage-intune error");
                    return Unit.INSTANCE;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* compiled from: MDMAuthenticator.kt */
    @Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 3, xi = 48, d1 = {"��\f\n��\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\u0010��\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u008a@"}, d2 = {"<anonymous>", "", "responseBody", "Lcom/tm/authenticatorsdk/mamsdk/network/ResponseBody;"})
    @DebugMetadata(f = "MDMAuthenticator.kt", l = {330}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "com.tm.authenticatorsdk.mamsdk.MDMAuthenticator$manageNetwork$1$2")
    /* renamed from: com.tm.authenticatorsdk.mamsdk.MDMAuthenticator$manageNetwork$1$2  reason: invalid class name */
    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/MDMAuthenticator$manageNetwork$1$2.class */
    public static final class AnonymousClass2 extends SuspendLambda implements Function2<ResponseBody, Continuation<? super Unit>, Object> {
        int label;
        /* synthetic */ Object L$0;

        AnonymousClass2(Continuation<? super AnonymousClass2> continuation) {
            super(2, continuation);
        }

        @NotNull
        public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> continuation) {
            Continuation<Unit> anonymousClass2 = new AnonymousClass2(continuation);
            anonymousClass2.L$0 = value;
            return anonymousClass2;
        }

        @Nullable
        public final Object invoke(@Nullable ResponseBody p1, @Nullable Continuation<? super Unit> continuation) {
            return create(p1, continuation).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Removed duplicated region for block: B:24:0x00f1  */
        /* JADX WARN: Removed duplicated region for block: B:25:0x00fc  */
        @org.jetbrains.annotations.Nullable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final java.lang.Object invokeSuspend(@org.jetbrains.annotations.NotNull java.lang.Object r7) {
            /*
                Method dump skipped, instructions count: 403
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.tm.authenticatorsdk.mamsdk.MDMAuthenticator$manageNetwork$1.AnonymousClass2.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }
}
