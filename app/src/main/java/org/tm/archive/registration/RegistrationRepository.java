package org.tm.archive.registration;

import android.app.Application;
import android.app.backup.BackupManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.core.app.NotificationManagerCompat;

import org.signal.core.util.logging.Log;
import org.signal.libsignal.protocol.IdentityKeyPair;
import org.signal.libsignal.protocol.state.KyberPreKeyRecord;
import org.signal.libsignal.protocol.state.SignedPreKeyRecord;
import org.signal.libsignal.protocol.util.KeyHelper;
import org.signal.libsignal.zkgroup.profiles.ProfileKey;
import org.tm.archive.crypto.PreKeyUtil;
import org.tm.archive.crypto.ProfileKeyUtil;
import org.tm.archive.crypto.SenderKeyUtil;
import org.tm.archive.crypto.storage.PreKeyMetadataStore;
import org.tm.archive.crypto.storage.SignalServiceAccountDataStoreImpl;
import org.tm.archive.database.IdentityTable;
import org.tm.archive.database.RecipientTable;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobmanager.JobManager;
import org.tm.archive.jobs.DirectoryRefreshJob;
import org.tm.archive.jobs.PreKeysSyncJob;
import org.tm.archive.jobs.RotateCertificateJob;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.notifications.NotificationIds;
import org.tm.archive.pin.SvrRepository;
import org.tm.archive.push.AccountManagerFactory;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.service.DirectoryRefreshListener;
import org.tm.archive.service.RotateSignedPreKeyListener;
import org.tm.archive.util.TextSecurePreferences;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.account.PreKeyCollection;
import org.whispersystems.signalservice.api.push.ServiceId.ACI;
import org.whispersystems.signalservice.api.push.ServiceId.PNI;
import org.whispersystems.signalservice.api.push.ServiceId;
import org.whispersystems.signalservice.api.push.SignalServiceAddress;
import org.whispersystems.signalservice.api.util.Preconditions;
import org.whispersystems.signalservice.internal.ServiceResponse;
import org.whispersystems.signalservice.internal.push.BackupAuthCheckProcessor;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Operations required for finalizing the registration of an account. This is
 * to be used after verifying the code and registration lock (if necessary) with
 * the server and being issued a UUID.
 */
public final class RegistrationRepository {

  private static final String TAG = Log.tag(RegistrationRepository.class);

  private final Application context;

  public RegistrationRepository(@NonNull Application context) {
    this.context = context;
  }

  public int getRegistrationId() {
    int registrationId = SignalStore.account().getRegistrationId();
    if (registrationId == 0) {
      registrationId = KeyHelper.generateRegistrationId(false);
      SignalStore.account().setRegistrationId(registrationId);
    }
    return registrationId;
  }

  public int getPniRegistrationId() {
    int pniRegistrationId = SignalStore.account().getPniRegistrationId();
    if (pniRegistrationId == 0) {
      pniRegistrationId = KeyHelper.generateRegistrationId(false);
      SignalStore.account().setPniRegistrationId(pniRegistrationId);
    }
    return pniRegistrationId;
  }

  public @NonNull ProfileKey getProfileKey(@NonNull String e164) {
    ProfileKey profileKey = findExistingProfileKey(e164);

    if (profileKey == null) {
      profileKey = ProfileKeyUtil.createNew();
      Log.i(TAG, "No profile key found, created a new one");
    }

    return profileKey;
  }

  public Single<ServiceResponse<VerifyResponse>> registerAccount(@NonNull RegistrationData registrationData,
                                                                 @NonNull VerifyResponse response,
                                                                 boolean setRegistrationLockEnabled)
  {
    return Single.<ServiceResponse<VerifyResponse>>fromCallable(() -> {
      try {
        registerAccountInternal(registrationData, response, setRegistrationLockEnabled);

        JobManager jobManager = ApplicationDependencies.getJobManager();
        jobManager.add(new DirectoryRefreshJob(false));
        jobManager.add(new RotateCertificateJob());

        DirectoryRefreshListener.schedule(context);
        RotateSignedPreKeyListener.schedule(context);

        return ServiceResponse.forResult(response, 200, null);
      } catch (IOException e) {
        return ServiceResponse.forUnknownError(e);
      }
    }).subscribeOn(Schedulers.io());
  }

