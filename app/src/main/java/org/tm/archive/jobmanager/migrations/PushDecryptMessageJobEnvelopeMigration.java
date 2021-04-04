package org.tm.archive.jobmanager.migrations;

import android.content.Context;

import androidx.annotation.NonNull;

import org.signal.core.util.logging.Log;
import org.tm.archive.database.DatabaseFactory;
import org.tm.archive.database.NoSuchMessageException;
import org.tm.archive.database.PushDatabase;
import org.tm.archive.groups.BadGroupIdException;
import org.tm.archive.groups.GroupId;
import org.tm.archive.jobmanager.Data;
import org.tm.archive.jobmanager.JobMigration;
import org.tm.archive.jobs.FailingJob;
import org.tm.archive.recipients.Recipient;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.util.Base64;
import org.tm.archive.util.GroupUtil;
import org.whispersystems.signalservice.api.messages.SignalServiceContent;
import org.whispersystems.signalservice.api.messages.SignalServiceEnvelope;

import java.io.IOException;

/**
 * We removed the messageId property from the job data and replaced it with a serialized envelope,
 * so we need to take jobs that referenced an ID and replace it with the envelope instead.
 */
public class PushDecryptMessageJobEnvelopeMigration extends JobMigration {

  private static final String TAG = Log.tag(PushDecryptMessageJobEnvelopeMigration.class);

  private final PushDatabase pushDatabase;

  public PushDecryptMessageJobEnvelopeMigration(@NonNull Context context) {
    super(8);
    this.pushDatabase = DatabaseFactory.getPushDatabase(context);
  }

  @Override
  protected @NonNull JobData migrate(@NonNull JobData jobData) {
    if ("PushDecryptJob".equals(jobData.getFactoryKey())) {
      Log.i(TAG, "Found a PushDecryptJob to migrate.");
      return migratePushDecryptMessageJob(pushDatabase, jobData);
    } else {
      return jobData;
    }
  }

  private static @NonNull JobData migratePushDecryptMessageJob(@NonNull PushDatabase pushDatabase, @NonNull JobData jobData) {
    Data data = jobData.getData();

    if (data.hasLong("message_id")) {
      long messageId = data.getLong("message_id");
      try {
        SignalServiceEnvelope envelope = pushDatabase.get(messageId);
        return jobData.withData(jobData.getData()
                                       .buildUpon()
                                       .putBlobAsString("envelope", envelope.serialize())
                                       .build());
      } catch (NoSuchMessageException e) {
        Log.w(TAG, "Failed to find envelope in DB! Failing.");
        return jobData.withFactoryKey(FailingJob.KEY);
      }
    } else {
      Log.w(TAG, "No message_id property?");
      return jobData;
    }
  }
}
