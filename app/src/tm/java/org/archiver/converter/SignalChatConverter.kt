package org.archiver.converter

import android.content.Context
import com.tm.androidcopysdk.model.ArchiveChat
import org.archiver.model.Messages.chatRecipient
import org.archiver.model.Messages.chatType
import org.tm.archive.database.model.MessageRecord
import kotlin.jvm.optionals.getOrNull

class SignalChatConverter(
  @Deprecated("Converter should never have a context reference")
  private val context: Context
) {

  fun convert(message: MessageRecord): ArchiveChat {
    val chatRecipient = message.chatRecipient()
    return ArchiveChat(
      id = chatRecipient?.groupId?.getOrNull()?.toString() ?: "",
      type = message.chatType(),
      name = chatRecipient.getGroupName(context),
      isSecret = false,
    )
  }

}