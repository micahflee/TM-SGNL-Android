package org.tm.archive.exporter.flow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.tm.archive.LoggingFragment
import org.tm.archive.R
import org.tm.archive.databinding.SmsRemovalInformationFragmentBinding
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.util.CommunicationActions
import org.tm.archive.util.DateUtils
import org.tm.archive.util.navigation.safeNavigate
import java.util.Locale

/**
 * Fragment shown when entering the sms export flow from the basic megaphone.
 *
 * Layout shared with full screen megaphones for Phase 2/3.
 */
class SmsRemovalInformationFragment : LoggingFragment() {
  private val viewModel: SmsExportViewModel by activityViewModels()

  private lateinit var binding: SmsRemovalInformationFragmentBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = SmsRemovalInformationFragmentBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    if (!viewModel.isFromMegaphone) {
      findNavController().safeNavigate(SmsRemovalInformationFragmentDirections.actionSmsRemovalInformationFragmentToExportYourSmsMessagesFragment())
    } else {
      val goBackClickListener = { _: View ->
        if (!findNavController().popBackStack()) {
          requireActivity().finish()
        }
      }

      val phase3Start = DateUtils.formatDateWithMonthAndDay(Locale.getDefault(), SignalStore.misc().smsPhase3Start)
      binding.bullet1Text.text = getString(R.string.SmsRemoval_info_bullet_1_s, phase3Start)

      binding.toolbar.setNavigationOnClickListener(goBackClickListener)
      binding.laterButton.setOnClickListener(goBackClickListener)

      binding.learnMoreButton.setOnClickListener {
        CommunicationActions.openBrowserLink(requireContext(), getString(R.string.sms_export_url))
      }

      binding.exportSmsButton.setOnClickListener {
        findNavController().safeNavigate(SmsRemovalInformationFragmentDirections.actionSmsRemovalInformationFragmentToExportYourSmsMessagesFragment())
      }
    }
  }
}
