package com.tm.androidcopysdk.device;

import com.tm.androidcopysdk.model.ArchiveMessage;
import com.tm.androidcopysdk.model.ArchiveMessageType;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: MessageStoreProcessor.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��,\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0003\b&\u0018��2\u00020\u0001B\u0019\u0012\u0012\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003\"\u00020\u0004¢\u0006\u0002\u0010\u0005J\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u000eJ\u001a\u0010\u0010\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u000eH$R\u0012\u0010\u0006\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\tR\u0018\u0010\u0002\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u0003X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\n¨\u0006\u0011"}, d2 = {"Lcom/tm/androidcopysdk/device/MessageStoreProcessor;", "", "types", "", "Lcom/tm/androidcopysdk/model/ArchiveMessageType;", "([Lcom/tm/androidcopysdk/model/ArchiveMessageType;)V", "tag", "", "getTag", "()Ljava/lang/String;", "[Lcom/tm/androidcopysdk/model/ArchiveMessageType;", "afterMessageStateChanged", "", "message", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", "existing", "processAfterMessageStateChanged", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/device/MessageStoreProcessor.class */
public abstract class MessageStoreProcessor {
    @NotNull
    private final ArchiveMessageType[] types;

    @NotNull
    public abstract String getTag();

    protected abstract void processAfterMessageStateChanged(@NotNull ArchiveMessage archiveMessage, @Nullable ArchiveMessage archiveMessage2);

    public MessageStoreProcessor(@NotNull ArchiveMessageType... types) {
        Intrinsics.checkNotNullParameter(types, "types");
        this.types = types;
    }

    public final void afterMessageStateChanged(@NotNull ArchiveMessage message, @Nullable ArchiveMessage existing) {
        Intrinsics.checkNotNullParameter(message, "message");
        if (!ArraysKt.contains(this.types, message.getType())) {
            return;
        }
        processAfterMessageStateChanged(message, existing);
    }
}
