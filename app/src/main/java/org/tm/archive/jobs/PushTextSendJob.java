package org.tm.archive.jobs;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import org.tm.archive.crypto.UnidentifiedAccessUtil;
import org.tm.archive.database.MessageDatabase;
import org.tm.archive.database.MessageDatabase.SyncMessageId;
import org.tm.archive.database.NoSuchMessageException;
import org.tm.archive.database.RecipientDatabase.UnidentifiedAccessMode;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.database.model.MessageId;
import org.tm.archive.database.model.SmsMessageRecord;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobmanager.Data;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.notifications.v2.ConversationId;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.recipients.RecipientUtil;
import org.tm.archive.service.ExpiringMessageManager;
import org.tm.archive.transport.InsecureFallbackApprovalException;
import org.tm.archive.transport.RetryLaterException;
import org.tm.archive.transport.UndeliverableMessageException;
import org.tm.archive.util.SignalLocalMetrics;
import org.tm.archive.util.Util;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.crypto.ContentHint;
import org.whispersystems.signalservice.api.crypto.UnidentifiedAccessPair;
import org.whispersystems.signalservice.api.crypto.UntrustedIdentityException;
import org.whispersystems.signalservice.api.messages.SendMessageResult;
import org.whispersystems.signalservice.api.messages.SignalServiceDataMessage;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;
import org.whispersystems.signalservice.api.push.exceptions.ProofRequiredException;
import org.whispersystems.signalservice.api.push.exceptions.ServerRejectedException;
import org.whispersystems.signalservice.api.push.exceptions.UnregisteredUserException;

import java.io.IOException;
import java.util.Optional;

public class PushTextSendJob extends PushSendJob {

  public static final String KEY = "PushTextSendJob";

  private static final String TAG = Log.tag(PushTextSendJob.class);

  private static final String KEY_MESSAGE_ID = "message_id";

  private final long messageId;

  public PushTextSendJob(long messageId, @NonNull Recipient recipient) {
    this(constructParameters(recipient, false), messageId);
  }

  private PushTextSendJob(@NonNull Job.Parameters parameters, long messageId) {
    super(parameters);
    this.messageId = messageId;
  }

