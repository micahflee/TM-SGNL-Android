package com.tm.logger.config;

import com.tm.utils.Definitions;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: LogConfiguration.kt */
@Metadata(mv = {1, Definitions.sendSubject, 0}, k = 1, xi = 48, d1 = {"��,\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n��\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n��\n\u0002\u0010\t\n��\n\u0002\u0010\u000b\n\u0002\b8\b\u0086\b\u0018��2\u00020\u0001B}\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u0012\u001a\u00020\u000e¢\u0006\u0002\u0010\u0013J\t\u00105\u001a\u00020\u0003HÆ\u0003J\t\u00106\u001a\u00020\u000eHÆ\u0003J\t\u00107\u001a\u00020\u000eHÆ\u0003J\t\u00108\u001a\u00020\u000eHÆ\u0003J\t\u00109\u001a\u00020\u0005HÆ\u0003J\t\u0010:\u001a\u00020\u0003HÆ\u0003J\t\u0010;\u001a\u00020\u0003HÆ\u0003J\t\u0010<\u001a\u00020\u0003HÆ\u0003J\t\u0010=\u001a\u00020\nHÆ\u0003J\t\u0010>\u001a\u00020\fHÆ\u0003J\t\u0010?\u001a\u00020\u000eHÆ\u0003J\t\u0010@\u001a\u00020\u000eHÆ\u0003J\u0081\u0001\u0010A\u001a\u00020��2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000e2\b\b\u0002\u0010\u0010\u001a\u00020\u000e2\b\b\u0002\u0010\u0011\u001a\u00020\u000e2\b\b\u0002\u0010\u0012\u001a\u00020\u000eHÆ\u0001J\u0013\u0010B\u001a\u00020\u000e2\b\u0010C\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010D\u001a\u00020\nHÖ\u0001J\t\u0010E\u001a\u00020\u0003HÖ\u0001R\u001a\u0010\b\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0006\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0018\u0010\u0015\"\u0004\b\u0019\u0010\u0017R\u001a\u0010\r\u001a\u00020\u000eX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001dR\u001a\u0010\u0012\u001a\u00020\u000eX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u001e\u0010\u001b\"\u0004\b\u001f\u0010\u001dR\u001a\u0010\u0007\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b \u0010\u0015\"\u0004\b!\u0010\u0017R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\"\u0010\u0015R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b#\u0010$\"\u0004\b%\u0010&R\u001a\u0010\u000b\u001a\u00020\fX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b'\u0010(\"\u0004\b)\u0010*R\u001a\u0010\u0011\u001a\u00020\u000eX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b+\u0010\u001b\"\u0004\b,\u0010\u001dR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b-\u0010.\"\u0004\b/\u00100R\u001a\u0010\u0010\u001a\u00020\u000eX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b1\u0010\u001b\"\u0004\b2\u0010\u001dR\u001a\u0010\u000f\u001a\u00020\u000eX\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b3\u0010\u001b\"\u0004\b4\u0010\u001d¨\u0006F"}, d2 = {"Lcom/tm/logger/config/LogConfiguration;", "", "loggerName", "", "rootLevel", "Lcom/tm/logger/config/Level;", "filePattern", "logcatPattern", "fileName", "maxFileCount", "", "maxFileSize", "", "immediateFlush", "", "useLogcatAppender", "useFileAppender", "resetConfiguration", "internalDebugging", "(Ljava/lang/String;Lcom/tm/logger/config/Level;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IJZZZZZ)V", "getFileName", "()Ljava/lang/String;", "setFileName", "(Ljava/lang/String;)V", "getFilePattern", "setFilePattern", "getImmediateFlush", "()Z", "setImmediateFlush", "(Z)V", "getInternalDebugging", "setInternalDebugging", "getLogcatPattern", "setLogcatPattern", "getLoggerName", "getMaxFileCount", "()I", "setMaxFileCount", "(I)V", "getMaxFileSize", "()J", "setMaxFileSize", "(J)V", "getResetConfiguration", "setResetConfiguration", "getRootLevel", "()Lcom/tm/logger/config/Level;", "setRootLevel", "(Lcom/tm/logger/config/Level;)V", "getUseFileAppender", "setUseFileAppender", "getUseLogcatAppender", "setUseLogcatAppender", "component1", "component10", "component11", "component12", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "common_release"})
/* loaded from: input.aar:classes.jar:com/tm/logger/config/LogConfiguration.class */
public final class LogConfiguration {
    @NotNull
    private final String loggerName;
    @NotNull
    private Level rootLevel;
    @NotNull
    private String filePattern;
    @NotNull
    private String logcatPattern;
    @NotNull
    private String fileName;
    private int maxFileCount;
    private long maxFileSize;
    private boolean immediateFlush;
    private boolean useLogcatAppender;
    private boolean useFileAppender;
    private boolean resetConfiguration;
    private boolean internalDebugging;

