package org.tm.archive.stories.settings.select

import org.tm.archive.database.model.DistributionListId
import org.tm.archive.database.model.DistributionListRecord
import org.tm.archive.recipients.RecipientId

data class BaseStoryRecipientSelectionState(
  val distributionListId: DistributionListId?,
  val privateStory: DistributionListRecord? = null,
  val selection: Set<RecipientId> = emptySet()
)
