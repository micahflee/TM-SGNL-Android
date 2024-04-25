package org.archiver.converter

import android.content.Context
import com.tm.androidcopysdk.model.ArchiveChat
import com.tm.androidcopysdk.model.ArchiveRecipient
import com.tm.androidcopysdk.model.ChatType
import com.tm.androidcopysdk.model.Direction
import com.tm.androidcopysdk.utils.PrefManager
import org.archiver.ArchivePreferenceConstants
import org.archiver.ArchiveSender
import org.archiver.model.Messages.isGroupMessage
import org.archiver.model.Recipients.toParticipant
import org.archiver.model.Recipients.toParticipants
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.database.model.ThreadRecord
import org.tm.archive.recipients.Recipient
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull

class SignalArchiveRecipientConverter(
  @Deprecated("Converter should never have a context reference")
  private val context: Context
) {

  fun convertSenderRecipient(message: MessageRecord, chat: ArchiveChat, direction: Direction): ArchiveRecipient {
    val recipient = message.fromRecipient
    if (direction == Direction.Outgoing)
      return ArchiveRecipient.forLongName(id = null, address = getMyPhoneNumber(), longName = recipient.displayName())
    var phoneNumber = getPhoneNumber(recipient)
    if (phoneNumber == null && chat.type == ChatType.Group)
      phoneNumber = recipient.toParticipant { it == recipient.id }?.e164?.getOrNull() // TODO find message.authorId
    phoneNumber = recipient.e164.getOrDefault(phoneNumber ?: "")
    return ArchiveRecipient.forLongName(id = null, address = phoneNumber, longName = recipient.displayName())
  }

  fun convertReceiverRecipients(message: MessageRecord, thread: ThreadRecord?, chat: ArchiveChat, sender: ArchiveRecipient, direction: Direction): List<ArchiveRecipient> {
    val recipient = thread?.takeIf { chat.type != ChatType.Chat && (it.recipient.participantIds.isNotEmpty() || it.recipient.resolve().participantIds.isNotEmpty()) }?.recipient?.resolve() ?: message.toRecipient
    if (chat.type == ChatType.Group) {
      return recipient.toParticipants().map { r -> ArchiveRecipient.forLongName(id = null, address = r.e164.getOrNull(), longName = r.displayName()) }
    }
    if (direction == Direction.Incoming)
      return listOf(ArchiveRecipient.forLongName(id = null, address = getMyPhoneNumber(), longName = recipient.displayName()))
    return listOf(ArchiveRecipient.forLongName(id = null, address = recipient.e164.getOrNull() ?: "", longName = recipient.displayName()))
  }

  private fun Recipient.displayName() = getDisplayName(context)

  private fun getPhoneNumber(recipient: Recipient): String? {
    return recipient.e164.getOrNull()
  }

  private fun getMyPhoneNumber(): String {
    return PrefManager.getStringPref(context, ArchivePreferenceConstants.PREF_KEY_DEVICE_PHONE_NUMBER, "")
  }
}