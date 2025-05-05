package com.tm.androidcopysdk.network.interceptor;

import com.tm.ILoggingInterceptor;
import com.tm.androidcopysdk.DefinitionsSDKKt;
import com.tm.androidcopysdk.database.DBHeadersTable;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.logger.Log;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
/* compiled from: LoggingInterceptor.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n��\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\f\u0018�� %2\u00020\u0001:\u0001%B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u0010\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\b\u0010\u0011\u001a\u00020\u0012H\u0016J\b\u0010\u0013\u001a\u00020\u0012H\u0016J\u0018\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0015\u001a\u00020\u0004H\u0002J\"\u0010\u0014\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0016\u001a\u00020\u000e2\b\b\u0002\u0010\u0015\u001a\u00020\u0004H\u0002J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\u0010\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u0010\u0010\u001c\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u0010\u0010\u001d\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u0010\u0010\u001e\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u0010\u0010\u001f\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u0010\u0010 \u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u0010\u0010!\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u0010\u0010\"\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u0010\u0010#\u001a\u00020\u00122\u0006\u0010\u000f\u001a\u00020\u000eH\u0002J\u0010\u0010$\u001a\u00020��2\b\u0010\u0007\u001a\u0004\u0018\u00010\u0006R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n��R\u001e\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0006@BX\u0086\u000e¢\u0006\b\n��\u001a\u0004\b\b\u0010\t¨\u0006&"}, d2 = {"Lcom/tm/androidcopysdk/network/interceptor/LoggingInterceptor;", "Lcom/tm/ILoggingInterceptor;", "()V", "isEnabled", "", "<set-?>", "Lcom/tm/androidcopysdk/network/interceptor/NetworkLogLevel;", "level", "getLevel", "()Lcom/tm/androidcopysdk/network/interceptor/NetworkLogLevel;", "bodyEncoded", DBHeadersTable.HeadersEntry.TABLE_NAME, "Lokhttp3/Headers;", "cleanupBody", "", "message", "cleanupHeader", "disable", "", "enable", "hideJsonValue", "revealLeadingAndTrailingLetter", "name", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "log", "logRequestBody", "logRequestEnd", "logRequestHeader", "logRequestStart", "logResponseBody", "logResponseEnd", "logResponseHeader", "logResponseStart", "setLevel", "Companion", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nLoggingInterceptor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LoggingInterceptor.kt\ncom/tm/androidcopysdk/network/interceptor/LoggingInterceptor\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,270:1\n1864#2,3:271\n*S KotlinDebug\n*F\n+ 1 LoggingInterceptor.kt\ncom/tm/androidcopysdk/network/interceptor/LoggingInterceptor\n*L\n107#1:271,3\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/interceptor/LoggingInterceptor.class */
public final class LoggingInterceptor implements ILoggingInterceptor {
    @NotNull
    private volatile NetworkLogLevel level = NetworkLogLevel.NONE;
    private volatile boolean isEnabled;
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private static final String[] HEADER_CONSTRAINTS = {"Authorization:"};
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @NotNull
    public final NetworkLogLevel getLevel() {
        return this.level;
    }

    public void enable() {
        this.isEnabled = true;
    }

    public void disable() {
        this.isEnabled = false;
    }

    @NotNull
    public final LoggingInterceptor setLevel(@Nullable NetworkLogLevel level) {
        if (level == null) {
            throw new NullPointerException("level == null. Use NetworkLogLevel.NONE instead.");
        }
        this.level = level;
        return this;
    }

    private final void logRequestStart(String message) {
        log(message);
    }

    private final void logRequestHeader(String message) {
        log(cleanupHeader(message));
    }

    private final void logRequestBody(String message) {
        log(cleanupBody(message));
    }

    private final void logRequestEnd(String message) {
        log(message);
    }

    private final void logResponseStart(String message) {
        log(message);
    }

    private final void logResponseHeader(String message) {
        log(cleanupHeader(message));
    }

    private final void logResponseBody(String message) {
        log(cleanupBody(message));
    }

    private final void logResponseEnd(String message) {
        log(message);
    }

    private final void log(String message) {
        if (!this.isEnabled) {
            return;
        }
        Log.d("LoggingInterceptor", message);
    }

    private final String cleanupHeader(String message) {
        String result = message;
        if (StringsKt.contains$default(result, "Authorization:", false, 2, (Object) null)) {
            StringBuilder sb = new StringBuilder();
            String substring = result.substring(0, StringsKt.indexOf$default(result, "Authorization:", 0, false, 6, (Object) null) + "Authorization:".length());
            Intrinsics.checkNotNullExpressionValue(substring, "substring(...)");
            result = sb.append(substring).append(" ****").toString();
        }
        return result;
    }

