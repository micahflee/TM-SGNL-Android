package com.tm.androidcopysdk.network.interceptor;

import android.content.Context;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: SocgenHeadersInterceptor.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n��\n\u0002\u0010\u000e\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0016\u0018�� \r2\u00020\u0001:\u0001\rB\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u001a\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\fH\u0014R\u0016\u0010\u0002\u001a\u00020\u00038\u0002X\u0083\u0004¢\u0006\b\n��\u0012\u0004\b\u0005\u0010\u0006¨\u0006\u000e"}, d2 = {"Lcom/tm/androidcopysdk/network/interceptor/SocgenHeadersInterceptor;", "Lcom/tm/androidcopysdk/network/interceptor/DefaultHeadersInterceptor;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "getContext$annotations", "()V", "addHeaderSignature", "", "url", "", "requestBuilder", "Lokhttp3/Request$Builder;", "Companion", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nSocgenHeadersInterceptor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SocgenHeadersInterceptor.kt\ncom/tm/androidcopysdk/network/interceptor/SocgenHeadersInterceptor\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,20:1\n1#2:21\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/interceptor/SocgenHeadersInterceptor.class */
public class SocgenHeadersInterceptor extends DefaultHeadersInterceptor {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final Context context;
    @NotNull
    private static final String PRODUCT_ID = "16";

    @Deprecated(message = "")
    private static /* synthetic */ void getContext$annotations() {
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public SocgenHeadersInterceptor(@NotNull Context context) {
        super(context);
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.tm.androidcopysdk.network.interceptor.DefaultHeadersInterceptor
    public void addHeaderSignature(@Nullable String url, @NotNull Request.Builder requestBuilder) {
        Intrinsics.checkNotNullParameter(requestBuilder, "requestBuilder");
        super.addHeaderSignature(url, requestBuilder);
        requestBuilder.addHeader(DefaultHeadersInterceptor.HEADER_PRODUCT_ID, PRODUCT_ID);
        String it = getDateHeader();
        if (it != null) {
            requestBuilder.addHeader(DefaultHeadersInterceptor.HEADER_REQUEST_DATE, it);
        }
        String it2 = getUidHeader();
        if (it2 != null) {
            requestBuilder.addHeader(DefaultHeadersInterceptor.HEADER_UID, it2);
        }
        String it3 = getTokenHeader();
        if (it3 != null) {
            requestBuilder.addHeader(DefaultHeadersInterceptor.HEADER_TOKEN, it3);
        }
    }

    /* compiled from: SocgenHeadersInterceptor.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��\u0012\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u000e\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n��¨\u0006\u0005"}, d2 = {"Lcom/tm/androidcopysdk/network/interceptor/SocgenHeadersInterceptor$Companion;", "", "()V", "PRODUCT_ID", "", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/interceptor/SocgenHeadersInterceptor$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }
    }
}
