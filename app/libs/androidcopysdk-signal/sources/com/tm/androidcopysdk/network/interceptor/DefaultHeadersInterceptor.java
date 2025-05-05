package com.tm.androidcopysdk.network.interceptor;

import android.content.Context;
import com.tm.IHeaderInterceptor;
import com.tm.androidcopysdk.HeaderObj;
import com.tm.androidcopysdk.network.CallLogMessageRecorder;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.FlavorSettings;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: DefaultHeadersInterceptor.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000b\n\u0002\b\u0010\b\u0016\u0018�� 02\u00020\u0001:\u00010B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u001a\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\b2\u0006\u0010\u001a\u001a\u00020\u001bH\u0014J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0016J\u0012\u0010 \u001a\u00020!2\b\u0010\u0019\u001a\u0004\u0018\u00010\bH\u0002J\u0012\u0010\"\u001a\u00020!2\b\u0010\u0019\u001a\u0004\u0018\u00010\bH\u0002J\u0012\u0010#\u001a\u00020!2\b\u0010\u0019\u001a\u0004\u0018\u00010\bH\u0002J\u0012\u0010$\u001a\u00020!2\b\u0010\u0019\u001a\u0004\u0018\u00010\bH\u0004J\u001c\u0010%\u001a\u00020\u00182\b\u0010\u0016\u001a\u0004\u0018\u00010\b2\b\u0010\u000e\u001a\u0004\u0018\u00010\bH\u0016J\u0012\u0010&\u001a\u00020\u00182\b\u0010'\u001a\u0004\u0018\u00010\bH\u0016J\u0012\u0010(\u001a\u00020\u00182\b\u0010\r\u001a\u0004\u0018\u00010\bH\u0016J\u0012\u0010)\u001a\u00020\u00182\b\u0010*\u001a\u0004\u0018\u00010\bH\u0016J\u0012\u0010+\u001a\u00020\u00182\b\u0010,\u001a\u0004\u0018\u00010\bH\u0016J\u0012\u0010-\u001a\u00020\u00182\b\u0010.\u001a\u0004\u0018\u00010\bH\u0016J\u0012\u0010/\u001a\u00020!2\b\u0010\u0019\u001a\u0004\u0018\u00010\bH\u0002R\u0016\u0010\u0002\u001a\u00020\u00038\u0002X\u0083\u0004¢\u0006\b\n��\u0012\u0004\b\u0005\u0010\u0006R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0084\u000e¢\u0006\u000e\n��\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u0010\u0010\r\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n��R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n��R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n��R\u001c\u0010\u0010\u001a\u0004\u0018\u00010\bX\u0084\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0011\u0010\n\"\u0004\b\u0012\u0010\fR\u001c\u0010\u0013\u001a\u0004\u0018\u00010\bX\u0084\u000e¢\u0006\u000e\n��\u001a\u0004\b\u0014\u0010\n\"\u0004\b\u0015\u0010\fR\u0010\u0010\u0016\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n��¨\u00061"}, d2 = {"Lcom/tm/androidcopysdk/network/interceptor/DefaultHeadersInterceptor;", "Lcom/tm/IHeaderInterceptor;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "getContext$annotations", "()V", "dateHeader", "", "getDateHeader", "()Ljava/lang/String;", "setDateHeader", "(Ljava/lang/String;)V", CallLogMessageRecorder.messageId_key, "password", "signatureHeader", "tokenHeader", "getTokenHeader", "setTokenHeader", "uidHeader", "getUidHeader", "setUidHeader", "userName", "addHeaderSignature", "", "url", "requestBuilder", "Lokhttp3/Request$Builder;", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "isArchiveRequest", "", "isJson", "isSecuredRequest", "isSignatureRequiredRequest", "setAuthentication", "setDate", "date", "setMessageId", "setSignature", "signature", "setToken", "token", "setUid", "uid", "shouldAddSecurityHeader", "Companion", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nDefaultHeadersInterceptor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DefaultHeadersInterceptor.kt\ncom/tm/androidcopysdk/network/interceptor/DefaultHeadersInterceptor\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,148:1\n1855#2,2:149\n*S KotlinDebug\n*F\n+ 1 DefaultHeadersInterceptor.kt\ncom/tm/androidcopysdk/network/interceptor/DefaultHeadersInterceptor\n*L\n70#1:149,2\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/interceptor/DefaultHeadersInterceptor.class */
public class DefaultHeadersInterceptor implements IHeaderInterceptor {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final Context context;
    @Nullable
    private String userName;
    @Nullable
    private String password;
    @Nullable
    private String messageId;
    @Nullable
    private String signatureHeader;
    @Nullable
    private String dateHeader;
    @Nullable
    private String uidHeader;
    @Nullable
    private String tokenHeader;
    @NotNull
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    @NotNull
    public static final String HEADER_AUTHORIZATION = "Authorization";
    @NotNull
    public static final String HEADER_REQUEST_SIGNATURE = "request-signature";
    @NotNull
    public static final String HEADER_PRODUCT_ID = "Product-Id";
    @NotNull
    public static final String HEADER_REQUEST_DATE = "Request-Date";
    @NotNull
    public static final String HEADER_UID = "Uid";
    @NotNull
    public static final String HEADER_TOKEN = "Token";

    @Deprecated(message = "")
    private static /* synthetic */ void getContext$annotations() {
    }

    public DefaultHeadersInterceptor(@NotNull Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public final String getDateHeader() {
        return this.dateHeader;
    }

    protected final void setDateHeader(@Nullable String str) {
        this.dateHeader = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public final String getUidHeader() {
        return this.uidHeader;
    }

    protected final void setUidHeader(@Nullable String str) {
        this.uidHeader = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Nullable
    public final String getTokenHeader() {
        return this.tokenHeader;
    }

    protected final void setTokenHeader(@Nullable String str) {
        this.tokenHeader = str;
    }

    public void setAuthentication(@Nullable String userName, @Nullable String password) {
        this.userName = userName;
        this.password = password;
    }

    public void setMessageId(@Nullable String messageId) {
        this.messageId = messageId;
    }

    public void setSignature(@Nullable String signature) {
        this.signatureHeader = signature;
    }

    public void setDate(@Nullable String date) {
        this.dateHeader = date;
    }

    public void setUid(@Nullable String uid) {
        this.uidHeader = uid;
    }

    public void setToken(@Nullable String token) {
        this.tokenHeader = token;
    }

    @NotNull
    public Response intercept(@NotNull Interceptor.Chain chain) {
        Intrinsics.checkNotNullParameter(chain, "chain");
        String userName = this.userName;
        String password = this.password;
        Request request = chain.request();
        String url = request.url().url().toString();
        Intrinsics.checkNotNullExpressionValue(url, "toString(...)");
        Request.Builder requestBuilder = request.newBuilder();
        if (isJson(url)) {
            requestBuilder.addHeader(HEADER_CONTENT_TYPE, "application/json");
        }
        if (shouldAddSecurityHeader(url)) {
            String str = userName;
            if (!(str == null || str.length() == 0)) {
                String str2 = password;
                if (!(str2 == null || str2.length() == 0)) {
                    requestBuilder.addHeader(HEADER_AUTHORIZATION, Credentials.basic(userName, password));
                }
            }
        }
        if (isSignatureRequiredRequest(url)) {
            Intrinsics.checkNotNull(requestBuilder);
            addHeaderSignature(url, requestBuilder);
        }
        String messageId = this.messageId;
        if (isArchiveRequest(url) && messageId != null) {
            Iterable headers = FlavorSettings.getInstance().getHeaders(this.context, messageId);
            Intrinsics.checkNotNullExpressionValue(headers, "getHeaders(...)");
            Iterable $this$forEach$iv = headers;
            for (Object element$iv : $this$forEach$iv) {
                HeaderObj it = (HeaderObj) element$iv;
                requestBuilder.addHeader(it.getKey(), it.getValue());
            }
        }
        Response proceed = chain.proceed(requestBuilder.build());
        Intrinsics.checkNotNullExpressionValue(proceed, "proceed(...)");
        return proceed;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void addHeaderSignature(@Nullable String url, @NotNull Request.Builder requestBuilder) {
        Intrinsics.checkNotNullParameter(requestBuilder, "requestBuilder");
        String signature = this.signatureHeader;
        if (signature == null) {
            return;
        }
        requestBuilder.addHeader(HEADER_REQUEST_SIGNATURE, signature);
    }

    private final boolean isJson(String url) {
        String str = url;
        if (str == null || str.length() == 0) {
            return false;
        }
        if (isArchiveRequest(url)) {
            return true;
        }
        String postSettingsUrl = FlavorSettings.getInstance().getPostSettingsUrl();
        Intrinsics.checkNotNullExpressionValue(postSettingsUrl, "getPostSettingsUrl(...)");
        if (!StringsKt.contains$default(url, postSettingsUrl, false, 2, (Object) null) && !StringsKt.endsWith$default(url, "keepAlive", false, 2, (Object) null) && !StringsKt.endsWith$default(url, "keepAliveV3", false, 2, (Object) null) && !StringsKt.endsWith$default(url, "getAppVersion", false, 2, (Object) null) && StringsKt.endsWith$default(url, "setAppVersion", false, 2, (Object) null)) {
            return true;
        }
        return true;
    }

    private final boolean shouldAddSecurityHeader(String url) {
        if (isArchiveRequest(url)) {
            return FlavorSettings.getInstance().isAppServerNeedAuthorization();
        }
        return isSecuredRequest(url);
    }

    private final boolean isSecuredRequest(String url) {
        String str = url;
        if ((str == null || str.length() == 0) || StringsKt.contains$default(url, "log_post", false, 2, (Object) null) || StringsKt.endsWith$default(url, "wp-json/jwt-auth/v1/token", false, 2, (Object) null) || StringsKt.endsWith$default(url, "signUpUser", false, 2, (Object) null) || StringsKt.endsWith$default(url, "retrieveOneTimePINExt", false, 2, (Object) null) || StringsKt.endsWith$default(url, "retrieveCredentials", false, 2, (Object) null) || StringsKt.endsWith$default(url, "signUpUserWithManager", false, 2, (Object) null)) {
            return false;
        }
        return true;
    }

    protected final boolean isSignatureRequiredRequest(@Nullable String url) {
        String str = url;
        if (str == null || str.length() == 0) {
            return false;
        }
        if (StringsKt.endsWith$default(url, "ensureIPDeviceSigned", false, 2, (Object) null) || StringsKt.endsWith$default(url, "retrieveOneTimePINExt", false, 2, (Object) null) || StringsKt.endsWith$default(url, "retrieveCredentials", false, 2, (Object) null)) {
            return true;
        }
        return false;
    }

    private final boolean isArchiveRequest(String url) {
        String str = url;
        if (str == null || str.length() == 0) {
            return false;
        }
        if (StringsKt.endsWith(url, "telemessageIncomingMessage", true) || StringsKt.endsWith(url, "telemessageIncomingMessageV2", true)) {
            return true;
        }
        return false;
    }

    /* compiled from: DefaultHeadersInterceptor.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0014\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\b\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\t\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��R\u000e\u0010\n\u001a\u00020\u0004X\u0086T¢\u0006\u0002\n��¨\u0006\u000b"}, d2 = {"Lcom/tm/androidcopysdk/network/interceptor/DefaultHeadersInterceptor$Companion;", "", "()V", "HEADER_AUTHORIZATION", "", "HEADER_CONTENT_TYPE", "HEADER_PRODUCT_ID", "HEADER_REQUEST_DATE", "HEADER_REQUEST_SIGNATURE", "HEADER_TOKEN", "HEADER_UID", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/interceptor/DefaultHeadersInterceptor$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }
    }
}
