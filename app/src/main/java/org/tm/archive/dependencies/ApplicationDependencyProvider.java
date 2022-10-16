package org.tm.archive.dependencies;

import android.app.Application;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import org.signal.core.util.Hex;
import org.signal.core.util.concurrent.DeadlockDetector;
import org.signal.core.util.concurrent.SignalExecutors;
import org.signal.libsignal.zkgroup.profiles.ClientZkProfileOperations;
import org.signal.libsignal.zkgroup.receipts.ClientZkReceiptOperations;
import org.tm.archive.BuildConfig;
import org.tm.archive.KbsEnclave;
import org.tm.archive.components.TypingStatusRepository;
import org.tm.archive.components.TypingStatusSender;
import org.tm.archive.crypto.ReentrantSessionLock;
import org.tm.archive.crypto.storage.SignalBaseIdentityKeyStore;
import org.tm.archive.crypto.storage.SignalIdentityKeyStore;
import org.tm.archive.crypto.storage.SignalSenderKeyStore;
import org.tm.archive.crypto.storage.SignalServiceAccountDataStoreImpl;
import org.tm.archive.crypto.storage.SignalServiceDataStoreImpl;
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
import org.tm.archive.jobs.PreKeysSyncJob;
import org.tm.archive.jobs.PushDecryptMessageJob;
import org.tm.archive.jobs.PushGroupSendJob;
import org.tm.archive.jobs.PushMediaSendJob;
import org.tm.archive.jobs.PushProcessMessageJob;
import org.tm.archive.jobs.PushTextSendJob;
import org.tm.archive.jobs.ReactionSendJob;
import org.tm.archive.jobs.TypingSendJob;
import org.tm.archive.keyvalue.SignalStore;
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
import org.tm.archive.service.ExpiringStoriesManager;
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
import org.tm.archive.video.exo.GiphyMp4Cache;
import org.tm.archive.video.exo.SimpleExoPlayerPool;
import org.tm.archive.webrtc.audio.AudioManagerCompat;
import org.whispersystems.signalservice.api.KeyBackupService;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.SignalServiceDataStore;
import org.whispersystems.signalservice.api.SignalServiceMessageReceiver;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.SignalWebSocket;
import org.whispersystems.signalservice.api.groupsv2.ClientZkOperations;
import org.whispersystems.signalservice.api.groupsv2.GroupsV2Operations;
import org.whispersystems.signalservice.api.push.ACI;
import org.whispersystems.signalservice.api.push.PNI;
import org.whispersystems.signalservice.api.services.DonationsService;
import org.whispersystems.signalservice.api.services.ProfileService;
import org.whispersystems.signalservice.api.util.CredentialsProvider;
import org.whispersystems.signalservice.api.util.SleepTimer;
import org.whispersystems.signalservice.api.util.UptimeSleepTimer;
import org.whispersystems.signalservice.api.websocket.WebSocketFactory;
import org.whispersystems.signalservice.internal.configuration.SignalServiceConfiguration;
import org.whispersystems.signalservice.internal.websocket.WebSocketConnection;

import java.security.KeyStore;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Implementation of {@link ApplicationDependencies.Provider} that provides real app dependencies.
 */
public class ApplicationDependencyProvider implements ApplicationDependencies.Provider {

  private final Application context;

  public ApplicationDependencyProvider(@NonNull Application context) {
    this.context = context;
  }

  private @NonNull ClientZkOperations provideClientZkOperations(@NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return ClientZkOperations.create(signalServiceConfiguration);
  }

  @Override
  public @NonNull GroupsV2Operations provideGroupsV2Operations(@NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return new GroupsV2Operations(provideClientZkOperations(signalServiceConfiguration), FeatureFlags.groupLimits().getHardLimit());
  }

  @Override
  public @NonNull SignalServiceAccountManager provideSignalServiceAccountManager(@NonNull SignalServiceConfiguration signalServiceConfiguration, @NonNull GroupsV2Operations groupsV2Operations) {
    return new SignalServiceAccountManager(signalServiceConfiguration,
                                           new DynamicCredentialsProvider(),
                                           BuildConfig.SIGNAL_AGENT,
                                           groupsV2Operations,
                                           FeatureFlags.okHttpAutomaticRetry());
  }

