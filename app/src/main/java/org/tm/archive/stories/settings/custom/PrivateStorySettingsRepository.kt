package org.tm.archive.stories.settings.custom

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.DistributionListId
import org.tm.archive.database.model.DistributionListRecord
import org.tm.archive.recipients.RecipientId
import org.tm.archive.stories.Stories

class PrivateStorySettingsRepository {
  fun getRecord(distributionListId: DistributionListId): Single<DistributionListRecord> {
    return Single.fromCallable {
      SignalDatabase.distributionLists.getList(distributionListId) ?: error("Record does not exist.")
    }.subscribeOn(Schedulers.io())
  }

  fun removeMember(distributionListRecord: DistributionListRecord, member: RecipientId): Completable {
    return Completable.fromAction {
      SignalDatabase.distributionLists.removeMemberFromList(distributionListRecord.id, distributionListRecord.privacyMode, member)
      Stories.onStorySettingsChanged(distributionListRecord.id)
    }.subscribeOn(Schedulers.io())
  }

  fun delete(distributionListId: DistributionListId): Completable {
    return Completable.fromAction {
      SignalDatabase.distributionLists.deleteList(distributionListId)
      Stories.onStorySettingsChanged(distributionListId)
    }.subscribeOn(Schedulers.io())
  }

  fun getRepliesAndReactionsEnabled(distributionListId: DistributionListId): Single<Boolean> {
    return Single.fromCallable {
      SignalDatabase.distributionLists.getStoryType(distributionListId).isStoryWithReplies
    }.subscribeOn(Schedulers.io())
  }

  fun setRepliesAndReactionsEnabled(distributionListId: DistributionListId, repliesAndReactionsEnabled: Boolean): Completable {
    return Completable.fromAction {
      SignalDatabase.distributionLists.setAllowsReplies(distributionListId, repliesAndReactionsEnabled)
      Stories.onStorySettingsChanged(distributionListId)
    }.subscribeOn(Schedulers.io())
  }
}
