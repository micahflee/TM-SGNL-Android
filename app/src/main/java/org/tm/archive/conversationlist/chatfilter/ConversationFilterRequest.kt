package org.tm.archive.conversationlist.chatfilter

import org.tm.archive.conversationlist.model.ConversationFilter

data class ConversationFilterRequest(
  val filter: ConversationFilter,
  val source: ConversationFilterSource
)
