package org.tm.archive.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.logging.Log;
import org.signal.libsignal.protocol.state.PreKeyRecord;
import org.signal.libsignal.protocol.state.SignedPreKeyRecord;
import org.tm.archive.crypto.PreKeyUtil;
import org.tm.archive.crypto.storage.PreKeyMetadataStore;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.jobmanager.impl.NetworkConstraint;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.recipients.Recipient;
import org.whispersystems.signalservice.api.SignalServiceAccountDataStore;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.account.PreKeyUpload;
import org.whispersystems.signalservice.api.push.ServiceId.PNI;
import org.whispersystems.signalservice.api.push.ServiceIdType;

import java.io.IOException;
import java.util.List;

/**
 * Initializes various aspects of the PNI identity. Notably:
 * - Creates an identity key
 * - Creates and uploads one-time prekeys
 * - Creates and uploads signed prekeys
 */
public class PniAccountInitializationMigrationJob extends MigrationJob {

  private static final String TAG = Log.tag(PniAccountInitializationMigrationJob.class);

  public static final String KEY = "PniAccountInitializationMigrationJob";

  PniAccountInitializationMigrationJob() {
    this(new Parameters.Builder()
                       .addConstraint(NetworkConstraint.KEY)
                       .build());
  }

  private PniAccountInitializationMigrationJob(@NonNull Parameters parameters) {
    super(parameters);
  }

  @Override
  public boolean isUiBlocking() {
    return false;
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  public void performMigration() throws IOException {
    PNI pni = SignalStore.account().getPni();

    if (pni == null || SignalStore.account().getAci() == null || !Recipient.self().isRegistered()) {
      Log.w(TAG, "Not yet registered! No need to perform this migration.");
      return;
    }

    if (!SignalStore.account().hasPniIdentityKey()) {
      Log.i(TAG, "Generating PNI identity.");
      SignalStore.account().generatePniIdentityKeyIfNecessary();
    } else {
      Log.w(TAG, "Already generated the PNI identity. Skipping this step.");
    }

    SignalServiceAccountManager   accountManager = ApplicationDependencies.getSignalServiceAccountManager();
    SignalServiceAccountDataStore protocolStore  = ApplicationDependencies.getProtocolStore().pni();
    PreKeyMetadataStore           metadataStore  = SignalStore.account().pniPreKeys();

    if (!metadataStore.isSignedPreKeyRegistered()) {
      Log.i(TAG, "Uploading signed prekey for PNI.");
      SignedPreKeyRecord signedPreKey   = PreKeyUtil.generateAndStoreSignedPreKey(protocolStore, metadataStore);
      List<PreKeyRecord> oneTimePreKeys = PreKeyUtil.generateAndStoreOneTimeEcPreKeys(protocolStore, metadataStore);

      accountManager.setPreKeys(new PreKeyUpload(ServiceIdType.PNI, signedPreKey, oneTimePreKeys, null, null));
      metadataStore.setActiveSignedPreKeyId(signedPreKey.getId());
      metadataStore.setSignedPreKeyRegistered(true);
    } else {
      Log.w(TAG, "Already uploaded signed prekey for PNI. Skipping this step.");
    }
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return e instanceof IOException;
  }

  public static class Factory implements Job.Factory<PniAccountInitializationMigrationJob> {
    @Override
    public @NonNull PniAccountInitializationMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new PniAccountInitializationMigrationJob(parameters);
    }
  }
}
