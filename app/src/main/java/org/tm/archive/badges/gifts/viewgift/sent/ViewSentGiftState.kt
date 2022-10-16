package org.tm.archive.badges.gifts.viewgift.sent

import org.tm.archive.badges.models.Badge
import org.tm.archive.recipients.Recipient

data class ViewSentGiftState(
  val recipient: Recipient? = null,
  val badge: Badge? = null
)
