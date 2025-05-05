package com.tm.androidcopysdk.database;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Looper;
import com.tm.androidcopysdk.MessageType;
import com.tm.androidcopysdk.api.IArchiveMessageDao;
import com.tm.androidcopysdk.api.IArchiveMessageHeaderDao;
import com.tm.androidcopysdk.converter.ArchiveMessageConverter;
import com.tm.androidcopysdk.model.ArchiveMessage;
import com.tm.androidcopysdk.model.ArchiveMessageHeader;
import com.tm.androidcopysdk.model.ArchiveMessageIdentifier;
import com.tm.androidcopysdk.model.ArchiveMessageType;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.ContentExtensions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ArchiveMessageDao.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000e\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\r\n\u0002\b\u0003\u0018��2\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0002J\u0012\u0010\u0011\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0012\u001a\u00020\u0002H\u0016J\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00142\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00020\u0014H\u0016J \u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0018\u001a\u00020\u00192\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00020\u0014H\u0002J\u0010\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u0012\u001a\u00020\u0002H\u0002J\u0010\u0010\u001c\u001a\u00020\u00192\u0006\u0010\u001d\u001a\u00020\u0002H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n��R\u0016\u0010\n\u001a\n \f*\u0004\u0018\u00010\u000b0\u000bX\u0082\u0004¢\u0006\u0002\n��¨\u0006\u001e"}, d2 = {"Lcom/tm/androidcopysdk/database/ArchiveMessageDao;", "Lcom/tm/androidcopysdk/api/IArchiveMessageDao;", "Lcom/tm/androidcopysdk/model/ArchiveMessageIdentifier;", "contentResolver", "Landroid/content/ContentResolver;", "messageHeaderDao", "Lcom/tm/androidcopysdk/api/IArchiveMessageHeaderDao;", "(Landroid/content/ContentResolver;Lcom/tm/androidcopysdk/api/IArchiveMessageHeaderDao;)V", "converter", "Lcom/tm/androidcopysdk/converter/ArchiveMessageConverter;", "uri", "Landroid/net/Uri;", "kotlin.jvm.PlatformType", "deserialize", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", "cursor", "Landroid/database/Cursor;", "find", "id", "findAll", "", "ids", "getSelection", "", "type", "Lcom/tm/androidcopysdk/MessageType;", "getSelectionLike", "", "getType", "identifier", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nArchiveMessageDao.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveMessageDao.kt\ncom/tm/androidcopysdk/database/ArchiveMessageDao\n+ 2 ContentExtensions.kt\ncom/tm/androidcopysdk/utils/ContentExtensions\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,65:1\n81#2,20:66\n81#2,20:106\n3190#3,10:86\n3190#3,10:96\n1603#3,9:126\n1855#3:135\n766#3:136\n857#3,2:137\n1856#3:140\n1612#3:141\n1#4:139\n1#4:142\n*S KotlinDebug\n*F\n+ 1 ArchiveMessageDao.kt\ncom/tm/androidcopysdk/database/ArchiveMessageDao\n*L\n30#1:66,20\n40#1:106,20\n36#1:86,10\n37#1:96,10\n41#1:126,9\n41#1:135\n41#1:136\n41#1:137,2\n41#1:140\n41#1:141\n41#1:139\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/ArchiveMessageDao.class */
public final class ArchiveMessageDao implements IArchiveMessageDao<ArchiveMessageIdentifier> {
    @NotNull
    private final ContentResolver contentResolver;
    @NotNull
    private final IArchiveMessageHeaderDao messageHeaderDao;
    private final Uri uri;
    @NotNull
    private final ArchiveMessageConverter converter;

