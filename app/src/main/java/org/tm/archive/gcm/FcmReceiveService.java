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
import org.tm.archive.util.FeatureFlags;
import org.tm.archive.util.NetworkUtil;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FcmReceiveService extends FirebaseMessagingService implements IOnCredentialsArrived { //*TM_SA*//

  private static final String TAG = Log.tag(FcmReceiveService.class);

  private static final long FCM_FOREGROUND_INTERVAL = TimeUnit.MINUTES.toMillis(3);

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    //**TM_SA**// Start
    com.tm.logger.Log.d("SelfAuthenticatorM", "onMessageReceived!!!!! message = " + remoteMessage.getData().toString());
    if (remoteMessage.getData().get("Type") != null && remoteMessage.getData().get("Type").equals(FCMConnector.RETRIEVE_ONE_TIME_PIN_FCM_FROM_TYPE)) {
      String msgBody = remoteMessage.getData().get(FCMConnector.RETRIEVE_ONE_TIME_PIN_FCM_MSG);
      if (msgBody != null) {
        String pinCode = msgBody.split(" ")[msgBody.split(" ").length -1];
        com.tm.logger.Log.d("SelfAuthenticatorProcess", "The code is: " + pinCode);
        SelfAuthenticator.INSTANCE.getUserCredentials(pinCode, this);
      }
    } else { //**TM_SA**// END

      Log.i(TAG, String.format(Locale.US,
                               "onMessageReceived() ID: %s, Delay: %d, Priority: %d, Original Priority: %d, Network: %s",
                               remoteMessage.getMessageId(),
                               (System.currentTimeMillis() - remoteMessage.getSentTime()),
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
    Log.i(TAG, "onNewToken()");

    if (!SignalStore.account().isRegistered()) {
      Log.i(TAG, "Got a new FCM token, but the user isn't registered.");
      return;
    }

    //**TM_SA**//
    if(FirebaseApp.getInstance().getOptions().getProjectId().startsWith("signal")){
      PrefManager.setStringPref(getApplicationContext(), ArchivePreferenceConstants.FCM_TOKEN_PREFERENCE_KEY, token);
    }else {
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
    boolean enqueueSuccessful = false;

    try {
      boolean highPriority         = remoteMessage != null && remoteMessage.getPriority() == RemoteMessage.PRIORITY_HIGH;
      long    timeSinceLastRefresh = System.currentTimeMillis() - SignalStore.misc().getLastFcmForegroundServiceTime();

      Log.d(TAG, String.format(Locale.US, "[handleReceivedNotification] API: %s, FeatureFlag: %s, RemoteMessagePriority: %s, TimeSinceLastRefresh: %s ms", Build.VERSION.SDK_INT, FeatureFlags.useFcmForegroundService(), remoteMessage != null ? remoteMessage.getPriority() : "n/a", timeSinceLastRefresh));

      if (highPriority && FeatureFlags.useFcmForegroundService()) {
        enqueueSuccessful = FcmFetchManager.enqueue(context, true);
        SignalStore.misc().setLastFcmForegroundServiceTime(System.currentTimeMillis());
      } else if (highPriority && Build.VERSION.SDK_INT >= 31 && timeSinceLastRefresh > FCM_FOREGROUND_INTERVAL) {
        enqueueSuccessful = FcmFetchManager.enqueue(context, true);
        SignalStore.misc().setLastFcmForegroundServiceTime(System.currentTimeMillis());
      } else if (highPriority || Build.VERSION.SDK_INT < 26 || remoteMessage == null) {
        enqueueSuccessful = FcmFetchManager.enqueue(context, false);
      }
    } catch (Exception e) {
      Log.w(TAG, "Failed to start service.", e);
      enqueueSuccessful = false;
    }

    if (!enqueueSuccessful) {
      Log.w(TAG, "Unable to start service. Falling back to legacy approach.");
      FcmFetchManager.tryLegacyFallback(context);
    }
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
    com.tm.logger.Log.d("SelfAuthenticatorM", "onCredentialsArrived user = " + userName + "   password = " + password + " environmentProduction = " + environmentProduction + "  environmentKeeper = " + environmentKeeper);

    if(!userName.isEmpty()) {
      com.tm.logger.Log.d("SelfAuthenticatorM", "before updateSignUpCredentials");
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

      com.tm.logger.Log.d("SelfAuthenticatorM", "after updateSignUpCredentials");
      EventBus.getDefault().post(new MessageEvent(SelfAuthenticatorConstants.Companion.getSelfAuthenticationSucceed()));
    }else{
      EventBus.getDefault().post(new MessageEvent(SelfAuthenticatorConstants.Companion.getSelfAuthenticationFailed()));
    }

  }

  private void doSelfAuthenticationSucceed(@NotNull String userName, @NotNull String password, String environmentProduction, String environmentKeeper) {
    if(CommonUtils.isMyServiceRunning(ApplicationContext.getInstance(), BackupService.class)){
      CommonUtils.stopBackupService(ApplicationContext.getInstance());
    }

    FCMConnector.updateSignUpCredentials(getApplicationContext() ,userName, password);
    CommonUtils.setUrl(ApplicationContext.getInstance(), environmentProduction, environmentKeeper);

    CommonUtils.startBackupService(ApplicationContext.getInstance());
  }

  // <!--//**TM_SA**//--> END
}