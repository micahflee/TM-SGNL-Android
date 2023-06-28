package org.tm.archive.database.model

import org.tm.archive.recipients.RecipientId

/** A model for [org.tm.archive.database.PendingRetryReceiptTable] */
data class PendingRetryReceiptModel(
  val id: Long,
  val author: RecipientId,
  val authorDevice: Int,
  val sentTimestamp: Long,
  val receivedTimestamp: Long,
  val threadId: Long
)
