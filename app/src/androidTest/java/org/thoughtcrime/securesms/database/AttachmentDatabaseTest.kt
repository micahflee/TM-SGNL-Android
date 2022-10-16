package org.tm.archive.database

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.tm.archive.attachments.UriAttachment
import org.tm.archive.mms.MediaStream
import org.tm.archive.mms.SentMediaQuality
import org.tm.archive.providers.BlobProvider
import org.tm.archive.util.MediaUtil
import java.util.Optional

@RunWith(AndroidJUnit4::class)
class AttachmentDatabaseTest {

  @Before
  fun setUp() {
    SignalDatabase.attachments.deleteAllAttachments()
  }

  @Test
  fun givenABlob_whenIInsert2AttachmentsForPreUpload_thenIExpectDistinctIdsButSameFileName() {
    val blob = BlobProvider.getInstance().forData(byteArrayOf(1, 2, 3, 4, 5)).createForSingleSessionInMemory()
    val highQualityProperties = createHighQualityTransformProperties()
    val highQualityImage = createAttachment(1, blob, highQualityProperties)
    val attachment = SignalDatabase.attachments.insertAttachmentForPreUpload(highQualityImage)
    val attachment2 = SignalDatabase.attachments.insertAttachmentForPreUpload(highQualityImage)

    assertNotEquals(attachment2.attachmentId, attachment.attachmentId)
    assertEquals(attachment2.fileName, attachment.fileName)
  }

  @Test
  fun givenABlobAndDifferentTransformQuality_whenIInsert2AttachmentsForPreUpload_thenIExpectDifferentFileInfos() {
    val blob = BlobProvider.getInstance().forData(byteArrayOf(1, 2, 3, 4, 5)).createForSingleSessionInMemory()
    val highQualityProperties = createHighQualityTransformProperties()
    val highQualityImage = createAttachment(1, blob, highQualityProperties)
    val lowQualityImage = createAttachment(1, blob, AttachmentDatabase.TransformProperties.empty())
    val attachment = SignalDatabase.attachments.insertAttachmentForPreUpload(highQualityImage)
    val attachment2 = SignalDatabase.attachments.insertAttachmentForPreUpload(lowQualityImage)

    SignalDatabase.attachments.updateAttachmentData(
      attachment,
      createMediaStream(byteArrayOf(1, 2, 3, 4, 5)),
      false
    )

    SignalDatabase.attachments.updateAttachmentData(
      attachment2,
      createMediaStream(byteArrayOf(1, 2, 3)),
      false
    )

    val attachment1Info = SignalDatabase.attachments.getAttachmentDataFileInfo(attachment.attachmentId, AttachmentDatabase.DATA)
    val attachment2Info = SignalDatabase.attachments.getAttachmentDataFileInfo(attachment2.attachmentId, AttachmentDatabase.DATA)

    assertNotEquals(attachment1Info, attachment2Info)
  }

  @Test
  fun givenIdenticalAttachmentsInsertedForPreUpload_whenIUpdateAttachmentDataAndSpecifyOnlyModifyThisAttachment_thenIExpectDifferentFileInfos() {
    val blob = BlobProvider.getInstance().forData(byteArrayOf(1, 2, 3, 4, 5)).createForSingleSessionInMemory()
    val highQualityProperties = createHighQualityTransformProperties()
    val highQualityImage = createAttachment(1, blob, highQualityProperties)
    val attachment = SignalDatabase.attachments.insertAttachmentForPreUpload(highQualityImage)
    val attachment2 = SignalDatabase.attachments.insertAttachmentForPreUpload(highQualityImage)

    SignalDatabase.attachments.updateAttachmentData(
      attachment,
      createMediaStream(byteArrayOf(1, 2, 3, 4, 5)),
      true
    )

    SignalDatabase.attachments.updateAttachmentData(
      attachment2,
      createMediaStream(byteArrayOf(1, 2, 3, 4)),
      true
    )

    val attachment1Info = SignalDatabase.attachments.getAttachmentDataFileInfo(attachment.attachmentId, AttachmentDatabase.DATA)
    val attachment2Info = SignalDatabase.attachments.getAttachmentDataFileInfo(attachment2.attachmentId, AttachmentDatabase.DATA)

    assertNotEquals(attachment1Info, attachment2Info)
  }

  private fun createAttachment(id: Long, uri: Uri, transformProperties: AttachmentDatabase.TransformProperties): UriAttachment {
    return UriAttachmentBuilder.build(
      id,
      uri = uri,
      contentType = MediaUtil.IMAGE_JPEG,
      transformProperties = transformProperties
    )
  }

  private fun createHighQualityTransformProperties(): AttachmentDatabase.TransformProperties {
    return AttachmentDatabase.TransformProperties.forSentMediaQuality(Optional.empty(), SentMediaQuality.HIGH)
  }

  private fun createMediaStream(byteArray: ByteArray): MediaStream {
    return MediaStream(byteArray.inputStream(), MediaUtil.IMAGE_JPEG, 2, 2)
  }
}
