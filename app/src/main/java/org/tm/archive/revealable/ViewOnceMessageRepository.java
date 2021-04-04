package org.tm.archive.revealable;

import android.content.Context;

import androidx.annotation.NonNull;

import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import org.tm.archive.database.DatabaseFactory;
import org.tm.archive.database.MessageDatabase;
import org.tm.archive.database.MmsDatabase;
import org.tm.archive.database.model.MmsMessageRecord;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobs.SendViewedReceiptJob;
import org.whispersystems.libsignal.util.guava.Optional;

class ViewOnceMessageRepository {

  private static final String TAG = Log.tag(ViewOnceMessageRepository.class);

  private final MessageDatabase mmsDatabase;

  ViewOnceMessageRepository(@NonNull Context context) {
    this.mmsDatabase = DatabaseFactory.getMmsDatabase(context);
  }

  void getMessage(long messageId, @NonNull Callback<Optional<MmsMessageRecord>> callback) {
    SignalExecutors.BOUNDED.execute(() -> {
      try (MmsDatabase.Reader reader = MmsDatabase.readerFor(mmsDatabase.getMessageCursor(messageId))) {
        MmsMessageRecord record = (MmsMessageRecord) reader.getNext();
        MessageDatabase.MarkedMessageInfo info = mmsDatabase.setIncomingMessageViewed(record.getId());
        if (info != null) {
          ApplicationDependencies.getJobManager().add(new SendViewedReceiptJob(record.getThreadId(),
                                                                               info.getSyncMessageId().getRecipientId(),
                                                                               info.getSyncMessageId().getTimetamp()));
        }
        callback.onComplete(Optional.fromNullable(record));
      }
    });
  }

  interface Callback<T> {
    void onComplete(T result);
  }
}
