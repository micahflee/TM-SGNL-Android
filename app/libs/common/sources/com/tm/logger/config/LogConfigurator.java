package com.tm.logger.config;

import com.tm.utils.Definitions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.tinylog.configuration.Configuration;
/* compiled from: LogConfigurator.kt */
@Metadata(mv = {1, Definitions.sendSubject, 0}, k = 1, xi = 48, d1 = {"��6\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0003\u0018�� \u00142\u00020\u0001:\u0001\u0014B\u0005¢\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006J$\u0010\u0007\u001a\u00020\u00042\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u000b\u001a\u00020\u0006H\u0002J$\u0010\f\u001a\u00020\u00042\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u000b\u001a\u00020\u0006H\u0002J\u0016\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\nJ\u001c\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00122\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\nJ\u0018\u0010\u0013\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\nH\u0002¨\u0006\u0015"}, d2 = {"Lcom/tm/logger/config/LogConfigurator;", "", "()V", "configure", "", "logConfig", "Lcom/tm/logger/config/LogConfiguration;", "createFileWriter", "props", "", "", "configuration", "createLogcatWriter", "deleteOldFile", "parent", "Ljava/io/File;", Definitions.tName, "findAllLogFiles", "", "obtainNewName", "Companion", "common_release"})
@SourceDebugExtension({"SMAP\nLogConfigurator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LogConfigurator.kt\ncom/tm/logger/config/LogConfigurator\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,68:1\n215#2,2:69\n3792#3:71\n4307#3,2:72\n1045#4:74\n*S KotlinDebug\n*F\n+ 1 LogConfigurator.kt\ncom/tm/logger/config/LogConfigurator\n*L\n24#1:69,2\n52#1:71\n52#1:72,2\n52#1:74\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/logger/config/LogConfigurator.class */
public final class LogConfigurator {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private static final String WRITER_NAME = "writer";

    public final void configure(@NotNull LogConfiguration logConfig) {
        Intrinsics.checkNotNullParameter(logConfig, "logConfig");
        Map props = new LinkedHashMap();
        if (logConfig.getUseFileAppender()) {
            createFileWriter(props, logConfig);
        }
        if (logConfig.getUseLogcatAppender()) {
            createLogcatWriter(props, logConfig);
        }
        if (logConfig.getResetConfiguration()) {
            Configuration.replace(props);
            return;
        }
        for (Map.Entry element$iv : props.entrySet()) {
            Configuration.set(element$iv.getKey(), element$iv.getValue());
        }
    }

    private final void createFileWriter(Map<String, String> map, LogConfiguration configuration) {
        String name = obtainNewName(configuration, "rolling_file");
        map.put(name, TinyLogWriterType.RollingFile.getPropertyName());
        map.put(name + ".tag", configuration.getLoggerName());
        map.put(name + ".file", configuration.getFileName() + ".{count}");
        map.put(name + ".level", configuration.getRootLevel().getPropertyName());
        map.put(name + ".format", configuration.getFilePattern());
        map.put(name + ".policies", TinyLogPolicy.Size.getPropertyName() + ", size: " + (configuration.getMaxFileSize() / 1024) + "kb");
        map.put(name + ".backups", String.valueOf(configuration.getMaxFileCount()));
        map.put(name + ".buffered", configuration.getImmediateFlush() ? "false" : "true");
    }

    private final void createLogcatWriter(Map<String, String> map, LogConfiguration configuration) {
        String name = obtainNewName(configuration, "logcat");
        map.put(name, TinyLogWriterType.Logcat.getPropertyName());
        map.put(name + ".tag", configuration.getLoggerName());
        map.put(name + ".level", configuration.getRootLevel().getPropertyName());
        map.put(name + ".format", configuration.getLogcatPattern());
    }

    private final String obtainNewName(LogConfiguration configuration, String name) {
        return "writer_" + configuration.getLoggerName() + '_' + name;
    }

    @NotNull
    public final List<File> findAllLogFiles(@NotNull File parent, @NotNull String name) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        Intrinsics.checkNotNullParameter(name, Definitions.tName);
        File[] listFiles = parent.listFiles();
        if (listFiles != null) {
            Collection destination$iv$iv = new ArrayList();
            for (File file : listFiles) {
                String name2 = file.getName();
                Intrinsics.checkNotNullExpressionValue(name2, "getName(...)");
                if (StringsKt.startsWith$default(name2, name, false, 2, (Object) null)) {
                    destination$iv$iv.add(file);
                }
            }
            Iterable $this$sortedBy$iv = (List) destination$iv$iv;
            List<File> sortedWith = CollectionsKt.sortedWith($this$sortedBy$iv, new Comparator() { // from class: com.tm.logger.config.LogConfigurator$findAllLogFiles$$inlined$sortedBy$1
                @Override // java.util.Comparator
                public final int compare(T t, T t2) {
                    File it = (File) t;
                    File it2 = (File) t2;
                    return ComparisonsKt.compareValues(Long.valueOf(it.lastModified()), Long.valueOf(it2.lastModified()));
                }
            });
            if (sortedWith != null) {
                return sortedWith;
            }
        }
        return CollectionsKt.emptyList();
    }

    public final void deleteOldFile(@NotNull File parent, @NotNull String name) {
        Intrinsics.checkNotNullParameter(parent, "parent");
        Intrinsics.checkNotNullParameter(name, Definitions.tName);
        File file = new File(parent, name);
        if (file.exists()) {
            List logs = findAllLogFiles(parent, name);
            if (logs.size() >= 3) {
                file.delete();
            }
        }
    }

    /* compiled from: LogConfigurator.kt */
    @Metadata(mv = {1, Definitions.sendSubject, 0}, k = 1, xi = 48, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��¨\u0006\u0005"}, d2 = {"Lcom/tm/logger/config/LogConfigurator$Companion;", "", "()V", "WRITER_NAME", "", "common_release"})
    /* loaded from: input.aar:classes.jar:com/tm/logger/config/LogConfigurator$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }
    }
}
