package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
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
/* compiled from: ArchiveMessage.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��v\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010 \n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b6\b\u0086\b\u0018��2\u00020\u0001Bß\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\u0006\u0010\u0011\u001a\u00020\u0010\u0012\u0006\u0010\u0012\u001a\u00020\u0010\u0012\u0006\u0010\u0013\u001a\u00020\u0010\u0012\u0006\u0010\u0014\u001a\u00020\u0015\u0012\u0006\u0010\u0016\u001a\u00020\u0017\u0012\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00170\u0019\u0012\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u0019\u0012\u0010\b\u0002\u0010\u001c\u001a\n\u0012\u0004\u0012\u00020\u001d\u0018\u00010\u0019\u0012\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001f\u0012\u0010\b\u0002\u0010 \u001a\n\u0012\u0004\u0012\u00020!\u0018\u00010\u0019\u0012\u0010\b\u0002\u0010\"\u001a\n\u0012\u0004\u0012\u00020#\u0018\u00010\u0019¢\u0006\u0002\u0010$J\t\u0010P\u001a\u00020\u0003HÆ\u0003J\t\u0010Q\u001a\u00020\u0010HÆ\u0003J\t\u0010R\u001a\u00020\u0010HÆ\u0003J\t\u0010S\u001a\u00020\u0010HÆ\u0003J\t\u0010T\u001a\u00020\u0015HÆ\u0003J\t\u0010U\u001a\u00020\u0017HÆ\u0003J\u000f\u0010V\u001a\b\u0012\u0004\u0012\u00020\u00170\u0019HÆ\u0003J\u000f\u0010W\u001a\b\u0012\u0004\u0012\u00020\u001b0\u0019HÆ\u0003J\u0011\u0010X\u001a\n\u0012\u0004\u0012\u00020\u001d\u0018\u00010\u0019HÆ\u0003J\u000b\u0010Y\u001a\u0004\u0018\u00010\u001fHÆ\u0003J\u0011\u0010Z\u001a\n\u0012\u0004\u0012\u00020!\u0018\u00010\u0019HÆ\u0003J\u000b\u0010[\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0011\u0010\\\u001a\n\u0012\u0004\u0012\u00020#\u0018\u00010\u0019HÆ\u0003J\u000b\u0010]\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010^\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\t\u0010_\u001a\u00020\tHÆ\u0003J\t\u0010`\u001a\u00020\u000bHÆ\u0003J\u000b\u0010a\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010b\u001a\u00020\u000eHÆ\u0003J\t\u0010c\u001a\u00020\u0010HÆ\u0003Jÿ\u0001\u0010d\u001a\u00020��2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00102\b\b\u0002\u0010\u0012\u001a\u00020\u00102\b\b\u0002\u0010\u0013\u001a\u00020\u00102\b\b\u0002\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u0016\u001a\u00020\u00172\u000e\b\u0002\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00170\u00192\u000e\b\u0002\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u00192\u0010\b\u0002\u0010\u001c\u001a\n\u0012\u0004\u0012\u00020\u001d\u0018\u00010\u00192\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0010\b\u0002\u0010 \u001a\n\u0012\u0004\u0012\u00020!\u0018\u00010\u00192\u0010\b\u0002\u0010\"\u001a\n\u0012\u0004\u0012\u00020#\u0018\u00010\u0019HÆ\u0001J\u0013\u0010e\u001a\u00020\u00102\b\u0010f\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\u0010\u0010(\u001a\u00020\u00032\b\u0010g\u001a\u0004\u0018\u00010��J\u0006\u0010h\u001a\u00020\u0010J\t\u0010i\u001a\u000208HÖ\u0001J\u0010\u0010j\u001a\u00020\u00102\b\u0010g\u001a\u0004\u0018\u00010��J\u0006\u0010k\u001a\u00020\u0003J\u0006\u0010l\u001a\u00020\u001dJ\t\u0010m\u001a\u00020\u0003HÖ\u0001R\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b%\u0010&R\u0011\u0010'\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b(\u0010&R\u0011\u0010)\u001a\u00020*¢\u0006\b\n��\u001a\u0004\b+\u0010,R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u0019¢\u0006\b\n��\u001a\u0004\b-\u0010.R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b/\u0010&R\u0019\u0010\u001c\u001a\n\u0012\u0004\u0012\u00020\u001d\u0018\u00010\u0019¢\u0006\b\n��\u001a\u0004\b0\u0010.R\u0011\u0010\u0014\u001a\u00020\u0015¢\u0006\b\n��\u001a\u0004\b1\u00102R\u0013\u00103\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b4\u0010&R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n��\u001a\u0004\b5\u00106R\u0011\u00107\u001a\u000208¢\u0006\b\n��\u001a\u0004\b9\u0010:R\u0019\u0010 \u001a\n\u0012\u0004\u0012\u00020!\u0018\u00010\u0019¢\u0006\b\n��\u001a\u0004\b;\u0010.R\u0013\u0010\u001e\u001a\u0004\u0018\u00010\u001f¢\u0006\b\n��\u001a\u0004\b<\u0010=R\"\u0010\"\u001a\n\u0012\u0004\u0012\u00020#\u0018\u00010\u0019X\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b>\u0010.\"\u0004\b?\u0010@R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\bA\u0010&R\u0011\u0010\u000f\u001a\u00020\u0010¢\u0006\b\n��\u001a\u0004\b\u000f\u0010BR\u0011\u0010\u0011\u001a\u00020\u0010¢\u0006\b\n��\u001a\u0004\b\u0011\u0010BR\u0011\u0010C\u001a\u00020\u0010¢\u0006\b\n��\u001a\u0004\bC\u0010BR\u0011\u0010\u0013\u001a\u00020\u0010¢\u0006\b\n��\u001a\u0004\b\u0013\u0010BR\u0011\u0010\u0012\u001a\u00020\u0010¢\u0006\b\n��\u001a\u0004\b\u0012\u0010BR\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00170\u0019¢\u0006\b\n��\u001a\u0004\bD\u0010.R\u0011\u0010\u0016\u001a\u00020\u0017¢\u0006\b\n��\u001a\u0004\bE\u0010FR\u0011\u0010G\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\bH\u0010&R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n��\u001a\u0004\bI\u0010JR\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n��\u001a\u0004\bK\u0010LR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\b\n��\u001a\u0004\bM\u0010NR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\bO\u0010&¨\u0006n"}, d2 = {"Lcom/tm/androidcopysdk/model/ArchiveMessage;", "", "id", "", "uniqueId", "accountPhoneNumber", "type", "Lcom/tm/androidcopysdk/model/ArchiveMessageType;", "direction", "Lcom/tm/androidcopysdk/model/Direction;", "status", "Lcom/tm/androidcopysdk/model/MessageStatus;", "body", "timestamp", "Lcom/tm/androidcopysdk/model/Timestamp;", "isAosp", "", "isDeleted", "isRemoteDeleted", "isForwarded", "chat", "Lcom/tm/androidcopysdk/model/ArchiveChat;", "sender", "Lcom/tm/androidcopysdk/model/ArchiveRecipient;", "receivers", "", "attachments", "Lcom/tm/androidcopysdk/model/ArchiveAttachment;", CallLogMessageRecorder.callinfo_key, "Lcom/tm/androidcopysdk/model/ArchiveCallInfo;", "eventInfo", "Lcom/tm/androidcopysdk/model/ArchiveEventInfo;", "edits", "Lcom/tm/androidcopysdk/model/ArchiveMessageEdit;", DBHeadersTable.HeadersEntry.TABLE_NAME, "Lcom/tm/androidcopysdk/model/ArchiveMessageHeader;", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/tm/androidcopysdk/model/ArchiveMessageType;Lcom/tm/androidcopysdk/model/Direction;Lcom/tm/androidcopysdk/model/MessageStatus;Ljava/lang/String;Lcom/tm/androidcopysdk/model/Timestamp;ZZZZLcom/tm/androidcopysdk/model/ArchiveChat;Lcom/tm/androidcopysdk/model/ArchiveRecipient;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lcom/tm/androidcopysdk/model/ArchiveEventInfo;Ljava/util/List;Ljava/util/List;)V", "getAccountPhoneNumber", "()Ljava/lang/String;", "archiveId", "getArchiveId", "archiveIdentifier", "Lcom/tm/androidcopysdk/model/ArchiveMessageIdentifier;", "getArchiveIdentifier", "()Lcom/tm/androidcopysdk/model/ArchiveMessageIdentifier;", "getAttachments", "()Ljava/util/List;", "getBody", "getCallInfo", "getChat", "()Lcom/tm/androidcopysdk/model/ArchiveChat;", "cleanAccountPhoneNumber", "getCleanAccountPhoneNumber", "getDirection", "()Lcom/tm/androidcopysdk/model/Direction;", "editCount", "", "getEditCount", "()I", "getEdits", "getEventInfo", "()Lcom/tm/androidcopysdk/model/ArchiveEventInfo;", "getHeaders", "setHeaders", "(Ljava/util/List;)V", "getId", "()Z", "isEdited", "getReceivers", "getSender", "()Lcom/tm/androidcopysdk/model/ArchiveRecipient;", "senderAddress", "getSenderAddress", "getStatus", "()Lcom/tm/androidcopysdk/model/MessageStatus;", "getTimestamp", "()Lcom/tm/androidcopysdk/model/Timestamp;", "getType", "()Lcom/tm/androidcopysdk/model/ArchiveMessageType;", "getUniqueId", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component2", "component20", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "existing", "hasDeletions", "hashCode", "isNewEdit", "receiverName", "requireFirstCallInfo", "toString", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nArchiveMessage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveMessage.kt\ncom/tm/androidcopysdk/model/ArchiveMessage\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,97:1\n1#2:98\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ArchiveMessage.class */
public final class ArchiveMessage {
    @NotNull
    private final String id;
    @Nullable
    private final String uniqueId;
    @Nullable
    private final String accountPhoneNumber;
    @Nullable
    private final ArchiveMessageType type;
    @NotNull
    private final Direction direction;
    @NotNull
    private final MessageStatus status;
    @Nullable
    private final String body;
    @NotNull
    private final Timestamp timestamp;
    private final boolean isAosp;
    private final boolean isDeleted;
    private final boolean isRemoteDeleted;
    private final boolean isForwarded;
    @NotNull
    private final ArchiveChat chat;
    @NotNull
    private final ArchiveRecipient sender;
    @NotNull
    private final List<ArchiveRecipient> receivers;
    @NotNull
    private final List<ArchiveAttachment> attachments;
    @Nullable
    private final List<ArchiveCallInfo> callInfo;
    @Nullable
    private final ArchiveEventInfo eventInfo;
    @Nullable
    private final List<ArchiveMessageEdit> edits;
    @Nullable
    private List<ArchiveMessageHeader> headers;
    private final int editCount;
    private final boolean isEdited;
    @NotNull
    private final String senderAddress;
    @Nullable
    private final String cleanAccountPhoneNumber;
    @NotNull
    private final ArchiveMessageIdentifier archiveIdentifier;
    @NotNull
    private final String archiveId;

