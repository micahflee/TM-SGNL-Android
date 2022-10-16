package org.tm.archive.stories.settings.story

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tm.archive.database.GroupDatabase
import org.tm.archive.database.SignalDatabase
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId
import org.tm.archive.storage.StorageSyncHelper
import org.tm.archive.stories.Stories

class StoriesPrivacySettingsRepository {
  fun markGroupsAsStories(groups: List<RecipientId>): Completable {
    return Completable.fromCallable {
      SignalDatabase.groups.setShowAsStoryState(groups, GroupDatabase.ShowAsStoryState.ALWAYS)
      SignalDatabase.recipients.markNeedsSync(groups)
      StorageSyncHelper.scheduleSyncForDataChange()
    }
  }

  fun setStoriesEnabled(isEnabled: Boolean): Completable {
    return Completable.fromAction {
      SignalStore.storyValues().isFeatureDisabled = !isEnabled
      Stories.onStorySettingsChanged(Recipient.self().id)
    }.subscribeOn(Schedulers.io())
  }
}
