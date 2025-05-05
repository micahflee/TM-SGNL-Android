package com.tm.androidcopysdk.api;

import android.os.HandlerThread;
import com.tm.androidcopysdk.AndroidCopySDK;
import com.tm.androidcopysdk.DataGrabber;
import com.tm.androidcopysdk.model.ArchiveSettings;
import com.tm.androidcopysdk.model.ClientType;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
/* compiled from: SdkModule.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��D\n\u0002\u0018\u0002\n��\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0016\u0018��*\u0004\b��\u0010\u00012\u00020\u0002BM\b\u0007\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00028��0\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010¢\u0006\u0002\u0010\u0012J\u0006\u0010\u000f\u001a\u00020\u0011R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00028��0\n¢\u0006\b\n��\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n��\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n��\u001a\u0004\b\u0017\u0010\u0018R\u001c\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\u000e\n��\u0012\u0004\b\u0019\u0010\u001a\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n��\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\u001f\u001a\u00020 ¢\u0006\b\n��\u001a\u0004\b!\u0010\"R\u001c\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\u000e\n��\u0012\u0004\b#\u0010\u001a\u001a\u0004\b$\u0010%R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010X\u0082\u0004¢\u0006\u0002\n��¨\u0006&"}, d2 = {"Lcom/tm/androidcopysdk/api/SdkModule;", "Id", "", "sdk", "Lcom/tm/androidcopysdk/AndroidCopySDK;", "dataGrabber", "Lcom/tm/androidcopysdk/DataGrabber;", "clientType", "Lcom/tm/androidcopysdk/model/ClientType;", "applicationDatabase", "Lcom/tm/androidcopysdk/api/IDatabase;", "archiverDatabase", "Lcom/tm/androidcopysdk/api/IArchiveDatabase;", "filer", "Lcom/tm/androidcopysdk/api/IFiler;", "settings", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/tm/androidcopysdk/model/ArchiveSettings;", "(Lcom/tm/androidcopysdk/AndroidCopySDK;Lcom/tm/androidcopysdk/DataGrabber;Lcom/tm/androidcopysdk/model/ClientType;Lcom/tm/androidcopysdk/api/IDatabase;Lcom/tm/androidcopysdk/api/IArchiveDatabase;Lcom/tm/androidcopysdk/api/IFiler;Lkotlinx/coroutines/flow/MutableStateFlow;)V", "getApplicationDatabase", "()Lcom/tm/androidcopysdk/api/IDatabase;", "getArchiverDatabase", "()Lcom/tm/androidcopysdk/api/IArchiveDatabase;", "getClientType", "()Lcom/tm/androidcopysdk/model/ClientType;", "getDataGrabber$annotations", "()V", "getDataGrabber", "()Lcom/tm/androidcopysdk/DataGrabber;", "getFiler", "()Lcom/tm/androidcopysdk/api/IFiler;", "handlerThread", "Landroid/os/HandlerThread;", "getHandlerThread", "()Landroid/os/HandlerThread;", "getSdk$annotations", "getSdk", "()Lcom/tm/androidcopysdk/AndroidCopySDK;", "androidcopysdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/api/SdkModule.class */
public class SdkModule<Id> {
    @NotNull
    private final AndroidCopySDK sdk;
    @NotNull
    private final DataGrabber dataGrabber;
    @NotNull
    private final ClientType clientType;
    @NotNull
    private final IDatabase<Id> applicationDatabase;
    @NotNull
    private final IArchiveDatabase archiverDatabase;
    @NotNull
    private final IFiler filer;
    @NotNull
    private final MutableStateFlow<ArchiveSettings> settings;
    @NotNull
    private final HandlerThread handlerThread;

    @Deprecated(message = "Invert dependency")
    public static /* synthetic */ void getSdk$annotations() {
    }

    @Deprecated(message = "Invert dependency")
    public static /* synthetic */ void getDataGrabber$annotations() {
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    @JvmOverloads
    public SdkModule(@NotNull AndroidCopySDK sdk, @NotNull DataGrabber dataGrabber, @NotNull ClientType clientType, @NotNull IDatabase<Id> iDatabase, @NotNull IArchiveDatabase archiverDatabase, @NotNull IFiler filer) {
        this(sdk, dataGrabber, clientType, iDatabase, archiverDatabase, filer, null, 64, null);
        Intrinsics.checkNotNullParameter(sdk, "sdk");
        Intrinsics.checkNotNullParameter(dataGrabber, "dataGrabber");
        Intrinsics.checkNotNullParameter(clientType, "clientType");
        Intrinsics.checkNotNullParameter(iDatabase, "applicationDatabase");
        Intrinsics.checkNotNullParameter(archiverDatabase, "archiverDatabase");
        Intrinsics.checkNotNullParameter(filer, "filer");
    }

    @JvmOverloads
    public SdkModule(@NotNull AndroidCopySDK sdk, @NotNull DataGrabber dataGrabber, @NotNull ClientType clientType, @NotNull IDatabase<Id> iDatabase, @NotNull IArchiveDatabase archiverDatabase, @NotNull IFiler filer, @NotNull MutableStateFlow<ArchiveSettings> mutableStateFlow) {
        Intrinsics.checkNotNullParameter(sdk, "sdk");
        Intrinsics.checkNotNullParameter(dataGrabber, "dataGrabber");
        Intrinsics.checkNotNullParameter(clientType, "clientType");
        Intrinsics.checkNotNullParameter(iDatabase, "applicationDatabase");
        Intrinsics.checkNotNullParameter(archiverDatabase, "archiverDatabase");
        Intrinsics.checkNotNullParameter(filer, "filer");
        Intrinsics.checkNotNullParameter(mutableStateFlow, "settings");
        this.sdk = sdk;
        this.dataGrabber = dataGrabber;
        this.clientType = clientType;
        this.applicationDatabase = iDatabase;
        this.archiverDatabase = archiverDatabase;
        this.filer = filer;
        this.settings = mutableStateFlow;
        this.handlerThread = new HandlerThread("SdkModule");
        this.handlerThread.start();
    }

    public /* synthetic */ SdkModule(AndroidCopySDK androidCopySDK, DataGrabber dataGrabber, ClientType clientType, IDatabase iDatabase, IArchiveDatabase iArchiveDatabase, IFiler iFiler, MutableStateFlow mutableStateFlow, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(androidCopySDK, dataGrabber, clientType, iDatabase, iArchiveDatabase, iFiler, (i & 64) != 0 ? StateFlowKt.MutableStateFlow(new ArchiveSettings(false, null, false, false, false, false, 63, null)) : mutableStateFlow);
    }

    @NotNull
    public final AndroidCopySDK getSdk() {
        return this.sdk;
    }

    @NotNull
    public final DataGrabber getDataGrabber() {
        return this.dataGrabber;
    }

    @NotNull
    public final ClientType getClientType() {
        return this.clientType;
    }

    @NotNull
    public final IDatabase<Id> getApplicationDatabase() {
        return this.applicationDatabase;
    }

    @NotNull
    public final IArchiveDatabase getArchiverDatabase() {
        return this.archiverDatabase;
    }

    @NotNull
    public final IFiler getFiler() {
        return this.filer;
    }

    @NotNull
    public final HandlerThread getHandlerThread() {
        return this.handlerThread;
    }

    @NotNull
    public final ArchiveSettings settings() {
        return (ArchiveSettings) this.settings.getValue();
    }
}
