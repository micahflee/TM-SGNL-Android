package org.archiver.converter

import com.tm.androidcopysdk.Models.ArchiveAttachment
import com.tm.androidcopysdk.Models.ArchiveAttachmentType
import org.archiver.model.Attachments.status
import org.tm.archive.attachments.DatabaseAttachment
import org.tm.archive.mms.AudioSlide
import org.tm.archive.mms.DocumentSlide
import org.tm.archive.mms.GifSlide
import org.tm.archive.mms.ImageSlide
import org.tm.archive.mms.LocationSlide
import org.tm.archive.mms.MmsSlide
import org.tm.archive.mms.Slide
import org.tm.archive.mms.SlideDeck
import org.tm.archive.mms.StickerSlide
import org.tm.archive.mms.TextSlide
import org.tm.archive.mms.VideoSlide

class SignalAttachmentConverter {

  fun convert(slideDeck: SlideDeck?): List<ArchiveAttachment> {
    if (slideDeck == null)
      return emptyList()
    return convert(slideDeck.slides)
  }

  fun convert(slides: List<Slide>): List<ArchiveAttachment> = slides.mapNotNull(this::convert)

  fun convert(slide: Slide?): ArchiveAttachment? {
    if (slide == null)
      return null
    val attachment = slide.asAttachment() as? DatabaseAttachment ?: return null
    return ArchiveAttachment(
      id = attachment.attachmentId.id.toString(),
      type = convertAttachmentType(slide),
      contentType = attachment.contentType,
      sourcePath = slide.uri?.toString(),
      archivePath = null,
//      archivePath = slide.fileName,
      status = slide.status(),
      isViewOnce = false
    )
  }

  private fun convertAttachmentType(slide: Slide): ArchiveAttachmentType {
    return when (slide) {

      is TextSlide -> ArchiveAttachmentType.Text

      is AudioSlide -> ArchiveAttachmentType.Audio

      is LocationSlide -> ArchiveAttachmentType.Location

      is MmsSlide -> ArchiveAttachmentType.Mms

      is DocumentSlide -> ArchiveAttachmentType.Document

      is GifSlide -> ArchiveAttachmentType.Gif

      is StickerSlide -> ArchiveAttachmentType.Sticker

      is VideoSlide -> ArchiveAttachmentType.Video

      is ImageSlide -> ArchiveAttachmentType.Image

      else -> ArchiveAttachmentType.Unknown
    }
  }

}