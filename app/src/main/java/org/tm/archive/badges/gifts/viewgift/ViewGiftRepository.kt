package org.tm.archive.badges.gifts.viewgift

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.libsignal.zkgroup.receipts.ReceiptCredentialPresentation
import org.tm.archive.badges.Badges
import org.tm.archive.badges.models.Badge
import org.tm.archive.database.DatabaseObserver
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.database.model.databaseprotos.GiftBadge
import org.tm.archive.dependencies.ApplicationDependencies
import java.util.Locale

/**
 * Shared repository for getting information about a particular gift.
 */
class ViewGiftRepository {
  fun getBadge(giftBadge: GiftBadge): Single<Badge> {
    val presentation = ReceiptCredentialPresentation(giftBadge.redemptionToken.toByteArray())
    return Single
      .fromCallable {
        ApplicationDependencies
          .getDonationsService()
          .getGiftBadge(Locale.getDefault(), presentation.receiptLevel)
      }
      .flatMap { it.flattenResult() }
      .map { Badges.fromServiceBadge(it) }
      .subscribeOn(Schedulers.io())
  }

  fun getGiftBadge(messageId: Long): Observable<GiftBadge> {
    return Observable.create { emitter ->
      fun refresh() {
        val record = SignalDatabase.mms.getMessageRecord(messageId)
        val giftBadge: GiftBadge = (record as MmsMessageRecord).giftBadge!!

        emitter.onNext(giftBadge)
      }

      val messageObserver = DatabaseObserver.MessageObserver {
        if (it.mms && messageId == it.id) {
          refresh()
        }
      }

      ApplicationDependencies.getDatabaseObserver().registerMessageUpdateObserver(messageObserver)
      emitter.setCancellable {
        ApplicationDependencies.getDatabaseObserver().unregisterObserver(messageObserver)
      }

      refresh()
    }
  }
}
