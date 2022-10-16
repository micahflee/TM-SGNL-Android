package org.tm.archive.stories.viewer.reply.group

import android.content.Context
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.tm.archive.contacts.paged.ContactSearchKey
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.identity.IdentityRecordList
import org.tm.archive.database.model.Mention
import org.tm.archive.database.model.ParentStoryId
import org.tm.archive.database.model.StoryType
import org.tm.archive.mediasend.v2.UntrustedRecords
import org.tm.archive.mms.OutgoingMediaMessage
import org.tm.archive.mms.OutgoingSecureMediaMessage
import org.tm.archive.sms.MessageSender

/**
 * Stateless message sender for Story Group replies and reactions.
 */
object StoryGroupReplySender {

  fun sendReply(context: Context, storyId: Long, body: CharSequence, mentions: List<Mention>): Completable {
    return sendInternal(context, storyId, body, mentions, false)
  }

  fun sendReaction(context: Context, storyId: Long, emoji: String): Completable {
    return sendInternal(context, storyId, emoji, emptyList(), true)
  }

  private fun sendInternal(context: Context, storyId: Long, body: CharSequence, mentions: List<Mention>, isReaction: Boolean): Completable {
    val messageAndRecipient = Single.fromCallable {
      val message = SignalDatabase.mms.getMessageRecord(storyId)
      val recipient = SignalDatabase.threads.getRecipientForThreadId(message.threadId)!!

      message to recipient
    }

    return messageAndRecipient.flatMapCompletable { (message, recipient) ->
      UntrustedRecords.checkForBadIdentityRecords(setOf(ContactSearchKey.RecipientSearchKey.KnownRecipient(recipient.id)), System.currentTimeMillis() - IdentityRecordList.DEFAULT_UNTRUSTED_WINDOW)
        .andThen(
          Completable.create {
            MessageSender.send(
              context,
              OutgoingSecureMediaMessage(
                OutgoingMediaMessage(
                  recipient,
                  body.toString(),
                  emptyList(),
                  System.currentTimeMillis(),
                  0,
                  0L,
                  false,
                  0,
                  StoryType.NONE,
                  ParentStoryId.GroupReply(message.id),
                  isReaction,
                  null,
                  emptyList(),
                  emptyList(),
                  mentions,
                  emptySet(),
                  emptySet(),
                  null
                )
              ),
              message.threadId,
              false,
              null
            ) {
              it.onComplete()
            }
          }
        )
    }.subscribeOn(Schedulers.io())
  }
}
