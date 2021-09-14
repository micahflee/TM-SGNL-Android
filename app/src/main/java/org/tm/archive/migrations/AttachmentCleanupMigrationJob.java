package org.tm.archive.migrations;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import org.tm.archive.database.DatabaseFactory;
import org.tm.archive.jobmanager.Data;
import org.tm.archive.jobmanager.Job;

/**
 * Check for abandoned attachments and delete them.
 */
public class AttachmentCleanupMigrationJob extends MigrationJob {

  private static final String TAG = Log.tag(AttachmentCleanupMigrationJob.class);

  public static final String KEY = "AttachmentCleanupMigrationJob";

  AttachmentCleanupMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private AttachmentCleanupMigrationJob(@NonNull Parameters parameters) {
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
    int deletes = DatabaseFactory.getAttachmentDatabase(context).deleteAbandonedAttachmentFiles();
    Log.i(TAG, "Deleted " + deletes + " abandoned attachments.");
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<AttachmentCleanupMigrationJob> {
    @Override
    public @NonNull AttachmentCleanupMigrationJob create(@NonNull Parameters parameters, @NonNull Data data) {
      return new AttachmentCleanupMigrationJob(parameters);
    }
  }
}
