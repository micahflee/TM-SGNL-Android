package org.archiver.data

import android.content.ContentValues
import android.content.Context
import com.tm.androidcopysdk.api.IAndroidCopySdk
import com.tm.androidcopysdk.api.IArchiveMessageDao
import com.tm.androidcopysdk.model.ArchiveMessage
import com.tm.androidcopysdk.model.query.ArchiveMessageFilter
import org.archiver.ArchiveUtil
import org.archiver.converter.SignalArchiveMessageConverter
import org.signal.core.util.Stopwatch
import org.tm.archive.attachments.Attachment
import org.tm.archive.attachments.AttachmentId
import org.tm.archive.contactshare.Contact
import org.tm.archive.database.CallTable
import org.tm.archive.database.MessageTable
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.Mention
import org.tm.archive.database.model.MessageId
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.database.model.databaseprotos.BodyRangeList
import org.tm.archive.database.model.withCall
import org.tm.archive.linkpreview.LinkPreview
import org.tm.archive.mms.IncomingMessage
import org.tm.archive.mms.OutgoingMessage
import org.tm.archive.recipients.RecipientId
import org.tm.archive.service.webrtc.state.CallInfoState
import java.util.Optional
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

class TeleMessageTable(
  context: Context,

  databaseHelper: SignalDatabase,

) : MessageTable(context, databaseHelper), IArchiveMessageDao<Long> {

	private val sdk: IAndroidCopySdk<Long> = IAndroidCopySdk.getInstance()

  private val converter = SignalArchiveMessageConverter(context)

  override suspend fun find(id: Long): ArchiveMessage? {
    val message = getMessageRecordOrNull(id) ?: return null
    val thread = SignalDatabase.threads.getThreadRecord(message.threadId)
    return converter.convert(getMessageRecordOrNull(id), thread, getAccountPhoneNumber())
  }

  override suspend fun findAll(ids: List<Long>): List<ArchiveMessage> {
    val accountPhoneNumber = getAccountPhoneNumber()
    val threads = SignalDatabase.threads
    return getMessages(ids).use { reader -> reader.mapNotNull { converter.convert(it, threads.getThreadRecord(it.threadId), accountPhoneNumber) } }
  }

	override suspend fun findAll(filter: ArchiveMessageFilter<Long>): List<ArchiveMessage> {
		val messageIds = filter.messageIds
		if (messageIds != null)
			return findAll(messageIds)
		val accountPhoneNumber = getAccountPhoneNumber()
		val threads = SignalDatabase.threads
		val afterSelector = ">=".takeIf { filter.afterInclusive } ?: ">"
		val beforeSelector = "<=".takeIf { filter.beforeInclusive } ?: "<"
		val before = filter.before
		var selection = "$TABLE_NAME.$DATE_SENT $afterSelector ${filter.after}"
		if (before != null && before > filter.after)
			selection += " AND $TABLE_NAME.$DATE_SENT $beforeSelector $before"
		rawQueryWithAttachments(selection, null).use { cursor ->
			return MmsReader(cursor).use { reader -> reader.mapNotNull { converter.convert(it, threads.getThreadRecord(it.threadId), accountPhoneNumber) } }
		}
	}

	override suspend fun count(filter: ArchiveMessageFilter<Long>): Int {
		return 0
	}

  fun onSubmitCall(call: CallTable.Call, callInfo: CallInfoState) {
    val message = getMessageRecordOrNull(call.messageId ?: return)?.withCall(call)
    val thread = SignalDatabase.threads.getThreadRecord(message?.threadId)
    val archiveMessage = converter.convertCall(message, thread, getAccountPhoneNumber(), callInfo) ?: return
    sdk.afterMessageStateChanged(archiveMessage)
  }

  private fun getAccountPhoneNumber() = ArchiveUtil.getPhoneNumberInTestMode(context)

  // region Call
  // region Call - Insert
  override fun insertCallLog(recipientId: RecipientId, type: Long, timestamp: Long, outgoing: Boolean): InsertResult {
    val result = super.insertCallLog(recipientId, type, timestamp, outgoing)
    sdk.afterMessageIdStateChanged(result.messageId)
    return result
  }

  override fun insertGroupCall(groupRecipientId: RecipientId, sender: RecipientId, timestamp: Long, eraId: String,
                               joinedUuids: Collection<UUID>, isCallFull: Boolean): MessageId {
    val result = super.insertGroupCall(groupRecipientId, sender, timestamp, eraId, joinedUuids, isCallFull)
    sdk.afterMessageIdStateChanged(result.id)
    return result
  }
  // endregion Call - Insert

  // region Call - Update
  override fun updateCallLog(messageId: Long, type: Long) {
    super.updateCallLog(messageId, type)
    sdk.afterMessageIdStateChanged(messageId)
  }

  override fun updateGroupCall(messageId: Long, eraId: String, joinedUuids: Collection<UUID>, isCallFull: Boolean): MessageId {
    val result = super.updateGroupCall(messageId, eraId, joinedUuids, isCallFull)
    val call = SignalDatabase.calls.getCallByMessageId(messageId)
    if (call?.event == CallTable.Event.DECLINED || call?.event == CallTable.Event.MISSED) {
      val message = getMessageRecordOrNull(messageId)?.withCall(call)
      val thread = SignalDatabase.threads.getThreadRecord(message?.threadId)
      val archiveMessage = converter.convertCall(message, thread, getAccountPhoneNumber(), call.event)
      if (archiveMessage != null) {
        sdk.afterMessageStateChanged(archiveMessage)
        return result
      }
    }
    sdk.afterMessageIdStateChanged(messageId)
    return result
  }
  // endregion Call - Update
  // endregion Call

  // region Message
  // region Message - Insert
  override fun insertMessageInbox(retrieved: IncomingMessage, candidateThreadId: Long, editedMessage: MmsMessageRecord?, notifyObservers: Boolean): Optional<InsertResult> {
    val result = super.insertMessageInbox(retrieved, candidateThreadId, editedMessage, notifyObservers)
    val messageId = result.getOrNull()?.messageId ?: return result
    sdk.afterMessageIdStateChanged(messageId)
    return result
  }

  override fun insertMessageOutbox(message: OutgoingMessage, threadId: Long, forceSms: Boolean, defaultReceiptStatus: Int, insertListener: InsertListener?): Long {
    val result = super.insertMessageOutbox(message, threadId, forceSms, defaultReceiptStatus, insertListener)
    sdk.afterMessageIdStateChanged(result)
    return result
  }

  override fun insertMediaMessage(threadId: Long, body: String?, attachments: List<Attachment>, quoteAttachments: List<Attachment>, sharedContacts: List<Contact>, linkPreviews: List<LinkPreview>, mentions: List<Mention>, messageRanges: BodyRangeList?, contentValues: ContentValues, insertListener: InsertListener?, updateThread: Boolean, unarchive: Boolean): Pair<Long, Map<Attachment, AttachmentId>?> {
    val result = super.insertMediaMessage(threadId, body, attachments, quoteAttachments, sharedContacts, linkPreviews, mentions, messageRanges, contentValues, insertListener, updateThread, unarchive)
		sdk.afterMessageIdStateChanged(result.first)
    return result
  }
  // endregion Message - Insert

  // region Message - Update
  override fun markSmsStatus(id: Long, status: Int) {
    super.markSmsStatus(id, status)
    sdk.afterMessageIdStateChanged(id)
  }

  override fun setIncomingMessagesViewed(messageIds: List<Long>): List<MarkedMessageInfo> {
    val result = super.setIncomingMessagesViewed(messageIds)
    sdk.afterMessageIdsStateChanged(result.map { it.messageId.id })
    return result
  }

  override fun insertEditMessageInbox(mediaMessage: IncomingMessage, targetMessage: MmsMessageRecord): Optional<InsertResult> {
    return super.insertEditMessageInbox(mediaMessage, targetMessage)
  }

  override fun markAsOutbox(messageId: Long) {
    super.markAsOutbox(messageId)
    sdk.afterMessageIdStateChanged(messageId)
  }

  override fun markAsSending(messageId: Long) {
    super.markAsSending(messageId)
    sdk.afterMessageIdStateChanged(messageId)
  }

  override fun markAsSentFailed(messageId: Long) {
    super.markAsSentFailed(messageId)
    sdk.afterMessageIdStateChanged(messageId)
  }

  override fun markAsSent(messageId: Long, secure: Boolean) {
    super.markAsSent(messageId, secure)
    sdk.afterMessageIdStateChanged(messageId)
  }

  override fun markDownloadState(messageId: Long, state: Long) {
    super.markDownloadState(messageId, state)
    sdk.afterMessageIdStateChanged(messageId)
  }

  override fun setMessagesRead(where: String, arguments: Array<String>?): List<MarkedMessageInfo> {
    val result = super.setMessagesRead(where, arguments)
    sdk.afterMessageIdsStateChanged(result.map { it.messageId.id })
    return result
  }

  override fun incrementReceiptCountInternal(targetTimestamp: Long, receiptAuthor: RecipientId, receiptSentTimestamp: Long,
                                             receiptType: ReceiptType, messageQualifier: MessageQualifier, stopwatch: Stopwatch?): Set<MessageReceiptUpdate> {
    val result = super.incrementReceiptCountInternal(targetTimestamp, receiptAuthor, receiptSentTimestamp, receiptType, messageQualifier, stopwatch)
    sdk.afterMessageIdsStateChanged(result.map { it.messageId.id })
    return result
  }

  override fun updateTypeBitmask(id: Long, maskOff: Long, maskOn: Long) {
    super.updateTypeBitmask(id, maskOff, maskOn)
    sdk.afterMessageIdStateChanged(id)
  }

  override fun markAsRemoteDeleteInternal(messageId: Long) {
    val record = getMessageRecordOrNull(messageId)
    val message = converter.convert(record, record?.threadId?.let(SignalDatabase.threads::getThreadRecord), getAccountPhoneNumber(), isRemoteDeleted = true)
    super.markAsRemoteDeleteInternal(messageId)
    sdk.afterMessageStateChanged(message ?: return)
  }
  // endregion Message - Update

  // region Message - Delete
  override fun deleteMessage(messageId: Long, threadId: Long, notify: Boolean, updateThread: Boolean): Boolean {
    val record = getMessageRecordOrNull(messageId)
    val message = converter.convert(record, record?.threadId?.let(SignalDatabase.threads::getThreadRecord), getAccountPhoneNumber(), isDeleted = true)
    val threadDeleted = super.deleteMessage(messageId, threadId, notify, updateThread)
    sdk.afterMessageStateChanged(message ?: return threadDeleted)
    return threadDeleted
  }
  // endregion Message - Delete
  // endregion Message

	companion object {
		private const val RAW_FILTER_WHERE = "$TABLE_NAME.$ID = %s"
	}
}