    @NotNull
    public final String component1() {
        return this.id;
    }

    @Nullable
    public final String component2() {
        return this.uniqueId;
    }

    @Nullable
    public final String component3() {
        return this.accountPhoneNumber;
    }

    @Nullable
    public final ArchiveMessageType component4() {
        return this.type;
    }

    @NotNull
    public final Direction component5() {
        return this.direction;
    }

    @NotNull
    public final MessageStatus component6() {
        return this.status;
    }

    @Nullable
    public final String component7() {
        return this.body;
    }

    @NotNull
    public final Timestamp component8() {
        return this.timestamp;
    }

    public final boolean component9() {
        return this.isAosp;
    }

    public final boolean component10() {
        return this.isDeleted;
    }

    public final boolean component11() {
        return this.isRemoteDeleted;
    }

    public final boolean component12() {
        return this.isForwarded;
    }

    @NotNull
    public final ArchiveChat component13() {
        return this.chat;
    }

    @NotNull
    public final ArchiveRecipient component14() {
        return this.sender;
    }

    @NotNull
    public final List<ArchiveRecipient> component15() {
        return this.receivers;
    }

    @NotNull
    public final List<ArchiveAttachment> component16() {
        return this.attachments;
    }

    @Nullable
    public final List<ArchiveCallInfo> component17() {
        return this.callInfo;
    }

