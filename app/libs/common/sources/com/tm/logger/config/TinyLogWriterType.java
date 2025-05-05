package com.tm.logger.config;

import com.tm.utils.Definitions;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;
/* compiled from: TinyLogWriterType.kt */
@Metadata(mv = {1, Definitions.sendSubject, 0}, k = 1, xi = 48, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n��\n\u0002\u0010\u000e\n\u0002\b\f\b\u0086\u0081\u0002\u0018��2\b\u0012\u0004\u0012\u00020��0\u0001B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000e¨\u0006\u000f"}, d2 = {"Lcom/tm/logger/config/TinyLogWriterType;", "", "propertyName", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getPropertyName", "()Ljava/lang/String;", "Console", "File", "Jdbc", "Json", "Logcat", "RollingFile", "SharedFile", "SysLog", "common_release"})
/* loaded from: input.aar:classes.jar:com/tm/logger/config/TinyLogWriterType.class */
public enum TinyLogWriterType {
    Console("console"),
    File("file"),
    Jdbc("jdbc"),
    Json("json"),
    Logcat("logcat"),
    RollingFile("rolling file"),
    SharedFile("shared file"),
    SysLog("syslog");
    
    @NotNull
    private final String propertyName;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

    @NotNull
    public static EnumEntries<TinyLogWriterType> getEntries() {
        return $ENTRIES;
    }

    TinyLogWriterType(String propertyName) {
        this.propertyName = propertyName;
    }

    @NotNull
    public final String getPropertyName() {
        return this.propertyName;
    }
}
