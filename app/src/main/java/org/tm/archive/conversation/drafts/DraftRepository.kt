package org.tm.archive.conversation.drafts

import android.content.Context
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.signal.core.util.StreamUtil
import org.signal.core.util.concurrent.SignalExecutors
import org.signal.core.util.logging.Log
import org.tm.archive.components.mention.MentionAnnotation
import org.tm.archive.conversation.ConversationMessage
import org.tm.archive.conversation.ConversationMessage.ConversationMessageFactory
import org.tm.archive.conversation.MessageStyler
import org.tm.archive.database.DraftTable
import org.tm.archive.database.DraftTable.Drafts
import org.tm.archive.database.MentionUtil
import org.tm.archive.database.MessageTypes
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.ThreadTable
import org.tm.archive.database.adjustBodyRanges
import org.tm.archive.database.model.MediaMmsMessageRecord
import org.tm.archive.database.model.Mention
import org.tm.archive.database.model.MessageId
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.database.model.databaseprotos.BodyRangeList
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.mms.PartAuthority
import org.tm.archive.mms.QuoteId
import org.tm.archive.providers.BlobProvider
import org.tm.archive.recipients.Recipient
import org.tm.archive.util.Base64
import org.tm.archive.util.concurrent.SerialMonoLifoExecutor
import org.tm.archive.util.hasTextSlide
import org.tm.archive.util.requireTextSlide
import java.io.IOException
import java.util.concurrent.Executor

class DraftRepository(
  private val context: Context = ApplicationDependencies.getApplication(),
  private val threadTable: ThreadTable = SignalDatabase.threads,
  private val draftTable: DraftTable = SignalDatabase.drafts,
  private val saveDraftsExecutor: Executor = SerialMonoLifoExecutor(SignalExecutors.BOUNDED)
) {

  companion object {
    val TAG = Log.tag(DraftRepository::class.java)
  }

  fun deleteVoiceNoteDraftData(draft: DraftTable.Draft?) {
    if (draft != null) {
      SignalExecutors.BOUNDED.execute {
        BlobProvider.getInstance().delete(context, Uri.parse(draft.value).buildUpon().clearQuery().build())
      }
    }
  }

  fun saveDrafts(recipient: Recipient, threadId: Long, distributionType: Int, drafts: Drafts) {
    saveDraftsExecutor.execute {
      if (drafts.isNotEmpty()) {
        val actualThreadId = if (threadId == -1L) {
          threadTable.getOrCreateThreadIdFor(recipient, distributionType)
        } else {
          threadId
        }

        draftTable.replaceDrafts(actualThreadId, drafts)
        if (drafts.shouldUpdateSnippet()) {
          threadTable.updateSnippet(actualThreadId, drafts.getSnippet(context), drafts.getUriSnippet(), System.currentTimeMillis(), MessageTypes.BASE_DRAFT_TYPE, true)
        } else {
          threadTable.update(actualThreadId, unarchive = false, allowDeletion = false)
        }
      } else if (threadId > 0) {
        draftTable.clearDrafts(threadId)
        threadTable.update(threadId, unarchive = false, allowDeletion = false)
      }
    }
  }

  fun loadDrafts(threadId: Long): Single<DatabaseDraft> {
    return Single.fromCallable {
      val drafts: Drafts = draftTable.getDrafts(threadId)
      val bodyRangesDraft: DraftTable.Draft? = drafts.getDraftOfType(DraftTable.Draft.BODY_RANGES)
      val textDraft: DraftTable.Draft? = drafts.getDraftOfType(DraftTable.Draft.TEXT)
      var updatedText: Spannable? = null

      if (textDraft != null && bodyRangesDraft != null) {
        val bodyRanges: BodyRangeList = BodyRangeList.parseFrom(Base64.decodeOrThrow(bodyRangesDraft.value))
        val mentions: List<Mention> = MentionUtil.bodyRangeListToMentions(bodyRanges)

        val updated = MentionUtil.updateBodyAndMentionsWithDisplayNames(context, textDraft.value, mentions)

        updatedText = SpannableString(updated.body)
        MentionAnnotation.setMentionAnnotations(updatedText, updated.mentions)
        MessageStyler.style(id = MessageStyler.DRAFT_ID, messageRanges = bodyRanges.adjustBodyRanges(updated.bodyAdjustments), span = updatedText, hideSpoilerText = false)
      }

      DatabaseDraft(drafts, updatedText)
    }.subscribeOn(Schedulers.io())
  }

  fun loadDraftQuote(serialized: String): Maybe<ConversationMessage> {
    return Maybe.fromCallable {
      val quoteId: QuoteId = QuoteId.deserialize(context, serialized) ?: return@fromCallable null
      val messageRecord: MessageRecord = SignalDatabase.messages.getMessageFor(quoteId.id, quoteId.author)?.let {
        if (it is MediaMmsMessageRecord) {
          it.withAttachments(context, SignalDatabase.attachments.getAttachmentsForMessage(it.id))
        } else {
          it
        }
      } ?: return@fromCallable null

      val threadRecipient = requireNotNull(SignalDatabase.threads.getRecipientForThreadId(messageRecord.threadId))
      ConversationMessageFactory.createWithUnresolvedData(context, messageRecord, threadRecipient)
    }
  }

  fun loadDraftMessageEdit(serialized: String): Maybe<ConversationMessage> {
    return Maybe.fromCallable {
      val messageId = MessageId.deserialize(serialized)
      val messageRecord: MessageRecord = SignalDatabase.messages.getMessageRecordOrNull(messageId.id) ?: return@fromCallable null
      val threadRecipient: Recipient = requireNotNull(SignalDatabase.threads.getRecipientForThreadId(messageRecord.threadId))
      if (messageRecord.hasTextSlide()) {
        val textSlide = messageRecord.requireTextSlide()
        if (textSlide.uri != null) {
          try {
            PartAuthority.getAttachmentStream(context, textSlide.uri!!).use { stream ->
              val body = StreamUtil.readFullyAsString(stream)
              return@fromCallable ConversationMessageFactory.createWithUnresolvedData(context, messageRecord, body, threadRecipient)
            }
          } catch (e: IOException) {
            Log.e(TAG, "Failed to load text slide", e)
          }
        }
      }
      ConversationMessageFactory.createWithUnresolvedData(context, messageRecord, threadRecipient)
    }
  }

  data class DatabaseDraft(val drafts: Drafts, val updatedText: CharSequence?)
}
