package org.tm.archive.stories.settings.create

import androidx.navigation.fragment.findNavController
import org.tm.archive.R
import org.tm.archive.database.model.DistributionListId
import org.tm.archive.recipients.RecipientId
import org.tm.archive.stories.settings.select.BaseStoryRecipientSelectionFragment
import org.tm.archive.util.navigation.safeNavigate

/**
 * Allows user to select who will see the story they are creating
 */
class CreateStoryViewerSelectionFragment : BaseStoryRecipientSelectionFragment() {
  override val actionButtonLabel: Int = R.string.CreateStoryViewerSelectionFragment__next
  override val distributionListId: DistributionListId? = null

  override fun goToNextScreen(recipients: Set<RecipientId>) {
    findNavController().safeNavigate(CreateStoryViewerSelectionFragmentDirections.actionCreateStoryViewerSelectionToCreateStoryWithViewers(recipients.toTypedArray()))
  }
}
