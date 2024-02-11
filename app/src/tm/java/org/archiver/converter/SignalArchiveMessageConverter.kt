package org.archiver.converter

import android.content.Context
import com.tm.androidcopysdk.model.ArchiveMessage
import com.tm.androidcopysdk.model.ArchiveMessageType
import com.tm.androidcopysdk.model.Direction
import com.tm.androidcopysdk.model.Timestamp
import org.archiver.model.Messages.archiveType
import org.archiver.model.Messages.isMultimediaMessage
import org.archiver.model.Messages.isSmsMessage
import org.archiver.model.Messages.status
import org.tm.archive.database.CallTable
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.database.model.MmsMessageRecord

class SignalArchiveMessageConverter(
  @Deprecated("Converter should never have a context reference")
  private val context: Context
) {

  private val chatConverter = SignalChatConverter(context)
  private val recipientConverter = SignalArchiveRecipientConverter(context)
  private val attachmentConverter = SignalAttachmentConverter()

  fun convert(messages: List<MessageRecord?>): List<ArchiveMessage> {
    return messages.mapNotNull(this::convert)
  }

  fun convert(message: MessageRecord?, isDeleted: Boolean = false): ArchiveMessage? {
    if (message == null)
      return null

    val type = getTransportType(message) ?: return null
    if (type == ArchiveMessageType.Call)
      return (message as MmsMessageRecord).let { convert(it, requireNotNull(it.call)) }
    val sender = recipientConverter.convertSenderRecipient(message)
    val receivers = recipientConverter.convertReceiverRecipients(message)
    return ArchiveMessage(
      id = message.id.toString(),
      uniqueId = null,
      type = type,
      direction = if (message.isOutgoing) Direction.Outgoing else Direction.Incoming,
      archiveType = message.archiveType(),
      status = message.status(),
      isDeleted = isDeleted,
      isRemoteDeleted = message.isRemoteDelete,
      body = message.getDisplayBody(context).toString(),
      timestamp = Timestamp(message.timestamp),
      chat = chatConverter.convert(message),
      sender = sender,
      receivers = receivers,
      attachments = attachmentConverter.convert((message as? MmsMessageRecord)?.slideDeck),
      edits = null,
      headers = null
    )
  }

  private fun getTransportType(message: MessageRecord): ArchiveMessageType? {
    if (message is MmsMessageRecord && message.call != null)
      return ArchiveMessageType.Call
    return if (message.isSmsMessage()) ArchiveMessageType.Sms else if (message.isMultimediaMessage()) ArchiveMessageType.Mms else null
  }

  private fun convert(message: MmsMessageRecord, call: CallTable.Call): ArchiveMessage {
    val sender = recipientConverter.convertSenderRecipient(message)
    val receivers = recipientConverter.convertReceiverRecipients(message)
    return ArchiveMessage(
      id = message.id.toString(),
      uniqueId = null,
      type = ArchiveMessageType.Call,
      direction = if (message.isOutgoing) Direction.Outgoing else Direction.Incoming,
      archiveType = message.archiveType(),
      status = message.status(),
      isDeleted = false,
      isRemoteDeleted = message.isRemoteDelete,
      body = message.getDisplayBody(context).toString(),
      timestamp = Timestamp(message.timestamp),
      chat = chatConverter.convert(message),
      sender = sender,
      receivers = receivers,
      attachments = attachmentConverter.convert((message as? MmsMessageRecord)?.slideDeck),
      edits = null,
      headers = null
    )
  }

}