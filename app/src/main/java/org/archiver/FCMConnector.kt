package org.archiver

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.utils.PrefManager
import com.tm.logger.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.archiver.ArchiveUtil.Companion.fetchFCMToken
import org.archiver.ArchiveUtil.Companion.getFCMTokenIfExists
import org.tm.archive.ApplicationContext
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.gcm.FcmUtil
import org.tm.archive.jobs.FcmRefreshJob
import org.tm.archive.keyvalue.SignalStore

class FCMConnector {

  companion object {

    const val TAG = "FCMConnector"

    const val RETRIEVE_ONE_TIME_PIN_FCM_FROM_TYPE = "Get Inbox"
    const val RETRIEVE_ONE_TIME_PIN_FCM_MSG = "MESSAGE"
    const val RETRIEVE_ONE_TIME_PIN_CODE_SUCCESSES_SUB_STRING = "code : "


    @JvmStatic
    fun initOfficialSignalFirebaseAccount(context: Context) {
      Log.d(TAG, "init---Official---SignalFirebaseAccount");
      val options = FirebaseOptions.Builder()
        .setApplicationId("1:312334754206:android:a9297b152879f266")
        .setApiKey("AIzaSyDrfzNAPBPzX6key51hqo3p5LZXF5Y-yxU")
        .setProjectId("api-project-312334754206")
        .setGcmSenderId("312334754206")
        .build()

      try {
        FirebaseApp.clearInstancesForTest()
        FirebaseApp.initializeApp(
          ApplicationContext.getInstance(context).applicationContext,
          options
        )
      } catch (e: Exception) {
        Log.d(TAG, "App already exists " + e.message)
      }
      CoroutineScope(Dispatchers.IO).launch {
        val token = FcmUtil.getToken(context)
        if (token.isPresent) {
          val oldToken = SignalStore.account().fcmToken
          Log.i(
            TAG,
            "fcmconnector -> FCM_TM_UTILS token: " + token.get().length
          )
          if (token.get() != oldToken) {
            val oldLength = oldToken?.length ?: -1
            Log.i(
              TAG,
              "fcmconnector -> FCM_TM_UTILS Token changed. oldLength: " + oldLength + "  newLength: " + token.get().length
            )

            Log.i(
              TAG, "FCM_TM_UTILS Token changed. new Token: " + token.get()
            )

          } else {
            Log.i(TAG, "Token didn't change.")
          }

          if (SignalStore.account().isRegistered) {

            ApplicationDependencies.getJobManager().add(FcmRefreshJob())


          }
        }

      }
    }

    @JvmStatic
    fun initTeleMessageSignalFirebaseAccount(context: Context, fcmName: String?, isClearAll: Boolean) {
      Log.d(TAG,"init---Telemessage---SignalFirebaseAccount")
      val isAlreadyDoneSelfAuthentication =
        PrefManager.getBooleanPref(context, "isAlreadyDoneSelfAuthentication", false)
      Log.d(
        TAG,
        "SelfAuthenticatorProcess -> onCreate = isAlreadyDoneSelfAuthentication = $isAlreadyDoneSelfAuthentication"
      )
      if (getFCMTokenIfExists(context) == null || getFCMTokenIfExists(context)!!
          .isEmpty() || !isAlreadyDoneSelfAuthentication
      ) {
        Log.d(TAG, "ArchiveUtil.getFCMTokenIfExists(this) == null --" + (getFCMTokenIfExists(context) == null))
        Log.d(TAG, "ArchiveUtil.getFCMTokenIfExists(this).isEmpty() --" + getFCMTokenIfExists(context)!!.isEmpty())
        Log.d(TAG, "!isAlreadyDoneSelfAuthentication --" + !isAlreadyDoneSelfAuthentication)
        Log.i(TAG, "init Telemessage -> current FCM: " + FirebaseApp.getInstance().options.projectId)
        val options = FirebaseOptions.Builder()
          .setApplicationId("1:578202328450:android:0c71bb144fc9cf628e039b")
          .setApiKey("AIzaSyAl8hz1VyCAniywmN4_3yUTK17-PNmn98M")
          .setProjectId("signal-d0e5e")
          .setGcmSenderId("578202328450")
          .build()
        try {
          if (isClearAll) {
            FirebaseApp.clearInstancesForTest()
          }
          if (fcmName == null || fcmName.isEmpty()) {
            FirebaseApp.initializeApp(
              ApplicationContext.getInstance(context).applicationContext,
              options
            )
          } else {
            FirebaseApp.initializeApp(
              ApplicationContext.getInstance(context).applicationContext,
              options,
              fcmName
            )
          }
          Log.d(TAG, "FirebaseApp.getApps(context): " + FirebaseApp.getApps(context))
          Log.i(
            TAG,
            "init telemessage account"
          )
        } catch (e: java.lang.Exception) {
          Log.d(TAG, "App already exists")
        }
      }
      fetchFCMToken(context, null)
    }

    @JvmStatic
    fun updateSignUpCredentials(context: Context, userName: String?, password: String?) {
      AndroidCopySDK.getInstance(context)
        .signupSucess(userName, password)
    }

  }
}