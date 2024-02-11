package org.archiver.core

import android.os.Handler
import android.os.Looper
import com.tm.androidcopysdk.model.ArchiveAttachment
import com.tm.androidcopysdk.model.ArchiveAttachmentType
import com.tm.androidcopysdk.model.ArchiveMessage
import com.tm.androidcopysdk.model.ArchiveMessageType
import com.tm.androidcopysdk.model.Direction
import com.tm.androidcopysdk.model.MessageAttachmentStatus
import com.tm.androidcopysdk.model.MessageStatus
import com.tm.androidcopysdk.api.IMessageStoreObserver
import com.tm.androidcopysdk.api.SdkModule
import com.tm.androidcopysdk.converter.MessageDetailsConverter
import com.tm.androidcopysdk.converter.MessageDetailsConverter.Companion.getFileNameWithType
import com.tm.androidcopysdk.utils.RuntimeObject.getCallerClassMethodAndLine
import com.tm.logger.Log
import java.io.File

class DefaultMessageStoreObserver<Id> : IMessageStoreObserver<Id> {

  private lateinit var module: SdkModule<Id>

  private lateinit var handler: Handler

  private var enabled = true

  override fun initialize(module: SdkModule<Id>) {
    this.module = module
    handler = Handler(module.handlerThread.looper)
  }

  override fun enable() {
    enabled = true
  }

  override fun disable() {
    enabled = false
  }

  override fun beforeMessageStateChanged(message: ArchiveMessage) {
    execute { afterMessagesStateChanged(listOf(message)) }
  }

  override fun beforeMessagesStateChanged(messages: List<ArchiveMessage>) {
    execute { afterMessagesStateChanged(messages) }
  }

  override fun afterMessageIdStateChanged(id: Id) {
    execute { afterMessagesStateChanged(listOfNotNull(getApplicationMessageDao().find(id))) }
  }

  override fun afterMessageStateChanged(message: ArchiveMessage) {
    execute { afterMessagesStateChanged(listOf(message)) }
  }

  override fun afterMessageIdsStateChanged(ids: List<Id>) {
    execute { afterMessagesStateChanged(getApplicationMessageDao().findAll(ids)) }
  }

  override fun afterMessagesStateChanged(messages: List<ArchiveMessage>) {
    if (messages.isEmpty())
      return
    messages.forEach { afterMessageStateChanged(it, getArchiverMessageDao().find(it.archiveIdentifier)) }
  }

  private fun afterMessageStateChanged(message: ArchiveMessage, existing: ArchiveMessage?) {
    if (!enabled)
      return
    if (message.accountPhoneNumber.isNullOrEmpty()) {
      Log.d(TAG, "ignoring archive message ${message.archiveId}, account phone number is missing.")
      return
    }
    if (!isArchivingSupported(message) || message.timestamp.value <= 0) {
      Log.d(TAG, "ignoring unsupported message ${message.archiveId}.")
      return
    }
    if (message.hasDeletions()) {
      archiveDeletedMessage(message, existing)
      return
    }
    when (message.type) {
      ArchiveMessageType.Sms -> archiveMessage(message, existing)
      ArchiveMessageType.Mms -> archiveMmsMessage(message, existing)
      else -> Log.w(TAG, "not sure how to handle $message $existing ${MessageDetailsConverter(module.filer).convert(message, existing)}")
    }
  }

  private fun archiveDeletedMessage(message: ArchiveMessage, existing: ArchiveMessage?) {
    if (message.direction != Direction.Outgoing || existing != null)
      return
    val details = MessageDetailsConverter(module.filer).convert(message, existing)
    Log.d(TAG, "setDeletedMessage $message $details")
    module.dataGrabber.setMessage(details)
  }

  private fun archiveMessage(message: ArchiveMessage, existing: ArchiveMessage?) {
    if (existing != null) {
      Log.d(TAG, "message ${message.archiveId} was already archived, skipping.")
      return
    }
    if (message.status == MessageStatus.Sending && !message.chat.isSecret) {
      Log.d(TAG, "message ${message.archiveId} is still in 'Sending' state, skipping.")
      return
    }
    Log.d(TAG, "setMessage $message")
    module.dataGrabber.setMessage(MessageDetailsConverter(module.filer).convert(message, existing))
  }

