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
import org.tm.archive.database.CallTable
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.service.webrtc.state.CallInfoState

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

  fun convertCall(message: MessageRecord?, accountPhoneNumber: String?, callInfo: CallInfoState): ArchiveMessage? {
    return convert(message, accountPhoneNumber, false, callInfo)
  }

  private fun convert(message: MessageRecord?, accountPhoneNumber: String?, isDeleted: Boolean, callInfo: CallInfoState?): ArchiveMessage? {
    if (message == null)
      return null

    val type = getMessageType(message)
    val direction = message.getDirection()
    val call = callInfoConverter.convert(message, type, callInfo)
    val sender = recipientConverter.convertSenderRecipient(message, direction)
    val receivers = recipientConverter.convertReceiverRecipients(message, direction)
    return ArchiveMessage(
      id = message.id.toString(),
      uniqueId = null,
      accountPhoneNumber = accountPhoneNumber,
      type = type,
      direction = direction,
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
      callInfo = listOfNotNull(call),
      edits = null,
      headers = null
    )
  }

  private fun MessageRecord.getDirection(): Direction {
    return when ((this as? MmsMessageRecord)?.call?.direction) {
      CallTable.Direction.INCOMING -> Direction.Incoming
      CallTable.Direction.OUTGOING -> Direction.Outgoing
      else -> Direction.Outgoing.takeIf { isOutgoing } ?: Direction.Incoming
    }
  }

  private fun getMessageType(message: MessageRecord): ArchiveMessageType {
    if (message.isStory())
      return ArchiveMessageType.Unknown
    if (message.isCallMessage())
      return ArchiveMessageType.Call
    if (message.isUpdate)
      return ArchiveMessageType.Event
    if (message.isSmsMessage())
      return ArchiveMessageType.Sms
    if (message.isMultimediaMessage())
      return ArchiveMessageType.Mms
    return ArchiveMessageType.Unknown
  }

}