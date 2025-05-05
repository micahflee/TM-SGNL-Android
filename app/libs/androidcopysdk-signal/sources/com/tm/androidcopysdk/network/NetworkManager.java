package com.tm.androidcopysdk.network;

import android.content.Context;
import com.tm.INetworkProvider;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.HandleResponseListener;
import com.tm.androidcopysdk.events.EventsAPI;
import com.tm.androidcopysdk.network.appSettings.ServerAppSettingsRequest;
import com.tm.androidcopysdk.network.keepAlive.KeepAliveRequest;
import com.tm.androidcopysdk.network.keepAlive.KeepAliveRequestV3;
import com.tm.androidcopysdk.utils.FlavorSettings;
import com.tm.androidcopysdk.utils.TMCredentialsStore;
import com.tm.logger.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
/* loaded from: input.aar:classes.jar:com/tm/androidcopysdk/network/NetworkManager.class */
public class NetworkManager {
    private static final String ARCIVER_APP_ID = "600701";
    public static final String LOGS_BASE_URL = "https://www.telemessage.com/";
    HandleResponseListener mListener;
    String mBaseUrl;
    public static SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    static final byte[] EmptyZip;

    static {
        mFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        EmptyZip = new byte[]{80, 75, 5, 6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    }

    public static SimpleDateFormat formatter() {
        return mFormat;
    }

    public NetworkManager(Context context, String url) {
        this.mBaseUrl = "https://archive.telemessage.com/";
        if (url != null && !url.isEmpty()) {
            this.mBaseUrl = url;
        }
    }

    private static String bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            return "did not work";
        }
    }

    public <T> retrofit2.Response<T> start(BodyBase message, HandleResponseListener listener, Context context, boolean log) {
        this.mListener = listener;
        Log.d("network", "started api call");
        INetworkProvider networkProvider = context.getApplicationContext().networkProvider();
        TMCredentialsStore credentialsStore = TMCredentialsStore.getInstance(context);
        networkProvider.headersInterceptor().setAuthentication(credentialsStore.userName(context), credentialsStore.password(context));
        AndroidCopyServerAPI TMAndroidCopyAPI = (AndroidCopyServerAPI) networkProvider.service(this.mBaseUrl, AndroidCopyServerAPI.class);
        Call caller = null;
        if (message instanceof TeleMessageMMSArchive) {
            if (!CommonUtils.isUserArchive(context)) {
                caller = TMAndroidCopyAPI.postTelemessageMMSMessage(NetworkManagerAPIUtil.getPostMessageURLByAPIVersion(context), new EnaMmsRequestBody(context, (TeleMessageMMSArchive) message));
            }
        } else if (message instanceof TelemessageArchiverMessage) {
            if (!CommonUtils.isUserArchive(context)) {
                networkProvider.headersInterceptor().setMessageId(message.getMessageId());
                caller = TMAndroidCopyAPI.postSMSMessage(NetworkManagerAPIUtil.getPostMessageURLByAPIVersion(context), (TelemessageArchiverMessage) message);
            }
        } else if (message instanceof SMSMessageRecord) {
            caller = message instanceof SMSEVENTMessageRecord ? TMAndroidCopyAPI.postSMSMessage((SMSMessageRecord) message) : TMAndroidCopyAPI.postSMSMessageToken((SMSMessageRecord) message);
        } else if ((message instanceof MMSMessageRecorder) && !FlavorSettings.getInstance().supportNativeMsg()) {
            caller = TMAndroidCopyAPI.telegramUploadPostMMSMessage(new MmsRequestBody(context, (MMSMessageRecorder) message));
        } else if (message instanceof MMSMessageRecorder) {
            caller = TMAndroidCopyAPI.postMMSMessageToken((MMSMessageRecorder) message);
        } else if (message instanceof CallLogMessageRecorder) {
            if (!CommonUtils.isUserArchive(context)) {
                networkProvider.headersInterceptor().setMessageId(message.getMessageId());
                caller = TMAndroidCopyAPI.archiveCallObj(NetworkManagerAPIUtil.getPostMessageURLByAPIVersion(context), new CallRequestBody(context, (CallLogMessageRecorder) message));
            }
        } else if (message instanceof EventsAPI) {
            caller = TMAndroidCopyAPI.postEvent((EventsAPI) message);
        } else if (message instanceof ServerAppSettingsRequest) {
            caller = TMAndroidCopyAPI.getAppSettings(FlavorSettings.getInstance().getPostSettingsUrl(), ((ServerAppSettingsRequest) message).getSettingsAsJson());
        } else if (message instanceof ReportMessage) {
            caller = TMAndroidCopyAPI.postReportMessage(new IssueReportMessageBody(context));
        } else if (message instanceof ReportLogs) {
            File zipFile = (File) Log.getAttachmentsLog(NetworkConstance.SEND_LOGS_REQUEST_FILE_NAME, ((ReportLogs) message).getUserPhone()).get("zip");
            RequestBody requestFile = RequestBody.create(MediaType.parse(NetworkConstance.SEND_LOGS_REQUEST_FILE_TYPE), zipFile);
            MultipartBody.Part body = MultipartBody.Part.createFormData(NetworkConstance.SEND_LOGS_REQUEST_FILE_KEY_NAME, zipFile.getName(), requestFile);
            RequestBody email = RequestBody.create(MultipartBody.FORM, NetworkConstance.SEND_LOGS_MAILS);
            RequestBody subject = RequestBody.create(MultipartBody.FORM, ((ReportLogs) message).getSubject());
            RequestBody phone = RequestBody.create(MultipartBody.FORM, ((ReportLogs) message).getUserPhone());
            RequestBody userName = RequestBody.create(MultipartBody.FORM, ((ReportLogs) message).getUserName());
            RequestBody flavorNameAndFreeText = RequestBody.create(MultipartBody.FORM, FlavorSettings.getInstance().getLogsBody() + " \n" + ((ReportLogs) message).getFreeText());
            RequestBody firstName = RequestBody.create(MultipartBody.FORM, ((ReportLogs) message).getmFirstName());
            RequestBody lastName = RequestBody.create(MultipartBody.FORM, ((ReportLogs) message).getmLastName());
            RequestBody emailUser = RequestBody.create(MultipartBody.FORM, ((ReportLogs) message).getmEmail());
            caller = TMAndroidCopyAPI.postReportLogs(((ReportLogs) message).getAuthenticationToken(), body, subject, email, phone, userName, firstName, lastName, emailUser, flavorNameAndFreeText);
        } else if (message instanceof GetAuthenticationToken) {
            RequestBody user = RequestBody.create(MultipartBody.FORM, ((GetAuthenticationToken) message).getUser());
            RequestBody pass = RequestBody.create(MultipartBody.FORM, ((GetAuthenticationToken) message).getPass());
            caller = TMAndroidCopyAPI.postGetAuthenticationToken(user, pass);
        } else if (message instanceof EnsureRequest) {
            ArrayList<Object> req = new ArrayList<>();
            EnsureRequest cReq = (EnsureRequest) message;
            req.add(cReq.authententicationDetails);
            req.add(cReq.prams);
            caller = TMAndroidCopyAPI.ensureIPDevice(req);
        } else if (message instanceof UpdateVersionRequest) {
            ArrayList<Object> req2 = new ArrayList<>();
            UpdateVersionRequest cReq2 = (UpdateVersionRequest) message;
            req2.add(cReq2.authententicationDetails);
            req2.add(cReq2.params);
            caller = TMAndroidCopyAPI.updaetVersionRequest(req2);
        } else if (message instanceof KeepAliveRequest) {
            caller = ((KeepAliveRequest) message).getDeviceToken() != null ? TMAndroidCopyAPI.keepAliveRest(new KeepAliveRequest(((KeepAliveRequest) message).getAppId(), ((KeepAliveRequest) message).getAppVersion(), ((KeepAliveRequest) message).getInstallId(), ((KeepAliveRequest) message).getDeviceToken())) : TMAndroidCopyAPI.keepAliveRest(new KeepAliveRequest(null));
        } else if (message instanceof KeepAliveRequestV3) {
            caller = TMAndroidCopyAPI.keepAliveRestV3(((KeepAliveRequestV3) message).getBodyAsJson());
        } else if (message instanceof VersionAutomaticRollOutRequest) {
            int[] intArray = {Integer.parseInt(FlavorSettings.getInstance().getAppIdForKeepAlive())};
            caller = TMAndroidCopyAPI.getAppVersion(intArray);
        }
        if (caller != null) {
            try {
                retrofit2.Response<T> response = caller.execute();
                Log.d("network", "executed api call");
                if (!(message instanceof CallLogMessageRecorder) && !(message instanceof MMSMessageRecorder) && !(message instanceof TeleMessageMMSArchive)) {
                    Log.d("network", "!(message instanceof CallLogMessageRecorder) && !(message instanceof MMSMessageRecorder)&& !(message instanceof TeleMessageMMSArchive) ");
                } else if (message instanceof MMSMessageRecorder) {
                    Log.d("network", "request body: <id>=" + ((MMSMessageRecorder) message).getMessageId() + " message instanceof MMSMessageRecorder");
                } else {
                    Log.d("network", "request body else ");
                }
                return response;
            } catch (ConnectException e) {
                Log.e("MNSMSNSNSM", "ConnectException = " + e.getMessage(), e);
                e.printStackTrace();
                return null;
            } catch (SocketTimeoutException e2) {
                Log.e("MNSMSNSNSM", "SocketTimeoutException = " + e2.getMessage(), e2);
                e2.printStackTrace();
                return null;
            } catch (IOException e3) {
                Log.e("MNSMSNSNSM", "IOException = " + e3.getMessage(), e3);
                e3.printStackTrace();
                return null;
            } catch (Exception e4) {
                e4.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static void createEmptyZip(String path) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(path));
            fos.write(EmptyZip, 0, 22);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}
