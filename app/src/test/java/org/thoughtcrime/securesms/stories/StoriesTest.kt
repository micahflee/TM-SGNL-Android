package org.tm.archive.stories

import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.isA
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.tm.archive.attachments.AttachmentId
import org.tm.archive.database.AttachmentTable
import org.tm.archive.database.FakeMessageRecords
import org.tm.archive.database.SignalDatabase
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.jobmanager.JobManager
import org.tm.archive.jobs.AttachmentDownloadJob

class StoriesTest {

  @Rule
  @JvmField
  val mockitoRule: MockitoRule = MockitoJUnit.rule()

  @Mock
  private lateinit var mockAttachmentTable: AttachmentTable

  @Mock
  private lateinit var mockJobManager: JobManager

  @Mock
  private lateinit var mockApplicationDependenciesStatic: MockedStatic<ApplicationDependencies>

  @Mock
  private lateinit var mockSignalDatabaseStatic: MockedStatic<SignalDatabase>

  @Mock
  private lateinit var mockSignalDatabase: SignalDatabase

  private val testScheduler = TestScheduler()

  @Before
  fun setUp() {
    RxJavaPlugins.setInitIoSchedulerHandler { testScheduler }
    RxJavaPlugins.setIoSchedulerHandler { testScheduler }

    SignalDatabase.setSignalDatabaseInstanceForTesting(mockSignalDatabase)
    whenever(SignalDatabase.attachments).thenReturn(mockAttachmentTable)
    whenever(ApplicationDependencies.getJobManager()).thenReturn(mockJobManager)
    whenever(mockAttachmentTable.getAttachmentsForMessage(any())).thenReturn(emptyList())
  }

  @After
  fun tearDown() {
    RxJavaPlugins.reset()
  }

  @Test
  fun `Given a MessageRecord with no attachments and a LinkPreview without a thumbnail, when I enqueueAttachmentsFromStoryForDownload, then I enqueue nothing`() {
    // GIVEN
    val messageRecord = FakeMessageRecords.buildMediaMmsMessageRecord(
      linkPreviews = listOf(FakeMessageRecords.buildLinkPreview())
    )

    // WHEN
    val testObserver = Stories.enqueueAttachmentsFromStoryForDownload(messageRecord, true).test()
    testScheduler.triggerActions()

    // THEN
    testObserver.assertComplete()
    verify(mockJobManager, never()).add(any())
  }

  @Test
  fun `Given a MessageRecord with no attachments and a LinkPreview with a thumbnail, when I enqueueAttachmentsFromStoryForDownload, then I enqueue once`() {
    // GIVEN
    val messageRecord = FakeMessageRecords.buildMediaMmsMessageRecord(
      linkPreviews = listOf(
        FakeMessageRecords.buildLinkPreview(
          attachmentId = AttachmentId(1, 2)
        )
      )
    )

    // WHEN
    val testObserver = Stories.enqueueAttachmentsFromStoryForDownload(messageRecord, true).test()
    testScheduler.triggerActions()

    // THEN
    testObserver.assertComplete()
    verify(mockJobManager).add(isA<AttachmentDownloadJob>())
  }

  @Test
  fun `Given a MessageRecord with an attachment, when I enqueueAttachmentsFromStoryForDownload, then I enqueue once`() {
    // GIVEN
    val attachment = FakeMessageRecords.buildDatabaseAttachment()
    val messageRecord = FakeMessageRecords.buildMediaMmsMessageRecord()
    whenever(mockAttachmentTable.getAttachmentsForMessage(any())).thenReturn(listOf(attachment))

    // WHEN
    val testObserver = Stories.enqueueAttachmentsFromStoryForDownload(messageRecord, true).test()
    testScheduler.triggerActions()

    // THEN
    testObserver.assertComplete()
    verify(mockJobManager).add(isA<AttachmentDownloadJob>())
  }
}
