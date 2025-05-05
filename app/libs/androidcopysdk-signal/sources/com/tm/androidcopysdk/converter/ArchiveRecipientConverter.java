package com.tm.androidcopysdk.converter;

import android.database.Cursor;
import com.tm.androidcopysdk.model.ArchiveRecipient;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.ContentExtensions;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
/* compiled from: ArchiveRecipientConverter.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u001e\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018��2\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\t"}, d2 = {"Lcom/tm/androidcopysdk/converter/ArchiveRecipientConverter;", "", "()V", "convertReceivers", "", "Lcom/tm/androidcopysdk/model/ArchiveRecipient;", "cursor", "Landroid/database/Cursor;", "convertSender", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nArchiveRecipientConverter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveRecipientConverter.kt\ncom/tm/androidcopysdk/converter/ArchiveRecipientConverter\n+ 2 ContentExtensions.kt\ncom/tm/androidcopysdk/utils/ContentExtensions\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Cursor.kt\nandroidx/core/database/CursorKt\n*L\n1#1,32:1\n44#2,2:33\n40#2:35\n41#2:37\n44#2,2:39\n40#2:41\n41#2:43\n45#2:45\n40#2:46\n41#2:48\n45#2:50\n40#2:51\n41#2:53\n1#3:36\n1#3:42\n1#3:47\n1#3:52\n112#4:38\n112#4:44\n112#4:49\n112#4:54\n*S KotlinDebug\n*F\n+ 1 ArchiveRecipientConverter.kt\ncom/tm/androidcopysdk/converter/ArchiveRecipientConverter\n*L\n13#1:33,2\n13#1:35\n13#1:37\n14#1:39,2\n14#1:41\n14#1:43\n19#1:45\n19#1:46\n19#1:48\n20#1:50\n20#1:51\n20#1:53\n13#1:36\n14#1:42\n19#1:47\n20#1:52\n13#1:38\n14#1:44\n19#1:49\n20#1:54\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/converter/ArchiveRecipientConverter.class */
public final class ArchiveRecipientConverter {
    @NotNull
    public final ArchiveRecipient convertSender(@NotNull Cursor cursor) {
        String str;
        String str2;
        Intrinsics.checkNotNullParameter(cursor, "cursor");
        ArchiveRecipient.Companion companion = ArchiveRecipient.Companion;
        ContentExtensions contentExtensions = ContentExtensions.INSTANCE;
        Integer valueOf = Integer.valueOf(cursor.getColumnIndex("from_value"));
        int it$iv$iv = valueOf.intValue();
        Integer num = it$iv$iv >= 0 ? valueOf : null;
        if (num != null) {
            int index$iv$iv = num.intValue();
            str = cursor.isNull(index$iv$iv) ? null : cursor.getString(index$iv$iv);
        } else {
            str = null;
        }
        if (str == null) {
            str = "";
        }
        ContentExtensions contentExtensions2 = ContentExtensions.INSTANCE;
        Integer valueOf2 = Integer.valueOf(cursor.getColumnIndex("address_from"));
        int it$iv$iv2 = valueOf2.intValue();
        Integer num2 = it$iv$iv2 >= 0 ? valueOf2 : null;
        if (num2 != null) {
            int index$iv$iv2 = num2.intValue();
            str2 = cursor.isNull(index$iv$iv2) ? null : cursor.getString(index$iv$iv2);
        } else {
            str2 = null;
        }
        if (str2 == null) {
            str2 = "";
        }
        return companion.forLongName(null, str, str2);
    }

    @NotNull
    public final List<ArchiveRecipient> convertReceivers(@NotNull Cursor cursor) {
        String str;
        String str2;
        Intrinsics.checkNotNullParameter(cursor, "cursor");
        ContentExtensions contentExtensions = ContentExtensions.INSTANCE;
        Integer valueOf = Integer.valueOf(cursor.getColumnIndex("to_name"));
        int it$iv$iv = valueOf.intValue();
        Integer num = it$iv$iv >= 0 ? valueOf : null;
        if (num != null) {
            int index$iv$iv = num.intValue();
            str = cursor.isNull(index$iv$iv) ? null : cursor.getString(index$iv$iv);
        } else {
            str = null;
        }
        if (str == null) {
            str = "";
        }
        List names = StringsKt.split$default(str, new String[]{";"}, false, 0, 6, (Object) null);
        ContentExtensions contentExtensions2 = ContentExtensions.INSTANCE;
        Integer valueOf2 = Integer.valueOf(cursor.getColumnIndex("to_value"));
        int it$iv$iv2 = valueOf2.intValue();
        Integer num2 = it$iv$iv2 >= 0 ? valueOf2 : null;
        if (num2 != null) {
            int index$iv$iv2 = num2.intValue();
            str2 = cursor.isNull(index$iv$iv2) ? null : cursor.getString(index$iv$iv2);
        } else {
            str2 = null;
        }
        if (str2 == null) {
            str2 = "";
        }
        List addresses = StringsKt.split$default(str2, new String[]{";"}, false, 0, 6, (Object) null);
        int max = Math.max(names.size(), addresses.size());
        ArrayList arrayList = new ArrayList(max);
        for (int i = 0; i < max; i++) {
            int index = i;
            String it = (String) CollectionsKt.getOrNull(addresses, index);
            arrayList.add(it != null ? ArchiveRecipient.Companion.forLongName(null, it, (String) CollectionsKt.getOrNull(names, index)) : null);
        }
        return CollectionsKt.filterNotNull(arrayList);
    }
}