  @Override
  public @NonNull Data serialize() {
    return new Data.Builder().putLong(KEY_MESSAGE_ID, messageId).build();
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void onAdded() {
    SignalDatabase.sms().markAsSending(messageId);
  }

  @Override
  public void onPushSend() throws IOException, NoSuchMessageException, UndeliverableMessageException, RetryLaterException {
    SignalLocalMetrics.IndividualMessageSend.onJobStarted(messageId);

    ExpiringMessageManager expirationManager = ApplicationDependencies.getExpiringMessageManager();
    MessageDatabase        database          = SignalDatabase.sms();
    SmsMessageRecord       record            = database.getSmsMessage(messageId);

    if (!record.isPending() && !record.isFailed()) {
      warn(TAG, String.valueOf(record.getDateSent()), "Message " + messageId + " was already sent. Ignoring.");
      return;
    }

    try {
      log(TAG, String.valueOf(record.getDateSent()), "Sending message: " + messageId + ",  Recipient: " + record.getRecipient().getId() + ", Thread: " + record.getThreadId());

      RecipientUtil.shareProfileIfFirstSecureMessage(context, record.getRecipient());

      Recipient              recipient  = record.getRecipient().resolve();
      byte[]                 profileKey = recipient.getProfileKey();
      UnidentifiedAccessMode accessMode = recipient.getUnidentifiedAccessMode();

      boolean unidentified = deliver(record);

      database.markAsSent(messageId, true);
      database.markUnidentified(messageId, unidentified);

      if (recipient.isSelf()) {
        SyncMessageId id = new SyncMessageId(recipient.getId(), record.getDateSent());
        SignalDatabase.mmsSms().incrementDeliveryReceiptCount(id, System.currentTimeMillis());
        SignalDatabase.mmsSms().incrementReadReceiptCount(id, System.currentTimeMillis());
      }

      if (unidentified && accessMode == UnidentifiedAccessMode.UNKNOWN && profileKey == null) {
        log(TAG, String.valueOf(record.getDateSent()), "Marking recipient as UD-unrestricted following a UD send.");
        SignalDatabase.recipients().setUnidentifiedAccessMode(recipient.getId(), UnidentifiedAccessMode.UNRESTRICTED);
      } else if (unidentified && accessMode == UnidentifiedAccessMode.UNKNOWN) {
        log(TAG, String.valueOf(record.getDateSent()), "Marking recipient as UD-enabled following a UD send.");
        SignalDatabase.recipients().setUnidentifiedAccessMode(recipient.getId(), UnidentifiedAccessMode.ENABLED);
      } else if (!unidentified && accessMode != UnidentifiedAccessMode.DISABLED) {
        log(TAG, String.valueOf(record.getDateSent()), "Marking recipient as UD-disabled following a non-UD send.");
        SignalDatabase.recipients().setUnidentifiedAccessMode(recipient.getId(), UnidentifiedAccessMode.DISABLED);
      }

      if (record.getExpiresIn() > 0) {
        database.markExpireStarted(messageId);
        expirationManager.scheduleDeletion(record.getId(), record.isMms(), record.getExpiresIn());
      }

      log(TAG, String.valueOf(record.getDateSent()), "Sent message: " + messageId);

    } catch (InsecureFallbackApprovalException e) {
      warn(TAG, String.valueOf(record.getDateSent()), "Failure", e);
      database.markAsPendingInsecureSmsFallback(record.getId());
      ApplicationDependencies.getMessageNotifier().notifyMessageDeliveryFailed(context, record.getRecipient(), ConversationId.forConversation(record.getThreadId()));
      ApplicationDependencies.getJobManager().add(new DirectoryRefreshJob(false));
    } catch (UntrustedIdentityException e) {
      warn(TAG, String.valueOf(record.getDateSent()), "Failure", e);
      RecipientId recipientId = Recipient.external(context, e.getIdentifier()).getId();
      database.addMismatchedIdentity(record.getId(), recipientId, e.getIdentityKey());
      database.markAsSentFailed(record.getId());
      database.markAsPush(record.getId());
      RetrieveProfileJob.enqueue(recipientId);
    } catch (ProofRequiredException e) {
      handleProofRequiredException(context, e, record.getRecipient(), record.getThreadId(), messageId, false);
    }

    SignalLocalMetrics.IndividualMessageSend.onJobFinished(messageId);
  }

  @Override
  public void onRetry() {
    SignalLocalMetrics.IndividualMessageSend.cancel(messageId);
    super.onRetry();
  }

  @Override
  public void onFailure() {
    SignalDatabase.sms().markAsSentFailed(messageId);

    long      threadId  = SignalDatabase.sms().getThreadIdForMessage(messageId);
    Recipient recipient = SignalDatabase.threads().getRecipientForThreadId(threadId);

    if (threadId != -1 && recipient != null) {
      ApplicationDependencies.getMessageNotifier().notifyMessageDeliveryFailed(context, recipient, ConversationId.forConversation(threadId));
    }
  }

  private boolean deliver(SmsMessageRecord message)
      throws UntrustedIdentityException, InsecureFallbackApprovalException, UndeliverableMessageException, IOException
  {
    try {
      rotateSenderCertificateIfNecessary();

      Recipient messageRecipient = message.getIndividualRecipient().resolve();

      if (messageRecipient.isUnregistered()) {
        throw new UndeliverableMessageException(messageRecipient.getId() + " not registered!");
      }

      SignalServiceMessageSender       messageSender      = ApplicationDependencies.getSignalServiceMessageSender();
      SignalServiceAddress             address            = RecipientUtil.toSignalServiceAddress(context, messageRecipient);
      Optional<byte[]>                 profileKey         = getProfileKey(messageRecipient);
      Optional<UnidentifiedAccessPair> unidentifiedAccess = UnidentifiedAccessUtil.getAccessFor(context, messageRecipient);

      log(TAG, String.valueOf(message.getDateSent()), "Have access key to use: " + unidentifiedAccess.isPresent());

      SignalServiceDataMessage textSecureMessage = SignalServiceDataMessage.newBuilder()
                                                                           .withTimestamp(message.getDateSent())
                                                                           .withBody(message.getBody())
                                                                           .withExpiration((int)(message.getExpiresIn() / 1000))
                                                                           .withProfileKey(profileKey.orElse(null))
                                                                           .asEndSessionMessage(message.isEndSession())
                                                                           .build();

      if (Util.equals(SignalStore.account().getAci(), address.getServiceId())) {
        Optional<UnidentifiedAccessPair> syncAccess  = UnidentifiedAccessUtil.getAccessForSync(context);

        SignalLocalMetrics.IndividualMessageSend.onDeliveryStarted(messageId);
        SendMessageResult result = messageSender.sendSyncMessage(textSecureMessage);

        SignalDatabase.messageLog().insertIfPossible(messageRecipient.getId(), message.getDateSent(), result, ContentHint.RESENDABLE, new MessageId(messageId, false), false);
        return syncAccess.isPresent();
      } else {
        SignalLocalMetrics.IndividualMessageSend.onDeliveryStarted(messageId);
        SendMessageResult result = messageSender.sendDataMessage(address, unidentifiedAccess, ContentHint.RESENDABLE, textSecureMessage, new MetricEventListener(messageId), true, messageRecipient.needsPniSignature());

        SignalDatabase.messageLog().insertIfPossible(messageRecipient.getId(), message.getDateSent(), result, ContentHint.RESENDABLE, new MessageId(messageId, false), true);

        if (messageRecipient.needsPniSignature()) {
          SignalDatabase.pendingPniSignatureMessages().insertIfNecessary(messageRecipient.getId(), message.getDateSent(), result);
        }

        return result.getSuccess().isUnidentified();
      }
    } catch (UnregisteredUserException e) {
      warn(TAG, "Failure", e);
      throw new InsecureFallbackApprovalException(e);
    } catch (ServerRejectedException e) {
      throw new UndeliverableMessageException(e);
    }
  }

  public static long getMessageId(@NonNull Data data) {
    return data.getLong(KEY_MESSAGE_ID);
  }

  private static class MetricEventListener implements SignalServiceMessageSender.IndividualSendEvents {
    private final long messageId;

    private MetricEventListener(long messageId) {
      this.messageId = messageId;
    }

    @Override
    public void onMessageEncrypted() {
      SignalLocalMetrics.IndividualMessageSend.onMessageEncrypted(messageId);
    }

    @Override
    public void onMessageSent() {
      SignalLocalMetrics.IndividualMessageSend.onMessageSent(messageId);
    }

    @Override
    public void onSyncMessageSent() {
      SignalLocalMetrics.IndividualMessageSend.onSyncMessageSent(messageId);
    }
  }

  public static class Factory implements Job.Factory<PushTextSendJob> {
    @Override
    public @NonNull PushTextSendJob create(@NonNull Parameters parameters, @NonNull Data data) {
      return new PushTextSendJob(parameters, data.getLong(KEY_MESSAGE_ID));
    }
  }
}
