package org.tm.archive.messages;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import org.tm.archive.crypto.DatabaseSessionLock;
import org.tm.archive.crypto.IdentityKeyUtil;
import org.tm.archive.database.DatabaseFactory;
import org.tm.archive.database.GroupDatabase;
import org.tm.archive.database.MessageDatabase.SyncMessageId;
import org.tm.archive.database.MmsSmsDatabase;
import org.tm.archive.database.PushDatabase;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.groups.BadGroupIdException;
import org.tm.archive.groups.GroupChangeBusyException;
import org.tm.archive.groups.GroupId;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.jobmanager.JobManager;
import org.tm.archive.jobs.PushDecryptMessageJob;
import org.tm.archive.jobs.PushProcessMessageJob;
import org.tm.archive.messages.MessageDecryptionUtil.DecryptionResult;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.util.FeatureFlags;
import org.tm.archive.util.GroupUtil;
import org.tm.archive.util.SetUtil;
import org.tm.archive.util.Stopwatch;
import org.tm.archive.util.TextSecurePreferences;
import org.whispersystems.signalservice.api.SignalSessionLock;
import org.whispersystems.signalservice.api.messages.SignalServiceEnvelope;
import org.whispersystems.signalservice.api.messages.SignalServiceGroupContext;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The central entry point for all envelopes that have been retrieved. Envelopes must be processed
 * here to guarantee proper ordering.
 */
public class IncomingMessageProcessor {

  private static final String TAG = Log.tag(IncomingMessageProcessor.class);

  private final Application   context;
  private final ReentrantLock lock;

  public IncomingMessageProcessor(@NonNull Application context) {
    this.context = context;
    this.lock    = new ReentrantLock();
  }

  /**
   * @return An instance of a Processor that will allow you to process messages in a thread safe
   * way. Must be closed.
   */
  public Processor acquire() {
    lock.lock();
    return new Processor(context);
  }

  private void release() {
    lock.unlock();
  }

  public class Processor implements Closeable {

    private final Context           context;
    private final MmsSmsDatabase    mmsSmsDatabase;
    private final JobManager        jobManager;

    private Processor(@NonNull Context context) {
      this.context           = context;
      this.mmsSmsDatabase    = DatabaseFactory.getMmsSmsDatabase(context);
      this.jobManager        = ApplicationDependencies.getJobManager();
    }

    /**
     * @return The id of the {@link PushDecryptMessageJob} that was scheduled to process the message, if
     *         one was created. Otherwise null.
     */
    public @Nullable String processEnvelope(@NonNull SignalServiceEnvelope envelope) {
      if (envelope.hasSource()) {
        Recipient.externalHighTrustPush(context, envelope.getSourceAddress());
      }

      if (envelope.isReceipt()) {
        processReceipt(envelope);
        return null;
      } else if (envelope.isPreKeySignalMessage() || envelope.isSignalMessage() || envelope.isUnidentifiedSender()) {
        return processMessage(envelope);
      } else {
        Log.w(TAG, "Received envelope of unknown type: " + envelope.getType());
        return null;
      }
    }

    private @Nullable String processMessage(@NonNull SignalServiceEnvelope envelope) {
      if (FeatureFlags.internalUser()) {
        return processMessageInline(envelope);
      } else {
        return processMessageDeferred(envelope);
      }
    }

    private @Nullable String processMessageDeferred(@NonNull SignalServiceEnvelope envelope) {
      Job job = new PushDecryptMessageJob(context, envelope);
      jobManager.add(job);
      return job.getId();
    }

