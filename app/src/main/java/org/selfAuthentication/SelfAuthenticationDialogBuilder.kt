package com.tm.authenticatorsdk.selfAuthenticator

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.ISendLogCallback
import com.tm.logger.BuildConfig
import com.tm.logger.Log
import org.archiver.ArchiveConstants
import org.selfAuthentication.AuthenticationUtils
import org.selfAuthentication.ProgressDialog
import org.selfAuthentication.SelfAuthenticatorManager
import org.tm.archive.ApplicationContext
import org.tm.archive.R

import java.util.*


class SelfAuthenticationDialogBuilder : ISendLogCallback{

    lateinit var mFirstFailureWarningAlertDialog : AlertDialog
    lateinit var mFirstFailureWarningLogsSent : AlertDialog
    lateinit var mFirstFailureSecondFailureWarning : AlertDialog
    lateinit var mLogsSentContext : Activity
    lateinit var mProgressDialog : Dialog

    companion object{
        val TEXT_MESSAGE_FOR_SENDING_LOGS = "Signal signup failure for " + BuildConfig.VERSION_NAME + " – could not locate the TeleMessage account. Please help."
    }


    fun showSelfAuthenticationFirstFailureWarning(
        context: Activity
    ) {
        if(!::mFirstFailureWarningAlertDialog.isInitialized || !mFirstFailureWarningAlertDialog.isShowing) {
            initFirstAuthenticationFailureMessageDialog(context)
        }

        if(!mFirstFailureWarningAlertDialog.isShowing){
            if(!context.isFinishing){
                mFirstFailureWarningAlertDialog.show()
            }
        }
    }

    fun showSelfAuthenticationFirstFailureWarningLogsSent(context: Activity) {
        if(!::mFirstFailureWarningLogsSent.isInitialized || !mFirstFailureWarningLogsSent.isShowing) {
            initAuthenticationFailureLogsSentMessageDialog(context)
        }

        if(!mFirstFailureWarningLogsSent.isShowing){
            if(!context.isFinishing){
                mFirstFailureWarningLogsSent.show()
            }
          //  showDialogInTheActivity(context, mFirstFailureWarningLogsSent)
        }
    }


    fun checkIfNeedToCloseTheAppOrJustDismissTheDialog(context: Activity, dialog: DialogInterface?) {
        if (SelfAuthenticatorManager.isAppValidationTimePassed()) {
          AuthenticationUtils.forceCloseApplication(context)
        } else {
            dialog?.dismiss()
        }
    }


    fun showSelfAuthenticationSecondFailureWarning(
        context: Activity
    ) {
        Log.d("SelfAuthenticatorProcess", "${!::mFirstFailureSecondFailureWarning.isInitialized}")
        if(::mFirstFailureSecondFailureWarning.isInitialized){
            Log.d("SelfAuthenticatorProcess",  "---> ${!mFirstFailureSecondFailureWarning.isShowing}")
        }
        if (!::mFirstFailureSecondFailureWarning.isInitialized || !mFirstFailureSecondFailureWarning.isShowing) {
            initSecondAuthenticationFailureMessageDialog(context)
        }else {
            Log.d("SelfAuthenticatorProcess",  "else to -- > !::mFirstFailureSecondFailureWarning.isInitialized || !mFirstFailureSecondFailureWarning.isShowing")
            if(!context.isFinishing){
                mFirstFailureSecondFailureWarning.show()
            }
        }
    }

    private fun initFirstAuthenticationFailureMessageDialog(context: Activity) {
        val mFirstFailureWarningDialogBuilder = AlertDialog.Builder(context)

        // set message of alert dialog
        mFirstFailureWarningDialogBuilder.setMessage(context.resources.getString(R.string.get_self_authentication_first_warning_message_step_1))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setNegativeButton(
                context.resources.getString(R.string.get_self_authentication_first_warning_message_step_1_dismiss),
                DialogInterface.OnClickListener { dialog, id ->
                    checkIfNeedToCloseTheAppOrJustDismissTheDialog(context, dialog)
                })
            .setPositiveButton(
                context.resources.getString(R.string.get_self_authentication_failure_contact_us),
                DialogInterface.OnClickListener { dialog, id ->
                    onTeleMessageSendLogsToSupportClicked(context)
                    if (!context.isFinishing) {
                        getAuthenticationProgressDialog(context).show()
                    }
                    dialog.dismiss()
                })

        mFirstFailureWarningAlertDialog = mFirstFailureWarningDialogBuilder.create()
    }

