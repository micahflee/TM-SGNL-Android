package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
/* compiled from: CallRtcMode.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n��\n\u0002\u0010\b\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018�� \t2\b\u0012\u0004\u0012\u00020��0\u0001:\u0001\tB\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\b¨\u0006\n"}, d2 = {"Lcom/tm/androidcopysdk/model/CallRtcMode;", "", "code", "", "(Ljava/lang/String;II)V", "getCode", "()I", "Voice", "Video", "Companion", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/CallRtcMode.class */
public enum CallRtcMode {
    Voice(0),
    Video(1);
    
    private final int code;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);
    @NotNull
    public static final Companion Companion = new Companion(null);

    @NotNull
    public static EnumEntries<CallRtcMode> getEntries() {
        return $ENTRIES;
    }

    CallRtcMode(int code) {
        this.code = code;
    }

    public final int getCode() {
        return this.code;
    }

    /* compiled from: CallRtcMode.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0018\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000b\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006¨\u0006\u0007"}, d2 = {"Lcom/tm/androidcopysdk/model/CallRtcMode$Companion;", "", "()V", "fromIsVideo", "Lcom/tm/androidcopysdk/model/CallRtcMode;", "isVideo", "", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/CallRtcMode$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final CallRtcMode fromIsVideo(boolean isVideo) {
            return isVideo ? CallRtcMode.Video : CallRtcMode.Voice;
        }
    }
}
