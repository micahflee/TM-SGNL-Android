package org.tm.archive.testutil

import org.tm.archive.attachments.Attachment
import org.tm.archive.contactshare.Contact
import org.tm.archive.database.ThreadDatabase
import org.tm.archive.database.documents.IdentityKeyMismatch
import org.tm.archive.database.documents.NetworkFailure
import org.tm.archive.database.model.Mention
import org.tm.archive.database.model.ParentStoryId
import org.tm.archive.database.model.StoryType
import org.tm.archive.database.model.databaseprotos.GiftBadge
import org.tm.archive.linkpreview.LinkPreview
import org.tm.archive.mms.OutgoingMediaMessage
import org.tm.archive.mms.OutgoingSecureMediaMessage
import org.tm.archive.mms.QuoteModel
import org.tm.archive.recipients.Recipient

object OutgoingMediaMessageBuilder {
  fun create(
    recipient: Recipient = Recipient.UNKNOWN,
    message: String = "",
    attachments: List<Attachment> = emptyList(),
    sentTimeMillis: Long = System.currentTimeMillis(),
    subscriptionId: Int = -1,
    expiresIn: Long = -1,
    viewOnce: Boolean = false,
    distributionType: Int = ThreadDatabase.DistributionTypes.DEFAULT,
    storyType: StoryType = StoryType.NONE,
    parentStoryId: ParentStoryId? = null,
    isStoryReaction: Boolean = false,
    quoteModel: QuoteModel? = null,
    contacts: List<Contact> = emptyList(),
    linkPreviews: List<LinkPreview> = emptyList(),
    mentions: List<Mention> = emptyList(),
    networkFailures: Set<NetworkFailure> = emptySet(),
    identityKeyMismatches: Set<IdentityKeyMismatch> = emptySet(),
    giftBadge: GiftBadge? = null
  ): OutgoingMediaMessage {
    return OutgoingMediaMessage(
      recipient,
      message,
      attachments,
      sentTimeMillis,
      subscriptionId,
      expiresIn,
      viewOnce,
      distributionType,
      storyType,
      parentStoryId,
      isStoryReaction,
      quoteModel,
      contacts,
      linkPreviews,
      mentions,
      networkFailures,
      identityKeyMismatches,
      giftBadge
    )
  }

  fun OutgoingMediaMessage.secure(): OutgoingSecureMediaMessage = OutgoingSecureMediaMessage(this)
}
