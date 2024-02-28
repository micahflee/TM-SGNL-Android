package org.archiver

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.tm.archive.R
import org.tm.archive.components.reminder.DozeReminder
import org.tm.archive.util.PowerManagerCompat
import org.tm.archive.util.TextSecurePreferences

class DozeOptimizer {

  private var alert: AlertDialog? = null

  fun optimize(activity: Activity) {
    requestBatteryOptimisation(activity)
  }

  fun cancel() {
    dismiss()
  }

  private fun requestBatteryOptimisation(activity: Activity) {
    if (!DozeReminder.isEligible(activity)) {
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
      PowerManagerCompat.requestIgnoreBatteryOptimizations(activity)
    }
    this.alert = builder.show()
  }

  private fun dismiss() {
    val alert = alert
    if (alert?.isShowing == true) {
      try {
        alert.dismiss()
      } finally { }
    }
    this.alert = null
  }
}