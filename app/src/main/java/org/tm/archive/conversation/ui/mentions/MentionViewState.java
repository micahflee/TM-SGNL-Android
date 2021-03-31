package org.tm.archive.conversation.ui.mentions;

import androidx.annotation.NonNull;

import org.tm.archive.recipients.Recipient;
import org.tm.archive.util.viewholders.RecipientMappingModel;

public final class MentionViewState extends RecipientMappingModel<MentionViewState> {

  private final Recipient recipient;

  public MentionViewState(@NonNull Recipient recipient) {
    this.recipient = recipient;
  }

  @Override
  public @NonNull Recipient getRecipient() {
    return recipient;
  }
}
