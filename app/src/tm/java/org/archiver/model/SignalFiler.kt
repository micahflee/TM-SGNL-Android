package org.archiver.model

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import com.tm.androidcopysdk.api.IFiler
import com.tm.androidcopysdk.device.AbstractFiler
import com.tm.androidcopysdk.model.ArchiveAttachment
import com.tm.androidcopysdk.model.ArchiveAttachmentType
import com.tm.androidcopysdk.model.ArchiveMessage
import com.tm.androidcopysdk.model.ArchiveMessageType
import com.tm.androidcopysdk.model.MessageAttachmentStatus
import com.tm.logger.Log
import org.archiver.ArchiveConstants
import org.archiver.ArchiveConstants.Companion.SIGNAL_ARCHIVE_ATTACHMENT_TEMPLATE_PREFIX
import org.archiver.ArchiveFileUtil
import org.tm.archive.attachments.AttachmentId
import org.tm.archive.database.AttachmentTable
import org.tm.archive.database.MessageTable
import org.tm.archive.database.SignalDatabase
import org.tm.archive.database.StickerTable
import org.tm.archive.database.model.MmsMessageRecord
import org.tm.archive.dependencies.ApplicationDependencies
import org.tm.archive.providers.BlobProvider
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream

class SignalFiler(
  context: Context,

  private val messages: MessageTable,

  private val attachments: AttachmentTable,

  private val stickers: StickerTable,

  private val blobProvider: BlobProvider,

) : AbstractFiler(context) {

  override fun streamIntoFile(message: ArchiveMessage, attachment: ArchiveAttachment) {
		if (message.type == ArchiveMessageType.Call) {
			super.streamIntoFile(message, attachment)
			return
		}
    val sourcePath = attachment.sourcePath ?: return
    val archivePath = attachment.archivePath ?: return
    val archiveFile = File(archivePath)
    try {
      if (attachment.type == ArchiveAttachmentType.VCard)
        writeFirstVCardToFile(message, archiveFile)
      else if (sourcePath.contains(ArchiveConstants.SIGNAL_PART_PATH))
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

	fun getRecordedFile(id: String?): ArchiveAttachment? {
		if (id == null)
			return null
		val cacheFiles = cacheDir().listFiles() ?: return null
		return cacheFiles.firstNotNullOfOrNull {
			if (it.extension == "pcm" && (it.name.startsWith("${id}_") || it.name.startsWith("${id}-")))
				ArchiveAttachment(id, ArchiveAttachmentType.Audio,  "audio/pcm", it.absolutePath, null, MessageAttachmentStatus.Success, true)
			else
				null
		}
	}

  override fun getAttachmentFile(message: ArchiveMessage, attachment: ArchiveAttachment): File {
    if (attachment.type == ArchiveAttachmentType.VCard)
      return File(getArchiveFileDir(), attachment.sourcePath ?: "contact.vcf")
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
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
          outputStream.write(buffer, 0, length)
        }
      }
    }
  }

  private fun writeFirstVCardToFile(message: ArchiveMessage, file: File) {
    val dbMessage = messages.getMessageRecordOrNull(message.id.toLongOrNull() ?: return) as? MmsMessageRecord ?: return
    val contact = dbMessage.sharedContacts.firstOrNull() ?: return
    FileWriter(file).use { writer ->
      writer.write(VCARD_BEGIN)
      writer.write(VCARD_VERSION)
      writer.write(String.format(VCARD_NAME, contact.name.familyName, contact.name.givenName))
      writer.write(String.format(VCARD_FAMILY_NAME, contact.name.givenName, contact.name.familyName))
      if (contact.organization != null)
        writer.write(String.format(VCARD_ORGANIZATION, contact.organization))

//      contact.postalAddresses.forEach { writer.write(String.format(VCARD_PHONE_NUMBERS, it.label)) }  // TODO this is probably a bug
      contact.postalAddresses.forEach { writer.write(String.format(VCARD_POSTAL_ADDRESS, it.label)) }
      contact.phoneNumbers.forEach { writer.write(String.format(VCARD_PHONE_NUMBERS, it.number)) }
//      writer.write(String.format(VCARD_POSTAL_ADDRESS, contact.postalAddresses)) // TODO this is probably a bug

      contact.emails.forEach { writer.write(String.format(VCARD_EMAIL, it.email)) }
      writer.write(VCARD_END)
    }
  }

  companion object {

    private const val TAG = "SignalFiler"

    private const val VCARD_BEGIN = "BEGIN:VCARD\r\n"
    private const val VCARD_END = "END:VCARD\r\n"
    private const val VCARD_VERSION = "VERSION:3.0\r\n"
    private const val VCARD_NAME = "N:%s;%s\r\n"
    private const val VCARD_FAMILY_NAME = "FN:%s %s\r\n"
    private const val VCARD_ORGANIZATION = "ORG:%s\r\n"
    private const val VCARD_POSTAL_ADDRESS = "ADR;TYPE=WORK:;;%s\r\n"
    private const val VCARD_PHONE_NUMBERS = "TEL;TYPE=WORK,VOICE:%s\r\n"
    private const val VCARD_EMAIL = "EMAIL;TYPE=PREF,INTERNET:%s\r\n"


		var filer: IFiler? = null

		fun requireInstance() = requireNotNull(filer)

		fun getInstance(context: Context): IFiler {
			var filer = filer
			if (filer == null) {
				filer = SignalFiler(context, SignalDatabase.messages, SignalDatabase.attachments, SignalDatabase.stickers, BlobProvider.getInstance())
				SignalFiler.filer = filer
			}
			return filer
		}
  }
}