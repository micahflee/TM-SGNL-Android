package com.tm.androidcopysdk.device;

import android.content.Context;
import android.webkit.MimeTypeMap;
import com.tm.androidcopysdk.api.IFiler;
import com.tm.androidcopysdk.converter.MessageDetailsConverter;
import com.tm.androidcopysdk.model.ArchiveAttachment;
import com.tm.androidcopysdk.model.ArchiveMessage;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: AbstractFiler.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��8\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\b&\u0018�� \u00182\u00020\u0001:\u0001\u0018B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\u0007\u001a\u00020\bH\u0016J\b\u0010\t\u001a\u00020\bH\u0016J\u0014\u0010\n\u001a\u0004\u0018\u00010\b2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016J\b\u0010\r\u001a\u00020\bH\u0004J\u0018\u0010\u000e\u001a\u00020\b2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016J\u001e\u0010\u0013\u001a\u0004\u0018\u00010\f2\b\u0010\u0014\u001a\u0004\u0018\u00010\f2\b\u0010\u0015\u001a\u0004\u0018\u00010\fH\u0016J\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0016R\u0014\u0010\u0002\u001a\u00020\u0003X\u0084\u0004¢\u0006\b\n��\u001a\u0004\b\u0005\u0010\u0006¨\u0006\u0019"}, d2 = {"Lcom/tm/androidcopysdk/device/AbstractFiler;", "Lcom/tm/androidcopysdk/api/IFiler;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "getContext", "()Landroid/content/Context;", "cacheDir", "Ljava/io/File;", "filesDir", "findCallRecording", "id", "", "getArchiveFileDir", "getAttachmentFile", "message", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", CallLogMessageRecorder.attachment_key, "Lcom/tm/androidcopysdk/model/ArchiveAttachment;", "getExtensionFromMimeType", "mimeType", "fileName", "streamIntoFile", "", "Companion", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nAbstractFiler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AbstractFiler.kt\ncom/tm/androidcopysdk/device/AbstractFiler\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 5 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,60:1\n1#2:61\n731#3,9:62\n37#4,2:71\n1282#5,2:73\n*S KotlinDebug\n*F\n+ 1 AbstractFiler.kt\ncom/tm/androidcopysdk/device/AbstractFiler\n*L\n35#1:62,9\n35#1:71,2\n49#1:73,2\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/device/AbstractFiler.class */
public abstract class AbstractFiler implements IFiler {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final Context context;
    @NotNull
    private static final String RECORDED_FILE_PREFIX = "recorded_audio_";
    @NotNull
    private static final String RECORDER_FILE_SUFFIX = ".pcm";
    @NotNull
    private static final String ARCHIVE_FILE_FOLDER_NAME = "aa_archiver";

    public AbstractFiler(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    @NotNull
    protected final Context getContext() {
        return this.context;
    }

    @Override // com.tm.androidcopysdk.api.IFiler
    @NotNull
    public File cacheDir() {
        File cacheDir = this.context.getCacheDir();
        Intrinsics.checkNotNullExpressionValue(cacheDir, "getCacheDir(...)");
        return cacheDir;
    }

    @Override // com.tm.androidcopysdk.api.IFiler
    @NotNull
    public File filesDir() {
        File filesDir = this.context.getFilesDir();
        Intrinsics.checkNotNullExpressionValue(filesDir, "getFilesDir(...)");
        return filesDir;
    }

    @Override // com.tm.androidcopysdk.api.IFiler
    public void streamIntoFile(@NotNull ArchiveMessage message, @NotNull ArchiveAttachment attachment) {
        Intrinsics.checkNotNullParameter(message, "message");
        Intrinsics.checkNotNullParameter(attachment, CallLogMessageRecorder.attachment_key);
        String p0 = attachment.getSourcePath();
        if (p0 != null) {
            File it = new File(p0);
            File file = it.exists() ? it : null;
            if (file == null) {
                return;
            }
            File sourcePath = file;
            String p02 = attachment.getArchivePath();
            if (p02 != null) {
                File archivePath = new File(p02);
                FileInputStream fileInputStream = new FileInputStream(sourcePath);
                try {
                    FileInputStream input = fileInputStream;
                    FileOutputStream fileOutputStream = new FileOutputStream(archivePath);
                    FileOutputStream output = fileOutputStream;
                    IOUtils.copy(input, output);
                    CloseableKt.closeFinally(fileOutputStream, (Throwable) null);
                    CloseableKt.closeFinally(fileInputStream, (Throwable) null);
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        CloseableKt.closeFinally(fileInputStream, th);
                        throw th2;
                    }
                }
            }
        }
    }

