package com.tm.androidcopysdk.version_aoutomatic_rollout;

import android.content.Context;
import com.tm.androidcopysdk.HandleResponseListener;
import com.tm.androidcopysdk.network.NetworkManager;
import com.tm.androidcopysdk.network.VersionAutomaticRollOutRequest;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.logger.Log;
import com.tm.utils.Definitions;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;
/* compiled from: VersionAutomaticRollOutManager.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��*\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n��\bÆ\u0002\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0007\u001a\u00020\bH\u0002J\u000e\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0007\u001a\u00020\bJ\u0010\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002R\u0014\u0010\u0003\u001a\u00020\u0004X\u0086D¢\u0006\b\n��\u001a\u0004\b\u0005\u0010\u0006R\u001a\u0010\u0007\u001a\u00020\bX\u0086.¢\u0006\u000e\n��\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\f¨\u0006\u0013"}, d2 = {"Lcom/tm/androidcopysdk/version_aoutomatic_rollout/VersionAutomaticRollOutManager;", "", "()V", "TAG", "", "getTAG", "()Ljava/lang/String;", "context", "Landroid/content/Context;", "getContext", "()Landroid/content/Context;", "setContext", "(Landroid/content/Context;)V", "getAppVersion", "", "manageVersions", "saveMinMaxVersionToSharedPrefs", "versionModel", "Lcom/tm/androidcopysdk/version_aoutomatic_rollout/VersionAutomaticRollOutModel;", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/version_aoutomatic_rollout/VersionAutomaticRollOutManager.class */
public final class VersionAutomaticRollOutManager {
    @NotNull
    public static final VersionAutomaticRollOutManager INSTANCE = new VersionAutomaticRollOutManager();
    @NotNull
    private static final String TAG = "VersionAutomaticRollOutManager";
    public static Context context;

    private VersionAutomaticRollOutManager() {
    }

    @NotNull
    public final String getTAG() {
        return TAG;
    }

    @NotNull
    public final Context getContext() {
        Context context2 = context;
        if (context2 != null) {
            return context2;
        }
        Intrinsics.throwUninitializedPropertyAccessException("context");
        return null;
    }

    public final void setContext(@NotNull Context context2) {
        Intrinsics.checkNotNullParameter(context2, "<set-?>");
        context = context2;
    }

    public final void manageVersions(@NotNull Context context2) {
        Intrinsics.checkNotNullParameter(context2, "context");
        setContext(context2);
        getAppVersion(context2);
    }

    private final void getAppVersion(Context context2) {
        Object obj;
        try {
            Result.Companion companion = Result.Companion;
            VersionAutomaticRollOutManager versionAutomaticRollOutManager = INSTANCE;
            Log.d(TAG, "runCatching");
            NetworkManager nm = new NetworkManager(context2, Definitions.BaseUrl);
            HandleResponseListener handleResponseListener = new HandleResponseListener() { // from class: com.tm.androidcopysdk.version_aoutomatic_rollout.VersionAutomaticRollOutManager$getAppVersion$1$handleResponseListener$1
                @Override // com.tm.androidcopysdk.HandleResponseListener
                public void backoffRetryOnFailedMessages() {
                }

                @Override // com.tm.androidcopysdk.HandleResponseListener
                public void reportOnSuccess() {
                    Log.d(VersionAutomaticRollOutManager.INSTANCE.getTAG(), "reportOnSuccess");
                }

                @Override // com.tm.androidcopysdk.HandleResponseListener
                public void reportOnFailure() {
                    Log.d(VersionAutomaticRollOutManager.INSTANCE.getTAG(), "reportOnFailure");
                }
            };
            Response res = nm.start(new VersionAutomaticRollOutRequest(), handleResponseListener, context2.getApplicationContext(), true);
            Intrinsics.checkNotNullExpressionValue(res, "start(...)");
            if (res != null && res.isSuccessful()) {
                VersionAutomaticRollOutManager versionAutomaticRollOutManager2 = INSTANCE;
                String str = TAG;
                StringBuilder append = new StringBuilder().append("res.isSuccessful = ").append(res).append(", ");
                ArrayList arrayList = (ArrayList) res.body();
                Log.d(str, append.append(arrayList != null ? (VersionAutomaticRollOutModel) arrayList.get(0) : null).toString());
                ArrayList arrayList2 = (ArrayList) res.body();
                VersionAutomaticRollOutModel versionAutomaticRollOutModelType = arrayList2 != null ? (VersionAutomaticRollOutModel) arrayList2.get(0) : null;
                if (versionAutomaticRollOutModelType == null) {
                    return;
                }
                Intrinsics.checkNotNull(versionAutomaticRollOutModelType);
                INSTANCE.saveMinMaxVersionToSharedPrefs(versionAutomaticRollOutModelType);
            }
            obj = Result.constructor-impl(Unit.INSTANCE);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.Companion;
            obj = Result.constructor-impl(ResultKt.createFailure(th));
        }
        Object obj2 = obj;
        if (Result.isSuccess-impl(obj2)) {
            Unit it = (Unit) obj2;
            VersionAutomaticRollOutManager versionAutomaticRollOutManager3 = INSTANCE;
            Log.d(TAG, "onSuccess. result is " + it);
        }
        Throwable it2 = Result.exceptionOrNull-impl(obj2);
        if (it2 != null) {
            VersionAutomaticRollOutManager versionAutomaticRollOutManager4 = INSTANCE;
            Log.d(TAG, "onFailure, error = " + it2);
        }
    }

    private final void saveMinMaxVersionToSharedPrefs(VersionAutomaticRollOutModel versionModel) {
        PrefManager.setStringPref(getContext(), VersionAutomaticRollOutManagerKt.MIN_VERSION_KEY, versionModel.getApplicationVersion().getMinAllowedVersion());
        PrefManager.setStringPref(getContext(), VersionAutomaticRollOutManagerKt.MAX_VERSION_KEY, versionModel.getApplicationVersion().getLatestVersion());
        PrefManager.setBooleanPref(getContext(), VersionAutomaticRollOutManagerKt.IS_NEED_TO_SHOW_MAX_VERSION, true);
    }
}
