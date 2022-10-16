package org.tm.archive.migrations;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobmanager.Data;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.jobmanager.impl.NetworkConstraint;
import org.tm.archive.keyvalue.SignalStore;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.whispersystems.signalservice.api.push.PNI;

import java.io.IOException;

/**
 * Migration to fetch our own PNI from the service.
 */
public class PniMigrationJob extends MigrationJob {

  public static final String KEY = "PniMigrationJob";

  private static final String TAG = Log.tag(PniMigrationJob.class);

  PniMigrationJob() {
    this(new Parameters.Builder().addConstraint(NetworkConstraint.KEY).build());
  }

  private PniMigrationJob(@NonNull Parameters parameters) {
    super(parameters);
  }

  @Override
  public @NonNull String getFactoryKey() {
    return KEY;
  }

  @Override
  boolean isUiBlocking() {
    return false;
  }

  @Override
  void performMigration() throws Exception {
    if (!SignalStore.account().isRegistered() || SignalStore.account().getAci() == null) {
      Log.w(TAG, "Not registered! Skipping migration, as it wouldn't do anything.");
      return;
    }

    PNI pni = PNI.parseOrNull(ApplicationDependencies.getSignalServiceAccountManager().getWhoAmI().getPni());

    if (pni == null) {
      throw new IOException("Invalid PNI!");
    }

    SignalDatabase.recipients().linkIdsForSelf(SignalStore.account().requireAci(), pni, SignalStore.account().requireE164());
    SignalStore.account().setPni(pni);
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return e instanceof IOException;
  }

  public static class Factory implements Job.Factory<PniMigrationJob> {
    @Override
    public @NonNull PniMigrationJob create(@NonNull Parameters parameters, @NonNull Data data) {
      return new PniMigrationJob(parameters);
    }
  }
}
