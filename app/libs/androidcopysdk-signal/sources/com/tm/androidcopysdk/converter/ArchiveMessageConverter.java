package com.tm.androidcopysdk.converter;

import android.database.Cursor;
import com.tm.androidcopysdk.MessageType;
import com.tm.androidcopysdk.model.ArchiveMessage;
import com.tm.androidcopysdk.model.ArchiveMessageIdentifier;
import com.tm.androidcopysdk.model.ArchiveMessageType;
import com.tm.androidcopysdk.model.Direction;
import com.tm.androidcopysdk.model.MessageStatus;
import com.tm.androidcopysdk.model.Timestamp;
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
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveMessageConverter.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��D\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010 \n��\b��\u0018��2\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010J\u001e\u0010\u0011\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0012\u001a\u00020\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0015R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n��¨\u0006\u0016"}, d2 = {"Lcom/tm/androidcopysdk/converter/ArchiveMessageConverter;", "", "()V", "attachmentConverter", "Lcom/tm/androidcopysdk/converter/ArchiveAttachmentConverter;", "callInfoConverter", "Lcom/tm/androidcopysdk/converter/ArchiveCallInfoConverter;", "chatConverter", "Lcom/tm/androidcopysdk/converter/ArchiveChatConverter;", "editConverter", "Lcom/tm/androidcopysdk/converter/ArchiveEditMessageConverter;", "recipientConverter", "Lcom/tm/androidcopysdk/converter/ArchiveRecipientConverter;", "convert", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", "cursor", "Landroid/database/Cursor;", "convertFullMessage", "id", "Lcom/tm/androidcopysdk/model/ArchiveMessageIdentifier;", "messages", "", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nArchiveMessageConverter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveMessageConverter.kt\ncom/tm/androidcopysdk/converter/ArchiveMessageConverter\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ContentExtensions.kt\ncom/tm/androidcopysdk/utils/ContentExtensions\n+ 4 Cursor.kt\nandroidx/core/database/CursorKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,69:1\n1#2:70\n1#2:72\n1#2:76\n1#2:80\n1#2:84\n1#2:88\n1#2:92\n1#2:96\n40#3:71\n41#3:73\n40#3:75\n41#3:77\n40#3:79\n41#3:81\n40#3:83\n41#3:85\n40#3:87\n41#3:89\n40#3:91\n41#3:93\n40#3:95\n41#3:97\n112#4:74\n112#4:78\n112#4:82\n112#4:86\n112#4:90\n112#4:94\n112#4:98\n1855#5,2:99\n*S KotlinDebug\n*F\n+ 1 ArchiveMessageConverter.kt\ncom/tm/androidcopysdk/converter/ArchiveMessageConverter\n*L\n24#1:72\n25#1:76\n26#1:80\n27#1:84\n29#1:88\n35#1:92\n36#1:96\n24#1:71\n24#1:73\n25#1:75\n25#1:77\n26#1:79\n26#1:81\n27#1:83\n27#1:85\n29#1:87\n29#1:89\n35#1:91\n35#1:93\n36#1:95\n36#1:97\n24#1:74\n25#1:78\n26#1:82\n27#1:86\n29#1:90\n35#1:94\n36#1:98\n59#1:99,2\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/converter/ArchiveMessageConverter.class */
public final class ArchiveMessageConverter {
    @NotNull
    private final ArchiveChatConverter chatConverter = new ArchiveChatConverter();
    @NotNull
    private final ArchiveRecipientConverter recipientConverter = new ArchiveRecipientConverter();
    @NotNull
    private final ArchiveAttachmentConverter attachmentConverter = new ArchiveAttachmentConverter();
    @NotNull
    private final ArchiveCallInfoConverter callInfoConverter = new ArchiveCallInfoConverter();
    @NotNull
    private final ArchiveEditMessageConverter editConverter = new ArchiveEditMessageConverter();

