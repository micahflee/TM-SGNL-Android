/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.database

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.recipients.Recipient
import org.tm.archive.storage.StorageRecordUpdate
import org.tm.archive.storage.StorageSyncModels
import org.tm.archive.testing.SignalActivityRule
import org.tm.archive.testing.assertIs
import org.tm.archive.util.MessageTableTestUtils
import org.whispersystems.signalservice.api.storage.SignalContactRecord
import org.whispersystems.signalservice.internal.storage.protos.ContactRecord

@Suppress("ClassName")
@RunWith(AndroidJUnit4::class)
class RecipientTableTest_applyStorageSyncContactUpdate {
  @get:Rule
  val harness = SignalActivityRule()

  @Test
  fun insertMessageOnVerifiedToDefault() {
    // GIVEN
    val identities = ApplicationDependencies.getProtocolStore().aci().identities()
    val other = Recipient.resolved(harness.others[0])

    MmsHelper.insert(recipient = other)
    identities.setVerified(other.id, harness.othersKeys[0].publicKey, IdentityTable.VerifiedStatus.VERIFIED)

    val oldRecord: SignalContactRecord = StorageSyncModels.localToRemoteRecord(SignalDatabase.recipients.getRecordForSync(harness.others[0])!!).contact.get()

    val newProto = oldRecord
      .toProto()
      .newBuilder()
      .identityState(ContactRecord.IdentityState.DEFAULT)
      .build()
    val newRecord = SignalContactRecord(oldRecord.id, newProto)

    val update = StorageRecordUpdate<SignalContactRecord>(oldRecord, newRecord)

    // WHEN
    val oldVerifiedStatus: IdentityTable.VerifiedStatus = identities.getIdentityRecord(other.id).get().verifiedStatus
    SignalDatabase.recipients.applyStorageSyncContactUpdate(update)
    val newVerifiedStatus: IdentityTable.VerifiedStatus = identities.getIdentityRecord(other.id).get().verifiedStatus

    // THEN
    oldVerifiedStatus assertIs IdentityTable.VerifiedStatus.VERIFIED
    newVerifiedStatus assertIs IdentityTable.VerifiedStatus.DEFAULT

    val messages = MessageTableTestUtils.getMessages(SignalDatabase.threads.getThreadIdFor(other.id)!!)
    messages.first().isIdentityDefault assertIs true
  }
}
