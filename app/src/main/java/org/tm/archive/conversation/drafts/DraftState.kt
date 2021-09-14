package org.tm.archive.conversation.drafts

import org.tm.archive.database.DraftDatabase
import org.tm.archive.recipients.Recipient
import org.tm.archive.recipients.RecipientId

/**
 * State object responsible for holding Voice Note draft state. The intention is to allow
 * other pieces of draft state to be held here as well in the future, and to serve as a
 * management pattern going forward for drafts.
 */
data class DraftState(
  val recipientId: RecipientId = Recipient.UNKNOWN.id,
  val voiceNoteDraft: DraftDatabase.Draft? = null
)
