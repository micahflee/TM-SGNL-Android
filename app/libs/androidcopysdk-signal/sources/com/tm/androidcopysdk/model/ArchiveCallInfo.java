package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveCallInfo.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��>\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n��\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\b\u0018�� -2\u00020\u0001:\u0001-B=\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n¢\u0006\u0002\u0010\u000bJ\u000b\u0010\u001d\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\u0010\u0011J\u0010\u0010\u001f\u001a\u0004\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\u0010\u0011J\u000b\u0010 \u001a\u0004\u0018\u00010\bHÆ\u0003J\t\u0010!\u001a\u00020\nHÆ\u0003JH\u0010\"\u001a\u00020��2\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\nHÆ\u0001¢\u0006\u0002\u0010#J\u0013\u0010$\u001a\u00020%2\b\u0010&\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010'\u001a\u00020(HÖ\u0001J\t\u0010)\u001a\u00020\u0003HÖ\u0001J\u000e\u0010*\u001a\u00020(2\u0006\u0010+\u001a\u00020,R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000fR\u001e\u0010\u0006\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u0010\n\u0002\u0010\u0014\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u0015\u0010\u0016R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u001e\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u0010\n\u0002\u0010\u0014\u001a\u0004\b\u001b\u0010\u0011\"\u0004\b\u001c\u0010\u0013¨\u0006."}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveCallInfo;", "", "id", "", "startedAt", "", "durationMs", "answerType", "Lcom/tm/androidcopysdk/model/CallAnswerType;", "rtcMode", "Lcom/tm/androidcopysdk/model/CallRtcMode;", "(Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Long;Lcom/tm/androidcopysdk/model/CallAnswerType;Lcom/tm/androidcopysdk/model/CallRtcMode;)V", "getAnswerType", "()Lcom/tm/androidcopysdk/model/CallAnswerType;", "setAnswerType", "(Lcom/tm/androidcopysdk/model/CallAnswerType;)V", "getDurationMs", "()Ljava/lang/Long;", "setDurationMs", "(Ljava/lang/Long;)V", "Ljava/lang/Long;", "getId", "()Ljava/lang/String;", "getRtcMode", "()Lcom/tm/androidcopysdk/model/CallRtcMode;", "setRtcMode", "(Lcom/tm/androidcopysdk/model/CallRtcMode;)V", "getStartedAt", "setStartedAt", "component1", "component2", "component3", "component4", "component5", "copy", "(Ljava/lang/String;Ljava/lang/Long;Ljava/lang/Long;Lcom/tm/androidcopysdk/model/CallAnswerType;Lcom/tm/androidcopysdk/model/CallRtcMode;)Lcom/tm/androidcopysdk/model/ArchiveCallInfo;", "equals", "", "other", "hashCode", "", "toString", "type", "direction", "Lcom/tm/androidcopysdk/model/Direction;", "Companion", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nArchiveCallInfo.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveCallInfo.kt\ncom/tm/androidcopysdk/model/ArchiveCallInfo\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,35:1\n1#2:36\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveCallInfo.class */
public final class ArchiveCallInfo {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @Nullable
    private final String id;
    @Nullable
    private Long startedAt;
    @Nullable
    private Long durationMs;
    @Nullable
    private CallAnswerType answerType;
    @NotNull
    private CallRtcMode rtcMode;
    public static final int INCOMING_TYPE = 1;
    public static final int OUTGOING_TYPE = 2;
    public static final int DISCARD_REASON_MISSED = 3;
    public static final int DISCARD_REASON_LINE_BUSY = 4;
    public static final int DISCARD_REASON_REJECTED = 5;
    public static final int MISSED_CALL_OUTGOING_TYPE = 8;
    public static final int REJECTED_TYPE_OUTGOING_TYPE = 9;

    @Nullable
    public final String component1() {
        return this.id;
    }

    @Nullable
    public final Long component2() {
        return this.startedAt;
    }

    @Nullable
    public final Long component3() {
        return this.durationMs;
    }

    @Nullable
    public final CallAnswerType component4() {
        return this.answerType;
    }

    @NotNull
    public final CallRtcMode component5() {
        return this.rtcMode;
    }

    @NotNull
    public final ArchiveCallInfo copy(@Nullable String id, @Nullable Long startedAt, @Nullable Long durationMs, @Nullable CallAnswerType answerType, @NotNull CallRtcMode rtcMode) {
        Intrinsics.checkNotNullParameter(rtcMode, "rtcMode");
        return new ArchiveCallInfo(id, startedAt, durationMs, answerType, rtcMode);
    }

