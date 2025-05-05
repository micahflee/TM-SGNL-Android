package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveEventInfo.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��&\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n��\n\u0002\u0010\u000e\n��\b\u0086\b\u0018��2\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\t\u0010\u0007\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\b\u001a\u00020��2\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\f\u001a\u00020\rHÖ\u0001J\t\u0010\u000e\u001a\u00020\u000fHÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0010"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveEventInfo;", "", "type", "Lcom/tm/androidcopysdk/model/ArchiveEventType;", "(Lcom/tm/androidcopysdk/model/ArchiveEventType;)V", "getType", "()Lcom/tm/androidcopysdk/model/ArchiveEventType;", "component1", "copy", "equals", "", "other", "hashCode", "", "toString", "", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveEventInfo.class */
public final class ArchiveEventInfo {
    @NotNull
    private final ArchiveEventType type;

    @NotNull
    public final ArchiveEventType component1() {
        return this.type;
    }

    @NotNull
    public final ArchiveEventInfo copy(@NotNull ArchiveEventType type) {
        Intrinsics.checkNotNullParameter(type, "type");
        return new ArchiveEventInfo(type);
    }

    public static /* synthetic */ ArchiveEventInfo copy$default(ArchiveEventInfo archiveEventInfo, ArchiveEventType archiveEventType, int i, Object obj) {
        if ((i & 1) != 0) {
            archiveEventType = archiveEventInfo.type;
        }
        return archiveEventInfo.copy(archiveEventType);
    }

    @NotNull
    public String toString() {
        return "ArchiveEventInfo(type=" + this.type + ')';
    }

    public int hashCode() {
        return this.type.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        return (other instanceof ArchiveEventInfo) && this.type == ((ArchiveEventInfo) other).type;
    }

    public ArchiveEventInfo(@NotNull ArchiveEventType type) {
        Intrinsics.checkNotNullParameter(type, "type");
        this.type = type;
    }

    @NotNull
    public final ArchiveEventType getType() {
        return this.type;
    }
}
