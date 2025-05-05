package com.tm.authenticatorsdk.socgen;

import com.tm.IService;
import com.tm.authenticatorsdk.socgen.RetrieveCredentials.UserResponse;
import com.tm.authenticatorsdk.socgen.RetrieveOneTimePin.Response;
import com.tm.authenticatorsdk.socgen.signup.SignupResponse;
import com.tm.authenticatorsdk.socgen.signup.UserFields;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;
/* loaded from: input.aar:classes.jar:com/tm/authenticatorsdk/socgen/AuthenticatorAPI.class */
public interface AuthenticatorAPI extends IService {
    @POST("rest/user/signUpUser")
    Observable<List<SignupResponse>> Signup(@Body ArrayList<UserFields[]> arrayList);

    @POST("rest/user/retrieveOneTimePINExt")
    Observable<List<Response>> RetrieveOneTime(@Body Object[] objArr);

    @POST("rest/user/retrieveCredentials")
    Observable<List<UserResponse>> RetrieveCredentials(@Body Object[] objArr);

    @POST("rest/user/signUpUserWithManager")
    Call<List<SignupResponse>> SignUpUserWithManager(@Body ArrayList<UserFields[]> arrayList);
}
