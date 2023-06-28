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
import org.tm.archive.database.model.databaseprotos.BodyRangeList
import org.tm.archive.mediasend.v2.UntrustedRecords
import org.tm.archive.mms.OutgoingMessage
import org.tm.archive.sms.MessageSender

/**
 * Stateless message sender for Story Group replies and reactions.
 */
object StoryGroupReplySender {

  fun sendReply(context: Context, storyId: Long, body: CharSequence, mentions: List<Mention>, bodyRanges: BodyRangeList?): Completable {
    return sendInternal(
      context = context,
      storyId = storyId,
      body = body,
      mentions = mentions,
      bodyRanges = bodyRanges,
      isReaction = false
    )
  }

  fun sendReaction(context: Context, storyId: Long, emoji: String): Completable {
    return sendInternal(
      context = context,
      storyId = storyId,
      body = emoji,
      mentions = emptyList(),
      bodyRanges = null,
      isReaction = true
    )
  }

  private fun sendInternal(context: Context, storyId: Long, body: CharSequence, mentions: List<Mention>, bodyRanges: BodyRangeList?, isReaction: Boolean): Completable {
    val messageAndRecipient = Single.fromCallable {
      val message = SignalDatabase.messages.getMessageRecord(storyId)
      val recipient = SignalDatabase.threads.getRecipientForThreadId(message.threadId)!!

      message to recipient
    }

    return messageAndRecipient.flatMapCompletable { (message, recipient) ->
      UntrustedRecords.checkForBadIdentityRecords(setOf(ContactSearchKey.RecipientSearchKey(recipient.id, false)), System.currentTimeMillis() - IdentityRecordList.DEFAULT_UNTRUSTED_WINDOW)
        .andThen(
          Completable.create {
            MessageSender.send(
              context,
              OutgoingMessage(
                threadRecipient = recipient,
                body = body.toString(),
                sentTimeMillis = System.currentTimeMillis(),
                parentStoryId = ParentStoryId.GroupReply(message.id),
                isStoryReaction = isReaction,
                mentions = mentions,
                isSecure = true,
                bodyRanges = bodyRanges
              ),
              message.threadId,
              MessageSender.SendType.SIGNAL,
              null
            ) {
              it.onComplete()
            }
          }
        )
    }.subscribeOn(Schedulers.io())
  }
}