  @WorkerThread
  private void registerAccountInternal(@NonNull RegistrationData registrationData,
                                       @NonNull VerifyResponse response,
                                       boolean setRegistrationLockEnabled)
      throws IOException
  {
    Preconditions.checkNotNull(response.getAciPreKeyCollection(), "Missing ACI prekey collection!");
    Preconditions.checkNotNull(response.getPniPreKeyCollection(), "Missing PNI prekey collection!");

    ACI     aci    = ACI.parseOrThrow(response.getVerifyAccountResponse().getUuid());
    PNI     pni    = PNI.parseOrThrow(response.getVerifyAccountResponse().getPni());
    boolean hasPin = response.getVerifyAccountResponse().isStorageCapable();

    SignalStore.account().setAci(aci);
    SignalStore.account().setPni(pni);

    ApplicationDependencies.getProtocolStore().aci().sessions().archiveAllSessions();
    ApplicationDependencies.getProtocolStore().pni().sessions().archiveAllSessions();
    SenderKeyUtil.clearAllState();

    SignalServiceAccountDataStoreImpl aciProtocolStore = ApplicationDependencies.getProtocolStore().aci();
    PreKeyMetadataStore               aciMetadataStore = SignalStore.account().aciPreKeys();

    SignalServiceAccountDataStoreImpl pniProtocolStore = ApplicationDependencies.getProtocolStore().pni();
    PreKeyMetadataStore               pniMetadataStore = SignalStore.account().pniPreKeys();

    storeSignedAndLastResortPreKeys(aciProtocolStore, aciMetadataStore, response.getAciPreKeyCollection());
    storeSignedAndLastResortPreKeys(pniProtocolStore, pniMetadataStore, response.getPniPreKeyCollection());

    RecipientTable recipientTable = SignalDatabase.recipients();
    RecipientId    selfId         = Recipient.trustedPush(aci, pni, registrationData.getE164()).getId();

    recipientTable.setProfileSharing(selfId, true);
    recipientTable.markRegisteredOrThrow(selfId, aci);
    recipientTable.linkIdsForSelf(aci, pni, registrationData.getE164());
    recipientTable.setProfileKey(selfId, registrationData.getProfileKey());

    ApplicationDependencies.getRecipientCache().clearSelf();

    SignalStore.account().setE164(registrationData.getE164());
    SignalStore.account().setFcmToken(registrationData.getFcmToken());
    SignalStore.account().setFcmEnabled(registrationData.isFcm());

    long now = System.currentTimeMillis();
    saveOwnIdentityKey(selfId, aci, aciProtocolStore, now);
    saveOwnIdentityKey(selfId, pni, pniProtocolStore, now);

    SignalStore.account().setServicePassword(registrationData.getPassword());
    SignalStore.account().setRegistered(true);
    TextSecurePreferences.setPromptedPushRegistration(context, true);
    TextSecurePreferences.setUnauthorizedReceived(context, false);
    NotificationManagerCompat.from(context).cancel(NotificationIds.UNREGISTERED_NOTIFICATION_ID);

    SvrRepository.onRegistrationComplete(response.getMasterKey(), response.getPin(), hasPin, setRegistrationLockEnabled);

    ApplicationDependencies.closeConnections();
    ApplicationDependencies.getIncomingMessageObserver();
    PreKeysSyncJob.enqueue();
  }

  public static PreKeyCollection generateSignedAndLastResortPreKeys(IdentityKeyPair identity, PreKeyMetadataStore metadataStore) {
    SignedPreKeyRecord      signedPreKey          = PreKeyUtil.generateSignedPreKey(metadataStore.getNextSignedPreKeyId(), identity.getPrivateKey());
    KyberPreKeyRecord       lastResortKyberPreKey = PreKeyUtil.generateLastRestortKyberPreKey(metadataStore.getNextKyberPreKeyId(), identity.getPrivateKey());

    return new PreKeyCollection(
        identity.getPublicKey(),
        signedPreKey,
        lastResortKyberPreKey
    );
  }

  private static void storeSignedAndLastResortPreKeys(SignalServiceAccountDataStoreImpl protocolStore, PreKeyMetadataStore metadataStore, PreKeyCollection preKeyCollection) {
    PreKeyUtil.storeSignedPreKey(protocolStore, metadataStore, preKeyCollection.getSignedPreKey());
    metadataStore.setSignedPreKeyRegistered(true);
    metadataStore.setActiveSignedPreKeyId(preKeyCollection.getSignedPreKey().getId());
    metadataStore.setLastSignedPreKeyRotationTime(System.currentTimeMillis());

    PreKeyUtil.storeLastResortKyberPreKey(protocolStore, metadataStore, preKeyCollection.getLastResortKyberPreKey());
    metadataStore.setLastResortKyberPreKeyId(preKeyCollection.getLastResortKyberPreKey().getId());
    metadataStore.setLastResortKyberPreKeyRotationTime(System.currentTimeMillis());
  }

  private void saveOwnIdentityKey(@NonNull RecipientId selfId, @NonNull ServiceId serviceId, @NonNull SignalServiceAccountDataStoreImpl protocolStore, long now) {
    protocolStore.identities().saveIdentityWithoutSideEffects(selfId,
                                                              serviceId,
                                                              protocolStore.getIdentityKeyPair().getPublicKey(),
                                                              IdentityTable.VerifiedStatus.VERIFIED,
                                                              true,
                                                              now,
                                                              true);
  }

  @WorkerThread
  private static @Nullable ProfileKey findExistingProfileKey(@NonNull String e164number) {
    RecipientTable        recipientTable = SignalDatabase.recipients();
    Optional<RecipientId> recipient      = recipientTable.getByE164(e164number);

    if (recipient.isPresent()) {
      return ProfileKeyUtil.profileKeyOrNull(Recipient.resolved(recipient.get()).getProfileKey());
    }

    return null;
  }

  public Single<BackupAuthCheckProcessor> getSvrAuthCredential(@NonNull RegistrationData registrationData, List<String> usernamePasswords) {
    SignalServiceAccountManager accountManager = AccountManagerFactory.getInstance().createUnauthenticated(context, registrationData.getE164(), SignalServiceAddress.DEFAULT_DEVICE_ID, registrationData.getPassword());

    Log.d(TAG, "Fetching SVR backup credentials.");
    return accountManager.checkBackupAuthCredentials(registrationData.getE164(), usernamePasswords)
                         .map(BackupAuthCheckProcessor::new)
                         .doOnSuccess(processor -> {
                           Log.d(TAG, "Received SVR backup auth credential response.");
                           if (SignalStore.svr().removeAuthTokens(processor.getInvalid())) {
                             new BackupManager(context).dataChanged();
                           }
                         });
  }

}
