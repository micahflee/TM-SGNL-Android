package com.mindorks.retrofit.coroutines.data.api;

import com.google.gson.JsonArray;
import com.tm.IService;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import java.util.Map;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
/* compiled from: ApiService.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b\u0007\bf\u0018��2\u00020\u0001J4\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00042\u0014\b\u0001\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\u0007H§@¢\u0006\u0002\u0010\tJ\u001e\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u000b\u001a\u00020\u0004H§@¢\u0006\u0002\u0010\fJ\u001e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u000e\u001a\u00020\u0004H§@¢\u0006\u0002\u0010\f¨\u0006\u000f"}, d2 = {"Lcom/mindorks/retrofit/coroutines/data/api/ApiService;", "Lcom/tm/IService;", "ensureIP", "Lretrofit2/Response;", "Lcom/google/gson/JsonArray;", "aEnsureIPModel", "headers", "", "", "(Lcom/google/gson/JsonArray;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUserCredentials", "retrieveCredentials", "(Lcom/google/gson/JsonArray;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "retrieveOneTime", "retrieveOTP", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/mindorks/retrofit/coroutines/data/api/ApiService.class */
public interface ApiService extends IService {
    @POST("/rest/user/ensureIPDeviceSigned")
    @Nullable
    Object ensureIP(@Body @NotNull JsonArray jsonArray, @HeaderMap @NotNull Map<String, String> map, @NotNull Continuation<? super Response<JsonArray>> continuation);

    @POST("rest/user/retrieveOneTimePINExt")
    @Nullable
    Object retrieveOneTime(@Body @NotNull JsonArray jsonArray, @NotNull Continuation<? super Response<JsonArray>> continuation);

    @POST("rest/user/retrieveCredentials")
    @Nullable
    Object getUserCredentials(@Body @NotNull JsonArray jsonArray, @NotNull Continuation<? super Response<JsonArray>> continuation);
}
