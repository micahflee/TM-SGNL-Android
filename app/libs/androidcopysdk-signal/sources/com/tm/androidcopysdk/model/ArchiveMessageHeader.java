package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveMessageHeader.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\"\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018��2\u00020\u0001B'\b\u0007\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\u0006J\u000b\u0010\u000b\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0003HÆ\u0003J-\u0010\u000e\u001a\u00020��2\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003HÆ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0012\u001a\u00020\u0013HÖ\u0001J\t\u0010\u0014\u001a\u00020\u0003HÖ\u0001R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\t\u0010\bR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\n\u0010\b¨\u0006\u0015"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveMessageHeader;", "", CallLogMessageRecorder.messageId_key, "", DBHeadersTable.HeadersEntry.COLUMN_NAME_KEY, DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE, "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getKey", "()Ljava/lang/String;", "getMessageId", "getValue", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveMessageHeader.class */
public final class ArchiveMessageHeader {
    @Nullable
    private final String messageId;
    @Nullable
    private final String key;
    @Nullable
    private final String value;

    @Nullable
    public final String component1() {
        return this.messageId;
    }

    @Nullable
    public final String component2() {
        return this.key;
    }

    @Nullable
    public final String component3() {
        return this.value;
    }

    @NotNull
    public final ArchiveMessageHeader copy(@Nullable String messageId, @Nullable String key, @Nullable String value) {
        return new ArchiveMessageHeader(messageId, key, value);
    }

    public static /* synthetic */ ArchiveMessageHeader copy$default(ArchiveMessageHeader archiveMessageHeader, String str, String str2, String str3, int i, Object obj) {
        if ((i & 1) != 0) {
            str = archiveMessageHeader.messageId;
        }
        if ((i & 2) != 0) {
            str2 = archiveMessageHeader.key;
        }
        if ((i & 4) != 0) {
            str3 = archiveMessageHeader.value;
        }
        return archiveMessageHeader.copy(str, str2, str3);
    }

    @NotNull
    public String toString() {
        return "ArchiveMessageHeader(messageId=" + this.messageId + ", key=" + this.key + ", value=" + this.value + ')';
    }

    public int hashCode() {
        int result = this.messageId == null ? 0 : this.messageId.hashCode();
        return (((result * 31) + (this.key == null ? 0 : this.key.hashCode())) * 31) + (this.value == null ? 0 : this.value.hashCode());
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ArchiveMessageHeader) {
            ArchiveMessageHeader archiveMessageHeader = (ArchiveMessageHeader) other;
            return Intrinsics.areEqual(this.messageId, archiveMessageHeader.messageId) && Intrinsics.areEqual(this.key, archiveMessageHeader.key) && Intrinsics.areEqual(this.value, archiveMessageHeader.value);
        }
        return false;
    }

    @JvmOverloads
    public ArchiveMessageHeader(@Nullable String messageId, @Nullable String key) {
        this(messageId, key, null, 4, null);
    }

    @JvmOverloads
    public ArchiveMessageHeader(@Nullable String messageId, @Nullable String key, @Nullable String value) {
        this.messageId = messageId;
        this.key = key;
        this.value = value;
    }

    public /* synthetic */ ArchiveMessageHeader(String str, String str2, String str3, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, str2, (i & 4) != 0 ? null : str3);
    }

    @Nullable
    public final String getMessageId() {
        return this.messageId;
    }

    @Nullable
    public final String getKey() {
        return this.key;
    }

    @Nullable
    public final String getValue() {
        return this.value;
    }
}
