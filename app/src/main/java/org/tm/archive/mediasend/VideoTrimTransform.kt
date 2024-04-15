package org.tm.archive.mediasend

import android.content.Context
import androidx.annotation.WorkerThread
import org.tm.archive.database.AttachmentTable.TransformProperties
import org.tm.archive.mediasend.v2.videos.VideoTrimData
import org.tm.archive.mms.SentMediaQuality
import java.util.Optional

class VideoTrimTransform(private val data: VideoTrimData) : MediaTransform {
  @WorkerThread
  override fun transform(context: Context, media: Media): Media {
    return Media(
      media.uri,
      media.mimeType,
      media.date,
      media.width,
      media.height,
      media.size,
      media.duration,
      media.isBorderless,
      media.isVideoGif,
      media.bucketId,
      media.caption,
      Optional.of(TransformProperties(false, data.isDurationEdited, data.startTimeUs, data.endTimeUs, SentMediaQuality.STANDARD.code, false))
    )
  }
}
