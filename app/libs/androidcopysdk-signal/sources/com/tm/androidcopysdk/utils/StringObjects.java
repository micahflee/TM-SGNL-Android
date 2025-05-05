package com.tm.androidcopysdk.utils;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: StringObjects.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\bÆ\u0002\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0011\u0010\u0003\u001a\u0004\u0018\u00010\u0004*\u0004\u0018\u00010\u0004H\u0086\b¨\u0006\u0005"}, d2 = {"Lcom/tm/androidcopysdk/utils/StringObjects;", "", "()V", "nonNullText", "", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nStringObjects.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StringObjects.kt\ncom/tm/androidcopysdk/utils/StringObjects\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,6:1\n1#2:7\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/utils/StringObjects.class */
public final class StringObjects {
    @NotNull
    public static final StringObjects INSTANCE = new StringObjects();

    private StringObjects() {
    }

    @Nullable
    public final String nonNullText(@Nullable String $this$nonNullText) {
        if ($this$nonNullText != null) {
            if (!StringsKt.equals($this$nonNullText, "null", true)) {
                return $this$nonNullText;
            }
            return null;
        }
        return null;
    }
}
