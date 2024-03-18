package org.selfAuthentication

import android.app.Activity
import android.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tm.androidcopysdk.AndroidCopySDK
import com.tm.androidcopysdk.ISendLogCallback
import com.tm.androidcopysdk.model.resource.ResourceStatus
import com.tm.androidcopysdk.utils.PrefManager
import org.archiver.ArchivePreferenceConstants
import org.archiver.ArchiveSender
import org.tm.archive.R


class SelfAuthenticationDialogBuilder : ISendLogCallback{
    lateinit var mLogsSentContext : Activity
//    lateinit var mProgressDialog : View
    private val logCallbackMutable : MutableLiveData<ResourceStatus> = MutableLiveData()
  val logCallback : LiveData<ResourceStatus> = logCallbackMutable


  fun doSendLogsClicked(activity: Activity) : AlertDialog {
    mLogsSentContext = activity
    val builder = AlertDialog.Builder(activity)

    builder.setTitle(R.string.not_activated_user_dialog_title)
    builder.setMessage(activity.getString(R.string.not_activated_user_dialog_message))

    builder.setPositiveButton(R.string.DebugSendLogs) { dialog, which ->
      logCallbackMutable.postValue(ResourceStatus.Loading)
      ArchiveSender.sendLogs(activity, this)
    }
    builder.setNegativeButton(R.string.OK, null)
    val alertDialog = builder.create()
    alertDialog.show()
    return alertDialog
  }


  override fun sendLogFailure() {
      logCallbackMutable.postValue(ResourceStatus.Error)
    }

    override fun sendLogSucceed() {
      logCallbackMutable.postValue(ResourceStatus.Success)
    }


}