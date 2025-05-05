package com.mindorks.retrofit.coroutines.data.api;

import com.google.gson.JsonArray;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import java.util.Map;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Response;
/* compiled from: ApiHelper.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��*\n\u0002\u0018\u0002\n\u0002\u0010��\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0007\u0018��2\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J0\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\b\u001a\u00020\u00072\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b0\nH\u0086@¢\u0006\u0002\u0010\fJ\u001c\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u000e\u001a\u00020\u0007H\u0086@¢\u0006\u0002\u0010\u000fJ\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u0011\u001a\u00020\u0007H\u0086@¢\u0006\u0002\u0010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n��¨\u0006\u0012"}, d2 = {"Lcom/mindorks/retrofit/coroutines/data/api/ApiHelper;", "", "apiService", "Lcom/mindorks/retrofit/coroutines/data/api/ApiService;", "(Lcom/mindorks/retrofit/coroutines/data/api/ApiService;)V", "ensureIPDeviceSigned", "Lretrofit2/Response;", "Lcom/google/gson/JsonArray;", "aEnsureIPModel", "headers", "", "", "(Lcom/google/gson/JsonArray;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUserCredentials", "doRetrieveCredentialsRequest", "(Lcom/google/gson/JsonArray;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "retrieveOTP", "otpData", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/mindorks/retrofit/coroutines/data/api/ApiHelper.class */
public final class ApiHelper {
    @NotNull
    private final ApiService apiService;

    public ApiHelper(@NotNull ApiService apiService) {
        Intrinsics.checkNotNullParameter(apiService, "apiService");
        this.apiService = apiService;
    }

    @Nullable
    public final Object ensureIPDeviceSigned(@NotNull JsonArray aEnsureIPModel, @NotNull Map<String, String> map, @NotNull Continuation<? super Response<JsonArray>> continuation) {
        return this.apiService.ensureIP(aEnsureIPModel, map, continuation);
    }

    @Nullable
    public final Object retrieveOTP(@NotNull JsonArray otpData, @NotNull Continuation<? super Response<JsonArray>> continuation) {
        return this.apiService.retrieveOneTime(otpData, continuation);
    }

    @Nullable
    public final Object getUserCredentials(@NotNull JsonArray doRetrieveCredentialsRequest, @NotNull Continuation<? super Response<JsonArray>> continuation) {
        return this.apiService.getUserCredentials(doRetrieveCredentialsRequest, continuation);
    }
}