    @NotNull
    public final String component1() {
        return this.loggerName;
    }

    @NotNull
    public final Level component2() {
        return this.rootLevel;
    }

    @NotNull
    public final String component3() {
        return this.filePattern;
    }

    @NotNull
    public final String component4() {
        return this.logcatPattern;
    }

    @NotNull
    public final String component5() {
        return this.fileName;
    }

    public final int component6() {
        return this.maxFileCount;
    }

    public final long component7() {
        return this.maxFileSize;
    }

    public final boolean component8() {
        return this.immediateFlush;
    }

    public final boolean component9() {
        return this.useLogcatAppender;
    }

    public final boolean component10() {
        return this.useFileAppender;
    }

    public final boolean component11() {
        return this.resetConfiguration;
    }

    public final boolean component12() {
        return this.internalDebugging;
    }

    @NotNull
    public final LogConfiguration copy(@NotNull String loggerName, @NotNull Level rootLevel, @NotNull String filePattern, @NotNull String logcatPattern, @NotNull String fileName, int maxFileCount, long maxFileSize, boolean immediateFlush, boolean useLogcatAppender, boolean useFileAppender, boolean resetConfiguration, boolean internalDebugging) {
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
        Intrinsics.checkNotNullParameter(filePattern, "filePattern");
        Intrinsics.checkNotNullParameter(logcatPattern, "logcatPattern");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        return new LogConfiguration(loggerName, rootLevel, filePattern, logcatPattern, fileName, maxFileCount, maxFileSize, immediateFlush, useLogcatAppender, useFileAppender, resetConfiguration, internalDebugging);
    }

