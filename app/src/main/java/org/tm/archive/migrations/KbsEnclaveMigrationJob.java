package org.tm.archive.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.jobs.KbsEnclaveMigrationWorkerJob;

/**
 * A job to be run whenever we add a new KBS enclave. In order to prevent this moderately-expensive
 * task from blocking the network for too long, this task simply enqueues another non-migration job,
 * {@link KbsEnclaveMigrationWorkerJob}, to do the heavy lifting.
 */
public class KbsEnclaveMigrationJob extends MigrationJob {

  public static final String KEY = "KbsEnclaveMigrationJob";

  KbsEnclaveMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private KbsEnclaveMigrationJob(@NonNull Parameters parameters) {
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
  public void performMigration() {
    ApplicationDependencies.getJobManager().add(new KbsEnclaveMigrationWorkerJob());
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<KbsEnclaveMigrationJob> {
    @Override
    public @NonNull KbsEnclaveMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new KbsEnclaveMigrationJob(parameters);
    }
  }
}
