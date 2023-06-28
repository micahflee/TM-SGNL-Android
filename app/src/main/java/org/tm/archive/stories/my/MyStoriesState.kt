package org.tm.archive.stories.my

import org.tm.archive.conversation.ConversationMessage
import org.tm.archive.database.model.MessageRecord

data class MyStoriesState(
  val distributionSets: List<DistributionSet> = emptyList()
) {

  data class DistributionSet(
    val label: String?,
    val stories: List<DistributionStory>
  )

  data class DistributionStory(
    val message: ConversationMessage,
    val views: Int
  ) {
    val messageRecord: MessageRecord = message.messageRecord
  }
}
