package com.tm.androidcopysdk.converter;

import android.database.Cursor;
import com.tm.androidcopysdk.model.ArchiveCallInfo;
import com.tm.androidcopysdk.model.ArchiveMessageType;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.ContentExtensions;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveCallInfoConverter.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u001e\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\u0018��2\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b¨\u0006\t"}, d2 = {"Lcom/tm/androidcopysdk/converter/ArchiveCallInfoConverter;", "", "()V", "convert", "Lcom/tm/androidcopysdk/model/ArchiveCallInfo;", "cursor", "Landroid/database/Cursor;", "type", "Lcom/tm/androidcopysdk/model/ArchiveMessageType;", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nArchiveCallInfoConverter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveCallInfoConverter.kt\ncom/tm/androidcopysdk/converter/ArchiveCallInfoConverter\n+ 2 ContentExtensions.kt\ncom/tm/androidcopysdk/utils/ContentExtensions\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Cursor.kt\nandroidx/core/database/CursorKt\n*L\n1#1,17:1\n40#2:18\n41#2:20\n1#3:19\n112#4:21\n*S KotlinDebug\n*F\n+ 1 ArchiveCallInfoConverter.kt\ncom/tm/androidcopysdk/converter/ArchiveCallInfoConverter\n*L\n14#1:18\n14#1:20\n14#1:19\n14#1:21\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/converter/ArchiveCallInfoConverter.class */
public final class ArchiveCallInfoConverter {
    @Nullable
    public final ArchiveCallInfo convert(@NotNull Cursor cursor, @NotNull ArchiveMessageType type) {
        String str;
        Intrinsics.checkNotNullParameter(cursor, "cursor");
        Intrinsics.checkNotNullParameter(type, "type");
        if (type != ArchiveMessageType.Call) {
            return null;
        }
        ContentExtensions contentExtensions = ContentExtensions.INSTANCE;
        Integer valueOf = Integer.valueOf(cursor.getColumnIndex("duration"));
        int it$iv = valueOf.intValue();
        Integer num = it$iv >= 0 ? valueOf : null;
        if (num != null) {
            int index$iv = num.intValue();
            str = cursor.isNull(index$iv) ? null : cursor.getString(index$iv);
        } else {
            str = null;
        }
        String duration = str;
        return new ArchiveCallInfo(null, null, duration != null ? StringsKt.toLongOrNull(duration) : null, null, null, 26, null);
    }
}
