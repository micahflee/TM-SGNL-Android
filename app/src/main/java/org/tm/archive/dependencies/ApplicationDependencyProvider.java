package org.tm.archive.dependencies;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;

import org.signal.core.util.concurrent.SignalExecutors;
import org.tm.archive.BuildConfig;
import org.tm.archive.components.TypingStatusRepository;
import org.tm.archive.components.TypingStatusSender;
import org.tm.archive.crypto.ReentrantSessionLock;
import org.tm.archive.crypto.storage.SignalProtocolStoreImpl;
import org.tm.archive.crypto.storage.SignalSenderKeyStore;
import org.tm.archive.crypto.storage.TextSecureIdentityKeyStore;
import org.tm.archive.crypto.storage.TextSecurePreKeyStore;
import org.tm.archive.crypto.storage.TextSecureSessionStore;
import org.tm.archive.database.DatabaseObserver;
import org.tm.archive.database.JobDatabase;
import org.tm.archive.database.PendingRetryReceiptCache;
import org.tm.archive.jobmanager.JobManager;
import org.tm.archive.jobmanager.JobMigrator;
import org.tm.archive.jobmanager.impl.FactoryJobPredicate;
import org.tm.archive.jobmanager.impl.JsonDataSerializer;
import org.tm.archive.jobs.FastJobStorage;
import org.tm.archive.jobs.GroupCallUpdateSendJob;
import org.tm.archive.jobs.JobManagerFactories;
import org.tm.archive.jobs.MarkerJob;
import org.tm.archive.jobs.PushDecryptMessageJob;
import org.tm.archive.jobs.PushGroupSendJob;
import org.tm.archive.jobs.PushMediaSendJob;
import org.tm.archive.jobs.PushProcessMessageJob;
import org.tm.archive.jobs.PushTextSendJob;
import org.tm.archive.jobs.ReactionSendJob;
import org.tm.archive.jobs.TypingSendJob;
import org.tm.archive.megaphone.MegaphoneRepository;
import org.tm.archive.messages.BackgroundMessageRetriever;
import org.tm.archive.messages.IncomingMessageObserver;
import org.tm.archive.messages.IncomingMessageProcessor;
import org.tm.archive.net.SignalWebSocketHealthMonitor;
import org.tm.archive.notifications.MessageNotifier;
import org.tm.archive.notifications.OptimizedMessageNotifier;
import org.tm.archive.payments.MobileCoinConfig;
import org.tm.archive.payments.Payments;
import org.tm.archive.push.SecurityEventListener;
import org.tm.archive.push.SignalServiceNetworkAccess;
import org.tm.archive.recipients.LiveRecipientCache;
import org.tm.archive.revealable.ViewOnceMessageManager;
import org.tm.archive.service.ExpiringMessageManager;
import org.tm.archive.service.PendingRetryReceiptManager;
import org.tm.archive.service.TrimThreadsByDateManager;
import org.tm.archive.service.webrtc.SignalCallManager;
import org.tm.archive.shakereport.ShakeToReport;
import org.tm.archive.util.AlarmSleepTimer;
import org.tm.archive.util.AppForegroundObserver;
import org.tm.archive.util.ByteUnit;
import org.tm.archive.util.EarlyMessageCache;
import org.tm.archive.util.FeatureFlags;
import org.tm.archive.util.FrameRateTracker;
import org.tm.archive.util.TextSecurePreferences;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.util.guava.Optional;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.SignalServiceMessageReceiver;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.SignalWebSocket;
import org.whispersystems.signalservice.api.groupsv2.ClientZkOperations;
import org.whispersystems.signalservice.api.groupsv2.GroupsV2Operations;
import org.whispersystems.signalservice.api.util.CredentialsProvider;
import org.whispersystems.signalservice.api.util.SleepTimer;
import org.whispersystems.signalservice.api.util.UptimeSleepTimer;
import org.whispersystems.signalservice.api.websocket.WebSocketFactory;
import org.whispersystems.signalservice.internal.websocket.WebSocketConnection;

import java.util.UUID;

/**
 * Implementation of {@link ApplicationDependencies.Provider} that provides real app dependencies.
 */
public class ApplicationDependencyProvider implements ApplicationDependencies.Provider {

  private final Application context;

  public ApplicationDependencyProvider(@NonNull Application context) {
    this.context = context;
  }

  private @NonNull ClientZkOperations provideClientZkOperations() {
    return ClientZkOperations.create(provideSignalServiceNetworkAccess().getConfiguration(context));
  }

