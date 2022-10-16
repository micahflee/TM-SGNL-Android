package org.tm.archive.components.settings.app.subscription.receipts.list

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tm.archive.badges.Badges
import org.tm.archive.database.model.DonationReceiptRecord
import org.tm.archive.dependencies.ApplicationDependencies
import java.util.Locale

class DonationReceiptListRepository {
  fun getBadges(): Single<List<DonationReceiptBadge>> {
    val boostBadges: Single<List<DonationReceiptBadge>> = Single
      .fromCallable {
        ApplicationDependencies.getDonationsService()
          .getBoostBadge(Locale.getDefault())
      }
      .map { response ->
        if (response.result.isPresent) {
          listOf(DonationReceiptBadge(DonationReceiptRecord.Type.BOOST, -1, Badges.fromServiceBadge(response.result.get())))
        } else {
          emptyList()
        }
      }

    val subBadges: Single<List<DonationReceiptBadge>> = Single
      .fromCallable { ApplicationDependencies.getDonationsService().getSubscriptionLevels(Locale.getDefault()) }
      .map { response ->
        if (response.result.isPresent) {
          response.result.get().levels.map {
            DonationReceiptBadge(
              level = it.key.toInt(),
              badge = Badges.fromServiceBadge(it.value.badge),
              type = DonationReceiptRecord.Type.RECURRING
            )
          }
        } else {
          emptyList()
        }
      }

    return boostBadges.zipWith(subBadges) { a, b -> a + b }.subscribeOn(Schedulers.io())
  }
}
