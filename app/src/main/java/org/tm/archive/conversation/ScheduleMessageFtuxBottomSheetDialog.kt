package org.tm.archive.conversation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import org.tm.archive.R
import org.tm.archive.components.FixedRoundedCornerBottomSheetDialogFragment
import org.tm.archive.components.ViewBinderDelegate
import org.tm.archive.databinding.ScheduleMessageFtuxBottomSheetBinding
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.util.ServiceUtil
import org.tm.archive.util.fragments.findListener

class ScheduleMessageFtuxBottomSheetDialog : FixedRoundedCornerBottomSheetDialogFragment() {
  override val peekHeightPercentage: Float = 0.66f
  override val themeResId: Int = R.style.Widget_Signal_FixedRoundedCorners_Messages

  private val binding by ViewBinderDelegate(ScheduleMessageFtuxBottomSheetBinding::bind)

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    return inflater.inflate(R.layout.schedule_message_ftux_bottom_sheet, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    if (Build.VERSION.SDK_INT >= 31 && !ServiceUtil.getAlarmManager(context).canScheduleExactAlarms()) {
      binding.reenableSettings.visibility = View.VISIBLE
      binding.okay.visibility = View.GONE

      val launcher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Build.VERSION.SDK_INT < 31 || ServiceUtil.getAlarmManager(context).canScheduleExactAlarms()) {
          proceedWithScheduledSend()
        }
      }

      binding.enableScheduledMessagesGoToSettings.setOnClickListener {
        SignalStore.uiHints().markHasSeenScheduledMessagesInfoSheet()
        launcher.launch(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + requireContext().packageName)))
      }
    } else {
      binding.okay.setOnClickListener {
        proceedWithScheduledSend()
      }
    }
  }

  private fun proceedWithScheduledSend() {
    SignalStore.uiHints().markHasSeenScheduledMessagesInfoSheet()
    findListener<ScheduleMessageDialogCallback>()?.onSchedulePermissionsGranted(
      requireArguments().getString(ScheduleMessageDialogCallback.ARGUMENT_METRIC_ID),
      requireArguments().getLong(ScheduleMessageDialogCallback.ARGUMENT_SCHEDULED_DATE)
    )
    dismissAllowingStateLoss()
  }
}
