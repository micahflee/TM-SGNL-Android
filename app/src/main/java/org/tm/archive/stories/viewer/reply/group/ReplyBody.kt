package org.tm.archive.stories.viewer.reply.group

import org.tm.archive.conversation.ConversationMessage
import org.tm.archive.database.model.MessageId
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.recipients.Recipient

sealed class ReplyBody(val messageRecord: MessageRecord) {

  val key: MessageId = MessageId(messageRecord.id)
  val sender: Recipient = messageRecord.fromRecipient.resolve()
  val sentAtMillis: Long = messageRecord.dateSent

  open fun hasSameContent(other: ReplyBody): Boolean {
    return key == other.key &&
      sender.hasSameContent(other.sender) &&
      sentAtMillis == other.sentAtMillis
  }

  class Text(val message: ConversationMessage) : ReplyBody(message.messageRecord) {
    override fun hasSameContent(other: ReplyBody): Boolean {
      return super.hasSameContent(other) &&
        (other as? Text)?.let { messageRecord.body == other.messageRecord.body } ?: false
    }
  }

  class Reaction(messageRecord: MessageRecord) : ReplyBody(messageRecord) {
    val emoji: CharSequence = messageRecord.body

    override fun hasSameContent(other: ReplyBody): Boolean {
      return super.hasSameContent(other) &&
        (other as? Reaction)?.let { emoji == other.emoji } ?: false
    }
  }

  class RemoteDelete(messageRecord: MessageRecord) : ReplyBody(messageRecord)
}
