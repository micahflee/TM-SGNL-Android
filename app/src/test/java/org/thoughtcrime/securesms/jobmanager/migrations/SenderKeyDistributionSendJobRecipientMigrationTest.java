package org.tm.archive.jobmanager.migrations;

import org.junit.Test;
import org.tm.archive.database.GroupTable;
import org.tm.archive.database.model.GroupRecord;
import org.tm.archive.groups.GroupId;
import org.tm.archive.jobmanager.JsonJobData;
import org.tm.archive.jobmanager.JobMigration;
import org.tm.archive.jobs.FailingJob;
import org.tm.archive.jobs.SenderKeyDistributionSendJob;
import org.tm.archive.recipients.RecipientId;
import org.tm.archive.util.Util;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SenderKeyDistributionSendJobRecipientMigrationTest {

  private final GroupTable                                     mockDatabase = mock(GroupTable.class);
  private final SenderKeyDistributionSendJobRecipientMigration testSubject  = new SenderKeyDistributionSendJobRecipientMigration(mockDatabase);

  private static final GroupId GROUP_ID = GroupId.pushOrThrow(Util.getSecretBytes(32));

  @Test
  public void normalMigration() {
    // GIVEN
    JobMigration.JobData jobData = new JobMigration.JobData(SenderKeyDistributionSendJob.KEY,
                                                            "asdf",
                                                            new JsonJobData.Builder()
                                                                    .putString("recipient_id", RecipientId.from(1).serialize())
                                                                    .putBlobAsString("group_id", GROUP_ID.getDecodedId())
                                                                    .serialize());

    GroupRecord mockGroup = mock(GroupRecord.class);
    when(mockGroup.getRecipientId()).thenReturn(RecipientId.from(2));
    when(mockDatabase.getGroup(GROUP_ID)).thenReturn(Optional.of(mockGroup));

    // WHEN
    JobMigration.JobData result = testSubject.migrate(jobData);
    JsonJobData          data   = JsonJobData.deserialize(result.getData());

    // THEN
    assertEquals(RecipientId.from(1).serialize(), data.getString("recipient_id"));
    assertEquals(RecipientId.from(2).serialize(), data.getString("thread_recipient_id"));
  }

  @Test
  public void cannotFindGroup() {
    // GIVEN
    JobMigration.JobData jobData = new JobMigration.JobData(SenderKeyDistributionSendJob.KEY,
                                                            "asdf",
                                                            new JsonJobData.Builder()
                                                                .putString("recipient_id", RecipientId.from(1).serialize())
                                                                .putBlobAsString("group_id", GROUP_ID.getDecodedId())
                                                                .serialize());

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
                                                            new JsonJobData.Builder()
                                                                .putString("recipient_id", RecipientId.from(1).serialize())
                                                                .serialize());

    // WHEN
    JobMigration.JobData result = testSubject.migrate(jobData);

    // THEN
    assertEquals(FailingJob.KEY, result.getFactoryKey());
  }
}