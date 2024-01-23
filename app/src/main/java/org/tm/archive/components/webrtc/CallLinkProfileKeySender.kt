/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.components.webrtc

import org.signal.core.util.logging.Log
import org.tm.archive.database.identity.IdentityRecordList
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobs.ProfileKeySendJob
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId

/**
 * In-Memory Cache that keeps track of whom we've sent profile keys to. This is
 * something that really only needs to happen once so that profile information is
 * displayed correctly, so we maintain an application process scoped cache.
 */
object CallLinkProfileKeySender {
  private val TAG = Log.tag(CallLinkProfileKeySender::class.java)

  private val cache = hashSetOf<RecipientId>()

  /**
   * Invoked after pressing "Join" or "Continue" on the safety number change dialog before
   * or during a call.
   */
  @JvmStatic
  fun onSendAnyway(recipientIds: Set<RecipientId>) {
    val toSendMessagesTo: Set<RecipientId> = recipientIds - cache
    cache += recipientIds

    if (toSendMessagesTo.isNotEmpty()) {
      Log.i(TAG, "Sending profile key to $toSendMessagesTo users.")
      val job = ProfileKeySendJob.createForCallLinks(toSendMessagesTo.toList())
      ApplicationDependencies.getJobManager().add(job)
    } else {
      Log.i(TAG, "No users to send profile key to.")
    }
  }

  /**
   * Given the set of recipients, for each unblocked recipient we don't distrust, send a NullMessage
   */
  fun onRecipientsUpdated(recipients: Set<Recipient>) {
    val nonBlockedNonSelfRecipients: List<Recipient> = recipients.filterNot { it.isBlocked || it.isSelf }

    val identityRecords: IdentityRecordList = ApplicationDependencies
      .getProtocolStore()
      .aci()
      .identities()
      .getIdentityRecords(nonBlockedNonSelfRecipients)

    val untrustedAndUnverifiedRecipients = if (identityRecords.isUntrusted(false) || identityRecords.isUnverified(false)) {
      (identityRecords.untrustedRecipients + identityRecords.unverifiedRecipients).toSet()
    } else {
      emptySet()
    }

    val trustedRecipients: Set<RecipientId> = (nonBlockedNonSelfRecipients - untrustedAndUnverifiedRecipients).map { it.id }.toSet()

    onSendAnyway(trustedRecipients)
  }
}
