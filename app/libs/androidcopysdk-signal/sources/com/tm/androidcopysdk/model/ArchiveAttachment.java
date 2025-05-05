package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveAttachment.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��.\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n��\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000b\n\u0002\b\u0016\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018��2\u00020\u0001BE\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0002\u0010\rJ\u000b\u0010\u0018\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u001d\u001a\u00020\nHÆ\u0003J\t\u0010\u001e\u001a\u00020\fHÆ\u0003JW\u0010\u001f\u001a\u00020��2\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fHÆ\u0001J\u0013\u0010 \u001a\u00020\f2\b\u0010!\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\"\u001a\u00020#HÖ\u0001J\t\u0010$\u001a\u00020\u0003HÖ\u0001R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u0010\u0010\u000fR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n��\u001a\u0004\b\u000b\u0010\u0012R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u0013\u0010\u000fR\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n��\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n��\u001a\u0004\b\u0016\u0010\u0017¨\u0006%"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveAttachment;", "", "id", "", "type", "Lcom/tm/androidcopysdk/model/ArchiveAttachmentType;", CallLogMessageRecorder.contentType_key, "sourcePath", "archivePath", "status", "Lcom/tm/androidcopysdk/model/MessageAttachmentStatus;", "isViewOnce", "", "(Ljava/lang/String;Lcom/tm/androidcopysdk/model/ArchiveAttachmentType;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/tm/androidcopysdk/model/MessageAttachmentStatus;Z)V", "getArchivePath", "()Ljava/lang/String;", "getContentType", "getId", "()Z", "getSourcePath", "getStatus", "()Lcom/tm/androidcopysdk/model/MessageAttachmentStatus;", "getType", "()Lcom/tm/androidcopysdk/model/ArchiveAttachmentType;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "", "toString", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveAttachment.class */
public final class ArchiveAttachment {
    @Nullable
    private final String id;
    @NotNull
    private final ArchiveAttachmentType type;
    @Nullable
    private final String contentType;
    @Nullable
    private final String sourcePath;
    @Nullable
    private final String archivePath;
    @NotNull
    private final MessageAttachmentStatus status;
    private final boolean isViewOnce;

    @Nullable
    public final String component1() {
        return this.id;
    }

    @NotNull
    public final ArchiveAttachmentType component2() {
        return this.type;
    }

    @Nullable
    public final String component3() {
        return this.contentType;
    }

    @Nullable
    public final String component4() {
        return this.sourcePath;
    }

    @Nullable
    public final String component5() {
        return this.archivePath;
    }

    @NotNull
    public final MessageAttachmentStatus component6() {
        return this.status;
    }

    public final boolean component7() {
        return this.isViewOnce;
    }

    @NotNull
    public final ArchiveAttachment copy(@Nullable String id, @NotNull ArchiveAttachmentType type, @Nullable String contentType, @Nullable String sourcePath, @Nullable String archivePath, @NotNull MessageAttachmentStatus status, boolean isViewOnce) {
        Intrinsics.checkNotNullParameter(type, "type");
        Intrinsics.checkNotNullParameter(status, "status");
        return new ArchiveAttachment(id, type, contentType, sourcePath, archivePath, status, isViewOnce);
    }

    public static /* synthetic */ ArchiveAttachment copy$default(ArchiveAttachment archiveAttachment, String str, ArchiveAttachmentType archiveAttachmentType, String str2, String str3, String str4, MessageAttachmentStatus messageAttachmentStatus, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            str = archiveAttachment.id;
        }
        if ((i & 2) != 0) {
            archiveAttachmentType = archiveAttachment.type;
        }
        if ((i & 4) != 0) {
            str2 = archiveAttachment.contentType;
        }
        if ((i & 8) != 0) {
            str3 = archiveAttachment.sourcePath;
        }
        if ((i & 16) != 0) {
            str4 = archiveAttachment.archivePath;
        }
        if ((i & 32) != 0) {
            messageAttachmentStatus = archiveAttachment.status;
        }
        if ((i & 64) != 0) {
            z = archiveAttachment.isViewOnce;
        }
        return archiveAttachment.copy(str, archiveAttachmentType, str2, str3, str4, messageAttachmentStatus, z);
    }

    @NotNull
    public String toString() {
        return "ArchiveAttachment(id=" + this.id + ", type=" + this.type + ", contentType=" + this.contentType + ", sourcePath=" + this.sourcePath + ", archivePath=" + this.archivePath + ", status=" + this.status + ", isViewOnce=" + this.isViewOnce + ')';
    }

    public int hashCode() {
        int result = this.id == null ? 0 : this.id.hashCode();
        return (((((((((((result * 31) + this.type.hashCode()) * 31) + (this.contentType == null ? 0 : this.contentType.hashCode())) * 31) + (this.sourcePath == null ? 0 : this.sourcePath.hashCode())) * 31) + (this.archivePath == null ? 0 : this.archivePath.hashCode())) * 31) + this.status.hashCode()) * 31) + Boolean.hashCode(this.isViewOnce);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ArchiveAttachment) {
            ArchiveAttachment archiveAttachment = (ArchiveAttachment) other;
            return Intrinsics.areEqual(this.id, archiveAttachment.id) && this.type == archiveAttachment.type && Intrinsics.areEqual(this.contentType, archiveAttachment.contentType) && Intrinsics.areEqual(this.sourcePath, archiveAttachment.sourcePath) && Intrinsics.areEqual(this.archivePath, archiveAttachment.archivePath) && this.status == archiveAttachment.status && this.isViewOnce == archiveAttachment.isViewOnce;
        }
        return false;
    }

    public ArchiveAttachment(@Nullable String id, @NotNull ArchiveAttachmentType type, @Nullable String contentType, @Nullable String sourcePath, @Nullable String archivePath, @NotNull MessageAttachmentStatus status, boolean isViewOnce) {
        Intrinsics.checkNotNullParameter(type, "type");
        Intrinsics.checkNotNullParameter(status, "status");
        this.id = id;
        this.type = type;
        this.contentType = contentType;
        this.sourcePath = sourcePath;
        this.archivePath = archivePath;
        this.status = status;
        this.isViewOnce = isViewOnce;
    }

    @Nullable
    public final String getId() {
        return this.id;
    }

    @NotNull
    public final ArchiveAttachmentType getType() {
        return this.type;
    }

    @Nullable
    public final String getContentType() {
        return this.contentType;
    }

    @Nullable
    public final String getSourcePath() {
        return this.sourcePath;
    }

    @Nullable
    public final String getArchivePath() {
        return this.archivePath;
    }

    @NotNull
    public final MessageAttachmentStatus getStatus() {
        return this.status;
    }

    public final boolean isViewOnce() {
        return this.isViewOnce;
    }
}
