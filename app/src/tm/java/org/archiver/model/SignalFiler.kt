package org.archiver.model

import android.content.Context
import com.tm.androidcopysdk.Models.ArchiveAttachment
import com.tm.androidcopysdk.device.AbstractFiler
import org.archiver.ArchiveConstants.Companion.SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX
import org.tm.archive.attachments.AttachmentId
import org.tm.archive.database.AttachmentTable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SignalFiler(
  context: Context,
  private val attachments: AttachmentTable
) : AbstractFiler(context) {

  override fun streamIntoFile(attachment: ArchiveAttachment) {
    val sourcePath = attachment.sourcePath ?: return
    val archivePath = attachment.archivePath ?: return
    val splitUri = sourcePath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val splitLength = splitUri.size
    if (splitLength < 1)
      return
    val attachmentId = AttachmentId(splitUri[splitLength - 1].toLong())
    val archiveFile = File(archivePath)
    try {
      attachments.getAttachmentStream(attachmentId, 0).use { inputStream ->
        FileOutputStream(archiveFile).use { outputStream ->
          val buf = ByteArray(1024)
          var len: Int
          while (inputStream.read(buf).also { len = it } > 0) {
            outputStream.write(buf, 0, len)
          }
        }
      }
    } catch (e: IOException) {
      e.printStackTrace()
      return
    }
  }

  override fun archiveAttachmentFileTemplatePrefix() = SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX

//  private fun notSureWhatThisIs(uri: String) {
//    var resultFile: File? = null
//    resultFile = if (uri.contains(ArchiveConstants.SIGNAL_PART_PATH)) {
//      ArchiveFileUtil.getFileFromDataBaseUri(context, uri)
//    } else if (uri.contains(ArchiveConstants.SIGNAL_STICKER_PATH)) {
//      ArchiveFileUtil.getStickerFileFromBlobProvider(context, uri)
//    } else if (uri.contains(ArchiveConstants.SIGNAL_BLOB_PATH)) {
//      ArchiveFileUtil.getFileFromBlobProvider(context, uri)
//    } else {
//      ArchiveFileUtil.getFileFromDeviceUri(context, uri)
//    }
//    return resultFile
//  }
}