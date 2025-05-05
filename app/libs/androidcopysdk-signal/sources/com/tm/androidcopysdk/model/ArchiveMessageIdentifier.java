package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveMessageIdentifier.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��.\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n��\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\b\n��\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0086\b\u0018�� \"2\u00020\u0001:\u0001\"B)\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0002\u0010\tJ\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0006HÆ\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\bHÆ\u0003J5\u0010\u0015\u001a\u00020��2\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bHÆ\u0001J\u0006\u0010\u0016\u001a\u00020\u0003J\u0006\u0010\u0017\u001a\u00020\u0003J\u0006\u0010\u0018\u001a\u00020\u0003J\u000e\u0010\u0019\u001a\u00020\u00032\u0006\u0010\u001a\u001a\u00020\u001bJ\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001f\u001a\u00020\u001bHÖ\u0001J\u0006\u0010 \u001a\u00020\u0003J\t\u0010!\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n��\u001a\u0004\b\f\u0010\rR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\b\n��\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u0010\u0010\u000b¨\u0006#"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveMessageIdentifier;", "", "uniqueId", "", CallLogMessageRecorder.from_key, "timestamp", "", "transportType", "Lcom/tm/androidcopysdk/model/ArchiveMessageType;", "(Ljava/lang/String;Ljava/lang/String;JLcom/tm/androidcopysdk/model/ArchiveMessageType;)V", "getFrom", "()Ljava/lang/String;", "getTimestamp", "()J", "getTransportType", "()Lcom/tm/androidcopysdk/model/ArchiveMessageType;", "getUniqueId", "component1", "component2", "component3", "component4", "copy", "deletedAllId", "deletedForAllId", "deletedForMeId", "editIdAt", "index", "", "equals", "", "other", "hashCode", "rawId", "toString", "Companion", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveMessageIdentifier.class */
public final class ArchiveMessageIdentifier {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @Nullable
    private final String uniqueId;
    @NotNull
    private final String from;
    private final long timestamp;
    @Nullable
    private final ArchiveMessageType transportType;
    @NotNull
    public static final String CONSTANT_SUFFIX = "_0";
    @NotNull
    public static final String DELETE_FOR_ME = "DELETE_FOR_ME-";
    @NotNull
    public static final String DELETE_FOR_ALL = "DELETE_FOR_ALL-";
    @NotNull
    public static final String DELETE_ALL_MESSAGES = "DELETE_ALL_MESSAGES-";
    @NotNull
    public static final String EDIT = "EDIT-";

    @Nullable
    public final String component1() {
        return this.uniqueId;
    }

    @NotNull
    public final String component2() {
        return this.from;
    }

    public final long component3() {
        return this.timestamp;
    }

    @Nullable
    public final ArchiveMessageType component4() {
        return this.transportType;
    }

    @NotNull
    public final ArchiveMessageIdentifier copy(@Nullable String uniqueId, @NotNull String from, long timestamp, @Nullable ArchiveMessageType transportType) {
        Intrinsics.checkNotNullParameter(from, CallLogMessageRecorder.from_key);
        return new ArchiveMessageIdentifier(uniqueId, from, timestamp, transportType);
    }

    public static /* synthetic */ ArchiveMessageIdentifier copy$default(ArchiveMessageIdentifier archiveMessageIdentifier, String str, String str2, long j, ArchiveMessageType archiveMessageType, int i, Object obj) {
        if ((i & 1) != 0) {
            str = archiveMessageIdentifier.uniqueId;
        }
        if ((i & 2) != 0) {
            str2 = archiveMessageIdentifier.from;
        }
        if ((i & 4) != 0) {
            j = archiveMessageIdentifier.timestamp;
        }
        if ((i & 8) != 0) {
            archiveMessageType = archiveMessageIdentifier.transportType;
        }
        return archiveMessageIdentifier.copy(str, str2, j, archiveMessageType);
    }

    @NotNull
    public String toString() {
        return "ArchiveMessageIdentifier(uniqueId=" + this.uniqueId + ", from=" + this.from + ", timestamp=" + this.timestamp + ", transportType=" + this.transportType + ')';
    }

    public int hashCode() {
        int result = this.uniqueId == null ? 0 : this.uniqueId.hashCode();
        return (((((result * 31) + this.from.hashCode()) * 31) + Long.hashCode(this.timestamp)) * 31) + (this.transportType == null ? 0 : this.transportType.hashCode());
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ArchiveMessageIdentifier) {
            ArchiveMessageIdentifier archiveMessageIdentifier = (ArchiveMessageIdentifier) other;
            return Intrinsics.areEqual(this.uniqueId, archiveMessageIdentifier.uniqueId) && Intrinsics.areEqual(this.from, archiveMessageIdentifier.from) && this.timestamp == archiveMessageIdentifier.timestamp && this.transportType == archiveMessageIdentifier.transportType;
        }
        return false;
    }

    public ArchiveMessageIdentifier(@Nullable String uniqueId, @NotNull String from, long timestamp, @Nullable ArchiveMessageType transportType) {
        Intrinsics.checkNotNullParameter(from, CallLogMessageRecorder.from_key);
        this.uniqueId = uniqueId;
        this.from = from;
        this.timestamp = timestamp;
        this.transportType = transportType;
    }

    @Nullable
    public final String getUniqueId() {
        return this.uniqueId;
    }

    @NotNull
    public final String getFrom() {
        return this.from;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

    @Nullable
    public final ArchiveMessageType getTransportType() {
        return this.transportType;
    }

    @NotNull
    public final String rawId() {
        String uniqueId = this.uniqueId;
        String str = uniqueId;
        if (!(str == null || str.length() == 0)) {
            return uniqueId;
        }
        String content = this.timestamp + '_' + this.from;
        return content + CONSTANT_SUFFIX;
    }

    @NotNull
    public final String deletedForMeId() {
        return DELETE_FOR_ME + rawId();
    }

    @NotNull
    public final String deletedForAllId() {
        return DELETE_FOR_ALL + rawId();
    }

    @NotNull
    public final String deletedAllId() {
        return DELETE_ALL_MESSAGES + rawId();
    }

    @NotNull
    public final String editIdAt(int index) {
        return EDIT + rawId() + '-' + index;
    }

    /* compiled from: ArchiveMessageIdentifier.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"�� \n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fR\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\b\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��¨\u0006\r"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveMessageIdentifier$Companion;", "", "()V", "CONSTANT_SUFFIX", "", "DELETE_ALL_MESSAGES", "DELETE_FOR_ALL", "DELETE_FOR_ME", "EDIT", "fromMessage", "Lcom/tm/androidcopysdk/model/ArchiveMessageIdentifier;", "message", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", "androidcopysdk_signalRelease"})
    @SourceDebugExtension({"SMAP\nArchiveMessageIdentifier.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveMessageIdentifier.kt\ncom/tm/androidcopysdk/model/ArchiveMessageIdentifier$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,42:1\n1#2:43\n*E\n"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveMessageIdentifier$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final ArchiveMessageIdentifier fromMessage(@NotNull ArchiveMessage message) {
            Intrinsics.checkNotNullParameter(message, "message");
            String uniqueId = message.getUniqueId();
            String id = message.getSender().getId();
            if (id == null) {
                id = message.getSenderAddress();
            }
            return new ArchiveMessageIdentifier(uniqueId, id, message.getTimestamp().getValue(), message.getType());
        }
    }
}