    private final String cleanupBody(String message) {
        String result = message;
        if (result.length() > 1000) {
            String substring = message.substring(0, DefinitionsSDKKt.ID_BASE);
            Intrinsics.checkNotNullExpressionValue(substring, "substring(...)");
            result = substring;
        }
        int startIndex = StringsKt.indexOf$default(result, "resultCode", 0, false, 6, (Object) null);
        int endIndex = StringsKt.indexOf$default(result, "resultDescription", 0, false, 6, (Object) null) + 6;
        if (startIndex != -1 && endIndex != -1) {
            String substring2 = result.substring(startIndex, endIndex + "resultDescription".length());
            Intrinsics.checkNotNullExpressionValue(substring2, "substring(...)");
            result = substring2;
        }
        return hideJsonValue$default(this, hideJsonValue$default(this, hideJsonValue(result, "username\":\"", true), "password\":\"", false, 4, null), "textContent\":\"", false, 4, null);
    }

    static /* synthetic */ String hideJsonValue$default(LoggingInterceptor loggingInterceptor, String str, String str2, boolean z, int i, Object obj) {
        if ((i & 4) != 0) {
            z = false;
        }
        return loggingInterceptor.hideJsonValue(str, str2, z);
    }

    private final String hideJsonValue(String message, String name, boolean revealLeadingAndTrailingLetter) {
        String str;
        String str2 = "";
        Iterable $this$forEachIndexed$iv = StringsKt.split$default(message, new String[]{name}, false, 0, 6, (Object) null);
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            int index = index$iv;
            index$iv++;
            if (index < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            String s = (String) item$iv;
            StringBuilder append = new StringBuilder().append(str2);
            if (index == 0) {
                str = s;
            } else {
                str = name + hideJsonValue(s, revealLeadingAndTrailingLetter);
            }
            str2 = append.append(str).toString();
        }
        return str2;
    }

    private final String hideJsonValue(String message, boolean revealLeadingAndTrailingLetter) {
        String str;
        int index = StringsKt.indexOf$default(message, "\"", 0, false, 6, (Object) null);
        if (index <= 1) {
            return message;
        }
        String userName = message.substring(0, index);
        Intrinsics.checkNotNullExpressionValue(userName, "substring(...)");
        if (userName.length() == 0) {
            return message;
        }
        if (revealLeadingAndTrailingLetter) {
            StringBuilder sb = new StringBuilder();
            Object firstOrNull = StringsKt.firstOrNull(userName);
            if (firstOrNull == null) {
                firstOrNull = "";
            }
            StringBuilder append = sb.append(firstOrNull).append("****");
            Object lastOrNull = StringsKt.lastOrNull(userName);
            if (lastOrNull == null) {
                lastOrNull = "";
            }
            str = append.append(lastOrNull).toString();
        } else {
            str = "****";
        }
        String mask = str;
        StringBuilder append2 = new StringBuilder().append(mask);
        String substring = message.substring(index);
        Intrinsics.checkNotNullExpressionValue(substring, "substring(...)");
        return append2.append(substring).toString();
    }

