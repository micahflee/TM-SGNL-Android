package org.tm.archive.stories.tabs

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.logging.Log
import org.tm.archive.database.DatabaseObserver
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.recipients.Recipient

class ConversationListTabRepository {

  companion object {
    private val TAG = Log.tag(ConversationListTabRepository::class.java)
  }

  fun getNumberOfUnreadConversations(): Observable<Long> {
    return Observable.create<Long> {
      fun refresh() {
        it.onNext(SignalDatabase.threads.unreadThreadCount)

        val ids = SignalDatabase.threads.unreadThreadIdList
        Log.d(TAG, "Unread threads: { $ids }")
      }

      val listener = DatabaseObserver.Observer {
        refresh()
      }

      ApplicationDependencies.getDatabaseObserver().registerConversationListObserver(listener)
      it.setCancellable { ApplicationDependencies.getDatabaseObserver().unregisterObserver(listener) }

      refresh()
    }.subscribeOn(Schedulers.io())
  }

  fun getNumberOfUnseenStories(): Observable<Long> {
    return Observable.create<Long> { emitter ->
      fun refresh() {
        emitter.onNext(SignalDatabase.mms.unreadStoryThreadRecipientIds.map { Recipient.resolved(it) }.filterNot { it.shouldHideStory() }.size.toLong())
      }

      val listener = DatabaseObserver.Observer {
        refresh()
      }

      ApplicationDependencies.getDatabaseObserver().registerConversationListObserver(listener)
      emitter.setCancellable { ApplicationDependencies.getDatabaseObserver().unregisterObserver(listener) }
      refresh()
    }.subscribeOn(Schedulers.io())
  }
}
