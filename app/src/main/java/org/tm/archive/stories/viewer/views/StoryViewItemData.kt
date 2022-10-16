package org.tm.archive.stories.viewer.views

import org.tm.archive.recipients.Recipient

data class StoryViewItemData(
  val recipient: Recipient,
  val timeViewedInMillis: Long
)
