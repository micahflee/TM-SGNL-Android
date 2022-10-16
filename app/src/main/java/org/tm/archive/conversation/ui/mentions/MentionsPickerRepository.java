package org.tm.archive.conversation.ui.mentions;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import org.tm.archive.database.GroupDatabase;
import org.tm.archive.database.RecipientDatabase;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;

import java.util.Collections;
import java.util.List;

final class MentionsPickerRepository {

  private final RecipientDatabase recipientDatabase;
  private final GroupDatabase     groupDatabase;

  MentionsPickerRepository() {
    recipientDatabase = SignalDatabase.recipients();
    groupDatabase     = SignalDatabase.groups();
  }

  @WorkerThread
  @NonNull List<RecipientId> getMembers(@Nullable Recipient recipient) {
    if (recipient == null || !recipient.isPushV2Group()) {
      return Collections.emptyList();
    }

    return groupDatabase.getGroupMemberIds(recipient.requireGroupId(), GroupDatabase.MemberSet.FULL_MEMBERS_EXCLUDING_SELF);
  }

  @WorkerThread
  @NonNull List<Recipient> search(@NonNull MentionQuery mentionQuery) {
    if (mentionQuery.query == null || mentionQuery.members.isEmpty()) {
      return Collections.emptyList();
    }

    return recipientDatabase.queryRecipientsForMentions(mentionQuery.query, mentionQuery.members);
  }

  static class MentionQuery {
    @Nullable private final String            query;
    @NonNull  private final List<RecipientId> members;

    MentionQuery(@Nullable String query, @NonNull List<RecipientId> members) {
      this.query   = query;
      this.members = members;
    }
  }
}
