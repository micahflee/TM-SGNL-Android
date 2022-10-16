package org.tm.archive.stories.my

import org.tm.archive.conversation.ConversationMessage

data class MyStoriesState(
  val distributionSets: List<DistributionSet> = emptyList()
) {

  data class DistributionSet(
    val label: String?,
    val stories: List<ConversationMessage>
  )
}
