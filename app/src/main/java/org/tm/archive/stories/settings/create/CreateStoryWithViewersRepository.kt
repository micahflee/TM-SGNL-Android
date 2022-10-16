package org.tm.archive.stories.settings.create

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tm.archive.database.SignalDatabase
import org.tm.archive.recipients.RecipientId
import org.tm.archive.stories.Stories

class CreateStoryWithViewersRepository {
  fun createList(name: CharSequence, members: Set<RecipientId>): Single<RecipientId> {
    return Single.create<RecipientId> {
      val result = SignalDatabase.distributionLists.createList(name.toString(), members.toList())
      if (result == null) {
        it.onError(Exception("Null result, due to a duplicated name."))
      } else {
        Stories.onStorySettingsChanged(result)
        it.onSuccess(SignalDatabase.recipients.getOrInsertFromDistributionListId(result))
      }
    }.subscribeOn(Schedulers.io())
  }
}
