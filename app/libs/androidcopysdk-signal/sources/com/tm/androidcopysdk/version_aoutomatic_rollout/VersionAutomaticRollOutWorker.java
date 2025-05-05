package com.tm.androidcopysdk.version_aoutomatic_rollout;

import android.content.Context;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: VersionAutomaticRollOutWorker.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n��\u0018��2\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\u000b\u001a\u00020\fH\u0016R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\n¨\u0006\r"}, d2 = {"Lcom/tm/androidcopysdk/version_aoutomatic_rollout/VersionAutomaticRollOutWorker;", "Landroidx/work/Worker;", "context", "Landroid/content/Context;", "params", "Landroidx/work/WorkerParameters;", "(Landroid/content/Context;Landroidx/work/WorkerParameters;)V", "getContext", "()Landroid/content/Context;", "setContext", "(Landroid/content/Context;)V", "doWork", "Landroidx/work/ListenableWorker$Result;", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/version_aoutomatic_rollout/VersionAutomaticRollOutWorker.class */
public final class VersionAutomaticRollOutWorker extends Worker {
    @NotNull
    private Context context;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public VersionAutomaticRollOutWorker(@NotNull Context context, @NotNull WorkerParameters params) {
        super(context, params);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(params, "params");
        this.context = context;
    }

    @NotNull
    public final Context getContext() {
        return this.context;
    }

    public final void setContext(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "<set-?>");
        this.context = context;
    }

    @NotNull
    public ListenableWorker.Result doWork() {
        ListenableWorker.Result result;
        try {
            VersionAutomaticRollOutManager.INSTANCE.manageVersions(this.context);
            ListenableWorker.Result success = ListenableWorker.Result.success();
            Intrinsics.checkNotNull(success);
            result = success;
        } catch (Exception e) {
            ListenableWorker.Result failure = ListenableWorker.Result.failure();
            Intrinsics.checkNotNull(failure);
            result = failure;
        }
        return result;
    }
}
