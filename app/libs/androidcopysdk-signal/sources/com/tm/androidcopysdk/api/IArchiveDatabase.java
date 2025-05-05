package com.tm.androidcopysdk.api;

import com.tm.androidcopysdk.api.IDatabase;
import com.tm.androidcopysdk.model.ArchiveMessageIdentifier;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: IArchiveDatabase.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\bf\u0018��2\b\u0012\u0004\u0012\u00020\u00020\u0001¨\u0006\u0003"}, d2 = {"Lcom/tm/androidcopysdk/api/IArchiveDatabase;", "Lcom/tm/androidcopysdk/api/IDatabase;", "Lcom/tm/androidcopysdk/model/ArchiveMessageIdentifier;", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/api/IArchiveDatabase.class */
public interface IArchiveDatabase extends IDatabase<ArchiveMessageIdentifier> {

    /* compiled from: IArchiveDatabase.kt */
    @Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK)
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/api/IArchiveDatabase$DefaultImpls.class */
    public static final class DefaultImpls {
        @Nullable
        public static <T> T withTransaction(@NotNull IArchiveDatabase $this, @NotNull Function0<? extends T> function0) {
            Intrinsics.checkNotNullParameter(function0, "block");
            return (T) IDatabase.DefaultImpls.withTransaction($this, function0);
        }
    }
}
