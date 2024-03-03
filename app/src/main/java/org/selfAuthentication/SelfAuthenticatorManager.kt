package org.selfAuthentication

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.tm.authenticatorsdk.selfAuthenticator.AuthenticationAppType
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus
import com.tm.authenticatorsdk.selfAuthenticator.SelfAuthenticator
import com.tm.logger.Log
import org.tm.archive.BuildConfig
import org.tm.archive.R
import org.tm.archive.util.views.CircularProgressMaterialButton

//In order to change the environment base url call to this method:
//ApiUtil.Companion.selectServerEnvironment(Context)
//The default environment is charlieProduction = https://rest.telemessage.com

object SelfAuthenticatorManager {
  var shouldHideProgressDialog = false
  var shouldShowSuspendDialog = false
    init {
        Log.d("SelfAuthenticatorProcess","class SelfAuthenticatorManager started.")
    }

  lateinit var selfAuthenticator: SelfAuthenticator
  private var mAuthenticationProgressAlertDialogBuilder: AlertDialog.Builder? = null
  private var mAuthenticationProgressAlertDialog: AlertDialog? = null

    fun initAuthenticator(phoneNumber: String) {
        selfAuthenticator = SelfAuthenticator
        Log.d("SelfAuthenticatorProcess", "initAuthenticator - The phone number is: $phoneNumber")
        selfAuthenticator.initSelfAuthenticator(
            AuthenticationAppType.SIGNAL,
            phoneNumber,
          BuildConfig.VERSION_NAME
        )
    }

    fun startAuthentication(aIAuthenticationStatus: IAuthenticationStatus) {
        selfAuthenticator.startSelfAuthentication(aIAuthenticationStatus)
    }

    /*fun showProgressDialog(activity: Activity){
        mSelfAuthenticationDialogBuilder.showProgressDialog(activity)
    }

    fun hideProgressDialog(){
        mSelfAuthenticationDialogBuilder.hideProgressDialog()
    }*/


  fun startAuthenticationProcess(context: Context,
                                 phone: String?, aIAuthenticationStatus: IAuthenticationStatus) {
    initAuthenticator(phone!!)
    selfAuthenticator.startSelfAuthentication(aIAuthenticationStatus)
    createAuthenticationProgressAlertDialogIfNotExist(context, true)
    mAuthenticationProgressAlertDialog = mAuthenticationProgressAlertDialogBuilder!!.create()
    mAuthenticationProgressAlertDialog!!.show()
  }

  private fun createAuthenticationProgressAlertDialogIfNotExist(context: Context, isCanCancel: Boolean) {
    if (mAuthenticationProgressAlertDialogBuilder == null) {
      mAuthenticationProgressAlertDialogBuilder = AlertDialog.Builder(context, R.style.AuthTmProgressDialog)
      val view = LayoutInflater.from(context).inflate(R.layout.progress_bar_layout_with_background, null)
      mAuthenticationProgressAlertDialogBuilder!!.setView(view)
      mAuthenticationProgressAlertDialogBuilder!!.setCancelable(isCanCancel)
    }
  }
  enum class SuspendUIAction(val action : Int) {
    SHOULD_HIDE_PROGRESS_DIALOG(0),
    SHOULD_SHOW_SUSPEND_DIALOG(1)
  }

  fun hideAuthDialog() {
    if (mAuthenticationProgressAlertDialog != null) mAuthenticationProgressAlertDialog!!.dismiss()
  }

  fun hideDialogAndShowSuspendDialog(action : SuspendUIAction) {
    if (action == SuspendUIAction.SHOULD_HIDE_PROGRESS_DIALOG) shouldHideProgressDialog = true
    if (action == SuspendUIAction.SHOULD_SHOW_SUSPEND_DIALOG) shouldShowSuspendDialog = true
    if (shouldHideProgressDialog && shouldShowSuspendDialog) {
      hideAuthDialog()
      Log.d("ConversationListFragment", "mAuthenticationProgressAlertDialog dismiss")
      shouldHideProgressDialog = false
      shouldShowSuspendDialog = false
    }
  }

}