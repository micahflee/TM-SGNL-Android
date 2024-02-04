package org.archiver.model

import com.tm.androidcopysdk.Models.ChatType
import com.tm.androidcopysdk.Models.IArchiveType
import com.tm.androidcopysdk.Models.MessageStatus
import org.signal.glide.Log
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.util.isMediaMessage

object Messages {

  fun MessageRecord.printTimestamps() {
    Log.d("DavidLogger", "printTimestamps - " +
      "timestamp: $timestamp, " +
      "receiptTimestamp: $receiptTimestamp, " +
      "serverTimestamp: $serverTimestamp, " +
      "dateSent: $dateSent, " +
      "dateReceived: $dateReceived" +
      "")
  }

  fun MessageRecord.isMultimediaMessage() = isMediaMessage()// || (status() == MessageStatus.Sending && body.isEmpty())

  fun MessageRecord.isSmsMessage() = !isMultimediaMessage() && body.isNotEmpty()

  fun MessageRecord.isGroupMessage() = isGroupV2 || fromRecipient.isGroup || toRecipient.isGroup

  fun MessageRecord.isBroadcastMessage() = isGroupV2 || fromRecipient.isGroup || toRecipient.isGroup

  fun MessageRecord.isIncoming() = !isOutgoing

  fun MessageRecord.chatType() = if (isGroupMessage()) ChatType.Group else if (isBroadcastMessage()) ChatType.Broadcast else ChatType.Chat

  fun MessageRecord.status(): MessageStatus {
    if (isFailed)
      return MessageStatus.Failed
    if (isViewed)
      return MessageStatus.Read
    if (isDelivered || isIncoming())
      return MessageStatus.Delivered
    if (isSent)
      return MessageStatus.Sent
    if (isPending)
      return MessageStatus.Sending
    return MessageStatus.None
  }

  fun MessageRecord.archiveType(): IArchiveType = SignalArchiveType.AppMessage

  fun MessageRecord.chatRecipient() = fromRecipient.takeUnless { isOutgoing } ?: toRecipient

}