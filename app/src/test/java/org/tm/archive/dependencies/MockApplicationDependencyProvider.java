package org.tm.archive.dependencies;

import androidx.annotation.NonNull;

import org.signal.core.util.concurrent.DeadlockDetector;
import org.signal.zkgroup.receipts.ClientZkReceiptOperations;
import org.tm.archive.components.TypingStatusRepository;
import org.tm.archive.components.TypingStatusSender;
import org.tm.archive.crypto.storage.SignalServiceDataStoreImpl;
import org.tm.archive.database.DatabaseObserver;
import org.tm.archive.database.PendingRetryReceiptCache;
import org.tm.archive.jobmanager.JobManager;
import org.tm.archive.megaphone.MegaphoneRepository;
import org.tm.archive.messages.BackgroundMessageRetriever;
import org.tm.archive.messages.IncomingMessageObserver;
import org.tm.archive.messages.IncomingMessageProcessor;
import org.tm.archive.notifications.MessageNotifier;
import org.tm.archive.payments.Payments;
import org.tm.archive.push.SignalServiceNetworkAccess;
import org.tm.archive.recipients.LiveRecipientCache;
import org.tm.archive.revealable.ViewOnceMessageManager;
import org.tm.archive.service.ExpiringMessageManager;
import org.tm.archive.service.PendingRetryReceiptManager;
import org.tm.archive.service.TrimThreadsByDateManager;
import org.tm.archive.service.webrtc.SignalCallManager;
import org.tm.archive.shakereport.ShakeToReport;
import org.tm.archive.util.AppForegroundObserver;
import org.tm.archive.util.EarlyMessageCache;
import org.tm.archive.util.FrameRateTracker;
import org.tm.archive.video.exo.GiphyMp4Cache;
import org.tm.archive.video.exo.SimpleExoPlayerPool;
import org.tm.archive.webrtc.audio.AudioManagerCompat;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.SignalServiceDataStore;
import org.whispersystems.signalservice.api.SignalServiceMessageReceiver;
import org.whispersystems.signalservice.api.SignalServiceMessageSender;
import org.whispersystems.signalservice.api.SignalWebSocket;
import org.whispersystems.signalservice.api.groupsv2.GroupsV2Operations;
import org.whispersystems.signalservice.api.services.DonationsService;

import static org.mockito.Mockito.mock;

public class MockApplicationDependencyProvider implements ApplicationDependencies.Provider {
  @Override
  public @NonNull GroupsV2Operations provideGroupsV2Operations() {
    return null;
  }

  @Override
  public @NonNull SignalServiceAccountManager provideSignalServiceAccountManager() {
    return null;
  }

  @Override
  public @NonNull SignalServiceMessageSender provideSignalServiceMessageSender(@NonNull SignalWebSocket signalWebSocket, @NonNull SignalServiceDataStore protocolStore) {
    return null;
  }

  @Override
  public @NonNull SignalServiceMessageReceiver provideSignalServiceMessageReceiver() {
    return null;
  }

  @Override
  public @NonNull SignalServiceNetworkAccess provideSignalServiceNetworkAccess() {
    return null;
  }

  @Override
  public @NonNull IncomingMessageProcessor provideIncomingMessageProcessor() {
    return null;
  }

  @Override
  public @NonNull BackgroundMessageRetriever provideBackgroundMessageRetriever() {
    return null;
  }

  @Override
  public @NonNull LiveRecipientCache provideRecipientCache() {
    return null;
  }

  @Override
  public @NonNull JobManager provideJobManager() {
    return null;
  }

  @Override
  public @NonNull FrameRateTracker provideFrameRateTracker() {
    return null;
  }

  @Override
  public @NonNull MegaphoneRepository provideMegaphoneRepository() {
    return null;
  }

  @Override
  public @NonNull EarlyMessageCache provideEarlyMessageCache() {
    return null;
  }

  @Override
  public @NonNull MessageNotifier provideMessageNotifier() {
    return null;
  }

  @Override
  public @NonNull IncomingMessageObserver provideIncomingMessageObserver() {
    return null;
  }

  @Override
  public @NonNull TrimThreadsByDateManager provideTrimThreadsByDateManager() {
    return null;
  }

  @Override
  public @NonNull ViewOnceMessageManager provideViewOnceMessageManager() {
    return null;
  }

  @Override
  public @NonNull ExpiringMessageManager provideExpiringMessageManager() {
    return null;
  }

  @Override
  public @NonNull TypingStatusRepository provideTypingStatusRepository() {
    return null;
  }

  @Override
  public @NonNull TypingStatusSender provideTypingStatusSender() {
    return null;
  }

  @Override
  public @NonNull DatabaseObserver provideDatabaseObserver() {
    return mock(DatabaseObserver.class);
  }

  @Override
  public @NonNull Payments providePayments(@NonNull SignalServiceAccountManager signalServiceAccountManager) {
    return null;
  }

  @Override
  public @NonNull ShakeToReport provideShakeToReport() {
    return null;
  }

  @Override
  public @NonNull AppForegroundObserver provideAppForegroundObserver() {
    return mock(AppForegroundObserver.class);
  }

  @Override
  public @NonNull SignalCallManager provideSignalCallManager() {
    return null;
  }

  @Override
  public @NonNull PendingRetryReceiptManager providePendingRetryReceiptManager() {
    return null;
  }

  @Override
  public @NonNull PendingRetryReceiptCache providePendingRetryReceiptCache() {
    return null;
  }

  @Override
  public @NonNull SignalWebSocket provideSignalWebSocket() {
    return null;
  }

  @Override
  public @NonNull SignalServiceDataStoreImpl provideProtocolStore() {
    return null;
  }

  @Override
  public @NonNull GiphyMp4Cache provideGiphyMp4Cache() {
    return null;
  }

  @Override
  public @NonNull SimpleExoPlayerPool provideExoPlayerPool() {
    return null;
  }

  @Override
  public @NonNull AudioManagerCompat provideAndroidCallAudioManager() {
    return null;
  }

  @Override
  public @NonNull DonationsService provideDonationsService() {
    return null;
  }

  @Override
  public @NonNull DeadlockDetector provideDeadlockDetector() {
    return null;
  }

  @Override
  public @NonNull ClientZkReceiptOperations provideClientZkReceiptOperations() {
    return null;
  }
}
