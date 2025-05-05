package com.tm.androidcopysdk.device;

import android.os.Handler;
import android.os.Looper;
import com.tm.androidcopysdk.api.IArchiveMessageDao;
import com.tm.androidcopysdk.api.IMessageStoreObserver;
import com.tm.androidcopysdk.api.SdkModule;
import com.tm.androidcopysdk.model.ArchiveMessage;
import com.tm.androidcopysdk.model.ArchiveMessageIdentifier;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.RuntimeObject;
import com.tm.logger.Log;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: DefaultMessageStoreObserver.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��`\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010!\n\u0002\u0018\u0002\n��\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018�� -*\u0004\b��\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002:\u0001-B\u0005¢\u0006\u0002\u0010\u0003J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\rH\u0016J\u0015\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00028��H\u0016¢\u0006\u0002\u0010\u0013J\u0016\u0010\u0014\u001a\u00020\u000f2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00028��0\u0016H\u0016J\u0010\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u0019H\u0016J\u0016\u0010\u001a\u001a\u00020\u000f2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00190\u0016H\u0016J\b\u0010\u001c\u001a\u00020\u000fH\u0016J\b\u0010\u001d\u001a\u00020\u000fH\u0016J\b\u0010\u001e\u001a\u00020\u000fH\u0016J\b\u0010\u001f\u001a\u00020\u000fH\u0002J5\u0010 \u001a\u00020\u000f\"\u0004\b\u0001\u0010!2\u0014\b\u0004\u0010\"\u001a\u000e\u0012\u0004\u0012\u0002H!\u0012\u0004\u0012\u00020\u000f0#2\u000e\b\u0004\u0010$\u001a\b\u0012\u0004\u0012\u0002H!0%H\u0082\bJ\u0016\u0010&\u001a\u00020\u000f2\f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00190\u0016H\u0002J\u000e\u0010'\u001a\b\u0012\u0004\u0012\u00028��0(H\u0002J\u000e\u0010)\u001a\b\u0012\u0004\u0012\u00020*0(H\u0002J\u0016\u0010+\u001a\u00020\u000f2\f\u0010\t\u001a\b\u0012\u0004\u0012\u00028��0\nH\u0016J\u0010\u0010,\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\rH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n��R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.¢\u0006\u0002\n��R\u000e\u0010\b\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n��R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00028��0\nX\u0082.¢\u0006\u0002\n��R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004¢\u0006\u0002\n��¨\u0006."}, d2 = {"Lcom/tm/androidcopysdk/device/DefaultMessageStoreObserver;", "Id", "Lcom/tm/androidcopysdk/api/IMessageStoreObserver;", "()V", "enabled", "", "handler", "Landroid/os/Handler;", "initialized", "module", "Lcom/tm/androidcopysdk/api/SdkModule;", "processors", "", "Lcom/tm/androidcopysdk/device/MessageStoreProcessor;", "addProcessor", "", "processor", "afterMessageIdStateChanged", "id", "(Ljava/lang/Object;)V", "afterMessageIdsStateChanged", "ids", "", "afterMessageStateChanged", "message", "Lcom/tm/androidcopysdk/model/ArchiveMessage;", "afterMessagesStateChanged", "messages", "clearProcessors", "disable", "enable", "ensureBackgroundThread", "execute", "T", "block", "Lkotlin/Function1;", "valueFetcher", "Lkotlin/Function0;", "executeAfterMessagesStateChanged", "getApplicationMessageDao", "Lcom/tm/androidcopysdk/api/IArchiveMessageDao;", "getArchiverMessageDao", "Lcom/tm/androidcopysdk/model/ArchiveMessageIdentifier;", "initialize", "removeProcessor", "Companion", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nDefaultMessageStoreObserver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DefaultMessageStoreObserver.kt\ncom/tm/androidcopysdk/device/DefaultMessageStoreObserver\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,141:1\n92#1,8:142\n110#1,6:150\n92#1,8:156\n110#1,6:164\n92#1,8:170\n110#1,6:178\n92#1,8:184\n110#1,6:192\n1855#2:198\n1855#2,2:199\n1856#2:201\n1#3:202\n*S KotlinDebug\n*F\n+ 1 DefaultMessageStoreObserver.kt\ncom/tm/androidcopysdk/device/DefaultMessageStoreObserver\n*L\n56#1:142,8\n56#1:150,6\n60#1:156,8\n60#1:164,6\n64#1:170,8\n64#1:178,6\n68#1:184,8\n68#1:192,6\n81#1:198\n83#1:199,2\n81#1:201\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/device/DefaultMessageStoreObserver.class */
public final class DefaultMessageStoreObserver<Id> implements IMessageStoreObserver<Id> {
    @NotNull
    public static final Companion Companion = new Companion(null);
    private SdkModule<Id> module;
    private Handler handler;
    private boolean initialized;
    private boolean enabled = true;
    @NotNull
    private final List<MessageStoreProcessor> processors = new ArrayList();
    @NotNull
    private static final String TAG = "DefaultMessageStoreObserver";
    @Nullable
    private static IMessageStoreObserver<?> instance;

    @JvmStatic
    @NotNull
    public static final <T> IMessageStoreObserver<T> getInstance() {
        return Companion.getInstance();
    }

    @Override // com.tm.androidcopysdk.api.IMessageStoreObserver
    public void initialize(@NotNull SdkModule<Id> sdkModule) {
        Intrinsics.checkNotNullParameter(sdkModule, "module");
        if (this.initialized) {
            return;
        }
        this.initialized = true;
        this.module = sdkModule;
        this.handler = new Handler(sdkModule.getHandlerThread().getLooper());
    }

    @Override // com.tm.androidcopysdk.api.IMessageStoreObserver
    public void enable() {
        this.enabled = true;
    }

    @Override // com.tm.androidcopysdk.api.IMessageStoreObserver
    public void disable() {
        this.enabled = false;
    }

    @Override // com.tm.androidcopysdk.api.IMessageStoreObserver
    public void addProcessor(@NotNull MessageStoreProcessor processor) {
        Intrinsics.checkNotNullParameter(processor, "processor");
        if (!this.processors.contains(processor)) {
            this.processors.add(processor);
        }
    }

    @Override // com.tm.androidcopysdk.api.IMessageStoreObserver
    public void removeProcessor(@NotNull MessageStoreProcessor processor) {
        Intrinsics.checkNotNullParameter(processor, "processor");
        this.processors.remove(processor);
    }

    @Override // com.tm.androidcopysdk.api.IMessageStoreObserver
    public void clearProcessors() {
        this.processors.clear();
    }

    @Override // com.tm.androidcopysdk.api.IMessageStoreObserver
    public void afterMessageIdStateChanged(Id id) {
        ensureBackgroundThread();
        if (!this.initialized) {
            return;
        }
        final long start$iv = System.currentTimeMillis();
        final String sender$iv = RuntimeObject.INSTANCE.getCallerClassMethodAndLine(1);
        try {
            final Object value$iv = CollectionsKt.listOfNotNull(getApplicationMessageDao().find(id));
            Handler handler = this.handler;
            if (handler == null) {
                Intrinsics.throwUninitializedPropertyAccessException("handler");
                handler = null;
            }
            handler.post(new Runnable() { // from class: com.tm.androidcopysdk.device.DefaultMessageStoreObserver$afterMessageIdStateChanged$$inlined$execute$1
                @Override // java.lang.Runnable
                public final void run() {
                    long now;
                    try {
                        List p0 = (List) value$iv;
                        this.executeAfterMessagesStateChanged(p0);
                        Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now - start$iv) + "ms");
                    } catch (Throwable e) {
                        try {
                            e.printStackTrace();
                            Log.e("DefaultMessageStoreObserver", "post execute from " + sender$iv + " failed " + e.getMessage(), e);
                            long now2 = System.currentTimeMillis();
                            Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now2 - start$iv) + "ms");
                        } finally {
                            now = System.currentTimeMillis();
                            Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now - start$iv) + "ms");
                        }
                    }
                }
            });
        } catch (Throwable e$iv) {
            e$iv.printStackTrace();
            long now$iv = System.currentTimeMillis();
            Log.e(TAG, "execute from " + sender$iv + " failed. took " + (now$iv - start$iv) + "ms", e$iv);
        }
    }

    @Override // com.tm.androidcopysdk.api.IMessageStoreObserver
    public void afterMessageStateChanged(@NotNull ArchiveMessage message) {
        Intrinsics.checkNotNullParameter(message, "message");
        ensureBackgroundThread();
        if (!this.initialized) {
            return;
        }
        final long start$iv = System.currentTimeMillis();
        final String sender$iv = RuntimeObject.INSTANCE.getCallerClassMethodAndLine(1);
        try {
            final Object value$iv = CollectionsKt.listOf(message);
            Handler handler = this.handler;
            if (handler == null) {
                Intrinsics.throwUninitializedPropertyAccessException("handler");
                handler = null;
            }
            handler.post(new Runnable() { // from class: com.tm.androidcopysdk.device.DefaultMessageStoreObserver$afterMessageStateChanged$$inlined$execute$1
                @Override // java.lang.Runnable
                public final void run() {
                    long now;
                    try {
                        List p0 = (List) value$iv;
                        this.executeAfterMessagesStateChanged(p0);
                        Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now - start$iv) + "ms");
                    } catch (Throwable e) {
                        try {
                            e.printStackTrace();
                            Log.e("DefaultMessageStoreObserver", "post execute from " + sender$iv + " failed " + e.getMessage(), e);
                            long now2 = System.currentTimeMillis();
                            Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now2 - start$iv) + "ms");
                        } finally {
                            now = System.currentTimeMillis();
                            Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now - start$iv) + "ms");
                        }
                    }
                }
            });
        } catch (Throwable e$iv) {
            e$iv.printStackTrace();
            long now$iv = System.currentTimeMillis();
            Log.e(TAG, "execute from " + sender$iv + " failed. took " + (now$iv - start$iv) + "ms", e$iv);
        }
    }

    @Override // com.tm.androidcopysdk.api.IMessageStoreObserver
    public void afterMessageIdsStateChanged(@NotNull List<? extends Id> list) {
        Intrinsics.checkNotNullParameter(list, "ids");
        ensureBackgroundThread();
        if (!this.initialized) {
            return;
        }
        final long start$iv = System.currentTimeMillis();
        final String sender$iv = RuntimeObject.INSTANCE.getCallerClassMethodAndLine(1);
        try {
            final Object value$iv = getApplicationMessageDao().findAll(list);
            Handler handler = this.handler;
            if (handler == null) {
                Intrinsics.throwUninitializedPropertyAccessException("handler");
                handler = null;
            }
            handler.post(new Runnable() { // from class: com.tm.androidcopysdk.device.DefaultMessageStoreObserver$afterMessageIdsStateChanged$$inlined$execute$1
                @Override // java.lang.Runnable
                public final void run() {
                    long now;
                    try {
                        List p0 = (List) value$iv;
                        this.executeAfterMessagesStateChanged(p0);
                        Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now - start$iv) + "ms");
                    } catch (Throwable e) {
                        try {
                            e.printStackTrace();
                            Log.e("DefaultMessageStoreObserver", "post execute from " + sender$iv + " failed " + e.getMessage(), e);
                            long now2 = System.currentTimeMillis();
                            Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now2 - start$iv) + "ms");
                        } finally {
                            now = System.currentTimeMillis();
                            Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now - start$iv) + "ms");
                        }
                    }
                }
            });
        } catch (Throwable e$iv) {
            e$iv.printStackTrace();
            long now$iv = System.currentTimeMillis();
            Log.e(TAG, "execute from " + sender$iv + " failed. took " + (now$iv - start$iv) + "ms", e$iv);
        }
    }

    @Override // com.tm.androidcopysdk.api.IMessageStoreObserver
    public void afterMessagesStateChanged(@NotNull final List<ArchiveMessage> list) {
        Intrinsics.checkNotNullParameter(list, "messages");
        ensureBackgroundThread();
        if (!this.initialized) {
            return;
        }
        final long start$iv = System.currentTimeMillis();
        final String sender$iv = RuntimeObject.INSTANCE.getCallerClassMethodAndLine(1);
        try {
            Handler handler = this.handler;
            if (handler == null) {
                Intrinsics.throwUninitializedPropertyAccessException("handler");
                handler = null;
            }
            handler.post(new Runnable() { // from class: com.tm.androidcopysdk.device.DefaultMessageStoreObserver$afterMessagesStateChanged$$inlined$execute$1
                @Override // java.lang.Runnable
                public final void run() {
                    long now;
                    try {
                        List p0 = (List) list;
                        this.executeAfterMessagesStateChanged(p0);
                        Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now - start$iv) + "ms");
                    } catch (Throwable e) {
                        try {
                            e.printStackTrace();
                            Log.e("DefaultMessageStoreObserver", "post execute from " + sender$iv + " failed " + e.getMessage(), e);
                            long now2 = System.currentTimeMillis();
                            Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now2 - start$iv) + "ms");
                        } finally {
                            now = System.currentTimeMillis();
                            Log.d("DefaultMessageStoreObserver", "post execute from " + sender$iv + " took " + (now - start$iv) + "ms");
                        }
                    }
                }
            });
        } catch (Throwable e$iv) {
            e$iv.printStackTrace();
            long now$iv = System.currentTimeMillis();
            Log.e(TAG, "execute from " + sender$iv + " failed. took " + (now$iv - start$iv) + "ms", e$iv);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void executeAfterMessagesStateChanged(List<ArchiveMessage> list) {
        if (list.isEmpty() || !this.initialized || !this.enabled) {
            return;
        }
        SdkModule<Id> sdkModule = this.module;
        if (sdkModule == null) {
            Intrinsics.throwUninitializedPropertyAccessException("module");
            sdkModule = null;
        }
        boolean appActivated = sdkModule.settings().isAppActivated();
        if (!appActivated) {
            Log.d(TAG, "not registered, ignoring");
            return;
        }
        List<ArchiveMessage> $this$forEach$iv = list;
        for (Object element$iv : $this$forEach$iv) {
            ArchiveMessage m = (ArchiveMessage) element$iv;
            ArchiveMessage existing = getArchiverMessageDao().find(m.getArchiveIdentifier());
            Iterable $this$forEach$iv2 = this.processors;
            for (Object element$iv2 : $this$forEach$iv2) {
                MessageStoreProcessor it = (MessageStoreProcessor) element$iv2;
                it.afterMessageStateChanged(m, existing);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final IArchiveMessageDao<Id> getApplicationMessageDao() {
        SdkModule<Id> sdkModule = this.module;
        if (sdkModule == null) {
            Intrinsics.throwUninitializedPropertyAccessException("module");
            sdkModule = null;
        }
        return sdkModule.getApplicationDatabase().messageDao();
    }

    private final IArchiveMessageDao<ArchiveMessageIdentifier> getArchiverMessageDao() {
        SdkModule<Id> sdkModule = this.module;
        if (sdkModule == null) {
            Intrinsics.throwUninitializedPropertyAccessException("module");
            sdkModule = null;
        }
        return sdkModule.getArchiverDatabase().messageDao();
    }

    private final <T> void execute(final Function1<? super T, Unit> function1, Function0<? extends T> function0) {
        ensureBackgroundThread();
        if (!this.initialized) {
            return;
        }
        final long start = System.currentTimeMillis();
        final String sender = RuntimeObject.INSTANCE.getCallerClassMethodAndLine(1);
        try {
            final Object value = function0.invoke();
            Handler handler = this.handler;
            if (handler == null) {
                Intrinsics.throwUninitializedPropertyAccessException("handler");
                handler = null;
            }
            handler.post(new Runnable() { // from class: com.tm.androidcopysdk.device.DefaultMessageStoreObserver$execute$1
                @Override // java.lang.Runnable
                public final void run() {
                    long now;
                    try {
                        function1.invoke(value);
                        Log.d("DefaultMessageStoreObserver", "post execute from " + sender + " took " + (now - start) + "ms");
                    } catch (Throwable e) {
                        try {
                            e.printStackTrace();
                            Log.e("DefaultMessageStoreObserver", "post execute from " + sender + " failed " + e.getMessage(), e);
                            long now2 = System.currentTimeMillis();
                            Log.d("DefaultMessageStoreObserver", "post execute from " + sender + " took " + (now2 - start) + "ms");
                        } finally {
                            now = System.currentTimeMillis();
                            Log.d("DefaultMessageStoreObserver", "post execute from " + sender + " took " + (now - start) + "ms");
                        }
                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
            long now = System.currentTimeMillis();
            Log.e(TAG, "execute from " + sender + " failed. took " + (now - start) + "ms", e);
        }
    }

    private final void ensureBackgroundThread() {
        Looper looper = Looper.myLooper();
        if (!(looper == null || !Intrinsics.areEqual(looper, Looper.getMainLooper()))) {
            throw new IllegalArgumentException("You must call this method from a background thread".toString());
        }
    }

    /* compiled from: DefaultMessageStoreObserver.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u001a\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u0002H\b0\u0006\"\u0004\b\u0001\u0010\bH\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��R\u0014\u0010\u0005\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u0006X\u0082\u000e¢\u0006\u0002\n��¨\u0006\t"}, d2 = {"Lcom/tm/androidcopysdk/device/DefaultMessageStoreObserver$Companion;", "", "()V", "TAG", "", "instance", "Lcom/tm/androidcopysdk/api/IMessageStoreObserver;", "getInstance", "T", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/device/DefaultMessageStoreObserver$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        @NotNull
        public final <T> IMessageStoreObserver<T> getInstance() {
            IMessageStoreObserver result = DefaultMessageStoreObserver.instance;
            if (result == null) {
                result = new DefaultMessageStoreObserver();
                DefaultMessageStoreObserver.instance = result;
            }
            return result;
        }
    }
}
