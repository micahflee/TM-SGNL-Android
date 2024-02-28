package org.archiver

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.tm.archive.R
import org.tm.archive.components.reminder.DozeReminder

class DozeOptimizer {

  private var alert: AlertDialog? = null
  private var userApproved = false

  fun optimize(activity: Activity) {
    requestBatteryOptimisation(activity)
  }

  fun cancel() {
    dismiss()
  }

  fun onActivityResult(requestCode: Int, resultCode: Int) {
    if (requestCode != REQUEST_BATTERY_OPTIMIZATION  || resultCode == 0)
      return
    cancel()
    userApproved = true
  }

  private fun requestBatteryOptimisation(activity: Activity) {
    if (userApproved || !DozeReminder.isEligible(activity)) {
      dismiss()
      return
    }
    val alert = alert
    if (alert?.isShowing == true)
      return
    val builder = MaterialAlertDialogBuilder(activity)
    builder.setTitle(activity.resources.getString(R.string.DozeReminder_optimize_for_missing_play_services))
    builder.setMessage(activity.resources.getString(R.string.DozeReminder_this_device_does_not_support_play_services_tap_to_disable_system_battery))
    builder.setCancelable(false)
    builder.setPositiveButton(R.string.ok) { dialog, which ->
      dialog.dismiss()
      val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + activity.packageName))
      activity.startActivityForResult(intent, REQUEST_BATTERY_OPTIMIZATION)
//      PowerManagerCompat.requestIgnoreBatteryOptimizations(activity)
    }
    this.alert = builder.show()
  }

  private fun dismiss() {
    userApproved = false
    val alert = alert
    if (alert?.isShowing == true) {
      try {
        alert.dismiss()
      } finally { }
    }
    this.alert = null
  }

  companion object {
    const val REQUEST_BATTERY_OPTIMIZATION = 905
  }
}