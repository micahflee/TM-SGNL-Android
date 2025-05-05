package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveMessageEdit.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��.\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018��2\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\u0002\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006HÆ\u0003J/\u0010\u0011\u001a\u00020��2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006HÆ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0016HÖ\u0001J\t\u0010\u0017\u001a\u00020\u0003HÖ\u0001R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\b\n��\u001a\u0004\b\t\u0010\nR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\r\u0010\f¨\u0006\u0018"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveMessageEdit;", "", "id", "", "body", "attachments", "", "Lcom/tm/androidcopysdk/model/ArchiveAttachment;", "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;)V", "getAttachments", "()Ljava/util/List;", "getBody", "()Ljava/lang/String;", "getId", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveMessageEdit.class */
public final class ArchiveMessageEdit {
    @NotNull
    private final String id;
    @Nullable
    private final String body;
    @NotNull
    private final List<ArchiveAttachment> attachments;

    @NotNull
    public final String component1() {
        return this.id;
    }

    @Nullable
    public final String component2() {
        return this.body;
    }

    @NotNull
    public final List<ArchiveAttachment> component3() {
        return this.attachments;
    }

    @NotNull
    public final ArchiveMessageEdit copy(@NotNull String id, @Nullable String body, @NotNull List<ArchiveAttachment> list) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(list, "attachments");
        return new ArchiveMessageEdit(id, body, list);
    }

    public static /* synthetic */ ArchiveMessageEdit copy$default(ArchiveMessageEdit archiveMessageEdit, String str, String str2, List list, int i, Object obj) {
        if ((i & 1) != 0) {
            str = archiveMessageEdit.id;
        }
        if ((i & 2) != 0) {
            str2 = archiveMessageEdit.body;
        }
        if ((i & 4) != 0) {
            list = archiveMessageEdit.attachments;
        }
        return archiveMessageEdit.copy(str, str2, list);
    }

    @NotNull
    public String toString() {
        return "ArchiveMessageEdit(id=" + this.id + ", body=" + this.body + ", attachments=" + this.attachments + ')';
    }

    public int hashCode() {
        int result = this.id.hashCode();
        return (((result * 31) + (this.body == null ? 0 : this.body.hashCode())) * 31) + this.attachments.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ArchiveMessageEdit) {
            ArchiveMessageEdit archiveMessageEdit = (ArchiveMessageEdit) other;
            return Intrinsics.areEqual(this.id, archiveMessageEdit.id) && Intrinsics.areEqual(this.body, archiveMessageEdit.body) && Intrinsics.areEqual(this.attachments, archiveMessageEdit.attachments);
        }
        return false;
    }

    public ArchiveMessageEdit(@NotNull String id, @Nullable String body, @NotNull List<ArchiveAttachment> list) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(list, "attachments");
        this.id = id;
        this.body = body;
        this.attachments = list;
    }

    @NotNull
    public final String getId() {
        return this.id;
    }

    @Nullable
    public final String getBody() {
        return this.body;
    }

    @NotNull
    public final List<ArchiveAttachment> getAttachments() {
        return this.attachments;
    }
}
