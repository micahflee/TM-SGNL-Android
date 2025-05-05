package com.tm.androidcopysdk.api;

import com.tm.androidcopysdk.model.ArchiveMessageHeader;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import java.util.List;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
/* compiled from: IArchiveMessageHeaderDao.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n��\n\u0002\u0010 \n\u0002\b\u0002\bf\u0018��2\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001J\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u00052\u0006\u0010\u0006\u001a\u00020\u0002H&¨\u0006\u0007"}, d2 = {"Lcom/tm/androidcopysdk/api/IArchiveMessageHeaderDao;", "Lcom/tm/androidcopysdk/api/ISharedDao;", "", "Lcom/tm/androidcopysdk/model/ArchiveMessageHeader;", "findAllByMessageId", "", CallLogMessageRecorder.messageId_key, "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/api/IArchiveMessageHeaderDao.class */
public interface IArchiveMessageHeaderDao extends ISharedDao<String, ArchiveMessageHeader> {
    @NotNull
    List<ArchiveMessageHeader> findAllByMessageId(@NotNull String str);
}
