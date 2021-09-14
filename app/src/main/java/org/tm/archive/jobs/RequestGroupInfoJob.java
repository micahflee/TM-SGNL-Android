package org.tm.archive.jobs;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import org.tm.archive.crypto.UnidentifiedAccessUtil;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.groups.GroupId;
import org.tm.archive.jobmanager.Data;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.jobmanager.impl.NetworkConstraint;
import org.tm.archive.net.NotPushRegisteredException;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.recipients.RecipientUtil;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.SignalServiceMessageSender.IndividualSendEvents;
import org.whispersystems.signalservice.api.crypto.ContentHint;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.messages.SignalServiceDataMessage;
import org.whispersystems.signalservice.api.messages.SignalServiceGroup;
import org.whispersystems.signalservice.api.messages.SignalServiceGroup.Type;
import org.whispersystems.signalservice.api.push.exceptions.PushNetworkException;
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RequestGroupInfoJob extends BaseJob {

  public static final String KEY = "RequestGroupInfoJob";

  @SuppressWarnings("unused")
  private static final String TAG = Log.tag(RequestGroupInfoJob.class);

  private static final String KEY_SOURCE   = "source";
  private static final String KEY_GROUP_ID = "group_id";

  private final RecipientId source;
  private final GroupId     groupId;

  public RequestGroupInfoJob(@NonNull RecipientId source, @NonNull GroupId groupId) {
    this(new Job.Parameters.Builder()
                           .addConstraint(NetworkConstraint.KEY)
                           .setLifespan(TimeUnit.DAYS.toMillis(1))
                           .setMaxAttempts(Parameters.UNLIMITED)
                           .build(),
         source,
         groupId);

  }

  private RequestGroupInfoJob(@NonNull Job.Parameters parameters, @NonNull RecipientId source, @NonNull GroupId groupId) {
    super(parameters);

    this.source  = source;
    this.groupId = groupId;
  }

  @Override
  public @NonNull Data serialize() {
    return new Data.Builder().putString(KEY_SOURCE, source.serialize())
                             .putString(KEY_GROUP_ID, groupId.toString())
                             .build();
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void onRun() throws IOException, UntrustedIdentityException {
    if (!Recipient.self().isRegistered()) {
      throw new NotPushRegisteredException();
    }

    SignalServiceGroup       group   = SignalServiceGroup.newBuilder(Type.REQUEST_INFO)
                                                         .withId(groupId.getDecodedId())
                                                         .build();

    SignalServiceDataMessage message = SignalServiceDataMessage.newBuilder()
                                                               .asGroupMessage(group)
                                                               .withTimestamp(System.currentTimeMillis())
                                                               .build();

    SignalServiceMessageSender messageSender = ApplicationDependencies.getSignalServiceMessageSender();
    Recipient                  recipient     = Recipient.resolved(source);

    if (recipient.isUnregistered()) {
      Log.w(TAG, recipient.getId() + " is unregistered!");
      return;
    }

    messageSender.sendDataMessage(RecipientUtil.toSignalServiceAddress(context, recipient),
                                  UnidentifiedAccessUtil.getAccessFor(context, recipient),
                                  ContentHint.IMPLICIT,
                                  message,
                                  IndividualSendEvents.EMPTY);
  }

  @Override
  public boolean onShouldRetry(@NonNull Exception e) {
    if (e instanceof ServerRejectedException) return false;
    return e instanceof PushNetworkException;
  }

  @Override
  public void onFailure() {

  }

  public static final class Factory implements Job.Factory<RequestGroupInfoJob> {

    @Override
    public @NonNull RequestGroupInfoJob create(@NonNull Parameters parameters, @NonNull Data data) {
      return new RequestGroupInfoJob(parameters,
                                     RecipientId.from(data.getString(KEY_SOURCE)),
                                     GroupId.parseOrThrow(data.getString(KEY_GROUP_ID)));
    }
  }
}
