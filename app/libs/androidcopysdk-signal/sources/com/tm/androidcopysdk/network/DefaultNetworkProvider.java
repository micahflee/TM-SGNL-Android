package com.tm.androidcopysdk.network;

import android.content.Context;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.tm.IHeaderInterceptor;
import com.tm.ILoggingInterceptor;
import com.tm.INetworkProvider;
import com.tm.IService;
import com.tm.androidcopysdk.network.interceptor.DefaultHeadersInterceptor;
import com.tm.androidcopysdk.network.interceptor.LoggingInterceptor;
import com.tm.androidcopysdk.network.interceptor.NetworkLogLevel;
import com.tm.androidcopysdk.recorder.MyAudioFormat;
import com.tm.androidcopysdk.utils.FlavorSettings;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin._Assertions;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
/* compiled from: DefaultNetworkProvider.kt */
@Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��R\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0010\t\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018�� \u001b2\u00020\u0001:\u0001\u001bB\u0019\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\b\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0002\u001a\u00020\u0003H\u0002J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J-\u0010\u0015\u001a\u0002H\u0016\"\b\b��\u0010\u0016*\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u00142\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u0002H\u00160\u0019H\u0016¢\u0006\u0002\u0010\u001aR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n��R\u000e\u0010\t\u001a\u00020\nX\u0082D¢\u0006\u0002\n��¨\u0006\u001c"}, d2 = {"Lcom/tm/androidcopysdk/network/DefaultNetworkProvider;", "Lcom/tm/INetworkProvider;", "context", "Landroid/content/Context;", "headersInterceptor", "Lcom/tm/IHeaderInterceptor;", "(Landroid/content/Context;Lcom/tm/IHeaderInterceptor;)V", "logger", "Lcom/tm/androidcopysdk/network/interceptor/LoggingInterceptor;", "timeoutSeconds", "", "createGson", "Lcom/google/gson/Gson;", "createHttpClient", "Lokhttp3/OkHttpClient;", "loggerInterceptor", "Lcom/tm/ILoggingInterceptor;", "retrofit", "Lretrofit2/Retrofit;", "baseUrl", "", "service", "T", "Lcom/tm/IService;", "type", "Ljava/lang/Class;", "(Ljava/lang/String;Ljava/lang/Class;)Lcom/tm/IService;", "Companion", "androidcopysdk_signalRelease"})
@SourceDebugExtension({"SMAP\nDefaultNetworkProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DefaultNetworkProvider.kt\ncom/tm/androidcopysdk/network/DefaultNetworkProvider\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 MapsJVM.kt\nkotlin/collections/MapsKt__MapsJVMKt\n*L\n1#1,110:1\n1#2:111\n1#2:114\n72#3,2:112\n*S KotlinDebug\n*F\n+ 1 DefaultNetworkProvider.kt\ncom/tm/androidcopysdk/network/DefaultNetworkProvider\n*L\n36#1:114\n36#1:112,2\n*E\n"})
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/DefaultNetworkProvider.class */
public final class DefaultNetworkProvider implements INetworkProvider {
    @NotNull
    private final Context context;
    @NotNull
    private final IHeaderInterceptor headersInterceptor;
    private final long timeoutSeconds;
    @NotNull
    private final LoggingInterceptor logger;
    @Nullable
    private static INetworkProvider instance;
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private static ConcurrentHashMap<String, Retrofit> retrofit = new ConcurrentHashMap<>();
    @NotNull
    private static final ConcurrentHashMap<Pair<String, Class<? extends IService>>, IService> apis = new ConcurrentHashMap<>();

    @JvmStatic
    @JvmOverloads
    @NotNull
    public static final INetworkProvider getInstance(@NotNull Context context, @NotNull IHeaderInterceptor headersInterceptor) {
        return Companion.getInstance(context, headersInterceptor);
    }

    @JvmStatic
    @JvmOverloads
    @NotNull
    public static final INetworkProvider getInstance(@NotNull Context context) {
        return Companion.getInstance(context);
    }

    public /* synthetic */ DefaultNetworkProvider(Context context, IHeaderInterceptor headersInterceptor, DefaultConstructorMarker $constructor_marker) {
        this(context, headersInterceptor);
    }