    @Nullable
    public final ArchiveEventInfo component18() {
        return this.eventInfo;
    }

    @Nullable
    public final List<ArchiveMessageEdit> component19() {
        return this.edits;
    }

    @Nullable
    public final List<ArchiveMessageHeader> component20() {
        return this.headers;
    }

    @NotNull
    public final ArchiveMessage copy(@NotNull String id, @Nullable String uniqueId, @Nullable String accountPhoneNumber, @Nullable ArchiveMessageType type, @NotNull Direction direction, @NotNull MessageStatus status, @Nullable String body, @NotNull Timestamp timestamp, boolean isAosp, boolean isDeleted, boolean isRemoteDeleted, boolean isForwarded, @NotNull ArchiveChat chat, @NotNull ArchiveRecipient sender, @NotNull List<ArchiveRecipient> list, @NotNull List<ArchiveAttachment> list2, @Nullable List<ArchiveCallInfo> list3, @Nullable ArchiveEventInfo eventInfo, @Nullable List<ArchiveMessageEdit> list4, @Nullable List<ArchiveMessageHeader> list5) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(direction, "direction");
        Intrinsics.checkNotNullParameter(status, "status");
        Intrinsics.checkNotNullParameter(timestamp, "timestamp");
        Intrinsics.checkNotNullParameter(chat, "chat");
        Intrinsics.checkNotNullParameter(sender, "sender");
        Intrinsics.checkNotNullParameter(list, "receivers");
        Intrinsics.checkNotNullParameter(list2, "attachments");
        return new ArchiveMessage(id, uniqueId, accountPhoneNumber, type, direction, status, body, timestamp, isAosp, isDeleted, isRemoteDeleted, isForwarded, chat, sender, list, list2, list3, eventInfo, list4, list5);
    }

    public static /* synthetic */ ArchiveMessage copy$default(ArchiveMessage archiveMessage, String str, String str2, String str3, ArchiveMessageType archiveMessageType, Direction direction, MessageStatus messageStatus, String str4, Timestamp timestamp, boolean z, boolean z2, boolean z3, boolean z4, ArchiveChat archiveChat, ArchiveRecipient archiveRecipient, List list, List list2, List list3, ArchiveEventInfo archiveEventInfo, List list4, List list5, int i, Object obj) {
        if ((i & 1) != 0) {
            str = archiveMessage.id;
        }
        if ((i & 2) != 0) {
            str2 = archiveMessage.uniqueId;
        }
        if ((i & 4) != 0) {
            str3 = archiveMessage.accountPhoneNumber;
        }
        if ((i & 8) != 0) {
            archiveMessageType = archiveMessage.type;
        }
        if ((i & 16) != 0) {
            direction = archiveMessage.direction;
        }
        if ((i & 32) != 0) {
            messageStatus = archiveMessage.status;
        }
        if ((i & 64) != 0) {
            str4 = archiveMessage.body;
        }
        if ((i & 128) != 0) {
            timestamp = archiveMessage.timestamp;
        }
        if ((i & 256) != 0) {
            z = archiveMessage.isAosp;
        }
        if ((i & 512) != 0) {
            z2 = archiveMessage.isDeleted;
        }
        if ((i & 1024) != 0) {
            z3 = archiveMessage.isRemoteDeleted;
        }
        if ((i & 2048) != 0) {
            z4 = archiveMessage.isForwarded;
        }
        if ((i & 4096) != 0) {
            archiveChat = archiveMessage.chat;
        }
        if ((i & 8192) != 0) {
            archiveRecipient = archiveMessage.sender;
        }
        if ((i & 16384) != 0) {
            list = archiveMessage.receivers;
        }
        if ((i & 32768) != 0) {
            list2 = archiveMessage.attachments;
        }
        if ((i & MyAudioFormat.AUDIO_CHANNEL_OUT_TOP_BACK_CENTER) != 0) {
            list3 = archiveMessage.callInfo;
        }
        if ((i & MyAudioFormat.AUDIO_CHANNEL_OUT_TOP_BACK_RIGHT) != 0) {
            archiveEventInfo = archiveMessage.eventInfo;
        }
        if ((i & 262144) != 0) {
            list4 = archiveMessage.edits;
        }
        if ((i & 524288) != 0) {
            list5 = archiveMessage.headers;
        }
        return archiveMessage.copy(str, str2, str3, archiveMessageType, direction, messageStatus, str4, timestamp, z, z2, z3, z4, archiveChat, archiveRecipient, list, list2, list3, archiveEventInfo, list4, list5);
    }

    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ArchiveMessage(id=").append(this.id).append(", uniqueId=").append(this.uniqueId).append(", accountPhoneNumber=").append(this.accountPhoneNumber).append(", type=").append(this.type).append(", direction=").append(this.direction).append(", status=").append(this.status).append(", body=").append(this.body).append(", timestamp=").append(this.timestamp).append(", isAosp=").append(this.isAosp).append(", isDeleted=").append(this.isDeleted).append(", isRemoteDeleted=").append(this.isRemoteDeleted).append(", isForwarded=");
        sb.append(this.isForwarded).append(", chat=").append(this.chat).append(", sender=").append(this.sender).append(", receivers=").append(this.receivers).append(", attachments=").append(this.attachments).append(", callInfo=").append(this.callInfo).append(", eventInfo=").append(this.eventInfo).append(", edits=").append(this.edits).append(", headers=").append(this.headers).append(')');
        return sb.toString();
    }

    public int hashCode() {
        int result = this.id.hashCode();
        return (((((((((((((((((((((((((((((((((((((result * 31) + (this.uniqueId == null ? 0 : this.uniqueId.hashCode())) * 31) + (this.accountPhoneNumber == null ? 0 : this.accountPhoneNumber.hashCode())) * 31) + (this.type == null ? 0 : this.type.hashCode())) * 31) + this.direction.hashCode()) * 31) + this.status.hashCode()) * 31) + (this.body == null ? 0 : this.body.hashCode())) * 31) + this.timestamp.hashCode()) * 31) + Boolean.hashCode(this.isAosp)) * 31) + Boolean.hashCode(this.isDeleted)) * 31) + Boolean.hashCode(this.isRemoteDeleted)) * 31) + Boolean.hashCode(this.isForwarded)) * 31) + this.chat.hashCode()) * 31) + this.sender.hashCode()) * 31) + this.receivers.hashCode()) * 31) + this.attachments.hashCode()) * 31) + (this.callInfo == null ? 0 : this.callInfo.hashCode())) * 31) + (this.eventInfo == null ? 0 : this.eventInfo.hashCode())) * 31) + (this.edits == null ? 0 : this.edits.hashCode())) * 31) + (this.headers == null ? 0 : this.headers.hashCode());
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ArchiveMessage) {
            ArchiveMessage archiveMessage = (ArchiveMessage) other;
            return Intrinsics.areEqual(this.id, archiveMessage.id) && Intrinsics.areEqual(this.uniqueId, archiveMessage.uniqueId) && Intrinsics.areEqual(this.accountPhoneNumber, archiveMessage.accountPhoneNumber) && this.type == archiveMessage.type && this.direction == archiveMessage.direction && this.status == archiveMessage.status && Intrinsics.areEqual(this.body, archiveMessage.body) && Intrinsics.areEqual(this.timestamp, archiveMessage.timestamp) && this.isAosp == archiveMessage.isAosp && this.isDeleted == archiveMessage.isDeleted && this.isRemoteDeleted == archiveMessage.isRemoteDeleted && this.isForwarded == archiveMessage.isForwarded && Intrinsics.areEqual(this.chat, archiveMessage.chat) && Intrinsics.areEqual(this.sender, archiveMessage.sender) && Intrinsics.areEqual(this.receivers, archiveMessage.receivers) && Intrinsics.areEqual(this.attachments, archiveMessage.attachments) && Intrinsics.areEqual(this.callInfo, archiveMessage.callInfo) && Intrinsics.areEqual(this.eventInfo, archiveMessage.eventInfo) && Intrinsics.areEqual(this.edits, archiveMessage.edits) && Intrinsics.areEqual(this.headers, archiveMessage.headers);
        }
        return false;
    }

    public ArchiveMessage(@NotNull String id, @Nullable String uniqueId, @Nullable String accountPhoneNumber, @Nullable ArchiveMessageType type, @NotNull Direction direction, @NotNull MessageStatus status, @Nullable String body, @NotNull Timestamp timestamp, boolean isAosp, boolean isDeleted, boolean isRemoteDeleted, boolean isForwarded, @NotNull ArchiveChat chat, @NotNull ArchiveRecipient sender, @NotNull List<ArchiveRecipient> list, @NotNull List<ArchiveAttachment> list2, @Nullable List<ArchiveCallInfo> list3, @Nullable ArchiveEventInfo eventInfo, @Nullable List<ArchiveMessageEdit> list4, @Nullable List<ArchiveMessageHeader> list5) {
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(direction, "direction");
        Intrinsics.checkNotNullParameter(status, "status");
        Intrinsics.checkNotNullParameter(timestamp, "timestamp");
        Intrinsics.checkNotNullParameter(chat, "chat");
        Intrinsics.checkNotNullParameter(sender, "sender");
        Intrinsics.checkNotNullParameter(list, "receivers");
        Intrinsics.checkNotNullParameter(list2, "attachments");
        this.id = id;
        this.uniqueId = uniqueId;
        this.accountPhoneNumber = accountPhoneNumber;
        this.type = type;
        this.direction = direction;
        this.status = status;
        this.body = body;
        this.timestamp = timestamp;
        this.isAosp = isAosp;
        this.isDeleted = isDeleted;
        this.isRemoteDeleted = isRemoteDeleted;
        this.isForwarded = isForwarded;
        this.chat = chat;
        this.sender = sender;
        this.receivers = list;
        this.attachments = list2;
        this.callInfo = list3;
        this.eventInfo = eventInfo;
        this.edits = list4;
        this.headers = list5;
        List<ArchiveMessageEdit> list6 = this.edits;
        this.editCount = list6 != null ? list6.size() : 0;
        this.isEdited = this.editCount > 0;
        this.senderAddress = this.sender.getCleanAddress();
        String str = this.accountPhoneNumber;
        this.cleanAccountPhoneNumber = str != null ? StringsKt.replace$default(str, "+", "", false, 4, (Object) null) : null;
        this.archiveIdentifier = ArchiveMessageIdentifier.Companion.fromMessage(this);
        this.archiveId = this.archiveIdentifier.rawId();
    }

    public /* synthetic */ ArchiveMessage(String str, String str2, String str3, ArchiveMessageType archiveMessageType, Direction direction, MessageStatus messageStatus, String str4, Timestamp timestamp, boolean z, boolean z2, boolean z3, boolean z4, ArchiveChat archiveChat, ArchiveRecipient archiveRecipient, List list, List list2, List list3, ArchiveEventInfo archiveEventInfo, List list4, List list5, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, (i & 2) != 0 ? null : str2, str3, archiveMessageType, direction, messageStatus, str4, timestamp, (i & 256) != 0 ? false : z, z2, z3, z4, archiveChat, archiveRecipient, list, list2, (i & MyAudioFormat.AUDIO_CHANNEL_OUT_TOP_BACK_CENTER) != 0 ? null : list3, (i & MyAudioFormat.AUDIO_CHANNEL_OUT_TOP_BACK_RIGHT) != 0 ? null : archiveEventInfo, (i & 262144) != 0 ? null : list4, (i & 524288) != 0 ? null : list5);
    }

    @NotNull
    public final String getId() {
        return this.id;
    }

    @Nullable
    public final String getUniqueId() {
        return this.uniqueId;
    }

    @Nullable
    public final String getAccountPhoneNumber() {
        return this.accountPhoneNumber;
    }

    @Nullable
    public final ArchiveMessageType getType() {
        return this.type;
    }

    @NotNull
    public final Direction getDirection() {
        return this.direction;
    }

    @NotNull
    public final MessageStatus getStatus() {
        return this.status;
    }

    @Nullable
    public final String getBody() {
        return this.body;
    }

    @NotNull
    public final Timestamp getTimestamp() {
        return this.timestamp;
    }

    public final boolean isAosp() {
        return this.isAosp;
    }

    public final boolean isDeleted() {
        return this.isDeleted;
    }

    public final boolean isRemoteDeleted() {
        return this.isRemoteDeleted;
    }

    public final boolean isForwarded() {
        return this.isForwarded;
    }

    @NotNull
    public final ArchiveChat getChat() {
        return this.chat;
    }

    @NotNull
    public final ArchiveRecipient getSender() {
        return this.sender;
    }

    @NotNull
    public final List<ArchiveRecipient> getReceivers() {
        return this.receivers;
    }

    @NotNull
    public final List<ArchiveAttachment> getAttachments() {
        return this.attachments;
    }

    @Nullable
    public final List<ArchiveCallInfo> getCallInfo() {
        return this.callInfo;
    }

    @Nullable
    public final ArchiveEventInfo getEventInfo() {
        return this.eventInfo;
    }

    @Nullable
    public final List<ArchiveMessageEdit> getEdits() {
        return this.edits;
    }

    @Nullable
    public final List<ArchiveMessageHeader> getHeaders() {
        return this.headers;
    }

    public final void setHeaders(@Nullable List<ArchiveMessageHeader> list) {
        this.headers = list;
    }

    public final int getEditCount() {
        return this.editCount;
    }

    public final boolean isEdited() {
        return this.isEdited;
    }

    @NotNull
    public final String getSenderAddress() {
        return this.senderAddress;
    }

    @Nullable
    public final String getCleanAccountPhoneNumber() {
        return this.cleanAccountPhoneNumber;
    }

    @NotNull
    public final ArchiveMessageIdentifier getArchiveIdentifier() {
        return this.archiveIdentifier;
    }

    @NotNull
    public final String getArchiveId() {
        return this.archiveId;
    }

    public final boolean hasDeletions() {
        return this.isDeleted || this.isRemoteDeleted;
    }

    @NotNull
    public final String receiverName() {
        if (this.chat.getType().isNormalChat()) {
            ArchiveRecipient archiveRecipient = (ArchiveRecipient) CollectionsKt.firstOrNull(this.receivers);
            if (archiveRecipient != null) {
                String cleanAddress = archiveRecipient.getCleanAddress();
                if (cleanAddress != null) {
                    return cleanAddress;
                }
            }
            return "";
        }
        return CollectionsKt.joinToString$default(CollectionsKt.listOfNotNull(new String[]{this.chat.getType().getKey(), this.chat.getName()}), " ", (CharSequence) null, (CharSequence) null, 0, (CharSequence) null, (Function1) null, 62, (Object) null);
    }

    @NotNull
    public final ArchiveCallInfo requireFirstCallInfo() {
        if (!(this.type == ArchiveMessageType.Call)) {
            throw new IllegalArgumentException(("This should never be called with type " + this.type).toString());
        }
        List<ArchiveCallInfo> list = this.callInfo;
        ArchiveCallInfo archiveCallInfo = list != null ? (ArchiveCallInfo) CollectionsKt.firstOrNull(list) : null;
        if (archiveCallInfo == null) {
            throw new IllegalArgumentException("Required value was null.".toString());
        }
        return archiveCallInfo;
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0041  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0049  */
    @org.jetbrains.annotations.NotNull
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final java.lang.String getArchiveId(@org.jetbrains.annotations.Nullable com.tm.androidcopysdk.model.ArchiveMessage r5) {
        /*
            r4 = this;
            r0 = r4
            boolean r0 = r0.isRemoteDeleted
            if (r0 == 0) goto Lf
            r0 = r4
            com.tm.androidcopysdk.model.ArchiveMessageIdentifier r0 = r0.archiveIdentifier
            java.lang.String r0 = r0.deletedForAllId()
            return r0
        Lf:
            r0 = r4
            boolean r0 = r0.isDeleted
            if (r0 == 0) goto L1e
            r0 = r4
            com.tm.androidcopysdk.model.ArchiveMessageIdentifier r0 = r0.archiveIdentifier
            java.lang.String r0 = r0.deletedForMeId()
            return r0
        L1e:
            r0 = r4
            com.tm.androidcopysdk.model.ArchiveEventInfo r0 = r0.eventInfo
            r1 = r0
            if (r1 == 0) goto L3c
            com.tm.androidcopysdk.model.ArchiveEventType r0 = r0.getType()
            r1 = r0
            if (r1 == 0) goto L3c
            boolean r0 = r0.isDeleteAll()
            r1 = 1
            if (r0 != r1) goto L38
            r0 = 1
            goto L3e
        L38:
            r0 = 0
            goto L3e
        L3c:
            r0 = 0
        L3e:
            if (r0 == 0) goto L49
            r0 = r4
            com.tm.androidcopysdk.model.ArchiveMessageIdentifier r0 = r0.archiveIdentifier
            java.lang.String r0 = r0.deletedAllId()
            return r0
        L49:
            r0 = r4
            r1 = r5
            boolean r0 = r0.isNewEdit(r1)
            if (r0 == 0) goto L68
            r0 = r4
            com.tm.androidcopysdk.model.ArchiveMessageIdentifier r0 = r0.archiveIdentifier
            r1 = r5
            r2 = r1
            if (r2 == 0) goto L60
            int r1 = r1.editCount
            goto L62
        L60:
            r1 = 0
        L62:
            r2 = 1
            int r1 = r1 + r2
            java.lang.String r0 = r0.editIdAt(r1)
            return r0
        L68:
            r0 = r5
            r1 = r0
            if (r1 == 0) goto L7c
            boolean r0 = r0.isEdited
            r1 = 1
            if (r0 != r1) goto L78
            r0 = 1
            goto L7e
        L78:
            r0 = 0
            goto L7e
        L7c:
            r0 = 0
        L7e:
            if (r0 == 0) goto L8d
            r0 = r4
            com.tm.androidcopysdk.model.ArchiveMessageIdentifier r0 = r0.archiveIdentifier
            r1 = r5
            int r1 = r1.editCount
            java.lang.String r0 = r0.editIdAt(r1)
            return r0
        L8d:
            r0 = r4
            boolean r0 = r0.isEdited
            if (r0 == 0) goto La0
            r0 = r4
            com.tm.androidcopysdk.model.ArchiveMessageIdentifier r0 = r0.archiveIdentifier
            r1 = r4
            int r1 = r1.editCount
            java.lang.String r0 = r0.editIdAt(r1)
            return r0
        La0:
            r0 = r4
            java.lang.String r0 = r0.archiveId
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.model.ArchiveMessage.getArchiveId(com.tm.androidcopysdk.model.ArchiveMessage):java.lang.String");
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0031, code lost:
        if (r0 == null) goto L19;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final boolean isNewEdit(@org.jetbrains.annotations.Nullable com.tm.androidcopysdk.model.ArchiveMessage r4) {
        /*
            r3 = this;
            r0 = r4
            if (r0 == 0) goto L12
            r0 = r3
            boolean r0 = r0.hasDeletions()
            if (r0 != 0) goto L12
            r0 = r4
            boolean r0 = r0.hasDeletions()
            if (r0 == 0) goto L14
        L12:
            r0 = 0
            return r0
        L14:
            r0 = r4
            java.util.List<com.tm.androidcopysdk.model.ArchiveMessageEdit> r0 = r0.edits
            r1 = r0
            if (r1 == 0) goto L25
            java.lang.Object r0 = kotlin.collections.CollectionsKt.lastOrNull(r0)
            com.tm.androidcopysdk.model.ArchiveMessageEdit r0 = (com.tm.androidcopysdk.model.ArchiveMessageEdit) r0
            goto L27
        L25:
            r0 = 0
        L27:
            r5 = r0
            r0 = r5
            r1 = r0
            if (r1 == 0) goto L34
            java.lang.String r0 = r0.getBody()
            r1 = r0
            if (r1 != 0) goto L39
        L34:
        L35:
            r0 = r4
            java.lang.String r0 = r0.body
        L39:
            r6 = r0
            r0 = r3
            java.lang.String r0 = r0.body
            r1 = r6
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 != 0) goto L47
            r0 = 1
            return r0
        L47:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.model.ArchiveMessage.isNewEdit(com.tm.androidcopysdk.model.ArchiveMessage):boolean");
    }
}
