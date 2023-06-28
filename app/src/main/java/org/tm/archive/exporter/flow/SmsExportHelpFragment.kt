package org.tm.archive.exporter.flow

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import org.tm.archive.LoggingFragment
import org.tm.archive.R
import org.tm.archive.databinding.SmsExportHelpFragmentBinding
import org.tm.archive.help.HelpFragment

/**
 * Fragment wrapper around the app settings help fragment to provide a toolbar and set default category for sms export.
 */
class SmsExportHelpFragment : LoggingFragment(R.layout.sms_export_help_fragment) {
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val binding = SmsExportHelpFragmentBinding.bind(view)

    binding.toolbar.setOnClickListener {
      if (!findNavController().popBackStack()) {
        requireActivity().finish()
      }
    }

    childFragmentManager
      .beginTransaction()
      .replace(binding.smsExportHelpFragmentFragment.id, HelpFragment().apply { arguments = bundleOf(HelpFragment.START_CATEGORY_INDEX to HelpFragment.SMS_EXPORT_INDEX) })
      .commitNow()
  }
}
