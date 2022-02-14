package org.tm.archive.mediasend.v2

import android.net.Uri
import org.tm.archive.database.AttachmentDatabase
import org.tm.archive.mediasend.Media
import org.whispersystems.libsignal.util.guava.Optional

object MediaBuilder {
  fun buildMedia(
    uri: Uri,
    mimeType: String = "",
    date: Long = 0L,
    width: Int = 0,
    height: Int = 0,
    size: Long = 0L,
    duration: Long = 0L,
    borderless: Boolean = false,
    videoGif: Boolean = false,
    bucketId: Optional<String> = Optional.absent(),
    caption: Optional<String> = Optional.absent(),
    transformProperties: Optional<AttachmentDatabase.TransformProperties> = Optional.absent()
  ) = Media(uri, mimeType, date, width, height, size, duration, borderless, videoGif, bucketId, caption, transformProperties)
}