    @NotNull
    public final ArchiveMessage convert(@NotNull Cursor cursor) {
        String str;
        String str2;
        String str3;
        String str4;
        MessageType messageType;
        String str5;
        String str6;
        String str7;
        long j;
        Intrinsics.checkNotNullParameter(cursor, "cursor");
        Direction direction = Direction.Outgoing;
        ContentExtensions contentExtensions = ContentExtensions.INSTANCE;
        Integer valueOf = Integer.valueOf(cursor.getColumnIndex("direction"));
        int it$iv = valueOf.intValue();
        Integer num = it$iv >= 0 ? valueOf : null;
        if (num != null) {
            int index$iv = num.intValue();
            str = cursor.isNull(index$iv) ? null : cursor.getString(index$iv);
        } else {
            str = null;
        }
        Direction direction2 = Intrinsics.areEqual(str, "Out") ? direction : null;
        if (direction2 == null) {
            direction2 = Direction.Incoming;
        }
        Direction direction3 = direction2;
        ContentExtensions contentExtensions2 = ContentExtensions.INSTANCE;
        Integer valueOf2 = Integer.valueOf(cursor.getColumnIndex("native_message_id"));
        int it$iv2 = valueOf2.intValue();
        Integer num2 = it$iv2 >= 0 ? valueOf2 : null;
        if (num2 != null) {
            int index$iv2 = num2.intValue();
            str2 = cursor.isNull(index$iv2) ? null : cursor.getString(index$iv2);
        } else {
            str2 = null;
        }
        if (str2 == null) {
            throw new IllegalArgumentException("Required value was null.".toString());
        }
        String nativeId = str2;
        ContentExtensions contentExtensions3 = ContentExtensions.INSTANCE;
        Integer valueOf3 = Integer.valueOf(cursor.getColumnIndex("messageType"));
        int it$iv3 = valueOf3.intValue();
        Integer num3 = it$iv3 >= 0 ? valueOf3 : null;
        if (num3 != null) {
            int index$iv3 = num3.intValue();
            str3 = cursor.isNull(index$iv3) ? null : cursor.getString(index$iv3);
        } else {
            str3 = null;
        }
        ArchiveMessageType.Companion companion = ArchiveMessageType.Companion;
        ContentExtensions contentExtensions4 = ContentExtensions.INSTANCE;
        Integer valueOf4 = Integer.valueOf(cursor.getColumnIndex("type"));
        int it$iv4 = valueOf4.intValue();
        Integer num4 = it$iv4 >= 0 ? valueOf4 : null;
        if (num4 != null) {
            int index$iv4 = num4.intValue();
            str4 = cursor.isNull(index$iv4) ? null : cursor.getString(index$iv4);
        } else {
            str4 = null;
        }
        if (str4 != null) {
            String p0 = str4;
            companion = companion;
            messageType = MessageType.valueOf(p0);
        } else {
            messageType = null;
        }
        ArchiveMessageType messageType2 = companion.fromMessageTypeOrUnknown(messageType);
        ContentExtensions contentExtensions5 = ContentExtensions.INSTANCE;
        Integer valueOf5 = Integer.valueOf(cursor.getColumnIndex("_id"));
        int it$iv5 = valueOf5.intValue();
        Integer num5 = it$iv5 >= 0 ? valueOf5 : null;
        if (num5 != null) {
            int index$iv5 = num5.intValue();
            str5 = cursor.isNull(index$iv5) ? null : cursor.getString(index$iv5);
        } else {
            str5 = null;
        }
        if (str5 == null) {
            throw new IllegalArgumentException("Required value was null.".toString());
        }
        String str8 = str5;
        MessageStatus messageStatus = MessageStatus.Sent;
        ContentExtensions contentExtensions6 = ContentExtensions.INSTANCE;
        Integer valueOf6 = Integer.valueOf(cursor.getColumnIndex("body"));
        int it$iv6 = valueOf6.intValue();
        Integer num6 = it$iv6 >= 0 ? valueOf6 : null;
        if (num6 != null) {
            int index$iv6 = num6.intValue();
            str6 = cursor.isNull(index$iv6) ? null : cursor.getString(index$iv6);
        } else {
            str6 = null;
        }
        String str9 = str6;
        ContentExtensions contentExtensions7 = ContentExtensions.INSTANCE;
        Integer valueOf7 = Integer.valueOf(cursor.getColumnIndex("date"));
        int it$iv7 = valueOf7.intValue();
        Integer num7 = it$iv7 >= 0 ? valueOf7 : null;
        if (num7 != null) {
            int index$iv7 = num7.intValue();
            str7 = cursor.isNull(index$iv7) ? null : cursor.getString(index$iv7);
        } else {
            str7 = null;
        }
        if (str7 != null) {
            Long longOrNull = StringsKt.toLongOrNull(str7);
            if (longOrNull != null) {
                j = longOrNull.longValue();
                return new ArchiveMessage(str8, nativeId, null, messageType2, direction3, messageStatus, str9, new Timestamp(j, null, 2, null), false, StringsKt.startsWith$default(nativeId, ArchiveMessageIdentifier.DELETE_FOR_ME, false, 2, (Object) null), StringsKt.startsWith$default(nativeId, ArchiveMessageIdentifier.DELETE_FOR_ALL, false, 2, (Object) null), false, this.chatConverter.convert(cursor), this.recipientConverter.convertSender(cursor), this.recipientConverter.convertReceivers(cursor), this.attachmentConverter.convert(cursor), CollectionsKt.listOfNotNull(this.callInfoConverter.convert(cursor, messageType2)), null, null, null, 256, null);
            }
        }
        j = 0;
        return new ArchiveMessage(str8, nativeId, null, messageType2, direction3, messageStatus, str9, new Timestamp(j, null, 2, null), false, StringsKt.startsWith$default(nativeId, ArchiveMessageIdentifier.DELETE_FOR_ME, false, 2, (Object) null), StringsKt.startsWith$default(nativeId, ArchiveMessageIdentifier.DELETE_FOR_ALL, false, 2, (Object) null), false, this.chatConverter.convert(cursor), this.recipientConverter.convertSender(cursor), this.recipientConverter.convertReceivers(cursor), this.attachmentConverter.convert(cursor), CollectionsKt.listOfNotNull(this.callInfoConverter.convert(cursor, messageType2)), null, null, null, 256, null);
    }

