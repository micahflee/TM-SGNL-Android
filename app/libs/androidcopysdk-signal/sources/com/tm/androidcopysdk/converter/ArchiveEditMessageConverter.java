package com.tm.androidcopysdk.converter;

import com.tm.androidcopysdk.model.ArchiveMessage;
import com.tm.androidcopysdk.model.ArchiveMessageEdit;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
/* compiled from: ArchiveEditMessageConverter.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"�� \n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018�� \t2\u00020\u0001:\u0001\tB\u0005¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J\u0012\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0005\u001a\u00020\u0006H\u0002¨\u0006\n"}, d2 = {"Lcom/tm/androidcopysdk/converter/ArchiveEditMessageConverter;", "", "()V", "convert", "Lcom/tm/androidcopysdk/model/ArchiveMessageEdit;", "message", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", "convertBody", "", "Companion", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/converter/ArchiveEditMessageConverter.class */
public final class ArchiveEditMessageConverter {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    public static final String EDITED_TEXT_PREFIX = "Edited Text:\n";

    @NotNull
    public final ArchiveMessageEdit convert(@NotNull ArchiveMessage message) {
        Intrinsics.checkNotNullParameter(message, "message");
        String body = convertBody(message);
        String uniqueId = message.getUniqueId();
        if (uniqueId == null) {
            uniqueId = "Unknown";
        }
        return new ArchiveMessageEdit(uniqueId, body, message.getAttachments());
    }

    private final String convertBody(ArchiveMessage message) {
        String body = message.getBody();
        if (body == null) {
            return null;
        }
        if (!StringsKt.contains$default(body, EDITED_TEXT_PREFIX, false, 2, (Object) null)) {
            return body;
        }
        String substring = body.substring(StringsKt.lastIndexOf$default(body, EDITED_TEXT_PREFIX, 0, false, 6, (Object) null) + 13);
        Intrinsics.checkNotNullExpressionValue(substring, "substring(...)");
        return StringsKt.trimEnd(substring, new char[]{'\n'});
    }

    /* compiled from: ArchiveEditMessageConverter.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��¨\u0006\u0005"}, d2 = {"Lcom/tm/androidcopysdk/converter/ArchiveEditMessageConverter$Companion;", "", "()V", "EDITED_TEXT_PREFIX", "", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/converter/ArchiveEditMessageConverter$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }
    }
}
