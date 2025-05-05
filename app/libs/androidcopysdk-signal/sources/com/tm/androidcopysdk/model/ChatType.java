package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.DataGrabber;
import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ChatType.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��&\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n��\n\u0002\u0010\b\n��\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u000b\n��\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018�� \u00142\b\u0012\u0004\u0012\u00020��0\u0001:\u0001\u0014B\u0019\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0002\u0010\u0006J\u0006\u0010\u000b\u001a\u00020\fJ\u0006\u0010\r\u001a\u00020\u000eR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u0007\u0010\bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n��\u001a\u0004\b\t\u0010\nj\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013¨\u0006\u0015"}, d2 = {"Lcom/tm/androidcopysdk/model/ChatType;", "", "code", "", DBHeadersTable.HeadersEntry.COLUMN_NAME_KEY, "", "(Ljava/lang/String;IILjava/lang/String;)V", "getCode", "()I", "getKey", "()Ljava/lang/String;", "isNormalChat", "", "toChatMode", "Lcom/tm/androidcopysdk/DataGrabber$CHAT_MODE;", "Unknown", "Chat", "Group", "Broadcast", "Bot", "Companion", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ChatType.class */
public enum ChatType {
    Unknown(-1, null),
    Chat(0, null),
    Group(1, "chat group"),
    Broadcast(2, null),
    Bot(3, null);
    
    private final int code;
    @Nullable
    private final String key;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);
    @NotNull
    public static final Companion Companion = new Companion(null);

    /* compiled from: ChatType.kt */
    @Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK)
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ChatType$WhenMappings.class */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[ChatType.values().length];
            try {
                iArr[ChatType.Chat.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[ChatType.Group.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[ChatType.Broadcast.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    @NotNull
    public static EnumEntries<ChatType> getEntries() {
        return $ENTRIES;
    }

    ChatType(int code, String key) {
        this.code = code;
        this.key = key;
    }

    public final int getCode() {
        return this.code;
    }

    @Nullable
    public final String getKey() {
        return this.key;
    }

    public final boolean isNormalChat() {
        return this == Chat;
    }

    @NotNull
    public final DataGrabber.CHAT_MODE toChatMode() {
        switch (WhenMappings.$EnumSwitchMapping$0[ordinal()]) {
            case 1:
                return DataGrabber.CHAT_MODE.chat;
            case 2:
                return DataGrabber.CHAT_MODE.group;
            case 3:
                return DataGrabber.CHAT_MODE.broadcast;
            default:
                return DataGrabber.CHAT_MODE.chat;
        }
    }

    /* compiled from: ChatType.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0018\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\u0007"}, d2 = {"Lcom/tm/androidcopysdk/model/ChatType$Companion;", "", "()V", "fromChatMode", "Lcom/tm/androidcopysdk/model/ChatType;", "chatMode", "Lcom/tm/androidcopysdk/DataGrabber$CHAT_MODE;", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ChatType$Companion.class */
    public static final class Companion {

        /* compiled from: ChatType.kt */
        @Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK)
        /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ChatType$Companion$WhenMappings.class */
        public /* synthetic */ class WhenMappings {
            public static final /* synthetic */ int[] $EnumSwitchMapping$0;

            static {
                int[] iArr = new int[DataGrabber.CHAT_MODE.values().length];
                try {
                    iArr[DataGrabber.CHAT_MODE.chat.ordinal()] = 1;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[DataGrabber.CHAT_MODE.group.ordinal()] = 2;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[DataGrabber.CHAT_MODE.broadcast.ordinal()] = 3;
                } catch (NoSuchFieldError e3) {
                }
                $EnumSwitchMapping$0 = iArr;
            }
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final ChatType fromChatMode(@NotNull DataGrabber.CHAT_MODE chatMode) {
            Intrinsics.checkNotNullParameter(chatMode, "chatMode");
            switch (WhenMappings.$EnumSwitchMapping$0[chatMode.ordinal()]) {
                case 1:
                    return ChatType.Chat;
                case 2:
                    return ChatType.Group;
                case 3:
                    return ChatType.Broadcast;
                default:
                    throw new NoWhenBranchMatchedException();
            }
        }
    }
}
