package org.tm.archive.jobs;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import org.tm.archive.crypto.UnidentifiedAccessUtil;
import org.tm.archive.database.RecipientDatabase;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobmanager.Data;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.jobmanager.impl.NetworkConstraint;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.recipients.RecipientUtil;
import org.whispersystems.libsignal.util.guava.Optional;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.crypto.UnidentifiedAccessPair;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;
import org.whispersystems.signalservice.api.push.exceptions.PushNetworkException;

import java.util.concurrent.TimeUnit;

/**
 * Just sends an empty message to a target recipient. Only suitable for individuals, NOT groups.
 */
public class NullMessageSendJob extends BaseJob {

  public static final String KEY = "NullMessageSendJob";

  private static final String TAG = Log.tag(NullMessageSendJob.class);

  private final RecipientId recipientId;

  private static final String KEY_RECIPIENT_ID = "recipient_id";

  public NullMessageSendJob(@NonNull RecipientId recipientId) {
    this(recipientId,
         new Parameters.Builder()
                       .setQueue(recipientId.toQueueKey())
                       .addConstraint(NetworkConstraint.KEY)
                       .setLifespan(TimeUnit.DAYS.toMillis(1))
                       .setMaxAttempts(Parameters.UNLIMITED)
                       .build());
  }

  private NullMessageSendJob(@NonNull RecipientId recipientId, @NonNull Parameters parameters) {
    super(parameters);
    this.recipientId = recipientId;
  }

  @Override
  public @NonNull Data serialize() {
    return new Data.Builder().putString(KEY_RECIPIENT_ID, recipientId.serialize()).build();
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  protected void onRun() throws Exception {
    Recipient recipient = Recipient.resolved(recipientId);

    if (recipient.isGroup()) {
      Log.w(TAG, "Groups are not supported!");
      return;
    }

    if (recipient.isUnregistered()) {
      Log.w(TAG, recipient.getId() + " not registered!");
    }

    SignalServiceMessageSender       messageSender      = ApplicationDependencies.getSignalServiceMessageSender();
    SignalServiceAddress             address            = RecipientUtil.toSignalServiceAddress(context, recipient);
    Optional<UnidentifiedAccessPair> unidentifiedAccess = UnidentifiedAccessUtil.getAccessFor(context, recipient);

    try {
      messageSender.sendNullMessage(address, unidentifiedAccess);
    } catch (UntrustedIdentityException e) {
      Log.w(TAG, "Unable to send null message.");
    }
  }

  @Override
  protected boolean onShouldRetry(@NonNull Exception e) {
    return e instanceof PushNetworkException;
  }

  @Override
  public void onFailure() {
  }

  public static final class Factory implements Job.Factory<NullMessageSendJob> {

    @Override
    public @NonNull NullMessageSendJob create(@NonNull Parameters parameters, @NonNull Data data) {
      return new NullMessageSendJob(RecipientId.from(data.getString(KEY_RECIPIENT_ID)),
                                    parameters);
    }
  }
}
