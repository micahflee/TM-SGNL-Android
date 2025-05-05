package com.tm.androidcopysdk.Models;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: RecFileExt.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\"\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018��2\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0002\u0010\u0005J\t\u0010\t\u001a\u00020\u0003HÆ\u0003J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\u000b\u001a\u00020��2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u000f\u001a\u00020\u0010HÖ\u0001J\b\u0010\u0011\u001a\u00020\u0003H\u0016R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\b\u0010\u0007¨\u0006\u0012"}, d2 = {"Lcom/tm/androidcopysdk/Models/RecFileExt;", "", "name", "", "ext", "(Ljava/lang/String;Ljava/lang/String;)V", "getExt", "()Ljava/lang/String;", "getName", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/Models/RecFileExt.class */
public final class RecFileExt {
    @NotNull
    private final String name;
    @NotNull
    private final String ext;

    @NotNull
    public final String component1() {
        return this.name;
    }

    @NotNull
    public final String component2() {
        return this.ext;
    }

    @NotNull
    public final RecFileExt copy(@NotNull String name, @NotNull String ext) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(ext, "ext");
        return new RecFileExt(name, ext);
    }

    public static /* synthetic */ RecFileExt copy$default(RecFileExt recFileExt, String str, String str2, int i, Object obj) {
        if ((i & 1) != 0) {
            str = recFileExt.name;
        }
        if ((i & 2) != 0) {
            str2 = recFileExt.ext;
        }
        return recFileExt.copy(str, str2);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        return (result * 31) + this.ext.hashCode();
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof RecFileExt) {
            RecFileExt recFileExt = (RecFileExt) other;
            return Intrinsics.areEqual(this.name, recFileExt.name) && Intrinsics.areEqual(this.ext, recFileExt.ext);
        }
        return false;
    }

    public RecFileExt(@NotNull String name, @NotNull String ext) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(ext, "ext");
        this.name = name;
        this.ext = ext;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final String getExt() {
        return this.ext;
    }

    @NotNull
    public String toString() {
        return "name: " + this.name + " ext: " + this.ext;
    }
}
