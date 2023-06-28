package org.tm.archive.contacts.sync

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import org.tm.archive.R
import org.tm.archive.components.FixedRoundedCornerBottomSheetDialogFragment
import org.tm.archive.databinding.CdsPermanentErrorBottomSheetBinding
import org.tm.archive.util.BottomSheetUtil
import org.tm.archive.util.CommunicationActions

/**
 * Bottom sheet shown when CDS is in a permanent error state, preventing us from doing a sync.
 */
class CdsPermanentErrorBottomSheet : FixedRoundedCornerBottomSheetDialogFragment() {

  private lateinit var binding: CdsPermanentErrorBottomSheetBinding

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = CdsPermanentErrorBottomSheetBinding.inflate(inflater.cloneInContext(ContextThemeWrapper(inflater.context, themeResId)), container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.learnMoreButton.setOnClickListener {
      CommunicationActions.openBrowserLink(requireContext(), "https://support.signal.org/hc/articles/360007319011#android_contacts_error")
    }

    binding.settingsButton.setOnClickListener {
      val intent = Intent().apply {
        action = Intent.ACTION_VIEW
        data = android.provider.ContactsContract.Contacts.CONTENT_URI
      }
      try {
        startActivity(intent)
      } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, R.string.CdsPermanentErrorBottomSheet_no_contacts_toast, Toast.LENGTH_SHORT).show()
      }
    }
  }

  companion object {
    @JvmStatic
    fun show(fragmentManager: FragmentManager) {
      val fragment = CdsPermanentErrorBottomSheet()
      fragment.show(fragmentManager, BottomSheetUtil.STANDARD_BOTTOM_SHEET_FRAGMENT_TAG)
    }
  }
}
