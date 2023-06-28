package org.tm.archive.profiles.manage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.tm.archive.R
import org.tm.archive.components.ViewBinderDelegate
import org.tm.archive.databinding.UsernameEducationFragmentBinding
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.megaphone.Megaphones
import org.tm.archive.util.CommunicationActions
import org.tm.archive.util.navigation.safeNavigate

/**
 * Displays a Username education screen which displays some basic information
 * about usernames and provides a learn-more link.
 */
class UsernameEducationFragment : Fragment(R.layout.username_education_fragment) {
  private val binding by ViewBinderDelegate(UsernameEducationFragmentBinding::bind)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.toolbar.setNavigationOnClickListener {
      findNavController().popBackStack()
    }

    binding.usernameEducationLearnMore.setOnClickListener {
      CommunicationActions.openBrowserLink(requireContext(), getString(R.string.username_support_url))
    }

    binding.continueButton.setOnClickListener {
      SignalStore.uiHints().markHasSeenUsernameEducation()
      ApplicationDependencies.getMegaphoneRepository().markFinished(Megaphones.Event.SET_UP_YOUR_USERNAME)
      findNavController().safeNavigate(UsernameEducationFragmentDirections.actionUsernameEducationFragmentToUsernameManageFragment())
    }
  }
}
