package org.archiver.model

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.tm.androidcopysdk.device.AbstractFiler
import com.tm.androidcopysdk.model.ArchiveAttachment
import com.tm.androidcopysdk.model.ArchiveMessage
import com.tm.logger.Log
import org.archiver.ArchiveConstants
import org.archiver.ArchiveConstants.Companion.SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX
import org.archiver.ArchiveFileUtil
import org.tm.archive.attachments.AttachmentId
import org.tm.archive.database.AttachmentTable
import org.tm.archive.database.StickerTable
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.providers.BlobProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class SignalFiler(
  context: Context,

  private val attachments: AttachmentTable,

  private val stickers: StickerTable,

  private val blobProvider: BlobProvider,

  ) : AbstractFiler(context) {

  override fun streamIntoFile(message: ArchiveMessage, attachment: ArchiveAttachment) {
    val sourcePath = attachment.sourcePath ?: return
    val archivePath = attachment.archivePath ?: return
    val archiveFile = File(archivePath)
    try {
      if (sourcePath.contains(ArchiveConstants.SIGNAL_PART_PATH))
        copyStream(attachments.getAttachmentStream(getAttachmentId(sourcePath) ?: return, 0), archiveFile)
      else if (sourcePath.contains(ArchiveConstants.SIGNAL_STICKER_PATH))
        copyStream(stickers.getStickerStream(ContentUris.parseId(Uri.parse(sourcePath))) ?: return, archiveFile)
      else if (sourcePath.contains(ArchiveConstants.SIGNAL_BLOB_PATH))
        copyStream(blobProvider.getStream(ApplicationDependencies.getApplication(), Uri.parse(sourcePath)), archiveFile)
      else if (ArchiveFileUtil.checkWriteExternalPermission(context))
        copyStream(context.contentResolver.openInputStream(Uri.parse(sourcePath)) ?: return, archiveFile)
    } catch (e: IOException) {
      Log.e(TAG, "failed to stream $sourcePath into $archivePath", e)
      e.printStackTrace()
    }
  }

  override fun getAttachmentFile(message: ArchiveMessage, attachment: ArchiveAttachment): File {
    return super.getAttachmentFile(message, attachment)
  }

  override fun getExtensionFromMimeType(mimeType: String?, fileName: String?): String? {
    if (fileName.isNullOrEmpty())
      return super.getExtensionFromMimeType(mimeType, fileName)

    if (fileName.contains(ArchiveConstants.SIGNAL_PART_PATH))
      return super.getExtensionFromMimeType(mimeType, null)

    if (fileName.contains(ArchiveConstants.SIGNAL_STICKER_PATH))
      return "webp"
//
//    val splits = fileName.split("/").dropLastWhile { it.isEmpty() }.toTypedArray()
//    val type = "webp".takeIf { fileName.contains(ArchiveConstants.SIGNAL_STICKER_PATH) }
//      ?: MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(Uri.parse(fileName)))
//    val fileNameInDirectory = splits[splits.size - 1].split(".")[0]
//    return "$fileNameInDirectory.$type"
    return super.getExtensionFromMimeType(mimeType, fileName)
  }

  override fun archiveAttachmentFileTemplatePrefix() = SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX

  private fun getAttachmentId(sourcePath: String): AttachmentId? {
    val splitUri = sourcePath.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val splitLength = splitUri.size
    if (splitLength < 1)
      return null

    return AttachmentId(splitUri[splitLength - 1].toLong())
  }

  private fun copyStream(stream: InputStream, file: File?) {
    stream.use { inputStream ->
      FileOutputStream(file).use { outputStream ->
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) {
          outputStream.write(buf, 0, len)
        }
      }
    }
  }

  companion object {

    private const val TAG = "SignalFiler"
  }
}