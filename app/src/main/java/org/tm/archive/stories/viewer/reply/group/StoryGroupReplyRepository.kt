package org.tm.archive.stories.viewer.reply.group

import androidx.lifecycle.Observer
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.paging.ObservablePagedData
import org.signal.paging.PagedData
import org.signal.paging.PagingConfig
import org.signal.paging.PagingController
import org.tm.archive.conversation.colors.GroupAuthorNameColorHelper
import org.tm.archive.conversation.colors.NameColor
import org.tm.archive.database.DatabaseObserver
import org.tm.archive.database.NoSuchMessageException
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.MessageId
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.recipients.RecipientId

class StoryGroupReplyRepository {

  fun getThreadId(storyId: Long): Single<Long> {
    return Single.fromCallable {
      SignalDatabase.messages.getThreadIdForMessage(storyId)
    }.subscribeOn(Schedulers.io())
  }

  fun getPagedReplies(parentStoryId: Long): Observable<ObservablePagedData<MessageId, ReplyBody>> {
    return getThreadId(parentStoryId)
      .toObservable()
      .flatMap { threadId ->
        Observable.create<ObservablePagedData<MessageId, ReplyBody>> { emitter ->
          val pagedData: ObservablePagedData<MessageId, ReplyBody> = PagedData.createForObservable(StoryGroupReplyDataSource(parentStoryId), PagingConfig.Builder().build())
          val controller: PagingController<MessageId> = pagedData.controller

          val updateObserver = DatabaseObserver.MessageObserver { controller.onDataItemChanged(it) }
          val insertObserver = DatabaseObserver.MessageObserver { controller.onDataItemInserted(it, PagingController.POSITION_END) }
          val conversationObserver = DatabaseObserver.Observer { controller.onDataInvalidated() }

          ApplicationDependencies.getDatabaseObserver().registerMessageUpdateObserver(updateObserver)
          ApplicationDependencies.getDatabaseObserver().registerMessageInsertObserver(threadId, insertObserver)
          ApplicationDependencies.getDatabaseObserver().registerConversationObserver(threadId, conversationObserver)

          emitter.setCancellable {
            ApplicationDependencies.getDatabaseObserver().unregisterObserver(updateObserver)
            ApplicationDependencies.getDatabaseObserver().unregisterObserver(insertObserver)
            ApplicationDependencies.getDatabaseObserver().unregisterObserver(conversationObserver)
          }

          emitter.onNext(pagedData)
        }.subscribeOn(Schedulers.io())
      }
  }

  fun getNameColorsMap(storyId: Long): Observable<Map<RecipientId, NameColor>> {
    return Single
      .fromCallable {
        try {
          val messageRecord = SignalDatabase.messages.getMessageRecord(storyId)
          val groupId = messageRecord.toRecipient.groupId.or { messageRecord.fromRecipient.groupId }
          if (groupId.isPresent) {
            GroupAuthorNameColorHelper().getColorMap(groupId.get())
          } else {
            emptyMap()
          }
        } catch (e: NoSuchMessageException) {
          emptyMap()
        }
      }
      .toObservable()
  }
}
