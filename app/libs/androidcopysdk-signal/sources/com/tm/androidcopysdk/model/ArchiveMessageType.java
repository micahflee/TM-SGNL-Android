package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.MessageType;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveMessageType.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\t\b\u0086\u0081\u0002\u0018�� \t2\b\u0012\u0004\u0012\u00020��0\u0001:\u0001\tB\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b¨\u0006\n"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveMessageType;", "", "(Ljava/lang/String;I)V", "Sms", "Mms", "Event", "Call", "Story", "Unknown", "Companion", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveMessageType.class */
public enum ArchiveMessageType {
    Sms,
    Mms,
    Event,
    Call,
    Story,
    Unknown;
    
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);
    @NotNull
    public static final Companion Companion = new Companion(null);

    @NotNull
    public static EnumEntries<ArchiveMessageType> getEntries() {
        return $ENTRIES;
    }

    /* compiled from: ArchiveMessageType.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0018\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006¨\u0006\u0007"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveMessageType$Companion;", "", "()V", "fromMessageTypeOrUnknown", "Lcom/tm/androidcopysdk/model/ArchiveMessageType;", "type", "Lcom/tm/androidcopysdk/MessageType;", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveMessageType$Companion.class */
    public static final class Companion {

        /* compiled from: ArchiveMessageType.kt */
        @Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK)
        /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveMessageType$Companion$WhenMappings.class */
        public /* synthetic */ class WhenMappings {
            public static final /* synthetic */ int[] $EnumSwitchMapping$0;

            static {
                int[] iArr = new int[MessageType.values().length];
                try {
                    iArr[MessageType.SMS.ordinal()] = 1;
                } catch (NoSuchFieldError e) {
                }
                try {
                    iArr[MessageType.MMS.ordinal()] = 2;
                } catch (NoSuchFieldError e2) {
                }
                try {
                    iArr[MessageType.CallLog.ordinal()] = 3;
                } catch (NoSuchFieldError e3) {
                }
                try {
                    iArr[MessageType.CallAudio.ordinal()] = 4;
                } catch (NoSuchFieldError e4) {
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
        public final ArchiveMessageType fromMessageTypeOrUnknown(@Nullable MessageType type) {
            switch (type == null ? -1 : WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
                case 1:
                    return ArchiveMessageType.Sms;
                case 2:
                    return ArchiveMessageType.Mms;
                case 3:
                case 4:
                    return ArchiveMessageType.Call;
                default:
                    return ArchiveMessageType.Unknown;
            }
        }
    }
}
