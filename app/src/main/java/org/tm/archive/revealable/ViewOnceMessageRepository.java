package org.tm.archive.revealable;

import android.content.Context;

import androidx.annotation.NonNull;

import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import org.tm.archive.database.MessageDatabase;
import org.tm.archive.database.NoSuchMessageException;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.database.model.MmsMessageRecord;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobs.MultiDeviceViewedUpdateJob;
import org.tm.archive.jobs.SendViewedReceiptJob;
import org.whispersystems.libsignal.util.guava.Optional;

import java.util.Collections;

class ViewOnceMessageRepository {

  private static final String TAG = Log.tag(ViewOnceMessageRepository.class);

  private final MessageDatabase mmsDatabase;

  ViewOnceMessageRepository(@NonNull Context context) {
    this.mmsDatabase = SignalDatabase.mms();
  }

  void getMessage(long messageId, @NonNull Callback<Optional<MmsMessageRecord>> callback) {
    SignalExecutors.BOUNDED.execute(() -> {
      try {
        MmsMessageRecord record = (MmsMessageRecord) mmsDatabase.getMessageRecord(messageId);

        MessageDatabase.MarkedMessageInfo info = mmsDatabase.setIncomingMessageViewed(record.getId());
        if (info != null) {
          ApplicationDependencies.getJobManager().add(new SendViewedReceiptJob(record.getThreadId(),
                                                                               info.getSyncMessageId().getRecipientId(),
                                                                               info.getSyncMessageId().getTimetamp(),
                                                                               info.getMessageId()));
          MultiDeviceViewedUpdateJob.enqueue(Collections.singletonList(info.getSyncMessageId()));
        }

        callback.onComplete(Optional.fromNullable(record));
      } catch (NoSuchMessageException e) {
        callback.onComplete(Optional.absent());
      }
    });
  }

  interface Callback<T> {
    void onComplete(T result);
  }
}
