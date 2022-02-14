package org.tm.archive.badges

import android.content.Context
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tm.archive.badges.models.Badge
import org.tm.archive.database.RecipientDatabase
import org.tm.archive.database.SignalDatabase
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.storage.StorageSyncHelper
import org.tm.archive.util.ProfileUtil

class BadgeRepository(context: Context) {

  private val context = context.applicationContext

  fun setVisibilityForAllBadges(
    displayBadgesOnProfile: Boolean,
    selfBadges: List<Badge> = Recipient.self().badges
  ): Completable = Completable.fromAction {
    val recipientDatabase: RecipientDatabase = SignalDatabase.recipients
    val badges = selfBadges.map { it.copy(visible = displayBadgesOnProfile) }

    ProfileUtil.uploadProfileWithBadges(context, badges)
    SignalStore.donationsValues().setDisplayBadgesOnProfile(displayBadgesOnProfile)
    recipientDatabase.markNeedsSync(Recipient.self().id)
    StorageSyncHelper.scheduleSyncForDataChange()

    recipientDatabase.setBadges(Recipient.self().id, badges)
  }.subscribeOn(Schedulers.io())

  fun setFeaturedBadge(featuredBadge: Badge): Completable = Completable.fromAction {
    val badges = Recipient.self().badges
    val reOrderedBadges = listOf(featuredBadge.copy(visible = true)) + (badges.filterNot { it.id == featuredBadge.id })
    ProfileUtil.uploadProfileWithBadges(context, reOrderedBadges)

    val recipientDatabase: RecipientDatabase = SignalDatabase.recipients
    recipientDatabase.setBadges(Recipient.self().id, reOrderedBadges)
  }.subscribeOn(Schedulers.io())
}
