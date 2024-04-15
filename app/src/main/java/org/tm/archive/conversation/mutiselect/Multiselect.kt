package org.tm.archive.conversation.mutiselect

import org.tm.archive.conversation.ConversationMessage
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.mms.SlideDeck
import org.tm.archive.mms.TextSlide

/**
 * General helper object for all things multiselect. This is only utilized by
 * [ConversationMessage]
 */
object Multiselect {

  /**
   * Returns a list of parts in the order in which they would appear to the user.
   */
  @JvmStatic
  fun getParts(conversationMessage: ConversationMessage): MultiselectCollection {
    val messageRecord = conversationMessage.messageRecord

    if (messageRecord.isUpdate) {
      return MultiselectCollection.Single(MultiselectPart.Update(conversationMessage))
    }

    val parts: LinkedHashSet<MultiselectPart> = linkedSetOf()

    if (messageRecord is MmsMessageRecord) {
      parts.addAll(getMmsParts(conversationMessage, messageRecord))
    }

    if (messageRecord.body.isNotEmpty()) {
      parts.add(MultiselectPart.Text(conversationMessage))
    }

    return if (parts.isEmpty()) {
      MultiselectCollection.Single(MultiselectPart.Message(conversationMessage))
    } else {
      MultiselectCollection.fromSet(parts)
    }
  }

  private fun getMmsParts(conversationMessage: ConversationMessage, mmsMessageRecord: MmsMessageRecord): Set<MultiselectPart> {
    val parts: LinkedHashSet<MultiselectPart> = linkedSetOf()

    val slideDeck: SlideDeck = mmsMessageRecord.slideDeck

    if (slideDeck.slides.filterNot { it is TextSlide }.isNotEmpty()) {
      parts.add(MultiselectPart.Attachments(conversationMessage))
    }

    if (slideDeck.body.isNotEmpty()) {
      parts.add(MultiselectPart.Text(conversationMessage))
    }

    return parts
  }
}
