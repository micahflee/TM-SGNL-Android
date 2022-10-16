package org.tm.archive.components.settings.app.internal.donor

import org.signal.donations.StripeDeclineCode
import org.tm.archive.badges.models.Badge
import org.tm.archive.components.settings.app.subscription.errors.UnexpectedSubscriptionCancellation

data class DonorErrorConfigurationState(
  val badges: List<Badge> = emptyList(),
  val selectedBadge: Badge? = null,
  val selectedUnexpectedSubscriptionCancellation: UnexpectedSubscriptionCancellation? = null,
  val selectedStripeDeclineCode: StripeDeclineCode.Code? = null
)