    private fun initSecondAuthenticationFailureMessageDialog(context: Activity) {
        val dialogBuilder = AlertDialog.Builder(context)
        Log.d(
            "SelfAuthenticatorProcess",
            "!::mFirstFailureSecondFailureWarning.isInitialized || !mFirstFailureSecondFailureWarning.isShowing"
        )
        // set message of alert dialog
        dialogBuilder.setMessage(context.resources.getString(R.string.get_self_authentication_second_warning_message_step_2))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setNegativeButton(
                context.resources.getString(R.string.get_self_authentication_failure_message_close),
                DialogInterface.OnClickListener { dialog, id ->
                    checkIfNeedToCloseTheAppOrJustDismissTheDialog(context, dialog)
                })
            .setPositiveButton(
                context.resources.getString(R.string.get_self_authentication_failure_contact_us),
                DialogInterface.OnClickListener { dialog, id ->
                    onTeleMessageSendLogsToSupportClicked(context)
                    if (!context.isFinishing) {
                        getAuthenticationProgressDialog(context).show()
                    }
                })

        mFirstFailureSecondFailureWarning = dialogBuilder.create()
        if (!context.isFinishing) {
            mFirstFailureSecondFailureWarning.show()
        }
    }


    private fun initAuthenticationFailureLogsSentMessageDialog(context: Activity) {
        val dialogBuilder = AlertDialog.Builder(context)
        // set message of alert dialog
        dialogBuilder.setMessage(context.resources.getString(R.string.get_self_authentication_contact_us_sent))
            // if the dialog is cancelable
            .setCancelable(false)
            // positive button text and action
            .setNegativeButton(
                context.resources.getString(R.string.get_self_authentication_failure_message_close),
                DialogInterface.OnClickListener { dialog, id ->
                    checkIfNeedToCloseTheAppOrJustDismissTheDialog(context, dialog)
                })
        // create dialog box
        mFirstFailureWarningLogsSent = dialogBuilder.create()
    }


    fun onTeleMessageSendLogsToSupportClicked(context: Activity) {
        Log.d("SelfAuthenticatorProcess", "onTeleMessageSendLogsToSupportClicked started")
        mLogsSentContext = context
        val name = ""/*ProfileActivity.getUserName()*/
        val freeText = TEXT_MESSAGE_FOR_SENDING_LOGS
        AndroidCopySDK.getInstance(context).sentLogs(
            context,
            this,
          ApplicationContext.getInstance().getSharedPreferences(
                "archiveConfig",
                Context.MODE_PRIVATE
            ).getString("userPhoneNumber", ""),
            "Signal logs - " + Calendar.getInstance().time.toString(),
            name,
            freeText,
          "",
          "",
          "",
          ArchiveConstants.GENERATE_TOK_NAME,
          ArchiveConstants.GENERATE_TOK_PASS
        )
    }

    fun showDialogInTheActivity(activity: Activity, dialog: AlertDialog){
       Log.d("SelfAuthenticatorProcess", "showDialogInTheActivity ")
        if(!activity.isFinishing){
            Log.d("SelfAuthenticatorProcess", "showDialogInTheActivity !activity.isFinishing")
            //dialog.show()
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                dialog.show()
            }, 100)
        }
    }

    fun getAuthenticationProgressDialog(activity: Activity): Dialog {
        if(!::mProgressDialog.isInitialized || (mProgressDialog.ownerActivity != null && mProgressDialog.ownerActivity!!.localClassName != activity.localClassName)){
           mProgressDialog = ProgressDialog.progressDialog(activity)
        }

        return mProgressDialog
    }

    fun isOneOfTheAuthenticationDialogIsShowing(): Boolean{
        return  ::mFirstFailureWarningAlertDialog.isInitialized && mFirstFailureWarningAlertDialog.isShowing ||
        ::mFirstFailureWarningLogsSent.isInitialized && mFirstFailureWarningLogsSent.isShowing ||
        ::mFirstFailureSecondFailureWarning.isInitialized && mFirstFailureSecondFailureWarning.isShowing ||
        ::mProgressDialog.isInitialized && mProgressDialog.isShowing
    }

    override fun sendLogFailure() {
        if(::mProgressDialog.isInitialized) {
            mProgressDialog.dismiss()
        }
    }

    override fun sendLogSucceed() {
        if(::mProgressDialog.isInitialized) {
            mProgressDialog.dismiss()
        }
        SelfAuthenticatorManager.showLogSentIfNeeded(mLogsSentContext)
    }

    fun showProgressDialog(activity: Activity) {
        if(!::mProgressDialog.isInitialized || (mProgressDialog.ownerActivity != null && mProgressDialog.ownerActivity!!.localClassName != activity.localClassName)){
            mProgressDialog = AlertDialog.Builder(activity).setCancelable(false).create()
        }

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            mProgressDialog.show()
        }, 1)

    }

    fun hideProgressDialog() {
        if(::mProgressDialog.isInitialized) {
            Handler(Looper.getMainLooper()).post(Runnable {
                mProgressDialog.hide()
            })

        }
    }

}