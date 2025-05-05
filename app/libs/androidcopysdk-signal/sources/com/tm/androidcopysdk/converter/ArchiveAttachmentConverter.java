package com.tm.androidcopysdk.converter;

import android.database.Cursor;
import com.tm.androidcopysdk.database.MessageContentProvider;
import com.tm.androidcopysdk.model.ArchiveAttachment;
import com.tm.androidcopysdk.model.ArchiveAttachmentType;
import com.tm.androidcopysdk.model.MessageAttachmentStatus;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.ContentExtensions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
/* compiled from: ArchiveAttachmentConverter.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��0\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\u0018��2\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u0007J&\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\rH\u0002¨\u0006\u000f"}, d2 = {"Lcom/tm/androidcopysdk/converter/ArchiveAttachmentConverter;", "", "()V", "convert", "", "Lcom/tm/androidcopysdk/model/ArchiveAttachment;", "cursor", "Landroid/database/Cursor;", "getMediaStatus", "Lcom/tm/androidcopysdk/model/MessageAttachmentStatus;", "status", "Lcom/tm/androidcopysdk/database/MessageContentProvider$MessageDeliveryStatus;", "sourcePath", "", "updatedPath", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nArchiveAttachmentConverter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveAttachmentConverter.kt\ncom/tm/androidcopysdk/converter/ArchiveAttachmentConverter\n+ 2 ContentExtensions.kt\ncom/tm/androidcopysdk/utils/ContentExtensions\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Cursor.kt\nandroidx/core/database/CursorKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,45:1\n40#2:46\n41#2:48\n45#2:50\n40#2:51\n41#2:53\n45#2:55\n40#2:56\n41#2:58\n45#2:60\n40#2:61\n41#2:63\n40#2:65\n41#2:67\n1#3:47\n1#3:52\n1#3:57\n1#3:62\n1#3:66\n1#3:69\n112#4:49\n112#4:54\n112#4:59\n112#4:64\n112#4:68\n288#5,2:70\n766#5:72\n857#5,2:73\n*S KotlinDebug\n*F\n+ 1 ArchiveAttachmentConverter.kt\ncom/tm/androidcopysdk/converter/ArchiveAttachmentConverter\n*L\n16#1:46\n16#1:48\n17#1:50\n17#1:51\n17#1:53\n18#1:55\n18#1:56\n18#1:58\n19#1:60\n19#1:61\n19#1:63\n20#1:65\n20#1:67\n16#1:47\n17#1:52\n18#1:57\n19#1:62\n20#1:66\n16#1:49\n17#1:54\n18#1:59\n19#1:64\n20#1:68\n24#1:70,2\n34#1:72\n34#1:73,2\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/converter/ArchiveAttachmentConverter.class */
public final class ArchiveAttachmentConverter {
    @NotNull
    public final List<ArchiveAttachment> convert(@NotNull Cursor cursor) {
        String str;
        List fileNames;
        String str2;
        String str3;
        String str4;
        String str5;
        MessageContentProvider.MessageDeliveryStatus messageDeliveryStatus;
        String str6;
        Object obj;
        Intrinsics.checkNotNullParameter(cursor, "cursor");
        ContentExtensions contentExtensions = ContentExtensions.INSTANCE;
        Integer valueOf = Integer.valueOf(cursor.getColumnIndex("_data"));
        int it$iv = valueOf.intValue();
        Integer num = it$iv >= 0 ? valueOf : null;
        if (num != null) {
            int index$iv = num.intValue();
            str = cursor.isNull(index$iv) ? null : cursor.getString(index$iv);
        } else {
            str = null;
        }
        if (str == null || (fileNames = StringsKt.split$default(str, new String[]{";"}, false, 0, 6, (Object) null)) == null) {
            return CollectionsKt.emptyList();
        }
        ContentExtensions contentExtensions2 = ContentExtensions.INSTANCE;
        Integer valueOf2 = Integer.valueOf(cursor.getColumnIndex("_PATH"));
        int it$iv$iv = valueOf2.intValue();
        Integer num2 = it$iv$iv >= 0 ? valueOf2 : null;
        if (num2 != null) {
            int index$iv$iv = num2.intValue();
            str2 = cursor.isNull(index$iv$iv) ? null : cursor.getString(index$iv$iv);
        } else {
            str2 = null;
        }
        if (str2 == null) {
            str2 = "";
        }
        List paths = StringsKt.split$default(str2, new String[]{";"}, false, 0, 6, (Object) null);
        ContentExtensions contentExtensions3 = ContentExtensions.INSTANCE;
        Integer valueOf3 = Integer.valueOf(cursor.getColumnIndex("updated_files"));
        int it$iv$iv2 = valueOf3.intValue();
        Integer num3 = it$iv$iv2 >= 0 ? valueOf3 : null;
        if (num3 != null) {
            int index$iv$iv2 = num3.intValue();
            str3 = cursor.isNull(index$iv$iv2) ? null : cursor.getString(index$iv$iv2);
        } else {
            str3 = null;
        }
        if (str3 == null) {
            str3 = "";
        }
        Iterable updatedPaths = StringsKt.split$default(str3, new String[]{";"}, false, 0, 6, (Object) null);
        ContentExtensions contentExtensions4 = ContentExtensions.INSTANCE;
        Integer valueOf4 = Integer.valueOf(cursor.getColumnIndex("mime"));
        int it$iv$iv3 = valueOf4.intValue();
        Integer num4 = it$iv$iv3 >= 0 ? valueOf4 : null;
        if (num4 != null) {
            int index$iv$iv3 = num4.intValue();
            str4 = cursor.isNull(index$iv$iv3) ? null : cursor.getString(index$iv$iv3);
        } else {
            str4 = null;
        }
        if (str4 == null) {
            str4 = "";
        }
        List mimeTypes = StringsKt.split$default(str4, new String[]{";"}, false, 0, 6, (Object) null);
        ContentExtensions contentExtensions5 = ContentExtensions.INSTANCE;
        Integer valueOf5 = Integer.valueOf(cursor.getColumnIndex("status"));
        int it$iv2 = valueOf5.intValue();
        Integer num5 = it$iv2 >= 0 ? valueOf5 : null;
        if (num5 != null) {
            int index$iv2 = num5.intValue();
            str5 = cursor.isNull(index$iv2) ? null : cursor.getString(index$iv2);
        } else {
            str5 = null;
        }
        String it = str5;
        String str7 = it;
        String str8 = !(str7 == null || str7.length() == 0) ? it : null;
        if (str8 != null) {
            String p0 = str8;
            messageDeliveryStatus = MessageContentProvider.MessageDeliveryStatus.valueOf(p0);
        } else {
            messageDeliveryStatus = null;
        }
        MessageContentProvider.MessageDeliveryStatus status = messageDeliveryStatus;
        int max = Math.max(fileNames.size(), Math.max(paths.size(), mimeTypes.size()));
        ArrayList arrayList = new ArrayList(max);
        for (int i = 0; i < max; i++) {
            int it2 = i;
            String it3 = (String) CollectionsKt.getOrNull(mimeTypes, it2);
            String mimeType = it3 != null ? it3.length() > 0 ? it3 : null : null;
            String it4 = (String) CollectionsKt.getOrNull(paths, it2);
            String sourcePath = it4 != null ? it4.length() > 0 ? it4 : null : null;
            if (sourcePath != null) {
                String name = new File(sourcePath).getName();
                if (name != null) {
                    Intrinsics.checkNotNull(name);
                    Iterable $this$firstOrNull$iv = updatedPaths;
                    Iterator it5 = $this$firstOrNull$iv.iterator();
                    while (true) {
                        if (!it5.hasNext()) {
                            obj = null;
                            break;
                        }
                        Object element$iv = it5.next();
                        if (Intrinsics.areEqual((String) element$iv, name)) {
                            obj = element$iv;
                            break;
                        }
                    }
                    str6 = (String) obj;
                    String updatedPath = str6;
                    arrayList.add(new ArchiveAttachment(null, ArchiveAttachmentType.Companion.fromMimeTypeOrUnknown(mimeType), mimeType, sourcePath, updatedPath, getMediaStatus(status, sourcePath, updatedPath), false));
                }
            }
            str6 = null;
            String updatedPath2 = str6;
            arrayList.add(new ArchiveAttachment(null, ArchiveAttachmentType.Companion.fromMimeTypeOrUnknown(mimeType), mimeType, sourcePath, updatedPath2, getMediaStatus(status, sourcePath, updatedPath2), false));
        }
        ArrayList $this$filter$iv = arrayList;
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            ArchiveAttachment it6 = (ArchiveAttachment) element$iv$iv;
            if ((it6.getSourcePath() == null && it6.getArchivePath() == null && it6.getContentType() == null) ? false : true) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        return (List) destination$iv$iv;
    }

    private final MessageAttachmentStatus getMediaStatus(MessageContentProvider.MessageDeliveryStatus status, String sourcePath, String updatedPath) {
        String str = sourcePath;
        if (!(str == null || str.length() == 0)) {
            String str2 = updatedPath;
            if (!(str2 == null || str2.length() == 0)) {
                return MessageAttachmentStatus.Success;
            }
        }
        if (status == MessageContentProvider.MessageDeliveryStatus.NotReadyToBeDelivered) {
            return MessageAttachmentStatus.Loading;
        }
        return MessageAttachmentStatus.Success;
    }
}
