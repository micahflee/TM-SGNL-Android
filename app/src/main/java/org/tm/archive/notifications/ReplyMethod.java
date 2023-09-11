package org.tm.archive.notifications;

import androidx.annotation.NonNull;

import org.tm.archive.recipients.Recipient;

public enum ReplyMethod {

  GroupMessage,
  SecureMessage;

  public static @NonNull ReplyMethod forRecipient(Recipient recipient) {
    if (recipient.isGroup()) {
      return ReplyMethod.GroupMessage;
    } else {
      return ReplyMethod.SecureMessage;
    }
  }
}
