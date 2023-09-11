package org.tm.archive.migrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.signal.core.util.SqlUtil;
import org.tm.archive.database.EmojiSearchTable;
import org.tm.archive.database.SignalDatabase;
import org.tm.archive.dependencies.ApplicationDependencies;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.jobs.DownloadLatestEmojiDataJob;
import org.tm.archive.jobs.EmojiSearchIndexDownloadJob;
import org.tm.archive.keyvalue.SignalStore;

/**
 * Schedules job to get the latest emoji search index if it's empty.
 */
public final class EmojiSearchIndexCheckMigrationJob extends MigrationJob {

  public static final String KEY = "EmojiSearchIndexCheckMigrationJob";

  EmojiSearchIndexCheckMigrationJob() {
    this(new Parameters.Builder().build());
  }

  private EmojiSearchIndexCheckMigrationJob(@NonNull Parameters parameters) {
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
    if (SqlUtil.isEmpty(SignalDatabase.getRawDatabase(), EmojiSearchTable.TABLE_NAME)) {
      SignalStore.emojiValues().clearSearchIndexMetadata();
      EmojiSearchIndexDownloadJob.scheduleImmediately();
    }
  }

  @Override
  boolean shouldRetry(@NonNull Exception e) {
    return false;
  }

  public static class Factory implements Job.Factory<EmojiSearchIndexCheckMigrationJob> {
    @Override
    public @NonNull EmojiSearchIndexCheckMigrationJob create(@NonNull Parameters parameters, @Nullable byte[] serializedData) {
      return new EmojiSearchIndexCheckMigrationJob(parameters);
    }
  }
}
