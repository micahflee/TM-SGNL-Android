package org.archiver.converter

import android.content.Context
import com.tm.androidcopysdk.model.ArchiveChat
import com.tm.androidcopysdk.model.ChatType
import org.archiver.model.Messages.chatRecipient
import org.archiver.model.Messages.chatType
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.database.model.ThreadRecord
import org.tm.archive.recipients.Recipient
import kotlin.jvm.optionals.getOrNull

class SignalChatConverter(
  @Deprecated("Converter should never have a context reference")
  private val context: Context
) {

  fun convert(message: MessageRecord, thread: ThreadRecord?): ArchiveChat {
    val type = message.chatType(thread)
    val chatRecipient = message.chatRecipient(type, thread)
    return ArchiveChat(
      id = getChatId(chatRecipient, type) ?: "",
      type = type,
      name = chatRecipient.getDisplayName(context),
      isSecret = false,
    )
  }

  private fun getChatId(chatRecipient: Recipient?, type: ChatType): String? {
    if (type == ChatType.Group)
      return chatRecipient?.groupId?.getOrNull()?.toString()?.split(":")?.lastOrNull()
    return chatRecipient?.serviceId?.getOrNull()?.toString()?.split(":")?.lastOrNull()// ?: chatRecipient?.id?.toLong()?.toString()
  }

}