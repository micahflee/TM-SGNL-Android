package com.tm.androidcopysdk.model;

import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ClientType.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n��\n\u0002\u0010\u000e\n\u0002\b\n\b\u0086\u0081\u0002\u0018��2\b\u0012\u0004\u0012\u00020��0\u0001B\u0011\b\u0002\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0002\u0010\u0004R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n��\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\f¨\u0006\r"}, d2 = {"Lcom/tm/androidcopysdk/model/ClientType;", "", DBHeadersTable.HeadersEntry.COLUMN_NAME_KEY, "", "(Ljava/lang/String;ILjava/lang/String;)V", "getKey", "()Ljava/lang/String;", "Archiver", "Ena", "Wpa", "Telegram", "Signal", "Unknown", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/model/ClientType.class */
public enum ClientType {
    Archiver(""),
    Ena(""),
    Wpa("Whatsapp"),
    Telegram("Telegram"),
    Signal("Signal"),
    Unknown(null);
    
    @Nullable
    private final String key;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

    @NotNull
    public static EnumEntries<ClientType> getEntries() {
        return $ENTRIES;
    }

    ClientType(String key) {
        this.key = key;
    }

    @Nullable
    public final String getKey() {
        return this.key;
    }
}
