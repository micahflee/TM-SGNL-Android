package org.tm.archive.database

import org.tm.archive.attachments.AttachmentId
import org.tm.archive.attachments.DatabaseAttachment
import org.tm.archive.audio.AudioHash
import org.tm.archive.blurhash.BlurHash
import org.tm.archive.contactshare.Contact
import org.tm.archive.database.documents.IdentityKeyMismatch
import org.tm.archive.database.documents.NetworkFailure
import org.tm.archive.database.model.MediaMmsMessageRecord
import org.tm.archive.database.model.ParentStoryId
import org.tm.archive.database.model.Quote
import org.tm.archive.database.model.ReactionRecord
import org.tm.archive.database.model.StoryType
import org.tm.archive.database.model.databaseprotos.BodyRangeList
import org.tm.archive.database.model.databaseprotos.GiftBadge
import org.tm.archive.linkpreview.LinkPreview
import org.tm.archive.mms.SlideDeck
import org.tm.archive.recipients.Recipient
import org.tm.archive.stickers.StickerLocator
import org.tm.archive.util.MediaUtil

/**
 * Builds MessageRecords and related components for direct usage in unit testing. Does not modify the database.
 */
object FakeMessageRecords {

  fun buildDatabaseAttachment(
    attachmentId: AttachmentId = AttachmentId(1, 1),
    mmsId: Long = 1,
    hasData: Boolean = true,
    hasThumbnail: Boolean = true,
    contentType: String = MediaUtil.IMAGE_JPEG,
    transferProgress: Int = AttachmentDatabase.TRANSFER_PROGRESS_DONE,
    size: Long = 0L,
    fileName: String = "",
    cdnNumber: Int = 1,
    location: String = "",
    key: String = "",
    relay: String = "",
    digest: ByteArray = byteArrayOf(),
    fastPreflightId: String = "",
    voiceNote: Boolean = false,
    borderless: Boolean = false,
    videoGif: Boolean = false,
    width: Int = 0,
    height: Int = 0,
    quote: Boolean = false,
    caption: String? = null,
    stickerLocator: StickerLocator? = null,
    blurHash: BlurHash? = null,
    audioHash: AudioHash? = null,
    transformProperties: AttachmentDatabase.TransformProperties? = null,
    displayOrder: Int = 0,
    uploadTimestamp: Long = 200
  ): DatabaseAttachment {
    return DatabaseAttachment(
      attachmentId,
      mmsId,
      hasData,
      hasThumbnail,
      contentType,
      transferProgress,
      size,
      fileName,
      cdnNumber,
      location,
      key,
      relay,
      digest,
      fastPreflightId,
      voiceNote,
      borderless,
      videoGif,
      width,
      height,
      quote,
      caption,
      stickerLocator,
      blurHash,
      audioHash,
      transformProperties,
      displayOrder,
      uploadTimestamp
    )
  }

  fun buildLinkPreview(
    url: String = "",
    title: String = "",
    description: String = "",
    date: Long = 200,
    attachmentId: AttachmentId? = null
  ): LinkPreview {
    return LinkPreview(
      url,
      title,
      description,
      date,
      attachmentId
    )
  }

  fun buildMediaMmsMessageRecord(
    id: Long = 1,
    conversationRecipient: Recipient = Recipient.UNKNOWN,
    individualRecipient: Recipient = conversationRecipient,
    recipientDeviceId: Int = 1,
    dateSent: Long = 200,
    dateReceived: Long = 400,
    dateServer: Long = 300,
    deliveryReceiptCount: Int = 0,
    threadId: Long = 1,
    body: String = "body",
    slideDeck: SlideDeck = SlideDeck(),
    partCount: Int = slideDeck.slides.count(),
    mailbox: Long = MmsSmsColumns.Types.BASE_INBOX_TYPE,
    mismatches: Set<IdentityKeyMismatch> = emptySet(),
    failures: Set<NetworkFailure> = emptySet(),
    subscriptionId: Int = -1,
    expiresIn: Long = -1,
    expireStarted: Long = -1,
    viewOnce: Boolean = false,
    readReceiptCount: Int = 0,
    quote: Quote? = null,
    contacts: List<Contact> = emptyList(),
    linkPreviews: List<LinkPreview> = emptyList(),
    unidentified: Boolean = false,
    reactions: List<ReactionRecord> = emptyList(),
    remoteDelete: Boolean = false,
    mentionsSelf: Boolean = false,
    notifiedTimestamp: Long = 350,
    viewedReceiptCount: Int = 0,
    receiptTimestamp: Long = 0,
    messageRanges: BodyRangeList? = null,
    storyType: StoryType = StoryType.NONE,
    parentStoryId: ParentStoryId? = null,
    giftBadge: GiftBadge? = null
  ): MediaMmsMessageRecord {
    return MediaMmsMessageRecord(
      id,
      conversationRecipient,
      individualRecipient,
      recipientDeviceId,
      dateSent,
      dateReceived,
      dateServer,
      deliveryReceiptCount,
      threadId,
      body,
      slideDeck,
      partCount,
      mailbox,
      mismatches,
      failures,
      subscriptionId,
      expiresIn,
      expireStarted,
      viewOnce,
      readReceiptCount,
      quote,
      contacts,
      linkPreviews,
      unidentified,
      reactions,
      remoteDelete,
      mentionsSelf,
      notifiedTimestamp,
      viewedReceiptCount,
      receiptTimestamp,
      messageRanges,
      storyType,
      parentStoryId,
      giftBadge
    )
  }
}