    @NotNull
    public Response intercept(@NotNull Interceptor.Chain chain) throws IOException {
        Intrinsics.checkNotNullParameter(chain, "chain");
        if (!this.isEnabled) {
            Response proceed = chain.proceed(chain.request());
            Intrinsics.checkNotNullExpressionValue(proceed, "proceed(...)");
            return proceed;
        }
        NetworkLogLevel level = this.level;
        Request request = chain.request();
        if (level == NetworkLogLevel.NONE) {
            Response proceed2 = chain.proceed(request);
            Intrinsics.checkNotNullExpressionValue(proceed2, "proceed(...)");
            return proceed2;
        }
        boolean logBody = level == NetworkLogLevel.BODY;
        boolean logHeaders = logBody || level == NetworkLogLevel.HEADERS;
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (!logHeaders && hasRequestBody) {
            StringBuilder append = new StringBuilder().append(requestStartMessage).append(" (");
            Intrinsics.checkNotNull(requestBody);
            requestStartMessage = append.append(requestBody.contentLength()).append("-byte body)").toString();
        }
        logRequestStart(requestStartMessage);
        if (logHeaders) {
            if (hasRequestBody) {
                Intrinsics.checkNotNull(requestBody);
                if (requestBody.contentType() != null) {
                    logRequestHeader("Content-Type: " + requestBody.contentType());
                }
                if (requestBody.contentLength() != -1) {
                    logRequestHeader("Content-Length: " + requestBody.contentLength());
                }
            }
            Headers headers = request.headers();
            int count = headers.size();
            for (int i = 0; i < count; i++) {
                String name = headers.name(i);
                if (!StringsKt.equals(DefaultHeadersInterceptor.HEADER_CONTENT_TYPE, name, true) && !StringsKt.equals("Content-Length", name, true)) {
                    logRequestHeader(name + ": " + headers.value(i));
                }
            }
            if (!logBody || !hasRequestBody) {
                logRequestEnd("--> END " + request.method());
            } else {
                Intrinsics.checkNotNull(headers);
                if (bodyEncoded(headers)) {
                    logRequestEnd("--> END " + request.method() + " (encoded body omitted)");
                } else {
                    Buffer buffer = new Buffer();
                    Intrinsics.checkNotNull(requestBody);
                    requestBody.writeTo((BufferedSink) buffer);
                    Charset charset = UTF8;
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }
                    logRequestBody("");
                    if (Companion.isPlaintext(buffer)) {
                        String readString = buffer.readString(charset);
                        Intrinsics.checkNotNullExpressionValue(readString, "readString(...)");
                        logRequestBody(readString);
                        logRequestEnd("--> END " + request.method() + " (" + requestBody.contentLength() + "-byte body)");
                    } else {
                        logRequestEnd("--> END " + request.method() + " (binary " + requestBody.contentLength() + "-byte body omitted)");
                    }
                }
            }
        }
        long startNs = System.nanoTime();
        try {
            Response response = chain.proceed(request);
            Intrinsics.checkNotNullExpressionValue(response, "proceed(...)");
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            ResponseBody responseBody = response.body();
            Intrinsics.checkNotNull(responseBody);
            long contentLength = responseBody.contentLength();
            String bodySize = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
            logResponseStart("<-- " + response.code() + ' ' + response.message() + ' ' + response.request().url() + " (" + tookMs + "ms" + (!logHeaders ? ", " + bodySize + " body" : "") + ')');
            if (logHeaders) {
                Headers headers2 = response.headers();
                int count2 = headers2.size();
                for (int i2 = 0; i2 < count2; i2++) {
                    logResponseHeader(headers2.name(i2) + ": " + headers2.value(i2));
                }
                if (!logBody || !HttpHeaders.hasBody(response)) {
                    logResponseEnd("<-- END HTTP");
                } else {
                    Intrinsics.checkNotNull(headers2);
                    if (bodyEncoded(headers2)) {
                        logResponseEnd("<-- END HTTP (encoded body omitted)");
                    } else {
                        BufferedSource source = responseBody.source();
                        source.request(Long.MAX_VALUE);
                        Buffer buffer2 = source.buffer();
                        Charset charset2 = UTF8;
                        MediaType contentType2 = responseBody.contentType();
                        if (contentType2 != null) {
                            charset2 = contentType2.charset(UTF8);
                        }
                        Companion companion = Companion;
                        Intrinsics.checkNotNull(buffer2);
                        if (!companion.isPlaintext(buffer2)) {
                            logResponseBody("");
                            logRequestEnd("<-- END HTTP (binary " + buffer2.size() + "-byte body omitted)");
                            return response;
                        }
                        if (contentLength != 0) {
                            logResponseBody("");
                            String readString2 = buffer2.clone().readString(charset2);
                            Intrinsics.checkNotNullExpressionValue(readString2, "readString(...)");
                            logResponseBody(readString2);
                        }
                        logRequestEnd("<-- END HTTP (" + buffer2.size() + "-byte body)");
                    }
                }
            }
            return response;
        } catch (Exception e) {
            logResponseEnd("<-- HTTP FAILED: " + e);
            throw e;
        }
    }

    private final boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return (contentEncoding == null || StringsKt.equals(contentEncoding, "identity", true)) ? false : true;
    }

    /* compiled from: LoggingInterceptor.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��,\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n��\n\u0002\u0018\u0002\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rR\u0016\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0006R\u0016\u0010\u0007\u001a\n \t*\u0004\u0018\u00010\b0\bX\u0082\u0004¢\u0006\u0002\n��¨\u0006\u000e"}, d2 = {"Lcom/tm/androidcopysdk/network/interceptor/LoggingInterceptor$Companion;", "", "()V", "HEADER_CONSTRAINTS", "", "", "[Ljava/lang/String;", "UTF8", "Ljava/nio/charset/Charset;", "kotlin.jvm.PlatformType", "isPlaintext", "", "buffer", "Lokio/Buffer;", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/interceptor/LoggingInterceptor$Companion.class */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        public final boolean isPlaintext(@NotNull Buffer buffer) {
            Intrinsics.checkNotNullParameter(buffer, "buffer");
            try {
                Buffer prefix = new Buffer();
                long byteCount = buffer.size() < 64 ? buffer.size() : 64L;
                buffer.copyTo(prefix, 0L, byteCount);
                for (int i = 0; i < 16; i++) {
                    if (!prefix.exhausted()) {
                        int codePoint = prefix.readUtf8CodePoint();
                        if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                            return false;
                        }
                    } else {
                        return true;
                    }
                }
                return true;
            } catch (EOFException e) {
                return false;
            }
        }
    }
}
