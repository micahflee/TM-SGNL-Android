package org.selfAuthentication

import android.content.Context
import com.tm.androidcopysdk.network.TMEnsureIPManager

import com.tm.authenticatorsdk.selfAuthenticator.AuthenticationAppType
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus
import com.tm.authenticatorsdk.selfAuthenticator.IEnsureIpDataArrived
import com.tm.authenticatorsdk.selfAuthenticator.SelfAuthenticator
import com.tm.logger.Log
import com.tm.utils.ApplicationInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.tm.archive.BaseActivity
import org.tm.archive.BuildConfig
import org.tm.archive.TeleMessageSignalApplication
import org.tm.archive.dependencies.ApplicationDependencies

//In order to change the environment base url call to this method:
//ApiUtil.Companion.selectServerEnvironment(Context)
//The default environment is charlieProduction = https://rest.telemessage.com

object SelfAuthenticatorManager : IEnsureIpDataArrived {

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
          BuildConfig.VERSION_NAME,
          this
        )
    }

  fun removeLinkedDevices(baseActivity: BaseActivity) {
    DevicesDisconnector(baseActivity)
  }

    fun startAuthentication(applicationInterface: ApplicationInterface, aIAuthenticationStatus: IAuthenticationStatus) {
        selfAuthenticator.startSelfAuthentication(applicationInterface, aIAuthenticationStatus)
    }

  fun startAuthenticationProcess(context: Context,
                                 phone: String?, applicationInterface: ApplicationInterface, aIAuthenticationStatus: IAuthenticationStatus) {
    Log.d("SelfAuthenticatorManager","startAuthenticationProcess")
//    createAndShowAuthProgressDialog(context, true)
    CoroutineScope(Dispatchers.IO).launch {
      initAuthenticator(phone!!)
      selfAuthenticator.startSelfAuthentication(applicationInterface, aIAuthenticationStatus)
    }
  }

  override fun onEnsureIpDataArrived(firstName: String, lastName: String, email: String) {
    TMEnsureIPManager.saveUserNameData(ApplicationDependencies.getApplication(), firstName, lastName, email)
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