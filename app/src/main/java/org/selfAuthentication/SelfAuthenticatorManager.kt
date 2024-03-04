package org.selfAuthentication

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.tm.authenticatorsdk.selfAuthenticator.AuthenticationAppType
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus
import com.tm.authenticatorsdk.selfAuthenticator.SelfAuthenticator
import com.tm.logger.Log
import org.tm.archive.BaseActivity
import org.tm.archive.BuildConfig
import org.tm.archive.R

//In order to change the environment base url call to this method:
//ApiUtil.Companion.selectServerEnvironment(Context)
//The default environment is charlieProduction = https://rest.telemessage.com

object SelfAuthenticatorManager {
  var shouldHideProgressDialog = false
  var shouldShowSuspendDialog = false
    init {
        Log.d("SelfAuthenticatorManager","class SelfAuthenticatorManager started")
    }

  lateinit var selfAuthenticator: SelfAuthenticator
  private var mAuthenticationProgressAlertDialogBuilder: AlertDialog.Builder? = null
  private var mAuthenticationProgressAlertDialog: AlertDialog? = null

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
    createAndShowAuthProgressDialog(context, true)
    initAuthenticator(phone!!)
    selfAuthenticator.startSelfAuthentication(aIAuthenticationStatus)
  }

  private fun createAndShowAuthProgressDialog(context: Context, isCanCancel: Boolean) {
    if (mAuthenticationProgressAlertDialogBuilder == null) {
      Log.d("SelfAuthenticatorManager","createAndShowAuthProgressDialog")
      mAuthenticationProgressAlertDialogBuilder = AlertDialog.Builder(context, R.style.AuthTmProgressDialog)
      val view = LayoutInflater.from(context).inflate(R.layout.progress_bar_layout_with_background, null)
      mAuthenticationProgressAlertDialogBuilder!!.setView(view)
      mAuthenticationProgressAlertDialogBuilder!!.setCancelable(isCanCancel)
      mAuthenticationProgressAlertDialog = mAuthenticationProgressAlertDialogBuilder!!.create()
      if (!mAuthenticationProgressAlertDialog!!.isShowing) {
        Log.d("SelfAuthenticatorManager","createAndShowAuthProgressDialog -> mAuthenticationProgressAlertDialog show")
        mAuthenticationProgressAlertDialog!!.show()
      }
    }
  }
  enum class SuspendUIAction(val action : Int) {
    SHOULD_HIDE_PROGRESS_DIALOG(0),
    SHOULD_SHOW_SUSPEND_DIALOG(1)
  }

  fun endAuthDialog() {
    if (mAuthenticationProgressAlertDialog != null) {
      Log.d("SelfAuthenticatorManager","endAuthDialog -> mAuthenticationProgressAlertDialog dismiss")
      mAuthenticationProgressAlertDialog!!.dismiss()
      mAuthenticationProgressAlertDialog = null
    }
  }

  fun hideDialogAndShowSuspendDialog(action : SuspendUIAction) {
    if (action == SuspendUIAction.SHOULD_HIDE_PROGRESS_DIALOG) shouldHideProgressDialog = true
    if (action == SuspendUIAction.SHOULD_SHOW_SUSPEND_DIALOG) shouldShowSuspendDialog = true
    if (shouldHideProgressDialog && shouldShowSuspendDialog) {
      endAuthDialog()
      shouldHideProgressDialog = false
      shouldShowSuspendDialog = false
    }
  }

}