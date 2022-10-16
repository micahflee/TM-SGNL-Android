package org.tm.archive.components.settings.app.subscription.receipts.list

import org.tm.archive.database.model.DonationReceiptRecord

data class DonationReceiptListPageState(
  val records: List<DonationReceiptRecord> = emptyList(),
  val isLoaded: Boolean = false
)
