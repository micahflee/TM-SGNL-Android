package org.tm.archive.reactions

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tm.archive.components.emoji.EmojiUtil
import org.tm.archive.database.DatabaseObserver
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.MessageId
import org.tm.archive.database.model.ReactionRecord
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.recipients.Recipient

class ReactionsRepository {

  fun getReactions(messageId: MessageId): Observable<List<ReactionDetails>> {
    return Observable.create { emitter: ObservableEmitter<List<ReactionDetails>> ->
      val databaseObserver: DatabaseObserver = ApplicationDependencies.getDatabaseObserver()

      val messageObserver = DatabaseObserver.MessageObserver { reactionMessageId ->
        if (reactionMessageId == messageId) {
          emitter.onNext(fetchReactionDetails(reactionMessageId))
        }
      }

      databaseObserver.registerMessageUpdateObserver(messageObserver)

      emitter.setCancellable {
        databaseObserver.unregisterObserver(messageObserver)
      }

      emitter.onNext(fetchReactionDetails(messageId))
    }.subscribeOn(Schedulers.io())
  }

  private fun fetchReactionDetails(messageId: MessageId): List<ReactionDetails> {
    val reactions: List<ReactionRecord> = SignalDatabase.reactions.getReactions(messageId)

    return reactions.map { reaction ->
      ReactionDetails(
        sender = Recipient.resolved(reaction.author),
        baseEmoji = EmojiUtil.getCanonicalRepresentation(reaction.emoji),
        displayEmoji = reaction.emoji,
        timestamp = reaction.dateReceived
      )
    }
  }
}
