package org.archiver.core

import android.os.Handler
import android.os.Looper
import com.tm.androidcopysdk.Models.ArchiveAttachment
import com.tm.androidcopysdk.Models.ArchiveAttachmentType
import com.tm.androidcopysdk.Models.ArchiveMessage
import com.tm.androidcopysdk.Models.ArchiveMessageIdentifier
import com.tm.androidcopysdk.Models.ArchiveMessageType
import com.tm.androidcopysdk.Models.Direction
import com.tm.androidcopysdk.Models.MessageAttachmentStatus
import com.tm.androidcopysdk.Models.MessageDetailsArchive
import com.tm.androidcopysdk.Models.MessageStatus
import com.tm.androidcopysdk.api.IMessageStoreObserver
import com.tm.androidcopysdk.api.StoreListenerModule
import com.tm.androidcopysdk.device.DefaultMessageStoreObserver
import com.tm.androidcopysdk.utils.RuntimeObject.getCallerClassMethodAndLine
import com.tm.logger.Log
import java.io.File

class DefaultMessageStoreObserver<Id> : IMessageStoreObserver<Id> {

  private lateinit var module: StoreListenerModule<Id>

  private lateinit var handler: Handler

  private var accountPhoneNumber: String? = null

  override fun initialize(module: StoreListenerModule<Id>) {
    this.module = module
    handler = Handler(module.handlerThread.looper)
  }

  override fun setAccountPhoneNumber(accountPhoneNumber: String?) {
    this.accountPhoneNumber = accountPhoneNumber
  }

  override fun afterMessageIdStateChanged(id: Id) {
    ensureBackgroundThread()
    execute { afterMessagesStateChanged(listOfNotNull(getApplicationMessageDao().find(id))) }
  }

  override fun afterMessageStateChanged(message: ArchiveMessage) {
    execute { afterMessagesStateChanged(listOf(message)) }
  }

  override fun afterMessageIdsStateChanged(ids: List<Id>) {
    ensureBackgroundThread()
    execute { afterMessagesStateChanged(getApplicationMessageDao().findAll(ids)) }
  }

  override fun afterMessagesStateChanged(messages: List<ArchiveMessage>) {
    if (messages.isEmpty())
      return
    handler.post { messages.forEach { afterMessageStateChanged(it, it.archiveIdentifier?.let(getArchiverMessageDao()::find)) } }
  }

  private fun afterMessageStateChanged(message: ArchiveMessage, existing: ArchiveMessage?) {
    val accountPhoneNumber = accountPhoneNumber
    if (accountPhoneNumber.isNullOrEmpty()) {
      Log.d(TAG, "ignoring archive message ${message.archiveId}, account phone number is missing.")
      return
    }
    if (!isArchivingSupported(message)) {
      Log.d(TAG, "ignoring unsupported message ${message.archiveId}.")
      return
    }
    if (message.hasDeletions()) {
      archiveDeletedMessage(message, existing, accountPhoneNumber)
      return
    }
    when (message.transportType) {
      ArchiveMessageType.Sms -> archiveMessage(message, existing, accountPhoneNumber)
      ArchiveMessageType.Mms -> archiveMmsMessage(message, existing, accountPhoneNumber)
      else -> Log.w(TAG, "not sure how to handle $message ${createMessageDetails(message, accountPhoneNumber)}")
    }
  }

  private fun archiveDeletedMessage(message: ArchiveMessage, existing: ArchiveMessage?, accountPhoneNumber: String) {
    if (message.direction != Direction.Outgoing || existing != null)
      return
    val details = createMessageDetails(message, accountPhoneNumber)
    Log.d(TAG, "setDeletedMessage $message $details")
    module.dataGrabber.setMessage(details)
  }

  private fun archiveMessage(message: ArchiveMessage, existing: ArchiveMessage?, accountPhoneNumber: String) {
    if (existing != null) {
      Log.d(TAG, "message ${message.archiveId} was already archived, skipping.")
      return
    }
    if (message.status == MessageStatus.Sending && !message.chat.isSecret) { // TODO  this is new logic
      Log.d(TAG, "message ${message.archiveId} is still in 'Sending' state, skipping.")
      return
    }
    Log.d(TAG, "setMessage $message")
    module.dataGrabber.setMessage(createMessageDetails(message, accountPhoneNumber))
  }

