package org.tm.archive.gcm;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tm.androidcopysdk.BackupService;
import com.tm.androidcopysdk.CommonUtils;
import com.tm.androidcopysdk.MessageEvent;
import com.tm.androidcopysdk.utils.PrefManager;
import com.tm.authenticatorsdk.selfAuthenticator.IOnCredentialsArrived;
import com.tm.authenticatorsdk.selfAuthenticator.SelfAuthenticator;

import org.archive.selfAuthentication.SelfAuthenticatorConstants;
import org.archiver.ArchiveConstants;
import org.archiver.ArchivePreferenceConstants;
import org.archiver.FCMConnector;
import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.signal.core.util.logging.Log;
import org.tm.archive.ApplicationContext;
import org.tm.archive.BuildConfig;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobs.FcmRefreshJob;
import org.tm.archive.jobs.SubmitRateLimitPushChallengeJob;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.registration.PushChallengeRequest;
import org.tm.archive.util.NetworkUtil;
import org.tm.archive.util.SignalLocalMetrics;

import java.util.Locale;

public class FcmReceiveService extends FirebaseMessagingService implements IOnCredentialsArrived { //*TM_SA*//

  private static final String TAG = Log.tag(FcmReceiveService.class);

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    //**TM_SA**// Start
    com.tm.logger.Log.d(TAG, "SelfAuthenticatorM -> onMessageReceived!!!!! message = " + remoteMessage.getData().toString());
    if (remoteMessage.getData().get("Type") != null && remoteMessage.getData().get("Type").equals(FCMConnector.RETRIEVE_ONE_TIME_PIN_FCM_FROM_TYPE)) {
      String msgBody = remoteMessage.getData().get(FCMConnector.RETRIEVE_ONE_TIME_PIN_FCM_MSG);
      if (msgBody != null) {
        String pinCode = msgBody.split(" ")[msgBody.split(" ").length -1];
        com.tm.logger.Log.d(TAG, "The code is: " + pinCode);
        SelfAuthenticator.INSTANCE.getUserCredentials(pinCode, this);
      }
    } else { //**TM_SA**// END

      Log.i(TAG, String.format(Locale.US,
                               "onMessageReceived() ID: %s, Delay: %d (Server offset: %d), Priority: %d, Original Priority: %d, Network: %s",
                               remoteMessage.getMessageId(),
                               (System.currentTimeMillis() - remoteMessage.getSentTime()),
                               SignalStore.misc().getLastKnownServerTimeOffset(),
                               remoteMessage.getPriority(),
                               remoteMessage.getOriginalPriority(),
                               NetworkUtil.getNetworkStatus(this)));

      String registrationChallenge = remoteMessage.getData().get("challenge");
      String rateLimitChallenge    = remoteMessage.getData().get("rateLimitChallenge");

      if (registrationChallenge != null) {
        handleRegistrationPushChallenge(registrationChallenge);
      } else if (rateLimitChallenge != null) {
        handleRateLimitPushChallenge(rateLimitChallenge);
      } else {
        handleReceivedNotification(ApplicationDependencies.getApplication(), remoteMessage);
      }
    }
  }

  @Override
  public void onDeletedMessages() {
    Log.w(TAG, "onDeleteMessages() -- Messages may have been dropped. Doing a normal message fetch.");
    handleReceivedNotification(ApplicationDependencies.getApplication(), null);
  }

  @Override
  public void onNewToken(String token) {
    com.tm.logger.Log.i(TAG, "onNewToken(). token: " + token);//**TM_SA**//

    //**TM_SA**//
    com.tm.logger.Log.i(TAG, "current FCM: " + FirebaseApp.getInstance().getOptions().getProjectId());
    if(FirebaseApp.getInstance().getOptions().getProjectId().startsWith("signal")){
      PrefManager.setStringPref(getApplicationContext(), ArchivePreferenceConstants.FCM_TOKEN_PREFERENCE_KEY, token);
    }
    //**TM_SA**//

    if (!SignalStore.account().isRegistered()) {
      Log.i(TAG, "Got a new FCM token, but the user isn't registered.");
      return;
    }

    //**TM_SA**//
    if(!FirebaseApp.getInstance().getOptions().getProjectId().startsWith("signal")){
      ApplicationDependencies.getJobManager().add(new FcmRefreshJob());
    }
    //**TM_SA**//
  }

  @Override
  public void onMessageSent(@NonNull String s) {
    Log.i(TAG, "onMessageSent()" + s);
  }

  @Override
  public void onSendError(@NonNull String s, @NonNull Exception e) {
    Log.w(TAG, "onSendError()", e);
  }

  private static void handleReceivedNotification(Context context, @Nullable RemoteMessage remoteMessage) {
    boolean highPriority = remoteMessage != null && remoteMessage.getPriority() == RemoteMessage.PRIORITY_HIGH;
    try {
      Log.d(TAG, String.format(Locale.US, "[handleReceivedNotification] API: %s, RemoteMessagePriority: %s", Build.VERSION.SDK_INT, remoteMessage != null ? remoteMessage.getPriority() : "n/a"));

      if (highPriority) {
        FcmFetchManager.startForegroundService(context);
      } else if (Build.VERSION.SDK_INT < 26) {
        FcmFetchManager.startBackgroundService(context);
      }
    } catch (Exception e) {
      Log.w(TAG, "Failed to start service.", e);
      SignalLocalMetrics.FcmServiceStartFailure.onFcmFailedToStart();
    }

    FcmFetchManager.enqueueFetch(context, highPriority);
  }

  private static void handleRegistrationPushChallenge(@NonNull String challenge) {
    Log.d(TAG, "Got a registration push challenge.");
    PushChallengeRequest.postChallengeResponse(challenge);
  }

  private static void handleRateLimitPushChallenge(@NonNull String challenge) {
    Log.d(TAG, "Got a rate limit push challenge.");
    ApplicationDependencies.getJobManager().add(new SubmitRateLimitPushChallengeJob(challenge));
  }

  // <!--//**TM_SA**//--> START
  @Override
  public void onCredentialsArrived(@NotNull String userName, @NotNull String password, String environmentProduction, String environmentKeeper ) {
    com.tm.logger.Log.d("SelfAuthenticatorM", "onCredentialsArrived user = " + userName /*+ "   password = " + password*/ + " environmentProduction = " + environmentProduction + "  environmentKeeper = " + environmentKeeper);

    if(!userName.isEmpty()) {
      com.tm.logger.Log.d(TAG , "SelfAuthenticatorM -> before updateSignUpCredentials");
      new Handler(Looper.getMainLooper()).post(new Runnable() {
        @Override
        public void run() {
          if(BuildConfig.DEBUG){
            String baseUrlPrefProd = PrefManager.getStringPref(ApplicationContext.getInstance(), ArchiveConstants.SHARED_PREFERENCE_SELECTED_BASE_URL_PRODUCTION_KEY, ArchiveConstants.charlieProduction);
            String baseUrlPrefKeeper = PrefManager.getStringPref(ApplicationContext.getInstance(), ArchiveConstants.SHARED_PREFERENCE_SELECTED_BASE_URL_KEEPER_KEY, ArchiveConstants.prodKeeper);
            doSelfAuthenticationSucceed(userName, password,  baseUrlPrefProd, baseUrlPrefKeeper);
          }else {
            doSelfAuthenticationSucceed(userName, password, environmentProduction, environmentKeeper);
          }
        }
      });

      com.tm.logger.Log.d(TAG, "SelfAuthenticatorM -> after updateSignUpCredentials");
      EventBus.getDefault().post(new MessageEvent(SelfAuthenticatorConstants.Companion.getSelfAuthenticationSucceed()));
    }else{
      EventBus.getDefault().post(new MessageEvent(SelfAuthenticatorConstants.Companion.getSelfAuthenticationFailed()));
    }

  }

  private void doSelfAuthenticationSucceed(@NotNull String userName, @NotNull String password, String environmentProduction, String environmentKeeper) {
    if(CommonUtils.isMyServiceRunning(ApplicationContext.getInstance(), BackupService.class)){
      CommonUtils.stopBackupService(ApplicationContext.getInstance(), false);
    }

    FCMConnector.updateSignUpCredentials(getApplicationContext() ,userName, password);
    CommonUtils.setUrl(ApplicationContext.getInstance(), environmentProduction, environmentKeeper);

    CommonUtils.startBackupService(ApplicationContext.getInstance());
  }

  // <!--//**TM_SA**//--> END
}