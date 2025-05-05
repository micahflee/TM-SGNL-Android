package com.tm.androidcopysdk.utils;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.channels.ProduceKt;
import kotlinx.coroutines.channels.ProducerScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ContentExtensions.kt */
@Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u000e\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\u0010��\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/channels/ProducerScope;", ""})
@DebugMetadata(f = "ContentExtensions.kt", l = {31}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "com.tm.androidcopysdk.utils.ContentExtensions$register$1")
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/ContentExtensions$register$1.class */
final class ContentExtensions$register$1 extends SuspendLambda implements Function2<ProducerScope<? super Boolean>, Continuation<? super Unit>, Object> {
    int label;
    private /* synthetic */ Object L$0;
    final /* synthetic */ ContentResolver $this_register;
    final /* synthetic */ Uri $uri;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ContentExtensions$register$1(ContentResolver $receiver, Uri $uri, Continuation<? super ContentExtensions$register$1> continuation) {
        super(2, continuation);
        this.$this_register = $receiver;
        this.$uri = $uri;
    }

    @NotNull
    public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> continuation) {
        Continuation<Unit> contentExtensions$register$1 = new ContentExtensions$register$1(this.$this_register, this.$uri, continuation);
        contentExtensions$register$1.L$0 = value;
        return contentExtensions$register$1;
    }

    @Nullable
    public final Object invoke(@NotNull ProducerScope<? super Boolean> producerScope, @Nullable Continuation<? super Unit> continuation) {
        return create(producerScope, continuation).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Type inference failed for: r0v11, types: [com.tm.androidcopysdk.utils.ContentExtensions$register$1$observer$1] */
    @Nullable
    public final Object invokeSuspend(@NotNull Object $result) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (this.label) {
            case 0:
                ResultKt.throwOnFailure($result);
                final ProducerScope $this$callbackFlow = (ProducerScope) this.L$0;
                final ?? r0 = new ContentObserver() { // from class: com.tm.androidcopysdk.utils.ContentExtensions$register$1$observer$1
                    /* JADX INFO: Access modifiers changed from: package-private */
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    /* JADX WARN: Multi-variable type inference failed */
                    {
                        super(null);
                    }

                    @Override // android.database.ContentObserver
                    public void onChange(boolean selfChange) {
                        $this$callbackFlow.trySend-JP2dKIU(Boolean.valueOf(selfChange));
                    }
                };
                this.$this_register.registerContentObserver(this.$uri, true, (ContentObserver) r0);
                $this$callbackFlow.trySend-JP2dKIU(Boxing.boxBoolean(false));
                final ContentResolver contentResolver = this.$this_register;
                this.label = 1;
                if (ProduceKt.awaitClose($this$callbackFlow, new Function0<Unit>() { // from class: com.tm.androidcopysdk.utils.ContentExtensions$register$1.1
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    {
                        super(0);
                    }

                    public final void invoke() {
                        contentResolver.unregisterContentObserver(r0);
                    }

                    /* renamed from: invoke  reason: collision with other method in class */
                    public /* bridge */ /* synthetic */ Object m111invoke() {
                        invoke();
                        return Unit.INSTANCE;
                    }
                }, (Continuation) this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                break;
            case 1:
                ResultKt.throwOnFailure($result);
                break;
            default:
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        return Unit.INSTANCE;
    }
}
