package com.tm.androidcopysdk.api;

import com.tm.androidcopysdk.model.ArchiveAttachment;
import com.tm.androidcopysdk.model.ArchiveMessage;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import java.io.File;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: IFiler.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��,\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0010\u000e\n��\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n��\bf\u0018��2\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0005H&J\u0014\u0010\u0007\u001a\u0004\u0018\u00010\u00052\b\u0010\b\u001a\u0004\u0018\u00010\u0003H&J\u0018\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH&J \u0010\u000e\u001a\u0004\u0018\u00010\u00032\b\u0010\u000f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u0003H&J\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH&¨\u0006\u0013"}, d2 = {"Lcom/tm/androidcopysdk/api/IFiler;", "", "archiveAttachmentFileTemplatePrefix", "", "cacheDir", "Ljava/io/File;", "filesDir", "findCallRecording", "id", "getAttachmentFile", "message", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", CallLogMessageRecorder.attachment_key, "Lcom/tm/androidcopysdk/model/ArchiveAttachment;", "getExtensionFromMimeType", "mimeType", "fileName", "streamIntoFile", "", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/api/IFiler.class */
public interface IFiler {
    @NotNull
    File filesDir();

    @NotNull
    File cacheDir();

    void streamIntoFile(@NotNull ArchiveMessage archiveMessage, @NotNull ArchiveAttachment archiveAttachment);

    @Nullable
    String getExtensionFromMimeType(@Nullable String str, @Nullable String str2);

    @NotNull
    String archiveAttachmentFileTemplatePrefix();

    @NotNull
    File getAttachmentFile(@NotNull ArchiveMessage archiveMessage, @NotNull ArchiveAttachment archiveAttachment);

    @Nullable
    File findCallRecording(@Nullable String str);

    /* compiled from: IFiler.kt */
    @Metadata(mv = {1, 9, 0}, k = 3, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK)
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/api/IFiler$DefaultImpls.class */
    public static final class DefaultImpls {
        public static /* synthetic */ String getExtensionFromMimeType$default(IFiler iFiler, String str, String str2, int i, Object obj) {
            if (obj != null) {
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getExtensionFromMimeType");
            }
            if ((i & 2) != 0) {
                str2 = null;
            }
            return iFiler.getExtensionFromMimeType(str, str2);
        }
    }
}
