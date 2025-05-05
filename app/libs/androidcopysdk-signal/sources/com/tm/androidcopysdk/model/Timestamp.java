package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.DefinitionsSDKKt;
import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: Timestamp.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��.\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\t\n��\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\b\u0086\b\u0018��2\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\t\u0010\f\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\r\u001a\u00020��2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0011\u001a\u00020\u0012HÖ\u0001J\u0006\u0010\u0013\u001a\u00020\u0003J\t\u0010\u0014\u001a\u00020\u0015HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n��\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\t\u0010\n¨\u0006\u0016"}, d2 = {"Lcom/tm/androidcopysdk/model/Timestamp;", "", DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE, "", "unit", "Lcom/tm/androidcopysdk/model/TimeUnit;", "(JLcom/tm/androidcopysdk/model/TimeUnit;)V", "getUnit", "()Lcom/tm/androidcopysdk/model/TimeUnit;", "getValue", "()J", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "milliseconds", "toString", "", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/Timestamp.class */
public final class Timestamp {
    private final long value;
    @NotNull
    private final TimeUnit unit;

    /* compiled from: Timestamp.kt */
    @Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK)
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/Timestamp$WhenMappings.class */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[TimeUnit.values().length];
            try {
                iArr[TimeUnit.Seconds.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[TimeUnit.MilliSeconds.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public final long component1() {
        return this.value;
    }

    @NotNull
    public final TimeUnit component2() {
        return this.unit;
    }

    @NotNull
    public final Timestamp copy(long value, @NotNull TimeUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        return new Timestamp(value, unit);
    }

    public static /* synthetic */ Timestamp copy$default(Timestamp timestamp, long j, TimeUnit timeUnit, int i, Object obj) {
        if ((i & 1) != 0) {
            j = timestamp.value;
        }
        if ((i & 2) != 0) {
            timeUnit = timestamp.unit;
        }
        return timestamp.copy(j, timeUnit);
    }

    @NotNull
    public String toString() {
        return "Timestamp(value=" + this.value + ", unit=" + this.unit + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.value);
        return (result * 31) + this.unit.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) other;
            return this.value == timestamp.value && this.unit == timestamp.unit;
        }
        return false;
    }

    public Timestamp(long value, @NotNull TimeUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        this.value = value;
        this.unit = unit;
    }

    public /* synthetic */ Timestamp(long j, TimeUnit timeUnit, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(j, (i & 2) != 0 ? TimeUnit.MilliSeconds : timeUnit);
    }

    public final long getValue() {
        return this.value;
    }

    @NotNull
    public final TimeUnit getUnit() {
        return this.unit;
    }

    public final long milliseconds() {
        switch (WhenMappings.$EnumSwitchMapping$0[this.unit.ordinal()]) {
            case 1:
                return this.value * ((long) DefinitionsSDKKt.ID_BASE);
            case 2:
                return this.value;
            default:
                throw new NoWhenBranchMatchedException();
        }
    }
}
