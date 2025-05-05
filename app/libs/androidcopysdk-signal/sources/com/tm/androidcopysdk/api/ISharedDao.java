package com.tm.androidcopysdk.api;

import com.tm.androidcopysdk.recorder.MyAudioFormat;
import java.util.List;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: ISharedDao.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0018\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010��\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0002\bf\u0018��*\u0004\b��\u0010\u0001*\u0004\b\u0001\u0010\u00022\u00020\u0003J\u0017\u0010\u0004\u001a\u0004\u0018\u00018\u00012\u0006\u0010\u0005\u001a\u00028��H&¢\u0006\u0002\u0010\u0006J\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00010\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00028��0\bH&¨\u0006\n"}, d2 = {"Lcom/tm/androidcopysdk/api/ISharedDao;", "Id", "Entity", "", "find", "id", "(Ljava/lang/Object;)Ljava/lang/Object;", "findAll", "", "ids", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/api/ISharedDao.class */
public interface ISharedDao<Id, Entity> {
    @Nullable
    Entity find(Id id);

    @NotNull
    List<Entity> findAll(@NotNull List<? extends Id> list);
}
