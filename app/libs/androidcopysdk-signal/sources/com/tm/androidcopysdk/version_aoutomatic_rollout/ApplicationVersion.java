package com.tm.androidcopysdk.version_aoutomatic_rollout;

import com.google.gson.annotations.SerializedName;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ApplicationVersion.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"�� \n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n��\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018��2\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003¢\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J;\u0010\u0016\u001a\u00020��2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001a\u001a\u00020\u0005HÖ\u0001J\t\u0010\u001b\u001a\u00020\u0003HÖ\u0001R\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n��\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n��\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n��\u001a\u0004\b\u000e\u0010\rR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n��\u001a\u0004\b\u000f\u0010\rR\u0016\u0010\u0007\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n��\u001a\u0004\b\u0010\u0010\r¨\u0006\u001c"}, d2 = {"Lcom/tm/androidcopysdk/version_aoutomatic_rollout/ApplicationVersion;", "", "latestVersion", "", "appId", "", "description", "minAllowedVersion", "className", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getAppId", "()I", "getClassName", "()Ljava/lang/String;", "getDescription", "getLatestVersion", "getMinAllowedVersion", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/version_aoutomatic_rollout/ApplicationVersion.class */
public final class ApplicationVersion {
    @SerializedName("latestVersion")
    @NotNull
    private final String latestVersion;
    @SerializedName("appId")
    private final int appId;
    @SerializedName("description")
    @NotNull
    private final String description;
    @SerializedName("minAllowedVersion")
    @NotNull
    private final String minAllowedVersion;
    @SerializedName("class")
    @NotNull
    private final String className;

    @NotNull
    public final String component1() {
        return this.latestVersion;
    }

    public final int component2() {
        return this.appId;
    }

    @NotNull
    public final String component3() {
        return this.description;
    }

    @NotNull
    public final String component4() {
        return this.minAllowedVersion;
    }

    @NotNull
    public final String component5() {
        return this.className;
    }

    @NotNull
    public final ApplicationVersion copy(@NotNull String latestVersion, int appId, @NotNull String description, @NotNull String minAllowedVersion, @NotNull String className) {
        Intrinsics.checkNotNullParameter(latestVersion, "latestVersion");
        Intrinsics.checkNotNullParameter(description, "description");
        Intrinsics.checkNotNullParameter(minAllowedVersion, "minAllowedVersion");
        Intrinsics.checkNotNullParameter(className, "className");
        return new ApplicationVersion(latestVersion, appId, description, minAllowedVersion, className);
    }

    public static /* synthetic */ ApplicationVersion copy$default(ApplicationVersion applicationVersion, String str, int i, String str2, String str3, String str4, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = applicationVersion.latestVersion;
        }
        if ((i2 & 2) != 0) {
            i = applicationVersion.appId;
        }
        if ((i2 & 4) != 0) {
            str2 = applicationVersion.description;
        }
        if ((i2 & 8) != 0) {
            str3 = applicationVersion.minAllowedVersion;
        }
        if ((i2 & 16) != 0) {
            str4 = applicationVersion.className;
        }
        return applicationVersion.copy(str, i, str2, str3, str4);
    }

    @NotNull
    public String toString() {
        return "ApplicationVersion(latestVersion=" + this.latestVersion + ", appId=" + this.appId + ", description=" + this.description + ", minAllowedVersion=" + this.minAllowedVersion + ", className=" + this.className + ')';
    }

    public int hashCode() {
        int result = this.latestVersion.hashCode();
        return (((((((result * 31) + Integer.hashCode(this.appId)) * 31) + this.description.hashCode()) * 31) + this.minAllowedVersion.hashCode()) * 31) + this.className.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ApplicationVersion) {
            ApplicationVersion applicationVersion = (ApplicationVersion) other;
            return Intrinsics.areEqual(this.latestVersion, applicationVersion.latestVersion) && this.appId == applicationVersion.appId && Intrinsics.areEqual(this.description, applicationVersion.description) && Intrinsics.areEqual(this.minAllowedVersion, applicationVersion.minAllowedVersion) && Intrinsics.areEqual(this.className, applicationVersion.className);
        }
        return false;
    }

    public ApplicationVersion() {
        this(null, 0, null, null, null, 31, null);
    }

    public ApplicationVersion(@NotNull String latestVersion, int appId, @NotNull String description, @NotNull String minAllowedVersion, @NotNull String className) {
        Intrinsics.checkNotNullParameter(latestVersion, "latestVersion");
        Intrinsics.checkNotNullParameter(description, "description");
        Intrinsics.checkNotNullParameter(minAllowedVersion, "minAllowedVersion");
        Intrinsics.checkNotNullParameter(className, "className");
        this.latestVersion = latestVersion;
        this.appId = appId;
        this.description = description;
        this.minAllowedVersion = minAllowedVersion;
        this.className = className;
    }

    public /* synthetic */ ApplicationVersion(String str, int i, String str2, String str3, String str4, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? "" : str, (i2 & 2) != 0 ? 0 : i, (i2 & 4) != 0 ? "" : str2, (i2 & 8) != 0 ? "" : str3, (i2 & 16) != 0 ? "" : str4);
    }

    @NotNull
    public final String getLatestVersion() {
        return this.latestVersion;
    }

    public final int getAppId() {
        return this.appId;
    }

    @NotNull
    public final String getDescription() {
        return this.description;
    }

    @NotNull
    public final String getMinAllowedVersion() {
        return this.minAllowedVersion;
    }

    @NotNull
    public final String getClassName() {
        return this.className;
    }
}
