/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.recipients.ui.about

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tm.archive.database.IdentityTable
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.recipients.RecipientId

class AboutSheetRepository {
  fun getGroupsInCommonCount(recipientId: RecipientId): Single<Int> {
    return Single.fromCallable {
      SignalDatabase.groups.getPushGroupsContainingMember(recipientId).size
    }.subscribeOn(Schedulers.io())
  }

  fun getVerified(recipientId: RecipientId): Single<Boolean> {
    return Single.fromCallable {
      val identityRecord = ApplicationDependencies.getProtocolStore().aci().identities().getIdentityRecord(recipientId)
      identityRecord.isPresent && identityRecord.get().verifiedStatus == IdentityTable.VerifiedStatus.VERIFIED
    }.subscribeOn(Schedulers.io())
  }
}
