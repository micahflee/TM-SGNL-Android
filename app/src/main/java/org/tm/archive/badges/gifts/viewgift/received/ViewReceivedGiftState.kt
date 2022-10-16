package org.tm.archive.badges.gifts.viewgift.received

import org.tm.archive.badges.models.Badge
import org.tm.archive.database.model.databaseprotos.GiftBadge
import org.tm.archive.recipients.Recipient

data class ViewReceivedGiftState(
  val recipient: Recipient? = null,
  val giftBadge: GiftBadge? = null,
  val badge: Badge? = null,
  val controlState: ControlState? = null,
  val hasOtherBadges: Boolean = false,
  val displayingOtherBadges: Boolean = false,
  val userCheckSelection: Boolean? = false,
  val redemptionState: RedemptionState = RedemptionState.NONE
) {

  fun getControlChecked(): Boolean {
    return when {
      userCheckSelection != null -> userCheckSelection
      controlState == ControlState.FEATURE -> false
      !displayingOtherBadges -> false
      else -> true
    }
  }

  enum class ControlState {
    DISPLAY,
    FEATURE
  }

  enum class RedemptionState {
    NONE,
    IN_PROGRESS
  }
}
