package com.tm;

import com.tm.utils.Definitions;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
/* compiled from: INetworkProvider.kt */
@Metadata(mv = {1, Definitions.sendSubject, 0}, k = 1, xi = 48, d1 = {"��2\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018��2\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H&J\b\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH&J-\u0010\n\u001a\u0002H\u000b\"\b\b��\u0010\u000b*\u00020\f2\u0006\u0010\b\u001a\u00020\t2\f\u0010\r\u001a\b\u0012\u0004\u0012\u0002H\u000b0\u000eH&¢\u0006\u0002\u0010\u000f¨\u0006\u0010"}, d2 = {"Lcom/tm/INetworkProvider;", "", "headersInterceptor", "Lcom/tm/IHeaderInterceptor;", "loggerInterceptor", "Lcom/tm/ILoggingInterceptor;", "retrofit", "Lretrofit2/Retrofit;", "baseUrl", "", "service", "T", "Lcom/tm/IService;", Definitions.sdType, "Ljava/lang/Class;", "(Ljava/lang/String;Ljava/lang/Class;)Lcom/tm/IService;", "common_release"})
/* loaded from: input.aar:classes.jar:com/tm/INetworkProvider.class */
public interface INetworkProvider {
    @NotNull
    Retrofit retrofit(@NotNull String str);

    @NotNull
    <T extends IService> T service(@NotNull String str, @NotNull Class<T> cls);

    @NotNull
    IHeaderInterceptor headersInterceptor();

    @NotNull
    ILoggingInterceptor loggerInterceptor();
}
