package org.tm.archive.mediasend;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.core.util.logging.Log;
import org.tm.archive.R;
import org.tm.archive.contacts.ContactRepository;
import org.tm.archive.database.GroupDatabase;
import org.tm.archive.database.RecipientDatabase;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.database.ThreadDatabase;
import org.tm.archive.database.model.ThreadRecord;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Handles retrieving the data to be shown in {@link CameraContactSelectionFragment}.
 */
class CameraContactsRepository {

  private static final String TAG = Log.tag(CameraContactsRepository.class);

  private static final int RECENT_MAX = 25;

  private final Context           context;
  private final ThreadDatabase    threadDatabase;
  private final GroupDatabase     groupDatabase;
  private final RecipientDatabase recipientDatabase;
  private final ContactRepository contactRepository;
  private final Executor          serialExecutor;
  private final ExecutorService   parallelExecutor;

  CameraContactsRepository(@NonNull Context context) {
    this.context           = context.getApplicationContext();
    this.threadDatabase    = SignalDatabase.threads();
    this.groupDatabase     = SignalDatabase.groups();
    this.recipientDatabase = SignalDatabase.recipients();
    this.contactRepository = new ContactRepository(context, context.getString(R.string.note_to_self));
    this.serialExecutor    = SignalExecutors.SERIAL;
    this.parallelExecutor  = SignalExecutors.BOUNDED;
  }

  void getCameraContacts(@NonNull Callback<CameraContacts> callback) {
    getCameraContacts("", callback);
  }

  void getCameraContacts(@NonNull String query, @NonNull Callback<CameraContacts> callback) {
    serialExecutor.execute(() -> {
      Future<List<Recipient>> recents  = parallelExecutor.submit(() -> getRecents(query));
      Future<List<Recipient>> contacts = parallelExecutor.submit(() -> getContacts(query));
      Future<List<Recipient>> groups   = parallelExecutor.submit(() -> getGroups(query));

      try {
        long           startTime = System.currentTimeMillis();
        CameraContacts result    = new CameraContacts(recents.get(), contacts.get(), groups.get());

        Log.d(TAG, "Total time: " + (System.currentTimeMillis() - startTime) + " ms");

        callback.onComplete(result);
      } catch (InterruptedException | ExecutionException e) {
        Log.w(TAG, "Failed to perform queries.", e);
        callback.onComplete(new CameraContacts(Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));
      }
    });
  }


  @WorkerThread
  private @NonNull List<Recipient> getRecents(@NonNull String query) {
    if (!TextUtils.isEmpty(query)) {
      return Collections.emptyList();
    }

    List<Recipient> recipients = new ArrayList<>(RECENT_MAX);

    try (ThreadDatabase.Reader threadReader = threadDatabase.readerFor(threadDatabase.getRecentPushConversationList(RECENT_MAX, false))) {
      ThreadRecord threadRecord;
      while ((threadRecord = threadReader.getNext()) != null) {
        recipients.add(threadRecord.getRecipient().resolve());
      }
    }

    return recipients;
  }

  @WorkerThread
  private @NonNull List<Recipient> getContacts(@NonNull String query) {
    List<Recipient> recipients = new ArrayList<>();

    try (Cursor cursor = contactRepository.querySignalContacts(query)) {
      while (cursor.moveToNext()) {
        RecipientId id        = RecipientId.from(cursor.getLong(cursor.getColumnIndexOrThrow(ContactRepository.ID_COLUMN)));
        Recipient   recipient = Recipient.resolved(id);
        recipients.add(recipient);
      }
    }

    return recipients;
  }

  @WorkerThread
  private @NonNull List<Recipient> getGroups(@NonNull String query) {
    if (TextUtils.isEmpty(query)) {
      return Collections.emptyList();
    }

    List<Recipient> recipients = new ArrayList<>();

    try (GroupDatabase.Reader reader = groupDatabase.queryGroupsByTitle(query, false, true, true)) {
      GroupDatabase.GroupRecord groupRecord;
      while ((groupRecord = reader.getNext()) != null) {
        RecipientId recipientId = recipientDatabase.getOrInsertFromGroupId(groupRecord.getId());
        recipients.add(Recipient.resolved(recipientId));
      }
    }

    return recipients;
  }

  interface Callback<E> {
    void onComplete(E result);
  }
}
