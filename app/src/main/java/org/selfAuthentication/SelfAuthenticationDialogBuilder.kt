package org.selfAuthentication

import android.app.Activity
import android.app.AlertDialog
import android.view.View
import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.ISendLogCallback
import com.tm.androidcopysdk.utils.PrefManager
import org.archiver.ArchivePreferenceConstants
import org.tm.archive.R


class SelfAuthenticationDialogBuilder : ISendLogCallback{
    lateinit var mLogsSentContext : Activity
    lateinit var mProgressDialog : View

  fun doSendLogsClicked(context: Activity, view : View) {
    mLogsSentContext = context
    val builder = AlertDialog.Builder(context)
    mProgressDialog = view

    builder.setTitle(R.string.not_activated_user_dialog_title)
    builder.setMessage(context.getString(R.string.not_activated_user_dialog_message) + "?")

    builder.setPositiveButton(R.string.DebugSendLogs) { dialog, which ->

//      mProgressDialog.show()
      mProgressDialog.visibility = View.VISIBLE
      AndroidCopySDK.getInstance(context).sentLogs(
        context,
        this,
        PrefManager.getStringPref(context, ArchivePreferenceConstants.PREF_KEY_DEVICE_PHONE_NUMBER, ""),
        "Signal Archiver logs",
        PrefManager.getStringPref(context, ArchivePreferenceConstants.PREF_KEY_DEVICE_NAME, ""),
        "",
        "",
        "",
        "",
        ArchivePreferenceConstants.GENERATE_TOK_NAME,
        ArchivePreferenceConstants.GENERATE_TOK_PASS
      )
    }
    builder.setNegativeButton(R.string.OK, null)
    builder.show()

  }

    override fun sendLogFailure() {
        if(::mProgressDialog.isInitialized) {
//            mProgressDialog.dismiss()
          mProgressDialog.visibility = View.GONE
        }
    }

    override fun sendLogSucceed() {
        if(::mProgressDialog.isInitialized) {
//            mProgressDialog.dismiss()
          mProgressDialog.visibility = View.GONE
        }
    }


}