  private fun archiveMmsMessage(message: ArchiveMessage, existing: ArchiveMessage?) {
    if (existing == null) {
      val details = MessageDetailsConverter(module.filer).convert(message, existing)
      val files = message.attachments.map { getAttachmentFile(message, it) }.toTypedArray()
      Log.d(TAG, "setMmsMessage $message ${files.joinToString { it.absolutePath }}")
      module.dataGrabber.setMmsMessage(
        details.protocol, details.toPhonesArray, details.fromPhoneNumber, details.body,
        details.id, details.date, details.subject, message.accountPhoneNumber, details.chatMode, details.chatName, details.chatId,
        details.fromName, details.fromValue, details.toNameArray, details.toPhoneNumberArrayValue, files
      )
      return
    }
    message.attachments.forEach { maybeUpdateAttachmentsFileMms(message, existing, it) }
  }

  private fun maybeUpdateAttachmentsFileMms(message: ArchiveMessage, existing: ArchiveMessage, attachment: ArchiveAttachment) {
    val targetPath = getAttachmentFile(message, attachment).absolutePath
    val existingAttachment = existing.attachments.firstOrNull { it.sourcePath == targetPath } ?: return
    if (!existingAttachment.archivePath.isNullOrEmpty() || existingAttachment.status != MessageAttachmentStatus.Loading) {
      Log.d(TAG, "message ${message.archiveId} was already archived, skipping.")
      return
    }
    if (attachment.status != MessageAttachmentStatus.Success || attachment.sourcePath == null) {
      Log.d(TAG, "message ${message.archiveId} has source of ${attachment.sourcePath}, and is in '${attachment.status}' state, skipping.")
      return
    }
    val sourceFile = File(targetPath)
    if (sourceFile.exists() && sourceFile.length() > 0) {
      Log.d(TAG, "message ${message.archiveId} source file already exists, skipping.")
      return
    }

    module.filer.streamIntoFile(message, attachment.copy(archivePath = targetPath))
    if (!sourceFile.exists() || sourceFile.length() <= 0) {
      Log.d(TAG, "message ${message.archiveId} source file failed to stream, skipping.")
      return
    }

    val needsCompression = message.direction == Direction.Outgoing || attachment.type == ArchiveAttachmentType.Sticker
    val file = File(targetPath)
    Log.d(TAG, "updateFileMms(${file.exists()}) $needsCompression $message $existing")
    module.dataGrabber.updateFileMms(file.name, needsCompression)
    Log.d(TAG, "updateFileMms(${file.exists()})")
  }

  private fun isArchivingSupported(message: ArchiveMessage): Boolean = module.settings().isArchivingSupported(message)

  private fun getApplicationMessageDao() = module.applicationDatabase.messageDao()

  private fun getArchiverMessageDao() = module.archiverDatabase.messageDao()

  private fun getAttachmentFile(message: ArchiveMessage, attachment: ArchiveAttachment): File {
    val name = message.getFileNameWithType(module.filer, attachment)
    val archiveFileDir = File(module.filer.filesDir(), ARCHIVE_FILE_FOLDER_NAME).also { if (!it.exists()) it.mkdir() }
    return File(archiveFileDir, name)
  }

  private inline fun execute(crossinline block: () -> Unit) {
    ensureBackgroundThread()
    val start = System.currentTimeMillis()
    val sender = getCallerClassMethodAndLine(1)
    handler.post {
      try {
        block()
      } catch (e: Throwable) {
        e.printStackTrace()
        Log.e(TAG, "execute from $sender failed ${e.message}", e)
      } finally {
        val now = System.currentTimeMillis()
        Log.d(TAG, "execute from $sender took ${now - start}ms")
      }
    }
  }

  private fun ensureBackgroundThread() {
    val looper = Looper.myLooper()
    require(looper == null || looper != Looper.getMainLooper()) { "You must call this method from a background thread" }
  }


  companion object {

    private const val TAG = "DefaultMessageStoreObserver"

    private const val ARCHIVE_FILE_FOLDER_NAME = "aa_archiver"

    private var instance: IMessageStoreObserver<*>? = null

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun<T> getInstance(): IMessageStoreObserver<T> {
      var result: IMessageStoreObserver<*>? = instance
      if (result == null) {
        result = DefaultMessageStoreObserver<T>()
        instance = result
      }
      return result as IMessageStoreObserver<T>
    }
  }

}