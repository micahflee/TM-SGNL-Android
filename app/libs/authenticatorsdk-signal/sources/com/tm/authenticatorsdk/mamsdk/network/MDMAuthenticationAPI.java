package com.tm.authenticatorsdk.mamsdk.network;

import com.google.gson.JsonArray;
import com.tm.IService;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;
/* compiled from: MDMAuthenticationAPI.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 1, xi = 48, d1 = {"��$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018��2\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J\u001e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00032\b\b\u0001\u0010\n\u001a\u00020\tH§@¢\u0006\u0002\u0010\u000b¨\u0006\f"}, d2 = {"Lcom/tm/authenticatorsdk/mamsdk/network/MDMAuthenticationAPI;", "Lcom/tm/IService;", "ensureIP", "Lretrofit2/Response;", "Lcom/tm/authenticatorsdk/mamsdk/network/ResponseBody;", "requestBody", "Lcom/tm/authenticatorsdk/mamsdk/network/RequestBody;", "(Lcom/tm/authenticatorsdk/mamsdk/network/RequestBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "retrieveOneTime", "Lcom/google/gson/JsonArray;", "retrieveOTP", "(Lcom/google/gson/JsonArray;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "authenticatorsdk_signalRelease"})
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/mamsdk/network/MDMAuthenticationAPI.class */
public interface MDMAuthenticationAPI extends IService {
    @POST("/api/rest/mdm/signup")
    @Nullable
    Object ensureIP(@Body @NotNull RequestBody requestBody, @NotNull Continuation<? super Response<ResponseBody>> continuation);

    @POST("rest/user/retrieveOneTimePINExt")
    @Nullable
    Object retrieveOneTime(@Body @NotNull JsonArray jsonArray, @NotNull Continuation<? super Response<JsonArray>> continuation);
}
