package com.tm.androidcopysdk.version_aoutomatic_rollout;

import com.google.gson.annotations.SerializedName;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: VersionAutomaticRollOutModel.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��&\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\b\n��\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018��2\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007¢\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0007HÆ\u0003J1\u0010\u0015\u001a\u00020��2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007HÆ\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0019\u001a\u00020\u0005HÖ\u0001J\t\u0010\u001a\u001a\u00020\u0007HÖ\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n��\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\b\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n��\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n��\u001a\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n��\u001a\u0004\b\u0010\u0010\r¨\u0006\u001b"}, d2 = {"Lcom/tm/androidcopysdk/version_aoutomatic_rollout/VersionAutomaticRollOutModel;", "", "applicationVersion", "Lcom/tm/androidcopysdk/version_aoutomatic_rollout/ApplicationVersion;", "resultCode", "", "resultDescription", "", "className", "(Lcom/tm/androidcopysdk/version_aoutomatic_rollout/ApplicationVersion;ILjava/lang/String;Ljava/lang/String;)V", "getApplicationVersion", "()Lcom/tm/androidcopysdk/version_aoutomatic_rollout/ApplicationVersion;", "getClassName", "()Ljava/lang/String;", "getResultCode", "()I", "getResultDescription", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/version_aoutomatic_rollout/VersionAutomaticRollOutModel.class */
public final class VersionAutomaticRollOutModel {
    @SerializedName("applicationVersion")
    @NotNull
    private final ApplicationVersion applicationVersion;
    @SerializedName("resultCode")
    private final int resultCode;
    @SerializedName("resultDescription")
    @NotNull
    private final String resultDescription;
    @SerializedName("class")
    @NotNull
    private final String className;

    @NotNull
    public final ApplicationVersion component1() {
        return this.applicationVersion;
    }

    public final int component2() {
        return this.resultCode;
    }

    @NotNull
    public final String component3() {
        return this.resultDescription;
    }

    @NotNull
    public final String component4() {
        return this.className;
    }

    @NotNull
    public final VersionAutomaticRollOutModel copy(@NotNull ApplicationVersion applicationVersion, int resultCode, @NotNull String resultDescription, @NotNull String className) {
        Intrinsics.checkNotNullParameter(applicationVersion, "applicationVersion");
        Intrinsics.checkNotNullParameter(resultDescription, "resultDescription");
        Intrinsics.checkNotNullParameter(className, "className");
        return new VersionAutomaticRollOutModel(applicationVersion, resultCode, resultDescription, className);
    }

    public static /* synthetic */ VersionAutomaticRollOutModel copy$default(VersionAutomaticRollOutModel versionAutomaticRollOutModel, ApplicationVersion applicationVersion, int i, String str, String str2, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            applicationVersion = versionAutomaticRollOutModel.applicationVersion;
        }
        if ((i2 & 2) != 0) {
            i = versionAutomaticRollOutModel.resultCode;
        }
        if ((i2 & 4) != 0) {
            str = versionAutomaticRollOutModel.resultDescription;
        }
        if ((i2 & 8) != 0) {
            str2 = versionAutomaticRollOutModel.className;
        }
        return versionAutomaticRollOutModel.copy(applicationVersion, i, str, str2);
    }

    @NotNull
    public String toString() {
        return "VersionAutomaticRollOutModel(applicationVersion=" + this.applicationVersion + ", resultCode=" + this.resultCode + ", resultDescription=" + this.resultDescription + ", className=" + this.className + ')';
    }

    public int hashCode() {
        int result = this.applicationVersion.hashCode();
        return (((((result * 31) + Integer.hashCode(this.resultCode)) * 31) + this.resultDescription.hashCode()) * 31) + this.className.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof VersionAutomaticRollOutModel) {
            VersionAutomaticRollOutModel versionAutomaticRollOutModel = (VersionAutomaticRollOutModel) other;
            return Intrinsics.areEqual(this.applicationVersion, versionAutomaticRollOutModel.applicationVersion) && this.resultCode == versionAutomaticRollOutModel.resultCode && Intrinsics.areEqual(this.resultDescription, versionAutomaticRollOutModel.resultDescription) && Intrinsics.areEqual(this.className, versionAutomaticRollOutModel.className);
        }
        return false;
    }

    public VersionAutomaticRollOutModel(@NotNull ApplicationVersion applicationVersion, int resultCode, @NotNull String resultDescription, @NotNull String className) {
        Intrinsics.checkNotNullParameter(applicationVersion, "applicationVersion");
        Intrinsics.checkNotNullParameter(resultDescription, "resultDescription");
        Intrinsics.checkNotNullParameter(className, "className");
        this.applicationVersion = applicationVersion;
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
        this.className = className;
    }

    public /* synthetic */ VersionAutomaticRollOutModel(ApplicationVersion applicationVersion, int i, String str, String str2, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(applicationVersion, (i2 & 2) != 0 ? 0 : i, (i2 & 4) != 0 ? "" : str, (i2 & 8) != 0 ? "" : str2);
    }

    @NotNull
    public final ApplicationVersion getApplicationVersion() {
        return this.applicationVersion;
    }

    public final int getResultCode() {
        return this.resultCode;
    }

    @NotNull
    public final String getResultDescription() {
        return this.resultDescription;
    }

    @NotNull
    public final String getClassName() {
        return this.className;
    }
}