    /* compiled from: ArchiveMessageDao.kt */
    @Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK)
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/ArchiveMessageDao$WhenMappings.class */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[ArchiveMessageType.values().length];
            try {
                iArr[ArchiveMessageType.Sms.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[ArchiveMessageType.Call.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public ArchiveMessageDao(@NotNull ContentResolver contentResolver, @NotNull IArchiveMessageHeaderDao messageHeaderDao) {
        Intrinsics.checkNotNullParameter(contentResolver, "contentResolver");
        Intrinsics.checkNotNullParameter(messageHeaderDao, "messageHeaderDao");
        this.contentResolver = contentResolver;
        this.messageHeaderDao = messageHeaderDao;
        this.uri = MessageContentProvider.CONTENT_URI_MESSAGES;
        this.converter = new ArchiveMessageConverter();
    }

    @Override // com.tm.androidcopysdk.api.ISharedDao
    @Nullable
    public ArchiveMessage find(@NotNull ArchiveMessageIdentifier id) {
        Intrinsics.checkNotNullParameter(id, "id");
        String selection = "type = '" + getType(id).name() + "' AND " + ((Object) getSelectionLike(id));
        ArchiveMessageConverter archiveMessageConverter = this.converter;
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
                            ArchiveMessage deserialize = deserialize($this$iterate$iv$iv);
                            if (deserialize != null) {
                                result$iv$iv.add(deserialize);
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
            return archiveMessageConverter.convertFullMessage(id, result$iv$iv);
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }

    @Override // com.tm.androidcopysdk.api.ISharedDao
    @NotNull
    public List<ArchiveMessage> findAll(@NotNull List<ArchiveMessageIdentifier> list) {
        Intrinsics.checkNotNullParameter(list, "ids");
        if (list.isEmpty()) {
            return CollectionsKt.emptyList();
        }
        List<ArchiveMessageIdentifier> $this$partition$iv = list;
        ArrayList first$iv = new ArrayList();
        ArrayList second$iv = new ArrayList();
        for (Object element$iv : $this$partition$iv) {
            ArchiveMessageIdentifier it = (ArchiveMessageIdentifier) element$iv;
            if (it.getTransportType() == ArchiveMessageType.Sms) {
                first$iv.add(element$iv);
            } else {
                second$iv.add(element$iv);
            }
        }
        Pair pair = new Pair(first$iv, second$iv);
        List sms = (List) pair.component1();
        Iterable other = (List) pair.component2();
        Iterable $this$partition$iv2 = other;
        ArrayList first$iv2 = new ArrayList();
        ArrayList second$iv2 = new ArrayList();
        for (Object element$iv2 : $this$partition$iv2) {
            ArchiveMessageIdentifier it2 = (ArchiveMessageIdentifier) element$iv2;
            if (it2.getTransportType() == ArchiveMessageType.Call) {
                first$iv2.add(element$iv2);
            } else {
                second$iv2.add(element$iv2);
            }
        }
        Pair pair2 = new Pair(first$iv2, second$iv2);
        List call = (List) pair2.component1();
        List mms = (List) pair2.component2();
        String selection = CollectionsKt.joinToString$default(CollectionsKt.listOfNotNull(new String[]{getSelection(MessageType.SMS, sms), getSelection(MessageType.MMS, mms), getSelection(MessageType.CallLog, call)}), " ", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null);
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
                            ArchiveMessage deserialize = deserialize($this$iterate$iv$iv);
                            if (deserialize != null) {
                                result$iv$iv.add(deserialize);
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
            Iterable messages = CollectionsKt.distinct(result$iv$iv);
            List<ArchiveMessageIdentifier> $this$mapNotNull$iv = list;
            Collection destination$iv$iv = new ArrayList();
            for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
                ArchiveMessageIdentifier id = (ArchiveMessageIdentifier) element$iv$iv$iv;
                ArchiveMessageConverter archiveMessageConverter = this.converter;
                Iterable $this$filter$iv = messages;
                Collection destination$iv$iv2 = new ArrayList();
                for (Object element$iv$iv : $this$filter$iv) {
                    ArchiveMessage it3 = (ArchiveMessage) element$iv$iv;
                    String uniqueId = it3.getUniqueId();
                    if (uniqueId != null ? StringsKt.contains$default(uniqueId, id.rawId(), false, 2, (Object) null) : false) {
                        destination$iv$iv2.add(element$iv$iv);
                    }
                }
                ArchiveMessage convertFullMessage = archiveMessageConverter.convertFullMessage(id, (List) destination$iv$iv2);
                if (convertFullMessage != null) {
                    destination$iv$iv.add(convertFullMessage);
                }
            }
            return (List) destination$iv$iv;
        }
        throw new IllegalArgumentException("Failed requirement.".toString());
    }

    private final String getSelection(MessageType type, List<ArchiveMessageIdentifier> list) {
        if (list.isEmpty()) {
            return null;
        }
        return "type = '" + type.name() + "' AND " + CollectionsKt.joinToString$default(list, " OR ", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, new Function1<ArchiveMessageIdentifier, CharSequence>() { // from class: com.tm.androidcopysdk.database.ArchiveMessageDao$getSelection$1
            /* JADX INFO: Access modifiers changed from: package-private */
            {
                super(1);
            }

            @NotNull
            public final CharSequence invoke(@NotNull ArchiveMessageIdentifier it) {
                CharSequence selectionLike;
                Intrinsics.checkNotNullParameter(it, "it");
                selectionLike = ArchiveMessageDao.this.getSelectionLike(it);
                return selectionLike;
            }
        }, 30, (Object) null) + ')';
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final CharSequence getSelectionLike(ArchiveMessageIdentifier id) {
        return "native_message_id LIKE '%" + id.rawId() + "%'";
    }

    private final MessageType getType(ArchiveMessageIdentifier identifier) {
        ArchiveMessageType transportType = identifier.getTransportType();
        switch (transportType == null ? -1 : WhenMappings.$EnumSwitchMapping$0[transportType.ordinal()]) {
            case 1:
                return MessageType.SMS;
            case 2:
                return MessageType.CallLog;
            default:
                return MessageType.MMS;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final ArchiveMessage deserialize(Cursor cursor) {
        List<ArchiveMessageHeader> list;
        ArchiveMessage message = this.converter.convert(cursor);
        ArchiveMessage archiveMessage = message;
        String p0 = message.getUniqueId();
        if (p0 != null) {
            archiveMessage = archiveMessage;
            list = this.messageHeaderDao.findAllByMessageId(p0);
        } else {
            list = null;
        }
        archiveMessage.setHeaders(list);
        return message;
    }
}
