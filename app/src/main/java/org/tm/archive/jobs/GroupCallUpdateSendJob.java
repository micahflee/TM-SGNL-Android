package org.tm.archive.jobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.annimon.stream.Stream;

import org.signal.core.util.logging.Log;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobmanager.Data;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.messages.GroupSendUtil;
import org.tm.archive.net.NotPushRegisteredException;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.recipients.RecipientUtil;
import org.tm.archive.transport.RetryLaterException;
import org.tm.archive.util.GroupUtil;
import org.whispersystems.signalservice.api.crypto.ContentHint;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.messages.SendMessageResult;
import org.whispersystems.signalservice.api.messages.SignalServiceDataMessage;
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Send a group call update message to every one in a V2 group. Used to indicate you
 * have joined or left a call.
 */
public class GroupCallUpdateSendJob extends BaseJob {

  public static final String KEY = "GroupCallUpdateSendJob";

  private static final String TAG = Log.tag(GroupCallUpdateSendJob.class);

  private static final String KEY_RECIPIENT_ID            = "recipient_id";
  private static final String KEY_ERA_ID                  = "era_id";
  private static final String KEY_RECIPIENTS              = "recipients";
  private static final String KEY_INITIAL_RECIPIENT_COUNT = "initial_recipient_count";

  private final RecipientId       recipientId;
  private final String            eraId;
  private final List<RecipientId> recipients;
  private final int               initialRecipientCount;

  @WorkerThread
  public static @NonNull GroupCallUpdateSendJob create(@NonNull RecipientId recipientId, @Nullable String eraId) {
    Recipient conversationRecipient = Recipient.resolved(recipientId);

    if (!conversationRecipient.isPushV2Group()) {
      throw new AssertionError("We have a recipient, but it's not a V2 Group");
    }

    List<RecipientId> recipientIds = Stream.of(RecipientUtil.getEligibleForSending(Recipient.resolvedList(conversationRecipient.getParticipantIds())))
                                           .filterNot(Recipient::isSelf)
                                           .map(Recipient::getId)
                                           .toList();

    return new GroupCallUpdateSendJob(recipientId,
                                      eraId,
                                      recipientIds,
                                      recipientIds.size(),
                                      new Parameters.Builder()
                                                    .setQueue(conversationRecipient.getId().toQueueKey())
                                                    .setLifespan(TimeUnit.MINUTES.toMillis(5))
                                                    .setMaxAttempts(3)
                                                    .build());
  }

  private GroupCallUpdateSendJob(@NonNull RecipientId recipientId,
                                 @NonNull String eraId,
                                 @NonNull List<RecipientId> recipients,
                                 int initialRecipientCount,
                                 @NonNull Parameters parameters)
  {
    super(parameters);

    this.recipientId           = recipientId;
    this.eraId                 = eraId;
    this.recipients            = recipients;
    this.initialRecipientCount = initialRecipientCount;
  }

  @Override
  public @NonNull Data serialize() {
    return new Data.Builder().putString(KEY_RECIPIENT_ID, recipientId.serialize())
                             .putString(KEY_ERA_ID, eraId)
                             .putString(KEY_RECIPIENTS, RecipientId.toSerializedList(recipients))
                             .putInt(KEY_INITIAL_RECIPIENT_COUNT, initialRecipientCount)
                             .build();
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  protected void onRun() throws Exception {
    if (!Recipient.self().isRegistered()) {
      throw new NotPushRegisteredException();
    }

    Recipient conversationRecipient = Recipient.resolved(recipientId);

    if (!conversationRecipient.isPushV2Group()) {
      throw new AssertionError("We have a recipient, but it's not a V2 Group");
    }

    List<Recipient> destinations = Stream.of(recipients).map(Recipient::resolved).toList();
    List<Recipient> completions  = deliver(conversationRecipient, destinations);

    for (Recipient completion : completions) {
      recipients.remove(completion.getId());
    }

    Log.i(TAG, "Completed now: " + completions.size() + ", Remaining: " + recipients.size());

    if (!recipients.isEmpty()) {
      Log.w(TAG, "Still need to send to " + recipients.size() + " recipients. Retrying.");
      throw new RetryLaterException();
    }
  }

  @Override
  protected boolean onShouldRetry(@NonNull Exception e) {
    if (e instanceof ServerRejectedException) return false;
    return e instanceof IOException ||
           e instanceof RetryLaterException;
  }

  @Override
  public void onFailure() {
    if (recipients.size() < initialRecipientCount) {
      Log.w(TAG, "Only sent a group update to " + recipients.size() + "/" + initialRecipientCount + " recipients. Still, it sent to someone, so it stays.");
      return;
    }

    Log.w(TAG, "Failed to send the group update to all recipients!");
  }

  private @NonNull List<Recipient> deliver(@NonNull Recipient conversationRecipient, @NonNull List<Recipient> destinations)
      throws IOException, UntrustedIdentityException
  {
    SignalServiceDataMessage.Builder dataMessageBuilder = SignalServiceDataMessage.newBuilder()
                                                                                  .withTimestamp(System.currentTimeMillis())
                                                                                  .withGroupCallUpdate(new SignalServiceDataMessage.GroupCallUpdate(eraId));

    GroupUtil.setDataMessageGroupContext(context, dataMessageBuilder, conversationRecipient.requireGroupId().requirePush());

    SignalServiceDataMessage dataMessage         = dataMessageBuilder.build();
    List<Recipient>          nonSelfDestinations = destinations.stream().filter(r -> !r.isSelf()).collect(Collectors.toList());
    boolean                  includesSelf        = nonSelfDestinations.size() != destinations.size();
    List<SendMessageResult>  results             = GroupSendUtil.sendUnresendableDataMessage(context,
                                                                                             conversationRecipient.requireGroupId().requireV2(),
                                                                                             nonSelfDestinations,
                                                                                             false,
                                                                                             ContentHint.DEFAULT,
                                                                                             dataMessage,
                                                                                             false);

    if (includesSelf) {
      results.add(ApplicationDependencies.getSignalServiceMessageSender().sendSyncMessage(dataMessage));
    }

    return GroupSendJobHelper.getCompletedSends(destinations, results).completed;
  }

  public static class Factory implements Job.Factory<GroupCallUpdateSendJob> {

    @Override
    public @NonNull
    GroupCallUpdateSendJob create(@NonNull Parameters parameters, @NonNull Data data) {
      RecipientId       recipientId           = RecipientId.from(data.getString(KEY_RECIPIENT_ID));
      String            eraId                 = data.getString(KEY_ERA_ID);
      List<RecipientId> recipients            = RecipientId.fromSerializedList(data.getString(KEY_RECIPIENTS));
      int               initialRecipientCount = data.getInt(KEY_INITIAL_RECIPIENT_COUNT);

      return new GroupCallUpdateSendJob(recipientId, eraId, recipients, initialRecipientCount, parameters);
    }
  }
}
