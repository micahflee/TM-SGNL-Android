package com.tm.authenticatorsdk.selfAuthenticator;

import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
/* compiled from: AuthenticatorConstants.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\f\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0003\u0018�� \u00032\u00020\u0001:\u0001\u0003B\u0005¢\u0006\u0002\u0010\u0002¨\u0006\u0004"}, d2 = {"Lcom/tm/authenticatorsdk/selfAuthenticator/AuthenticatorConstants;", "", "()V", "Companion", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/AuthenticatorConstants.class */
public final class AuthenticatorConstants {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private static final String encodeKey = "QgudCol8Ays5mFLukXPpAj3GuDQda6Yhi9yFcNCNgIjMzvpXxIILpzYd8wNhE6vCQ4camThaASzowjsYU3dPrL7eA63GJ3C1piN2";
    @NotNull
    private static final Pair<String, String> charlieURL = new Pair<>("https://rest.telemessage.com", "https://api-gateway-charlie.kapi.telemessage.com");
    @NotNull
    private static final Pair<String, String> charlieQaURL = new Pair<>("https://qa.telemessage.com", "https://api-gateway-qacharlie.devops.telemessage.co.il");
    @NotNull
    private static final Pair<String, String> IntegrationURL = new Pair<>("https://integration.telemessage.co.il", "https://api-gateway-integration.devops.telemessage.co.il");
    @NotNull
    private static final String productionUrlMdm = "https://secure.telemessage.com";
    @NotNull
    private static final String integrationUrlMdm = "https://integration.telemessage.co.il";
    @NotNull
    private static Pair<String, String> BASE_URL = charlieURL;
    @NotNull
    private static String BASE_URL_MDM = productionUrlMdm;
    @NotNull
    private static final String ENSURE_IP_RESUTL_STATUS = "resultDescription";

    /* compiled from: AuthenticatorConstants.kt */
    @Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\u0018\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0018\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R&\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004X\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n��\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u0014\u0010\u000f\u001a\u00020\u0005X\u0086D¢\u0006\b\n��\u001a\u0004\b\u0010\u0010\fR\u001d\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\b\n��\u001a\u0004\b\u0012\u0010\u0007R\u001d\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\b\n��\u001a\u0004\b\u0014\u0010\u0007R\u001d\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\b\n��\u001a\u0004\b\u0016\u0010\u0007R\u0014\u0010\u0017\u001a\u00020\u0005X\u0086D¢\u0006\b\n��\u001a\u0004\b\u0018\u0010\fR\u0014\u0010\u0019\u001a\u00020\u0005X\u0086D¢\u0006\b\n��\u001a\u0004\b\u001a\u0010\fR\u0014\u0010\u001b\u001a\u00020\u0005X\u0086D¢\u0006\b\n��\u001a\u0004\b\u001c\u0010\f¨\u0006\u001d"}, d2 = {"Lcom/tm/authenticatorsdk/selfAuthenticator/AuthenticatorConstants$Companion;", "", "()V", "BASE_URL", "Lkotlin/Pair;", "", "getBASE_URL", "()Lkotlin/Pair;", "setBASE_URL", "(Lkotlin/Pair;)V", "BASE_URL_MDM", "getBASE_URL_MDM", "()Ljava/lang/String;", "setBASE_URL_MDM", "(Ljava/lang/String;)V", "ENSURE_IP_RESUTL_STATUS", "getENSURE_IP_RESUTL_STATUS", "IntegrationURL", "getIntegrationURL", "charlieQaURL", "getCharlieQaURL", "charlieURL", "getCharlieURL", "encodeKey", "getEncodeKey", "integrationUrlMdm", "getIntegrationUrlMdm", "productionUrlMdm", "getProductionUrlMdm", "authenticatorsdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/AuthenticatorConstants$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        @NotNull
        public final String getEncodeKey() {
            return AuthenticatorConstants.encodeKey;
        }

        @NotNull
        public final Pair<String, String> getCharlieURL() {
            return AuthenticatorConstants.charlieURL;
        }

        @NotNull
        public final Pair<String, String> getCharlieQaURL() {
            return AuthenticatorConstants.charlieQaURL;
        }

        @NotNull
        public final Pair<String, String> getIntegrationURL() {
            return AuthenticatorConstants.IntegrationURL;
        }

        @NotNull
        public final String getProductionUrlMdm() {
            return AuthenticatorConstants.productionUrlMdm;
        }

        @NotNull
        public final String getIntegrationUrlMdm() {
            return AuthenticatorConstants.integrationUrlMdm;
        }

        @NotNull
        public final Pair<String, String> getBASE_URL() {
            return AuthenticatorConstants.BASE_URL;
        }

        public final void setBASE_URL(@NotNull Pair<String, String> pair) {
            Intrinsics.checkNotNullParameter(pair, "<set-?>");
            AuthenticatorConstants.BASE_URL = pair;
        }

        @NotNull
        public final String getBASE_URL_MDM() {
            return AuthenticatorConstants.BASE_URL_MDM;
        }

        public final void setBASE_URL_MDM(@NotNull String str) {
            Intrinsics.checkNotNullParameter(str, "<set-?>");
            AuthenticatorConstants.BASE_URL_MDM = str;
        }

        @NotNull
        public final String getENSURE_IP_RESUTL_STATUS() {
            return AuthenticatorConstants.ENSURE_IP_RESUTL_STATUS;
        }
    }
}
