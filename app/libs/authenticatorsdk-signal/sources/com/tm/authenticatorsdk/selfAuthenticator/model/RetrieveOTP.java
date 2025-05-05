package com.tm.authenticatorsdk.selfAuthenticator.model;

import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: RetrieveOTP.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"�� \n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n��\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018��2\u00020\u0001B#\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0005¢\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0005HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0005HÆ\u0003J'\u0010\u0010\u001a\u00020��2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0014\u001a\u00020\u0005HÖ\u0001J\t\u0010\u0015\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n��\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n��\u001a\u0004\b\f\u0010\t¨\u0006\u0016"}, d2 = {"Lcom/tm/authenticatorsdk/selfAuthenticator/model/RetrieveOTP;", "", "mobile", "", "OPTSendingType", "", "retrieveMode", "(Ljava/lang/String;II)V", "getOPTSendingType", "()I", "getMobile", "()Ljava/lang/String;", "getRetrieveMode", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/model/RetrieveOTP.class */
public final class RetrieveOTP {
    @NotNull
    private final String mobile;
    private final int OPTSendingType;
    private final int retrieveMode;

    @NotNull
    public final String component1() {
        return this.mobile;
    }

    public final int component2() {
        return this.OPTSendingType;
    }

    public final int component3() {
        return this.retrieveMode;
    }

    @NotNull
    public final RetrieveOTP copy(@NotNull String mobile, int OPTSendingType, int retrieveMode) {
        Intrinsics.checkNotNullParameter(mobile, "mobile");
        return new RetrieveOTP(mobile, OPTSendingType, retrieveMode);
    }

    public static /* synthetic */ RetrieveOTP copy$default(RetrieveOTP retrieveOTP, String str, int i, int i2, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            str = retrieveOTP.mobile;
        }
        if ((i3 & 2) != 0) {
            i = retrieveOTP.OPTSendingType;
        }
        if ((i3 & 4) != 0) {
            i2 = retrieveOTP.retrieveMode;
        }
        return retrieveOTP.copy(str, i, i2);
    }

    @NotNull
    public String toString() {
        return "RetrieveOTP(mobile=" + this.mobile + ", OPTSendingType=" + this.OPTSendingType + ", retrieveMode=" + this.retrieveMode + ')';
    }

    public int hashCode() {
        int result = this.mobile.hashCode();
        return (((result * 31) + Integer.hashCode(this.OPTSendingType)) * 31) + Integer.hashCode(this.retrieveMode);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof RetrieveOTP) {
            RetrieveOTP retrieveOTP = (RetrieveOTP) other;
            return Intrinsics.areEqual(this.mobile, retrieveOTP.mobile) && this.OPTSendingType == retrieveOTP.OPTSendingType && this.retrieveMode == retrieveOTP.retrieveMode;
        }
        return false;
    }

    public RetrieveOTP() {
        this(null, 0, 0, 7, null);
    }

    public RetrieveOTP(@NotNull String mobile, int OPTSendingType, int retrieveMode) {
        Intrinsics.checkNotNullParameter(mobile, "mobile");
        this.mobile = mobile;
        this.OPTSendingType = OPTSendingType;
        this.retrieveMode = retrieveMode;
    }

    public /* synthetic */ RetrieveOTP(String str, int i, int i2, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this((i3 & 1) != 0 ? "" : str, (i3 & 2) != 0 ? 0 : i, (i3 & 4) != 0 ? 0 : i2);
    }

    @NotNull
    public final String getMobile() {
        return this.mobile;
    }

    public final int getOPTSendingType() {
        return this.OPTSendingType;
    }

    public final int getRetrieveMode() {
        return this.retrieveMode;
    }
}
