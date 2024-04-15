package org.tm.archive.mediasend.v2.capture

import org.tm.archive.mediasend.Media
import org.tm.archive.recipients.Recipient

sealed class MediaCaptureEvent {
  data class MediaCaptureRendered(val media: Media) : MediaCaptureEvent()
  data class UsernameScannedFromQrCode(val recipient: Recipient, val username: String) : MediaCaptureEvent()
  object DeviceLinkScannedFromQrCode : MediaCaptureEvent()
  object MediaCaptureRenderFailed : MediaCaptureEvent()
}
