package com.tm.androidcopysdk.device;

import android.content.Context;
import android.preference.PreferenceManager;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import com.tm.logger.Log;
import java.io.File;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabaseHook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: SdkMigrator.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��6\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n��\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018�� \u00132\u00020\u0001:\u0001\u0013B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0006\u0010\t\u001a\u00020\u0006J\"\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000f\u001a\u00020\u0010H\u0002J\b\u0010\u0011\u001a\u00020\u000bH\u0002J\b\u0010\u0012\u001a\u00020\u000bH\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n��R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u000e¢\u0006\u0002\n��¨\u0006\u0014"}, d2 = {"Lcom/tm/androidcopysdk/device/SdkMigrator;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "calledOnce", "", "lastLocalVersion", "", "migrate", "openAndCloseWithHook", "", "name", "", "pass", "hook", "Lnet/sqlcipher/database/SQLiteDatabaseHook;", "updateLocalVersion", "updateToVersion1", "Companion", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nSdkMigrator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SdkMigrator.kt\ncom/tm/androidcopysdk/device/SdkMigrator\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,85:1\n1#2:86\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/device/SdkMigrator.class */
public final class SdkMigrator {
    @NotNull
    private final Context context;
    private int lastLocalVersion;
    private boolean calledOnce;
    @NotNull
    private static final String TAG = "SdkMigrator";
    private static final int version1 = 1;
    private static final int LOCAL_VERSION = 1;
    @Nullable
    private static SdkMigrator instance;
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private static final String LOCAL_VERSION_KEY = "androidcopysdk_migration_version";

    @JvmStatic
    @NotNull
    public static final SdkMigrator getInstance(@NotNull Context context) {
        return Companion.getInstance(context);
    }

    public /* synthetic */ SdkMigrator(Context context, DefaultConstructorMarker $constructor_marker) {
        this(context);
    }

    private SdkMigrator(Context context) {
        this.context = context;
        this.lastLocalVersion = PreferenceManager.getDefaultSharedPreferences(this.context).getInt(LOCAL_VERSION_KEY, 0);
    }

    public final boolean migrate() {
        if (!this.calledOnce) {
            this.calledOnce = true;
            updateLocalVersion();
            return true;
        }
        return true;
    }

    private final void updateLocalVersion() {
        if (this.lastLocalVersion != 1) {
            Log.d(TAG, "updating from local version " + this.lastLocalVersion + " to 1");
            if (this.lastLocalVersion < 1) {
                updateToVersion1();
            }
            PreferenceManager.getDefaultSharedPreferences(this.context).edit().putInt(LOCAL_VERSION_KEY, 1).commit();
            this.lastLocalVersion = 1;
        }
    }

    private final void updateToVersion1() {
        String pass;
        if (FlavorSettings.getInstance().usingCustomSqlCipherVersion() || (pass = TMCredentialsStore.getInstance(this.context).password(this.context)) == null) {
            return;
        }
        SQLiteDatabaseHook hook = new SQLiteDatabaseHook() { // from class: com.tm.androidcopysdk.device.SdkMigrator$updateToVersion1$hook$1
            public void preKey(@NotNull SQLiteDatabase sqLiteDatabase) {
                Intrinsics.checkNotNullParameter(sqLiteDatabase, "sqLiteDatabase");
            }

            public void postKey(@NotNull SQLiteDatabase sqLiteDatabase) {
                Intrinsics.checkNotNullParameter(sqLiteDatabase, "sqLiteDatabase");
                sqLiteDatabase.rawExecSQL("PRAGMA cipher_migrate;");
            }
        };
        openAndCloseWithHook("message_enc.db", pass, hook);
        openAndCloseWithHook("message.db", null, hook);
    }

    private final void openAndCloseWithHook(String name, String pass, SQLiteDatabaseHook hook) {
        File it = this.context.getDatabasePath(name);
        File file = it.exists() ? it : null;
        if (file == null) {
            return;
        }
        File path = file;
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(path, pass, (SQLiteDatabase.CursorFactory) null, hook);
        database.close();
    }

    /* compiled from: SdkMigrator.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��(\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\b\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u000b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\rH\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D¢\u0006\u0002\n��R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n��R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n��R\u000e\u0010\n\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��¨\u0006\u000e"}, d2 = {"Lcom/tm/androidcopysdk/device/SdkMigrator$Companion;", "", "()V", "LOCAL_VERSION", "", "LOCAL_VERSION_KEY", "", "TAG", "instance", "Lcom/tm/androidcopysdk/device/SdkMigrator;", "version1", "getInstance", "context", "Landroid/content/Context;", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/device/SdkMigrator$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        @NotNull
        public final SdkMigrator getInstance(@NotNull Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            SdkMigrator instance = SdkMigrator.instance;
            if (instance == null) {
                instance = new SdkMigrator(context, null);
                Companion companion = SdkMigrator.Companion;
                SdkMigrator.instance = instance;
            }
            return instance;
        }
    }
}