    @Nullable
    public final ArchiveMessage convertFullMessage(@NotNull ArchiveMessageIdentifier id, @NotNull List<ArchiveMessage> list) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(list, "messages");
        if (list.isEmpty()) {
            return null;
        }
        String rawId = id.rawId();
        boolean isDeleted = false;
        boolean isRemoteDeleted = false;
        List edits = new ArrayList();
        ArchiveMessage archiveMessage = null;
        List<ArchiveMessage> $this$forEach$iv = list;
        for (Object element$iv : $this$forEach$iv) {
            ArchiveMessage it = (ArchiveMessage) element$iv;
            if (archiveMessage == null && Intrinsics.areEqual(it.getUniqueId(), rawId)) {
                archiveMessage = it;
            }
            isDeleted = isDeleted || it.isDeleted();
            isRemoteDeleted = isRemoteDeleted || it.isRemoteDeleted();
            String uniqueId = it.getUniqueId();
            if (uniqueId != null ? StringsKt.startsWith$default(uniqueId, ArchiveMessageIdentifier.EDIT, false, 2, (Object) null) : false) {
                edits.add(this.editConverter.convert(it));
            }
        }
        ArchiveMessage archiveMessage2 = archiveMessage;
        if (archiveMessage2 == null) {
            archiveMessage2 = (ArchiveMessage) CollectionsKt.first(list);
        }
        return ArchiveMessage.copy$default(archiveMessage2, null, null, null, null, null, null, null, null, false, isDeleted, isRemoteDeleted, false, null, null, null, null, null, null, !edits.isEmpty() ? edits : null, null, 784895, null);
    }
}
