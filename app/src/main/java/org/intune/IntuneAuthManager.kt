package org.intune

import android.app.Activity
import android.app.AlertDialog
import com.tm.authenticatorsdk.mamsdk.MDMAuthenticator
import com.tm.authenticatorsdk.selfAuthenticator.IAuthenticationStatus
import com.tm.logger.Log
import org.tm.archive.R
import kotlin.system.exitProcess

//**TM_SA**//add all intune package
object IntuneAuthManager {
  const val MDM_Auth_Status_String = "MDMAuthStatus"

  enum class MdmAuthStatus{
    ALREADY_SIGN,
    START_INTUNE_AUTH,
    START_SELF_AUTH
  }

  fun continueIntuneAuthentication(aIAuthenticationStatus: IAuthenticationStatus) {
    /*if (!SelfAuthenticatorManager.isSelfAuthenticationAlreadyStarted()) {
      SelfAuthenticatorManager.saveSelfAuthenticationFirstTimeTryingTime()
    }*/
    MDMAuthenticator.continueIntuneSelfAuthentication(aIAuthenticationStatus)
  }

  fun showDialog(context: Activity, listener: MDMDialogListener) {

    val dialogBuilder = AlertDialog.Builder(context)
    Log.d("IntuneAuthManager", "showDialog")
    // set message of alert dialog
    dialogBuilder.setTitle(context.resources.getString(R.string.intune_auth_title))
    dialogBuilder.setMessage(context.resources.getString(R.string.intune_auth_message))
      // if the dialog is cancelable
      .setCancelable(false)
      // positive button text and action
      .setNegativeButton(
        context.resources.getString(R.string.intune_auth_ok)
      ) { _, _ ->
        listener.startIntuneAgain()
      }
      .setPositiveButton(
        context.resources.getString(R.string.intune_auth_cancel)
      ) { _, _ ->
        exitProcess(0)
      }

    val alertDialog = dialogBuilder.create()
    alertDialog.show()
  }

  /**
   * update app status of auth.
   * it has two cases, intune auth already finish successfully or start self auth even it's managed device
   * 0 - intune auth finished successfully. don't start auth again
   * 1 - start intune auth
   * 2 - start self auth even it's managed device
   */
  /*fun updatedIntuneAuthenticatorPreference(_MDMAuthStatus: Int) {
    Log.d("IntuneAuthManager",
      "updatedIntuneAuthenticatorPreference," +
        " status auth is(" +
        "0 - already signed, " +
        "1 - should sign intune auth," +
        " 2 - should sign self auth) ? $_MDMAuthStatus")
    val mDMAuthStatusString = "MDMAuthStatus"
    val preferences = ApplicationContext.getInstance().getSharedPreferences(
      INTUNE_AUTHENTICATION_PREFERENCE_NAME, Context.MODE_PRIVATE
    )

    val editor = preferences.edit()
    editor.putInt(mDMAuthStatusString, _MDMAuthStatus)
    editor.apply()
  }*/

}