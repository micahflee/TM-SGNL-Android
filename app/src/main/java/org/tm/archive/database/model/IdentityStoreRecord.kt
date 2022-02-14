package org.tm.archive.database.model

import org.tm.archive.database.IdentityDatabase
import org.tm.archive.recipients.RecipientId
import org.whispersystems.libsignal.IdentityKey

data class IdentityStoreRecord(
  val addressName: String,
  val identityKey: IdentityKey,
  val verifiedStatus: IdentityDatabase.VerifiedStatus,
  val firstUse: Boolean,
  val timestamp: Long,
  val nonblockingApproval: Boolean
) {
  fun toIdentityRecord(recipientId: RecipientId): IdentityRecord {
    return IdentityRecord(
      recipientId = recipientId,
      identityKey = identityKey,
      verifiedStatus = verifiedStatus,
      firstUse = firstUse,
      timestamp = timestamp,
      nonblockingApproval = nonblockingApproval
    )
  }
}
