package org.archiver.data

import android.content.Context
import com.tm.androidcopysdk.api.IArchiveMessageDao
import com.tm.androidcopysdk.api.IMessageStoreObserver
import com.tm.androidcopysdk.model.ArchiveMessage
import org.archiver.ArchiveUtil
import org.archiver.converter.SignalArchiveMessageConverter
import org.archiver.di.TeleMessageApplicationDependencyProvider
import org.signal.core.util.Stopwatch
import org.tm.archive.database.CallTable
import org.tm.archive.database.MessageTable
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.model.MessageId
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.database.model.withCall
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

  private val messageStoreObserver: IMessageStoreObserver<Long> = TeleMessageApplicationDependencyProvider.messageStoreObserver

  private val converter = SignalArchiveMessageConverter(context)

  override fun find(id: Long): ArchiveMessage? {
    val message = getMessageRecordOrNull(id) ?: return null
    val thread = SignalDatabase.threads.getThreadRecord(message.threadId)
    return converter.convert(getMessageRecordOrNull(id), thread, getAccountPhoneNumber())
  }

  override fun findAll(ids: List<Long>): List<ArchiveMessage> {
    val accountPhoneNumber = getAccountPhoneNumber()
    val threads = SignalDatabase.threads
    return getMessages(ids).use { reader -> reader.mapNotNull { converter.convert(it, threads.getThreadRecord(it.threadId), accountPhoneNumber) } }
  }

  fun onSubmitCall(call: CallTable.Call, callInfo: CallInfoState) {
    val message = getMessageRecordOrNull(call.messageId ?: return)?.withCall(call)
    val thread = SignalDatabase.threads.getThreadRecord(message?.threadId)
    val archiveMessage = converter.convertCall(message, thread, getAccountPhoneNumber(), callInfo) ?: return
    messageStoreObserver.afterMessageStateChanged(archiveMessage)
  }

  private fun getAccountPhoneNumber() = ArchiveUtil.getPhoneNumberInTestMode(context)

  // region Call
  // region Call - Insert
  override fun insertCallLog(recipientId: RecipientId, type: Long, timestamp: Long, outgoing: Boolean): InsertResult {
    val result = super.insertCallLog(recipientId, type, timestamp, outgoing)
    messageStoreObserver.afterMessageIdStateChanged(result.messageId)
    return result
  }

  override fun insertGroupCall(groupRecipientId: RecipientId, sender: RecipientId, timestamp: Long, eraId: String,
                               joinedUuids: Collection<UUID>, isCallFull: Boolean): MessageId {
    val result = super.insertGroupCall(groupRecipientId, sender, timestamp, eraId, joinedUuids, isCallFull)
    messageStoreObserver.afterMessageIdStateChanged(result.id)
    return result
  }
  // endregion Call - Insert

  // region Call - Update
  override fun updateCallLog(messageId: Long, type: Long) {
    super.updateCallLog(messageId, type)
    messageStoreObserver.afterMessageIdStateChanged(messageId)
  }

  override fun updateGroupCall(messageId: Long, eraId: String, joinedUuids: Collection<UUID>, isCallFull: Boolean): MessageId {
    val result = super.updateGroupCall(messageId, eraId, joinedUuids, isCallFull)
    val call = SignalDatabase.calls.getCallByMessageId(messageId)
    if (call?.event == CallTable.Event.DECLINED || call?.event == CallTable.Event.MISSED) {
      val message = getMessageRecordOrNull(messageId)?.withCall(call)
      val thread = SignalDatabase.threads.getThreadRecord(message?.threadId)
      val archiveMessage = converter.convertCall(message, thread, getAccountPhoneNumber(), call.event)
      if (archiveMessage != null) {
        messageStoreObserver.afterMessageStateChanged(archiveMessage)
        return result
      }
    }
    messageStoreObserver.afterMessageIdStateChanged(messageId)
    return result
  }
  // endregion Call - Update
  // endregion Call

  // region Message
  // region Message - Insert
  override fun insertMessageInbox(retrieved: IncomingMessage, candidateThreadId: Long, editedMessage: MmsMessageRecord?, notifyObservers: Boolean): Optional<InsertResult> {
    val result = super.insertMessageInbox(retrieved, candidateThreadId, editedMessage, notifyObservers)
    val messageId = result.getOrNull()?.messageId ?: return result
    messageStoreObserver.afterMessageIdStateChanged(messageId)
    return result
  }

  override fun insertMessageOutbox(message: OutgoingMessage, threadId: Long, forceSms: Boolean, defaultReceiptStatus: Int, insertListener: InsertListener?): Long {
    val result = super.insertMessageOutbox(message, threadId, forceSms, defaultReceiptStatus, insertListener)
    messageStoreObserver.afterMessageIdStateChanged(result)
    return result
  }
  // endregion Message - Insert

  // region Message - Update
  override fun markSmsStatus(id: Long, status: Int) {
    super.markSmsStatus(id, status)
    messageStoreObserver.afterMessageIdStateChanged(id)
  }

  override fun setIncomingMessagesViewed(messageIds: List<Long>): List<MarkedMessageInfo> {
    val result = super.setIncomingMessagesViewed(messageIds)
    messageStoreObserver.afterMessageIdsStateChanged(result.map { it.messageId.id })
    return result
  }

  override fun insertEditMessageInbox(mediaMessage: IncomingMessage, targetMessage: MmsMessageRecord): Optional<InsertResult> {
    return super.insertEditMessageInbox(mediaMessage, targetMessage)
  }

  override fun markAsOutbox(messageId: Long) {
    super.markAsOutbox(messageId)
    messageStoreObserver.afterMessageIdStateChanged(messageId)
  }

  override fun markAsSending(messageId: Long) {
    super.markAsSending(messageId)
    messageStoreObserver.afterMessageIdStateChanged(messageId)
  }

  override fun markAsSentFailed(messageId: Long) {
    super.markAsSentFailed(messageId)
    messageStoreObserver.afterMessageIdStateChanged(messageId)
  }

  override fun markAsSent(messageId: Long, secure: Boolean) {
    super.markAsSent(messageId, secure)
    messageStoreObserver.afterMessageIdStateChanged(messageId)
  }

  override fun markDownloadState(messageId: Long, state: Long) {
    super.markDownloadState(messageId, state)
    messageStoreObserver.afterMessageIdStateChanged(messageId)
  }

  override fun setMessagesRead(where: String, arguments: Array<String>?): List<MarkedMessageInfo> {
    val result = super.setMessagesRead(where, arguments)
    messageStoreObserver.afterMessageIdsStateChanged(result.map { it.messageId.id })
    return result
  }

  override fun incrementReceiptCountInternal(targetTimestamp: Long, receiptAuthor: RecipientId, receiptSentTimestamp: Long,
                                             receiptType: ReceiptType, messageQualifier: MessageQualifier, stopwatch: Stopwatch?): Set<MessageReceiptUpdate> {
    val result = super.incrementReceiptCountInternal(targetTimestamp, receiptAuthor, receiptSentTimestamp, receiptType, messageQualifier, stopwatch)
    messageStoreObserver.afterMessageIdsStateChanged(result.map { it.messageId.id })
    return result
  }

  override fun updateTypeBitmask(id: Long, maskOff: Long, maskOn: Long) {
    super.updateTypeBitmask(id, maskOff, maskOn)
    messageStoreObserver.afterMessageIdStateChanged(id)
  }

  override fun markAsRemoteDeleteInternal(messageId: Long) {
    val record = getMessageRecordOrNull(messageId)
    val message = converter.convert(record, record?.threadId?.let(SignalDatabase.threads::getThreadRecord), getAccountPhoneNumber(), isRemoteDeleted = true)
    super.markAsRemoteDeleteInternal(messageId)
    messageStoreObserver.afterMessageStateChanged(message ?: return)
  }
  // endregion Message - Update

  // region Message - Delete
  override fun deleteMessage(messageId: Long, threadId: Long, notify: Boolean, updateThread: Boolean): Boolean {
    val record = getMessageRecordOrNull(messageId)
    val message = converter.convert(record, record?.threadId?.let(SignalDatabase.threads::getThreadRecord), getAccountPhoneNumber(), isDeleted = true)
    val threadDeleted = super.deleteMessage(messageId, threadId, notify, updateThread)
    messageStoreObserver.afterMessageStateChanged(message ?: return threadDeleted)
    return threadDeleted
  }
  // endregion Message - Delete
  // endregion Message
}