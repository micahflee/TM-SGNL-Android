package org.tm.archive.conversation.mutiselect

import android.view.View
import org.tm.archive.conversation.ConversationMessage
import org.tm.archive.conversation.colors.Colorizable
import org.tm.archive.giph.mp4.GiphyMp4Playable

interface Multiselectable : Colorizable, GiphyMp4Playable {
  val conversationMessage: ConversationMessage

  fun getTopBoundaryOfMultiselectPart(multiselectPart: MultiselectPart): Int

  fun getBottomBoundaryOfMultiselectPart(multiselectPart: MultiselectPart): Int

  fun getMultiselectPartForLatestTouch(): MultiselectPart

  fun getHorizontalTranslationTarget(): View?

  fun hasNonSelectableMedia(): Boolean
}
