package com.tm.androidcopysdk.Models;

import com.tm.androidcopysdk.DataGrabber;
import com.tm.androidcopysdk.database.DBKeepAliveQueryHelper;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.Contact;
import com.tm.androidcopysdk.utils.FlavorSettings;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: MessageDetailsArchive.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��@\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n��\n\u0002\u0010\u0011\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b,\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018��2\u00020\u0001B·\u0001\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u000e\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011\u0012\b\u0010\u0012\u001a\u0004\u0018\u00010\u0003\u0012\u000e\u0010\u0013\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0005\u0012\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005\u0012\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0016¢\u0006\u0002\u0010\u0017J\u000b\u00100\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u00101\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u00102\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u00103\u001a\u0004\u0018\u00010\u0011HÆ\u0003J\u000b\u00104\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0016\u00105\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\u0010*J\u0014\u00106\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005HÆ\u0003¢\u0006\u0002\u0010-J\u000b\u00107\u001a\u0004\u0018\u00010\u0016HÆ\u0003J\u0016\u00108\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0005HÆ\u0003¢\u0006\u0002\u0010-J\u000b\u00109\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010:\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010;\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010<\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010=\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010>\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010?\u001a\u0004\u0018\u00010\rHÆ\u0003JÞ\u0001\u0010@\u001a\u00020��2\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0010\b\u0002\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00112\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00032\u0010\b\u0002\u0010\u0013\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u00052\u000e\b\u0002\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00030\u00052\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0016HÆ\u0001¢\u0006\u0002\u0010AJ\u0013\u0010B\u001a\u00020C2\b\u0010D\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010E\u001a\u00020FHÖ\u0001J\t\u0010G\u001a\u00020\u0003HÖ\u0001R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u0018\u0010\u0019R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u001a\u0010\u0019R\u0013\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\b\n��\u001a\u0004\b\u001b\u0010\u001cR\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u001d\u0010\u0019R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u001e\u0010\u0019R\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0011¢\u0006\b\n��\u001a\u0004\b\u001f\u0010 R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b!\u0010\u0019R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\"\u0010\u0019R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b#\u0010\u0019R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u0016¢\u0006\b\n��\u001a\u0004\b$\u0010%R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b&\u0010\u0019R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b'\u0010\u0019R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b(\u0010\u0019R\u001b\u0010\u0013\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0005¢\u0006\n\n\u0002\u0010+\u001a\u0004\b)\u0010*R\u0019\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00030\u0005¢\u0006\n\n\u0002\u0010.\u001a\u0004\b,\u0010-R\u001b\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0005¢\u0006\n\n\u0002\u0010.\u001a\u0004\b/\u0010-¨\u0006H"}, d2 = {"Lcom/tm/androidcopysdk/Models/MessageDetailsArchive;", "", "protocol", "", "toPhonesArray", "", "fromPhoneNumber", "body", "id", "date", "subject", "myNumber", "chatMode", "Lcom/tm/androidcopysdk/DataGrabber$CHAT_MODE;", "chatName", "chatId", "fromName", "Lcom/tm/androidcopysdk/utils/Contact;", "fromValue", "toNameArray", "toPhoneNumberArrayValue", "messageType", "Lcom/tm/androidcopysdk/database/DBKeepAliveQueryHelper$MessageType;", "(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/tm/androidcopysdk/DataGrabber$CHAT_MODE;Ljava/lang/String;Ljava/lang/String;Lcom/tm/androidcopysdk/utils/Contact;Ljava/lang/String;[Lcom/tm/androidcopysdk/utils/Contact;[Ljava/lang/String;Lcom/tm/androidcopysdk/database/DBKeepAliveQueryHelper$MessageType;)V", "getBody", "()Ljava/lang/String;", "getChatId", "getChatMode", "()Lcom/tm/androidcopysdk/DataGrabber$CHAT_MODE;", "getChatName", "getDate", "getFromName", "()Lcom/tm/androidcopysdk/utils/Contact;", "getFromPhoneNumber", "getFromValue", "getId", "getMessageType", "()Lcom/tm/androidcopysdk/database/DBKeepAliveQueryHelper$MessageType;", "getMyNumber", "getProtocol", "getSubject", "getToNameArray", "()[Lcom/tm/androidcopysdk/utils/Contact;", "[Lcom/tm/androidcopysdk/utils/Contact;", "getToPhoneNumberArrayValue", "()[Ljava/lang/String;", "[Ljava/lang/String;", "getToPhonesArray", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/tm/androidcopysdk/DataGrabber$CHAT_MODE;Ljava/lang/String;Ljava/lang/String;Lcom/tm/androidcopysdk/utils/Contact;Ljava/lang/String;[Lcom/tm/androidcopysdk/utils/Contact;[Ljava/lang/String;Lcom/tm/androidcopysdk/database/DBKeepAliveQueryHelper$MessageType;)Lcom/tm/androidcopysdk/Models/MessageDetailsArchive;", "equals", "", "other", "hashCode", "", "toString", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/Models/MessageDetailsArchive.class */
public final class MessageDetailsArchive {
    @Nullable
    private final String protocol;
    @Nullable
    private final String[] toPhonesArray;
    @Nullable
    private final String fromPhoneNumber;
    @Nullable
    private final String body;
    @Nullable
    private final String id;
    @Nullable
    private final String date;
    @Nullable
    private final String subject;
    @Nullable
    private final String myNumber;
    @Nullable
    private final DataGrabber.CHAT_MODE chatMode;
    @Nullable
    private final String chatName;
    @Nullable
    private final String chatId;
    @Nullable
    private final Contact fromName;
    @Nullable
    private final String fromValue;
    @Nullable
    private final Contact[] toNameArray;
    @NotNull
    private final String[] toPhoneNumberArrayValue;
    @Nullable
    private final DBKeepAliveQueryHelper.MessageType messageType;

    @Nullable
    public final String component1() {
        return this.protocol;
    }

    @Nullable
    public final String[] component2() {
        return this.toPhonesArray;
    }

    @Nullable
    public final String component3() {
        return this.fromPhoneNumber;
    }

    @Nullable
    public final String component4() {
        return this.body;
    }

    @Nullable
    public final String component5() {
        return this.id;
    }

    @Nullable
    public final String component6() {
        return this.date;
    }

    @Nullable
    public final String component7() {
        return this.subject;
    }

    @Nullable
    public final String component8() {
        return this.myNumber;
    }

    @Nullable
    public final DataGrabber.CHAT_MODE component9() {
        return this.chatMode;
    }

    @Nullable
    public final String component10() {
        return this.chatName;
    }

    @Nullable
    public final String component11() {
        return this.chatId;
    }

    @Nullable
    public final Contact component12() {
        return this.fromName;
    }

    @Nullable
    public final String component13() {
        return this.fromValue;
    }

    @Nullable
    public final Contact[] component14() {
        return this.toNameArray;
    }

    @NotNull
    public final String[] component15() {
        return this.toPhoneNumberArrayValue;
    }

    @Nullable
    public final DBKeepAliveQueryHelper.MessageType component16() {
        return this.messageType;
    }

    @NotNull
    public final MessageDetailsArchive copy(@Nullable String protocol, @Nullable String[] toPhonesArray, @Nullable String fromPhoneNumber, @Nullable String body, @Nullable String id, @Nullable String date, @Nullable String subject, @Nullable String myNumber, @Nullable DataGrabber.CHAT_MODE chatMode, @Nullable String chatName, @Nullable String chatId, @Nullable Contact fromName, @Nullable String fromValue, @Nullable Contact[] toNameArray, @NotNull String[] toPhoneNumberArrayValue, @Nullable DBKeepAliveQueryHelper.MessageType messageType) {
        Intrinsics.checkNotNullParameter(toPhoneNumberArrayValue, "toPhoneNumberArrayValue");
        return new MessageDetailsArchive(protocol, toPhonesArray, fromPhoneNumber, body, id, date, subject, myNumber, chatMode, chatName, chatId, fromName, fromValue, toNameArray, toPhoneNumberArrayValue, messageType);
    }

    public static /* synthetic */ MessageDetailsArchive copy$default(MessageDetailsArchive messageDetailsArchive, String str, String[] strArr, String str2, String str3, String str4, String str5, String str6, String str7, DataGrabber.CHAT_MODE chat_mode, String str8, String str9, Contact contact, String str10, Contact[] contactArr, String[] strArr2, DBKeepAliveQueryHelper.MessageType messageType, int i, Object obj) {
        if ((i & 1) != 0) {
            str = messageDetailsArchive.protocol;
        }
        if ((i & 2) != 0) {
            strArr = messageDetailsArchive.toPhonesArray;
        }
        if ((i & 4) != 0) {
            str2 = messageDetailsArchive.fromPhoneNumber;
        }
        if ((i & 8) != 0) {
            str3 = messageDetailsArchive.body;
        }
        if ((i & 16) != 0) {
            str4 = messageDetailsArchive.id;
        }
        if ((i & 32) != 0) {
            str5 = messageDetailsArchive.date;
        }
        if ((i & 64) != 0) {
            str6 = messageDetailsArchive.subject;
        }
        if ((i & 128) != 0) {
            str7 = messageDetailsArchive.myNumber;
        }
        if ((i & 256) != 0) {
            chat_mode = messageDetailsArchive.chatMode;
        }
        if ((i & 512) != 0) {
            str8 = messageDetailsArchive.chatName;
        }
        if ((i & 1024) != 0) {
            str9 = messageDetailsArchive.chatId;
        }
        if ((i & 2048) != 0) {
            contact = messageDetailsArchive.fromName;
        }
        if ((i & 4096) != 0) {
            str10 = messageDetailsArchive.fromValue;
        }
        if ((i & 8192) != 0) {
            contactArr = messageDetailsArchive.toNameArray;
        }
        if ((i & 16384) != 0) {
            strArr2 = messageDetailsArchive.toPhoneNumberArrayValue;
        }
        if ((i & 32768) != 0) {
            messageType = messageDetailsArchive.messageType;
        }
        return messageDetailsArchive.copy(str, strArr, str2, str3, str4, str5, str6, str7, chat_mode, str8, str9, contact, str10, contactArr, strArr2, messageType);
    }

    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MessageDetailsArchive(protocol=").append(this.protocol).append(", toPhonesArray=").append(Arrays.toString(this.toPhonesArray)).append(", fromPhoneNumber=").append(this.fromPhoneNumber).append(", body=").append(this.body).append(", id=").append(this.id).append(", date=").append(this.date).append(", subject=").append(this.subject).append(", myNumber=").append(this.myNumber).append(", chatMode=").append(this.chatMode).append(", chatName=").append(this.chatName).append(", chatId=").append(this.chatId).append(", fromName=");
        sb.append(this.fromName).append(", fromValue=").append(this.fromValue).append(", toNameArray=").append(Arrays.toString(this.toNameArray)).append(", toPhoneNumberArrayValue=").append(Arrays.toString(this.toPhoneNumberArrayValue)).append(", messageType=").append(this.messageType).append(')');
        return sb.toString();
    }

    public int hashCode() {
        int result = this.protocol == null ? 0 : this.protocol.hashCode();
        return (((((((((((((((((((((((((((((result * 31) + (this.toPhonesArray == null ? 0 : Arrays.hashCode(this.toPhonesArray))) * 31) + (this.fromPhoneNumber == null ? 0 : this.fromPhoneNumber.hashCode())) * 31) + (this.body == null ? 0 : this.body.hashCode())) * 31) + (this.id == null ? 0 : this.id.hashCode())) * 31) + (this.date == null ? 0 : this.date.hashCode())) * 31) + (this.subject == null ? 0 : this.subject.hashCode())) * 31) + (this.myNumber == null ? 0 : this.myNumber.hashCode())) * 31) + (this.chatMode == null ? 0 : this.chatMode.hashCode())) * 31) + (this.chatName == null ? 0 : this.chatName.hashCode())) * 31) + (this.chatId == null ? 0 : this.chatId.hashCode())) * 31) + (this.fromName == null ? 0 : this.fromName.hashCode())) * 31) + (this.fromValue == null ? 0 : this.fromValue.hashCode())) * 31) + (this.toNameArray == null ? 0 : Arrays.hashCode(this.toNameArray))) * 31) + Arrays.hashCode(this.toPhoneNumberArrayValue)) * 31) + (this.messageType == null ? 0 : this.messageType.hashCode());
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof MessageDetailsArchive) {
            MessageDetailsArchive messageDetailsArchive = (MessageDetailsArchive) other;
            return Intrinsics.areEqual(this.protocol, messageDetailsArchive.protocol) && Intrinsics.areEqual(this.toPhonesArray, messageDetailsArchive.toPhonesArray) && Intrinsics.areEqual(this.fromPhoneNumber, messageDetailsArchive.fromPhoneNumber) && Intrinsics.areEqual(this.body, messageDetailsArchive.body) && Intrinsics.areEqual(this.id, messageDetailsArchive.id) && Intrinsics.areEqual(this.date, messageDetailsArchive.date) && Intrinsics.areEqual(this.subject, messageDetailsArchive.subject) && Intrinsics.areEqual(this.myNumber, messageDetailsArchive.myNumber) && this.chatMode == messageDetailsArchive.chatMode && Intrinsics.areEqual(this.chatName, messageDetailsArchive.chatName) && Intrinsics.areEqual(this.chatId, messageDetailsArchive.chatId) && Intrinsics.areEqual(this.fromName, messageDetailsArchive.fromName) && Intrinsics.areEqual(this.fromValue, messageDetailsArchive.fromValue) && Intrinsics.areEqual(this.toNameArray, messageDetailsArchive.toNameArray) && Intrinsics.areEqual(this.toPhoneNumberArrayValue, messageDetailsArchive.toPhoneNumberArrayValue) && this.messageType == messageDetailsArchive.messageType;
        }
        return false;
    }

    public MessageDetailsArchive(@Nullable String protocol, @Nullable String[] toPhonesArray, @Nullable String fromPhoneNumber, @Nullable String body, @Nullable String id, @Nullable String date, @Nullable String subject, @Nullable String myNumber, @Nullable DataGrabber.CHAT_MODE chatMode, @Nullable String chatName, @Nullable String chatId, @Nullable Contact fromName, @Nullable String fromValue, @Nullable Contact[] toNameArray, @NotNull String[] toPhoneNumberArrayValue, @Nullable DBKeepAliveQueryHelper.MessageType messageType) {
        Intrinsics.checkNotNullParameter(toPhoneNumberArrayValue, "toPhoneNumberArrayValue");
        this.protocol = protocol;
        this.toPhonesArray = toPhonesArray;
        this.fromPhoneNumber = fromPhoneNumber;
        this.body = body;
        this.id = id;
        this.date = date;
        this.subject = subject;
        this.myNumber = myNumber;
        this.chatMode = chatMode;
        this.chatName = chatName;
        this.chatId = chatId;
        this.fromName = fromName;
        this.fromValue = fromValue;
        this.toNameArray = toNameArray;
        this.toPhoneNumberArrayValue = toPhoneNumberArrayValue;
        this.messageType = messageType;
    }

    public /* synthetic */ MessageDetailsArchive(String str, String[] strArr, String str2, String str3, String str4, String str5, String str6, String str7, DataGrabber.CHAT_MODE chat_mode, String str8, String str9, Contact contact, String str10, Contact[] contactArr, String[] strArr2, DBKeepAliveQueryHelper.MessageType messageType, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, strArr, str2, str3, str4, str5, str6, str7, chat_mode, str8, str9, contact, str10, contactArr, strArr2, (i & 32768) != 0 ? FlavorSettings.getInstance().getMessageContext() : messageType);
    }

    @Nullable
    public final String getProtocol() {
        return this.protocol;
    }

    @Nullable
    public final String[] getToPhonesArray() {
        return this.toPhonesArray;
    }

    @Nullable
    public final String getFromPhoneNumber() {
        return this.fromPhoneNumber;
    }

    @Nullable
    public final String getBody() {
        return this.body;
    }

    @Nullable
    public final String getId() {
        return this.id;
    }

    @Nullable
    public final String getDate() {
        return this.date;
    }

    @Nullable
    public final String getSubject() {
        return this.subject;
    }

    @Nullable
    public final String getMyNumber() {
        return this.myNumber;
    }

    @Nullable
    public final DataGrabber.CHAT_MODE getChatMode() {
        return this.chatMode;
    }

    @Nullable
    public final String getChatName() {
        return this.chatName;
    }

    @Nullable
    public final String getChatId() {
        return this.chatId;
    }

    @Nullable
    public final Contact getFromName() {
        return this.fromName;
    }

    @Nullable
    public final String getFromValue() {
        return this.fromValue;
    }

    @Nullable
    public final Contact[] getToNameArray() {
        return this.toNameArray;
    }

    @NotNull
    public final String[] getToPhoneNumberArrayValue() {
        return this.toPhoneNumberArrayValue;
    }

    @Nullable
    public final DBKeepAliveQueryHelper.MessageType getMessageType() {
        return this.messageType;
    }
}
