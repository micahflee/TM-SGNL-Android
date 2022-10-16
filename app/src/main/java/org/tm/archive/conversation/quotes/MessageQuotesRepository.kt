package org.tm.archive.conversation.quotes

import android.app.Application
import androidx.annotation.WorkerThread
import io.reactivex.rxjava3.core.Observable
import org.signal.core.util.logging.Log
import org.tm.archive.conversation.ConversationDataSource
import org.tm.archive.conversation.ConversationMessage
import org.tm.archive.conversation.ConversationMessage.ConversationMessageFactory
import org.tm.archive.database.DatabaseObserver
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.MediaMmsMessageRecord
import org.tm.archive.database.model.MessageId
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.database.model.Quote
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.util.getQuote

class MessageQuotesRepository {

  companion object {
    private val TAG = Log.tag(MessageQuotesRepository::class.java)
  }

  /**
   * Retrieves all messages that quote the target message, as well as any messages that quote _those_ messages, recursively.
   */
  fun getMessagesInQuoteChain(application: Application, messageId: MessageId): Observable<List<ConversationMessage>> {
    return Observable.create { emitter ->
      val threadId: Long = SignalDatabase.mmsSms.getThreadId(messageId)
      if (threadId < 0) {
        Log.w(TAG, "Could not find a threadId for $messageId!")
        emitter.onNext(emptyList())
        return@create
      }

      val databaseObserver: DatabaseObserver = ApplicationDependencies.getDatabaseObserver()
      val observer = DatabaseObserver.Observer { emitter.onNext(getMessageInQuoteChainSync(application, messageId)) }

      databaseObserver.registerConversationObserver(threadId, observer)

      emitter.setCancellable { databaseObserver.unregisterObserver(observer) }
      emitter.onNext(getMessageInQuoteChainSync(application, messageId))
    }
  }

  @WorkerThread
  private fun getMessageInQuoteChainSync(application: Application, messageId: MessageId): List<ConversationMessage> {
    val originalRecord: MessageRecord? = if (messageId.mms) {
      SignalDatabase.mms.getMessageRecordOrNull(messageId.id)
    } else {
      SignalDatabase.sms.getMessageRecordOrNull(messageId.id)
    }

    if (originalRecord == null) {
      return emptyList()
    }

    val replyRecords: List<MessageRecord> = SignalDatabase.mmsSms.getAllMessagesThatQuote(messageId)

    val replies: List<ConversationMessage> = ConversationDataSource.ReactionHelper()
      .apply {
        addAll(replyRecords)
        fetchReactions()
      }
      .buildUpdatedModels(replyRecords)
      .map { replyRecord ->
        val replyQuote: Quote? = replyRecord.getQuote()
        if (replyQuote != null && replyQuote.id == originalRecord.dateSent) {
          (replyRecord as MediaMmsMessageRecord).withoutQuote()
        } else {
          replyRecord
        }
      }
      .map { ConversationMessageFactory.createWithUnresolvedData(application, it) }

    val originalMessage: List<ConversationMessage> = ConversationDataSource.ReactionHelper()
      .apply {
        add(originalRecord)
        fetchReactions()
      }
      .buildUpdatedModels(listOf(originalRecord))
      .map { ConversationMessageFactory.createWithUnresolvedData(application, it, it.getDisplayBody(application), false) }

    return replies + originalMessage
  }
}
