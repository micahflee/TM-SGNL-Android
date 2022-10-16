package org.tm.archive.jobmanager.migrations;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.tm.archive.jobmanager.Data;
import org.tm.archive.jobmanager.Job;
import org.tm.archive.jobmanager.JobMigration.JobData;
import org.tm.archive.jobs.FailingJob;
import org.tm.archive.jobs.SendDeliveryReceiptJob;
import org.tm.archive.recipients.Recipient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class RecipientIdFollowUpJobMigrationTest {

  @Rule
  public MockitoRule rule = MockitoJUnit.rule();

  @Mock
  private MockedStatic<Recipient> recipientMockedStatic;

  @Mock
  private MockedStatic<Job.Parameters> jobParametersMockedStatic;

  @Test
  public void migrate_sendDeliveryReceiptJob_good() throws Exception {
    JobData testData = new JobData("SendDeliveryReceiptJob", null, new Data.Builder().putString("recipient", "1")
                                                                                     .putLong("message_id", 1)
                                                                                     .putLong("timestamp", 2)
                                                                                     .build());
    RecipientIdFollowUpJobMigration subject   = new RecipientIdFollowUpJobMigration();
    JobData                         converted = subject.migrate(testData);

    assertEquals("SendDeliveryReceiptJob", converted.getFactoryKey());
    assertNull(converted.getQueueKey());
    assertEquals("1", converted.getData().getString("recipient"));
    assertEquals(1, converted.getData().getLong("message_id"));
    assertEquals(2, converted.getData().getLong("timestamp"));

    new SendDeliveryReceiptJob.Factory().create(mock(Job.Parameters.class), converted.getData());
  }

  @Test
  public void migrate_sendDeliveryReceiptJob_bad() throws Exception {
    JobData testData = new JobData("SendDeliveryReceiptJob", null, new Data.Builder().putString("recipient", "1")
                                                                                     .build());
    RecipientIdFollowUpJobMigration subject   = new RecipientIdFollowUpJobMigration();
    JobData                         converted = subject.migrate(testData);

    assertEquals("FailingJob", converted.getFactoryKey());
    assertNull(converted.getQueueKey());

    new FailingJob.Factory().create(mock(Job.Parameters.class), converted.getData());
  }
}
