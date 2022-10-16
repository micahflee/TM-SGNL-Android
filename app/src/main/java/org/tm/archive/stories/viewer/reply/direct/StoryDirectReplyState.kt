package org.tm.archive.stories.viewer.reply.direct

import org.tm.archive.database.model.MessageRecord
import org.tm.archive.recipients.Recipient

data class StoryDirectReplyState(
  val groupDirectReplyRecipient: Recipient? = null,
  val storyRecord: MessageRecord? = null
)
