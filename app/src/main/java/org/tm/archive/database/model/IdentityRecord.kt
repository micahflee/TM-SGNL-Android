package org.tm.archive.database.model

import org.tm.archive.database.IdentityDatabase
import org.tm.archive.recipients.RecipientId
import org.whispersystems.libsignal.IdentityKey

data class IdentityRecord(
  val recipientId: RecipientId,
  val identityKey: IdentityKey,
  val verifiedStatus: IdentityDatabase.VerifiedStatus,
  @get:JvmName("isFirstUse")
  val firstUse: Boolean,
  val timestamp: Long,
  @get:JvmName("isApprovedNonBlocking")
  val nonblockingApproval: Boolean
)
