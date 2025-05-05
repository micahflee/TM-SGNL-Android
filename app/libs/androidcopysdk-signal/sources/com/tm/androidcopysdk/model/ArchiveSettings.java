package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveSettings.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��2\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000b\n��\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\b\u0086\b\u0018��2\u00020\u0001BG\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003\u0012\b\b\u0002\u0010\n\u001a\u00020\u0003¢\u0006\u0002\u0010\u000bJ\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003JK\u0010\u0019\u001a\u00020��2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u001a\u001a\u00020\u00032\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001c\u001a\u00020\u001dHÖ\u0001J\u0016\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u0003J\t\u0010\"\u001a\u00020#HÖ\u0001R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n��\u001a\u0004\b\f\u0010\rR\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u0002\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\n\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u0012\u0010\u000f¨\u0006$"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveSettings;", "", "isAppActivated", "", "archivingSupportedChatTypes", "", "Lcom/tm/androidcopysdk/model/ChatType;", "secretChatsArchivingSupported", "deleteMessageArchivingSupported", "editMessageArchivingSupported", "supportCallArchiving", "(ZLjava/util/List;ZZZZ)V", "getArchivingSupportedChatTypes", "()Ljava/util/List;", "getDeleteMessageArchivingSupported", "()Z", "getEditMessageArchivingSupported", "getSecretChatsArchivingSupported", "getSupportCallArchiving", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "isArchivingSupported", "message", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", "isNewEdit", "toString", "", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveSettings.class */
public final class ArchiveSettings {
    private final boolean isAppActivated;
    @NotNull
    private final List<ChatType> archivingSupportedChatTypes;
    private final boolean secretChatsArchivingSupported;
    private final boolean deleteMessageArchivingSupported;
    private final boolean editMessageArchivingSupported;
    private final boolean supportCallArchiving;

    public final boolean component1() {
        return this.isAppActivated;
    }

    @NotNull
    public final List<ChatType> component2() {
        return this.archivingSupportedChatTypes;
    }

    public final boolean component3() {
        return this.secretChatsArchivingSupported;
    }

    public final boolean component4() {
        return this.deleteMessageArchivingSupported;
    }

    public final boolean component5() {
        return this.editMessageArchivingSupported;
    }

    public final boolean component6() {
        return this.supportCallArchiving;
    }

    @NotNull
    public final ArchiveSettings copy(boolean isAppActivated, @NotNull List<? extends ChatType> list, boolean secretChatsArchivingSupported, boolean deleteMessageArchivingSupported, boolean editMessageArchivingSupported, boolean supportCallArchiving) {
        Intrinsics.checkNotNullParameter(list, "archivingSupportedChatTypes");
        return new ArchiveSettings(isAppActivated, list, secretChatsArchivingSupported, deleteMessageArchivingSupported, editMessageArchivingSupported, supportCallArchiving);
    }

    public static /* synthetic */ ArchiveSettings copy$default(ArchiveSettings archiveSettings, boolean z, List list, boolean z2, boolean z3, boolean z4, boolean z5, int i, Object obj) {
        if ((i & 1) != 0) {
            z = archiveSettings.isAppActivated;
        }
        if ((i & 2) != 0) {
            list = archiveSettings.archivingSupportedChatTypes;
        }
        if ((i & 4) != 0) {
            z2 = archiveSettings.secretChatsArchivingSupported;
        }
        if ((i & 8) != 0) {
            z3 = archiveSettings.deleteMessageArchivingSupported;
        }
        if ((i & 16) != 0) {
            z4 = archiveSettings.editMessageArchivingSupported;
        }
        if ((i & 32) != 0) {
            z5 = archiveSettings.supportCallArchiving;
        }
        return archiveSettings.copy(z, list, z2, z3, z4, z5);
    }

