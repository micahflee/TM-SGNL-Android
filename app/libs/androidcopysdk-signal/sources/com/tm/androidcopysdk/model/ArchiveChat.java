package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveChat.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��(\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018��2\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\bHÆ\u0003J3\u0010\u0014\u001a\u00020��2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bHÆ\u0001J\u0013\u0010\u0015\u001a\u00020\b2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0017\u001a\u00020\u0018HÖ\u0001J\t\u0010\u0019\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n��\u001a\u0004\b\u0007\u0010\fR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n��\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001a"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveChat;", "", "id", "", "type", "Lcom/tm/androidcopysdk/model/ChatType;", "name", "isSecret", "", "(Ljava/lang/String;Lcom/tm/androidcopysdk/model/ChatType;Ljava/lang/String;Z)V", "getId", "()Ljava/lang/String;", "()Z", "getName", "getType", "()Lcom/tm/androidcopysdk/model/ChatType;", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveChat.class */
public final class ArchiveChat {
    @NotNull
    private final String id;
    @NotNull
    private final ChatType type;
    @Nullable
    private final String name;
    private final boolean isSecret;

    @NotNull
    public final String component1() {
        return this.id;
    }

    @NotNull
    public final ChatType component2() {
        return this.type;
    }

    @Nullable
    public final String component3() {
        return this.name;
    }

    public final boolean component4() {
        return this.isSecret;
    }

    @NotNull
    public final ArchiveChat copy(@NotNull String id, @NotNull ChatType type, @Nullable String name, boolean isSecret) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(type, "type");
        return new ArchiveChat(id, type, name, isSecret);
    }

    public static /* synthetic */ ArchiveChat copy$default(ArchiveChat archiveChat, String str, ChatType chatType, String str2, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            str = archiveChat.id;
        }
        if ((i & 2) != 0) {
            chatType = archiveChat.type;
        }
        if ((i & 4) != 0) {
            str2 = archiveChat.name;
        }
        if ((i & 8) != 0) {
            z = archiveChat.isSecret;
        }
        return archiveChat.copy(str, chatType, str2, z);
    }

    @NotNull
    public String toString() {
        return "ArchiveChat(id=" + this.id + ", type=" + this.type + ", name=" + this.name + ", isSecret=" + this.isSecret + ')';
    }

    public int hashCode() {
        int result = this.id.hashCode();
        return (((((result * 31) + this.type.hashCode()) * 31) + (this.name == null ? 0 : this.name.hashCode())) * 31) + Boolean.hashCode(this.isSecret);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ArchiveChat) {
            ArchiveChat archiveChat = (ArchiveChat) other;
            return Intrinsics.areEqual(this.id, archiveChat.id) && this.type == archiveChat.type && Intrinsics.areEqual(this.name, archiveChat.name) && this.isSecret == archiveChat.isSecret;
        }
        return false;
    }

    public ArchiveChat(@NotNull String id, @NotNull ChatType type, @Nullable String name, boolean isSecret) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(type, "type");
        this.id = id;
        this.type = type;
        this.name = name;
        this.isSecret = isSecret;
    }

    @NotNull
    public final String getId() {
        return this.id;
    }

    @NotNull
    public final ChatType getType() {
        return this.type;
    }

    @Nullable
    public final String getName() {
        return this.name;
    }

    public final boolean isSecret() {
        return this.isSecret;
    }
}