    public static /* synthetic */ ArchiveCallInfo copy$default(ArchiveCallInfo archiveCallInfo, String str, Long l, Long l2, CallAnswerType callAnswerType, CallRtcMode callRtcMode, int i, Object obj) {
        if ((i & 1) != 0) {
            str = archiveCallInfo.id;
        }
        if ((i & 2) != 0) {
            l = archiveCallInfo.startedAt;
        }
        if ((i & 4) != 0) {
            l2 = archiveCallInfo.durationMs;
        }
        if ((i & 8) != 0) {
            callAnswerType = archiveCallInfo.answerType;
        }
        if ((i & 16) != 0) {
            callRtcMode = archiveCallInfo.rtcMode;
        }
        return archiveCallInfo.copy(str, l, l2, callAnswerType, callRtcMode);
    }

    @NotNull
    public String toString() {
        return "ArchiveCallInfo(id=" + this.id + ", startedAt=" + this.startedAt + ", durationMs=" + this.durationMs + ", answerType=" + this.answerType + ", rtcMode=" + this.rtcMode + ')';
    }

    public int hashCode() {
        int result = this.id == null ? 0 : this.id.hashCode();
        return (((((((result * 31) + (this.startedAt == null ? 0 : this.startedAt.hashCode())) * 31) + (this.durationMs == null ? 0 : this.durationMs.hashCode())) * 31) + (this.answerType == null ? 0 : this.answerType.hashCode())) * 31) + this.rtcMode.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ArchiveCallInfo) {
            ArchiveCallInfo archiveCallInfo = (ArchiveCallInfo) other;
            return Intrinsics.areEqual(this.id, archiveCallInfo.id) && Intrinsics.areEqual(this.startedAt, archiveCallInfo.startedAt) && Intrinsics.areEqual(this.durationMs, archiveCallInfo.durationMs) && this.answerType == archiveCallInfo.answerType && this.rtcMode == archiveCallInfo.rtcMode;
        }
        return false;
    }

    public ArchiveCallInfo(@Nullable String id, @Nullable Long startedAt, @Nullable Long durationMs, @Nullable CallAnswerType answerType, @NotNull CallRtcMode rtcMode) {
        Intrinsics.checkNotNullParameter(rtcMode, "rtcMode");
        this.id = id;
        this.startedAt = startedAt;
        this.durationMs = durationMs;
        this.answerType = answerType;
        this.rtcMode = rtcMode;
    }

    public /* synthetic */ ArchiveCallInfo(String str, Long l, Long l2, CallAnswerType callAnswerType, CallRtcMode callRtcMode, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, (i & 2) != 0 ? null : l, (i & 4) != 0 ? null : l2, (i & 8) != 0 ? null : callAnswerType, (i & 16) != 0 ? CallRtcMode.Voice : callRtcMode);
    }

    @Nullable
    public final String getId() {
        return this.id;
    }

    @Nullable
    public final Long getStartedAt() {
        return this.startedAt;
    }

    public final void setStartedAt(@Nullable Long l) {
        this.startedAt = l;
    }

    @Nullable
    public final Long getDurationMs() {
        return this.durationMs;
    }

    public final void setDurationMs(@Nullable Long l) {
        this.durationMs = l;
    }

    @Nullable
    public final CallAnswerType getAnswerType() {
        return this.answerType;
    }

    public final void setAnswerType(@Nullable CallAnswerType callAnswerType) {
        this.answerType = callAnswerType;
    }

    @NotNull
    public final CallRtcMode getRtcMode() {
        return this.rtcMode;
    }

    public final void setRtcMode(@NotNull CallRtcMode callRtcMode) {
        Intrinsics.checkNotNullParameter(callRtcMode, "<set-?>");
        this.rtcMode = callRtcMode;
    }

    public final int type(@NotNull Direction direction) {
        Intrinsics.checkNotNullParameter(direction, "direction");
        boolean isOutgoing = direction == Direction.Outgoing;
        Long it = this.durationMs;
        return it != null && (it.longValue() > 0L ? 1 : (it.longValue() == 0L ? 0 : -1)) > 0 ? isOutgoing ? 2 : 1 : isOutgoing ? this.answerType == CallAnswerType.Missed ? 8 : 9 : this.answerType == CallAnswerType.Missed ? 3 : 5;
    }

    /* compiled from: ArchiveCallInfo.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0014\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\b\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\t\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\n\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��¨\u0006\u000b"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveCallInfo$Companion;", "", "()V", "DISCARD_REASON_LINE_BUSY", "", "DISCARD_REASON_MISSED", "DISCARD_REASON_REJECTED", "INCOMING_TYPE", "MISSED_CALL_OUTGOING_TYPE", "OUTGOING_TYPE", "REJECTED_TYPE_OUTGOING_TYPE", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveCallInfo$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }
    }
}