  @Override
  public @NonNull GroupsV2Operations provideGroupsV2Operations() {
    return new GroupsV2Operations(provideClientZkOperations());
  }

  @Override
  public @NonNull SignalServiceAccountManager provideSignalServiceAccountManager() {
    return new SignalServiceAccountManager(provideSignalServiceNetworkAccess().getConfiguration(context),
                                           new DynamicCredentialsProvider(context),
                                           BuildConfig.SIGNAL_AGENT,
                                           provideGroupsV2Operations(),
                                           FeatureFlags.okHttpAutomaticRetry());
  }

  @Override
  public @NonNull SignalServiceMessageSender provideSignalServiceMessageSender(@NonNull SignalWebSocket signalWebSocket) {
      return new SignalServiceMessageSender(provideSignalServiceNetworkAccess().getConfiguration(context),
                                            new DynamicCredentialsProvider(context),
                                            new SignalProtocolStoreImpl(context),
                                            ReentrantSessionLock.INSTANCE,
                                            BuildConfig.SIGNAL_AGENT,
                                            signalWebSocket,
                                            Optional.of(new SecurityEventListener(context)),
                                            provideClientZkOperations().getProfileOperations(),
                                            SignalExecutors.newCachedBoundedExecutor("signal-messages", 1, 16),
                                            ByteUnit.KILOBYTES.toBytes(512),
                                            FeatureFlags.okHttpAutomaticRetry());
  }

  @Override
  public @NonNull SignalServiceMessageReceiver provideSignalServiceMessageReceiver() {
    return new SignalServiceMessageReceiver(provideSignalServiceNetworkAccess().getConfiguration(context),
                                            new DynamicCredentialsProvider(context),
                                            BuildConfig.SIGNAL_AGENT,
                                            provideClientZkOperations().getProfileOperations(),
                                            FeatureFlags.okHttpAutomaticRetry());
  }

  @Override
  public @NonNull SignalServiceNetworkAccess provideSignalServiceNetworkAccess() {
    return new SignalServiceNetworkAccess(context);
  }

  @Override
  public @NonNull IncomingMessageProcessor provideIncomingMessageProcessor() {
    return new IncomingMessageProcessor(context);
  }

  @Override
  public @NonNull BackgroundMessageRetriever provideBackgroundMessageRetriever() {
    return new BackgroundMessageRetriever();
  }

  @Override
  public @NonNull LiveRecipientCache provideRecipientCache() {
    return new LiveRecipientCache(context);
  }

  @Override
  public @NonNull JobManager provideJobManager() {
    JobManager.Configuration config = new JobManager.Configuration.Builder()
                                                                  .setDataSerializer(new JsonDataSerializer())
                                                                  .setJobFactories(JobManagerFactories.getJobFactories(context))
                                                                  .setConstraintFactories(JobManagerFactories.getConstraintFactories(context))
                                                                  .setConstraintObservers(JobManagerFactories.getConstraintObservers(context))
                                                                  .setJobStorage(new FastJobStorage(JobDatabase.getInstance(context)))
                                                                  .setJobMigrator(new JobMigrator(TextSecurePreferences.getJobManagerVersion(context), JobManager.CURRENT_VERSION, JobManagerFactories.getJobMigrations(context)))
                                                                  .addReservedJobRunner(new FactoryJobPredicate(PushDecryptMessageJob.KEY, PushProcessMessageJob.KEY, MarkerJob.KEY))
                                                                  .addReservedJobRunner(new FactoryJobPredicate(PushTextSendJob.KEY, PushMediaSendJob.KEY, PushGroupSendJob.KEY, ReactionSendJob.KEY, TypingSendJob.KEY, GroupCallUpdateSendJob.KEY))
                                                                  .build();
    return new JobManager(context, config);
  }

  @Override
  public @NonNull FrameRateTracker provideFrameRateTracker() {
    return new FrameRateTracker(context);
  }

  public @NonNull MegaphoneRepository provideMegaphoneRepository() {
    return new MegaphoneRepository(context);
  }

  @Override
  public @NonNull EarlyMessageCache provideEarlyMessageCache() {
    return new EarlyMessageCache();
  }

  @Override
  public @NonNull MessageNotifier provideMessageNotifier() {
    return new OptimizedMessageNotifier(context);
  }

  @Override
  public @NonNull IncomingMessageObserver provideIncomingMessageObserver() {
    return new IncomingMessageObserver(context);
  }

  @Override
  public @NonNull TrimThreadsByDateManager provideTrimThreadsByDateManager() {
    return new TrimThreadsByDateManager(context);
  }