    @NotNull
    public String toString() {
        return "ArchiveSettings(isAppActivated=" + this.isAppActivated + ", archivingSupportedChatTypes=" + this.archivingSupportedChatTypes + ", secretChatsArchivingSupported=" + this.secretChatsArchivingSupported + ", deleteMessageArchivingSupported=" + this.deleteMessageArchivingSupported + ", editMessageArchivingSupported=" + this.editMessageArchivingSupported + ", supportCallArchiving=" + this.supportCallArchiving + ')';
    }

    public int hashCode() {
        int result = Boolean.hashCode(this.isAppActivated);
        return (((((((((result * 31) + this.archivingSupportedChatTypes.hashCode()) * 31) + Boolean.hashCode(this.secretChatsArchivingSupported)) * 31) + Boolean.hashCode(this.deleteMessageArchivingSupported)) * 31) + Boolean.hashCode(this.editMessageArchivingSupported)) * 31) + Boolean.hashCode(this.supportCallArchiving);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ArchiveSettings) {
            ArchiveSettings archiveSettings = (ArchiveSettings) other;
            return this.isAppActivated == archiveSettings.isAppActivated && Intrinsics.areEqual(this.archivingSupportedChatTypes, archiveSettings.archivingSupportedChatTypes) && this.secretChatsArchivingSupported == archiveSettings.secretChatsArchivingSupported && this.deleteMessageArchivingSupported == archiveSettings.deleteMessageArchivingSupported && this.editMessageArchivingSupported == archiveSettings.editMessageArchivingSupported && this.supportCallArchiving == archiveSettings.supportCallArchiving;
        }
        return false;
    }

    public ArchiveSettings() {
        this(false, null, false, false, false, false, 63, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public ArchiveSettings(boolean isAppActivated, @NotNull List<? extends ChatType> list, boolean secretChatsArchivingSupported, boolean deleteMessageArchivingSupported, boolean editMessageArchivingSupported, boolean supportCallArchiving) {
        Intrinsics.checkNotNullParameter(list, "archivingSupportedChatTypes");
        this.isAppActivated = isAppActivated;
        this.archivingSupportedChatTypes = list;
        this.secretChatsArchivingSupported = secretChatsArchivingSupported;
        this.deleteMessageArchivingSupported = deleteMessageArchivingSupported;
        this.editMessageArchivingSupported = editMessageArchivingSupported;
        this.supportCallArchiving = supportCallArchiving;
    }

    public /* synthetic */ ArchiveSettings(boolean z, List list, boolean z2, boolean z3, boolean z4, boolean z5, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? false : z, (i & 2) != 0 ? ArraysKt.toList(ChatType.values()) : list, (i & 4) != 0 ? true : z2, (i & 8) != 0 ? true : z3, (i & 16) != 0 ? true : z4, (i & 32) != 0 ? true : z5);
    }

    public final boolean isAppActivated() {
        return this.isAppActivated;
    }

    @NotNull
    public final List<ChatType> getArchivingSupportedChatTypes() {
        return this.archivingSupportedChatTypes;
    }

    public final boolean getSecretChatsArchivingSupported() {
        return this.secretChatsArchivingSupported;
    }

    public final boolean getDeleteMessageArchivingSupported() {
        return this.deleteMessageArchivingSupported;
    }

    public final boolean getEditMessageArchivingSupported() {
        return this.editMessageArchivingSupported;
    }

    public final boolean getSupportCallArchiving() {
        return this.supportCallArchiving;
    }

    public final boolean isArchivingSupported(@NotNull ArchiveMessage message, boolean isNewEdit) {
        Intrinsics.checkNotNullParameter(message, "message");
        if (message.hasDeletions() && !this.deleteMessageArchivingSupported) {
            return false;
        }
        if (message.getType() == ArchiveMessageType.Call && !this.supportCallArchiving) {
            return false;
        }
        if (((message.isEdited() || isNewEdit) && !this.editMessageArchivingSupported) || !this.archivingSupportedChatTypes.contains(message.getChat().getType())) {
            return false;
        }
        if (message.getChat().isSecret() && !this.secretChatsArchivingSupported) {
            return false;
        }
        return true;
    }
}