    private @Nullable String processMessageInline(@NonNull SignalServiceEnvelope envelope) {
      Log.i(TAG, "Received message " + envelope.getTimestamp() + ".");

      Stopwatch stopwatch = new Stopwatch("message");

      if (needsToEnqueueDecryption()) {
        Log.d(TAG, "Need to enqueue decryption.");
        PushDecryptMessageJob job = new PushDecryptMessageJob(context, envelope);
        jobManager.add(job);
        return job.getId();
      }

      stopwatch.split("queue-check");

      long ownerThreadId = DatabaseSessionLock.INSTANCE.getLikeyOwnerThreadId();
      if (ownerThreadId != DatabaseSessionLock.NO_OWNER && ownerThreadId != Thread.currentThread().getId()) {
        Log.i(TAG, "It is likely that some other thread has this lock. Owner: " + ownerThreadId + ", Us: " + Thread.currentThread().getId());
      }

      try (SignalSessionLock.Lock unused = DatabaseSessionLock.INSTANCE.acquire()) {
        Log.i(TAG, "Acquired lock while processing message " + envelope.getTimestamp() + ".");

        DecryptionResult result = MessageDecryptionUtil.decrypt(context, envelope);
        Log.d(TAG, "Decryption finished for " + envelope.getTimestamp());
        stopwatch.split("decrypt");

        for (Job job : result.getJobs()) {
          jobManager.add(job);
        }

        stopwatch.split("jobs");

        if (needsToEnqueueProcessing(result)) {
          Log.d(TAG, "Need to enqueue processing.");
          jobManager.add(new PushProcessMessageJob(result.getState(), result.getContent(), result.getException(), -1, envelope.getTimestamp()));
          return null;
        }

        stopwatch.split("group-check");

        try {
          MessageContentProcessor processor = new MessageContentProcessor(context);
          processor.process(result.getState(), result.getContent(), result.getException(), envelope.getTimestamp(), -1);
          return null;
        } catch (IOException | GroupChangeBusyException e) {
          Log.w(TAG, "Exception during message processing.", e);
          jobManager.add(new PushProcessMessageJob(result.getState(), result.getContent(), result.getException(), -1, envelope.getTimestamp()));
        }
      } finally {
        stopwatch.split("process");
        stopwatch.stop(TAG);
      }

      return null;
    }

    private void processReceipt(@NonNull SignalServiceEnvelope envelope) {
      Log.i(TAG, "Received server receipt for " + envelope.getTimestamp());
      mmsSmsDatabase.incrementDeliveryReceiptCount(new SyncMessageId(Recipient.externalHighTrustPush(context, envelope.getSourceAddress()).getId(), envelope.getTimestamp()),
                                                   System.currentTimeMillis());
    }

    private boolean needsToEnqueueDecryption() {
      return !jobManager.areQueuesEmpty(SetUtil.newHashSet(Job.Parameters.MIGRATION_QUEUE_KEY, PushDecryptMessageJob.QUEUE)) ||
             !IdentityKeyUtil.hasIdentityKey(context)                                                                        ||
             TextSecurePreferences.getNeedsSqlCipherMigration(context);
    }

    private boolean needsToEnqueueProcessing(@NonNull DecryptionResult result) {
      SignalServiceGroupContext groupContext = GroupUtil.getGroupContextIfPresent(result.getContent());

      if (groupContext != null) {
        try {
          GroupId groupId = GroupUtil.idFromGroupContext(groupContext);

          if (groupId.isV2()) {
            String        queueName     = PushProcessMessageJob.getQueueName(Recipient.externalPossiblyMigratedGroup(context, groupId).getId());
            GroupDatabase groupDatabase = DatabaseFactory.getGroupDatabase(context);

            return !jobManager.isQueueEmpty(queueName)                                                                   ||
                   groupContext.getGroupV2().get().getRevision() > groupDatabase.getGroupV2Revision(groupId.requireV2()) ||
                   groupDatabase.getGroupV1ByExpectedV2(groupId.requireV2()).isPresent();
          } else {
            return false;
          }
        } catch (BadGroupIdException e) {
          Log.w(TAG, "Bad group ID!");
          return false;
        }
      } else if (result.getContent() != null) {
        RecipientId recipientId = RecipientId.fromHighTrust(result.getContent().getSender());
        String      queueKey    = PushProcessMessageJob.getQueueName(recipientId);

        return !jobManager.isQueueEmpty(queueKey);
      } else if (result.getException() != null) {
        RecipientId recipientId = Recipient.external(context, result.getException().getSender()).getId();
        String      queueKey    = PushProcessMessageJob.getQueueName(recipientId);

        return !jobManager.isQueueEmpty(queueKey);
      } else {
        return false;
      }
    }

    @Override
    public void close() {
      release();
    }
  }
}