  @Override
  public @NonNull SignalServiceMessageSender provideSignalServiceMessageSender(@NonNull SignalWebSocket signalWebSocket, @NonNull SignalServiceDataStore protocolStore, @NonNull SignalServiceConfiguration signalServiceConfiguration) {
      return new SignalServiceMessageSender(signalServiceConfiguration,
                                            new DynamicCredentialsProvider(),
                                            protocolStore,
                                            ReentrantSessionLock.INSTANCE,
                                            BuildConfig.SIGNAL_AGENT,
                                            signalWebSocket,
                                            Optional.of(new SecurityEventListener(context)),
                                            provideGroupsV2Operations(signalServiceConfiguration).getProfileOperations(),
                                            SignalExecutors.newCachedBoundedExecutor("signal-messages", 1, 16, 30),
                                            ByteUnit.KILOBYTES.toBytes(256),
                                            FeatureFlags.okHttpAutomaticRetry());
  }

  @Override
  public @NonNull SignalServiceMessageReceiver provideSignalServiceMessageReceiver(@NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return new SignalServiceMessageReceiver(signalServiceConfiguration,
                                            new DynamicCredentialsProvider(),
                                            BuildConfig.SIGNAL_AGENT,
                                            provideGroupsV2Operations(signalServiceConfiguration).getProfileOperations(),
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
  public @NonNull ExpiringStoriesManager provideExpiringStoriesManager() {
    return new ExpiringStoriesManager(context);
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
    return new PendingRetryReceiptCache();
  }

  @Override
  public @NonNull SignalWebSocket provideSignalWebSocket(@NonNull Supplier<SignalServiceConfiguration> signalServiceConfigurationSupplier) {
    SleepTimer                   sleepTimer      = SignalStore.account().isFcmEnabled() ? new UptimeSleepTimer() : new AlarmSleepTimer(context);
    SignalWebSocketHealthMonitor healthMonitor   = new SignalWebSocketHealthMonitor(context, sleepTimer);
    SignalWebSocket              signalWebSocket = new SignalWebSocket(provideWebSocketFactory(signalServiceConfigurationSupplier, healthMonitor));

    healthMonitor.monitor(signalWebSocket);

    return signalWebSocket;
  }

  @Override
  public @NonNull SignalServiceDataStoreImpl provideProtocolStore() {
    ACI localAci = SignalStore.account().getAci();
    PNI localPni = SignalStore.account().getPni();

    if (localAci == null) {
      throw new IllegalStateException("No ACI set!");
    }

    if (localPni == null) {
      throw new IllegalStateException("No PNI set!");
    }

    boolean needsPreKeyJob = false;

    if (!SignalStore.account().hasAciIdentityKey()) {
      SignalStore.account().generateAciIdentityKeyIfNecessary();
      needsPreKeyJob = true;
    }

    if (!SignalStore.account().hasPniIdentityKey()) {
      SignalStore.account().generatePniIdentityKeyIfNecessary();
      needsPreKeyJob = true;
    }

    if (needsPreKeyJob) {
      PreKeysSyncJob.enqueueIfNeeded();
    }

    SignalBaseIdentityKeyStore baseIdentityStore = new SignalBaseIdentityKeyStore(context);

    SignalServiceAccountDataStoreImpl aciStore = new SignalServiceAccountDataStoreImpl(context,
                                                                                       new TextSecurePreKeyStore(localAci),
                                                                                       new SignalIdentityKeyStore(baseIdentityStore, () -> SignalStore.account().getAciIdentityKey()),
                                                                                       new TextSecureSessionStore(localAci),
                                                                                       new SignalSenderKeyStore(context));

    SignalServiceAccountDataStoreImpl pniStore = new SignalServiceAccountDataStoreImpl(context,
                                                                                       new TextSecurePreKeyStore(localPni),
                                                                                       new SignalIdentityKeyStore(baseIdentityStore, () -> SignalStore.account().getPniIdentityKey()),
                                                                                       new TextSecureSessionStore(localPni),
                                                                                       new SignalSenderKeyStore(context));
    return new SignalServiceDataStoreImpl(context, aciStore, pniStore);
  }

  @Override
  public @NonNull GiphyMp4Cache provideGiphyMp4Cache() {
    return new GiphyMp4Cache(ByteUnit.MEGABYTES.toBytes(16));
  }

  @Override
  public @NonNull SimpleExoPlayerPool provideExoPlayerPool() {
    return new SimpleExoPlayerPool(context);
  }

  @Override
  public @NonNull AudioManagerCompat provideAndroidCallAudioManager() {
    return AudioManagerCompat.create(context);
  }

  @Override
  public @NonNull DonationsService provideDonationsService(@NonNull SignalServiceConfiguration signalServiceConfiguration, @NonNull GroupsV2Operations groupsV2Operations) {
    return new DonationsService(signalServiceConfiguration,
                                new DynamicCredentialsProvider(),
                                BuildConfig.SIGNAL_AGENT,
                                groupsV2Operations,
                                FeatureFlags.okHttpAutomaticRetry());
  }

  @Override
  public @NonNull ProfileService provideProfileService(@NonNull ClientZkProfileOperations clientZkProfileOperations,
                                                       @NonNull SignalServiceMessageReceiver receiver,
                                                       @NonNull SignalWebSocket signalWebSocket)
  {
    return new ProfileService(clientZkProfileOperations, receiver, signalWebSocket);
  }

  @Override
  public @NonNull DeadlockDetector provideDeadlockDetector() {
    HandlerThread handlerThread = new HandlerThread("signal-DeadlockDetector");
    handlerThread.start();
    return new DeadlockDetector(new Handler(handlerThread.getLooper()), TimeUnit.SECONDS.toMillis(5));
  }

  @Override
  public @NonNull ClientZkReceiptOperations provideClientZkReceiptOperations(@NonNull SignalServiceConfiguration signalServiceConfiguration) {
    return provideClientZkOperations(signalServiceConfiguration).getReceiptOperations();
  }

  @Override
  public @NonNull KeyBackupService provideKeyBackupService(@NonNull SignalServiceAccountManager signalServiceAccountManager, @NonNull KeyStore keyStore, @NonNull KbsEnclave enclave) {
    return signalServiceAccountManager.getKeyBackupService(keyStore,
                                                           enclave.getEnclaveName(),
                                                           Hex.fromStringOrThrow(enclave.getServiceId()),
                                                           enclave.getMrEnclave(),
                                                           10);
  }

  @NonNull WebSocketFactory provideWebSocketFactory(@NonNull Supplier<SignalServiceConfiguration> signalServiceConfigurationSupplier, @NonNull SignalWebSocketHealthMonitor healthMonitor) {
    return new WebSocketFactory() {
      @Override
      public WebSocketConnection createWebSocket() {
        return new WebSocketConnection("normal",
                                       signalServiceConfigurationSupplier.get(),
                                       Optional.of(new DynamicCredentialsProvider()),
                                       BuildConfig.SIGNAL_AGENT,
                                       healthMonitor);
      }

      @Override
      public WebSocketConnection createUnidentifiedWebSocket() {
        return new WebSocketConnection("unidentified",
                                       signalServiceConfigurationSupplier.get(),
                                       Optional.empty(),
                                       BuildConfig.SIGNAL_AGENT,
                                       healthMonitor);
      }
    };
  }

  @VisibleForTesting
  static class DynamicCredentialsProvider implements CredentialsProvider {

    @Override
    public ACI getAci() {
      return SignalStore.account().getAci();
    }

    @Override
    public PNI getPni() {
      return SignalStore.account().getPni();
    }

    @Override
    public String getE164() {
      return SignalStore.account().getE164();
    }

    @Override
    public String getPassword() {
      return SignalStore.account().getServicePassword();
    }

    @Override
    public int getDeviceId() {
      return SignalStore.account().getDeviceId();
    }
  }
}
