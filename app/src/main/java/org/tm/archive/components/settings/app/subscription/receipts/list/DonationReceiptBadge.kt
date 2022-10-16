package org.tm.archive.components.settings.app.subscription.receipts.list

import org.tm.archive.badges.models.Badge
import org.tm.archive.database.model.DonationReceiptRecord

data class DonationReceiptBadge(
  val type: DonationReceiptRecord.Type,
  val level: Int,
  val badge: Badge
)
