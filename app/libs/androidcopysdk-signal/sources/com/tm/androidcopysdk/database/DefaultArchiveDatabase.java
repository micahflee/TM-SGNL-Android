package com.tm.androidcopysdk.database;

import android.content.ContentResolver;
import android.content.Context;
import com.tm.androidcopysdk.api.IArchiveDatabase;
import com.tm.androidcopysdk.api.IArchiveMessageDao;
import com.tm.androidcopysdk.api.IArchiveMessageHeaderDao;
import com.tm.androidcopysdk.model.ArchiveMessageIdentifier;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import net.zetetic.database.sqlcipher.SQLiteDatabase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: DefaultArchiveDatabase.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018��2\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\b\u0010\u0011\u001a\u00020\u0010H\u0016J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u000e\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014H\u0016J\b\u0010\u0016\u001a\u00020\u0010H\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n��R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\f¨\u0006\u0017"}, d2 = {"Lcom/tm/androidcopysdk/database/DefaultArchiveDatabase;", "Lcom/tm/androidcopysdk/api/IArchiveDatabase;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "messageDao", "Lcom/tm/androidcopysdk/database/ArchiveMessageDao;", "messageHeaderDao", "Lcom/tm/androidcopysdk/api/IArchiveMessageHeaderDao;", "sqlDatabase", "Lnet/zetetic/database/sqlcipher/SQLiteDatabase;", "getSqlDatabase", "()Lnet/zetetic/database/sqlcipher/SQLiteDatabase;", "sqlDatabase$delegate", "Lkotlin/Lazy;", "beginTransaction", "", "endTransaction", "isInTransaction", "", "Lcom/tm/androidcopysdk/api/IArchiveMessageDao;", "Lcom/tm/androidcopysdk/model/ArchiveMessageIdentifier;", "setTransactionSuccessful", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DefaultArchiveDatabase.class */
public final class DefaultArchiveDatabase implements IArchiveDatabase {
    @NotNull
    private final Lazy sqlDatabase$delegate;
    @NotNull
    private final IArchiveMessageHeaderDao messageHeaderDao;
    @NotNull
    private final ArchiveMessageDao messageDao;

    public DefaultArchiveDatabase(@NotNull final Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.sqlDatabase$delegate = LazyKt.lazy(new Function0<SQLiteDatabase>() { // from class: com.tm.androidcopysdk.database.DefaultArchiveDatabase$sqlDatabase$2
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
            }

            @NotNull
            /* renamed from: invoke */
            public final SQLiteDatabase m42invoke() {
                return DBMessagesHelper.getInstance(context).m37getReadableDatabase();
            }
        });
        ContentResolver contentResolver = context.getContentResolver();
        Intrinsics.checkNotNullExpressionValue(contentResolver, "getContentResolver(...)");
        this.messageHeaderDao = new ArchiveMessageHeaderDao(contentResolver);
        ContentResolver contentResolver2 = context.getContentResolver();
        Intrinsics.checkNotNullExpressionValue(contentResolver2, "getContentResolver(...)");
        this.messageDao = new ArchiveMessageDao(contentResolver2, this.messageHeaderDao);
    }

    @Override // com.tm.androidcopysdk.api.IDatabase
    @Nullable
    public <T> T withTransaction(@NotNull Function0<? extends T> function0) {
        return (T) IArchiveDatabase.DefaultImpls.withTransaction(this, function0);
    }

    private final SQLiteDatabase getSqlDatabase() {
        return (SQLiteDatabase) this.sqlDatabase$delegate.getValue();
    }

    @Override // com.tm.androidcopysdk.api.IDatabase
    @NotNull
    public IArchiveMessageDao<ArchiveMessageIdentifier> messageDao() {
        return this.messageDao;
    }

    @Override // com.tm.androidcopysdk.api.IDatabase
    public void beginTransaction() {
        getSqlDatabase().beginTransaction();
    }

    @Override // com.tm.androidcopysdk.api.IDatabase
    public void setTransactionSuccessful() {
        getSqlDatabase().setTransactionSuccessful();
    }

    @Override // com.tm.androidcopysdk.api.IDatabase
    public void endTransaction() {
        getSqlDatabase().endTransaction();
    }

    @Override // com.tm.androidcopysdk.api.IDatabase
    public boolean isInTransaction() {
        return getSqlDatabase().inTransaction();
    }
}
