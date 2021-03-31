package org.tm.archive.mms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tm.archive.attachments.Attachment;
import org.tm.archive.contactshare.Contact;
import org.tm.archive.database.model.Mention;
import org.tm.archive.linkpreview.LinkPreview;
import org.tm.archive.recipients.Recipient;

import java.util.Collections;
import java.util.List;

public class OutgoingSecureMediaMessage extends OutgoingMediaMessage {

  public OutgoingSecureMediaMessage(Recipient recipient, String body,
                                    List<Attachment> attachments,
                                    long sentTimeMillis,
                                    int distributionType,
                                    long expiresIn,
                                    boolean viewOnce,
                                    @Nullable QuoteModel quote,
                                    @NonNull List<Contact> contacts,
                                    @NonNull List<LinkPreview> previews,
                                    @NonNull List<Mention> mentions)
  {
    super(recipient, body, attachments, sentTimeMillis, -1, expiresIn, viewOnce, distributionType, quote, contacts, previews, mentions, Collections.emptyList(), Collections.emptyList());
  }

  public OutgoingSecureMediaMessage(OutgoingMediaMessage base) {
    super(base);
  }

  @Override
  public boolean isSecure() {
    return true;
  }
}
