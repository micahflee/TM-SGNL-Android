package org.tm.archive.exporter.flow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.signal.smsexporter.DefaultSmsHelper
import org.tm.archive.R
import org.tm.archive.databinding.ExportYourSmsMessagesFragmentBinding
import org.tm.archive.util.navigation.safeNavigate

/**
 * "Welcome" screen for exporting sms
 */
class ExportYourSmsMessagesFragment : Fragment(R.layout.export_your_sms_messages_fragment) {

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val binding = ExportYourSmsMessagesFragmentBinding.bind(view)

    binding.toolbar.setOnClickListener {
      requireActivity().finish()
    }

    binding.continueButton.setOnClickListener {
      if (DefaultSmsHelper.isDefaultSms(requireContext())) {
        findNavController().safeNavigate(ExportYourSmsMessagesFragmentDirections.actionExportYourSmsMessagesFragmentToExportingSmsMessagesFragment())
      } else {
        findNavController().safeNavigate(ExportYourSmsMessagesFragmentDirections.actionExportYourSmsMessagesFragmentToSetSignalAsDefaultSmsAppFragment())
      }
    }
  }
}
