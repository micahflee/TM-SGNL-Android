package org.tm.archive.mms;

import org.tm.archive.attachments.Attachment;
import org.tm.archive.database.ThreadDatabase;
import org.tm.archive.recipients.Recipient;

import java.util.Collections;
import java.util.LinkedList;

public class OutgoingExpirationUpdateMessage extends OutgoingSecureMediaMessage {

  public OutgoingExpirationUpdateMessage(Recipient recipient, long sentTimeMillis, long expiresIn) {
    super(recipient, "", new LinkedList<Attachment>(), sentTimeMillis,
          ThreadDatabase.DistributionTypes.CONVERSATION, expiresIn, false, null, Collections.emptyList(),
          Collections.emptyList(), Collections.emptyList());
  }

  @Override
  public boolean isExpirationUpdate() {
    return true;
  }

}
