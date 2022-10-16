package org.tm.archive.stories.viewer

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tm.archive.database.MessageDatabase
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.DistributionListId
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId

/**
 * Open for testing
 */
open class StoryViewerRepository {
  fun getFirstStory(recipientId: RecipientId, storyId: Long): Single<MmsMessageRecord> {
    return if (storyId > 0) {
      Single.fromCallable {
        SignalDatabase.mms.getMessageRecord(storyId) as MmsMessageRecord
      }
    } else {
      Single.fromCallable {
        val recipient = Recipient.resolved(recipientId)
        val reader: MessageDatabase.Reader = if (recipient.isMyStory || recipient.isSelf) {
          SignalDatabase.mms.getAllOutgoingStories(false, 1)
        } else {
          val unread = SignalDatabase.mms.getUnreadStories(recipientId, 1)
          if (unread.iterator().hasNext()) {
            unread
          } else {
            SignalDatabase.mms.getAllStoriesFor(recipientId, 1)
          }
        }
        reader.use { it.iterator().next() } as MmsMessageRecord
      }
    }
  }

  fun getStories(hiddenStories: Boolean, isOutgoingOnly: Boolean): Single<List<RecipientId>> {
    return Single.create<List<RecipientId>> { emitter ->
      val myStoriesId = SignalDatabase.recipients.getOrInsertFromDistributionListId(DistributionListId.MY_STORY)
      val myStories = Recipient.resolved(myStoriesId)
      val releaseChannelId = SignalStore.releaseChannelValues().releaseChannelRecipientId
      val recipientIds = SignalDatabase.mms.getOrderedStoryRecipientsAndIds(isOutgoingOnly).groupBy {
        val recipient = Recipient.resolved(it.recipientId)
        if (recipient.isDistributionList) {
          myStories
        } else {
          recipient
        }
      }.keys.filter {
        if (hiddenStories) {
          it.shouldHideStory()
        } else {
          !it.shouldHideStory()
        }
      }.map { it.id }

      emitter.onSuccess(
        recipientIds.floatToTop(releaseChannelId).floatToTop(myStoriesId)
      )
    }.subscribeOn(Schedulers.io())
  }

  private fun List<RecipientId>.floatToTop(recipientId: RecipientId?): List<RecipientId> {
    return if (recipientId != null && contains(recipientId)) {
      listOf(recipientId) + (this - recipientId)
    } else {
      this
    }
  }
}
