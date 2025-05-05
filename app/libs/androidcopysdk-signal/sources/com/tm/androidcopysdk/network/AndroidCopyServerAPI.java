package com.tm.androidcopysdk.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tm.IService;
import com.tm.androidcopysdk.events.EventsAPI;
import com.tm.androidcopysdk.network.Base.ServerBaseResponse;
import com.tm.androidcopysdk.network.keepAlive.KeepAliveRequest;
import com.tm.androidcopysdk.version_aoutomatic_rollout.VersionAutomaticRollOutModel;
import java.util.ArrayList;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/AndroidCopyServerAPI.class */
public interface AndroidCopyServerAPI extends IService {
    @POST("api/rest/archive/updateappevent")
    Call<Void> postEvent(@Body EventsAPI eventsAPI);

    @POST("api/rest/archive/telemessageincomingmessage")
    Call<Void> postSMSMessage(@Body SMSMessageRecord sMSMessageRecord);

    @POST
    Call<Void> postSMSMessage(@Url String str, @Body TelemessageArchiverMessage telemessageArchiverMessage);

    @POST
    Call<Void> postTelemessageMMSMessage(@Url String str, @Body EnaMmsRequestBody enaMmsRequestBody);

    @POST("api/rest/archive/telemessageincomingmessage")
    Call<Void> postMMSMessage(@Body MMSMessageRecorder mMSMessageRecorder);

    @POST("basicsecured/api/rest/archive/telemessageIncomingMessage")
    Call<Void> postSMSMessageToken(@Body SMSMessageRecord sMSMessageRecord);

    @POST("basicsecured/api/rest/archive/telemessageIncomingMessage")
    Call<Void> postMMSMessageToken(@Body MMSMessageRecorder mMSMessageRecorder);

    @POST("api/rest/archive/telemessageincomingmessage")
    Call<Void> postMMSMessage(@Body CallLogMessageRecorder callLogMessageRecorder);

    @POST
    Call<JsonElement> getAppSettings(@Url String str, @Body JsonObject jsonObject);

    @POST("rest/message/send")
    Call<Void> postReportMessage(@Body IssueReportMessageBody issueReportMessageBody);

    @POST("wp-json/jwt-auth/v1/log_post")
    @Multipart
    Call<String> postReportLogs(@Header("Authorization") String str, @Part MultipartBody.Part part, @Part("user_text_field ") RequestBody requestBody, @Part("email_to") RequestBody requestBody2, @Part("user_phone") RequestBody requestBody3, @Part("user_name") RequestBody requestBody4, @Part("first_name") RequestBody requestBody5, @Part("last_name") RequestBody requestBody6, @Part("user_email") RequestBody requestBody7, @Part("user_text_field") RequestBody requestBody8);

    @POST("wp-json/jwt-auth/v1/token")
    @Multipart
    Call<ResponseBody> postGetAuthenticationToken(@Part("username") RequestBody requestBody, @Part("password") RequestBody requestBody2);

    @POST("api/rest/archive/telemessageIncomingMessage")
    Call<Void> uploadPostMMSMessage(@Body CallRequestBody callRequestBody);

    @POST
    Call<Void> archiveCallObj(@Url String str, @Body CallRequestBody callRequestBody);

    @POST("basicsecured/api/rest/archive/telemessageIncomingMessage")
    Call<Void> socGenUploadPostMMSMessage(@Body CallRequestBody callRequestBody);

    @POST("rest/user/updateUser")
    Call<ArrayList<UpdateUserResponse>> updateUserRequest(@Body ArrayList<Object> arrayList);

    @POST("/rest/user/ensureIPDevice")
    Call<ArrayList<UpdateUserResponse>> ensureIPDevice(@Body ArrayList<Object> arrayList);

    @POST("/rest/user/setAppVersion")
    Call<ArrayList<ServerBaseResponse>> updaetVersionRequest(@Body ArrayList<Object> arrayList);

    @POST("api/rest/AppPresenceController/keepAlive")
    Call<JsonElement> keepAliveRest(@Body KeepAliveRequest keepAliveRequest);

    @POST("api/rest/AppPresenceController/keepAliveV3")
    Call<JsonElement> keepAliveRestV3(@Body JsonObject jsonObject);

    @POST("rest/user/getAppVersion")
    Call<ArrayList<VersionAutomaticRollOutModel>> getAppVersion(@Body int[] iArr);

    @POST("basicsecured/api/rest/archive/telemessageIncomingMessage")
    Call<Void> telegramUploadPostMMSMessage(@Body MmsRequestBody mmsRequestBody);
}
