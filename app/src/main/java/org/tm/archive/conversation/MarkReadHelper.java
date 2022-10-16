package org.tm.archive.conversation;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import org.tm.archive.database.MessageDatabase;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.database.ThreadDatabase;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.notifications.MarkReadReceiver;
import org.tm.archive.notifications.v2.ConversationId;
import org.tm.archive.util.Debouncer;
import org.tm.archive.util.concurrent.SerialMonoLifoExecutor;

import java.util.List;
import java.util.concurrent.Executor;

public class MarkReadHelper {
  private static final String TAG = Log.tag(MarkReadHelper.class);

  private static final long     DEBOUNCE_TIMEOUT = 100;
  private static final Executor EXECUTOR         = new SerialMonoLifoExecutor(SignalExecutors.BOUNDED);

  private final ConversationId conversationId;
  private final Context        context;
  private final LifecycleOwner     lifecycleOwner;
  private final Debouncer          debouncer = new Debouncer(DEBOUNCE_TIMEOUT);
  private       long               latestTimestamp;

  public MarkReadHelper(@NonNull ConversationId conversationId, @NonNull Context context, @NonNull LifecycleOwner lifecycleOwner) {
    this.conversationId = conversationId;
    this.context        = context.getApplicationContext();
    this.lifecycleOwner = lifecycleOwner;
  }

  public void onViewsRevealed(long timestamp) {
    if (timestamp <= latestTimestamp || lifecycleOwner.getLifecycle().getCurrentState() != Lifecycle.State.RESUMED) {
      return;
    }

    latestTimestamp = timestamp;

    debouncer.publish(() -> {
      EXECUTOR.execute(() -> {
        ThreadDatabase                          threadDatabase = SignalDatabase.threads();
        List<MessageDatabase.MarkedMessageInfo> infos          = threadDatabase.setReadSince(conversationId, false, timestamp);

        Log.d(TAG, "Marking " + infos.size() + " messages as read.");

        ApplicationDependencies.getMessageNotifier().updateNotification(context);
        MarkReadReceiver.process(context, infos);
      });
    });
  }
}
