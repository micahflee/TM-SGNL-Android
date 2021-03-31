package org.tm.archive.conversation.ui.error;

import androidx.annotation.NonNull;

import org.tm.archive.database.IdentityDatabase;
import org.tm.archive.database.IdentityDatabase.IdentityRecord;
import org.tm.archive.recipients.Recipient;

/**
 * Wrapper class for helping show a list of recipients that had recent safety number changes.
 *
 * Also provides helper methods for behavior used in multiple spots.
 */
final class ChangedRecipient {
  private final Recipient      recipient;
  private final IdentityRecord record;

  ChangedRecipient(@NonNull Recipient recipient, @NonNull IdentityRecord record) {
    this.recipient = recipient;
    this.record    = record;
  }

  @NonNull Recipient getRecipient() {
    return recipient;
  }

  @NonNull IdentityRecord getIdentityRecord() {
    return record;
  }

  boolean isUnverified() {
    return record.getVerifiedStatus() == IdentityDatabase.VerifiedStatus.UNVERIFIED;
  }

  boolean isVerified() {
    return record.getVerifiedStatus() == IdentityDatabase.VerifiedStatus.VERIFIED;
  }
}
