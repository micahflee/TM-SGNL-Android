package org.tm.archive.conversation

import org.tm.archive.recipients.RecipientId

data class ConversationSecurityInfo(
  val recipientId: RecipientId = RecipientId.UNKNOWN,
  val isPushAvailable: Boolean = false,
  val isDefaultSmsApplication: Boolean = false,
  val isInitialized: Boolean = false,
  val hasUnexportedInsecureMessages: Boolean = false,
  val isClientExpired: Boolean = false,
  val isUnauthorized: Boolean = false
)
