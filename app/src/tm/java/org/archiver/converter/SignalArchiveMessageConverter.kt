package org.archiver.converter

import android.content.Context
import com.tm.androidcopysdk.model.ArchiveMessage
import com.tm.androidcopysdk.model.ArchiveMessageType
import com.tm.androidcopysdk.model.Direction
import com.tm.androidcopysdk.model.Timestamp
import com.tm.model.SecretProperty
import org.archiver.model.Messages.isCallMessage
import org.archiver.model.Messages.isMultimediaMessage
import org.archiver.model.Messages.isSmsMessage
import org.archiver.model.Messages.isStory
import org.archiver.model.Messages.status
import org.tm.archive.database.CallTable
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.database.model.ThreadRecord
import org.tm.archive.service.webrtc.state.CallInfoState

class SignalArchiveMessageConverter(
  @Deprecated("Converter should never have a context reference")
  private val context: Context
) {

  private val chatConverter = SignalChatConverter(context)
  private val recipientConverter = SignalArchiveRecipientConverter(context)
  private val attachmentConverter = SignalAttachmentConverter()
  private val callInfoConverter = SignalCallInfoConverter()

  fun convert(messages: List<MessageRecord?>, thread: ThreadRecord?, accountPhoneNumber: String?): List<ArchiveMessage> {
    return messages.mapNotNull { convert(it, thread, accountPhoneNumber) }
  }

  fun convert(message: MessageRecord?, thread: ThreadRecord?, accountPhoneNumber: String?, isDeleted: Boolean = false, isRemoteDeleted: Boolean = false): ArchiveMessage? {
    return convert(message, thread, accountPhoneNumber, isDeleted, isRemoteDeleted, null, null)
  }

  fun convertCall(message: MessageRecord?, thread: ThreadRecord?, accountPhoneNumber: String?, event: CallTable.Event): ArchiveMessage? {
    return convert(message, thread, accountPhoneNumber, isDeleted = false, isRemoteDeleted = false,callInfo = null, event = event)
  }

  fun convertCall(message: MessageRecord?, thread: ThreadRecord?, accountPhoneNumber: String?, callInfo: CallInfoState): ArchiveMessage? {
    return convert(message, thread, accountPhoneNumber, isDeleted = false, isRemoteDeleted = false, callInfo = callInfo, event = null)
  }

  private fun convert(message: MessageRecord?, thread: ThreadRecord?, accountPhoneNumber: String?, isDeleted: Boolean,
                      isRemoteDeleted: Boolean, callInfo: CallInfoState?, event: CallTable.Event?): ArchiveMessage? {
    if (message == null)
      return null

    val type = getMessageType(message)
    val direction = message.getDirection()
    val chat = chatConverter.convert(message, thread)
    val call = callInfoConverter.convert(message, type, callInfo = callInfo, event)
    val sender = recipientConverter.convertSenderRecipient(message, chat, direction)
    val receivers = recipientConverter.convertReceiverRecipients(message, thread, chat, sender, direction)
    return ArchiveMessage(
      id = message.id.toString(),
      uniqueId = null,
      accountPhoneNumber = accountPhoneNumber,
      type = type,
      direction = direction,
      status = message.status(),
      isDeleted = isDeleted,
      isRemoteDeleted = isRemoteDeleted,
      isForwarded = false,
      body = SecretProperty(message.getDisplayBody(context).toString()),
      timestamp = Timestamp(message.timestamp),
      chat = chat,
      sender = sender,
      receivers = receivers,
      attachments = attachmentConverter.convert(message),
      callInfo = listOfNotNull(call),
      edits = null)
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