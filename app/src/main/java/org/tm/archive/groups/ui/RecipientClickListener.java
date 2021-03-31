package org.tm.archive.groups.ui;

import androidx.annotation.NonNull;

import org.tm.archive.recipients.Recipient;

public interface RecipientClickListener {
  void onClick(@NonNull Recipient recipient);
}
