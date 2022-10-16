package org.tm.archive.mediasend.v2

import android.net.Uri
import org.tm.archive.conversation.MessageSendType
import org.tm.archive.keyvalue.SignalStore
import org.tm.archive.mediasend.Media
import org.tm.archive.mediasend.MediaSendConstants
import org.tm.archive.mms.SentMediaQuality
import org.tm.archive.recipients.Recipient
import org.tm.archive.stories.Stories

data class MediaSelectionState(
  val sendType: MessageSendType,
  val selectedMedia: List<Media> = listOf(),
  val focusedMedia: Media? = null,
  val recipient: Recipient? = null,
  val quality: SentMediaQuality = SignalStore.settings().sentMediaQuality,
  val message: CharSequence? = null,
  val viewOnceToggleState: ViewOnceToggleState = ViewOnceToggleState.INFINITE,
  val isTouchEnabled: Boolean = true,
  val isSent: Boolean = false,
  val isPreUploadEnabled: Boolean = false,
  val isMeteredConnection: Boolean = false,
  val editorStateMap: Map<Uri, Any> = mapOf(),
  val cameraFirstCapture: Media? = null,
  val isStory: Boolean,
  val storySendRequirements: Stories.MediaTransform.SendRequirements = Stories.MediaTransform.SendRequirements.CAN_NOT_SEND
) {

  val maxSelection = if (sendType.usesSmsTransport) {
    MediaSendConstants.MAX_SMS
  } else {
    MediaSendConstants.MAX_PUSH
  }

  val canSend = !isSent && selectedMedia.isNotEmpty()

  enum class ViewOnceToggleState(val code: Int) {
    INFINITE(0),
    ONCE(1);

    fun next(): ViewOnceToggleState {
      return when (this) {
        INFINITE -> ONCE
        ONCE -> INFINITE
      }
    }

    companion object {
      fun fromCode(code: Int): ViewOnceToggleState {
        return when (code) {
          1 -> ONCE
          else -> INFINITE
        }
      }
    }
  }
}
