package org.selfAuthentication

import android.content.Context

import com.tm.authenticatorsdk.selfAuthenticator.AuthenticationAppType
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus
import com.tm.authenticatorsdk.selfAuthenticator.SelfAuthenticator
import com.tm.logger.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tm.archive.BaseActivity
import org.tm.archive.BuildConfig

//In order to change the environment base url call to this method:
//ApiUtil.Companion.selectServerEnvironment(Context)
//The default environment is charlieProduction = https://rest.telemessage.com

object SelfAuthenticatorManager {
  var mProgressDialogVisibility = true
  var mSuspendDialogVisibility = false
    init {
        Log.d("SelfAuthenticatorManager","class SelfAuthenticatorManager started")
    }

  lateinit var selfAuthenticator: SelfAuthenticator


    fun initAuthenticator(phoneNumber: String) {
        selfAuthenticator = SelfAuthenticator
        Log.d("SelfAuthenticatorManager", "initAuthenticator - The phone number is: $phoneNumber")
        selfAuthenticator.initSelfAuthenticator(
            AuthenticationAppType.SIGNAL,
            phoneNumber,
          BuildConfig.VERSION_NAME
        )
    }

  fun removeLinkedDevices(baseActivity: BaseActivity) {
    DevicesDisconnector(baseActivity)
  }

    fun startAuthentication(aIAuthenticationStatus: IAuthenticationStatus) {
        selfAuthenticator.startSelfAuthentication(aIAuthenticationStatus)
    }

  fun startAuthenticationProcess(context: Context,
                                 phone: String?, aIAuthenticationStatus: IAuthenticationStatus) {
    Log.d("SelfAuthenticatorManager","startAuthenticationProcess")
//    createAndShowAuthProgressDialog(context, true)
    CoroutineScope(Dispatchers.IO).launch {
      initAuthenticator(phone!!)
      selfAuthenticator.startSelfAuthentication(aIAuthenticationStatus)
    }
  }



  /*fun isEndAuthenticationDialog() {
   if (!mProgressDialogVisibility && mSuspendDialogVisibility) {
      endAuthDialog()
      mProgressDialogVisibility = true
      mSuspendDialogVisibility = false
    }
  }

  fun setProgressDialogVisibility(isShown : Boolean){
    mProgressDialogVisibility = isShown
    isEndAuthenticationDialog()
  }
  fun setSuspendDialogVisibility(isShown : Boolean){
    mSuspendDialogVisibility = isShown
    isEndAuthenticationDialog()
  }*/


}