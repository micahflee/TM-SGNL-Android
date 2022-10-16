package org.tm.archive.mms;

import org.tm.archive.database.ThreadDatabase;
import org.tm.archive.database.model.StoryType;
import org.tm.archive.recipients.Recipient;

import java.util.Collections;
import java.util.LinkedList;

public class OutgoingExpirationUpdateMessage extends OutgoingSecureMediaMessage {

  public OutgoingExpirationUpdateMessage(Recipient recipient, long sentTimeMillis, long expiresIn) {
    super(recipient,
          "",
          new LinkedList<>(),
          sentTimeMillis,
          ThreadDatabase.DistributionTypes.CONVERSATION,
          expiresIn,
          false,
          StoryType.NONE,
          null,
          false,
          null,
          Collections.emptyList(),
          Collections.emptyList(),
          Collections.emptyList(),
          null);
  }

  @Override
  public boolean isExpirationUpdate() {
    return true;
  }

  @Override
  public boolean isUrgent() {
    return false;
  }
}
