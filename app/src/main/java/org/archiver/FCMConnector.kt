package org.archiver

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.tm.androidcopysdk.AndroidCopySDK
import org.signal.core.util.logging.Log
import org.tm.archive.ApplicationContext

class FCMConnector {

  companion object {

    const val TAG = "FCMConnector"

    const val RETRIEVE_ONE_TIME_PIN_FCM_FROM_TYPE = "Get Inbox"
    const val RETRIEVE_ONE_TIME_PIN_FCM_MSG = "MESSAGE"
    const val RETRIEVE_ONE_TIME_PIN_CODE_SUCCESSES_SUB_STRING = "code : "


    @JvmStatic
    fun initOfficialSignalFirebaseAccount(context: Context,) {
      val options = FirebaseOptions.Builder()
        .setApplicationId("1:312334754206:android:a9297b152879f266")
        .setApiKey("AIzaSyDrfzNAPBPzX6key51hqo3p5LZXF5Y-yxU")
        .setProjectId("api-project-312334754206")
        .setGcmSenderId("312334754206")
        .build()

      try {
        FirebaseApp.clearInstancesForTest()
        FirebaseApp.initializeApp(ApplicationContext.getInstance(context).applicationContext, options)
      } catch (e: Exception) {
        Log.d(TAG, "App already exists " + e.message)
      }
    }

    @JvmStatic
    fun initTeleMessageSignalFirebaseAccount(context: Context, fcmName: String?, isClearAll: Boolean) {
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
          FirebaseApp.initializeApp(ApplicationContext.getInstance(context).applicationContext, options)
        } else {
          FirebaseApp.initializeApp(ApplicationContext.getInstance(context).applicationContext, options, fcmName)
        }
      } catch (e: java.lang.Exception) {
        Log.d(TAG, "App already exists")
      }
    }

    @JvmStatic
    fun updateSignUpCredentials(context: Context, userName: String?, password: String?) {
      AndroidCopySDK.getInstance(context)
        .signupSucess(userName, password)
    }

  }
}