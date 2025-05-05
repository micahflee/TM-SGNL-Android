package com.tm.androidcopysdk.converter;

import com.tm.androidcopysdk.DataGrabber;
import com.tm.androidcopysdk.Models.MessageDetailsArchive;
import com.tm.androidcopysdk.api.IFiler;
import com.tm.androidcopysdk.converter.MessageDetailsConverter;
import com.tm.androidcopysdk.database.DBKeepAliveQueryHelper;
import com.tm.androidcopysdk.model.ArchiveAttachment;
import com.tm.androidcopysdk.model.ArchiveMessage;
import com.tm.androidcopysdk.model.ArchiveMessageType;
import com.tm.androidcopysdk.model.ArchiveRecipient;
import com.tm.androidcopysdk.model.ChatType;
import com.tm.androidcopysdk.model.ClientType;
import com.tm.androidcopysdk.model.Direction;
import com.tm.androidcopysdk.model.MessageStatus;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.Contact;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: MessageDetailsConverter.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��@\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018�� \u001f2\u00020\u0001:\u0001\u001fB\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bH\u0002J\"\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\f2\u0006\u0010\u000e\u001a\u00020\bH\u0002J\u001a\u0010\u000f\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\fH\u0002J\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\fJ\u0010\u0010\u0012\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\u0013\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0018\u0010\u0014\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0015\u001a\u00020\bH\u0002J\u001a\u0010\u0016\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\fH\u0002J\u0012\u0010\u0017\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001a\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\u001d\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\f\u0010\u001e\u001a\u00020\b*\u00020\fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n��¨\u0006 "}, d2 = {"Lcom/tm/androidcopysdk/converter/MessageDetailsConverter;", "", "clientType", "Lcom/tm/androidcopysdk/model/ClientType;", "filer", "Lcom/tm/androidcopysdk/api/IFiler;", "(Lcom/tm/androidcopysdk/model/ClientType;Lcom/tm/androidcopysdk/api/IFiler;)V", "cleanupBodyFromUnusedCharacters", "", "body", "constructBody", "message", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", "existing", "archiveId", "constructSubject", "convert", "Lcom/tm/androidcopysdk/Models/MessageDetailsArchive;", "createDeleteMessagePrefix", "createEditMessagePrefix", "createMessagePrefix", "prefix", "getBody", "getChatName", "chatType", "Lcom/tm/androidcopysdk/model/ChatType;", "getListFileAsString", "getMessageType", "Lcom/tm/androidcopysdk/database/DBKeepAliveQueryHelper$MessageType;", "receiverName", "subject", "Companion", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nMessageDetailsConverter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MessageDetailsConverter.kt\ncom/tm/androidcopysdk/converter/MessageDetailsConverter\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,167:1\n1#2:168\n1549#3:169\n1620#3,3:170\n1549#3:175\n1620#3,3:176\n1549#3:181\n1620#3,3:182\n37#4,2:173\n37#4,2:179\n37#4,2:185\n*S KotlinDebug\n*F\n+ 1 MessageDetailsConverter.kt\ncom/tm/androidcopysdk/converter/MessageDetailsConverter\n*L\n27#1:169\n27#1:170,3\n39#1:175\n39#1:176,3\n40#1:181\n40#1:182,3\n27#1:173,2\n39#1:179,2\n40#1:185,2\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/converter/MessageDetailsConverter.class */
public final class MessageDetailsConverter {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final ClientType clientType;
    @NotNull
    private final IFiler filer;
    @NotNull
    private static final String DELETE_PREFIX = "DELETED For";
    @NotNull
    private static final String DELETE_FOR_ALL_PREFIX = "DELETED For All";
    @NotNull
    private static final String DELETE_FOR_ME_PREFIX = "DELETED For Me";
    @NotNull
    private static final String EDIT_PREFIX = "EDIT";
    @NotNull
    private static final String FILES_PREFIX = "Files:";
    @NotNull
    private static final String PREFIX_STATUS_UNKNOWN = "UNKNOWN";
    @NotNull
    private static final String PREFIX_STATUS_READ = "READ";
    @NotNull
    private static final String PREFIX_STATUS_UNREAD = "UNREAD";
    @NotNull
    private static final String DELETED_MESSAGE_BODY_ORIGINAL_ID_REFERENCE = "Original Message (Msg ID - %s)";
    @NotNull
    private static final String EDITED_MESSAGE_BODY_ORIGINAL_ID_REFERENCE = "Original Message ID: %s";
    @NotNull
    private static final String EDITED_MESSAGE_BODY_EDIT_ID = "Edit ID - %s";
    @NotNull
    private static final String EDITED_MESSAGE_BODY_LAST_EDIT_ID = "\nLast Edit ID - %s";
    @NotNull
    private static final String EDITED_MESSAGE_BODY_PREVIOUS_TEXT = "\nPrevious Text:\n%s";
    @NotNull
    private static final String EDITED_MESSAGE_BODY_EDITED_TEXT = "Edited Text:\n%s";

    /* compiled from: MessageDetailsConverter.kt */
    @Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK)
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/converter/MessageDetailsConverter$WhenMappings.class */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[ChatType.values().length];
            try {
                iArr[ChatType.Broadcast.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public MessageDetailsConverter(@NotNull ClientType clientType, @NotNull IFiler filer) {
        Intrinsics.checkNotNullParameter(clientType, "clientType");
        Intrinsics.checkNotNullParameter(filer, "filer");
        this.clientType = clientType;
        this.filer = filer;
    }

    @NotNull
    public final MessageDetailsArchive convert(@NotNull ArchiveMessage message, @Nullable ArchiveMessage existing) {
        Intrinsics.checkNotNullParameter(message, "message");
        Iterable it = message.getReceivers();
        Iterable iterable = !((Collection) it).isEmpty() ? it : null;
        if (iterable == null) {
            iterable = CollectionsKt.listOf(new ArchiveRecipient(null, null, null, null, 12, null));
        }
        Iterable receivers = iterable;
        String archiveId = message.getArchiveId(existing);
        String str = message.getDirection() == Direction.Outgoing ? "1" : null;
        if (str == null) {
            str = "0";
        }
        Iterable $this$map$iv = receivers;
        String str2 = str;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            destination$iv$iv.add(((ArchiveRecipient) item$iv$iv).getCleanAddress());
        }
        Collection $this$toTypedArray$iv = (List) destination$iv$iv;
        String[] strArr = (String[]) $this$toTypedArray$iv.toArray(new String[0]);
        String senderAddress = message.getSenderAddress();
        String constructBody = constructBody(message, existing, archiveId);
        String valueOf = String.valueOf(message.getTimestamp().milliseconds());
        String constructSubject = constructSubject(message, existing);
        String cleanAccountPhoneNumber = message.getCleanAccountPhoneNumber();
        DataGrabber.CHAT_MODE chatMode = message.getChat().getType().toChatMode();
        String name = message.getChat().getName();
        String id = message.getChat().getId();
        Contact contact = message.getSender().toContact();
        String cleanAddress = message.getSender().getCleanAddress();
        Iterable $this$map$iv2 = receivers;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv2, 10));
        for (Object item$iv$iv2 : $this$map$iv2) {
            destination$iv$iv2.add(((ArchiveRecipient) item$iv$iv2).toContact());
        }
        Collection $this$toTypedArray$iv2 = (List) destination$iv$iv2;
        Contact[] contactArr = (Contact[]) $this$toTypedArray$iv2.toArray(new Contact[0]);
        Iterable $this$map$iv3 = receivers;
        Collection destination$iv$iv3 = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv3, 10));
        for (Object item$iv$iv3 : $this$map$iv3) {
            destination$iv$iv3.add(((ArchiveRecipient) item$iv$iv3).getCleanAddress());
        }
        Collection $this$toTypedArray$iv3 = (List) destination$iv$iv3;
        return new MessageDetailsArchive(str2, strArr, senderAddress, constructBody, archiveId, valueOf, constructSubject, cleanAccountPhoneNumber, chatMode, name, id, contact, cleanAddress, contactArr, (String[]) $this$toTypedArray$iv3.toArray(new String[0]), getMessageType(message));
    }

    private final DBKeepAliveQueryHelper.MessageType getMessageType(ArchiveMessage message) {
        if (message.hasDeletions()) {
            return DBKeepAliveQueryHelper.MessageType.EVENT;
        }
        return DBKeepAliveQueryHelper.MessageType.APP_MESSAGE;
    }

    private final String subject(ArchiveMessage $this$subject) {
        String prefix = this.clientType.getKey() + " message";
        return prefix + " from " + $this$subject.getSenderAddress() + " to " + receiverName($this$subject);
    }

    private final String receiverName(ArchiveMessage message) {
        if (message.getChat().getType().isNormalChat()) {
            ArchiveRecipient archiveRecipient = (ArchiveRecipient) CollectionsKt.firstOrNull(message.getReceivers());
            if (archiveRecipient != null) {
                String cleanAddress = archiveRecipient.getCleanAddress();
                if (cleanAddress != null) {
                    return cleanAddress;
                }
            }
            return "";
        }
        return CollectionsKt.joinToString$default(CollectionsKt.listOfNotNull(new String[]{getChatName(message.getChat().getType()), message.getChat().getName()}), " ", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null);
    }

    private final String getChatName(ChatType chatType) {
        if (WhenMappings.$EnumSwitchMapping$0[chatType.ordinal()] == 1) {
            if (this.clientType == ClientType.Telegram) {
                return "channel";
            }
            return null;
        }
        return chatType.getKey();
    }

    private final String constructSubject(ArchiveMessage message, ArchiveMessage existing) {
        String subject = subject(message);
        if (message.hasDeletions()) {
            return createDeleteMessagePrefix(message) + ' ' + subject;
        }
        if (message.isNewEdit(existing)) {
            return createEditMessagePrefix(message) + ' ' + subject;
        }
        return subject;
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x012a, code lost:
        if (r0 == null) goto L39;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x014d, code lost:
        if (r0 == null) goto L34;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x0197, code lost:
        if (r0 == null) goto L33;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final java.lang.String constructBody(com.tm.androidcopysdk.model.ArchiveMessage r6, com.tm.androidcopysdk.model.ArchiveMessage r7, java.lang.String r8) {
        /*
            Method dump skipped, instructions count: 597
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.converter.MessageDetailsConverter.constructBody(com.tm.androidcopysdk.model.ArchiveMessage, com.tm.androidcopysdk.model.ArchiveMessage, java.lang.String):java.lang.String");
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0043, code lost:
        if (r0 == null) goto L18;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private final java.lang.String getBody(com.tm.androidcopysdk.model.ArchiveMessage r4, com.tm.androidcopysdk.model.ArchiveMessage r5) {
        /*
            r3 = this;
            r0 = r4
            java.lang.String r0 = r0.getBody()
            r6 = r0
            r0 = r4
            boolean r0 = r0.isRemoteDeleted()
            if (r0 == 0) goto L55
            r0 = r6
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r7 = r0
            r0 = r7
            if (r0 == 0) goto L21
            r0 = r7
            int r0 = r0.length()
            if (r0 != 0) goto L25
        L21:
            r0 = 1
            goto L26
        L25:
            r0 = 0
        L26:
            if (r0 == 0) goto L55
            r0 = r5
            r1 = r0
            if (r1 == 0) goto L46
            java.util.List r0 = r0.getEdits()
            r1 = r0
            if (r1 == 0) goto L46
            java.lang.Object r0 = kotlin.collections.CollectionsKt.lastOrNull(r0)
            com.tm.androidcopysdk.model.ArchiveMessageEdit r0 = (com.tm.androidcopysdk.model.ArchiveMessageEdit) r0
            r1 = r0
            if (r1 == 0) goto L46
            java.lang.String r0 = r0.getBody()
            r1 = r0
            if (r1 != 0) goto L54
        L46:
        L47:
            r0 = r5
            r1 = r0
            if (r1 == 0) goto L52
            java.lang.String r0 = r0.getBody()
            goto L54
        L52:
            r0 = 0
        L54:
            r6 = r0
        L55:
            r0 = r6
            java.lang.CharSequence r0 = (java.lang.CharSequence) r0
            r7 = r0
            r0 = r7
            if (r0 == 0) goto L6a
            r0 = r7
            int r0 = r0.length()
            if (r0 != 0) goto L6e
        L6a:
            r0 = 1
            goto L6f
        L6e:
            r0 = 0
        L6f:
            if (r0 == 0) goto L76
            java.lang.String r0 = ""
            return r0
        L76:
            r0 = r3
            r1 = r6
            java.lang.String r0 = r0.cleanupBodyFromUnusedCharacters(r1)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.converter.MessageDetailsConverter.getBody(com.tm.androidcopysdk.model.ArchiveMessage, com.tm.androidcopysdk.model.ArchiveMessage):java.lang.String");
    }

    private final String cleanupBodyFromUnusedCharacters(String body) {
        return StringsKt.replace$default(StringsKt.replace$default(body, "\u2069", "", false, 4, (Object) null), "\u2068", "", false, 4, (Object) null);
    }

    private final String createDeleteMessagePrefix(ArchiveMessage message) {
        String str = message.isRemoteDeleted() ? DELETE_FOR_ALL_PREFIX : null;
        if (str == null) {
            str = DELETE_FOR_ME_PREFIX;
        }
        String prefix = str;
        return createMessagePrefix(message, prefix);
    }

    private final String createEditMessagePrefix(ArchiveMessage message) {
        return createMessagePrefix(message, EDIT_PREFIX);
    }

    private final String createMessagePrefix(ArchiveMessage message, String prefix) {
        String statusPrefix = "UNKNOWN";
        if (message.getChat().getType().isNormalChat()) {
            statusPrefix = message.getStatus() == MessageStatus.Read ? PREFIX_STATUS_READ : PREFIX_STATUS_UNREAD;
        }
        return prefix + ' ' + statusPrefix;
    }

    private final String getListFileAsString(final ArchiveMessage message) {
        if (message.getType() != ArchiveMessageType.Mms || message.getAttachments().isEmpty()) {
            return "";
        }
        return "\nFiles:\n" + CollectionsKt.joinToString$default(message.getAttachments(), "\n", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, new Function1<ArchiveAttachment, CharSequence>() { // from class: com.tm.androidcopysdk.converter.MessageDetailsConverter$getListFileAsString$1
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(1);
            }

            @NotNull
            public final CharSequence invoke(@NotNull ArchiveAttachment it) {
                IFiler iFiler;
                Intrinsics.checkNotNullParameter(it, "it");
                MessageDetailsConverter.Companion companion = MessageDetailsConverter.Companion;
                ArchiveMessage archiveMessage = ArchiveMessage.this;
                iFiler = this.filer;
                return companion.getFileNameWithType(archiveMessage, iFiler, it);
            }
        }, 30, (Object) null);
    }

    /* compiled from: MessageDetailsConverter.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��(\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J \u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\u0004H\u0002J\u001a\u0010\u0017\u001a\u00020\u0004*\u00020\u00182\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u001aR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\u0007\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\b\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\t\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\n\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\f\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\r\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\u000e\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\u000f\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\u0010\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\u0011\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��¨\u0006\u001b"}, d2 = {"Lcom/tm/androidcopysdk/converter/MessageDetailsConverter$Companion;", "", "()V", "DELETED_MESSAGE_BODY_ORIGINAL_ID_REFERENCE", "", "DELETE_FOR_ALL_PREFIX", "DELETE_FOR_ME_PREFIX", "DELETE_PREFIX", "EDITED_MESSAGE_BODY_EDITED_TEXT", "EDITED_MESSAGE_BODY_EDIT_ID", "EDITED_MESSAGE_BODY_LAST_EDIT_ID", "EDITED_MESSAGE_BODY_ORIGINAL_ID_REFERENCE", "EDITED_MESSAGE_BODY_PREVIOUS_TEXT", "EDIT_PREFIX", "FILES_PREFIX", "PREFIX_STATUS_READ", "PREFIX_STATUS_UNKNOWN", "PREFIX_STATUS_UNREAD", "generateAttachmentName", "filer", "Lcom/tm/androidcopysdk/api/IFiler;", CallLogMessageRecorder.messageId_key, "attachmentId", "getFileNameWithType", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", CallLogMessageRecorder.attachment_key, "Lcom/tm/androidcopysdk/model/ArchiveAttachment;", "androidcopysdk_signalRelease"})
    @SourceDebugExtension({"SMAP\nMessageDetailsConverter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MessageDetailsConverter.kt\ncom/tm/androidcopysdk/converter/MessageDetailsConverter$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,167:1\n1#2:168\n*E\n"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/converter/MessageDetailsConverter$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:13:0x0040, code lost:
            if (r0 == null) goto L15;
         */
        @org.jetbrains.annotations.NotNull
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final java.lang.String getFileNameWithType(@org.jetbrains.annotations.NotNull com.tm.androidcopysdk.model.ArchiveMessage r7, @org.jetbrains.annotations.NotNull com.tm.androidcopysdk.api.IFiler r8, @org.jetbrains.annotations.NotNull com.tm.androidcopysdk.model.ArchiveAttachment r9) {
            /*
                r6 = this;
                r0 = r7
                java.lang.String r1 = "<this>"
                kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r1)
                r0 = r8
                java.lang.String r1 = "filer"
                kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r1)
                r0 = r9
                java.lang.String r1 = "attachment"
                kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r1)
                r0 = r9
                java.lang.String r0 = r0.getId()
                r1 = r0
                if (r1 == 0) goto L43
                r11 = r0
                r0 = r11
                r12 = r0
                r0 = 0
                r13 = r0
                r0 = r12
                java.lang.CharSequence r0 = (java.lang.CharSequence) r0
                int r0 = r0.length()
                if (r0 <= 0) goto L34
                r0 = 1
                goto L35
            L34:
                r0 = 0
            L35:
                if (r0 == 0) goto L3e
                r0 = r11
                goto L3f
            L3e:
                r0 = 0
            L3f:
                r1 = r0
                if (r1 != 0) goto L46
            L43:
            L44:
                java.lang.String r0 = "0"
            L46:
                r10 = r0
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r1 = r0
                r1.<init>()
                r1 = r6
                r2 = r8
                r3 = r7
                java.lang.String r3 = r3.getId()
                r4 = r10
                java.lang.String r1 = r1.generateAttachmentName(r2, r3, r4)
                java.lang.StringBuilder r0 = r0.append(r1)
                r1 = 46
                java.lang.StringBuilder r0 = r0.append(r1)
                r1 = r8
                r2 = r9
                java.lang.String r2 = r2.getContentType()
                r3 = r9
                java.lang.String r3 = r3.getSourcePath()
                java.lang.String r1 = r1.getExtensionFromMimeType(r2, r3)
                java.lang.StringBuilder r0 = r0.append(r1)
                java.lang.String r0 = r0.toString()
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.converter.MessageDetailsConverter.Companion.getFileNameWithType(com.tm.androidcopysdk.model.ArchiveMessage, com.tm.androidcopysdk.api.IFiler, com.tm.androidcopysdk.model.ArchiveAttachment):java.lang.String");
        }

        private final String generateAttachmentName(IFiler filer, String messageId, String attachmentId) {
            return filer.archiveAttachmentFileTemplatePrefix() + attachmentId + '_' + messageId;
        }
    }
}
