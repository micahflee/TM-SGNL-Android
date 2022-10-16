package org.tm.archive.mediasend.v2

import androidx.annotation.WorkerThread
import androidx.core.util.Consumer
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.concurrent.SignalExecutors
import org.tm.archive.contacts.paged.ContactSearchKey
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.IdentityRecord
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.recipients.Recipient
import java.util.concurrent.TimeUnit

object UntrustedRecords {

  fun checkForBadIdentityRecords(contactSearchKeys: Set<ContactSearchKey.RecipientSearchKey>, changedSince: Long): Completable {
    return Completable.fromAction {
      val untrustedRecords: List<IdentityRecord> = checkForBadIdentityRecordsSync(contactSearchKeys, changedSince)
      if (untrustedRecords.isNotEmpty()) {
        throw UntrustedRecordsException(untrustedRecords, contactSearchKeys)
      }
    }.subscribeOn(Schedulers.io())
  }

  fun checkForBadIdentityRecords(contactSearchKeys: Set<ContactSearchKey.RecipientSearchKey>, changedSince: Long, consumer: Consumer<List<IdentityRecord>>) {
    SignalExecutors.BOUNDED.execute {
      consumer.accept(checkForBadIdentityRecordsSync(contactSearchKeys, changedSince))
    }
  }

  @WorkerThread
  private fun checkForBadIdentityRecordsSync(contactSearchKeys: Set<ContactSearchKey.RecipientSearchKey>, changedSince: Long): List<IdentityRecord> {
    val recipients: List<Recipient> = contactSearchKeys
      .map { Recipient.resolved(it.recipientId) }
      .map { recipient ->
        when {
          recipient.isGroup -> Recipient.resolvedList(recipient.participantIds)
          recipient.isDistributionList -> Recipient.resolvedList(SignalDatabase.distributionLists.getMembers(recipient.distributionListId.get()))
          else -> listOf(recipient)
        }
      }
      .flatten()

    val calculatedUntrustedWindow = System.currentTimeMillis() - changedSince
    return ApplicationDependencies
      .getProtocolStore()
      .aci()
      .identities()
      .getIdentityRecords(recipients)
      .getUntrustedRecords(calculatedUntrustedWindow.coerceIn(TimeUnit.SECONDS.toMillis(5)..TimeUnit.HOURS.toMillis(1)))
  }

  class UntrustedRecordsException(val untrustedRecords: List<IdentityRecord>, val destinations: Set<ContactSearchKey.RecipientSearchKey>) : Throwable()
}