    private DefaultNetworkProvider(Context context, IHeaderInterceptor headersInterceptor) {
        this.context = context;
        this.headersInterceptor = headersInterceptor;
        this.timeoutSeconds = 45L;
        LoggingInterceptor $this$logger_u24lambda_u241 = new LoggingInterceptor();
        Boolean bool = false;
        NetworkLogLevel networkLogLevel = bool.booleanValue() ? NetworkLogLevel.BODY : null;
        $this$logger_u24lambda_u241.setLevel(networkLogLevel == null ? NetworkLogLevel.HEADERS : networkLogLevel);
        this.logger = $this$logger_u24lambda_u241;
    }

    /* synthetic */ DefaultNetworkProvider(Context context, IHeaderInterceptor iHeaderInterceptor, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? new DefaultHeadersInterceptor(context) : iHeaderInterceptor);
    }

    @NotNull
    public Retrofit retrofit(@NotNull String baseUrl) {
        Intrinsics.checkNotNullParameter(baseUrl, "baseUrl");
        ConcurrentMap $this$getOrPut$iv = retrofit;
        Retrofit retrofit2 = $this$getOrPut$iv.get(baseUrl);
        if (retrofit2 == null) {
            Gson gson = createGson();
            OkHttpClient client = createHttpClient(this.context);
            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(baseUrl).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create(gson)).client(client);
            Retrofit build = builder.build();
            retrofit2 = $this$getOrPut$iv.putIfAbsent(baseUrl, build);
            if (retrofit2 == null) {
                retrofit2 = build;
            }
        }
        Intrinsics.checkNotNullExpressionValue(retrofit2, "getOrPut(...)");
        return retrofit2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v28 */
    /* JADX WARN: Type inference failed for: r0v51, types: [com.tm.IService] */
    @NotNull
    public <T extends IService> T service(@NotNull String baseUrl, @NotNull Class<T> cls) {
        T t;
        Intrinsics.checkNotNullParameter(baseUrl, "baseUrl");
        Intrinsics.checkNotNullParameter(cls, "type");
        Iterator<Map.Entry<Pair<String, Class<? extends IService>>, IService>> it = apis.entrySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                t = null;
                break;
            }
            Map.Entry it2 = it.next();
            Map.Entry entry = Intrinsics.areEqual(it2.getKey().getFirst(), baseUrl) && Intrinsics.areEqual(it2.getKey().getSecond(), cls) ? it2 : null;
            T value = entry != null ? entry.getValue() : null;
            if (value != null) {
                t = value;
                break;
            }
        }
        T t2 = t;
        if (t2 == null) {
            Retrofit retrofit2 = retrofit(baseUrl);
            IService api = (IService) retrofit2.create(cls);
            IService putIfAbsent = apis.putIfAbsent(TuplesKt.to(baseUrl, cls), api);
            if (putIfAbsent == 0) {
                putIfAbsent = api;
            }
            t2 = putIfAbsent;
        }
        boolean z = t2 != null;
        if (!_Assertions.ENABLED || z) {
            T t3 = t2;
            Intrinsics.checkNotNull(t3, "null cannot be cast to non-null type T of com.tm.androidcopysdk.network.DefaultNetworkProvider.service");
            return t3;
        }
        throw new AssertionError("Assertion failed");
    }

    @NotNull
    public IHeaderInterceptor headersInterceptor() {
        return this.headersInterceptor;
    }

    @NotNull
    public ILoggingInterceptor loggerInterceptor() {
        return this.logger;
    }

    private final Gson createGson() {
        GsonBuilder gsonBuilder = new GsonBuilder().setLenient();
        gsonBuilder.registerTypeAdapter(Date.class, DefaultNetworkProvider::createGson$lambda$5);
        Gson create = gsonBuilder.create();
        Intrinsics.checkNotNullExpressionValue(create, "create(...)");
        return create;
    }

    private static final JsonElement createGson$lambda$5(Date src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(NetworkManager.mFormat.format(src));
    }

    private final OkHttpClient createHttpClient(Context context) {
        X509TrustManager trustManager;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (FlavorSettings.getInstance().isAppSupportSSLFactory()) {
            String[] certArrayFromPreference = SSLFactoryUtils.getCertArrayFromPreference(context);
            Intrinsics.checkNotNullExpressionValue(certArrayFromPreference, "getCertArrayFromPreference(...)");
            if (!(certArrayFromPreference.length == 0)) {
                String[] certArrayFromPreference2 = SSLFactoryUtils.getCertArrayFromPreference(context);
                SSLSocketFactory sslSocketFactory = SSLFactoryUtils.createCertificatePinSSLSocketFactory((String[]) Arrays.copyOf(certArrayFromPreference2, certArrayFromPreference2.length));
                if (sslSocketFactory != null && (trustManager = SSLFactoryUtils.systemDefaultTrustManager()) != null) {
                    builder.sslSocketFactory(sslSocketFactory, trustManager);
                }
            }
        }
        OkHttpClient build = builder.followRedirects(false).connectTimeout(this.timeoutSeconds, TimeUnit.SECONDS).readTimeout(this.timeoutSeconds, TimeUnit.SECONDS).addInterceptor(headersInterceptor()).addInterceptor(loggerInterceptor()).build();
        Intrinsics.checkNotNullExpressionValue(build, "build(...)");
        return build;
    }

    /* compiled from: DefaultNetworkProvider.kt */
    @Metadata(mv = {1, 9, 0}, k = 1, xi = MyAudioFormat.AUDIO_CHANNEL_IN_FRONT_BACK, d1 = {"��<\n\u0002\u0018\u0002\n\u0002\u0010��\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\b\u0086\u0003\u0018��2\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001a\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0007R.\u0010\u0003\u001a\"\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\u0006\u0012\f\u0012\n\u0012\u0006\b\u0001\u0012\u00020\b0\u00070\u0005\u0012\u0004\u0012\u00020\b0\u0004X\u0082\u0004¢\u0006\u0002\n��R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n��R\u001a\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\f0\u0004X\u0082\u000e¢\u0006\u0002\n��¨\u0006\u0012"}, d2 = {"Lcom/tm/androidcopysdk/network/DefaultNetworkProvider$Companion;", "", "()V", "apis", "Ljava/util/concurrent/ConcurrentHashMap;", "Lkotlin/Pair;", "", "Ljava/lang/Class;", "Lcom/tm/IService;", "instance", "Lcom/tm/INetworkProvider;", "retrofit", "Lretrofit2/Retrofit;", "getInstance", "context", "Landroid/content/Context;", "headersInterceptor", "Lcom/tm/IHeaderInterceptor;", "androidcopysdk_signalRelease"})
    /* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/DefaultNetworkProvider$Companion.class */
    public static final class Companion {
        @JvmStatic
        @JvmOverloads
        @NotNull
        public final INetworkProvider getInstance(@NotNull Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            return getInstance$default(this, context, null, 2, null);
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }

        private Companion() {
        }

        public static /* synthetic */ INetworkProvider getInstance$default(Companion companion, Context context, IHeaderInterceptor iHeaderInterceptor, int i, Object obj) {
            if ((i & 2) != 0) {
                iHeaderInterceptor = new DefaultHeadersInterceptor(context);
            }
            return companion.getInstance(context, iHeaderInterceptor);
        }

        @JvmStatic
        @JvmOverloads
        @NotNull
        public final INetworkProvider getInstance(@NotNull Context context, @NotNull IHeaderInterceptor headersInterceptor) {
            Intrinsics.checkNotNullParameter(context, "context");
            Intrinsics.checkNotNullParameter(headersInterceptor, "headersInterceptor");
            if (DefaultNetworkProvider.instance == null) {
                DefaultNetworkProvider.instance = new DefaultNetworkProvider(context, headersInterceptor, null);
            }
            INetworkProvider iNetworkProvider = DefaultNetworkProvider.instance;
            if (iNetworkProvider == null) {
                throw new IllegalArgumentException("Required value was null.".toString());
            }
            return iNetworkProvider;
        }
    }
}
