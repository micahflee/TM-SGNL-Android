package org.archiver.converter

import android.content.Context
import com.tm.androidcopysdk.model.ArchiveRecipient
import com.tm.androidcopysdk.utils.PrefManager
import org.archiver.ArchivePreferenceConstants
import org.archiver.model.Messages.isGroupMessage
import org.archiver.model.Recipients.toParticipant
import org.archiver.model.Recipients.toParticipants
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.recipients.Recipient
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull

class SignalArchiveRecipientConverter(
  @Deprecated("Converter should never have a context reference")
  private val context: Context
) {

  fun convertSenderRecipient(message: MessageRecord): ArchiveRecipient {
    val recipient = message.fromRecipient
    if (message.isOutgoing)
      return ArchiveRecipient.forLongName(id = null, address = getMyPhoneNumber(), longName = recipient.displayName())
    var phoneNumber = getPhoneNumber(recipient)
    if (phoneNumber == null && message.isGroupMessage()) {
      phoneNumber = recipient.toParticipant { it == recipient.id }?.e164?.getOrNull() // TODO find message.authorId
    }
    phoneNumber = recipient.e164.getOrDefault(phoneNumber ?: recipient.requireE164())
    return ArchiveRecipient.forLongName(id = null, address = phoneNumber, longName = recipient.displayName())
  }

  fun convertReceiverRecipients(message: MessageRecord): List<ArchiveRecipient> {
    val recipient = message.toRecipient
    if (message.isGroupMessage())
      return recipient.toParticipants().mapNotNull { r -> r.e164.getOrNull()?.let { ArchiveRecipient.forLongName(id = null, address = it, longName = r.displayName()) } }
    if (!message.isOutgoing)
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