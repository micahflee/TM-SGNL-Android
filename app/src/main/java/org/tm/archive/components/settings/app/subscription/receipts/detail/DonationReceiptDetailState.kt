package org.tm.archive.components.settings.app.subscription.receipts.detail

import org.tm.archive.database.model.DonationReceiptRecord

data class DonationReceiptDetailState(
  val donationReceiptRecord: DonationReceiptRecord? = null,
  val subscriptionName: String? = null
)
