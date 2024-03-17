package org.archiver.converter

import com.tm.androidcopysdk.model.ArchiveAttachment
import com.tm.androidcopysdk.model.ArchiveAttachmentType
import com.tm.androidcopysdk.model.MessageAttachmentStatus
import org.archiver.model.Attachments.status
import org.tm.archive.attachments.DatabaseAttachment
import org.tm.archive.contactshare.Contact
import org.tm.archive.database.model.MessageRecord
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.mms.AudioSlide
import org.tm.archive.mms.DocumentSlide
import org.tm.archive.mms.GifSlide
import org.tm.archive.mms.ImageSlide
import org.tm.archive.mms.LocationSlide
import org.tm.archive.mms.MmsSlide
import org.tm.archive.mms.Slide
import org.tm.archive.mms.StickerSlide
import org.tm.archive.mms.TextSlide
import org.tm.archive.mms.VideoSlide

class SignalAttachmentConverter {

  fun convert(message: MessageRecord): List<ArchiveAttachment> {
    return convert(message as? MmsMessageRecord)
  }

  fun convert(mmsMessage: MmsMessageRecord?): List<ArchiveAttachment> {
    if (mmsMessage == null)
      return emptyList()
    return convertSlides(mmsMessage.slideDeck.slides) + convertContacts(mmsMessage.sharedContacts)
  }

  private fun convertContacts(contacts: List<Contact>): List<ArchiveAttachment> = contacts.mapNotNull(this::convertContact)

  private fun convertSlides(slides: List<Slide>): List<ArchiveAttachment> = slides.mapNotNull(this::convertSlide)

  private fun convertContact(contact: Contact?): ArchiveAttachment? {
    if (contact == null)
      return null
    return ArchiveAttachment(
      id = null,
      type = ArchiveAttachmentType.VCard,
      contentType = "text/x-vcard",
      sourcePath = "${contact.name.displayName?.replace(" ", "_") ?: "contact"}.vcf",
      archivePath = null,
      status = MessageAttachmentStatus.Success,
      isViewOnce = false
    )
  }

  private fun convertSlide(slide: Slide?): ArchiveAttachment? {
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

  private fun convertCallRecording(callMessage: MessageRecord): ArchiveAttachment? {
    return null
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