package org.archiver.gcm

import android.os.Handler
import android.os.Looper
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.RemoteMessage
import com.tm.androidcopysdk.BackupService
import com.tm.androidcopysdk.CommonUtils
import com.tm.androidcopysdk.MessageEvent
import com.tm.androidcopysdk.network.appSettings.UpdateEvent
import com.tm.androidcopysdk.utils.PrefManager
import com.tm.authenticatorsdk.selfAuthenticator.IOnCredentialsArrived
import com.tm.authenticatorsdk.selfAuthenticator.SelfAuthenticator.getUserCredentials
import com.tm.logger.Log
//import org.archive.selfAuthentication.SelfAuthenticatorConstants.Companion.selfAuthenticationFailed
//import org.archive.selfAuthentication.SelfAuthenticatorConstants.Companion.selfAuthenticationSucceed
import org.archiver.ArchiveConstants
import org.archiver.ArchivePreferenceConstants
import org.archiver.FCMConnector
import org.archiver.FCMConnector.Companion.updateSignUpCredentials
import org.greenrobot.eventbus.EventBus
import org.signal.core.util.logging.Log.i
import org.tm.archive.BuildConfig
import org.tm.archive.gcm.FcmReceiveService
import org.tm.archive.keyvalue.SignalStore

class TeleMessageFcmReceiveService : FcmReceiveService(), IOnCredentialsArrived {

  override fun onMessageReceived(remoteMessage: RemoteMessage) {
    Log.d(TAG, "SelfAuthenticatorM -> onMessageReceived!!!!! message = " + remoteMessage.data.toString())
    if (remoteMessage.data["Type"] != null && remoteMessage.data["Type"] == FCMConnector.RETRIEVE_ONE_TIME_PIN_FCM_FROM_TYPE) {
      val msgBody = remoteMessage.data[FCMConnector.RETRIEVE_ONE_TIME_PIN_FCM_MSG]
      if (msgBody != null) {
        val pinCode = msgBody.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
          .toTypedArray()[msgBody.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray().size - 1]
        Log.d(TAG, "The code is: $pinCode")
        getUserCredentials(pinCode, this)
      }
    } else
      super.onMessageReceived(remoteMessage)
  }

  override fun onNewToken(token: String) {
    Log.i(TAG, "onNewToken(). token: $token")

    Log.i(TAG, "current FCM: " + FirebaseApp.getInstance().options.projectId)
    if (FirebaseApp.getInstance().options.projectId!!.startsWith("signal")) {
      PrefManager.setStringPref(applicationContext, ArchivePreferenceConstants.FCM_TOKEN_PREFERENCE_KEY, token)
    }

    if (!SignalStore.account().isRegistered) {
      i(TAG, "Got a new FCM token, but the user isn't registered.")
      return
    }

    if (!FirebaseApp.getInstance().options.projectId!!.startsWith("signal"))
      super.onNewToken(token)
  }

  override fun onCredentialsArrived(userName: String, password: String, environmentProduction: String, environmentKeeper: String) {
    Log.d("SelfAuthenticatorM", "onCredentialsArrived user = $userName environmentProduction = $environmentProduction  environmentKeeper = $environmentKeeper")
    if (!userName.isEmpty()) {
      Log.d(TAG, "SelfAuthenticatorM -> before updateSignUpCredentials")
      Handler(Looper.getMainLooper()).post {
        if (BuildConfig.DEBUG) {
          val baseUrlPrefProd = PrefManager.getStringPref(applicationContext, ArchiveConstants.SHARED_PREFERENCE_SELECTED_BASE_URL_PRODUCTION_KEY, ArchiveConstants.charlieProduction)
          val baseUrlPrefKeeper = PrefManager.getStringPref(applicationContext, ArchiveConstants.SHARED_PREFERENCE_SELECTED_BASE_URL_KEEPER_KEY, ArchiveConstants.prodKeeper)
          doSelfAuthenticationSucceed(userName, password, baseUrlPrefProd, baseUrlPrefKeeper)
        } else {
          doSelfAuthenticationSucceed(userName, password, environmentProduction, environmentKeeper)
        }
      }
      Log.d(TAG, "SelfAuthenticatorM -> after updateSignUpCredentials")
      EventBus.getDefault().post(UpdateEvent(UpdateEvent.EVENTS_TYPE.activated))
//      EventBus.getDefault().post(MessageEvent(selfAuthenticationSucceed))
    } else {
      EventBus.getDefault().post(UpdateEvent(UpdateEvent.EVENTS_TYPE.suspension))
//      EventBus.getDefault().post(MessageEvent(selfAuthenticationFailed))
    }
  }

  private fun doSelfAuthenticationSucceed(userName: String, password: String, environmentProduction: String, environmentKeeper: String) {
    if (CommonUtils.isMyServiceRunning(applicationContext, BackupService::class.java)) {
      CommonUtils.stopBackupService(applicationContext, false)
    }
    updateSignUpCredentials(applicationContext, userName, password)
    CommonUtils.setUrl(applicationContext, environmentProduction, environmentKeeper)
    CommonUtils.startBackupService(applicationContext)
  }


  companion object {
    private const val TAG = "TeleMessageFcmReceiveService"
  }
}