  private fun archiveMmsMessage(message: ArchiveMessage, existing: ArchiveMessage?, accountPhoneNumber: String) {
    if (existing == null) {
      val details = createMessageDetails(message, accountPhoneNumber)
      Log.d(TAG, "setMmsMessage $message")
      val files = message.attachments.map { getAttachmentFile(message, it) }.toTypedArray()
      module.dataGrabber.setMmsMessage(
        details.protocol, details.toPhonesArray, details.fromPhoneNumber, details.body,
        details.id, details.date, details.subject, accountPhoneNumber, details.chatMode, details.chatName, details.chatId,
        details.fromName, details.fromValue, details.toNameArray, details.toPhoneNumberArrayValue, files
      )
      return
    }
    message.attachments.forEach { maybeUpdateAttachmentsFileMms(message, existing, it) }
  }

  private fun maybeUpdateAttachmentsFileMms(message: ArchiveMessage, existing: ArchiveMessage, attachment: ArchiveAttachment) {
    val targetPath = getAttachmentFile(message, attachment).absolutePath
    val existingAttachment = existing.attachments.firstOrNull { it.sourcePath == targetPath } ?: return
    if (!existingAttachment.archivePath.isNullOrEmpty() || existingAttachment.status != MessageAttachmentStatus.Loading)
      return
    if (attachment.status != MessageAttachmentStatus.Success || attachment.sourcePath == null)
      return
    val sourceFile = File(targetPath)
    if (sourceFile.exists() && sourceFile.length() > 0)
      return

    module.filer.streamIntoFile(attachment.copy(archivePath = targetPath))
    if (!sourceFile.exists() && sourceFile.length() > 0)
      return
    val needsCompression = message.direction == Direction.Outgoing || attachment.type == ArchiveAttachmentType.Sticker
    val file = File(targetPath)
    Log.d(TAG, "updateFileMms(${file.exists()}) $needsCompression $message $existing")
    module.dataGrabber.updateFileMms(File(targetPath).name, needsCompression)
    Log.d(TAG, "updateFileMms(${file.exists()})")
  }

  private fun isArchivingSupported(message: ArchiveMessage): Boolean = module.settings().isArchivingSupported(message)

  private fun createMessageDetails(message: ArchiveMessage, accountPhoneNumber: String): MessageDetailsArchive {
    val protocol = "1".takeIf { message.direction == Direction.Outgoing } ?: "0"
    return message.run {
      MessageDetailsArchive(
        protocol = protocol,
        toPhonesArray = receivers.map { it.cleanAddress }.toTypedArray(),
        fromPhoneNumber = senderAddress,
        body = constructBody(message),
        id = archiveId,
        date = timestamp.toString(),
        subject = constructSubject(message),
        myNumber = accountPhoneNumber,
        chatMode = chat.type.toChatMode(),
        chatName = chat.name,
        chatId = chat.id,
        fromName = sender.toContact(),
        fromValue = sender.cleanAddress,
        toNameArray = receivers.map { it.toContact() }.toTypedArray(),
        toPhoneNumberArrayValue = receivers.map { it.cleanAddress }.toTypedArray(),
      )
    }
  }

  @Suppress("SameParameterValue")
  private fun generateAttachmentName(messageId: String, attachmentId: String): String =
    "${module.filer.archiveAttachmentFileTemplatePrefix()}${attachmentId}_${messageId}"

  private fun getApplicationMessageDao() = module.applicationDatabase.messageDao()

  private fun getArchiverMessageDao() = module.archiverDatabase.messageDao()

  private fun createOriginalMessageIdentifier(message: ArchiveMessage) = message.takeIf { it.timestamp > 0 }?.run {
    ArchiveMessageIdentifier(senderAddress, timestamp, transportType, isDeleted = false, isRemoteDeleted = false, editCount = 0)
  }

  private fun constructSubject(message: ArchiveMessage): String {
    val subject = message.subject()
    if (message.hasDeletions())
      return "${createDeleteMessagePrefix(message)} $subject"
    if (message.isEdited)
      return "${createEditMessagePrefix(message)} $subject"
    return subject
  }

  private fun constructBody(message: ArchiveMessage): String {
    val body = cleanupBody(message.body)
    if (message.hasDeletions()) {
      val originalMessageId = String.format(DELETED_MESSAGE_BODY_ORIGINAL_ID_REFERENCE, createOriginalMessageIdentifier(message) ?: "")
      val subject = message.subject()
      return "${createDeleteMessagePrefix(message)} ${subject}\n\n${originalMessageId}\n\n${body}${getListFileAsString(message)}"
    }
    if (message.isEdited) {
      val originalMessageId = String.format(EDITED_MESSAGE_BODY_ORIGINAL_ID_REFERENCE, createOriginalMessageIdentifier(message) ?: "")
      val subject = message.subject()
      return "${createEditMessagePrefix(message)} ${subject}\n\n${originalMessageId}\n\n${body}${getListFileAsString(message)}"
    }
    return body
  }

