package com.tm.androidcopysdk.database;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: DBKeepAliveQueryHelper.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\f\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0005\u0018�� \u00032\u00020\u0001:\u0003\u0003\u0004\u0005B\u0005¢\u0006\u0002\u0010\u0002¨\u0006\u0006"}, d2 = {"Lcom/tm/androidcopysdk/database/DBKeepAliveQueryHelper;", "", "()V", "Companion", "MessagePendingReason", "MessageType", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBKeepAliveQueryHelper.class */
public final class DBKeepAliveQueryHelper {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private static final String TAG = "DBKeepAliveHelper";

    @JvmStatic
    public static final void updateSendToServerTime(@NotNull Context context, @Nullable String messageId, @NotNull String time) {
        Companion.updateSendToServerTime(context, messageId, time);
    }

    @JvmStatic
    public static final void updateResponseCode(@NotNull Context context, @NotNull String messageId, int responseCode) {
        Companion.updateResponseCode(context, messageId, responseCode);
    }

    @JvmStatic
    public static final void updatePendingReason(@NotNull Context context, @NotNull String messageId, @NotNull String pendingReason) {
        Companion.updatePendingReason(context, messageId, pendingReason);
    }

    /* compiled from: DBKeepAliveQueryHelper.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��6\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\b\n��\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0007\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007J!\u0010\u000b\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\n2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e¢\u0006\u0002\u0010\u000fJ \u0010\u0010\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u0004H\u0007J \u0010\u0014\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00020\fH\u0007J\"\u0010\u0016\u001a\u00020\u00112\u0006\u0010\t\u001a\u00020\n2\b\u0010\u0012\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0017\u001a\u00020\u0004H\u0007R\u0014\u0010\u0003\u001a\u00020\u0004X\u0086D¢\u0006\b\n��\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0018"}, d2 = {"Lcom/tm/androidcopysdk/database/DBKeepAliveQueryHelper$Companion;", "", "()V", "TAG", "", "getTAG", "()Ljava/lang/String;", "getAllMessagesForSendingToKeepAlive", "Lorg/json/JSONArray;", "context", "Landroid/content/Context;", "updateMessagesListAsAlreadySentToKeepAlive", "", "listOfMessagesId", "", "(Landroid/content/Context;[Ljava/lang/String;)I", "updatePendingReason", "", CallLogMessageRecorder.messageId_key, "pendingReason", "updateResponseCode", "responseCode", "updateSendToServerTime", "time", "androidcopysdk_signalRelease"})
    @SourceDebugExtension({"SMAP\nDBKeepAliveQueryHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DBKeepAliveQueryHelper.kt\ncom/tm/androidcopysdk/database/DBKeepAliveQueryHelper$Companion\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,219:1\n13374#2,3:220\n731#3,9:223\n37#4,2:232\n*S KotlinDebug\n*F\n+ 1 DBKeepAliveQueryHelper.kt\ncom/tm/androidcopysdk/database/DBKeepAliveQueryHelper$Companion\n*L\n30#1:220,3\n88#1:223,9\n88#1:232,2\n*E\n"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBKeepAliveQueryHelper$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final String getTAG() {
            return DBKeepAliveQueryHelper.TAG;
        }

        public final int updateMessagesListAsAlreadySentToKeepAlive(@NotNull Context context, @NotNull String[] listOfMessagesId) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(listOfMessagesId, "listOfMessagesId");
            ContentValues cv = new ContentValues();
            cv.put("keep_alive_sent", (Integer) 1);
            String str = "";
            int index$iv = 0;
            for (String str2 : listOfMessagesId) {
                index$iv++;
                if (str2.length() > 0) {
                    str = str + '\'' + str2 + "',";
                }
            }
            if (str.length() > 0) {
                str = StringsKt.removeSuffix(str, ",");
            }
            int res = context.getContentResolver().update(MessageContentProvider.CONTENT_URI_MESSAGES, cv, "native_message_id IN (" + str + ')', null);
            return res;
        }

        /* JADX WARN: Code restructure failed: missing block: B:36:0x01bb, code lost:
            if ((r0.toArray(new java.lang.String[0]).length == 0) != false) goto L39;
         */
        @android.annotation.SuppressLint({"Recycle"})
        @org.jetbrains.annotations.NotNull
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public final org.json.JSONArray getAllMessagesForSendingToKeepAlive(@org.jetbrains.annotations.NotNull android.content.Context r8) {
            /*
                Method dump skipped, instructions count: 1131
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.database.DBKeepAliveQueryHelper.Companion.getAllMessagesForSendingToKeepAlive(android.content.Context):org.json.JSONArray");
        }

        @JvmStatic
        public final void updateSendToServerTime(@NotNull Context context, @Nullable String messageId, @NotNull String time) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(time, "time");
            if (messageId == null) {
                return;
            }
            String[] condition = {messageId};
            ContentValues cv = new ContentValues();
            cv.put("sentToServerTime", time);
            Uri messageToUpdate = MessageContentProvider.CONTENT_URI_MESSAGES;
            context.getContentResolver().update(messageToUpdate, cv, "native_message_id = ? ", condition);
        }

        @JvmStatic
        public final void updateResponseCode(@NotNull Context context, @NotNull String messageId, int responseCode) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(messageId, CallLogMessageRecorder.messageId_key);
            ContentValues cv = new ContentValues();
            cv.put("serverResponse", Integer.valueOf(responseCode));
            Uri messageToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, messageId);
            context.getContentResolver().update(messageToUpdate, cv, null, null);
        }

        @JvmStatic
        public final void updatePendingReason(@NotNull Context context, @NotNull String messageId, @NotNull String pendingReason) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(messageId, CallLogMessageRecorder.messageId_key);
            Intrinsics.checkNotNullParameter(pendingReason, "pendingReason");
            ContentValues cv = new ContentValues();
            cv.put("pendingReason", pendingReason);
            Uri messageToUpdate = Uri.withAppendedPath(MessageContentProvider.CONTENT_URI_MESSAGES, messageId);
            context.getContentResolver().update(messageToUpdate, cv, null, null);
        }
    }

    /* compiled from: DBKeepAliveQueryHelper.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n��\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u0086\u0081\u0002\u0018��2\b\u0012\u0004\u0012\u00020��0\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\r¨\u0006\u000e"}, d2 = {"Lcom/tm/androidcopysdk/database/DBKeepAliveQueryHelper$MessageType;", "", "type", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getType", "()Ljava/lang/String;", "APP_MESSAGE", "PAGER_MESSAGE", "MULTI_MEDIA_MESSAGE", "EVENT", "CALL_LOG", "VOICE_CALL", "VIDEO_CALL", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBKeepAliveQueryHelper$MessageType.class */
    public enum MessageType {
        APP_MESSAGE("app-message"),
        PAGER_MESSAGE("pager-message"),
        MULTI_MEDIA_MESSAGE("multimedia-message"),
        EVENT("event"),
        CALL_LOG("call-log"),
        VOICE_CALL("voice-call"),
        VIDEO_CALL("video-call");
        
        @NotNull
        private final String type;
        private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

        @NotNull
        public static EnumEntries<MessageType> getEntries() {
            return $ENTRIES;
        }

        MessageType(String type) {
            this.type = type;
        }

        @NotNull
        public final String getType() {
            return this.type;
        }
    }

    /* compiled from: DBKeepAliveQueryHelper.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n��\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018��2\b\u0012\u0004\u0012\u00020��0\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\t¨\u0006\n"}, d2 = {"Lcom/tm/androidcopysdk/database/DBKeepAliveQueryHelper$MessagePendingReason;", "", "type", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getType", "()Ljava/lang/String;", "SERVER_REJECTED", "WAITING_TO_MEDIA", "NO_ATTEMPT_YET", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/database/DBKeepAliveQueryHelper$MessagePendingReason.class */
    public enum MessagePendingReason {
        SERVER_REJECTED("server rejected"),
        WAITING_TO_MEDIA("waiting for media"),
        NO_ATTEMPT_YET("no attempt yet");
        
        @NotNull
        private final String type;
        private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

        @NotNull
        public static EnumEntries<MessagePendingReason> getEntries() {
            return $ENTRIES;
        }

        MessagePendingReason(String type) {
            this.type = type;
        }

        @NotNull
        public final String getType() {
            return this.type;
        }
    }
}
