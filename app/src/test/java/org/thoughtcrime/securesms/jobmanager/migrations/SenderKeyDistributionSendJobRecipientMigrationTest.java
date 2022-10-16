package org.tm.archive.jobmanager.migrations;

import org.junit.Test;
import org.tm.archive.database.GroupDatabase;
import org.tm.archive.database.MmsSmsDatabase;
import org.tm.archive.groups.GroupId;
import org.tm.archive.jobmanager.Data;
import org.tm.archive.jobmanager.JobMigration;
import org.tm.archive.jobs.FailingJob;
import org.tm.archive.jobs.SendReadReceiptJob;
import org.tm.archive.jobs.SenderKeyDistributionSendJob;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.util.Util;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SenderKeyDistributionSendJobRecipientMigrationTest {

  private final GroupDatabase                                  mockDatabase = mock(GroupDatabase.class);
  private final SenderKeyDistributionSendJobRecipientMigration testSubject  = new SenderKeyDistributionSendJobRecipientMigration(mockDatabase);

  private static final GroupId GROUP_ID = GroupId.pushOrThrow(Util.getSecretBytes(32));

  @Test
  public void normalMigration() {
    // GIVEN
    JobMigration.JobData jobData = new JobMigration.JobData(SenderKeyDistributionSendJob.KEY,
                                                            "asdf",
                                                            new Data.Builder()
                                                                    .putString("recipient_id", RecipientId.from(1).serialize())
                                                                    .putBlobAsString("group_id", GROUP_ID.getDecodedId())
                                                                    .build());

    GroupDatabase.GroupRecord mockGroup = mock(GroupDatabase.GroupRecord.class);
    when(mockGroup.getRecipientId()).thenReturn(RecipientId.from(2));
    when(mockDatabase.getGroup(GROUP_ID)).thenReturn(Optional.of(mockGroup));

    // WHEN
    JobMigration.JobData result = testSubject.migrate(jobData);

    // THEN
    assertEquals(RecipientId.from(1).serialize(), result.getData().getString("recipient_id"));
    assertEquals(RecipientId.from(2).serialize(), result.getData().getString("thread_recipient_id"));
  }

  @Test
  public void cannotFindGroup() {
    // GIVEN
    JobMigration.JobData jobData = new JobMigration.JobData(SenderKeyDistributionSendJob.KEY,
                                                            "asdf",
                                                            new Data.Builder()
                                                                .putString("recipient_id", RecipientId.from(1).serialize())
                                                                .putBlobAsString("group_id", GROUP_ID.getDecodedId())
                                                                .build());

    // WHEN
    JobMigration.JobData result = testSubject.migrate(jobData);

    // THEN
    assertEquals(FailingJob.KEY, result.getFactoryKey());
  }

  @Test
  public void missingGroupId() {
    // GIVEN
    JobMigration.JobData jobData = new JobMigration.JobData(SenderKeyDistributionSendJob.KEY,
                                                            "asdf",
                                                            new Data.Builder()
                                                                .putString("recipient_id", RecipientId.from(1).serialize())
                                                                .build());

    // WHEN
    JobMigration.JobData result = testSubject.migrate(jobData);

    // THEN
    assertEquals(FailingJob.KEY, result.getFactoryKey());
  }
}