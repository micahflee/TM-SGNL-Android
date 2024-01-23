package org.tm.archive;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import org.tm.archive.conversationlist.model.ConversationSet;
import org.tm.archive.database.model.ThreadRecord;
import org.tm.archive.mms.GlideRequests;

import java.util.Locale;
import java.util.Set;

public interface BindableConversationListItem extends Unbindable {

  void bind(@NonNull LifecycleOwner lifecycleOwner,
            @NonNull ThreadRecord thread,
            @NonNull GlideRequests glideRequests, @NonNull Locale locale,
            @NonNull Set<Long> typingThreads,
            @NonNull ConversationSet selectedConversations);

  void setSelectedConversations(@NonNull ConversationSet conversations);
  void updateTypingIndicator(@NonNull Set<Long> typingThreads);
  void updateTimestamp();
}
