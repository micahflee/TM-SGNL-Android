package com.tm.androidcopysdk.database;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import com.tm.androidcopysdk.api.IArchiveMessageHeaderDao;
import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.model.ArchiveMessageHeader;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.ContentExtensions;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveMessageHeaderDao.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��6\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\n\u0002\u0010 \n\u0002\b\u0004\b��\u0018��2\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u0012\u0010\f\u001a\u0004\u0018\u00010\t2\u0006\u0010\r\u001a\u00020\u000eH\u0016J\u001c\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\t0\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0010H\u0016J\u0016\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\t0\u00102\u0006\u0010\u0013\u001a\u00020\u000eH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n��R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004¢\u0006\u0002\n��¨\u0006\u0014"}, d2 = {"Lcom/tm/androidcopysdk/database/ArchiveMessageHeaderDao;", "Lcom/tm/androidcopysdk/api/IArchiveMessageHeaderDao;", "contentResolver", "Landroid/content/ContentResolver;", "(Landroid/content/ContentResolver;)V", "uri", "Landroid/net/Uri;", "kotlin.jvm.PlatformType", "deserializeHeader", "Lcom/tm/androidcopysdk/model/ArchiveMessageHeader;", "cursor", "Landroid/database/Cursor;", "find", "id", "", "findAll", "", "ids", "findAllByMessageId", CallLogMessageRecorder.messageId_key, "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nArchiveMessageHeaderDao.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveMessageHeaderDao.kt\ncom/tm/androidcopysdk/database/ArchiveMessageHeaderDao\n+ 2 ContentExtensions.kt\ncom/tm/androidcopysdk/utils/ContentExtensions\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Cursor.kt\nandroidx/core/database/CursorKt\n*L\n1#1,45:1\n76#2,25:46\n81#2,20:71\n81#2,20:91\n40#2:111\n41#2:113\n40#2:115\n41#2:117\n40#2:119\n41#2:121\n1#3:112\n1#3:116\n1#3:120\n112#4:114\n112#4:118\n112#4:122\n*S KotlinDebug\n*F\n+ 1 ArchiveMessageHeaderDao.kt\ncom/tm/androidcopysdk/database/ArchiveMessageHeaderDao\n*L\n23#1:46,25\n30#1:71,20\n35#1:91,20\n40#1:111\n40#1:113\n41#1:115\n41#1:117\n42#1:119\n42#1:121\n40#1:112\n41#1:116\n42#1:120\n40#1:114\n41#1:118\n42#1:122\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/ArchiveMessageHeaderDao.class */
public final class ArchiveMessageHeaderDao implements IArchiveMessageHeaderDao {
    @NotNull
    private final ContentResolver contentResolver;
    private final Uri uri;

    public ArchiveMessageHeaderDao(@NotNull ContentResolver contentResolver) {
        Intrinsics.checkNotNullParameter(contentResolver, "contentResolver");
        this.contentResolver = contentResolver;
        this.uri = MessageContentProvider.CONTENT_URI_HEADERS;
    }

    @Override // com.tm.androidcopysdk.api.ISharedDao
    @Nullable
    public ArchiveMessageHeader find(@NotNull String id) {
        Intrinsics.checkNotNullParameter(id, "id");
        String selection = "key = '" + id + '\'';
        ContentExtensions contentExtensions = ContentExtensions.INSTANCE;
        ContentResolver $this$querySingle$iv = this.contentResolver;
        Uri uri$iv = this.uri;
        Intrinsics.checkNotNullExpressionValue(uri$iv, "uri");
        Cursor $this$iterate$iv$iv$iv = $this$querySingle$iv.query(uri$iv, null, selection, null, null);
        if (!Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper())) {
            List result$iv$iv$iv = new ArrayList();
            if ($this$iterate$iv$iv$iv != null) {
                try {
                    if ($this$iterate$iv$iv$iv.getCount() > 0) {
                        while ($this$iterate$iv$iv$iv.moveToNext()) {
                            ArchiveMessageHeader deserializeHeader = deserializeHeader($this$iterate$iv$iv$iv);
                            if (deserializeHeader != null) {
                                result$iv$iv$iv.add(deserializeHeader);
                            }
                        }
                    }
                } catch (Throwable e$iv$iv$iv) {
                    try {
                        e$iv$iv$iv.printStackTrace();
                        if ($this$iterate$iv$iv$iv != null) {
                            $this$iterate$iv$iv$iv.close();
                        }
                    } finally {
                        if ($this$iterate$iv$iv$iv != null) {
                            $this$iterate$iv$iv$iv.close();
                        }
                    }
                }
            }
            return (ArchiveMessageHeader) CollectionsKt.firstOrNull(result$iv$iv$iv);
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }

    @Override // com.tm.androidcopysdk.api.ISharedDao
    @NotNull
    public List<ArchiveMessageHeader> findAll(@NotNull List<? extends String> list) {
        Intrinsics.checkNotNullParameter(list, "ids");
        if (list.isEmpty()) {
            return CollectionsKt.emptyList();
        }
        String selection = "key in ('" + CollectionsKt.joinToString$default(list, "', '", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null) + "')";
        ContentExtensions contentExtensions = ContentExtensions.INSTANCE;
        ContentResolver $this$queryMultiple$iv = this.contentResolver;
        Uri uri$iv = this.uri;
        Intrinsics.checkNotNullExpressionValue(uri$iv, "uri");
        Cursor $this$iterate$iv$iv = $this$queryMultiple$iv.query(uri$iv, null, selection, null, null);
        if (!Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper())) {
            List result$iv$iv = new ArrayList();
            if ($this$iterate$iv$iv != null) {
                try {
                    if ($this$iterate$iv$iv.getCount() > 0) {
                        while ($this$iterate$iv$iv.moveToNext()) {
                            ArchiveMessageHeader deserializeHeader = deserializeHeader($this$iterate$iv$iv);
                            if (deserializeHeader != null) {
                                result$iv$iv.add(deserializeHeader);
                            }
                        }
                    }
                } catch (Throwable e$iv$iv) {
                    try {
                        e$iv$iv.printStackTrace();
                        if ($this$iterate$iv$iv != null) {
                            $this$iterate$iv$iv.close();
                        }
                    } finally {
                        if ($this$iterate$iv$iv != null) {
                            $this$iterate$iv$iv.close();
                        }
                    }
                }
            }
            return result$iv$iv;
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }

    @Override // com.tm.androidcopysdk.api.IArchiveMessageHeaderDao
    @NotNull
    public List<ArchiveMessageHeader> findAllByMessageId(@NotNull String messageId) {
        Intrinsics.checkNotNullParameter(messageId, CallLogMessageRecorder.messageId_key);
        ContentExtensions contentExtensions = ContentExtensions.INSTANCE;
        ContentResolver $this$queryMultiple$iv = this.contentResolver;
        Uri uri$iv = this.uri;
        Intrinsics.checkNotNullExpressionValue(uri$iv, "uri");
        String[] selectionArgs$iv = {messageId};
        Cursor $this$iterate$iv$iv = $this$queryMultiple$iv.query(uri$iv, null, "message_id = ?", selectionArgs$iv, null);
        if (!Intrinsics.areEqual(Looper.myLooper(), Looper.getMainLooper())) {
            List result$iv$iv = new ArrayList();
            if ($this$iterate$iv$iv != null) {
                try {
                    if ($this$iterate$iv$iv.getCount() > 0) {
                        while ($this$iterate$iv$iv.moveToNext()) {
                            ArchiveMessageHeader deserializeHeader = deserializeHeader($this$iterate$iv$iv);
                            if (deserializeHeader != null) {
                                result$iv$iv.add(deserializeHeader);
                            }
                        }
                    }
                } catch (Throwable e$iv$iv) {
                    try {
                        e$iv$iv.printStackTrace();
                        if ($this$iterate$iv$iv != null) {
                            $this$iterate$iv$iv.close();
                        }
                    } finally {
                        if ($this$iterate$iv$iv != null) {
                            $this$iterate$iv$iv.close();
                        }
                    }
                }
            }
            return result$iv$iv;
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final ArchiveMessageHeader deserializeHeader(Cursor cursor) {
        String str;
        String str2;
        String str3;
        ContentExtensions contentExtensions = ContentExtensions.INSTANCE;
        Integer valueOf = Integer.valueOf(cursor.getColumnIndex(DBHeadersTable.HeadersEntry.COLUMN_NAME_MSG_ID));
        int it$iv = valueOf.intValue();
        Integer num = it$iv >= 0 ? valueOf : null;
        if (num != null) {
            int index$iv = num.intValue();
            str = cursor.isNull(index$iv) ? null : cursor.getString(index$iv);
        } else {
            str = null;
        }
        ContentExtensions contentExtensions2 = ContentExtensions.INSTANCE;
        Integer valueOf2 = Integer.valueOf(cursor.getColumnIndex(DBHeadersTable.HeadersEntry.COLUMN_NAME_KEY));
        int it$iv2 = valueOf2.intValue();
        Integer num2 = it$iv2 >= 0 ? valueOf2 : null;
        if (num2 != null) {
            int index$iv2 = num2.intValue();
            str2 = cursor.isNull(index$iv2) ? null : cursor.getString(index$iv2);
        } else {
            str2 = null;
        }
        ContentExtensions contentExtensions3 = ContentExtensions.INSTANCE;
        Integer valueOf3 = Integer.valueOf(cursor.getColumnIndex(DBHeadersTable.HeadersEntry.COLUMN_NAME_VALUE));
        int it$iv3 = valueOf3.intValue();
        Integer num3 = it$iv3 >= 0 ? valueOf3 : null;
        if (num3 != null) {
            int index$iv3 = num3.intValue();
            str3 = cursor.isNull(index$iv3) ? null : cursor.getString(index$iv3);
        } else {
            str3 = null;
        }
        return new ArchiveMessageHeader(str, str2, str3);
    }
}
