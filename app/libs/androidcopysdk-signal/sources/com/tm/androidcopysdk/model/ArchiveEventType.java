package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;
/* compiled from: ArchiveEventType.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\n\b\u0086\u0081\u0002\u0018��2\b\u0012\u0004\u0012\u00020��0\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0003\u001a\u00020\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\r¨\u0006\u000e"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveEventType;", "", "(Ljava/lang/String;I)V", "isDeleteAll", "", "DeleteChat", "ClearChat", "Reaction", "Join", "Leave", "Remove", "Create", "Rename", "ChangeProfileImage", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveEventType.class */
public enum ArchiveEventType {
    DeleteChat,
    ClearChat,
    Reaction,
    Join,
    Leave,
    Remove,
    Create,
    Rename,
    ChangeProfileImage;
    
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

    @NotNull
    public static EnumEntries<ArchiveEventType> getEntries() {
        return $ENTRIES;
    }

    public final boolean isDeleteAll() {
        return DeleteChat == this || ClearChat == this;
    }
}