    public static /* synthetic */ LogConfiguration copy$default(LogConfiguration logConfiguration, String str, Level level, String str2, String str3, String str4, int i, long j, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = logConfiguration.loggerName;
        }
        if ((i2 & 2) != 0) {
            level = logConfiguration.rootLevel;
        }
        if ((i2 & 4) != 0) {
            str2 = logConfiguration.filePattern;
        }
        if ((i2 & 8) != 0) {
            str3 = logConfiguration.logcatPattern;
        }
        if ((i2 & 16) != 0) {
            str4 = logConfiguration.fileName;
        }
        if ((i2 & 32) != 0) {
            i = logConfiguration.maxFileCount;
        }
        if ((i2 & 64) != 0) {
            j = logConfiguration.maxFileSize;
        }
        if ((i2 & 128) != 0) {
            z = logConfiguration.immediateFlush;
        }
        if ((i2 & 256) != 0) {
            z2 = logConfiguration.useLogcatAppender;
        }
        if ((i2 & 512) != 0) {
            z3 = logConfiguration.useFileAppender;
        }
        if ((i2 & 1024) != 0) {
            z4 = logConfiguration.resetConfiguration;
        }
        if ((i2 & 2048) != 0) {
            z5 = logConfiguration.internalDebugging;
        }
        return logConfiguration.copy(str, level, str2, str3, str4, i, j, z, z2, z3, z4, z5);
    }

    @NotNull
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LogConfiguration(loggerName=").append(this.loggerName).append(", rootLevel=").append(this.rootLevel).append(", filePattern=").append(this.filePattern).append(", logcatPattern=").append(this.logcatPattern).append(", fileName=").append(this.fileName).append(", maxFileCount=").append(this.maxFileCount).append(", maxFileSize=").append(this.maxFileSize).append(", immediateFlush=").append(this.immediateFlush).append(", useLogcatAppender=").append(this.useLogcatAppender).append(", useFileAppender=").append(this.useFileAppender).append(", resetConfiguration=").append(this.resetConfiguration).append(", internalDebugging=");
        sb.append(this.internalDebugging).append(')');
        return sb.toString();
    }

    public int hashCode() {
        int result = this.loggerName.hashCode();
        return (((((((((((((((((((((result * 31) + this.rootLevel.hashCode()) * 31) + this.filePattern.hashCode()) * 31) + this.logcatPattern.hashCode()) * 31) + this.fileName.hashCode()) * 31) + Integer.hashCode(this.maxFileCount)) * 31) + Long.hashCode(this.maxFileSize)) * 31) + Boolean.hashCode(this.immediateFlush)) * 31) + Boolean.hashCode(this.useLogcatAppender)) * 31) + Boolean.hashCode(this.useFileAppender)) * 31) + Boolean.hashCode(this.resetConfiguration)) * 31) + Boolean.hashCode(this.internalDebugging);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof LogConfiguration) {
            LogConfiguration logConfiguration = (LogConfiguration) other;
            return Intrinsics.areEqual(this.loggerName, logConfiguration.loggerName) && this.rootLevel == logConfiguration.rootLevel && Intrinsics.areEqual(this.filePattern, logConfiguration.filePattern) && Intrinsics.areEqual(this.logcatPattern, logConfiguration.logcatPattern) && Intrinsics.areEqual(this.fileName, logConfiguration.fileName) && this.maxFileCount == logConfiguration.maxFileCount && this.maxFileSize == logConfiguration.maxFileSize && this.immediateFlush == logConfiguration.immediateFlush && this.useLogcatAppender == logConfiguration.useLogcatAppender && this.useFileAppender == logConfiguration.useFileAppender && this.resetConfiguration == logConfiguration.resetConfiguration && this.internalDebugging == logConfiguration.internalDebugging;
        }
        return false;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName, @NotNull Level rootLevel, @NotNull String filePattern, @NotNull String logcatPattern, @NotNull String fileName, int maxFileCount, long maxFileSize, boolean immediateFlush, boolean useLogcatAppender, boolean useFileAppender, boolean resetConfiguration) {
        this(loggerName, rootLevel, filePattern, logcatPattern, fileName, maxFileCount, maxFileSize, immediateFlush, useLogcatAppender, useFileAppender, resetConfiguration, false, 2048, null);
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
        Intrinsics.checkNotNullParameter(filePattern, "filePattern");
        Intrinsics.checkNotNullParameter(logcatPattern, "logcatPattern");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName, @NotNull Level rootLevel, @NotNull String filePattern, @NotNull String logcatPattern, @NotNull String fileName, int maxFileCount, long maxFileSize, boolean immediateFlush, boolean useLogcatAppender, boolean useFileAppender) {
        this(loggerName, rootLevel, filePattern, logcatPattern, fileName, maxFileCount, maxFileSize, immediateFlush, useLogcatAppender, useFileAppender, false, false, 3072, null);
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
        Intrinsics.checkNotNullParameter(filePattern, "filePattern");
        Intrinsics.checkNotNullParameter(logcatPattern, "logcatPattern");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName, @NotNull Level rootLevel, @NotNull String filePattern, @NotNull String logcatPattern, @NotNull String fileName, int maxFileCount, long maxFileSize, boolean immediateFlush, boolean useLogcatAppender) {
        this(loggerName, rootLevel, filePattern, logcatPattern, fileName, maxFileCount, maxFileSize, immediateFlush, useLogcatAppender, false, false, false, 3584, null);
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
        Intrinsics.checkNotNullParameter(filePattern, "filePattern");
        Intrinsics.checkNotNullParameter(logcatPattern, "logcatPattern");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName, @NotNull Level rootLevel, @NotNull String filePattern, @NotNull String logcatPattern, @NotNull String fileName, int maxFileCount, long maxFileSize, boolean immediateFlush) {
        this(loggerName, rootLevel, filePattern, logcatPattern, fileName, maxFileCount, maxFileSize, immediateFlush, false, false, false, false, 3840, null);
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
        Intrinsics.checkNotNullParameter(filePattern, "filePattern");
        Intrinsics.checkNotNullParameter(logcatPattern, "logcatPattern");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName, @NotNull Level rootLevel, @NotNull String filePattern, @NotNull String logcatPattern, @NotNull String fileName, int maxFileCount, long maxFileSize) {
        this(loggerName, rootLevel, filePattern, logcatPattern, fileName, maxFileCount, maxFileSize, false, false, false, false, false, 3968, null);
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
        Intrinsics.checkNotNullParameter(filePattern, "filePattern");
        Intrinsics.checkNotNullParameter(logcatPattern, "logcatPattern");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName, @NotNull Level rootLevel, @NotNull String filePattern, @NotNull String logcatPattern, @NotNull String fileName, int maxFileCount) {
        this(loggerName, rootLevel, filePattern, logcatPattern, fileName, maxFileCount, 0L, false, false, false, false, false, 4032, null);
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
        Intrinsics.checkNotNullParameter(filePattern, "filePattern");
        Intrinsics.checkNotNullParameter(logcatPattern, "logcatPattern");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName, @NotNull Level rootLevel, @NotNull String filePattern, @NotNull String logcatPattern, @NotNull String fileName) {
        this(loggerName, rootLevel, filePattern, logcatPattern, fileName, 0, 0L, false, false, false, false, false, 4064, null);
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
        Intrinsics.checkNotNullParameter(filePattern, "filePattern");
        Intrinsics.checkNotNullParameter(logcatPattern, "logcatPattern");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName, @NotNull Level rootLevel, @NotNull String filePattern, @NotNull String logcatPattern) {
        this(loggerName, rootLevel, filePattern, logcatPattern, null, 0, 0L, false, false, false, false, false, 4080, null);
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
        Intrinsics.checkNotNullParameter(filePattern, "filePattern");
        Intrinsics.checkNotNullParameter(logcatPattern, "logcatPattern");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName, @NotNull Level rootLevel, @NotNull String filePattern) {
        this(loggerName, rootLevel, filePattern, null, null, 0, 0L, false, false, false, false, false, 4088, null);
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
        Intrinsics.checkNotNullParameter(filePattern, "filePattern");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName, @NotNull Level rootLevel) {
        this(loggerName, rootLevel, null, null, null, 0, 0L, false, false, false, false, false, 4092, null);
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName) {
        this(loggerName, null, null, null, null, 0, 0L, false, false, false, false, false, 4094, null);
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
    }

    @JvmOverloads
    public LogConfiguration(@NotNull String loggerName, @NotNull Level rootLevel, @NotNull String filePattern, @NotNull String logcatPattern, @NotNull String fileName, int maxFileCount, long maxFileSize, boolean immediateFlush, boolean useLogcatAppender, boolean useFileAppender, boolean resetConfiguration, boolean internalDebugging) {
        Intrinsics.checkNotNullParameter(loggerName, "loggerName");
        Intrinsics.checkNotNullParameter(rootLevel, "rootLevel");
        Intrinsics.checkNotNullParameter(filePattern, "filePattern");
        Intrinsics.checkNotNullParameter(logcatPattern, "logcatPattern");
        Intrinsics.checkNotNullParameter(fileName, "fileName");
        this.loggerName = loggerName;
        this.rootLevel = rootLevel;
        this.filePattern = filePattern;
        this.logcatPattern = logcatPattern;
        this.fileName = fileName;
        this.maxFileCount = maxFileCount;
        this.maxFileSize = maxFileSize;
        this.immediateFlush = immediateFlush;
        this.useLogcatAppender = useLogcatAppender;
        this.useFileAppender = useFileAppender;
        this.resetConfiguration = resetConfiguration;
        this.internalDebugging = internalDebugging;
    }

    public /* synthetic */ LogConfiguration(String str, Level level, String str2, String str3, String str4, int i, long j, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, (i2 & 2) != 0 ? Level.Trace : level, (i2 & 4) != 0 ? "{date: yyyy-MM-dd HH:mm:ss.SSS} [{{level}|size=5}::{thread|size=10}::{class-name}] {message}" : str2, (i2 & 8) != 0 ? "{message}" : str3, (i2 & 16) != 0 ? "android-log4j.log" : str4, (i2 & 32) != 0 ? 5 : i, (i2 & 64) != 0 ? 524288L : j, (i2 & 128) != 0 ? true : z, (i2 & 256) != 0 ? true : z2, (i2 & 512) != 0 ? true : z3, (i2 & 1024) != 0 ? true : z4, (i2 & 2048) != 0 ? false : z5);
    }

    @NotNull
    public final String getLoggerName() {
        return this.loggerName;
    }

    @NotNull
    public final Level getRootLevel() {
        return this.rootLevel;
    }

    public final void setRootLevel(@NotNull Level level) {
        Intrinsics.checkNotNullParameter(level, "<set-?>");
        this.rootLevel = level;
    }

    @NotNull
    public final String getFilePattern() {
        return this.filePattern;
    }

    public final void setFilePattern(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.filePattern = str;
    }

    @NotNull
    public final String getLogcatPattern() {
        return this.logcatPattern;
    }

    public final void setLogcatPattern(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.logcatPattern = str;
    }

    @NotNull
    public final String getFileName() {
        return this.fileName;
    }

    public final void setFileName(@NotNull String str) {
        Intrinsics.checkNotNullParameter(str, "<set-?>");
        this.fileName = str;
    }

    public final int getMaxFileCount() {
        return this.maxFileCount;
    }

    public final void setMaxFileCount(int i) {
        this.maxFileCount = i;
    }

    public final long getMaxFileSize() {
        return this.maxFileSize;
    }

    public final void setMaxFileSize(long j) {
        this.maxFileSize = j;
    }

    public final boolean getImmediateFlush() {
        return this.immediateFlush;
    }

    public final void setImmediateFlush(boolean z) {
        this.immediateFlush = z;
    }

    public final boolean getUseLogcatAppender() {
        return this.useLogcatAppender;
    }

    public final void setUseLogcatAppender(boolean z) {
        this.useLogcatAppender = z;
    }

    public final boolean getUseFileAppender() {
        return this.useFileAppender;
    }

    public final void setUseFileAppender(boolean z) {
        this.useFileAppender = z;
    }

    public final boolean getResetConfiguration() {
        return this.resetConfiguration;
    }

    public final void setResetConfiguration(boolean z) {
        this.resetConfiguration = z;
    }

    public final boolean getInternalDebugging() {
        return this.internalDebugging;
    }

    public final void setInternalDebugging(boolean z) {
        this.internalDebugging = z;
    }
}
