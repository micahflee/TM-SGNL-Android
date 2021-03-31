package org.tm.archive.notifications;

import android.content.Context;

import androidx.annotation.NonNull;

import org.tm.archive.database.RecipientDatabase;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.util.TextSecurePreferences;

public enum ReplyMethod {

  GroupMessage,
  SecureMessage,
  UnsecuredSmsMessage;

  public static @NonNull ReplyMethod forRecipient(Context context, Recipient recipient) {
    if (recipient.isGroup()) {
      return ReplyMethod.GroupMessage;
    } else if (TextSecurePreferences.isPushRegistered(context) && recipient.getRegistered() == RecipientDatabase.RegisteredState.REGISTERED && !recipient.isForceSmsSelection()) {
      return ReplyMethod.SecureMessage;
    } else {
      return ReplyMethod.UnsecuredSmsMessage;
    }
  }
}
