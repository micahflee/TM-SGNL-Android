/*
 * Copyright 2023 Signal Messenger, LLC
 * SPDX-License-Identifier: AGPL-3.0-only
 */

package org.tm.archive.conversation.v2.items

import com.bumptech.glide.RequestManager
import org.tm.archive.conversation.ConversationAdapter
import org.tm.archive.conversation.ConversationItemDisplayMode
import org.tm.archive.conversation.colors.Colorizer
import org.tm.archive.conversation.mutiselect.MultiselectPart
import org.tm.archive.database.model.MessageRecord

/**
 * Describes the Adapter "context" that would normally have been
 * visible to an inner class.
 */
interface V2ConversationContext {
  val requestManager: RequestManager
  val displayMode: ConversationItemDisplayMode
  val clickListener: ConversationAdapter.ItemClickListener
  val selectedItems: Set<MultiselectPart>
  val isMessageRequestAccepted: Boolean
  val searchQuery: String?
  val isParentInScroll: Boolean

  fun getChatColorsData(): ChatColorsDrawable.ChatColorsData

  fun onStartExpirationTimeout(messageRecord: MessageRecord)

  fun hasWallpaper(): Boolean
  fun getColorizer(): Colorizer
  fun getNextMessage(adapterPosition: Int): MessageRecord?
  fun getPreviousMessage(adapterPosition: Int): MessageRecord?
}
