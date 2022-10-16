package org.tm.archive.database.model

import org.signal.libsignal.protocol.IdentityKey
import org.tm.archive.database.IdentityDatabase
import org.tm.archive.recipients.RecipientId

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
