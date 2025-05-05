package com.tm.androidcopysdk.utils;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: RuntimeObject.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u001c\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\n\u0002\u0010\b\n\u0002\u0018\u0002\n��\bÆ\u0002\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0012\u0010\u0003\u001a\u0004\u0018\u00010\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u0006J\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004*\u0004\u0018\u00010\u0007H\u0002¨\u0006\b"}, d2 = {"Lcom/tm/androidcopysdk/utils/RuntimeObject;", "", "()V", "getCallerClassMethodAndLine", "", "index", "", "Ljava/lang/StackTraceElement;", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/RuntimeObject.class */
public final class RuntimeObject {
    @NotNull
    public static final RuntimeObject INSTANCE = new RuntimeObject();

    private RuntimeObject() {
    }

    public static /* synthetic */ String getCallerClassMethodAndLine$default(RuntimeObject runtimeObject, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = 0;
        }
        return runtimeObject.getCallerClassMethodAndLine(i);
    }

    @Nullable
    public final String getCallerClassMethodAndLine(int index) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        int elementIndex = index + 3;
        if (stackTrace.length < elementIndex) {
            Intrinsics.checkNotNull(stackTrace);
            return getCallerClassMethodAndLine((StackTraceElement) ArraysKt.lastOrNull(stackTrace));
        }
        return getCallerClassMethodAndLine(stackTrace[elementIndex]);
    }

    private final String getCallerClassMethodAndLine(StackTraceElement $this$getCallerClassMethodAndLine) {
        if ($this$getCallerClassMethodAndLine == null) {
            return null;
        }
        return $this$getCallerClassMethodAndLine.getClassName() + " at method: " + $this$getCallerClassMethodAndLine.getMethodName() + " at line: " + $this$getCallerClassMethodAndLine.getLineNumber();
    }
}
