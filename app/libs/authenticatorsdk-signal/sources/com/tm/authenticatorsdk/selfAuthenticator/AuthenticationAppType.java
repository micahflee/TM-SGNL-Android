package com.tm.authenticatorsdk.selfAuthenticator;

import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;
/* compiled from: AuthenticationAppType.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n��\n\u0002\u0010\u000e\n��\n\u0002\u0010\b\n\u0002\b\u000b\b\u0086\u0081\u0002\u0018��2\b\u0012\u0004\u0012\u00020��0\u0001B\u001f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0002\u0010\u0007R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n��\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n��\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n��\u001a\u0004\b\f\u0010\tj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000f¨\u0006\u0010"}, d2 = {"Lcom/tm/authenticatorsdk/selfAuthenticator/AuthenticationAppType;", "", "aAppType", "", "aAppServerId", "", "retrieveModePushBitMaskCode", "(Ljava/lang/String;ILjava/lang/String;II)V", "getAAppServerId", "()I", "getAAppType", "()Ljava/lang/String;", "getRetrieveModePushBitMaskCode", "TELEGRAM", "SIGNAL", "NONE", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/AuthenticationAppType.class */
public enum AuthenticationAppType {
    TELEGRAM("TELEGRAM_ARCHIVER_CLIENT", 600921, 2048),
    SIGNAL("SIGNAL_ARCHIVER_CLIENT", 600911, 1024),
    NONE("", 0, 0);
    
    @NotNull
    private final String aAppType;
    private final int aAppServerId;
    private final int retrieveModePushBitMaskCode;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

    @NotNull
    public static EnumEntries<AuthenticationAppType> getEntries() {
        return $ENTRIES;
    }

    AuthenticationAppType(String aAppType, int aAppServerId, int retrieveModePushBitMaskCode) {
        this.aAppType = aAppType;
        this.aAppServerId = aAppServerId;
        this.retrieveModePushBitMaskCode = retrieveModePushBitMaskCode;
    }

    @NotNull
    public final String getAAppType() {
        return this.aAppType;
    }

    public final int getAAppServerId() {
        return this.aAppServerId;
    }

    public final int getRetrieveModePushBitMaskCode() {
        return this.retrieveModePushBitMaskCode;
    }
}
