package org.tm.archive.mms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tm.archive.attachments.Attachment;
import org.tm.archive.contactshare.Contact;
import org.tm.archive.database.model.Mention;
import org.tm.archive.database.model.ParentStoryId;
import org.tm.archive.database.model.StoryType;
import org.tm.archive.database.model.databaseprotos.GiftBadge;
import org.tm.archive.linkpreview.LinkPreview;
import org.tm.archive.recipients.Recipient;

import java.util.Collections;
import java.util.List;

public class OutgoingSecureMediaMessage extends OutgoingMediaMessage {

  public OutgoingSecureMediaMessage(Recipient recipient,
                                    String body,
                                    List<Attachment> attachments,
                                    long sentTimeMillis,
                                    int distributionType,
                                    long expiresIn,
                                    boolean viewOnce,
                                    @NonNull StoryType storyType,
                                    @Nullable ParentStoryId parentStoryId,
                                    boolean isStoryReaction,
                                    @Nullable QuoteModel quote,
                                    @NonNull List<Contact> contacts,
                                    @NonNull List<LinkPreview> previews,
                                    @NonNull List<Mention> mentions,
                                    @Nullable GiftBadge giftBadge)
  {
    super(recipient, body, attachments, sentTimeMillis, -1, expiresIn, viewOnce, distributionType, storyType, parentStoryId, isStoryReaction, quote, contacts, previews, mentions, Collections.emptySet(), Collections.emptySet(), giftBadge);
  }

  public OutgoingSecureMediaMessage(OutgoingMediaMessage base) {
    super(base);
  }

  @Override
  public boolean isSecure() {
    return true;
  }

  @Override
  public @NonNull OutgoingMediaMessage withExpiry(long expiresIn) {
    return new OutgoingSecureMediaMessage(getRecipient(),
                                          getBody(),
                                          getAttachments(),
                                          getSentTimeMillis(),
                                          getDistributionType(),
                                          expiresIn,
                                          isViewOnce(),
                                          getStoryType(),
                                          getParentStoryId(),
                                          isStoryReaction(),
                                          getOutgoingQuote(),
                                          getSharedContacts(),
                                          getLinkPreviews(),
                                          getMentions(),
                                          getGiftBadge());
  }

  public @NonNull OutgoingSecureMediaMessage withSentTimestamp(long sentTimestamp) {
    return new OutgoingSecureMediaMessage(getRecipient(),
                                          getBody(),
                                          getAttachments(),
                                          sentTimestamp,
                                          getDistributionType(),
                                          getExpiresIn(),
                                          isViewOnce(),
                                          getStoryType(),
                                          getParentStoryId(),
                                          isStoryReaction(),
                                          getOutgoingQuote(),
                                          getSharedContacts(),
                                          getLinkPreviews(),
                                          getMentions(),
                                          getGiftBadge());
  }

  public @NonNull OutgoingSecureMediaMessage stripAttachments() {
    return new OutgoingSecureMediaMessage(getRecipient(),
                                          getBody(),
                                          Collections.emptyList(),
                                          getSentTimeMillis(),
                                          getDistributionType(),
                                          getExpiresIn(),
                                          isViewOnce(),
                                          getStoryType(),
                                          getParentStoryId(),
                                          isStoryReaction(),
                                          getOutgoingQuote(),
                                          Collections.emptyList(),
                                          getLinkPreviews(),
                                          getMentions(),
                                          getGiftBadge());
  }
}
