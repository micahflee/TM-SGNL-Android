package com.tm.androidcopysdk.database;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import org.jetbrains.annotations.NotNull;
/* compiled from: SqlCipherLibraryLoader.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u001e\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000b\n��\n\u0002\u0010\u0002\n��\bÆ\u0002\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\bH\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n��¨\u0006\t"}, d2 = {"Lcom/tm/androidcopysdk/database/SqlCipherLibraryLoader;", "", "()V", "LOCK", "Ljava/lang/Object;", "loaded", "", "load", "", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/SqlCipherLibraryLoader.class */
public final class SqlCipherLibraryLoader {
    private static volatile boolean loaded;
    @NotNull
    public static final SqlCipherLibraryLoader INSTANCE = new SqlCipherLibraryLoader();
    @NotNull
    private static final Object LOCK = new Object();

    private SqlCipherLibraryLoader() {
    }

    @JvmStatic
    public static final void load() {
        if (!loaded) {
            synchronized (LOCK) {
                if (!loaded) {
                    System.loadLibrary("sqlcipher");
                    SqlCipherLibraryLoader sqlCipherLibraryLoader = INSTANCE;
                    loaded = true;
                }
                Unit unit = Unit.INSTANCE;
            }
        }
    }
}
