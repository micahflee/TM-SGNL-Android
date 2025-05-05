package com.tm.authenticatorsdk.selfAuthenticator;

import com.google.gson.JsonArray;
import com.mindorks.retrofit.coroutines.data.api.ApiHelper;
import com.tm.authenticatorsdk.AppSignatureHelper;
import com.tm.authenticatorsdk.BuildConfig;
import com.tm.logger.Log;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Ref;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Response;
/* compiled from: SelfAuthenticator.kt */
@Metadata(mv = {1, AppSignatureHelper.NUM_HASHED_BYTES, BuildConfig.DEBUG}, k = 3, xi = 48, d1 = {"��\n\n��\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010��\u001a\u00020\u0001*\u00020\u0002H\u008a@"}, d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"})
@DebugMetadata(f = "SelfAuthenticator.kt", l = {183}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "com.tm.authenticatorsdk.selfAuthenticator.SelfAuthenticator$getUserCredentials$1")
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/selfAuthenticator/SelfAuthenticator$getUserCredentials$1.class */
final class SelfAuthenticator$getUserCredentials$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    int label;
    final /* synthetic */ String $oneTimePIN;
    final /* synthetic */ Ref.ObjectRef<Pair<String, String>> $credentialsPair;
    final /* synthetic */ IOnCredentialsArrived $onCredentialsArrived;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public SelfAuthenticator$getUserCredentials$1(String $oneTimePIN, Ref.ObjectRef<Pair<String, String>> objectRef, IOnCredentialsArrived $onCredentialsArrived, Continuation<? super SelfAuthenticator$getUserCredentials$1> continuation) {
        super(2, continuation);
        this.$oneTimePIN = $oneTimePIN;
        this.$credentialsPair = objectRef;
        this.$onCredentialsArrived = $onCredentialsArrived;
    }

    @NotNull
    public final Continuation<Unit> create(@Nullable Object value, @NotNull Continuation<?> continuation) {
        return new SelfAuthenticator$getUserCredentials$1(this.$oneTimePIN, this.$credentialsPair, this.$onCredentialsArrived, continuation);
    }

    @Nullable
    public final Object invoke(@NotNull CoroutineScope p1, @Nullable Continuation<? super Unit> continuation) {
        return create(p1, continuation).invokeSuspend(Unit.INSTANCE);
    }

    @Nullable
    public final Object invokeSuspend(@NotNull Object $result) {
        Object obj;
        JsonArray userCredentialsBody;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        try {
            switch (this.label) {
                case BuildConfig.DEBUG /* 0 */:
                    ResultKt.throwOnFailure($result);
                    ApiHelper mApiHelper = SelfAuthenticator.INSTANCE.getMApiHelper();
                    userCredentialsBody = SelfAuthenticator.INSTANCE.getUserCredentialsBody(SelfAuthenticator.INSTANCE.getMMobileNumber(), this.$oneTimePIN);
                    this.label = 1;
                    obj = mApiHelper.getUserCredentials(userCredentialsBody, (Continuation) this);
                    if (obj == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    break;
                case 1:
                    ResultKt.throwOnFailure($result);
                    obj = $result;
                    break;
                default:
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            Response getUserCredentialsResult = (Response) obj;
            this.$credentialsPair.element = SelfAuthenticator.INSTANCE.getUserNameAndPasswordFromResponse(String.valueOf(getUserCredentialsResult.body()));
            String userName = (String) ((Pair) this.$credentialsPair.element).getFirst();
            String userNameHide = StringsKt.replaceRange(userName, RangesKt.until(1, userName.length() - 1), StringsKt.repeat("*", userName.length() - 2)).toString();
            Log.d("SelfAuthenticatorM", "user name = " + userNameHide + "  password = *****");
            this.$credentialsPair.element = new Pair(((Pair) this.$credentialsPair.element).getFirst(), ((Pair) this.$credentialsPair.element).getSecond());
            this.$onCredentialsArrived.onCredentialsArrived((String) ((Pair) this.$credentialsPair.element).getFirst(), (String) ((Pair) this.$credentialsPair.element).getSecond(), (String) AuthenticatorConstants.Companion.getBASE_URL().getFirst(), (String) AuthenticatorConstants.Companion.getBASE_URL().getSecond());
        } catch (Exception e) {
            this.$onCredentialsArrived.onCredentialsArrived("", "", (String) AuthenticatorConstants.Companion.getBASE_URL().getFirst(), (String) AuthenticatorConstants.Companion.getBASE_URL().getSecond());
            Log.d("SelfAuthenticatorM", "getUserCredentialsResult Failed! " + e.getMessage());
        }
        return Unit.INSTANCE;
    }
}