    @Override // com.tm.androidcopysdk.api.IFiler
    @Nullable
    public String getExtensionFromMimeType(@Nullable String mimeType, @Nullable String fileName) {
        String extensionFromMimeType;
        Collection emptyList;
        boolean z;
        String str = fileName;
        if (str == null || str.length() == 0) {
            if (mimeType == null) {
                return null;
            }
            return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }
        try {
            List $this$dropLastWhile$iv = new Regex("\\.").split(fileName, 0);
            if (!$this$dropLastWhile$iv.isEmpty()) {
                ListIterator iterator$iv = $this$dropLastWhile$iv.listIterator($this$dropLastWhile$iv.size());
                while (iterator$iv.hasPrevious()) {
                    String it = (String) iterator$iv.previous();
                    if (it.length() == 0) {
                        z = true;
                        continue;
                    } else {
                        z = false;
                        continue;
                    }
                    if (!z) {
                        emptyList = CollectionsKt.take($this$dropLastWhile$iv, iterator$iv.nextIndex() + 1);
                        break;
                    }
                }
            }
            emptyList = CollectionsKt.emptyList();
            Collection $this$toTypedArray$iv = emptyList;
            String[] splits = (String[]) $this$toTypedArray$iv.toArray(new String[0]);
            extensionFromMimeType = splits[splits.length - 1];
        } catch (Exception e) {
            extensionFromMimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }
        return extensionFromMimeType;
    }

    @Override // com.tm.androidcopysdk.api.IFiler
    @NotNull
    public File getAttachmentFile(@NotNull ArchiveMessage message, @NotNull ArchiveAttachment attachment) {
        Intrinsics.checkNotNullParameter(message, "message");
        Intrinsics.checkNotNullParameter(attachment, CallLogMessageRecorder.attachment_key);
        String name = MessageDetailsConverter.Companion.getFileNameWithType(message, this, attachment);
        return new File(getArchiveFileDir(), name);
    }

    @Override // com.tm.androidcopysdk.api.IFiler
    @Nullable
    public File findCallRecording(@Nullable String id) {
        String prefix = id + "_recorded_audio_";
        File[] listFiles = cacheDir().listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                String it = file.getName();
                Intrinsics.checkNotNull(it);
                if (StringsKt.startsWith$default(it, prefix, false, 2, (Object) null) && StringsKt.endsWith$default(it, RECORDER_FILE_SUFFIX, false, 2, (Object) null)) {
                    return file;
                }
            }
            return null;
        }
        return null;
    }

    @NotNull
    protected final File getArchiveFileDir() {
        File it = new File(filesDir(), ARCHIVE_FILE_FOLDER_NAME);
        if (!it.exists()) {
            it.mkdir();
        }
        return it;
    }

    /* compiled from: AbstractFiler.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0014\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��¨\u0006\u0007"}, d2 = {"Lcom/tm/androidcopysdk/device/AbstractFiler$Companion;", "", "()V", "ARCHIVE_FILE_FOLDER_NAME", "", "RECORDED_FILE_PREFIX", "RECORDER_FILE_SUFFIX", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/device/AbstractFiler$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }
    }
}