  @Override
  public @NonNull ViewOnceMessageManager provideViewOnceMessageManager() {
    return new ViewOnceMessageManager(context);
  }

  @Override
  public @NonNull ExpiringMessageManager provideExpiringMessageManager() {
    return new ExpiringMessageManager(context);
  }

  @Override
  public @NonNull TypingStatusRepository provideTypingStatusRepository() {
    return new TypingStatusRepository();
  }

  @Override
  public @NonNull TypingStatusSender provideTypingStatusSender() {
    return new TypingStatusSender();
  }

  @Override
  public @NonNull DatabaseObserver provideDatabaseObserver() {
    return new DatabaseObserver(context);
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public @NonNull Payments providePayments(@NonNull SignalServiceAccountManager signalServiceAccountManager) {
    MobileCoinConfig network;

    if      (BuildConfig.MOBILE_COIN_ENVIRONMENT.equals("mainnet")) network = MobileCoinConfig.getMainNet(signalServiceAccountManager);
    else if (BuildConfig.MOBILE_COIN_ENVIRONMENT.equals("testnet")) network = MobileCoinConfig.getTestNet(signalServiceAccountManager);
    else throw new AssertionError("Unknown network " + BuildConfig.MOBILE_COIN_ENVIRONMENT);

    return new Payments(network);
  }

  @Override
  public @NonNull ShakeToReport provideShakeToReport() {
    return new ShakeToReport(context);
  }

  @Override
  public @NonNull AppForegroundObserver provideAppForegroundObserver() {
    return new AppForegroundObserver();
  }

  @Override
  public @NonNull SignalCallManager provideSignalCallManager() {
    return new SignalCallManager(context);
  }

  @Override
  public @NonNull PendingRetryReceiptManager providePendingRetryReceiptManager() {
    return new PendingRetryReceiptManager(context);
  }

  @Override
  public @NonNull PendingRetryReceiptCache providePendingRetryReceiptCache() {
    return new PendingRetryReceiptCache(context);
  }

  @Override
  public @NonNull SignalWebSocket provideSignalWebSocket() {
    SleepTimer                   sleepTimer      = TextSecurePreferences.isFcmDisabled(context) ? new AlarmSleepTimer(context) : new UptimeSleepTimer();
    SignalWebSocketHealthMonitor healthMonitor   = new SignalWebSocketHealthMonitor(context, sleepTimer);
    SignalWebSocket              signalWebSocket = new SignalWebSocket(provideWebSocketFactory(healthMonitor));

    healthMonitor.monitor(signalWebSocket);

    return signalWebSocket;
  }

  @Override
  public @NonNull TextSecureIdentityKeyStore provideIdentityStore() {
    return new TextSecureIdentityKeyStore(context);
  }

  @Override
  public @NonNull TextSecureSessionStore provideSessionStore() {
    return new TextSecureSessionStore(context);
  }

  @Override
  public @NonNull TextSecurePreKeyStore providePreKeyStore() {
    return new TextSecurePreKeyStore(context);
  }

  @Override
  public @NonNull SignalSenderKeyStore provideSenderKeyStore() {
    return new SignalSenderKeyStore(context);
  }

  private @NonNull WebSocketFactory provideWebSocketFactory(@NonNull SignalWebSocketHealthMonitor healthMonitor) {
    return new WebSocketFactory() {
      @Override
      public WebSocketConnection createWebSocket() {
        return new WebSocketConnection("normal",
                                       provideSignalServiceNetworkAccess().getConfiguration(context),
                                       Optional.of(new DynamicCredentialsProvider(context)),
                                       BuildConfig.SIGNAL_AGENT,
                                       healthMonitor);
      }

      @Override
      public WebSocketConnection createUnidentifiedWebSocket() {
        return new WebSocketConnection("unidentified",
                                       provideSignalServiceNetworkAccess().getConfiguration(context),
                                       Optional.absent(),
                                       BuildConfig.SIGNAL_AGENT,
                                       healthMonitor);
      }
    };
  }

  private static class DynamicCredentialsProvider implements CredentialsProvider {

    private final Context context;

    private DynamicCredentialsProvider(Context context) {
      this.context = context.getApplicationContext();
    }

    @Override
    public UUID getUuid() {
      return TextSecurePreferences.getLocalUuid(context);
    }

    @Override
    public String getE164() {
      return TextSecurePreferences.getLocalNumber(context);
    }

    @Override
    public String getPassword() {
      return TextSecurePreferences.getPushServerPassword(context);
    }
  }
}
