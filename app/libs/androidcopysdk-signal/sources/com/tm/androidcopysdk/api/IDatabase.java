package com.tm.androidcopysdk.api;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: IDatabase.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��*\n\u0002\u0018\u0002\n��\n\u0002\u0010��\n��\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n��\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018��*\u0004\b��\u0010\u00012\u00020\u0002J\b\u0010\u0003\u001a\u00020\u0004H&J\b\u0010\u0005\u001a\u00020\u0004H&J\b\u0010\u0006\u001a\u00020\u0007H&J\u000e\u0010\b\u001a\b\u0012\u0004\u0012\u00028��0\tH&J\b\u0010\n\u001a\u00020\u0004H&J#\u0010\u000b\u001a\u0004\u0018\u0001H\f\"\u0004\b\u0001\u0010\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u0002H\f0\u000eH\u0016¢\u0006\u0002\u0010\u000f¨\u0006\u0010"}, d2 = {"Lcom/tm/androidcopysdk/api/IDatabase;", "MId", "", "beginTransaction", "", "endTransaction", "isInTransaction", "", "messageDao", "Lcom/tm/androidcopysdk/api/IArchiveMessageDao;", "setTransactionSuccessful", "withTransaction", "T", "block", "Lkotlin/Function0;", "(Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/api/IDatabase.class */
public interface IDatabase<MId> {
    @NotNull
    IArchiveMessageDao<MId> messageDao();

    void beginTransaction();

    void setTransactionSuccessful();

    void endTransaction();

    boolean isInTransaction();

    @Nullable
    <T> T withTransaction(@NotNull Function0<? extends T> function0);

    /* compiled from: IDatabase.kt */
    @Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK)
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/api/IDatabase$DefaultImpls.class */
    public static final class DefaultImpls {
        /* JADX WARN: Multi-variable type inference failed */
        @Nullable
        public static <MId, T> T withTransaction(@NotNull IDatabase<MId> iDatabase, @NotNull Function0<? extends T> function0) {
            Intrinsics.checkNotNullParameter(function0, "block");
            T t = null;
            try {
                iDatabase.beginTransaction();
                t = function0.invoke();
                iDatabase.setTransactionSuccessful();
                iDatabase.endTransaction();
            } catch (Throwable e) {
                try {
                    e.printStackTrace();
                    iDatabase.endTransaction();
                } catch (Throwable th) {
                    iDatabase.endTransaction();
                    throw th;
                }
            }
            return t;
        }
    }
}
