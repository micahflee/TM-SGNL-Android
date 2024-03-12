package org.archiver.converter

import android.content.Context
import com.tm.androidcopysdk.model.ArchiveMessage
import com.tm.androidcopysdk.model.ArchiveMessageType
import com.tm.androidcopysdk.model.Direction
import com.tm.androidcopysdk.model.Timestamp
import org.archiver.model.Messages.isCallMessage
import org.archiver.model.Messages.isMultimediaMessage
import org.archiver.model.Messages.isSmsMessage
import org.archiver.model.Messages.isStory
import org.archiver.model.Messages.status
import org.tm.archive.database.model.MessageRecord

class SignalArchiveMessageConverter(
  @Deprecated("Converter should never have a context reference")
  private val context: Context
) {

  private val chatConverter = SignalChatConverter(context)
  private val recipientConverter = SignalArchiveRecipientConverter(context)
  private val attachmentConverter = SignalAttachmentConverter()
  private val callInfoConverter = SignalCallInfoConverter()

  fun convert(messages: List<MessageRecord?>, accountPhoneNumber: String?): List<ArchiveMessage> {
    return messages.mapNotNull { convert(it, accountPhoneNumber) }
  }

  fun convert(message: MessageRecord?, accountPhoneNumber: String?, isDeleted: Boolean = false): ArchiveMessage? {
    return convert(message, accountPhoneNumber, isDeleted, null)
  }

  fun convertCall(message: MessageRecord?, accountPhoneNumber: String?, startedAt: Long?): ArchiveMessage? {
    return convert(message, accountPhoneNumber, false, startedAt)
  }

  private fun convert(message: MessageRecord?, accountPhoneNumber: String?, isDeleted: Boolean, startedAt: Long?): ArchiveMessage? {
    if (message == null)
      return null

    val type = getTransportType(message) ?: return null
    val sender = recipientConverter.convertSenderRecipient(message)
    val receivers = recipientConverter.convertReceiverRecipients(message)
    return ArchiveMessage(
      id = message.id.toString(),
      uniqueId = null,
      accountPhoneNumber = accountPhoneNumber,
      type = type,
      direction = if (message.isOutgoing) Direction.Outgoing else Direction.Incoming,
      status = message.status(),
      isDeleted = isDeleted,
      isRemoteDeleted = message.isRemoteDelete,
      isForwarded = false,
      body = message.getDisplayBody(context).toString(),
      timestamp = Timestamp(message.timestamp),
      chat = chatConverter.convert(message),
      sender = sender,
      receivers = receivers,
      attachments = attachmentConverter.convert(message),
      callInfo = listOfNotNull(callInfoConverter.convert(message, type, startedAt)),
      edits = null,
      headers = null
    )
  }

  private fun getTransportType(message: MessageRecord): ArchiveMessageType? {
    if (message.isStory())
      return ArchiveMessageType.Unknown
    if (message.isCallMessage())
      return ArchiveMessageType.Call
    if (message.isUpdate)
      return ArchiveMessageType.Event
    return if (message.isSmsMessage()) ArchiveMessageType.Sms else if (message.isMultimediaMessage()) ArchiveMessageType.Mms else null
  }

}