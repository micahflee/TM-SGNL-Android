package org.tm.archive.exporter.flow

import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.tm.archive.LoggingFragment
import org.tm.archive.R
import org.tm.archive.SmsExportDirections
import org.tm.archive.databinding.ExportSmsFullErrorFragmentBinding
import org.tm.archive.util.navigation.safeNavigate

/**
 * Fragment shown when all export messages failed.
 */
class ExportSmsFullErrorFragment : LoggingFragment(R.layout.export_sms_full_error_fragment) {
  private val args: ExportSmsFullErrorFragmentArgs by navArgs()

  override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
    val inflater = super.onGetLayoutInflater(savedInstanceState)
    val contextThemeWrapper: Context = ContextThemeWrapper(requireContext(), R.style.Signal_DayNight)
    return inflater.cloneInContext(contextThemeWrapper)
  }

  @Suppress("UsePropertyAccessSyntax")
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val binding = ExportSmsFullErrorFragmentBinding.bind(view)

    val exportSuccessCount = args.exportMessageCount - args.exportMessageFailureCount
    binding.exportCompleteStatus.text = resources.getQuantityString(R.plurals.ExportSmsCompleteFragment__d_of_d_messages_exported, args.exportMessageCount, exportSuccessCount, args.exportMessageCount)
    binding.retryButton.setOnClickListener { findNavController().safeNavigate(SmsExportDirections.actionDirectToExportYourSmsMessagesFragment()) }
    binding.pleaseTryAgain.apply {
      setLinkColor(ContextCompat.getColor(requireContext(), R.color.signal_colorPrimary))
      setLearnMoreVisible(true, R.string.ExportSmsPartiallyComplete__contact_us)
      setOnLinkClickListener {
        findNavController().safeNavigate(SmsExportDirections.actionDirectToHelpFragment())
      }
    }
  }
}
