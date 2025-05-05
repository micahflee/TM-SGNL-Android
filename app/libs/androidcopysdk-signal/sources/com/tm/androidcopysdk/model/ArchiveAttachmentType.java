package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveAttachmentType.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u000e\b\u0086\u0081\u0002\u0018�� \u000e2\b\u0012\u0004\u0012\u00020��0\u0001:\u0001\u000eB\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\r¨\u0006\u000f"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveAttachmentType;", "", "(Ljava/lang/String;I)V", "Unknown", "Audio", "Document", "Gif", "Image", "Location", "Mms", "Sticker", "Text", "Video", "VCard", "Companion", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveAttachmentType.class */
public enum ArchiveAttachmentType {
    Unknown,
    Audio,
    Document,
    Gif,
    Image,
    Location,
    Mms,
    Sticker,
    Text,
    Video,
    VCard;
    
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);
    @NotNull
    public static final Companion Companion = new Companion(null);

    @NotNull
    public static EnumEntries<ArchiveAttachmentType> getEntries() {
        return $ENTRIES;
    }

    /* compiled from: ArchiveAttachmentType.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0018\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006¨\u0006\u0007"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveAttachmentType$Companion;", "", "()V", "fromMimeTypeOrUnknown", "Lcom/tm/androidcopysdk/model/ArchiveAttachmentType;", "mimeType", "", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveAttachmentType$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final ArchiveAttachmentType fromMimeTypeOrUnknown(@Nullable String mimeType) {
            return ArchiveAttachmentType.Unknown;
        }
    }
}
