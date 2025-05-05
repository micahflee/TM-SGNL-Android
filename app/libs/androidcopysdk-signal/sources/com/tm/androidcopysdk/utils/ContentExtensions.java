package com.tm.androidcopysdk.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import androidx.annotation.RequiresPermission;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.internal.CombineKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ContentExtensions.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��^\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u0011\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001f\u0010\u0003\u001a\u00020\u0004*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0004H\u0086\bJ\u001c\u0010\t\u001a\u0004\u0018\u00010\u0004*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0086\b¢\u0006\u0002\u0010\nJ\u001f\u0010\u000b\u001a\u00020\f*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\fH\u0086\bJ\u001c\u0010\r\u001a\u0004\u0018\u00010\f*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0086\b¢\u0006\u0002\u0010\u000eJ&\u0010\u000f\u001a\u0004\u0018\u00010\u0010*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0010H\u0086\b¢\u0006\u0002\u0010\u0011J\u001c\u0010\u0012\u001a\u0004\u0018\u00010\u0010*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0086\b¢\u0006\u0002\u0010\u0013J\u001f\u0010\u0014\u001a\u00020\u0007*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007H\u0086\bJ\u0017\u0010\u0015\u001a\u0004\u0018\u00010\u0007*\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0086\bJE\u0010\u0016\u001a\b\u0012\u0004\u0012\u0002H\u00180\u0017\"\u0004\b��\u0010\u0018*\u0004\u0018\u00010\u00052%\b\u0004\u0010\u0019\u001a\u001f\u0012\u0013\u0012\u00110\u0005¢\u0006\f\b\u001b\u0012\b\b\u0006\u0012\u0004\b\b(\u001c\u0012\u0006\u0012\u0004\u0018\u0001H\u00180\u001aH\u0086\bø\u0001��J}\u0010\u001d\u001a\b\u0012\u0004\u0012\u0002H\u00180\u0017\"\u0004\b��\u0010\u0018*\u00020\u001e2\b\b\u0001\u0010\u001f\u001a\u00020 2\u0010\b\u0002\u0010!\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\"2\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\u00072\u0010\b\u0002\u0010$\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\"2\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u00072\u0014\b\u0004\u0010&\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u0002H\u00180\u001aH\u0086\bø\u0001��¢\u0006\u0002\u0010'Jy\u0010(\u001a\u0004\u0018\u0001H\u0018\"\u0004\b��\u0010\u0018*\u00020\u001e2\b\b\u0001\u0010\u001f\u001a\u00020 2\u0010\b\u0002\u0010!\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\"2\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\u00072\u0010\b\u0002\u0010$\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\"2\n\b\u0002\u0010%\u001a\u0004\u0018\u00010\u00072\u0014\b\u0004\u0010&\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u0002H\u00180\u001aH\u0086\bø\u0001��¢\u0006\u0002\u0010)J\u001a\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00040+*\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0007J)\u0010*\u001a\u0006\u0012\u0002\b\u00030+*\u00020\u001e2\u0012\u0010\u001f\u001a\n\u0012\u0006\b\u0001\u0012\u00020 0\"\"\u00020 H\u0007¢\u0006\u0002\u0010,\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006-"}, d2 = {"Lcom/tm/androidcopysdk/utils/ContentExtensions;", "", "()V", "getBoolean", "", "Landroid/database/Cursor;", "name", "", "default", "getBooleanOrNull", "(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/Boolean;", "getInt", "", "getIntOrNull", "(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/Integer;", "getLong", "", "(Landroid/database/Cursor;Ljava/lang/String;J)Ljava/lang/Long;", "getLongOrNull", "(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/Long;", "getString", "getStringOrNull", "iterate", "", "T", "block", "Lkotlin/Function1;", "Lkotlin/ParameterName;", "cursor", "queryMultiple", "Landroid/content/ContentResolver;", "uri", "Landroid/net/Uri;", "projection", "", "selection", "selectionArgs", "sortOrder", "callback", "(Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)Ljava/util/List;", "querySingle", "(Landroid/content/ContentResolver;Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "register", "Lkotlinx/coroutines/flow/Flow;", "(Landroid/content/ContentResolver;[Landroid/net/Uri;)Lkotlinx/coroutines/flow/Flow;", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nContentExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ContentExtensions.kt\ncom/tm/androidcopysdk/utils/ContentExtensions\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 Zip.kt\nkotlinx/coroutines/flow/FlowKt__ZipKt\n+ 5 SafeCollector.common.kt\nkotlinx/coroutines/flow/internal/SafeCollector_commonKt\n+ 6 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 7 Cursor.kt\nandroidx/core/database/CursorKt\n*L\n1#1,104:1\n40#1:116\n41#1:118\n40#1,2:120\n66#1:122\n67#1:124\n49#1:126\n66#1:127\n67#1:129\n49#1:131\n66#1,2:132\n57#1:135\n58#1:137\n57#1,2:139\n66#1:142\n67#1:144\n66#1,2:146\n81#1,20:148\n85#1,16:168\n11065#2:105\n11400#2,3:106\n37#3,2:109\n237#4:111\n239#4:113\n106#5:112\n1#6:114\n1#6:117\n1#6:123\n1#6:128\n1#6:136\n1#6:143\n112#7:115\n112#7:119\n73#7:125\n73#7:130\n86#7:134\n86#7:138\n73#7:141\n73#7:145\n*S KotlinDebug\n*F\n+ 1 ContentExtensions.kt\ncom/tm/androidcopysdk/utils/ContentExtensions\n*L\n45#1:116\n45#1:118\n45#1:120,2\n49#1:122\n49#1:124\n53#1:126\n53#1:127\n53#1:129\n53#1:131\n53#1:132,2\n62#1:135\n62#1:137\n62#1:139,2\n71#1:142\n71#1:144\n71#1:146,2\n76#1:148,20\n81#1:168,16\n36#1:105\n36#1:106,3\n36#1:109,2\n36#1:111\n36#1:113\n36#1:112\n45#1:117\n49#1:123\n53#1:128\n62#1:136\n71#1:143\n41#1:115\n45#1:119\n49#1:125\n53#1:130\n58#1:134\n62#1:138\n67#1:141\n71#1:145\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/ContentExtensions.class */
public final class ContentExtensions {
    @NotNull
    public static final ContentExtensions INSTANCE = new ContentExtensions();

    private ContentExtensions() {
    }

    @ExperimentalCoroutinesApi
    @NotNull
    public final Flow<Boolean> register(@NotNull ContentResolver $this$register, @NotNull Uri uri) {
        Intrinsics.checkNotNullParameter($this$register, "<this>");
        Intrinsics.checkNotNullParameter(uri, "uri");
        return FlowKt.callbackFlow(new ContentExtensions$register$1($this$register, uri, null));
    }

    @ExperimentalCoroutinesApi
    @NotNull
    public final Flow<?> register(@NotNull ContentResolver $this$register, @NotNull Uri... uri) {
        Intrinsics.checkNotNullParameter($this$register, "<this>");
        Intrinsics.checkNotNullParameter(uri, "uri");
        Collection destination$iv$iv = new ArrayList(uri.length);
        for (Uri uri2 : uri) {
            destination$iv$iv.add(INSTANCE.register($this$register, uri2));
        }
        Collection $this$toTypedArray$iv = (List) destination$iv$iv;
        Flow[] flowArr = (Flow[]) $this$toTypedArray$iv.toArray(new Flow[0]);
        final Flow[] flows$iv = (Flow[]) Arrays.copyOf(flowArr, flowArr.length);
        return new Flow<List<? extends Boolean>>() { // from class: com.tm.androidcopysdk.utils.ContentExtensions$register$$inlined$combine$1
            @Nullable
            public Object collect(@NotNull FlowCollector collector, @NotNull Continuation $completion) {
                Flow[] flowArr2 = flows$iv;
                final Flow[] flowArr3 = flows$iv;
                Object combineInternal = CombineKt.combineInternal(collector, flowArr2, new Function0<Boolean[]>() { // from class: com.tm.androidcopysdk.utils.ContentExtensions$register$$inlined$combine$1.2
                    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
                    {
                        super(0);
                    }

                    @Nullable
                    /* renamed from: invoke */
                    public final Boolean[] m110invoke() {
                        return new Boolean[flowArr3.length];
                    }
                }, new AnonymousClass3(null), $completion);
                return combineInternal == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? combineInternal : Unit.INSTANCE;
            }

            /* compiled from: Zip.kt */
            @Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0016\n��\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u0011\n��\u0010��\u001a\u00020\u0001\"\u0006\b��\u0010\u0002\u0018\u0001\"\u0004\b\u0001\u0010\u0003*\b\u0012\u0004\u0012\u0002H\u00030\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0006H\u008a@¨\u0006\u0007"}, d2 = {"<anonymous>", "", "T", "R", "Lkotlinx/coroutines/flow/FlowCollector;", "it", "", "kotlinx/coroutines/flow/FlowKt__ZipKt$combine$5$2"})
            @DebugMetadata(f = "ContentExtensions.kt", l = {238}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "com.tm.androidcopysdk.utils.ContentExtensions$register$$inlined$combine$1$3")
            @SourceDebugExtension({"SMAP\nZip.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Zip.kt\nkotlinx/coroutines/flow/FlowKt__ZipKt$combine$5$2\n+ 2 ContentExtensions.kt\ncom/tm/androidcopysdk/utils/ContentExtensions\n*L\n1#1,332:1\n36#2:333\n*E\n"})
            /* renamed from: com.tm.androidcopysdk.utils.ContentExtensions$register$$inlined$combine$1$3  reason: invalid class name */
            /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/ContentExtensions$register$$inlined$combine$1$3.class */
            public static final class AnonymousClass3 extends SuspendLambda implements Function3<FlowCollector<? super List<? extends Boolean>>, Boolean[], Continuation<? super Unit>, Object> {
                int label;
                private /* synthetic */ Object L$0;
                /* synthetic */ Object L$1;

                public AnonymousClass3(Continuation $completion) {
                    super(3, $completion);
                }

                @Nullable
                public final Object invoke(@NotNull FlowCollector<? super List<? extends Boolean>> flowCollector, @NotNull Boolean[] boolArr, @Nullable Continuation<? super Unit> continuation) {
                    AnonymousClass3 anonymousClass3 = new AnonymousClass3(continuation);
                    anonymousClass3.L$0 = flowCollector;
                    anonymousClass3.L$1 = boolArr;
                    return anonymousClass3.invokeSuspend(Unit.INSTANCE);
                }

                /* JADX WARN: Multi-variable type inference failed */
                public /* bridge */ /* synthetic */ Object invoke(Object p1, Object p2, Object p3) {
                    return invoke((FlowCollector<? super List<? extends Boolean>>) p1, (Boolean[]) p2, (Continuation<? super Unit>) p3);
                }

                @Nullable
                public final Object invokeSuspend(@NotNull Object $result) {
                    Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                    switch (this.label) {
                        case 0:
                            ResultKt.throwOnFailure($result);
                            FlowCollector $this$combineInternal = (FlowCollector) this.L$0;
                            Object[] it = (Object[]) this.L$1;
                            Continuation continuation = (Continuation) this;
                            this.label = 1;
                            if ($this$combineInternal.emit(ArraysKt.asList((Boolean[]) it), (Continuation) this) == coroutine_suspended) {
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
        };
    }

    @Nullable
    public final String getStringOrNull(@NotNull Cursor $this$getStringOrNull, @NotNull String name) {
        Intrinsics.checkNotNullParameter($this$getStringOrNull, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Integer valueOf = Integer.valueOf($this$getStringOrNull.getColumnIndex(name));
        int it = valueOf.intValue();
        Integer num = it >= 0 ? valueOf : null;
        if (num != null) {
            int index = num.intValue();
            if ($this$getStringOrNull.isNull(index)) {
                return null;
            }
            return $this$getStringOrNull.getString(index);
        }
        return null;
    }

    public static /* synthetic */ String getString$default(ContentExtensions $this, Cursor $receiver, String name, String str, int i, Object obj) {
        String str2;
        if ((i & 2) != 0) {
            str = "";
        }
        Intrinsics.checkNotNullParameter($receiver, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(str, "default");
        Integer valueOf = Integer.valueOf($receiver.getColumnIndex(name));
        int it$iv = valueOf.intValue();
        Integer num = it$iv >= 0 ? valueOf : null;
        if (num != null) {
            int index$iv = num.intValue();
            str2 = $receiver.isNull(index$iv) ? null : $receiver.getString(index$iv);
        } else {
            str2 = null;
        }
        return str2 == null ? str : str2;
    }

    @NotNull
    public final String getString(@NotNull Cursor $this$getString, @NotNull String name, @NotNull String str) {
        String str2;
        Intrinsics.checkNotNullParameter($this$getString, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(str, "default");
        Integer valueOf = Integer.valueOf($this$getString.getColumnIndex(name));
        int it$iv = valueOf.intValue();
        Integer num = it$iv >= 0 ? valueOf : null;
        if (num != null) {
            int index$iv = num.intValue();
            str2 = $this$getString.isNull(index$iv) ? null : $this$getString.getString(index$iv);
        } else {
            str2 = null;
        }
        return str2 == null ? str : str2;
    }

    @Nullable
    public final Boolean getBooleanOrNull(@NotNull Cursor $this$getBooleanOrNull, @NotNull String name) {
        Integer num;
        Intrinsics.checkNotNullParameter($this$getBooleanOrNull, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Integer valueOf = Integer.valueOf($this$getBooleanOrNull.getColumnIndex(name));
        int it$iv = valueOf.intValue();
        Integer num2 = it$iv >= 0 ? valueOf : null;
        if (num2 != null) {
            int index$iv = num2.intValue();
            num = $this$getBooleanOrNull.isNull(index$iv) ? null : Integer.valueOf($this$getBooleanOrNull.getInt(index$iv));
        } else {
            num = null;
        }
        if (num != null) {
            int it = num.intValue();
            return Boolean.valueOf(it != 0);
        }
        return null;
    }

    public static /* synthetic */ boolean getBoolean$default(ContentExtensions $this, Cursor $receiver, String name, boolean z, int i, Object obj) {
        Integer num;
        Boolean bool;
        if ((i & 2) != 0) {
            z = false;
        }
        Intrinsics.checkNotNullParameter($receiver, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Integer valueOf = Integer.valueOf($receiver.getColumnIndex(name));
        int it$iv$iv = valueOf.intValue();
        Integer num2 = it$iv$iv >= 0 ? valueOf : null;
        if (num2 != null) {
            int index$iv$iv = num2.intValue();
            num = $receiver.isNull(index$iv$iv) ? null : Integer.valueOf($receiver.getInt(index$iv$iv));
        } else {
            num = null;
        }
        if (num != null) {
            int it$iv = num.intValue();
            bool = Boolean.valueOf(it$iv != 0);
        } else {
            bool = null;
        }
        return bool != null ? bool.booleanValue() : z;
    }

    public final boolean getBoolean(@NotNull Cursor $this$getBoolean, @NotNull String name, boolean z) {
        Integer num;
        Boolean bool;
        Intrinsics.checkNotNullParameter($this$getBoolean, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Integer valueOf = Integer.valueOf($this$getBoolean.getColumnIndex(name));
        int it$iv$iv = valueOf.intValue();
        Integer num2 = it$iv$iv >= 0 ? valueOf : null;
        if (num2 != null) {
            int index$iv$iv = num2.intValue();
            num = $this$getBoolean.isNull(index$iv$iv) ? null : Integer.valueOf($this$getBoolean.getInt(index$iv$iv));
        } else {
            num = null;
        }
        if (num != null) {
            int it$iv = num.intValue();
            bool = Boolean.valueOf(it$iv != 0);
        } else {
            bool = null;
        }
        return bool != null ? bool.booleanValue() : z;
    }

    @Nullable
    public final Long getLongOrNull(@NotNull Cursor $this$getLongOrNull, @NotNull String name) {
        Intrinsics.checkNotNullParameter($this$getLongOrNull, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Integer valueOf = Integer.valueOf($this$getLongOrNull.getColumnIndex(name));
        int it = valueOf.intValue();
        Integer num = it >= 0 ? valueOf : null;
        if (num != null) {
            int index = num.intValue();
            if ($this$getLongOrNull.isNull(index)) {
                return null;
            }
            return Long.valueOf($this$getLongOrNull.getLong(index));
        }
        return null;
    }

    public static /* synthetic */ Long getLong$default(ContentExtensions $this, Cursor $receiver, String name, long j, int i, Object obj) {
        Long l;
        if ((i & 2) != 0) {
            j = 0;
        }
        Intrinsics.checkNotNullParameter($receiver, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Integer valueOf = Integer.valueOf($receiver.getColumnIndex(name));
        int it$iv = valueOf.intValue();
        Integer num = it$iv >= 0 ? valueOf : null;
        if (num != null) {
            int index$iv = num.intValue();
            l = $receiver.isNull(index$iv) ? null : Long.valueOf($receiver.getLong(index$iv));
        } else {
            l = null;
        }
        return l == null ? Long.valueOf(j) : l;
    }

    @Nullable
    public final Long getLong(@NotNull Cursor $this$getLong, @NotNull String name, long j) {
        Long l;
        Intrinsics.checkNotNullParameter($this$getLong, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Integer valueOf = Integer.valueOf($this$getLong.getColumnIndex(name));
        int it$iv = valueOf.intValue();
        Integer num = it$iv >= 0 ? valueOf : null;
        if (num != null) {
            int index$iv = num.intValue();
            l = $this$getLong.isNull(index$iv) ? null : Long.valueOf($this$getLong.getLong(index$iv));
        } else {
            l = null;
        }
        return l == null ? Long.valueOf(j) : l;
    }

    @Nullable
    public final Integer getIntOrNull(@NotNull Cursor $this$getIntOrNull, @NotNull String name) {
        Intrinsics.checkNotNullParameter($this$getIntOrNull, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Integer valueOf = Integer.valueOf($this$getIntOrNull.getColumnIndex(name));
        int it = valueOf.intValue();
        Integer num = it >= 0 ? valueOf : null;
        if (num != null) {
            int index = num.intValue();
            if ($this$getIntOrNull.isNull(index)) {
                return null;
            }
            return Integer.valueOf($this$getIntOrNull.getInt(index));
        }
        return null;
    }

    public static /* synthetic */ int getInt$default(ContentExtensions $this, Cursor $receiver, String name, int i, int i2, Object obj) {
        Integer num;
        if ((i2 & 2) != 0) {
            i = 0;
        }
        Intrinsics.checkNotNullParameter($receiver, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Integer valueOf = Integer.valueOf($receiver.getColumnIndex(name));
        int it$iv = valueOf.intValue();
        Integer num2 = it$iv >= 0 ? valueOf : null;
        if (num2 != null) {
            int index$iv = num2.intValue();
            num = $receiver.isNull(index$iv) ? null : Integer.valueOf($receiver.getInt(index$iv));
        } else {
            num = null;
        }
        return num != null ? num.intValue() : i;
    }

    public final int getInt(@NotNull Cursor $this$getInt, @NotNull String name, int i) {
        Integer num;
        Intrinsics.checkNotNullParameter($this$getInt, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Integer valueOf = Integer.valueOf($this$getInt.getColumnIndex(name));
        int it$iv = valueOf.intValue();
        Integer num2 = it$iv >= 0 ? valueOf : null;
        if (num2 != null) {
            int index$iv = num2.intValue();
            num = $this$getInt.isNull(index$iv) ? null : Integer.valueOf($this$getInt.getInt(index$iv));
        } else {
            num = null;
        }
        return num != null ? num.intValue() : i;
    }

    public static /* synthetic */ Object querySingle$default(ContentExtensions $this, ContentResolver $receiver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, Function1 callback, int i, Object obj) {
        if ((i & 2) != 0) {
            projection = null;
        }
        if ((i & 4) != 0) {
            selection = null;
        }
        if ((i & 8) != 0) {
            selectionArgs = null;
        }
        if ((i & 16) != 0) {
            sortOrder = null;
        }
        Intrinsics.checkNotNullParameter($receiver, "<this>");
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(callback, "callback");
        Cursor $this$iterate$iv$iv = $receiver.query(uri, projection, selection, selectionArgs, sortOrder);
        if (!Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper())) {
            List result$iv$iv = new ArrayList();
            if ($this$iterate$iv$iv != null) {
                try {
                    if ($this$iterate$iv$iv.getCount() > 0) {
                        while ($this$iterate$iv$iv.moveToNext()) {
                            Object e$iv$iv = callback.invoke($this$iterate$iv$iv);
                            if (e$iv$iv != null) {
                                result$iv$iv.add(e$iv$iv);
                            }
                        }
                    }
                } catch (Throwable e$iv$iv2) {
                    try {
                        e$iv$iv2.printStackTrace();
                        InlineMarker.finallyStart(1);
                        if ($this$iterate$iv$iv != null) {
                            $this$iterate$iv$iv.close();
                        }
                        InlineMarker.finallyEnd(1);
                    } finally {
                        InlineMarker.finallyStart(1);
                        if ($this$iterate$iv$iv != null) {
                            $this$iterate$iv$iv.close();
                        }
                        InlineMarker.finallyEnd(1);
                    }
                }
            }
            return CollectionsKt.firstOrNull(result$iv$iv);
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }

    @Nullable
    public final <T> T querySingle(@NotNull ContentResolver $this$querySingle, @RequiresPermission.Read @NotNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder, @NotNull Function1<? super Cursor, ? extends T> function1) {
        Intrinsics.checkNotNullParameter($this$querySingle, "<this>");
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(function1, "callback");
        Cursor $this$iterate$iv$iv = $this$querySingle.query(uri, projection, selection, selectionArgs, sortOrder);
        if (!Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper())) {
            List result$iv$iv = new ArrayList();
            if ($this$iterate$iv$iv != null) {
                try {
                    if ($this$iterate$iv$iv.getCount() > 0) {
                        while ($this$iterate$iv$iv.moveToNext()) {
                            Object e$iv$iv = function1.invoke($this$iterate$iv$iv);
                            if (e$iv$iv != null) {
                                result$iv$iv.add(e$iv$iv);
                            }
                        }
                    }
                } catch (Throwable e$iv$iv2) {
                    try {
                        e$iv$iv2.printStackTrace();
                        InlineMarker.finallyStart(1);
                        if ($this$iterate$iv$iv != null) {
                            $this$iterate$iv$iv.close();
                        }
                        InlineMarker.finallyEnd(1);
                    } finally {
                        InlineMarker.finallyStart(1);
                        if ($this$iterate$iv$iv != null) {
                            $this$iterate$iv$iv.close();
                        }
                        InlineMarker.finallyEnd(1);
                    }
                }
            }
            return (T) CollectionsKt.firstOrNull(result$iv$iv);
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }

    public static /* synthetic */ List queryMultiple$default(ContentExtensions $this, ContentResolver $receiver, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, Function1 callback, int i, Object obj) {
        if ((i & 2) != 0) {
            projection = null;
        }
        if ((i & 4) != 0) {
            selection = null;
        }
        if ((i & 8) != 0) {
            selectionArgs = null;
        }
        if ((i & 16) != 0) {
            sortOrder = null;
        }
        Intrinsics.checkNotNullParameter($receiver, "<this>");
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(callback, "callback");
        Cursor $this$iterate$iv = $receiver.query(uri, projection, selection, selectionArgs, sortOrder);
        if (!Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper())) {
            List result$iv = new ArrayList();
            if ($this$iterate$iv != null) {
                try {
                    if ($this$iterate$iv.getCount() > 0) {
                        while ($this$iterate$iv.moveToNext()) {
                            Object e$iv = callback.invoke($this$iterate$iv);
                            if (e$iv != null) {
                                result$iv.add(e$iv);
                            }
                        }
                    }
                } catch (Throwable e$iv2) {
                    try {
                        e$iv2.printStackTrace();
                        InlineMarker.finallyStart(1);
                        if ($this$iterate$iv != null) {
                            $this$iterate$iv.close();
                        }
                        InlineMarker.finallyEnd(1);
                    } finally {
                        InlineMarker.finallyStart(1);
                        if ($this$iterate$iv != null) {
                            $this$iterate$iv.close();
                        }
                        InlineMarker.finallyEnd(1);
                    }
                }
            }
            return result$iv;
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }

    @NotNull
    public final <T> List<T> queryMultiple(@NotNull ContentResolver $this$queryMultiple, @RequiresPermission.Read @NotNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder, @NotNull Function1<? super Cursor, ? extends T> function1) {
        Intrinsics.checkNotNullParameter($this$queryMultiple, "<this>");
        Intrinsics.checkNotNullParameter(uri, "uri");
        Intrinsics.checkNotNullParameter(function1, "callback");
        Cursor $this$iterate$iv = $this$queryMultiple.query(uri, projection, selection, selectionArgs, sortOrder);
        if (!Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper())) {
            List result$iv = new ArrayList();
            if ($this$iterate$iv != null) {
                try {
                    if ($this$iterate$iv.getCount() > 0) {
                        while ($this$iterate$iv.moveToNext()) {
                            Object e$iv = function1.invoke($this$iterate$iv);
                            if (e$iv != null) {
                                result$iv.add(e$iv);
                            }
                        }
                    }
                } catch (Throwable e$iv2) {
                    try {
                        e$iv2.printStackTrace();
                        InlineMarker.finallyStart(1);
                        if ($this$iterate$iv != null) {
                            $this$iterate$iv.close();
                        }
                        InlineMarker.finallyEnd(1);
                    } finally {
                        InlineMarker.finallyStart(1);
                        if ($this$iterate$iv != null) {
                            $this$iterate$iv.close();
                        }
                        InlineMarker.finallyEnd(1);
                    }
                }
            }
            return result$iv;
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }

    @NotNull
    public final <T> List<T> iterate(@Nullable Cursor $this$iterate, @NotNull Function1<? super Cursor, ? extends T> function1) {
        Intrinsics.checkNotNullParameter(function1, "block");
        if (!Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper())) {
            List result = new ArrayList();
            if ($this$iterate != null) {
                try {
                    if ($this$iterate.getCount() > 0) {
                        while ($this$iterate.moveToNext()) {
                            Object e = function1.invoke($this$iterate);
                            if (e != null) {
                                result.add(e);
                            }
                        }
                    }
                } catch (Throwable e2) {
                    try {
                        e2.printStackTrace();
                        InlineMarker.finallyStart(1);
                        if ($this$iterate != null) {
                            $this$iterate.close();
                        }
                        InlineMarker.finallyEnd(1);
                    } finally {
                        InlineMarker.finallyStart(1);
                        if ($this$iterate != null) {
                            $this$iterate.close();
                        }
                        InlineMarker.finallyEnd(1);
                    }
                }
            }
            return result;
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }
}