  private fun cleanupBody(body: String?): String {
    if (body.isNullOrEmpty())
      return ""
    return cleanupBodyFromUnusedCharacters(body)
  }

  private fun cleanupBodyFromUnusedCharacters(body: String): String {
    return body.replace("\u2069", "").replace("\u2068", "")
  }

  private fun createDeleteMessagePrefix(message: ArchiveMessage): String {
    val prefix = DELETE_FOR_ALL_PREFIX.takeIf { message.isRemoteDeleted } ?: DELETE_FOR_ME_PREFIX
    return createMessagePrefix(message, prefix)
  }

  private fun createEditMessagePrefix(message: ArchiveMessage): String = createMessagePrefix(message, EDIT_PREFIX)

  private fun createMessagePrefix(message: ArchiveMessage, prefix: String): String {
    var statusPrefix = PREFIX_STATUS_UNKNOWN
    if (message.chat.type.isNormalChat())
      statusPrefix = if (message.status == MessageStatus.Read) PREFIX_STATUS_READ else PREFIX_STATUS_UNREAD
    return "$prefix – $statusPrefix"
  }

  private fun getListFileAsString(message: ArchiveMessage): String {
    if (message.transportType != ArchiveMessageType.Mms || message.attachments.isEmpty()) // TODO second boolean is new logic
      return ""
    return "\n${FILES_PREFIX}\n${message.attachments.joinToString("\n") { getFileNameWithType(message, it) }}"
  }

  private fun getAttachmentFile(message: ArchiveMessage, attachment: ArchiveAttachment): File {
    val name = getFileNameWithType(message, attachment)
    val archiveFileDir = File(module.filer.filesDir(), ARCHIVE_FILE_FOLDER_NAME).also { if (!it.exists()) it.mkdir() }
    return File(archiveFileDir, name)
  }

  private fun getFileNameWithType(message: ArchiveMessage, attachment: ArchiveAttachment): String {
    val isIncoming = message.direction == Direction.Incoming
    val fileName = attachment.archivePath
    val attachmentId = "0" // attachment.id!!
    return if (isIncoming || fileName == null)
      "${generateAttachmentName(message.id, attachmentId)}.${module.filer.getExtensionFromMimeType(mimeType = attachment.contentType, fileName = fileName)}"
    else
      fileName.replace(" ", "_")
  }

  private inline fun execute(crossinline block: () -> Unit) {
    val start = System.currentTimeMillis()
    val sender = getCallerClassMethodAndLine(1)
    try {
      block()
    } catch (e: Throwable) {
      Log.e(TAG, "execute failed ${e.message}", e)
      e.printStackTrace()
    } finally {
      val now = System.currentTimeMillis()
      Log.d(TAG, "execute from $sender took ${now - start}ms")
    }
  }

  private fun ensureBackgroundThread() {
    val looper = Looper.myLooper()
    require(looper == null || looper != Looper.getMainLooper()) { "You must call this method from a background thread" }
  }

  companion object {

    private const val TAG = "MessageStoreListener"

    private const val ARCHIVE_FILE_FOLDER_NAME = "aa_archiver"

    private const val DELETE_PREFIX = "DELETED For"
    private const val DELETE_FOR_ALL_PREFIX = "$DELETE_PREFIX All"
    private const val DELETE_FOR_ME_PREFIX = "$DELETE_PREFIX Me"
    private const val EDIT_PREFIX = "EDIT"
    private const val FILES_PREFIX = "Files:"

    private const val PREFIX_STATUS_UNKNOWN = "UNKNOWN"
    private const val PREFIX_STATUS_READ = "READ"
    private const val PREFIX_STATUS_UNREAD = "UNREAD"

    private const val DELETED_MESSAGE_BODY_ORIGINAL_ID_REFERENCE = "Original Message (Msg ID - %s)"
    private const val EDITED_MESSAGE_BODY_ORIGINAL_ID_REFERENCE = "Original Message ID: %s"

    val instance: IMessageStoreObserver<Long> by lazy { DefaultMessageStoreObserver() }

  }

}