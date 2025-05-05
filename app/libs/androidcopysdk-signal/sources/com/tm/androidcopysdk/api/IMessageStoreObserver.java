package com.tm.androidcopysdk.api;

import com.tm.androidcopysdk.device.MessageStoreProcessor;
import com.tm.androidcopysdk.model.ArchiveMessage;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import java.util.List;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
/* compiled from: IMessageStoreObserver.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��2\n\u0002\u0018\u0002\n��\n\u0002\u0010��\n��\n\u0002\u0010\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018��*\u0004\b��\u0010\u00012\u00020\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H&J\u0015\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00028��H&¢\u0006\u0002\u0010\tJ\u0016\u0010\n\u001a\u00020\u00042\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00028��0\fH&J\u0010\u0010\r\u001a\u00020\u00042\u0006\u0010\u000e\u001a\u00020\u000fH&J\u0016\u0010\u0010\u001a\u00020\u00042\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000f0\fH&J\b\u0010\u0012\u001a\u00020\u0004H&J\b\u0010\u0013\u001a\u00020\u0004H&J\b\u0010\u0014\u001a\u00020\u0004H&J\u0016\u0010\u0015\u001a\u00020\u00042\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00028��0\u0017H&J\u0010\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H&¨\u0006\u0019"}, d2 = {"Lcom/tm/androidcopysdk/api/IMessageStoreObserver;", "Id", "", "addProcessor", "", "processor", "Lcom/tm/androidcopysdk/device/MessageStoreProcessor;", "afterMessageIdStateChanged", "id", "(Ljava/lang/Object;)V", "afterMessageIdsStateChanged", "ids", "", "afterMessageStateChanged", "message", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", "afterMessagesStateChanged", "messages", "clearProcessors", "disable", "enable", "initialize", "module", "Lcom/tm/androidcopysdk/api/SdkModule;", "removeProcessor", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/api/IMessageStoreObserver.class */
public interface IMessageStoreObserver<Id> {
    void initialize(@NotNull SdkModule<Id> sdkModule);

    void enable();

    void disable();

    void addProcessor(@NotNull MessageStoreProcessor messageStoreProcessor);

    void removeProcessor(@NotNull MessageStoreProcessor messageStoreProcessor);

    void clearProcessors();

    void afterMessageIdStateChanged(Id id);

    void afterMessageStateChanged(@NotNull ArchiveMessage archiveMessage);

    void afterMessageIdsStateChanged(@NotNull List<? extends Id> list);

    void afterMessagesStateChanged(@NotNull List<ArchiveMessage> list);
}
