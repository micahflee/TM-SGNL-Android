package com.tm.androidcopysdk.device;

import com.tm.androidcopysdk.api.SdkModule;
import com.tm.androidcopysdk.model.ArchiveMessage;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: SendSignatureProcessor.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018��2\u00020\u0001B\u0011\u0012\n\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003¢\u0006\u0002\u0010\u0004J\u001a\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\fH\u0014R\u0012\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003X\u0082\u0004¢\u0006\u0002\n��R\u0014\u0010\u0005\u001a\u00020\u0006X\u0096D¢\u0006\b\n��\u001a\u0004\b\u0007\u0010\b¨\u0006\u000e"}, d2 = {"Lcom/tm/androidcopysdk/device/SendSignatureProcessor;", "Lcom/tm/androidcopysdk/device/MessageStoreProcessor;", "module", "Lcom/tm/androidcopysdk/api/SdkModule;", "(Lcom/tm/androidcopysdk/api/SdkModule;)V", "tag", "", "getTag", "()Ljava/lang/String;", "processAfterMessageStateChanged", "", "message", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", "existing", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/device/SendSignatureProcessor.class */
public final class SendSignatureProcessor extends MessageStoreProcessor {
    @NotNull
    private final SdkModule<?> module;
    @NotNull
    private final String tag;

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public SendSignatureProcessor(@org.jetbrains.annotations.NotNull com.tm.androidcopysdk.api.SdkModule<?> r5) {
        /*
            r4 = this;
            r0 = r5
            java.lang.String r1 = "module"
            kotlin.jvm.internal.Intrinsics.checkNotNullParameter(r0, r1)
            r0 = r4
            com.tm.androidcopysdk.model.ArchiveMessageType[] r1 = com.tm.androidcopysdk.model.ArchiveMessageType.values()
            r6 = r1
            r1 = r6
            r2 = r6
            int r2 = r2.length
            java.lang.Object[] r1 = java.util.Arrays.copyOf(r1, r2)
            com.tm.androidcopysdk.model.ArchiveMessageType[] r1 = (com.tm.androidcopysdk.model.ArchiveMessageType[]) r1
            r0.<init>(r1)
            r0 = r4
            r1 = r5
            r0.module = r1
            r0 = r4
            java.lang.String r1 = "SignatureMessageStoreCallback"
            r0.tag = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tm.androidcopysdk.device.SendSignatureProcessor.<init>(com.tm.androidcopysdk.api.SdkModule):void");
    }

    @Override // com.tm.androidcopysdk.device.MessageStoreProcessor
    @NotNull
    public String getTag() {
        return this.tag;
    }

    @Override // com.tm.androidcopysdk.device.MessageStoreProcessor
    protected void processAfterMessageStateChanged(@NotNull ArchiveMessage message, @Nullable ArchiveMessage existing) {
        Intrinsics.checkNotNullParameter(message, "message");
    }
}
