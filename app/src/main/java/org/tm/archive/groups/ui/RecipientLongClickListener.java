package org.tm.archive.groups.ui;

import androidx.annotation.NonNull;

import org.tm.archive.recipients.Recipient;

public interface RecipientLongClickListener {
  boolean onLongClick(@NonNull Recipient recipient);
}
